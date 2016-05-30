package edu.northwestern.at.wordhoard.tools;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import java.sql.*;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.counts.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.db.mysql.*;

/**	Calculates counts.
 *
 *	<p>This tool computes and creates all of the 
 *	{@link edu.northwestern.at.wordhoard.model.counts count}
 *	objects and makes them persistent in the MySQL database. It must be run 
 *	after all of the other build programs have constructed and populated
 *	the rest of the object model.
 *
 *	<p>Usage:
 *
 *	<p><code>CalculateCounts dbname username passwrod 
 *	wordDataPath wordPartDataPath speechDataPath</code>
 *
 *	<p>dbname = Database name.
 *
 *	<p>username = MySQL username.
 *
 *	<p>password = MySQL password.
 *
 *	<p>wordDataPath = Path to word data file containing the following 
 *	tab-delimited data:
 *
 *		<ol>
 *		<li>Corpus id.
 *		<li>Work id.
 *		<li>Work part id.
 *		<li>Word id.
 *		<li>Spelling string, case-sensitive.
 *		<li>Spelling string, case-insensitive.
 *		<li>Character set (0 = Latin, 1 = Greek).
 *		<li>Prosodic attribute.
 *		<li>Metrical shape attribute, or \N if none.
 *		<li>Speech id, or \N if none.
 *		</ol>
 *
 *	<p>wordPartDataPath = Path to word part data file containing the following 
 *	tab-delimited data:
 *
 *		<ol>
 *		<li>Corpus id.
 *		<li>Work id.
 *		<li>Work part id.
 *		<li>Word id.
 *		<li>Part index.
 *		<li>LemPos id.
 *		</ol>
 *
 *	<p>speechDataPath = Path to speech data file containing the following
 *	tab-delimited data:
 *
 *		<ol>
 *		<li>Corpus id.
 *		<li>Work id.
 *		<li>Word part id.
 *		<li>Speech id.
 *		<li>Gender attribute.
 *		<li>Mortality attribute.
 *		</ol>
 *
 *	<p>The three input data files must be sorted in increasing lexical or
 *	numeric order.
 *
 *	<p>The file "hibernate.properties" specifies the parameters for our
 *	object model MySQL database.
 */

public class CalculateCounts {

	/**	LemPos info class. */
	
	private static class LemPosInfo {
		public long lemmaId;
		public long posId;
	}
	
	/**	Lemma info class. */
	
	private static class LemmaInfo {
		public String tagString;
		public byte tagCharset;
		public long wordClassId;
		public int homonym;
	}
	
	/**	Word class info class. */
	
	private static class WordClassInfo {
		public String tag;
		public String majorClass;
	}

	/**	Word data class. */
	
	private static class WordData {
		public Long corpusId;
		public Long workId;
		public Long workPartId;
		public Long wordId;
		public String spelling;
		public String spellingInsensitive;
		public byte charset;
		public byte prosodic;
		public String metricalShape;
		public Long speechId;
	}
	
	/**	Word part data class. */
	
	private static class WordPartData {
		public Long corpusId;
		public Long workId;
		public Long workPartId;
		public Long wordId;
		public int partIndex;
		public Long lemPosId;
	}
	
	/**	Speech data class. */
	
	private static class SpeechData {
		public Long corpusId;
		public Long workId;
		public Long workPartId;
		public Long speechId;
		public byte gender;
		public byte mortality;
	}
	
	/**	Data file reader abstract base class.
	 *
	 *	<p>A data reader implements a "put back" feature where a read data item
	 *	can be pushed back onto the input stream for reading again later.
	 *	This provides the equivalent of a one line lookahead facility.
	 */
	
	private static abstract class DataReader {
		public BufferedReader in;
		public Object data;
		public DataReader (String path)
			throws Exception
		{
			File f = new File(path);
			FileInputStream fis = new FileInputStream(f);
			InputStreamReader isr = new InputStreamReader(fis, "utf-8");
			in = new BufferedReader(isr);
		}
		public void close () 
			throws Exception
		{
			in.close();
		}
		public void put (Object data) {
			this.data = data;
		}
	}
	
	/**	Word data file reader class. */
	
	private static class WordDataReader extends DataReader {
		public WordDataReader (String path)
			throws Exception
		{
			super(path);
		}
		public WordData get ()
			throws Exception
		{
			WordData result = (WordData)data;
			if (data != null) {
				data = null;
				return result;
			}
			String line = in.readLine();
			if (line == null) return null;
			StringTokenizer tok = new StringTokenizer(line, "\t");
			result = new WordData();
			result.corpusId = new Long(tok.nextToken());
			result.workId = new Long(tok.nextToken());
			result.workPartId = new Long(tok.nextToken());
			result.wordId = new Long(tok.nextToken());
			result.spelling = tok.nextToken().replaceAll("\\\\\\\\", "\\\\");
			result.spellingInsensitive = tok.nextToken().replaceAll("\\\\\\\\", "\\\\");
			result.charset = Byte.parseByte(tok.nextToken());
			result.prosodic = Byte.parseByte(tok.nextToken());
			String str = tok.nextToken();
			result.metricalShape = str.equals("\\N") ? null : str;
			str = tok.nextToken();
			result.speechId = str.equals("\\N") ? null : new Long(str);
			return result;
		}
	}
	
	/**	Word part data file reader class. */
	
