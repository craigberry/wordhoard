package edu.northwestern.at.wordhoard.swing.work;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.tcon.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.utils.db.*;

import edu.northwestern.at.wordhoard.swing.annotations.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.annotations.*;

/**	A work window.
 */

public class WorkWindow extends AbstractWorkPanelWindow
	implements WindowFocusListener
{
	/**	Work panel. */

	WorkPanel workPanel;

	/**	Creates a new work window.
	 *
	 *	@param	corpus			The corpus.
	 *
	 *	@param	workPart		The work part.
	 *
	 *	@param	parentWindow	The parent window, or null if none.
	 *
	 *	@throws	PersistenceException
	 */

	public WorkWindow (Corpus corpus, WorkPart workPart,
		AbstractWindow parentWindow)
			throws PersistenceException
	{

		super(workPart.getWork().getFullTitle(), parentWindow);

		setCorpus(corpus);

		workPanel = new WorkPanel(WordHoard.getPm(),
			this);
		workPanel.setBorder(BorderFactory.createEmptyBorder(
			10, 0, 0, 0));

		setWorkPanel(workPanel);

		setContentPane(workPanel);

		pack();
		Dimension screenSize = getToolkit().getScreenSize();
		Dimension windowSize = getSize();
		windowSize.height = screenSize.height -
			(WordHoardSettings.getTopSlop() + WordHoardSettings.getBotSlop());
		setSize(windowSize);
		setLocation(new Point(3, WordHoardSettings.getTopSlop()));
		if (parentWindow instanceof TableOfContentsWindow) {
			int tconWidth = parentWindow.getSize().width;
			int workWidth = windowSize.width;
			int screenWidth = screenSize.width;
			if (24 + tconWidth + 2*workWidth <= screenWidth) {
				positionNextTo(parentWindow);
			}
		} else if (parentWindow != null) {
			positionNextTo(parentWindow);
		}

		addWindowFocusListener(this);
		setVisible(true);

		workPanel.setPart(workPart);
	}

	/**	Handle window gained focus event.
	 *
	 *	@param	e	Window event.
	 *
	 *	<p>
	 *	Enables the annotate menu command, if available.
	 *	</p>
	 */

	public void windowGainedFocus(WindowEvent e) {
		annotateCmd.setEnabled( isAnnotateCmdAvailable() &&
			(WordHoardSettings.getUserID() != null) &&
			(true));
//			(WordHoardSettings.getAnnotationWriteServerURL() != null));
	}

	/**	Handle window lost focus event.
	 *
	 *	@param	e	Window event.
	 *
	 *	<p>
	 *	Disables the annotate menu command.
	 *	</p>
	 */

	public void windowLostFocus(WindowEvent e) {
		annotateCmd.setEnabled(false);
	}

	/**	Handles the "attach annotation" command.
	 *
	 *	@throws	Exception
	 */

	public void handleAnnotateCmd ()
		throws Exception
	{
		if (workPanel == null) return;

		TextRange selection = workPanel.getSelection();
		if(selection!=null) {
			try {
				WorkPart wp = workPanel.getWorkPart();
				AuthoredTextAnnotation ra = new AuthoredTextAnnotation(WordHoardSettings.getUserID(), wp, selection);
				AnnotationEditor ae = new AnnotationEditor(ra);
				if(!ae.isCanceled()) workPanel.resetPart(selection);
			} catch (PersistenceException e) {
				Err.err(e);
				return;
			}
		}
	}

	/**	Determine is annotate menu item available.
	 *
	 *	@return		true if annotate menu item available.
	 */

	protected boolean isAnnotateCmdAvailable() {
		return annotateCmd.isVisible();
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

