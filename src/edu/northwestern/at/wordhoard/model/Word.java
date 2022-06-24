package edu.northwestern.at.wordhoard.model;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.text.*;

import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.wordhoard.model.morphology.*;
import edu.northwestern.at.wordhoard.model.speakers.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.model.grouping.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.db.mysql.*;

/**	A word occurrence.
 *
 *	<p>A word records an occurrence of a specific word in a
 *	specific work at a specific location. Each word occurrence has the
 *	following attributes:
 *
 *	<ul>
 *	<li>A unique persistence id.
 *	<li>The spelling exactly as it appears in the text.
 *	<li>The spelling with all diacriticals removed and mapped to lower case.
 *	<li>The {@link edu.northwestern.at.wordhoard.model.WorkPart
 *		work part} in which the word occurs.
 *	<li>The {@link edu.northwestern.at.wordhoard.model.Work
 *		work} in which the word occurs.
 *	<li>The {@link edu.northwestern.at.wordhoard.model.Line line}
 *		in which the word occurs. This attribute may be null.
 *	<li>The path to the word.
 *	<li>A tag for the word occurrence (see below).
 *	<li>The {@link edu.northwestern.at.wordhoard.model.text.TextRange
 *		location} of the word in the text for the work part.
 *	<li>Punctuation preceding and following the word in the text,
 *		including space characters, with line breaks represented by
 *		a slash character ("/").
 *	<li>Links to the preceding and next words in the work part. These
 *		links implement a doubly-linked list of all the word occurrences
 *		in the part.
 *	<li>A list of all the
 *		{@link edu.northwestern.at.wordhoard.model.morphology.WordPart
 *		word parts} for this word occurrence.
 *	<li>The tag of the work in which the word occurs.
 *	<li>The ordinal within the work of the word.
 *	<li>The "colocation ordinal": A number which combines the id of the
 *		work part and the ordinal within the work. This field is used to
 *		quickly find word colocates. It satisfies the following property:
 *		Word1 is n words to the left of word2 and in the same work part as
 *		word2 iff word1.colocationOrdinal + n = word2.colocationOrdinal, for
 *		all n < 2^31.
 *	<li>The {@link edu.northwestern.at.wordhoard.model.speakers.Speech
 *		speech} in which the word occurs, or null if none.
 *	<li>The {@link edu.northwestern.at.wordhoard.model.wrappers.Prosodic
 *		prosodic} attribute.
 *	<li>The {@link edu.northwestern.at.wordhoard.model.wrappers.MetricalShape
 *		metrical shape} attribute.
 *	</ul>
 *
 *	<p>The tag is required. It permanently and uniquely identifies the word
 *	occurrence across all corpora. It may be used to refer to the word
 *	from outside the object model. It is guaranteed not to change across
 *	versions of the object model. For example, the tag for the first word
 *	in Hamlet Act 3, Scene 2 is "sha-ham30200101".
 *
 *	@hibernate.class table="word"
 */

public class Word implements PersistentObject, SearchDefaults, Comparable {

	/**	Unique persistence id (primary key). */

	private Long id;

	/**	The spelling exactly as it occurs in the text. */

	private Spelling spelling;

	/**	The spelling with all diacriticals removed and mapped to lower case. */

	private Spelling spellingInsensitive;

	/**	The work part. */

	private WorkPart workPart;

	/**	The work. */

	private Work work;

	/**	The line. */

	private Line line;

	/**	The path. */

	private String path;

	/**	The tag of the word in the work part. */

	private String tag;

	/**	The location of the word in the part text. */

	private TextRange location;

	/**	Punctuation preceding the word in the text. */

	private String puncBefore = "$";

	/**	Punctuation following the word in the text. */

	private String puncAfter = "$";

	/**	The preceding word in the work part. */

	private Word prev;

	/**	The next word in the work part. */

	private Word next;

	/**	List of word parts. */

	private List wordParts = new ArrayList();

	/**	The work tag. */

	private String workTag;

	/**	The ordinal of the word within the work. */

	private int workOrdinal;

	/**	The colocation ordinal. */

	private long colocationOrdinal;

	/**	The speech, or null if none. */

	private Speech speech;

	/**	The prosodic attribute. */

	private Prosodic prosodic = new Prosodic(Prosodic.UNKNOWN);

