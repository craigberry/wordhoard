package edu.northwestern.at.wordhoard.model;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.*;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import edu.northwestern.at.wordhoard.model.annotations.*;
import edu.northwestern.at.wordhoard.model.bibtool.*;
import edu.northwestern.at.wordhoard.model.counts.*;
import edu.northwestern.at.wordhoard.model.morphology.*;
import edu.northwestern.at.wordhoard.model.querytool.*;
import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.wordhoard.model.speakers.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.db.*;
import edu.northwestern.at.utils.db.hibernate.*;

import edu.northwestern.at.wordhoard.swing.text.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.utils.xml.*;
import edu.northwestern.at.wordhoard.server.*;

/**	WordHoard persistence manager.
 *
 *	<p>The "getxxx" and "findxxx" methods fetch objects and collections of
 *	objects by various criteria. They are implemented using Hibernate queries,
 *	which in turn become SQL select statements.</p>
 */

public class PersistenceManager extends HibernatePersistenceManager {

	/**	Creates a new persistence manager.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public PersistenceManager ()
		throws PersistenceException
	{
		super();
	}

	/**	Initializes Hibernate for WordHoard using default properties file.
	 *
	 *	@param	cache2			True to enable second-level cache.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public static void init (boolean cache2)
		throws PersistenceException
	{
		init(PersistentClasses.persistentClasses, cache2);
	}

	/**	Initializes Hibernate for WordHoard using specified parameters.
	 *
	 *	@param	url				URL for MySQL database, or null to use the
	 *							"hibernate.properties" file for setting
	 *							the Hibernate parameters.
	 *
	 *	@param	username		Username for MySQL database access. Ignored
	 *							if url = null.
	 *
	 *	@param	password		Password for MySQL database access. Ignored
	 *							if url = null.
	 *
	 *	@param	cache2			True to enable second-level cache.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public static void init (String url, String username, String password,
		boolean cache2)
		throws PersistenceException
	{
		init(url, username, password, null, PersistentClasses.persistentClasses,
			cache2);
	}

	/**	Gets a persistence manager for the current thread.
	 *
	 *	@return		The current persistence manager.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public static PersistenceManager getPM()
		throws PersistenceException
	{
		PersistenceManager pm	= null;

		try
		{
			Object opm	= threadPM.get();

			if ( ( opm == null ) || !( opm instanceof PersistenceManager ) )
			{
				pm	= new PersistenceManager();

				threadPM.set( pm );
			}
			else
			{
				pm	= (PersistenceManager)opm;
			}
		}
		catch ( Exception e )
		{
			throw new PersistenceException( e );
		}

		return pm;
	}

	/**	Close persistence manager for the current thread.
	 */

	public static void closePM()
	{
		try
		{
			Object opm	= threadPM.get();

			if ( ( opm != null ) && ( opm instanceof PersistenceManager ) )
			{
				PersistenceManager pm	= (PersistenceManager)opm;
				pm.close();
			}
		}
		catch ( Exception e )
		{
		}
	}

	/**	Clones an object.
	 *
	 *	<p>Loads a new copy of a persistent object in this persistence
	 *	manager.
	 *
	 *	@param	obj		Persistent object.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 *
	 *	@return	Clone of persistent object.
	 */

	public Object clone (PersistentObject obj)
		throws PersistenceException
	{
		return load(obj.getClass(), obj.getId());
	}

	/**	Gets all the corpora.
	 *
	 *	<p>In addition to loading and returning all the corpora, all the works,  
	 *	table of contents views, authors, and work child parts are preloaded.
	 *
	 *	@return			A collection of all the corpora.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public Collection getAllCorpora ()
		throws PersistenceException
	{
		return query("select distinct corpus " +
			"from Corpus corpus " +
			"left join fetch corpus.works as work " +
			"left join fetch corpus.tconViews as tconView " +
			"left join fetch work.authors as author");
	}

	/**	Gets all the work parts.
	 *
	 *	@return			A collection of all the work parts.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public Collection getAllWorkParts ()
		throws PersistenceException
	{
		return query("from WorkPart");
	}
	
	/**	Gets all the work parts for a work.
	 *
	 *	@param	work	Work.
	 *
	 *	@return			A collection of all the work parts for the work.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */
	 
	public Collection getWorkParts (Work work)
		throws PersistenceException
	{
		return query("select distinct workPart " +
			"from WorkPart workPart " +
			"left join fetch workPart.children as child " +
			"where workPart.work = :work ",
			new String[]{"work"},
			new Object[]{work});
	}

