INSERT INTO tbl_room(id, name, color, active) VALUES
    (1, 'Asia 1', '#A9A9A9', 1),
    (2, 'Asia 2', 'green', 1),
    (3, 'Asia 3', '#006400', 1),
    (4, 'Asia 4', '#8B0000', 1),
    (5, 'Asia 5', '#2F4F4F', 1);

INSERT INTO tbl_user(id, username, password, email, first_name, last_name, active) VALUES
    (1, 'admin', 'admin', 'admin@user.com.sg', 'Admin', 'User', 1),
    (2, 'moderator', 'moderator', 'moderator@user.com.sg', 'Moderator', 'User', 1);

INSERT INTO tbl_weekday(id, name, active) VALUES
    (1, 'Mon', 1), (2, 'TUE', 1), (3, 'Wed', 1), (4, 'Thu', 1), (5, 'Fri', 1), (6, 'Sat', 0);

--INSERT INTO tbl_booking(id, subject, start_time, end_time, is_all_day, is_readonly, recurrence_rule, room_id, user_id, active) VALUES
--    (1, 'Scrum meeting Dev team', '2021-07-05T01:30:00', '2021-07-05T02:30:00', 1, 1, 'FREQ=DAILY;INTERVAL=2;COUNT=10', 1, 1, 1),
--    (2, 'Scrum meeting Test team', '2021-07-05T02:30:00', '2021-07-05T03:30:00', 0, 0, '', 2, 2, 1);
