package edu.northwestern.at.wordhoard.tools;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import java.sql.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.morphology.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.model.tconview.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.xml.*;
import edu.northwestern.at.utils.db.mysql.*;

/**	Builds the corpora.
 *
 *	<p>Usage:
 *
 *	<p><code>BuildCorpora in dbname username password</code>
 *
 *	<p>in = Path to the corpus definition XML input file.
 *
 *	<p>dbname = Database name.
 *
 *	<p>username = MySQL username.
 *
 *	<p>password = MySQL password.
 */
 
public class BuildCorpora {
	
	/**	MySQL table exporter/importer for corpus objects. */
	
	private static TableExporterImporter corpusTableExporterImporter;
	
	/**	MySQL table exporter/importer for the corpus_tconviews table. */
	
	private static TableExporterImporter corpusTconViewsTableExporterImporter;
	
	/**	MySQL table exporter/importer for the tconview table. */
	
	private static TableExporterImporter tconViewTableExporterImporter;
	
	/**	MySQL table exporter/importer for the tconview_worktags table. */
	
	private static TableExporterImporter tconViewWorkTagsTableExporterImporter;
	
	/**	MySQL table exporter/importer for the tconview_categories table. */
	
	private static TableExporterImporter tconViewCategoriesTableExporterImporter;
	
	/**	MySQL table exporter/importer for the tconcategory table. */
	
	private static TableExporterImporter tconCategoryTableExporterImporter;
	
	/**	MySQL table exporter/importer for the tconcategory_worktags table. */
	
	private static TableExporterImporter tconCategoryWorkTagsTableExporterImporter;
	
	/**	Next available corpus id. */
	
	private static long nextCorpusId = 0;
	
	/**	Next available tconview id. */
	
	private static long nextTconViewId = 0;
	
	/**	Next available tconcatagory id. */
	
	private static long nextTconCategoryId = 0;
	
	/**	Builds a corpus.
	 *
	 *	@param	el			"corpus" element.
	 *
	 *	@param	ordinal		The ordinal of the corpus.
	 *
	 *	@throws	Exception
	 */
	 
	private static void buildCorpus (Element el, int ordinal) 
		throws Exception
	{
	
		String tag = el.getAttribute("id");
		if (tag.length() == 0) {
			BuildUtils.emsg("Required id attribute missing on corpus element");
			return;
		}
		
		String charsetStr = el.getAttribute("charset");
		byte charset = 0;
		if (charsetStr.length() == 0) {
			BuildUtils.emsg(tag + ": Missing required charset attribute");
		} else if (charsetStr.equals("roman")) {
			charset = TextParams.ROMAN;
		} else if (charsetStr.equals("greek")) {
			charset = TextParams.GREEK;
		} else {
			BuildUtils.emsg(tag + ": Illegal value for charset attribute");
			return;
		}
		
		String posTypeStr = el.getAttribute("posType");
		byte posType = 0;
		if (posTypeStr.length() == 0) {
			BuildUtils.emsg(tag + ": Missing required posType attribute");
		} else if (posTypeStr.equals("english")) {
			posType = Pos.ENGLISH;
		} else if (posTypeStr.equals("greek")) {
			posType = Pos.GREEK;
		} else {
			BuildUtils.emsg(tag + ": Illegal value for posType attribute");
			return;
		}
		
		Element titleEl = DOMUtils.getChild(el, "title");
		if (titleEl == null) {
			BuildUtils.emsg(tag + ": Missing title");
			return;
		}
		String title = DOMUtils.getText(titleEl);
		System.out.println("   " + tag + ": " + title);

		Element taggingDataEl = DOMUtils.getChild(el, "taggingData");
		if (taggingDataEl == null) {
			BuildUtils.emsg(tag + ": Missing taggingData");
			return;
		}
		long taggingDataFlags = BuildUtils.getTaggingDataFlags(taggingDataEl);
		
		String translations = null;
		String tranDescription = null;
		Element translationsEl = DOMUtils.getChild(el, "translations");
		if (translationsEl != null) {
			ArrayList translationChildren = 
				DOMUtils.getChildren(translationsEl, "translation");
			StringBuffer buf = new StringBuffer();
			for (Iterator it = translationChildren.iterator(); it.hasNext(); ) {
				Element translationEl = (Element)it.next();
				String translationId = translationEl.getAttribute("id");
				if (translationId.length() == 0) {
					BuildUtils.emsg("Missing required id attribute on " +
						"translation element");
					continue;
				}
				buf.append(translationId);
				buf.append(",");
			}
			if (buf.length() == 0) {
				BuildUtils.emsg("translations element has no valid " +
					"translation child elements");
			} else {
				buf.setLength(buf.length()-1);
				translations = buf.toString();
			}
			Element tranDescriptionEl = 
				DOMUtils.getChild(translationsEl, "description");
			if (tranDescriptionEl != null) {
				tranDescription = DOMUtils.getText(tranDescriptionEl);
			}
		}
		
		long corpusId = nextCorpusId++;
		corpusTableExporterImporter.print(corpusId);
		corpusTableExporterImporter.print(tag);
		corpusTableExporterImporter.print(title);
		corpusTableExporterImporter.print(charset);
		corpusTableExporterImporter.print(posType);
		corpusTableExporterImporter.print(taggingDataFlags);
		corpusTableExporterImporter.print(0);
		corpusTableExporterImporter.print(0);
		corpusTableExporterImporter.print(0);
		corpusTableExporterImporter.print(0);
		corpusTableExporterImporter.print(translations);
		corpusTableExporterImporter.print(tranDescription);
		corpusTableExporterImporter.print(ordinal);
		corpusTableExporterImporter.println();
		
		buildTconViews(el, corpusId);
	}
	
