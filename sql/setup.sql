-- create this database if you dont have it
use quizkhana;

-- drop tables
drop table if exists user_answers;
drop table if exists notes;
drop table if exists challenges;
drop table if exists friend_requests;
drop table if exists friendships;
drop table if exists quiz_results;
drop table if exists user_achievements;
drop table if exists achievements;
drop table if exists questions;
drop table if exists quizzes;
drop table if exists users;

-- create tables
create table users
(
    id           int primary key auto_increment,
    username     varchar(50) unique                   not null,
    salt         varchar(255)                         not null,
    password     varchar(255)                         not null,
    type         enum ('admin', 'basic') not null,
    creationdate timestamp                            not null default current_timestamp
);

create table quizzes
(
    id              int primary key auto_increment,
    title               varchar(255)   not null,
    userid              int            not null,
    creationdate        timestamp      not null default current_timestamp,
    totalscore          decimal(10, 2) not null default 0,
    totalquestions      int            not null default 0,
    random            boolean        not null default false,
    singlepage          boolean        not null default true,
    immediatecorrection boolean        not null default false,
    practicemode        boolean        not null default true,
    timelimit           int            not null default -1, -- in seconds
    foreign key (userid) references users (id)
);


create table questions
(
    id int primary key auto_increment,
    quizid     int                                                                                                  not null,
    question   varchar(1000)                                                                                        not null,
    imagelink  varchar(255),
    type       enum ('singlechoice', 'multichoice', 'textanswer', 'multitextanswer', 'fillinblanks', 'fillchoices') not null,
    maxscore   int                                                                                                  not null,
    weight     double                                                                                               not null,
    jsondata   json,
    foreign key (quizid) references quizzes (id)
);


create table achievements
(
    id       int primary key auto_increment,
    title    varchar(255) not null,
    description varchar(255),
    iconlink varchar(255),
    rarity   enum ('common', 'rare', 'epic', 'legendary') not null
);


create table user_achievements
(
    id            int primary key auto_increment,
    userid        int       not null,
    achievementid int       not null,
    creationdate  timestamp not null default current_timestamp,
    foreign key (userid) references users (id),
    foreign key (achievementid) references achievements (id)
);


create table quiz_results
(
    id           int primary key auto_increment,
    userid       int            not null,
    quizid       int            not null,
    creationdate timestamp      not null default current_timestamp,
    timetaken    int            not null default 0,  -- seconds
    totalscore   decimal(10, 2) not null default 0,
    foreign key (userid) references users (id),
    foreign key (quizid) references quizzes (id)
);


create table friendships
(
    id           int primary key auto_increment,
    firstid      int       not null,
    secondid     int       not null,
    creationdate timestamp not null default current_timestamp,
    foreign key (firstid) references users (id),
    foreign key (secondid) references users (id),

    -- ensure no duplicate friendships (regardless of order)
    constraint unique_friendship unique (firstid, secondid),
    constraint check_ids check (firstid < secondid)
);


create table friend_requests
(
    id           int primary key auto_increment,
    firstid      int       not null,
    secondid     int       not null,
    creationdate timestamp not null default current_timestamp,
    foreign key (firstid) references users (id),
    foreign key (secondid) references users (id)
);


create table challenges
(
    id           int primary key auto_increment,
    quizid       int            not null,
    senderid     int            not null,
    recipientid  int            not null,
    bestscore    decimal(10, 2) not null default 0,
    quiztitle    varchar(255)   not null,
    creationdate timestamp      not null default current_timestamp,
    foreign key (quizid) references quizzes (id),
    foreign key (senderid) references users (id),
    foreign key (recipientid) references users (id)
);

create table user_answers
(
    id         int primary key auto_increment,
    questionid int         not null,
    resultid   int         not null,
    isstring   boolean     not null,
    jsondata   json,
    foreign key (questionid) references questions (id),
    foreign key (resultid) references quiz_results (id)
);