	private static class WordPartDataReader extends DataReader {
		public WordPartDataReader (String path)
			throws Exception
		{
			super(path);
		}
		public WordPartData get ()
			throws Exception
		{
			WordPartData result = (WordPartData)data;
			if (data != null) {
				data = null;
				return result;
			}
			String line = in.readLine();
			if (line == null) return null;
			StringTokenizer tok = new StringTokenizer(line, "\t");
			result = new WordPartData();
			result.corpusId = new Long(tok.nextToken());
			result.workId = new Long(tok.nextToken());
			result.workPartId = new Long(tok.nextToken());
			result.wordId = new Long(tok.nextToken());
			result.partIndex = Integer.parseInt(tok.nextToken());
			result.lemPosId = new Long(tok.nextToken());
			return result;
		}
	}
	
	/**	Speech data file reader class. */
	
	private static class SpeechDataReader extends DataReader {
		public SpeechDataReader (String path)
			throws Exception
		{
			super(path);
		}
		public SpeechData get ()
			throws Exception
		{
			SpeechData result = (SpeechData)data;
			if (data != null) {
				data = null;
				return result;
			}
			String line = in.readLine();
			if (line == null) return null;
			StringTokenizer tok = new StringTokenizer(line, "\t");
			result = new SpeechData();
			result.corpusId = new Long(tok.nextToken());
			result.workId = new Long(tok.nextToken());
			result.workPartId = new Long(tok.nextToken());
			result.speechId = new Long(tok.nextToken());
			result.gender = Byte.parseByte(tok.nextToken());
			result.mortality = Byte.parseByte(tok.nextToken());
			return result;
		}
	}
	
	/**	Work counter class. */
	
	private static class WorkCounter {
		private long wordClassId;
		private String lemmaTagString;
		private int lemmaTagCharset;
		private String majorClass;
		private int termFreq;
		private int rank1;
		private int rank2; 
		private int numMajorClass;
	}
	
	/**	Corpus counter class. */
	
	private static class CorpusCounter {
		private long wordClassId;
		private String lemmaTagString;
		private int lemmaTagCharset;
		private String majorClass;
		private int colFreq;
		private int docFreq;
		private int rank1;
		private int rank2;
		private int numMajorClass;
	}
	
	/**	Lemma/Pos/Spelling key class. */
	
	private static class LemmaPosSpelling {
		private long lemmaId;
		private long posId;
		private String string;
		private int charset;
		private LemmaPosSpelling (long lemmaId, long posId,
			String string, int charset)
		{
			this.lemmaId = lemmaId;
			this.posId = posId;
			this.string = string;
			this.charset = charset;
		}
		public boolean equals (Object obj) {
			LemmaPosSpelling other = (LemmaPosSpelling)obj;
			return lemmaId == other.lemmaId && posId == other.posId &&
				string.equals(other.string) && charset == other.charset;
		}
		public int hashCode () {
			return (int)(lemmaId + posId + string.hashCode() + charset);
		}
	}
	
	/**	Frequency counter class. */
	
	private static class FrequencyCounter {
		private int freq;
		private int freqFirstWordPart;
	}
	
	/**	JDBC connection to model database. */
	
	private static Connection c;
	
	/**	Maps lemPos ids to lemPos info. */
	
	private static HashMap lemPosInfoMap;
	
	/**	Maps lemma ids to lemma info. */
	
	private static HashMap lemmaInfoMap;
	
	/**	Maps word class ids to word class info. */
	
	private static HashMap wordClassInfoMap;
	
	/**	Maps work part ids to parent work part ids. */
	
	private static HashMap workPartParentMap;
	
	/**	Maps work ids to sets of descendant work part ids. */
	
	private static HashMap workDescendantsMap; 
	
	/**	Word data reader. */
	
	private static WordDataReader wordDataReader;
	
	/**	Word part data reader. */
	
	private static WordPartDataReader wordPartDataReader;
	
	/**	Speech data reader. */
	
	private static SpeechDataReader speechDataReader;
	
	/**	Current corpus id. */
	
	private static Long corpusId;
	
	/**	Current work id. */
	
	private static Long workId;
	
	/**	Current work part id. */
	
	private static Long workPartId;
	
	/**	Current word id. */
	
	private static Long wordId;
	
	/**	Current word data. */
	
	private static WordData wordData;
	
	/**	Current word part data. */
	
	private static WordPartData wordPartData;
	
	/**	String buffer for building word classes string for current word. */
	
	private static StringBuffer wordClassesBuffer;
	
	/**	True if first part of current word. */
	
	private static boolean firstWordPart;
	
	/**	Map from speech ids to speech data for current work part. */
	
	private static HashMap speechDataMap;
	
	/**	Map from lemma ids to corpus counts for current corpus. */
	
	private static HashMap lemmaCorpusCountsMap;
	
	/**	Map from lemma ids to work counts for current work. */
	
	private static HashMap lemmaWorkCountsMap;
	
	/**	Map from lemma/pos/spelling keys to frequency counters for current corpus. */
		
	private static HashMap corpusLemmaPosSpellingCountsMap;
	
	/** Map from lemma/pos/spelling keys to frequency counters for current work. */
	
	private static HashMap workLemmaPosSpellingCountsMap;
	
	/**	Map from lemma/pos/spelling keys to frequency counts for current work part. */
	
	private static HashMap workPartLemmaPosSpellingCountsMap;
	
	/**	Word form counter array for current work.
	 *
	 *	<p>A word form counter array is used to accumulate frequency counts
	 *	by work part for various kinds of word forms (spelling, lemma, word 
	 *	class, etc.). These arrays are used to generate the WordCount and
	 *	TotalWordFormCount objects.
	 *
	 *	<p>A word form counter array is indexed by word form kind. Each
	 *	element is a hashmap mapping work part ids to hashmaps mapping spellings
	 *	to frequency counts.
	 */
	 
	private static HashMap[] wordFormCounterArray;

	/**	LemmaWorkCounts exporter/importer. */
	
	private static TableExporterImporter lemmaWorkCountsExporterImporter;
	
