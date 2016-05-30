package edu.northwestern.at.wordhoard.swing.text;

/*	Please see the license information at the end of this file. */

import java.util.*;

/**	Characters used in the text. 
 *
 *	<p>The character set tables are produced by the tool 
 *	{@link edu.northwestern.at.wordhoard.tools.GetCharsUsed
 *	GetCharsUsed}.
 */

public class CharsUsed {

	/**	Array of all the Roman characters used. */
	
	private static final char[] ROMAN_CHAR_ARRAY =
		new char[] {
            (char)0x0020,
            (char)0x0021,
            (char)0x0022,
            (char)0x0026,
            (char)0x0027,
            (char)0x0028,
            (char)0x0029,
            (char)0x002a,
            (char)0x002b,
            (char)0x002c,
            (char)0x002d,
            (char)0x002e,
            (char)0x002f,
            (char)0x0030,
            (char)0x0031,
            (char)0x0032,
            (char)0x0033,
            (char)0x0034,
            (char)0x0035,
            (char)0x0036,
            (char)0x0037,
            (char)0x0038,
            (char)0x0039,
            (char)0x003a,
            (char)0x003b,
            (char)0x003c,
            (char)0x003d,
            (char)0x003e,
            (char)0x003f,
            (char)0x0041,
            (char)0x0042,
            (char)0x0043,
            (char)0x0044,
            (char)0x0045,
            (char)0x0046,
            (char)0x0047,
            (char)0x0048,
            (char)0x0049,
            (char)0x004a,
            (char)0x004b,
            (char)0x004c,
            (char)0x004d,
            (char)0x004e,
            (char)0x004f,
            (char)0x0050,
            (char)0x0051,
            (char)0x0052,
            (char)0x0053,
            (char)0x0054,
            (char)0x0055,
            (char)0x0056,
            (char)0x0057,
            (char)0x0058,
            (char)0x0059,
            (char)0x005a,
            (char)0x005b,
            (char)0x005d,
            (char)0x0061,
            (char)0x0062,
            (char)0x0063,
            (char)0x0064,
            (char)0x0065,
            (char)0x0066,
            (char)0x0067,
            (char)0x0068,
            (char)0x0069,
            (char)0x006a,
            (char)0x006b,
            (char)0x006c,
            (char)0x006d,
            (char)0x006e,
            (char)0x006f,
            (char)0x0070,
            (char)0x0071,
            (char)0x0072,
            (char)0x0073,
            (char)0x0074,
            (char)0x0075,
            (char)0x0076,
            (char)0x0077,
            (char)0x0078,
            (char)0x0079,
            (char)0x007a,
            (char)0x007b,
            (char)0x007c,
            (char)0x007d,
            (char)0x00a9,
            (char)0x00c4,
            (char)0x00c6,
            (char)0x00c7,
            (char)0x00d6,
            (char)0x00dc,
            (char)0x00df,
            (char)0x00e0,
            (char)0x00e1,
            (char)0x00e4,
            (char)0x00e6,
            (char)0x00e7,
            (char)0x00e8,
            (char)0x00e9,
            (char)0x00ea,
            (char)0x00eb,
            (char)0x00ee,
            (char)0x00ef,
            (char)0x00f6,
            (char)0x00f9,
            (char)0x00fc,
            (char)0x0112,
            (char)0x0113,
            (char)0x012b,
            (char)0x014c,
            (char)0x014d,
            (char)0x0153,
            (char)0x016b,
            (char)0x016d,
            (char)0x1fb0,
            (char)0x2014,
            (char)0x2018,
            (char)0x2019,
            (char)0x201c,
            (char)0x201d,
            (char)0x201e,
            (char)0x2020,
		};

	/**	Array of all the Greek characters used. */
	