	/**	Gets all the works.
	 *
	 *	@return			A collection of all the works.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public Collection getAllWorks ()
		throws PersistenceException
	{
		return query("from Work");
	}

	/**	Gets all the authors.
	 *
	 *	@return			A collection of all the authors.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public Collection getAllAuthors ()
		throws PersistenceException
	{
		return query("from Author");
	}

	/**	Gets all the authors.
	 *
	 *	@param	corpus		Corpus, or null.
	 *
	 *	@return				A collection of all the authors, in increasing
	 *						case-insensitive alphabetical order by name.
	 *						If the corpus argument is not null, the collection
	 *						is limited to authors in the spcecified corpus.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public Collection getAllAuthors (Corpus corpus)
		throws PersistenceException
	{
		Collection result;

		if (corpus == null) {
			result = query(	"from Author order by name.string" );
		} else {
			result = query(
				"select distinct author " +
				"from Author author " +
				"inner join author.works as work " +
				"where work.corpus = :corpus " +
				"order by author.name.string",
			new String[]{"corpus"},
			new Object[]{corpus});
		}

		return result;
	}

	/**	Gets all the parts of speech.
	 *
	 *	@return			A collection of all the parts of speech.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public Collection getAllPos ()
		throws PersistenceException
	{
		return query("from Pos order by tag");
	}

	/**	Gets all the word classes.
	 *
	 *	@return			A collection of all the word classes.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public Collection getAllWordClasses ()
		throws PersistenceException
	{
		return query( "from WordClass order by tag" );
	}

	/**	Gets all the lemmas.
	 *
	 *	@return			A collection of all the lemmas.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public Collection getAllLemmas ()
		throws PersistenceException
	{
		return query( "from Lemma" );
	}

	/**	Gets all the LemPos objects.
	 *
	 *	@return			A collection of all the LemPos objects.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public Collection getAllLemPos ()
		throws PersistenceException
	{
		return query( "from LemPos" );
	}

	/**	Gets all the major word classes.
	 *
	 *	@return			A collection of all the major word class strings,
	 *					in increasing alphabetical order.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public Collection getAllMajorWordClasses ()
		throws PersistenceException
	{
		return query(
			"select distinct wordClass.majorWordClass " +
			"from WordClass wordClass " +
			"order by wordClass.majorWordClass.majorWordClass" );
	}

	/**	Gets all the metrical shapes.
	 *
	 *	@return			A collection of all the metrical shapes,
	 *					in increasing alphabetical order.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public Collection getAllMetricalShapes ()
		throws PersistenceException
	{
		return query( "from MetricalShape order by MetricalShape" );
	}

	/**	Gets a corpus by tag.
	 *
	 *	@param	tag		Corpus tag.
	 *
	 *	@return			The corpus, or null if none found.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public Corpus getCorpusByTag (String tag)
		throws PersistenceException
	{
		Collection qList = query(
			"from Corpus corpus where corpus.tag = :tag",
			new String[]{"tag"},
			new Object[]{tag});
		Iterator it = qList.iterator();
		return it.hasNext() ? (Corpus)it.next() : null;
	}

	/**	Gets a word by tag.
	 *
	 *	@param	tag			tag.
	 *
	 *	@return				The word with the specified tag,
	 *						or null if none found.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public Word getWordByTag (String tag)
		throws PersistenceException
	{
		Collection qList = query(
			"select word " +
			"from Word word " +
			"where word.tag = :tag",
			new String[]{"tag"},
			new Object[]{tag});
		Iterator it = qList.iterator();
		return it.hasNext() ? (Word)it.next() : null;
	}

	/**	Gets a work part by tag.
	 *
	 *	@param	tag			tag.
	 *
	 *	@return				The work part with the specified tag,
	 *						or null if none found.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public WorkPart getWorkPartByTag (String tag)
		throws PersistenceException
	{
		Collection qList = query(
			"select workpart " +
			"from WorkPart workpart " +
			"where workpart.tag = :tag",
			new String[]{"tag"},
			new Object[]{tag});
		Iterator it = qList.iterator();
		return it.hasNext() ? (WorkPart)it.next() : null;
	}

	/**	Gets work parts by their tags.
	 *
	 *	@param	tags		Collection of work part tags.
	 *
	 *	@return				Array of work parts with the specified tags.
	 *						Empty if none found.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public WorkPart[] getWorkPartsByTag (Collection tags)
		throws PersistenceException
	{
		Collection qList = query(
			"select workpart " +
			"from WorkPart workpart " +
			"where workpart.tag in (:tags)",
			new String[]{"tags"},
			new Object[]{tags});
		WorkPart[] workParts = new WorkPart[0];
		if (qList != null) {
			workParts = (WorkPart[])qList.toArray(new WorkPart[]{});
		}
		return workParts;
	}

	/**	Gets a lemma by tag.
	 *
	 *	@param	tag			tag.
	 *
	 *	@return				The lemma with the specified tag,
	 *						or null if none found.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public Lemma getLemmaByTag (String tag)
		throws PersistenceException
	{
		String tagInsensitive = CharsetUtils.translateToInsensitive(tag);

		Collection qList = query(
			"from Lemma lemma " +
			"where lemma.tagInsensitive.string = :tagInsensitive",
			new String[]{"tagInsensitive"},
			new Object[]{tagInsensitive});
		Iterator it = qList.iterator();
		return it.hasNext() ? (Lemma)it.next() : null;
	}

	/**	Gets a line by tag.
	 *
	 *	@param	tag			tag.
	 *
	 *	@return				The line with the specified tag,
	 *						or null if none found.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public Line getLineByTag (String tag)
		throws PersistenceException
	{
		Collection qList = query(
			"from Line line " +
			"where tag = :tag",
			new String[]{"tag"},
			new Object[]{tag});
		Iterator it = qList.iterator();
		return it.hasNext() ? (Line)it.next() : null;
	}

	/**	Gets an author by name.
	 *
	 *	@param	name		English name.
	 *
	 *	@return				The author with the specified name, or null
	 *						if none found.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public Author getAuthorByName (String name)
		throws PersistenceException
	{
		Collection qList = query(
				"from Author author " +
				"where author.name.string = :name",
			new String[]{"name"},
			new Object[]{name});
		Iterator it = qList.iterator();
		return it.hasNext() ? (Author)it.next() : null;
	}

	/**	Gets an annotation category by name.
	 *
	 *	@param	name		Catgegory name.
	 *
	 *	@return				The annotation category with the specified
	 *						name, or null if none found.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public AnnotationCategory getAnnotationCategoryByName (String name)
		throws PersistenceException
	{
		Collection qList = query(
			"from AnnotationCategory category " +
			"where category.name = :name",
			new String[]{"name"},
			new Object[]{name});
		Iterator it = qList.iterator();
		return it.hasNext() ? (AnnotationCategory)it.next() : null;
	}

	/**	Gets the words in a work part.
	 *
	 *	@param	workPart		Work part.
	 *
	 *	@return			All of the {@link
	 *					edu.northwestern.at.wordhoard.model.Word
	 *					words} for this work part.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	 public Collection getWordsInWorkPart (WorkPart workPart)
		throws PersistenceException
	{
		return query(
			"from Word word where word.workPart = :workPart",
			new String[]{"workPart"},
			new Object[]{ workPart});
	 }

	/**	Gets the words in a work part with preloading.
	 *
	 *	<p>All related morphology and speech objects are preloaded.
	 *
	 *	@param	workPart		Work part.
	 *
	 *	@return			All of the {@link
	 *					edu.northwestern.at.wordhoard.model.Word
	 *					words} for this work part.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	 public Collection getWordsInWorkPartWithPreloading (WorkPart workPart)
		throws PersistenceException
	{
		Collection words = query(
			"from Word word " +
			"left join fetch word.wordParts as wordPart " +
			"left join fetch wordPart.lemPos as lemPos " +
			"left join fetch lemPos.lemma as lemma " +
			"left join fetch lemPos.pos " +
			"left join fetch lemma.wordClass " +
			"left join fetch word.speech " +
			"where word.workPart = :workPart",
			new String[]{"workPart"},
			new Object[]{ workPart});
		// Remove duplicate words.
		return new HashSet(words);
	 }

	/**	Gets the lines in a work part.
	 *
	 *	@param	workPart		Work part.
	 *
	 *	@return			All of the {@link
	 *					edu.northwestern.at.wordhoard.model.Line
	 *					lines} in the work part.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	 public Collection getLinesInWorkPart (WorkPart workPart)
		throws PersistenceException
	{
		return query(
			"from Line line where line.workPart = :workPart",
			new String[]{"workPart"},
			new Object[]{ workPart});
	 }

	/**	Gets the lines in a work.
	 *
	 *	@param	work		Work.
	 *
	 *	@return			All of the {@link
	 *					edu.northwestern.at.wordhoard.model.Line
	 *					lines} in the work.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	 public Collection getLinesInWork (Work work)
		throws PersistenceException
	{
		return query(
			"from Line line where line.workPart.work = :work",
			new String[]{"work"},
			new Object[]{ work});
	 }

	/**	Gets the words in a work.
	 *
	 *	@param	work		Work.
	 *
	 *	@return			All of the {@link
	 *					edu.northwestern.at.wordhoard.model.Word
	 *					words} in the work.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	 public Collection getWordsInWork (Work work)
		throws PersistenceException
	{
		return query(
			"from Word word where word.work = :work",
			new String[]{"work"},
			new Object[]{ work});
	 }

	/**	Gets the annotations for a work part.
	 *
	 *	@param	workPart		Work part.
	 *
	 *	@return		All of the {@link
	 *				edu.northwestern.at.wordhoard.model.annotations.TextAnnotation
	 *				text annotations} for the work part.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	 public Collection getAnnotationsForWorkPart (WorkPart workPart)
		throws PersistenceException
	{
		return query(
			"from TextAnnotation annotation " +
			"where annotation.workPart = :workPart",
			new String[]{"workPart"},
			new Object[]{ workPart});
	 }



	/**	Gets the remote annotations for a work part.
	 *
	 *	<p>(Defunct.)
	 *
	 *	@param	workPart		Work part.
	 *
	 *	@return		All of the {@link
	 *				edu.northwestern.at.wordhoard.model.annotations.RemoteAnnotation
	 *				remote annotations} for the work part.
	 *
	 */

/*
	 public Collection getRemoteAnnotationsForWorkPart (WorkPart workPart) throws PersistenceException
	{

		ArrayList annotationList = new ArrayList();
		if((WordHoardSettings.getUserID()==null) ||
				(WordHoardSettings.getAnnotationWriteServerURL()==null) ||
				(WordHoardSettings.getAnnotationReadServerURL()==null)) { return annotationList; }

		String baseURL = WordHoardSettings.getAnnotationReadServerURL();

		try {
			String urlstring = baseURL + workPart.getTag();
			URL url = new URL(urlstring);
			Document document = DOMUtils.parse(url);

			Element mainEl = DOMUtils.getDescendant(document,"annotations");
			ArrayList annotList = DOMUtils.getChildren(mainEl, "annotation");

			for (Iterator it = annotList.iterator(); it.hasNext(); ) {
				Element annotEl = (Element)it.next();
				RemoteAnnotation annotation = new RemoteAnnotation(annotEl);
				annotation.setWorkPart(workPart);
//				annotation.setCategory(annotationCategory);
				annotation.setCategory(null);
				annotationList.add(annotation);
			}
		} catch (Exception e) {
			System.out.println("Error creating remote annotation");
		}

		return annotationList;
	}
*/

