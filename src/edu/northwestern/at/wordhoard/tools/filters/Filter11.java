package edu.northwestern.at.wordhoard.tools.filters;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import java.sql.*;
import java.text.*;

import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.speakers.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;

/**	Creates a Greek work XML file.
 *
 *	<p>Usage:
 *
 *	<p><code>Filter11 in out</code>
 *
 *	<p>in = Path to TEI XML input file for a work. Ignored if ege.
 *
 *	<p>out = Path to TEI XML output file for a work.
 */
 
public class Filter11 {
	
	/**	Input file path. */
	
	private static String inPath;
	
	/**	Output file path. */
	
	private static String outPath;
	
	/**	Connection to Martin's database. */
	
	private static Connection martinConnection;
	
	/**	Output file. */
	
	private static PrintWriter out;
	
	/**	Corpus tag. */
	
	private static String corpusTag;
	
	/**	Work tag. */
	
	private static String workTag;
	
	/**	Work info. */
	
	private static WorkInfo workInfo;
	
	/**	Map from Martin's metrical shape id's to metrical shape strings. */
	
	private static HashMap metricalShapeMap = new HashMap();
	
	/**	Map from Martin's speaker id's to Speaker objects. */
	
	private static HashMap speakerMap = new HashMap();

	/**	Two digit decimal integer formatter. */

	private static final DecimalFormat TWO_DIGIT_FORMATTER =
		new DecimalFormat("00");
		
	/**	Four digit decimal integer formatter. */

	private static final DecimalFormat FOUR_DIGIT_FORMATTER =
		new DecimalFormat("0000");
	
	/**	Work information class. */
	
	private static class WorkInfo {
	
		/**	Work tag. */
	
		private String tag;
		
		/**	Work title. */
		
		private String title;
		
		/**	Author name. */
		
		private String authorName;
		
		/**	Work group. */
		
		private int workGroup;
		
		/**	Book number, or 0 if multiple books. */
		
		private int book;
		
		/**	Work id for Greek beta code text. */
		
		private int greekId;
		
		/**	Number of parts ("books"). */
		
		private int numParts;
		
		/**	Array of part titles or null. If null and numParts > 1,
		 *	then the part titles are "Book 1", "Book 2", etc. If null
		 *	and numParts == 1, then the part title is the same as the work
		 *	title.
		 */
		 
		private String[] partTitles;
		
		/**	Creates new work information.
		 *
		 *	@param	tag			Work tag.
		 *
		 *	@param	title		Title.
		 *
		 *	@param	authorName	Author.
		 *
		 *	@param	workGroup	Work group.
		 *
		 *	@param	book		Book number, or 0 if multiple books.
		 *
		 *	@param	greekId		Work id of Greek beta code text.
		 *
		 *	@param	numParts	Number of parts ("books").
		 *
		 *	@param	partTitles	Part titles or null.
		 */
		
		private WorkInfo (String tag, String title, String authorName,
			int workGroup, int book,
			int greekId, 
			int numParts, String[] partTitles)
		{
			this.tag = tag;
			this.title = title;
			this.authorName = authorName;
			this.workGroup = workGroup;
			this.book = book;
			this.greekId = greekId;
			this.numParts = numParts;
			this.partTitles = partTitles;
		}
	}
	
	/**	Map from work tags to work information. */
	
	private static HashMap workInfoMap = new HashMap();
	
	static {
		workInfoMap.put("IL",
			new WorkInfo("IL", "The Iliad", "Homer", 1, 0, 
				1, 
				24, null));
		workInfoMap.put("OD",
			new WorkInfo("OD", "The Odyssey", "Homer", 2, 0, 
				4, 
				24, null));
		workInfoMap.put("TH",
			new WorkInfo("TH", "Theogony", "Hesiod", 3, 1, 
				7, 
				1, null));
		workInfoMap.put("WD",
			new WorkInfo("WD", "Works and Days", "Hesiod", 3, 2, 
				10, 
				1, null));
		workInfoMap.put("SH",
			new WorkInfo("SH", "Shield of Herakles", "pseudo-Hesiod", 3, 3, 
				13, 
				1, null));
		workInfoMap.put("HH",
			new WorkInfo("HH", "Homeric Hymns", "anonymous", 4, 0,
				16, 
				34,
				new String[] {
					"Dionysus (1)",
					"Demeter (2)",
					"Apollo (3)",
					"Hermes (4)",
					"Aphrodite (5)",
					"Aphrodite (6)",
					"Dionysus (7)",
					"Ares (8)",
					"Artemis (9)",
					"Aphrodite (10)",
					"Athene (11)",
					"Hera (12)",
					"Demeter (13)",
					"Mother of the gods (14)",
					"Herakles (15)",
					"Asklepios (16)",
					"Dioskouroi (17)",
					"Hermes (18)",
					"Pan (19)",
					"Hephaistos (20)",
					"Apollo (21)",
					"Poseidon (22)",
					"Dia (23)",
					"Hestia (24)",
					"Muses and Apollo (25)",
					"Dionysus (26)",
					"Artemis (27)",
					"Athene (28)",
					"Hestia (29)",
					"Gaia (30)",
					"Helios (31)",
					"Selene (32)",
					"Dioskouroi (33)",
					"Untitled (34)"
				}
			));
	}
	
