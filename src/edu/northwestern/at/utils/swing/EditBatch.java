package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

/** EditBatch -- interface for object implementing batch edit undo.
 *
 *	<p>
 *	This interface defines methods for starting and ending a batch sequence
 *	of edits.  These methods should be implemented by objects providing an
 *	undo facility in which a batch of edits is to be treated as a single edit
 *	for purposes of undo or redo.
 *	</p>
 *
 *	<p>
 *	For example, a text editor may use EditBatch to ensure a single undo
 *	undoes all of the replacements performed by a single "replace all."
 *	</p>
 */

public interface EditBatch
{
	/** Start batched edit sequence to be considered a single undo edit. */

	public void startEditBatch();

	/** End batched edit sequence. */

	public void endEditBatch();
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

