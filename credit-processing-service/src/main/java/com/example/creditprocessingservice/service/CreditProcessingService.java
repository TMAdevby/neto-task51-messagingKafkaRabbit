package com.example.creditprocessingservice.service;

import com.example.creditprocessingservice.event.CreditApplicationEvent;
import com.example.creditprocessingservice.event.CreditDecisionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditProcessingService {

    private final RabbitTemplate rabbitTemplate;

    private static final String KAFKA_TOPIC = "credit-applications";

    private static final String RABBITMQ_QUEUE = "credit-decisions";

    private static final double DEBT_TO_INCOME_THRESHOLD = 0.5;

    @KafkaListener(topics = KAFKA_TOPIC, groupId = "credit-processing-group")
    public void processApplication(CreditApplicationEvent event) {
        log.info("Получена заявка из Kafka ID: {}", event.getApplicationId());

        double monthlyPayment = event.getAmount() / event.getTerm();

        double totalDebt = monthlyPayment + event.getCurrentDebt();

        double debtToIncomeRatio = totalDebt / event.getIncome();

        log.info("Заявка ID {}: DTI = {:.2f} (порог: {:.2f})",
                event.getApplicationId(),
                debtToIncomeRatio,
                DEBT_TO_INCOME_THRESHOLD);

        boolean approved;
        String reason;

        if (debtToIncomeRatio > DEBT_TO_INCOME_THRESHOLD) {
            approved = false;
            reason = String.format(
                    "Кредитная нагрузка превышает 50%% от дохода (%.1f%%)",
                    debtToIncomeRatio * 100
            );
            log.info("Заявка ID {} ОТКЛОНЕНА: {}", event.getApplicationId(), reason);

        } else if (event.getCreditRating() < 500) {
            approved = false;
            reason = String.format(
                    "Низкий кредитный рейтинг (%d)",
                    event.getCreditRating()
            );
            log.info("Заявка ID {} ОТКЛОНЕНА: {}", event.getApplicationId(), reason);

        } else {
            approved = true;
            reason = "Заявка одобрена";
            log.info("Заявка ID {} ОДОБРЕНА", event.getApplicationId());
        }

        CreditDecisionEvent decision = new CreditDecisionEvent(
                event.getApplicationId(),  // Ссылка на исходную заявку
                approved,                   // Результат решения
                reason                      // Причина (особенно при отказе)
        );

        rabbitTemplate.convertAndSend(RABBITMQ_QUEUE, decision);
        log.info("Решение для заявки ID {} отправлено в RabbitMQ",
                event.getApplicationId());
    }
}