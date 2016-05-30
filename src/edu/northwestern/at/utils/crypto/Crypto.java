package edu.northwestern.at.utils.crypto;

/*	Please see the license information at the end of this file. */

/** Cryptography utilities.
 *
 *	<p>This static class is a port of the old Ph encryption code from 
 *	C to Java.
 */

public class Crypto {

	private static final int IP[] = {
		58,50,42,34,26,18,10, 2,
		60,52,44,36,28,20,12, 4,
		62,54,46,38,30,22,14, 6,
		64,56,48,40,32,24,16, 8,
		57,49,41,33,25,17, 9, 1,
		59,51,43,35,27,19,11, 3,
		61,53,45,37,29,21,13, 5,
		63,55,47,39,31,23,15, 7,
	};
	
	private static final int FP[] = {
		40, 8,48,16,56,24,64,32,
		39, 7,47,15,55,23,63,31,
		38, 6,46,14,54,22,62,30,
		37, 5,45,13,53,21,61,29,
		36, 4,44,12,52,20,60,28,
		35, 3,43,11,51,19,59,27,
		34, 2,42,10,50,18,58,26,
		33, 1,41, 9,49,17,57,25,
	};

	private static final int PC1_C[] = {
		57,49,41,33,25,17, 9,
		 1,58,50,42,34,26,18,
		10, 2,59,51,43,35,27,
		19,11, 3,60,52,44,36,
	};

	private static final int PC1_D[] = {
		63,55,47,39,31,23,15,
		 7,62,54,46,38,30,22,
		14, 6,61,53,45,37,29,
		21,13, 5,28,20,12, 4,
	};

	private static final int shifts[] = {
		1,1,2,2,2,2,2,2,1,2,2,2,2,2,2,1,
	};

	private static final int PC2_C[] = {
		14,17,11,24, 1, 5,
		 3,28,15, 6,21,10,
		23,19,12, 4,26, 8,
		16, 7,27,20,13, 2,
	};

	private static final int PC2_D[] = {
		41,52,31,37,47,55,
		30,40,51,45,33,48,
		44,49,39,56,34,53,
		46,42,50,36,29,32,
	};
	
	private static int C[] = new int[28];
	
	private static int D[] = new int[28];
	
	private static int KS[][] = new int[16][48];
	
	private static int E[] = new int[48];
	
	private static final int e[] = {
		32, 1, 2, 3, 4, 5,
		 4, 5, 6, 7, 8, 9,
		 8, 9,10,11,12,13,
		12,13,14,15,16,17,
		16,17,18,19,20,21,
		20,21,22,23,24,25,
		24,25,26,27,28,29,
		28,29,30,31,32, 1,
	};
	
	private static void setkey (int[] key) {
		int i, j, k;
		int t;
		for (i = 0; i < 28; i++) {
			C[i] = key[PC1_C[i]-1];
			D[i] = key[PC1_D[i]-1];
		}
		for (i = 0; i < 16; i++) {
			for (k = 0; k < shifts[i]; k++) {
				t = C[0];
				for (j = 0; j < 28-1; j++)
					C[j] = C[j+1];
				C[27] = t;
				t = D[0];
				for (j = 0; j < 28-1; j++)
					D[j] = D[j+1];
				D[27] = t;
			}
			for (j = 0; j < 24; j++) {
				KS[i][j] = C[PC2_C[j]-1];
				KS[i][j+24] = D[PC2_D[j]-28-1];
			}
		}
		for (i = 0; i < 48; i++)
			E[i] = e[i];
	}
	
