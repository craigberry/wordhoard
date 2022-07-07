package edu.northwestern.at.wordhoard.model.text;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import java.text.*;
import java.awt.*;

/**	Text.
 *
 *	<p>Text consists of a list of lines ({@link
 *	edu.northwestern.at.wordhoard.model.text.TextLine TextLine} objects),
 *	plus attributes that specify whether or not the text includes 
 *	line numbers or marginalia.
 *
 *	<p>Text objects for work parts are created and made persistent in the 
 *	object model by the build tools. 
 *	They are stored as serialized objects in a type "mediumblob" column in 
 *	the MySQL database. They are fetched as needed from the database and 
 *	deserialized by Hibernate. 
 *
 *	<p>Some properties are used only during construction of the object, some
 *	are used only during presentation in the client, and some are used by
 *	both. Only the properties used by both are serialized and persisted.
 *
 *	<p>The text must be finalized by calling the {@link #finalize finalize}
 *	method before it can be serialized or used. Some methods are
 *	intended for use during construction, and can only be called before
 *	finalization. Other methods are intended for use only after finalization.
 */

public class Text implements Externalizable, Cloneable {

	/**	Serial version UID. */

	static final long serialVersionUID = 4722056890310561290L;
	
	/**	Array of lines, or null if not yet finalized. */
	
	private TextLine[] lines;
	
	/**	Number of lines, or 0 if not yet finalized. */
	
	private int numLines;
	
	/**	True if the text has line numbers. */
	
	private boolean lineNumbers;
	
	/**	True if the text has marginalia. */
	
	private boolean marginalia;
	
	/**	List of lines, or null if finalized. 
	 *	(Used only during construction - not serialized.) 
	 */
	
	private ArrayList lineList = new ArrayList();
	
	/**	True if the last line was blank. 
	 *	(Used only during construction - not serialized.) 
	 */
	
	private boolean lastWasBlank;
	
	/**	True to collaps multiple blank lines to one blank line. */
	
	private boolean collapseBlankLines = true;
	
	/**	Extra indentation in pixels for new lines. 
	 *	(Used only during construction - not serialized.) 
	 */
	
	private int extraIndentation;
	
	/**	Drawing context. 
	 *	(Used only in client - not serialized.) 
	 */
	
	private DrawingContext context;
	
	/**	The selection range, or null if none. 
	 *	(Used only in client - not serialized.) 
	 */
	
	private TextRange selection;
	
	/**	The pixel offsets (y coordinates) of the top of each line, with
	 *	an extra element at the end of the array containing the pixel
	 *	offset (y coordinate) of the bottom of the last line.
	 *	(Used only in client - not serialized.) 
	 */
	
	private int[] linePixelOffsets;
	
	/**	Creates a new text object.
	 *
	 *	<p>The new text object initially contains no lines.
	 *
	 *	@param	lineNumbers		True if this text has line numbers.
	 *
	 *	@param	marginalia		True if this text has marginalia.
	 */
	 
	public Text (boolean lineNumbers, boolean marginalia) {
		this.lineNumbers = lineNumbers;
		this.marginalia = marginalia;
	}
	
	/**	Creates a new text object.
	 *
	 *	<p>The new text object has no line numbers or marginalia and 
	 *	initially contains no lines.
	 */
	 
	public Text () {
		this(false, false);
	}
	
	/**	Creates a new text object with a single line and no line
	 *	numbers or marginalia.
	 *
	 *	<p>The width and vertical positioning information are computed
	 *	for the line, and the text is finalized and ready to be
	 *	used.
	 *
	 *	@param	line		Line.
	 */
	 
	public Text (TextLine line) {
		this();
		line.finalize();
		line.computeWidth();
		line.computeVerticalPositioningInformation();
		appendLine(line);
		finalize();
	}
	
	/**	Creates a new text object with a single plain line and no line
	 *	numbers or marginalia.
	 *
	 *	<p>The width and vertical positioning information are computed
	 *	for the line, and the text is finalized and ready to be
	 *	used.
	 *
	 *	@param	str			Text.
	 *
	 *	@param	fontInfo	Font info.
	 */
	 
	public Text (String str, FontInfo fontInfo) {
		this(new TextLine(str, fontInfo));
	}
	
