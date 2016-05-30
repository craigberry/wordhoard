#!/bin/csh

###	Builds a work.
#
#	build-work.csh corpus-tag work-tag [debug]
#
#	corpus-tag = corpus tag (e.g., "sha" for Shakespeare).
#	work-tag = work tag (e.g. "ham" for Hamlet).
#	debug = if present, build in debug mode.

set corpusTag = $1
set workTag = $2
set debug = $3

if (-e data/spellings) then
   java -Xmx1000m edu/northwestern/at/wordhoard/tools/BuildWorks \
   "data/works/$corpusTag/$workTag.xml" wordhoard \
   $MYSQL_ROOT_USERNAME $MYSQL_ROOT_PASSWORD data/spellings $debug
else
   java -Xmx1000m edu/northwestern/at/wordhoard/tools/BuildWorks \
   "data/works/$corpusTag/$workTag.xml" wordhoard \
   $MYSQL_ROOT_USERNAME $MYSQL_ROOT_PASSWORD $debug
endif   
