USE quizKhana;

-- Insert users (5 entries)
INSERT INTO users (username, salt, password, type) VALUES
                                                       ('admin1', 'salt1', 'hashed_password1', 'Admin'),
                                                       ('user1', 'salt2', 'hashed_password2', 'Basic'),
                                                       ('user2', 'salt3', 'hashed_password3', 'Basic'),
                                                       ('user3', 'salt4', 'hashed_password4', 'Basic'),
                                                       ('user4', 'salt5', 'hashed_password5', 'Basic');

-- Insert quizzes (5 entries)
INSERT INTO quizzes (title, userid, totalscore, totalquestions, random, singlepage, immediatecorrection, practicemode, timelimit, description) VALUES
                                                                                                                                                   ('General Knowledge Quiz', 2, 100, 10, FALSE, TRUE, TRUE, TRUE, 600, "a"),
                                                                                                                                                   ('Science Trivia', 3, 150, 15, TRUE, FALSE, FALSE, TRUE, 900, "aba"),
                                                                                                                                                   ('History Challenge', 2, 75, 5, FALSE, TRUE, TRUE, FALSE, 300, "desc"),
                                                                                                                                                    ('Math Quick Test', 4, 50, 5, FALSE, TRUE, FALSE, TRUE, 300, "123"),

                                                                                                                                                        ('Geography Quiz', 3, 120, 12, TRUE, FALSE, TRUE, TRUE, 720, "1234");



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

INSERT INTO friendships (firstid, secondid) VALUES
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


INSERT INTO announcements (imagelink, title, content, author) VALUES
                                                                  ('imglink1.png', 'announcement1', 'very important announcement to see', 'admin1'),
                                                                  ('imglink2.png', 'announcement2', 'very important 222 announcement to see', 'admin1'),
                                                                  ('imglink3.png', 'announcement3', 'very important  333 announcement to see', 'admin1'),
                                                                  ('imglink4.png', 'announcement4', 'very important  4444 announcement to see', 'admin1'),
                                                                  ('imglink5.png', 'announcement5', 'very important  55555 announcement to see', 'admin1');

INSERT INTO notes (senderid, recipientid, text, viewed) VALUES
                                                            (2, 3, 'Great job on the Science Trivia quiz!', false),
                                                            (3, 2, 'Thanks for the challenge, let me know when you want a rematch!', false),
                                                            (4, 5, 'How did you solve question 4 on the Math test?', false),
                                                            (5, 4, 'Check out this new geography quiz I created!', false),
                                                            (2, 4, 'Your history knowledge is impressive!', false)

INSERT INTO achievements (id, title, description, iconlink, rarity) VALUES
                                                                        (1, 'Amateur Author', 'Create a quiz', 'https://i.pinimg.com/originals/20/6d/71/206d71d869aeb22b8f9e0e7a6a4f7b54.png', 'Common'),
                                                                        (2, 'Prolific Author', 'Create five quizzes', 'icon2.png', 'Rare'),
                                                                        (3, 'Prodigious Author', 'Create ten quizzes', 'icon3.png', 'Epic'),
                                                                        (4, 'Quiz Machine', 'The user took ten quizzes.', 'https://res.cloudinary.com/jerrick/image/upload/c_scale,f_jpg,q_auto/63f6c0563f141d001d7bfe65.jpg', 'Common'),
                                                                        (5, 'I am the Greatest', 'The user had the highest score on a quiz', 'icon5.png', 'Legendary'),
                                                                        (6, 'Practice Makes Perfect', 'The user took a quiz in practice mode', 'icon6.png', 'Epic'),
                                                                        (7, 'Newbie', 'The user took first quiz', 'https://transforms.stlzoo.org/production/animals/chimpanzee-01.jpg?w=576&h=576&auto=compress%2Cformat&fit=crop&dm=1658432698&s=9138ff00319b661d5a4edfbb733bd8fb', 'Common'),
                                                                        (8, 'Amateur Quizzer', 'The user took five quizzes', 'https://humanorigins.si.edu/sites/default/files/styles/grid_thumbnail/public/images/landscape/S_tchadensis_front_h_DH.jpg.webp?itok=W_WoKPCh', 'Epic'),
                                                                        (9, 'Quiz Master', 'The user took 50 quizzes', 'https://i.pinimg.com/736x/88/16/87/881687946e98f0172075051bedbcd301.jpg', 'Legendary'),
                                                                        (10, 'Quiz Legend', 'The user took 100 quizzes', 'https://shorturl.at/HskqB', 'Legendary');
