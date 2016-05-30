package edu.northwestern.at.wordhoard.swing.bibtool;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.tree.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

/** Defines a node for a WorkPartTreeNode. This class indicates whether node is not a full workpart, but just structure wrapper for some children. */

public class WorkPartTreeNode extends DefaultMutableTreeNode
{
	private boolean isWrapper = false;
	
	public WorkPartTreeNode( Object obj )
	{
		super(obj);
	}

	public WorkPartTreeNode()
	{
		super();
	}

/*	public WorkPartTreeNode( Object obj, isWrapper )
	{
		this.obj=obj;
		this.isWrapper = isWrapper;
	}
*/
	public void	setIsWrapper(boolean isWrapper) {
		this.isWrapper = isWrapper;
	}
	
	public boolean isWrapper() {return isWrapper;}
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

