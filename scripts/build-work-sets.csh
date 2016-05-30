#!/bin/csh

###	Builds work sets.
#
#	build-work-sets.csh [dir] [db]
#
#	dir = data directory (default "data")
#
#	db = database name (default "wordhoard")

set dir = $1
if ($dir == "") set dir = "data"
set db = $2
if ($db == "") set db = "wordhoard"

java -Xmx500m edu/northwestern/at/wordhoard/tools/BuildWorkSets $dir/work-sets.xml $db \
   $MYSQL_ROOT_USERNAME $MYSQL_ROOT_PASSWORD
