package edu.northwestern.at.wordhoard.tools.fixers;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import org.w3c.dom.*;

import edu.northwestern.at.utils.xml.*;

/**	Fixes Chaucer tags.
 *
 *	<p>Adds "tag" and "pathTag" attributes to the "div" elements for 
 *	Chaucer works.
 *
 *	<p>This fixer must be run AFTER the NukeDiv and ChaTitle fixers.
 */

public class ChaTag extends Fixer {

	/**	Fixes an XML DOM tree.
	 *
	 *	@param	corpusTag	Corpus tag.
	 *
	 *	@param	workTag		Work tag.
	 *
	 *	@param	document	XML DOM tree.
	 *
	 *	@throws Exception
	 */

	public void fix (String corpusTag, String workTag, Document document) 
		throws Exception
	{
		Element bodyEl = DOMUtils.getDescendant(document,
			"TEI.2/text/body");
		NodeList list = bodyEl.getElementsByTagName("div");
		int n = list.getLength();
		String astPartTag = null;
		String boeBookTag = null;
		for (int i = 0; i < n; i++) {
			Element divEl = (Element)list.item(i);
			String title = divEl.getAttribute("head");
			if (title == null || title.length() == 0) {
				Element headEl = DOMUtils.getChild(divEl, "head");
				title = DOMUtils.getText(headEl);
			}
			String tag = null;
			String pathTag = null;
			if (workTag.equals("aaa")) {
				pathTag = null;
				tag = "body";
			} else if (workTag.equals("ast")) {
				pathTag = astPathTag(title);
				if (pathTag.equals("i") || pathTag.equals("ii") || pathTag.equals("s")) {
					tag = pathTag;
					astPartTag = pathTag;
				} else {
					tag = astPartTag + "-" + pathTag;
				}
			} else if (workTag.equals("bod")) {
				pathTag = null;
				tag = "body";
			} else if (workTag.equals("boe")) {
				pathTag = boePathTag(title);
				if (pathTag.equals("i") || pathTag.equals("ii") || pathTag.equals("iii") ||
					pathTag.equals("iv") || pathTag.equals("v"))
				{
					tag = pathTag;
					boeBookTag = pathTag;
				} else {
					tag = boeBookTag + "-" + pathTag;
				}
			} else if (workTag.equals("can")) {
				pathTag = canPathTag(title);
				tag = canTag;
			} else if (workTag.equals("hof")) {
				pathTag = null;
				tag = "body";
			} else if (workTag.equals("lgw")) {
				pathTag = lgwPathTag(title);
				tag = pathTag;
			} else if (workTag.equals("poe")) {
				pathTag = poePathTag(title);
				tag = pathTag;
			} else if (workTag.equals("pof")) {
				pathTag = null;
				tag = "body";
			} else if (workTag.equals("ror")) {
				pathTag = rorPathTag(title);
				tag = pathTag;
			} else if (workTag.equals("tro")) {
				pathTag = troPathTag(title);
				tag = pathTag;
			}
			if (pathTag != null) divEl.setAttribute("pathTag", pathTag);
			divEl.setAttribute("tag", tag);
		}
		log("ChaTag", "Tags set on div elements");
	}
	
	/**	Gets a path tag for A Treatise on the Astrolabe.
	 *
	 *	@param	title		Part title.
	 *
	 *	@return				Path tag.
	 */
	
	private String astPathTag (String title) {
		if (title.equals("Prologue")) {
			return "p";
		} else if (title.equals("Part I")) {
			return "i";
		} else if (title.equals("Part II")) {
			return "ii";
		} else if (title.equals("Supplemental Propositions (Apocrypha)")) {
			return "s";
		} else {
			return title.substring(1, title.length()-1);
		}
	}
	
	/**	Gets a path tag for Boece.
	 *
	 *	@param	title		Part title.
	 *
	 *	@return				Path tag.
	 */
	
	private String boePathTag (String title) {
		if (title.startsWith("Book")) {
			return title.substring(5).toLowerCase();
		} else if (title.startsWith("Metrum")) {
			return "m" + title.substring(7);
		} else if (title.startsWith("Prosa")) {
			return "p" + title.substring(6);
		}
		return null;
	}
	
	/**	Gets a path tag for The Canterbury Tales.
	 *
	 *	@param	title		Part title.
	 *
	 *	@return				Path tag.
	 */
	 
	private int melibeeCount;
	
	private String canTag;
	