	/**	Line information class. */
	
	private static class LineInfo {
	
		/** Tag for line (e.g., IL.1.1). */
	
		private String lineTag;
	
		/** True if this line starts a new paragraph. */
	
		private boolean par;
	
		/** Greek text for line. */
	
		private String greek;
		
		/**	Array of metrical shapes for words in line. */
		
		private String[] metricalShapes = new String[20];
		
		/**	Speaker id or null if none. */
		
		private Integer speakerId;
		
		/**	Creates a new line information object. 
		 *
		 *	@param	lineTag		Line tag.
		 *
		 *	@param	par			True if line starts a new paragraph.
		 *
		 *	@param	greek		Greek text for line.
		 */
		 
		private LineInfo (String lineTag, boolean par, String greek) {
			this.lineTag = lineTag;
			this.par = par;
			this.greek = greek;
		}

	}
	
	/**	Parses command line arguments.
	 *
	 *	@param	args		Command line arguments.
	 *
	 *	@throws Exception
	 */
	
	private static void parseArgs (String[] args) 
		throws Exception
	{
		int n = args.length;
		if (n != 2) {
			System.err.println("Usage: Filter11 in out");
			System.exit(1);
		}
		inPath = args[0];
		outPath = args[1];
	}
	
	/**	Prints tabs.
	 *
	 *	@param	indentation		Indentation level.
	 */
	 
	private static void tab (int indentation) {
		for (int i = 0; i < indentation; i++) out.print("\t");
	}
	
	/**	Prints a string with indentation.
	 *
	 *	@param	indentation		Indentation level.
	 *
	 *	@param	str				String.
	 */
	 
	private static void print (int indentation, String str) {
		tab(indentation);
		out.println(str);
	}
	
	/**	Generates a speaker id from a speaker name.
	 *
	 *	@param	name		Speaker name.
	 *
	 *	@return				Speaker id = name with spaces replaced by "-".
	 */
	
	private static String getSpeakerId (String name) {
		return name.replaceAll(" ", "-");
	}
	
	/**	Prints the cast items.
	 *
	 *	@throws Exception
	 */
	 
	private static void printCastItems ()
		throws Exception
	{
		PreparedStatement p = null;
		if (workInfo.book == 0) {
			p = martinConnection.prepareStatement(
				"select distinct SPEAKER_ID " +
				"from EGE_NAR_PROS " +
				"where WORK_GROUP=? and SPEAKER_ID > 0");
			p.setInt(1, workInfo.workGroup);
		} else {
			p = martinConnection.prepareStatement(
				"select distinct SPEAKER_ID " +
				"from EGE_NAR_PROS " +
				"where WORK_GROUP=? and BOOK=? and SPEAKER_ID > 0");
			p.setInt(1, workInfo.workGroup);
			p.setInt(2, workInfo.book);
		}
		ResultSet r = p.executeQuery();
		while (r.next()) {
			int speakerId = r.getInt(1);
			Speaker speaker = (Speaker)speakerMap.get(new Integer(speakerId));
			String name = speaker.getName();
			Spelling originalNameSpelling = speaker.getOriginalName();
			String originalName = originalNameSpelling == null ? null :
				originalNameSpelling.getString();
			byte gender = speaker.getGender().getGender();
			byte mortality = speaker.getMortality().getMortality();
			String genderStr = "";
			switch (gender) {
				case Gender.MALE:
					genderStr = "male";
					break;
				case Gender.FEMALE:
					genderStr = "female";
					break;
				case Gender.UNCERTAIN_MIXED_OR_UNKNOWN:
					genderStr = "uncertainMixedOrUnknown";
					break;
			}
			String mortalityStr = "";
			switch (mortality) {
				case Mortality.MORTAL:
					mortalityStr = "mortal";
					break;
				case Mortality.IMMORTAL_OR_SUPERNATURAL:
					mortalityStr = "immortalOrSupernatural";
					break;
				case Mortality.UNKNOWN_OR_OTHER:
					mortalityStr = "unknownOrOther";
					break;
			}
			print(5, "<castItem type=\"role\">");
				StringBuffer buf = new StringBuffer();
				buf.append("<role id=\"");
				buf.append(getSpeakerId(name));
				buf.append("\" gender=\"");
				buf.append(genderStr);
				buf.append("\" mortality=\"");
				buf.append(mortalityStr);
				buf.append("\"");
				if (originalName != null) {
					buf.append(" originalName=\"");
					buf.append(originalName);
					buf.append("\"");
				}
				buf.append(">");
				buf.append(name);
				buf.append("</role>");
				print(6, buf.toString());
			print(5, "</castItem>");
		}
	}
	