	/**	Appends a line to the text.
	 *
	 *	<p>This method can only be used during construction, before the
	 *	text is finalized. If it is called after finalization a null 
	 *	pointer exception is thrown.
	 *
	 *	@param	line		The line.
	 */
	 
	public void appendLine (TextLine line) {
		if (line.isEmpty()) {
			appendBlankLine();
		} else {
			line.addIndentation(extraIndentation);
			lineList.add(line);
			lastWasBlank = false;
		}
	}
	
	/**	Appends a plain line to the text.
	 *
	 *	<p>This method can only be used during construction, before the
	 *	text is finalized. If it is called after finalization a null 
	 *	pointer exception is thrown.
	 *
	 *	@param	str			The text of the line.
	 *
	 *	@param	charset		Character set.
	 *
	 *	@param	number		Integer line number.
	 *
	 *	@param	label		Line number label.
	 *
	 *	@return				Index of appended line.
	 */
	 
	public int appendLine (String str, byte charset, int number, String label) {
		int lineIndex = lineList.size();
		appendLine(new TextLine(str, charset, TextParams.NOMINAL_FONT_SIZE, 
			false, false, TextLine.LEFT, 0, number, label));
		return lineIndex;
	}
	
	/**	Appends a blank line to the text. 
	 *
	 *	<p>This method can only be used during construction, before the
	 *	text is finalized. If it is called after finalization a null 
	 *	pointer exception is thrown.
	 */
	
	public void appendBlankLine () {
		if (lastWasBlank && collapseBlankLines) return;
		lineList.add(new TextLine("", TextParams.ROMAN,
			TextParams.BLANK_LINE_FONT_SIZE, 
			false, false, TextLine.LEFT, 0, 0, null));
		lastWasBlank = true;
	}
	
	/**	Copies a line to the text.
	 *
	 *	<p>This method can only be used during construction, before the
	 *	text is finalized. If it is called after finalization a null 
	 *	pointer exception is thrown.
	 *
	 *	<p>The line is appended to the text unconditionally and without
	 *	alteration. There is no check for collapsing blank lines, and no
	 *	extra indentation is added.
	 *
	 *	@param	line		The line.
	 */
	 
	public void copyLine (TextLine line) {
		lineList.add(line);
	}
	
	/**	Copies a line with font information.
	 *
	 *	<p>This method can only be used during construction, before the
	 *	text is finalized. If it is called after finalization a null 
	 *	pointer exception is thrown.
	 *
	 *	<p>The line is appended to the text unconditionally and without
	 *	alteration. There is no check for collapsing blank lines, and no
	 *	extra indentation is added.
	 *
	 *	<p>The line is finalized, and width and vertical positioning 
	 *	information are computed for the line.
	 *
	 *	@param	str			String.
	 *
	 *	@param	fontInfo	Font info.
	 */
	 
	public void copyLine (String str, FontInfo fontInfo) {
		TextLine line = new TextLine(str, fontInfo);
		copyLine(line);
	}
	
	/**	Gets the number of lines.
	 *
	 *	<p>This method can be used before or after finalization.
	 *
	 *	@return		The number of lines.
	 */
	 
	public int getNumLines () {
		return lines == null ? lineList.size() : lines.length;
	}
	
	/**	Sets the collapse blank lines option.
	 *
	 *	@param	collapseBlankLines		If true, multiple sequental blank lines
	 *									are collapsed to a single blank line.
	 *									This is the default.
	 */
	 
	public void setCollapseBlankLines (boolean collapseBlankLines) {
		this.collapseBlankLines = collapseBlankLines;
	}
	
	/**	Sets extra indentation.
	 *
	 *	@param	extraIndentation	Extra indentation for all subsequent 
	 *								lines.
	 */
	 
	public void setExtraIndentation (int extraIndentation) {
		this.extraIndentation = extraIndentation;
	}
	
	/**	Finalizes the text.
	 *
	 *	<p>Trims trailing blank lines and finalizes the text object and
	 *	all of its lines. This method must be called before the text object 
	 *	can be serialized or used. If the text has already been finalized, 
	 *	this method does nothing.
	 */
	
	public void finalize () {
		if (lines == null) {
			for (Iterator it = lineList.iterator(); it.hasNext(); ) {
				TextLine line = (TextLine)it.next();
				line.finalize();
			}
			for (int i = lineList.size()-1; i >= 0; i--) {
				TextLine line = (TextLine)lineList.get(i);
				if (!line.isEmpty()) break;
				lineList.remove(i);
			}
			numLines = lineList.size();
			lines = (TextLine[])lineList.toArray(
				new TextLine[numLines]);
		}
		lineList = null;
	}
	
