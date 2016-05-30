package edu.northwestern.at.wordhoard.model;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.db.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;

/**	A colocate preloader.
 *
 *	<p>A colocate preloader dramatically improves performance when many
 *	colocates for words are needed, as in a KWIC display. It loads up large
 *	batches of colocates in advance instead of loading them one at a time.
 */

public class ColocatePreloader {

	/**	The persistence manager. */

	private PersistenceManager pm;

	/**	A map from words to word information. */

	private HashMap map = new HashMap();

	/**	Word information. */

	private static class Info {
		private boolean loaded;		// True if colocates loaded
		private List list;			// List in which word occurs
		private int pos;			// Position of word in list.
	}

	/**	Creates a new colocate preloader.
	 *
	 *	@param	pm		Persistence manager.
	 */

	public ColocatePreloader (PersistenceManager pm) {
		this.pm = pm;
	}

	/**	Adds a list of words to the preloader.
	 *
	 *	@param	words		List of words.
	 */

	public void add (List words) {
		int pos = 0;
		for (Iterator it = words.iterator(); it.hasNext(); ) {
			Word word = (Word)it.next();
			Info info = (Info)map.get(word);
			if (info == null) {
				info = new Info();
				info.loaded = false;
				map.put(word, info);
			}
			info.list = words;
			info.pos = pos;
			pos++;
		}
	}

	/**	Preloads colocates for words.
	 *
	 *	<p>Loads the colocates preceding and following the word, plus
	 *	preceding and following colocates for words following
	 *	the word in its list.
	 *
	 *	@param	word		The word.
	 *
	 *	@param	distance	The number of preceding and following
	 *						colocates to preload.
	 *
	 *	@param	lookahead	This many additional words following the word
	 *						in its list also have their colocates preloaded.
	 *
	 *	@throws	PersistenceException
	 */

	public void load (Word word, int distance, int lookahead)
		throws PersistenceException
	{
		Info info = (Info)map.get(word);
		if (info == null || info.loaded) return;
		List list = info.list;
		int listSize = list.size();
		int pos = info.pos;
		int lastPos = Math.min(listSize, pos + lookahead + 1);
		ArrayList words = new ArrayList();
		for (int i = pos; i < lastPos; i++) {
			word = (Word)list.get(i);
			info = (Info)map.get(word);
			if (!info.loaded) {
				words.add(word);
				info.loaded = true;
			}
		}
		pm.getColocates(words, distance);
//		CollocateUtils.getColocates(pm, words, distance);
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

