package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

/** SelectedTextReplacer -- interface for selected text replacement.
 *
 *	<p>
 *	This interface defines the replaceSelection method which
 *	is intended to replace selected text some kind of Document type
 *	typically held in a JTextPane or descendant.
 *	</p>
 */

public interface SelectedTextReplacer
{
	/** Replace currently selected document text.
	 *
	 *	@param	replacementText 	The replacement text.
	 */

	public void replaceSelection( String replacementText );
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

