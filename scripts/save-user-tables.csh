#!/bin/csh

###	Saves user tables.

rm -rf user-tables
mkdir user-tables

mv $MYSQL_DATA/wordhoard/phrase.* user-tables
mv $MYSQL_DATA/wordhoard/phrase_wordtags.* user-tables
mv $MYSQL_DATA/wordhoard/phraseset_phrases.* user-tables
mv $MYSQL_DATA/wordhoard/phrasesetphrasecount.* user-tables
mv $MYSQL_DATA/wordhoard/phrasesettotalwordformcount.* user-tables
mv $MYSQL_DATA/wordhoard/query.* user-tables
mv $MYSQL_DATA/wordhoard/wordset.* user-tables
mv $MYSQL_DATA/wordhoard/wordset_wordtags.* user-tables
mv $MYSQL_DATA/wordhoard/wordset_workparttags.* user-tables
mv $MYSQL_DATA/wordhoard/wordset_worktags.* user-tables
mv $MYSQL_DATA/wordhoard/wordsettotalwordformcount.* user-tables
mv $MYSQL_DATA/wordhoard/wordsetwordcount.* user-tables
mv $MYSQL_DATA/wordhoard/workset.* user-tables
mv $MYSQL_DATA/wordhoard/workset_workparttags.* user-tables
mv $MYSQL_DATA/wordhoard/authoredtextannotation.* user-tables
mv $MYSQL_DATA/wordhoard/usergroup.* user-tables
mv $MYSQL_DATA/wordhoard/usergroup_admins.* user-tables
mv $MYSQL_DATA/wordhoard/usergroup_members.* user-tables
mv $MYSQL_DATA/wordhoard/usergrouppermission.* user-tables
