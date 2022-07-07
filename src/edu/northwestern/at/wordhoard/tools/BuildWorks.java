package edu.northwestern.at.wordhoard.tools;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import java.sql.*;

import org.w3c.dom.*;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.speakers.*;
import edu.northwestern.at.wordhoard.model.morphology.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.xml.*;
import edu.northwestern.at.utils.db.mysql.*;

/**	Builds works.
 *
 *	<p>Usage:
 *
 *	<p><code>BuildWorks in db username password [spellings] [debug]</code>
 *
 *	<p>in = Path to a work definition XML input file, or a path to a directory
 *	of such files. If a directory path is specified, all files in the file
 *	system tree rooted at the directory whose names end in ".xml" are
 *	processed.
 *
 *	<p>dbname = Database name.
 *
 *	<p>username = MySQL username.
 *
 *	<p>password = MySQL password.
 *
 *	<p>spellings = Optional path to a standard spellings definition XML
 *	input file, or a path to a directory of such files. If a directory path
 *	is specified, all files in the file system tree rooted at the directory
 *	whose names end in ".xml" are processed.
 *
 *	<p>debug = Debugging option. If present, the WordHoard client is run
 *	and the newly built work is opened. In addition, tagged words are not
 *	saved in the database, to make the program run much faster. This option
 *	is useful when working on text formatting issues. If a directory is being
 *	processed, this option is ignored.
 */

public class BuildWorks {

	/**	Constants. */

	private static final int MAX_TITLE_LEN = 50;
	private static final char ELLIPSIS = '\u2026';

	/**	Input file or directory path. */

	private static String inPath;

	/**	Database name. */

	private static String dbname;

	/**	MySQL usename. */

	private static String username;

	/**	MySQL password. */

	private static String password;

	/**	Standard spellings file or directory path, or null if none. */

	private static String spellingPath;

	/**	True if debug mode. */

	private static boolean debug = false;

	/**	MySQL table exporter/importer for lemma objects. */

	private static TableExporterImporter lemmaTableExporterImporter;

	/**	MySQL table exporter/importer for lemPos objects. */

	private static TableExporterImporter lemPosTableExporterImporter;

	/**	MySQL table exporter/importer for word objects. */

	private static TableExporterImporter wordTableExporterImporter;

	/**	MySQL table exporter/importer for word part objects. */

	private static TableExporterImporter wordPartTableExporterImporter;

	/**	MySQL table exporter/importer for line objects. */

	private static TableExporterImporter lineTableExporterImporter;

	/**	MySQL table exporter/importer for speech objects. */

	private static TableExporterImporter speechTableExporterImporter;

	/**	MySQL table exporter/importer for speech_speakers objects. */

	private static TableExporterImporter speechSpeakersTableExporterImporter;

	/**	DOM tree for parsed XML document. */

	private static Document document;

	/**	Persistence manager for static object model. */

	private static PersistenceManager pm;

	/**	Next available id for lemma objects. */

	private static long lemmaId;

	/**	Next available id for lemPos objects. */

	private static long lemPosId;

	/**	Next available id for word objects. */

	private static long wordId;

	/**	Next available id for word part objects. */

	private static long wordPartId;

	/**	Next available id for work part objects. */

	private static long workPartId;

	/**	Next available id for line objects. */

	private static long lineId;

	/**	Next available id for speech objects. */

	private static long speechId;

	/**	Map from word class tags to WordClass objects. */

	private static HashMap wordClassMap = new HashMap();

	/**	Map from part of speech tags to Pos objects. */

	private static HashMap posMap = new HashMap();

	/**	Map from lemma tags to Lemma objects. */

	private static HashMap lemmaMap = new HashMap();

	/**	A (lemma id, pos id) pair. */

	private static class LemPosIdPair {
		private long lemmaId;
		private long posId;
		private LemPosIdPair (long lemmaId, long posId) {
			this.lemmaId = lemmaId;
			this.posId = posId;
		}
		public boolean equals (Object obj) {
			LemPosIdPair other = (LemPosIdPair)obj;
			return lemmaId == other.lemmaId && posId == other.posId;
		}
		public int hashCode () {
			return (int)(lemmaId + posId);
		}
	}

	/**	Map from LemPosIdPair objects to LemPos objects. */

	private static HashMap lemPosMap = new HashMap();

	/**	Corpus tag. E.g., "sha". */

	private static String corpusTag;

	/**	Work tag. E.g., "ham". */

	private static String workTag;

	/**	Full work tag. E.g., "sha-ham". */

	private static String fullWorkTag;

	/**	The corpus. */

	private static Corpus corpus;

	/**	Character set. */

	private static byte charset;

	/**	Part of speech type. */

	private static byte posType;

	/**	The work. */

	private static Work work;

	/**	Number of parts created. */

	private static int numParts;

	/**	Maximum word path length in corpus. */

	private static int maxWordPathLength;

	/**	Current word ordinal in work. */

	private static int wordOrdinalInWork;

	/**	Current work part ordinal in work. */

	private static int partOrdinalInWork;

	/**	Map from speaker tags to speaker objects. */

	private static HashMap speakerMap;

	/**	Set of speaker tags for which we have no gender/mortality data. */

	private static HashSet missingSpeakerData;

	/**	Set of speaker tags used in speeches. */

	private static HashSet usedSpeakerTags;

	/**	Number of works built. */

	private static int numWorks;

	/**	Path to temporary directory. */

	private static String tempDirPath;

	/**	Map from "rend" attribute values to style masks. */

	private static HashMap rendMap = new HashMap();

	static {
		rendMap.put("bold", new Integer(TextRun.BOLD));
		rendMap.put("italic", new Integer(TextRun.ITALIC));
		rendMap.put("extended", new Integer(TextRun.EXTENDED));
		rendMap.put("sperrtext", new Integer(TextRun.EXTENDED));
		rendMap.put("underline", new Integer(TextRun.UNDERLINE));
		rendMap.put("overline", new Integer(TextRun.OVERLINE));
		rendMap.put("macron", new Integer(TextRun.OVERLINE));
		rendMap.put("superscript", new Integer(TextRun.SUPERSCRIPT));
		rendMap.put("subscript", new Integer(TextRun.SUBSCRIPT));
		rendMap.put("monospaced", new Integer(TextRun.MONOSPACED));
	}

	/**	Parses command line arguments.
	 *
	 *	@param	args		Command line arguments.
	 */

	private static void parseArgs (String[] args) {
		int n = args.length;
		if (n < 4 || n > 6) {
			System.out.println("Usage: BuildWorks in db username password [spellings] [debug]");
			System.exit(1);
		}
		inPath = args[0];
		dbname = args[1];
		username = args[2];
		password = args[3];
		if (n == 5) {
			String arg2 = args[4];
			if (arg2.equals("debug")) {
				debug = true;
			} else {
				spellingPath = arg2;
			}
		} else if (n == 6) {
			spellingPath = args[4];
			if (!args[5].equals("debug")) {
				System.out.println("Usage: BuildWorks in db username password [spellings] [debug]");
				System.exit(1);
			}
			debug = true;
		}
	}

	/**	Gets the corpus.
	 *
	 *	@param	corpusAttr	Corpus attribute on wordHoardHeader element.
	 *
	 *	@throws Exception
	 */

	private static void getCorpus (String corpusAttr)
		throws Exception
	{
		StringTokenizer tok = new StringTokenizer(corpusAttr, "|");
		while (tok.hasMoreTokens()) {
			String tag = tok.nextToken();
			corpus = pm.getCorpusByTag(tag);
			if (corpus == null) continue;
			corpusTag = tag;
			charset = corpus.getCharset();
			posType = corpus.getPosType();
			maxWordPathLength = corpus.getMaxWordPathLength();
			return;
		}
		BuildUtils.emsg("Corpus " + corpusAttr + " does not exist");
	}

	/**	Gets the authors.
	 *
	 *	@throws	Exception
	 */

	private static void getAuthors ()
		throws Exception
	{
		Element titleStmtEl = DOMUtils.getDescendant(document,
			"WordHoardText/teiHeader/fileDesc/titleStmt");
		ArrayList authorEls =
			DOMUtils.getChildren(titleStmtEl, "author");
		int numAuthors = 0;
		for (Iterator it = authorEls.iterator(); it.hasNext(); ) {
			Element authorEl = (Element)it.next();
			String name = DOMUtils.getText(authorEl);
			Author author = pm.getAuthorByName(name);
			if (author == null) {
				BuildUtils.emsg("Author " + name + " does not exist");
				continue;
			}
			author.addWork(work);
			work.addAuthor(author);
			numAuthors++;
		}
		if (numAuthors == 0)
			BuildUtils.emsg("No valid authors were defined in " +
				"WordHoardText/teiHeader/fileDesc/titleStmt/author " +
				"elements");
	}

	/**	Deletes an old copy of the work.
	 *
	 *	<p>The old objects for the work (the work and word parts) are
	 *	not actually deleted from the database. They are only unlinked
	 *	so that they become "dead" objects.
	 *
	 *	@throws Exception
	 */

