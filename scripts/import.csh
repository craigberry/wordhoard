#!/bin/csh

###	Imports a table into the MySQL "martin" database.
#
#	Usage:
#
#	import table-name file-name
#
#	The text file containing the table data as exported from Microsoft
#	Acces on ariadne is located in martin/file-name.txt.
#
#	Note: Null values in Martin's tables are not preserved, but instead
#	are imported as empty strings or the number 0. This unfortunate
#	infidelity turns out not to cause any problems.
#
#	WARNING: It is very important that Martin's tables be exported using
#	the UTF8 code page.

$MYSQL_BIN/mysql -u $MYSQL_ROOT_USERNAME -p$MYSQL_ROOT_PASSWORD --batch --verbose --verbose <<eof
use martin;
delete from $1;
load data infile
'/Users/jln/Documents/WordHoard/dev/martin/$2.txt'
into table $1
fields terminated by ',' optionally enclosed by '"' escaped by ''
lines terminated by '\r\n'
eof
