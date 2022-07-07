package edu.northwestern.at.wordhoard.swing.bibtool;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.awt.datatransfer.*;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.bibtool.*;

import edu.northwestern.at.wordhoard.model.querytool.*;
import edu.northwestern.at.wordhoard.model.userdata.*;

import edu.northwestern.at.wordhoard.swing.querytool.*;
import edu.northwestern.at.wordhoard.swing.work.*;
import edu.northwestern.at.wordhoard.swing.*;

import edu.northwestern.at.wordhoard.swing.calculator.*;
import edu.northwestern.at.wordhoard.swing.concordance.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.db.*;
import edu.northwestern.at.utils.swing.*;

import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXParseException;

/**	A search results window.
 */

public class WorkSetWindow extends AbstractWorkPanelWindow
	implements WindowFocusListener
{
	/**	Persistence manager. */

	private PersistenceManager pm;

	/**	Search results panel. */

	private WorkSetPanel panel;

	/**	Creates a new search results window and fills it with query results
     *
	 *	@param	parentWindow		Parent window.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public WorkSetWindow (AbstractWindow parentWindow)
			throws PersistenceException
	{
		super
		(
			WordHoardSettings.getString
			(
				"WorkSetEditor" ,
				"Work Set Editor"
			) ,
			parentWindow
		);

		pm = new PersistenceManager();

		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setAlignmentX(Component.LEFT_ALIGNMENT);

		panel = new WorkSetPanel(pm, this);

		enableSelectAllCmd(false);
		Font font = new JLabel().getFont();
		font = new Font(font.getName(), font.getStyle(), 9);
		p.add(panel);

		setContentPane(panel);

		pack();
		Dimension windowSize = getSize();
		Dimension screenSize = getToolkit().getScreenSize();
		windowSize.height = (screenSize.height/3) -
			(WordHoardSettings.getTopSlop() +
			WordHoardSettings.getBotSlop());
		setSize(windowSize);
		setLocation(new Point(3, WordHoardSettings.getTopSlop()));
		positionNextTo(getParentWindow());

		addWindowFocusListener(this);
		setVisible( true );
	}

	/**	Handles "Save Work Set" command.
	 */

	public void handleSaveWorkSetCmd()
	{
		try
		{
			panel.saveWorkSet();
		}
		catch ( DuplicateWorkSetException e )
		{
			new ErrorMessage
			(
				WordHoardSettings.getString
				(
					"duplicateworkset" ,
					"A work set of that name already exists."
				)
			);
		}
		catch ( Exception e )
		{
			Err.err( e );
		}
	}

	/**	Open from file.
	 *
	 */

	 public void open() {
		String[] fileToOpen	= FileDialogs.open( this );

		if ( fileToOpen != null ) {
			File file = new File( fileToOpen[ 0 ] , fileToOpen[ 1 ] );
			try {
				XMLReader xr = XMLReaderFactory.createXMLReader(
					"org.apache.xerces.parsers.SAXParser");
				WorkBagHandler handler = new WorkBagHandler();
				xr.setContentHandler(handler);
				xr.setErrorHandler(handler);
				InputStream mi = new FileInputStream(file);
		    	xr.parse(new InputSource(new InputStreamReader(mi)));
				mi.close();
				setTitle(file.getName());
			} catch (Exception e) {Err.err(e);}
		} else {
//				System.out.println("Open command cancelled by user.");
		}
	}

	 protected void open(File file) {
			try {

				XMLReader xr = XMLReaderFactory.createXMLReader(
					"org.apache.xerces.parsers.SAXParser");
				WorkBagHandler handler = new WorkBagHandler();
				xr.setContentHandler(handler);
				xr.setErrorHandler(handler);
				InputStream mi = new FileInputStream(file);
		    	xr.parse(new InputSource(new InputStreamReader(mi)));
				mi.close();
				setTitle(file.getName());
			} catch (SAXParseException e) {setTitle(""); // ignore bad file
			} catch (Exception e) {Err.err(e);}
	}


	public void windowGainedFocus(WindowEvent e) {
		saveWorkSetCmd.setEnabled( WordHoardSettings.isLoggedIn() );
	}

	public void windowLostFocus(WindowEvent e) {
		saveWorkSetCmd.setEnabled(false);
	}

	public void loadWorkSet() throws Exception {
		panel.loadWorkSet();
	}

	/**	Handles file menu selected.
	 *
	 *
	 *	@throws	Exception	general error.
	 */

	public void handleFileMenuSelected ()
		throws Exception
	{
		newWorkSetCmd.setEnabled( true );
		openWorkSetCmd.setEnabled( true );
		saveWorkSetCmd.setEnabled( WordHoardSettings.isLoggedIn() );
		saveWordSetCmd.setEnabled( false );
	}

	/**	Adjusts menu items to reflect logged-in status.
	 *
	 *	<p>
	 *	Enables/disables the "Save Work Set" command in this window.
	 *	</p>
	 */

	public void adjustAccountCommands()
	{
		super.adjustAccountCommands();

		saveWorkSetCmd.setEnabled( WordHoardSettings.isLoggedIn() );
	}

	public class WorkBagHandler extends DefaultHandler
	{
		java.util.ArrayList items	= new java.util.ArrayList();

		public void startElement(
			String uri, String name, String qName, Attributes atts )
		{
			if ( name.equals( "work" ) )
			{
				try
				{
					String id	= atts.getValue( "id" );

					items.add( pm.load( Work.class , Long.valueOf( id ) ) );

				}
				catch ( Exception e )
				{
					Err.err(e);
				}
			}
		}

		java.util.List getItems()
		{
			return items;
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

