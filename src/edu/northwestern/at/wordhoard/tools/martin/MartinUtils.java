package edu.northwestern.at.wordhoard.tools.martin;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import java.sql.*;

/**	Martin utilities.
 */
 
public class MartinUtils {
	
	/**	Connection to Martin's database. */
	
	private static Connection martinConnection;
	
	/**	Issues an error message. 
	 *
	 *	@param	msg		Error message.
	 *
	 *	@throws Err
	 */
	 
	public static void emsg (String msg) {
		System.out.println("##### " + msg);
	}

	/**	Gets a connection to Martin's database.
	 *
	 *	@return		Connection to Martin's database.
	 *
	 *	@throws	Exception
	 */
	 
	public static Connection getConnection ()
		throws Exception
	{
		Class.forName("com.mysql.jdbc.Driver");
		InputStream in = new FileInputStream("misc/martin.properties");
		Properties properties = new Properties();
		properties.load(in);
		in.close();
		String url = properties.getProperty("database-url");
		String username = properties.getProperty("database-username");
		String password = properties.getProperty("database-password");
		martinConnection = 
			DriverManager.getConnection(url, username, password);
		return martinConnection;
	}
	
	/**	Opens the output file.
	 *
	 *	@param	outPath		Path to output file.
	 *
	 *	@return				File opened for writing.
	 *
	 *	@throws Exception
	 */
	 
	public static PrintWriter openOutputFile (String outPath)
		throws Exception
	{
		FileOutputStream fos = new FileOutputStream(outPath);
		OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
		BufferedWriter bw = new BufferedWriter(osw);
		return new PrintWriter(bw);
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

