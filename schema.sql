SET TIMEZONE = 'UTC';

ALTER TABLE IF EXISTS appointments DROP CONSTRAINT appointments_fk_user;
ALTER TABLE IF EXISTS appointments DROP CONSTRAINT appointments_fk_client;
ALTER TABLE IF EXISTS appointments DROP CONSTRAINT appointments_fk_contact;
ALTER TABLE IF EXISTS administrative_divisions DROP CONSTRAINT divisions_fk_country; 
ALTER TABLE IF EXISTS clients DROP CONSTRAINT clients_fk_division;

DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS contacts CASCADE;
DROP TABLE IF EXISTS appointments CASCADE;
DROP TABLE IF EXISTS clients CASCADE;
DROP TABLE IF EXISTS administrative_divisions CASCADE;
DROP TABLE IF EXISTS countries CASCADE;
DROP TABLE IF EXISTS appointment_report;
DROP TABLE IF EXISTS contact_report;
DROP VIEW IF EXISTS appointment_report_view;

CREATE TABLE countries (
  country_id SERIAL PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  create_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
  created_by VARCHAR(50) DEFAULT NULL,
  last_update TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_updated_by VARCHAR(50) DEFAULT NULL
);

CREATE TABLE administrative_divisions (
  division_id SERIAL PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  create_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
  created_by VARCHAR(50),
  last_update TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_updated_by VARCHAR(50),
  country_id INTEGER NOT NULL,
  CONSTRAINT divisions_fk_country FOREIGN KEY (country_id) 
    REFERENCES countries (country_id)
);

CREATE TABLE clients (
  client_id SERIAL PRIMARY KEY,
  name VARCHAR (50) NOT NULL,
  address VARCHAR (100) NOT NULL,
  postal_code VARCHAR (50) NOT NULL,
  phone VARCHAR (50) NOT NULL,
  create_date  TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
  created_by VARCHAR (50),
  last_update TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_updated_by VARCHAR (50),
  division_id INTEGER NOT NULL,
  CONSTRAINT clients_fk_division FOREIGN KEY (division_id) 
    REFERENCES administrative_divisions (division_id)
);

CREATE TABLE users (
  user_id SERIAL PRIMARY KEY,
  username VARCHAR(50) UNIQUE,
  password text,
  create_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
  created_by VARCHAR(50),
  last_update TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_updated_by VARCHAR(50)
);

CREATE TABLE contacts (
  contact_id SERIAL PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  email VARCHAR(50) NOT NULL,
  create_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
  created_by VARCHAR(50),
  last_update TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_updated_by VARCHAR(50)
);

CREATE TABLE appointments (
  appointment_id SERIAL PRIMARY KEY,
  description VARCHAR (100),
  location VARCHAR (50) NOT NULL,
  appointment_type VARCHAR (50),
  start_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
  end_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
  create_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
  created_by VARCHAR (50),
  last_update TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_updated_by VARCHAR (50),
  client_id INTEGER DEFAULT NULL,
  user_id INTEGER DEFAULT NULL,
  contact_id INTEGER DEFAULT NULL,
  CONSTRAINT appointments_fk_client FOREIGN KEY (client_id) 
    REFERENCES clients (client_id),
  CONSTRAINT appointments_fk_user FOREIGN KEY (user_id) 
    REFERENCES users (user_id),
  CONSTRAINT appointments_fk_contact FOREIGN KEY (contact_id) 
    REFERENCES contacts (contact_id)
); 

CREATE TABLE appointment_report (
  appointment_id INTEGER,
  description VARCHAR(100),
  location VARCHAR(50),
  appointment_type VARCHAR(50),
  appointment_date DATE,
  start_time TIME,
  end_time TIME,
  client VARCHAR(50),
  address VARCHAR(50),
  postal_code VARCHAR(50),
  division VARCHAR(50),
  country VARCHAR(50),
  phone VARCHAR(15),
  contact_id INTEGER,
  contact VARCHAR(50)
);

CREATE TABLE contact_report(
  contact_id INTEGER,
  contact VARCHAR(50),
  num_appointments INTEGER,
  total_appointment_hrs NUMERIC(5, 2)
);

CREATE OR REPLACE FUNCTION calculate_contact_appointment_hours(_contact_id INTEGER)
  RETURNS NUMERIC(5,2)
  LANGUAGE PLPGSQL
AS $$
DECLARE
  total_hrs NUMERIC(5,2) := 0;
BEGIN
  SELECT
    SUM(EXTRACT(EPOCH FROM (end_time - start_time)) / 3600) INTO total_hrs
  FROM appointments
  WHERE contact_id = _contact_id;
