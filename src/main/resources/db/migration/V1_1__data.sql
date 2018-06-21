CREATE TABLE USERS(
	ID INTEGER NOT NULL AUTO_INCREMENT,
	NAME varchar(40) NOT NULL,
	PASSWORD varchar(60),
	TOKEN varchar(64),
	GRUPPE_ID INTEGER NOT NULL,
	CREATED_DATE DATETIME  NOT NULL,
	PRIMARY KEY(ID)
);


CREATE TABLE HELDEN(
  ID INTEGER NOT NULL,
  USER_ID INTEGER NOT NULL,
  NAME MEDIUMTEXT NOT NULL,
  GRUPPE_ID INTEGER NOT NULL,
  PUBLIC TINYINT NOT NULL,
  DELETED TINYINT NOT NULL,
  CREATED_DATE DATETIME NOT NULL,
  PRIMARY KEY(ID)
);

CREATE TABLE HELD_VERSION(
  HELDID BIGINT NOT NULL,
  VERSION INTEGER NOT NULL,
  CREATED_DATE DATETIME NOT NULL,
  LAST_EVENT MEDIUMTEXT,
  PRIMARY KEY(HELDID, VERSION)
);

CREATE TABLE GRUPPEN(
  ID INTEGER NOT NULL,
  NAME VARCHAR(40) NOT NULL,
  PRIMARY KEY(ID)
);

INSERT INTO GRUPPEN VALUES(1, 'Der Runde Tisch');
INSERT INTO GRUPPEN VALUES(2, 'TestGruppe');
INSERT INTO GRUPPEN VALUES(3, 'Schlacht um Hamburg-Mitte');

INSERT INTO USERS VALUES(1, 'Admin', null, null,1, NOW());

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


INSERT INTO ROLES VALUES
	(1, 'Administrator');


INSERT INTO RIGHTS VALUES
	(1, 'CREATE_USER'),
	(2, 'VIEW_ALL'),
	(3, 'EDIT_RIGHTS'),
	(4, 'EDIT_ALL');

INSERT INTO ROLES_TO_USER VALUES
  (1, 1);

INSERT INTO ROLES_TO_RIGHTS VALUES
	(1, 1),
	(1, 2),
	(1, 3),
	(1, 4);