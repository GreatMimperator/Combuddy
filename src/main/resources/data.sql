INSERT INTO role(name)
VALUES
    ('ROLE_USER'),
    ('ROLE_MODERATOR'),
    ('ROLE_MAIN_MODERATOR');

INSERT INTO user_account(username, role_id, frozen)
VALUES
    ('random_user', 1, false),
    ('moderator', 2, false),
    ('main_moderator', 3, false),
    ('another_moderator', 2, false);

INSERT INTO privacy_policy(user_id, registered_date_access_level, subscriptions_access_level)
VALUES
    (1, 'NOBODY', 'NOBODY'),
    (2, 'NOBODY', 'EVERYBODY'),
    (3, 'EVERYBODY', 'EVERYBODY'),
    (4, 'NOBODY', 'EVERYBODY');


-- CREATE EXTENSION pgcrypto; before execution for gen_salt function
INSERT INTO user_base_auth(user_id, encrypted_password)
VALUES
    (1, crypt('random_user_password', gen_salt('bf'))),
    (2, crypt('moderator_password', gen_salt('bf'))),
    (3, crypt('main_moderator_password', gen_salt('bf'))),
    (4, crypt('another_moderator_password', gen_salt('bf')));

INSERT INTO user_info(user_id, registered_date)
VALUES
    (1, now()),
    (2, now()),
    (3, now()),
    (4, now());


INSERT INTO subscription(subscriber_id, poster_id)
VALUES
    (1, 2),
    (1, 4),
    (2, 1),
    (3, 1),
    (4, 1);
