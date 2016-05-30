package edu.northwestern.at.wordhoard.swing.querytool;

/*	Please see the license information at the end of this file. */

import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;    

import	edu.northwestern.at.wordhoard.model.querytool.*;
import	edu.northwestern.at.wordhoard.model.grouping.*;

public class SearchCriteriaTransferable implements Transferable {
		SearchCriteriaTransferData data;
		DataFlavor xferFlavor;
		String localType = DataFlavor.javaJVMLocalObjectMimeType + ";class=edu.northwestern.at.wordhoard.swing.querytool.SearchCriteriaTransferData";

        public SearchCriteriaTransferable(SearchCriteriaTransferData gos) {
            data = gos;
			try {
				xferFlavor = new DataFlavor(localType);
			} catch (ClassNotFoundException e) {
				System.out.println("SearchCriteriaTransferable: unable to create data flavor, " + e.getMessage());
			}
        }

        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
            if (!isDataFlavorSupported(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            return data;
        }

        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[] { xferFlavor };
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            if (xferFlavor.equals(flavor)) {
                return true;
            }
            return false;
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

