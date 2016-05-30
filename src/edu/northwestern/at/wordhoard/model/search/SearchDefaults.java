package edu.northwestern.at.wordhoard.model.search;

/*	Please see the license information at the end of this file. */

/**	A supplier of word search criteria defaults.
 *
 *	<p>In the {@link edu.northwestern.at.wordhoard.swing.find.FindWindow
 *	find dialog}, 
 *	users can construct search criteria. The default
 *	values for the criteria added by the user are derived from the object
 *	that was "focused" when the user opened the dialog.
 *
 *	<p>For example, suppose a word is selected in a work panel, and the
 *	the user brings up the find dialog. The default values for the search
 *	criteria in the dialog are supplied by the word. If the user adds
 *	a spelling criterion, the initial value of the spelling text field is
 *	the spelling of the word. If the user adds a lemma criterion, the
 *	initial value of the lemma field is the lemma of the first part
 *	of the word. If the user adds a work criterion, the initally selected
 *	work is the work containing the word. And so on.
 *
 *	<p>In this example, the 
 *	{@link edu.northwestern.at.wordhoard.model.Word Word}
 *	class implements the SearchDefaults 
 *	interface. getSearchDefault(Spelling.class) returns the spelling of
 *	the word. getSearchDefault(Lemma.class) returns the lemma of the
 *	first part of the word. getSearchDefault(Work.class) returns the
 *	work containing the word. And so on.
 */

public interface SearchDefaults {

	/**	Gets a search criterion default value.
	 *
	 *	@param	cls		Model class of search criterion.
	 *
	 *	@return			Default value for search criterion.
	 */
	
	public SearchCriterion getSearchDefault (Class cls);
	
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

