package edu.northwestern.at.wordhoard.swing.calculator.menus;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.net.URL;
import java.io.*;
import java.lang.reflect.*;

import javax.help.*;

import javax.swing.*;
import javax.swing.event.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.sys.*;

import edu.northwestern.at.wordhoard.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.userdata.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.calculator.*;
import edu.northwestern.at.wordhoard.swing.calculator.analysis.*;
import edu.northwestern.at.wordhoard.swing.calculator.cql.*;
import edu.northwestern.at.wordhoard.swing.calculator.dialogs.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;
import edu.northwestern.at.wordhoard.swing.calculator.widgets.*;
import edu.northwestern.at.wordhoard.swing.dialogs.*;

import org.krysalis.jcharts.*;
import org.krysalis.jcharts.axisChart.*;

/**	WordHoard Calculator Analysis Menu.
 */

public class AnalysisMenu extends BaseMenu
{
	/**	The analysis menu items. */

	/**	Create a word list. */

	protected JMenuItem createWordListMenuItem;

	/** Get collocates. */

	protected JMenuItem createCollocationsMenuItem;

	/**	Find multiword units. */

	protected JMenuItem findMultiwordUnitsItem;

	/**	Track word usage over time */

	protected JMenuItem trackWordOverTimeMenuItem;

	/**	Compare counts for single word. */

	protected JMenuItem compareSingleWordMenuItem;

	/**	Compare counts for multiple words. */

	protected JMenuItem compareManyWordsMenuItem;

	/**	Compare counts for collocates. */

	protected JMenuItem compareCollocationsItem;

	/**	Compare texts. */

	protected JMenuItem compareTextsItem;

	/**	Create analysis menu.
	 */

	public AnalysisMenu()
	{
		super
		(
			WordHoardSettings.getString
			(
				"analysisMenuName" ,
				"Analysis"
			)
		);
	}

	/**	Create analysis menu.
	 *
	 *	@param	menuBar		The menu bar to which to attach the analysis menu.
	 */

	public AnalysisMenu( JMenuBar menuBar )
	{
		super
		(
			WordHoardSettings.getString( "analysisMenuName" , "Analysis" ) ,
			menuBar
		);
	}

	/**	Create analysis menu.
	 *
 	 *	@param	parentWindow	The parent window of this menu.
	 */

	public AnalysisMenu( AbstractWindow parentWindow )
	{
		super
		(
			WordHoardSettings.getString
			(
				"analysisMenuName" ,
				"Analysis"
			) ,
			null ,
			parentWindow
		);
	}

	/**	Create analysis menu.
	 *
	 *	@param	menuBar			The menu bar to which to attach the menu.
 	 *	@param	parentWindow	The parent window of this menu.
	 */

	public AnalysisMenu( JMenuBar menuBar , AbstractWindow parentWindow )
	{
		super
		(
			WordHoardSettings.getString
			(
				"analysisMenuName" ,
				"Analysis"
			) ,
			menuBar ,
			parentWindow
		);
	}

	/**	Create the menu items.
	 */

	protected void createMenuItems()
	{
								//	Word frequencies

		createWordListMenuItem	=
			addMenuItem
			(
				WordHoardSettings.getString
				(
					"analysisMenuCreatewordlistItem" ,
					"Create word list..."
				) ,
				new GenericActionListener( "createWordList" )
			);

		createWordListMenuItem.setEnabled( true );

								//	Create collocations

		createCollocationsMenuItem	=
			addMenuItem(
				WordHoardSettings.getString(
					"analysisMenuCreatecollocationsItem" ,
					"Find collocates..." ) ,
				new GenericActionListener( "findCollocates" ) );

		createCollocationsMenuItem.setEnabled( true );

		findMultiwordUnitsItem	=
			addMenuItem(
				WordHoardSettings.getString(
					"analysisMenuFindmultiwordunitsItem" ,
					"Find multiword units..." ) ,
				new GenericActionListener( "findMultiwordUnits" ) );

        findMultiwordUnitsItem.setEnabled( true );

								//	Track a word over time

		trackWordOverTimeMenuItem	=
			addMenuItem(
				WordHoardSettings.getString(
					"analysisMenuTrackwordovertimeItem" ,
					"Track word over time..." ) ,
				new GenericActionListener( "trackWordOverTime" ) );

		trackWordOverTimeMenuItem.setEnabled( true );
//
		addSeparator();
//
								//	Single word frequency profile

		compareSingleWordMenuItem	=
			addMenuItem(
				WordHoardSettings.getString(
					"analysisMenuComparesinglewordItem" ,
					"Compare single word..." ) ,
				new GenericActionListener( "compareSingleWord" ) );

		compareSingleWordMenuItem.setEnabled( true );

								//	Multiple word frequency profile

		compareManyWordsMenuItem	=
			addMenuItem(
				WordHoardSettings.getString(
					"analysisMenuComparemanywordsItem" ,
					"Compare many words..." ) ,
				new GenericActionListener( "compareManyWords" ) );

		compareManyWordsMenuItem.setEnabled( true );

								//	Single word collocation comparison.

		compareCollocationsItem	=
			addMenuItem(
				WordHoardSettings.getString(
					"analysisMenuComparecollocationsItem" ,
					"Compare collocates..." ) ,
				new GenericActionListener( "compareCollocations" ) );

		compareCollocationsItem.setEnabled( true );

								//	Compare texts.

		compareTextsItem		=
			addMenuItem(
				WordHoardSettings.getString(
					"analysisMenuComparetextsItem" ,
					"Compare texts..." ) ,
				new GenericActionListener( "compareTexts" ) );

		compareTextsItem.setEnabled( true );

		if ( menuBar != null ) menuBar.add( this );
	}