	/**	The metrical shape. */

	private MetricalShape metricalShape;

	/**	Creates a new word.
	 */

	public Word () {
	}

	/**	Gets the unique id.
	 *
	 *	@return		The unique id.
	 *
	 *	@hibernate.id access="field" generator-class="assigned"
	 */

	public Long getId () {
		return id;
	}

	/**	Sets the unique id.
	 *
	 *	@param	id		The unique id.
	 */

	public void setId (Long id) {
		this.id = id;
	}

	/**	Gets the spelling.
	 *
	 *	@return		The spelling.
	 *
	 *	@hibernate.component prefix="spelling_"
	 */

	public Spelling getSpelling () {
		return spelling;
	}

	/**	Sets the spelling.
	 *
	 *	<p>This method also sets the spelling mapped to lower case.
	 *
	 *	@param	spelling	The spelling.
	 */

	public void setSpelling (Spelling spelling) {
		this.spelling = spelling;
		String string = spelling.getString();
		byte charset = spelling.getCharset();
		spellingInsensitive = new Spelling(
			CharsetUtils.translateToInsensitive(string),
			charset);
	}

	/**	Gets the insensitive spelling.
	 *
	 *	@return		The spelling with all diacriticals removed and
	 *				mapped to lower case.
	 *
	 *	@hibernate.component prefix="spellingInsensitive_"
	 */

	public Spelling getSpellingInsensitive () {
		return spellingInsensitive;
	}

	/**	Sets the insensitive spelling.
	 *
	 *	<p>Hibernate needs this method.
	 *
	 *	@param	spellingInsensitive		The insensitive spelling.
	 */

	private void setSpellingInsensitive (Spelling spellingInsensitive) {
		this.spellingInsensitive = spellingInsensitive;
	}

	/**	Gets the work part.
	 *
	 *	@return		The work part.
	 *
	 *	@hibernate.many-to-one access="field" foreign-key="workPart_index"
	 */

	public WorkPart getWorkPart () {
		return workPart;
	}

	/**	Sets the work part.
	 *
	 *	<p>This method also sets the work.
	 *
	 *	@param	workPart	The work part.
	 */

	public void setWorkPart (WorkPart workPart) {
		this.workPart = workPart;
		this.work = workPart.getWork();
	}

	/**	Gets the work.
	 *
	 *	@return		The work.
	 *
	 *	@hibernate.many-to-one access="field" foreign-key="work_index"
	 */

	public Work getWork () {
		return work;
	}

	/**	Gets the line.
	 *
	 *	@return		The line, or null if none.
	 *
	 *	@hibernate.many-to-one access="field" foreign-key="line_index"
	 */

	public Line getLine () {
		return line;
	}

	/**	Sets the line.
	 *
	 *	@param	line	The line.
	 */

	public void setLine (Line line) {
		this.line = line;
	}

	/**	Gets the path.
	 *
	 *	@return		The path.
	 *
	 *	@hibernate.property access="field"
	 */

	public String getPath () {
		return path;
	}

	/**	Gets the tag.
	 *
	 *	@return		The tag.
	 *
	 *	@hibernate.property access="field"
	 *	@hibernate.column name="tag" index="tag_index"
	 */

	public String getTag () {
		return tag;
	}

	/**	Sets the tag.
	 *
	 *	@param	tag		The tag.
	 */

	public void setTag (String tag) {
		this.tag = tag;
	}

	/**	Gets the word location.
	 *
	 *	@return		The word location.
	 *
	 *	@hibernate.component prefix="location_"
	 */

	public TextRange getLocation () {
		return location;
	}

	/**	Sets the word location.
	 *
	 *	@param	location	The word location in the part text.
	 */

	public void setLocation (TextRange location) {
		this.location = location;
	}

	/**	Gets the punctuation preceding the word.
	 *
	 *	@return		The punctuation preceding the word.
	 *
	 *	@hibernate.property access="field"
	 */

	public String getPuncBefore () {
		return puncBefore.substring(0, puncBefore.length()-1);
	}

	/**	Sets the punctuation preceding the word.
	 *
	 *	@param	puncBefore		The punctuation preceding the word.
	 */

	public void setPuncBefore (String puncBefore) {
		this.puncBefore = puncBefore + "$";
	}

