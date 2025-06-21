-- create this database if you dont have it
USE quizKhana;

-- DROP TABLES
DROP TABLE IF EXISTS questions;

-- CREATE TABLES

CREATE TABLE questions
(
    questionid INT PRIMARY KEY AUTO_INCREMENT,
    quizid     INT                                                                                                  NOT NULL,
    question   VARCHAR(1000)                                                                                        NOT NULL,
    imagelink  VARCHAR(255),
    type       ENUM ('SingleChoice', 'MultiChoice', 'TextAnswer', 'MultiTextAnswer', 'FillInBlanks', 'FillChoices') NOT NULL,
    maxscore   INT                                                                                                  NOT NULL,
    jsondata   JSON
    -- (THIS WILL GET UNCOMENTED WHEN quizzes gets created) FOREIGN KEY (quizid) REFERENCES quizzes (quizid)
);
