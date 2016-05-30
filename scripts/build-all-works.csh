#!/bin/csh

###	Builds all the works.
#
#	build-all-works.csh [dir] [db]
#
#	dir = data directory (default "data")
#
#	db = database name (default "wordhoard")
#
#	environment variable FULL_BUILD_MEMORY = heap space for BuildWorks (default 1g).

set dir = $1
if ($dir == "") set dir = "data"
set db = $2
if ($db == "") set db = "wordhoard"

if ($?FULL_BUILD_MEMORY) then
	set mem = $FULL_BUILD_MEMORY
else
	set mem = 1g
endif

if (-e $dir/spellings) then
   java -Xmx$mem edu/northwestern/at/wordhoard/tools/BuildWorks \
   $dir/works $db $MYSQL_ROOT_USERNAME $MYSQL_ROOT_PASSWORD $dir/spellings
else
   java -Xmx$mem edu/northwestern/at/wordhoard/tools/BuildWorks \
   $dir/works $db $MYSQL_ROOT_USERNAME $MYSQL_ROOT_PASSWORD
endif

