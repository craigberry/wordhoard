#!/bin/csh

###	Applies Martin updates.
#
#	Before running this script, export Martin's new tables on Ariadne,
#	copy the exported files to your development host, and run the
#	import-all script to import the new tables into the MySQL "martin"
#	database.

java edu/northwestern/at/wordhoard/tools/martin/MartinWordClasses \
data/word-classes.xml

java edu/northwestern/at/wordhoard/tools/martin/MartinPos \
data/pos.xml

java -Xmx1000m edu/northwestern/at/wordhoard/tools/martin/UpdateWorkFiles \
data/works
