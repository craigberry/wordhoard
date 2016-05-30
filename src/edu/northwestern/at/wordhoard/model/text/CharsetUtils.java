package edu.northwestern.at.wordhoard.model.text;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.text.*;

/**	Character set utilities.
 */
 
public class CharsetUtils {

	/**	Greek sigma unicode character. */
	
	private static final char SIGMA = (char)0x03c3;

	/**	Greek terminal sigma unicode character. */
	
	private static final char TERM_SIGMA = (char)0x03c2;
	
	/**	Bad (unknown) beta code sequence from most recent call to
	 *	translateBetaToUni, or null if none.
	 */
	 
	private static String badBetaSeq;
	
	/**	Array of beta code character types.
	 *
	 *	<ul>
	 *	<li>0 = unknown.
	 *	<li>1 = letter (upper or lower case).
	 *	<li>2 = beta code accent character - can be part of a multiple
	 *		character beta code sequence.
	 *	<li>3 = single character beta code sequence
	 *	</ul>
	 */
	
	private static byte[] charTypes = new byte[0x80];
	
	static {
		for (int i = 'a'; i <= 'z'; i++) charTypes[i] = 1;
		for (int i = 'A'; i <= 'A'; i++) charTypes[i] = 1;
		charTypes['|'] = 2;		// iota subscript
		charTypes['\''] = 3;	// apostrophe
		charTypes['*'] = 2;		// uppercase
		charTypes['-'] = 3;		// hyphen
		charTypes[','] = 3;		// comma
		charTypes['.'] = 3;		// period
		charTypes[':'] = 3;		// raised dot
		charTypes[';'] = 3;		// question mark
		charTypes['_'] = 3;		// dash
		charTypes['?'] = 2;		// subscript dot
		charTypes[')'] = 2;		// smooth breathing
		charTypes['('] = 2;		// rough breathing
		charTypes['/'] = 2;		// acute accent
		charTypes['\\'] = 2;	// grave accent
		charTypes['='] = 2;		// circumflex accent
		charTypes['+'] = 2;		// diaeresis
		charTypes['<'] = 2;		// lower prime
		charTypes['>'] = 2;		// upper prime
		charTypes['@'] = 2;		// qoppa
		charTypes['#'] = 2;		// sampi
		charTypes['!'] = 2;		// obscure letter
	}
	
	/**	A unicode character wrapped in an object. */
	
	private static class Uni {
		private char c;
		private Uni (char c) {
			this.c = c;
		}
		private Uni (int c) {
			this.c = (char)c;
		}
	}
	
	/**	Map from beta code sequences to unicode characters. */
	
	private static HashMap betaToUniMap = new HashMap();
	
	/**	Array mapping Unicode basic Latin and Latin-1 supplement characters
	 *	to their beta code sequences.
	 */
	
	private static String[] basicUniToBeta =
		new String[0x0100];
	
	/**	Array mapping Unicode Greek and Coptic characters
	 *	to their beta code sequences.
	 */
	
	private static String[] greekAndCopticUniToBeta =
		new String[0x0400-0x0370];
	
	/**	Array mapping Unicode Greek extended characters
	 *	to their beta code sequences.
	 */
	
	private static String[] greekExtendedUniToBeta =
		new String[0x2000-0x1f00];	
	
