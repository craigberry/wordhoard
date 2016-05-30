package edu.northwestern.at.wordhoard.tools.martin;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;

import org.w3c.dom.*;

import edu.northwestern.at.wordhoard.tools.TaggingDataProvider;
import edu.northwestern.at.wordhoard.tools.PrettyPrint;
import edu.northwestern.at.utils.xml.*;

/**	Maps old to new Greek parts of speech.
 *
 *	<p>Usage:
 *
 *	<p><code>MapGreekPos oldDir newDir martinData</code>
 *
 *	<p>oldDir = Path to old data dir.
 *
 *	<p>newDir = Path to new data dir.
 *
 *	<p>martinData = Path to data file for Martin's Access database table.
 *
 *	<p>This is a program we wrote to run once, as part of the project to
 *	convert WordHoard to Martin's new NUPOS part of speech tagset. While
 *	this program is no longer used, we keep it in the source code tree
 *	because we may need some kind of similar program some day.
 *
 *	<p>oldDir is the path to the old WordHaord data directory, containing
 *	the XML data files for the old part of speech tagset. MapGreekPos reads
 *	the old Greek part of speech file in pos/greek-pos.xml and the old
 *	EGE corpus work files in works/ege.
 *
 *	<p>newDir is the path to the new WordHoard data directory, containing
 *	the XML data files for the new part of speech tagset. MapGreekPos read
 *	the new part of speech file in pos.xml and writes new versions the
 *	EGE corpus work files in works/ege, with the old pos and lemma tagging
 *	data mapped to their new values.
 *
 *	<p>martinData is an output file to which MapGreekPos writes tab-delimited
 *	Greek word occurrence tagging data. Martin used this file to populate
 *	the NUPOS_GreekData table in his NUPOS Access database.
 */
 
/*

java -Xmx500m \
   edu/northwestern/at/wordhoard/tools/martin/MapGreekPos \
   ../olddev/data \
   data \
   martin/NUPOS_GreekData.txt

*/
 
public class MapGreekPos {

	/**	Special case one word with bad pos code. */

	private static String SPECIAL_CASE_WORD_ID = "ege-404003202";
	private static String SPECIAL_CASE_NUPOS_POS = "4645201";
	private static String SPECIAL_CASE_OLDPOS_POS = "aor";
	
	/**	PrintWriter for Martin's data file. */
	
	private static PrintWriter martinDataOut;
	
	/**	Pos mapping.
	 *
	 *	<p>Maps old parts of speech to new parts of speech. 
	 */
	 
	private static Map posMap = new HashMap();
	
	static {
		posMap.put(SPECIAL_CASE_OLDPOS_POS, SPECIAL_CASE_NUPOS_POS);
	}
	
	/**	Word class mapping. 
	 *
	 *	<p>Maps old word classes to new word classes.
	 */
	
	private static Map wordClassMap = new HashMap();
	
	static {
		wordClassMap.put("at", "dt");
		wordClassMap.put("pcl", "ptl");
		wordClassMap.put("cj", "cj");
		wordClassMap.put("npg", "ng");
		wordClassMap.put("pnp", "pn");
		wordClassMap.put("aj", "j");
		wordClassMap.put("v", "v");
		wordClassMap.put("prp", "pp");
		wordClassMap.put("np", "np");
		wordClassMap.put("n", "n");
		wordClassMap.put("pron_adj", "pj");
		wordClassMap.put("vp", "cop");
		wordClassMap.put("pni", "pi");
		wordClassMap.put("av", "av");
		wordClassMap.put("nu", "nu");
		wordClassMap.put("neg", "xx");
		wordClassMap.put("pnr", "pr");
		wordClassMap.put("ajp", "jp");
		wordClassMap.put("pnq", "pq");
		wordClassMap.put("pos_pron", "po");
		wordClassMap.put("itrg", "pq");
		wordClassMap.put("dem_pron", "pd");
		wordClassMap.put("itj", "uh");
		wordClassMap.put("ajn", "jn");		
	}
	
	/**	Tagging data provider. */
	
	private static TaggingDataProvider provider =
		new TaggingDataProvider() {
			public String[] getMorph (Element el, String wordTag)
				throws Exception
			{
				String oldLemma = el.getAttribute("lemma");
				String oldPos = el.getAttribute("pos");
				String[] morph = new String[] {"", ""};
				
				int i = oldLemma.indexOf('(') + 1;
				int j = oldLemma.indexOf(')', i);
				if (i <= 2 || j <= i) {
					System.out.println("Syntax error in lemma for word " + wordTag);
					return morph;
				}
				String oldWordClass = oldLemma.substring(i, j);
				String newWordClass = (String)wordClassMap.get(oldWordClass);
				if (newWordClass == null) {
					System.out.println("Could not map word class for word " + wordTag);
					return morph;
				}
				String newLemma = oldLemma.substring(0, i) + newWordClass +
					oldLemma.substring(j);
				morph[0] = newLemma;
				
				String newPos = (String)posMap.get(oldPos);
				if (newPos == null) {
					System.out.println("Could not map pos for word " + wordTag);
					return morph;
				}
				morph[1] = newPos;
				
				martinDataOut.println("," +
					"\"" + wordTag + "\"," +
					",," +
					"\"" + newPos + "\"," +
					"\"" + newLemma + "\"," +
					",,,,,");
				
				return morph;
			}
		};


