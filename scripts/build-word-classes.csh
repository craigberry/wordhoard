#!/bin/csh

###	Builds the word classes.
#
#	build-word-classes.csh [dir] [db]
#
#	dir = data directory (default "data")
#
#	db = database name (default "wordhoard")

set dir = $1
if ($dir == "") set dir = "data"
set db = $2
if ($db == "") set db = "wordhoard"

java edu/northwestern/at/wordhoard/tools/BuildWordClasses $dir/word-classes.xml $db \
   $MYSQL_ROOT_USERNAME $MYSQL_ROOT_PASSWORD