	/**	Gets the punctuation following the word.
	 *
	 *	@return		The punctuation following the word.
	 *
	 *	@hibernate.property access="field"
	 */

	public String getPuncAfter () {
		return puncAfter.substring(0, puncAfter.length()-1);
	}

	/**	Sets the punctuation following the word.
	 *
	 *	@param	puncAfter		The punctuation following the word.
	 */

	public void setPuncAfter (String puncAfter) {
		this.puncAfter = puncAfter + "$";
	}

	/**	Gets the previous word.
	 *
	 *	@return		The previous word.
	 *
	 *	@hibernate.many-to-one access="field" foreign-key="prev_index"
	 */

	public Word getPrev () {
		return prev;
	}

	/**	Sets the previous word.
	 *
	 *	@param	prev		The previous word.
	 */

	public void setPrev (Word prev) {
		this.prev = prev;
	}

	/**	Gets the next word.
	 *
	 *	@return		The next word.
	 *
	 *	@hibernate.many-to-one access="field" foreign-key="next_index"
	 */

	public Word getNext () {
		return next;
	}

	/**	Sets the next word.
	 *
	 *	@param	next		The next word.
	 */

	public void setNext (Word next) {
		this.next = next;
	}

	/**	Gets the word parts.
	 *
	 *	@return		The word parts as an unmodifiable list.
	 *
	 *	@hibernate.list access="field" lazy="true"
	 *	@hibernate.collection-key column="word"
	 *	@hibernate.collection-index column="partIndex"
	 *	@hibernate.collection-one-to-many
	 *		class="edu.northwestern.at.wordhoard.model.morphology.WordPart"
	 */

	public List getWordParts () {
		return Collections.unmodifiableList(wordParts);
	}

	/**	Gets the work tag.
	 *
	 *	@return		The work tag.
	 *
	 *	@hibernate.property access="field"
	 */

	public String getWorkTag () {
		return workTag;
	}

	/**	Sets the work tag.
	 *
	 *	@param	workTag		The work tag.
	 */

	public void setWorkTag (String workTag) {
		this.workTag = workTag;
	}

	/**	Gets the work ordinal.
	 *
	 *	@return		The ordinal of the word within the work.
	 *
	 *	@hibernate.property access="field"
	 */

	public int getWorkOrdinal () {
		return workOrdinal;
	}

	/**	Sets the work ordinal.
	 *
	 *	@param	workOrdinal		The ordinal of the word within the work.
	 */

	public void setWorkOrdinal (int workOrdinal) {
		this.workOrdinal = workOrdinal;
	}

	/**	Gets the colocation ordinal.
	 *
	 *	@return		The colocation ordinal.
	 *
	 *	@hibernate.property access="field"
	 *	@hibernate.column name="colocationOrdinal"
	 *		index="colocationOrdinal_index"
	 */

	public long getColocationOrdinal () {
		return colocationOrdinal;
	}

	/**	Gets the speech.
	 *
	 *	@return		The speech, or null if none.
	 *
	 *	@hibernate.many-to-one access="field" foreign-key="speech_index"
	 */

	public Speech getSpeech () {
		return speech;
	}

	/**	Sets the speech.
	 *
	 *	@param	speech		The speech, or null if none.
	 */

	public void setSpeech (Speech speech) {
		this.speech = speech;
	}

	/**	Gets the prosodic attribute.
	 *
	 *	@return		The prosodic attribute.
	 *
	 *	@hibernate.component prefix="prosodic_"
	 */

	public Prosodic getProsodic () {
		return prosodic;
	}

	/**	Sets the prosodic attribute.
	 *
	 *	@param	prosodic	The prosodic attibute.
	 */

	public void setProsodic (Prosodic prosodic) {
		this.prosodic = prosodic;
	}

	/**	Sets the prosodic attribute.
	 *
	 *	@param	prosodic	The prosodic attibute.
	 */

	public void setProsodic (byte prosodic) {
		this.prosodic = new Prosodic(prosodic);
	}

	/**	Gets the metrical shape.
	 *
	 *	@return		The metrical shape.
	 *
	 *	@hibernate.component prefix="metricalShape_"
	 */

	public MetricalShape getMetricalShape () {
		return metricalShape;
	}