	/**	LemmaCorpusCounts exporter/importer. */
	
	private static TableExporterImporter lemmaCorpusCountsExporterImporter;
	
	/**	LemmaPosSpellingCounts exporter/importer. */
	
	private static TableExporterImporter lemmaPosSpellingCountsExporterImporter;
	
	/**	WordCount exporter/importer. */
	
	private static TableExporterImporter wordCountExporterImporter;
	
	/**	TotalWordFormCount exporter/importer. */
	
	private static TableExporterImporter totalWordFormCountExporterImporter;
	
	/**	Path to temporary directory. */
	
	private static String tempDirPath;
	
	/**	Reads database info.
	 *
	 *	@throws Exception
	 */
	 
	private static void readDatabaseInfo () 
		throws Exception
	{
		Statement s = c.createStatement();
		
		//	Build the lemPos info map.
		
		lemPosInfoMap = new HashMap();
		ResultSet r = s.executeQuery(
			"select id, lemma, pos from lempos");
		while (r.next()) {
			Long lemPosId = (Long)r.getObject(1);
			LemPosInfo lemPosInfo = new LemPosInfo();
			lemPosInfo.lemmaId = r.getLong(2);
			lemPosInfo.posId = r.getLong(3);
			lemPosInfoMap.put(lemPosId, lemPosInfo);
		}
		r.close();
		
		//	Build the lemma info map.
		
		lemmaInfoMap = new HashMap();
		r = s.executeQuery(
			"select id, tag_string, tag_charset, wordClass, homonym from lemma");
		while (r.next()) {
			Long lemmaId = (Long)r.getObject(1);
			LemmaInfo lemmaInfo = new LemmaInfo();
			lemmaInfo.tagString = r.getString(2);
			lemmaInfo.tagCharset = r.getByte(3);
			lemmaInfo.wordClassId = r.getLong(4);
			lemmaInfo.homonym = r.getInt(5);
			lemmaInfoMap.put(lemmaId, lemmaInfo);
		}
		r.close();
		
		//	Build the word class info map.
		
		wordClassInfoMap = new HashMap();
		r = s.executeQuery(
			"select id, tag, majorWordClass_majorWordClass from wordclass");
		while (r.next()) {
			Long wordClassId = (Long)r.getObject(1);
			WordClassInfo wordClassInfo = new WordClassInfo();
			wordClassInfo.tag = r.getString(2);
			wordClassInfo.majorClass = r.getString(3);
			wordClassInfoMap.put(wordClassId, wordClassInfo);
		}
		r.close();
		
		//	Build the work part parent map and the work descendants map.
		
		workPartParentMap = new HashMap();
		workDescendantsMap = new HashMap();
		r = s.executeQuery(
			"select id, parent, work from workpart");
		while (r.next()) {
			Long workPartId = (Long)r.getObject(1);
			Long parentId = (Long)r.getObject(2);
			Long workId = (Long)r.getObject(3);
			workPartParentMap.put(workPartId, parentId);
			HashSet descendants = (HashSet)workDescendantsMap.get(workId);
			if (descendants == null) {
				descendants = new HashSet();
				workDescendantsMap.put(workId, descendants);
			}
			descendants.add(workPartId);
		}
		r.close();
		
		s.close();

	}
	
	/**	Initializes the exporter/importers.
	 *
	 *	@throws	Exception
	 */
	 
	private static void initExporterImporters ()
		throws Exception
	{
		lemmaWorkCountsExporterImporter =
			new TableExporterImporter(
				"lemmaworkcounts",
				"work,lemma,termFreq,rank1,rank2,numMajorClass",
				tempDirPath + "lemmaWorkCounts.txt", 
				false);
		lemmaCorpusCountsExporterImporter =
			new TableExporterImporter(
				"lemmacorpuscounts",
				"corpus,lemma,tag_string,tag_charset,wordClass,majorClass," +
				"colFreq,docFreq,rank1,rank2,numMajorClass",
				tempDirPath + "lemmaCorpusCounts.txt", 
				false);
		lemmaPosSpellingCountsExporterImporter =
			new TableExporterImporter(
				"lemmaposspellingcounts ",
				"kind,corpus,work,workPart," +
				"lemma,pos,spelling_string,spelling_charset,freq,freqFirstWordPart",
				tempDirPath + "wordFormCounts.txt", 
				false);
		wordCountExporterImporter =
			new TableExporterImporter(
				"wordcount",
				"word_string,word_charset,wordForm,workPart,work,wordCount",
				tempDirPath + "wordCount.txt", 
				false);
		totalWordFormCountExporterImporter =
			new TableExporterImporter(
				"totalwordformcount",
				"wordForm,workPart,work,wordFormCount",
				tempDirPath + "totalWordFormCount.txt", 
				false);
	}
	
	/**	Updates word form counts.
	 *
	 *	@param	kind		Word form kind.
	 *
	 *	@param	str			String.
	 *
	 *	@param	charset		Character set.
	 *
	 *	@throws	Exception
	 */
	 
	private static void updateWordFormCounts (int kind, String str,
		byte charset)
			throws Exception
	{
		HashMap map = wordFormCounterArray[kind];
		Long partId = workPartId;
		Spelling spelling = new Spelling(str, charset);
		while (partId != null) {
			HashMap subMap = (HashMap)map.get(partId);
			FrequencyCounter fc = (FrequencyCounter)subMap.get(spelling);
			if (fc == null) {
				fc = new FrequencyCounter();
				subMap.put(spelling, fc);
			}
			fc.freq++;
			partId = (Long)workPartParentMap.get(partId);
		}
	}
	
	/**	Accumulates lemma corpus counts.
	 *
	 *	<p>After a work has been counted, we add the counts for the work
	 *	to the counts for the current corpus.
	 *
	 *	@throws	Exception
	 */
	