	/**	Gets lemma/work counts.
	 *
	 *	@param	lemma		Lemma.
	 *
	 *	@param	work		Work.
	 *
	 *	@return		Lemma/work counts, or null if there are no counts
	 *				for this lemma and work.
	 *
	 *	@throws PersistenceException	error in persistence layer.
	 */

	public LemmaWorkCounts getLemmaWorkCounts (Lemma lemma, Work work)
		throws PersistenceException
	{
		Collection qList = query(
			"from LemmaWorkCounts counts " +
			"where counts.lemma = :lemma " +
			"and counts.work = :work",
			new String[]{"lemma", "work"},
			new Object[]{lemma, work});
		Iterator it = qList.iterator();
		return it.hasNext() ? (LemmaWorkCounts)it.next() : null;
	}

	/**	Gets lemma/corpus counts.
	 *
	 *	@param	lemma		Lemma.
	 *
	 *	@param	corpus		Corpus.
	 *
	 *	@return		Lemma/corpus counts, or null if there are no counts
	 *				for this lemma and corpus.
	 *
	 *	@throws PersistenceException	error in persistence layer.
	 */

	public LemmaCorpusCounts getLemmaCorpusCounts (Lemma lemma, Corpus corpus)
		throws PersistenceException
	{
		Collection qList = query(
			"from LemmaCorpusCounts counts " +
			"where counts.lemma = :lemma " +
			"and counts.corpus = :corpus",
			new String[]{"lemma", "corpus"},
			new Object[]{lemma, corpus});
		Iterator it = qList.iterator();
		return it.hasNext() ? (LemmaCorpusCounts)it.next() : null;
	}