	private static void deleteOldWork ()
		throws Exception
	{
		// Unlink the work from its corpus and authors.

		pm.begin();
		corpus.removeWork(work);
		Collection authors = new ArrayList(work.getAuthors());
		for (Iterator it = authors.iterator(); it.hasNext(); ) {
			Author author = (Author)it.next();
			work.removeAuthor(author);
			author.removeWork(work);
		}
		pm.commit();
		if (debug) return;

		// Unlink all the word parts for the work.

		Connection c = pm.getConnection();
		PreparedStatement p1 = c.prepareStatement(
			"select id from workpart where work=?");
		PreparedStatement p2 = c.prepareStatement(
			"update wordpart set tag=null, word=null, workPart=null " +
			"where workpart=?");
		p1.setLong(1, work.getId().longValue());
		ResultSet r1 = p1.executeQuery();
		while (r1.next()) {
			long partId = r1.getLong(1);
			p2.setLong(1, partId);
			p2.executeUpdate();
		}
		p1.close();
		p2.close();
	}

	/**	Creates the work.
	 *
	 *	@throws	Exeption
	 */

	private static void createWork ()
		throws Exception
	{
		Element titleStmtEl = DOMUtils.getDescendant(document,
			"WordHoardText/teiHeader/fileDesc/titleStmt");
		if (titleStmtEl == null) {
			BuildUtils.emsg("Missing required " +
				"WordHoardText/teiHeader/fileDesc/titleStmt " +
				"element");
			return;
		}
		Element fullTitleEl =
			DOMUtils.getChild(titleStmtEl, "title");
		if (fullTitleEl == null) {
			BuildUtils.emsg("Missing required " +
				"WordHoardText/teiHeader/fileDesc/titleStmt/title " +
				"element");
			return;
		}
		String fullTitle = DOMUtils.getText(fullTitleEl);
		if (fullTitle.length() > MAX_TITLE_LEN)
			fullTitle = fullTitle.substring(0, MAX_TITLE_LEN) + ELLIPSIS;
		String shortTitle = fullTitle;
		Element shortTitleEl =
			DOMUtils.getChild(titleStmtEl, "shortTitle");
		if (shortTitleEl != null) shortTitle = DOMUtils.getText(shortTitleEl);
		if (shortTitle != null & shortTitle.length() > MAX_TITLE_LEN)
			shortTitle = shortTitle.substring(0, MAX_TITLE_LEN) + ELLIPSIS;

		Element headerEl = DOMUtils.getDescendant(document,
			"WordHoardText/wordHoardHeader");

		Element pubDateEl = DOMUtils.getChild(headerEl, "pubDate");
		PubYearRange pubDate = null;
		if (pubDateEl != null) {
			String pubDateStr = DOMUtils.getText(pubDateEl);
			String earlyDateStr = pubDateStr;
			String lateDateStr = pubDateStr;
			int k = pubDateStr.indexOf('-');
			if (k > 0) {
				earlyDateStr = pubDateStr.substring(0, k);
				lateDateStr = pubDateStr.substring(k+1);
			}
			int earlyDate = 0;
			int lateDate = 0;
			try {
				earlyDate = Integer.parseInt(earlyDateStr);
				lateDate = Integer.parseInt(lateDateStr);
				if (earlyDate > lateDate) throw new NumberFormatException();
			} catch (NumberFormatException e) {
				BuildUtils.emsg("Invalid publication date: " + pubDateStr);
			}
			pubDate = new PubYearRange(new Integer(earlyDate),
				new Integer(lateDate));
		}

		Element taggingDataEl = DOMUtils.getChild(headerEl, "taggingData");
		if (taggingDataEl == null) {
			BuildUtils.emsg("Missing required " +
				"WordHoardText/teiHeader/taggingData " +
				"element");
			return;
		}
		long taggingDataFlags =
			BuildUtils.getTaggingDataFlags(taggingDataEl);

		work = corpus.getWorkByTag(fullWorkTag);
		if (work != null) deleteOldWork();

		work = new Work();
		work.setId(new Long(workPartId++));
		work.setTag(fullWorkTag);
		work.setPathTag(workTag);
		work.setFullTitle(fullTitle);
		work.setShortTitle(shortTitle);
		work.setTaggingData(new TaggingData(taggingDataFlags));
		work.setWork(work);
		work.setWorkOrdinal(partOrdinalInWork++);
		work.setPubDate(pubDate);

		numParts++;

		pm.begin();
		pm.save(work);
		corpus.addWork(work);
		getAuthors();
		pm.commit();
	}

	/**	Creates the title page.
	 *
	 *	@param	context		Parsing context.
	 *
	 *	@throws	Exception
	 */

	private static void createTitlePage (Context context)
		throws Exception
	{
		context = (Context)context.clone();

		System.out.println("      Title");

		context.startNewText(false);

		Element titleStmtEl = DOMUtils.getDescendant(document,
			"WordHoardText/teiHeader/fileDesc/titleStmt");
		Element fullTitleEl = DOMUtils.getChild(titleStmtEl, "title");
		String fullTitle = DOMUtils.getText(fullTitleEl);

		context.setJustification(TextLine.CENTER);

		context.startLine();
		context.setBold(true);
		context.setSize(TextParams.WORK_TITLE_FONT_SIZE);
		context.appendText(fullTitle);
		context.endLine();
		context.setSize(TextParams.NOMINAL_FONT_SIZE);

		context.appendBlankLine();
		Collection authors = work.getAuthors();
		for (Iterator it = authors.iterator(); it.hasNext(); ) {
			Author author = (Author)it.next();
			context.startLine();
			context.appendText(author.getName().getString());
			context.endLine();
		}
		context.setBold(false);

		ArrayList respStmtList =
			DOMUtils.getChildren(titleStmtEl, "respStmt");
		for (Iterator it = respStmtList.iterator(); it.hasNext(); ) {
			Element respStmtEl = (Element)it.next();
			String name = "";
			String resp = "";
			Element nameEl = DOMUtils.getChild(respStmtEl, "name");
			if (nameEl == null) {
				BuildUtils.emsg("Missing name child in respStmt element");
			} else {
				name = DOMUtils.getText(nameEl);
			}
			Element respEl = DOMUtils.getChild(respStmtEl, "resp");
			if (respEl == null) {
				BuildUtils.emsg("Missing resp child in respStmt element");
			} else {
				resp = DOMUtils.getText(respEl);
			}
			context.appendBlankLine();
			context.startLine();
			context.setSize(TextParams.RESP_NAME_FONT_SIZE);
			context.appendText(name);
			context.endLine();
			context.startLine();
			context.setSize(TextParams.RESP_RESP_FONT_SIZE);
			context.appendText(resp);
			context.endLine();
		}

		Element publicationStmtEl = DOMUtils.getDescendant(document,
			"WordHoardText/teiHeader/fileDesc/publicationStmt");
		if (publicationStmtEl != null) {
			ArrayList pList = DOMUtils.getChildren(publicationStmtEl, "p");
			for (Iterator it = pList.iterator(); it.hasNext(); ) {
				Element pEl = (Element)it.next();
				context.appendBlankLine();
				context.setSize(TextParams.PUB_STMT_FONT_SIZE);
				processParagraph(context, pEl);
			}
		}

		TextWrapper textWrapper = context.finalizeText();

		WorkPart titlePart = new WorkPart();
		titlePart.setId(new Long(workPartId++));
		titlePart.setTag(fullWorkTag + "-title");
		titlePart.setShortTitle("Title");
		titlePart.setFullTitle("Title");
		titlePart.setTaggingData(new TaggingData(0));
		titlePart.setWork(work);
		titlePart.setPrimaryText(textWrapper);
		titlePart.setWorkOrdinal(partOrdinalInWork++);
		titlePart.setHasStanzaNumbers(false);

		numParts++;

		pm.begin();
		work.addChild(titlePart);
		pm.save(textWrapper);
		pm.save(titlePart);
		pm.commit();
	}

	/**	Creates the speaker objects.
	 *
	 *	@throws	Exception
	 */

