CREATE TABLE monthly_approvals (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL REFERENCES employees(id),
    year_month VARCHAR(7) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    approver_id BIGINT REFERENCES employees(id),
    approved_at TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (employee_id, year_month)
);

CREATE INDEX idx_monthly_approvals_year_month ON monthly_approvals (year_month);
