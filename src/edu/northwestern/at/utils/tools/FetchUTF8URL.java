package edu.northwestern.at.utils.tools;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.net.*;

/**	Fetches a UTF-8 encoded web page to a file.
 *
 *	<p>Usage:
 *
 *	<p><code>FetchUTF8URL url file</code>
 *
 *	<p>url = URL of the web page to be fetched.
 *
 *	<p>file = Path to file on which to store the web page.
 */

public class FetchUTF8URL {

	/**	The main program.
	 *
	 *	@param	args		Command line arguments.
	 */

	public static void main (String[] args) {
		try {
			if (args.length != 2) {
				System.out.println("Usage: FetchUTF8URL url file");
				System.exit(1);
			}
			URL url = new URL(args[0]);
			int len = 10000000;
			char buf[] = new char[len];
			InputStreamReader in = new InputStreamReader(
				url.openStream(), "utf-8");
			int i = 0;
			int numRead;
			while ((numRead = in.read(buf, i, len-i)) != -1) i += numRead;
			in.close();
			OutputStreamWriter out = new OutputStreamWriter(
				new FileOutputStream(args[1]), "utf-8");
			out.write(buf, 0, i);
			out.close();
		} catch (Throwable e) {
			e.printStackTrace();
			System.exit(1);
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