RETURN total_hrs;
END; $$;

CREATE VIEW appointment_report_view AS
  SELECT
    appointment_id,
    description,
    location,
    appointment_type,
    start_time::DATE AS date,
    start_time::TIME,
    end_time::TIME,
    client.name AS client,
    client.address,
    client.postal_code,
    div.name AS division,
    country.name AS country,
    client.phone,
    contact.contact_id,
    contact.name AS contact
  FROM
    appointments a
  JOIN clients client
    ON a.client_id = client.client_id
  JOIN administrative_divisions div 
    ON client.division_id = div.division_id
  JOIN countries country
    ON div.country_id = country.country_id
  JOIN contacts contact 
    ON a.contact_id = contact.contact_id
  ORDER BY appointment_id DESC;

CREATE OR REPLACE FUNCTION refresh_contact_report()
  RETURNS TRIGGER
  LANGUAGE PLPGSQL
AS $$
BEGIN
  DELETE FROM contact_report;
  INSERT INTO contact_report(
    SELECT 
      contact_id,
      contact,
      COUNT(*) AS num_appointments,
      calculate_contact_appointment_hours(contact_id)
    FROM appointment_report
    GROUP BY contact_id, contact
    ORDER BY contact_id
  );
RETURN NEW;
END; $$;

CREATE OR REPLACE TRIGGER contact_report_trigger
  AFTER INSERT
  ON appointment_report
  FOR EACH STATEMENT
  EXECUTE PROCEDURE refresh_contact_report();

CREATE OR REPLACE PROCEDURE create_reports()
LANGUAGE PLPGSQL
AS $$
BEGIN
  DELETE FROM appointment_report;
  DELETE FROM contact_report;
  INSERT INTO appointment_report(
    SELECT * FROM appointment_report_view
  );
END; $$;


INSERT INTO users VALUES(1, 'user', 'user', NOW(), 'script', NOW(), 'script');
INSERT INTO users VALUES(2, 'admin', 'admin', NOW(), 'script', NOW(), 'script');


INSERT INTO contacts VALUES(1,	'sergio perez', 'sperez@company.com');
INSERT INTO contacts VALUES(2,	'daniel suarez',	'dsuarez@company.com');
INSERT INTO contacts VALUES(3,	'patricio o''ward',	'pato@company.com');


INSERT INTO countries VALUES(1,	'United States',	NOW(), 'script', NOW(), 'script');
INSERT INTO countries VALUES(2,	'United Kingdom',	NOW(), 'script', NOW(), 'script');
INSERT INTO countries VALUES(3,	'Canada',	NOW(), 'script', NOW(), 'script');


INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Alabama', 1, NOW(), 'script', NOW(), 'script', 1 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Alaska', 54, NOW(), 'script', NOW(), 'script', 1 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Arizona', 02, NOW(), 'script', NOW(), 'script', 1 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Arkansas', 03, NOW(), 'script', NOW(), 'script', 1 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('California', 04, NOW(), 'script', NOW(), 'script', 1 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Colorado', 05, NOW(), 'script', NOW(), 'script', 1 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Connecticut', 06, NOW(), 'script', NOW(), 'script', 1 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Delaware', 07, NOW(), 'script', NOW(), 'script', 1 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('District of Columbia', 08, NOW(), 'script', NOW(), 'script', 1 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Florida', 09, NOW(), 'script', NOW(), 'script', 1 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Georgia', 10, NOW(), 'script', NOW(), 'script', 1 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Hawaii', 52, NOW(), 'script', NOW(), 'script', 1 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Idaho', 11, NOW(), 'script', NOW(), 'script', 1 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Illinois', 12, NOW(), 'script', NOW(), 'script', 1 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Indiana', 13, NOW(), 'script', NOW(), 'script', 1 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Iowa', 14, NOW(), 'script', NOW(), 'script', 1 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Kansas', 15, NOW(), 'script', NOW(), 'script', 1 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Kentucky', 16, NOW(), 'script', NOW(), 'script', 1 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Louisiana', 17, NOW(), 'script', NOW(), 'script', 1 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Maine', 18, NOW(), 'script', NOW(), 'script', 1 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Maryland', 19, NOW(), 'script', NOW(), 'script', 1 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Massachusetts', 20, NOW(), 'script', NOW(), 'script', 1 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Michigan', 21, NOW(), 'script', NOW(), 'script', 1 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Minnesota', 22, NOW(), 'script', NOW(), 'script', 1 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Mississippi', 23, NOW(), 'script', NOW(), 'script', 1 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Missouri', 24, NOW(), 'script', NOW(), 'script', 1 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Montana', 25, NOW(), 'script', NOW(), 'script', 1 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Nebraska', 26, NOW(), 'script', NOW(), 'script', 1 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Nevada', 27, NOW(), 'script', NOW(), 'script', 1 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('New Hampshire', 28, NOW(), 'script', NOW(), 'script', 1 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('New Jersey', 29, NOW(), 'script', NOW(), 'script', 1 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('New Mexico', 30, NOW(), 'script', NOW(), 'script', 1 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('New York', 31, NOW(), 'script', NOW(), 'script', 1 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('North Carolina', 32, NOW(), 'script', NOW(), 'script', 1 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('North Dakota', 33, NOW(), 'script', NOW(), 'script', 1 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Ohio', 34, NOW(), 'script', NOW(), 'script', 1 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Oklahoma', 35, NOW(), 'script', NOW(), 'script', 1 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Oregon', 36, NOW(), 'script', NOW(), 'script', 1 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Pennsylvania', 37, NOW(), 'script', NOW(), 'script', 1 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Rhode Island', 38, NOW(), 'script', NOW(), 'script', 1 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('South Carolina', 39, NOW(), 'script', NOW(), 'script', 1 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('South Dakota', 40, NOW(), 'script', NOW(), 'script', 1 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Tennessee', 41, NOW(), 'script', NOW(), 'script', 1 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Texas', 42, NOW(), 'script', NOW(), 'script', 1 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Utah', 43, NOW(), 'script', NOW(), 'script', 1 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Vermont', 44, NOW(), 'script', NOW(), 'script', 1 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Virginia', 45, NOW(), 'script', NOW(), 'script', 1 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Washington', 46, NOW(), 'script', NOW(), 'script', 1 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('West Virginia', 47, NOW(), 'script', NOW(), 'script', 1 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Wisconsin', 48, NOW(), 'script', NOW(), 'script', 1 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Wyoming', 49, NOW(), 'script', NOW(), 'script', 1 );


INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Alberta', 61, NOW(), 'script', NOW(), 'script', 3 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('British Columbia', 62, NOW(), 'script', NOW(), 'script', 3 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Manitoba', 63, NOW(), 'script', NOW(), 'script', 3 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('New Brunswick', 64, NOW(), 'script', NOW(), 'script', 3 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Newfoundland and Labrador', 72, NOW(), 'script', NOW(), 'script', 3 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Northwest Territories', 60, NOW(), 'script', NOW(), 'script', 3 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Nova Scotia', 65, NOW(), 'script', NOW(), 'script', 3 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Nunavut', 70, NOW(), 'script', NOW(), 'script', 3 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Ontario', 67, NOW(), 'script', NOW(), 'script', 3 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Prince Edward Island', 66, NOW(), 'script', NOW(), 'script', 3 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('QuÃ©bec', 68, NOW(), 'script', NOW(), 'script', 3 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Saskatchewan', 69, NOW(), 'script', NOW(), 'script', 3 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Yukon', 71, NOW(), 'script', NOW(), 'script', 3 );

INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('England', 101, NOW(), 'script', NOW(), 'script', 2 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Wales', 102, NOW(), 'script', NOW(), 'script', 2 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Scotland',103, NOW(), 'script', NOW(), 'script', 2 );
INSERT INTO administrative_divisions(name, division_id, create_date, created_by, last_update, last_updated_by, country_id) VALUES('Northern Ireland', 104, NOW(), 'script', NOW(), 'script', 2 );


--INSERT INTO clients VALUES(1, 'Betty Rubble', '345 Cave Stone Rd', '01291', '869-908-1875', NOW(), 'script', NOW(), 'script', 29);
--INSERT INTO clients VALUES(2, 'Scrappy Doo', '123 Evergreen Ter', 'AF19B', '11-445-910-2135', NOW(), 'script', NOW(), 'script', 103);
--INSERT INTO clients VALUES(3, 'Hong Kong Phooey', '48 Lotus Manor ', '28198', '874-916-2671', NOW(), 'script', NOW(), 'script', 60);


--INSERT INTO appointments VALUES(1, 'strategy session', 'lounge', 'daily scrum', '2020-05-28 12:00:00', '2020-05-28 13:00:00', NOW(), 'script', NOW(), 'script', 1, 1, 3);
--INSERT INTO appointments VALUES(2, 'review safety best-practices', 'courtyard', 'Safety-Briefing', '2020-05-29 12:00:00', '2020-05-29 13:00:00', NOW(), 'script', NOW(), 'script', 2, 2, 2);
