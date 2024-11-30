
    drop table if exists annotation;

    drop table if exists annotationcategory;

    drop table if exists author;

    drop table if exists authoredtextannotation;

    drop table if exists authors_works;

    drop table if exists bensonlemma;

    drop table if exists bensonlempos;

    drop table if exists bensonpos;

    drop table if exists corpus;

    drop table if exists corpus_tconviews;

    drop table if exists lemma;

    drop table if exists lemmacorpuscounts;

    drop table if exists lemmaposspellingcounts;

    drop table if exists lemmaworkcounts;

    drop table if exists lempos;

    drop table if exists line;

    drop table if exists metricalshape;

    drop table if exists phrase;

    drop table if exists phrase_wordtags;

    drop table if exists phraseset_phrases;

    drop table if exists phrasesetphrasecount;

    drop table if exists phrasesettotalwordformcount;

    drop table if exists pos;

    drop table if exists query;

    drop table if exists speaker;

    drop table if exists speech;

    drop table if exists speech_speakers;

    drop table if exists tconcategory;

    drop table if exists tconcategory_worktags;

    drop table if exists tconview;

    drop table if exists tconview_categories;

    drop table if exists tconview_worktags;

    drop table if exists textwrapper;

    drop table if exists totalwordformcount;

    drop table if exists usergroup;

    drop table if exists usergroup_admins;

    drop table if exists usergroup_members;

    drop table if exists usergrouppermission;

    drop table if exists word;

    drop table if exists wordclass;

    drop table if exists wordcount;

    drop table if exists wordpart;

    drop table if exists wordset;

    drop table if exists wordset_wordtags;

    drop table if exists wordset_workparttags;

    drop table if exists wordset_worktags;

    drop table if exists wordsettotalwordformcount;

    drop table if exists wordsetwordcount;

    drop table if exists workpart;

    drop table if exists workpart_children;

    drop table if exists workpart_translations;

    drop table if exists workset;

    drop table if exists workset_workparttags;

    create table annotation (
       type varchar(255) not null,
        id bigint not null auto_increment,
        target_end_index integer,
        target_end_offset integer,
        target_start_index integer,
        target_start_offset integer,
        category bigint,
        text bigint,
        workPart bigint,
        primary key (id)
    ) engine=MyISAM;

    create table annotationcategory (
       id bigint not null auto_increment,
        name varchar(255),
        primary key (id)
    ) engine=MyISAM;

    create table author (
       id bigint not null auto_increment,
        birthYear integer,
        deathYear integer,
        earliestWorkYear integer,
        latestWorkYear integer,
        name_charset tinyint,
        name_string varchar(255),
        originalName_charset tinyint,
        originalName_string varchar(255),
        primary key (id)
    ) engine=MyISAM;

    create table authoredtextannotation (
       id bigint not null auto_increment,
        annotates varchar(255),
        author varchar(255),
        body text,
        creationTime datetime,
        description varchar(255),
        isActive bit,
        isPublic bit,
        modificationTime datetime,
        owner varchar(255),
        query varchar(255),
        status varchar(255),
        target_end_index integer,
        target_end_offset integer,
        target_start_index integer,
        target_start_offset integer,
        title varchar(255),
        type varchar(255),
        webPageURL varchar(255),
        category bigint,
        primary key (id)
    ) engine=MyISAM;

    create table authors_works (
       work_id bigint not null,
        author_id bigint not null,
        primary key (author_id, work_id)
    ) engine=MyISAM;

    create table bensonlemma (
       id bigint not null,
        comment varchar(255),
        definition varchar(255),
        homonym integer,
        oedLemma varchar(255),
        word varchar(255),
        wordClass varchar(255),
        primary key (id)
    ) engine=MyISAM;

    create table bensonlempos (
       id bigint not null,
        lemma bigint,
        pos bigint,
        primary key (id)
    ) engine=MyISAM;

    create table bensonpos (
       id bigint not null,
        description varchar(255),
        tag varchar(255),
        primary key (id)
    ) engine=MyISAM;

    create table corpus (
       id bigint not null,
        charset tinyint,
        maxWordPathLength integer,
        numLines integer,
        numWords integer,
        numWorkParts integer,
        ordinal integer,
        posType tinyint,
        tag varchar(255),
        taggingData_flags bigint,
        title varchar(255),
        tranDescription longtext,
        translations varchar(255),
        primary key (id)
    ) engine=MyISAM;

    create table corpus_tconviews (
       corpus bigint not null,
        tconview bigint not null,
        corpus_index integer not null,
        primary key (corpus, corpus_index)
    ) engine=MyISAM;

    create table lemma (
       id bigint not null,
        homonym integer,
        spelling_charset tinyint,
        spelling_string varchar(255),
        tag_charset tinyint,
        tag_string varchar(255),
        tagInsensitive_charset tinyint,
        tagInsensitive_string varchar(255),
        wordClass bigint,
        primary key (id)
    ) engine=MyISAM;

    create table lemmacorpuscounts (
       id bigint not null auto_increment,
        colFreq integer,
        docFreq integer,
        majorClass varchar(255),
        numMajorClass integer,
        rank1 integer,
        rank2 integer,
        tag_charset tinyint,
        tag_string varchar(255),
        corpus bigint,
        lemma bigint,
        wordClass bigint,
        primary key (id)
    ) engine=MyISAM;

    create table lemmaposspellingcounts (
       id bigint not null auto_increment,
        freq integer,
        freqFirstWordPart integer,
        kind tinyint,
        spelling_charset tinyint,
        spelling_string varchar(255),
        corpus bigint,
        lemma bigint,
        pos bigint,
        work bigint,
        workPart bigint,
        primary key (id)
    ) engine=MyISAM;

    create table lemmaworkcounts (
       id bigint not null auto_increment,
        numMajorClass integer,
        rank1 integer,
        rank2 integer,
        termFreq integer,
        lemma bigint,
        work bigint,
        primary key (id)
    ) engine=MyISAM;

    create table lempos (
       id bigint not null,
        standardSpelling_charset tinyint,
        standardSpelling_string varchar(255),
        lemma bigint,
        pos bigint,
        primary key (id)
    ) engine=MyISAM;

    create table line (
       id bigint not null,
        label varchar(255),
        location_end_index integer,
        location_end_offset integer,
        location_start_index integer,
        location_start_offset integer,
        number integer,
        stanzaLabel varchar(255),
        tag varchar(255),
        workPart bigint,
        primary key (id)
    ) engine=MyISAM;

    create table metricalshape (
       id bigint not null auto_increment,
        metricalShape varchar(255),
        primary key (id)
    ) engine=MyISAM;

    create table phrase (
       id bigint not null auto_increment,
        tagsHashCode integer,
        workTag varchar(32),
        primary key (id)
    ) engine=MyISAM;

    create table phrase_wordtags (
       phraseId bigint not null,
        wordTag varchar(32),
        word_index integer not null,
        primary key (phraseId, word_index)
    ) engine=MyISAM;

    create table phraseset_phrases (
       phraseSetId bigint primary key not null,
        phraseId bigint primary key not null
    ) engine=MyISAM;

    create table phrasesetphrasecount (
       id bigint not null auto_increment,
        phraseCount integer,
        phraseText_charset tinyint,
        phraseText_string varchar(255),
        wordForm integer,
        workPartTag varchar(32),
        phraseSet bigint,
        primary key (id)
    ) engine=MyISAM;

    create table phrasesettotalwordformcount (
       id bigint not null auto_increment,
        wordForm integer,
        wordFormCount integer,
        workPartTag varchar(32),
        phraseSet bigint,
        primary key (id)
    ) engine=MyISAM;

    create table pos (
       id bigint not null auto_increment,
        degree varchar(255),
        description varchar(255),
        gender varchar(255),
        language varchar(255),
        mood varchar(255),
        negative varchar(255),
        number varchar(255),
        person varchar(255),
        syntax varchar(255),
        tag varchar(255),
        tense varchar(255),
        voice varchar(255),
        xcase varchar(255),
        wordClass bigint,
        primary key (id)
    ) engine=MyISAM;

    create table query (
       id bigint not null auto_increment,
        creationTime datetime,
        description text,
        isActive bit,
        isPublic bit,
        modificationTime datetime,
        owner varchar(255),
        queryText varchar(255),
        queryType integer,
        title varchar(255),
        webPageURL varchar(255),
        primary key (id)
    ) engine=MyISAM;

    create table speaker (
       id bigint not null auto_increment,
        description varchar(255),
        gender_gender tinyint,
        mortality_mortality tinyint,
        name varchar(255),
        originalName_charset tinyint,
        originalName_string varchar(255),
        tag varchar(255),
        work bigint,
        primary key (id)
    ) engine=MyISAM;

    create table speech (
       id bigint not null,
        gender_gender tinyint,
        mortality_mortality tinyint,
        workPart bigint,
        primary key (id)
    ) engine=MyISAM;

    create table speech_speakers (
       speech_id bigint not null,
        speaker_id bigint not null,
        primary key (speech_id, speaker_id)
    ) engine=MyISAM;

    create table tconcategory (
       id bigint not null,
        title varchar(255),
        primary key (id)
    ) engine=MyISAM;

    create table tconcategory_worktags (
       tconcategory bigint not null,
        worktag varchar(32),
        tconcategory_index integer not null,
        primary key (tconcategory, tconcategory_index)
    ) engine=MyISAM;

    create table tconview (
       id bigint not null,
        radioButtonLabel varchar(255),
        viewType integer,
        primary key (id)
    ) engine=MyISAM;

    create table tconview_categories (
       tconview bigint not null,
        category bigint not null,
        tconview_index integer not null,
        primary key (tconview, tconview_index)
    ) engine=MyISAM;

    create table tconview_worktags (
       tconview bigint not null,
        worktag varchar(32),
        tconview_index integer not null,
        primary key (tconview, tconview_index)
    ) engine=MyISAM;

    create table textwrapper (
       id bigint not null auto_increment,
        text mediumblob,
        primary key (id)
    ) engine=MyISAM;

    create table totalwordformcount (
       id bigint not null auto_increment,
        wordForm integer,
        wordFormCount integer,
        work bigint,
        workPart bigint,
        primary key (id)
    ) engine=MyISAM;

    create table usergroup (
       id bigint not null auto_increment,
        creationTime datetime,
        description varchar(255),
        isActive bit,
        isPublic bit,
        modificationTime datetime,
        owner varchar(255),
        query varchar(255),
        title varchar(255),
        webPageURL varchar(255),
        primary key (id)
    ) engine=MyISAM;

    create table usergroup_admins (
       usergroup bigint not null,
        admin varchar(32)
    ) engine=MyISAM;

    create table usergroup_members (
       usergroup bigint not null,
        member varchar(32)
    ) engine=MyISAM;

    create table usergrouppermission (
       id bigint not null auto_increment,
        creationTime datetime,
        description varchar(255),
        isActive bit,
        isPublic bit,
        modificationTime datetime,
        owner varchar(255),
        permission varchar(255),
        query varchar(255),
        title varchar(255),
        webPageURL varchar(255),
        authoredTextAnnotation bigint,
        userGroup bigint,
        primary key (id)
    ) engine=MyISAM;

    create table word (
       id bigint not null,
        colocationOrdinal bigint,
        location_end_index integer,
        location_end_offset integer,
        location_start_index integer,
        location_start_offset integer,
        metricalShape_metricalShape varchar(255),
        path varchar(255),
        prosodic_prosodic tinyint,
        puncAfter varchar(255),
        puncBefore varchar(255),
        spelling_charset tinyint,
        spelling_string varchar(255),
        spellingInsensitive_charset tinyint,
        spellingInsensitive_string varchar(255),
        tag varchar(255),
        workOrdinal integer,
        workTag varchar(255),
        line bigint,
        next bigint,
        prev bigint,
        speech bigint,
        work bigint,
        workPart bigint,
        primary key (id)
    ) engine=MyISAM;

    create table wordclass (
       id bigint not null auto_increment,
        description varchar(255),
        majorWordClass_majorWordClass varchar(255),
        tag varchar(255),
        primary key (id)
    ) engine=MyISAM;

    create table wordcount (
       id bigint not null auto_increment,
        word_charset tinyint,
        word_string varchar(255),
        wordCount integer,
        wordForm integer,
        work bigint,
        workPart bigint,
        primary key (id)
    ) engine=MyISAM;

    create table wordpart (
       id bigint not null,
        partIndex integer,
        tag varchar(255),
        bensonLemPos bigint,
        lemPos bigint,
        word bigint,
        workPart bigint,
        primary key (id)
    ) engine=MyISAM;

    create table wordset (
       is_wordset integer not null,
        id bigint not null auto_increment,
        creationTime datetime,
        description text,
        isActive bit,
        isPublic bit,
        modificationTime datetime,
        owner varchar(255),
        query varchar(255),
        title varchar(255),
        webPageURL varchar(255),
        sumPhraseLengths double precision,
        primary key (id)
    ) engine=MyISAM;

    create table wordset_wordtags (
       wordSet bigint not null,
        wordTag varchar(32)
    ) engine=MyISAM;

    create table wordset_workparttags (
       wordSet bigint not null,
        tag varchar(32)
    ) engine=MyISAM;

    create table wordset_worktags (
       wordSet bigint not null,
        tag varchar(32)
    ) engine=MyISAM;

    create table wordsettotalwordformcount (
       id bigint not null auto_increment,
        wordForm integer,
        wordFormCount integer,
        workPartTag varchar(32),
        wordSet bigint,
        primary key (id)
    ) engine=MyISAM;

    create table wordsetwordcount (
       id bigint not null auto_increment,
        word_charset tinyint,
        word_string varchar(255),
        wordCount integer,
        wordForm integer,
        workPartTag varchar(32),
        wordSet bigint,
        primary key (id)
    ) engine=MyISAM;

    create table workpart (
       is_work integer not null,
        id bigint not null,
        fullTitle varchar(255),
        hasChildren bit,
        hasStanzaNumbers bit,
        numLines integer,
        numWords integer,
        pathTag varchar(255),
        shortTitle varchar(255),
        tag varchar(255),
        taggingData_flags bigint,
        workOrdinal integer,
        pubDate_endYear integer,
        pubDate_startYear integer,
        parent bigint,
        primaryText bigint,
        work bigint,
        corpus bigint,
        primary key (id)
    ) engine=MyISAM;

    create table workpart_children (
       parent_id bigint not null,
        child_id bigint not null,
        child_index integer not null,
        primary key (parent_id, child_index)
    ) engine=MyISAM;

    create table workpart_translations (
       workPart_id bigint not null,
        textWrapper_id bigint not null,
        translation_name varchar(255) not null,
        primary key (workPart_id, translation_name)
    ) engine=MyISAM;

    create table workset (
       id bigint not null auto_increment,
        creationTime datetime,
        description text,
        isActive bit,
        isPublic bit,
        modificationTime datetime,
        owner varchar(255),
        query varchar(255),
        title varchar(255),
        webPageURL varchar(255),
        primary key (id)
    ) engine=MyISAM;

    create table workset_workparttags (
       workSet bigint not null,
        tag varchar(32)
    ) engine=MyISAM;