	private static void accumulateLemmaCorpusCounts ()
		throws Exception
	{
		for (Iterator it = lemmaWorkCountsMap.keySet().iterator(); 
			it.hasNext(); ) 
		{
			Long lemmaId = (Long)it.next();
			WorkCounter workCounter = 
				(WorkCounter)lemmaWorkCountsMap.get(lemmaId);
			CorpusCounter corpusCounter = 
				(CorpusCounter)lemmaCorpusCountsMap.get(lemmaId);
			if (corpusCounter == null) {
				corpusCounter = new CorpusCounter();
				corpusCounter.wordClassId = workCounter.wordClassId;
				corpusCounter.lemmaTagString = workCounter.lemmaTagString;
				corpusCounter.lemmaTagCharset = workCounter.lemmaTagCharset;
				corpusCounter.majorClass = workCounter.majorClass;
				lemmaCorpusCountsMap.put(lemmaId, corpusCounter);
			}
			corpusCounter.colFreq += workCounter.termFreq;
			corpusCounter.docFreq++;
		}
	}
	
	/**	Computes lemma ranks in a work.
	 *
	 *	@throws Exception
	 */
	
	private static void computeLemmaWorkRanks ()
		throws Exception
	{
		Collection values = lemmaWorkCountsMap.values();
		WorkCounter[] a = (WorkCounter[])values.toArray(
			new WorkCounter[values.size()]);
		Arrays.sort(a,
			new Comparator() {
				public int compare (Object o1, Object o2) {
					WorkCounter wc1 = (WorkCounter)o1;
					WorkCounter wc2 = (WorkCounter)o2;
					int k = wc1.majorClass.compareTo(wc2.majorClass);
					if (k != 0) return k;
					return wc2.termFreq - wc1.termFreq;
				}
			}
		);
		int majorClassStartIndex = 0;
		int majorClassEndIndex = 0;
		while (majorClassStartIndex < a.length) {
			majorClassEndIndex = majorClassStartIndex + 1;
			String majorClass = a[majorClassStartIndex].majorClass;
			while (majorClassEndIndex < a.length) {
				if (!a[majorClassEndIndex].majorClass.equals(majorClass)) break;
				majorClassEndIndex++;
			}
			int numMajorClass = majorClassEndIndex - majorClassStartIndex;
			int runStartIndex = majorClassStartIndex;
			int runEndIndex = majorClassStartIndex;
			while (runStartIndex < majorClassEndIndex) {
				runEndIndex = runStartIndex + 1;
				int termFreq = a[runStartIndex].termFreq;
				while (runEndIndex < majorClassEndIndex) {
					if (a[runEndIndex].termFreq != termFreq) break;
					runEndIndex++;
				}
				int rank1 = runStartIndex - majorClassStartIndex + 1;
				int rank2 = runEndIndex - majorClassStartIndex;
				for(int i = runStartIndex; i < runEndIndex; i++) {
					a[i].rank1 = rank1;
					a[i].rank2 = rank2;
					a[i].numMajorClass = numMajorClass;
				}
				runStartIndex = runEndIndex;
			}
			majorClassStartIndex = majorClassEndIndex;
		}
	}
	
	/**	Computes lemma ranks in a corpus.
	 *
	 *	@throws Exception
	 */
	
	private static void computeLemmaCorpusRanks () 
		throws Exception
	{
		Collection values = lemmaCorpusCountsMap.values();
		CorpusCounter[] a = (CorpusCounter[])values.toArray(
			new CorpusCounter[values.size()]);
		Arrays.sort(a,
			new Comparator() {
				public int compare (Object o1, Object o2) {
					CorpusCounter cc1 = (CorpusCounter)o1;
					CorpusCounter cc2 = (CorpusCounter)o2;
					int k = cc1.majorClass.compareTo(cc2.majorClass);
					if (k != 0) return k;
					return cc2.colFreq - cc1.colFreq;
				}
			}
		);
		int majorClassStartIndex = 0;
		int majorClassEndIndex = 0;
		while (majorClassStartIndex < a.length) {
			majorClassEndIndex = majorClassStartIndex + 1;
			String majorClass = a[majorClassStartIndex].majorClass;
			while (majorClassEndIndex < a.length) {
				if (!a[majorClassEndIndex].majorClass.equals(majorClass)) break;
				majorClassEndIndex++;
			}
			int numMajorClass = majorClassEndIndex - majorClassStartIndex;
			int runStartIndex = majorClassStartIndex;
			int runEndIndex = majorClassStartIndex;
			while (runStartIndex < majorClassEndIndex) {
				runEndIndex = runStartIndex + 1;
				int colFreq = a[runStartIndex].colFreq;
				while (runEndIndex < majorClassEndIndex) {
					if (a[runEndIndex].colFreq != colFreq) break;
					runEndIndex++;
				}
				int rank1 = runStartIndex - majorClassStartIndex + 1;
				int rank2 = runEndIndex - majorClassStartIndex;
				for(int i = runStartIndex; i < runEndIndex; i++) {
					a[i].rank1 = rank1;
					a[i].rank2 = rank2;
					a[i].numMajorClass = numMajorClass;
				}
				runStartIndex = runEndIndex;
			}
			majorClassStartIndex = majorClassEndIndex;
		}
	}
	
	/**	Prints lemma work counts.
	 *
	 *	@throws Exception
	 */
	 