	/**	Gets the lexicon for a corpus.
	 *
	 *	@param	corpus		Corpus.
	 *
	 *	@return				The lexicon: a collection of all the lemma/corpus
	 *						counts objects for the corpus, in no particular
	 *						order.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public Collection getLexicon (Corpus corpus)
		throws PersistenceException
	{
		return query(
			"from LemmaCorpusCounts counts where " +
			"counts.corpus = :corpus",
			new String[]{"corpus"},
			new Object[]{corpus});
	}

	/**	Gets lemma/pos/spelling counts.
	 *
	 *	@param	lemma		Lemma.
	 *
	 *	@param	corpus		Corpus.
	 *
	 *	@return		A collection of all the lemma/pos/spelling counts for this lemma
	 *				and corpus.
	 *
	 *	@throws PersistenceException	error in persistence layer.
	 */

	public Collection getLemmaPosSpellingCounts (Lemma lemma, Corpus corpus)
		throws PersistenceException
	{
		return query(
			"from LemmaPosSpellingCounts counts " +
			"where counts.kind = 0 " +
			"and counts.corpus = :corpus " +
			"and counts.lemma = :lemma",
			new String[]{"lemma", "corpus"},
			new Object[]{lemma, corpus});
	}

	/**	Finds a word in a work part.
	 *
	 *	@param	workPart		Work part.
	 *
	 *	@param	lineIndex		Index of line containing word.
	 *
	 *	@param	startOffset		Starting offset of word in line.
	 *
	 *	@param	endOffset		Ending offset of word in line.
	 *
	 *	@return		The word, or null if none found.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public Word findWord (WorkPart workPart, int lineIndex, int startOffset,
		int endOffset)
			throws PersistenceException
	{
		Collection qList = query(
			"select word " +
			"from Word word " +
			"where word.workPart=:part " +
			"and word.location.start.index=:lineIndex " +
			"and word.location.start.offset <= :startOffset " +
			"and :endOffset <= word.location.end.offset",
			new String[]{"part", "lineIndex", "startOffset", "endOffset"},
			new Object[]{workPart, new Integer(lineIndex),
				new Integer(startOffset), new Integer(endOffset)});
		Iterator it = qList.iterator();
		return it.hasNext() ? (Word)it.next() : null;
	}

	/**	Finds lemmas by tag prefix.
	 *
	 *	@param	prefix		Tag prefix.
	 *
	 *	@param	corpus		Corpus, or null.
	 *
	 *	@return		A collection of up to the first 100
	 *				{@link edu.northwestern.at.wordhoard.model.morphology.Lemma
	 *				lemmas} which have tags that start with the prefix string,
	 *				in case-insenstive increasing alphabetical order by tag.
	 *				If the corpus argument is not null, the collection is
	 *				limited to lemmas in the specified corpus.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public Collection findLemmasByTagPrefix (String prefix, Corpus corpus)
			throws PersistenceException
	{
		String prefixInsensitive =
			CharsetUtils.translateToInsensitive(prefix) + "%";
		if (corpus == null) {
			return query(
				"from Lemma lemma " +
				"where lemma.tagInsensitive.string like :prefixInsensitive " +
				"order by lemma.tagInsensitive.string",
			    new String[]{"prefixInsensitive"},
			    new Object[]{prefixInsensitive},
		    	false,
			    100 );
		} else {
			return query(
				"select lemma from " +
				"LemmaCorpusCounts lcc " +
				"inner join lcc.lemma as lemma " +
				"where lemma.tagInsensitive.string " +
				"like :prefixInsensitive " +
				"and lcc.corpus = :corpus " +
				"order by lemma.tagInsensitive.string",
		    new String[]{"prefixInsensitive","corpus"},
		    new Object[]{prefixInsensitive,corpus},
		    false,
		    100);
		}
	}

	/**	Finds speaker names by prefix.
	 *
	 *	@param	prefix		Speaker name prefix.
	 *
	 *	@param	corpus		Corpus, or null.
	 *
	 *	@return		A collection of up to the first 100 speaker names which
	 *				start with the prefix string, ordered by name. If the
	 *				corpus argument is not null, the collection is limited
	 *				to speakers in the specified corpus.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public Collection findSpeakerNamesbyPrefix (String prefix, Corpus corpus)
			throws PersistenceException
	{
		String prefixPat = prefix + "%";
		if (corpus == null) {
			return query(
				"select distinct speaker.name " +
				"from Speaker speaker " +
				"where speaker.name like :prefix " +
				"order by speaker.name",
				new String[]{"prefix"},
				new Object[]{prefixPat},
				false,
				100);
		} else {
			return query(
				"select distinct speaker.name " +
				"from Speaker speaker, Work work " +
				"where speaker.name like :prefix " +
				"and speaker.work = work " +
				"and work.corpus = :corpus " +
				"order by speaker.name",
				new String[]{"prefix", "corpus"},
				new Object[]{prefixPat, corpus},
				false,
				100);
		}
	}

	/**	Finds authors by name prefix.
	 *
	 *	@param	prefix		name prefix.
	 *
	 *	@return		A collection of up to the first 100
	 *				{@link edu.northwestern.at.wordhoard.model.Author
	 *				authors} which have name that start with the prefix string,
	 *				ordered by name.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public Collection findAuthorsByNamePrefix (String prefix)
			throws PersistenceException
	{
		String prefixPat = prefix + "%";
		return query(
			"from Author author where author.name.string like :prefix " +
			"order by author.name.string",
			new String[]{"prefix"},
			new Object[]{prefixPat},
			false,
			100);
	}

	/**	Gets colocates for a collection of words.
	 *
	 *	@param	words		Collection of words.
	 *
	 *	@param	distance	Max distance.
	 *
	 *	@return		A collection of all the
	 *				{@link edu.northwestern.at.wordhoard.model.Word
	 *				words} which are within "distance" words
	 *				of one of the specified words.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public Collection getColocates (Collection words, int distance)
		throws PersistenceException
	{
		return query(
			"select colocate from Word colocate, Word word where " +
			"word in (:words) and " +
			"colocate.colocationOrdinal between " +
			"word.colocationOrdinal - :distance and " +
			"word.colocationOrdinal + :distance",
			new String[]{"words", "distance"},
			new Object[]{words, new Integer(distance)});
	}

	/**	Preloads concordance information.
	 *
	 *	<p>This method is a performance optimization for concordance displays.
	 *	Given a collection of words, we preload all the related objects for the
	 *	words that might be needed in a concordance display.
	 *
	 *	@param	words		Collection of words.
	 *
	 *	@throws PersistenceException	error in persistence layer.
	 */

