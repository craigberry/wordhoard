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

/**
  * Some helper routines for data conversion, all data is treated in network
  * byte order.
  * @author Markus Hahn <markus_hahn@gmx.net>
  * @version April 14, 2003
  */
public class BinConverter
{

	/**
	  * Gets bytes from an array into an integer.
	  * @param buf where to get the bytes
	  * @param nOfs index from where to read the data
	  * @return the 32bit integer
	  */
	public static int byteArrayToInt(byte[] buf, int nOfs)
	{
		return ((int) buf[nOfs] << 24)
			| (((int) buf[nOfs + 1] & 0x0ff) << 16)
			| (((int) buf[nOfs + 2] & 0x0ff) << 8)
			| ((int) buf[nOfs + 3] & 0x0ff);
	}

	/**
	  * Converts an integer to bytes, which are put into an array.
	  * @param nValue the 32bit integer to convert
	  * @param buf the target buf 
	  * @param nOfs where to place the bytes in the buf
	  */
	public static void intToByteArray(int nValue, byte[] buf, int nOfs)
	{
		buf[nOfs] = (byte) ((nValue >>> 24) & 0x0ff);
		buf[nOfs + 1] = (byte) ((nValue >>> 16) & 0x0ff);
		buf[nOfs + 2] = (byte) ((nValue >>> 8) & 0x0ff);
		buf[nOfs + 3] = (byte) nValue;
	}

	/**
	  * Gets bytes from an array into a long.
	  * @param buf where to get the bytes
	  * @param nOfs index from where to read the data
	  * @return the 64bit integer
	  */
	public static long byteArrayToLong(byte[] buf, int nOfs)
	{
		return (((long) buf[nOfs]) << 56)
			| (((long) buf[nOfs + 1] & 0x0ffL) << 48)
			| (((long) buf[nOfs + 2] & 0x0ffL) << 40)
			| (((long) buf[nOfs + 3] & 0x0ffL) << 32)
			| (((long) buf[nOfs + 4] & 0x0ffL) << 24)
			| (((long) buf[nOfs + 5] & 0x0ffL) << 16)
			| (((long) buf[nOfs + 6] & 0x0ffL) << 8)
			| ((long) buf[nOfs + 7] & 0x0ffL);
	}

	/**
	  * Converts a long to bytes, which are put into an array.
	  * @param lValue the 64bit integer to convert
	  * @param buf the target buf 
	  * @param nOfs where to place the bytes in the buf
	  */
	public static void longToByteArray(long lValue, byte[] buf, int nOfs)
	{
		buf[nOfs] = (byte) (lValue >>> 56);
		buf[nOfs + 1] = (byte) ((lValue >>> 48) & 0x0ff);
		buf[nOfs + 2] = (byte) ((lValue >>> 40) & 0x0ff);
		buf[nOfs + 3] = (byte) ((lValue >>> 32) & 0x0ff);
		buf[nOfs + 4] = (byte) ((lValue >>> 24) & 0x0ff);
		buf[nOfs + 5] = (byte) ((lValue >>> 16) & 0x0ff);
		buf[nOfs + 6] = (byte) ((lValue >>> 8) & 0x0ff);
		buf[nOfs + 7] = (byte) lValue;
	}

	/**
	  * Converts values from an integer array to a long.
	  * @param buf where to get the bytes
	  * @param nOfs index from where to read the data
	  * @return the 64bit integer
	  */
	public static long intArrayToLong(int[] buf, int nOfs)
	{
		return (((long) buf[nOfs]) << 32)
			| (((long) buf[nOfs + 1]) & 0x0ffffffffL);
	}

	/**
	  * Converts a long to integers which are put into an array.
	  * @param lValue the 64bit integer to convert
	  * @param buf the target buf 
	  * @param nOfs where to place the bytes in the buf
	  */
	public static void longToIntArray(long lValue, int[] buf, int nOfs)
	{
		buf[nOfs] = (int) (lValue >>> 32);
		buf[nOfs + 1] = (int) lValue;
	}

	/**
	  * Makes a long from two integers (treated unsigned).
	  * @param nLo lower 32bits
	  * @param nHi higher 32bits
	  * @return the built long
	  */
	public static long makeLong(int nLo, int nHi)
	{
		return (((long) nHi << 32) | ((long) nLo & 0x00000000ffffffffL));
	}