	private static void printLemmaWorkCounts ()
		throws Exception
	{
		for (Iterator it = lemmaWorkCountsMap.keySet().iterator(); 
			it.hasNext(); ) 
		{
			Long lemmaKey = (Long)it.next();
			WorkCounter wc = 
				(WorkCounter)lemmaWorkCountsMap.get(lemmaKey);
			lemmaWorkCountsExporterImporter.print(workId);
			lemmaWorkCountsExporterImporter.print(lemmaKey);
			lemmaWorkCountsExporterImporter.print(wc.termFreq);
			lemmaWorkCountsExporterImporter.print(wc.rank1);
			lemmaWorkCountsExporterImporter.print(wc.rank2);
			lemmaWorkCountsExporterImporter.print(wc.numMajorClass);
			lemmaWorkCountsExporterImporter.println();
		}
	}
	
	/**	Prints word form counter array counts.
	 *
	 *	@throws Exception
	 */
	 
	private static void printWordFormCounterArray ()
		throws Exception
	{
		for (int kind = 0; kind < WordForms.NUMBEROFWORDFORMS; kind++) {
			Map map = wordFormCounterArray[kind];
			for (Iterator it1 = map.keySet().iterator(); it1.hasNext(); ) {
				Long workPartId = (Long)it1.next();
				Map subMap = (Map)map.get(workPartId);
				int total = 0;
				for (Iterator it2 = subMap.keySet().iterator(); 
					it2.hasNext(); )
				{
					Spelling formSpelling = (Spelling)it2.next();
					FrequencyCounter fc = 
						(FrequencyCounter)subMap.get(formSpelling);
					int freq = fc.freq;
					wordCountExporterImporter.print(formSpelling.getString());
					wordCountExporterImporter.print(formSpelling.getCharset());
					wordCountExporterImporter.print(kind);
					wordCountExporterImporter.print(workPartId);
					wordCountExporterImporter.print(workId);
					wordCountExporterImporter.print(freq);
					wordCountExporterImporter.println();
					total += freq;
				}
				totalWordFormCountExporterImporter.print(kind);
				totalWordFormCountExporterImporter.print(workPartId);
				totalWordFormCountExporterImporter.print(workId);
				totalWordFormCountExporterImporter.print(total);
				totalWordFormCountExporterImporter.println();
			}
		}
	}
	
	/**	Prints corpus lemma/pos/spelling counts.
	 *
	 *	@throws Exception
	 */
	 
	private static void printCorpusLemmaPosSpellingCounts ()
		throws Exception
	{
		for (Iterator it = corpusLemmaPosSpellingCountsMap.keySet().iterator(); 
			it.hasNext(); ) 
		{
			LemmaPosSpelling lps = (LemmaPosSpelling)it.next();
			FrequencyCounter fc = 
				(FrequencyCounter)corpusLemmaPosSpellingCountsMap.get(lps);
			lemmaPosSpellingCountsExporterImporter.print(LemmaPosSpellingCounts.CORPUS_COUNT);
			lemmaPosSpellingCountsExporterImporter.print(corpusId);
			lemmaPosSpellingCountsExporterImporter.printNull();
			lemmaPosSpellingCountsExporterImporter.printNull();
			lemmaPosSpellingCountsExporterImporter.print(lps.lemmaId);
			lemmaPosSpellingCountsExporterImporter.print(lps.posId);
			lemmaPosSpellingCountsExporterImporter.print(lps.string);
			lemmaPosSpellingCountsExporterImporter.print(lps.charset);
			lemmaPosSpellingCountsExporterImporter.print(fc.freq);
			lemmaPosSpellingCountsExporterImporter.print(fc.freqFirstWordPart);
			lemmaPosSpellingCountsExporterImporter.println();
		}
	}
	
	/**	Prints work lemma/pos/spelling counts.
	 *
	 *	@throws Exception
	 */
	 
	private static void printWorkLemmaPosSpellingCounts ()
		throws Exception
	{
		for (Iterator it = workLemmaPosSpellingCountsMap.keySet().iterator(); 
			it.hasNext(); ) 
		{
			LemmaPosSpelling lps = (LemmaPosSpelling)it.next();
			FrequencyCounter fc = 
				(FrequencyCounter)workLemmaPosSpellingCountsMap.get(lps);
			lemmaPosSpellingCountsExporterImporter.print(LemmaPosSpellingCounts.WORK_COUNT);
			lemmaPosSpellingCountsExporterImporter.print(corpusId);
			lemmaPosSpellingCountsExporterImporter.print(workId);
			lemmaPosSpellingCountsExporterImporter.printNull();
			lemmaPosSpellingCountsExporterImporter.print(lps.lemmaId);
			lemmaPosSpellingCountsExporterImporter.print(lps.posId);
			lemmaPosSpellingCountsExporterImporter.print(lps.string);
			lemmaPosSpellingCountsExporterImporter.print(lps.charset);
			lemmaPosSpellingCountsExporterImporter.print(fc.freq);
			lemmaPosSpellingCountsExporterImporter.print(fc.freqFirstWordPart);
			lemmaPosSpellingCountsExporterImporter.println();
		}
	}
	
	/**	Prints work part lemma/pos/spelling counts.
	 *
	 *	@throws Exception
	 */
	 
	private static void printWorkPartLemmaPosSpellingCounts ()
		throws Exception
	{
		for (Iterator it = workPartLemmaPosSpellingCountsMap.keySet().iterator(); 
			it.hasNext(); ) 
		{
			LemmaPosSpelling lps = (LemmaPosSpelling)it.next();
			FrequencyCounter fc = 
				(FrequencyCounter)workPartLemmaPosSpellingCountsMap.get(lps);
			lemmaPosSpellingCountsExporterImporter.print(LemmaPosSpellingCounts.WORK_PART_COUNT);
			lemmaPosSpellingCountsExporterImporter.print(corpusId);
			lemmaPosSpellingCountsExporterImporter.print(workId);
			lemmaPosSpellingCountsExporterImporter.print(workPartId);
			lemmaPosSpellingCountsExporterImporter.print(lps.lemmaId);
			lemmaPosSpellingCountsExporterImporter.print(lps.posId);
			lemmaPosSpellingCountsExporterImporter.print(lps.string);
			lemmaPosSpellingCountsExporterImporter.print(lps.charset);
			lemmaPosSpellingCountsExporterImporter.print(fc.freq);
			lemmaPosSpellingCountsExporterImporter.print(fc.freqFirstWordPart);
			lemmaPosSpellingCountsExporterImporter.println();
		}
	}
	
