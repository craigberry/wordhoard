package edu.northwestern.at.wordhoard.swing;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.morphology.*;
import edu.northwestern.at.wordhoard.model.counts.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.db.*;

/**	Cached collections.
 */

public class CachedCollections {

	/**	The corpora, in increasing order by title. */

	private static Corpus[] corpora = null;

	/**	Authors. */

	private static Author[] authors;

	/**	Map from corpora to authors. */

	private static HashMap corpusAuthors = new HashMap();
	
	/**	The parts of speech, in increasing order by tag. */
	
	private static Pos[] pos = null;

	/**	Word classes, in increasing order by tag. */

	private static WordClass[] wordClasses;

	/**	Major word classes. */

	private static MajorWordClass[] majorWordClasses;

	/**	Metrical shapes. */

	private static MetricalShape[] metricalShapes;

	/**	Works. */

	private static Work[] works;

	/**	Map from corpora to lexicons (collections of LemmaCorpusCounts). */

	private static HashMap lexicons = new HashMap();

	/**	Gets the corpora.
	 *
	 *	<p>In addition to loading and returning all the corpora, all the works,  
	 *	table of contents views, authors, and work child parts are preloaded.
	 *
	 *	@return			All the corpora, in increasing order by ordinal.
	 *
	 *	@throws	PersistenceException
	 */

	public static Corpus[] getCorpora ()
		throws PersistenceException
	{
		if (corpora == null) {
			Collection corporaCollection =
				WordHoard.getPm().getAllCorpora();
			corpora = (Corpus[])corporaCollection.toArray(
				new Corpus[corporaCollection.size()]);
			Arrays.sort(corpora,
				new Comparator () {
					public int compare (Object o1, Object o2) {
						Corpus c1 = (Corpus)o1;
						Corpus c2 = (Corpus)o2;
						return c1.getOrdinal() - c2.getOrdinal();
					}
				}
			);
		}
		return corpora;
	}

	/**	Gets the authors.
	 *
	 *	@return			All the authors, in increasing case-insensitive
	 *					alphabetical order by name.
	 *
	 *	@throws	PersistenceException
	 */

	public static Author[] getAuthors ()
		throws PersistenceException
	{
		if (authors == null) {
			Collection authCollection =
				WordHoard.getPm().getAllAuthors();
			authors =(Author[])authCollection.toArray(
				new Author[authCollection.size()]);
		}
		return authors;
	}

	/**	Gets the authors for a corpus.
	 *
	 *	@param	corpus		Corpus.
	 *
	 *	@return				All the authors for the corpus, in increasing
	 *						case-insensitive alphabetical order by name.
	 *
	 *	@throws	PersistenceException
	 */

	 public static Author[] getAuthors (Corpus corpus)
	 	throws PersistenceException
	 {
	 	Author[] result = (Author[])corpusAuthors.get(corpus);
	 	if (result == null) {
			Collection authCollection =
				WordHoard.getPm().getAllAuthors(corpus);
			result = (Author[])authCollection.toArray(
				new Author[authCollection.size()]);
			corpusAuthors.put(corpus, result);
		}
		return result;
	 }
	 
	/**	Gets the parts of speech.
	 *
	 *	@return			All the parts of speech, in increasing case-insensitive
	 *					alphabetical order by tag.
	 *
	 *	@throws	PersistenceException
	 */
	 
	public static Pos[] getPos ()
		throws PersistenceException
	{
		if (pos == null) {
			Collection posCollection =
				WordHoard.getPm().getAllPos();
			pos = (Pos[])posCollection.toArray(
				new Pos[posCollection.size()]);
		}
		return pos;
	}

	/**	Gets the word classes.
	 *
	 *	@return			All the word classes, in increasing
	 *					case-insensitive order by tag.
	 *
	 *	@throws	PersistenceException
	 */

	public static WordClass[] getWordClasses ()
		throws PersistenceException
	{
		if (wordClasses == null) {
			Collection wcCollection =
				WordHoard.getPm().getAllWordClasses();
			wordClasses =
				(WordClass[])wcCollection.toArray(
					new WordClass[wcCollection.size()]);
		}
		return wordClasses;
	}

	/**	Gets all the major word classes.
	 *
	 *	@return		All the major word classes, in increasing
	 *				case-insensitive alphabetical order.
	 *
	 *	@throws	PersistenceException
	 */

	public static MajorWordClass[] getMajorWordClasses ()
		throws PersistenceException
	{
		if (majorWordClasses == null) {
			Collection mwcCollection =
				WordHoard.getPm().getAllMajorWordClasses();
			majorWordClasses = (MajorWordClass[])mwcCollection.toArray(
				new MajorWordClass[mwcCollection.size()]);
		}
		return majorWordClasses;
	}

	/**	Gets the metrical shapes.
	 *
	 *	@return			All the metrical shapes, in increasing
	 *					case-insensitive alphabetical order.
	 *
	 *	@throws	PersistenceException
	 */

	public static MetricalShape[] getMetricalShapes ()
		throws PersistenceException
	{
		if (metricalShapes == null) {
			Collection shapeCollection =
				WordHoard.getPm().getAllMetricalShapes();
			metricalShapes =(MetricalShape[])shapeCollection.toArray(
				new MetricalShape[shapeCollection.size()]);
		}
		return metricalShapes;
	}

	/**	Gets the lexicon for a corpus.
	 *
	 *	@param	corpus		Corpus.
	 *
	 *	@return				The lexicon: an array of all the lemma/corpus
	 *						counts objects for the corpus, in no particular
	 *						order.
	 *
	 *	@throws	PersistenceException
	 */

	public static LemmaCorpusCounts[] getLexicon (Corpus corpus)
		throws PersistenceException
	{
		LemmaCorpusCounts[] result = (LemmaCorpusCounts[])lexicons.get(corpus);
		if (result == null) {
			Collection lexiconCollection =
				WordHoard.getPm().getLexicon(corpus);
			result = (LemmaCorpusCounts[])lexiconCollection.toArray(
				new LemmaCorpusCounts[lexiconCollection.size()]);
			lexicons.put(corpus, result);
		}
		return result;
	}

	/**	Gets the works.
	 *
	 *	@return			All the works.
	 *
	 *	@throws	PersistenceException
	 */

	public static Work[] getWorks ()
		throws PersistenceException
	{
		if (works == null) {
			Collection worksCollection =
				WordHoard.getPm().getAllWorks();
			works = (Work[])worksCollection.toArray(
				new Work[worksCollection.size()]);
		}
		return works;
	}

	/**	Hides the default no-arg constructor.
	 */

	private CachedCollections () {
		throw new UnsupportedOperationException();
	}

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

