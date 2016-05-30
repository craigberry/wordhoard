#!/bin/csh

###	Runs a filter on a single file.
#
#	run-filter filter-number corpus-tag work-tag
#
#	filter-number = filter number.
#	corpus-tag = corpus tag (e.g., "sha" for Shakespeare).
#	work-tag = work tag (e.g. "ham" for Hamlet).

set n = $1
set corpusTag = $2
set workTag = $3

filters/filter$n/filter-one.csh $corpusTag $workTag
