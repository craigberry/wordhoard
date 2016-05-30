#!/bin/csh

###	Creates MySQL grants for the wordhoard database.

$MYSQL_BIN/mysql -u $MYSQL_ROOT_USERNAME -p$MYSQL_ROOT_PASSWORD --batch --verbose --verbose <<eof
revoke all privileges, grant option from wordhoard;
grant select on wordhoard.* to wordhoard identified by 'wordhoard';
grant all on wordhoard.* to wordhoardserver@localhost 
	identified by 'wordhoardserver';
eof
