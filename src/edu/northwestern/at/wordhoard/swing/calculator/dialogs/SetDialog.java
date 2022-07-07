package edu.northwestern.at.wordhoard.swing.calculator.dialogs;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import javax.swing.*;

import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.wordhoard.model.userdata.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;
import edu.northwestern.at.wordhoard.swing.*;

/**	Common methods used by create, edit, and delete dialogs for
*	work sets, word sets, phrase sets, and queries.
 */

public abstract class SetDialog extends SkeletonDialog
{
	/**	True if editing set. */

	protected boolean editingSet	= false;

	/**	Set title. */

	protected String title			= "";

	/**	Title field label. */

	protected JLabel titleLabel		= new JLabel( "" );

	/**	Title edit field. */

	protected XTextField titleEditField	= new XTextField();

	/**	Set description. */

	protected String description	= "";

	/**	Description label. */

	protected JLabel descriptionLabel	= new JLabel( "" );

	/**	Description edit field. */

	protected XTextArea descriptionEditField	= new XTextArea( 5 , 50 );

	/**	Scroll pane for description. */

	protected XScrollPane descriptionScrollPane	=
		new XScrollPane( descriptionEditField );

	/**	Set web page URL. */

	protected String webPageURL			= "";

	/**	Web page URL label. */

	protected JLabel webPageURLLabel	= new JLabel( "" );

	/**	Web page URL edit field. */

	protected XTextField webPageURLEditField	= new XTextField();

	/**	True if set is public. */

	protected static boolean isPublic	= false;

	/**	Dialog field for "is public" flag. */

	protected JCheckBox isPublicCheckBox		= new JCheckBox();

	/**	Create a set dialog.
	 *
	 *	@param	dialogTitle			The dialog title.
	 *	@param	parentWindow		The parent window.
	 *	@param	okButtonName		The OK button name.
	 *	@param	closeButtonName		The Close button name.
	 *	@param	revertButtonName	The Revert button name.
	 *	@param	editingSet			True if we're editing set.
	 */

	public SetDialog
	(
		String dialogTitle ,
		Frame parentWindow ,
		String okButtonName ,
		String closeButtonName ,
		String revertButtonName ,
		boolean editingSet
	)
	{
		super
		(
			dialogTitle ,
			parentWindow ,
			okButtonName ,
			closeButtonName ,
			revertButtonName
		);

    	this.editingSet	= editingSet;

//    	buildDialog();
	}

	/**	Initialize the dialog fields. */

	protected void initializeFields()
	{
								//	Set title edit field.

		titleEditField.setText( title );

								//	Set description edit field.

		descriptionEditField.setText( description );

								//	Set web page URL edit field.

		descriptionEditField.setText( webPageURL );

								//	Set "is public" checkbox.

		String isPublicString	=
			WordHoardSettings.getString
			(
				"Public" ,
				"Public"
			);

		isPublicCheckBox.setText( isPublicString );
		isPublicCheckBox.setSelected( false );
	}

	/** Adds fields to the dialog.
	 *
	 *	@param	dialogFields	The component holding the dialog fields.
	 */

	protected void addFields( LabeledColumn dialogFields )
	{
		initializeFields();

								//	Add set title.
		titleLabel	=
			new JLabel
			(
				WordHoardSettings.getString
				(
					"SetTitle" ,
					"Title"
				) + ":"
			);

		dialogFields.addPair
		(
			titleLabel ,
			titleEditField
		);
								//	Add set description.

		descriptionLabel	=
			new JLabel
			(
				WordHoardSettings.getString
				(
					"SetDescription" ,
					"Description"
				) + ":"
			);

		dialogFields.addPair
		(
			descriptionLabel ,
			descriptionScrollPane
		);
								//	Add set web page URL.

		webPageURLLabel	=
			new JLabel
			(
				WordHoardSettings.getString
				(
					"SetWebPageURL" ,
					"Web Page URL"
				) + ":"
			);

		dialogFields.addPair
		(
			webPageURLLabel ,
			webPageURLEditField
		);
								//	Add "is public" check box.
		dialogFields.addPair
		(
			"" ,
			isPublicCheckBox
		);
	}

	/**	Get work set is public flag.
	 *
	 *	@return		true if set public, false otherwise.
	 */

	public boolean getIsPublic()
	{
		return isPublic;
	}