	/**	Run frequency analysis.
	 *
	 *	@param	analysis	Frequency analysis object.
	 */

	protected void doRunFrequencyAnalysis( final AnalysisRunner analysis )
	{
								//	Note if calculator window
								//	is open already.

		final boolean isCalculatorWindowVisible	=
			getCalculatorWindow().isVisible();

								//	If the dialog was not cancelled,
								//	run the analysis.

		if ( analysis.showDialog( parentWindow ) )
		{
			try
			{
				SwingUtilities.invokeAndWait
				(
					new Runnable()
					{
						public void run()
						{
							if ( !getCalculatorWindow().isVisible() )
							{
								getCalculatorWindow().setVisible( true );
							}
						}
					}
				);
			}
			catch ( Exception e )
			{
				Err.err( e );
			}

			OutputResults outputResults	= createProgressPanel( "" , "" );

			final DialogPanel panel		= outputResults.getOutputPanel();
			final JButton closeButton	= outputResults.getCloseButton();

								//	Enable the busy cursor.
            setBusyCursor();
								//	Run the analysis.

			analysis.runAnalysis
			(
				parentWindow ,
				outputResults.getProgressReporter()
			);
								//	Retrieve the analysis results.

			final ResultsPanel resultsPanel	= analysis.getResults();

								//	Close the persistence manager.

			getCalculatorWindow().closePersistenceManager();

								//	Update the display on the AWT event
								//	thread or bad things will happen.

			Runnable runnable	=
				new Runnable()
				{
					public void run()
					{
								//	Change the cancel button to a close button.

						closeButton.setVisible( false );

								//	Get tabbed pane.

						WordHoardTabbedPane mainTabbedPane	=
							getMainTabbedPane();

								//	If the analysis pane is null,
								//	the analysis was not performed.
								//	Otherwise, create a new tabbed pane
								//	to hold the results.

						if ( resultsPanel != null )
						{
							resultsPanel.validate();

							panel.getBody().removeAll();
							panel.add( resultsPanel );
							panel.revalidate();
							panel.paintImmediately(
								panel.getVisibleRect() );

								//	Add result options if available.

							if ( analysis.areResultOptionsAvailable() )
							{
								addResultOptions( panel , analysis );
							}
								//	Add filter button if analysis output
								//	can be filtered.

							if ( analysis.isFilterAvailable() )
							{
								addAFilterButton( panel , analysis );
							}
								//	Add chart button if analysis can
								//	produce a chart.

							if ( analysis.isChartAvailable() )
							{
								addAChartButton( panel , analysis );
							}
								//	Add cloud button if analysis can
								//	produce a cloud.

							if ( analysis.isCloudAvailable() )
							{
								addACloudButton( panel , analysis );
							}
								//	Add context button if analysis can
								//	produce a context for selected results.

							if ( analysis.isContextAvailable() )
							{
								addAContextButton( panel , analysis );
							}
								//	Enable/disable cut/copy/paste as
								//	needed.

							if	(	resultsPanel.getResults() instanceof
									CutCopyPaste )
							{
								CutCopyPaste copyable	=
									(CutCopyPaste)resultsPanel.getResults();

								getCalculatorWindow().getEditMenu().
									setCutCopyPaste( copyable );
							}

							mainTabbedPane.undock( isCalculatorWindowVisible );
						}
						else
						{
							mainTabbedPane.remove( panel );
						}
								//	Make sure the main pane gets redrawn
								//	to remove any artifacts left over from
								//	the analysis.

						mainTabbedPane.paintImmediately(
							mainTabbedPane.getVisibleRect() );

						setDefaultCursor();

								//	Close calculator window if it
								//	was not already open when
								//	analysis started.

						if ( !isCalculatorWindowVisible )
						{
							getCalculatorWindow().setVisible( false );
						}
					}
				};

			SwingUtilities.invokeLater( runnable );
		}
	}

