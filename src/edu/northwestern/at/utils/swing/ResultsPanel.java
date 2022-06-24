package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.awt.*;

import edu.northwestern.at.utils.swing.printing.*;

/**	A results panel.
 *
 *	<p>
 *	A results panel is a dialog panel that knows how to print
 *	and save its contents to a file.
 *	</p>
 */

public class ResultsPanel
	extends DialogPanel
	implements PrintableContainer , SaveToFile
{
	/**	Object holding actual results. */

	protected Object results			= null;

	/**	Results header describing results. */

	protected String resultsHeader		= "";

	/**	Results title. */

	protected String resultsTitle		= "";

   	/**	Constructs a new results panel.
	 */

	public ResultsPanel()
	{
		super();
	}

	/**	Page setup.
	 */

	public void doPageSetup()
	{
		PrinterSettings.doPageSetup();
	}

	/**	Print the results.
	 */

	public void doPrint()
	{
		Thread runner = new Thread( "Printing" )
		{
			public void run()
			{
				PrintUtilities.printComponent
				(
					(Component)results ,
					resultsTitle ,
					null ,
					null ,
					true
				);
			}
		};

		runner.start();
	}

	/**	Print preview of results.
	 */

	public void doPrintPreview()
	{
		Thread runner = new Thread( "Print Preview" )
		{
			public void run()
			{
				PrintUtilities.printPreview
				(
					(Component)results ,
					resultsTitle
				);
			}
		};

		runner.start();
	}

	/**	Get component holding actual results.
	 *
	 *	@return		The results component.
	 */

	public Object getResults()
	{
		return results;
	}

	/**	Get results title.
	 *
	 *	@return		The results header.
	 */

	public String getResultsHeader()
	{
		return resultsHeader;
	}

	/**	Get short results title.
	 *
	 *	@return		The results title.
	 */

	public String getResultsTitle()
	{
		return resultsTitle;
	}

	/**	Set object holding actual results.
	 *
	 *	@param	results		The results object.
	 */

	public void setResults( Object results )
	{
		this.results	= results;
	}

	/**	Set results header.
	 *
	 *	@param	resultsHeader	The results header.
	 */

	public void setResultsHeader( String resultsHeader )
	{
		this.resultsHeader	= resultsHeader;
	}

	/**	Set results title.
	 *
	 *	@param	resultsTitle	The results title.
	 */

	public void setResultsTitle( String resultsTitle )
	{
		this.resultsTitle	= resultsTitle;
	}

	/**	Save results to a file.
	 *
	 *	@param	fileName	Name of file to save results to.
	 *
	 *	<p>
	 *	Can save any results where the results component implements
	 *	the SaveToFile interface.  Override this if you need other types
	 *	of saves.
	 *	</p>
	 */

	public void saveToFile( String fileName )
	{
		if ( results instanceof SaveToFile )
		{
			((SaveToFile)results).saveToFile( fileName );
		}
	}

	/**	Save results to a file.
	 *
	 *	@param	parentWindow	Parent window for file dialog.
	 *
	 *	<p>
	 *	Runs a file dialog to get the name of the file to which to
	 *	save the results.
	 *	</p>
	 *
	 *	<p>
	 *	Can save any results where the results component implements
	 *	the SaveToFile interface.  Override this if you need other types
	 *	of saves.
	 *	</p>
	 */

	public void saveToFile( Window parentWindow )
	{
		if ( results instanceof SaveToFile )
		{
			((SaveToFile)results).saveToFile( parentWindow );
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

