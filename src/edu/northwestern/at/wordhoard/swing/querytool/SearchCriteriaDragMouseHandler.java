package edu.northwestern.at.wordhoard.swing.querytool;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.event.*;

public class SearchCriteriaDragMouseHandler implements MouseListener, MouseMotionListener {
	int mouseButtonDown = MouseEvent.NOBUTTON;
	long mouseButtonDownSince = 0;
	MouseEvent firstMouseEvent = null;

	public void mousePressed(MouseEvent e) {
		firstMouseEvent = e;
		e.consume();
	}
	public void mouseReleased(MouseEvent e)  {mouseButtonDown = MouseEvent.NOBUTTON;}
	public void mouseClicked(MouseEvent e)  {;}
	public void mouseEntered(MouseEvent e)  {;}
	public void mouseExited(MouseEvent e)  {;}

	public void mouseDragged(MouseEvent e) {
		if (firstMouseEvent != null) {
			e.consume();

			int ctrlMask = InputEvent.CTRL_DOWN_MASK;
			int action = ((e.getModifiersEx() & ctrlMask) == ctrlMask) ?
				  TransferHandler.COPY : TransferHandler.MOVE;

			int dx = Math.abs(e.getX() - firstMouseEvent.getX());
			int dy = Math.abs(e.getY() - firstMouseEvent.getY());
			if (dx > 5 || dy > 5) {
				JComponent c = (JComponent)e.getSource();
				TransferHandler handler = c.getTransferHandler();
				handler.exportAsDrag(c, firstMouseEvent, action);
				firstMouseEvent = null;
			}
		}
	}
	public void mouseMoved(MouseEvent e) {;}
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