	private static final int S[][] = {
		{14, 4,13, 1, 2,15,11, 8, 3,10, 6,12, 5, 9, 0, 7,
		  0,15, 7, 4,14, 2,13, 1,10, 6,12,11, 9, 5, 3, 8,
		  4, 1,14, 8,13, 6, 2,11,15,12, 9, 7, 3,10, 5, 0,
		 15,12, 8, 2, 4, 9, 1, 7, 5,11, 3,14,10, 0, 6, 13},

		{15, 1, 8,14, 6,11, 3, 4, 9, 7, 2,13,12, 0, 5,10,
		  3,13, 4, 7,15, 2, 8,14,12, 0, 1,10, 6, 9,11, 5,
		  0,14, 7,11,10, 4,13, 1, 5, 8,12, 6, 9, 3, 2,15,
		 13, 8,10, 1, 3,15, 4, 2,11, 6, 7,12, 0, 5,14, 9},

		{10, 0, 9,14, 6, 3,15, 5, 1,13,12, 7,11, 4, 2, 8,
		 13, 7, 0, 9, 3, 4, 6,10, 2, 8, 5,14,12,11,15, 1,
		 13, 6, 4, 9, 8,15, 3, 0,11, 1, 2,12, 5,10,14, 7,
		  1,10,13, 0, 6, 9, 8, 7, 4,15,14, 3,11, 5, 2,12},

		{ 7,13,14, 3, 0, 6, 9,10, 1, 2, 8, 5,11,12, 4,15,
		 13, 8,11, 5, 6,15, 0, 3, 4, 7, 2,12, 1,10,14, 9,
		 10, 6, 9, 0,12,11, 7,13,15, 1, 3,14, 5, 2, 8, 4,
		  3,15, 0, 6,10, 1,13, 8, 9, 4, 5,11,12, 7, 2,14},

		{ 2,12, 4, 1, 7,10,11, 6, 8, 5, 3,15,13, 0,14, 9,
		 14,11, 2,12, 4, 7,13, 1, 5, 0,15,10, 3, 9, 8, 6,
		  4, 2, 1,11,10,13, 7, 8,15, 9,12, 5, 6, 3, 0,14,
		 11, 8,12, 7, 1,14, 2,13, 6,15, 0, 9,10, 4, 5, 3},

		{12, 1,10,15, 9, 2, 6, 8, 0,13, 3, 4,14, 7, 5,11,
		 10,15, 4, 2, 7,12, 9, 5, 6, 1,13,14, 0,11, 3, 8,
		  9,14,15, 5, 2, 8,12, 3, 7, 0, 4,10, 1,13,11, 6,
		  4, 3, 2,12, 9, 5,15,10,11,14, 1, 7, 6, 0, 8,13},

		{ 4,11, 2,14,15, 0, 8,13, 3,12, 9, 7, 5,10, 6, 1,
		 13, 0,11, 7, 4, 9, 1,10,14, 3, 5,12, 2,15, 8, 6,
		  1, 4,11,13,12, 3, 7,14,10,15, 6, 8, 0, 5, 9, 2,
		  6,11,13, 8, 1, 4,10, 7, 9, 5, 0,15,14, 2, 3,12},

		{13, 2, 8, 4, 6,15,11, 1,10, 9, 3,14, 5, 0,12, 7,
		  1,15,13, 8,10, 3, 7, 4,12, 5, 6,11, 0,14, 9, 2,
		  7,11, 4, 1, 9,12,14, 2, 0, 6,10,13,15, 3, 5, 8,
		  2, 1,14, 7, 4,10, 8,13,15,12, 9, 0, 3, 5, 6,11},
	};
	
	private static final int P[] = {
		16, 7,20,21,
		29,12,28,17,
		 1,15,23,26,
		 5,18,31,10,
		 2, 8,24,14,
		32,27, 3, 9,
		19,13,30, 6,
		22,11, 4,25,
	};
	
	private static int L[] = new int[64];
	
	private static int tempL[] = new int[32];
	
	private static int f[] = new int[32];
	
	private static int preS[] = new int[48];
	
	private static void encrypt (int[] block, boolean edflag) {
		int i, ii, j;
		int k, t;
		for (j = 0; j < 64; j++)
			L[j] = block[IP[j]-1];		
		for (ii = 0; ii < 16; ii++) {
			if (edflag) {
				i = 15-ii;
			} else {
				i = ii;
			}
			for (j = 0; j < 32; j++)
				tempL[j] = L[32+j];
			for (j = 0; j < 48; j++)
				preS[j] = L[32+E[j]-1] ^ KS[i][j];
			for (j = 0; j < 8; j++) {
				t = 6*j;
				k = S[j][(preS[t+0]<<5) +
					(preS[t+1]<<3) +
					(preS[t+2]<<2) +
					(preS[t+3]<<1) +
					(preS[t+4]<<0) +
					(preS[t+5]<<4)];
				t = 4*j;
				f[t+0] = (k>>3)&01;
				f[t+1] = (k>>2)&01;
				f[t+2] = (k>>1)&01;
				f[t+3] = (k>>0)&01;
			}
			for (j = 0; j < 32; j++)
				L[32+j] = L[j] ^ f[P[j]-1];
			for (j = 0; j < 32; j++)
				L[j] = tempL[j];
		}
		
		for (j = 0; j < 32; j++) {
			t = L[j];
			L[j] = L[32+j];
			L[32+j] = t;
		}
		for (j = 0; j < 64; j++)
			block[j] = L[FP[j]-1];
	}
	