	/**	Get the query title.
	 *
	 *	@return		The query title.
	 */

	public String getSetTitle()
	{
		return title;
	}

	/**	Get the query description.
	 *
	 *	@return		The query description.
	 */

	public String getDescription()
	{
		return description;
	}

	/**	Get the query web page URL.
	 *
	 *	@return		The query web page URL.
	 */

	public String getWebPageURL()
	{
		return webPageURL;
	}

	/**	Get dialog cancelled flag.
	 *
	 *	@return		true if dialog cancelled, false otherwise.
	 */

	public boolean getCancelled()
	{
		return cancelled;
	}

	/**	Renderer for checkbox list with work sets only as entries.
	 */

	protected class WorkSetsCheckBoxListRenderer
		extends CheckBoxListRenderer
	{
		public void setListItemText( Object object )
		{
			WorkSet workSet	=
				(WorkSet)((CheckBoxListItem)object).getObject();

			setText( new WordCounter( workSet ).toHTMLString() );
		}
	}

	/**	Renderer for checkbox list with word sets only as entries.
	 */

	protected class WordSetsCheckBoxListRenderer
		extends CheckBoxListRenderer
	{
		public void setListItemText( Object object )
		{
			WordSet wordSet	=
				(WordSet)((CheckBoxListItem)object).getObject();

			setText( new WordCounter( wordSet ).toHTMLString() );
		}
	}

	/**	Renderer for checkbox list with phrase sets only as entries.
	 */

	protected class PhraseSetsCheckBoxListRenderer
		extends CheckBoxListRenderer
	{
		public void setListItemText( Object object )
		{
			PhraseSet phraseSet	=
				(PhraseSet)((CheckBoxListItem)object).getObject();

			setText( new WordCounter( phraseSet ).toHTMLString() );
		}
	}

	/**	Renderer for checkbox list with queries only as entries.
	 */

	protected class QueriesCheckBoxListRenderer
		extends CheckBoxListRenderer
	{
		public void setListItemText( Object object )
		{
			WHQuery query	=
				(WHQuery)((CheckBoxListItem)object).getObject();

			String displayString	=
				query.toString() + " (" + query.getOwner() + ")";

			setText( displayString );
		}
	}

	/**	Renderer for list with queries only as entries.
	 */

	protected class QueriesListRenderer
		extends DefaultListCellRenderer
	{
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
		 *	@param	cellHasFocus	True if the cell has the focus.
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
			boolean cellHasFocus
		)
		{
			super.getListCellRendererComponent
			(
				list ,
				value ,
				index ,
				isSelected ,
				cellHasFocus
			);

			String displayString		= "";

			if ( value != null )
			{
				WHQuery query				= (WHQuery)value;

				displayString	=
					query.toString() + " (" + query.getOwner() + ")";
			}

			setText( displayString );

			return this;
		}
	}

	/**	Check if any word sets selected.
	 *
	 *	@param	listBox	The checkbox list.
	 *	@return		true if any set selected.
	 */

	protected boolean anySelected( CheckBoxList listBox )
	{
		boolean anyChecked	= false;

		for	(	int i = 0 ;
				i < listBox.getModel().getSize() ;
				i++ )
		{

			anyChecked	= anyChecked || listBox.isChecked( i );
			if ( anyChecked ) break;
		}

		return anyChecked;
	}

	/**	Renderer for list with worksets as entries.
	 */

	protected class WorkSetsListRenderer
		extends DefaultListCellRenderer
	{
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
		 *	@param	cellHasFocus	True if the cell has the focus.
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
			boolean cellHasFocus
		)
		{
			super.getListCellRendererComponent
			(
				list ,
				value ,
				index ,
				isSelected ,
				cellHasFocus
			);

			WorkSet workSet	= (WorkSet)value;

			setText( new WordCounter( workSet ).toHTMLString() );

			return this;
		}
	}

	/**	Renderer for list with word sets as entries.
	 */

	protected class WordSetsListRenderer
		extends DefaultListCellRenderer
	{
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
		 *	@param	cellHasFocus	True if the cell has the focus.
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
			boolean cellHasFocus
		)
		{
			super.getListCellRendererComponent
			(
				list ,
				value ,
				index ,
				isSelected ,
				cellHasFocus
			);

			WordSet wordSet	= (WordSet)value;

			setText( new WordCounter( wordSet ).toHTMLString() );

			return this;
		}
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

