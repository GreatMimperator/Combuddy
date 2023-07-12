INSERT INTO user_account(username, frozen)
VALUES
    ('Miron', false),
    ('Mike', false),
    ('python_enjoyed', true);

INSERT INTO user_info(user_id, registered_date, moderator, main_moderator)
VALUES
    (1, now(), true, true),
    (2, now(), true, false),
    (3, now(), false, false);


INSERT INTO subscription(subscriber_id, poster_id)
VALUES
    (1, 2),
    (1, 3),
    (2, 3);