	/**	Gets the lines.
	 *
	 *	@return		The lines, or null if the text has not yet been finalized.
	 */
	 
	public TextLine[] getLines () {
		return lines;
	}
	
	/**	Gets a specific line.
	 *
	 *	@param	lineIndex	The index of the requested line.
	 *	@return		The requested line, or null if the text has not yet been finalized.
	 */
	 
	public TextLine getLine (int lineIndex) {
		if(lineIndex>0 && lineIndex<lines.length) return (TextLine)lines[lineIndex];
		else return null;
	}
	

	/**	Returns true if the text has line numbers.
	 *
	 *	@return		True if text has line numbers.
	 */
	 
	public boolean hasLineNumbers () {
		return lineNumbers;
	}
	
	/**	Returns true if the text has marginalia.
	 *
	 *	@return		True if text has marginalia.
	 */
	 
	public boolean hasMarginalia () {
		return marginalia;
	}
	
	/**	Rounds a location to the nearest character of text.
	 *
	 *	<p>The text must be finalized, or a null pointer exception is 
	 *	thrown.
	 *
	 *	@param	loc		Location.
	 *
	 *	@return			Rounded location.
	 */
	
	public TextLocation roundLocation (TextLocation loc) {
		int lineIndex = loc.getIndex();
		int charOffsetInLine = loc.getOffset();
		if (lineIndex == -1) {
			return new TextLocation(0, 0);
		} else if (lineIndex == Integer.MAX_VALUE) {
			if (numLines == 0) return new TextLocation(0, 0);
			TextLine line = lines[numLines-1];
			return new TextLocation(numLines-1, line.getLength());
		} else if (charOffsetInLine == -1) {
			return new TextLocation(lineIndex, 0);
		} else if (charOffsetInLine == Integer.MAX_VALUE) {
			TextLine line = lines[lineIndex];
			return new TextLocation(lineIndex, 
				line.getLength());
		} else {
			return loc;
		}
	}
	
	/**	Gets the location of a word.
	 *
	 *	<p>The text must be finalized, or a null pointer exception is 
	 *	thrown.
	 *
	 *	@param	loc			A location in the text.
	 *
	 *	@param	charset		Character set.
	 *
	 *	@return				The location (range) of the word containing that 
	 *						location, in derived coordinates.
	 */
	
	public TextRange getWordLocation (TextLocation loc, byte charset) {
		int lineIndex = loc.getIndex();
		int charOffsetInLine = loc.getOffset();
		TextLine line = lines[lineIndex];
		String str = line.getText();
		int len = str.length();
		BreakIterator breakIterator = BreakIterator.getWordInstance();
		breakIterator.setText(str);
		int wordStart = 0;
		int wordEnd = 0;
		if (charOffsetInLine >= len) {
			wordStart = len;
			wordEnd = len;
		} else {
			wordEnd = breakIterator.following(charOffsetInLine);
			wordStart = breakIterator.previous();
			if (charset == TextParams.GREEK) {
				// In Greek, an apostrophe at the end of a word
				// is part of the word.
				if (wordEnd < len && str.charAt(wordEnd) == '\'') {
					wordEnd++;
				} else if (wordStart < len && 
					str.charAt(wordStart) == '\'' &&
					wordEnd == wordStart + 1 &&
					wordStart > 0 &&
					str.charAt(wordStart-1) > 0x100)
				{
					wordStart = breakIterator.previous();
				}
				
			}
		}
		return new TextRange(
			new TextLocation(lineIndex, wordStart),
			new TextLocation(lineIndex, wordEnd));
	}
	
	/**	Gets the location of a line.
	 *
	 *	<p>The text must be finalized, or a null pointer exception is 
	 *	thrown.
	 *
	 *	@param	loc		A location in the text.
	 *
	 *	@return			The location (range) of the line containing that 
	 *					location, in derived coordinates.
	 */
	 
	public TextRange getLineLocation (TextLocation loc) {
		int lineIndex = loc.getIndex();
		TextLine line = lines[lineIndex];
		return new TextRange(
			new TextLocation(lineIndex, 0),
			new TextLocation(lineIndex, line.getLength()));
	}
	