	static {
	
		// Punctuation.
		
		initBetaCode(" ",      (int)' ');
		initBetaCode("\"",     (int)'"');
		initBetaCode("'",      (int)'\'');
		initBetaCode(",",      (int)',');
		initBetaCode(".",      (int)'.');
		initBetaCode(":",      0x00b7);	// raised dot
		initBetaCode(";",      (int)';');
		initBetaCode("[",      (int)'[');
		initBetaCode("]",      (int)']');
		
		//	Alpha.
		
		initBetaCode("*a",     0x0391);
		initBetaCode("a",      0x03b1);
		initBetaCode("a)",     0x1f00);
		initBetaCode("a(",     0x1f01);
		initBetaCode("a)\\",   0x1f02);
		initBetaCode("a(\\",   0x1f03);
		initBetaCode("a)/",    0x1f04);
		initBetaCode("a(/",    0x1f05);
		initBetaCode("a)=",    0x1f06);
		initBetaCode("a(=",    0x1f07);
		initBetaCode("*)a",    0x1f08);
		initBetaCode("*(a",    0x1f09);
		initBetaCode("*)\\a",  0x1f0a);
		initBetaCode("*(\\a",  0x1f0b);
		initBetaCode("*)/a",   0x1f0c);
		initBetaCode("*(/a",   0x1f0d);
		initBetaCode("*)=a",   0x1f0e);
		initBetaCode("*(=a",   0x1f0f);
		initBetaCode("a\\",    0x1f70);
		initBetaCode("a/",     0x1f71);
		initBetaCode("a)|",    0x1f80);
		initBetaCode("a(|",    0x1f81);
		initBetaCode("a)\\|",  0x1f82);
		initBetaCode("a(\\|",  0x1f83);
		initBetaCode("a)/|",   0x1f84);
		initBetaCode("a(/|",   0x1f85);
		initBetaCode("a)=|",   0x1f86);
		initBetaCode("a(=|",   0x1f87);
		initBetaCode("*)|a",   0x1f88);
		initBetaCode("*(|a",   0x1f89);
		initBetaCode("*)\\|a", 0x1f8a);
		initBetaCode("*(\\|a", 0x1f8b);
		initBetaCode("*)/|a",  0x1f8c);
		initBetaCode("*(/|a",  0x1f8d);
		initBetaCode("*)=|a",  0x1f8e);
		initBetaCode("*(=|a",  0x1f8f);
		initBetaCode("a\\|",   0x1fb2);
		initBetaCode("a|",     0x1fb3);
		initBetaCode("a/|",    0x1fb4);
		initBetaCode("a=",     0x1fb6);
		initBetaCode("a=|",    0x1fb7);
		initBetaCode("*\\a",   0x1fba);
		initBetaCode("*/a",    0x1fbb);
		initBetaCode("*|a",    0x1fbc);
		
		//	Beta.
		
		initBetaCode("*b",     0x0392);
		initBetaCode("b",      0x03b2);
		
		//	Xi.
		
		initBetaCode("*c",     0x039e);
		initBetaCode("c",      0x03be);
		
		//	Delta.
		
		initBetaCode("*d",     0x0394);
		initBetaCode("d",      0x03b4);
		
		//	Epsilon.
		
		initBetaCode("*e",     0x0395);
		initBetaCode("e",      0x03b5);
		initBetaCode("e)",     0x1f10);
		initBetaCode("e(",     0x1f11);
		initBetaCode("e)\\",   0x1f12);
		initBetaCode("e(\\",   0x1f13);
		initBetaCode("e)/",    0x1f14);
		initBetaCode("e(/",    0x1f15);
		initBetaCode("*)e",    0x1f18);
		initBetaCode("*(e",    0x1f19);
		initBetaCode("*)\\e",  0x1f1a);
		initBetaCode("*(\\e",  0x1f1b);
		initBetaCode("*)/e",   0x1f1c);
		initBetaCode("*(/e",   0x1f1d);
		initBetaCode("e\\",    0x1f72);
		initBetaCode("e/",     0x1f73);
		initBetaCode("*\\e",   0x1fc8);
		initBetaCode("*/e",    0x1fc9);
		
		//	Phi.
		
		initBetaCode("*f",     0x03a6);
		initBetaCode("f",      0x03c6);
		
		//	Gamma.
		
		initBetaCode("*g",     0x0393);
		initBetaCode("g",      0x03b3);
		
		//	Eta.
		
		initBetaCode("*h",     0x0397);
		initBetaCode("h",      0x03b7);
		initBetaCode("h)",     0x1f20);
		initBetaCode("h(",     0x1f21);
		initBetaCode("h)\\",   0x1f22);
		initBetaCode("h(\\",   0x1f23);
		initBetaCode("h)/",    0x1f24);
		initBetaCode("h(/",    0x1f25);
		initBetaCode("h)=",    0x1f26);
		initBetaCode("h(=",    0x1f27);
		initBetaCode("*)h",    0x1f28);
		initBetaCode("*(h",    0x1f29);
		initBetaCode("*)\\h",  0x1f2a);
		initBetaCode("*(\\h",  0x1f2b);
		initBetaCode("*)/h",   0x1f2c);
		initBetaCode("*(/h",   0x1f2d);
		initBetaCode("*)=h",   0x1f2e);
		initBetaCode("*(=h",   0x1f2f);
		initBetaCode("h\\",    0x1f74);
		initBetaCode("h/",     0x1f75);
		initBetaCode("h)|",    0x1f90);
		initBetaCode("h(|",    0x1f91);
		initBetaCode("h)\\|",  0x1f92);
		initBetaCode("h(\\|",  0x1f93);
		initBetaCode("h)/|",   0x1f94);
		initBetaCode("h(/|",   0x1f95);
		initBetaCode("h)=|",   0x1f96);
		initBetaCode("h(=|",   0x1f97);
		initBetaCode("*)|h",   0x1f98);
		initBetaCode("*(|h",   0x1f99);
		initBetaCode("*)\\|h", 0x1f9a);
		initBetaCode("*(\\|h", 0x1f9b);
		initBetaCode("*)/|h",  0x1f9c);
		initBetaCode("*(/|h",  0x1f9d);
		initBetaCode("*)=|h",  0x1f9e);
		initBetaCode("*(=|h",  0x1f9f);
		initBetaCode("h\\|",   0x1fc2);
		initBetaCode("h|",     0x1fc3);
		initBetaCode("h/|",    0x1fc4);
		initBetaCode("h=",     0x1fc6);
		initBetaCode("h=|",    0x1fc7);
		initBetaCode("*\\h",   0x1fca);
		initBetaCode("*/h",    0x1fcb);
		initBetaCode("*|h",    0x1fcc);
		
		//	Iota.
		
		initBetaCode("*/+i",   0x0390);
		initBetaCode("*i",     0x0399);
		initBetaCode("i",      0x03b9);
		initBetaCode("*i+",    0x03aa);
		initBetaCode("i+",     0x03ca);
		initBetaCode("i)",     0x1f30);
		initBetaCode("i(",     0x1f31);
		initBetaCode("i)\\",   0x1f32);
		initBetaCode("i(\\",   0x1f33);
		initBetaCode("i)/",    0x1f34);
		initBetaCode("i(/",    0x1f35);
		initBetaCode("i)=",    0x1f36);
		initBetaCode("i(=",    0x1f37);
		initBetaCode("*)i",    0x1f38);
		initBetaCode("*(i",    0x1f39);
		initBetaCode("*)\\i",  0x1f3a);
		initBetaCode("*(\\i",  0x1f3b);
		initBetaCode("*)/i",   0x1f3c);
		initBetaCode("*(/i",   0x1f3d);
		initBetaCode("*)=i",   0x1f3e);
		initBetaCode("*(=i",   0x1f3f);
		initBetaCode("i\\",    0x1f76);
		initBetaCode("i/",     0x1f77);
		initBetaCode("i\\+",   0x1fd2);
		initBetaCode("i/+",    0x1fd3);
		initBetaCode("i=",     0x1fd6);
		initBetaCode("i=+",    0x1fd7);
		initBetaCode("*\\i",   0x1fda);
		initBetaCode("*/i",    0x1fdb);
		
		//	Terminal sigma.
		
		initBetaCode("j",      0x03c2);
		
		//	Kappa.
		
		initBetaCode("*k",     0x039a);
		initBetaCode("k",      0x03ba);
		
		//	Lambda.
		
		initBetaCode("*l",     0x039b);
		initBetaCode("l",      0x03bb);
		
		//	Mu.
		
		initBetaCode("*m",     0x039c);
		initBetaCode("m",      0x03bc);
		
		//	Nu.
		
		initBetaCode("*n",     0x039d);
		initBetaCode("n",      0x03bd);
		
		//	Omicron.
		
		initBetaCode("*o",     0x039f);
		initBetaCode("o",      0x03bf);
		initBetaCode("o)",     0x1f40);
		initBetaCode("o(",     0x1f41);
		initBetaCode("o)\\",   0x1f42);
		initBetaCode("o(\\",   0x1f43);
		initBetaCode("o)/",    0x1f44);
		initBetaCode("o(/",    0x1f45);
		initBetaCode("*)o",    0x1f48);
		initBetaCode("*(o",    0x1f49);
		initBetaCode("*)\\o",  0x1f4a);
		initBetaCode("*(\\o",  0x1f4b);
		initBetaCode("*)/o",   0x1f4c);
		initBetaCode("*(/o",   0x1f4d);
		initBetaCode("o\\",    0x1f78);
		initBetaCode("o/",     0x1f79);
		initBetaCode("*\\o",   0x1ff8);
		initBetaCode("*/o",    0x1ff9);
		
		//	Pi.
		
		initBetaCode("*p",     0x03a0);
		initBetaCode("p",      0x03c0);
		
		//	Theta.
		
		initBetaCode("*q",     0x0398);
		initBetaCode("q",      0x03b8);
		
		//	Rho.
		
		initBetaCode("*r",     0x03a1);
		initBetaCode("r",      0x03c1);
		initBetaCode("r)",     0x1fe4);
		initBetaCode("r(",     0x1fe5);
		initBetaCode("*(r",    0x1fec);
		
		//	Sigma.
		
		initBetaCode("*s",     0x03a3);
		initBetaCode("s",      0x03c3);
		
		//	Tau.
		
		initBetaCode("*t",     0x03a4);
		initBetaCode("t",      0x03c4);
		
		//	Upsilon.
		
		initBetaCode("*u",     0x03a5);
		initBetaCode("*u+",    0x03ab);
		initBetaCode("u",      0x03c5);
		initBetaCode("u+",     0x03cb);
		initBetaCode("u)",     0x1f50);
		initBetaCode("u(",     0x1f51);
		initBetaCode("u)\\",   0x1f52);
		initBetaCode("u(\\",   0x1f53);
		initBetaCode("u)/",    0x1f54);
		initBetaCode("u(/",    0x1f55);
		initBetaCode("u)=",    0x1f56);
		initBetaCode("u(=",    0x1f57);
		initBetaCode("*(u",    0x1f59);
		initBetaCode("*)/u",   0x1f5b);
		initBetaCode("*(/u",   0x1f5d);
		initBetaCode("*(=u",   0x1f5f);
		initBetaCode("u\\",    0x1f7a);
		initBetaCode("u/",     0x1f7b);
		initBetaCode("u\\+",   0x1fe2);
		initBetaCode("u/+",    0x1fe3);
		initBetaCode("u=",     0x1fe6);
		initBetaCode("u=+",    0x1fe7);
		initBetaCode("*\\u",   0x1fea);
		initBetaCode("*/u",    0x1feb);
		
		//	Omega.
		
		initBetaCode("*w",     0x03a9);
		initBetaCode("w",      0x03c9);
		initBetaCode("w)",     0x1f60);
		initBetaCode("w(",     0x1f61);
		initBetaCode("w)\\",   0x1f62);
		initBetaCode("w(\\",   0x1f63);
		initBetaCode("w)/",    0x1f64);
		initBetaCode("w(/",    0x1f65);
		initBetaCode("w(=",    0x1f66);
		initBetaCode("w)=",    0x1f67);
		initBetaCode("*)w",    0x1f68);
		initBetaCode("*(w",    0x1f69);
		initBetaCode("*(\\w",  0x1f6a);
		initBetaCode("*)\\w",  0x1f6b);
		initBetaCode("*)/w",   0x1f6c);
		initBetaCode("*(/w",   0x1f6d);
		initBetaCode("*)=w",   0x1f6e);
		initBetaCode("*(=w",   0x1f6f);
		initBetaCode("w\\",    0x1f7c);
		initBetaCode("w/",     0x1f7d);
		initBetaCode("w)|",    0x1fa0);
		initBetaCode("w(|",    0x1fa1);
		initBetaCode("w)\\|",  0x1fa2);
		initBetaCode("w(\\|",  0x1fa3);
		initBetaCode("w)/|",   0x1fa4);
		initBetaCode("w(/|",   0x1fa5);
		initBetaCode("w)=|",   0x1fa6);
		initBetaCode("w(=|",   0x1fa7);
		initBetaCode("*)|w",   0x1fa8);
		initBetaCode("*(|w",   0x1fa9);
		initBetaCode("*)\\|w", 0x1faa);
		initBetaCode("*(\\|w", 0x1fab);
		initBetaCode("*)/|w",  0x1fac);
		initBetaCode("*(/|w",  0x1fad);
		initBetaCode("*)=|w",  0x1fae);
		initBetaCode("*(=|w",  0x1faf);
		initBetaCode("w\\|",   0x1ff2);
		initBetaCode("w|",     0x1ff3);
		initBetaCode("w/|",    0x1ff4);
		initBetaCode("w=",     0x1ff6);
		initBetaCode("w=|",    0x1ff7);
		initBetaCode("*\\w",   0x1ffa);
		initBetaCode("*/w",    0x1ffb);
		initBetaCode("*|w",    0x1ffc);
		
		//	Chi.
		
		initBetaCode("*x",     0x03a7);
		initBetaCode("x",      0x03c7);
		
		//	Psi.
		
		initBetaCode("*y",     0x03a8);
		initBetaCode("y",      0x03c8);
		
		//	Zeta.
		
		initBetaCode("*z",     0x0396);
		initBetaCode("z",      0x03b6);
	}
		
