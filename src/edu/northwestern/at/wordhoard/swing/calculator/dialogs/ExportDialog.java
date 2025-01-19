package edu.northwestern.at.wordhoard.swing.calculator.dialogs;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import edu.northwestern.at.wordhoard.model.userdata.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;

/**	Displays export dialog.
 */

public class ExportDialog extends ImportExportDialog
{
	/**	Create new export dialog.
	 *
	 *	@param	title		Title for dialog.
	 *	@param	parent		Parent window for dialog.
	 */

	public ExportDialog
	(
		String title ,
		JFrame parent
	)
	{
		super( title , parent , null );
	}

	/**	Get phrase sets.
	 *
	 *	@return		The phrase sets.
	 */

	protected PhraseSet[] getPhraseSets()
	{
		return PhraseSetUtils.getPhraseSets();
	}

	/**	Get word sets.
	 *
	 *	@return		The word sets.
	 */

	protected WordSet[] getWordSets()
	{
		return WordSetUtils.getWordSets();
	}

	/**	Get work sets.
	 *
	 *	@return		The work sets.
	 */

	protected WorkSet[] getWorkSets()
	{
		return WorkSetUtils.getWorkSets();
	}

	/**	Get work part queries.
	 *
	 *	@return		The work part queries.
	 */

	protected WHQuery[] getWorkPartQueries()
	{
		return QueryUtils.getQueries( WHQuery.WORKPARTQUERY );
	}

	/**	Get word queries.
	 *
	 *	@return		The word queries.
	 */

	protected WHQuery[] getWordQueries()
	{
		return QueryUtils.getQueries( WHQuery.WORDQUERY );
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

