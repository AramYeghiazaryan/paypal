
drop table if exists users;
drop table if exists transactions;


create table users (
  id int unique auto_increment,
  first_name text not null,
  last_name text not null,
  balance real not null default 0
);

create table transactions (
  id int unique auto_increment,
  user_from int,
  user_to int,
  transaction_amount real not null,
  transaction_date timestamp not null default now()
);

insert into users(first_name,last_name,balance) values (
'Valodik',
'Valodikyan',
2000
);
insert into users values (
null,
'Poghos',
'Poghosyan',
1000
);
insert into users values (
null,
'Petros',
'Petrosyan',
3000
);

