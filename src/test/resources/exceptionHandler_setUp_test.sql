insert into users (email, name)
values ('user@yandex.ru', 'name'), ('user1@yandex.ru', 'name1'), ('user2@yandex.ru', 'name2');

insert into items (name, description, is_available, owner_id)
values ('item1', 'description1', true, 1), ('item2', 'description2', false, 1),
       ('item3', 'description3', true, 1), ('item4', 'description4', true, 1),
       ('item5', 'description5', true, 1);

insert into bookings (start_date, end_date, item_id, booker_id, status)
values ('2020-01-17 23:15:42', '2020-01-17 23:15:43', 1, 2, 'APPROVED');