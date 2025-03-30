-- Table for Users (Authentication & Authorization)
CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('USER', 'ADMIN')),
    active BOOLEAN NOT NULL DEFAULT TRUE
);

-- Table for Portfolios
CREATE TABLE portfolio (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    portfolio_name VARCHAR(255) NOT NULL,
    owner VARCHAR(255) NOT NULL,
    total_value DECIMAL(15,2) NOT NULL,
    user_id BIGINT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_portfolio_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

-- Table for Investments
CREATE TABLE investment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    investment_name VARCHAR(255) NOT NULL,
    investment_type VARCHAR(255) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    purchase_date DATE NOT NULL,
    sell_date DATE,
    current_value DECIMAL(15,2),
    profit_loss DECIMAL(15,2),
    portfolio_id BIGINT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_investment_portfolio FOREIGN KEY (portfolio_id) REFERENCES portfolio(id) ON DELETE CASCADE
);

-- Table for Transactions (Buy/Sell history)
CREATE TABLE transaction (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    investment_id BIGINT NOT NULL,
    transaction_date DATE NOT NULL,
    transaction_type ENUM('BUY', 'SELL') NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    CONSTRAINT fk_transaction_investment FOREIGN KEY (investment_id) REFERENCES investment(id) ON DELETE CASCADE
);

-- Table for Dividends (Dividend Payments)
CREATE TABLE dividend (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    investment_id BIGINT NOT NULL,
    payment_date DATE NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    CONSTRAINT fk_dividend_investment FOREIGN KEY (investment_id) REFERENCES investment(id) ON DELETE CASCADE
);

-- Table for Investment Audit (Tracks updates)
CREATE TABLE investment_audit (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    investment_id BIGINT NOT NULL,
    change_type VARCHAR(50) NOT NULL,
    change_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    old_value DECIMAL(15,2),
    new_value DECIMAL(15,2),
    CONSTRAINT fk_audit_investment FOREIGN KEY (investment_id) REFERENCES investment(id) ON DELETE CASCADE
);

-- Indexes for Performance Optimization
CREATE INDEX idx_user_username ON user(username);
CREATE INDEX idx_portfolio_owner ON portfolio(owner);
CREATE INDEX idx_investment_type ON investment(investment_type);
CREATE INDEX idx_investment_portfolio_id ON investment(portfolio_id);
CREATE INDEX idx_transaction_investment ON transaction(investment_id);
CREATE INDEX idx_dividend_investment ON dividend(investment_id);
CREATE INDEX idx_audit_investment ON investment_audit(investment_id);