	/**	Gets a range of text.
	 *
	 *	<p>The text must be finalized, or a null pointer exception is 
	 *	thrown.
	 *
	 *	@param	range	Range.
	 *
	 *	@return			Text contained in range, as a string.
	 */
	 
	public String getText (TextRange range) {
		StringBuffer buf = new StringBuffer();
		TextLocation start = range.getStart();
		TextLocation end = range.getEnd();
		int startIndex = start.getIndex();
		int endIndex = end.getIndex();
		int startOffset = start.getOffset();
		int endOffset = end.getOffset();
		if (startIndex < 0) startIndex = 0;
		if (endIndex >= startIndex) {
			if (startOffset < 0) startOffset = 0;
			TextLine line = lines[endIndex];
			String text = line.getText();
			int len = text.length();
			if (endOffset > len) endOffset = len;
		}
		for (int i = startIndex; i <= endIndex; i++) {
			TextLine line = lines[i];
			String text = line.getText();
			if (i == startIndex) {
				if (i == endIndex) {
					buf.append(text.substring(startOffset, endOffset));
				} else {
					buf.append(text.substring(startOffset));
				}
			} else if (i == endIndex) {
				buf.append(text.substring(0, endOffset));
			} else {
				buf.append(text);
			}
			if (i < endIndex) buf.append('\n');
		}
		return buf.toString();
	}
	
	/**	Computes line widths.
	 *
	 *	<p>The text must be finalized, or a null pointer exception is 
	 *	thrown.
	 *
	 *	<p>The font info must be set for all the runs in the line.
	 */
	 
	public void computeLineWidths () {
		for (int i = 0; i < lines.length; i++) lines[i].computeWidth();
	}
	
	/**	Computes vertical positioning information.
	 *
	 *	<p>The text must be finalized, or a null pointer exception is 
	 *	thrown.
	 *
	 *	<p>The font info must be set for all the runs in the line.
	 */
	 
	public void computeVerticalPositioningInformation () {
		for (int i = 0; i < lines.length; i++)
			lines[i].computeVerticalPositioningInformation();
	}
	
	/**	Converts a location from derived to base coordinates.
	 *
	 *	<p>Subclasses which present derived views of model text (e.g., 
	 *	paragraph wapped views or views with translations) must override
	 *	this method to convert a location in the derived coordinate system
	 *	to the corresponding location in the base coordinate system.
	 *
	 *	<p>This base class implementation does no translation. The
	 *	location is returned unchanged.
	 *
	 *	<p>See the package documentation for more details on derived
	 *	and base coordinate systems.
	 *
	 *	@param	loc		Text location in derived coordinates.
	 *
	 *	@return			Text location in base coordinates.
	 */
	 
	public TextLocation derivedToBase (TextLocation loc) {
		return loc;
	}
	
	/**	Converts a range from derived to base coordinates.
	 *
	 *	@param	range		Text range in derived coordinates.
	 *
	 *	@return				Text range in base coordinates.
	 */
	 
	public TextRange derivedToBase (TextRange range) {
		TextLocation start = range.getStart();
		TextLocation end = range.getEnd();
		return new TextRange(derivedToBase(start),
			derivedToBase(end));
	}
	
	/**	Converts a location from base to derived coordinates.
	 *
	 *	<p>Subclasses which present derived views of model text (e.g., 
	 *	paragraph wapped views or views with translations) must override
	 *	this method to convert a location in the base coordinate system
	 *	to the corresponding location in the derived coordinate system.
	 *
	 *	<p>This base class implementation does no translation. The
	 *	location is returned unchanged.
	 *
	 *	<p>See the package documentation for more details on derived
	 *	and base coordinate systems.
	 *
	 *	@param	loc		Text location in base cooordinates.
	 *
	 *	@return			Text location in derived coordinates.
	 */
	 
	public TextLocation baseToDerived (TextLocation loc) {
		return loc;
	}
	
	/**	Converts a model range to view text coordinates.
	 *
	 *	@param	range		Text range in model text cooordinates.
	 *
	 *	@return				Text range in view text coordinates.
	 */
	 
	public TextRange baseToDerived (TextRange range) {
		TextLocation start = range.getStart();
		TextLocation end = range.getEnd();
		return new TextRange(baseToDerived(start),
			baseToDerived(end));
	}
	