	/**
	  * Gets the lower 32 bits of a long.
	  * @param lVal the long integer
	  * @return lower 32 bits
	  */
	public static int longLo32(long lVal)
	{
		return (int) lVal;
	}

	/**
	  * Gets the higher 32 bits of a long.
	  * @param lVal the long integer
	  * @return higher 32 bits
	  */
	public static int longHi32(long lVal)
	{
		return (int) ((long) (lVal >>> 32));
	}

	// our table for binhex conversion
	final static char[] HEXTAB =
		{
			'0', '1', '2', '3',	'4', '5', '6', '7',
			'8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/**
	  * Converts a byte array to a binhex string.
	  * @param data the byte array
	  * @return the binhex string
	  */
	public static String bytesToBinHex(byte[] data)
	{
		// just map the call
		return bytesToBinHex(data, 0, data.length);
	}

	/**
	  * Converts a byte array to a binhex string.
	  * @param data the byte array
	  * @param nOfs start index where to get the bytes
	  * @param nLen number of bytes to convert
	  * @return the binhex string
	  */
	public static String bytesToBinHex(byte[] data, int nOfs, int nLen)
	{
		StringBuffer sbuf = new StringBuffer();
		sbuf.setLength(nLen << 1);

		int nPos = 0;

		int nC = nOfs + nLen;

		while (nOfs < nC)
		{
			sbuf.setCharAt(nPos++, HEXTAB[(data[nOfs] >> 4) & 0x0f]);
			sbuf.setCharAt(nPos++, HEXTAB[data[nOfs++] & 0x0f]);
		}
		return sbuf.toString();
	}

	/**
	  * Converts a binhex string back into a byte array (invalid codes will be
	  * skipped).
	  * @param sBinHex binhex string
	  * @param data the target array
	  * @param nSrcOfs from which character in the string the conversion should
	  * begin, remember that (nSrcPos modulo 2) should equals 0 normally
	  * @param nDstOfs to store the bytes from which position in the array 
	  * @param nLen number of bytes to extract
	  * @return number of extracted bytes
	  */
	public static int binHexToBytes(
		String sBinHex,
		byte[] data,
		int nSrcOfs,
		int nDstOfs,
		int nLen)
	{
		// check for correct ranges   
		int nStrLen = sBinHex.length();

		int nAvailBytes = (nStrLen - nSrcOfs) >> 1;
		if (nAvailBytes < nLen)
		{
			nLen = nAvailBytes;
		}

		int nOutputCapacity = data.length - nDstOfs;
		if (nLen > nOutputCapacity)
		{
			nLen = nOutputCapacity;
		}

		// convert now

		int nDstOfsBak = nDstOfs;

		for (int nI = 0; nI < nLen; nI++)
		{
			byte bActByte = 0;
			boolean blConvertOK = true;
			for (int nJ = 0; nJ < 2; nJ++)
			{
				bActByte <<= 4;
				char cActChar = sBinHex.charAt(nSrcOfs++);

				if ((cActChar >= 'a') && (cActChar <= 'f'))
				{
					bActByte |= (byte) (cActChar - 'a') + 10;
				}
				else
				{
					if ((cActChar >= '0') && (cActChar <= '9'))
					{
						bActByte |= (byte) (cActChar - '0');
					}
					else
					{
						blConvertOK = false;
					}
				}
			}
			if (blConvertOK)
			{
				data[nDstOfs++] = bActByte;
			}
		}

		return (nDstOfs - nDstOfsBak);
	}

	/**
	  * Converts a byte array into an Unicode string.
	  * @param data the byte array
	  * @param nOfs where to begin the conversion
	  * @param nLen number of bytes to handle
	  * @return the string
	  */
	public static String byteArrayToUNCString(byte[] data, int nOfs, int nLen)
	{
		// we need two bytes for every character
		nLen &= ~1;

		// enough bytes in the buf?
		int nAvailCapacity = data.length - nOfs;

		if (nAvailCapacity < nLen)
		{
			nLen = nAvailCapacity;
		}

		StringBuffer sbuf = new StringBuffer();
		sbuf.setLength(nLen >> 1);

		int nSBufPos = 0;

		while (nLen > 0)
		{
			sbuf.setCharAt(
				nSBufPos++,
				(char) (((int) data[nOfs] << 8)
					| ((int) data[nOfs + 1] & 0x0ff)));
			nOfs += 2;
			nLen -= 2;
		}

		return sbuf.toString();
	}
}
