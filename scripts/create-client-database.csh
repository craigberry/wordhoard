#!/bin/csh

###	Creates an empty wordhoard database.
#
#	create-client-database.csh [db]
#
#	db = database name (default "wordhoard")

set db = $1
if ($db == "") set db = "wordhoard"

echo Create client database

$MYSQL_BIN/mysql -u $MYSQL_ROOT_USERNAME -p$MYSQL_ROOT_PASSWORD --batch <<eof
drop database if exists $db;
create database $db character set utf8;
eof

java \
-Dhibernate.connection.url="jdbc:mysql://localhost/$db?characterEncoding=UTF-8" \
-Dhibernate.connection.username="$MYSQL_ROOT_USERNAME" \
-Dhibernate.connection.password="$MYSQL_ROOT_PASSWORD" \
org.hibernate.tool.hbm2ddl.SchemaExport \
--format \
bin/edu/northwestern/at/wordhoard/model/*.hbm.xml \
bin/edu/northwestern/at/wordhoard/model/annotations/*.hbm.xml \
bin/edu/northwestern/at/wordhoard/model/counts/*.hbm.xml \
bin/edu/northwestern/at/wordhoard/model/morphology/*.hbm.xml \
bin/edu/northwestern/at/wordhoard/model/speakers/*.hbm.xml \
bin/edu/northwestern/at/wordhoard/model/userdata/*.hbm.xml \
bin/edu/northwestern/at/wordhoard/model/tconview/*.hbm.xml \
bin/edu/northwestern/at/wordhoard/model/wrappers/*.hbm.xml \
>/dev/null

$MYSQL_BIN/mysql -u $MYSQL_ROOT_USERNAME -p$MYSQL_ROOT_PASSWORD --batch <<eof

use $db

alter table annotation drop index text_index;

create index nameString_index on author (name_string);

alter table bensonlempos drop index lemma_index;
alter table bensonlempos drop index pos_index;

create index title_index on corpus (title);
create index numWords_index on corpus (numWords);

alter table lemma change column tagInsensitive_string 
   tagInsensitive_string varchar(255)
   character set utf8 collate utf8_bin;
create index tagInsensitive_index on lemma (tagInsensitive_string);

alter table lemmacorpuscounts drop index corpus_index;
alter table lemmacorpuscounts drop index wordClass_index;
create index corpus_lemma_index on lemmacorpuscounts (corpus, lemma);

alter table lemmaworkcounts drop index work_index;
create index work_lemma_index on lemmaworkcounts (work, lemma);

alter table lempos drop index lemma_index;
create index lemma_pos_index on lempos (lemma, pos);

create index syntax_index on pos (syntax);
create index tense_index on pos (tense);
create index mood_index on pos (mood);
create index voice_index on pos (voice);
create index xcase_index on pos (xcase);
create index gender_index on pos (gender);
create index person_index on pos (person);
create index number_index on pos (number);
create index degree_index on pos (degree);
create index negative_index on pos (negative);

alter table phrasesetphrasecount change column phraseText_string
	phraseText_string text;
create index phrase_index on phrasesetphrasecount (phraseText_string(255));
create index phrasewordformworkparttag_index on 
	phrasesetphrasecount (phraseText_string(255), wordForm,workPartTag);

create index gender_index on speaker (gender_gender);
create index mortality_index on speaker (mortality_mortality);

create index gender_index on speech(gender_gender);
create index mortality_index on speech(mortality_mortality);

alter table word drop index workPart_index;
alter table word drop index prev_index;
alter table word drop index next_index;
alter table word drop index line_index;
create index part_line_index on word (workPart, location_start_index);
alter table word change column spellingInsensitive_string 
   spellingInsensitive_string varchar(255)
   character set utf8 collate utf8_bin;
create index spellingInsensitive_index on word (spellingInsensitive_string);
create index prosodic_index on word (prosodic_prosodic);
create index metricalShape_index on word (metricalShape_metricalShape);

create index wordform_work_word_index on wordcount (wordForm, work, word_string);
create index wordform_workpart_word_index on wordcount (wordForm, workPart, word_string);
create index wordform_word_index on wordcount (wordForm, word_string);

alter table lemmaposspellingcounts drop index corpus_index;
alter table lemmaposspellingcounts drop index work_index;
alter table lemmaposspellingcounts drop index workPart_index;
alter table lemmaposspellingcounts drop index lemma_index;
alter table lemmaposspellingcounts drop index pos_index;
create index kind_corpus_lemma_freq_index on lemmaposspellingcounts (kind, corpus, lemma, freq);
create index kind_corpus_pos_freq_index on lemmaposspellingcounts (kind, corpus, pos, freq);
create index kind_corpus_spelling_freq_index on lemmaposspellingcounts (kind, corpus, spelling_string, freq);
create index kind_work_lemma_freq_index on lemmaposspellingcounts (kind, work, lemma, freq);
create index kind_work_pos_freq_index on lemmaposspellingcounts (kind, work, pos, freq);
create index kind_work_spelling_freq_index on lemmaposspellingcounts (kind, work, spelling_string, freq);
create index kind_workPart_lemma_freq_index on lemmaposspellingcounts (kind, workPart, lemma, freq);
create index kind_workPart_pos_freq_index on lemmaposspellingcounts (kind, workPart, pos, freq);
create index kind_workPart_spelling_freq_index on lemmaposspellingcounts (kind, workPart, spelling_string, freq);
create index kind_lemma_freq_index on lemmaposspellingcounts (kind, lemma, freq);
create index kind_pos_freq_index on lemmaposspellingcounts (kind, pos, freq);
create index kind_spelling_freq_index on lemmaposspellingcounts (kind, spelling_string, freq);
create index kind_corpus_freq_index on lemmaposspellingcounts (kind, corpus, freq);
create index kind_work_freq_index on lemmaposspellingcounts (kind, work, freq);
create index kind_workPart_freq_index on lemmaposspellingcounts (kind, workPart, freq);
create index kind_freq_index on lemmaposspellingcounts (kind, freq);

alter table wordpart drop index bensonLemPos_index;

create index word_index on wordsetwordcount (word_string);
create index wordwordformworkparttag_index on 
	wordsetwordcount (word_string, wordForm,workPartTag);

create index wordtags_index on wordset_wordtags (wordTag);
create index tags_index on wordset_worktags (tag);
create index tags_index on wordset_workparttags (tag);
create index tags_index on workset_workparttags (tag);

alter table workpart drop index primaryText_index;
alter table workpart drop index parent_index;
create index numWords_index on workpart (numWords);
create index fullTitle_index on workpart (fullTitle);
create index pubDateStartYear_index on workpart (pubDate_startYear);
create index pubDateEndYear_index on workpart (pubDate_endYear);

create index majorWordClass_index on wordclass (majorWordClass_majorWordClass);

eof