	/**	Prints the castlist.
	 *
	 *	@throws Exception
	 */
	 
	private static void printCastList ()
		throws Exception
	{	
		print(2, "<front>");
			print(3, "<div type=\"castList\" rend=\"none\">");
				print(4, "<castList>");
					printCastItems();
				print(4, "</castList>");
			print(3, "</div>");
		print(2, "</front>");
	}
	
	/**	Prints one line of text.
	 *
	 *	@param	n					Line number
	 *
	 *	@param	lineInfo			Line info.
	 *
	 *	@param	wordTagPrefix		Word tag prefix.
	 *
	 *	@param	level				Indentation level.
	 *
	 *	@throws Exception
	 */
	 
	private static void printLine (int n, LineInfo lineInfo, 
		String wordTagPrefix, int level)
			throws Exception
	{
	
		print(level, "<wordHoardTaggedLine id=\"" + lineInfo.lineTag +
			"\" n=\"" + n + "\">");
		
		char[] c = lineInfo.greek.toCharArray();
		int len = c.length;
		int pos = 0;
		int wordOrdinalInLine = 1;
		
		while (pos < len) {
		
			int puncStart = pos;
			while (pos < len && c[pos] < 0x100) pos++;
			int wordStart = pos;
			while (pos < len && c[pos] != ' ') pos++;
			pos--;
			while (pos > wordStart && c[pos] < 0x100 && c[pos] != '\'')
				pos--;
			pos++;
			int wordEnd = pos;
			
			String wordTag = wordTagPrefix +
				TWO_DIGIT_FORMATTER.format(wordOrdinalInLine);
			
			if (puncStart < wordStart) {
				String punc = lineInfo.greek.substring(puncStart, wordStart);
				print(level+1, "<punc>" + punc + "</punc>");
			}
			
			if (wordStart < wordEnd) {
				String metricalShape = 
					lineInfo.metricalShapes[wordOrdinalInLine-1];
				String word = lineInfo.greek.substring(wordStart, wordEnd);
				print(level+1, "<w id=\"" + wordTag + "\"" +
					" metricalShape=\"" + metricalShape +
					"\">" + word + "</w>");
				wordOrdinalInLine++;
			}
			
		}
		
		print(level, "</wordHoardTaggedLine>");
	}
	
	/**	Starts a speech.
	 *
	 *	@param	speakerId 	Speaker id.
	 *
	 *	@param	lineNumber	Line number.
	 */
	 
	private static void startSpeech (Integer speakerId, int lineNumber) {
		String speakerName = "unknown";
		Speaker speaker = (Speaker)speakerMap.get(speakerId);
		if (speaker == null) {
			System.out.println("         ###### " +
				"Invalid speaker id: " + speakerId + 
				" for line " + lineNumber);
		} else {
			speakerName = speaker.getName();
		}
		print(4, "<sp who=\"" + getSpeakerId(speakerName) + 
			"\" rend=\"none\">");
	}
	
	/**	Prints lines of text for a part.
	 *
	 *	@param	greekId		Work id in Martin's database.
	 *
	 *	@param	book		Book number in Martin's database.
	 *
	 *	@throws	Exception
	 */
	 
