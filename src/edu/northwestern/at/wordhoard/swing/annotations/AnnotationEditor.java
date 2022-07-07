package edu.northwestern.at.wordhoard.swing.annotations;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.text.*;
import javax.swing.border.*;
import javax.swing.*;

import edu.northwestern.at.utils.db.*;
import edu.northwestern.at.utils.swing.*;

import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.text.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.annotations.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;

import edu.northwestern.at.wordhoard.model.userdata.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;

/**	Simple text editor for annotations.
 */

public class AnnotationEditor
	extends ModalDialog
	implements CutCopyPaste, SelectAll
{
	/**	The annotation. */

	protected AuthoredTextAnnotation remoteAnnotation;


	/**	True if user hits cancel button. */

	protected boolean canceled	= false;

	/**	Default title for new file. */

	protected String newTitle	= "Annotation Editor";

	/**	Current title for editor. */

	protected String title		= "Annotation Editor";

	/**	Tab size in columns.
	 */

	public static final int tabSize	= 4;

	/**	True if readonly.
	 */

	protected final boolean readOnly	= false;

	/**	Content pane for editor. */

	protected JPanel contentPanel	= null;

	/**	The font.
	 */

	public static final Font font	=
		new Font( "monospaced" , Font.PLAIN , 12 );

	/**	Width of a single character in the font.
	 */

	public static int fontCharWidth;

	/**	The text area holding the text being edited.
	 */

	public XTextArea editor;


	protected HashMap perms = new HashMap();
	protected	SmallComboBox groupComboBox;
	protected	SmallComboBox accessComboBox;

	protected static class PermissionItem {
		UserGroup group;
		String permission;
		boolean dirty = false;
	}

	/**	Create annotation editor.
	 *
	 *	@param	remoteAnnotation	The annotation to edit.
	 *	@throws	PersistenceException	error in persistence layer.
	 */


//	public AnnotationEditor( RemoteAnnotation remoteAnnotation )
	public AnnotationEditor( AuthoredTextAnnotation remoteAnnotation )
		throws PersistenceException
	{
		super( null );

		this.remoteAnnotation	= remoteAnnotation;

		contentPanel	= new JPanel( true );

		contentPanel.setBorder( BorderFactory.createEtchedBorder() );
		contentPanel.setLayout( new BorderLayout() );

								//	Create the embedded JTextComponent.

		editor = createEditor();

		String body = convertTextToString(remoteAnnotation.getText());
		if(body!=null && !body.equals("")) {
			try {
				editor.getDocument().insertString(0, body,null);
			} catch (Exception e) {
				Err.err(e);
			}
		}
								//	Wrap editor with a scroll bar pane.

		JScrollPane scroller	=
			new JScrollPane
			(
				editor ,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
			);
								//	Lay out editor window components.

		JPanel aePanel	= new JPanel();
		aePanel.setLayout(new BorderLayout());

		JPanel ePanel	= new JPanel();
		ePanel.setLayout( new BorderLayout() );

		FontManager fontManager = new FontManager();
		Font latinFont = fontManager.getFont(12);

		JPanel controlPanel = createControlPanel(latinFont);
		contentPanel.add( "North" , controlPanel );
		contentPanel.add( "Center" , scroller );
	
		JPanel groupPanel = createGroupPanel(latinFont, remoteAnnotation);
		aePanel.add("Center", contentPanel);
		aePanel.add("South", groupPanel);
		
		Dimension size = new Dimension(400, 400);
		contentPanel.setPreferredSize(size);
		contentPanel.setSize(size);

		getContentPane().add( aePanel );

		setTitle( title );

		setVisible(true);

	}

	/**	Return true if editing canceled.
	 * @return	True or false.
	*/

	public boolean isCanceled()
	{
		return canceled;
	}

	/**	Create an XTextArea to hold the document text.
	 * @return	The created text area.
	 */

	protected XTextArea createEditor()
	{
								//	Create editor.

		XTextArea c = new XTextArea();

								//	Set default font.

		c.setFont( font );

								//	Get width for a single character.

		fontCharWidth	= c.getFontMetrics( font ).charWidth( 'w' );

								//	Enable tabs.

		c.setTabsEnabled( true );

								//	Set tabs every four characters.

		c.setTabSize( tabSize );

								//	Set text to be read-only if requested.

		c.setEditable( !readOnly );

								//	Allow text dragging.

		c.setDragEnabled( true );

								//	Disable word wrap.

		c.setLineWrap( true );
	//	c.setLineWrap( false );

		return c;
	}

	/**	Return the editor contained in this panel.
	 *	@return	The editor.
	 */

	protected JTextArea getEditor()
	{
		return editor;
	}

	/**	Return the editor text as a string.
	 *	@return	The editor text.
	 */

	protected String getEditorText()
	{
		return editor.getText();
	}

	/**	Create the group panel.
	 *
	 *	@param	theFont	The font.
	 *	@param	annotation	The annotation.
	 *	@return		The group panel.
	 *
	 */

	protected JPanel createGroupPanel(Font theFont, AuthoredTextAnnotation annotation) {
		Collection ugs = UserGroupUtils.getUserGroups(WordHoardSettings.getUserID());
		Map persistentPerms = UserGroupPermissionUtils.getPermissionsForItem(annotation);

		// init temporary permission state
		Iterator ugs_iter = ugs.iterator();
		while (ugs_iter.hasNext()) {
                UserGroup ug = (UserGroup)ugs_iter.next();
				if(persistentPerms.containsKey(ug)) {
					PermissionItem item = new PermissionItem();
					item.group = ug;
					item.permission =((UserGroupPermission)persistentPerms.get(ug)).getPermission();
					item.dirty=false;
					perms.put(ug,item);
				}
		}
		
		groupComboBox = new SmallComboBox(font);
		Iterator iter = ugs.iterator();
		while (iter.hasNext()) {
                UserGroup ug = (UserGroup)iter.next();
				groupComboBox.addItem(ug);
		}

		accessComboBox = new SmallComboBox(font);
		accessComboBox.addItem("Read");
		accessComboBox.addItem("None");
		
		UserGroup sug = (UserGroup)groupComboBox.getSelectedItem();
		if(perms.containsKey(sug)) accessComboBox.setSelectedItem(((PermissionItem)perms.get(sug)).permission);
		else accessComboBox.setSelectedItem("None");

		groupComboBox.addActionListener(
				new ActionListener () {
					public void actionPerformed (ActionEvent event) {
						UserGroup sug = (UserGroup)groupComboBox.getSelectedItem();
						if(perms.containsKey(sug)) accessComboBox.setSelectedItem(((PermissionItem)perms.get(sug)).permission);
						else accessComboBox.setSelectedItem("None");
					}
				}
		);

		accessComboBox.addActionListener(
				new ActionListener () {
					public void actionPerformed (ActionEvent event) {
						UserGroup sug = (UserGroup)groupComboBox.getSelectedItem();
						String perm = (String)accessComboBox.getSelectedItem();

						PermissionItem pi;
						
						if(perms.containsKey(sug)) { pi = (PermissionItem)perms.get(sug); }
						else { 
							pi = new PermissionItem(); 
							pi.group = sug;
							perms.put(sug,pi);
						}

						pi.permission=perm;
						pi.dirty=true;
					}
				}
		);
		
		JPanel gPanel = new JPanel();

		TitledBorder tb = new TitledBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED), "Access Control", TitledBorder.CENTER, TitledBorder.BELOW_TOP);

		JLabel groupLabel = WordHoard.getSmallComboBoxLabel("Group: ", theFont);
		gPanel.setLayout(new BorderLayout());
		gPanel.setBorder(tb);


		JPanel grPanel = new JPanel();
		grPanel.setLayout(new BoxLayout(grPanel, BoxLayout.X_AXIS));
		grPanel.add(groupLabel);
		grPanel.add(groupComboBox);

		gPanel.add("West", grPanel);
		
		JPanel pPanel = new JPanel();
		pPanel.setLayout(new BoxLayout(pPanel, BoxLayout.X_AXIS));
		JLabel permsLabel = WordHoard.getSmallComboBoxLabel("Access: ", theFont);
		pPanel.add(permsLabel);
		pPanel.add(accessComboBox);

		gPanel.add("East", pPanel);

		return gPanel;
	}
	
	/**	Create the control panel.
	 *
	 *	@param	theFont	The font.
	 *	@return		The control panel.
	 *
	 */

	protected JPanel createControlPanel(Font theFont )
	{
		JButton b = null;

		WorkPart wp = remoteAnnotation.getWorkPart();
		Work w = wp.getWork();
		TextRange tr = remoteAnnotation.getTarget();

		String workString = w.toString() + ", " + wp.toString();
		JLabel workLabel = new JLabel(workString);
		workLabel.setFont(theFont);
		workLabel.setBorder(BorderFactory.createEmptyBorder(3,5,0,0));
		workLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

		if(remoteAnnotation.getId()==null) {
			addButton("Attach",
				new ActionListener () {
					public void actionPerformed (ActionEvent event) {
						// attach();
						createAnnotation();
						canceled=false;
						dispose();
					}
				}
			);
		} else {
			addButton("Save",
				new ActionListener () {
					public void actionPerformed (ActionEvent event) {
					//	save();
						updateAnnotation();
						canceled=false;
						dispose();
					}
				}
			);
		}

		addDefaultButton("Cancel",
			new ActionListener() {
				public void actionPerformed (ActionEvent event) {
					canceled = true;
					dispose();
				}
			}
		);

		JPanel cPanel = new JPanel();
		cPanel.setLayout(new BoxLayout(cPanel, BoxLayout.Y_AXIS));
		cPanel.add(Box.createVerticalStrut(3));
		cPanel.add(workLabel);

		cPanel.add(Box.createVerticalStrut(3));
		return cPanel;
	}

	//	(Defunct)
