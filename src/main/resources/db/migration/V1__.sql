CREATE TABLE instrument
(
    id              BIGINT AUTO_INCREMENT NOT NULL,
    instrument_name VARCHAR(255) NOT NULL,
    voice           VARCHAR(255) NOT NULL,
    CONSTRAINT pk_instrument PRIMARY KEY (id)
);

CREATE TABLE user_profile
(
    id                  BIGINT AUTO_INCREMENT NOT NULL,
    iam_id              VARCHAR(255) NOT NULL,
    username            VARCHAR(255) NOT NULL,
    first_name          VARCHAR(255) NOT NULL,
    last_name           VARCHAR(255) NOT NULL,
    second_last_name    VARCHAR(255) NULL,
    email               VARCHAR(255) NOT NULL,
    birth_date          date NULL,
    band_join_date      date NULL,
    system_signup_date  date NULL,
    active              BIT(1)       NOT NULL,
    phone               VARCHAR(255) NULL,
    notes               VARCHAR(255) NULL,
    profile_picture_url VARCHAR(255) NULL,
    CONSTRAINT pk_user_profile PRIMARY KEY (id)
);

CREATE TABLE user_profile_instrument
(
    instrument_id   BIGINT NOT NULL,
    user_profile_id BIGINT NOT NULL,
    CONSTRAINT pk_user_profile_instrument PRIMARY KEY (instrument_id, user_profile_id)
);

ALTER TABLE instrument
    ADD CONSTRAINT uc_02daaf6f0e93d4aaefde0e689 UNIQUE (instrument_name, voice);

ALTER TABLE user_profile
    ADD CONSTRAINT uc_user_profile_email UNIQUE (email);

ALTER TABLE user_profile
    ADD CONSTRAINT uc_user_profile_iamid UNIQUE (iam_id);

ALTER TABLE user_profile_instrument
    ADD CONSTRAINT fk_useproins_on_instrument FOREIGN KEY (instrument_id) REFERENCES instrument (id);

ALTER TABLE user_profile_instrument
    ADD CONSTRAINT fk_useproins_on_user_profile FOREIGN KEY (user_profile_id) REFERENCES user_profile (id);


INSERT INTO instrument (id,instrument_name,voice) VALUES (5,'Clarinete','1');
INSERT INTO instrument (id,instrument_name,voice) VALUES (6,'Clarinete','2');
INSERT INTO instrument (id,instrument_name,voice) VALUES (1,'Director','Principal');
INSERT INTO instrument (id,instrument_name,voice) VALUES (17,'Fagot','1');
INSERT INTO instrument (id,instrument_name,voice) VALUES (7,'Flauta','1');
INSERT INTO instrument (id,instrument_name,voice) VALUES (8,'Flauta','Principal');
INSERT INTO instrument (id,instrument_name,voice) VALUES (14,'Oboe','1');
INSERT INTO instrument (id,instrument_name,voice) VALUES (11,'Saxofón','1');
INSERT INTO instrument (id,instrument_name,voice) VALUES (12,'Saxofón','2');
INSERT INTO instrument (id,instrument_name,voice) VALUES (9,'Trombón','1');
INSERT INTO instrument (id,instrument_name,voice) VALUES (10,'Trombón','2');
INSERT INTO instrument (id,instrument_name,voice) VALUES (15,'Trompa','1');
INSERT INTO instrument (id,instrument_name,voice) VALUES (16,'Trompa','2');
INSERT INTO instrument (id,instrument_name,voice) VALUES (2,'Trompeta','1');
INSERT INTO instrument (id,instrument_name,voice) VALUES (3,'Trompeta','2');
INSERT INTO instrument (id,instrument_name,voice) VALUES (4,'Trompeta','3');
INSERT INTO instrument (id,instrument_name,voice) VALUES (13,'Tuba','1');

