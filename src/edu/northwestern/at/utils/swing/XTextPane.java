package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.awt.image.*;
import java.awt.print.*;
import javax.swing.undo.*;
import javax.swing.text.html.HTMLEditorKit;

import edu.northwestern.at.utils.swing.icons.*;
import edu.northwestern.at.utils.swing.printing.*;
import edu.northwestern.at.utils.swing.styledtext.*;
import edu.northwestern.at.utils.*;

/**	A JTextPane with extra support for {@link StyledString}'s and other
 *	features.
 *
 *	<ul>
 *	<li>Methods are provided to get, set, insert, and append styled strings.
 *	<li>Cut, copy, and paste of styled strings is supported.
 *	<li>Links are supported - underlined blue text which can be single-clicked
 *		to "go to" the link target.
 *	<li>The text pane is given a default two pixel border on all four sides.
 *	<li>The default font is Serif 14.
 *	<li>Tab stops are set to every 25 pixels.
 *	<li>For Java 1.3 we fix a bug where character attributes are erroneously
 *		reset to default values when a break is inserted at the end of the
 *		document to start a new paragraph.
 *	</ul>
 */

public class XTextPane extends JTextPane
	implements ClipboardOwner,
		PrintableContents, ClipboardHasPasteableData, CutCopyPaste,
		SelectAll, SaveToFile
{
	/**	Link information. */

	public static class LinkInfo {
		Position begin;		// beginning of link in document
		int length;			// length of link in document
		Link link;			// link target
	}

	/**	Left-alignment simple attribute set. */

	private static SimpleAttributeSet leftAlign = new SimpleAttributeSet();

	static {
		StyleConstants.setAlignment(leftAlign, StyleConstants.ALIGN_LEFT);
	}

	/**	True if the left, center, and right alignment options are allowed. */

	private boolean alignment;

	/**	The styled document. */

	private DefaultStyledDocument doc;

	/**	List of link information. */

	private ArrayList linkInfoList = new ArrayList();

	/** Current cursor. */

	private Cursor currentCursor = Cursors.DEFAULT_CURSOR;

	/** Undo manager.  May be null if none. */

	private UndoManager undoManager;

	/**	Constructs a new XTextPane.
	 *
	 *	@param	styledStr	Initial styled text contents, or null if none.
	 *
	 *	@param	alignment	True if the left, center, and right alignment
	 *						options are allowed.
	 */

	public XTextPane (StyledString styledStr, boolean alignment) {

		super();

		this.alignment = alignment;

		if (!Env.IS_JAVA_14_OR_LATER) {

			//	This hack fixes a bug in Java 1.3, where character attributes
			//	are erroneously reset to default values when a break is
			//	inserted at the end of the document to start a new paragraph.
			//
			//	We fix this by overriding createInputAttributes. In the case
			//	that needs fixing (at the end of the doucment), we back up
			//	one position to get the right element to use to reset the
			//	input attributes.

			setEditorKit(
				new XStyledEditorKit () {
//				new StyledEditorKit () {
					protected void createInputAttributes (Element element,
						MutableAttributeSet set)
					{
						DefaultStyledDocument doc =
							(DefaultStyledDocument)element.getDocument();
						int docLength = doc.getLength();
						int startOffset = element.getStartOffset();
						int endOffset = element.getEndOffset();
						if (docLength > 0 &&
							startOffset == docLength &&
							endOffset == startOffset+1)
								element = doc.getCharacterElement(docLength-1);
						super.createInputAttributes(element, set);
					}
				}
			);
		}
		else
		{
			setEditorKit(
				new XStyledEditorKit() );
//				new StyledEditorKit() );
		}

		//	Get the styled document.

		doc = (DefaultStyledDocument)getDocument();

		//	Set the border and font.

		setBorder(2);
		setFont(Fonts.serif, 14);

		//	Set the tab stops to every 25 pixels.

		TabStop[] tabStops = new TabStop[100];
		for (int i = 0; i < 100; i++) tabStops[i] = new TabStop((i+1)*25);
		TabSet tabSet = new TabSet(tabStops);
		Style defaultStyle = doc.getStyle(StyleContext.DEFAULT_STYLE);
		StyleConstants.setTabSet(defaultStyle, tabSet);

		//	Set the initial styled string contents.

		if (styledStr != null) {
			if (styledStr.str != null) setText(styledStr.str);
			setStyleInfo(styledStr.styleInfo, 0);
		}

		//	Provide our own cut/copy/paste actions.

		InputMap inputMap = getInputMap();
		ActionMap actionMap = getActionMap();

		KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_X,
			Env.MENU_SHORTCUT_KEY_MASK);
		Object actionKey = inputMap.get(key);
		actionMap.put(actionKey, cutAction);

		key = KeyStroke.getKeyStroke(KeyEvent.VK_C,
			Env.MENU_SHORTCUT_KEY_MASK);
		actionKey = inputMap.get(key);
		actionMap.put(actionKey, copyAction);

		key = KeyStroke.getKeyStroke(KeyEvent.VK_V,
			Env.MENU_SHORTCUT_KEY_MASK);
		actionKey = inputMap.get(key);
		actionMap.put(actionKey, pasteAction);

		// No undo manager by default.

		undoManager = null;

		//	Add a mouse listener for clicks on links.

		addMouseListener(mouseListener);
		addMouseMotionListener(mouseMotionListener);
	}

	/**	Constructs a new XTextPane with plain string as initial text.
	 */

	public XTextPane ( String s ) {
		this( new StyledString( s , null ) , true );
	}

	/**	Constructs a new XTextPane with no initial text.
	 */

	public XTextPane () {
		this( new StyledString() , true );
	}

	/**	Constructs a new XTextPane with alignment allowed.
	 *
	 *	@param	styledStr	Initial styled text contents, or null if none.
	 */

	public XTextPane (StyledString styledStr) {
		this(styledStr, true);
	}

	/** Gets the document.
	 *
	 *	@return		The document.
	 */

	public Document getDoc() {
		return doc;
	}

	/** Gets the document length.
	 *
	 *	@return		The document length.
	 */

	public int getDocLength() {
		return doc.getLength();
	}

	/**	Sets the border.
	 *
	 *	@param	pixels		Number of pixels in border on all four sides.
	 */

	public void setBorder (int pixels) {
		setBorder(BorderFactory.createEmptyBorder(pixels, pixels,
			pixels, pixels));
	}

	/**	Sets the font.
	 *
	 *	@param	family		Font family name.
	 *
	 *	@param	size		Font size.
	 */

	public void setFont (String family, int size) {
		Style defaultStyle = doc.getStyle(StyleContext.DEFAULT_STYLE);
		StyleConstants.setFontFamily(defaultStyle, family);
		StyleConstants.setFontSize(defaultStyle, size);
	}

	/**	Gets the styled text.
	 *
	 *	@return		The styled string.
	 */

	public StyledString getStyledText () {
		try {
			return new StyledString(doc.getText(0, doc.getLength()),
				getStyleInfo());
		} catch (BadLocationException e) {
			return null;
		}
	}

	/**	Gets the styled currently selected text.
	 *
	 *	@return		The styled string for the currently selected text.
	 */

	public StyledString getStyledSelection () {
		String str = getSelectedText();
		StyleInfo info =
			getStyleInfo(getSelectionStart(), getSelectionEnd());
		return new StyledString(str, info);
	}

	/**	Sets the styled text.
	 *
	 *	@param	styledStr	Styled string.
	 */

	public void setStyledText (StyledString styledStr) {
		doc.setParagraphAttributes(0, 0, leftAlign, false);
		if (styledStr == null || styledStr.str == null) {
			setText("");
		} else {
			setText(styledStr.str);
			setStyleInfo(styledStr.styleInfo, 0);
		}
	}

	/**	Appends styled text.
	 *
	 *	@param	styledStr	Styled string.
	 */

	public void appendStyledText (StyledString styledStr) {
		insertStyledText(doc.getLength(), styledStr);
	}

	/**	Appends plain text.
	 *
	 *	@param	str			String.
	 */

	public void appendPlainText (String str) {
		insertPlainText(doc.getLength(), str);
	}

	/**	Appends bold text.
	 *
	 *	@param	str			String.
	 */

	public void appendBoldText (String str) {
		insertBoldText(doc.getLength(), str);
	}

	/**	Appends italic text.
	 *
	 *	@param	str			String.
	 */

	public void appendItalicText (String str) {
		insertItalicText(doc.getLength(), str);
	}

	/**	Appends underlined text.
	 *
	 *	@param	str			String.
	 */

	public void appendUnderlineText (String str) {
		insertUnderlinedText(doc.getLength(), str);
	}

	/**	Appends a link.
	 *
	 *	<p>Links are blue and underlined. A single-click on a link
	 *	goes to the linked object.
	 *
	 *	@param	str		String.
	 *
	 *	@param	link	Link.
	 */

	public void appendLink (String str, Link link) {
		insertLink(doc.getLength(), str, link);
	}

	/**	Inserts styled text.
	 *
	 *	@param	pos			Position in document to insert text.
	 *
	 *	@param	styledStr	Styled string.
	 *
	 *	@param	insertionAttributes		Default attributes for the
	 *									insertion.
	 *
	 *	@param	paragraphAttributes		Default paragraph attributes.
	 */

	public void insertStyledText (int pos, StyledString styledStr,
		AttributeSet insertionAttributes, AttributeSet paragraphAttributes)
	{
		if (styledStr == null || styledStr.str == null) return;
		try {
			doc.insertString(pos, styledStr.str, insertionAttributes);
			if (paragraphAttributes != null)
				doc.setParagraphAttributes(pos, styledStr.str.length(),
					paragraphAttributes, true);
			setStyleInfo(styledStr.styleInfo, pos);
		} catch (BadLocationException e) {
		}
	}

	/**	Inserts styled text.
	 *
	 *	@param	pos		Position in document to insert text.
	 *
	 *	@param	styledStr	Styled string.
	 */

	public void insertStyledText (int pos, StyledString styledStr) {
		insertStyledText(pos, styledStr, null, null);
	}

	/**	Inserts plain text.
	 *
	 *	@param	pos			Position in document to insert text.
	 *
	 *	@param	str			String.
	 */

	public void insertPlainText (int pos, String str) {
		if (str == null) return;
		try {
			doc.insertString(pos, str, null);
		} catch (BadLocationException e) {
		}
	}

	/**	Inserts bold text.
	 *
	 *	@param	pos			Position in document to insert text.
	 *
	 *	@param	str			String.
	 */

	public void insertBoldText (int pos, String str) {
		if (str == null) return;
		insertStyledText(pos, StyledStringUtils.emboldenText( str ),
			null, null);
	}

	/**	Inserts italicized text.
	 *
	 *	@param	pos			Position in document to insert text.
	 *
	 *	@param	str			String.
	 */

	public void insertItalicText (int pos, String str) {
		if (str == null) return;
		insertStyledText(pos, StyledStringUtils.italicizeText( str ),
			null, null);
	}

	/**	Inserts underlined text.
	 *
	 *	@param	pos			Position in document to insert text.
	 *
	 *	@param	str			String.
	 */

	public void insertUnderlinedText (int pos, String str) {
		if (str == null) return;
		insertStyledText(pos, StyledStringUtils.underlineText( str ),
			null, null);
	}

	/**	Inserts a link.
	 *
	 *	<p>Links are blue and underlined. A single-click on a link
	 *	goes to the linked object.
	 *
	 *	@param	pos		Position in document to insert text.
	 *
	 *	@param	str		String text to insert in document.
	 *
	 *	@param	link	Link, or null to just make the inserted
	 *					text blue and underlined.
	 */

	public void insertLink (int pos, String str, Link link) {
		SimpleAttributeSet sas = new SimpleAttributeSet();
		StyleConstants.setUnderline(sas, true);
		StyleConstants.setForeground(sas, Color.blue);
		try {
			doc.insertString(pos, str, sas);
			if (link == null) return;
			LinkInfo linkInfo = new LinkInfo();
			linkInfo.begin = doc.createPosition(pos);
			linkInfo.length = str.length();
			linkInfo.link= link;
			linkInfoList.add(linkInfo);
		} catch (BadLocationException e) {
		}
	}

	/** Replaces the currently selected text with styled text.
	 *
	 *	@param	styledStr	Replacement styled string.
	 */

	public void replaceSelection (StyledString styledStr) {
		replaceSelection("");
		insertStyledText(getSelectionStart(), styledStr);
	}

	/**	Notifies this object that it is no longer the owner of the
	 *	contents of the clipboard.
	 *
	 *	<p>This method does nothing. We ignored transfers of ownership
	 *	of the clipboard.
	 *
	 *	@param	clipboard	The clipboard that is no longer owned.
	 *
	 *	@param	contents	The contents which this owner had placed
	 *						on the clipboard.
	 */

	public void lostOwnership (Clipboard clipboard, Transferable contents) {
	}

	/**	Is cut enabled?
	 *
	 *	@return		true since cut is enabled.
	 */

	public boolean isCutEnabled()
	{
		return isEditable();
	}

	/**	Is copy enabled?
	 *
	 *	@return		true since copy is enabled.
	 */

	public boolean isCopyEnabled()
	{
		return true;
	}

	/**	Is paste enabled?
	 *
	 *	@return		true since paste is enabled.
	 */

	public boolean isPasteEnabled()
	{
		return isEditable();
	}

	/** Cut action. */

	private Action cutAction =
		new StyledEditorKit.StyledTextAction("cut")
	{
		public void actionPerformed (ActionEvent event) {
			cut();
		}
	};

	/**	Gets the cut action.
	 *
	 *	@return		The cut action.
	 */

	public Action getCutAction () {
		return cutAction;
	}

	/**	Cuts the selected text to the clipboard.
	 */

	public void cut () {
		if (!isEditable() || !isEnabled()) return;
		copy();
		replaceSelection("");
	}

	/** Copy action. */

	private Action copyAction =
		new StyledEditorKit.StyledTextAction("copy")
	{
		public void actionPerformed (ActionEvent event) {
			copy();
		}
	};

	/**	Gets the copy action.
	 *
	 *	@return		The copy action.
	 */

	public Action getCopyAction () {
		return copyAction;
	}

	/**	Copys the selected text to the clipboard.
	 */

	public void copy () {
		StyledString styledStr = getStyledSelection();
		SystemClipboard.setContents(styledStr, this);
	}

	/** Paste action. */

	private Action pasteAction =
		new StyledEditorKit.StyledTextAction("paste")
	{
		public void actionPerformed (ActionEvent event) {
			paste();
		}
	};

	/**	Gets the paste action.
	 *
	 *	@return		The paste action.
	 */

	public Action getPasteAction () {
		return pasteAction;
	}

	/** Delete action. */

	private Action deleteAction =
		new StyledEditorKit.StyledTextAction( "" )
	{
		public void actionPerformed( ActionEvent event )
		{
			replaceSelection( "" );
		}
	};

	/**	Gets the delete action.
	 *
	 *	@return		The delete action.
	 */

	public Action getDeleteAction () {
		return deleteAction;
	}

	/**	Pastes the contents of the clipboard.
	 */

	public void paste () {
		if (!isEditable() || !isEnabled()) return;
		Transferable content = SystemClipboard.getContents(this);
		if (content == null) return;
		try {
			if (content.isDataFlavorSupported(
				StyledString.STYLED_STRING_FLAVOR))
			{
				StyledString styledStr =
					(StyledString)content.getTransferData(
						StyledString.STYLED_STRING_FLAVOR);
				replaceSelection(styledStr);
			} else if (content.isDataFlavorSupported(
				StyledString.RTF_FLAVOR))
			{
				StyledString styledStr =
					StyledStringUtils.getStyledStringFromRTFTransferable( content );
				replaceSelection( styledStr );
			} else if (content.isDataFlavorSupported(
				DataFlavor.stringFlavor))
			{
				String str =
					(String)content.getTransferData(
						DataFlavor.stringFlavor);
				replaceSelection(str);
			}
		} catch (Exception e) {
		}
	}

	/** Check if clipboard has pasteable data.
	 *
	 *	@return		true if clipboard has pasteable text recognized
	 *				by XTextPane.
	 */

	public boolean clipboardHasPasteableData()
	{
		boolean result = false;

		Transferable content	= SystemClipboard.getContents( this );

		if ( content != null )
		{
			try
			{
				result =
					(content.isDataFlavorSupported(
						StyledString.STYLED_STRING_FLAVOR) ) ||
					(content.isDataFlavorSupported(
						StyledString.RTF_FLAVOR) ) ||
					(content.isDataFlavorSupported(
						DataFlavor.stringFlavor) );
			}
			catch ( Exception e )
			{
			}
        }

		return result;
	}

	/**	Gets the style information for a substring.
	 *
	 *	@param	subStart		Starting offset of substring.
	 *
	 *	@param	subEnd			Ending offset of substring.
	 *
	 *	@return					The style information for the substring.
	 */

	public StyleInfo getStyleInfo (int subStart, int subEnd) {
		ArrayList list = new ArrayList();
		ElementIterator it = new ElementIterator(doc);
		for (Element el = it.next(); el != null; el = it.next()) {
			int start = el.getStartOffset();
			int end = el.getEndOffset();
			start = Math.max(start-subStart, 0);
			end = Math.min(end-subStart, subEnd-subStart);
			if (start >= end) continue;
			AttributeSet a = el.getAttributes();
			for (Enumeration e = a.getAttributeNames();
				e.hasMoreElements(); )
			{
				Object key = e.nextElement();
				Object val = a.getAttribute(key);
				if (key.equals(StyleConstants.Bold)) {
					if (val.equals(Boolean.TRUE))
						list.add(new StyleRun(start, end,
							StyleRun.BOLD));
				} else if (key.equals(StyleConstants.Italic)) {
					if (val.equals(Boolean.TRUE))
						list.add(new StyleRun(start, end,
							StyleRun.ITALIC));
				} else if (key.equals(StyleConstants.Underline)) {
					if (val.equals(Boolean.TRUE))
						list.add(new StyleRun(start, end,
							StyleRun.UNDERLINE));
				} else if (key.equals(StyleConstants.StrikeThrough)) {
					if (val.equals(Boolean.TRUE))
						list.add(new StyleRun(start, end,
							StyleRun.STRIKETHROUGH));
				} else if (key.equals(StyleConstants.Subscript)) {
					if (val.equals(Boolean.TRUE))
						list.add(new StyleRun(start, end,
							StyleRun.SUBSCRIPT));
				} else if (key.equals(StyleConstants.Superscript)) {
					if (val.equals(Boolean.TRUE))
						list.add(new StyleRun(start, end,
							StyleRun.SUPERSCRIPT));
				} else if (key.equals(StyleConstants.Alignment)) {
					int alignment = ((Integer)val).intValue();
					if (alignment == StyleConstants.ALIGN_CENTER) {
						list.add(new StyleRun(start, end,
							StyleRun.ALIGNMENT, StyleRun.ALIGNMENT_CENTER));
					} else if (alignment == StyleConstants.ALIGN_RIGHT) {
						list.add(new StyleRun(start, end,
							StyleRun.ALIGNMENT, StyleRun.ALIGNMENT_RIGHT));
					} else if (alignment == StyleConstants.ALIGN_JUSTIFIED) {
						list.add(new StyleRun(start, end,
							StyleRun.ALIGNMENT, StyleRun.ALIGNMENT_JUSTIFIED));
					}
				} else if (key.equals(StyleConstants.FontFamily)) {
					String s = (String)val;
					if (s.equals("SansSerif")) {
						list.add(new StyleRun(start, end,
							StyleRun.FAMILY, StyleRun.FAMILY_SANS_SERIF));
					} else if (s.equals("Monospaced")) {
						list.add(new StyleRun(start, end,
							StyleRun.FAMILY, StyleRun.FAMILY_MONOSPACED));
					}
				} else if (key.equals(StyleConstants.FontSize)) {
					int size = ((Integer)val).intValue();
					int param = 0;
					switch (size) {
						case 8:
							param = StyleRun.SIZE_SMALLEST;
							break;
						case 10:
							param = StyleRun.SIZE_SMALLER;
							break;
						case 12:
							param = StyleRun.SIZE_SMALL;
							break;
						case 14:
							param = StyleRun.SIZE_NORMAL;
							break;
						case 16:
							param = StyleRun.SIZE_BIG;
							break;
						case 20:
							param = StyleRun.SIZE_BIGGER;
							break;
						case 26:
							param = StyleRun.SIZE_BIGGEST;
							break;
					}
					if (param != StyleRun.SIZE_NORMAL)
						list.add(new StyleRun(start, end,
							StyleRun.SIZE, param));
				} else if (key.equals(StyleConstants.Foreground)) {
					Color color = (Color)val;
					if (color.equals(Color.red)) {
						list.add(new StyleRun(start, end,
							StyleRun.COLOR, StyleRun.COLOR_RED));
					} else if (color.equals(Color.blue)) {
						list.add(new StyleRun(start, end,
							StyleRun.COLOR, StyleRun.COLOR_BLUE));
					} else if (color.equals(Color.green)) {
						list.add(new StyleRun(start, end,
							StyleRun.COLOR, StyleRun.COLOR_GREEN));
					}
				} else if (key.equals(StyleConstants.IconAttribute)) {
					ImageIcon icon = (ImageIcon)val;
					int iconIndex = Emoticons.indexOf( icon );
					if ( iconIndex >= 0 ) {
						list.add(new StyleRun(start, end,
							StyleRun.EMOTICON, iconIndex ));
					} else {
						iconIndex = StarIcons.indexOf( icon );
						if ( iconIndex >= 0 ) {
							list.add(new StyleRun(start, end,
								StyleRun.STARICON, iconIndex ));
						}
					}
				}
			}
		}
		return new StyleInfo(list);
	}

	/**	Gets the style information.
	 *
	 *	@return			The style information.
	 */

	private StyleInfo getStyleInfo () {
		return getStyleInfo(0, doc.getLength());
	}

	/**	Gets the links.
	 *
	 *	@return			Array of Links.
	 */

	public LinkInfo[] getLinks()
	{
		LinkInfo[] result = new LinkInfo[ linkInfoList.size() ];

		for ( int i = 0; i < linkInfoList.size(); i++ )
		{
			result[ i ] = (LinkInfo)(linkInfoList.get( i ) );
		}

		return result;
	}

	/**	Sets the links.
	 *
	 *	@param	links		Array of Links.
	 */

	public void setLinks( LinkInfo[] links )
	{
		linkInfoList = new ArrayList();

		for ( int i = 0; i < links.length; i++ )
		{
			linkInfoList.add( links[ i ] );
		}
	}

	/**	Sets the style information.
	 *
	 *	@param	styleInfo		The style information.
	 *
	 *	@param	pos				The position in the document of the string
	 *							for which style information is to be set.
	 */

	public void setStyleInfo (StyleInfo styleInfo, int pos) {
		if (styleInfo == null) return;
		StyleRun[] info = styleInfo.info;
		if (info == null) return;
		int docLength = doc.getLength();
		for (int i = 0; i < info.length; i++) {
			SimpleAttributeSet sas = new SimpleAttributeSet();
			StyleRun run = info[i];
			int offset = pos + run.start;
			if (offset < 0 || offset >= docLength) continue;
			int length = run.end - run.start;
			int end = offset + length;
			if (end <= 0) continue;
			if (end > docLength) end = docLength;
			int kind = run.kind;
			int param = run.param;
			switch (kind) {
				case StyleRun.BOLD:
					StyleConstants.setBold(sas, true);
					break;
				case StyleRun.ITALIC:
					StyleConstants.setItalic(sas, true);
					break;
				case StyleRun.UNDERLINE:
					StyleConstants.setUnderline(sas, true);
					break;
				case StyleRun.STRIKETHROUGH:
					StyleConstants.setStrikeThrough(sas, true);
					break;
				case StyleRun.SUBSCRIPT:
					StyleConstants.setSubscript(sas, true);
					break;
				case StyleRun.SUPERSCRIPT:
					StyleConstants.setSuperscript(sas, true);
					break;
				case StyleRun.ALIGNMENT:
					int align = StyleConstants.ALIGN_LEFT;
					switch (param) {
						case StyleRun.ALIGNMENT_CENTER:
							align = StyleConstants.ALIGN_CENTER;
							break;
						case StyleRun.ALIGNMENT_RIGHT:
							align = StyleConstants.ALIGN_RIGHT;
							break;
						case StyleRun.ALIGNMENT_JUSTIFIED:
							align = StyleConstants.ALIGN_JUSTIFIED;
							break;
					}
					StyleConstants.setAlignment(sas, align);
					break;
				case StyleRun.FAMILY:
					String family = "Serif";
					switch (param) {
						case StyleRun.FAMILY_SANS_SERIF:
							family = "SansSerif";
							break;
						case StyleRun.FAMILY_MONOSPACED:
							family = "Monospaced";
							break;
					}
					StyleConstants.setFontFamily(sas, family);
					break;
				case StyleRun.SIZE:
					int size = 14;
					switch (param) {
						case StyleRun.SIZE_SMALLEST:
							size = 8;
							break;
						case StyleRun.SIZE_SMALLER:
							size = 10;
							break;
						case StyleRun.SIZE_SMALL:
							size = 12;
							break;
						case StyleRun.SIZE_BIG:
							size = 16;
							break;
						case StyleRun.SIZE_BIGGER:
							size = 20;
							break;
						case StyleRun.SIZE_BIGGEST:
							size = 26;
							break;
					}
					StyleConstants.setFontSize(sas, size);
					break;
				case StyleRun.COLOR:
					Color color = Color.black;
					switch (param) {
						case StyleRun.COLOR_RED:
							color = Color.red;
							break;
						case StyleRun.COLOR_BLUE:
							color = Color.blue;
							break;
						case StyleRun.COLOR_GREEN:
							color = Color.green;
							break;
					}
					StyleConstants.setForeground(sas, color);
					break;
				case StyleRun.EMOTICON:
					ImageIcon icon = Emoticons.get(param);
					if (icon != null)
						StyleConstants.setIcon(sas, icon);
					break;
				case StyleRun.STARICON:
					icon = StarIcons.get(param);
					if (icon != null)
						StyleConstants.setIcon(sas, icon);
					break;
			}
			if (kind == StyleRun.ALIGNMENT) {
				if (alignment)
					doc.setParagraphAttributes(offset, length, sas, false);
			} else {
				doc.setCharacterAttributes(offset, length, sas, false);
			}
			if ((kind == StyleRun.EMOTICON) || (kind == StyleRun.STARICON)) {
				// Fix Swing bug with icons.
				try {
					doc.insertString(offset+1, " ", null);
					doc.remove(offset+1, 1);
				} catch (BadLocationException e) {
				}
			}
		}
	}

	/**	Mouse listener.
	 *
	 *	<p>Handles clicks on links.
	 */

	private MouseListener mouseListener =
		new TextFieldPopupMenuMouseListener()
		{
			public void mouseClicked (MouseEvent event) {
				Point pt = event.getPoint();
				int pos = viewToModel(pt);
				if ( pos == -1 ) return;
				for (Iterator it = linkInfoList.iterator(); it.hasNext(); ) {
					LinkInfo linkInfo = (LinkInfo)it.next();
					int offset = linkInfo.begin.getOffset();
					if (offset <= pos && pos < offset + linkInfo.length) {
						linkInfo.link.go();
						break;
					}
				}

				super.mouseClicked( event );
			}
		};

	/** Checks if mouse cursor is positioned over a link.
	 *
	 *	@param	event	The mouse motion event.
	 *
	 *	@return			True if the cursor is positioned over
	 *					a link.
	 */

	private boolean cursorOverLink( MouseEvent event )
	{
		boolean result = false;

		Point pt = event.getPoint();
		int pos = viewToModel(pt);

		if ( pos == -1 ) return result;

		for (Iterator it = linkInfoList.iterator(); it.hasNext(); ) {
			LinkInfo linkInfo = (LinkInfo)it.next();
			int offset = linkInfo.begin.getOffset();
			if (offset <= pos && pos < offset + linkInfo.length) {
				result = true;
				break;
			}
		}

		return result;
	}

	/** Change cursor.
	 *
	 *	@param	cursor		The new cursor.
	 */

	private void changeCursor( Cursor cursor )
	{
		currentCursor = cursor;

		setCursor( cursor );
	}

	/**	Mouse motion listener.
	 *
	 *	<p>Changes mouse cursor to hand when passing over links.</p>
	 */

	private MouseMotionListener mouseMotionListener =
		new MouseMotionAdapter() {
			public void mouseMoved( MouseEvent event )
			{
				if ( cursorOverLink( event ) )
				{
					changeCursor( Cursors.HAND_CURSOR );
				}
				else
				{
					changeCursor( Cursors.DEFAULT_CURSOR );
				}
			}
		};

	/** Prints the styled text.
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
		Thread runner = new Thread("Print text pane")
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
	 *	@return					Printable component for XTextPane.
	 */

	public PrintableComponent getPrintableComponent
	(
		String title,
		PageFormat pageFormat
	)
	{
/*
//								PrintJTextPane doesn 't always work,
//								so use generic printing code for the
//								present.

		return
			new PrintJTextPane
			(
				this ,
				pageFormat ,
				new PrintHeaderFooter
				(
					title ,
					"Printed " + DateTimeUtils.formatDateOnAt( new Date() ) ,
					"Page "
				)
			);
*/
		return
			new PrintableComponent
			(
				this ,
				pageFormat ,
				new PrintHeaderFooter
				(
					title ,
					"Printed " + DateTimeUtils.formatDateOnAt( new Date() ) ,
					"Page "
				)
			);
	}

	/**	Scrolls to the beginning of the document.
	 */

	public void scrollToBeginning () {
		setCaretPosition(0);
		moveCaretPosition(0);
	}

	/**	Scrolls to the end of the document.
	 */

	public void scrollToEnd () {
		int len = getDocLength();
		setCaretPosition(len);
		moveCaretPosition(len);
	}

	/**	Scrolls to the end of the document for sure.
	 *
	 *	<p>The Swing text package is very ornery when it comes to programmatic
	 *	scrolling. This "for sure" method starts a thread that wakes up every
	 *	tenth of a second for two seconds and makes sure that the text pane is
	 *	scrolled to the end.
	 */

	public void scrollToEndForSure () {
		scrollToEnd();
		new Thread(
			new Runnable() {
				public void run () {
					for (int i = 0; i < 20; i++) {
						try {
							Thread.sleep(100);
							SwingUtilities.invokeLater(
								new Runnable() {
									public void run () {
										scrollToEnd();
									}
								}
							);
						} catch (InterruptedException e) {
							break;
						}
					}
				}
			},
			"Scroll text pane to end for sure"
		).start();
	}

	/**	Scrolls to an offset in the document.
	 *
	 *	<p>The offset is positioned at the top of the viewport.
	 *
	 *	<p>This is an incredible hack and doe
	 *
	 *	@param	offset		The offset in the document.
	 *
	 *	@param	scrollPane	The scroll pane.
	 */

	public void scrollTo (final int offset, final JScrollPane scrollPane) {
		new Thread (
			new Runnable () {
				public void run () {
					try {
						for (int i = 0; i < 10; i++) {
							Thread.sleep(100);
							SwingUtilities.invokeLater(
								new Runnable() {
									public void run () {
										try {
											Rectangle r =
												modelToView(offset);
											if (r == null) return;
											Point p = new Point(0, r.y);
											scrollPane.getViewport().
												setViewPosition(p);
										} catch (Exception e) {
										}
									}
								}
							);

						}
					} catch (Exception e) {
					}
				}
			},
			"Scroll text pane to"
		).start();
	}

	/** Get the current cursor.
	 *
	 *	@return		The current cursor.
	 */

	public Cursor getCurrentCursor()
	{
		return currentCursor;
	}

	/** Set honoring of alignment.
	 *
	 *	@param	alignment	True to allow left/center/right alignment.
	 */

	public void setAlignment( boolean alignment )
	{
		this.alignment = alignment;
	}

	/** Set undo manager used by parent.
	 *
	 *	@param	undoManager		The undo manager.
	 */

	public void setUndoManager( UndoManager undoManager )
	{
		this.undoManager = undoManager;
	}

	/** Get undo manager.
	 *
	 *	@return	undoManager		The undo manager, or null if none.
	 */

	public UndoManager getUndoManager()
	{
		return undoManager;
	}

	/**	Is anything selected which can be cut/copied? */

	public boolean isTextSelected()
	{
		String s	= getSelectedText();

		return ( ( s != null ) && ( s.length() > 0 ) );
	}

	/**	Selects all text.
	 */

	public void selectAll()
	{
		if ( isSelectAllEnabled() )
		{
			getCaret().setSelectionVisible( true );
			select( 0 , getDocLength() );
			getCaret().setSelectionVisible( true );
		}
	}

	/**	Checks if "select all" enabled.
	 *
	 *	@return		returns true if select all enabled.
	 */

	public boolean isSelectAllEnabled()
	{
		return ( getDocLength() > 0 );
	}

	/** Unselect selection. */

	public void unselect()
	{
		if ( isUnselectEnabled() )
		{
			select( getCaretPosition() , getCaretPosition() );
		}
	}

	/**	Is unselect enabled? */

	public boolean isUnselectEnabled()
	{
		return isTextSelected();
	}

	/**	Save to a named file.
	 *
	 *	@param	fileName	Name of file to which to save results.
	 */

	public void saveToFile( String fileName )
	{
		try
		{
			FileUtils.writeTextFile
			(
				fileName ,
				false ,
				getText()
			);
		}
		catch ( Exception e )
		{
		}
	}

	/**	Save to a file using a file dialog.
	 *
	 *	@param	parentWindow	Parent window for file dialog.
	 *
	 *	<p>
	 *	Runs a file dialog to get the name of the file to which to
	 *	save results.
	 *	</p>
	 */

	public void saveToFile( Window parentWindow )
	{
		FileExtensionFilter txtFilter	=
			new FileExtensionFilter
			(
				new String[]{ ".txt" },
				"Text Files"
			);

		FileDialogs.addFileFilter( txtFilter );

		FileExtensionFilter pngFilter	=
			new FileExtensionFilter
			(
				new String[]{ ".png" },
				"PNG Files"
			);

		FileDialogs.addFileFilter( pngFilter );

		FileExtensionFilter jpegFilter	=
			new FileExtensionFilter
			(
				new String[]{ ".jpg" , ".jpeg" },
				"JPEG Files"
			);

		FileDialogs.addFileFilter( jpegFilter );

		boolean isHTMLEditorKit	= false;

		if ( getEditorKit() instanceof HTMLEditorKit )
		{
			FileExtensionFilter	htmlFilter	=
				new FileExtensionFilter
				(   new String[]{ ".htm" , ".html" } ,
					"HTML Files"
				);

			FileDialogs.addFileFilter( htmlFilter );

			isHTMLEditorKit	= true;
		}

		String[] saveFile	= FileDialogs.save( parentWindow );

		FileDialogs.clearFileFilters();

		if ( saveFile != null )
		{
			File file	= new File( saveFile[ 0 ] , saveFile[ 1 ] );

			String extension	=
				FileNameUtils.getFileExtension
				(
					saveFile[ 1 ] ,
					false
				).toLowerCase();

								//	Choose output type based upon
								//	extension.

			if	(	isHTMLEditorKit &&
					(	extension.equals( "htm" ) ||
						extension.equals( "html" )
					)
				)
			{
				try
				{
				 	FileUtils.writeTextFile( file , false , getText() );
				}
				catch ( Exception e )
				{
				}
			}
			else if	(	extension.equals( "jpg" ) ||
						extension.equals( "jpeg" ) )
			{
				try
				{
					saveToJPEGFile( file );
				}
				catch ( Exception e )
				{
				}
			}
			else if	(	extension.equals( "png" ) )
			{
				try
				{
					saveToPNGFile( file );
				}
				catch ( Exception e )
				{
				}
			}
			else
			{
				try
				{
					Document doc = getDocument();
					Element rootElement = doc.getDefaultRootElement();
					String s = doc.getText( 0 , doc.getLength() );

				 	FileUtils.writeTextFile( file , false , s );
				}
				catch ( Exception e )
				{
				}
			}
		}
	}

	/**	Save editor contents to image file.
	 *
	 *	@param	imageFile	File to received editor contents as jpeg image.
	 *	@param	imageType	String providing image file type (jpg, png, etc.)
	 *
	 *	@throws	java.io.IOException	if I/O exception occurs.
	 */

	public void saveToImageFile( File imageFile , String imageType )
		throws IOException
	{
		BufferedImage bufferedImage	=
			new BufferedImage
			(
				getWidth() ,
				getHeight() ,
				BufferedImage.TYPE_INT_RGB
			);

		validate();

		Graphics graphics	= bufferedImage.getGraphics();

		printAll( graphics );

		graphics.dispose();

		ImageIO.write( bufferedImage , imageType , imageFile );
	}

	/**	Save editor contents to JPEG file.
	 *
	 *	@param	jpegFile	File to received editor contents as jpeg image.
	 *
	 *	@throws	java.io.IOException	if I/O exception occurs.
	 */

	public void saveToJPEGFile( File jpegFile )
		throws IOException
	{
		saveToImageFile( jpegFile , "jpg" );
	}

	/**	Save editor contents to PNG file.
	 *
	 *	@param	pngFile	File to received editor contents as png image.
	 *
	 *	@throws	java.io.IOException	if I/O exception occurs.
	 */

	public void saveToPNGFile( File pngFile )
		throws IOException
	{
		saveToImageFile( pngFile , "png" );
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

