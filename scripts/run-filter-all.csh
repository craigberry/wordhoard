#!/bin/csh

###	Runs a filter on all files.
#
#	run-filter-all filter-number
#
#	filter-number = filter number.

set n = $1

filters/filter$n/filter-all.csh