	/**	Builds the table of contents views for a corpus.
	 *
	 *	@param	el			"corpus" element.
	 *
	 *	@param	corpusId	Corpus id.
	 *
	 *	@throws	Exception
	 */
	 
	private static void buildTconViews (Element el, long corpusId) 
		throws Exception
	{
		ArrayList tconViewEls = DOMUtils.getChildren(el, "tconview");
		int index = 0;
		for (Iterator it = tconViewEls.iterator(); it.hasNext(); ) {
			Element tconViewEl = (Element)it.next();
			boolean ok = buildTconView(tconViewEl, corpusId, index);
			if (ok) index++;
		}
		if (index == 0) {
			long tconViewId = nextTconViewId++;
			tconViewTableExporterImporter.print(tconViewId);
			tconViewTableExporterImporter.print(TconView.LIST_TAG_VIEW_TYPE);
			String title = null;
			tconViewTableExporterImporter.print(title);
			tconViewTableExporterImporter.println();
			corpusTconViewsTableExporterImporter.print(corpusId);
			corpusTconViewsTableExporterImporter.print(tconViewId);
			corpusTconViewsTableExporterImporter.print(0);		
			corpusTconViewsTableExporterImporter.println();	
		}
	}
	
	/**	Builds a table of contents view.
	 *
	 *	@param	el			"tconview" element.
	 *
	 *	@param	corpusId	Corpus id.
	 *
	 *	@param	index		Index in corpus of view.
	 *
	 *	@return		True if view built, false if error.
	 *
	 *	@throws Exception
	 */
	 
	private static boolean buildTconView (Element el, long corpusId, int index)
		throws Exception
	{
		String typeAttrStr = el.getAttribute("type");
		int type = 0;
		if (typeAttrStr.length() == 0) {
			BuildUtils.emsg("tconview element missing required type attribute");
			return false;
		}
		if (typeAttrStr.equals("byTag")) {
			type = TconView.LIST_TAG_VIEW_TYPE;
		} else if (typeAttrStr.equals("byDate")) {
			type = TconView.LIST_PUB_YEAR_VIEW_TYPE;
		} else if (typeAttrStr.equals("list")) {
			type = TconView.LIST_VIEW_TYPE;
		} else if (typeAttrStr.equals("category")) {
			type = TconView.CATEGORY_VIEW_TYPE;
		} else if (typeAttrStr.equals("byAuthor")) {
			type = TconView.BY_AUTHOR_VIEW_TYPE;
		} else {
			BuildUtils.emsg("tconview illegal value for type attribute: " +
				typeAttrStr);
			return false;
		}
		
		String title = el.getAttribute("title");
		if (title.length() == 0) title = null;
		
		long tconViewId = nextTconViewId++;
		tconViewTableExporterImporter.print(tconViewId);
		tconViewTableExporterImporter.print(type);
		tconViewTableExporterImporter.print(title);
		tconViewTableExporterImporter.println();
		
		corpusTconViewsTableExporterImporter.print(corpusId);
		corpusTconViewsTableExporterImporter.print(tconViewId);
		corpusTconViewsTableExporterImporter.print(index);		
		corpusTconViewsTableExporterImporter.println();	
		
		switch (type) {
			case TconView.LIST_TAG_VIEW_TYPE:
				break;
			case TconView.LIST_PUB_YEAR_VIEW_TYPE:
				break;
			case TconView.LIST_VIEW_TYPE:
				buildWorkList(el, tconViewId, 
					tconViewWorkTagsTableExporterImporter);
				break;
			case TconView.CATEGORY_VIEW_TYPE:
				buildCategoryList(el, tconViewId);
				break;
			case TconView.BY_AUTHOR_VIEW_TYPE:
				break;
		}
		
		return true;
	}
	