	/**	Checks for a part of speech match.
	 *
	 *	@param	oldEl		Old Greek "pos" element.
	 *
	 *	@param	newEl		New Greek "pos" element.
	 *
	 *	@return		True if match.
	 *
	 *	@throws Exception
	 */
	 
	private static boolean match (Element oldEl, Element newEl)
		throws Exception
	{
		String oldMood = oldEl.getAttribute("mood");
		if (oldMood.equals("imperat")) oldMood = "impt";
		if (oldMood.equals("imper")) oldMood = "impt";
		if (oldMood.equals("part")) oldMood = "ppl";
		String oldCase = oldEl.getAttribute("case");
		if (oldCase.equals("locative")) oldCase = "loc";
		if (oldCase.equals("adverbial")) oldCase = "adv";
		String newMood = newEl.getAttribute("mood");
		if (newMood.equals("imperat")) newMood = "impt";
		if (newMood.equals("imper")) newMood = "impt";
		if (newMood.equals("part")) newMood = "ppl";
		String newCase = newEl.getAttribute("case");
		if (newCase.equals("locative")) newCase = "loc";
		if (newCase.equals("adverbial")) newCase = "adv";
		return
			oldEl.getAttribute("tense").equals(newEl.getAttribute("tense")) &&
			oldMood.equals(newMood) &&
			oldEl.getAttribute("voice").equals(newEl.getAttribute("voice")) &&
			oldCase.equals(newCase) &&
			oldEl.getAttribute("gender").equals(newEl.getAttribute("gender")) &&
			oldEl.getAttribute("person").equals(newEl.getAttribute("person")) &&
			oldEl.getAttribute("number").equals(newEl.getAttribute("number"));
	}

	/**	Map one part of speech.
	 *
	 *	@param	oldEl		Old Greek "pos" element.
	 *
	 *	@param	newList		List of new Greek "pos" elements.
	 *
	 *	@throws	Exception
	 */
	 
	private static void mapOnePos (Element oldEl, ArrayList newList)
		throws Exception
	{
		int ct = 0;
		ArrayList matches = new ArrayList();
		for (Iterator it = newList.iterator(); it.hasNext(); ) {
			Element newEl = (Element)it.next();
			if (match(oldEl, newEl)) {
				ct++;
				matches.add(newEl.getAttribute("id"));
			}
		}
		String oldId = oldEl.getAttribute("id");
		if (ct == 0) {
			if (!oldId.equals(SPECIAL_CASE_OLDPOS_POS))
				System.out.println("No match found for old pos " + oldId);
		} else if (ct > 1) {
			System.out.println("Multiple matches found for old pos " + oldId);
			System.out.print("   ");
			for (Iterator it = matches.iterator(); it.hasNext(); )
				System.out.print(it.next() + "   ");
			System.out.println();
		} else {
			String newId = (String)matches.get(0);
			posMap.put(oldId, newId);
		}
	}
	
	/**	Finds duplicate parts of speech.
	 *
	 *	@param	list	Parts of speech list.
	 *
	 *	@param	label	Label: "old" or "new".
	 *
	 *	@throws	Exception
	 */
	 
	private static void findDuplicates (ArrayList list, String label)
		throws Exception
	{
		HashSet dupSet = new HashSet();
		for (Iterator it1 = list.iterator(); it1.hasNext(); ) {
			Element el1 = (Element)it1.next();
			if (dupSet.contains(el1)) continue;
			ArrayList dupList = new ArrayList();
			int ct = 0;
			for (Iterator it2 = list.iterator(); it2.hasNext(); ) {
				Element el2 = (Element)it2.next();
				if (match(el1, el2)) {
					ct++;
					dupList.add(el2);
				}
			}
			if (ct > 1) {
				System.out.print("Duplicates found in " + label + " parts of speech: ");
				for (Iterator it2 = dupList.iterator(); it2.hasNext(); ) {
					Element dupEl = (Element)it2.next();
					dupSet.add(dupEl);
					System.out.print(dupEl.getAttribute("id") + " ");
				}
				System.out.println();
			}
		}
	}
	
	/**	Gets a Greek id digit.
	 *
	 *	@param	el		POS element.
	 *
	 *	@param	attr	Attribute name.
	 *
	 *	@param	vals	Array of attribute values in order 1, 2, 3, ...
	 *
	 *	@return		Digit for attribute value.
	 *
	 *	@throws Exception
	 */
	 