	public void preloadConcordanceInfo (Collection words)
		throws PersistenceException
	{
		Iterator it = words.iterator();
		while (it.hasNext()) {
			ArrayList batch = new ArrayList();
			int ct = 0;
			while (it.hasNext() && ct++ < /* 100 */ 10000)
				batch.add(it.next());
			query(
				"from Word word " +
				"left join fetch word.wordParts as wordPart " +
				"left join fetch wordPart.lemPos as lemPos " +
				"left join fetch lemPos.lemma as lemma " +
				"left join fetch lemPos.pos " +
				"left join fetch lemma.wordClass " +
				"left join fetch word.speech as speech " +
				"left join fetch speech.speakers " +
				"left join fetch word.work as work " +
				"left join fetch work.corpus " +
				"left join fetch work.authors " +
				"left join fetch word.workPart " +
				"where word in (:batch)",
				new String[]{"batch"},
				new Object[]{batch});
		}
	}

	/**	Preloads adjacent words information.
	 *
	 *	<p>This method is a performance optimization for the concordance
	 *	grouping options for preceding word forms and following word forms.
	 *	Given a collection of words, we preload all the adjacent word objects
	 *	and their word part, lempos, lemma, and pos objects.
	 *
	 *	@param	words		Collection of words.
	 *
	 *	@param	preceding	True to preload objects for adjacent words,
	 *						false for following words.
	 *
	 *	@throws PersistenceException	error in persistence layer.
	 */