	private String canPathTag (String title) {
		if (title.equals("General Prologue")) {
			canTag = "p";
			return "p";
		} else if (title.equals("The Knight")) {
			canTag = "kni";
			return "kni";
		} else if (title.equals("Knight's Tale")) {
			canTag = "kni-body";
			return null;
		} else if (title.equals("The Miller")) {
			canTag = "mil";
			return "mil";
		} else if (title.equals("Miller's Prologue")) {
			canTag = "mil-p";
			return "p";
		} else if (title.equals("Miller's Tale")) {
			canTag = "mil-body";
			return null;
		} else if (title.equals("The Reeve")) {
			canTag = "rev";
			return "rev";
		} else if (title.equals("Reeve's Prologue")) {
			canTag = "rev-p";
			return "p";
		} else if (title.equals("Reeve's Tale")) {
			canTag = "rev-body";
			return null;
		} else if (title.equals("The Cook")) {
			canTag = "cok";
			return "cok";
		} else if (title.equals("Cook's Prologue")) {
			canTag = "cok-p";
			return "p";
		} else if (title.equals("Cook's Tale")) {
			canTag = "cok-body";
			return null;
		} else if (title.equals("The Man of Law")) {
			canTag = "law";
			return "law";
		} else if (title.equals("Introduction to the Man of Law's Tale")) {
			canTag = "law-i";
			return "i";
		} else if (title.equals("Man of Law's Tale")) {
			canTag = "law-body";
			return null;
		} else if (title.equals("Epilogue to the Man of Law's Tale")) {
			canTag = "law-e";
			return "e";
		} else if (title.equals("The Wife of Bath")) {
			canTag = "wob";
			return "wob";
		} else if (title.equals("Wife of Bath's Prologue")) {
			canTag = "wob-p";
			return "p";
		} else if (title.equals("Wife of Bath's Tale")) {
			canTag = "wob-body";
			return null;
		} else if (title.equals("The Friar")) {
			canTag = "fri";
			return "fri";
		} else if (title.equals("Friar's Prologue")) {
			canTag = "fri-p";
			return "p";
		} else if (title.equals("Friar's Tale")) {
			canTag = "fri-body";
			return null;
		} else if (title.equals("The Summoner")) {
			canTag = "sum";
			return "sum";
		} else if (title.equals("Summoner's Prologue")) {
			canTag = "sum-p";
			return "p";
		} else if (title.equals("Summoner's Tale")) {
			canTag = "sum-body";
			return null;
		} else if (title.equals("The Clerk")) {
			canTag = "cle";
			return "cle";
		} else if (title.equals("Clerk's Prologue")) {
			canTag = "cle-p";
			return "p";
		} else if (title.equals("Clerk's Tale")) {
			canTag = "cle-body";
			return null;
		} else if (title.equals("The Merchant")) {
			canTag = "mer";
			return "mer";
		} else if (title.equals("Merchant's Prologue")) {
			canTag = "mer-p";
			return "p";
		} else if (title.equals("Merchant's Tale")) {
			canTag = "mer-body";
			return null;
		} else if (title.equals("Epilogue to the Merchant's Tale")) {
			canTag = "mer-e";
			return "e";
		} else if (title.equals("The Squire")) {
			canTag = "squ";
			return "squ";
		} else if (title.equals("Introduction to the Squire's Tale")) {
			canTag = "squ-i";
			return "i";
		} else if (title.equals("Squire's Tale")) {
			canTag = "squ-body";
			return null;
		} else if (title.equals("The Franklin")) {
			canTag = "fra";
			return "fra";
		} else if (title.equals("Prologue to the Franklin's Tale")) {
			canTag = "fra-p";
			return "p";
		} else if (title.equals("Franklin's Tale")) {
			canTag = "fra-body";
			return null;
		} else if (title.equals("The Physician")) {
			canTag = "phy";
			return "phy";
		} else if (title.equals("Physician's Tale")) {
			canTag = "phy-body";
			return null;
		} else if (title.equals("The Pardoner")) {
			canTag = "par";
			return "par";
		} else if (title.equals("Introduction to the Pardoner's Tale")) {
			canTag = "par-i";
			return "i";
		} else if (title.equals("Pardoner's Prologue")) {
			canTag = "par-p";
			return "p";
		} else if (title.equals("Pardoner's Tale")) {
			canTag = "par-body";
			return null;
		} else if (title.equals("The Shipman")) {
			canTag = "shi";
			return "shi";
		} else if (title.equals("Shipman's Tale")) {
			canTag = "shi-body";
			return null;
		} else if (title.equals("The Prioress")) {
			canTag = "pri";
			return "pri";
		} else if (title.equals("Prologue to the Prioress' Tale")) {
			canTag = "pri-p";
			return "p";
		} else if (title.equals("Prioress' Tale")) {
			canTag = "pri-body";
			return null;
		} else if (title.equals("Sir Thopas")) {
			canTag = "tho";
			return "tho";
		} else if (title.equals("Prologue to Sir Thopas")) {
			canTag = "tho-p";
			return "p";
		} else if (title.equals("Sir Thopas")) {
			canTag = "tho-body";
			return null;
		} else if (title.equals("Melibee")) {
			melibeeCount++;
			if (melibeeCount == 1) {
				canTag = "mel";
				return "mel";
			} else {
				canTag = "mel-body";
				return null;
			}
		} else if (title.equals("The Monk")) {
			canTag = "mon";
			return "mon";
		} else if (title.equals("Prologue to the Monk's Tale")) {
			canTag = "mon-p";
			return "p";
		} else if (title.equals("Monk's Tale")) {
			canTag = "mon-body";
			return null;
		} else if (title.equals("The Nun's Priest")) {
			canTag = "nun";
			return "nun";
		} else if (title.equals("Prologue to the Nun's Priest's Tale")) {
			canTag = "nun-p";
			return "p";
		} else if (title.equals("Nun's Priest's Tale")) {
			canTag = "nun-body";
			return null;
		} else if (title.equals("Epilogue to the Nun's Priest's Tale")) {
			canTag = "nun-e";
			return "e";
		} else if (title.equals("The Second Nun")) {
			canTag = "sec";
			return "sec";
		} else if (title.equals("Prologue to the Second Nun's Tale")) {
			canTag = "sec-p";
			return "p";
		} else if (title.equals("Second Nun's Tale")) {
			canTag = "sec-body";
			return null;
		} else if (title.equals("The Canon's Yeoman")) {
			canTag = "can";
			return "can";
		} else if (title.equals("Prologue to the Canon's Yeoman's Tale")) {
			canTag = "can-p";
			return "p";
		} else if (title.equals("Canon's Yeoman's Tale")) {
			canTag = "can-body";
			return null;
		} else if (title.equals("The Manciple")) {
			canTag = "man";
			return "man";
		} else if (title.equals("Prologue to the Manciple's Tale")) {
			canTag = "man-p";
			return "p";
		} else if (title.equals("Manciple's Tale")) {
			canTag = "man-body";
			return null;
		} else if (title.equals("The Parson")) {
			canTag = "par";
			return "par";
		} else if (title.equals("Prologue to the Parson's Tale")) {
			canTag = "par-p";
			return "p";
		} else if (title.equals("Parson's Tale")) {
			canTag = "par-body";
			return null;
		}
		return null;
	}
	
