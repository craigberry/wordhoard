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

	-- Removed all deprecated routines to evict compiler warnings.
	-- Philip R. "Pib" Burns.  Northwestern University.  2003/9/18.

*/

package BlowfishJ;

/*	Please see the license information in the header below. */

/**
  * Implementation of the Blowfish encryption algorithm in CBC mode.
  * @author Markus Hahn <markus_hahn@gmx.net>
  * @version April 14, 2003
  */
public class BlowfishCBC extends BlowfishECB
{

	// the CBC IV

	int m_nIVLo;
	int m_nIVHi;

	/**
	  * Gets the current CBC IV.
	  * @return current CBC IV
	  */
	public long getCBCIV()
	{
		return BinConverter.makeLong(m_nIVLo, m_nIVHi);
	}

	/**
	  * Gets a copy of the current CBC IV.
	  * @param dest where to put current CBC IV
	  * @deprecated
	  */
	public void getCBCIV(byte[] dest)
	{
		getCBCIV(dest, 0);
	}

	/**
	  * Gets a copy of the current CBC IV.
	  * @param dest buffer
	  * @param nOfs where to start writing
	  */
	public void getCBCIV(byte[] dest, int nOfs)
	{
		BinConverter.intToByteArray(m_nIVHi, dest, nOfs);
		BinConverter.intToByteArray(m_nIVLo, dest, nOfs + 4);
	}

	/**
	  * Sets the current CBC IV (for cipher resets).
	  * @param lNewCBCIV the new CBC IV
	  */
	public void setCBCIV(long lNewCBCIV)
	{
		m_nIVHi = BinConverter.longHi32(lNewCBCIV);
		m_nIVLo = BinConverter.longLo32(lNewCBCIV);
	}

	/**
	  * Sets the current CBC IV (for cipher resets).
	  * @param newCBCIV the new CBC IV
	  * @deprecated
	  */
	public void setCBCIV(byte[] newCBCIV)
	{
		setCBCIV(newCBCIV, 0);
	}

	/**
	  * Sets the current CBC IV (for cipher resets).
	  * @param newCBCIV the new CBC IV
	  * @param nOfs where to start reading the IV
	  */
	public void setCBCIV(byte[] newCBCIV, int nOfs)
	{
		m_nIVHi = BinConverter.byteArrayToInt(newCBCIV, nOfs);
		m_nIVLo = BinConverter.byteArrayToInt(newCBCIV, nOfs + 4);
	}

	/**
	  * Constructor, uses a zero CBC IV.
	  * @param bfkey key material, up to MAXKEYLENGTH bytes
	  * @param nOfs where to start reading the key
	  * @param nLen size of the key in bytes
	  */
	public BlowfishCBC(byte[] key, int nOfs, int nLen)
	{
		super(key, nOfs, nLen);

		m_nIVHi = m_nIVLo = 0;
	}

	/**
	  * Constructor to define the CBC IV.
	  * @param key key material, up to MAXKEYLENGTH bytes
	  * @param nOfs where to start reading the key
	  * @param nLen size of the key in bytes
	  * @param lInitCBCIV the CBC IV
	  */
	public BlowfishCBC(byte[] key, int nOfs, int nLen, long lInitCBCIV)
	{
		super(key, nOfs, nLen);

		setCBCIV(lInitCBCIV);
	}

	/**
	  * Constructor to define the CBC IV.
	  * @param key key material, up to MAXKEYLENGTH bytes
	  * @param nOfs where to start reading the key
	  * @param nLen size of the key in bytes
	  * @param initCBCIV the CBC IV
	  * @param nIVOfs where to start reading the IV
	  */
	public BlowfishCBC(
		byte[] key,
		int nOfs,
		int nLen,
		byte[] initCBCIV,
		int nIVOfs)
	{
		super(key, nOfs, nLen);

		setCBCIV(initCBCIV, nIVOfs);
	}

	/**
	  * see BlowfishJ.BlowfishECB#cleanUp()
	  */
	public void cleanUp()
	{
		m_nIVHi = m_nIVLo = 0;
		super.cleanUp();
	}

