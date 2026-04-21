package com.example.creditapiservice.service;

import com.example.creditapiservice.dto.CreditRequest;
import com.example.creditapiservice.dto.CreditStatusResponse;
import com.example.creditapiservice.event.CreditApplicationEvent;
import com.example.creditapiservice.event.CreditDecisionEvent;
import com.example.creditapiservice.model.CreditApplication;
import com.example.creditapiservice.repository.CreditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditService {

    private final CreditRepository creditRepository;

    private final KafkaTemplate<String, CreditApplicationEvent> kafkaTemplate;

    private static final String KAFKA_TOPIC = "credit-applications";

    private static final String RABBITMQ_QUEUE = "credit-decisions";

    public Long createApplication(CreditRequest request) {

        CreditApplication application = new CreditApplication();
        application.setAmount(request.getAmount());
        application.setTerm(request.getTerm());
        application.setIncome(request.getIncome());
        application.setCurrentDebt(request.getCurrentDebt());
        application.setCreditRating(request.getCreditRating());
        application.setStatus("в обработке");

        CreditApplication saved = creditRepository.save(application);
        log.info("Заявка сохранена в БД с ID: {}", saved.getId());

        CreditApplicationEvent event = new CreditApplicationEvent(
                saved.getId(),
                saved.getAmount(),
                saved.getTerm(),
                saved.getIncome(),
                saved.getCurrentDebt(),
                saved.getCreditRating()
        );

        kafkaTemplate.send(KAFKA_TOPIC, event);
        log.info("Заявка ID {} отправлена в Kafka", saved.getId());

        return saved.getId();
    }

    public CreditStatusResponse getStatus(Long id) {

        Optional<CreditApplication> application = creditRepository.findById(id);

        if (application.isPresent()) {
            CreditApplication app = application.get();

            return new CreditStatusResponse(
                    app.getId(),
                    app.getStatus(),
                    app.getAmount(),
                    app.getTerm()
            );
        }

        return null;
    }

    @RabbitListener(queues = RABBITMQ_QUEUE)
    public void handleDecision(CreditDecisionEvent decision) {
        log.info("Получено решение из RabbitMQ для заявки ID: {}",
                decision.getApplicationId());

        Optional<CreditApplication> application =
                creditRepository.findById(decision.getApplicationId());

        if (application.isPresent()) {
            CreditApplication app = application.get();

            if (decision.isApproved()) {
                app.setStatus("одобрено");
                log.info("Заявка ID {} одобрена", app.getId());
            } else {
                app.setStatus("отказано");
                log.info("Заявка ID {} отклонена. Причина: {}",
                        app.getId(), decision.getReason());
            }

            creditRepository.save(app);
        } else {
            log.warn("Заявка ID {} не найдена в БД",
                    decision.getApplicationId());
        }
    }
}