	/**	Gets a path tag for The Legend of Good Women.
	 *
	 *	@param	title		Part title.
	 *
	 *	@return				Path tag.
	 */
	
	private String lgwPathTag (String title) {
		if (title.startsWith("Prologue F")) {
			return "f";
		} else if (title.startsWith("Prologue G")) {
			return "g";
		} else if (title.equals("The Legend of Hypsipyle and Medea")) {
			return "hyps";
		} else if (title.equals("The Legend of Hypermnestra")) {
			return "hype";
		} else if (title.startsWith("The Legend of")) {
			return title.substring(14,17).toLowerCase();
		}
		return null;
	}
	
	/**	Gets a path tag for Short Poems.
	 *
	 *	@param	title		Part title.
	 *
	 *	@return				Path tag.
	 */
	
	private String poePathTag (String title) {
		if (title.equals("ABC")) {
			return "abc";
		} else if (title.equals("Complaint unto Pity")) {
			return "cup";
		} else if (title.equals("Complaint to His Lady")) {
			return "chl";
		} else if (title.equals("Complaint of Mars")) {
			return "com";
		} else if (title.equals("Complaint of Venus")) {
			return "cov";
		} else if (title.equals("To Rosemounde")) {
			return "ros";
		} else if (title.equals("Womanly Noblesse")) {
			return "wom";
		} else if (title.equals("Adam Scrivener")) {
			return "ads";
		} else if (title.equals("Former Age")) {
			return "age";
		} else if (title.equals("Fortune")) {
			return "for";
		} else if (title.equals("Truth")) {
			return "tru";
		} else if (title.equals("Gentilesse")) {
			return "gen";
		} else if (title.equals("Lak of Stedfastnesse")) {
			return "los";
		} else if (title.equals("Lenvoy de Chaucer a Scogan")) {
			return "lcs";
		} else if (title.equals("Lenvoy de Chaucer a Bukton")) {
			return "lcb";
		} else if (title.equals("Complaint of Chaucer to his Purse")) {
			return "ccp";
		} else if (title.equals("Proverbs")) {
			return "pro";
		} else if (title.equals("Against Women Unconstant (Apocrypha)")) {
			return "awu";
		} else if (title.equals("Merciles Beaute (Beauty)")) {
			return "meb";
		} else if (title.equals("Complaynt D'Amours (Complaint) (Apocrypha)")) {
			return "cda";
		} else if (title.equals("Balade of Complaint (Apocrypha)")) {
			return "boc";
		}
		return null;
	}
	
	/**	Gets a path tag for The Romaunt of the Rose.
	 *
	 *	@param	title		Part title.
	 *
	 *	@return				Path tag.
	 */
	
	private String rorPathTag (String title) {
		return title.substring(9,10).toLowerCase();
	}
	
	/**	Gets a path tag for Troilus and Criseyde.
	 *
	 *	@param	title		Part title.
	 *
	 *	@return				Path tag.
	 */
	
	private String troPathTag (String title) {
		return title.substring(5);
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

