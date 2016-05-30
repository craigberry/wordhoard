#!/bin/csh

###	Analyzes tables.
#
#	analyze-tables [db]
#
#	db = database name (default "wordhoard")

set db = $1
if ($db == "") set db = "wordhoard"

echo "Analyzing tables"

$MYSQL_BIN/mysql -u $MYSQL_ROOT_USERNAME -p$MYSQL_ROOT_PASSWORD --batch >/dev/null <<eof
use $db;
analyze table annotation;
analyze table annotationcategory;
analyze table author;
analyze table authoredtextannotation;
analyze table authors_works;
analyze table bensonlemma;
analyze table bensonlempos;
analyze table bensonpos;
analyze table corpus;
analyze table corpus_tconviews;
analyze table lemma;
analyze table lemmacorpuscounts;
analyze table lemmaposspellingcounts;
analyze table lemmaworkcounts;
analyze table lempos;
analyze table line;
analyze table metricalshape;
analyze table phrase;
analyze table phrase_wordtags;
analyze table phraseset_phrases;
analyze table phrasesetphrasecount;
analyze table phrasesettotalwordformcount;
analyze table pos;
analyze table query;
analyze table speaker;
analyze table speech;
analyze table speech_speakers;
analyze table tconcategory;
analyze table tconcategory_worktags;
analyze table tconview;
analyze table tconview_categories;
analyze table tconview_worktags;
analyze table textwrapper;
analyze table totalwordformcount;
analyze table usergroup;
analyze table usergroup_admins;
analyze table usergroup_members;
analyze table usergrouppermission;
analyze table word;
analyze table wordclass;
analyze table wordcount;
analyze table wordpart;
analyze table wordset;
analyze table wordset_wordtags;
analyze table wordset_workparttags;
analyze table wordset_worktags;
analyze table wordsettotalwordformcount;
analyze table wordsetwordcount;
analyze table workpart;
analyze table workpart_children;
analyze table workpart_translations;
analyze table workset;
analyze table workset_workparttags;
eof
