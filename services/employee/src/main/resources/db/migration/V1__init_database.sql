-- Створення таблиці employees (синхронізовано з твоєю ентіті)

create table employees (
                           id                     bigserial primary key,

                           org_id                 bigint      not null,
                           user_id                varchar(64) not null,

                           dept_id                bigint,
                           position_id            bigint,
                           manager_id             bigint,
                           hr_id                  bigint,

                           email                  varchar(255) not null,
                           first_name             varchar(100) not null,
                           last_name              varchar(100) not null,
                           phone                  varchar(50),

                           status                 varchar(32)  not null,
                           gender                 varchar(16),
                           marital_status         varchar(16),

                           tax_number             varchar(64),
                           about                  text,
                           office_location        varchar(255),

                           birth_date             date,
                           hired_at               date,
                           terminated_at          date,

                           avatar_url             varchar(512),

                           languages_json         jsonb default '[]'::jsonb,
                           address_json           jsonb default '{}'::jsonb,
                           education_json         jsonb default '[]'::jsonb,
                           work_experience_json   jsonb default '[]'::jsonb,
                           cv_key                 varchar(255),
                           profile_json           jsonb default '{}'::jsonb
);

alter table employees
    add constraint uq_employees_org_email unique (org_id, email);

alter table employees
    add constraint ck_employees_status
        check (status in ('ACTIVE','INACTIVE','ON_LEAVE','TERMINATED'));

alter table employees
    add constraint ck_employees_gender
        check (gender is null or gender in ('MALE','FEMALE'));

alter table employees
    add constraint ck_employees_marital
        check (marital_status is null or marital_status in ('SINGLE','MARRIED'));

alter table employees
    add constraint ck_employees_term_date
        check (status <> 'TERMINATED' or terminated_at is not null);

alter table employees
    add constraint ck_employees_dates_order
        check (terminated_at is null or hired_at is null or terminated_at >= hired_at);

create index idx_emp_org           on employees(org_id);
create index idx_emp_dept          on employees(dept_id);
create index idx_emp_org_status    on employees(org_id, status);
