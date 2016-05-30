#!/bin/csh

###	Restores user tables.

mv user-tables/* $MYSQL_DATA/wordhoard
rm -rf user-tables
