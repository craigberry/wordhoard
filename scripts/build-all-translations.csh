#!/bin/csh

###	Builds all the translations.
#
#	build-all-translations.csh [dir] [db]
#
#	dir = data directory (default "data")
#
#	db = database name (default "wordhoard")

set dir = $1
if ($dir == "") set dir = "data"
set db = $2
if ($db == "") set db = "wordhoard"

java -Xmx500m edu/northwestern/at/wordhoard/tools/BuildTranslations $dir/translations $db \
   $MYSQL_ROOT_USERNAME $MYSQL_ROOT_PASSWORD