	/**
	  * @see BlowfishJ.BlowfishECB#encrypt(byte[], int, byte[], int, int)
	  */
	public int encrypt(
		byte[] inBuf,
		int nInPos,
		byte[] outBuf,
		int nOutPos,
		int nLen)
	{
		// same speed tricks than in the ECB variant ...

		nLen -= nLen % BLOCKSIZE;

		long lTemp;

		int nC = nInPos + nLen;

		int[] pbox = m_pbox;
		int nPBox0 = pbox[0];
		int nPBox1 = pbox[1];
		int nPBox2 = pbox[2];
		int nPBox3 = pbox[3];
		int nPBox4 = pbox[4];
		int nPBox5 = pbox[5];
		int nPBox6 = pbox[6];
		int nPBox7 = pbox[7];
		int nPBox8 = pbox[8];
		int nPBox9 = pbox[9];
		int nPBox10 = pbox[10];
		int nPBox11 = pbox[11];
		int nPBox12 = pbox[12];
		int nPBox13 = pbox[13];
		int nPBox14 = pbox[14];
		int nPBox15 = pbox[15];
		int nPBox16 = pbox[16];
		int nPBox17 = pbox[17];

		int[] sbox1 = m_sbox1;
		int[] sbox2 = m_sbox2;
		int[] sbox3 = m_sbox3;
		int[] sbox4 = m_sbox4;

		int nIVHi = m_nIVHi;
		int nIVLo = m_nIVLo;

		int nHi, nLo;

		while (nInPos < nC)
		{
			nHi = inBuf[nInPos++] << 24;
			nHi |= (inBuf[nInPos++] << 16) & 0x0ff0000;
			nHi |= (inBuf[nInPos++] << 8) & 0x000ff00;
			nHi |= inBuf[nInPos++] & 0x00000ff;

			nLo = inBuf[nInPos++] << 24;
			nLo |= (inBuf[nInPos++] << 16) & 0x0ff0000;
			nLo |= (inBuf[nInPos++] << 8) & 0x000ff00;
			nLo |= inBuf[nInPos++] & 0x00000ff;

			// extra step: chain with IV

			nHi ^= nIVHi;
			nLo ^= nIVLo;

			nHi ^= nPBox0;
			nLo
				^= (((sbox1[nHi >>> 24] + sbox2[(nHi >>> 16) & 0x0ff])
					^ sbox3[(nHi >>> 8)
					& 0x0ff])
					+ sbox4[nHi & 0x0ff])
				^ nPBox1;
			nHi
				^= (((sbox1[nLo >>> 24] + sbox2[(nLo >>> 16) & 0x0ff])
					^ sbox3[(nLo >>> 8)
					& 0x0ff])
					+ sbox4[nLo & 0x0ff])
				^ nPBox2;
			nLo
				^= (((sbox1[nHi >>> 24] + sbox2[(nHi >>> 16) & 0x0ff])
					^ sbox3[(nHi >>> 8)
					& 0x0ff])
					+ sbox4[nHi & 0x0ff])
				^ nPBox3;
			nHi
				^= (((sbox1[nLo >>> 24] + sbox2[(nLo >>> 16) & 0x0ff])
					^ sbox3[(nLo >>> 8)
					& 0x0ff])
					+ sbox4[nLo & 0x0ff])
				^ nPBox4;
			nLo
				^= (((sbox1[nHi >>> 24] + sbox2[(nHi >>> 16) & 0x0ff])
					^ sbox3[(nHi >>> 8)
					& 0x0ff])
					+ sbox4[nHi & 0x0ff])
				^ nPBox5;
			nHi
				^= (((sbox1[nLo >>> 24] + sbox2[(nLo >>> 16) & 0x0ff])
					^ sbox3[(nLo >>> 8)
					& 0x0ff])
					+ sbox4[nLo & 0x0ff])
				^ nPBox6;
			nLo
				^= (((sbox1[nHi >>> 24] + sbox2[(nHi >>> 16) & 0x0ff])
					^ sbox3[(nHi >>> 8)
					& 0x0ff])
					+ sbox4[nHi & 0x0ff])
				^ nPBox7;
			nHi
				^= (((sbox1[nLo >>> 24] + sbox2[(nLo >>> 16) & 0x0ff])
					^ sbox3[(nLo >>> 8)
					& 0x0ff])
					+ sbox4[nLo & 0x0ff])
				^ nPBox8;
			nLo
				^= (((sbox1[nHi >>> 24] + sbox2[(nHi >>> 16) & 0x0ff])
					^ sbox3[(nHi >>> 8)
					& 0x0ff])
					+ sbox4[nHi & 0x0ff])
				^ nPBox9;
			nHi
				^= (((sbox1[nLo >>> 24] + sbox2[(nLo >>> 16) & 0x0ff])
					^ sbox3[(nLo >>> 8)
					& 0x0ff])
					+ sbox4[nLo & 0x0ff])
				^ nPBox10;
			nLo
				^= (((sbox1[nHi >>> 24] + sbox2[(nHi >>> 16) & 0x0ff])
					^ sbox3[(nHi >>> 8)
					& 0x0ff])
					+ sbox4[nHi & 0x0ff])
				^ nPBox11;
			nHi
				^= (((sbox1[nLo >>> 24] + sbox2[(nLo >>> 16) & 0x0ff])
					^ sbox3[(nLo >>> 8)
					& 0x0ff])
					+ sbox4[nLo & 0x0ff])
				^ nPBox12;
			nLo
				^= (((sbox1[nHi >>> 24] + sbox2[(nHi >>> 16) & 0x0ff])
					^ sbox3[(nHi >>> 8)
					& 0x0ff])
					+ sbox4[nHi & 0x0ff])
				^ nPBox13;
			nHi
				^= (((sbox1[nLo >>> 24] + sbox2[(nLo >>> 16) & 0x0ff])
					^ sbox3[(nLo >>> 8)
					& 0x0ff])
					+ sbox4[nLo & 0x0ff])
				^ nPBox14;
			nLo
				^= (((sbox1[nHi >>> 24] + sbox2[(nHi >>> 16) & 0x0ff])
					^ sbox3[(nHi >>> 8)
					& 0x0ff])
					+ sbox4[nHi & 0x0ff])
				^ nPBox15;
			nHi
				^= (((sbox1[nLo >>> 24] + sbox2[(nLo >>> 16) & 0x0ff])
					^ sbox3[(nLo >>> 8)
					& 0x0ff])
					+ sbox4[nLo & 0x0ff])
				^ nPBox16;

			nLo ^= nPBox17;

			outBuf[nOutPos++] = (byte) (nLo >>> 24);
			outBuf[nOutPos++] = (byte) (nLo >>> 16);
			outBuf[nOutPos++] = (byte) (nLo >>> 8);
			outBuf[nOutPos++] = (byte) nLo;

			outBuf[nOutPos++] = (byte) (nHi >>> 24);
			outBuf[nOutPos++] = (byte) (nHi >>> 16);
			outBuf[nOutPos++] = (byte) (nHi >>> 8);
			outBuf[nOutPos++] = (byte) nHi;

			// (the encrypted block becomes the new IV)

			nIVHi = nLo;
			nIVLo = nHi;
		}

		m_nIVHi = nIVHi;
		m_nIVLo = nIVLo;

		return nLen;
	}


