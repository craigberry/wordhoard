#!/bin/csh

###	Does a full rebuild of the WordHoard database.
#
#	full-build.csh [dir] [db]
#
#   dir = data directory (default "data")
#
#	db = database name (default "wordhoard")
#
#	environment variable FULL_BUILD_MEMORY = heap space for BuildWorks (default 1g).

set dir = $1
if ($dir == "") set dir = "data"
set db = $2
if ($db == "") set db = "wordhoard"

date
echo "Building database $db from directory $dir"

echo "================================================================================="
scripts/create-client-database.csh $db

echo "================================================================================="
scripts/build-corpora.csh $dir $db

echo "================================================================================="
scripts/build-authors.csh $dir $db

echo "================================================================================="
scripts/build-word-classes.csh $dir $db

echo "================================================================================="
scripts/build-pos.csh $dir $db

if (-e $dir/benson-glosses.xml) then
   echo "================================================================================="
   scripts/build-benson-glosses.csh $dir $db
endif

echo "================================================================================="
scripts/build-all-works.csh $dir $db

if (-e $dir/annotations) then
   echo "================================================================================="
   scripts/build-annotations.csh $dir $db
endif

if (-e $dir/translations) then
   echo "================================================================================="
   scripts/build-all-translations.csh $dir $db
endif

echo "================================================================================="
scripts/calculate-counts.csh $db

if (-e $dir/work-sets.xml) then
   echo "================================================================================="
   scripts/build-work-sets.csh $dir $db
endif

echo "================================================================================="
scripts/analyze-tables.csh $db

echo "================================================================================="

date