	private static void createSpeakers ()
		throws Exception
	{
		Element frontEl = DOMUtils.getDescendant(document,
			"WordHoardText/text/front");
		if (frontEl == null) return;

		ArrayList divList = DOMUtils.getChildren(frontEl, "div",
			"type", "castList");

		ArrayList speakerList = new ArrayList();
		speakerMap = new HashMap();

		for (Iterator divIt = divList.iterator(); divIt.hasNext(); ) {

			Element divEl = (Element)divIt.next();
			NodeList castItemList = divEl.getElementsByTagName("castItem");

			for (int i = 0; i < castItemList.getLength(); i++) {

				Element castItemEl = (Element)castItemList.item(i);
				String castItemType = castItemEl.getAttribute("type");
				boolean roleType = castItemType.equals("role");
				boolean listType = castItemType.equals("list");
				if (!roleType && !listType) {
					BuildUtils.emsg("Invalid cast item type: " + castItemType);
					continue;
				}
				Element roleDescEl = DOMUtils.getChild(castItemEl, "roleDesc");
				String description = roleDescEl == null ? null :
					DOMUtils.getText(roleDescEl);
				if (description != null && description.length() == 0)
					description = null;

				ArrayList roleElList = DOMUtils.getChildren(castItemEl, "role");
				for (Iterator it = roleElList.iterator(); it.hasNext(); ) {

					Element roleEl = (Element)it.next();
					String tag = roleEl.getAttribute("id").trim();
					if (tag.length() == 0) continue;
					String name = DOMUtils.getText(roleEl);

					if (name == null || name.length() == 0) {
						name = tag;
						BuildUtils.emsg("Missing name for speaker " + tag +
							", name set to: " + tag);
					}

					Speaker speaker = new Speaker();
					speaker.setWork(work);
					speaker.setTag(tag);
					speaker.setName(name);
					speaker.setDescription(description);

					String genderStr = roleEl.getAttribute("gender");
					if (genderStr.equals("uncertainMixedOrUnknown")) {
						speaker.setGender(Gender.UNCERTAIN_MIXED_OR_UNKNOWN);
					} else if (genderStr.equals("male")) {
						speaker.setGender(Gender.MALE);
					} else if (genderStr.equals("female")) {
						speaker.setGender(Gender.FEMALE);
					} else if (genderStr.length() == 0) {
						missingSpeakerData.add(tag);
					} else {
						BuildUtils.emsg("Invalid gender for speaker: " + tag);
					}

					String mortalityStr = roleEl.getAttribute("mortality");
					if (mortalityStr.equals("mortal")) {
						speaker.setMortality(Mortality.MORTAL);
					} else if (mortalityStr.equals("immortalOrSupernatural")) {
						speaker.setMortality(Mortality.IMMORTAL_OR_SUPERNATURAL);
					} else if (mortalityStr.equals("unknownOrOther")) {
						speaker.setMortality(Mortality.UNKNOWN_OR_OTHER);
					} else if (mortalityStr.length() == 0) {
						missingSpeakerData.add(tag);
					} else {
						BuildUtils.emsg("Invalid mortality for speaker: " +
							tag);
					}

					String originalNameStr = roleEl.getAttribute("originalName");
					if (originalNameStr.length() > 0) {
						Spelling originalName = new Spelling(originalNameStr,
							charset);
						speaker.setOriginalName(originalName);
					}

					if (speakerMap.get(tag) == null) {
						speakerMap.put(tag, speaker);
					} else {
						BuildUtils.emsg("Duplicate speaker id: " + tag);
					}

					speakerList.add(speaker);

				}
			}

		}

		pm.begin();
		pm.save(speakerList);
		pm.commit();
	}

	/**	Processes a cast item.
	 *
	 *	@param	context		Parsing context.
	 *
	 *	@param	el			Cast item element.
	 *
	 *	@throws	Exception
	 */

	private static void processCastItem (Context context, Element el)
			throws Exception
	{
		context = (Context)context.clone();
		if (el.getAttribute("rend").equals("none")) return;
		String type = el.getAttribute("type");
		boolean isList = type.equals("list");
		ArrayList roleElList = DOMUtils.getChildren(el, "role");
		int numRole = roleElList.size();
		String role = null;
		if (!isList || numRole <= 1) {
			Element roleEl = DOMUtils.getChild(el, "role");
			if (roleEl != null) role = DOMUtils.getText(roleEl);
		}
		Element roleDescEl = DOMUtils.getChild(el, "roleDesc");
		String description = roleDescEl == null ? null :
			DOMUtils.getText(roleDescEl);
		if (isList && description == null)
			description = el.getAttribute("roleDesc");
		if (role == null && description == null) return;
		context.startLine();
		if (role == null) {
			context.appendText(description);
		} else if (description == null) {
			context.appendText(role);
		} else {
			context.appendText(role + ", ");
			context.setItalic(true);
			context.appendText(description);
		}
		context.endLine();
	}

	/**	Processes a cast group element.
	 *
	 *	@param	context		Parsing context.
	 *
	 *	@param	el			"castGroup" element.
	 *
	 *	@throws	Exception
	 */

	private static void processCastGroup (Context context, Element el)
		throws Exception
	{
		context = (Context)context.clone();
		if (el.getAttribute("rend").equals("none")) return;
		Element castGroupTitleEl = DOMUtils.getChild(el, "title");
		boolean indent = false;
		if (castGroupTitleEl != null) {
			String castGroupTitle = DOMUtils.getText(castGroupTitleEl);
			context.startLine();
			context.appendText(castGroupTitle);
			context.endLine();
			indent = true;
		}
		if (indent) context.indent(TextParams.CAST_INDENTATION);
		ArrayList groupChildren =
			DOMUtils.getChildren(el, "castItem");
		for (Iterator it = groupChildren.iterator(); it.hasNext(); ) {
			Element groupChildEl = (Element)it.next();
			processCastItem(context, groupChildEl);
		}
		if (indent) context.indent(-TextParams.CAST_INDENTATION);
	}

	/**	Processes a cast list element.
	 *
	 *	@param	context		Parsing context.
	 *
	 *	@param	el			"castList" element.
	 *
	 *	@throws	Exception
	 */

	private static void processCastList (Context context, Element el)
		throws Exception
	{
		context = (Context)context.clone();

		NodeList children = el.getChildNodes();
		int numChildren = children.getLength();
		for (int i = 0; i < numChildren; i++) {
			Node child = children.item(i);
			if (!(child instanceof Element)) continue;
			Element childEl = (Element)child;
			String name = childEl.getNodeName();
			if (name.equals("castItem")) {
				processCastItem(context, childEl);
			} else if (name.equals("castGroup")) {
				processCastGroup(context, childEl);
			} else {
				BuildUtils.emsg("Cast list child element " +
					name + " ignored");
			}
		}

	}

	/**	Creates a line object.
	 *
	 *	@param	context		Parsing context.
	 *
	 *	@aram	el			"p" or "wordHoardTaggedLine" element.
	 *
	 *	@return				Line object, or null.
	 *
	 *	@throws	Exception
	 */

	private static Line createLine (Context context, Element el)
		throws Exception
	{
		String tag = el.getAttribute("id");
		if (tag.length() == 0) return null;
		Line line = new Line();
		line.setId(new Long(lineId++));
		line.setTag(tag);

		String lineNumberAttrValue = el.getAttribute("n");
		int number = 0;
		if (lineNumberAttrValue.length() > 0) {
			try {
				number = Integer.parseInt(lineNumberAttrValue);
			} catch (NumberFormatException e) {
				BuildUtils.emsg("Error in line number: " +
					tag);
				return null;
			}
		}

		boolean stanzaNumbering = context.getStanzaNumbering();
		String stanzaNumber = context.getStanzaNumber();
		boolean firstLineOfStanza = context.getFirstLineOfStanza();

		String label = el.getAttribute("label");
		if (label.length() == 0) {
			label = lineNumberAttrValue;
			if (stanzaNumbering && stanzaNumber != null)
				label = stanzaNumber + "." + lineNumberAttrValue;
		}

		String stanzaLabel = firstLineOfStanza ? stanzaNumber : null;

		line.setNumber(number);
		line.setLabel(label);
		line.setStanzaLabel(stanzaLabel);
		line.setWorkPart(context.getWorkPart());

		return line;
	}

	/**	Issues a syntax error message for a lemma.
	 *
	 *	@param	wordTag			Word tag.
	 *
	 *	@param	lemmaTag		Lemma tag.
	 */

	private static void lemmaSyntaxErrorMessage (String wordTag, String lemmaTag) {
		BuildUtils.emsg("Syntax error in lemma attribute \"" + lemmaTag + "\"" +
			(wordTag == null ? "" : (" for word: " + wordTag)));
	}

	/**	Parses a lemma tag.
	 *
	 *	@param	wordTag			Word tag.
	 *
	 *	@param	lemmaTag		Lemma tag.
	 *
	 *	@param	charset			Character set.
	 *
	 *	@return					Lemma, or null if error.
	 */

	private static Lemma parseLemma (String wordTag, String lemmaTag, byte charset) {
		int len = lemmaTag.length();
		int i = lemmaTag.indexOf(' ');
		if (i <= 0) {
			lemmaSyntaxErrorMessage(wordTag, lemmaTag);
			return null;
		}
		String spelling = lemmaTag.substring(0, i);
		while (i < len && lemmaTag.charAt(i) == ' ') i++;
		if (i == len || lemmaTag.charAt(i) != '(') {
			lemmaSyntaxErrorMessage(wordTag, lemmaTag);
			return null;
		}
		int j = lemmaTag.indexOf(')', i);
		if (j < 0) {
			lemmaSyntaxErrorMessage(wordTag, lemmaTag);
			return null;
		}
		String wordClassTag = lemmaTag.substring(i+1, j);
		WordClass wordClass = (WordClass)wordClassMap.get(wordClassTag);
		if (wordClass == null) {
			BuildUtils.emsg("No such word class \"" + wordClassTag +
				"\" in lemma attribute \"" + lemmaTag + "\"" +
			(wordTag == null ? "" : (" for word: " + wordTag)));
			return null;
		}
		j++;
		int homonym = 0;
		if (j < len) {
			while (j < len && lemmaTag.charAt(j) == ' ') j++;
			if (j == len || lemmaTag.charAt(j) != '(') {
				lemmaSyntaxErrorMessage(wordTag, lemmaTag);
				return null;
			}
			int k = lemmaTag.indexOf(')', j);
			if (k < 0) {
				lemmaSyntaxErrorMessage(wordTag, lemmaTag);
				return null;
			}
			String homonymStr = lemmaTag.substring(j+1, k);
			try {
				homonym = Integer.parseInt(homonymStr);
			} catch (NumberFormatException e) {
				lemmaSyntaxErrorMessage(wordTag, lemmaTag);
				return null;
			}
			if (homonym <= 0 || k + 1 != len) {
				lemmaSyntaxErrorMessage(wordTag, lemmaTag);
				return null;
			}
		}
		Lemma lemma = new Lemma();
		lemma.setId(new Long(lemmaId++));
		lemma.setTag(new Spelling(lemmaTag, charset));
		String lemmaTagInsensitive =
			CharsetUtils.translateToInsensitive(lemmaTag);
		lemma.setTagInsensitive(new Spelling(lemmaTagInsensitive, charset));
		lemma.setSpelling(new Spelling(spelling, charset));
		lemma.setHomonym(homonym);
		lemma.setWordClass(wordClass);
		return lemma;
	}

