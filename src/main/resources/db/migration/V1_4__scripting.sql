CREATE TABLE SCRIPTS(
	ID INTEGER NOT NULL AUTO_INCREMENT,
	NAME varchar(40) NOT NULL,
  BODY MEDIUMTEXT NOT NULL,
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