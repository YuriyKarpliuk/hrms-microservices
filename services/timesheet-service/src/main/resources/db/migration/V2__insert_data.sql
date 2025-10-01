-- Insert sample timesheets
INSERT INTO timesheets (employee_id, week_start, week_end, status)
VALUES (1, '2025-09-01', '2025-09-07', 'SUBMITTED'),
       (2, '2025-09-01', '2025-09-07', 'APPROVED');

-- Insert sample timesheet entries
INSERT INTO timesheet_entries (work_date, project, notes, hours, activity_type,
                               timesheet_id)
VALUES ('2025-09-01', 'HRMS', 'Worked on backend services', 8, 'WORK', 1),
       ('2025-09-02', 'HRMS', 'Frontend bugfixes', 6.5, 'WORK', 1),
       ('2025-09-03', 'HRMS', 'Team meeting', 2, 'MEETING', 1),
       ('2025-09-01', 'Analytics', 'Prepared reports', 7, 'WORK', 2),
       ('2025-09-02', 'Analytics', 'Research', 5.5, 'TRAINING', 2);