	/**	Creates a lempos.
	 *
	 *	@param	lemmaTag		Lemma tag.
	 *
	 *	@param	posTag			Pos tag.
	 *
	 *	@param	spelling		Standard spelling or null.
	 *
	 *	@param	charset			Character set.
	 *
	 *	@param	posType			Pos type.
	 *
	 *	@param	wordTag			Word tag for error messages, or null.
	 *
	 *	@return					LemPos object for lemma/pos combination,
	 *							or null if error.
	 *
	 *	@throws Exception
	 */

	 private static LemPos createLemPos (String lemmaTag, String posTag,
	 	String spelling, byte charset, byte posType, String wordTag)
	 		throws Exception
	 {
	 	String lemmaTagLower = lemmaTag.toLowerCase();
		Lemma lemma = (Lemma)lemmaMap.get(lemmaTagLower);
		if (lemma == null) {
			lemma = parseLemma(wordTag, lemmaTag, charset);
			if (lemma == null) return null;
			lemma.export(lemmaTableExporterImporter);
			lemmaMap.put(lemmaTagLower, lemma);
		} else {
			if (!lemmaTag.equals(lemma.getTag().getString())) {
				BuildUtils.emsg("Lemma case mismatch: " + lemmaTag +
					(wordTag == null ? "" : (" for word: " + wordTag)));
			}
		}
		Pos pos = (Pos)posMap.get(posTag);
		if (pos == null) {
			BuildUtils.emsg("Invalid pos attribute " + posTag +
				(wordTag == null ? "" : (" for word: " + wordTag)));
			return null;
		}
		if (posType == Pos.ENGLISH && !lemma.getWordClass().equals(pos.getWordClass())) {
			BuildUtils.emsg("Incompatible word classes for lemma " + lemmaTag +
				" and pos " + posTag +
				(wordTag == null ? "" : (" for word: " + wordTag)));
			return null;
		}
		long lemmaId = lemma.getId().longValue();
		long posId = pos.getId().longValue();
		LemPosIdPair pair = new LemPosIdPair(lemmaId, posId);
		LemPos lemPos = (LemPos)lemPosMap.get(pair);
		if (lemPos == null) {
			lemPos = new LemPos();
			lemPos.setId(new Long(lemPosId++));
			lemPos.setLemma(lemma);
			lemPos.setPos(pos);
			if (spelling != null)
				lemPos.setStandardSpelling(new Spelling(spelling, charset));
			lemPos.export(lemPosTableExporterImporter);
			lemPosMap.put(pair, lemPos);
		}
		return lemPos;
	 }

	/**	Processes morphology word attributes.
	 *
	 *	@param	context		Parsing context.
	 *
	 *	@param	el			"w" element.
	 *
	 *	@param	word		Word being constructed.
	 *
	 *	@throws Exception
	 */

	private static void processMorphology (Context context, Element el,
		Word word)
			throws Exception
	{
		if (!context.getLemmaTagging()) return;
		String wordTag = word.getTag();
		if (wordTag.equals("untagged")) return;
		WorkPart workPart = context.getWorkPart();
		String lemmaAttrStr = el.getAttribute("lemma");
		StringTokenizer lemmaTok = new StringTokenizer(lemmaAttrStr, "|");
		int numParts = lemmaTok.countTokens();
		if (numParts == 0) {
			WorkPart part = context.getWorkPart();
			long flags = part.getTaggingData().getFlags();
			if ((flags & TaggingData.LEMMA) != 0)
				BuildUtils.emsg("Missing lemma attribute for word: " +
					wordTag);
			return;
		}
		String posAttrStr = el.getAttribute("pos");
		StringTokenizer posTok = new StringTokenizer(posAttrStr, "|");
		int numPos = posTok.countTokens();
		if (numPos == 0) {
			BuildUtils.emsg("Missing part of speech tag for word: " + wordTag);
			return;
		} else if (numPos != numParts) {
			BuildUtils.emsg("Number of lemmas and parts of " +
				"speech not equal for word: " + wordTag);
			return;
		}
		String bensonGlossAttrStr = el.getAttribute("bensonGloss");
		BensonLemPos bensonLemPos = null;
		if (bensonGlossAttrStr.length() > 0) {
			Long bensonLemPosId = null;
			try {
				bensonLemPosId = new Long(bensonGlossAttrStr);
			} catch (NumberFormatException e) {
				BuildUtils.emsg("Invalid bensonGloss attriubte " +
					bensonGlossAttrStr + " for word: " + wordTag);
				return;
			}
			bensonLemPos = new BensonLemPos();
			bensonLemPos.setId(bensonLemPosId);
		}
		for (int partIndex = 0; partIndex < numParts; partIndex++) {
			String lemmaTag = lemmaTok.nextToken();
			String posTag = posTok.nextToken();
			LemPos lemPos = createLemPos(lemmaTag, posTag, null,
				charset, posType, wordTag);
			if (lemPos == null) continue;
			WordPart wordPart = new WordPart();
			wordPart.setId(new Long(wordPartId++));
			wordPart.setTag(wordTag);
			wordPart.setPartIndex(partIndex);
			wordPart.setWord(word);
			wordPart.setWorkPart(workPart);
			wordPart.setLemPos(lemPos);
			wordPart.setBensonLemPos(bensonLemPos);
			wordPart.export(wordPartTableExporterImporter);
		}
	}

	/**	Processes a word element.
	 *
	 *	@param	context		Parsing context.
	 *
	 *	@aram	el			"w" element.
	 *
	 *	@throws Exception
	 */

	private static void processWord (Context context, Element el)
		throws Exception
	{
		Prosodic oldProsodic = context.getProsodic();

		String str = DOMUtils.getText(el);
		String tag = el.getAttribute("id");
		if (tag.length() == 0)
			BuildUtils.emsg("Missing id attribute in w element");

		Word word = context.appendWord(str);

		word.setId(new Long(wordId++));
		word.setTag(tag);
		word.setWorkTag(workTag);
		word.setWorkOrdinal(wordOrdinalInWork++);
		word.setSpeech(context.getSpeech());

		context.setProsodic(el);
		Prosodic prosodic = context.getProsodic();
		word.setProsodic(prosodic);
		if (prosodic != null && prosodic.getProsodic() == Prosodic.UNKNOWN) {
			WorkPart part = context.getWorkPart();
			long flags = part.getTaggingData().getFlags();
			if ((flags & TaggingData.PROSODIC) != 0)
				BuildUtils.emsg("Missing prosodic attribute for word: " +
					tag);
		}

		String metricalShape = el.getAttribute("metricalShape");
		if (metricalShape.length() > 0)
			word.setMetricalShape(metricalShape);

		processMorphology(context, el, word);

		context.addWord(word);

		context.setProsodic(oldProsodic);
	}

	/**	Processes a punctuation element.
	 *
	 *	@param	context		Parsing context.
	 *
	 *	@aram	el			"punc" element.
	 *
	 *	@throws Exception
	 */

	private static void processPunctuation (Context context, Element el)
		throws Exception
	{
		Node firstChild = el.getFirstChild();
		if (firstChild == null) return;
		String str = firstChild.getNodeValue();
		context.appendText(str);
	}

	/**	Processes a rend attribute.
	 *
	 *	@param	context		Parsing context.
	 *
	 *	@param	el			Element.
	 */

	private static void processRend (Context context, Element el) {
		String rend = el.getAttribute("rend");
		if (rend.equals("normal") || rend.equals("roman") ||
			rend.equals("plain"))
		{
			context.setNormal();
		} else {
			Integer maskInteger = (Integer)rendMap.get(rend);
			if (maskInteger == null) {
				BuildUtils.emsg("Invalid rend attribute: " + rend);
			} else {
				context.setStyle(maskInteger.intValue(), true);
			}
		}
	}

	/**	Processes an align attribute.
	 *
	 *	@param	context		Parsing context.
	 *
	 *	@param	el			Element.
	 */

