INSERT INTO user_account(username, frozen)
VALUES
    ('user', false),
    ('moderator', false),
    ('main_moderator', true);

-- CREATE EXTENSION pgcrypto; before execution for gen_salt function
INSERT INTO user_base_auth(user_id, encrypted_password)
VALUES
    (1, crypt('user_password', gen_salt('bf'))),
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
