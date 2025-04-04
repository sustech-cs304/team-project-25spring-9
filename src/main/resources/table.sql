create table if not exists building
(
    building_id          int auto_increment
        primary key,
    building_name        text          not null,
    building_img         int default 0 not null,
    building_description text          null,
    building_x           double        not null,
    building_y           double        not null,
    building_comments    int default 0 null
);
create table if not exists building_img
(
    buildingImg_id int auto_increment
        primary key,
    building_id    int  not null,
    img_name       text not null,
    constraint buildingImg_building_building_id_fk
        foreign key (building_id) references building (building_id)
);
create table if not exists bus
(
    bus_id    int auto_increment
        primary key,
    bus_name  text                 null,
    bus_valid tinyint(1) default 1 null
);
create table if not exists  comment
(
    comment_id      int auto_increment
        primary key,
    user_id         int                       not null,
    building_id     int                       not null,
    comment_img     int       default 0       not null,
    comment_content text                      not null,
    comment_time    timestamp default (now()) not null,
    comment_likes   int       default 0       not null,
    comment_replies int       default 0       not null,
    comment_valid   int       default 0       not null,
    constraint comment_pk
        unique (comment_id),
    constraint comment_building_null_fk
        foreign key (building_id) references building (building_id),
    constraint comment_user_null_fk
        foreign key (user_id) references user (User_id)
);
create table if not exists  comment_img
(
    commentImg_id int auto_increment
        primary key,
    comment_id    int  not null,
    img_name      text not null,
    constraint comment_img_comment_comment_id_fk
        foreign key (comment_id) references comment (comment_id)
);
create table if not exists  comment_likes
(
    id             int auto_increment
        primary key,
    user_id        int                        not null,
    comment_id     int                        not null,
    like_time      timestamp  default (now()) not null,
    read_condition tinyint(1) default 0       not null,
    constraint comment_likes_pk
        unique (comment_id, user_id),
    constraint comment_likes_comment_comment_id_fk
        foreign key (comment_id) references comment (comment_id),
    constraint comment_likes_user_User_id_fk
        foreign key (user_id) references user (User_id)
);
create table if not exists  commodity
(
    commodity_name text   null,
    commodity_id   int auto_increment
        primary key,
    window_id      int    null,
    price          double null
);
create table if not exists  food
(
    food_id                   int auto_increment
        primary key,
    food_name                 text                 not null,
    food_img                  text                 null,
    food_amount               int                  not null comment 'the amount of food that can be ordered daily',
    food_ordered_amount       int        default 0 not null comment 'the amount of food that have be ordered today',
    restaurant_id             int                  not null,
    food_total_ordered_amount int        default 0 not null comment 'the amount of food that have be ordered up to now',
    food_price                double               null,
    food_valid                tinyint(1) default 1 null,
    constraint food_restaurant_null_fk
        foreign key (restaurant_id) references restaurant (restaurant_id)
);
create table if not exists  food_order
(
    food_order_id int auto_increment
        primary key,
    food_id       int                  not null,
    order_id      int                  not null,
    food_amount   int        default 1 not null,
    order_valid   tinyint(1) default 1 null,
    constraint food_order_food_null_fk
        foreign key (food_id) references food (food_id)
);
create table if not exists  food_rank_date
(
    food_rank_id int auto_increment
        primary key,
    food_id      int           null,
    food_cnt     int default 0 not null,
    date         date          null
);
create table if not exists  food_window
(
    window_id     int auto_increment
        primary key,
    restaurant_id int                  null,
    window_name   text                 null,
    window_valid  tinyint(1) default 1 not null
);
create table if not exists  orders
(
    order_id        int auto_increment
        primary key,
    user_id         int                  null,
    food_order_time datetime             not null,
    pay_time        datetime             null,
    payed           tinyint(1) default 0 not null,
    orders_valid    tinyint(1) default 1 null,
    food_get_time   datetime             null,
    orders_price    double               null,
    order_taken     tinyint(1) default 0 null,
    order_reference int                  null
);
create table if not exists  permission
(
    user_id       int not null,
    role_id       int not null,
    permission_id int auto_increment
        primary key,
    constraint permission_pk
        unique (permission_id)
);
create table if not exists  reply
(
    reply_id      int auto_increment
        primary key,
    user_id       int                        not null comment '进行回复的用户',
    target_id     int                        not null comment '被回复的用户',
    replied_id    int        default -1      not null,
    comment_id    int                        not null comment '被回复的评论',
    reply_content text                       not null,
    reply_time    timestamp  default (now()) not null,
    reply_likes   int        default 0       not null,
    reply_valid   int        default 0       not null,
    reply_read    tinyint(1) default 0       not null,
    constraint reply_comment_comment_id_fk
        foreign key (comment_id) references comment (comment_id),
    constraint reply_user_User_id_fk
        foreign key (user_id) references user (User_id),
    constraint reply_user_User_id_fk2
        foreign key (target_id) references user (User_id)
);
create table if not exists  reply_likes
(
    id             int auto_increment
        primary key,
    user_id        int                        not null,
    reply_id       int                        not null,
    like_time      timestamp  default (now()) not null,
    read_condition tinyint(1) default 0       not null,
    constraint reply_likes_pk
        unique (user_id, reply_id),
    constraint reply_like_reply_reply_id_fk
        foreign key (reply_id) references reply (reply_id),
    constraint reply_like_user_User_id_fk
        foreign key (user_id) references user (User_id)
);
create table if not exists  restaurant
(
    restaurant_id    int auto_increment
        primary key,
    restaurant_name  text                 not null,
    restaurant_valid tinyint(1) default 1 null
);
create table if not exists  role
(
    role_id   int auto_increment
        primary key,
    role_name text not null,
    constraint role_pk
        unique (role_id)
);
create table if not exists  store_order
(
    order_id   int auto_increment
        primary key,
    user_id    int                                  null,
    order_time datetime   default CURRENT_TIMESTAMP null,
    pay_time   datetime                             null,
    payed      tinyint(1) default 0                 null,
    price      double                               null
);
create table if not exists  store_order_item
(
    store_order_item_id int auto_increment
        primary key,
    order_id            int null,
    item_id             int null,
    item_count          int null
);
create table if not exists  user
(
    User_id       int auto_increment
        primary key,
    user_name     varchar(30)          not null,
    user_password text                 not null,
    user_img      text                 null,
    user_mail     text                 null,
    user_nickname text                 null,
    user_valid    tinyint(1) default 1 null,
    constraint user_name_pk
        unique (user_name),
    constraint user_pk
        unique (User_id)
);

create index user_User_id_index
    on user (User_id);