	public static int getDig (Element el, String attr, String[] vals)
		throws Exception
	{
		String val = el.getAttribute(attr);
		if (val.equals("")) return 0;
		int n = vals.length;
		for (int i = 0; i < n; i++) {
			if (val.equals(vals[i])) return i+1;
		}
		System.out.println("Unknown attribute value: " + attr + "=\"" +
			val + "\"");
		return 0;
	}
	
	/**	Checks numeric new Greek ids.
	 *
	 *	@param	list	New Greek parts of speech list.
	 *
	 *	@throws Exception
	 */
	 
	public static void checkIds (ArrayList list)
		throws Exception
	{
		for (Iterator it = list.iterator(); it.hasNext(); ) {
			Element el = (Element)it.next();
			int[] digits = new int[7];
			digits[0] = getDig(el, 
				"tense", 
				new String[] {"pres", "imperf", "fut", "aor", "perf", "plup", "futperf"});
			digits[1] = getDig(el,
				"mood",
				new String[] {"ind", "subj", "opt", "impt", "inf", "ppl"});
			digits[2] = getDig(el,
				"voice",
				new String[] {"act", "mp", "mid", "pass"});
			digits[3] = getDig(el,
				"case",
				new String[] {"nom", "gen", "dat", "acc", "voc", "loc", "adv"});
			digits[4] = getDig(el,
				"gender",
				new String[] {"masc", "fem", "neut", "mf"});
			digits[5] = getDig(el,
				"person",
				new String[] {"1st", "2nd", "3rd"});
			digits[6] = getDig(el,
				"number",
				new String[] {"sg", "dual", "pl"});
			String id = "";
			boolean allZerosSoFar = true;
			for (int i = 0; i < 7; i++) {
				int dig = digits[i];
				if (dig == 0 && allZerosSoFar) continue;
				allZerosSoFar = false;
				id = id + dig;
			}
			if (id.equals("")) id = "0";
			String idAttr = el.getAttribute("id");
			if (!id.equals(idAttr)) 
				System.out.println("Bad pos id " + idAttr + 
					", id does not match category values");
		}
	}

	/**	The main program.
	 *
	 *	@param	args		Command line arguments.
	 */
	
	public static void main (final String args[]) {
	
		try {
		
			int n = args.length;
			if (n != 3) {
				System.err.println("Usage: MapGreekPos oldDir newDir martinData");
				System.exit(1);
			}
			
			File oldDataDir = new File(args[0]);
			File newDataDir = new File(args[1]);
			File martinDataFile = new File(args[2]);
			
			File oldPosFile = new File(oldDataDir, "pos/greek-pos.xml");
			File newPosFile = new File(newDataDir, "pos.xml");
			Document oldPosDoc = DOMUtils.parse(oldPosFile);
			Document newPosDoc = DOMUtils.parse(newPosFile);
			Node oldRoot = DOMUtils.getChild(oldPosDoc, "WordHoardGreekPos");
			Node newRoot = DOMUtils.getChild(newPosDoc, "WordHoardPos");
			ArrayList oldList = DOMUtils.getChildren(oldRoot, "pos");
			ArrayList newList = DOMUtils.getChildren(newRoot, "pos", "language", "greek");
			checkIds(newList);
			//findDuplicates(oldList, "old");
			findDuplicates(newList, "new");
			for (Iterator it = oldList.iterator(); it.hasNext(); ) {
				Element oldEl = (Element)it.next();
				mapOnePos(oldEl, newList);
			}
			System.out.println("Built map for " + posMap.size() +
				" Greek parts of speech");
			System.out.println("Built map for " + wordClassMap.size() +
				" Greek word classes");
				
			File oldEgeDir = new File(oldDataDir, "works/ege");
			File newEgeDir = new File(newDataDir, "works/ege");
			File[] oldEgeDirContents = oldEgeDir.listFiles();
			FileOutputStream fos = new FileOutputStream(martinDataFile);
			OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
			BufferedWriter bw = new BufferedWriter(osw);
			martinDataOut = new PrintWriter(bw);
			for (int i = 0; i < oldEgeDirContents.length; i++) {
				File oldFile = oldEgeDirContents[i];
				String fileName = oldFile.getName();
				if (!fileName.endsWith(".xml")) continue;
				File newFile = new File(newEgeDir, fileName);
				System.out.println("Mapping work file " + oldFile.getCanonicalPath() +
					" --> " +  newFile.getCanonicalPath());
				Document workDoc = DOMUtils.parse(oldFile);
				PrettyPrint.prettyPrint(workDoc, newFile, provider);
			}
			martinDataOut.close();
				
		
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	
	}
	
	/**	Hides the default no-arg constructor.
	 */
	 
	private MapGreekPos () {
		throw new UnsupportedOperationException();
	}

}
