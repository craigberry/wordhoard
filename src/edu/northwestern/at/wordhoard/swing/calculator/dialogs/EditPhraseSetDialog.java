package edu.northwestern.at.wordhoard.swing.calculator.dialogs;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.wordhoard.model.userdata.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;
import edu.northwestern.at.wordhoard.swing.*;

/**	Displays edit phrase set dialog.
 */

public class EditPhraseSetDialog
	extends NewPhraseSetDialog
{
	/**	List holds phrase sets. */

	protected DefaultListModel listModel		= new DefaultListModel();
	protected XList phaseSetsListBox			= new XList( listModel );

	/**	Scroll pane for list of phrase sets. */

	protected XScrollPane phaseSetsScrollPane	=
		new XScrollPane( phaseSetsListBox );

	/**	The phrase set to update. */

	protected static PhraseSet phaseSetToUpdate	= null;

	/**	Create edit phrase set dialog.
	 *
	 *	@param	dialogTitle	Title for dialog.
	 *	@param	parent		Parent window for dialog.
	 */

	public EditPhraseSetDialog
	(
		String dialogTitle ,
		JFrame parent
	)
	{
		super
		(
			dialogTitle ,
			parent ,
			true
		);

		buildDialog();

		phaseSetsScrollPane.setPreferredSize( new Dimension( 400 , 100 ) );
	}

	/**	Initialize the dialog fields. */

	protected void initializeFields()
	{
		super.initializeFields();

								//	Word set to update.

		phaseSetToUpdate		= null;

								//	Remove current phrase set list.
		listModel.clear();
								//	Get phrase sets.

		PhraseSet[] phaseSets	= PhraseSetUtils.getPhraseSets();

								//	Add them to list box.

		if ( phaseSets != null ) phaseSetsListBox.setListData( phaseSets );

								//	Set renderer for phrase sets to edit.

		phaseSetsListBox.setCellRenderer
		(
			new PhraseSetsListRenderer()
		);
								//	Single selection in
								//	phrase sets list box.

		phaseSetsListBox.setSelectionMode
		(
			ListSelectionModel.SINGLE_SELECTION
		);
								//	Listen for changes to list.

		phaseSetsListBox.addListSelectionListener
		(
			new PhraseSetsListSelectionListener()
		);
	}

	/** Adds fields to the dialog.
	 *
	 *	@param	dialogFields	The component holding the dialog fields.
	 */

	protected void addFields( LabeledColumn dialogFields )
	{
		dialogFields.addPair
		(
			WordHoardSettings.getString
			(
				"PhraseSets" ,
				"Phrase sets:"
			) ,
			phaseSetsScrollPane
		);

		initializeFields();
	}

	/**	Handles the OK button pressed. */

	protected void handleOKButtonPressed( ActionEvent event )
	{
		if ( phaseSetsListBox.getSelectedValue() == null )
		{
			new ErrorMessage
			(
				WordHoardSettings.getString
				(
					"youDidNotSelectAPhraseSet",
					"You did not select a phrase set. "
				)
			);

			return;
		}
		else
		{
			phaseSetToUpdate	=
				(PhraseSet)phaseSetsListBox.getSelectedValue();
		}

		super.handleOKButtonPressed( event );
	}

	/**	Get the selected phrase set.
	 *
	 *	@return		Work set to update.
	 */

	public PhraseSet getPhraseSet()
	{
		return phaseSetToUpdate;
	}

	/**	Handle changes to selected phrase set. */

	protected class PhraseSetsListSelectionListener
		implements ListSelectionListener
	{
		/**	Create listener.
		 */

		public PhraseSetsListSelectionListener()
		{
			super();
		}

		/**	Handle change to selection in phaseSet list.
		 *
		 *	@param	event	The list selection event.
		 */

		public void valueChanged( ListSelectionEvent event )
		{
								//	Get list which fired this event.

			JList list		= (JList)event.getSource();

								//	Get currently selected value.

			Object object	= list.getSelectedValue();

								//	If there is a selected value ...

			if ( object != null )
			{
								//	Set the phrase set title field and
								//	change the list of checked work parts
								//	in the work parts tree to match those
								//	in the selected phrase set.
				try
				{
					PhraseSet phaseSet	= (PhraseSet)object;
					titleEditField.setText( phaseSet.getTitle() );
					descriptionEditField.setText( phaseSet.getDescription() );
					webPageURLEditField.setText( phaseSet.getWebPageURL() );
					isPublicCheckBox.setSelected( phaseSet.getIsPublic() );
				}
				catch ( Exception e )
				{
					Err.err( e );
				}
	        }
		}
	}

	/**	Renderer for list with phrase sets as entries.
	 */

	protected class PhraseSetsListRenderer
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

			PhraseSet phaseSet	= (PhraseSet)value;

			setText( new WordCounter( phaseSet ).toHTMLString() );

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

