-- ================================
-- Create table: timesheets
-- ================================
CREATE TABLE timesheets
(
    id          BIGSERIAL PRIMARY KEY,
    employee_id BIGINT      NOT NULL,
    week_start  DATE        NOT NULL,
    week_end    DATE        NOT NULL,
    status      VARCHAR(50) NOT NULL
);

-- ================================
-- Create table: timesheet_entries
-- ================================
CREATE TABLE timesheet_entries
(
    id            BIGSERIAL PRIMARY KEY,
    work_date     DATE        NOT NULL,
    project       VARCHAR(255),
    notes         TEXT,
    hours         DOUBLE PRECISION,
    activity_type VARCHAR(50) NOT NULL,
    timesheet_id  BIGINT      NOT NULL,
    CONSTRAINT fk_timesheet FOREIGN KEY (timesheet_id)
        REFERENCES timesheets (id) ON DELETE CASCADE
);
