#!/bin/csh

###	Builds annotations.
#
#	build-annotations.csh [dir] [db]
#
#	dir = data directory (default "data")
#
#	db = database name (default "wordhoard")

set dir = $1
if ($dir == "") set dir = "data"
set db = $2
if ($db == "") set db = "wordhoard"

java -Xmx500m edu/northwestern/at/wordhoard/tools/BuildAnnotations $dir/annotations $db \
   $MYSQL_ROOT_USERNAME $MYSQL_ROOT_PASSWORD
