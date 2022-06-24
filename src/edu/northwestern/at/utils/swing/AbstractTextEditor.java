package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.awt.event.*;

/**	Base class for actions common to text editors.
 *
 *	<p>
 *	We use this instead of an interface in order to keep the action methods
 *	protected.  All of the action methods here are empty stubs.
 *	</p>
 */

public abstract class AbstractTextEditor
{
	/**	Files. */

	protected void doNew( ActionEvent e ){};
	protected void doOpen( ActionEvent e ){};
	protected void doSave( ActionEvent e ){};
	protected void doSaveAs( ActionEvent e ){};
	protected void doExit( ActionEvent e ){};

	/**	Printing. */

	protected void doPageSetup( ActionEvent e ){};
	protected void doPrintPreview( ActionEvent e ){};
	protected void doPrint( ActionEvent e ){};

	/**	Cut/copy/paste/select. */

	protected void doCopy( ActionEvent e ){};
	protected void doCut( ActionEvent e ){};
	protected void doPaste( ActionEvent e ){};
	protected void doSelectAll( ActionEvent e ){};

	/**	Search/replace. */

	protected void doFind( ActionEvent e ){};
	protected void doReplace( ActionEvent e ){};
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