	/**	Sets the metrical shape.
	 *
	 *	@param	metricalShape	The metrical shape.
	 */

	public void setMetricalShape (MetricalShape metricalShape) {
		this.metricalShape = metricalShape;
	}

	/**	Sets the metrical shape.
	 *
	 *	@param	metricalShape	The metrical shape.
	 */

	public void setMetricalShape (String metricalShape) {
		this.metricalShape = new MetricalShape(metricalShape);
	}

	/**	Initializes derived values.
	 *
	 *	<p>Sets the colocation ordinal to partId << 32 | workOrdinal,
	 *	where partId = the persistence id of the work part.
	 *	The work part and work ordinal must be set, and
	 *	the work part must have a persistence id.
	 *
	 *	<p>Sets the word path. The line and work part must be set.
	 */

	public void initDerivedValues () {
		this.colocationOrdinal =
			workPart.getId().longValue() << 32 | workOrdinal;
		if (line == null) {
			path = workPart.getPath();
		} else {
			path = workPart.getPath() + "." + line.getLabel();
		}
	}

	/**	Gets a search criterion default value.
	 *
	 *	@param	cls		Model class of search criterion.
	 *
	 *	@return			Default value for search criterion.
	 */

	public SearchCriterion getSearchDefault (Class cls) {
		if (cls.equals(Spelling.class)) {
			return new SpellingWithCollationStrength(spelling,
				Collator.PRIMARY);
		} else if (cls.equals(Prosodic.class)) {
			return prosodic;
		} else if (cls.equals(MetricalShape.class)) {
			return metricalShape;
		} else if (cls.equals(Speaker.class) || cls.equals(Gender.class) ||
			cls.equals(Mortality.class))
		{
			if (speech == null) return null;
			return speech.getSearchDefault(cls);
		} else if (cls.equals(Narrative.class)) {
			return new Narrative(speech == null ? Narrative.NARRATION :
				Narrative.SPEECH);
		} else if (cls.equals(Lemma.class) || cls.equals(Pos.class) ||
			cls.equals(MajorWordClass.class) ||
			cls.equals(WordClass.class))
		{
			if (wordParts == null || wordParts.size() == 0) return null;
			WordPart wordPart = (WordPart)wordParts.get(0);
			return wordPart.getSearchDefault(cls);
		} else {
			if (workPart == null) return null;
			return workPart.getSearchDefault(cls);
		}
	}

	/**	Gets grouping objects.
	 *
	 *	<p>Returns the grouping object(s) for the word given a grouping
	 *	class and a word part index.
	 *
	 *	@param	groupBy		Grouping class.
	 *
	 *	@param	partIndex	Part index, or -1 if none.
	 *
	 *	@return				List of grouping objects.
	 */

