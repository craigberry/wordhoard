package edu.northwestern.at.wordhoard.swing.annotations;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.*;

import edu.northwestern.at.utils.db.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.text.*;

/**	An abstract base class for annotation windows.
 *
 *	<p>An annotation window contains an optional annotation panel on the
 *	right which can be shown and hidden. The subclass is responsible for
 *	the primary content of the window. This class manages showing and
 *	hiding the annotation panel.
 */

public abstract class AnnotationWindow extends AbstractWindow {

	/**	Annotation model. */

	private AnnotationModel model;

	/**	Window content without annotations. */

	private Container contentWithoutAnnotations;

	/**	Window size without annotations. */

	private Dimension windowSizeWithoutAnnotations;

	/**	Window location without annotations. */

	private Point windowLocationWithoutAnnotations;

	/**	Annotation panel. */

	private AnnotationPanel annotationPanel;

	/**	Font manager. */

	private FontManager fontManager = new FontManager();

	/**	Creates a new annotation window.
	 *
	 *	@param	title			The window title.
	 *
	 *	@param	parentWindow	The parent window, or null if none.
	 *
	 *	@throws	PersistenceException
	 */

	public AnnotationWindow (String title, AbstractWindow parentWindow)
		throws PersistenceException
	{
		super(title, parentWindow);
		model = new AnnotationModel();
		enableShowHideAnnotationMarkersCmd(true);
		enableShowHideAnnotationPanelCmd(true);
		setShowHideAnnotationMarkersCmdText(model.markersShown());
		setShowHideAnnotationPanelCmdText(model.panelShown());

		model.addListener(
			new AnnotationAdapter() {
				public void markersShownOrHidden (boolean shown) {
					showMarkers(shown);
				}
				public void panelShownOrHidden (boolean shown) {
					showPanel(shown);
				}
			}
		);
	}

	/**	Gets the annotation model.
	 *
	 *	@return		The annotation model.
	 */

	public AnnotationModel getAnnotationModel () {
		return model;
	}

	/**	Handles the "Show/Hide Annotation Markers" command.
	 *
	 *	@throws	PersistenceException
	 */

	public void handleShowHideAnnotationMarkersCmd ()
		throws PersistenceException
	{
		boolean markersShown = model.markersShown();
		model.showMarkers(!markersShown);
	}

	/**	Shows or hides annnotation markers.
	 *
	 *	@param	show		True to show markers, false to hide them.
	 */

	private void showMarkers (boolean show) {
		setShowHideAnnotationMarkersCmdText(show);
	}

	/**	Handles the "Show/Hide Annotation Panel" command.
	 */

	public void handleShowHideAnnotationPanelCmd () {
		boolean panelShown = model.panelShown();
		model.showPanel(!panelShown);
		if (!panelShown) model.setIndex(0);
	}

	/**	Shows or hides the annotation panel.
	 *
	 *	@param	show		True to show the panel, false to hide it.
	 */

	private void showPanel (boolean show) {
		setShowHideAnnotationPanelCmdText(show);
		if (show) {
			windowSizeWithoutAnnotations = getSize();
			windowLocationWithoutAnnotations = getLocation();
			contentWithoutAnnotations = getContentPane();
			JPanel leftPanel = new JPanel();
			leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
			leftPanel.add(contentWithoutAnnotations);
			leftPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,10));
			annotationPanel = new AnnotationPanel(model, this, fontManager);
			JSplitPane splitPane = new JSplitPane(
				JSplitPane.HORIZONTAL_SPLIT, leftPanel, annotationPanel);
			splitPane.setDividerLocation(500);
			splitPane.setResizeWeight(1.0);
			splitPane.setContinuousLayout(true);
			setContentPane(splitPane);
			Dimension size = getSize();
			Point location = getLocation();
			Dimension screenSize = getToolkit().getScreenSize();
			int screenWidth = screenSize.width;
			size.width += 510;
			location.x = Math.min(location.x, screenWidth - 3 - size.width);
			location.x = Math.max(location.x, 3);
			setSize(size);
			setLocation(location);
		} else {
			setContentPane(contentWithoutAnnotations);
			setSize(windowSizeWithoutAnnotations);
			setLocation(windowLocationWithoutAnnotations);
			contentWithoutAnnotations.requestFocus();
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

