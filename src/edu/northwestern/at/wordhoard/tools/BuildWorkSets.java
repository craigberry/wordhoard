package edu.northwestern.at.wordhoard.tools;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.xml.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.tconview.*;
import edu.northwestern.at.wordhoard.model.userdata.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;

/**	Builds work sets.
 *
 *	<p>Usage:
 *
 *	<p><code>BuildWorkSets in dbname username password</code>
 *
 *	<p>in = Path to the work sets definition XML input file.
 *
 *	<p>dbname = Database name. May be in form "dbname" for a database on localhost or in 
 *	form "host/dbname" or "host:port/dbname" for a database on a remote host.
 *
 *	<p>username = MySQL username.
 *
 *	<p>password = MySQL password.
 */

public class BuildWorkSets {
	
	/**	Input file. */
	
	private static String inPath;
	
	/**	Database name. */
	
	private static String dbname;
	
	/**	MySQL usename. */
	
	private static String username;
	
	/**	MySQL password. */
	
	private static String password;
	
	/**	Number of work sets built. */
	
	private static int numWorkSets;
	
	/**	Persistence manager. */
	
	private static PersistenceManager pm;
	
	/**	DOM tree for parsed XML document. */
	
	private static Document document;
	
	/**	Parses command line arguments.
	 *
	 *	@param	args		Command line arguments.
	 */
	
	private static void parseArgs (String[] args) {
		int n = args.length;
		if (n != 4) {
			System.out.println("Usage: BuildWorkSets in dbname username password");
			System.exit(1);
		}
		inPath = args[0];
		dbname = args[1];
		username = args[2];
		password = args[3];
	}
	
	/**	Deletes the old system work sets.
	 *
	 *	@throws Exception
	 */
	 
	private static void deleteOldSystemWorkSets ()
		throws Exception
	{
		List systemWorkSets = pm.query("from WorkSet where owner='system'");
		int numOldWorkSets = systemWorkSets.size();
		if (numOldWorkSets > 0) pm.delete(systemWorkSets);
		System.out.println(Formatters.formatIntegerWithCommas(numOldWorkSets) +
			" old system work " +
			(numOldWorkSets == 1 ? "set" : "sets") +
			" deleted");
	}
	
	/**	Creates a system work set.
	 *
	 *	@param	title			Work set title.
	 *
	 *	@param	workSetParts	List of work set parts.
	 *
	 *	@throws	Excpetion
	 */
	 
	private static void createSystemWorkSet (String title, List workSetParts)
		throws Exception
	{
		WorkSet workSet = new WorkSet(title, "", "", "system", true, "", workSetParts);
		pm.save(workSet);
		System.out.println("Created system work set " + title);
		numWorkSets++;
		for (Iterator it = workSetParts.iterator(); it.hasNext(); ) {
			WorkPart workPart = (WorkPart)it.next();
			System.out.println("   " + workPart.getTag() + " " + workPart.getFullTitle());
		}
	}
	
	/**	Creates a system work set.
	 *
	 *	@param	title		Work set title.
	 *
	 *	@param	workPart	Work part.
	 *
	 *	@throws	Excpetion
	 */
	 
	private static void createSystemWorkSet (String title, WorkPart workPart)
		throws Exception
	{
		List workSetParts = new ArrayList(1);
		workSetParts.add(workPart);
		createSystemWorkSet(title, workSetParts);
	}
	
	/**	Adds a work set defined by a list of work parts.
	 *
	 *	@param	el		addWorkParts element.
	 *
	 *	@throws Exception
	 */
	 
	private static void addWorkParts (Element el) 
		throws Exception
	{
		String title = el.getAttribute("title");
		if (title.length() == 0) {
			BuildUtils.emsg("Missing required title attribute in addWorkParts element");
			return;
		}
		List workParts = new ArrayList();
		NodeList children = el.getChildNodes();
		int numChildren = children.getLength();
		for (int i = 0; i < numChildren; i++) {
			Node child = children.item(i);
			int type = child.getNodeType();
			if (type == Node.ELEMENT_NODE) {
				Element childEl = (Element)child;
				String name = childEl.getNodeName();
				if (name.equals("include")) {
					String partTag = childEl.getAttribute("id");
					if (partTag.length() == 0) {
						BuildUtils.emsg("Missing required id attribute in " +
							"include element");
						return;
					}
					WorkPart workPart = pm.getWorkPartByTag(partTag);
					if (workPart == null) {
						BuildUtils.emsg("No such work part: " + partTag);
						return;
					}
					workParts.add(workPart);
				} else {
					BuildUtils.emsg("Child element " +
						name + " ignored");
				}
			}
		}
		createSystemWorkSet(title, workParts);
	}
	
