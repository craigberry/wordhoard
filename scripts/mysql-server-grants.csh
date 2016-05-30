#!/bin/csh

###	Creates MySQL grants for the wordhoardserver database.

$MYSQL_BIN/mysql -u $MYSQL_ROOT_USERNAME -p$MYSQL_ROOT_PASSWORD --batch --verbose --verbose <<eof
revoke all privileges, grant option from wordhoardserver@localhost;
grant all on wordhoardserver.* to wordhoardserver@localhost 
	identified by 'wordhoardserver';
eof