	/**	Builds a work list for a tconview or category element.
	 *
	 *	@param	el			"tconview" or "category" element.
	 *
	 *	@param	parentId	Id of parent TconView or TconCategory object.
	 *
	 *	@param	exporterImporter	Table exporter/importer for
	 *								tconview_worktags or tconcategory_worktags
	 *								table.
	 *
	 *	@throws	Exception
	 */
	 
	private static void buildWorkList (Element el, long parentId,
		TableExporterImporter exporterImporter) 
			throws Exception
	{
		ArrayList workEls = DOMUtils.getChildren(el, "work");
		int index = 0;
		for (Iterator it = workEls.iterator(); it.hasNext(); ) {
			Element workEl = (Element)it.next();
			boolean ok = buildWorkId(workEl, parentId, index, exporterImporter);
			if (ok) index++;
		}
	}
	
	/**	Builds a work id.
	 *
	 *	@param	el			"work" element.
	 *
	 *	@param	parentId	Id of parent TconView or TconCategory object.
	 *
	 *	@param	index		Index in corpus of view.
	 *
	 *	@param	exporterImporter	Table exporter/importer for
	 *								tconview_worktags or tconcategory_worktags
	 *								table.
	 *
	 *	@return		True if work id built, false if error.
	 *
	 *	@throws Exception
	 */
	 
	private static boolean buildWorkId (Element el, long parentId, 
		int index, TableExporterImporter exporterImporter)
		throws Exception
	{
		String workTag = el.getAttribute("id");
		if (workTag.length() == 0) {
			BuildUtils.emsg("work element missing required id attribute");
			return false;
		}
		
		exporterImporter.print(parentId);
		exporterImporter.print(workTag);
		exporterImporter.print(index);		
		exporterImporter.println();	
		
		return true;
	}
	
	/**	Builds a category list for a tconview element.
	 *
	 *	@param	el			"tconview" element.
	 *
	 *	@param	tconViewId	Id of parent TconView.
	 *
	 *	@throws	Exception
	 */
	 
	private static void buildCategoryList (Element el, long tconViewId) 
		throws Exception
	{
		ArrayList categoryEls = DOMUtils.getChildren(el, "category");
		int index = 0;
		for (Iterator it = categoryEls.iterator(); it.hasNext(); ) {
			Element categoryEl = (Element)it.next();
			boolean ok = buildCategory(categoryEl, tconViewId, index);
			if (ok) index++;
		}
	}
	
	/**	Builds a category.
	 *
	 *	@param	el			"category" element.
	 *
	 *	@param	tconViewId	tconview id.
	 *
	 *	@param	index		Index in tconview of category.
	 *
	 *	@return		True if category built, false if error.
	 *
	 *	@throws Exception
	 */
	 
	private static boolean buildCategory (Element el, long tconViewId, int index)
		throws Exception
	{
		String title = el.getAttribute("title");
		if (title.length() == 0) {
			BuildUtils.emsg("category element missing required title attribute");
			return false;
		}
		
		long tconCategoryId = nextTconCategoryId++;
		tconCategoryTableExporterImporter.print(tconCategoryId);
		tconCategoryTableExporterImporter.print(title);
		tconCategoryTableExporterImporter.println();
		
		tconViewCategoriesTableExporterImporter.print(tconViewId);
		tconViewCategoriesTableExporterImporter.print(tconCategoryId);
		tconViewCategoriesTableExporterImporter.print(index);		
		tconViewCategoriesTableExporterImporter.println();	

		buildWorkList(el, tconCategoryId, 
			tconCategoryWorkTagsTableExporterImporter);
		
		return true;
	}

	/**	The main program.
	 *
	 *	@param	args		Command line arguments.
	 */
	
