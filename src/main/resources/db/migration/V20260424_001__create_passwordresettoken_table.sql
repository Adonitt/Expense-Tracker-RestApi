CREATE TABLE password_reset_token
(
    id          BIGINT,
    token       VARCHAR(255),
    expiry_date TIMESTAMP WITHOUT TIME ZONE,
    user_id     BIGINT
);

ALTER TABLE password_reset_token
    ADD CONSTRAINT FK_PASSWORDRESETTOKEN_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);