	public void preloadAdjacentInfo (Collection words, boolean preceding)
		throws PersistenceException
	{
		String prevOrNext = preceding ? "prev" : "next";
		Iterator it = words.iterator();
		while (it.hasNext()) {
			ArrayList batch = new ArrayList();
			int ct = 0;
			while (it.hasNext() && ct++ < /* 100 */ 10000)
				batch.add(it.next());
			query(
				"from Word word " +
				"left join fetch word." + prevOrNext + " as adjacent " +
				"left join fetch adjacent.wordParts as wordPart " +
				"left join fetch wordPart.lemPos as lemPos " +
				"left join fetch lemPos.lemma " +
				"left join fetch lemPos.pos " +
				"where word in (:batch)",
				new String[]{"batch"},
				new Object[]{batch});
		}
	}

	/**	Searches for lemmmata.
	 *
	 *	@param	sq		Search criteria.
	 *
	 *	@return		A list of all the
	 *				{@link edu.northwestern.at.wordhoard.model.search.SearchResult
	 *				search results} which match the search criteria,
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public List searchLemmata (SearchCriteriaLemmaSearch sq)
		throws PersistenceException
	{
		try {
			return sq.search(session);
		} catch (org.hibernate.HibernateException e) {
			throw new PersistenceException(e);
		}
	}

	/**	Searches for words.
	 *
	 *	@param	sq		Search criteria.
	 *
	 *	@return		A list of all the
	 *				{@link edu.northwestern.at.wordhoard.model.search.SearchResult
	 *				search results} which match the search criteria,
	 *				ordered by location (by work tag, then by ordinal
	 *				within work).
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public List searchWords (SearchCriteria sq)
		throws PersistenceException
	{
		try {
			return sq.search(session);
		} catch (org.hibernate.HibernateException e) {
			throw new PersistenceException(e);
		}
	}

	/**	Searches for works.
	 *
	 *	@param	sq		Work search criteria.
	 *
	 *	@return		A list of all the
	 *				{@link edu.northwestern.at.wordhoard.model.Work
	 *				works} which match the search criteria,
	 *				ordered by full title.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public List searchWorks (SearchWorkCriteria sq)
			throws PersistenceException
	{
		ArrayList result = new ArrayList();
		try {
			Corpus corpus = sq.getCorpus();
			String authorname = sq.getAuthorName();
			String title = sq.getTitle();
			String yearStart = sq.getYearStart();
			String yearEnd = sq.getYearEnd();
			boolean caseSensitive = sq.getCaseSensitive();
			boolean haveTitle=false,haveAuthor=false,
				haveYearStart=false,haveYearEnd=false;
			if((title != null && title!="") ||
				(authorname != null && authorname!=""))
			{
				String fromClause = null;
				String whereClause = null;
				if((title != null) &&  !title.equals("")) {
					fromClause = "select work from Work work ";
					if (title.indexOf("*") != -1) {
						title = title.replaceAll("\\*","%");
						whereClause = " where work.fullTitle like :title";
					} else {
						whereClause = " where work.fullTitle=:title";
					}
					haveTitle=true;
				}

				if((authorname != null) && !authorname.equals("")) {
					if (fromClause == null) {
						fromClause = "select work from Work work " +
							"inner join work.authors as author ";
					} else {
						fromClause += "inner join work.authors as author";
					}
					if (authorname.indexOf("*") != -1) {
						authorname = authorname.replaceAll("\\*", "%");
					}
					if (whereClause == null) {
						whereClause =
							" where author.name.string like :authorname";
					} else {
						whereClause +=
							" and author.name.string like :authorname";
					}
					haveAuthor=true;
				}

				int earlyDate=0,lateDate=0;
				try {
					if((yearStart != null) && !yearStart.equals("")) {
						earlyDate = Integer.parseInt(yearStart);
						if(fromClause==null) {
							fromClause = "select work from Work work ";
						}
						if(whereClause==null) {
							whereClause =
								" where (work.pubDate.startYear >= " +
								":earlyDate OR " +
								"work.pubDate.endYear >=:earlyDate)";
						} else {
							whereClause +=
								" AND (work.pubDate.startYear " +
								">=:earlyDate OR " +
								"work.pubDate.endYear >=:earlyDate)  ";
						}
						haveYearStart=true;
					}
				} catch (NumberFormatException e) {
				}

				try {
					if((yearEnd != null) && !yearEnd.equals("")) {
						lateDate = Integer.parseInt(yearEnd);
						if(fromClause==null) {
							fromClause = "select work from Work work ";
						}
						if(whereClause==null) {
							whereClause =
								" where (work.pubDate.endYear <= " +
								":lateDate OR work.pubDate.startYear " +
								"<=:lateDate)";
						} else {
							whereClause +=
								" AND (work.pubDate.endYear <= " +
								":lateDate OR work.pubDate.startYear "+
								"<=:lateDate)  ";
						}
						haveYearEnd=true;
					}
				} catch (NumberFormatException e) {
				}

				if(fromClause!=null && whereClause!=null) {
					String queryString = fromClause + whereClause +
						" order by work.fullTitle";
					org.hibernate.Query q = session.createQuery(queryString);
					if(haveTitle) {q.setString("title", title);}
					if(haveAuthor) {q.setString("authorname", authorname);}
					if(haveYearStart) {q.setInteger("earlyDate", earlyDate);}
					if(haveYearEnd) {q.setInteger("lateDate", lateDate);}
					List matches = q.list();
					if (!caseSensitive) return matches;
					result = new ArrayList();
					for (Iterator it = matches.iterator(); it.hasNext(); ) {
						Work work = (Work)it.next();
						if (title.equals(work.getFullTitle())) result.add(work);
					}
				}
			}
			return result;
		} catch (org.hibernate.HibernateException e) {
			throw new PersistenceException(e);
		}
	}

	/**	Generates "item in (:items)" query phrase.
	 *
	 *	@param	objectName	The name of the object.
	 *	@param	objects		Array of PersistentObject objects.
	 *
	 *	@return				The HQL "where" clause query phrase
	 *						as a StringBuffer.
	 *
	 *	<p>
	 *	Hibernate exhibits extremely poor performance when
	 *	asked to create a where clause phrase of the form:
	 *	</p>
	 *
	 *	<blockquote>
	 *	<p>
	 *	object in (:objects)
	 *	</p>
	 *	</blockquote>
	 *
	 *	<p>
	 *	For example, when "items" is an array of the 29,350
	 *	Word objects for the words in Hamlet, and all the Word
	 *	objects are already loaded, creating the simple
	 *	query "from Word where word in (:words)" takes nearly eight
	 *	minutes on a 2.5 Ghz Pentium system.  Note:  this is not
	 *	the execution time for the query, just the time to construct
	 *	the query.
	 *	</p>
	 *
	 *	<P>
	 *	This method takes a parameter name, e.g., "word", and
	 *	an array of PersistentObject objects, e.g., "words", and
	 *	generates the correponding HQL "where" clause phrase
	 *	for "word in (:words)" by expanding this to
	 *	"word.id in (id1, id2, id3 ... )".  This process
	 *	only a fraction of a second for the Hamlet example.
	 *	</p>
	 */

