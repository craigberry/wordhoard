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

java edu.northwestern.at.utils.tools.ExportSchema \
--dialect=edu.northwestern.at.utils.db.mysql.WordHoardMySQLDialect \
--entities=edu.northwestern.at.wordhoard.server.model \
--output=misc/wordhoardserver.ddl \
--url="jdbc:mysql://localhost/wordhoardserver?characterEncoding=UTF-8&useSSL=true&verifyServerCertificate=false" \
--jdbc_driver="com.mysql.cj.jdbc.Driver" \
--username="$MYSQL_ROOT_USERNAME" \
--password="$MYSQL_ROOT_PASSWORD"

perl -pi -e 's/engine=InnoDB/engine=MyISAM/' misc/wordhoardserver.ddl

$MYSQL_BIN/mysql -u $MYSQL_ROOT_USERNAME -p$MYSQL_ROOT_PASSWORD $db < misc/wordhoardserver.ddl

