package edu.northwestern.at.wordhoard.swing.calculator.analysis;

/*	Please see the license information at the end of this file. */

import java.util.*;
import javax.swing.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.swing.*;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;
import edu.northwestern.at.wordhoard.swing.calculator.widgets.*;

/**	Displays collocate frequencies for selected works.
 */

public class CollocateFrequencies
	extends FrequencyAnalysisRunnerBase
	implements AnalysisRunner
{
    /**	Create a collocate frequency analysis object.
     *
     *	@param	dialogType	Type of collocation analysis.
     */

	public CollocateFrequencies( int dialogType )
	{
		super( dialogType );
	}

	/**	Get collocation counts.
	 *
	 *	@param	wordCounter		The texts in which to find collocates.
	 *	@param	wordToAnalyze		The words for which to find collocates.
	 *	@param	progressReporterText	Progress dialog text.
	 *
	 *	@return						Two element array of TreeMap
	 *								[0]	TreeMap with collocates as keys and
	 *									frequencies as values, cutoff honored.
	 *								[1] TreeMap with collocates as keys and
	 *									work frequencies as values.
	 *								[2]	TreeMap with collocates as keys and
	 *									frequencies as values, ignoring cutoff.
	 *
	 *	<p>
	 *	If the progress dialog is cancelled, the return value is null.
	 *	</p>
	 */

	protected TreeMap[] getCollocateCounts
	(
		WordCounter wordCounter ,
		Spelling wordToAnalyze ,
		ProgressReporter progressReporter ,
		String progressReporterText
	)
	{
								//	Get word occurrences
								//	for word form whose
								//	collocates are desired.

//		String caseInsensitiveWordToAnalyze	=
//			wordToAnalyze.toLowerCase();

		String caseInsensitiveWordToAnalyze	=
			CharsetUtils.translateToInsensitive
			(
				wordToAnalyze.getString()
			);

		long startTime	= System.currentTimeMillis();

		wordOccs	=
			wordCounter.getWordOccurrences( wordToAnalyze , wordForm );

		long endTime	= System.currentTimeMillis() - startTime;

								//	Reset progress dialog.

		if ( displayProgress )
		{
			progressReporter.setMaximumBarValue( wordOccs.length - 1 );
			progressReporter.setIndeterminate( false );
			progressReporter.updateProgress
			(
				0 ,
				progressReporterText
			);
		}
								//	Preload collocates.

		startTime	= System.currentTimeMillis();

		PersistenceManager pm	= PMUtils.getPM();

		Collection colWords	=
			CollocateUtils.getColocates
			(
				pm ,
				Arrays.asList( wordOccs ) ,
				Math.max( leftSpan , rightSpan )
			);

		endTime		= System.currentTimeMillis() - startTime;

								//	Preload word parts.

		startTime	= System.currentTimeMillis();

		Collection colWordParts	= null;

		if ( ( colWords != null ) && ( colWords.size() > 0 ) )
		{
			colWordParts	=
				PreloadUtils.preloadWordParts( pm , colWords );
        }

		endTime	= System.currentTimeMillis() - startTime;

								//	Get collocation counts.

		TreeMap collocationCountMap	= new TreeMap();
		TreeMap workCountsMap		= new TreeMap();

		startTime	= System.currentTimeMillis();

		for ( int i = 0 ; i < wordOccs.length ; i++ )
		{
								//	Pick up next occurrence of the word
								//	for which to find collocates.

			Word wordOcc	= wordOccs[ i ];

			boolean inCache	= pm.contains( wordOcc );

			if ( !inCache )
			{
				wordOcc	=
					(Word)PersistenceManager.doLoad
					(
						Word.class ,
						wordOcc.getId()
					);
            }
								//	Get words in specified span interval.

			ArrayList spanList	= new ArrayList();

			if ( wordOcc != null )
			{
				spanList.add( wordOcc );

				Word prevOcc	= wordOcc.getPrev();
				Word nextOcc	= wordOcc.getNext();

				if ( prevOcc != null )
				{
					for ( int j = 0 ; j < leftSpan ; j++ )
					{
						spanList.add( prevOcc );
						prevOcc	= prevOcc.getPrev();
						if ( prevOcc == null ) break;
					}
				}

				if ( nextOcc != null )
				{
					for ( int j = 0 ; j < rightSpan ; j++ )
					{
						spanList.add( nextOcc );
						nextOcc	= nextOcc.getNext();
						if ( nextOcc == null ) break;
					}
				}
			}

			if ( spanList.size() == 0 ) continue;

			Word[] spanOccs	= (Word[])spanList.toArray( new Word[]{} );

								//	For each word in the span ...

			for ( int j = 0 ; j < spanOccs.length ; j++ )
			{
				String[] keys;
								//	Get spelling or lemma as specified
								//	for the next word in the span.

				if ( wordForm == WordForms.SPELLING )
				{
					keys		=
						new String[]
						{
							WordUtils.getSpellingAndCompoundWordClass(
								spanOccs[ j ] )
						};
                }
                else
                {
					keys	= WordUtils.getLemmaTags( spanOccs[ j ] );
                }

				for ( int k = 0 ; k < keys.length ; k++ )
				{
				    String key	= keys[ k ];

					Spelling spKey	=
						new Spelling
						(
							key ,
							spanOccs[ j ].getSpelling().getCharset()
						);

//					if ( spKey.equalsIgnoreCase( wordToAnalyze ) ) continue;

					if	(	CharsetUtils.translateToInsensitive(
							spKey.getString() ).equals(
						caseInsensitiveWordToAnalyze ) ) continue;

								//	If the destination map does not contain
								//	this word, add it with the count from
								//	the source map.

					if ( !collocationCountMap.containsKey( spKey ) )
					{
						collocationCountMap.put( spKey , new Integer( 1 ) );
					}
					else
					{
								//	If the destination map contains the word,
								//	pick up the current count of the word
								//	in the destination map.

						Integer count	=
							(Integer)collocationCountMap.get( spKey );

								//	Add the source count to the destination
								//	count in the destination map.

						collocationCountMap.put
						(
							spKey ,
							new Integer( count.intValue() + 1 )
						);
					}
								//	Add this word occurrence's work
								//	to list of works for this collocate.

					if ( !workCountsMap.containsKey( spKey ) )
					{
						TreeMap workMap	= new TreeMap();

						workMap.put(
							spanOccs[ j ].getWork().getId() , "1" );

						workCountsMap.put( spKey , workMap );
					}
					else
					{
						TreeMap workMap	=
							(TreeMap)workCountsMap.get( spKey );

						Long workKey	=
							spanOccs[ j ].getWork().getId();

						if ( !workMap.containsKey( workKey ) )
						{
							workMap.put( workKey , "1" );

							workCountsMap.put( spKey , workMap );
						}
					}
								//	Add the current word occurrence to the
								//	list of word occurrences for this word.

					if ( !collocationOccurrenceMap.containsKey( spKey ) )
					{
						ArrayList wordOccsList	= new ArrayList();

						wordOccsList.add( wordOcc );

						collocationOccurrenceMap.put( spKey , wordOccsList );
					}
					else
					{
								//	If the destination map contains the word,
								//	pick up the current array list of
								//	word occurrences.

						ArrayList wordOccsList	=
							(ArrayList)collocationOccurrenceMap.get( spKey );

								//	Add this word occurrence to the list
								//	if we haven't already done so.

						if ( !wordOccsList.contains( wordOcc ) )
						{
							wordOccsList.add( wordOcc );
						}
					}
				}
			}
								//	Update progress dialog.

			if ( displayProgress )
			{
				progressReporter.updateProgress
				(
					i ,
					progressReporterText
				);
								//	If progress dialog cancelled,
								//	return null value.

				if ( progressReporter.isCancelled() )
				{
					return null;
				}
			}
		}

		endTime	= System.currentTimeMillis() - startTime;

		TreeMap outputMap	= new TreeMap();

		Iterator iterator	= collocationCountMap.keySet().iterator();

		while ( iterator.hasNext() )
		{
			Object key 		= iterator.next();
			Integer count	= (Integer)collocationCountMap.get( key );

			if ( count.intValue() >= cutoff )
			{
				outputMap.put( key , count );
			}
		}

		return new
			TreeMap[]
			{
				outputMap ,
				workCountsMap ,
				collocationCountMap
			};
	}

	/**	Run analysis
	 */

	public void runAnalysis
	(
		JFrame parentWindow ,
		ProgressReporter progressReporter
	)
	{
//		this.parentWindow		= parentWindow;
		this.progressReporter	= progressReporter;

								//	Create progress display.

		if ( displayProgress && ( progressReporter != null ) )
		{
			progressReporter.setTitle
			(
				WordHoardSettings.getString
				(
					"Collocates" ,
					"Collocates"
				)
			);

			progressReporter.updateProgress
			(
				WordHoardSettings.getString
				(
					"Findingwordoccurrences" ,
					"Finding word occurrences"
				)
			);

			progressReporter.setIndeterminate( true );
		}
								//	No results to start.

		resultsPanel	= null;

								//	Get map of collocate frequencies.

		TreeMap[] collocateMaps	=
			getCollocateCounts
			(
				analysisText ,
				wordToAnalyze ,
				progressReporter ,
				WordHoardSettings.getString
				(
					"Findingwordoccurrences" ,
					"Finding word occurrences"
				)
			);
								//	If operation cancelled, the collocateMaps
								//	tree is null.  Quit if so.

		if ( collocateMaps	== null )
		{
			closeProgressReporter();
			return;
		}

		TreeMap outputMap		= collocateMaps[ 0 ];
		TreeMap workCountsMap	= collocateMaps[ 1 ];

								//	Get column titles for output display.

    	String[] columnNames	=
    		new String[]
	    	{
				WordHoardSettings.getString(
					"coltitleCollocate" , "<html><br>Collocate</html>" ) ,
				WordHoardSettings.getString(
					"coltitleAnalysiscount" , "<html>analysis<br>count</html>" ) ,
				WordHoardSettings.getString(
					"coltitleWorkcount" , "<html>work<br>count</html>" )
    		};
								//	Create sorted table model to hold
								//	the output.
		model		=
			new WordHoardSortedTableModel( columnNames , 0 , true );

								//	Create table entries.  The columns
								//	are the collocate text, the count
								//	of the collocate in the selected
								//	corpus/text, and the number of texts
								//	in the selected corpus/text in which
								//	this collocate occurs.

		Set keyset			= outputMap.keySet();

		int maxLabelWidth	= 0;
		String maxLabel		= "";

		for	( Iterator iterator	= keyset.iterator(); iterator.hasNext(); )
		{
			Spelling collocate	= (Spelling)iterator.next();

			int collocateLength	= collocate.getString().length();

			if ( collocateLength > maxLabelWidth )
			{
				maxLabelWidth	= collocateLength;
				maxLabel		= collocate.getString();
			}

			Integer collocateCount	=
				(Integer)outputMap.get( collocate );

			double[] freqAnal	= new double[ 2 ];

			freqAnal[ 0 ]		= collocateCount.intValue();
			freqAnal[ 1 ]		= 0.0D;

			if ( workCountsMap.containsKey( collocate ) )
			{
				TreeMap workMap	=
					(TreeMap)workCountsMap.get( collocate );

				freqAnal[ 1 ]	= workMap.size();
			}

			model.add
			(
				new FrequencyAnalysisDataRow
				(
					collocate.getString() ,
					freqAnal
				)
			);

			if ( displayProgress )
			{
				if ( progressReporter.isCancelled() ) break;
			}
		}

										//	Close progress dialog.

		boolean cancelled	= closeProgressReporter();

										//	Generate the results.
		resultsPanel		=
			cancelled ? null :
				generateResults( model , maxLabel , maxLabel );
	}

	/**	Displays results of collocation frequency analysis in a sorted table.
	 *
	 *	@param	model		Table model holding data to display.
	 *	@param	maxLabel	Maximum width value for label column.
	 *	@param	maxLabel2	Maximum width value for word class column.
	 */

	protected ResultsPanel generateResults
	(
		WordHoardSortedTableModel model ,
		String maxLabel ,
		String maxLabel2
	)
	{
		int rowCount	= model.getRowCount();

		PrintfFormat titleFormat	=
			new PrintfFormat
			(
				WordHoardSettings.getString(
					"collocationFrequenciesTitle" +
						( ( rowCount == 1 ) ? "S" : "P" ) ,
			    	"Frequencies of %s collocates of %s (%s) in %s" )
			);

		String title	=
			titleFormat.sprintf
			(
				new Object[]
				{
					StringUtils.formatNumberWithCommas( rowCount ) ,
					wordToAnalyze ,
					CharsetUtils.translateToInsensitive( wordFormString ) ,
					analysisText.toString( useShortWorkTitlesInHeaders )
				}
			);

		String shortTitle	=
			WordHoardSettings.getString
			(
				"Collocatefrequencies" ,
				"Collocate frequencies"
			);

		return super.generateResults
		(
			new Spelling( "" , TextParams.ROMAN ) ,
			title ,
			shortTitle ,
			model.getColumnNames() ,
			new String[]
			{
				"%s" ,
				getDoubleFormat( 12 ) ,
				getDoubleFormat( 12 )
			} ,
			1 ,
			-1 ,
			-1 ,
			model ,
			new String[]{ maxLabel , maxLabel2 }
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


