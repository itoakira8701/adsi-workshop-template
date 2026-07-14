CREATE TABLE attendances (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL REFERENCES employees(id),
    work_date DATE NOT NULL,
    clock_in_time TIME NOT NULL,
    clock_out_time TIME,
    working_minutes INTEGER,
    overtime_minutes INTEGER,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (employee_id, work_date)
);

CREATE INDEX idx_attendances_employee_work_date ON attendances (employee_id, work_date);