	/**
	  * @see BlowfishJ.BlowfishECB#decrypt(byte[], int, byte[], int, int)
	  */
	public int decrypt(
		byte[] inBuf,
		int nInPos,
		byte[] outBuf,
		int nOutPos,
		int nLen)
	{
		nLen -= nLen % BLOCKSIZE;

		int nC = nInPos + nLen;

		int[] pbox = m_pbox;
		int nPBox0 = pbox[0];
		int nPBox1 = pbox[1];
		int nPBox2 = pbox[2];
		int nPBox3 = pbox[3];
		int nPBox4 = pbox[4];
		int nPBox5 = pbox[5];
		int nPBox6 = pbox[6];
		int nPBox7 = pbox[7];
		int nPBox8 = pbox[8];
		int nPBox9 = pbox[9];
		int nPBox10 = pbox[10];
		int nPBox11 = pbox[11];
		int nPBox12 = pbox[12];
		int nPBox13 = pbox[13];
		int nPBox14 = pbox[14];
		int nPBox15 = pbox[15];
		int nPBox16 = pbox[16];
		int nPBox17 = pbox[17];

		int[] sbox1 = m_sbox1;
		int[] sbox2 = m_sbox2;
		int[] sbox3 = m_sbox3;
		int[] sbox4 = m_sbox4;

		int nIVHi = m_nIVHi;
		int nIVLo = m_nIVLo;

		int nTmpHi, nTmpLo;

		int nHi, nLo;

		while (nInPos < nC)
		{
			nHi = inBuf[nInPos++] << 24;
			nHi |= (inBuf[nInPos++] << 16) & 0x0ff0000;
			nHi |= (inBuf[nInPos++] << 8) & 0x000ff00;
			nHi |= inBuf[nInPos++] & 0x00000ff;

			nLo = inBuf[nInPos++] << 24;
			nLo |= (inBuf[nInPos++] << 16) & 0x0ff0000;
			nLo |= (inBuf[nInPos++] << 8) & 0x000ff00;
			nLo |= inBuf[nInPos++] & 0x00000ff;

			// (save the current block, it will become the new IV)
			nTmpHi = nHi;
			nTmpLo = nLo;

			nHi ^= nPBox17;
			nLo
				^= (((sbox1[nHi >>> 24] + sbox2[(nHi >>> 16) & 0x0ff])
					^ sbox3[(nHi >>> 8)
					& 0x0ff])
					+ sbox4[nHi & 0x0ff])
				^ nPBox16;
			nHi
				^= (((sbox1[nLo >>> 24] + sbox2[(nLo >>> 16) & 0x0ff])
					^ sbox3[(nLo >>> 8)
					& 0x0ff])
					+ sbox4[nLo & 0x0ff])
				^ nPBox15;
			nLo
				^= (((sbox1[nHi >>> 24] + sbox2[(nHi >>> 16) & 0x0ff])
					^ sbox3[(nHi >>> 8)
					& 0x0ff])
					+ sbox4[nHi & 0x0ff])
				^ nPBox14;
			nHi
				^= (((sbox1[nLo >>> 24] + sbox2[(nLo >>> 16) & 0x0ff])
					^ sbox3[(nLo >>> 8)
					& 0x0ff])
					+ sbox4[nLo & 0x0ff])
				^ nPBox13;
			nLo
				^= (((sbox1[nHi >>> 24] + sbox2[(nHi >>> 16) & 0x0ff])
					^ sbox3[(nHi >>> 8)
					& 0x0ff])
					+ sbox4[nHi & 0x0ff])
				^ nPBox12;
			nHi
				^= (((sbox1[nLo >>> 24] + sbox2[(nLo >>> 16) & 0x0ff])
					^ sbox3[(nLo >>> 8)
					& 0x0ff])
					+ sbox4[nLo & 0x0ff])
				^ nPBox11;
			nLo
				^= (((sbox1[nHi >>> 24] + sbox2[(nHi >>> 16) & 0x0ff])
					^ sbox3[(nHi >>> 8)
					& 0x0ff])
					+ sbox4[nHi & 0x0ff])
				^ nPBox10;
			nHi
				^= (((sbox1[nLo >>> 24] + sbox2[(nLo >>> 16) & 0x0ff])
					^ sbox3[(nLo >>> 8)
					& 0x0ff])
					+ sbox4[nLo & 0x0ff])
				^ nPBox9;
			nLo
				^= (((sbox1[nHi >>> 24] + sbox2[(nHi >>> 16) & 0x0ff])
					^ sbox3[(nHi >>> 8)
					& 0x0ff])
					+ sbox4[nHi & 0x0ff])
				^ nPBox8;
			nHi
				^= (((sbox1[nLo >>> 24] + sbox2[(nLo >>> 16) & 0x0ff])
					^ sbox3[(nLo >>> 8)
					& 0x0ff])
					+ sbox4[nLo & 0x0ff])
				^ nPBox7;
			nLo
				^= (((sbox1[nHi >>> 24] + sbox2[(nHi >>> 16) & 0x0ff])
					^ sbox3[(nHi >>> 8)
					& 0x0ff])
					+ sbox4[nHi & 0x0ff])
				^ nPBox6;
			nHi
				^= (((sbox1[nLo >>> 24] + sbox2[(nLo >>> 16) & 0x0ff])
					^ sbox3[(nLo >>> 8)
					& 0x0ff])
					+ sbox4[nLo & 0x0ff])
				^ nPBox5;
			nLo
				^= (((sbox1[nHi >>> 24] + sbox2[(nHi >>> 16) & 0x0ff])
					^ sbox3[(nHi >>> 8)
					& 0x0ff])
					+ sbox4[nHi & 0x0ff])
				^ nPBox4;
			nHi
				^= (((sbox1[nLo >>> 24] + sbox2[(nLo >>> 16) & 0x0ff])
					^ sbox3[(nLo >>> 8)
					& 0x0ff])
					+ sbox4[nLo & 0x0ff])
				^ nPBox3;
			nLo
				^= (((sbox1[nHi >>> 24] + sbox2[(nHi >>> 16) & 0x0ff])
					^ sbox3[(nHi >>> 8)
					& 0x0ff])
					+ sbox4[nHi & 0x0ff])
				^ nPBox2;
			nHi
				^= (((sbox1[nLo >>> 24] + sbox2[(nLo >>> 16) & 0x0ff])
					^ sbox3[(nLo >>> 8)
					& 0x0ff])
					+ sbox4[nLo & 0x0ff])
				^ nPBox1;

			nLo ^= nPBox0;

			// extra step: "dechain"

			nHi ^= nIVLo;
			nLo ^= nIVHi;

			outBuf[nOutPos++] = (byte) (nLo >>> 24);
			outBuf[nOutPos++] = (byte) (nLo >>> 16);
			outBuf[nOutPos++] = (byte) (nLo >>> 8);
			outBuf[nOutPos++] = (byte) nLo;

			outBuf[nOutPos++] = (byte) (nHi >>> 24);
			outBuf[nOutPos++] = (byte) (nHi >>> 16);
			outBuf[nOutPos++] = (byte) (nHi >>> 8);
			outBuf[nOutPos++] = (byte) nHi;

			// (now set the new IV)
			nIVHi = nTmpHi;
			nIVLo = nTmpLo;
		}

		m_nIVHi = nIVHi;
		m_nIVLo = nIVLo;

		return nLen;
	}
}