	private static int[] block = new int[66];
	
	private static int[] iobuf = new int[16];
	
	private static int[] crypt (int[] pw, int[] salt) {
		int i, j, c, temp, pwIndex, saltIndex;
		for (i = 0; i < 66; i++)
			block[i] = 0;
		for (i = 0, pwIndex = 0; 
			(c = pw[pwIndex]) != 0 && i < 64; 
			pwIndex++)
		{
			for (j = 0; j < 7; j++, i++)
				block[i] = (c>>(6-j)) & 01;
			i++;
		}
		setkey(block);		
		for (i = 0; i < 66; i++)
			block[i] = 0;
		for (i = 0, saltIndex = 0; i < 2; i++) {
			c = salt[saltIndex++];
			iobuf[i] = c;
			if (c > 'Z') c -= 6;
			if (c > '9') c -= 7;
			c -= '.';
			for (j = 0; j < 6; j++) {
				if (((c>>j) & 01) != 0) {
					temp = E[6*i+j];
					E[6*i+j] = E[6*i+j+24];
					E[6*i+j+24] = temp;
				}
			}
		}
		for (i = 0; i < 25; i++)
			encrypt(block, false);
		
		for (i = 0; i < 11; i++) {
			c = 0;
			for (j = 0; j < 6; j++) {
				c <<= 1;
				c |= block[6*i+j];
			}
			c += '.';
			if (c>'9') c += 7;
			if (c>'Z') c += 6;
			iobuf[i+2] = c;
		}
		iobuf[i+2] = 0;
		if (iobuf[1] == 0)
			iobuf[1] = iobuf[0];
		return iobuf;
	}
	
	private static final int ROTORSZ = 256;
	
	private static final int MASK = 0377;
	
	private static int t1[] = new int[ROTORSZ];
	
	private static int t2[] = new int[ROTORSZ];
	
	private static int t3[] = new int[ROTORSZ];
	
	private static boolean encrypting = false;
	
	private static int ccnt, nchars, n1, n2;
	
	private static void crypt_init (int[] pw) {
		int ic, i, k, temp;
		long random;
		int buf[] = new int[13];
		int seed;
		for (i = 0; i < ROTORSZ; i++) 
			t1[i] = t2[i] = t3[i] = 0;
		int[] cryptpwpw = crypt(pw, pw);
		for (k = 0; k < 13; k++) 
			buf[k] = cryptpwpw[k];
		seed = 123;
		for (i = 0; i < 13; i++)
			seed = seed * buf[i] + i;
		for (i = 0; i < ROTORSZ; i++)
			t1[i] = i;
		for (i = 0; i < ROTORSZ; i++) {
			seed = 5*seed + buf[i % 13];
			random = seed % 65521;
			k = ROTORSZ - 1 - i;
			ic = (int)(random & MASK) % (k+1);
			random >>= 8;
			temp = t1[k];
			t1[k] = t1[ic];
			t1[ic] = temp;
			if (t3[k] != 0) continue;
			ic = (int)(random & MASK) % k;
			while (t3[ic] != 0)
				ic = (ic + 1) % k;
			t3[k] = ic;
			t3[ic] = k;
		}
		for (i = 0; i < ROTORSZ; i++)
			t2[t1[i] & MASK] = i;
	}
	
	private static void crypt_start (int[] pass) {
		n1 = 0;
		n2 = 0;
		ccnt = 0;
		nchars = 0;
		encrypting = true;
		crypt_init(pass);
	}
	
	private static void crypt_start (String pass) {
		int passLen = pass.length();
		int[] xpass = new int[passLen+1];
		for (int i = 0; i < passLen; i++) xpass[i] = pass.charAt(i) & 0xff;
		xpass[passLen] = 0;
		crypt_start(xpass);
	}
	
