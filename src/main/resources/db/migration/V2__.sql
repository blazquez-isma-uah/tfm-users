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
    role_names          VARCHAR(255) NULL,
    CONSTRAINT pk_user_profile PRIMARY KEY (id)
);

CREATE TABLE user_profile_instrument
(
    instrument_id   BIGINT NOT NULL,
    user_profile_id BIGINT NOT NULL,
    CONSTRAINT pk_user_profile_instrument PRIMARY KEY (instrument_id, user_profile_id)
);

ALTER TABLE instrument
    ADD CONSTRAINT uc_3907ad00a91e459a40092ab36 UNIQUE (instrument_name, voice);

ALTER TABLE user_profile
    ADD CONSTRAINT uc_user_profile_email UNIQUE (email);

ALTER TABLE user_profile
    ADD CONSTRAINT uc_user_profile_iam UNIQUE (iam_id);

ALTER TABLE user_profile
    ADD CONSTRAINT uc_user_profile_username UNIQUE (username);

ALTER TABLE user_profile_instrument
    ADD CONSTRAINT fk_useproins_on_instrument_entity FOREIGN KEY (instrument_id) REFERENCES instrument (id);

ALTER TABLE user_profile_instrument
    ADD CONSTRAINT fk_useproins_on_user_profile_entity FOREIGN KEY (user_profile_id) REFERENCES user_profile (id);