/*
	private void attach() {
		WorkPart wp = remoteAnnotation.getWorkPart();
		Work w = wp.getWork();
		TextRange tr = remoteAnnotation.getTarget();

		try {
			String urlstring = WordHoardSettings.getAnnotationWriteServerURL();
			if(urlstring==null) return;

			Document d = editor.getDocument();
			String body = d.getText(0,d.getLength());

			int startline = tr.getStart().getIndex();
			int startoffset = tr.getStart().getOffset();
			int endline = tr.getEnd().getIndex();
			int endoffset = tr.getEnd().getOffset();

			String context = "<start><line>" + startline + "</line><offset>" + startoffset + "</offset></start><end><line>" + endline + "</line><offset>" + endoffset + "</offset></end>";

			String author = null;
			if(WordHoardSettings.getUserID()==null) {
				author="anonymous";
			} else {
				author = (WordHoardSettings.getName()!=null) ? WordHoardSettings.getName() : WordHoardSettings.getUserID();
			}

			ClientHttpRequest cr = new ClientHttpRequest(urlstring);
			cr.setParameter("type", "user");
			cr.setParameter("status", "ready");
			cr.setParameter("author", author);
			cr.setParameter("annotates", wp.getTag());
			cr.setParameter("context", context);
			cr.setParameter("userid", WordHoardSettings.getUserID());
			cr.setParameter("body", body);
			InputStream in = cr.post();
			InputStreamReader inR = new InputStreamReader( in );
			BufferedReader buf = new BufferedReader( inR );
			String line;
			while ( ( line = buf.readLine() ) != null ) {
				System.out.println( line );
			}
			in.close();
		} catch(Exception ex) {
			System.out.println(getClass().getName() + " Exception during attach:" + ex.getMessage());
		}
	}
*/

	private void createAnnotation() {
		try {

			Document d = editor.getDocument();
			String body = d.getText(0,d.getLength());
			remoteAnnotation.resetText(body);

			String author = null;
			if(WordHoardSettings.getUserID()==null) {
				author="anonymous";
			} else {
				author = (WordHoardSettings.getName()!=null) ? WordHoardSettings.getName() : WordHoardSettings.getUserID();
			}

			WorkPart wp = remoteAnnotation.getWorkPart();

			remoteAnnotation.setAuthor(author);
			remoteAnnotation.setType("user");
			remoteAnnotation.setStatus("ready");
			remoteAnnotation.setAnnotates( wp.getTag());
			remoteAnnotation.setType("user");
			remoteAnnotation.setAuthor(author);
			remoteAnnotation.setOwner(WordHoardSettings.getUserID());
			remoteAnnotation = AnnotationUtils.createAnnotation(remoteAnnotation);

			// record permission settings 
			Iterator iter = perms.values().iterator();
			while (iter.hasNext()) {
					PermissionItem pi = (PermissionItem)iter.next();
					if(pi.dirty) {
						UserGroupPermissionUtils.setPermission(remoteAnnotation, pi.group,pi.permission);
						pi.dirty=false;
					}
			}
			
		} catch(Exception ex) {
			System.out.println(getClass().getName() + " Exception during createAnnotation:" + ex.getMessage());
			ex.printStackTrace();
		}
	}

	private void updateAnnotation() {
		try {

			Document d = editor.getDocument();
			String body = d.getText(0,d.getLength());
			remoteAnnotation.resetText(body);
			AnnotationUtils.updateAnnotation(remoteAnnotation, body);

			// record permission settings 
			Iterator iter = perms.values().iterator();
			while (iter.hasNext()) {
					PermissionItem pi = (PermissionItem)iter.next();
					if(pi.dirty) {
						UserGroupPermissionUtils.setPermission(remoteAnnotation, pi.group,pi.permission);
						pi.dirty=false;
					}
			}

		} catch(Exception ex) {
			System.out.println(getClass().getName() + " Exception during updateAnnotation:" + ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	// (Defunct)
/*
	private void save() {
		WorkPart wp = remoteAnnotation.getWorkPart();
		Work w = wp.getWork();
		TextRange tr = remoteAnnotation.getTarget();
		try {
			//String urlstring = WordHoardSettings.getAnnotationWriteServerURL();
			//if(urlstring==null) return;

			Document d = editor.getDocument();
			String body = d.getText(0,d.getLength());
			remoteAnnotation.resetText(body);
//			remoteAnnotation.setText(convertStringToText(body));

			int startline = tr.getStart().getIndex();
			int startoffset = tr.getStart().getOffset();
			int endline = tr.getEnd().getIndex();
			int endoffset = tr.getEnd().getOffset();

			String context = "<start><line>" + startline + "</line><offset>" + startoffset + "</offset></start><end><line>" + endline + "</line><offset>" + endoffset + "</offset></end>";

			ClientHttpRequest cr = new ClientHttpRequest(urlstring);
			cr.setParameter("id", remoteAnnotation.getId().toString());
			cr.setParameter("type", "user");
			cr.setParameter("status", "ready");
			cr.setParameter("annotates", wp.getTag());
			cr.setParameter("context", context);
			cr.setParameter("body", body);
			InputStream in = cr.post();
			InputStreamReader inR = new InputStreamReader( in );
			BufferedReader buf = new BufferedReader( inR );
			String line;
			while ( ( line = buf.readLine() ) != null ) {
				System.out.println( line );
			}
			in.close();
		} catch(Exception ex) {
			System.out.println(getClass().getName() + " Exception during save:" + ex.getMessage());
		}
	}
*/

	/**	Performs new action.
	 * @param	e	The event that triggered the action.
	 */

	protected void doNew( ActionEvent e )
	{
		WorkPart wp = remoteAnnotation.getWorkPart();
		Work w = wp.getWork();
		TextRange tr = remoteAnnotation.getTarget();

		System.out.println(w.toString() + " " + wp.toString() + " - " + tr.toString());
		try {
			Document d = editor.getDocument();
			System.out.println(d.getText(0,d.getLength()));
		} catch(Exception ex){}
	}

	/**	Cut to clipboard. */

	public void cut()
	{
		editor.cut();
	}

	/**	Copy to clipboard. */

	public void copy()
	{
		editor.copy();
	}

	/**	Paste from clipboard. */

	public void paste()
	{
		editor.paste();
	}

	/**	Is cut enabled? */

	public boolean isCutEnabled()
	{
		return !readOnly;
	}

	/**	Is copy enabled? */

	public boolean isCopyEnabled()
	{
		return true;
	}

	/**	Is paste enabled? */

	public boolean isPasteEnabled()
	{
		return !readOnly;
	}

	/**	Is anything selected which can be cut/copied? */

	public boolean isTextSelected()
	{
		String s	= editor.getSelectedText();

		return ( ( s != null ) && ( s.length() > 0 ) );
	}

	/** Unselect selection. */

	public void unselect()
	{
		if ( isUnselectEnabled() )
		{
			editor.select(
				editor.getCaretPosition() , editor.getCaretPosition() );
		}
	}

	/**	Is unselect enabled? */

	public boolean isUnselectEnabled()
	{
		return isTextSelected();
	}

	/**	Selects all text.
	 */

	public void selectAll()
	{
		if ( isSelectAllEnabled() )
		{
			editor.getCaret().setSelectionVisible( true );
			editor.select( 0 , editor.getDocument().getLength() );
			editor.getCaret().setSelectionVisible( true );
		}
	}

	/**	Checks if "select all" enabled.
	 *
	 *	@return		returns true if select all enabled.
	 */

	public boolean isSelectAllEnabled()
	{
		return true;
	}
	
	/**	Converts Text to a String.
	 *
	 *	@param	text	The wrapped text object.
	 *	@return		The text as a string.
	 *
	 */
	 
	public String convertTextToString (TextWrapped text) {
		String val = null;
		if(text!=null) {
			Text t = text.getText();
			TextLine[] lines = t.getLines();
			
			if(lines.length >0) {
				int endline = lines.length - 1;
				TextLine lastline = lines[endline];
				if(lastline!= null) {
					int endOffset = lastline.getLength();
					TextRange tr = new TextRange(new TextLocation(0,0), new TextLocation(endline,endOffset));
					val = t.getText(tr);
				} 
			}
		}
		return val;
	}

	/**	Reset the Text from a String.
	 *
	 *	@param	textString		The string.
	 *
	 */
	 
/*	public TextWrapped convertStringToText (String textString) {

		TextLine line = new TextLine();
		Context context = new Context();
		Text text = new Text();
		line = genText(textString,text,line,context);
		text.copyLine(line);
		text.finalize();
		MemoryTextWrapper textWrapper = new MemoryTextWrapper(text);
		return(textWrapper);
	}
*/
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

