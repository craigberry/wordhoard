
    alter table annotation 
        drop 
        foreign key workPart_index

    alter table annotation 
        drop 
        foreign key category_index

    alter table annotation 
        drop 
        foreign key text_index

    alter table authoredtextannotation 
        drop 
        foreign key category_index

    alter table authors_works 
        drop 
        foreign key author_id_index

    alter table authors_works 
        drop 
        foreign key work_id_index

    alter table bensonlempos 
        drop 
        foreign key pos_index

    alter table bensonlempos 
        drop 
        foreign key lemma_index

    alter table corpus_tconviews 
        drop 
        foreign key FK3B1FE2E970181B1E

    alter table corpus_tconviews 
        drop 
        foreign key FK3B1FE2E97B5EECB3

    alter table lemma 
        drop 
        foreign key wordClass_index

    alter table lemmacorpuscounts 
        drop 
        foreign key wordClass_index

    alter table lemmacorpuscounts 
        drop 
        foreign key lemma_index

    alter table lemmacorpuscounts 
        drop 
        foreign key corpus_index

    alter table lemmaposspellingcounts 
        drop 
        foreign key pos_index

    alter table lemmaposspellingcounts 
        drop 
        foreign key work_index

    alter table lemmaposspellingcounts 
        drop 
        foreign key lemma_index

    alter table lemmaposspellingcounts 
        drop 
        foreign key workPart_index

    alter table lemmaposspellingcounts 
        drop 
        foreign key corpus_index

    alter table lemmaworkcounts 
        drop 
        foreign key work_index

    alter table lemmaworkcounts 
        drop 
        foreign key lemma_index

    alter table lempos 
        drop 
        foreign key pos_index

    alter table lempos 
        drop 
        foreign key lemma_index

    alter table line 
        drop 
        foreign key workPart_index

    alter table phrase_wordtags 
        drop 
        foreign key phraseid_index

    alter table phraseset_phrases 
        drop 
        foreign key phrasesetid_index

    alter table phraseset_phrases 
        drop 
        foreign key phraseid_index

    alter table phrasesetphrasecount 
        drop 
        foreign key phraseSet_index

    alter table phrasesettotalwordformcount 
        drop 
        foreign key phrase_index

    alter table pos 
        drop 
        foreign key wordClass_index

    alter table speaker 
        drop 
        foreign key work_index

    alter table speech 
        drop 
        foreign key workPart_index

    alter table speech_speakers 
        drop 
        foreign key FKA9D0A711477943D1

    alter table speech_speakers 
        drop 
        foreign key FKA9D0A711E19992E3

    alter table tconcategory_worktags 
        drop 
        foreign key FK832AEDFD9A0015D0

    alter table tconview_categories 
        drop 
        foreign key FK66790DA870181B1E

    alter table tconview_categories 
        drop 
        foreign key FK66790DA8E7D04742

    alter table tconview_worktags 
        drop 
        foreign key FK37F8453670181B1E

    alter table totalwordformcount 
        drop 
        foreign key work_index

    alter table totalwordformcount 
        drop 
        foreign key workPart_index

    alter table usergroup_admins 
        drop 
        foreign key usergroup_admins_index

    alter table usergroup_members 
        drop 
        foreign key usergroup_members_index

    alter table usergrouppermission 
        drop 
        foreign key userdata_index

    alter table usergrouppermission 
        drop 
        foreign key usergroup_index

    alter table word 
        drop 
        foreign key speech_index

    alter table word 
        drop 
        foreign key line_index

    alter table word 
        drop 
        foreign key work_index

    alter table word 
        drop 
        foreign key workPart_index

    alter table word 
        drop 
        foreign key prev_index

    alter table word 
        drop 
        foreign key next_index

    alter table wordcount 
        drop 
        foreign key work_index

    alter table wordcount 
        drop 
        foreign key workPart_index

    alter table wordpart 
        drop 
        foreign key lemPos_index

    alter table wordpart 
        drop 
        foreign key word_index

    alter table wordpart 
        drop 
        foreign key bensonLemPos_index

    alter table wordpart 
        drop 
        foreign key workPart_index

    alter table wordset_wordtags 
        drop 
        foreign key wordset_wordTags_index

    alter table wordset_workparttags 
        drop 
        foreign key wordset_workPartTags_index

    alter table wordset_worktags 
        drop 
        foreign key wordset_workTags_index

    alter table wordsettotalwordformcount 
        drop 
        foreign key word_index

    alter table wordsetwordcount 
        drop 
        foreign key wordSet_index

    alter table workpart 
        drop 
        foreign key work_index

    alter table workpart 
        drop 
        foreign key primaryText_index

    alter table workpart 
        drop 
        foreign key parent_index

    alter table workpart 
        drop 
        foreign key corpus_index

    alter table workpart_children 
        drop 
        foreign key FK63AEE95ABAAEFE25

    alter table workpart_children 
        drop 
        foreign key FK63AEE95AD32B97D7

    alter table workpart_translations 
        drop 
        foreign key FK617DC9BD723B60CF

    alter table workpart_translations 
        drop 
        foreign key FK617DC9BDBC3FDFBD

    alter table workset_workparttags 
        drop 
        foreign key workset_index

    drop table if exists annotation

    drop table if exists annotationcategory

    drop table if exists author

    drop table if exists authoredtextannotation

    drop table if exists authors_works

    drop table if exists bensonlemma

    drop table if exists bensonlempos

    drop table if exists bensonpos

    drop table if exists corpus

    drop table if exists corpus_tconviews

    drop table if exists lemma

    drop table if exists lemmacorpuscounts

    drop table if exists lemmaposspellingcounts

    drop table if exists lemmaworkcounts

    drop table if exists lempos

    drop table if exists line

    drop table if exists metricalshape

    drop table if exists phrase

    drop table if exists phrase_wordtags

    drop table if exists phraseset_phrases

    drop table if exists phrasesetphrasecount

    drop table if exists phrasesettotalwordformcount

    drop table if exists pos

    drop table if exists query

    drop table if exists speaker

    drop table if exists speech

    drop table if exists speech_speakers

    drop table if exists tconcategory

    drop table if exists tconcategory_worktags

    drop table if exists tconview

    drop table if exists tconview_categories

    drop table if exists tconview_worktags

    drop table if exists textwrapper

    drop table if exists totalwordformcount

    drop table if exists usergroup

    drop table if exists usergroup_admins

    drop table if exists usergroup_members

    drop table if exists usergrouppermission

    drop table if exists word

    drop table if exists wordclass

    drop table if exists wordcount

    drop table if exists wordpart

    drop table if exists wordset

    drop table if exists wordset_wordtags

    drop table if exists wordset_workparttags

    drop table if exists wordset_worktags

    drop table if exists wordsettotalwordformcount

    drop table if exists wordsetwordcount

    drop table if exists workpart

    drop table if exists workpart_children

    drop table if exists workpart_translations

    drop table if exists workset

    drop table if exists workset_workparttags

    create table annotation (
        id bigint not null auto_increment,
        type varchar(255) not null,
        category bigint,
        text bigint,
        workPart bigint,
        target_start_index integer,
        target_start_offset integer,
        target_end_index integer,
        target_end_offset integer,
        primary key (id)
    )

    create table annotationcategory (
        id bigint not null auto_increment,
        name varchar(255),
        primary key (id)
    )

    create table author (
        id bigint not null auto_increment,
        name_string varchar(255),
        name_charset tinyint,
        originalName_string varchar(255),
        originalName_charset tinyint,
        birthYear integer,
        deathYear integer,
        earliestWorkYear integer,
        latestWorkYear integer,
        primary key (id)
    )

    create table authoredtextannotation (
        id bigint not null auto_increment,
        body text,
        category bigint,
        target_start_index integer,
        target_start_offset integer,
        target_end_index integer,
        target_end_offset integer,
        title varchar(255),
        description varchar(255),
        webPageURL varchar(255),
        creationTime datetime,
        modificationTime datetime,
        author varchar(255),
        annotates varchar(255),
        type varchar(255),
        status varchar(255),
        owner varchar(255),
        isPublic bit,
        isActive bit,
        query varchar(255),
        primary key (id)
    )

    create table authors_works (
        author_id bigint not null,
        work_id bigint not null,
        primary key (author_id, work_id)
    )

    create table bensonlemma (
        id bigint not null,
        word varchar(255),
        wordClass varchar(255),
        homonym integer,
        definition varchar(255),
        comment varchar(255),
        oedLemma varchar(255),
        primary key (id)
    )

    create table bensonlempos (
        id bigint not null,
        lemma bigint,
        pos bigint,
        primary key (id)
    )

    create table bensonpos (
        id bigint not null,
        tag varchar(255),
        description varchar(255),
        primary key (id)
    )

    create table corpus (
        id bigint not null,
        tag varchar(255),
        title varchar(255),
        charset tinyint,
        posType tinyint,
        taggingData_flags bigint,
        numWorkParts integer,
        numLines integer,
        numWords integer,
        maxWordPathLength integer,
        translations varchar(255),
        tranDescription text,
        ordinal integer,
        primary key (id)
    )

    create table corpus_tconviews (
        corpus bigint not null,
        tconview bigint not null,
        corpus_index integer not null,
        primary key (corpus, corpus_index)
    )

    create table lemma (
        id bigint not null,
        tag_string varchar(255),
        tag_charset tinyint,
        tagInsensitive_string varchar(255),
        tagInsensitive_charset tinyint,
        spelling_string varchar(255),
        spelling_charset tinyint,
        homonym integer,
        wordClass bigint,
        primary key (id)
    )

    create table lemmacorpuscounts (
        id bigint not null auto_increment,
        corpus bigint,
        lemma bigint,
        tag_string varchar(255),
        tag_charset tinyint,
        wordClass bigint,
        majorClass varchar(255),
        colFreq integer,
        docFreq integer,
        rank1 integer,
        rank2 integer,
        numMajorClass integer,
        primary key (id)
    )

    create table lemmaposspellingcounts (
        id bigint not null auto_increment,
        kind tinyint,
        corpus bigint,
        work bigint,
        workPart bigint,
        lemma bigint,
        pos bigint,
        spelling_string varchar(255),
        spelling_charset tinyint,
        freq integer,
        freqFirstWordPart integer,
        primary key (id)
    )

    create table lemmaworkcounts (
        id bigint not null auto_increment,
        work bigint,
        lemma bigint,
        termFreq integer,
        rank1 integer,
        rank2 integer,
        numMajorClass integer,
        primary key (id)
    )

    create table lempos (
        id bigint not null,
        standardSpelling_string varchar(255),
        standardSpelling_charset tinyint,
        lemma bigint,
        pos bigint,
        primary key (id)
    )

    create table line (
        id bigint not null,
        tag varchar(255),
        number integer,
        label varchar(255),
        stanzaLabel varchar(255),
        workPart bigint,
        location_start_index integer,
        location_start_offset integer,
        location_end_index integer,
        location_end_offset integer,
        primary key (id)
    )

    create table metricalshape (
        id bigint not null auto_increment,
        metricalShape varchar(255),
        primary key (id)
    )

    create table phrase (
        id bigint not null auto_increment,
        workTag varchar(32),
        tagsHashCode integer,
        primary key (id)
    )

    create table phrase_wordtags (
        phraseId bigint not null,
        wordTag varchar(32),
        word_index integer not null,
        primary key (phraseId, word_index)
    )

    create table phraseset_phrases (
        phraseSetId bigint not null,
        phraseId bigint not null,
        primary key (phraseSetId, phraseId)
    )

    create table phrasesetphrasecount (
        id bigint not null auto_increment,
        phraseText_string varchar(255),
        phraseText_charset tinyint,
        wordForm integer,
        phraseSet bigint,
        workPartTag varchar(32),
        phraseCount integer,
        primary key (id)
    )

    create table phrasesettotalwordformcount (
        id bigint not null auto_increment,
        wordForm integer,
        phraseSet bigint,
        workPartTag varchar(32),
        wordFormCount integer,
        primary key (id)
    )

    create table pos (
        id bigint not null auto_increment,
        tag varchar(255),
        description varchar(255),
        wordClass bigint,
        syntax varchar(255),
        tense varchar(255),
        mood varchar(255),
        voice varchar(255),
        xcase varchar(255),
        gender varchar(255),
        person varchar(255),
        number varchar(255),
        degree varchar(255),
        negative varchar(255),
        language varchar(255),
        primary key (id)
    )

    create table query (
        id bigint not null auto_increment,
        title varchar(255),
        description text,
        webPageURL varchar(255),
        creationTime datetime,
        modificationTime datetime,
        owner varchar(255),
        isPublic bit,
        isActive bit,
        queryType integer,
        queryText varchar(255),
        primary key (id)
    )

    create table speaker (
        id bigint not null auto_increment,
        work bigint,
        tag varchar(255),
        name varchar(255),
        originalName_string varchar(255),
        originalName_charset tinyint,
        description varchar(255),
        gender_gender tinyint,
        mortality_mortality tinyint,
        primary key (id)
    )

    create table speech (
        id bigint not null,
        workPart bigint,
        gender_gender tinyint,
        mortality_mortality tinyint,
        primary key (id)
    )

    create table speech_speakers (
        speech_id bigint not null,
        speaker_id bigint not null,
        primary key (speech_id, speaker_id)
    )

    create table tconcategory (
        id bigint not null,
        title varchar(255),
        primary key (id)
    )

    create table tconcategory_worktags (
        tconcategory bigint not null,
        worktag varchar(32),
        tconcategory_index integer not null,
        primary key (tconcategory, tconcategory_index)
    )

    create table tconview (
        id bigint not null,
        viewType integer,
        radioButtonLabel varchar(255),
        primary key (id)
    )

    create table tconview_categories (
        tconview bigint not null,
        category bigint not null,
        tconview_index integer not null,
        primary key (tconview, tconview_index)
    )

    create table tconview_worktags (
        tconview bigint not null,
        worktag varchar(32),
        tconview_index integer not null,
        primary key (tconview, tconview_index)
    )

    create table textwrapper (
        id bigint not null auto_increment,
        text mediumblob,
        primary key (id)
    )

    create table totalwordformcount (
        id bigint not null auto_increment,
        wordForm integer,
        workPart bigint,
        work bigint,
        wordFormCount integer,
        primary key (id)
    )

    create table usergroup (
        id bigint not null auto_increment,
        title varchar(255),
        description varchar(255),
        webPageURL varchar(255),
        creationTime datetime,
        modificationTime datetime,
        owner varchar(255),
        isPublic bit,
        isActive bit,
        query varchar(255),
        primary key (id)
    )

    create table usergroup_admins (
        usergroup bigint not null,
        admin varchar(32)
    )

    create table usergroup_members (
        usergroup bigint not null,
        member varchar(32)
    )

    create table usergrouppermission (
        id bigint not null auto_increment,
        title varchar(255),
        description varchar(255),
        webPageURL varchar(255),
        creationTime datetime,
        modificationTime datetime,
        owner varchar(255),
        isPublic bit,
        isActive bit,
        query varchar(255),
        permission varchar(255),
        userGroup bigint,
        authoredTextAnnotation bigint,
        primary key (id)
    )

    create table word (
        id bigint not null,
        spelling_string varchar(255),
        spelling_charset tinyint,
        spellingInsensitive_string varchar(255),
        spellingInsensitive_charset tinyint,
        workPart bigint,
        work bigint,
        line bigint,
        path varchar(255),
        tag varchar(255),
        location_start_index integer,
        location_start_offset integer,
        location_end_index integer,
        location_end_offset integer,
        puncBefore varchar(255),
        puncAfter varchar(255),
        prev bigint,
        next bigint,
        workTag varchar(255),
        workOrdinal integer,
        colocationOrdinal bigint,
        speech bigint,
        prosodic_prosodic tinyint,
        metricalShape_metricalShape varchar(255),
        primary key (id)
    )

    create table wordclass (
        id bigint not null auto_increment,
        tag varchar(255),
        description varchar(255),
        majorWordClass_majorWordClass varchar(255),
        primary key (id)
    )

    create table wordcount (
        id bigint not null auto_increment,
        word_string varchar(255),
        word_charset tinyint,
        wordForm integer,
        workPart bigint,
        work bigint,
        wordCount integer,
        primary key (id)
    )

    create table wordpart (
        id bigint not null,
        tag varchar(255),
        partIndex integer,
        word bigint,
        workPart bigint,
        lemPos bigint,
        bensonLemPos bigint,
        primary key (id)
    )

    create table wordset (
        id bigint not null auto_increment,
        is_wordset integer not null,
        title varchar(255),
        description text,
        webPageURL varchar(255),
        creationTime datetime,
        modificationTime datetime,
        owner varchar(255),
        isPublic bit,
        isActive bit,
        query varchar(255),
        sumPhraseLengths double precision,
        primary key (id)
    )

    create table wordset_wordtags (
        wordSet bigint not null,
        wordTag varchar(32)
    )

    create table wordset_workparttags (
        wordSet bigint not null,
        tag varchar(32)
    )

    create table wordset_worktags (
        wordSet bigint not null,
        tag varchar(32)
    )

    create table wordsettotalwordformcount (
        id bigint not null auto_increment,
        wordForm integer,
        wordSet bigint,
        workPartTag varchar(32),
        wordFormCount integer,
        primary key (id)
    )

    create table wordsetwordcount (
        id bigint not null auto_increment,
        word_string varchar(255),
        word_charset tinyint,
        wordForm integer,
        wordSet bigint,
        workPartTag varchar(32),
        wordCount integer,
        primary key (id)
    )

    create table workpart (
        id bigint not null,
        is_work integer not null,
        tag varchar(255),
        pathTag varchar(255),
        shortTitle varchar(255),
        fullTitle varchar(255),
        taggingData_flags bigint,
        parent bigint,
        work bigint,
        hasChildren bit,
        primaryText bigint,
        workOrdinal integer,
        numLines integer,
        numWords integer,
        hasStanzaNumbers bit,
        corpus bigint,
        pubDate_startYear integer,
        pubDate_endYear integer,
        primary key (id)
    )

    create table workpart_children (
        parent_id bigint not null,
        child_id bigint not null,
        child_index integer not null,
        primary key (parent_id, child_index)
    )

    create table workpart_translations (
        workPart_id bigint not null,
        textWrapper_id bigint not null,
        translation_name varchar(255) not null,
        primary key (workPart_id, translation_name)
    )

    create table workset (
        id bigint not null auto_increment,
        title varchar(255),
        description text,
        webPageURL varchar(255),
        creationTime datetime,
        modificationTime datetime,
        owner varchar(255),
        isPublic bit,
        isActive bit,
        query varchar(255),
        primary key (id)
    )

    create table workset_workparttags (
        workSet bigint not null,
        tag varchar(32)
    )

    alter table annotation 
        add index workPart_index (workPart), 
        add constraint workPart_index 
        foreign key (workPart) 
        references workpart (id)

    alter table annotation 
        add index category_index (category), 
        add constraint category_index 
        foreign key (category) 
        references annotationcategory (id)

    alter table annotation 
        add index text_index (text), 
        add constraint text_index 
        foreign key (text) 
        references textwrapper (id)

    create index birthYear_index on author (birthYear)

    create index latestWorkYear_index on author (latestWorkYear)

    create index deathYear_index on author (deathYear)

    create index earliestWorkYear_index on author (earliestWorkYear)

    create index isActive_index on authoredtextannotation (isActive)

    create index wordset_isPublic_index on authoredtextannotation (isPublic)

    create index wordset_owner_index on authoredtextannotation (owner)

    alter table authoredtextannotation 
        add index category_index (category), 
        add constraint category_index 
        foreign key (category) 
        references annotationcategory (id)

    alter table authors_works 
        add index author_id_index (author_id), 
        add constraint author_id_index 
        foreign key (author_id) 
        references author (id)

    alter table authors_works 
        add index work_id_index (work_id), 
        add constraint work_id_index 
        foreign key (work_id) 
        references workpart (id)

    alter table bensonlempos 
        add index pos_index (pos), 
        add constraint pos_index 
        foreign key (pos) 
        references bensonpos (id)

    alter table bensonlempos 
        add index lemma_index (lemma), 
        add constraint lemma_index 
        foreign key (lemma) 
        references bensonlemma (id)

    create index tag_index on corpus (tag)

    alter table corpus_tconviews 
        add index FK3B1FE2E970181B1E (tconview), 
        add constraint FK3B1FE2E970181B1E 
        foreign key (tconview) 
        references tconview (id)

    alter table corpus_tconviews 
        add index FK3B1FE2E97B5EECB3 (corpus), 
        add constraint FK3B1FE2E97B5EECB3 
        foreign key (corpus) 
        references corpus (id)

    alter table lemma 
        add index wordClass_index (wordClass), 
        add constraint wordClass_index 
        foreign key (wordClass) 
        references wordclass (id)

    alter table lemmacorpuscounts 
        add index wordClass_index (wordClass), 
        add constraint wordClass_index 
        foreign key (wordClass) 
        references wordclass (id)

    alter table lemmacorpuscounts 
        add index lemma_index (lemma), 
        add constraint lemma_index 
        foreign key (lemma) 
        references lemma (id)

    alter table lemmacorpuscounts 
        add index corpus_index (corpus), 
        add constraint corpus_index 
        foreign key (corpus) 
        references corpus (id)

    alter table lemmaposspellingcounts 
        add index pos_index (pos), 
        add constraint pos_index 
        foreign key (pos) 
        references pos (id)

    alter table lemmaposspellingcounts 
        add index work_index (work), 
        add constraint work_index 
        foreign key (work) 
        references workpart (id)

    alter table lemmaposspellingcounts 
        add index lemma_index (lemma), 
        add constraint lemma_index 
        foreign key (lemma) 
        references lemma (id)

    alter table lemmaposspellingcounts 
        add index workPart_index (workPart), 
        add constraint workPart_index 
        foreign key (workPart) 
        references workpart (id)

    alter table lemmaposspellingcounts 
        add index corpus_index (corpus), 
        add constraint corpus_index 
        foreign key (corpus) 
        references corpus (id)

    alter table lemmaworkcounts 
        add index work_index (work), 
        add constraint work_index 
        foreign key (work) 
        references workpart (id)

    alter table lemmaworkcounts 
        add index lemma_index (lemma), 
        add constraint lemma_index 
        foreign key (lemma) 
        references lemma (id)

    alter table lempos 
        add index pos_index (pos), 
        add constraint pos_index 
        foreign key (pos) 
        references pos (id)

    alter table lempos 
        add index lemma_index (lemma), 
        add constraint lemma_index 
        foreign key (lemma) 
        references lemma (id)

    create index tag_index on line (tag)

    alter table line 
        add index workPart_index (workPart), 
        add constraint workPart_index 
        foreign key (workPart) 
        references workpart (id)

    alter table phrase_wordtags 
        add index phraseid_index (phraseId), 
        add constraint phraseid_index 
        foreign key (phraseId) 
        references phrase (id)

    alter table phraseset_phrases 
        add index phrasesetid_index (phraseSetId), 
        add constraint phrasesetid_index 
        foreign key (phraseSetId) 
        references wordset (id)

    alter table phraseset_phrases 
        add index phraseid_index (phraseId), 
        add constraint phraseid_index 
        foreign key (phraseId) 
        references phrase (id)

    create index wordForm_index on phrasesetphrasecount (wordForm)

    create index workPartTag_index on phrasesetphrasecount (workPartTag)

    alter table phrasesetphrasecount 
        add index phraseSet_index (phraseSet), 
        add constraint phraseSet_index 
        foreign key (phraseSet) 
        references wordset (id)

    create index wordForm_index on phrasesettotalwordformcount (wordForm)

    create index workPartTag_index on phrasesettotalwordformcount (workPartTag)

    alter table phrasesettotalwordformcount 
        add index phrase_index (phraseSet), 
        add constraint phrase_index 
        foreign key (phraseSet) 
        references wordset (id)

    alter table pos 
        add index wordClass_index (wordClass), 
        add constraint wordClass_index 
        foreign key (wordClass) 
        references wordclass (id)

    create index queryType_index on query (queryType)

    create index isActive_index on query (isActive)

    create index title_index on query (title)

    create index owner_index on query (owner)

    create index isPublic_index on query (isPublic)

    create index name_index on speaker (name)

    alter table speaker 
        add index work_index (work), 
        add constraint work_index 
        foreign key (work) 
        references workpart (id)

    alter table speech 
        add index workPart_index (workPart), 
        add constraint workPart_index 
        foreign key (workPart) 
        references workpart (id)

    alter table speech_speakers 
        add index FKA9D0A711477943D1 (speech_id), 
        add constraint FKA9D0A711477943D1 
        foreign key (speech_id) 
        references speech (id)

    alter table speech_speakers 
        add index FKA9D0A711E19992E3 (speaker_id), 
        add constraint FKA9D0A711E19992E3 
        foreign key (speaker_id) 
        references speaker (id)

    alter table tconcategory_worktags 
        add index FK832AEDFD9A0015D0 (tconcategory), 
        add constraint FK832AEDFD9A0015D0 
        foreign key (tconcategory) 
        references tconcategory (id)

    alter table tconview_categories 
        add index FK66790DA870181B1E (tconview), 
        add constraint FK66790DA870181B1E 
        foreign key (tconview) 
        references tconview (id)

    alter table tconview_categories 
        add index FK66790DA8E7D04742 (category), 
        add constraint FK66790DA8E7D04742 
        foreign key (category) 
        references tconcategory (id)

    alter table tconview_worktags 
        add index FK37F8453670181B1E (tconview), 
        add constraint FK37F8453670181B1E 
        foreign key (tconview) 
        references tconview (id)

    create index wordForm_index on totalwordformcount (wordForm)

    alter table totalwordformcount 
        add index work_index (work), 
        add constraint work_index 
        foreign key (work) 
        references workpart (id)

    alter table totalwordformcount 
        add index workPart_index (workPart), 
        add constraint workPart_index 
        foreign key (workPart) 
        references workpart (id)

    create index isActive_index on usergroup (isActive)

    create index wordset_isPublic_index on usergroup (isPublic)

    create index wordset_owner_index on usergroup (owner)

    alter table usergroup_admins 
        add index usergroup_admins_index (usergroup), 
        add constraint usergroup_admins_index 
        foreign key (usergroup) 
        references usergroup (id)

    alter table usergroup_members 
        add index usergroup_members_index (usergroup), 
        add constraint usergroup_members_index 
        foreign key (usergroup) 
        references usergroup (id)

    create index isActive_index on usergrouppermission (isActive)

    create index wordset_isPublic_index on usergrouppermission (isPublic)

    create index wordset_owner_index on usergrouppermission (owner)

    alter table usergrouppermission 
        add index userdata_index (authoredTextAnnotation), 
        add constraint userdata_index 
        foreign key (authoredTextAnnotation) 
        references authoredtextannotation (id)

    alter table usergrouppermission 
        add index usergroup_index (userGroup), 
        add constraint usergroup_index 
        foreign key (userGroup) 
        references usergroup (id)

    create index colocationOrdinal_index on word (colocationOrdinal)

    create index tag_index on word (tag)

    alter table word 
        add index speech_index (speech), 
        add constraint speech_index 
        foreign key (speech) 
        references speech (id)

    alter table word 
        add index line_index (line), 
        add constraint line_index 
        foreign key (line) 
        references line (id)

    alter table word 
        add index work_index (work), 
        add constraint work_index 
        foreign key (work) 
        references workpart (id)

    alter table word 
        add index workPart_index (workPart), 
        add constraint workPart_index 
        foreign key (workPart) 
        references workpart (id)

    alter table word 
        add index prev_index (prev), 
        add constraint prev_index 
        foreign key (prev) 
        references word (id)

    alter table word 
        add index next_index (next), 
        add constraint next_index 
        foreign key (next) 
        references word (id)

    create index wordForm_index on wordcount (wordForm)

    alter table wordcount 
        add index work_index (work), 
        add constraint work_index 
        foreign key (work) 
        references workpart (id)

    alter table wordcount 
        add index workPart_index (workPart), 
        add constraint workPart_index 
        foreign key (workPart) 
        references workpart (id)

    create index tag_index on wordpart (tag)

    alter table wordpart 
        add index lemPos_index (lemPos), 
        add constraint lemPos_index 
        foreign key (lemPos) 
        references lempos (id)

    alter table wordpart 
        add index word_index (word), 
        add constraint word_index 
        foreign key (word) 
        references word (id)

    alter table wordpart 
        add index bensonLemPos_index (bensonLemPos), 
        add constraint bensonLemPos_index 
        foreign key (bensonLemPos) 
        references bensonlempos (id)

    alter table wordpart 
        add index workPart_index (workPart), 
        add constraint workPart_index 
        foreign key (workPart) 
        references workpart (id)

    create index isActive_index on wordset (isActive)

    create index wordset_isPublic_index on wordset (isPublic)

    create index wordset_title_index on wordset (title)

    create index wordset_owner_index on wordset (owner)

    alter table wordset_wordtags 
        add index wordset_wordTags_index (wordSet), 
        add constraint wordset_wordTags_index 
        foreign key (wordSet) 
        references wordset (id)

    alter table wordset_workparttags 
        add index wordset_workPartTags_index (wordSet), 
        add constraint wordset_workPartTags_index 
        foreign key (wordSet) 
        references wordset (id)

    alter table wordset_worktags 
        add index wordset_workTags_index (wordSet), 
        add constraint wordset_workTags_index 
        foreign key (wordSet) 
        references wordset (id)

    create index wordForm_index on wordsettotalwordformcount (wordForm)

    create index workPartTag_index on wordsettotalwordformcount (workPartTag)

    alter table wordsettotalwordformcount 
        add index word_index (wordSet), 
        add constraint word_index 
        foreign key (wordSet) 
        references wordset (id)

    create index wordcount_wordForm_index on wordsetwordcount (wordForm)

    create index workPartTag_index on wordsetwordcount (workPartTag)

    alter table wordsetwordcount 
        add index wordSet_index (wordSet), 
        add constraint wordSet_index 
        foreign key (wordSet) 
        references wordset (id)

    create index tag_index on workpart (tag)

    alter table workpart 
        add index work_index (work), 
        add constraint work_index 
        foreign key (work) 
        references workpart (id)

    alter table workpart 
        add index primaryText_index (primaryText), 
        add constraint primaryText_index 
        foreign key (primaryText) 
        references textwrapper (id)

    alter table workpart 
        add index parent_index (parent), 
        add constraint parent_index 
        foreign key (parent) 
        references workpart (id)

    alter table workpart 
        add index corpus_index (corpus), 
        add constraint corpus_index 
        foreign key (corpus) 
        references corpus (id)

    alter table workpart_children 
        add index FK63AEE95ABAAEFE25 (child_id), 
        add constraint FK63AEE95ABAAEFE25 
        foreign key (child_id) 
        references workpart (id)

    alter table workpart_children 
        add index FK63AEE95AD32B97D7 (parent_id), 
        add constraint FK63AEE95AD32B97D7 
        foreign key (parent_id) 
        references workpart (id)

    alter table workpart_translations 
        add index FK617DC9BD723B60CF (textWrapper_id), 
        add constraint FK617DC9BD723B60CF 
        foreign key (textWrapper_id) 
        references textwrapper (id)

    alter table workpart_translations 
        add index FK617DC9BDBC3FDFBD (workPart_id), 
        add constraint FK617DC9BDBC3FDFBD 
        foreign key (workPart_id) 
        references workpart (id)

    create index isActive_index on workset (isActive)

    create index title_index on workset (title)

    create index owner_index on workset (owner)

    create index isPublic_index on workset (isPublic)

    alter table workset_workparttags 
        add index workset_index (workSet), 
        add constraint workset_index 
        foreign key (workSet) 
        references workset (id)