	/** Add a chart button to a panel.
	 */

	protected JButton addAChartButton
	(
		DialogPanel panel ,
		final AnalysisRunner finalAnalysis
	)
	{
		return panel.addButton
		(
			WordHoardSettings.getString( "Chart" , "Chart" ),
			new ActionListener()
			{
				public void actionPerformed( ActionEvent e )
				{
					SwingUtilities.invokeLater
					(
						new Runnable()
						{
							public void run()
							{
        						ResultsPanel chartPanel	=
        							finalAnalysis.getChart();

								final String chartTitle	=
									getNextOutputWindowTitle( false );

								WordHoardTabbedPane mainTabbedPane	=
									getMainTabbedPane();

								mainTabbedPane.add
								(
									chartTitle ,
									chartPanel
								);

								mainTabbedPane.setSelectedIndex(
									mainTabbedPane.indexOfTab(
										chartTitle ) );

								mainTabbedPane.undock();
							}
						}
					);
				}
			}
		);
	}

	/** Add result options to a panel.
	 */

	protected void addResultOptions
	(
		final DialogPanel panel ,
		final AnalysisRunner finalAnalysis
	)
	{
		LabeledColumn resultOptions	= finalAnalysis.getResultOptions();

		if ( resultOptions != null )
		{
			panel.add( resultOptions );
		}
	}

	/** Add a cloud button to a panel.
	 */

	protected JButton addACloudButton
	(
		final DialogPanel panel ,
		final AnalysisRunner finalAnalysis
	)
	{
		return panel.addButton
		(
			WordHoardSettings.getString( "Cloud" , "Cloud" ),
			new ActionListener()
			{
				public void actionPerformed( ActionEvent e )
				{
					SwingUtilities.invokeLater
					(
						new Runnable()
						{
							public void run()
							{
								ResultsPanel cloudPanel	=
        							finalAnalysis.getCloud();

								final String cloudTitle	=
									getNextOutputWindowTitle( false );

								WordHoardTabbedPane mainTabbedPane	=
									getMainTabbedPane();

								mainTabbedPane.add
								(
									cloudTitle ,
									cloudPanel
								);

								mainTabbedPane.setSelectedIndex(
									mainTabbedPane.indexOfTab(
										cloudTitle ) );

								Window parent	=
									SwingUtils.getParentWindow( panel );

								if ( parent instanceof AbstractWindow )
								{
									mainTabbedPane.undock
									(
										(AbstractWindow)parent
									);
								}
								else
								{
									mainTabbedPane.undock();
								}
							}
						}
					);
				}
			}
		);
	}

	/** Add a filter button to a panel.
	 */

	protected JButton addAFilterButton
	(
		DialogPanel panel ,
		final AnalysisRunner finalAnalysis
	)
	{
		return panel.addButton
		(
			WordHoardSettings.getString( "Filter" , "Filter" ),
			new ActionListener()
			{
				public void actionPerformed( ActionEvent e )
				{
					SwingUtilities.invokeLater
					(
						new Runnable()
						{
							public void run()
							{
							}
						}
					);
				}
			}
		);
	}

	/**	Display context.
	 */

