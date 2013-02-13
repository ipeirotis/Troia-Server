create table projects ( id varchar(100) NOT NULL PRIMARY KEY, kind varchar(25) NOT NULL, data LONGTEXT NOT NULL);
create index idIndex on projects (id);
