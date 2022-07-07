package edu.northwestern.at.wordhoard.swing.text;

/*	Please see the license information at the end of this file. */

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.awt.print.*;

import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.model.text.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.swing.printing.*;
import edu.northwestern.at.utils.swing.plaf.smoothwindows.SmoothUtilities;

/**	A wrapped text component.
 *
 *	<p>A wrapped text component displays word-wrapped multilingual styled
 *	text with support for optional text selection and scrolling.
 */

public class WrappedTextComponent extends JComponent implements
	Scrollable, PrintableContents
{
	/**	The drawing context, or null if none. */

	private DrawingContext context;

	/**	The wrapped text, or null if none. */

	private WrappedText wrappedText;

	/**	Component height in pixels, not including insets. */

	private int height;

	/**	Font manager. */

	private FontManager fontManager = new FontManager();

	/**	The nominal line height. */

	private int nominalLineHeight;

	/**	True if dragging out a text selection. */

	private boolean draggingTextSelection;

	/**	Line index of marked line clicked, or -1 if none. */

	private int markedLineIndex = -1;

	/**	True if text selection is permitted. */

	private boolean selectable;

	/**	The selection in derived coordinates, or null. */

	private TextRange selection;

	/**	The anchor for dragging out selections. */

	private TextRange anchor;

	/**	Mode for extending selections: 0 = char, 1 = word, 2 = line. */

	private int extendSelectionMode;

	/**	Selection observable. */

	private Observable selectionObservable =
		new Observable() {
			public void notifyObservers () {
				setChanged();
				super.notifyObservers();
			}
		};

	/**	Autoscrolling thread, or null if none. */

	private Thread autoscrollThread;

	/**	Creates a new empty wrapped text component.
	 */

	public WrappedTextComponent () {
		addMouseListener(
			new MouseAdapter() {
				public void mousePressed (MouseEvent event) {
					try {
						handleMousePressed(event);
					} catch (Exception e) {
						Err.err(e);
					}
				}
				public void mouseReleased (MouseEvent event) {
					try {
						handleMouseReleased(event);
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);
		addMouseMotionListener(
			new MouseMotionAdapter() {
				public void mouseDragged (MouseEvent event) {
					try {
						handleMouseDragged(event);
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);
	}

	/**	Creates a new wrapped text component.
	 *
	 *	<p>Font information for the runs and line widths must be set
	 *	on entry for the unwrapped text.
	 *
	 *	@param	text			Unwrapped text.
	 *
	 *	@param	rightMargin		Right margin in pixels.
	 */

	public WrappedTextComponent (Text text, int rightMargin) {
		this();
		DrawingContext context = new DrawingContext(rightMargin);
		setText(text, context);
	}

	/**	Creates a new plain (unstyled) wrapped text component.
	 *
	 *	@param	str				Text.
	 *
	 *	@param	font			Font.
	 *
	 *	@param	rightMargin		Right margin.
	 */

	public WrappedTextComponent (String str, Font font, int rightMargin) {
		this();
		Text text = new Text(str, new FontInfoImpl(font));
		DrawingContext context = new DrawingContext(rightMargin);
		setText(text, context);
	}

	/**	Sets the text.
	 *
	 *	<p>Font information for the runs and line widths must be set
	 *	on entry for the unwrapped text.
	 *
	 *	@param	text				Unwrapped text.
	 *
	 *	@param	context				Drawing context.
	 *
	 *	@param	markerLocations		Marker locations in base coordinates,
	 *								or null if none.
	 */

	public void setText (Text text, DrawingContext context,
		TextLocation[] markerLocations)
	{
		try {
			this.context = context;
			int rightMargin = context.getRightMargin();
			int marginaliaWidth =
				context.hasMarginalia() ?
					context.getMarginaliaRightMargin() -
						context.getMarginaliaLeftMargin() :
				0;
			wrappedText =
				new WrappedText(text, rightMargin, marginaliaWidth);
			height = wrappedText.initializeForDrawing(context);
			nominalLineHeight =
				fontManager.getNominalLineHeight(TextParams.ROMAN);
			selection = null;
			if (markerLocations != null)
				wrappedText.setMarkers(markerLocations);
			revalidate();
			repaint();
		} catch (Exception e) {
			Err.err(e);
		}
	}

	/**	Sets the text.
	 *
	 *	<p>Font information for the runs and line widths must be set
	 *	on entry for the unwrapped text.
	 *
	 *	<p>No lines are marked.
	 *
	 *	@param	text				Unwrapped text.
	 *
	 *	@param	context				Drawing context.
	 */

	public void setText (Text text, DrawingContext context) {
		setText(text, context, null);
	}

	/**	Sets the selectable option.
	 *
	 *	@param	selectable		True if text selection is permitted. The
	 *							default value is false.
	 */

	public void setSelectable (boolean selectable) {
		this.selectable = selectable;
	}

	/**	Clears the text.
	 */

	public void clear () {
		wrappedText = null;
		selection = null;
		height = 0;
		revalidate();
		repaint();
	}

	/**	Gets the selection.
	 *
	 *	@return		The selection in base coordinates, or null if none.
	 */

	public TextRange getSelection () {
		if (wrappedText == null || selection == null) return null;
		return wrappedText.derivedToBase(selection);
	}

	/**	Sets the selection.
	 *
	 *	@param	selection	The selection in base coordinates,
	 *						or null if none.
	 */

	public void setSelection (TextRange selection) {
		if (wrappedText == null) return;
		this.selection = selection == null ? null :
			wrappedText.baseToDerived(selection);
		wrappedText.setSelection(selection);
		repaint();
		selectionObservable.notifyObservers();
	}

	/**	Selects all the text.
	 */

	public void selectAll () {
		if (wrappedText == null) return;
		TextLine[] lines = wrappedText.getLines();
		int numLines = lines.length;
		TextLine line = lines[numLines-1];
		selection =
			new TextRange(
				new TextLocation(0, 0),
				new TextLocation(numLines-1, line.getLength())
			);
		wrappedText.setSelection(selection);
		repaint();
		selectionObservable.notifyObservers();
	}

	/**	Copies the selected text to the system clipboard.
	 */

	public void copy () {
		if (wrappedText == null || selection == null ||
			selection.isEmpty()) return;
		String str = wrappedText.getText(selection);
		StringSelection sel = new StringSelection(str);
		Clipboard clipboard =
			Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(sel, sel);
	}

	/**	Scrolls to and selects a range of text.
	 *
	 *	@param	range		The text range in base coordinates.
	 *
	 *	@param	center		If true, center the range in
	 *						the view, even if it is already visible.
	 *						If false, center the range only if it is
	 *						not already visible.
	 */

	public void scrollTo (TextRange range, boolean center) {
		if (wrappedText == null) return;
		range = wrappedText.baseToDerived(range);
		JViewport viewport = getViewport();
		if (viewport != null) {
			viewport.setViewSize(getPreferredSize());
			int y = 0;
			if (range != null) {
				Insets insets = getInsets();
				TextLine[] lines = wrappedText.getLines();
				int numLines = lines.length;
				TextLocation start = range.getStart();
				TextLocation end = range.getEnd();
				int lineIndexStart = start.getIndex();
				int lineIndexEnd = end.getIndex();
				if (lineIndexStart >= 0 && lineIndexStart < numLines &&
					lineIndexEnd >= 0 && lineIndexEnd < numLines)
				{
					int linePixelOffsetStart =
						wrappedText.getLineLocation(lineIndexStart);
					int linePixelOffsetEnd =
						wrappedText.getLineLocation(lineIndexEnd);
					int lineHeightEnd = lines[lineIndexEnd].getHeight();
					Rectangle r = viewport.getViewRect();
					Dimension d = viewport.getViewSize();
					int yStart = insets.top + linePixelOffsetStart;
					int yEnd = insets.top + linePixelOffsetEnd +
						lineHeightEnd;
					int rangeHeight = yEnd - yStart;
					if (!center && yStart >= r.y &&
						yEnd <= r.y + r.height)
					{
						y = r.y;
					} else if (rangeHeight <= r.height) {
						y = insets.top + yStart - (r.height-rangeHeight)/2;
					} else {
						y = insets.top + yStart;
					}
					y = Math.min(y, d.height - r.height);
					y = Math.max(y, 0);
				}
			}
			viewport.setViewPosition(new Point(0,y));
		}
		selection = range;
		wrappedText.setSelection(selection);
		repaint();
		selectionObservable.notifyObservers();
	}

	/**	Adds a selection observer.
	 *
	 *	<p>The observer is notified whenever the selection changes.
	 *
	 *	@param	o	Observer.
	 */

	public void addSelectionObserver (Observer o) {
		selectionObservable.addObserver(o);
	}

	/**	Sets the line number interval.
	 *
	 *	<p>The line number interval controls the display of line numbers.
	 *	0 = no line numbers, 1 = every line numbered, n = every n'th line
	 *	numbered, -1 = stanza numbers.
	 *
	 *	@param	lineNumberInterval		The new line number interval.
	 */

	public void setLineNumberInterval (int lineNumberInterval) {
		if (wrappedText == null) return;
		wrappedText.setLineNumberInterval(lineNumberInterval);
		repaint();
	}

	/**	Sets or clears markers.
	 *
	 *	@param	markerLocations		Marker locations in base coordinates,
	 *								or null to clear markers.
	 */

	public void setMarkers (TextLocation[] markerLocations) {
		wrappedText.setMarkers(markerLocations);
		revalidate();
		repaint();
	}

	/**	Translates a point to text coordinates.
	 *
	 *	@param	p		Point in component coordinates.
	 *
	 *	@return			Point in text coordinates.
	 */

	private Point translatePoint (Point p) {
		Insets insets = getInsets();
		int markerOffset = context == null ? 0 : context.getMarkerOffset();
		return new Point(
			p.x - insets.left - markerOffset,
			p.y - insets.top);
	}

	/**	Gets the location of a point.
	 *
	 *	@param	p		Point.
	 *
	 *	@param	round	If true, location is rounded to nearest location
	 *					in the text.
	 *
	 *	@return			Location in base coordinates.
	 */

	public TextLocation getLocationOfPoint (Point p, boolean round) {
		if (wrappedText == null) return new TextLocation(-1, 0);
		p = translatePoint(p);
		LocationAndCharset locAndCharset = wrappedText.viewToModel(p);
		TextLocation loc = locAndCharset.getLocation();
		if (round) loc = wrappedText.roundLocation(loc);
		return wrappedText.derivedToBase(loc);
	}

	/**	Gets the preferred size of the component.
	 *
	 *	@return		Preferred size of the component.
	 */

	public Dimension getPreferredSize () {
		Insets insets = getInsets();
		if (context == null)
			return new Dimension(insets.left + insets.right,
				insets.top + insets.bottom);
		int rightMargin = context.getRightMargin();
		boolean hasLineNumbers = context.hasLineNumbers();
		boolean hasMarginalia = context.hasMarginalia();
		int lineNumberRightMargin = hasLineNumbers ?
			context.getLineNumberRightMargin() : 0;
		int marginaliaRightMargin = hasMarginalia ?
			context.getMarginaliaRightMargin() : 0;
		rightMargin = Math.max(rightMargin, lineNumberRightMargin);
		rightMargin = Math.max(rightMargin, marginaliaRightMargin);
		return new Dimension(
			insets.left + insets.right + rightMargin,
			insets.top + insets.bottom + height);
	}

	/**	Gets the minimum size of the component.
	 *
	 *	@return		Minimum size of the component.
	 */

	public Dimension getMinimumSize () {
		return getPreferredSize();
	}

	/**	Gets the maximum size of the component.
	 *
	 *	@return		Maximum size of the component.
	 */

	public Dimension getMaximumSize () {
		return getPreferredSize();
	}

	/**	Paints the component.
	 *
	 *	@param	graphics	Graphics environment.
	 */

	protected void paintComponent (Graphics graphics) {
		Graphics2D g = (Graphics2D)graphics.create();
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(getForeground());
		Insets insets = getInsets();
		if (wrappedText != null) wrappedText.draw(g,
			insets.left + context.getMarkerOffset(), insets.top);
		g.dispose();
	}

	/**	Paints the component.
	 *
	 *	@param	graphics	Graphics environment.
	 */

	public void paint (Graphics graphics) {
// $$$PIB$$$ Added to get font smoothing.
		SmoothUtilities.configureGraphics(graphics);
// $$$PIB$$$ End
		super.paint(graphics);
	}

	/**	Handles a mouse pressed event.
	 *
	 *	@param	event	Mouse event.
	 */

	private void handleMousePressed (MouseEvent event) {
		if (!selectable || wrappedText == null) return;
		Point p = translatePoint(event.getPoint());
		int clickCount = event.getClickCount();
		extendSelectionMode = clickCount <= 1 ? 0 :
			(clickCount == 2 ? 1 : 2);
		LocationAndCharset locAndCharset = wrappedText.viewToModel(p);
		byte charset = locAndCharset.getCharset();
		TextLocation loc = locAndCharset.getLocation();
		int lineIndex = loc.getIndex();
		if (p.x < 0 && wrappedText.isMarked(lineIndex)) {
			markedLineIndex = lineIndex;
			draggingTextSelection = false;
		} else {
			markedLineIndex = -1;
			draggingTextSelection = true;
			if (loc.isInText()) {
				if (clickCount <= 1) {
					selection = new TextRange(loc);
				} else if (clickCount == 2) {
					TextRange wordLocation =
						wrappedText.getWordLocation(loc, charset);
					selection = wordLocation;
				} else {
					TextRange lineLocation = wrappedText.getLineLocation(loc);
					selection = lineLocation;
				}
			} else {
				loc = wrappedText.roundLocation(loc);
				selection = new TextRange(loc);
			}
			wrappedText.setSelection(selection);
			anchor = selection;
			repaint();
		}
	}

	/**	Handles a mouse dragged event.
	 *
	 *	@param	event	Mouse event.
	 */

	private void handleMouseDragged (MouseEvent event) {
		if (!selectable || wrappedText == null) return;
		if (!draggingTextSelection) return;
		if (autoscrollThread != null) autoscrollThread.interrupt();
		Point p = event.getPoint();
		autoscroll(p);
		extendSelection(p);
	}

	/**	Handles a mouse released event.
	 *
	 *	@param	event	Mouse event.
	 *
	 *	@throws	Exception	general error.
	 */

	private void handleMouseReleased (MouseEvent event)
		throws Exception
	{
		if (wrappedText == null) return;
		if (draggingTextSelection) {
			if (!selectable) return;
			if (autoscrollThread != null) autoscrollThread.interrupt();
			selectionObservable.notifyObservers();
		} else if (markedLineIndex >= 0) {
			Point p = translatePoint(event.getPoint());
			LocationAndCharset locAndCharset = wrappedText.viewToModel(p);
			TextLocation loc = locAndCharset.getLocation();
			int lineIndex = loc.getIndex();
			if (p.x < 0 && lineIndex == markedLineIndex) {
				TextRange textRange = wrappedText.getLineLocation(loc);
				textRange = wrappedText.derivedToBase(textRange);
				handleMarkerClick(textRange);
			}
			markedLineIndex = -1;
		}
	}

	/**	Handles a marker click.
	 *
	 *	<p>Subclasses may override this method to handle clicks on
	 *	markers. The default action is to do nothing.
	 *
	 *	@param	range	Text range of line containing marker clicked,
	 *					in base coordinates.
	 *
	 *	@throws	Exception	general error.
	 */

	public void handleMarkerClick (TextRange range)
		throws Exception
	{
	}

	/**	Extends the selection.
	 *
	 *	@param	p	Mouse location.
	 */

	private void extendSelection (Point p) {
		p = translatePoint(p);
		LocationAndCharset locAndCharset = wrappedText.viewToModel(p);
		TextLocation loc = locAndCharset.getLocation();
		byte charset = locAndCharset.getCharset();
		loc = wrappedText.roundLocation(loc);
		switch (extendSelectionMode) {
			case 0:
				if (anchor.getStart().compareTo(loc) < 0) {
					selection = new TextRange(
						anchor.getStart(), loc);
				} else {
					selection = new TextRange(
						loc, anchor.getEnd());
				}
				break;
			case 1:
				TextRange wordLocation =
					wrappedText.getWordLocation(loc, charset);
				if (anchor.getStart().compareTo(wordLocation.getEnd()) < 0) {
					selection = new TextRange(
						anchor.getStart(), wordLocation.getEnd());
				} else {
					selection = new TextRange(
						wordLocation.getStart(), anchor.getEnd());
				}
				break;
			case 2:
				TextRange lineLocation = wrappedText.getLineLocation(loc);
				if (anchor.getStart().compareTo(lineLocation.getEnd()) < 0) {
					selection = new TextRange(
						anchor.getStart(), lineLocation.getEnd());
				} else {
					selection = new TextRange(
						lineLocation.getStart(), anchor.getEnd());
				}
				break;
		}
		wrappedText.setSelection(selection);
		repaint();
	}

	/**	Autoscrolls if the mouse is above or below the view rectangle.
	 *
	 *	@param	p		Mouse location.
	 */

	private void autoscroll (final Point p) {
		final JViewport viewport = getViewport();
		if (viewport == null) return;
		Rectangle r = viewport.getViewRect();
		if (p.y < r.y) {
			p.y = r.y;
		} else if (p.y >= r.y + r.height) {
			p.y = r.y + r.height;
		} else {
			return;
		}
		autoscrollThread =
			new Thread(
				new Runnable() {
					public void run () {
						while (!Thread.interrupted()) {
							try {
								Thread.sleep(100);
							} catch (Exception e) {
								break;
							}
							final Rectangle r = viewport.getViewRect();
							Dimension d = viewport.getViewSize();
							int delta = getScrollableUnitIncrement(
								r, SwingConstants.VERTICAL, 1);
							if (p.y == r.y) {
								if (p.y == 0) break;
								p.y = Math.max(0, p.y-delta);
								r.y = p.y;
							} else if (p.y == r.y + r.height) {
								if (p.y == d.height) break;
								p.y = Math.min(d.height, p.y+delta);
								r.y = p.y - r.height;
							} else {
								break;
							}
							SwingUtilities.invokeLater(
								new Runnable() {
									public void run () {
										viewport.setViewPosition(
											new Point(0, r.y));
										extendSelection(p);
									}
								}
							);
						}
					}
				}
			);
		autoscrollThread.start();
	}

	/**	Gets the parent viewport.
	 *
	 *	@return		The parent viewport, or null if none.
	 */

	public JViewport getViewport () {
		Container parent = getParent();
		return parent instanceof JViewport ? (JViewport)parent : null;
	}

	/**	Gets the preferred size of the viewport.
	 *
	 *	@return		Preferred size of the viewport.
	 */

	public Dimension getPreferredScrollableViewportSize () {
		return getPreferredSize();
	}

	/**	Gets the unit increment for scrolling.
	 *
	 *	@param		visibleRect		The view area visible within the
	 *								viewport.
	 *
	 *	@param		orientation		SwingConstants.VERTICAL or
	 *								SwingConstants.HORIZONTAL.
	 *
	 *	@param		direction		Less than zero to scroll up/left,
	 *								greater than zero for down/right.
	 *
	 *	@return		The unit increment = the height of one line of
	 *				nominal font size text (for vertical scrolling),
	 *				or 0 (for horizontal scrolling, which is unused).
	 */

	public int getScrollableUnitIncrement (Rectangle visibleRect,
		int orientation, int direction)
	{
		if (orientation == SwingConstants.VERTICAL) {
			return nominalLineHeight;
		} else {
			return 0;
		}
	}

	/**	Gets the block increment for scrolling.
	 *
	 *	@param		visibleRect		The view area visible within the
	 *								viewport.
	 *
	 *	@param		orientation		SwingConstants.VERTICAL or
	 *								SwingConstants.HORIZONTAL.
	 *
	 *	@param		direction		Less than zero to scroll up/left,
	 *								greater than zero for down/right.
	 *
	 *	@return		The block increment = the height of the visible
	 *				rect minus the height of one line of nominal font
	 *				size text (for vertical scrolling), or 0 (for
	 *				horizontal scrolling, which is unused).
	 */

	public int getScrollableBlockIncrement (Rectangle visibleRect,
		int orientation, int direction)
	{
		if (orientation == SwingConstants.VERTICAL) {
			return visibleRect.height - nominalLineHeight;
		} else {
			return 0;
		}
	}

	/**	Returns true if a viewport should always force the width of this
	 *	component to match the width of the viewport.
	 *
	 *	@return		false.
	 */

	public boolean getScrollableTracksViewportWidth () {
		return false;
	}

	/**	Returns true if a viewport should always force the height of this
	 *	component to match the height of the viewport.
	 *
	 *	@return		false.
	 */

	public boolean getScrollableTracksViewportHeight () {
		return false;
	}

	/** Prints the text,
	 *
	 *	@param	title			Title for output.
	 *	@param	printerJob		The printer job.
	 *	@param	pageFormat		The printer page format.
	 */

	public void printContents
	(
		final String title,
		final PrinterJob printerJob,
		final PageFormat pageFormat
	)
	{
		Thread runner = new Thread("Print text")
		{
			public void run()
			{
				PrintUtilities.printComponent(
					getPrintableComponent( title , pageFormat ),
					title,
					printerJob,
					pageFormat,
					true
				);
			}
		};

		runner.start();
	}

	/** Return printable component.
	 *
	 *	@param		title		Title for printing.
	 *
	 *	@param		pageFormat	Page format for printing.
	 *
	 *	@return					Printable component for XList.
	 */

	public PrintableComponent getPrintableComponent
	(
		String title,
		PageFormat pageFormat
	)
	{
		return
			new PrintableComponent
			(
				this ,
				pageFormat,
				new PrintHeaderFooter(
					title,
					"Printed " +
						DateTimeUtils.formatDateOnAt( new Date() ),
					"Page " )
			);
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

