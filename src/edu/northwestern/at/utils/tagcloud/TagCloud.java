package edu.northwestern.at.utils.tagcloud;

/*	Please see the license information at the end of this file. */

import java.util.*;
import edu.northwestern.at.utils.*;

/**	Generates HTML for a tag cloud.
 *
 *	<p>
 *	A tag cloud accepts a series of (string , score, color) triples
 *	and creates HTML to display the strings in descending order by score.
 *	The font size for each string is based upon the relative value
 *	of that string's score.  The colors are standard HTML colors,
 *	e.g., blue, green, black, etc.  The styles are defined by
 *	TagCloudTextStyle and can be plain, bold, italicized, or bold and
 *	italicized.
 *	</p>
 *
 *	<p>
 *	To create a tag cloud:
 *	</p>
 *
 *	<p>
 *	<code>
 *	TagCloud tagCloud	= new TagCloud();
 *	</code>
 *	</p>
 *
 *	<p>
 *	To add an entry to the cloud:
 *	</p>
 *
 *	<p>
 *	<code>
 *	tagCloud.addTag( "my tag" , 10.234 , "black" ,
 *		new TagCloudTextStyle( false , false ) );
 *	</code>
 *	</p>
 *
 *	<p>
 *	To get the HTML for displaying the cloud after all the tags
 *	have been added:
 *	</p>
 *
 *	<p>
 *	<code>
 *	String html	= tagCloud.getHTML();
 *	</code>
 *	</p>
 *
 *	<p>
 *	The generated HTML only contains style settings and a div wrapping
 *	a sequence of lines for displaying the tags.  This allows the tag cloud
 *	html to be embedded within a larger html context.
 *	</p>
 *
 *	<p>
 *	To set the font size for the largest score (default 100):
 *	</p>
 *
 *	<p>
 *	<code>
 *	tagCloud.setMaximumFontSize( 150 );
 *	</code>
 *	</p>
 *
 *	<p>
 *	To set the minimum font size for display (default 3):
 *	</p>
 *
 *	<p>
 *	<code>
 *	tagCloud.setMinimumFontSize( 150 );
 *	</code>
 *	</p>
 *
 *	<p>
 *	Tags whose font sizes are smaller than minFontSize are not
 *	displayed in the cloud.
 *	</p>
 *
 *	<p>
 *	To deactivate a tag once it has been added, so that the tag will
 *	not display in the cloud:
 *	</p>
 *
 *	<p>
 *	<code>
 *	tagCloud.deactivateTag( "my tag" , 10.234 );
 *	</code>
 *	</p>
 *
 *	<p>
 *	Both the tag and score must match an existing tag or the deactivation
 *	request is ignored.  To reactivate a tag for display:
 *	</p>
 *
 *	<p>
 *	<code>
 *	tagCloud.activateTag( "my tag" , 10.234 );
 *	</code>
 *	</p>
 */

public class TagCloud
{
	/**	Holds HTML text for tag cloud. */

	protected StringBuffer cloudHTML;

	/**	Tags sorted by score. */

	protected SortedArrayList tagScoreList	= new SortedArrayList();

	/**	Maximum font point size for display. */

	protected double maxFontSize	= 100.0D;

	/**	Minimum font point size for display. */

	protected double minFontSize	= 3.0D;

	/**	Create empty tag cloud. */

	public TagCloud()
	{
		cloudHTML	= new StringBuffer();

		addHeader();
	}

	/**	Add tag to cloud.
	 *
	 *	@param	tag		The tag string.
	 *	@param	score	The tag score.
	 *	@param	color	The color for the string.
	 *					Should be specified using standard HTML
	 *					color names, e.g., "black" , "blue", etc.
	 *	@param	style	The style for the string.
	 */

	public void addTag
	(
		String tag ,
		double score ,
		String color ,
		TagCloudTextStyle style
	)
	{
		StyledScoredString	scoredTag	=
			new StyledScoredString( tag , score , color , style );

		tagScoreList.add( scoredTag );
	}

	/**	Add tag to cloud.
	 *
	 *	@param	tag		The tag string.
	 *	@param	score	The tag score.
	 *	@param	color	The color for the string.
	 *					Should be specified using standard HTML
	 *					color names, e.g., "black" , "blue", etc.
	 *
	 *	<p>
	 *	Uses plain normal as the text style.
	 *	</p>
	 */

	public void addTag( String tag , double score , String color )
	{
		StyledScoredString	scoredTag	=
			new StyledScoredString( tag , score , color );

		tagScoreList.add( scoredTag );
	}

	/**	Add tag to cloud.
	 *
	 *	@param	tag		The tag string.
	 *	@param	score	The tag score.
	 *
	 *	<p>
	 *	Uses plain normal as the text style and black as the text color.
	 *	</p>
	 */

	public void addTag( String tag , double score )
	{
		StyledScoredString	scoredTag	=
			new StyledScoredString( tag , score );

		tagScoreList.add( scoredTag );
	}

