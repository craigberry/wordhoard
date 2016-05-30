#!/bin/csh

###	Builds a translation.
#
#	build-translation corpus-tag work-tag [debug]
#
#	corpus-tag = corpus tag (e.g., "ege" for Shakespeare).
#	tran-tag = translation tag (e.g., "english").
#	work-tag = work tag (e.g. "IL" for The Iliad).

set corpusTag = $1
set tranTag = $2
set workTag = $3

java -Xmx500m edu/northwestern/at/wordhoard/tools/BuildTranslations \
"data/translations/$corpusTag/$tranTag/$workTag.xml" \
wordhoard $MYSQL_ROOT_USERNAME $MYSQL_ROOT_PASSWORD