	public List getGroupingObjects (Class groupBy, int partIndex) {
		ArrayList result = new ArrayList();
		WordPart wordPart = null;
		LemPos lemPos = null;
		if (groupBy.equals(Work.class)) {
			if (work != null) result.add(work);
		} else if (groupBy.equals(WorkPart.class)) {
			if (workPart != null) result.add(workPart);
		} else if (groupBy.equals(PrecedingWordForm.class)) {
			if (partIndex <= 0) {
				if (prev == null) {
					wordPart = null;
				} else {
					java.util.List prevWordParts = prev.getWordParts();
					int n = prevWordParts.size();
					if (n == 0) {
						wordPart = null;
					} else {
						wordPart = (WordPart)prevWordParts.get(n-1);
					}
				}
			} else {
				wordPart = (WordPart)wordParts.get(partIndex-1);
			}
			lemPos = wordPart == null ? null : wordPart.getLemPos();
			if (lemPos != null) result.add(new PrecedingWordForm(lemPos));
		} else if (groupBy.equals(FollowingWordForm.class)) {
			int n = wordParts.size();
			if (partIndex < 0 || partIndex == n-1) {
				if (next == null) {
					wordPart = null;
				} else {
					java.util.List nextWordParts = next.getWordParts();
					if (nextWordParts.size() == 0) {
						wordPart = null;
					} else {
						wordPart = (WordPart)nextWordParts.get(0);
					}
				}
			} else {
				wordPart = (WordPart)wordParts.get(partIndex+1);
			}
			lemPos = wordPart == null ? null : wordPart.getLemPos();
			if (lemPos != null) result.add(new FollowingWordForm(lemPos));
		} else if (groupBy.equals(Spelling.class)) {
			result.add(spellingInsensitive);
		} else if (groupBy.equals(Narrative.class)) {
			result.add(speech == null ? Narrative.NARRATION_OBJECT :
				Narrative.SPEECH_OBJECT);
		} else if (groupBy.equals(Prosodic.class)) {
			if (prosodic != null) result.add(prosodic);
		} else if (groupBy.equals(MetricalShape.class)) {
			if (metricalShape != null) result.add(metricalShape);
		} else if (groupBy.equals(Lemma.class) ||
			groupBy.equals(Pos.class) ||
			groupBy.equals(WordClass.class) ||
			groupBy.equals(MajorWordClass.class))
		{
			if (partIndex < 0) {
				for (Iterator it = wordParts.iterator(); it.hasNext(); ) {
					wordPart = (WordPart)it.next();
					wordPart.getGroupingObjects(groupBy, result);
				}
			} else {
				wordPart = (WordPart)wordParts.get(partIndex);
				wordPart.getGroupingObjects(groupBy, result);
			}
		} else if (groupBy.equals(Speaker.class) ||
			groupBy.equals(SpeakerName.class) ||
			groupBy.equals(Gender.class) ||
			groupBy.equals(Mortality.class))
		{
			if (speech != null) {
				speech.getGroupingObjects(groupBy, result);
			}
			if (result.size() == 0) {
				result.add(NoneOfTheAbove.get(groupBy,
					"the narrator or poet"));
			}
		} else if (groupBy.equals(Corpus.class) ||
			groupBy.equals(Author.class) ||
			groupBy.equals(PubYearRange.class) ||
			groupBy.equals(PubDecade.class))
		{
			if (work != null)
				work.getGroupingObjects(groupBy, result);
		}
		if (result.size() == 0) result.add(NoneOfTheAbove.get(groupBy));
		return result;
	}

	/**	Gets a brief description of a word.
	 *
	 *	<p>Returns a brief one line description of the word, suitable for
	 *	display in a tool tip or as a footnote.
	 *
	 *	<p>For compound words (words with more than one part), the
	 *	brief description is simply "compound word".
	 *
	 *	<p>Otherwise, if a Benson gloss is available, the description
	 *	includes his lemma, part of speech, and definition.
	 *
	 *	<p>Otherwise, the description includes the lemma word and
	 *	part of speech.
	 *
	 *	@return			The brief description of the word, or null if none
	 *					available.
	 */

	public Spelling getBriefDescription () {
		if (wordParts == null || wordParts.size() == 0) return null;
		if (wordParts.size() > 1)
			return new Spelling("compound word", TextParams.ROMAN);
		WordPart wordPart = (WordPart)wordParts.get(0);
		if (wordPart == null) return null;
		BensonLemPos bensonLemPos = wordPart.getBensonLemPos();
		if (bensonLemPos == null) {
			LemPos lemPos = wordPart.getLemPos();
			if (lemPos == null) return null;
			Lemma lemma = lemPos.getLemma();
			if (lemma == null) return null;
			Spelling lemmaSpelling = lemma.getSpelling();
			if (lemmaSpelling == null) return null;
			String lemmaString = lemmaSpelling.getString();
			if (lemmaString == null) return null;
			Pos pos = lemPos.getPos();
			String posTag = pos == null ? null : pos.getTag();
			String str = posTag == null ? lemmaString :
				lemmaString + " (" + posTag + ")";
			return new Spelling(str, lemmaSpelling.getCharset());
		} else {
			BensonLemma bensonLemma = bensonLemPos.getLemma();
			if (bensonLemma == null) return null;
			String lemmaSpelling = bensonLemma.getWord();
			String definition = bensonLemma.getDefinition();
			if (lemmaSpelling == null && definition == null) return null;
			BensonPos bensonPos = bensonLemPos.getPos();
			String pos = bensonPos == null ? null : bensonPos.getTag();
			String lemmaSpellingWithPos = null;
			if (lemmaSpelling != null) {
				if (pos == null) {
					lemmaSpellingWithPos = lemmaSpelling;
				} else {
					lemmaSpellingWithPos = lemmaSpelling + " (" + pos + ")";
				}
			}
			String str;
			if (lemmaSpellingWithPos == null) {
				str = definition;
			} else if (definition == null) {
				str = lemmaSpellingWithPos;
			} else {
				str = lemmaSpellingWithPos + ": " + definition;
			}
			return new Spelling(str, TextParams.ROMAN);
		}
	}

