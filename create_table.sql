drop table if exists patients;


CREATE table patients(
NHS_number numeric (20),
title varchar (10),
first_name char (20),
last_name char (20),
address varchar (60),
contact_telephone_number varchar (15),
known_allergies varchar (100),
blood_group varchar (10),
PRIMARY KEY (NHS_number)
);