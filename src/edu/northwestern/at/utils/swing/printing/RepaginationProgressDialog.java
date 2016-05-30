package edu.northwestern.at.utils.swing.printing;

/*	Please see the license information at the end of this file. */

import java.awt.print.*;
import javax.swing.*;

import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.*;

/** Repagination progress dialog.
 *
 *	<p>
 *	Displays a non-modal dialog entitled "Repaginating" which
 *	displays the progress of a repagination operation.  Repagination
 *	is used to determine the number of pages to print for a
 *	Pageable component.
 *	</p>
 *
 *	<p>
 *	A label displaying the number of pages paginated so far takes
 *	the form:  Paginating n where "n" is the last page paginated.
 *	</p>
 */

public class RepaginationProgressDialog extends ProgressDialog
{
	/** Create print progress dialog.
	 */

	public RepaginationProgressDialog()
	{
		super(
			"Repaginating",
			"Paginating ",
			0,
			0,
			true );

		this.progressBar.setVisible( false );

		updateProgress( 0 );
	}

	/** Update repagination progress.
	 *
	 *	@param	pageCountRepaginated	Number of pages repaginated
	 *									starting at 1.
	 */

	public void updateProgress( int pageCountRepaginated )
	{
		final int finalPageCountRepaginated = pageCountRepaginated;

		if ( pageCountRepaginated > 0 )
		{
			SwingUtilities.invokeLater
			(
				new Runnable()
				{
					public void run()
					{
						label.setText(
							"Paginating " + finalPageCountRepaginated );

						label.setVisible( true );
					}
				}
			);
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

