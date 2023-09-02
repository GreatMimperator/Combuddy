create table working_refresh_token (
    id bigserial not null,
    owner_username varchar(35) not null unique,
    jwt_id varchar(255) not null unique
);

create table user_account (
    id bigserial primary key,
    username varchar(35) not null unique,
    role_name varchar(255) not null check (role_name in ('ROLE_USER','ROLE_MODERATOR','ROLE_MAIN_MODERATOR')),
    frozen boolean not null
);

create table user_base_auth (
    id bigserial primary key,
    user_id bigint not null unique references user_account,
    encrypted_password varchar(255) not null
);

create table user_info (
    id bigserial not null,
    user_id bigint not null unique references user_account,
    registered_date timestamp(6) not null,
    full_picture oid,
    picture_thumbnail oid
);

create table privacy_policy (
    id bigserial primary key,
    user_id bigint not null unique references user_account,
    registered_date_access_level varchar(20) check (registered_date_access_level in ('NOBODY','EVERYBODY')),
    subscriptions_access_level varchar(20) check (subscriptions_access_level in ('NOBODY','EVERYBODY'))
);

create table user_contact (
    id bigserial primary key,
    owner_id bigint not null references user_account,
    contact_type varchar(20) check (contact_type in ('TELEGRAM','X','VK','YOUTUBE','GITHUB','GITLAB','MAIL')),
    value varchar(255) not null,
    constraint user_contact_unique unique (owner_id, contact_type, value)
);

create table black_list (
    id bigserial primary key,
    aggressor_id bigint not null references user_account,
    defended_id bigint not null references user_account,
    constraint black_list_unique unique (aggressor_id, defended_id)
);


create table subscription (
    id bigserial primary key,
    poster_id bigint not null references user_account,
    subscriber_id bigint not null references user_account,
    constraint subscription_unique unique (poster_id, subscriber_id)
);

create table user_complaint (
    id bigserial primary key,
    state varchar(20) not null check (state in ('PROCESSED', 'BANNED', 'FAKE')),
    creation_date timestamp(6) not null,
    informer_id bigint not null references user_account,
    suspect_id bigint not null references user_account,
    text varchar(250) not null
);

create table user_complaint_judgment (
     id bigserial primary key,
     complaint_id bigint not null references user_complaint,
     judge_id bigint not null references user_account,
     state varchar(20) not null check (state in ('BANNED', 'FAKE')),
     constraint user_complaint_judgment_unique unique (complaint_id, judge_id)
);

create table post (
    id bigserial primary key,
    owner_id bigint not null references user_account,
    title varchar(150) not null,
    body varchar(2000) not null,
    state varchar(20) not null check (state in ('POSTED', 'DRAFT', 'HIDDEN', 'FROZEN')),
    creation_date timestamp(6) not null,
    posted_date timestamp(6),
    modification_date timestamp(6)
);

create table post_user_contact (
    id bigserial primary key,
    post_id bigint not null references post,
    user_contact_id bigint not null references user_contact,
    constraint post_user_contact_unique unique (post_id, user_contact_id)
);

create table post_complaint (
    id bigserial primary key,
    suspect_id bigint not null references post,
    informer_id bigint not null references user_account,
    state varchar(20) not null check (state in ('PROCESSED', 'BANNED', 'FAKE')),
    text varchar(250) not null,
    creation_date timestamp(6) not null
);

create table post_complaint_judgment (
    id bigserial primary key,
    complaint_id bigint not null references post_complaint,
    judge_id bigint not null references user_account,
    state varchar(20) not null check (state in ('BANNED', 'FAKE')),
    constraint post_complaint_judgment_unique unique (complaint_id, judge_id)
);

create table post_contact (
    id bigserial primary key,
    post_id bigint not null references post,
    contact_type varchar(255) check (contact_type in ('TELEGRAM','X','VK','YOUTUBE','GITHUB','GITLAB','MAIL')),
    value varchar(255) not null,
    constraint post_complaint_unique unique (post_id, contact_type, value)
);

create table dialog (
    id bigserial primary key,
    first_participant_id bigint not null references user_account,
    second_participant_id bigint not null references user_account,
    post_id bigint not null references post,
    archived boolean not null,
    constraint dialog_unique unique (first_participant_id, second_participant_id, post_id)
);

create table favorite_post (
    id bigserial primary key,
    post_id bigint not null references post,
    subscriber_id bigint not null references user_account,
    saved_time timestamp(6) not null,
    constraint favorite_post_unique unique (post_id, subscriber_id)
);


create table message (
    id bigserial primary key,
    dialog_id bigint references dialog,
    message_reply_to_id bigint references message,
    is_sender_first boolean not null,
    changed_date timestamp(6),
    creation_date timestamp(6) not null,
    text varchar(1000) not null,
    picture oid
);

create table tag (
    id bigserial primary key,
    name varchar(35) not null unique,
    description varchar(250)
);

create table post_tag (
    id bigserial primary key,
    post_id bigint not null references post,
    tag_id bigint not null references tag,
    constraint post_tag_unique unique (post_id, tag_id)
);

create table public_message (
    id bigserial primary key,
    post_id bigint not null references post,
    sender_id bigint not null references user_account,
    message_root_id bigint,
    creation_date timestamp(6) not null,
    text varchar(250) not null,
    picture oid
);

create table user_home_tag (
    id bigserial primary key,
    user_id bigint not null references user_account,
    tag_id bigint not null references tag,
    filter_type varchar(20) not null check (filter_type in ('INCLUDING', 'EXCLUDING')),
    constraint user_home_tag_unique unique (user_id, tag_id, filter_type)
);

create index privacy_policy_by_user_id_receive_index on privacy_policy (user_id);
create index user_info_by_user_id_receive_index on user_info (user_id);