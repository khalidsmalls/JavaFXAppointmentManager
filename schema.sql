SET TIMEZONE = 'UTC';

ALTER TABLE IF EXISTS appointments DROP CONSTRAINT appointments_fk_user;
ALTER TABLE IF EXISTS appointments DROP CONSTRAINT appointments_fk_client;
ALTER TABLE IF EXISTS appointments DROP CONSTRAINT appointments_fk_contact;
ALTER TABLE IF EXISTS administrative_divisions DROP CONSTRAINT divisions_fk_country; 
ALTER TABLE IF EXISTS clients DROP CONSTRAINT clients_fk_division;

DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS contacts;
DROP TABLE IF EXISTS appointments;
DROP TABLE IF EXISTS clients;
DROP TABLE IF EXISTS administrative_divisions;
DROP TABLE IF EXISTS countries;

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
  description VARCHAR (50),
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



INSERT INTO users VALUES(1, 'user', 'user', NOW(), 'script', NOW(), 'script');
INSERT INTO users VALUES(2, 'admin', 'admin', NOW(), 'script', NOW(), 'script');


INSERT INTO contacts VALUES(1,	'Anika Costa', 'acoasta@company.com');
INSERT INTO contacts VALUES(2,	'Daniel Garcia',	'dgarcia@company.com');
INSERT INTO contacts VALUES(3,	'Li Lee',	'llee@company.com');


INSERT INTO countries VALUES(1,	'U.S',	NOW(), 'script', NOW(), 'script');
INSERT INTO countries VALUES(2,	'UK',	NOW(), 'script', NOW(), 'script');
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


INSERT INTO clients VALUES(1, 'Daddy Warbucks', '1919 Boardwalk', '01291', '869-908-1875', NOW(), 'script', NOW(), 'script', 29);
INSERT INTO clients VALUES(2, 'Lady McAnderson', '2 Wonder Way', 'AF19B', '11-445-910-2135', NOW(), 'script', NOW(), 'script', 103);
INSERT INTO clients VALUES(3, 'Dudley Do-Right', '48 Horse Manor ', '28198', '874-916-2671', NOW(), 'script', NOW(), 'script', 60);


INSERT INTO appointments VALUES(1, 'description', 'location', 'Planning Session', '2020-05-28 12:00:00', '2020-05-28 13:00:00', NOW(), 'script', NOW(), 'script', 1, 1, 3);
INSERT INTO appointments VALUES(2, 'description', 'location', 'De-Briefing', '2020-05-29 12:00:00', '2020-05-29 13:00:00', NOW(), 'script', NOW(), 'script', 2, 2, 2);