	/**	Clones the text.
	 *
	 *	<p>The text must be finalized, or a null pointer exception is 
	 *	thrown.
	 *
	 *	<p>The clone is a deep copy of the text.
	 *
	 *	@return			Deep clone of the text.
	 */
	 
	public Object clone () {
		try {
			Text copy = (Text)super.clone();
			copy.lines = new TextLine[numLines];
			for (int i = 0; i < numLines; i++) 
				copy.lines[i] = (TextLine)lines[i].clone();
			return copy;
		} catch (CloneNotSupportedException e) {
			// can't happen.
			throw new InternalError(e.toString());
		}
	}

	/**	Writes the text to an object output stream (serializes the object).
	 *
	 *	<p>The text must be finalized, or a null pointer exception is 
	 *	thrown.
	 *
	 *	@param	out		Object output stream.
	 *
	 *	@throws	IOException	I/O error.
	 */

	public void writeExternal (ObjectOutput out)
		throws IOException
	{
		out.writeBoolean(lineNumbers);
		out.writeBoolean(marginalia);
		out.writeInt(numLines);
		for (int i = 0; i < numLines; i++) 
			lines[i].writeExternal(out);
	}

	/**	Reads the text from an object input stream (deserializes the object).
	 *
	 *	<p>The deserialized text is finalized.</p>
	 *
	 *	@param	in		Object input stream.
	 *
	 *	@throws	IOException	I/O error.
	 *
	 *	@throws	ClassNotFoundException	class not found.
	 */

	public void readExternal (ObjectInput in)
		throws IOException, ClassNotFoundException
	{
		lineNumbers = in.readBoolean();
		marginalia = in.readBoolean();
		numLines = in.readInt();
		lines = new TextLine[numLines];
		for (int i = 0; i < numLines; i++) {
			TextLine line = new TextLine();
			line.readExternal(in);
			lines[i] = line;
		}
	}
	
	/**	Initializes the text for drawing.
	 *
	 *	<p>The text must be finalized, the font info must be set for all the
	 *	runs in the line, and each line's width and vertical positioning
	 *	information must be computed.
	 *
	 *	@param	context			Drawing context.
	 *
	 *	@return					Height of the text in pixels.
	 */
	
	public int initializeForDrawing (DrawingContext context) {
		this.context = context;
		boolean hasMarginalia = context.hasMarginalia();
		DrawingContext marginaliaContext = null;
		if (hasMarginalia) {
			int marginaliaMargin = context.getMarginaliaRightMargin() -
				context.getMarginaliaLeftMargin();
			marginaliaContext = new DrawingContext(marginaliaMargin);
		}
		linePixelOffsets = new int[numLines+1];
		int y = 0;
		for (int i = 0; i < numLines; i++) {
			TextLine line = lines[i];
			linePixelOffsets[i] = y;
			y += line.getHeight();
			if (hasMarginalia) {
				Text marginalia = line.getMarginalia();
				if (marginalia != null)
					marginalia.initializeForDrawing(marginaliaContext);
			}
		}
		linePixelOffsets[numLines] = y;
		return y;
	}
	
	/**	Gets the index of a line given its pixel offset (a y coordinate
	 *	position in the component).
	 *
	 *	<p>If the y coordinate is above the first line, the line index 0
	 *	is returned (the first line). If the y coordinate is below the
	 *	last line, the line index numLines-1 is returned (the last line).
	 *
	 *	@param	y		Pixel offset (y coordinate).
	 *
	 *	@return			Index of the line.
	 */
	
	private int getLineIndexGivenPixelOffset (int y) {
		int k = Arrays.binarySearch(linePixelOffsets, y);
		if (k < 0) {
			k = -k-2;
			if (k < 0) k = 0;
		}
		if (k >= numLines) k = numLines - 1;
		return k;
	}
	
	/**	Draws the text.
	 *
	 *	<p>The text must be initialized for drawing.
	 *
	 *	<p>Drawing is done relative to the top and left pixel coordinates
	 *	of the text in the graphics context. The top coordinate is the y
	 *	coordinate of the top of the first line. The left coordinate is the
	 *	x coordinate of the left edge of the text, not counting markers in
	 *	the left margin. If the text contains markers, they are drawn to 
	 *	the left of the x coordinate, using an offset supplied by the
	 *	drawing context.
	 *
	 *	@param	g			Graphics context.
	 *
	 *	@param	x			X coordinate of left edge of text.
	 *
	 *	@param	y			Y coordinate of top of text.
	 */
	
