CREATE TABLE USERS(
	ID INTEGER NOT NULL AUTO_INCREMENT,
	NAME varchar(40) NOT NULL,
	PASSWORD varchar(60),
	TOKEN varchar(64) NOT NULL,
	GRUPPE_ID INTEGER NOT NULL,
	CREATED_DATE TIMESTAMP NOT NULL,
	PRIMARY KEY(ID)
);


CREATE TABLE HELDEN(
  BIGINT INTEGER NOT NULL AUTO_INCREMENT,
  USER_ID INTEGER NOT NULL AUTO_INCREMENT,
  NAME MEDIUMTEXT NOT NULL,
  ACTIVE TINYINT NOT NULL,
  XML TEXT NOT NULL,
  GRUPPE_ID INTEGER NOT NULL,
  CREATED_DATE TIMESTAMP NOT NULL
);

CREATE TABLE GRUPPEN(
  ID INTEGER NOT NULL,
  NAME VARCHAR(40) NOT NULL
);

INSERT INTO GRUPPEN VALUES(1, 'Der Runde Tisch');

INSERT INTO USERS VALUES(1, 'Failender', null, 'cead7ff39138dfb94171f19d8b46a487a4f1f53ad120ce819d6c0d86787b8c65',1, CURRENT_TIMESTAMP());