	 public static StringBuffer generateInPhrase
	 (
	 	String objectName ,
	 	PersistentObject[] objects
	 )
	 {
		StringBuffer result	= new StringBuffer();

		if ( objectName == null ) return result;

		String oName	= objectName.trim();

		if ( oName.length() == 0 ) return result;

		if ( objects == null ) return result;

		if ( objects.length < 1 ) return result;

		result.append( " " );
		result.append( oName );
		result.append( ".id in (" );

		for ( int i = 0 ; i < objects.length ; i++ )
		{
			if ( i > 0 ) result.append( "," );
			result.append( objects[ i ].getId().toString() );
		}
/*
		oName	= oName + ".id" ;

		result.append( " (" );

		for ( int i = 0 ; i < objects.length ; i++ )
		{
			if ( i > 0 ) result.append( " or " );
			result.append( oName );
			result.append( "=" );
			result.append( objects[ i ].getId().toString() );
		}
*/
		result.append( ") " );

		return result;
	}

	/**	Generates "item in (:items)" query phrase.
	 *
	 *	@param	objectName	The name of the object.
	 *	@param	objects		Collection of PersistentObject objects.
	 *
	 *	@return				The HQL "where" clause query phrase
	 *						as a StringBuffer.
	 *
	 *	<p>
	 *	Hibernate exhibits extremely poor performance when
	 *	asked to create a where clause phrase of the form:
	 *	</p>
	 *
	 *	<blockquote>
	 *	<p>
	 *	object in (:objects)
	 *	</p>
	 *	</blockquote>
	 *
	 *	<p>
	 *	For example, when "items" is a collection of the 29,350
	 *	Word objects for the words in Hamlet, and all the Word
	 *	objects are already loaded, creating the simple
	 *	query "from Word where word in (:words)" takes nearly eight
	 *	minutes on a 2.5 Ghz Pentium system.  Note:  this is not
	 *	the execution time for the query, just the time to construct
	 *	the query.
	 *	</p>
	 *
	 *	<P>
	 *	This method takes a parameter name, e.g., "word", and
	 *	a collection of PersistentObject objects, e.g., "words", and
	 *	generates the correponding HQL "where" clause phrase
	 *	for "word in (:words)" by expanding this to
	 *	"word.id in (id1, id2, id3 ... )".  This process
	 *	only a fraction of a second for the Hamlet example.
	 *	</p>
	 */