	protected void displayContext
	(
		AnalysisRunner analysis
	)
	{
								//	Note if calculator window
								//	is open already.

		final boolean isCalculatorWindowVisible	=
			getCalculatorWindow().isVisible();

								//	Create a progress panel.

		final OutputResults outputResults	=
			createProgressPanel( "" , "" );

								//	Get the output panel for the results.

		final DialogPanel panel	= outputResults.getOutputPanel();

		setBusyCursor();
								//	Get the context results.

		final ResultsPanel contextPanel	=
   	    	analysis.getContext
   	    	(
   	    		getCalculatorWindow() ,
   	    		outputResults.getProgressReporter()
   	    	);

		setDefaultCursor();

   		getCalculatorWindow().closePersistenceManager();

								//	Update the display on the AWT event
								//	thread or bad things will happen.

		Runnable runnable	=
			new Runnable()
			{
				public void run()
				{

								//	Hide the close button.

					outputResults.getCloseButton().setVisible( false );

								//	Get the main tabbed pane.

					WordHoardTabbedPane mainTabbedPane	=
						getMainTabbedPane();

								//	If the analysis pane is null,
								//	the analysis was not performed.
								//	Otherwise, create a new tabbed pane
								//	to hold the results.

					if ( contextPanel != null )
					{
						contextPanel.validate();
						panel.getBody().removeAll();
						panel.add( contextPanel );
						panel.revalidate();
						panel.paintImmediately( panel.getVisibleRect() );
						mainTabbedPane.undock( true );
					}
								//	Make sure the main pane gets redrawn
								//	to remove any artifacts left over from
								//	the analysis.

					mainTabbedPane.paintImmediately(
						mainTabbedPane.getVisibleRect() );

					setDefaultCursor();

								//	Close calculator window if it
								//	was not already open when
								//	analysis started.

					if ( !isCalculatorWindowVisible )
					{
						getCalculatorWindow().setVisible( false );
					}
				}
			};

		SwingUtilities.invokeLater( runnable );
	}

	/** Add a context button to a panel.
	 */

	protected JButton addAContextButton
	(
		DialogPanel panel ,
		final AnalysisRunner analysis
	)
	{
		JButton contextButton	=
			panel.addButton
			(
				WordHoardSettings.getString( "Context" , "Context" ) ,
				new ActionListener()
				{
					public void actionPerformed( ActionEvent e )
					{
						Thread runner = new Thread( "Analysis" )
						{
							public void run()
							{
								displayContext( analysis );
							}
						};

						Thread awtEventThread	=
							SwingUtils.getAWTEventThread();

						if ( awtEventThread != null )
						{
							ThreadUtils.setPriority(
								runner , awtEventThread.getPriority() - 1 );
						}

						runner.start();
					}
				}
			);

		analysis.setContextButton( contextButton );

		return contextButton;
	}

	/**	Run a frequency analysis.
	 *
	 *	@param	analysis	The analysis to run.
	 */

	protected void runFrequencyAnalysis( AnalysisRunner analysis )
	{
								//	Run the analysis in a thread so
								//	progress dialogs, etc. work.

		final AnalysisRunner finalAnalysis	= analysis;

		Thread runner = new Thread( "Analysis" )
		{
			public void run()
			{
				doRunFrequencyAnalysis( finalAnalysis );
			}
		};

		Thread awtEventThread	= SwingUtils.getAWTEventThread();

		if ( awtEventThread != null )
		{
			ThreadUtils.setPriority(
				runner , awtEventThread.getPriority() - 1 );
		}

		runner.start();
	}

	/**	Create a word list. */

	protected void createWordList()
	{
		WordFrequencies analysis	= new WordFrequencies();

		runFrequencyAnalysis( analysis );
	}

	/**	Single word collocation analysis of work with itself. */

	protected void findCollocates()
	{
		FindCollocates analysis	= new FindCollocates();

		runFrequencyAnalysis( analysis );
	}

	/**	Single word frequency profile analysis. */

	protected void compareSingleWord()
	{
		CompareSingleWordFrequencies analysis	=
			new CompareSingleWordFrequencies();

		runFrequencyAnalysis( analysis );
	}

	/**	Single word historical frequency analysis. */

	protected void trackWordOverTime()
	{
		TrackWordOverTime analysis	=
			new TrackWordOverTime();

		runFrequencyAnalysis( analysis );
	}

	/**	Compare collocate frequencies in one work with those in another. */

	protected void compareCollocations()
	{
		CompareCollocateFrequencies analysis	=
			new CompareCollocateFrequencies();

		runFrequencyAnalysis( analysis );
	}

	/**	Multiple word frequency profile analysis. */

	protected void compareManyWords()
	{
		CompareMultipleWordFrequencies analysis	=
			new CompareMultipleWordFrequencies();

		runFrequencyAnalysis( analysis );
	}

	/**	Compare texts analysis. */

	protected void compareTexts()
	{
		CompareTexts analysis	= new CompareTexts();

		runFrequencyAnalysis( analysis );
	}

	/**	Find multiword units. */

	protected void findMultiwordUnits()
	{
		FindMultiwordUnits analysis	= new FindMultiwordUnits();

		runFrequencyAnalysis( analysis );
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


