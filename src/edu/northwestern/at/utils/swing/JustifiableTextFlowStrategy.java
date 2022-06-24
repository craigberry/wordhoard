package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import javax.swing.text.*;

public class JustifiableTextFlowStrategy extends ParagraphView.FlowStrategy
{
	public void layout( FlowView fv )
	{
								// Do the normal layout.  For a justified
								// alignment, this will be start out as
								// a left alignment in this implementation.

		super.layout( fv );

								// Now see if we have to fix up the layout
								// because justified alignment was requested.

		AttributeSet attr	= fv.getElement().getAttributes();

		float lineSpacing	= StyleConstants.getLineSpacing( attr );

		boolean justifiedAlignment	=
			( StyleConstants.getAlignment( attr ) ==
				StyleConstants.ALIGN_JUSTIFIED );

								// Return if we're not doing justified alignment
								// or adding interline spacing.

		if ( !( justifiedAlignment || ( lineSpacing > 1 ) ) )
		{
			return;
		}
								// We have to do full justification or
								// change the line spacing.

								// Get the number of rows of text in this
								// paragraph view.

		int cnt = fv.getViewCount();

								// For each line of text in the paragraph ...

		for ( int i = 0; i < ( cnt - 1 ); i++ )
		{
								// Get the next row of text.

			JustifiedTextRowView row = (JustifiedTextRowView)fv.getView( i );

								// Add the specified amount of interline
								// spacing if > one line.

			if ( lineSpacing > 1 )
			{
				float height	= row.getMinimumSpan( View.Y_AXIS );
				float addition	= ( height * lineSpacing ) - height;

				if ( addition > 0 )
				{
					row.setInsets
					(
						row.getTopInset(),
						row.getLeftInset(),
						(short)addition,
						row.getRightInset()
					);
				}
			}
                                // Justify the text on the line if requested.

			if ( justifiedAlignment )
			{
				restructureRow( row , i );
				row.setRowNumber( i + 1 );
			}
		}
	}

	protected void restructureRow( View row , int rowNum )
	{
		int rowStartOffset	= row.getStartOffset();
		int rowEndOffset	= row.getEndOffset();
		String rowContent	= "";

		try
		{
			rowContent	=
				row.getDocument().getText(
					rowStartOffset,
					rowEndOffset - rowStartOffset );

			if ( rowNum == 0 )
			{
				int index = 0;

				while ( rowContent.charAt( 0 ) == ' ' )
				{
					rowContent	= rowContent.substring( 1 );

					if ( rowContent.length() == 0 )
						break;
				}
			}
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}

		int rowSpaceCount	= getSpaceCount( rowContent );

		if ( rowSpaceCount < 1 )
		{
			return;
		}

		int[] rowSpaceIndexes	=
			getSpaceIndexes( rowContent , row.getStartOffset() );

		int currentSpaceIndex	= 0;

		for ( int i = 0; i < row.getViewCount(); i++ )
		{
			View child = row.getView( i );

			if (	(	child.getStartOffset() <
							rowSpaceIndexes[ currentSpaceIndex ] ) &&
					(	child.getEndOffset() >
						rowSpaceIndexes[ currentSpaceIndex ] ) )
			{
								// Split view.

				View first	=
					child.createFragment(
						child.getStartOffset(),
						rowSpaceIndexes[ currentSpaceIndex ] );

				View second	=
					child.createFragment(
						rowSpaceIndexes[ currentSpaceIndex ],
						child.getEndOffset() );

				View[] repl = new View[ 2 ];

				repl[ 0 ] = first;
				repl[ 1 ] = second;

				row.replace( i, 1, repl );

				currentSpaceIndex++;

				if ( currentSpaceIndex >= rowSpaceIndexes.length )
				{
					break;
				}
			}
		}

		int childCnt = row.getViewCount();
	}

	protected static int getSpaceCount( String content )
	{
		int result	= 0;
		int index	= content.indexOf( ' ' );

		while ( index >= 0 )
		{
			result++;
			index = content.indexOf( ' ' , index + 1 );
		}

		return result;
	}

	protected static int[] getSpaceIndexes( String content , int shift )
	{
		int cnt			= getSpaceCount( content );
		int[] result	= new int[ cnt ];
		int counter		= 0;
		int index		= content.indexOf( ' ' );

		while ( index >= 0 )
		{
			result[ counter ]	= index + shift;
			counter++;
			index				= content.indexOf( ' ' , index + 1 );
		}

		return result;
	}

	public JustifiableTextFlowStrategy()
	{
		super();
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

