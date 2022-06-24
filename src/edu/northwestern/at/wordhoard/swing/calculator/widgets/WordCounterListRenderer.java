package edu.northwestern.at.wordhoard.swing.calculator.widgets;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;

import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;

/**	Renderer for tree combo boxes containing WordCounter objects.
 */

public class WordCounterListRenderer
	extends TreeComboListEntryRenderer
{
	/**	True to display short work titles. */

	protected boolean useShortWorkTitles	= true;

	/**	List renderer for WordCounter objects.
	 *
	 *	@param	useShortWorkTitles	Use short work titles.
	 */

	public WordCounterListRenderer( boolean useShortWorkTitles )
	{
		super( false );

		this.useShortWorkTitles	= useShortWorkTitles;
	}

	/**	Returns a component configured to display the specified value.
	 *
	 *	@param	list			The JList we're painting.
	 *
	 *	@param	value			The value to be painted.
	 *
	 *	@param	index			The cell's index.
	 *
	 *	@param	isSelected		True if the cell is selected.
	 *
	 *	@param	hasFocus		True if the cell has the focus.
	 *
	 *	@return		A component whose paint() method will render the
	 *				specified value.
	 */

	public Component getListCellRendererComponent
	(
		JList list ,
		Object value ,
		int index ,
		boolean isSelected ,
		boolean hasFocus
	)
	{
		super.getListCellRendererComponent
		(
			list ,
			value ,
			index ,
			isSelected ,
			hasFocus
		);

		if ( value != null )
		{
			TreeListEntry listEntry		= (TreeListEntry)value;

			Object object				= listEntry.getObject();

			DefaultMutableTreeNode node	= (DefaultMutableTreeNode)object;

			object						= node.getUserObject();

								//	If object implements CanCountWords,
								//	wrap it as a WordCounter object.

			if ( object instanceof CanCountWords )
			{
				WordCounter wordCounter	=
					new WordCounter( (CanCountWords)object );

								//	Get HTML text for display.

//				setColors( isSelected );

				setText( wordCounter.toHTMLString( useShortWorkTitles ) );

								//	Make sure corpus always displays as a
								//	folder even if no child works are
								//	present.

				if ( wordCounter.isCorpus() )
				{
					setIcon( nodeIcon );
				}
			}
		}

		return this;
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