	 public static StringBuffer generateInPhrase
	 (
	 	String objectName ,
	 	Collection objects
	 )
	 {
		StringBuffer result	= new StringBuffer();

		if ( objectName == null ) return result;

		String oName	= objectName.trim();

		if ( oName.length() == 0 ) return result;

		if ( objects == null ) return result;

		if ( objects.size() < 1 ) return result;

		result.append( " " );
		result.append( oName );
		result.append( ".id in (" );

		int i				= 0;
		Iterator iterator	= objects.iterator();

		while ( iterator.hasNext() )
		{
			if ( i++ > 0 ) result.append( "," );

			PersistentObject po	= (PersistentObject)iterator.next();

			result.append( po.getId().toString() );
		}

		result.append( ") " );
/*
		oName	= oName + ".id" ;

		result.append( " (" );

		int i				= 0;
		Iterator iterator	= objects.iterator();

		while ( iterator.hasNext() )
		{
			if ( i++ > 0 ) result.append( " or " );

			PersistentObject po	= (PersistentObject)iterator.next();

			result.append( oName );
			result.append( "=" );

			result.append( po.getId().toString() );
		}

		result.append( ") " );
*/
		return result;
	}

	/**	Generates "item in (:items)" query phrase.
	 *
	 *	@param	objectName	The name of the object.
	 *	@param	objects		Collection of String objects.
	 *
	 *	@return				The HQL "where" clause query phrase
	 *						as a StringBuffer.
	 *
	 *	<P>
	 *	This method takes a parameter name, e.g., "word.tag", and
	 *	a collection of objects with toString() methods, e.g.,
	 *	"wordTags", and
	 *	generates the correponding HQL "where" clause phrase
	 *	for "word.tag in (:wordTags)",
	 *	</p>
	 */

	 public static StringBuffer generateInPhrase2
	 (
	 	String objectName ,
	 	Collection objects
	 )
	 {
		StringBuffer result	= new StringBuffer();

		if ( objectName == null ) return result;

		String oName	= objectName.trim();

		if ( oName.length() == 0 ) return result;

		if ( objects == null ) return result;

		if ( objects.size() < 1 ) return result;

		result.append( " " );
		result.append( oName );
		result.append( " in (" );

		int i				= 0;
		Iterator iterator	= objects.iterator();

		while ( iterator.hasNext() )
		{
			if ( i++ > 0 ) result.append( "," );
			result.append( (iterator.next()).toString() );
		}

		result.append( ") " );

		return result;
	}

	/**	Performs a query.
	 *
	 *	@param	wq		Query manager.
	 *
	 *	@return			List of results.
	 *
	 *	@throws InsufficientConstraintsException
	 *
	 *	@throws UnsupportedConstraintTypeException
	 */

/*
	public java.util.List performQuery (QueryManager wq)
		throws InsufficientConstraintsException,
		UnsupportedConstraintTypeException
	{
		java.util.List l = null;
		org.hibernate.Query q = session.createQuery(wq.getHQL());
		Iterator it = wq.getConstraints().getConstraintTypes().iterator();
		while (it.hasNext()) {
			TypedConstraintSet tcs = (TypedConstraintSet)it.next();
			HashSet s = tcs.getExemplars();
			if(s.size()==1) {
				if(tcs.isEntity()) {
					q.setEntity(tcs.getField(),s.iterator().next());
				} else if(tcs.isString()) {
					q.setString(tcs.getField(),(String)s.iterator().next());
				} else if(tcs.isDateRange()) {
						DateRange d = (DateRange)s.iterator().next();
						q.setInteger("earlyDate",d.getStartYear().intValue());
						q.setInteger("lateDate",d.getEndYear().intValue());
				}
			}  else if(s.size()>1) {
				if(tcs.isDateRange()) {
					Iterator sit = s.iterator();
					int i = 0;
					while (sit.hasNext()) {
						DateRange d = (DateRange)sit.next();
						q.setInteger("earlyDate"+i,d.getStartYear().intValue());
						q.setInteger("lateDate"+i,d.getEndYear().intValue());
						i++;
					}
				} else {
					q.setParameterList(tcs.getField(),s);
				}
			}
		}
		l = q.list();
		return l;
	}
	*/
}

/*
 * <p>
 * Copyright &copy; 2004-2011 Northwestern University.
 * </p>
 * <p>
 * This program is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * </p>
 * <p>
 * This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU General Public License for more
 * details.
 * </p>
 * <p>
 * You should have received a copy of the GNU General Public
 * License along with this program; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307 USA.
 * </p>
 */


