package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.Locale;
import javax.imageio.*;
import javax.imageio.plugins.jpeg.*;
import javax.imageio.stream.ImageOutputStream;

/** Converts between buffered images and JPEG images stored in byte arrays. */

public class JPEGConverters
{
	/** Convert BufferedImage to byte array.
	 *
	 *	@param	image	The buffered image to convert to a byte array.
	 */

	public static byte[] toJPEG( BufferedImage image )
	{
		return toJPEG( image , 1.0f );
	}

	/** Convert BufferedImage to byte array with specified quality.
	 *
	 *	@param	image		The buffered image to convert to a byte array.
	 *	@param	quality		The JPEG encoding quality.
	 *					    Quality must lie in the interval [0.0,1.0].
	 *
	 *	<p>
	 *	The quality determines the degree of JPEG compression.
	 *	<p>
	 *
	 *	<p>
	 *	Some guidelines:
	 *	</p>
	 *
	 *	<ul>
	 *	<li>0.75 high quality</li>
	 *	<li>0.5  medium quality</li>
	 *	<li>0.25 low quality</li>
	 *	</ul>
	 */

	public static byte[] toJPEG( BufferedImage image , float quality )
	{
								//	Find a jpeg writer.

		ImageWriter jpegWriter = null;

		Iterator iter	= ImageIO.getImageWritersByFormatName( "jpg" );

		if ( iter.hasNext() )
		{
			jpegWriter = (ImageWriter)iter.next();
		}

		ByteArrayOutputStream bos	= null;
		ImageOutputStream ios		= null;

		try
		{
								//	Prepare output byte stream.

			bos	= new ByteArrayOutputStream();
			ios	= ImageIO.createImageOutputStream( bos );

			jpegWriter.setOutput( ios );

								//	Set the compression quality.

			ImageWriteParam params	= new MyImageWriteParam();

			params.setCompressionMode( ImageWriteParam.MODE_EXPLICIT );
			params.setCompressionQuality( quality );

								//	Write image to byte stream.
			jpegWriter.write
			(
				null ,
				new IIOImage( image , null , null ) ,
				params
			);

			ios.flush();
        }
        catch ( Exception e )
        {
e.printStackTrace();
        }
		finally
		{
			try
			{
	            jpegWriter.dispose();
				ios.close();
				bos.close();
			}
			catch ( Exception e )
			{
			}
		}

/*
		try
		{
			ImageIO.write( image , "jpg" , bos );
		}
		catch ( Exception e )
		{
		}
		finally
		{
			try
			{
				bos.close();
			}
			catch ( Exception e )
			{
			}
		}
*/
								//	Return bytes of compressed image.

		return bos.toByteArray();
	}

	/** Convert JPEG stored in byte array to BufferedImage.
	 *
	 *	@param	jpegBytes	The image as a byte array.
	 */

	public static BufferedImage fromJPEG( byte[] jpegBytes )
	{
		BufferedImage result = null;

		ByteArrayInputStream bis	= new ByteArrayInputStream( jpegBytes );

		try
		{
			result = ImageIO.read( bis );
		}
		catch ( Exception e )
		{
		}
		finally
		{
			try
			{
				bis.close();
			}
			catch ( Exception e )
			{
			}
		}

		return result;
	}


	public static class MyImageWriteParam extends JPEGImageWriteParam
	{
		public MyImageWriteParam()
		{
            super( Locale.getDefault() );
        }

        // This method accepts quality levels between 0 (lowest) and 1 (highest) and simply converts
        // it to a range between 0 and 256; this is not a correct conversion algorithm.
        // However, a proper alternative is a lot more complicated.
        // This should do until the bug is fixed.

		public void setCompressionQuality( float quality )
		{
			if ( quality < 0.0 ) quality = 0.0f;
			else if ( quality > 1.0 ) quality = 1.0f;

            this.compressionQuality	= 256 - ( quality * 256 );
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