	private static void processAlign (Context context, Element el) {
		String align = el.getAttribute("align");
		if (align.equals("left")) {
			context.setJustification(TextLine.LEFT);
		} else if (align.equals("center")) {
			context.setJustification(TextLine.CENTER);
		} else if (align.equals("right")) {
			context.setJustification(TextLine.RIGHT);
		} else {
			BuildUtils.emsg("Invalid align attribute: " + align);
		}
	}

	/**	Processes an indent attribute.
	 *
	 *	@param	context		Parsing context.
	 *
	 *	@param	el			Element.
	 *
	 *	@return				Indent value.
	 */

	private static int processIndent (Context context, Element el) {
		String indentStr = el.getAttribute("indent");
		if (indentStr == null || indentStr.length() == 0) return 0;
		try {
			int indent = Integer.parseInt(indentStr);
			context.indent(indent);
			return indent;
		} catch (NumberFormatException e) {
			BuildUtils.emsg("Illegal value for indent attribute: " +
				indentStr);
			return 0;
		}
	}

	/**	Processes an hi element.
	 *
	 *	@param	context		Parsing context.
	 *
	 *	@aram	el			"hi" or "title" element.
	 *
	 *	@throws Exception
	 */

	private static void processHi (Context context, Element el)
		throws Exception
	{
		//context = (Context)context.clone();
		int savedStyle = context.getStyle();
		boolean inTaggedLine = context.getInTaggedLine();
		String rend = el.getAttribute("rend");
		if ((rend == null) || (rend.length()==0)) {
			el.setAttribute("rend", "italic");
        }

		processRend(context, el);

		NodeList children = el.getChildNodes();
		int numChildren = children.getLength();
		for (int i = 0; i < numChildren; i++) {
			Node child = children.item(i);
			int type = child.getNodeType();
			if (type == Node.TEXT_NODE && !inTaggedLine) {
				String str = child.getNodeValue();
				context.appendText(str);
			} else if (type == Node.ELEMENT_NODE) {
				el = (Element)child;
				String name = el.getNodeName();
				if (name.equals("w") && inTaggedLine) {
					processWord(context, el);
				} else if (name.equals("punc") && inTaggedLine ) {
					processPunctuation(context, el);
				} else if (name.equals("hi")) {
					processHi(context, el);
				} else if (name.equals("title")) {
					processTitle(context, el);
				} else if (name.equals("stage") && inTaggedLine) {
					context.endLine();
					processStage(context, el);
					context.startLine();
				} else {
					BuildUtils.emsg("Hi child element " +
						name + " ignored");
				}
			}
		}
		context.flushRun();
		context.setStyle(savedStyle);
	}

	/**	Processes a title element.
	 *
	 *	@param	context		Parsing context.
	 *
	 *	@aram	el			"title" element.
	 *
	 *	@throws Exception
	 */

	private static void processTitle (Context context, Element el)
		throws Exception
	{
		el.setAttribute("rend", "italic");
		processHi(context, el);
	}

	/**	Processes a tagged line.
	 *
	 *	@param	context		Parsing context.
	 *
	 *	@param	el			"wordHoardTaggedLine" element.
	 *
	 *	@throws Exception
	 */

	private static void processTaggedLine (Context context, Element el)
			throws Exception
	{
		context = (Context)context.clone();

		//	Create the new line object.

		Line line = createLine(context, el);

		//	Adjust the context.

		String rendAttr = el.getAttribute("rend");
		if (!rendAttr.equals("")) processRend(context, el);

		String alignAttr = el.getAttribute("align");
		if (!alignAttr.equals("")) processAlign(context, el);

		processIndent(context, el);

		context.setLine(line);
		context.setInTaggedLine(true);
		context.startLine();

		context.setProsodic(el);

		//	Process the line's child nodes.

		NodeList children = el.getChildNodes();
		int numChildren = children.getLength();
		for (int i = 0; i < numChildren; i++) {
			Node child = children.item(i);
			int type = child.getNodeType();
			if (type == Node.TEXT_NODE) {
				continue;
			} else if (type == Node.ELEMENT_NODE) {
				el = (Element)child;
				String name = el.getNodeName();
				if (name.equals("w")) {
					processWord(context, el);
				} else if (name.equals("punc")) {
					processPunctuation(context, el);
				} else if (name.equals("hi")) {
					processHi(context, el);
				} else if (name.equals("title")) {
					processTitle(context, el);
				} else if (name.equals("stage")) {
					context.endLine();
					processStage(context, el);
					context.startLine();
				} else {
					BuildUtils.emsg("Tagged line child element " +
						name + " ignored");
				}
			}
		}

		context.endLine();
		context.setLineLocation();

	}

	/**	Processes a p element.
	 *
	 *	@param	context		Parsing context.
	 *
	 *	@param	el			"p" or "head".
	 *
	 *	@throws Exception
	 */

	private static void processParagraph (Context context, Element el)
			throws Exception
	{
		context = (Context)context.clone();

		//	Create the new line object.

		Line line = createLine(context, el);

		//	Adjust the context.

		String rendAttr = el.getAttribute("rend");
		if (!rendAttr.equals("")) processRend(context, el);

		String alignAttr = el.getAttribute("align");
		if (!alignAttr.equals("")) processAlign(context, el);

		processIndent(context, el);

		context.setLine(line);
		context.setInTaggedLine(false);
		context.startLine();

		//	Process the line's child nodes.

		NodeList children = el.getChildNodes();
		int numChildren = children.getLength();
		for (int i = 0; i < numChildren; i++) {
			Node child = children.item(i);
			int type = child.getNodeType();
			if (type == Node.TEXT_NODE) {
				String str = child.getNodeValue();
				context.appendText(str);
			} else if (type == Node.ELEMENT_NODE) {
				el = (Element)child;
				String name = el.getNodeName();
				if (name.equals("hi")) {
					processHi(context, el);
				} else if (name.equals("title")) {
					processTitle(context, el);
				} else {
					BuildUtils.emsg("Paragraph child element " +
						name + " ignored");
				}
			}
		}

		context.endLine();
		context.setLineLocation();

	}

	/**	Processes a heading.
	 *
	 *	@param	context		Parsing context.
	 *
	 *	@param	el			"head" element.
	 *
	 *	throws Exception
	 */

	private static void processHead (Context context, Element el)
		throws Exception
	{
		context = (Context)context.clone();
		context.appendBlankLine();
		context.setJustification(TextLine.CENTER);
		context.setBold(true);
		processParagraph(context, el);
		context.appendBlankLine();
	}

	/**	Processes a stage direction.
	 *
	 *	@param	context		Parsing context.
	 *
	 *	@param	el			"stage" element.
	 *
	 *	throws Exception
	 */

	private static void processStage (Context context, Element el)
		throws Exception
	{
		context = (Context)context.clone();
		String str = DOMUtils.getText(el);
		if (str.length() == 0) return;
		context.appendBlankLine();
		context.setJustification(TextLine.CENTER);
		context.startLine();
		context.setItalic(true);
		context.appendText(str);
		context.endLine();
		context.appendBlankLine();
	}

	/**	Processes a line group.
	 *
	 *	@param	context		Parsing context.
	 *
	 *	@param	el			"lg" element.
	 *
	 *	throws Exception
	 */

	private static void processLineGroup (Context context, Element el)
		throws Exception
	{
		context = (Context)context.clone();

		String type = el.getAttribute("type");
		boolean isStanza = type.equals("stanza");
		context.setStanzaNumber(isStanza ? el.getAttribute("n") : null);

		String rend = el.getAttribute("rend");
		boolean spenserIndentation = isStanza &&
			rend.equals("spenser-indentation");

		if (isStanza) context.appendBlankLine();

		int numLineChildren = 0;
		NodeList children = el.getChildNodes();
		int numChildren = children.getLength();
		for (int i = 0; i < numChildren; i++) {
			Node child = children.item(i);
			if (!(child instanceof Element)) continue;
			el = (Element)child;
			String name = el.getNodeName();
			if (name.equals("p") || name.equals("wordHoardTaggedLine"))
				numLineChildren++;
		}

		int lineChildIndex = 0;
		for (int i = 0; i < numChildren; i++) {
			Node child = children.item(i);
			if (!(child instanceof Element)) continue;
			el = (Element)child;
			String name = el.getNodeName();
			if (name.equals("p") || name.equals("wordHoardTaggedLine")) {
				lineChildIndex++;
				boolean firstLine = lineChildIndex == 1;
				boolean lastLine = lineChildIndex == numLineChildren;
				context.setFirstLineOfStanza(firstLine);
				if (spenserIndentation) {
					if (lineChildIndex == 2)
						context.indent(TextParams.SPENSER_INDENTATION);
					if (lastLine)
						context.indent(-TextParams.SPENSER_INDENTATION);
				}
				if (name.equals("p")) {
					processParagraph(context, el);
				} else {
					processTaggedLine(context, el);
				}
			} else if (name.equals("head")) {
				processHead(context, el);
			} else if (name.equals("lg")) {
				processLineGroup(context, el);
			} else if (name.equals("stage")) {
				processStage(context, el);
			} else {
				BuildUtils.emsg("Line group child element " +
					name + " ignored");
			}
		}

		if (isStanza) context.appendBlankLine();
	}

