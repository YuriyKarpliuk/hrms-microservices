CREATE TABLE payrolls
(
    id           BIGSERIAL PRIMARY KEY,
    employee_id  BIGINT         NOT NULL,
    period_start DATE           NOT NULL,
    period_end   DATE           NOT NULL,
    base_salary  NUMERIC(15, 2) NOT NULL,
    bonus        NUMERIC(15, 2),
    deductions   NUMERIC(15, 2),
    net_salary   NUMERIC(15, 2) NOT NULL,
    status       VARCHAR(50)    NOT NULL
);