	/**	Prints lemma corpus counts.
	 *
	 *	@throws Exception
	 */
	 
	private static void printLemmaCorpusCounts ()
		throws Exception
	{
		for (Iterator it = lemmaCorpusCountsMap.keySet().iterator(); 
			it.hasNext(); ) 
		{
			Long lemmaKey = (Long)it.next();
			CorpusCounter cc = 
				(CorpusCounter)lemmaCorpusCountsMap.get(lemmaKey);
			lemmaCorpusCountsExporterImporter.print(corpusId);
			lemmaCorpusCountsExporterImporter.print(lemmaKey);
			lemmaCorpusCountsExporterImporter.print(cc.lemmaTagString);
			lemmaCorpusCountsExporterImporter.print(cc.lemmaTagCharset);
			lemmaCorpusCountsExporterImporter.print(cc.wordClassId);
			lemmaCorpusCountsExporterImporter.print(cc.majorClass);
			lemmaCorpusCountsExporterImporter.print(cc.colFreq);
			lemmaCorpusCountsExporterImporter.print(cc.docFreq);
			lemmaCorpusCountsExporterImporter.print(cc.rank1);
			lemmaCorpusCountsExporterImporter.print(cc.rank2);
			lemmaCorpusCountsExporterImporter.print(cc.numMajorClass);
			lemmaCorpusCountsExporterImporter.println();
		}
	}
	
	/**	Counts a word part.
	 *
	 *	@throws Exception
	 */
	 
	private static void countWordPart ()
		throws Exception
	{
		Long lemPosKey = wordPartData.lemPosId;
		LemPosInfo lemPosInfo = (LemPosInfo)lemPosInfoMap.get(lemPosKey);
		long lemmaId = lemPosInfo.lemmaId;
		Long lemmaKey = new Long(lemmaId);
		long posId = lemPosInfo.posId;
		LemmaInfo lemmaInfo = (LemmaInfo)lemmaInfoMap.get(lemmaKey);
		long wordClassId = lemmaInfo.wordClassId;
		WordClassInfo wordClassInfo = (WordClassInfo)wordClassInfoMap.get(
			new Long(wordClassId));
		
		//	Increment the lemma/work counter.
		
		WorkCounter workCounter = (WorkCounter)lemmaWorkCountsMap.get(lemmaKey);
		if (workCounter == null) {
			workCounter = new WorkCounter();
			workCounter.wordClassId = lemmaInfo.wordClassId;
			workCounter.lemmaTagString = lemmaInfo.tagString;
			workCounter.lemmaTagCharset = lemmaInfo.tagCharset;
			workCounter.majorClass = wordClassInfo.majorClass;
			lemmaWorkCountsMap.put(lemmaKey, workCounter);
		}
		workCounter.termFreq++;
		
		//	Increment the corpus lemma/pos/spelling counters.
		
		LemmaPosSpelling lps = new LemmaPosSpelling(lemmaId, posId, 
			wordData.spelling, wordData.charset);
		FrequencyCounter fc = (FrequencyCounter)corpusLemmaPosSpellingCountsMap.get(lps);
		if (fc == null) {
			fc = new FrequencyCounter();
			corpusLemmaPosSpellingCountsMap.put(lps, fc);
		}
		fc.freq++;
		if (firstWordPart) fc.freqFirstWordPart++;
		
		//	Increment the work lemma/pos/spelling counters.
		
		fc = (FrequencyCounter)workLemmaPosSpellingCountsMap.get(lps);
		if (fc == null) {
			fc = new FrequencyCounter();
			workLemmaPosSpellingCountsMap.put(lps, fc);
		}
		fc.freq++;
		if (firstWordPart) fc.freqFirstWordPart++;
		
		//	Increment the work part lemma/pos/spelling counters.
		
		fc = (FrequencyCounter)workPartLemmaPosSpellingCountsMap.get(lps);
		if (fc == null) {
			fc = new FrequencyCounter();
			workPartLemmaPosSpellingCountsMap.put(lps, fc);
		}
		fc.freq++;
		if (firstWordPart) fc.freqFirstWordPart++;
		
		//	Append the word class for the current word part.
		
		if (!firstWordPart) wordClassesBuffer.append("-");
		wordClassesBuffer.append(wordClassInfo.tag);
		if (lemmaInfo.homonym > 0) wordClassesBuffer.append(" " + 
			lemmaInfo.homonym);
		firstWordPart = false;
		
		//	Update the LEMMA and WORDCLASS counts.
		
		updateWordFormCounts(WordForms.LEMMA, lemmaInfo.tagString, 
			lemmaInfo.tagCharset);
		
		updateWordFormCounts(WordForms.WORDCLASS, wordClassInfo.tag, 
			TextParams.ROMAN);
	}
	
	/**	Starts counting a new word.
	 *
	 *	@throws	Exception
	 */
	
	private static void startCountingWord () 
		throws Exception
	{
		//	Create a new empty buffer for accumulating word classes.
	
		wordClassesBuffer = new StringBuffer();
		firstWordPart = true;
	}
	
