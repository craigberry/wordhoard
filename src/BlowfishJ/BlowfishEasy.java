/*
	Copyright (C) 1997-2003 Markus Hahn <markus_hahn@gmx.net>
	
	This library is free software; you can redistribute it and/or
	modify it under the terms of the GNU Lesser General Public
	License as published by the Free Software Foundation; either
	version 2.1 of the License, or (at your option) any later version.
	
	This library is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
	Lesser General Public License for more details.
	
	You should have received a copy of the GNU Lesser General Public
	License along with this library; if not, write to the Free Software
	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package BlowfishJ;

/*	Please see the license information in the header below. */

import java.util.*;
import java.security.*;

/**
  * Support class for easy string encryption with the Blowfish algorithm, now 
  * in CBC mode with a SHA-1 key setup and correct padding - the purposes of
  * this module is mainly to show a possible implementation with Blowfish.
  *
  * @author Markus Hahn <markus_hahn@gmx.net>
  * @version April 14, 2003
  */
public class BlowfishEasy
{
	// the Blowfish CBC instance
	BlowfishCBC m_bfish;

	// one random generator for all simple callers...
	static SecureRandom m_secRnd;

	// ...and created early
	static {
		m_secRnd = new SecureRandom();
	}

	/**
	  * Constructor to set up a string as the key. 
	  * @param sPassword the password 
	  */
	public BlowfishEasy(String sPassword)
	{
		// hash down the password to a 160bit key

		MessageDigest mds = null;
		byte[] hash;

		// we now use the standard Java message digest algorithm

		try
		{
			mds = MessageDigest.getInstance("SHA");
		}
		catch (NoSuchAlgorithmException nsae)
		{
			// (tsnh)
		}

		// (stay compatible to what we had before)
		for (int nI = 0, nC = sPassword.length(); nI < nC; nI++)
		{
			mds.update((byte) (sPassword.charAt(nI) & 0x0ff));
		}

		// setup the encryptor (use a dummy IV)
		hash = mds.digest();
		m_bfish = new BlowfishCBC(hash, 0, hash.length, 0);
	}

	/**
	  * Encrypts a string (treated in Unicode) using the
	  * internal random generator.
	  * @param sPlainText string to encrypt
	  * @return encrypted string in binhex format
	  */
	public String encryptString(String sPlainText)
	{
		// get the IV
		long lCBCIV;
		synchronized (m_secRnd)
		{
			lCBCIV = m_secRnd.nextLong();
		}

		// map the call;
		return encStr(sPlainText, lCBCIV);
	}

	/**
	  * Encrypts a string (treated in Unicode).
	  * @param sPlainText string to encrypt
	  * @param rndGen random generator (usually a java.security.SecureRandom
	  * instance)
	  * @return encrypted string in binhex format
	  */
	public String encryptString(String sPlainText, Random rndGen)
	{
		// get the IV
		long lCBCIV = rndGen.nextLong();

		// map the call;
		return encStr(sPlainText, lCBCIV);
	}

	// internal routine for string encryption

	private String encStr(String sPlainText, long lNewCBCIV)
	{
		// allocate the buffer (align to the next 8 byte border plus padding)
		int nStrLen = sPlainText.length();
		byte[] buf = new byte[((nStrLen << 1) & 0xfffffff8) + 8];

		// copy all bytes of the string into the buffer (use network byte order)
		int nI;
		int nPos = 0;
		for (nI = 0; nI < nStrLen; nI++)
		{
			char cActChar = sPlainText.charAt(nI);
			buf[nPos++] = (byte) ((cActChar >> 8) & 0x0ff);
			buf[nPos++] = (byte) (cActChar & 0x0ff);
		}

		// pad the rest with the PKCS7 scheme
		byte bPadVal = (byte) (buf.length - (nStrLen << 1));
		while (nPos < buf.length)
		{
			buf[nPos++] = bPadVal;
		}

		// create the encryptor
		m_bfish.setCBCIV(lNewCBCIV);

		// encrypt the buffer
		m_bfish.encrypt(buf, 0, buf, 0, buf.length);

		// return the binhex string
		byte[] newCBCIV = new byte[BlowfishCBC.BLOCKSIZE];
		BinConverter.longToByteArray(lNewCBCIV, newCBCIV, 0);

		return BinConverter.bytesToBinHex(newCBCIV, 0, BlowfishCBC.BLOCKSIZE)
			+ BinConverter.bytesToBinHex(buf, 0, buf.length);
	}

	/**
	  * Decrypts a hexbin string (handling is case sensitive).
	  * @param sCipherText hexbin string to decrypt
	  * @return decrypted string (null equals an error)
	  */
	public String decryptString(String sCipherText)
	{
		// get the number of estimated bytes in the string (cut off broken
		// blocks)
		int nLen = (sCipherText.length() >> 1) & ~7;

		// does the given stuff make sense (at least the CBC IV)?
		if (nLen < BlowfishECB.BLOCKSIZE)
			return null;

		// get the CBC IV
		byte[] cbciv = new byte[BlowfishCBC.BLOCKSIZE];
		int nNumOfBytes =
			BinConverter.binHexToBytes(
				sCipherText,
				cbciv,
				0,
				0,
				BlowfishCBC.BLOCKSIZE);

		if (nNumOfBytes < BlowfishCBC.BLOCKSIZE)
			return null;

		// (got it)
		m_bfish.setCBCIV(cbciv, 0);

		// something left to decrypt?       
		nLen -= BlowfishCBC.BLOCKSIZE;
		if (nLen == 0)
		{
			return "";
		}

		// get all data bytes now
		byte[] buf = new byte[nLen];

		nNumOfBytes =
			BinConverter.binHexToBytes(
				sCipherText,
				buf,
				BlowfishCBC.BLOCKSIZE * 2,
				0,
				nLen);

		// we cannot accept broken binhex sequences due to padding
		// and decryption
		if (nNumOfBytes < nLen)
		{
			return null;
		}

		// decrypt the buffer
		m_bfish.decrypt(buf, 0, buf, 0, buf.length);

		// get the last padding byte
		int nPadByte = (int) buf[buf.length - 1] & 0x0ff;

		// (try to get all information if the padding doesn't seem to be
		//  correct)
		if ((nPadByte > 8) || (nPadByte < 0))
		{
			nPadByte = 0;
		}

		// calculate the real size of this message
		nNumOfBytes -= nPadByte;
		if (nNumOfBytes < 0)
		{
			return "";
		}

		// success
		return BinConverter.byteArrayToUNCString(buf, 0, nNumOfBytes);
	}

	/**
	  * Destroys (clears) the encryption engine, after that the instance is not 
	  * valid anymore.
	  */
	public void destroy()
	{
		m_bfish.cleanUp();
	}

}
