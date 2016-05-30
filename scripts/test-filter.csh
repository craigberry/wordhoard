#!/bin/csh

###	Tests a filter.
#
#	test-filter filter-number corpus-tag work-tag
#
#	filter-number = filter number.
#	corpus-tag = corpus tag (e.g., "sha" for Shakespeare).
#	work-tag = work tag (e.g. "ham" for Hamlet).

set n = $1
set corpusTag = $2
set workTag = $3

java -Xmx500m edu/northwestern/at/wordhoard/tools/BuildWorks \
filters/filter$n/out/$corpusTag/$workTag.xml debug
