-- RESET AUTO_INCREMENT COUNTERS
ALTER TABLE user AUTO_INCREMENT = 1;
ALTER TABLE portfolio AUTO_INCREMENT = 1;
ALTER TABLE investment AUTO_INCREMENT = 1;
ALTER TABLE transaction AUTO_INCREMENT = 1;
ALTER TABLE dividend AUTO_INCREMENT = 1;
ALTER TABLE investment_audit AUTO_INCREMENT = 1;

-- USERS
INSERT INTO user (username, password, role, active) VALUES
('admin', '$2a$10$vvf6gDcj0v0aoMCxq6slseslwoQEjewjOy28J0Fn2DNAx0GuHCCSi', 'ADMIN', TRUE),
('john_doe', '$2a$10$vvf6gDcj0v0aoMCxq6slseslwoQEjewjOy28J0Fn2DNAx0GuHCCSi', 'USER', TRUE),
('jane_smith', '$2a$10$vvf6gDcj0v0aoMCxq6slseslwoQEjewjOy28J0Fn2DNAx0GuHCCSi', 'USER', TRUE),
('alice_wonder', '$2a$10$vvf6gDcj0v0aoMCxq6slseslwoQEjewjOy28J0Fn2DNAx0GuHCCSi', 'USER', TRUE),
('bob_marley', '$2a$10$vvf6gDcj0v0aoMCxq6slseslwoQEjewjOy28J0Fn2DNAx0GuHCCSi', 'USER', FALSE); -- INACTIVE USER

-- PORTFOLIOS
INSERT INTO portfolio (portfolio_name, owner, total_value, user_id, active) VALUES
('Tech Growth Fund', 'John Doe', 120000.00, 2, TRUE),
('Retirement Plan', 'Jane Smith', 95000.00, 3, TRUE),
('Crypto Portfolio', 'John Doe', 60000.00, 2, TRUE),
('High Risk Portfolio', 'Alice Wonder', 40000.00, 4, TRUE),
('Dividend Stocks', 'Bob Marley', 85000.00, 5, FALSE);

-- INVESTMENTS
INSERT INTO investment (investment_name, investment_type, amount, purchase_date, sell_date, current_value, profit_loss, portfolio_id, active) VALUES
('Apple Inc. Stock', 'Stock', 30000.00, '2023-01-01', NULL, 35000.00, 5000.00, 1, TRUE),
('Ethereum', 'Crypto', 20000.00, '2023-05-10', NULL, 18000.00, -2000.00, 3, TRUE),
('Tesla Inc. Stock', 'Stock', 25000.00, '2022-11-20', '2023-12-20', 28000.00, 3000.00, 1, TRUE),
('Real Estate Fund', 'REIT', 15000.00, '2021-06-15', NULL, 16000.00, 1000.00, 2, TRUE),
('Bitcoin', 'Crypto', 45000.00, '2021-08-10', NULL, 60000.00, 15000.00, 3, TRUE),
('Amazon Inc. Stock', 'Stock', 35000.00, '2023-03-15', NULL, 38000.00, 3000.00, 4, TRUE),
('Microsoft Corp.', 'Stock', 42000.00, '2022-12-01', NULL, 46000.00, 4000.00, 5, FALSE),
('Gold ETF', 'Commodity', 15000.00, '2023-02-05', NULL, 17000.00, 2000.00, 4, TRUE);

-- TRANSACTIONS
INSERT INTO transaction (investment_id, transaction_date, transaction_type, amount) VALUES
(1, '2023-01-01', 'BUY', 30000.00),
(2, '2023-05-10', 'BUY', 20000.00),
(3, '2023-12-20', 'SELL', 28000.00),
(4, '2021-06-15', 'BUY', 15000.00),
(5, '2021-08-10', 'BUY', 45000.00),
(6, '2023-03-15', 'BUY', 35000.00),
(7, '2022-12-01', 'BUY', 42000.00),
(8, '2023-02-05', 'BUY', 15000.00);

-- DIVIDENDS
INSERT INTO dividend (investment_id, payment_date, amount) VALUES
(1, '2023-03-15', 500.00),
(1, '2023-06-15', 700.00),
(4, '2023-09-10', 400.00),
(4, '2023-12-20', 450.00),
(5, '2023-10-05', 800.00),
(6, '2023-07-15', 600.00),
(7, '2023-11-20', 900.00),
(8, '2023-08-25', 550.00);

-- INVESTMENT AUDIT LOGS
INSERT INTO investment_audit (investment_id, change_type, change_date, old_value, new_value) VALUES
(1, 'Value Update', '2023-06-15 10:30:00', 30000.00, 35000.00),
(2, 'Value Update', '2023-07-01 14:00:00', 20000.00, 18000.00),
(3, 'Profit/Loss Update', '2023-12-21 12:45:00', 25000.00, 28000.00),
(5, 'Value Update', '2023-11-10 09:15:00', 45000.00, 60000.00),
(6, 'Dividend Paid', '2023-09-18 13:25:00', NULL, 600.00),
(7, 'Profit/Loss Update', '2023-11-30 15:50:00', 42000.00, 46000.00);
