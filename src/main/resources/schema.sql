CREATE TABLE reservation_time
(
    id       BIGINT       NOT NULL AUTO_INCREMENT,
    start_at VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE theme
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    img_url     VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE market
(
    id   BIGINT       NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE member
(
    id        BIGINT       NOT NULL AUTO_INCREMENT,
    email     VARCHAR(255) NOT NULL,
    password  VARCHAR(255) NOT NULL,
    name      VARCHAR(255) NOT NULL,
    role      VARCHAR(20)  NOT NULL DEFAULT 'USER'
        CHECK (role IN ('USER', 'MANAGER')),
    market_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT uq_member_email UNIQUE (email),
    FOREIGN KEY (market_id) REFERENCES market (id)
);

CREATE TABLE reservation
(
    id        BIGINT       NOT NULL AUTO_INCREMENT,
    member_id BIGINT       NOT NULL,
    date      VARCHAR(255) NOT NULL,
    time_id   BIGINT       NOT NULL,
    theme_id  BIGINT       NOT NULL,
    market_id BIGINT       NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (member_id) REFERENCES member (id),
    FOREIGN KEY (time_id) REFERENCES reservation_time (id),
    FOREIGN KEY (theme_id) REFERENCES theme (id),
    FOREIGN KEY (market_id) REFERENCES market (id),
    CONSTRAINT uq_reservation_market_date_time_theme UNIQUE (market_id, date, time_id, theme_id)
);