	/**	Adds the children of a work part.
	 *
	 *	@param	el		addChildren element.
	 *
	 *	@throws Exception
	 */
	 
	private static void addChildren (Element el) 
		throws Exception
	{
		String title = el.getAttribute("title");
		if (title.length() == 0) {
			BuildUtils.emsg("Missing required title attribute in addChildren element");
			return;
		}
		String partTag = el.getAttribute("id");
		if (partTag.length() == 0) {
			BuildUtils.emsg("Missing required id attribute in addChildren element");
			return;
		}
		HashSet excludeSet = new HashSet();
		NodeList children = el.getChildNodes();
		int numChildren = children.getLength();
		for (int i = 0; i < numChildren; i++) {
			Node child = children.item(i);
			int type = child.getNodeType();
			if (type == Node.ELEMENT_NODE) {
				Element childEl = (Element)child;
				String name = childEl.getNodeName();
				if (name.equals("exclude")) {
					String excludeTag = childEl.getAttribute("id");
					if (excludeTag.length() == 0) {
						BuildUtils.emsg("Missing required id attribute in " +
							"exclude element");
						return;
					}
					excludeSet.add(excludeTag);
				} else {
					BuildUtils.emsg("Child element " +
						name + " ignored");
				}
			}
		}
		
		WorkPart workPart = pm.getWorkPartByTag(partTag);
		if (workPart == null) {
			BuildUtils.emsg("No such work part; " + partTag);
			return;
		}
		List childParts = workPart.getChildren();
		for (Iterator it = childParts.iterator(); it.hasNext(); ) {
			WorkPart childPart = (WorkPart)it.next();
			if (excludeSet.contains(childPart.getTag())) continue;
			String shortTitle = childPart.getShortTitle();
			String workSetTitle = title.replaceAll("%t", shortTitle);
			createSystemWorkSet(workSetTitle, childPart);
		}
	}
	
	/**	Adds the works in a corpus.
	 *
	 *	@param	el		addCorpus element.
	 *
	 *	@throws Exception
	 */
	 
	private static void addCorpus (Element el) 
		throws Exception
	{
		String title = el.getAttribute("title");
		if (title.length() == 0) {
			BuildUtils.emsg("Missing required title attribute in addCorpus element");
			return;
		}
		String corpusTag = el.getAttribute("id");
		if (corpusTag.length() == 0) {
			BuildUtils.emsg("Missing required id attribute in addCorpus element");
			return;
		}
		HashSet excludeSet = new HashSet();
		NodeList children = el.getChildNodes();
		int numChildren = children.getLength();
		for (int i = 0; i < numChildren; i++) {
			Node child = children.item(i);
			int type = child.getNodeType();
			if (type == Node.ELEMENT_NODE) {
				Element childEl = (Element)child;
				String name = childEl.getNodeName();
				if (name.equals("exclude")) {
					String excludeTag = childEl.getAttribute("id");
					if (excludeTag.length() == 0) {
						BuildUtils.emsg("Missing required id attribute in " +
							"exclude element");
						return;
					}
					excludeSet.add(excludeTag);
				} else {
					BuildUtils.emsg("Child element " +
						name + " ignored");
				}
			}
		}
		
		Corpus corpus = pm.getCorpusByTag(corpusTag);
		if (corpus == null) {
			BuildUtils.emsg("No such corpus; " + corpusTag);
			return;
		}
		Set works = corpus.getWorks();
		List workSetParts = new ArrayList();
		for (Iterator it = works.iterator(); it.hasNext(); ) {
			Work work = (Work)it.next();
			if (excludeSet.contains(work.getTag())) continue;
			workSetParts.add(work);
		}
		createSystemWorkSet(title, workSetParts);
	}
	
	/**	Adds the works in a table of contents view category.
	 *
	 *	@param	el		addTconViewCategories element.
	 *
	 *	@throws Exception
	 */
	 
