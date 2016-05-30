#!/bin/csh

###	Builds the authors.
#
#	build-authors.csh [dir] [db]
#
#	dir = data directory (default "data")
#
#	db = database name (default "wordhoard")

set dir = $1
if ($dir == "") set dir = "data"
set db = $2
if ($db == "") set db = "wordhoard"

java edu/northwestern/at/wordhoard/tools/BuildAuthors $dir/authors.xml $db \
   $MYSQL_ROOT_USERNAME $MYSQL_ROOT_PASSWORD