	/**	Gets a string representation of the word.
	 *
	 *	@return			The tag.
	 */

	public String toString () {
		return tag;
	}

	/**	Exports the object to a MySQL table exporter/importer.
	 *
	 *	@param	exporterImporter	MySQL table exporter/importer.
	 */

	public void export (TableExporterImporter exporterImporter) {
		exporterImporter.print(id);
		String spellingString = null;
		byte spellingCharset = 0;
		if (spelling != null) {
			spellingString = spelling.getString();
			spellingCharset = spelling.getCharset();
		}
		exporterImporter.print(spellingString);
		exporterImporter.print(spellingCharset);
		String spellingInsensitiveString = null;
		byte spellingInsensitiveCharset = 0;
		if (spellingInsensitive != null) {
			spellingInsensitiveString = spellingInsensitive.getString();
			spellingInsensitiveCharset = spellingInsensitive.getCharset();
		}
		exporterImporter.print(spellingInsensitiveString);
		exporterImporter.print(spellingInsensitiveCharset);
		Long workPartId = workPart == null ? null : workPart.getId();
		exporterImporter.print(workPartId);
		Long workId = work == null ? null : work.getId();
		exporterImporter.print(workId);
		Long lineId = line == null ? null : line.getId();
		exporterImporter.print(lineId);
		exporterImporter.print(path);
		exporterImporter.print(tag);
		int locationStartIndex = 0;
		int locationStartOffset = 0;
		int locationEndIndex = 0;
		int locationEndOffset = 0;
		if (location != null) {
			TextLocation start = location.getStart();
			TextLocation end = location.getEnd();
			if (start != null) {
				locationStartIndex = start.getIndex();
				locationStartOffset = start.getOffset();
			}
			if (end != null) {
				locationEndIndex = end.getIndex();
				locationEndOffset = end.getOffset();
			}
		}
		exporterImporter.print(locationStartIndex);
		exporterImporter.print(locationStartOffset);
		exporterImporter.print(locationEndIndex);
		exporterImporter.print(locationEndOffset);
		exporterImporter.print(puncBefore);
		exporterImporter.print(puncAfter);
		Long prevId = prev == null ? null : prev.getId();
		exporterImporter.print(prevId);
		Long nextId = next == null ? null : next.getId();
		exporterImporter.print(nextId);
		exporterImporter.print(workTag);
		exporterImporter.print(workOrdinal);
		exporterImporter.print(colocationOrdinal);
		Long speechId = speech == null ? null : speech.getId();
		exporterImporter.print(speechId);
		byte prosodicByte = prosodic == null ? 0 : prosodic.getProsodic();
		exporterImporter.print(prosodicByte);
		String metricalShapeStr = metricalShape == null ? null :
			metricalShape.getMetricalShape();
		exporterImporter.print(metricalShapeStr);
		exporterImporter.println();
	}

	/**	Returns true if some other object is equal to this one.
	 *
	 *	<p>The two words are equal if their work parts and tags are equal.
	 *
	 *	@param	obj		The other object.
	 *
	 *	@return			True if this object equals the other object.
	 */

	public boolean equals (Object obj) {
		if (obj == null || !(obj instanceof Word)) return false;
		Word other = (Word)obj;
		return Compare.equals(workPart, other.getWorkPart()) &&
			Compare.equals(tag, other.getTag());
	}

	/**	Returns a hash code for the object.
	 *
	 *	@return		The hash code.
	 */

	public int hashCode () {
		return workPart.hashCode() + tag.hashCode();
	}

	/**	Implement Comparable interface.
	 *
	 *	@param	obj		Other word object to which to compare this object.
	 *
	 *	<p>
	 *	The word tag is used for comparison.
	 *	</p>
	 */

	public int compareTo (Object obj) {
		if (obj == null || !(obj instanceof Word)) return Integer.MIN_VALUE;
		int result = Compare.compare(tag, ((Word)obj).getTag());
		return result;
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