	private static void printLines (int greekId, int book)
		throws Exception
	{
		
		//	Get the Greek line information for the work part. 
		
		LineInfo[] lineInfoArray = new LineInfo[2000];
		int numLinesInPart = 0;
		PreparedStatement p = martinConnection.prepareStatement(
			"select LINE, TRAD_LOC, LINE_TEXT, PARAGRAPH " +
			"from EGE_LINE_LOCATION " +
			"where WORK_ID=? and BOOK=?");
		p.setInt(1, greekId);
		p.setInt(2, book);
		ResultSet r = p.executeQuery();
		while (r.next()) {
			int lineNumber = r.getInt(1);
			String lineTag = r.getString(2);
			String betaText = r.getString(3);
			String paragraph = r.getString(4);
			if (lineNumber < 1 || lineNumber >= 2000) {
				System.out.println("      ##### " +
					"Bad line number " + lineNumber + " for work id " +
					greekId + " and book " + book);
				continue;
			}
			String greek = CharsetUtils.translateBetaToUni(betaText);
			String badBetaSeq = CharsetUtils.getBadBetaSeq();
			if (badBetaSeq != null) {
				System.out.println("         ###### " +
					"Bad beta code sequence in line " + 
					lineNumber + ": " + badBetaSeq);
				System.out.println("            " + betaText);
			}
			boolean par = paragraph.equals("T");
			LineInfo lineInfo = new LineInfo(lineTag, par, greek);
			lineInfoArray[lineNumber] = lineInfo;
			if (lineNumber > numLinesInPart) numLinesInPart = lineNumber;
		}
		
		//	Get the metrical shapes for the part.
		
		p = martinConnection.prepareStatement(
			"select LINE, WORD_POS, WORDSHAPE_ID " +
			"from EGE_LEMMA_OCCURRENCE " +
			"where WORK_GROUP=? and BOOK=?");
		p.setInt(1, workInfo.workGroup);
		p.setInt(2, book);
		r = p.executeQuery();
		while (r.next()) {
			int lineNumber = r.getInt(1);
			int wordPos = r.getInt(2);
			int metricalShapeId = r.getInt(3);
			LineInfo lineInfo = lineInfoArray[lineNumber];
			if (lineInfo == null) {
				System.out.println("         ###### " +
					"Metrical shapes found for non-existant line " + lineNumber);
				continue;
			}
			if (wordPos < 1 || wordPos > 20) {
				System.out.println("         ###### " +
					"Invalid word position " + wordPos +
					" for line " + lineNumber);
				continue;
			}
			String metricalShape = 
				(String)metricalShapeMap.get(new Integer(metricalShapeId));
			if (metricalShape == null) {
				System.out.println("         ###### " +
					"Invalid metrical shape id for word " + wordPos +
					", line " + lineNumber);
				continue;
			}
			lineInfo.metricalShapes[wordPos-1] = metricalShape;
		}
		
		//	Get the speaker ids for the lines.
		
		p = martinConnection.prepareStatement(
			"select LINE, SPEAKER_ID " +
			"from EGE_NAR_PROS " +
			"where WORK_GROUP=? and BOOK=? and SPEAKER_ID > 0");
		p.setInt(1, workInfo.workGroup);
		p.setInt(2, book);
		r = p.executeQuery();
		while (r.next()) {
			int lineNumber = r.getInt(1);
			int speakerId = r.getInt(2);
			LineInfo lineInfo = lineInfoArray[lineNumber];
			if (lineInfo == null) {
				System.out.println("         ###### " +
					"Speaker found for non-existant line " + lineNumber);
				continue;
			}
			lineInfo.speakerId = new Integer(speakerId);
		}
				
		//	Print the lines.
		
		String partWordTagPrefix = "ege-" + 
			Integer.toString(workInfo.workGroup) +
			TWO_DIGIT_FORMATTER.format(book);
		Integer speakerId = null;
		for (int lineNumber = 1; lineNumber <= numLinesInPart; lineNumber++) {
			LineInfo lineInfo = lineInfoArray[lineNumber];
			if (lineInfo == null) {
				System.out.println("      ##### " +
					"Missing line " + lineNumber + " for work id " +
					greekId + " and book " + book);
				continue;
			}
			if (speakerId == null) {
				if (lineInfo.speakerId != null) {
					speakerId = lineInfo.speakerId;
					startSpeech(speakerId, lineNumber);
				}
			} else if (!speakerId.equals(lineInfo.speakerId)) {
				print(4, "</sp>");
				if (lineInfo.speakerId == null) {
					speakerId = null;
				} else {
					speakerId = lineInfo.speakerId;
					startSpeech(speakerId, lineNumber);
				}
			}
			int level = speakerId == null ? 4 : 5;
			if (lineInfo.par) print(level, "<p/>");
			String wordTagPrefix = partWordTagPrefix +
				FOUR_DIGIT_FORMATTER.format(lineNumber);
			printLine(lineNumber, lineInfo, wordTagPrefix, level);
		}
		if (speakerId != null) print(4, "</sp>");
		
	}
	
