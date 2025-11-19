ALTER TABLE timesheet_entries
DROP CONSTRAINT IF EXISTS timesheet_entries_activity_type_check;

ALTER TABLE timesheet_entries
ADD CONSTRAINT timesheet_entries_activity_type_check
CHECK (
    activity_type IN (
        'REGULAR', 'OVERTIME', 'VACATION', 'SICK', 'UNPAID',
        'TRAINING', 'DEVELOPMENT', 'TESTING', 'MEETING',
        'RESEARCH', 'SUPPORT'
    )
);
