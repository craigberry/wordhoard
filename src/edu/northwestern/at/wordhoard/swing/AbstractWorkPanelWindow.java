package edu.northwestern.at.wordhoard.swing;

/*	Please see the license information at the end of this file. */

import java.awt.*;

import edu.northwestern.at.wordhoard.swing.annotations.*;
import edu.northwestern.at.wordhoard.swing.info.*;
import edu.northwestern.at.wordhoard.swing.dialogs.*;
import edu.northwestern.at.wordhoard.swing.work.*;
import edu.northwestern.at.wordhoard.swing.find.*;
import edu.northwestern.at.wordhoard.swing.text.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.utils.db.*;

/**	An abstract base class for windows which contain a work panel.
 */

public abstract class AbstractWorkPanelWindow extends AnnotationWindow {

	/**	The work panel associated with this window. */

	private WorkPanel workPanel;

	/**	Creates a new abstract work panel window.
	 *
	 *	@param	title		The window title.
	 *
	 *	@param	parentWindow	The parent window, or null if none.
	 *
	 *	@throws	PersistenceException
	 */

	public AbstractWorkPanelWindow (String title, AbstractWindow parentWindow)
		throws PersistenceException
	{
		super(title, parentWindow);
		enableGetInfoCmd(true);
		enableLineNumberCmds(true);
		AnnotationModel model = getAnnotationModel();
		model.setNoAnnotationsMessage(
			"There are no annotations for this text");
	}

	/**	Handles the first window activation event.
	 *
	 *	<p>Gives focus to the work panel.
	 */

	public void handleFirstWindowActivation () {
		if (workPanel != null) workPanel.requestFocus();
	}

	/**	Gets the work panel associated with this window.
	 *
	 *	@return		The work panel associated with this window.
	 */

	public WorkPanel getWorkPanel () {
		return workPanel;
	}

	/**	Sets the work panel associated with this window.
	 *
	 *	@param	workPanel		The work panel associated with this window.
	 */

	public void setWorkPanel (WorkPanel workPanel) {
		this.workPanel = workPanel;
	}

	/**	Handles the "Get Info" command.
	 *
	 *	@throws	Exception
	 */

	public void handleGetInfoCmd ()
		throws Exception
	{
		if (workPanel == null) return;
		Word word = workPanel.getWord();
		WordInfoWindow.open(word, this);
	}


	/**	Gets the currently focused wrapped text component.
	 *
	 *	@return		The currently focused wrapped text component,
	 *				or null if none.
	 */

	private WrappedTextComponent getFocusedTextComponent () {
		KeyboardFocusManager kfm =
			KeyboardFocusManager.getCurrentKeyboardFocusManager();
		Component c = kfm.getPermanentFocusOwner();
		if (c != null && c instanceof WrappedTextComponent &&
			((WrappedTextComponent)c).getTopLevelAncestor() == this)
		{
			return (WrappedTextComponent)c;
		} else {
			return null;
		}
	}

	/**	Handles edit menu selected.
	 *
	 *	@throws	Exception
	 */

	public void handleEditMenuSelected ()
		throws Exception
	{
		enableCutCmd(false);
		enablePasteCmd(false);
		WrappedTextComponent c = getFocusedTextComponent();
		if (c == null) {
			if (workPanel == null) {
				enableCopyCmd(false);
				enableSelectAllCmd(false);
			} else {
				TextRange selection = workPanel.getSelection();
				enableCopyCmd(selection != null && !selection.isEmpty());
				enableSelectAllCmd(true);
			}
		} else {
			TextRange selection = c.getSelection();
			enableCopyCmd(selection != null && !selection.isEmpty());
			enableSelectAllCmd(true);
		}

	}

	/**	Handles the "Copy" command.
	 *
	 *	@throws	Exception
	 */

	public void handleCopyCmd ()
		throws Exception
	{
		WrappedTextComponent c = getFocusedTextComponent();
		if (c == null) {
			if (workPanel != null) workPanel.handleCopyCmd();
		} else {
			c.copy();
		}
	}

	/**	Handles the "Select All" command.
	 *
	 *	@throws	Exception
	 */

	public void handleSelectAllCmd ()
		throws Exception
	{
		WrappedTextComponent c = getFocusedTextComponent();
		if (c == null) {
			if (workPanel != null) workPanel.handleSelectAllCmd();
		} else {
			c.selectAll();
		}
	}

	/**	Handles the "Find Words" command.
	 *
	 *	@throws	Exception
	 */

	public void handleFindWordsCmd ()
		throws Exception
	{
		if (workPanel == null) return;
		SearchDefaults defaults = workPanel.getWord();
		if (defaults == null) defaults = workPanel.getWorkPart();
		new FindWindow(getCorpus(), defaults, this);
	}

	/**	Handles the "Go To" command.
	 *
	 *	@param	str		Ignored.
	 *
	 *	@throws	Exception
	 */

	public void handleGoToWordCmd (String str)
		throws Exception
	{
		if (workPanel == null) return;
		Word word = workPanel.getWord();
		String tag = word == null ? null : word.getTag();
		super.handleGoToWordCmd(tag);
	}

	/**	Handles the line number commands.
	 *
	 *	@param	n		0 for no line numbers, 1 to number every line,
	 *					5 to number every fifth line, or -1 to number
	 *					stanzas.
	 *
	 *	@throws	Exception
	 */

	public void handleLineNumberCmd (int n)
		throws Exception
	{
		if (workPanel == null) return;
		workPanel.setLineNumberInterval(n);
	}

	/**	Handles the "Translations" command.
	 *
	 *	@throws	Exception
	 */

	public void handleTranslationsCmd ()
		throws Exception
	{
		if (workPanel == null) return;
		Corpus corpus = getCorpus();
		if (!corpus.hasTranslations()) return;
		TranslationsDialog dlog = new TranslationsDialog(corpus, this);
		if (dlog.canceled()) return;
		workPanel.changeTranslations();
	}

	/**	Find a printable component.
	 *
	 *	@return		A printable component.
	 */

	protected Component findPrintableComponent () {
		WrappedTextComponent c = getFocusedTextComponent();
		if (c == null) return this;
		else return c;
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