	/**	Initializes a beta code to unicode mapping.
	 *
	 *	@param	betaSeq		Beta code sequence.
	 *
	 *	@param	uni			Unicode character.
	 */
	 
	private static void initBetaCode (String betaSeq, int uni) {
		if (betaToUniMap.get(betaSeq) == null) {
			betaToUniMap.put(betaSeq, new Uni(uni));
		} else {
			System.out.println("Duplicate beta code mapping for " + betaSeq);
		}
		if (uni < 0x0100) {
			if (basicUniToBeta[uni] == null)
				basicUniToBeta[uni] = betaSeq;
		} else if (uni >= 0x0370 && uni < 0x0400) {
			int k = uni-0x0370;
			if (greekAndCopticUniToBeta[k] == null) {
				greekAndCopticUniToBeta[k] = betaSeq;
			} else {
				System.out.println("Duplicate beta code for unicode " + 
					Integer.toString(uni,16));
			}
		} else if (uni >= 0x1f00 && uni < 0x2000) {
			int k = uni-0x1f00;
			if (greekExtendedUniToBeta[k] == null) {
				greekExtendedUniToBeta[k] = betaSeq;
			} else {
				System.out.println("Duplicate beta code for unicode " + 
					Integer.toString(uni,16));
			}
		} else {
			System.out.println("Bad unicode: " + 
				Integer.toString(uni,16));
		}
	}
	
