USE quizKhana;

-- Insert users (5 entries)
INSERT INTO users (username, salt, password, type) VALUES
                                                       ('admin1', 'salt1', 'hashed_password1', 'Admin'),
                                                       ('user1', 'salt2', 'hashed_password2', 'Basic'),
                                                       ('user2', 'salt3', 'hashed_password3', 'Basic'),
                                                       ('user3', 'salt4', 'hashed_password4', 'Basic'),
                                                       ('user4', 'salt5', 'hashed_password5', 'Basic');

-- Insert quizzes (5 entries)
INSERT INTO quizzes (title, userid, totalscore, totalquestions, random, singlepage, immediatecorrection, practicemode, timelimit) VALUES
                                                                                                                                      ('General Knowledge Quiz', 2, 100, 10, FALSE, TRUE, TRUE, TRUE, 600),
                                                                                                                                      ('Science Trivia', 3, 150, 15, TRUE, FALSE, FALSE, TRUE, 900),
                                                                                                                                      ('History Challenge', 2, 75, 5, FALSE, TRUE, TRUE, FALSE, 300),
                                                                                                                                      ('Math Quick Test', 4, 50, 5, FALSE, TRUE, FALSE, TRUE, 300),
                                                                                                                                      ('Geography Quiz', 3, 120, 12, TRUE, FALSE, TRUE, TRUE, 720);



-- Insert achievements (5 entries)
INSERT INTO achievements (title, description, iconlink, rarity) VALUES
                                                                    ('Quiz Master', 'Complete 10 quizzes', 'icon1.png', 'Common'),
                                                                    ('Perfect Score', 'Get a perfect score on any quiz', 'icon2.png', 'Rare'),
                                                                    ('Speed Runner', 'Complete a quiz in under 1 minute', 'icon3.png', 'Epic'),
                                                                    ('Social Butterfly', 'Make 5 friends', 'icon4.png', 'Common'),
                                                                    ('Challenge Champion', 'Win 3 challenges', 'icon5.png', 'Legendary');

-- Insert user_achievements (5 entries)
INSERT INTO user_achievements (userid, achievementid) VALUES
                                                          (2, 1),
                                                          (2, 2),
                                                          (3, 1),
                                                          (4, 4),
                                                          (5, 3);

-- Insert quiz_results (5 entries)
INSERT INTO quiz_results (userid, quizid, timetaken, totalscore) VALUES
                                                                     (2, 1, 180, 85),
                                                                     (3, 2, 240, 120),
                                                                     (2, 3, 150, 60),
                                                                     (4, 4, 90, 45),
                                                                     (5, 5, 300, 110);

-INSERT INTO friendships (firstid, secondid) VALUES
(2, 3),
(2, 4),
(3, 5),
(4, 5),
(1, 2);  -- Admin is friends with user1

-- Insert friend_requests (5 entries)
-- Only between users who aren't already friends
INSERT INTO friend_requests (firstid, secondid) VALUES
                                                    (1, 3),  -- Admin requests user2 (not friends yet)
                                                    (1, 4),  -- Admin requests user3
                                                    (1, 5),  -- Admin requests user4
                                                    (3, 4),  -- user2 requests user3 (they're not friends yet)
                                                    (5, 2);  -- user4 requests user1 (they're not friends yet)

-- Insert challenges (5 entries)
INSERT INTO challenges (quizid, senderid, recipientid, bestscore, quiztitle, viewed) VALUES
                                                                                 (1, 2, 3, 75, 'General Knowledge Quiz', false),
                                                                                 (2, 3, 4, 100, 'Science Trivia', false),
                                                                                 (3, 4, 5, 50, 'History Challenge', false),
                                                                                 (4, 5, 2, 40, 'Math Quick Test', false),
                                                                                 (5, 2, 4, 90, 'Geography Quiz', false);

-- Insert notes (5 entries)
INSERT INTO notes (senderid, recipientid, text, viewed) VALUES
                                                    (2, 3, 'Great job on the Science Trivia quiz!', false),
                                                    (3, 2, 'Thanks for the challenge, let me know when you want a rematch!', false),
                                                    (4, 5, 'How did you solve question 4 on the Math test?', false),
                                                    (5, 4, 'Check out this new geography quiz I created!', false),
                                                    (2, 4, 'Your history knowledge is impressive!', false);