	/**	Gets the tag of the first line of a speech.
	 *
	 *	@param	el		"sp" element.
	 *
	 *	@return			Tag of first line.
	 */

	private static String getFirstLineTagOfSpeech (Element el) {
		NodeList lineNodeList =
			el.getElementsByTagName("wordHoardTaggedLine");
		String tag = "";
		if (lineNodeList.getLength() > 0) {
			Element lineEl = (Element)lineNodeList.item(0);
			tag = lineEl.getAttribute("id");
		}
		return tag;
	}

	/**	Processes a speech.
	 *
	 *	@param	context		Parsing context.
	 *
	 *	@param	el			"sp" element.
	 *
	 *	throws Exception
	 */

	private static void processSpeech (Context context, Element el)
		throws Exception
	{
		context = (Context)context.clone();

		Element speakerEl = DOMUtils.getChild(el, "speaker");

		String rendAttr = el.getAttribute("rend");
		boolean rendNone = rendAttr.equals("none");
		boolean rendIndent = rendAttr.equals("indent");

		if (rendNone || rendIndent) {
			if (speakerEl != null)
				BuildUtils.emsg("Speech with rend = none or indent has speaker: " +
					getFirstLineTagOfSpeech(el));
		} else {
			if (speakerEl == null) {
				BuildUtils.emsg("Speech with no rend attribute has no speaker: " +
					getFirstLineTagOfSpeech(el));
			} else {
				String speaker = DOMUtils.getText(speakerEl);
				context.appendBlankLine();
				context.startLine();
				context.appendText(speaker);
				context.endLine();
				context.appendBlankLine();
			}
		}

		Speech speech = new Speech();
		speech.setId(new Long(speechId++));
		context.setSpeech(speech);
		if (!rendNone) context.indent(TextParams.SPEECH_INDENTATION);
		speech.setWorkPart(context.getWorkPart());
		context.addSpeech(speech);
		String who = el.getAttribute("who");
		if (who.length() == 0) {
			BuildUtils.emsg("Speech has no who element: " +
				getFirstLineTagOfSpeech(el));
		} else {
			StringTokenizer tok = new StringTokenizer(who, " ");
			while (tok.hasMoreTokens()) {
				String tag = tok.nextToken();
				Speaker speaker = null;
				if (speakerMap != null) {
					speaker = (Speaker)speakerMap.get(tag);
				}
				if (speaker == null) {
					BuildUtils.emsg("Speech with invalid who attribute: " +
						who);
				} else {
					speech.addSpeaker(speaker);
					usedSpeakerTags.add(tag);
				}
			}
		}
		NodeList children = el.getChildNodes();
		int numChildren = children.getLength();
		for (int i = 0; i < numChildren; i++) {
			Node child = children.item(i);
			if (!(child instanceof Element)) continue;
			el = (Element)child;
			String name = el.getNodeName();
			if (name.equals("wordHoardTaggedLine")) {
				processTaggedLine(context, el);
			} else if (name.equals("p")) {
				processParagraph(context, el);
			} else if (name.equals("head")) {
				processHead(context, el);
			} else if (name.equals("stage")) {
				processStage(context, el);
			} else if (name.equals("lg")) {
				processLineGroup(context, el);
			} else if (name.equals("speaker")) {
			} else {
				BuildUtils.emsg("Speech child element " +
					name + " ignored");
			}
		}
	}

	/**	Processes a div element.
	 *
	 *	@param	context			Parsing context.
	 *
	 *	@param	el				"div" element.
	 *
	 *	throws Exception
	 */

	private static void processDiv (Context context, Element el)
		throws Exception
	{
		if (el.getAttribute("type").equals("castList") &&
			el.getAttribute("rend").equals("none")) return;

		context = (Context)context.clone();
		int partLevel = context.getPartLevel();

		WorkPart parentPart = context.getWorkPart();

		String tag = el.getAttribute("id");
		if (tag.length() == 0) {
			BuildUtils.emsg("Missing id on div element");
			return;
		}

		String numberingStyle = el.getAttribute("numberingStyle");
		context.setStanzaNumbering(numberingStyle.equals("stanza"));

		Element headerEl = DOMUtils.getChild(el, "wordHoardHeader");
		if (headerEl == null) {
			BuildUtils.emsg("Missing required wordHoardHeader child of " +
				"body div element");
			return;
		}

		Element titleEl = DOMUtils.getChild(headerEl, "title");
		String shortTitle = titleEl == null ? null : DOMUtils.getText(titleEl);
		int shortTitleLen = shortTitle.length();
		if (shortTitle == null || shortTitleLen == 0) {
			BuildUtils.emsg("Missing title");
			shortTitle = "";
		} else if (shortTitleLen > MAX_TITLE_LEN) {
			shortTitle = shortTitle.substring(0, MAX_TITLE_LEN) + ELLIPSIS;
		}

		Element fullTitleEl = DOMUtils.getChild(headerEl, "fullTitle");
		String fullTitle = fullTitleEl == null ? null :
			DOMUtils.getText(fullTitleEl);
		if (fullTitle == null || fullTitle.length() == 0) {
			fullTitle = shortTitle;
			WorkPart ancestor = parentPart;
			for (int i = partLevel; i > 1; i--) {
				fullTitle = ancestor.getShortTitle() + ", " + fullTitle;
				ancestor = ancestor.getParent();
			}
		}
		int fullTitleLen = fullTitle.length();
		if (fullTitleLen > MAX_TITLE_LEN)
			fullTitle = fullTitle.substring(0, MAX_TITLE_LEN) + ELLIPSIS;

		Element pathTagEl = DOMUtils.getChild(headerEl, "pathTag");
		String pathTag = pathTagEl == null ? null :
			DOMUtils.getText(pathTagEl);

		for (int i = 0; i <= partLevel; i++)
			System.out.print("   ");
		System.out.println(shortTitle);

		Element taggingDataEl = DOMUtils.getChild(headerEl, "taggingData");
		if (taggingDataEl == null) {
			BuildUtils.emsg("Missing required taggingData child of " +
				"wordHoardHeader child of body div element");
			return;
		}
		long partTaggingDataFlags =
			BuildUtils.getTaggingDataFlags(taggingDataEl);
		context.setLemmaTagging((partTaggingDataFlags & TaggingData.LEMMA) == 1);

		WorkPart part = new WorkPart();
		part.setId(new Long(workPartId++));
		part.setTag(fullWorkTag + "-" + tag);
		part.setPathTag(pathTag);
		part.setShortTitle(shortTitle);
		part.setFullTitle(fullTitle);
		part.setTaggingData(new TaggingData(partTaggingDataFlags));
		part.setWork(work);
		part.setWorkOrdinal(partOrdinalInWork++);
		part.setHasStanzaNumbers(context.getStanzaNumbering());

		context.startWorkPart(part, headerEl);

		numParts++;

		int indent = processIndent(context, el);

		NodeList children = el.getChildNodes();
		int numChildren = children.getLength();
		for (int i = 0; i < numChildren; i++) {
			Node child = children.item(i);
			if (!(child instanceof Element)) continue;
			Element childEl = (Element)child;
			String name = childEl.getNodeName();
			if (name.equals("lg")) {
				processLineGroup(context, childEl);
			} else if (name.equals("sp")) {
				processSpeech(context, childEl);
			} else if (name.equals("wordHoardTaggedLine")) {
				processTaggedLine(context, childEl);
			} else if (name.equals("p")) {
				processParagraph(context, childEl);
			} else if (name.equals("head")) {
				processHead(context, childEl);
			} else if (name.equals("stage")) {
				processStage(context, childEl);
			} else if (name.equals("castList")) {
				processCastList(context, childEl);
			} else if (name.equals("hi")) {
				processHi(context, childEl);
			} else if (name.equals("wordHoardHeader")) {
			} else if (name.equals("div")) {
			} else {
				BuildUtils.emsg("Div child element " +
					name + " ignored");
			}
		}

		context.indent(-indent);

		ArrayList wordList = context.getWordList();
		ArrayList lineList = context.getLineList();
		ArrayList speechList = context.getSpeechList();

		TextWrapper textWrapper = null;

		boolean partHasText = DOMUtils.nodeHasDescendant(el,
			new String[]{"wordHoardTaggedLine", "p", "head", "castItem"},
			new String[]{"div"});
		if (partHasText) {
			textWrapper = context.finalizeText();
			part.setPrimaryText(textWrapper);
		}

		if ((partTaggingDataFlags & TaggingData.LEMMA) == 0) {
			wordList.clear();
			lineList.clear();
			speechList.clear();
		}

		pm.begin();
		parentPart.addChild(part);
		if (textWrapper != null) pm.save(textWrapper);
		pm.save(part);
		pm.commit();

		Word prev = null;
		for (Iterator it = wordList.iterator(); it.hasNext(); ) {
			Word word = (Word)it.next();
			word.setPrev(prev);
			if (prev != null) prev.setNext(word);
			prev = word;
			word.initDerivedValues();
			maxWordPathLength = Math.max(maxWordPathLength,
				word.getPath().length());
		}

		if (!debug) {
			for (Iterator it = wordList.iterator(); it.hasNext(); ) {
				Word word = (Word)it.next();
				word.export(wordTableExporterImporter);
			}
			for (Iterator it = lineList.iterator(); it.hasNext(); ) {
				Line line = (Line)it.next();
				line.export(lineTableExporterImporter);
			}
			for (Iterator it = speechList.iterator(); it.hasNext(); ) {
				Speech speech = (Speech)it.next();
				speech.export(speechTableExporterImporter);
				Long speechId = speech.getId();
				Set speakers = speech.getSpeakers();
				for (Iterator it2 = speakers.iterator(); it2.hasNext(); ) {
					Speaker speaker = (Speaker)it2.next();
					Long speakerId = speaker.getId();
					speechSpeakersTableExporterImporter.print(speechId);
					speechSpeakersTableExporterImporter.print(speakerId);
					speechSpeakersTableExporterImporter.println();
				}
			}
		}

		ArrayList divChildren = DOMUtils.getChildren(el, "div");
		for (Iterator it = divChildren.iterator(); it.hasNext(); ) {
			Element divEl = (Element)it.next();
			processDiv(context, divEl);
		}

		setPartCounters(part, lineList.size(), wordList.size());

	}