	public void draw (Graphics2D g, int x, int y) {
		if (numLines == 0) return;
		context.setGraphics(g);
		Rectangle clipRect = g.getClipBounds();
		int topOffset = clipRect.y - y;
		int botOffset = topOffset + clipRect.height;
		int firstIndex = getLineIndexGivenPixelOffset(topOffset);
		int lastIndex = getLineIndexGivenPixelOffset(botOffset);
		int selStartLineIndex = Integer.MAX_VALUE;
		int selStartCharOffset = Integer.MAX_VALUE;
		int selEndLineIndex = -1;
		int selEndCharOffset = -1;
		if (selection != null) {
			TextLocation selStart = selection.getStart();
			TextLocation selEnd = selection.getEnd();
			selStartLineIndex = selStart.getIndex();
			selStartCharOffset = selStart.getOffset();
			selEndLineIndex = selEnd.getIndex();
			selEndCharOffset = selEnd.getOffset();
		}
		for (int lineIndex = firstIndex; lineIndex <= lastIndex; lineIndex++) {
			int selStartOffsetInLine = Integer.MAX_VALUE;
			int selEndOffsetInLine = -1;
			if (selStartLineIndex <= lineIndex &&
				selEndLineIndex >= lineIndex)
			{
				selStartOffsetInLine = 
					selStartLineIndex < lineIndex ? 0 :
						selStartCharOffset;
				selEndOffsetInLine = 
					selEndLineIndex > lineIndex ? Integer.MAX_VALUE :
						selEndCharOffset;
			}
			TextLine line = lines[lineIndex];
			line.draw(context, x, y + linePixelOffsets[lineIndex], 
				selStartOffsetInLine, selEndOffsetInLine);
		}
	}
	
	/**	Sets the line number interval.
	 *
	 *	<p>The line number interval controls the display of line numbers.
	 *	0 = no line numbers, 1 = every line numbered, n = every n'th line
	 *	numbered, -1 = stanza numbers.
	 *
	 *	@param	lineNumberInterval		Line number interval.
	 */
	 
	public void setLineNumberInterval (int lineNumberInterval) {
		context.setLineNumberInterval(lineNumberInterval);
	}
	
	/**	Sets the selection.
	 *
	 *	@param	selection		The selection range, or null if none.
	 */
	 
	public void setSelection (TextRange selection) {
		this.selection = selection;
	}
	
	/**	Converts a point to a location and character set.
	 *
	 *	@param	p		Point.
	 *
	 *	@return			Location and character set.
	 */
	
	public LocationAndCharset viewToModel (Point p) {
		int x = p.x;
		int y = p.y;
		if (y < 0) return new LocationAndCharset(-1, 0);
		if (y >= linePixelOffsets[numLines])
			return new LocationAndCharset(Integer.MAX_VALUE, 0);
		int lineIndex = getLineIndexGivenPixelOffset(y);
		TextLine line = lines[lineIndex];
		return line.viewToModel(context, lineIndex, x);
	}
	
	/**	Gets the location of a line.
	 *
	 *	@param	lineIndex		Line index.
	 *
	 *	@return					Y coordinate of top of line.
	 */
	 
	public int getLineLocation (int lineIndex) {
		return linePixelOffsets[lineIndex];
	}
	
	/**	Sets markers.
	 *
	 *	@param	locations	Marker locations in base coordinates, or
	 *						null to clear all markers.
	 */
	 
	public void setMarkers (TextLocation[] locations) {
		for (int i = 0; i < lines.length; i++) lines[i].setMarked(false);
		if (locations != null) {
			for (int i = 0; i < locations.length; i++) {
				TextLocation loc = locations[i];
				loc = roundLocation(loc);
				loc = baseToDerived(loc);
				int lineIndex = loc.getIndex();
				if (lineIndex >= 0 && lineIndex < numLines) 
					lines[lineIndex].setMarked(true);
			}
		}
	}
	
	/**	Returns true if a line is marked.
	 *
	 *	@param	lineIndex		Line index.
	 *
	 *	@return					True if line is marked.
	 */
	 
	public boolean isMarked (int lineIndex) {
		if (lineIndex < 0 || lineIndex >= numLines) return false;
		return lines[lineIndex].isMarked();
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

