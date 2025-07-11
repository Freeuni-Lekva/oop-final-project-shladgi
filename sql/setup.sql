-- create this database if you dont have it
USE quizKhana;

-- DROP TABLES
DROP TABLE IF EXISTS announcements;
DROP TABLE IF EXISTS user_answers;
DROP TABLE IF EXISTS notes;
DROP TABLE IF EXISTS challenges;
DROP TABLE IF EXISTS friend_requests;
DROP TABLE IF EXISTS friendships;
DROP TABLE IF EXISTS quiz_results;
DROP TABLE IF EXISTS user_achievements;
DROP TABLE IF EXISTS achievements;
DROP TABLE IF EXISTS questions;
DROP TABLE IF EXISTS quizzes;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS user_token;

-- CREATE TABLES
CREATE TABLE users
(
    id           INT PRIMARY KEY AUTO_INCREMENT,
    username     VARCHAR(50) UNIQUE                   NOT NULL,
    salt         VARCHAR(255)                         NOT NULL,
    password     VARCHAR(255)                         NOT NULL,
    type         ENUM ('Admin', 'Basic') NOT NULL,
    creationdate TIMESTAMP                            NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE quizzes
(
    id              INT PRIMARY KEY AUTO_INCREMENT,
    title               VARCHAR(255)   NOT NULL,
    userid              INT            NOT NULL,
    creationdate        TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    totalscore          DECIMAL(10, 2) NOT NULL DEFAULT 0,
    totalquestions      INT            NOT NULL DEFAULT 0,
    random            BOOLEAN        NOT NULL DEFAULT FALSE,
    singlepage          BOOLEAN        NOT NULL DEFAULT TRUE,
    immediatecorrection BOOLEAN        NOT NULL DEFAULT FALSE,
    practicemode        BOOLEAN        NOT NULL DEFAULT TRUE,
    timelimit           INT            NOT NULL DEFAULT -1, -- IN SECONDS
    description         VARCHAR(1000)   NOT NULL,
    FOREIGN KEY (userid) REFERENCES users (id)
);


CREATE TABLE questions
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    quizid     INT                                                                                                  NOT NULL,
    question   VARCHAR(1000)                                                                                        NOT NULL,
    imagelink  VARCHAR(255),
    type       ENUM ('SingleChoice', 'MultiChoice', 'TextAnswer', 'MultiTextAnswer', 'FillInBlanks', 'FillChoices') NOT NULL,
    maxscore   INT                                                                                                  NOT NULL,
    weight     double                                                                                               NOT NULL,
    jsondata   JSON,
    FOREIGN KEY (quizid) REFERENCES quizzes (id)
);


CREATE TABLE achievements
(
    id       INT PRIMARY KEY AUTO_INCREMENT,
    title    VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    iconlink VARCHAR(1000),
    rarity   ENUM ('Common', 'Rare', 'Epic', 'Legendary') NOT NULL
);


CREATE TABLE user_achievements
(
    id            INT PRIMARY KEY AUTO_INCREMENT,
    userid        INT       NOT NULL,
    achievementid INT       NOT NULL,
    creationdate  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (userid) REFERENCES users (id),
    FOREIGN KEY (achievementid) REFERENCES achievements (id)
);


CREATE TABLE quiz_results
(
    id           INT PRIMARY KEY AUTO_INCREMENT,
    userid       INT            NOT NULL,
    quizid       INT            NOT NULL,
    creationdate TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    timetaken    INT            NOT NULL DEFAULT 0,  -- seconds
    totalscore   DECIMAL(10, 2) NOT NULL DEFAULT 0,
    FOREIGN KEY (userid) REFERENCES users (id),
    FOREIGN KEY (quizid) REFERENCES quizzes (id)
);


CREATE TABLE friendships
(
    id           INT PRIMARY KEY AUTO_INCREMENT,
    firstid      INT       NOT NULL,
    secondid     INT       NOT NULL,
    creationdate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (firstid) REFERENCES users (id),
    FOREIGN KEY (secondid) REFERENCES users (id),

    -- Ensure no duplicate friendships (regardless of order)
    CONSTRAINT unique_friendship UNIQUE (firstid, secondid),
    CONSTRAINT check_ids CHECK (firstid < secondid)
);


CREATE TABLE friend_requests
(
    id           INT PRIMARY KEY AUTO_INCREMENT,
    firstid      INT       NOT NULL,
    secondid     INT       NOT NULL,
    creationdate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (firstid) REFERENCES users (id),
    FOREIGN KEY (secondid) REFERENCES users (id)
);


CREATE TABLE challenges
(
    id           INT PRIMARY KEY AUTO_INCREMENT,
    quizid       INT            NOT NULL,
    senderid     INT            NOT NULL,
    recipientid  INT            NOT NULL,
    bestscore    DECIMAL(10, 2) NOT NULL DEFAULT 0,
    quiztitle    VARCHAR(255)   NOT NULL,
    creationdate TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    viewed       BOOLEAN   NOT NULL,
    FOREIGN KEY (quizid) REFERENCES quizzes (id),
    FOREIGN KEY (senderid) REFERENCES users (id),
    FOREIGN KEY (recipientid) REFERENCES users (id)
);



CREATE TABLE notes
(
    id           INT PRIMARY KEY AUTO_INCREMENT,
    senderid     INT       NOT NULL,
    recipientid  INT       NOT NULL,
    creationdate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    text         TEXT      NOT NULL,
    viewed       BOOLEAN   NOT NULL,
    FOREIGN KEY (senderid) REFERENCES users (id),
    FOREIGN KEY (recipientid) REFERENCES users (id)
);

CREATE TABLE user_answers(
     id         INT PRIMARY KEY AUTO_INCREMENT,
     questionid INT         NOT NULL,
     resultid   INT         NOT NULL,
     isstring   BOOLEAN     NOT NULL,
     jsondata   JSON,
     FOREIGN KEY (questionid) REFERENCES questions (id),
     FOREIGN KEY (resultid) REFERENCES quiz_results (id)
);

CREATE TABLE announcements(
    id           INT PRIMARY KEY AUTO_INCREMENT,
    imagelink    VARCHAR(1000),
    title        VARCHAR(255) NOT NULL,
    content      VARCHAR(6500) NOT NUll,
    author       VARCHAR(255) NOT NULL,
    creationdate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (author) REFERENCES users (username)
);


CREATE TABLE user_tokens
(
    id         INT PRIMARY KEY AUTO_INCREMENT,
    token      VARCHAR(255) NOT NULL ,
    userid     INT  NOT NULL,
    expiredate  TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP + INTERVAL 1 DAY),
    FOREIGN KEY (userid) REFERENCES users (id)

);