	private static void addTconViewCategories (Element el) 
		throws Exception
	{
		String title = el.getAttribute("title");
		if (title.length() == 0) {
			BuildUtils.emsg("Missing required title attribute in addTconViewCategories element");
			return;
		}
		String corpusTag = el.getAttribute("id");
		if (corpusTag.length() == 0) {
			BuildUtils.emsg("Missing required id attribute in " +
				"addTconViewCategories element");
			return;
		}
		String tconViewTitle = el.getAttribute("tconViewTitle");
		if (tconViewTitle.length() == 0) {
			BuildUtils.emsg("Missing required tconViewTitle attribute in " +
				"addTconViewCategories element");
			return;
		}
		
		Corpus corpus = pm.getCorpusByTag(corpusTag);
		if (corpus == null) {
			BuildUtils.emsg("No such corpus; " + corpusTag);
			return;
		}
		TconView tconView = null;
		List tconViews = corpus.getTconViews();
		for (Iterator it = tconViews.iterator(); it.hasNext(); ) {
			TconView tconViewCandidate = (TconView)it.next();
			if (tconViewCandidate.getViewType() != TconView.CATEGORY_VIEW_TYPE) continue;
			if (tconViewCandidate.getRadioButtonLabel().equals(tconViewTitle)) {
				tconView = tconViewCandidate;
				break;
			}
		}
		if (tconView == null) {
			BuildUtils.emsg("No such tcon view: " + tconViewTitle);
			return;
		}
		List categories = tconView.getCategories();
		for (Iterator it = categories.iterator(); it.hasNext(); ) {
			TconCategory category = (TconCategory)it.next();
			String categoryTitle = category.getTitle();
			String workSetTitle = title.replaceAll("%t", categoryTitle);
			List workTags = category.getWorkTags();
			List workSetParts = new ArrayList();
			for (Iterator it2 = workTags.iterator(); it2.hasNext(); ) {
				String workPartTag = (String)it2.next();
				WorkPart workPart = pm.getWorkPartByTag(workPartTag);
				if (workPart == null) {
					System.out.println("No such work part: " + workPartTag);
					return;
				}
				workSetParts.add(workPart);
			}
			createSystemWorkSet(workSetTitle, workSetParts);
		}
	}
	
	/**	Adds authors.
	 *
	 *	@param	el		addAuthors element.
	 *
	 *	@throws Exception
	 */
	 
	private static void addAuthors (Element el) 
		throws Exception
	{
		String title = el.getAttribute("title");
		if (title.length() == 0) {
			BuildUtils.emsg("Missing required title attribute in addAuthors element");
			return;
		}
		
		Collection authors = pm.getAllAuthors();
		for (Iterator it = authors.iterator(); it.hasNext(); ) {
			Author author = (Author)it.next();
			String name = author.getName().getString();
			String workSetTitle = title.replaceAll("%t", name);
			Set works = author.getWorks();
			if (works.size() == 0) continue;
			List workSetParts = new ArrayList();
			for (Iterator it2 = works.iterator(); it2.hasNext(); ) {
				Work work = (Work)it2.next();
				workSetParts.add(work);
			}
			createSystemWorkSet(workSetTitle, workSetParts);
		}
	}
	
	/**	Adds one period.
	 *
	 *	@param	works		List of works.
	 *
	 *	@param	title		Title.
	 *
	 *	@param	subTitle	Subtitle.
	 *
	 *	@throws Exception
	 */
	 
	private static void addOnePeriod (List works, String title, String subTitle)
		throws Exception
	{
		if (works.size() == 0) return;
		String workSetTitle = title.replaceAll("%t", subTitle);
		createSystemWorkSet(workSetTitle, works);
	}
	
	/**	Adds periods.
	 *
	 *	@param	el		addPeriods element.
	 *
	 *	@throws Exception
	 */
	 
