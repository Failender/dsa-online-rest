CREATE TABLE HELDEN(
  ID INTEGER NOT NULL,
  USER_ID INTEGER NOT NULL,
  NAME MEDIUMTEXT NOT NULL,
  GRUPPE_ID INTEGER NOT NULL,
  PUBLIC TINYINT NOT NULL,
  DELETED TINYINT NOT NULL,
  ACTIVE TINYINT NOT NULL,
  CREATED_DATE DATETIME NOT NULL,
  PRIMARY KEY(ID)
);

CREATE TABLE HELD_VERSION(
  ID INTEGER NOT NULL AUTO_INCREMENT,
  HELDID BIGINT NOT NULL,
  VERSION INTEGER NOT NULL,
  CREATED_DATE DATETIME NOT NULL,
  LAST_EVENT MEDIUMTEXT,
  CACHE_ID varchar(36) NOT NULL,
  AP INTEGER NOT NULL,
  PRIMARY KEY(ID)
);


CREATE TABLE ROLES(
	ID INTEGER NOT NULL AUTO_INCREMENT,
	NAME varchar(20) NOT NULL,
	PRIMARY KEY(ID)
);

CREATE TABLE RIGHTS(
	ID INTEGER NOT NULL AUTO_INCREMENT,
	NAME varchar(20) NOT NULL,
	PRIMARY KEY(ID)
);

CREATE TABLE ROLES_TO_RIGHTS(
	ROLE_ID INTEGER NOT NULL,
	RIGHT_ID INTEGER NOT NULL,
	PRIMARY KEY(ROLE_ID , RIGHT_ID)
);

CREATE TABLE ROLES_TO_USER(
	ROLE_ID INTEGER NOT NULL,
	USER_ID INTEGER NOT NULL,
	PRIMARY KEY(USER_ID, ROLE_ID)
);

CREATE TABLE USER_TO_MEISTER(
  USER_ID INTEGER NOT NULL,
  GRUPPE_ID INTEGER NOT NULL,
  PRIMARY KEY (USER_ID, GRUPPE_ID)
);

INSERT INTO ROLES VALUES
	(1, 'Administrator'),
	(2, 'Meister');


INSERT INTO RIGHTS VALUES
	(1, 'CREATE_USER'),
	(2, 'VIEW_ALL'),
	(3, 'EDIT_RIGHTS'),
	(4, 'EDIT_ALL'),
	(5, 'MEISTER'),
	(6, 'EDIT_SCRIPTS'),
	(7, 'FULL_IMPORT'),
	(8, 'FULL_EXPORT');


INSERT INTO ROLES_TO_USER VALUES
  (1, 1);

INSERT INTO ROLES_TO_RIGHTS VALUES
	(1, 1),
	(1, 2),
	(1, 3),
	(1, 4),
	(1, 5),
	(2, 5);

CREATE TABLE EVENTS(
	ID INTEGER NOT NULL AUTO_INCREMENT,
	NAME varchar(40) NOT NULL,
	OWNER_ID INTEGER NOT NULL,
	TYPE MEDIUMINT NOT NULL,
	DATE INTEGER NOT NULL,
	PRIMARY KEY(ID)
);

CREATE TABLE ABENTEUER(
	ID INTEGER NOT NULL AUTO_INCREMENT,
	NAME varchar(40) NOT NULL,
  CREATED_DATE DATETIME  NOT NULL,
	GRUPPE_ID INTEGER NOT NULL,
  AP INTEGER NOT NULL,
	PRIMARY KEY(ID)
);

CREATE TABLE ABENTEUER_BONUS_AP(
  ID INTEGER NOT NULL AUTO_INCREMENT,
	HELD INTEGER NOT NULL,
	ABENTEUER_ID INTEGER,
  AP INTEGER NOT NULL,
	PRIMARY KEY(ID)
);

CREATE TABLE ABENTEUER_SES(
	ID INTEGER NOT NULL AUTO_INCREMENT,
	HELD INTEGER NOT NULL,
	ABENTEUER_ID INTEGER,
	SE VARCHAR(200) NOT NULL,
	PRIMARY KEY(ID)
);

CREATE TABLE SCRIPTS(
	ID INTEGER NOT NULL AUTO_INCREMENT,
	NAME varchar(40) NOT NULL,
  OWNER INTEGER NOT NULL,
  SCRIPT_HELPER MEDIUMTEXT NULL,
	PRIMARY KEY(ID)
);

CREATE TABLE SCRIPT_VARIABLE(
  ID INTEGER NOT NULL AUTO_INCREMENT,
	SCRIPT_ID INTEGER NOT NULL,
	NAME varchar(40),
	TYPE varchar(40),
	VALUE varchar(40),
	PRIMARY KEY(ID)
);
INSERT INTO ROLES_TO_RIGHTS VALUES
	(1, 6),
	(2, 6);

INSERT INTO ROLES_TO_RIGHTS VALUES
	(1, 7),
	(1, 8),
	(2, 8);