create index birthYear_index on author (birthYear);
create index deathYear_index on author (deathYear);
create index earliestWorkYear_index on author (earliestWorkYear);
create index latestWorkYear_index on author (latestWorkYear);
create index authoredtextannotation_IsPublic_index on authoredtextannotation (isPublic);
create index authoredtextannotation_IsActive_index on authoredtextannotation (isActive);
create index authoredtextannotation_owner_index on authoredtextannotation (owner);
create index tag_index on corpus (tag);

    alter table corpus_tconviews 
       add constraint UK_alfj49a8qp2nm2v4gulj2fwcb unique (tconview);
create index tag_index on line (tag);
create index wordForm_index on phrasesetphrasecount (wordForm);
create index workPartTag_index on phrasesetphrasecount (workPartTag);
create index wordForm_index on phrasesettotalwordformcount (wordForm);
create index workPartTag_index on phrasesettotalwordformcount (workPartTag);
create index title_index on query (title);
create index owner_index on query (owner);
create index isPublic_index on query (isPublic);
create index isActive_index on query (isActive);
create index queryType_index on query (queryType);
create index name_index on speaker (name);
create index wordForm_index on totalwordformcount (wordForm);
create index usergroup_owner_index on usergroup (owner);
create index usergroup_isPublic_index on usergroup (isPublic);
create index usergroup_isActive_index on usergroup (isActive);
create index permission_isPublic_index on usergrouppermission (isPublic);
create index permission_isActive_index on usergrouppermission (isActive);
create index tag_index on word (tag);
create index colocationOrdinal_index on word (colocationOrdinal);
create index wordForm_index on wordcount (wordForm);
create index tag_index on wordpart (tag);
create index wordset_title_index on wordset (title);
create index wordset_owner_index on wordset (owner);
create index wordset_isPublic_index on wordset (isPublic);
create index wordset_isActive_index on wordset (isActive);
create index wordForm_index on wordsettotalwordformcount (wordForm);
create index workPartTag_index on wordsettotalwordformcount (workPartTag);
create index wordcount_wordForm_index on wordsetwordcount (wordForm);
create index workPartTag_index on wordsetwordcount (workPartTag);
create index tag_index on workpart (tag);
create index title_index on workset (title);
create index owner_index on workset (owner);
create index isPublic_index on workset (isPublic);
create index isActive_index on workset (isActive);

    alter table annotation 
       add constraint category_index 
       foreign key (category) 
       references annotationcategory (id);

    alter table annotation 
       add constraint text_index 
       foreign key (text) 
       references textwrapper (id);

    alter table annotation 
       add constraint workPart_index 
       foreign key (workPart) 
       references workpart (id);

    alter table authoredtextannotation 
       add constraint category_index 
       foreign key (category) 
       references annotationcategory (id);

    alter table authors_works 
       add constraint author_id_index 
       foreign key (author_id) 
       references author (id);

    alter table authors_works 
       add constraint work_id_index 
       foreign key (work_id) 
       references workpart (id);

    alter table bensonlempos 
       add constraint lemma_index 
       foreign key (lemma) 
       references bensonlemma (id);

    alter table bensonlempos 
       add constraint pos_index 
       foreign key (pos) 
       references bensonpos (id);

    alter table corpus_tconviews 
       add constraint FK2rn048lib8wsm97ydks3cnxo9 
       foreign key (tconview) 
       references tconview (id);

    alter table corpus_tconviews 
       add constraint FK2m0dxguy5fllqitg3pv1wt2ma 
       foreign key (corpus) 
       references corpus (id);

    alter table lemma 
       add constraint wordClass_index 
       foreign key (wordClass) 
       references wordclass (id);

    alter table lemmacorpuscounts 
       add constraint corpus_index 
       foreign key (corpus) 
       references corpus (id);

    alter table lemmacorpuscounts 
       add constraint lemma_index 
       foreign key (lemma) 
       references lemma (id);

    alter table lemmacorpuscounts 
       add constraint wordClass_index 
       foreign key (wordClass) 
       references wordclass (id);

    alter table lemmaposspellingcounts 
       add constraint corpus_index 
       foreign key (corpus) 
       references corpus (id);

    alter table lemmaposspellingcounts 
       add constraint lemma_index 
       foreign key (lemma) 
       references lemma (id);

    alter table lemmaposspellingcounts 
       add constraint pos_index 
       foreign key (pos) 
       references pos (id);

    alter table lemmaposspellingcounts 
       add constraint work_index 
       foreign key (work) 
       references workpart (id);

    alter table lemmaposspellingcounts 
       add constraint workPart_index 
       foreign key (workPart) 
       references workpart (id);

    alter table lemmaworkcounts 
       add constraint lemma_index 
       foreign key (lemma) 
       references lemma (id);

    alter table lemmaworkcounts 
       add constraint work_index 
       foreign key (work) 
       references workpart (id);

    alter table lempos 
       add constraint lemma_index 
       foreign key (lemma) 
       references lemma (id);

    alter table lempos 
       add constraint pos_index 
       foreign key (pos) 
       references pos (id);

    alter table line 
       add constraint workPart_index 
       foreign key (workPart) 
       references workpart (id);

    alter table phrase_wordtags 
       add constraint phraseid_index 
       foreign key (phraseId) 
       references phrase (id);

    alter table phraseset_phrases 
       add constraint phraseid_index 
       foreign key (phraseId) 
       references phrase (id);

    alter table phraseset_phrases 
       add constraint FKdx75p4qbvxfcwmwbicrce8och 
       foreign key (phraseSetId) 
       references wordset (id);

    alter table phrasesetphrasecount 
       add constraint phraseSet_index 
       foreign key (phraseSet) 
       references wordset (id);

    alter table phrasesettotalwordformcount 
       add constraint phrase_index 
       foreign key (phraseSet) 
       references wordset (id);

    alter table pos 
       add constraint wordClass_index 
       foreign key (wordClass) 
       references wordclass (id);

    alter table speaker 
       add constraint work_index 
       foreign key (work) 
       references workpart (id);

    alter table speech 
       add constraint workPart_index 
       foreign key (workPart) 
       references workpart (id);

    alter table speech_speakers 
       add constraint FKf8s21bim9u6ebhs9rog1hy9fv 
       foreign key (speaker_id) 
       references speaker (id);

    alter table speech_speakers 
       add constraint FKm57xn861tj231h8ocey14rjob 
       foreign key (speech_id) 
       references speech (id);

    alter table tconcategory_worktags 
       add constraint FK88th587ad4v2jpxvs8r1suh77 
       foreign key (tconcategory) 
       references tconcategory (id);

    alter table tconview_categories 
       add constraint FKt1fgucsq0evvselevhbw5yml6 
       foreign key (category) 
       references tconcategory (id);

    alter table tconview_categories 
       add constraint FK1rqblm78psiubkgyqa0mbjdpb 
       foreign key (tconview) 
       references tconview (id);

    alter table tconview_worktags 
       add constraint FK3xbf309tehd0lu7r6n3w2ucyf 
       foreign key (tconview) 
       references tconview (id);

    alter table totalwordformcount 
       add constraint work_index 
       foreign key (work) 
       references workpart (id);

    alter table totalwordformcount 
       add constraint workPart_index 
       foreign key (workPart) 
       references workpart (id);

    alter table usergroup_admins 
       add constraint usergroup_admins_index 
       foreign key (usergroup) 
       references usergroup (id);

    alter table usergroup_members 
       add constraint usergroup_members_index 
       foreign key (usergroup) 
       references usergroup (id);

    alter table usergrouppermission 
       add constraint FK2oq7puddk10h1n17dwgn7te3x 
       foreign key (authoredTextAnnotation) 
       references authoredtextannotation (id);

    alter table usergrouppermission 
       add constraint FKskmya7xmwhj6xfqp6dquxb71n 
       foreign key (userGroup) 
       references usergroup (id);

    alter table word 
       add constraint line_index 
       foreign key (line) 
       references line (id);

    alter table word 
       add constraint next_index 
       foreign key (next) 
       references word (id);

    alter table word 
       add constraint prev_index 
       foreign key (prev) 
       references word (id);

    alter table word 
       add constraint speech_index 
       foreign key (speech) 
       references speech (id);

    alter table word 
       add constraint work_index 
       foreign key (work) 
       references workpart (id);

    alter table word 
       add constraint workPart_index 
       foreign key (workPart) 
       references workpart (id);

    alter table wordcount 
       add constraint work_index 
       foreign key (work) 
       references workpart (id);

    alter table wordcount 
       add constraint workPart_index 
       foreign key (workPart) 
       references workpart (id);

    alter table wordpart 
       add constraint bensonLemPos_index 
       foreign key (bensonLemPos) 
       references bensonlempos (id);

    alter table wordpart 
       add constraint lemPos_index 
       foreign key (lemPos) 
       references lempos (id);

    alter table wordpart 
       add constraint partIndex 
       foreign key (word) 
       references word (id);

    alter table wordpart 
       add constraint workPart_index 
       foreign key (workPart) 
       references workpart (id);

    alter table wordset_wordtags 
       add constraint wordset_wordTags_index 
       foreign key (wordSet) 
       references wordset (id);

    alter table wordset_workparttags 
       add constraint wordset_workPartTags_index 
       foreign key (wordSet) 
       references wordset (id);

    alter table wordset_worktags 
       add constraint wordset_workTags_index 
       foreign key (wordSet) 
       references wordset (id);

    alter table wordsettotalwordformcount 
       add constraint word_index 
       foreign key (wordSet) 
       references wordset (id);

    alter table wordsetwordcount 
       add constraint wordSet_index 
       foreign key (wordSet) 
       references wordset (id);

    alter table workpart 
       add constraint parent_index 
       foreign key (parent) 
       references workpart (id);

    alter table workpart 
       add constraint primaryText_index 
       foreign key (primaryText) 
       references textwrapper (id);

    alter table workpart 
       add constraint work_index 
       foreign key (work) 
       references workpart (id);

    alter table workpart 
       add constraint corpus_index 
       foreign key (corpus) 
       references corpus (id);

    alter table workpart_children 
       add constraint FK7jfidkif6sad8ht65tbhty6ex 
       foreign key (child_id) 
       references workpart (id);

    alter table workpart_children 
       add constraint FK6nday6ru7a2yu8wlyt84g9boi 
       foreign key (parent_id) 
       references workpart (id);

    alter table workpart_translations 
       add constraint FKhgpflk008ns4qie3s31gp1srp 
       foreign key (textWrapper_id) 
       references textwrapper (id);

    alter table workpart_translations 
       add constraint FKpeutxn4po1xf3th5cpmee3aki 
       foreign key (workPart_id) 
       references workpart (id);

    alter table workset_workparttags 
       add constraint workset_index 
       foreign key (workSet) 
       references workset (id);