	private static int ENC (int c) {
		return (c & 077) + '#';
	}
	
	private static void threecopy (int[] to, int toIndex, 
		int[] from, int fromIndex) 
	{
		int c1, c2, c3, c4;
		c1 = from[fromIndex] >> 2;
		c2 = (from[fromIndex] << 4) & 060 | (from[fromIndex+1] >> 4) & 017;
		c3 = (from[fromIndex+1] << 2) & 074 | (from[fromIndex+2] >> 6) & 03;
		c4 = from[fromIndex+2] & 077;
		to[toIndex++] = ENC(c1);
		to[toIndex++] = ENC(c2);
		to[toIndex++] = ENC(c3);
		to[toIndex] = ENC(c4);
	}
	
	private static int encode (int[] out, int[] buf, int n) {
		int i;
		int outIndex;
		int bufIndex;
		outIndex = 0;
		bufIndex = 0;
		out[outIndex++] = ENC(n);
		for (i = 0; i < n; bufIndex +=3, i+= 3, outIndex += 4)
			threecopy(out, outIndex, buf, bufIndex);
		out[outIndex] = 0;
		return outIndex;
	}
	
	private static int encryptit (int[] to, int[] from) {
		int scratch[] = new int[4096];
		int spIndex, fromIndex;
		spIndex = 0;
		for (fromIndex = 0; from[fromIndex] != 0; fromIndex++) {
			scratch[spIndex++] = t2[(t3[(t1[(from[fromIndex]+n1)&MASK]+
				n2)&MASK]-n2)&MASK]-n1;
			n1++;
			if (n1 == ROTORSZ) {
				n1 = 0;
				n2++;
				if (n2 == ROTORSZ) n2 = 0;
			}
		}
		return encode(to, scratch, spIndex);
	}
	
	private static String encryptit (String from) {
		int fromLen = from.length();
		int[] xfrom = new int[fromLen+1];
		for (int i = 0; i < fromLen; i++) xfrom[i] = from.charAt(i) & 0xff;
		xfrom[fromLen] = 0;
		int[] xto = new int[4096];
		int len = encryptit(xto, xfrom);
		StringBuffer sbresult = new StringBuffer();
		for (int i = 0; i < len; i++) {
			if (xto[i] == 0) break;
			sbresult.append((char)xto[i]);
		}
		return sbresult.toString();
	}
	
	/**	Encrypts a string using a salt.
	 *
	 *	<p>This is the BSD "crypt" function ported to Java.
	 *
	 *	@param	str		The string to encrypt.
	 *
	 *	@param	salt	The salt string.
	 *
	 *	@return			The encrypted string.
	 */
	
	public static String crypt (String str, String salt) {
		int strLen = str.length();
		int saltLen = salt.length();
		int[] xstr = new int[strLen+1];
		int[] xsalt = new int[saltLen+1];
		for (int i = 0; i < strLen; i++) xstr[i] = str.charAt(i) & 0xff;
		xstr[strLen] = 0;
		for (int i = 0; i < saltLen; i++) xsalt[i] = salt.charAt(i) & 0xff;
		xsalt[saltLen] = 0;
		int[] xresult = crypt(xstr, xsalt);
		StringBuffer sbresult = new StringBuffer();
		for (int i = 0; i < xresult.length; i++) {
			if (xresult[i] == 0) break;
			sbresult.append((char)xresult[i]);
		}
		return sbresult.toString();
	}
	
	/**	Encrypts a password.
	 *
	 *	<p>The password is one-way encrypted by using the BSD
	 *	"crypt" function to encrypt the password using itself
	 *	as the salt.
	 *
	 *	@param	password		The password to be encrypted.
	 *
	 *	@return					The encrypted password.
	 */
	 
	public static String encryptPassword (String password) {
		return crypt(password, password);
	}
	
	/**	Encrypts the response to a Ph challenge.
	 *
	 *	@param	password	The password entered by the user.
	 *
	 *	@param	challenge	The challenge sent by the Ph server.
	 *
	 *	@return				The response to send to the Ph server
	 *						in an "answer" command.
	 */
	
	public static String encryptPhChallenge (String password,
		String challenge)
	{
		crypt_start(password);
		return encryptit(challenge);
	}
	
	/** Hides the default no-arg constructor. */

	private Crypto () {
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

