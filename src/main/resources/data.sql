INSERT INTO user_account(username, frozen)
VALUES
    ('random_user', false),
    ('moderator', false),
    ('main_moderator', false);

-- CREATE EXTENSION pgcrypto; before execution for gen_salt function
INSERT INTO user_base_auth(user_id, encrypted_password)
VALUES
    (1, crypt('random_user_password', gen_salt('bf'))),
    (2, crypt('moderator_password', gen_salt('bf'))),
    (3, crypt('main_moderator_password', gen_salt('bf')));

INSERT INTO role(name)
VALUES
    ('ROLE_USER'),
    ('ROLE_MODERATOR'),
    ('ROLE_MAIN_MODERATOR');

INSERT INTO user_roles(user_id, role_id)
VALUES
    (1, 1),
    (2, 2),
    (3, 3);


INSERT INTO user_info(user_id, registered_date)
VALUES
    (1, now()),
    (2, now()),
    (3, now());


INSERT INTO subscription(subscriber_id, poster_id)
VALUES
    (1, 2),
    (1, 3),
    (2, 1),
    (3, 1),
    (2, 3);