	/**	Prints a part.
	 *
	 *	@param	partNumber	Part number.
	 *
	 *	@throws	Exception
	 */
	 
	private static void printPart (int partNumber)
		throws Exception
	{
		//	Get information about the part.
	
		int greekId = workInfo.greekId;
		String title;
		int book;
		String id;
		if (workInfo.partTitles == null) {
			if (workInfo.numParts == 1) {
				title = workInfo.title;
				book = workInfo.book;
			} else {
				title = "Book " + partNumber;
				book = partNumber;
			}
		} else {
			title = workInfo.partTitles[partNumber-1];
			book = partNumber;
		}
		id = workInfo.numParts > 1 ? Integer.toString(partNumber) : "body";
		
		//	Print the "div" element for the part.
		
		print(3, "<div id=\"" + id + "\" indent=\"20\">");
			print(4, "<wordHoardHeader>");
				print(5, "<title>" + title + "</title>");
				print(5, "<pathTag>" + id + "</pathTag>");
				print(5, "<taggingData>");
					print(6, "<lemma/>");
					print(6, "<pos/>");
					print(6, "<wordClass/>");
					print(6, "<spelling/>");
					print(6, "<speaker/>");
					print(6, "<gender/>");
					print(6, "<mortality/>");
					print(6, "<prosodic/>");
					print(6, "<metricalShape/>");
				print(5, "</taggingData>");
			print(4, "</wordHoardHeader>");
			printLines(greekId, book);
		print(3, "</div>");
	}
	
	/**	Prints the output file.
	 *
	 *	@throws Exception
	 */
	 
	private static void printOutput ()
		throws Exception
	{
		out.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		print(0, "<WordHoardText>");
			print(1, "<wordHoardHeader corpus=\"ege\" prosodic=\"verse\" " +
				"work=\"" + workInfo.tag + "\">");
				print(2, "<taggingData>");
					print(3, "<lemma/>");
					print(3, "<pos/>");
					print(3, "<wordClass/>");
					print(3, "<spelling/>");
					print(3, "<speaker/>");
					print(3, "<gender/>");
					print(3, "<mortality/>");
					print(3, "<prosodic/>");
					print(3, "<metricalShape/>");
				print(2, "</taggingData>");
			print(1, "</wordHoardHeader>");
			print(1, "<teiHeader>");
				print(2, "<fileDesc>");
					print(3, "<titleStmt>");
						print(4, "<title>" + workInfo.title + "</title>");
						print(4, "<author>" + workInfo.authorName + 
							"</author>");
					print(3, "</titleStmt>");
					print(3, "<publicationStmt>");
						print(4, "<p>Responsibility and publication " +
							"statements to be supplied.</p>");
					print(3, "</publicationStmt>");
				print(2, "</fileDesc>");
			print(1, "</teiHeader>");
			print(1, "<text>");
				printCastList();
				print(2, "<body>");
					for (int i = 1; i <= workInfo.numParts; i++)
						printPart(i);
				print(2, "</body>");
			print(1, "</text>");
		print(0, "</WordHoardText>");
	}
	
	/**	Builds the speaker map.
	 *
	 *	@throws	Exception
	 */
	 