	public static void main (final String args[]) {
	
		try {
		
			long startTime = System.currentTimeMillis();
		
			int numArgs = args.length;
			if (numArgs != 4) {
				System.out.println("Usage: BuildCorpora in dbname username password");
				System.exit(1);
			}
			String in = args[0];
			String dbname = args[1];
			String username = args[2];
			String password = args[3];
			
			System.out.println("Building corpora from file " + in);
			
			Connection c = BuildUtils.getConnection(dbname, username, password);

			Statement s = c.createStatement();
			int n = s.executeUpdate("delete from corpus");
			s.executeUpdate("delete from corpus_tconviews");
			s.executeUpdate("delete from tconview");
			s.executeUpdate("delete from tconview_worktags");
			s.executeUpdate("delete from tconview_categories");
			s.executeUpdate("delete from tconcategory");
			s.executeUpdate("delete from tconcategory_worktags");
			if (n > 0) System.out.println(n +
				(n == 1 ? " corpus" : " corpora") + " deleted");
			s.close();
			
			String tempDirPath = BuildUtils.createTempDir() + "/";
				
			corpusTableExporterImporter =
				new TableExporterImporter("corpus",
					"id, tag, title, charset, postype, taggingData_flags, " +
					"numWorkParts, numLines, numWords, " +
					"maxWordPathLength, translations, tranDescription, " +
					"ordinal",
					tempDirPath + "corpus.txt",
					false);
					
			corpusTconViewsTableExporterImporter =
				new TableExporterImporter("corpus_tconviews",
					"corpus, tconview, corpus_index",
					tempDirPath + "corpusTconViews.txt",
					false);
					
			tconViewTableExporterImporter =
				new TableExporterImporter("tconview",
					"id, viewType, radioButtonLabel",
					tempDirPath + "tconView.txt",
					false);
					
			tconViewWorkTagsTableExporterImporter =
				new TableExporterImporter("tconview_worktags",
					"tconview, worktag, tconview_index",
					tempDirPath + "tconViewWorkTags.txt",
					false);
					
			tconViewCategoriesTableExporterImporter =
				new TableExporterImporter("tconview_categories",
					"tconview, category, tconview_index",
					tempDirPath + "tconViewCategories.txt",
					false);
					
			tconCategoryTableExporterImporter =
				new TableExporterImporter("tconcategory",
					"id, title",
					tempDirPath + "tconCategory.txt",
					false);
					
			tconCategoryWorkTagsTableExporterImporter =
				new TableExporterImporter("tconcategory_worktags",
					"tconcategory, worktag, tconcategory_index",
					tempDirPath + "tconCategoryWorkTags.txt",
					false);
			
			Document document = DOMUtils.parse(in);
			
			Element el = DOMUtils.getDescendant(document, "WordHoardCorpora");
			if (el == null) {
				BuildUtils.emsg("Missing required WordHoardCorpora element");
				return;
			}
			
			NodeList children = el.getChildNodes();
			int numChildren = children.getLength();
			int ordinal = 0;
			for (int i = 0; i < numChildren; i++) {
				Node child = children.item(i);
				if (!(child instanceof Element)) continue;
				String childName = child.getNodeName();
				Element childEl = (Element)child;
				if (childName.equals("corpus")) {
					buildCorpus(childEl, ordinal++);
				} else {
					BuildUtils.emsg("Illegal element: " + childName);
				}
			}
			
			corpusTableExporterImporter.close();
			int ct = corpusTableExporterImporter.importData(c);
			
			corpusTconViewsTableExporterImporter.close();
			corpusTconViewsTableExporterImporter.importData(c);
			tconViewTableExporterImporter.close();
			tconViewTableExporterImporter.importData(c);
			tconViewWorkTagsTableExporterImporter.close();
			tconViewWorkTagsTableExporterImporter.importData(c);
			tconViewCategoriesTableExporterImporter.close();
			tconViewCategoriesTableExporterImporter.importData(c);
			tconCategoryTableExporterImporter.close();
			tconCategoryTableExporterImporter.importData(c);
			tconCategoryWorkTagsTableExporterImporter.close();
			tconCategoryWorkTagsTableExporterImporter.importData(c);
			
			c.close();
			
			BuildUtils.deleteTempDir();
			
			long endTime = System.currentTimeMillis();
			System.out.println(Formatters.formatIntegerWithCommas(ct) +
				(ct == 1 ? " corpus" : " corpora") +
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
	 
	private BuildCorpora () {
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