	/**	Translates a beta code string to unicode.
	 *
	 *	@param	str		Beta code string.
	 *
	 *	@return			Unicode string.
	 */
	 
	public static String translateBetaToUni (String str) {
		badBetaSeq = null;
		char[] in = str.toCharArray();
		char[] out = new char[in.length];
		int inLen = in.length;
		int inPos = 0;
		int outPos = 0;
		while (inPos < inLen) {
			int inSeqStart = inPos;
			char c = in[inPos++];
			if (c < 0 || c >= 0x80) {
				if (badBetaSeq == null)
					badBetaSeq = "0x" + Integer.toString(c, 16);
				inPos++;
				continue;
			}
			int type = charTypes[c];
			boolean haveLetter = type == 1;
			if (type == 1 || type == 2) {
				while (inPos < inLen) {
					c = in[inPos];
					if (c < 0 || c >= 0x080) {
						if (badBetaSeq == null)
							badBetaSeq = "0x" + Integer.toString(c, 16);
						inPos++;
						continue;
					}
					type = charTypes[c];
					if (type == 0 || type == 3 || c == '*') break;
					if (type == 1) {
						if (haveLetter) break;
						haveLetter = true;
					}
					inPos++;
				}
			}
			String seq = new String(in, inSeqStart, inPos-inSeqStart);
			Uni uni = (Uni)betaToUniMap.get(seq);
			if (uni == null) {
				if (badBetaSeq == null) badBetaSeq = seq;
			} else {
				c = uni.c;
				if (c == SIGMA) {
					if (inPos == inLen) {
						c = TERM_SIGMA;
					} else {
						char d = in[inPos];
						if ((d < 'a' || d > 'z') && d != '\'')
							c = TERM_SIGMA;
					}
				}
				out[outPos++] = c;
			}
		}
		return new String(out, 0, outPos);
	}
	