	/**	Reports missing speaker data.
	 */

	private static void reportMissingSpeakerData () {
		missingSpeakerData.retainAll(usedSpeakerTags);
		for (Iterator it = missingSpeakerData.iterator(); it.hasNext(); )
			BuildUtils.emsg("Missing gender/mortality data for speaker: " +
				it.next());
	}

	/**	Sets the line and word counters for a work part.
	 *
	 *	@param	part		Work part.
	 *
	 *	@param	numLines	Number of lines in work part proper (not including
	 *						descendants).
	 *
	 *	@param	numWords	Number of words in work part proper (not including
	 *						descendants).
	 *
	 *	@throws	Exception	general error.
	 */

	public static void setPartCounters (WorkPart part, int numLines, int numWords)
		throws Exception
	{
		for (Iterator it = part.getChildren().iterator(); it.hasNext(); ) {
			WorkPart child = (WorkPart)it.next();
			numLines += child.getNumLines();
			numWords += child.getNumWords();
		}
		pm.begin();
		part.setNumLines(numLines);
		part.setNumWords(numWords);
		pm.commit();
	}

	/**	Sets the corpus counters.
	 *
	 *	@throws	Exception	general error.
	 */

	public static void setCorpusCounters ()
		throws Exception
	{
		int numWorkParts = 0;
		int numLines = 0;
		int numWords = 0;
		Collection works = corpus.getWorks();
		for (Iterator it = works.iterator(); it.hasNext(); ) {
			Work work = (Work)it.next();
			numWorkParts += work.getNumWorkPartsTree();
			numLines += work.getNumLines();
			numWords += work.getNumWords();
		}
		pm.begin();
		corpus.setNumWorkParts(numWorkParts);
		corpus.setNumLines(numLines);
		corpus.setNumWords(numWords);
		corpus.setMaxWordPathLength(maxWordPathLength);
		pm.commit();
	}

	/**	Checks the XML document for required top-level elements.
	 *
	 *	@return		True if all required top-level elements present.
	 *
	 *	@throws	Exception
	 */

	private static boolean checkRequiredTopLevelElements ()
		throws Exception
	{
		Element rootEl = DOMUtils.getChild(document,
			"WordHoardText");
		if (rootEl == null) {
			BuildUtils.emsg("Missing required WordHoardText root element");
			return false;
		}
		Element wordHoardHeaderEl = DOMUtils.getChild(
			rootEl, "wordHoardHeader");
		if (wordHoardHeaderEl == null) {
			BuildUtils.emsg("Missing required " +
				"WordHoardText/wordHoardHeader element");
			return false;
		}
		Element teiHeaderEl = DOMUtils.getChild(
			rootEl, "teiHeader");
		if (teiHeaderEl == null) {
			BuildUtils.emsg("Missing required " +
				"WordHoardText/teiHeader element");
			return false;
		}
		Element textEl = DOMUtils.getChild(
			rootEl, "text");
		if (textEl == null) {
			BuildUtils.emsg("Missing required WordHoardText/text element");
			return false;
		}
		return true;
	}

	/**	Builds a work.
	 *
	 *	@param	file		XML file for work.
	 *
	 *	@throws	Exception
	 */

	private static void buildWork (File file)
		throws Exception
	{

		long startTime = System.currentTimeMillis();

		//	Read the XML file into a DOM tree.

		document = DOMUtils.parse(file);

		//	Check for required top-level elements.

		if (!checkRequiredTopLevelElements()) return;

		// Initialize for parsing the DOM tree and populating the
		// object model.

		pm = new PersistenceManager();
		numParts = 0;
		wordOrdinalInWork = 0;
		partOrdinalInWork = 0;
		missingSpeakerData = new HashSet();
		usedSpeakerTags = new HashSet();

		//	Get the corpus and work tags.

		Element wordHoardHeaderEl = DOMUtils.getDescendant(document,
			"WordHoardText/wordHoardHeader");
		String corpusAttr = wordHoardHeaderEl.getAttribute("corpus");
		if (corpusAttr.length() == 0) {
			BuildUtils.emsg("Missing required corpus attribute in " +
				"WordHoardText/wordHoardHeader element");
			return;
		}
		getCorpus(corpusAttr);
		if (corpus == null) return;
		workTag = wordHoardHeaderEl.getAttribute("work");
		if (workTag.length() == 0) {
			BuildUtils.emsg("Missing required work attribute in " +
				"WordHoardText/wordHoardHeader element");
			return;
		}
		fullWorkTag = corpusTag + "-" + workTag;
		System.out.println();
		System.out.println("Building " + fullWorkTag + " from file " +
			file.getPath());

		//	Create the work.

		createWork();
		System.out.println("   " + work.getFullTitle());

		//	Create the speaker objects.

		createSpeakers();

		//	Create the initial parsing context.

		Context context = new Context();
		context.setCharset(charset);
		context.setWorkPart(work);
		context.setProsodic(wordHoardHeaderEl);

		//	Create the title page.

		createTitlePage(context);

		//	Create the work parts.

		Element frontEl = DOMUtils.getDescendant(document,
			"WordHoardText/text/front");
		if (frontEl != null) {
			ArrayList children = DOMUtils.getChildren(frontEl, "div");
			for (Iterator it = children.iterator(); it.hasNext(); ) {
				Element divEl = (Element)it.next();
				processDiv(context, divEl);
			}
		}

		Element bodyEl = DOMUtils.getDescendant(document,
			"WordHoardText/text/body");
		if (bodyEl != null) {
			ArrayList children = DOMUtils.getChildren(bodyEl, "div");
			for (Iterator it = children.iterator(); it.hasNext(); ) {
				Element divEl = (Element)it.next();
				processDiv(context, divEl);
			}
		}

		//	Report missing speaker data.

		reportMissingSpeakerData();

		//	Set the counts.

		System.out.println("   Setting counts");
		setPartCounters(work, 0, 0);
		setCorpusCounters();

		//	Print stats.

		System.out.println("   " +
			Formatters.formatIntegerWithCommas(numParts) + " part" +
			(numParts == 1 ? "" : "s"));
		System.out.println("   " +
			Formatters.formatIntegerWithCommas(work.getNumLines()) + " lines");
		System.out.println("   " +
			Formatters.formatIntegerWithCommas(work.getNumWords()) + " words");
		long endTime = System.currentTimeMillis();
		System.out.println("   " + fullWorkTag +
			" hoarded in " +
			BuildUtils.formatElapsedTime(startTime, endTime));

		//	Finish up.

		numWorks++;
		pm.close();

	}

	/**	Builds a directory of works.
	 *
	 *	@param	dir		Directory.
	 *
	 *	@throws Exception
	 */

	private static void buildDir (File dir)
		throws Exception
	{
		File[] contents = dir.listFiles();
		for (int i = 0; i < contents.length; i++) {
			File file = contents[i];
			if (file.isDirectory()) {
				buildDir(file);
			} else if (file.getName().endsWith(".xml")) {
				buildWork(file);
			}
		}
	}

	/**	Creates the MySQL table exporter/importers.
	 *
	 *	@throws	Exception
	 */

	private static void createExporterImporters ()
		throws Exception
	{
		lemmaTableExporterImporter =
			new TableExporterImporter("lemma", null, tempDirPath + "lemma.txt", false);
		lemPosTableExporterImporter =
			new TableExporterImporter("lempos", null, tempDirPath + "lemPos.txt", false);
		wordTableExporterImporter =
			new TableExporterImporter("word", null, tempDirPath + "word.txt", false);
		wordPartTableExporterImporter =
			new TableExporterImporter("wordpart", null, tempDirPath + "wordPart.txt", false);
		lineTableExporterImporter =
			new TableExporterImporter("line", null, tempDirPath + "line.txt", false);
		speechTableExporterImporter =
			new TableExporterImporter("speech", null, tempDirPath + "speech.txt", false);
		speechSpeakersTableExporterImporter =
			new TableExporterImporter("speech_speakers", null,
				tempDirPath + "speechSpeakers.txt", false);
	}

