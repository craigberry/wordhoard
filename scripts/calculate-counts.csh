#!/bin/csh

###	Calculates counts.
#
#	calculate-counts [db]
#
#	db = database name (default "wordhoard")

set db = $1
if ($db == "") set db = "wordhoard"

date
echo "Calculating counts"

set tmpdir = "`pwd`/temp"
set word_file_1 = "$tmpdir/word_file_1"
set word_file_2 = "$tmpdir/word_file_2"
set word_file_3 = "$tmpdir/word_file_3"
set word_part_file_1 = "$tmpdir/word_part_file_1"
set word_part_file_2 = "$tmpdir/word_part_file_2"
set word_part_file_3 = "$tmpdir/word_part_file_3"
set speech_file_1 = "$tmpdir/speech_file_1"
set speech_file_2 = "$tmpdir/speech_file_2"
set speech_file_3 = "$tmpdir/speech_file_3"

rm -rf $tmpdir
mkdir $tmpdir
chmod 777 $tmpdir

echo "Dumping database tables"

$MYSQL_BIN/mysql -u $MYSQL_ROOT_USERNAME -p$MYSQL_ROOT_PASSWORD --batch <<eof
use $db
select workPart, id, spelling_string, spellingInsensitive_string, 
	spelling_charset, prosodic_prosodic, metricalShape_metricalShape,
	speech
	into outfile '$word_file_1'
	from word;
select workPart, word, partIndex, lemPos
	into outfile '$word_part_file_1'
	from wordpart;
select workPart, id, gender_gender, mortality_mortality 
	into outfile '$speech_file_1'
	from speech;
eof

echo "Preprocessing files"

java edu.northwestern.at.wordhoard.tools.CalculateCountsPreProcessor \
	$db $MYSQL_ROOT_USERNAME $MYSQL_ROOT_PASSWORD $word_file_1 $word_file_2
rm $word_file_1
java edu.northwestern.at.wordhoard.tools.CalculateCountsPreProcessor \
	$db $MYSQL_ROOT_USERNAME $MYSQL_ROOT_PASSWORD $word_part_file_1 $word_part_file_2
rm $word_part_file_1
java edu.northwestern.at.wordhoard.tools.CalculateCountsPreProcessor \
	$db $MYSQL_ROOT_USERNAME $MYSQL_ROOT_PASSWORD $speech_file_1 $speech_file_2
rm $speech_file_1
	
echo "Sorting files"

setenv LC_ALL C

sort $word_file_2 >$word_file_3
rm $word_file_2
sort $word_part_file_2 >$word_part_file_3
rm $word_part_file_2
sort $speech_file_2 >$speech_file_3	
rm $speech_file_2

java -Xmx500m edu.northwestern.at.wordhoard.tools.CalculateCounts \
	$db $MYSQL_ROOT_USERNAME $MYSQL_ROOT_PASSWORD $word_file_3 $word_part_file_3 $speech_file_3
	
rm -rf $tmpdir

date
