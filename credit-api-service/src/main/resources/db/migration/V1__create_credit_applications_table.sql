
CREATE TABLE IF NOT EXISTS credit_applications (

    id BIGSERIAL PRIMARY KEY,
    amount DOUBLE PRECISION NOT NULL,
    term INTEGER NOT NULL,
    income DOUBLE PRECISION NOT NULL,
    current_debt DOUBLE PRECISION NOT NULL,
    credit_rating INTEGER NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'в обработке',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

CREATE INDEX IF NOT EXISTS idx_credit_applications_id ON credit_applications(id);

CREATE INDEX IF NOT EXISTS idx_credit_applications_status ON credit_applications(status);


COMMENT ON TABLE credit_applications IS 'Заявки пользователей на кредит';
COMMENT ON COLUMN credit_applications.amount IS 'Сумма запрошенного кредита';
COMMENT ON COLUMN credit_applications.term IS 'Срок кредита в месяцах';
COMMENT ON COLUMN credit_applications.income IS 'Ежемесячный доход заявителя';
COMMENT ON COLUMN credit_applications.current_debt IS 'Текущие платежи по другим кредитам';
COMMENT ON COLUMN credit_applications.credit_rating IS 'Кредитный рейтинг (300-850)';
COMMENT ON COLUMN credit_applications.status IS 'Статус заявки: в обработке/одобрено/отказано';