	private static final char[] GREEK_CHAR_ARRAY =
		new char[] {
            (char)0x0020,
            (char)0x0022,
            (char)0x0027,
            (char)0x0028,
            (char)0x0029,
            (char)0x002c,
            (char)0x002e,
            (char)0x0030,
            (char)0x0031,
            (char)0x0032,
            (char)0x0033,
            (char)0x0034,
            (char)0x0035,
            (char)0x0036,
            (char)0x0037,
            (char)0x0038,
            (char)0x0039,
            (char)0x003b,
            (char)0x005b,
            (char)0x005d,
            (char)0x0061,
            (char)0x0062,
            (char)0x0063,
            (char)0x0064,
            (char)0x0065,
            (char)0x0066,
            (char)0x0067,
            (char)0x0068,
            (char)0x0069,
            (char)0x006a,
            (char)0x006b,
            (char)0x006c,
            (char)0x006d,
            (char)0x006e,
            (char)0x006f,
            (char)0x0070,
            (char)0x0071,
            (char)0x0072,
            (char)0x0073,
            (char)0x0074,
            (char)0x0075,
            (char)0x0076,
            (char)0x0077,
            (char)0x0078,
            (char)0x0079,
            (char)0x007a,
            (char)0x00b7,
            (char)0x0391,
            (char)0x0392,
            (char)0x0393,
            (char)0x0394,
            (char)0x0395,
            (char)0x0396,
            (char)0x0397,
            (char)0x0398,
            (char)0x0399,
            (char)0x039a,
            (char)0x039b,
            (char)0x039c,
            (char)0x039d,
            (char)0x039e,
            (char)0x039f,
            (char)0x03a0,
            (char)0x03a1,
            (char)0x03a3,
            (char)0x03a4,
            (char)0x03a5,
            (char)0x03a6,
            (char)0x03a7,
            (char)0x03a8,
            (char)0x03a9,
            (char)0x03aa,
            (char)0x03b1,
            (char)0x03b2,
            (char)0x03b3,
            (char)0x03b4,
            (char)0x03b5,
            (char)0x03b6,
            (char)0x03b7,
            (char)0x03b8,
            (char)0x03b9,
            (char)0x03ba,
            (char)0x03bb,
            (char)0x03bc,
            (char)0x03bd,
            (char)0x03be,
            (char)0x03bf,
            (char)0x03c0,
            (char)0x03c1,
            (char)0x03c2,
            (char)0x03c3,
            (char)0x03c4,
            (char)0x03c5,
            (char)0x03c6,
            (char)0x03c7,
            (char)0x03c8,
            (char)0x03c9,
            (char)0x03ca,
            (char)0x03cb,
            (char)0x1f00,
            (char)0x1f01,
            (char)0x1f02,
            (char)0x1f03,
            (char)0x1f04,
            (char)0x1f05,
            (char)0x1f06,
            (char)0x1f07,
            (char)0x1f08,
            (char)0x1f09,
            (char)0x1f0c,
            (char)0x1f0d,
            (char)0x1f0e,
            (char)0x1f10,
            (char)0x1f11,
            (char)0x1f12,
            (char)0x1f13,
            (char)0x1f14,
            (char)0x1f15,
            (char)0x1f18,
            (char)0x1f19,
            (char)0x1f1c,
            (char)0x1f1d,
            (char)0x1f20,
            (char)0x1f21,
            (char)0x1f22,
            (char)0x1f23,
            (char)0x1f24,
            (char)0x1f25,
            (char)0x1f26,
            (char)0x1f27,
            (char)0x1f28,
            (char)0x1f29,
            (char)0x1f2c,
            (char)0x1f2d,
            (char)0x1f2e,
            (char)0x1f30,
            (char)0x1f31,
            (char)0x1f32,
            (char)0x1f33,
            (char)0x1f34,
            (char)0x1f35,
            (char)0x1f36,
            (char)0x1f37,
            (char)0x1f38,
            (char)0x1f39,
            (char)0x1f3c,
            (char)0x1f3d,
            (char)0x1f3e,
            (char)0x1f40,
            (char)0x1f41,
            (char)0x1f42,
            (char)0x1f43,
            (char)0x1f44,
            (char)0x1f45,
            (char)0x1f48,
            (char)0x1f49,
            (char)0x1f4c,
            (char)0x1f4d,
            (char)0x1f50,
            (char)0x1f51,
            (char)0x1f53,
            (char)0x1f54,
            (char)0x1f55,
            (char)0x1f56,
            (char)0x1f57,
            (char)0x1f59,
            (char)0x1f5d,
            (char)0x1f5f,
            (char)0x1f60,
            (char)0x1f61,
            (char)0x1f62,
            (char)0x1f63,
            (char)0x1f64,
            (char)0x1f65,
            (char)0x1f66,
            (char)0x1f67,
            (char)0x1f68,
            (char)0x1f6c,
            (char)0x1f6d,
            (char)0x1f6e,
            (char)0x1f6f,
            (char)0x1f70,
            (char)0x1f71,
            (char)0x1f72,
            (char)0x1f73,
            (char)0x1f74,
            (char)0x1f75,
            (char)0x1f76,
            (char)0x1f77,
            (char)0x1f78,
            (char)0x1f79,
            (char)0x1f7a,
            (char)0x1f7b,
            (char)0x1f7c,
            (char)0x1f7d,
            (char)0x1f80,
            (char)0x1f84,
            (char)0x1f86,
            (char)0x1f90,
            (char)0x1f91,
            (char)0x1f94,
            (char)0x1f95,
            (char)0x1f96,
            (char)0x1f97,
            (char)0x1fa0,
            (char)0x1fa4,
            (char)0x1fa6,
            (char)0x1fa7,
            (char)0x1fb3,
            (char)0x1fb4,
            (char)0x1fb6,
            (char)0x1fb7,
            (char)0x1fc2,
            (char)0x1fc3,
            (char)0x1fc4,
            (char)0x1fc6,
            (char)0x1fc7,
            (char)0x1fd2,
            (char)0x1fd3,
            (char)0x1fd6,
            (char)0x1fd7,
            (char)0x1fdb,
            (char)0x1fe2,
            (char)0x1fe3,
            (char)0x1fe5,
            (char)0x1fe6,
            (char)0x1fe7,
            (char)0x1feb,
            (char)0x1fec,
            (char)0x1ff3,
            (char)0x1ff4,
            (char)0x1ff6,
            (char)0x1ff7,
            (char)0x1ff9,
		};
		
	/**	A string containing all the Roman characters used. */
	
	public static final String ROMAN = new String(ROMAN_CHAR_ARRAY);
		
	/**	A string containing all the Greek characters used. */
	
	public static final String GREEK = new String(GREEK_CHAR_ARRAY);
	
	/**	Returns true if a char is used in Roman.
	 *
	 *	@param	uni		Unicode character code.
	 *
	 *	@return			True if used in Roman.
	 */
	
	public static boolean isUsedRoman (int uni) {
		return Arrays.binarySearch(ROMAN_CHAR_ARRAY, (char)uni) >= 0;
	}
	
	/**	Returns true if a char is used in Greek.
	 *
	 *	@param	uni		Unicode character code.
	 *
	 *	@return			True if used in Greek.
	 */
	
	public static boolean isUsedGreek (int uni) {
		return Arrays.binarySearch(GREEK_CHAR_ARRAY, (char)uni) >= 0;
	}
	
	/**	Hides the default no-arg constructor.
	 */
	 
	private CharsUsed () {
		throw new UnsupportedOperationException();
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

