-- ============================
-- Indexes
-- ============================
CREATE INDEX idx_org_name ON organizations (name);
CREATE INDEX idx_org_address_city ON organization_addresses (city);
CREATE INDEX idx_org_address_country ON organization_addresses (country);

-- ============================
-- Insert initial data
-- ============================

-- Organizations
INSERT INTO organizations (name, website, currency, tax_number)
VALUES ('EPAM Systems', 'https://www.epam.com', 'USD', 'TAX123456'),
       ('SoftServe', 'https://www.softserveinc.com', 'EUR', 'TAX987654'),
       ('GlobalLogic', 'https://www.globallogic.com', 'UAH', 'TAX246810');

-- Addresses
INSERT INTO organization_addresses (house_number, street, city, state,
                                    postal_code, country, organization_id)
VALUES ('12A', 'Main Street', 'New York', 'NY', '10001', 'USA', 1),
       ('45', 'Innovation Drive', 'Lviv', 'Lvivska', '79000', 'Ukraine', 2),
       ('9B', 'Tech Park', 'Kyiv', 'Kyivska', '02000', 'Ukraine', 3),
       ('101', 'Software Ave', 'Berlin', 'Berlin', '10115', 'Germany', 1),
       ('33', 'Green Valley', 'Warsaw', 'Mazowieckie', '00-001', 'Poland', 2);