	/**	Deactivate tag in cloud.
	 *
	 *	@param	tag		The tag string.
	 *	@param	score	The tag score.
	 */

	public void deactivateTag( String tag , double score )
	{
								//	Get scored tag entry.
								//	StyledScoredString entries only
								//	based comparisons upon tag string
								//	and score, so we don't need the color
								//	or style.

		StyledScoredString scoredTag	=
			new StyledScoredString( tag , score );

								//	Look up scoredTag in tag list.

		int index	= tagScoreList.indexOf( scoredTag );

								//	If found, set the scored tag entry
								//	inactive.
		if ( index >= 0 )
		{
			scoredTag	= (StyledScoredString)tagScoreList.get( index );
			scoredTag.setIsActive( false );
		}
	}

	/**	Activate tag in cloud.
	 *
	 *	@param	tag		The tag string.
	 *	@param	score	The tag score.
	 */

	public void activateTag( String tag , double score )
	{
								//	Get scored tag entry.
								//	StyledScoredString entries only
								//	based comparisons upon tag string
								//	and score, so we don't need the color
								//	or style.

		StyledScoredString scoredTag	=
			new StyledScoredString( tag , score );

								//	Look up scoredTag in tag list.

		int index	= tagScoreList.indexOf( scoredTag );

								//	If found, set the scored tag entry
								//	inactive.
		if ( index >= 0 )
		{
			scoredTag	= (StyledScoredString)tagScoreList.get( index );
			scoredTag.setIsActive( true );
		}
	}

	/**	Return tag cloud HTML. */

	public String getHTML()
	{
								//	HTML buffer.

		cloudHTML	= new StringBuffer();

								//	If there are any tags ...

		if ( tagScoreList.size() > 0 )
		{
								//	Add the HTML header text.
			addHeader();
								//	Set maximum score to a negative value.

			double maxScore	= -1.0D;

								//	Run over all tags.

			for ( int i = tagScoreList.size() - 1 ; i >= 0 ; i-- )
			{
								//	Get next scored tag.

				StyledScoredString scoredTag	=
					(StyledScoredString)tagScoreList.get( i );

								//	If tag is not active, skip it.

				if ( !scoredTag.getIsActive() ) continue;

								//	Set maximum score value if not
								//	already set.

				if ( maxScore < 0.0D )
				{
					maxScore	= scoredTag.getScore();
				}
								//	Compute scaled score.

				double score	=
					( scoredTag.getScore() / maxScore ) * maxFontSize;

								//	If scaled score is greater or equal to
								//	the minimum font size, add the tag
								//	to the HTML display.

				if ( score >= minFontSize )
				{
					TagCloudTextStyle style	= scoredTag.getStyle();

					StringBuffer html		= new StringBuffer();

					html.append
					(
						"<a href=\"" + scoredTag.getString() + "\t" +
						scoredTag.getScore() + "\"" +
						" style=\"font-size: " +
						score +
						"px; color: " + scoredTag.getColor()
					);

					if ( style.isBold )
					{
						html.append( "; font-weight: bold" );
					}

					if ( style.isItalic )
					{
						html.append( "; font-style: italics" );
					}

					html.append
					(
						"\">" + scoredTag.getString() + "&nbsp;</a>"
					);

					addHTML( html.toString() );
				}
			}
								//	Add the HTML footer.
			addFooter();
		}
								//	Return HTML for cloud.

		return cloudHTML.toString();
	}

	/**	Set the maximum font size.
	 *
	 *	@param	maxFontSize	Maximum font size for display.
	 */

	public void setMaximumFontSize( double maxFontSize )
	{
		this.maxFontSize	= Math.max( maxFontSize , 1.0D );
	}

	/**	Set the minimum font size.
	 *
	 *	@param	minFontSize	Minimum font size for display.
	 *
	 *	<p>
	 *	Tags whose font sizes are smaller than minFontSize are not
	 *	displayed in the cloud.
	 *	</p>
	 */

	public void setMinimumFontSize( double minFontSize )
	{
		this.minFontSize	=
			Math.min( maxFontSize , Math.max( minFontSize , 1.0D ) );
	}

	/**	Add HTML line to cloud. */

	protected void addHTML( String html )
	{
		cloudHTML.append( html );
		cloudHTML.append( "\r\n" );
	}

	/** Add header. */

	protected void addHeader()
	{
		addHTML( "<style>" );
		addHTML( "body { font-family: Arial, Helvetica, Sans-serif; }" );
		addHTML( "a { text-decoration: none; }" );
		addHTML( "span { text-decoration: none; }" );
		addHTML( ".tagcloud { padding: 1px; border: 1px solid #eeeeee; }" );
		addHTML( "</style>" );
		addHTML( "<div class=\"tagcloud\" style=\"margin: auto; width: 100%;\">" );
	}

	/** Add footer. */

	protected void addFooter()
	{
		addHTML( "</div>" );
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


