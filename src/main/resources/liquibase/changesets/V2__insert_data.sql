insert into users (name, email, password)
values ('John Doe', 'johndoe@gmail.com', '$2y$10$A/zVux2Oi2OHTSJM4XnjW.PaTYFg83u8/f.4rwJapyjUDG.q2xMCq'),
       ('Tony Stark', 'tonystark@gmail.com', '$2y$10$A/zVux2Oi2OHTSJM4XnjW.PaTYFg83u8/f.4rwJapyjUDG.q2xMCq'),
       ('Mila Samilyak', 'mila.samilyak@gmail.com', '$2a$10$aO2meHecmRT.eiEhL/MnG.tBAbDanw6oGQDIb28XTmJZg/IMUnTPy'),
       ('Ilya Samilyak', 'ilyasamilyak@gmail.com', '$2a$10$aO2meHecmRT.eiEhL/MnG.tBAbDanw6oGQDIb28XTmJZg/IMUnTPy');

insert into tasks (title, description, status, expiration_date)
values ('By cheese', null, 'TODO', '2023-01-29 12:00:00'),
       ('Do homework', 'Math, Physics, Drama', 'IN_PROGRESS', '2022-04-29 11:00:00'),
       ('Clean rooms', null, 'DONE', null),
       ('Call Mike', 'Tell him a story', 'TODO', '2020-07-23 08:00:00');

insert into users_tasks (task_id, user_id)
values (1, 2),
       (2, 2),
       (3, 2),
       (4, 1),
       (2, 3),
       (3, 3),
       (4, 4),
       (1, 4);

insert into users_roles (user_id, role)
values (1, 'ROLE_ADMIN'),
       (1, 'ROLE_USER'),
       (2, 'ROLE_ADMIN'),
       (3, 'ROLE_USER'),
       (4, 'ROLE_USER'),
       (4, 'ROLE_ADMIN');