	private static void buildSpeakerMap ()
		throws Exception
	{
		PreparedStatement p = martinConnection.prepareStatement(
			"select SPEAKER_ID, NAME, betacode_name, SEX, " +
			"MORTALITY from EGE_SPEAKER");
		ResultSet r = p.executeQuery();
		while (r.next()) {
			int speakerId = r.getInt(1);
			String name = r.getString(2);
			String betacodeName = r.getString(3);
			int sex = r.getInt(4);
			int mortality = r.getInt(5);
			Speaker speaker = new Speaker();
			speaker.setTag(name);
			speaker.setName(name);
			if (betacodeName != null && betacodeName.length() > 0) {
				String greek = CharsetUtils.translateBetaToUni(betacodeName);
				String badBetaSeq = CharsetUtils.getBadBetaSeq();
				if (badBetaSeq == null) {
					speaker.setOriginalName(
						new Spelling(greek, TextParams.GREEK));
				} else {
					System.out.println("         ###### " +
						"Bad beta code sequence for speaker " + 
						speakerId + ": " + badBetaSeq);
					System.out.println("            " + betacodeName);
				}
			}
			switch (sex) {
				case 1:
					speaker.setGender(Gender.MALE);
					break;
				case 2:
					speaker.setGender(Gender.FEMALE);
					break;
				case 3: 
					speaker.setGender(
						Gender.UNCERTAIN_MIXED_OR_UNKNOWN);
					break;
				default:
					System.out.println("##### " +
						"Invalid sex for speaker: " + speakerId);
			}
			switch (mortality) {
				case 1:
					speaker.setMortality(Mortality.MORTAL);
					break;
				case 2:
					speaker.setMortality(Mortality.IMMORTAL_OR_SUPERNATURAL);
					break;
				case 3: 
					speaker.setMortality(Mortality.UNKNOWN_OR_OTHER);
					break;
				default:
					System.out.println("##### " +
						"Invalid mortality for speaker: " + speakerId);
			}
			speakerMap.put(new Integer(speakerId), speaker);
		}
	}

	/**	Builds the metrical shape map.
	 *
	 *	@throws Exception
	 */
	 
	 private static void buildMetricalShapeMap ()
	 	throws Exception
	 {
	 	PreparedStatement p = martinConnection.prepareStatement(
	 		"select WORD_SHAPE_ID, WORD_SHAPE from EGE_WORD_SHAPES");
	 	ResultSet r = p.executeQuery();
	 	while (r.next()) {
	 		int metricalShapeId = r.getInt(1);
	 		String metricalShape = r.getString(2);
	 		metricalShapeMap.put(new Integer(metricalShapeId), metricalShape);
	 	}
	}
	 
	/**	Creates the text.
	 *
	 *	@param	args		Command line arguments.
	 *
	 *	@throws Exception
	 */
	 
	private static void createGreekText (String args[])
		throws Exception
	{
	
		//	Parse the arguments.
	
		parseArgs(args);
		
		//	Get the corpus and work information.
		
		int k = outPath.lastIndexOf('.');
		int j = outPath.lastIndexOf('/', k-1);
		int i = outPath.lastIndexOf('/', j-1);
		corpusTag = outPath.substring(i+1, j);
		workTag = outPath.substring(j+1, k);
		
		if (corpusTag.equals("ege")) {
			System.out.println("Creating " + outPath);
		} else {
			System.out.println("Copying " + inPath + " -> " + outPath);
			Runtime.getRuntime().exec("cp " + inPath + " " + outPath);
			return;
		}
		
		//	Open the output file.
		
		FileOutputStream fos = new FileOutputStream(outPath);
		OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
		BufferedWriter bw = new BufferedWriter(osw);
		out = new PrintWriter(bw);
		
		//	Get a connection to Martin's database.
		
		Class.forName("com.mysql.cj.jdbc.Driver");
		ClassLoader loader = Filter10.class.getClassLoader();
		InputStream in = loader.getResourceAsStream("martin.properties");
		Properties properties = new Properties();
		properties.load(in);
		in.close();
		String url = properties.getProperty("database-url");
		String username = properties.getProperty("database-username");
		String password = properties.getProperty("database-password");
		martinConnection = 
			DriverManager.getConnection(url, username, password);
			
		//	Build the speaker and metrical shape maps.
		
		buildSpeakerMap();
		buildMetricalShapeMap();
		
		//	Get the work information.
		
		workInfo = (WorkInfo)workInfoMap.get(workTag);
		
		//	Print the output file.
		
		printOutput();
				
		//	Close the output file and the database connection.
		
		out.close();
		martinConnection.close();
		
	}
 
	/**	The main program.
	 *
	 *	@param	args		Command line arguments.
	 */
	
	public static void main (String args[]) {
		try {
			createGreekText(args);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**	Hides the default no-arg constructor.
	 */
	 
	private Filter11 () {
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