	/**	Ends counting a word.
	 *
	 *	@throws	Exception
	 */
	
	private static void endCountingWord () 
		throws Exception
	{
		//	Update the SPELLING counts.
	
		String str = wordData.spellingInsensitive + " (" +
			wordClassesBuffer.toString() + ")";
		updateWordFormCounts(WordForms.SPELLING, str, wordData.charset);
		
		//	Update the ISVERSE counts.
		
		switch (wordData.prosodic) {
			case Prosodic.PROSE:
				str = "N";
				break;
			case Prosodic.VERSE:
				str = "Y";
				break;
			case Prosodic.UNKNOWN:
				str = "U";
				break;
		}
		updateWordFormCounts(WordForms.ISVERSE, str, TextParams.ROMAN);
		
		//	Update the METRICALSHAPE counts.
		
		str = wordData.metricalShape;
		if (str == null) str="";
		updateWordFormCounts(WordForms.METRICALSHAPE, str, TextParams.ROMAN);
		
		//	Update the SPEAKERGENDER and SPEAKERMORTALITY counts.
		
		Long speechId = wordData.speechId;
		
		if (speechId == null) {
		
			updateWordFormCounts(WordForms.SPEAKERGENDER, "U", 
				TextParams.ROMAN);
			updateWordFormCounts(WordForms.SPEAKERMORTALITY, "U",
				TextParams.ROMAN);
				
		} else {
		
			SpeechData speechData = 
				(SpeechData)speechDataMap.get(speechId);
			
			switch (speechData.gender) {
				case Gender.MALE:
					str = "M";
					break;
				case Gender.FEMALE:
					str = "F";
					break;
				case Gender.UNCERTAIN_MIXED_OR_UNKNOWN:
					str = "U";
					break;
			}
			updateWordFormCounts(WordForms.SPEAKERGENDER, str, 
				TextParams.ROMAN);
			
			switch (speechData.mortality) {
				case Mortality.MORTAL:
					str = "M";
					break;
				case Mortality.IMMORTAL_OR_SUPERNATURAL:
					str = "I";
					break;
				case Mortality.UNKNOWN_OR_OTHER:
					str = "U";
					break;
			}
			updateWordFormCounts(WordForms.SPEAKERMORTALITY, str, 
				TextParams.ROMAN);
		
		}
			
	}
	
	/**	Starts counting a new work part.
	 *
	 *	@throws	Exception
	 */
	
	private static void startCountingWorkPart () 
		throws Exception
	{
		//	Create a new lemma/pos/spelling counter map for the work part.
		
		workPartLemmaPosSpellingCountsMap = new HashMap();
	
		//	Read the speech data for the current work part.
	
		speechDataMap = new HashMap();
		while (true) {
			SpeechData speechData = speechDataReader.get();
			if (speechData == null) return;
			if (!workPartId.equals(speechData.workPartId)) {
				speechDataReader.put(speechData);
				return;
			}
			speechDataMap.put(speechData.speechId, speechData);
		}
	}
	
	/**	Ends counting a work part.
	 *
	 *	@throws	Exception
	 */
	
	private static void endCountingWorkPart () 
		throws Exception
	{
		
		//	Print the lemma/pos/spelling counts for the work part.
		
		printWorkPartLemmaPosSpellingCounts();
	}
	
	/**	Starts counting a new work.
	 *
	 *	@throws	Exception
	 */
	
	private static void startCountingWork () 
		throws Exception
	{
		//	Create a new lemma counts map for the work.
	
		lemmaWorkCountsMap = new HashMap();
		
		//	Create a new word form counter array for the work.
		
		wordFormCounterArray = new HashMap[WordForms.NUMBEROFWORDFORMS];
		HashSet descendants = (HashSet)workDescendantsMap.get(workId);
		for (int i = 0; i < WordForms.NUMBEROFWORDFORMS; i++) {
			HashMap map = new HashMap();
			wordFormCounterArray[i] = map;
			for (Iterator it = descendants.iterator(); it.hasNext(); ) {
				Long workPartId = (Long)it.next();
				map.put(workPartId, new HashMap());
			}
		}
		
		//	Create a new lemma/pos/spelling counter map for the work.
		
		workLemmaPosSpellingCountsMap = new HashMap();
	}
	
	/**	Ends counting a work.
	 *
	 *	@throws	Exception
	 */
	
	private static void endCountingWork () 
		throws Exception
	{
		//	Add the lemma work counts to the lemma corpus counts.
	
		accumulateLemmaCorpusCounts();
		
		//	Compute the lemma ranks for the work.
		
		computeLemmaWorkRanks();
		
		//	Print the lemma counts for the work.
		
		printLemmaWorkCounts();
		
		//	Print the word form counts for all the parts of the work.
		
		printWordFormCounterArray();
		
		//	Print the lemma/pos/spelling counts for the work.
		
		printWorkLemmaPosSpellingCounts();
	}
	
	/**	Starts counting a new corpus.
	 *
	 *	@throws	Exception
	 */
	
	private static void startCountingCorpus () 
		throws Exception
	{
		//	Create a new lemma counts map for the corpus.
	
		lemmaCorpusCountsMap = new HashMap();
		
		//	Create a new lemma/pos/spelling counts map for the corpus.
		
		corpusLemmaPosSpellingCountsMap = new HashMap();
	}
	
	/**	Ends counting a corpus.
	 *
	 *	@throws	Exception
	 */
	
	private static void endCountingCorpus () 
		throws Exception
	{
		//	Compute the lemma ranks for the corpus. 
	
		computeLemmaCorpusRanks();
		
		//	Print the lemma counts for the corpus.
		
		printLemmaCorpusCounts();
		
		//	Print the lemma/pos/spelling counts for the corpus.
		
		printCorpusLemmaPosSpellingCounts();
	}
	
