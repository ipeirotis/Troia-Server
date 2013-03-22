CREATE DATABASE Troia 
 CHARACTER SET utf8
 DEFAULT CHARACTER SET utf8
 COLLATE utf8_general_ci
 DEFAULT COLLATE utf8_general_ci;

USE Troia;

CREATE TABLE projects ( id varchar(100) NOT NULL PRIMARY KEY, kind varchar(25) NOT NULL, data LONGTEXT NOT NULL, last_use TIMESTAMP) DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;

CREATE INDEX idIndex on projects (id);