	private static void addPeriods (Element el) 
		throws Exception
	{
		String title = el.getAttribute("title");
		if (title.length() == 0) {
			BuildUtils.emsg("Missing required title attribute in addPeriods element");
			return;
		}
		String corpusTag = el.getAttribute("id");
		if (corpusTag.length() == 0) corpusTag = null;
		String startStr = el.getAttribute("start");
		if (startStr.length() == 0) {
			BuildUtils.emsg("Missing required start attribute in addPeriods element");
			return;
		}
		int start;
		try {
			start = Integer.parseInt(startStr);
		} catch (NumberFormatException e) {
			BuildUtils.emsg("Syntax error in start attribute in addPeriods element: " +
				startStr);
			return;
		}
		String endStr = el.getAttribute("end");
		if (endStr.length() == 0) {
			BuildUtils.emsg("Missing required end attribute in addPeriods element");
			return;
		}
		int end;
		try {
			end = Integer.parseInt(endStr);
		} catch (NumberFormatException e) {
			BuildUtils.emsg("Syntax error in end attribute in addPeriods element: " +
				endStr);
			return;
		}
		String incrementStr = el.getAttribute("increment");
		if (incrementStr.length() == 0) {
			BuildUtils.emsg("Missing required increment attribute in addPeriods element");
			return;
		}
		int increment;
		try {
			increment = Integer.parseInt(incrementStr);
		} catch (NumberFormatException e) {
			BuildUtils.emsg("Syntax error in increment attribute in addPeriods element: " +
				incrementStr);
			return;
		}
		if (start >= end) {
			BuildUtils.emsg("Start must be less than end in addPeriods element");
			return;
		}
		if ((end-start) % increment != 0) {
			BuildUtils.emsg("End minus start must be an exact multiple of increment in " +
				"addPeriods element");
			return;
		}
		
		int numPeriods = (end-start) / increment;
		Collection works;
		if (corpusTag == null) {
			works = pm.getAllWorks();
		} else {
			Corpus corpus = pm.getCorpusByTag(corpusTag);
			if (corpus == null) {
				BuildUtils.emsg("No such corpus; " + corpusTag);
				return;
			}
			works = corpus.getWorks();
		}
		List before = new ArrayList();
		List after = new ArrayList();
		List[] buckets = new List[numPeriods];
		for (int i = 0; i < numPeriods; i++) buckets[i] = new ArrayList();
		for (Iterator it = works.iterator(); it.hasNext(); ) {
			Work work = (Work)it.next();
			PubYearRange pubYearRange = work.getPubDate();
			if (pubYearRange == null) continue;
			Integer startYearInteger = pubYearRange.getStartYear();
			if (startYearInteger == null) continue;
			int startYear = startYearInteger.intValue();
			if (startYear < start) {
				before.add(work);
			} else if (startYear >= end) {
				after.add(work);
			} else {
				buckets[(startYear-start)/increment].add(work);
			}
		}
		addOnePeriod(before, title, (start-1) + " or earlier");
		for (int i = 0; i < numPeriods; i++) {
			int bucketStart = start + i*increment;
			int bucketEnd = bucketStart + increment;
			addOnePeriod(buckets[i], title, bucketStart + "-" + (bucketEnd-1));
		}
		addOnePeriod(after, title, end + " or later");
	}
	
	/**	Builds the new system work sets.
	 *
	 *	@throws Exception
	 */
	 
	private static void buildNewSystemWorkSets ()
		throws Exception
	{
		
		Element rootEl = DOMUtils.getChild(document, "WordHoardWorkSets");
		if (rootEl == null) {
			BuildUtils.emsg("Missing required WordHoardWorkSets element");
			return;
		}
		
		NodeList children = rootEl.getChildNodes();
		int numChildren = children.getLength();
		for (int i = 0; i < numChildren; i++) {
			Node child = children.item(i);
			int type = child.getNodeType();
			if (type == Node.ELEMENT_NODE) {
				Element el = (Element)child;
				String name = el.getNodeName();
				if (name.equals("addWorkParts")) {
					addWorkParts(el);
				} else if (name.equals("addChildren")) {
					addChildren(el);
				} else if (name.equals("addCorpus")) {
					addCorpus(el);
				} else if (name.equals("addTconViewCategories")) {
					addTconViewCategories(el);
				} else if (name.equals("addAuthors")) {
					addAuthors(el);
				} else if (name.equals("addPeriods")) {
					addPeriods(el);
				} else {
					BuildUtils.emsg("Child element " +
						name + " ignored");
				}
			}
		}
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
			System.out.println("Building work sets from file " + inPath);
			BuildUtils.initHibernate(dbname, username, password);
			pm = new PersistenceManager();
			document = DOMUtils.parse(inPath);
			
			//	Delete old system work sets.
			
			deleteOldSystemWorkSets();
			
			//	Build new system work sets.
			
			buildNewSystemWorkSets();
			
			//	Report final stats.
			
			long endTime = System.currentTimeMillis();
			System.out.println(Formatters.formatIntegerWithCommas(numWorkSets) + 
				(numWorkSets == 1 ? " work set" : " work sets") +
				" created in " +
				BuildUtils.formatElapsedTime(startTime, endTime));
			BuildUtils.reportNumErrors();
			
		} catch (Exception e) {
		
			e.printStackTrace();
			System.exit(1);
			
		}
		
	}

	/**	Hides the default no-arg constructor.
	 */
	 
	private BuildWorkSets () {
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

