#!/bin/csh

###	Creates the "martin" database.

$MYSQL_BIN/mysql -u $MYSQL_ROOT_USERNAME -p$MYSQL_ROOT_PASSWORD --batch --verbose --verbose <<eof
drop database if exists martin;
create database martin character set utf8;
use martin;

--
-- Table structure for table wordclass;
--

DROP TABLE IF EXISTS wordclass;
CREATE TABLE wordclass (
  MAJORCLASS varchar(255) default NULL,
  WORDCLASS varchar(255) default NULL,
  ABBRWORDCLASS varchar(255) default NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Table structure for table pos;
--

DROP TABLE IF EXISTS pos;
CREATE TABLE pos (
  SYNTAX varchar(255) default NULL,
  NUPOS varchar(255) default NULL,
  TENSE varchar(255) default NULL,
  MOOD varchar(255) default NULL,
  VOICE varchar(255) default NULL,
  XCASE varchar(255) default NULL,
  GENDER varchar(255) default NULL,
  PERSON varchar(255) default NULL,
  NUMBER varchar(255) default NULL,
  DEGREE varchar(255) default NULL,
  NEGATIVE varchar(255) default NULL,
  LANGUAGE varchar(255) default NULL,
  ABBRWORDCLASS varchar(255) default NULL,
  MAJORCLASS varchar(255) default NULL,
  WORDCLASS varchar(255) default NULL,
  description varchar(255) default NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Table structure for table english;
--

DROP TABLE IF EXISTS english;
CREATE TABLE english (
  id varchar(255) default NULL,
  wordoccurrenceid varchar(255) default NULL,
  spelling varchar(255) default NULL,
  standardspelling varchar(255) default NULL,
  NUPOS varchar(255) default NULL,
  lemma varchar(255) default NULL,
  lemma_homonym varchar(255) default NULL,
  lemma_wordclass varchar(255) default NULL,
  lemma2 varchar(255) default NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
create index x on english (wordoccurrenceid);

--
-- Table structure for table greek;
--

DROP TABLE IF EXISTS greek;
CREATE TABLE greek (
  field1 varchar(255) default NULL,
  wordoccurrenceid varchar(255) default NULL,
  field3 varchar(255) default NULL,
  field4 varchar(255) default NULL,
  NUPOS varchar(255) default NULL,
  lemma varchar(255) default NULL,
  field7 varchar(255) default NULL,
  field8 varchar(255) default NULL,
  field9 varchar(255) default NULL,
  field10 varchar(255) default NULL,
  field11 varchar(255) default NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
create index x on greek (wordoccurrenceid);

eof
