#!/bin/csh

###	Creates an empty wordhoard server database.
#
#	create-server-database.csh [db]
#
#	db = database name (default "wordhoardserver")

set db = $1
if ($db == "") set db = "wordhoardserver"

$MYSQL_BIN/mysql -u $MYSQL_ROOT_USERNAME -p$MYSQL_ROOT_PASSWORD --batch --verbose --verbose <<eof
drop database if exists $db;
create database $db character set utf8;
eof

java \
-Dhibernate.connection.url="jdbc:mysql://localhost/$db?characterEncoding=UTF-8" \
-Dhibernate.connection.username="$MYSQL_ROOT_USERNAME" \
-Dhibernate.connection.password="$MYSQL_ROOT_PASSWORD" \
org.hibernate.tool.hbm2ddl.SchemaExport \
--properties=misc/server.properties \
--format bin/edu/northwestern/at/wordhoard/server/model/*.hbm.xml
