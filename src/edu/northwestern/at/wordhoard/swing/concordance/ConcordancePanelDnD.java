package edu.northwestern.at.wordhoard.swing.concordance;

/*	Please see the license information at the end of this file. */


import javax.swing.*;
import javax.swing.tree.*;
import java.text.*;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.model.grouping.*;
import edu.northwestern.at.wordhoard.swing.querytool.*;
import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.utils.db.*;

import java.awt.datatransfer.*;

/**	A search results panel.
 */

public class ConcordancePanelDnD extends ConcordancePanel implements ClipboardOwner  {

	/**	focue Owner for Edit Menu handling. */
    private JComponent focusOwner = null;

	/** Transfer Handler for cut/copy/past and DragNDrop */

	private	WordSearchTransferHandler wordSearchTransferHandler = null;

	/**	Drag icon. */
	private static ImageIcon dragicon = null;

	/**	Creates a new search results panel.
	 *
	 *	@param	pm					Persistence Manager.
	 *
	 *	@param	sq					Search criteria.
	 *
	 *	@param	parentWindow		Parent window.
	 */


	public ConcordancePanelDnD (PersistenceManager pm, SearchCriteria sq, AbstractWorkPanelWindow parentWindow) throws PersistenceException
	{
		super(pm,sq, parentWindow);
		wordSearchTransferHandler = new WordSearchTransferHandler();
		tree.setTransferHandler(wordSearchTransferHandler);
        tree.setDragEnabled(true);
		SearchCriteriaDragMouseHandler mh = new SearchCriteriaDragMouseHandler();
		tree.addMouseListener(mh);
		tree.addMouseMotionListener(mh);
		tree.getSelectionModel().setSelectionMode(
			TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

	}


		public void lostOwnership(Clipboard clipboard, Transferable contents) {
//				System.out.println(this.getClass().getName() + ": lostOwnership");
		}


	/**	TransferHandler for drag and drop
	 *
	 *
	 */

	public class WordSearchTransferHandler extends TransferHandler {
		SearchCriteriaTransferData data;
		DataFlavor xferFlavor;
		String xferType = DataFlavor.javaJVMLocalObjectMimeType + ";class=edu.northwestern.at.wordhoard.swing.querytool.SearchCriteriaTransferData";

		public WordSearchTransferHandler() {
			try {
				xferFlavor = new DataFlavor(xferType);
			} catch (ClassNotFoundException e) {
				System.out.println("WordSearchTransferHandler: unable to create data flavor");
			}
		}

		public WordSearchTransferHandler(String property) {
			try {
				xferFlavor = new DataFlavor(xferType);
			} catch (ClassNotFoundException e) {
				System.out.println("WordSearchTransferHandler: unable to create data flavor");
			}
		}

		public boolean importData(JComponent c, Transferable t) {return true;}

    protected void exportDone(JComponent c, Transferable data, int action) {}

    private boolean haslocalFlavor(DataFlavor[] flavors) {
        if (xferFlavor == null) {
            return false;
        }

        for (int i = 0; i < flavors.length; i++) {
            if (flavors[i].equals(xferFlavor)) {
                return true;
            }
        }
        return false;
    }

    public boolean canImport(JComponent c, DataFlavor[] flavors) { return false;}

    protected Transferable createTransferable(JComponent c) {
		SearchCriteriaTransferData gos = new SearchCriteriaTransferData();
        if (c instanceof JTree) {
			TreePath[] paths =  ((JTree)c).getSelectionPaths();
            if (paths == null || paths.length == 0) {
                return null;
            }
			try {
				for (int i = 0; i < paths.length; i++) {
				  Object o = ((DefaultMutableTreeNode)paths[i].getLastPathComponent()).getUserObject();
					if ((o instanceof SearchCriterion)) {
						gos.add(o);
					} else if(o instanceof Spelling) {
						gos.add(new SpellingWithCollationStrength((Spelling)o,Collator.TERTIARY));
					} else if(o instanceof PubDecade) { // PubDecade is not a SearchCriterion, but PubYearRange is.
						Integer startYear = new Integer(((PubDecade)o).getStartYear());
						Integer endYear = new Integer(startYear.intValue() + 10);
						gos.add(new PubYearRange(startYear,endYear));
					} else {System.out.println("ConcordancePanelDnD createTransferable: " + o.getClass().getName());}
				}
				return new SearchCriteriaTransferable(gos);
			} catch (Exception e) {Err.err(e);}
		}
        return null;
    }

    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }

	public Icon getVisualRepresentation(Transferable t) {
		if(dragicon==null) {
			dragicon = Images.get("icon.gif");
		}
		return dragicon;
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