	/**	Gets the next available id for a table.
	 *
	 *	@param	c			Database connection
	 *
	 *	@param	tableName	Table name.
	 *
	 *	@throws Exception
	 */

	private static long getNextId (Connection c, String tableName)
		throws Exception
	{
		PreparedStatement p = c.prepareStatement(
			"select max(id) from " + tableName);
		ResultSet r = p.executeQuery();
		long maxId = r.next() ? r.getLong(1) : 0;
		p.close();
		return maxId + 1;
	}

	/**	Reads database objects.
	 *
	 *	@throws	Exception
	 */

	private static void readDatabaseObjects ()
		throws Exception
	{
		PersistenceManager pm = new PersistenceManager();
		Connection c = pm.getConnection();

		wordId = getNextId(c, "word");
		wordPartId = getNextId(c, "wordpart");
		lemmaId = getNextId(c, "lemma");
		lemPosId = getNextId(c, "lempos");
		workPartId = getNextId(c, "workpart");
		lineId = getNextId(c, "line");
		speechId = getNextId(c, "speech");

		Collection wordClasses = pm.getAllWordClasses();
		for (Iterator it = wordClasses.iterator(); it.hasNext(); ) {
			WordClass wordClass = (WordClass)it.next();
			String tag = wordClass.getTag();
			wordClassMap.put(tag, wordClass);
		}

		Collection posCollection = pm.getAllPos();
		for (Iterator it = posCollection.iterator(); it.hasNext(); ) {
			Pos pos = (Pos)it.next();
			String tag = pos.getTag();
			posMap.put(tag, pos);
		}

		Collection lemmas = pm.getAllLemmas();
		for (Iterator it = lemmas.iterator(); it.hasNext(); ) {
			Lemma lemma = (Lemma)it.next();
			String tag = lemma.getTag().getString();
			lemmaMap.put(tag.toLowerCase(), lemma);
		}

		Collection lemPoses = pm.getAllLemPos();
		for (Iterator it = lemPoses.iterator(); it.hasNext(); ) {
			LemPos lemPos = (LemPos)it.next();
			Lemma lemma = lemPos.getLemma();
			if (lemma == null) continue;
			Pos pos = lemPos.getPos();
			if (pos == null) continue;
			long lemmaId = lemma.getId().longValue();
			long posId = pos.getId().longValue();
			LemPosIdPair pair = new LemPosIdPair(lemmaId, posId);
			lemPosMap.put(pair, lemPos);
		}

		pm.close();
	}

	/**	Processes a standard spelling element.
	 *
	 *	@param	el			Standard spelling element.
	 *
	 *	@param	charset		Character set.
	 *
	 *	@param	posType		Pos type.
	 *
	 *	@throws Exception
	 */

	private static void processSpelling (Element el, byte charset,
		byte posType)
			throws Exception
	{
		String lemmaTag = el.getAttribute("lemma");
		String posTag = el.getAttribute("pos");
		String spelling = DOMUtils.getText(el);
		if (spelling.length() == 0) {
			BuildUtils.emsg("Missing spelling");
			return;
		}
		createLemPos(lemmaTag, posTag, spelling, charset, posType, null);
	}

	/**	Processes a standard spelling definition file.
	 *
	 *	@param	file		Standard spelling definition XML file.
	 *
	 *	@throws	Exception
	 */

	private static void processSpellingFile (File file)
		throws Exception
	{
		System.out.println("Reading standard spelling file " +
			file.getPath());
		Document document = DOMUtils.parse(file);
		Element rootEl = DOMUtils.getChild(document,
			"WordHoardStandardSpellings");
		if (rootEl == null) {
			BuildUtils.emsg(
				"Missing required WordHoardStandardSpellings " +
				"root element");
			return;
		}
		String charsetStr = rootEl.getAttribute("charset");
		byte charset = 0;
		if (charsetStr.length() == 0) {
			BuildUtils.emsg(
				"Missing required charset attribute in " +
				"WordHoardStandardSpellings element");
			return;
		} else if (charsetStr.equals("roman")) {
			charset = TextParams.ROMAN;
		} else if (charsetStr.equals("greek")) {
			charset = TextParams.GREEK;
		} else {
			BuildUtils.emsg(
				"Illegal charset attribute " + charsetStr +
				" in WordHoardStandardSpellings element");
			return;
		}
		String posTypeStr = rootEl.getAttribute("posType");
		byte posType = 0;
		if (posTypeStr.length() == 0) {
			BuildUtils.emsg(
				"Missing required posType attribute in " +
				"WordHoardStandardSpellings element");
			return;
		} else if (posTypeStr.equals("english")) {
			posType = Pos.ENGLISH;
		} else if (posTypeStr.equals("greek")) {
			posType = Pos.GREEK;
		} else {
			BuildUtils.emsg(
				"Illegal posType attribute " + posTypeStr +
				" in WordHoardStandardSpellings element");
			return;
		}
		NodeList children = rootEl.getChildNodes();
		int numChildren = children.getLength();
		for (int i = 0; i < numChildren; i++) {
			Node child = children.item(i);
			if (!(child instanceof Element)) continue;
			String childName = child.getNodeName();
			Element childEl = (Element)child;
			if (childName.equals("standardSpelling")) {
				processSpelling(childEl, charset, posType);
			} else {
				BuildUtils.emsg("Illegal element: " + childName);
			}
		}
	}

	/**	Processes a directory of standard spelling definition files.
	 *
	 *	@param	dir		Directory.
	 *
	 *	@throws	Exception
	 */

	private static void processSpellingDir (File dir)
		throws Exception
	{
		File[] contents = dir.listFiles();
		for (int i = 0; i < contents.length; i++) {
			File file = contents[i];
			if (file.isDirectory()) {
				processSpellingDir(file);
			} else if (file.getName().endsWith(".xml")) {
				processSpellingFile(file);
			}
		}
	}

	/**	Reads standard spellings.
	 *
	 *	@throws	Exception
	 */

	private static void readStandardSpellings ()
		throws Exception
	{
		if (spellingPath == null || debug) return;
		File file = new File(spellingPath);
		if (file.isDirectory()) {
			processSpellingDir(file);
		} else {
			processSpellingFile(file);
		}
	}

	/**	Imports objects into a MySQL table.
	 *
	 *	@param	c					Database connection.
	 *
	 *	@param	exporterImporter	MySQL table exporter/importer.
	 *
	 *	@param	name				Table name.
	 *
	 *	@throws Exception
	 */

	private static void importTable (Connection c,
		TableExporterImporter exporterImporter, String name)
			throws Exception
	{
		long startTime = System.currentTimeMillis();
		exporterImporter.close();
		int ct = exporterImporter.importData(c);
		long endTime = System.currentTimeMillis();
		System.out.println(
			Formatters.formatIntegerWithCommas(ct) +
			(ct == 1 ? " object" : " objects") +
			" imported into " + name + " table in " +
			BuildUtils.formatElapsedTime(startTime, endTime));
	}

	/**	Imports objects into the MySQL database.
	 *
	 *	@throws	Exception
	 */

	private static void importObjects ()
		throws Exception
	{
		System.out.println();
		System.out.println("Importing objects into MySQL tables");
		pm = new PersistenceManager();
		Connection c = pm.getConnection();
		importTable(c, lemmaTableExporterImporter, "lemma");
		importTable(c, lemPosTableExporterImporter, "lempos");
		importTable(c, wordTableExporterImporter, "word");
		importTable(c, wordPartTableExporterImporter, "wordpart");
		importTable(c, lineTableExporterImporter, "line");
		importTable(c, speechTableExporterImporter, "speech");
		importTable(c, speechSpeakersTableExporterImporter, "speech_speakers");
		pm.close();
	}

	/**	The main program.
	 *
	 *	@param	args		Command line arguments.
	 */

	public static void main  (final String args[]) {

		try {

			//	Initialize.

			long startTime = System.currentTimeMillis();
			parseArgs(args);

			File file = new File(inPath);
			boolean isDir = file.isDirectory();
			if (isDir) {
				System.out.println("Building works from directory " + inPath);
			} else {
				System.out.println("Building work from file " + inPath);
			}

			tempDirPath = BuildUtils.createTempDir() + "/";
			BuildUtils.initHibernate(dbname, username, password);
			createExporterImporters();
			readDatabaseObjects();
			readStandardSpellings();

			//	Build file or directory.

			if (isDir) {
				buildDir(file);
			} else {
				buildWork(file);
			}

			//	Import objects into the MySQL database.

			if (!debug) importObjects();
			BuildUtils.deleteTempDir();

			//	Report final stats.

			long endTime = System.currentTimeMillis();
			System.out.println();
			System.out.println(Formatters.formatIntegerWithCommas(numWorks) +
				(numWorks == 1 ? " work" : " works") +
				" created in " +
				BuildUtils.formatElapsedTime(startTime, endTime));
			BuildUtils.reportNumErrors();

			//	Run client if debug mode and single file built.

			if (debug && !isDir)
				WordHoard.main(new String[]{corpusTag, fullWorkTag});

		} catch (Exception e) {

			e.printStackTrace();
			System.exit(1);

		}

	}

	/**	Hides the default no-arg constructor.
	 */

	private BuildWorks () {
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

