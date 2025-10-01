INSERT INTO departments (org_id, name, manager_id, parent_id)
VALUES
    (1, 'Head Office', NULL, NULL),
    (1, 'HR Department', 101, 1),
    (1, 'Engineering Department', 102, 1),
    (1, 'Frontend Team', 201, 3),
    (1, 'Backend Team', 202, 3);

INSERT INTO departments (org_id, name, manager_id, parent_id)
VALUES
    (2, 'School Management', NULL, NULL),
    (2, 'Teachers Department', 301, 6),
    (2, 'Math Team', 302, 7),
    (2, 'Science Team', 303, 7);
