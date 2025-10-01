INSERT INTO payrolls (employee_id, period_start, period_end, base_salary, bonus,
                      deductions, net_salary, status)
VALUES (1, '2025-01-01', '2025-01-31', 3000.00, 500.00, 200.00, 3300.00,
        'PAID'),
       (2, '2025-01-01', '2025-01-31', 4000.00, NULL, 100.00, 3900.00,
        'PENDING'),
       (1, '2025-02-01', '2025-02-28', 3000.00, 300.00, 150.00, 3150.00,
        'APPROVED'),
       (3, '2025-01-01', '2025-01-31', 2500.00, 0.00, 50.00, 2450.00,
        'REJECTED');
