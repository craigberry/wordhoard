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

use $db;
create table account (
    id bigint not null auto_increment,
    username varchar(255),
    password varchar(255),
    name varchar(255),
    nuAccount bit not null,
    canManageAccounts bit not null,
    primary key (id)
) engine=MyISAM;

alter table account
    add constraint UK_account_username unique (username);
eof

# The following runs but does not preserve column order, and also somehow gets polluted with a
# lot of classes from model, not server.model.  All we really want from it is the account table.

# java edu.northwestern.at.utils.tools.ExportSchema \
# --dialect=edu.northwestern.at.utils.db.mysql.WordHoardMySQLDialect \
# --entities=edu.northwestern.at.wordhoard.server.model \
# --output=misc/wordhoardserver.ddl \
# --url="jdbc:mysql://localhost/wordhoardserver?characterEncoding=UTF-8&useSSL=true&verifyServerCertificate=false" \
# --username="$MYSQL_ROOT_USERNAME" \
# --password="$MYSQL_ROOT_PASSWORD"
