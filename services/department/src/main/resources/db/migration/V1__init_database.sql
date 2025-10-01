CREATE TABLE departments (
                             id BIGSERIAL PRIMARY KEY,
                             org_id BIGINT NOT NULL,
                             name VARCHAR(255),
                             manager_id BIGINT,
                             parent_id BIGINT,
                             CONSTRAINT fk_department_parent FOREIGN KEY (parent_id) REFERENCES departments(id) ON DELETE SET NULL
);

CREATE INDEX idx_dept_org ON departments(org_id);
