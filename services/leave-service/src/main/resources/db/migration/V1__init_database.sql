CREATE TABLE leaves
(
    id          BIGSERIAL PRIMARY KEY,
    employee_id BIGINT      NOT NULL,
    type        VARCHAR(50) NOT NULL,
    start_date  DATE        NOT NULL,
    end_date    DATE        NOT NULL,
    status      VARCHAR(50) NOT NULL,
    action_by BIGINT      NOT NULL,
    reason      TEXT
);