	/**	Reads the data for a single word.
	 *
	 *	@return		True if done (no more words in current work part).
	 *
	 *	@throws Exception
	 */
	
	private static boolean readWordData ()
		throws Exception
	{
		wordData = wordDataReader.get();
		if (wordData == null) return true;
		if (!workPartId.equals(wordData.workPartId)) {
			wordDataReader.put(wordData);
			return true;
		}
		wordId = wordData.wordId;
		startCountingWord();
		int numParts = 0;
		while (true) {
			wordPartData = wordPartDataReader.get();
			if (wordPartData == null) break;
			if (!wordId.equals(wordPartData.wordId)) break;
			countWordPart();
			numParts++;
		}
		wordPartDataReader.put(wordPartData);
		endCountingWord();
		return false;
	}
	
	/**	Reads the data for a single work part.
	 *
	 *	@return		True if done (no more work parts in current work).
	 *
	 *	@throws Exception
	 */
	
	private static boolean readWorkPartData ()
		throws Exception
	{
		WordData wordData = wordDataReader.get();
		wordDataReader.put(wordData);
		if (wordData == null) return true;
		if (!workId.equals(wordData.workId)) return true;
		workPartId = wordData.workPartId;
		startCountingWorkPart();
		boolean done = false;
		while (!done) done = readWordData();
		endCountingWorkPart();
		return false;
	}
	
	/**	Reads the data for a single work.
	 *
	 *	@return		True if done (no more works in current corpus).
	 *
	 *	@throws Exception
	 */
	
	private static boolean readWorkData ()
		throws Exception
	{
		WordData wordData = wordDataReader.get();
		wordDataReader.put(wordData);
		if (wordData == null) return true;
		if (!corpusId.equals(wordData.corpusId)) return true;
		workId = wordData.workId;
		startCountingWork();
		boolean done = false;
		while (!done) done = readWorkPartData();
		endCountingWork();
		return false;
	}
	
	/**	Reads the data for a single corpus.
	 *
	 *	@return		True if done (no more corpora to read).
	 *
	 *	@throws Exception
	 */
	
	private static boolean readCorpusData ()
		throws Exception
	{
		WordData wordData = wordDataReader.get();
		wordDataReader.put(wordData);
		if (wordData == null) return true;
		corpusId = wordData.corpusId;
		startCountingCorpus();
		boolean done = false;
		while (!done) done = readWorkData();
		endCountingCorpus();
		return false;
	}
	
	/**	Deletes old counts. 
	 *
	 *	@throws		Exception
	 */
	
	private static void deleteOldCounts ()
		throws Exception
	{
		Statement s = c.createStatement();
		s.executeUpdate("delete from lemmaworkcounts");
		s.executeUpdate("delete from lemmacorpuscounts");
		s.executeUpdate("delete from lemmaposspellingcounts");
		s.executeUpdate("delete from wordcount");
		s.executeUpdate("delete from totalwordformcount");
		s.close();
	}
	
	/**	Imports one count table.
	 *
	 *	@param	tableName			Table name.
	 *
	 *	@param	exporterImporter	Table exporter/importer.
	 *
	 *	@throws Exception
	 */
	 
	private static void importOneTable (String tableName,
		TableExporterImporter exporterImporter)
			throws Exception
	{
		long startTime = System.currentTimeMillis();
		exporterImporter.close();
		int ct = exporterImporter.importData(c);
		long endTime = System.currentTimeMillis();
		System.out.println(
			Formatters.formatIntegerWithCommas(ct) + 
			(ct == 1 ? " object" : " objects") +
			" imported into " + tableName + " table in " +
			BuildUtils.formatElapsedTime(startTime, endTime));
	}
	
	/**	Imports the count tables.
	 *
	 *	@throws	Exception
	 */
	 
	private static void importTables ()
		throws Exception
	{
		System.out.println("Importing objects into MySQL tables");

		importOneTable("LemmaCorpusCounts", 
			lemmaCorpusCountsExporterImporter);
		importOneTable("LemmaWorkCounts", 
			lemmaWorkCountsExporterImporter);
		importOneTable("LemmaPosSpellingCounts", 
			lemmaPosSpellingCountsExporterImporter);
		importOneTable("WordCount", 
			wordCountExporterImporter);
		importOneTable("TotalWordFormCount", 
			totalWordFormCountExporterImporter);
	}

	/**	The main program.
	 *
	 *	@param	args		Command line arguments.
	 */
	
	public static void main (final String args[]) {
	
		try {
			
			if (args.length != 6) {
				System.out.println("Usage: CalculateCounts dbname username password " +
					"wordDataPath wordPartDataPath speechDataPath");
				System.exit(1);
			}
			
			System.out.println("Merging files and counting objects");
			
			c = BuildUtils.getConnection(args[0], args[1], args[2]);
			
			readDatabaseInfo();
			
			wordDataReader = new WordDataReader(args[3]);
			wordPartDataReader = new WordPartDataReader(args[4]);
			speechDataReader = new SpeechDataReader(args[5]);
			
			tempDirPath = BuildUtils.createTempDir() + "/";
			initExporterImporters();
			
			boolean done = false;
			while (!done) done = readCorpusData();
			
			wordDataReader.close();
			wordPartDataReader.close();
			speechDataReader.close();
			
			deleteOldCounts();
			importTables();
			BuildUtils.deleteTempDir();
			
			c.close();
			
			BuildUtils.reportNumErrors();
				
		} catch (Exception e) {
		
			e.printStackTrace();
			System.exit(1);
			
		}
		
	}

	/**	Hides the default no-arg constructor.
	 */
	 
	private CalculateCounts () {
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