	/**	Gets the bad beta code sequence.
	 *
	 *	@return		Bad (unknown) beta code sequence from most recent call
	 *				to translateBetaToUni, or null if none.
	 */
	
	public static String getBadBetaSeq () {
		return badBetaSeq;
	}
	
	/**	Translates a unicode string to beta code. 
	 *
	 *	@param	str		Unicode string.
	 *
	 *	@return			Beta code string.
	 */
	 
	public static String translateUniToBeta (String str) {
		char[] in = str.toCharArray();
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < in.length; i++) {
			int uni = (int)in[i];
			if (uni >= 0x0370 && uni < 0x0400) {
				int k = uni-0x0370;
				String betaSeq = greekAndCopticUniToBeta[k];
				if (betaSeq != null) buf.append(betaSeq);
			} else if (uni >= 0x1f00 && uni < 0x2000) {
				int k = uni-0x1f00;
				String betaSeq = greekExtendedUniToBeta[k];
				if (betaSeq != null) buf.append(betaSeq);
			}
		}
		return buf.toString();
	}
	
	/**	Array of collators indexed by character set and strength. */
	
	private static Collator[][] collators = new Collator[2][3];
	
	static {
		initCollator(TextParams.ROMAN, Collator.PRIMARY);			
		initCollator(TextParams.ROMAN, Collator.SECONDARY);			
		initCollator(TextParams.ROMAN, Collator.TERTIARY);			
		initCollator(TextParams.GREEK, Collator.PRIMARY);			
		initCollator(TextParams.GREEK, Collator.SECONDARY);			
		initCollator(TextParams.GREEK, Collator.TERTIARY);			
	}
	
	/**	Initializes a collator.
	 *
	 *	@param	charset		Character set.
	 *
	 *	@param	strength	Strength.
	 */
	 
	private static void initCollator (byte charset, int strength) {
		Locale locale = charset == TextParams.ROMAN ?
			Locale.ENGLISH : new Locale("el");
		Collator collator = Collator.getInstance(locale);
		collator.setStrength(strength);
		collators[charset][strength] = collator;
	}
	
	/**	Gets a collator.
	 *
	 *	<p>The character sets are:
	 *
	 *	<ul>
	 *	<li>TextParams.ROMAN: Roman.
	 *	<li>TextParams.GREEK: Polytonic greek.
	 *	</ul>
	 *
	 *	<p>The collation strengths are:
	 *	
	 *	<ul>
	 *	<li>Collator.PRIMARY: Case and diacritical insensitive.
	 *	<li>Collator.SECONDARY: Case insensitive, diacritical sensitive.
	 *	<li>Collator.TERTIARY: Case and diacritical sensitive.
	 *	</ul>
	 *
	 *	@param	charset		Character set.
	 *
	 *	@param	strength	Strength.
	 *
	 *	@return		Collator.
	 */
	 
	public static Collator getCollator (byte charset, int strength) {
		return collators[charset][strength];
	}
	
	/**	Array mapping Unicode basic Latin and Latin-1 supplement characters
	 *	to their case and diacritical insensitive equivalents.
	 */
	
	private static int[] basicInsensitiveTranslationTable =
		new int[0x0180];
		
	static {
		for (int i = 0; i < 0x0180; i++)
			basicInsensitiveTranslationTable[i] = i;
		for (int i = 0x0041; i <= 0x005a; i++)
			basicInsensitiveTranslationTable[i] = i + 0x0020;
		for (int i = 0x00c0; i <= 0x00c5; i++)
			basicInsensitiveTranslationTable[i] = (int)'a';
		basicInsensitiveTranslationTable[0x00c6] = 0x00e6;
		basicInsensitiveTranslationTable[0x00c7] = (int)'c';
		for (int i = 0x00c8; i <= 0x00cb; i++)
			basicInsensitiveTranslationTable[i] = (int)'e';
		for (int i = 0x00cc; i <= 0x00cf; i++)
			basicInsensitiveTranslationTable[i] = (int)'i';
		basicInsensitiveTranslationTable[0x00d0] = (int)'d';
		basicInsensitiveTranslationTable[0x00d1] = (int)'n';
		for (int i = 0x00d2; i <= 0x00d6; i++)
			basicInsensitiveTranslationTable[i] = (int)'o';
		basicInsensitiveTranslationTable[0x00d8] = (int)'o';
		for (int i = 0x00d9; i <= 0x00dc; i++)
			basicInsensitiveTranslationTable[i] = (int)'u';
		basicInsensitiveTranslationTable[0x00dd] = (int)'y';
		for (int i = 0x00e0; i <= 0x00e5; i++)
			basicInsensitiveTranslationTable[i] = (int)'a';
		basicInsensitiveTranslationTable[0x00e7] = (int)'c';
		for (int i = 0x00e8; i <= 0x00eb; i++)
			basicInsensitiveTranslationTable[i] = (int)'e';
		for (int i = 0x00ec; i <= 0x00ef; i++)
			basicInsensitiveTranslationTable[i] = (int)'i';
		basicInsensitiveTranslationTable[0x00f0] = (int)'d';
		basicInsensitiveTranslationTable[0x00f1] = (int)'n';
		for (int i = 0x00f2; i <= 0x00f6; i++)
			basicInsensitiveTranslationTable[i] = (int)'o';
		basicInsensitiveTranslationTable[0x00f8] = (int)'o';
		for (int i = 0x00f9; i <= 0x00fc; i++)
			basicInsensitiveTranslationTable[i] = (int)'u';
		basicInsensitiveTranslationTable[0x00fd] = (int)'y';
		basicInsensitiveTranslationTable[0x00fe] = 0x00fe;
		basicInsensitiveTranslationTable[0x00ff] = (int)'y';
		basicInsensitiveTranslationTable[0x0112] = (int)'e';
		basicInsensitiveTranslationTable[0x0113] = (int)'e';
		basicInsensitiveTranslationTable[0x014c] = (int)'o';
		basicInsensitiveTranslationTable[0x014d] = (int)'o';
	}
	
	/**	Array mapping Unicode Greek and Coptic characters
	 *	to their case and diacritical insensitive equivalents.
	 */
	
	private static int[] greekAndCopticInsensitiveTranslationTable =
		new int[0x0400-0x0370];
		
	static {
		for (int i = 0x0370; i < 0x0400; i++) 
			greekAndCopticInsensitiveTranslationTable[i-0x0370] = i;
		greekAndCopticInsensitiveTranslationTable[0x0386-0x0370] = 0x03b1;
		greekAndCopticInsensitiveTranslationTable[0x0388-0x0370] = 0x03b5;
		greekAndCopticInsensitiveTranslationTable[0x0389-0x0370] = 0x03b7;
		greekAndCopticInsensitiveTranslationTable[0x038a-0x0370] = 0x03b9;
		greekAndCopticInsensitiveTranslationTable[0x038c-0x0370] = 0x03bf;
		greekAndCopticInsensitiveTranslationTable[0x038e-0x0370] = 0x03c5;
		greekAndCopticInsensitiveTranslationTable[0x038f-0x0370] = 0x03c9;
		greekAndCopticInsensitiveTranslationTable[0x0390-0x0370] = 0x03b9;
		for (int i = 0x0391; i <= 0x03a9; i++)
			if (i != 0x03a2)
				greekAndCopticInsensitiveTranslationTable[i-0x370] = i + 0x0020;
		greekAndCopticInsensitiveTranslationTable[0x03aa-0x0370] = 0x03b9;
		greekAndCopticInsensitiveTranslationTable[0x03ab-0x0370] = 0x03c5;
		greekAndCopticInsensitiveTranslationTable[0x03ac-0x0370] = 0x03b1;
		greekAndCopticInsensitiveTranslationTable[0x03ad-0x0370] = 0x03b5;
		greekAndCopticInsensitiveTranslationTable[0x03ae-0x0370] = 0x03b7;
		greekAndCopticInsensitiveTranslationTable[0x03af-0x0370] = 0x03b9;
		greekAndCopticInsensitiveTranslationTable[0x03b0-0x0370] = 0x03c5;
		// Mapping from terminal sigma to sigma removed at Martin's request.
		//greekAndCopticInsensitiveTranslationTable[0x03c2-0x0370] = 0x03c3;
		greekAndCopticInsensitiveTranslationTable[0x03ca-0x0370] = 0x03b9;
		greekAndCopticInsensitiveTranslationTable[0x03cb-0x0370] = 0x03c5;
		greekAndCopticInsensitiveTranslationTable[0x03cc-0x0370] = 0x03bf;
		greekAndCopticInsensitiveTranslationTable[0x03cd-0x0370] = 0x03c5;
		greekAndCopticInsensitiveTranslationTable[0x03ce-0x0370] = 0x03c9;
		greekAndCopticInsensitiveTranslationTable[0x03d0-0x0370] = 0x03b2;
		greekAndCopticInsensitiveTranslationTable[0x03d1-0x0370] = 0x03b8;
		for (int i = 0x03d2; i <= 0x03d4; i++) 
			greekAndCopticInsensitiveTranslationTable[i-0x0370] = 0x03c5;
		greekAndCopticInsensitiveTranslationTable[0x03d5-0x0370] = 0x03c6;
		greekAndCopticInsensitiveTranslationTable[0x03d6-0x0370] = 0x03c0;
		greekAndCopticInsensitiveTranslationTable[0x03d8-0x0370] = 0x03d9;
		greekAndCopticInsensitiveTranslationTable[0x03da-0x0370] = 0x03db;
		greekAndCopticInsensitiveTranslationTable[0x03dc-0x0370] = 0x03dd;
		greekAndCopticInsensitiveTranslationTable[0x03de-0x0370] = 0x03df;
		greekAndCopticInsensitiveTranslationTable[0x03e0-0x0370] = 0x03e1;
		greekAndCopticInsensitiveTranslationTable[0x03f0-0x0370] = 0x03ba;
		greekAndCopticInsensitiveTranslationTable[0x03f1-0x0370] = 0x03c1;
		greekAndCopticInsensitiveTranslationTable[0x03f2-0x0370] = 0x03c3;
		greekAndCopticInsensitiveTranslationTable[0x03f4-0x0370] = 0x03b8;
		greekAndCopticInsensitiveTranslationTable[0x03f5-0x0370] = 0x03b5;
		greekAndCopticInsensitiveTranslationTable[0x03f7-0x0370] = 0x03f8;
		greekAndCopticInsensitiveTranslationTable[0x03f9-0x0370] = 0x03c3;
		greekAndCopticInsensitiveTranslationTable[0x03fa-0x0370] = 0x03fb;
	}
	
	/**	Array mapping Unicode Greek extended characters
	 *	to their case and diacritical insensitive equivalents.
	 */
	
	private static int[] greekExtendedInsensitiveTranslationTable =
		new int[0x2000-0x1f00];	
		
	static {
		for (int i = 0x1f00; i < 0x2000; i++) 
			greekExtendedInsensitiveTranslationTable[i-0x1f00] = i;
		for (int i = 0x1f00; i <= 0x1f0f; i++)
			greekExtendedInsensitiveTranslationTable[i-0x1f00] = 0x03b1;		
		for (int i = 0x1f10; i <= 0x1f15; i++)
			greekExtendedInsensitiveTranslationTable[i-0x1f00] = 0x03b5;		
		for (int i = 0x1f18; i <= 0x1f1d; i++)
			greekExtendedInsensitiveTranslationTable[i-0x1f00] = 0x03b5;		
		for (int i = 0x1f20; i <= 0x1f2f; i++)
			greekExtendedInsensitiveTranslationTable[i-0x1f00] = 0x03b7;		
		for (int i = 0x1f30; i <= 0x1f3f; i++)
			greekExtendedInsensitiveTranslationTable[i-0x1f00] = 0x03b9;		
		for (int i = 0x1f40; i <= 0x1f45; i++)
			greekExtendedInsensitiveTranslationTable[i-0x1f00] = 0x03bf;		
		for (int i = 0x1f48; i <= 0x1f4d; i++)
			greekExtendedInsensitiveTranslationTable[i-0x1f00] = 0x03bf;		
		for (int i = 0x1f50; i <= 0x1f57; i++)
			greekExtendedInsensitiveTranslationTable[i-0x1f00] = 0x03c5;		
		greekExtendedInsensitiveTranslationTable[0x1f59-0x1f00] = 0x03c5;
		greekExtendedInsensitiveTranslationTable[0x1f5b-0x1f00] = 0x03c5;
		greekExtendedInsensitiveTranslationTable[0x1f5d-0x1f00] = 0x03c5;
		greekExtendedInsensitiveTranslationTable[0x1f5f-0x1f00] = 0x03c5;
		for (int i = 0x1f60; i <= 0x1f6f; i++)
			greekExtendedInsensitiveTranslationTable[i-0x1f00] = 0x03c9;		
		greekExtendedInsensitiveTranslationTable[0x1f70-0x1f00] = 0x03b1;
		greekExtendedInsensitiveTranslationTable[0x1f71-0x1f00] = 0x03b1;
		greekExtendedInsensitiveTranslationTable[0x1f72-0x1f00] = 0x03b5;
		greekExtendedInsensitiveTranslationTable[0x1f73-0x1f00] = 0x03b5;
		greekExtendedInsensitiveTranslationTable[0x1f74-0x1f00] = 0x03b7;
		greekExtendedInsensitiveTranslationTable[0x1f75-0x1f00] = 0x03b7;
		greekExtendedInsensitiveTranslationTable[0x1f76-0x1f00] = 0x03b9;
		greekExtendedInsensitiveTranslationTable[0x1f77-0x1f00] = 0x03b9;
		greekExtendedInsensitiveTranslationTable[0x1f78-0x1f00] = 0x03bf;
		greekExtendedInsensitiveTranslationTable[0x1f79-0x1f00] = 0x03bf;
		greekExtendedInsensitiveTranslationTable[0x1f7a-0x1f00] = 0x03c5;
		greekExtendedInsensitiveTranslationTable[0x1f7b-0x1f00] = 0x03c5;
		greekExtendedInsensitiveTranslationTable[0x1f7c-0x1f00] = 0x03c9;
		greekExtendedInsensitiveTranslationTable[0x1f7d-0x1f00] = 0x03c9;
		for (int i = 0x1f80; i <= 0x1f8f; i++)
			greekExtendedInsensitiveTranslationTable[i-0x1f00] = 0x03b1;		
		for (int i = 0x1f90; i <= 0x1f9f; i++)
			greekExtendedInsensitiveTranslationTable[i-0x1f00] = 0x03b7;		
		for (int i = 0x1fa0; i <= 0x1faf; i++)
			greekExtendedInsensitiveTranslationTable[i-0x1f00] = 0x03c9;		
		for (int i = 0x1fb0; i <= 0x1fb4; i++)
			greekExtendedInsensitiveTranslationTable[i-0x1f00] = 0x03b1;		
		for (int i = 0x1fb6; i <= 0x1fbc; i++)
			greekExtendedInsensitiveTranslationTable[i-0x1f00] = 0x03b1;		
		for (int i = 0x1fc2; i <= 0x1fc4; i++)
			greekExtendedInsensitiveTranslationTable[i-0x1f00] = 0x03b7;		
		for (int i = 0x1fc6; i <= 0x1fc7; i++)
			greekExtendedInsensitiveTranslationTable[i-0x1f00] = 0x03b7;		
		for (int i = 0x1fc8; i <= 0x1fc9; i++)
			greekExtendedInsensitiveTranslationTable[i-0x1f00] = 0x03b5;		
		for (int i = 0x1fca; i <= 0x1fcc; i++)
			greekExtendedInsensitiveTranslationTable[i-0x1f00] = 0x03b7;		
		for (int i = 0x1fda; i <= 0x1fd3; i++)
			greekExtendedInsensitiveTranslationTable[i-0x1f00] = 0x03b9;		
		for (int i = 0x1fd6; i <= 0x1fdb; i++)
			greekExtendedInsensitiveTranslationTable[i-0x1f00] = 0x03b9;		
		for (int i = 0x1fe0; i <= 0x1fe3; i++)
			greekExtendedInsensitiveTranslationTable[i-0x1f00] = 0x03c5;		
		for (int i = 0x1fe4; i <= 0x1fe5; i++)
			greekExtendedInsensitiveTranslationTable[i-0x1f00] = 0x03c1;		
		for (int i = 0x1fe6; i <= 0x1feb; i++)
			greekExtendedInsensitiveTranslationTable[i-0x1f00] = 0x03c5;		
		greekExtendedInsensitiveTranslationTable[0x1fec-0x1f00] = 0x03c1;
		for (int i = 0x1ff2; i <= 0x1ff4; i++)
			greekExtendedInsensitiveTranslationTable[i-0x1f00] = 0x03c9;		
		for (int i = 0x1ff6; i <= 0x1ff7; i++)
			greekExtendedInsensitiveTranslationTable[i-0x1f00] = 0x03c9;		
		for (int i = 0x1ff8; i <= 0x1ff9; i++)
			greekExtendedInsensitiveTranslationTable[i-0x1f00] = 0x03bf;		
		for (int i = 0x1ffa; i <= 0x1ffc; i++)
			greekExtendedInsensitiveTranslationTable[i-0x1f00] = 0x03c9;		
	}
	
	/**	Translates a string to a case and diacritical insensitive version.
	 *
	 *	<p>All diacritical marks are removed and all letters are mapped to
	 *	lower case.
	 *
	 *	@param	str			String.
	 *
	 *	@return				Insensitive string.
	 */
	 
	public static String translateToInsensitive (String str) {
		char[] in = str.toCharArray();
		char[] out = new char[in.length];
		for (int i = 0; i < in.length; i++) {
			char c = in[i];
			if (c < 0x0180) {
				// Basic Latin, Latin-1 supplement, ad Latin Extended-A ranges.
				out[i] = (char)basicInsensitiveTranslationTable[c];
			} else if (c >= 0x0370 && c < 0x0400) {
				// Greek and Coptic range.
				out[i] = (char)greekAndCopticInsensitiveTranslationTable[c-0x0370];
			} else if (c >= 0x1f00 && c < 0x2000) {
				// Greek extended range.
				out[i] = (char)greekExtendedInsensitiveTranslationTable[c-0x1f00];
			} else {
				// Punt.
				out[i] = c;
			}
		}
		return new String(out);
	}
	
	/**	Array mapping Unicode Greek tonos accented vowels (in the Greek and
	 *	Coptic range) to oxia accented vowels (in the Greek extended range).
	 */
	
	private static int[] tonosToOxiaTranslationTable =
		new int[0x03cf-0x03ac];	
		
	static {
		for (int i = 0x03ac; i < 0x03cf; i++) 
			tonosToOxiaTranslationTable[i-0x03ac] = i;
		tonosToOxiaTranslationTable[0x03ac-0x03ac] = 0x1f71;
		tonosToOxiaTranslationTable[0x03ad-0x03ac] = 0x1f73;
		tonosToOxiaTranslationTable[0x03ae-0x03ac] = 0x1f75;
		tonosToOxiaTranslationTable[0x03af-0x03ac] = 0x1f77;
		tonosToOxiaTranslationTable[0x03cc-0x03ac] = 0x1f79;
		tonosToOxiaTranslationTable[0x03cd-0x03ac] = 0x1f7b;
		tonosToOxiaTranslationTable[0x03cd-0x03ac] = 0x1f7d;
	}
	
	/**	Translates tonos accents to oxia accents in a string.
	 *
	 *	<p>We use oxia accents on lower case vowels in the Greek Extended
	 *	Unicode range in the Early Greek Epic text and lemma spellings.
	 *	The tonos accents in the Greek and Coptic range are nearly 
	 *	indistinguishable visually and may be typed by users. For example, 
	 *	the Mac OS X Polytonic Greek input method results in tonos accents.
	 *	
	 *	<p>To prevent confusion, we convert tonos to oxia accents
	 *	in all strings typed by users before we attempt to do searches for
	 *	the strings in the WordHoard database.
	 *
	 *	@param	str		String with tonos accents.
	 *
	 *	@return			String with tonos accents translated to oxia accents.
	 */
	 
	public static String translateTonosToOxia (String str) {
		char[] in = str.toCharArray();
		char[] out = new char[in.length];
		for (int i = 0; i < in.length; i++) {
			char c = in[i];
			if (c >= 0x03ac && c < 0x03cf) {
				out[i] = (char)tonosToOxiaTranslationTable[c-0x03ac];
			} else {
				out[i] = c;
			}
		}
		return new String(out);
	}

	/**	Hides the default no-arg constructor.
	 */
	 
	private CharsetUtils () {
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

