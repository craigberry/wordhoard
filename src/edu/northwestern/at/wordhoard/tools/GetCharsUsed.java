package edu.northwestern.at.wordhoard.tools;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.model.annotations.*;

/**	Gets all the characters used.
 *
 *	<p>Usage:
 *
 *	<p><code>GetCharsUsed dbname username password</code></p>
 *
 *	<p>dbname = Database name.
 *
 *	<p>username = MySQL username.
 *
 *	<p>password = MySQL password.
 *
 *	<p>The output is in Java source code format, for pasting into the
 *	{@link edu.northwestern.at.wordhoard.swing.text.CharsUsed
 *	CharsUsed} class.
 */

public class GetCharsUsed {

	/**	Persistence manager. */
	
	private static PersistenceManager pm;
	
	/**	used[cs][x] = true if unicode code x is used in character set cs. */
	
	private static boolean[][] used = 
		new boolean[TextParams.NUM_CHARSETS][0x3000];
		
	/**	Work part title. */
		
	private static String partTitle;
	
	/**	Line number. */
	
	private static int lineNum;
	
	/**	Run number. */
	
	private static int runNum;
	
	/**	Does one run.
	 *
	 *	@param	run		Run.
	 *
	 *	@throws Exception
	 */
	 
	private static void doOneRun (TextRun run) 
		throws Exception
	{
		String text = run.getText();
		byte charset = run.getCharset();
		char[] chars = text.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			try {
				used[charset][chars[i]] = true;
				if (chars[i] == (char)0x000a) {
					System.out.println("##### New line (0x000a) found in " +
						partTitle + ", line " + lineNum + ", run " + 
						runNum + ": " + text);
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("charset = " + charset);
				System.out.println("char = " + Integer.toString(chars[i], 16));
				throw e;
			}
		}
	}
	
	/**	Does one line.
	 *
	 *	@param	line		Line.
	 *
	 *	@throws Exception
	 */
	 
	private static void doOneLine (TextLine line) 
		throws Exception
	{
		TextRun[] runs = line.getRuns();
		for (int j = 0; j < runs.length; j++) {
			runNum = j;
			TextRun run = runs[j];
			doOneRun(run);
		}
		Text marginalia = line.getMarginalia();
		if (marginalia != null) doOneText(marginalia);
	}
	
	/**	Does one text object.
	 *
	 *	@param	text		Text object.
	 *
	 *	@throws Exception
	 */
	 
	private static void doOneText (Text text) 
		throws Exception
	{
		TextLine[] lines = text.getLines();
		for (int i = 0; i < lines.length; i++) {
			lineNum = i;
			TextLine line = lines[i];
			doOneLine(line);
		}
	}
	
	/**	Does one text wrapper.
	 *
	 *	@param	wrapper		Text Wrapper.
	 *
	 *	@throws Exception
	 */
	 
	private static void doOneWrapper (TextWrapper wrapper) 
		throws Exception
	{
		Text text = wrapper.getText();
		doOneText(text);
	}
	
	/**	Does one work part.
	 *
	 *	@param	part		Work part.
	 *
	 *	@throws Exception
	 */
	 
	private static void doOnePart (WorkPart part) 
		throws Exception
	{
		partTitle = part.getFullTitle();
		TextWrapper wrapper = part.getPrimaryText();
		if (wrapper != null) doOneWrapper(wrapper);
		Map translations = part.getTranslations();
		if (translations == null) return;
		Collection translationWrappers = translations.values();
		for (Iterator it = translationWrappers.iterator(); it.hasNext(); ) {
			wrapper = (TextWrapper)it.next();
			doOneWrapper(wrapper);
		}
		Collection annotations = pm.getAnnotationsForWorkPart(part);
		for (Iterator it = annotations.iterator(); it.hasNext(); ) {
			TextAnnotation annotation = (TextAnnotation)it.next();
			wrapper = (TextWrapper)annotation.getText();
			if (wrapper != null) doOneWrapper(wrapper);
		}
	}
	
	/**	Does one work.
	 *
	 *	@param	work		Work.
	 *
	 *	@throws Exception
	 */
	 
	private static void doOneWork (Work work) 
		throws Exception
	{
		System.out.println(work.getFullTitle());
		pm = new PersistenceManager();
		work = (Work)pm.clone(work);
		List parts = work.getPartsWithText();
		for (Iterator it = parts.iterator(); it.hasNext(); ) {
			WorkPart part = (WorkPart)it.next();
			doOnePart(part);
		}
		pm.close();
	}
	
	/**	Adds basic Roman characters to the Greek set. 
	 *
	 *	<p>We need lower-case letters, digits, and parentheses to display
	 *	lemma tags, footer and tool tip brief descriptions.
	 */
	
	private static void moreGreek () {
		for (int c = 'a'; c <= 'z'; c++)
			used[TextParams.GREEK][c] = true;
		for (int c = '0'; c <= '9'; c++)
			used[TextParams.GREEK][c] = true;
		used[TextParams.GREEK]['('] = true;
		used[TextParams.GREEK][')'] = true;
	}
	
	/**	Writes the results. */
	
	private static void writeResults (String title, int charset) {
		System.out.println(title + ":");
		for (int i = 0; i < 0x3000; i++) {
			if (used[charset][i]) {
				String str = Integer.toString(i, 16);
				while (str.length() < 4) str = '0' + str;
				System.out.println("            (char)0x" + str + ",");
			}
		}
	}
	
	/**	Does it.
	 *
	 *	@param	args		Arguments.
	 *
	 *	@throws	Exception
	 */

	private static void doit (String[] args)
		throws Exception
	{
		BuildUtils.initHibernate(args[0], args[1], args[2]);
		PersistenceManager worksPm = new PersistenceManager();
		Collection works = worksPm.getAllWorks();
		for (Iterator it = works.iterator(); it.hasNext(); ) {
			Work work = (Work)it.next();
			doOneWork(work);
		}
		worksPm.close();
		moreGreek();
		writeResults("Roman", TextParams.ROMAN);
		writeResults("Greek", TextParams.GREEK);
	}

	/**	The main program.
	 *
	 *	@param	args		Command line arguments.
	 */

	public static void main (String[] args) {
		try {
			doit(args);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/**	Hides the default no-arg constructor.
	 */
	 
	private GetCharsUsed () {
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

