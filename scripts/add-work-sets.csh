#!/bin/csh

###	Adds work sets.
#
#	add-word-sets.csh [db]
#
#	db = database name (default "wordhoard")

set db = $1
if ($db == "") set db = "wordhoard"


echo "Adding work sets"
java -Xmx500m edu/northwestern/at/wordhoard/tools/AddWorkSets $db \
   $MYSQL_ROOT_USERNAME $MYSQL_ROOT_PASSWORD
