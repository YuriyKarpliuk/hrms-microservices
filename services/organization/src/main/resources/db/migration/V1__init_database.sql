-- ============================
-- Create table organizations
-- ============================
CREATE TABLE organizations (
                               id BIGSERIAL PRIMARY KEY,
                               name VARCHAR(255) NOT NULL UNIQUE,
                               website VARCHAR(255),
                               currency VARCHAR(10),
                               tax_number VARCHAR(50)
);

-- ============================
-- Create table organization_addresses
-- ============================
CREATE TABLE organization_addresses (
                                        id BIGSERIAL PRIMARY KEY,
                                        house_number VARCHAR(50),
                                        street VARCHAR(255),
                                        city VARCHAR(255),
                                        state VARCHAR(255),
                                        postal_code VARCHAR(20),
                                        country VARCHAR(100),
                                        organization_id BIGINT NOT NULL,
                                        CONSTRAINT fk_org FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE
);
