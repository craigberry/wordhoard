package edu.northwestern.at.wordhoard.tools;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import java.sql.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;

/**	Build utilities.
 */
 
public class BuildUtils {

	/**	Path to temp dir. */
	
	private static String TEMP_DIR_PATH = "temp";

	/**	Number of error messages issued. */
	
	private static int numErrors;
	
	/**	Issues an error message. 
	 *
	 *	@param	msg		Error message.
	 *
	 *	@throws Err
	 */
	 
	public static void emsg (String msg) {
		System.out.println("##### " + msg);
		numErrors++;
	}
	
	/**	Gets the number of error messages issued.
	 *
	 *	@return		The number of error messages issued.
	 */
	 
	public static int getNumErrorMessages () {
		return numErrors;
	}
	
	/**	Gets a connection to the static object model database.
	 *
	 *	@param	dbname		Database name.
	 *
	 *	@param	username	MySQL username.
	 *
	 *	@param	password	MySQL password.
	 *
	 *	@return		Connection to database.
	 *
	 *	@throws	Exception
	 */
	 
	public static Connection getConnection (String dbname, String username, String password) 
		throws Exception
	{
		Class.forName("com.mysql.jdbc.Driver");
		ClassLoader loader = BuildUtils.class.getClassLoader();
		String url = "jdbc:mysql://localhost/" + dbname +
			"?characterEncoding=UTF-8&useCompression=true";
		return DriverManager.getConnection(url, username, password);
	}
	
	/**	Initializes Hibernate.
	 *
	 *	@param	dbname		Database name. May be in form "dbname" for a database on
	 *						localhost or in form "host/dbname" or "host:port/dbname" for a 
	 *						database on a remote host.
	 *
	 *	@param	username	MySQL username.
	 *
	 *	@param	password	MySQL password.
	 *
	 *	@throws	Exception
	 */
	 
	public static void initHibernate (String dbname, String username, String password) 
		throws Exception
	{
		String hostAndName;
		if (dbname.indexOf("/") < 0) {
			hostAndName = "localhost/" + dbname;
		} else {
			hostAndName = dbname;
		}
		String url = "jdbc:mysql://" + hostAndName +
			"?characterEncoding=UTF-8&useCompression=true";
		PersistenceManager.init(
			url,
			username,
			password,
			null,
			PersistentClasses.persistentClasses,
			false
		);
	}
	
	/**	Gets tagging data flags.
	 *
	 *	@param	el		"taggingData" element.
	 *
	 *	@return			Tagging data flags.
	 */
	 
	public static long getTaggingDataFlags (Element el) {
		long flags = 0;
		NodeList children = el.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			int type = child.getNodeType();
			if (type == Node.TEXT_NODE) continue;
			String name = child.getNodeName();
			if (name.equals("lemma")) {
				flags |= TaggingData.LEMMA;
			} else if (name.equals("pos")) {
				flags |= TaggingData.POS;
			} else if (name.equals("wordClass")) {
				flags |= TaggingData.WORD_CLASS;
			} else if (name.equals("spelling")) {
				flags |= TaggingData.SPELLING;
			} else if (name.equals("speaker")) {
				flags |= TaggingData.SPEAKER;
			} else if (name.equals("gender")) {
				flags |= TaggingData.GENDER;
			} else if (name.equals("mortality")) {
				flags |= TaggingData.MORTALITY;
			} else if (name.equals("prosodic")) {
				flags |= TaggingData.PROSODIC;
			} else if (name.equals("metricalShape")) {
				flags |= TaggingData.METRICAL_SHAPE;
			} else if (name.equals("pubDates")) {
				flags |= TaggingData.PUB_DATES;
			} else {
				emsg("Illegal tagging data element: " + name);
			}
		}
		return flags;
	}
	
	/**	Gets the word class map.
	 *
	 *	@param	c	Connection to database.
	 *
	 *	@return		A map from word class tags to Hibernate ids.
	 *
	 *	@throws	Exception
	 */
	 
	public static Map getWordClassMap (Connection c) 
		throws Exception
	{
		Statement s = c.createStatement();
		ResultSet r = s.executeQuery(
			"select tag, id from wordclass");
		Map map = new HashMap();
		while (r.next()) {
			String tag = r.getString(1);
			long id = r.getLong(2);
			map.put(tag, new Long(id));
		}
		s.close();
		return map;
	}
	
	/**	Formats elapsed time.
	 *
	 *	@param	startTime		Start time in milliseconds.
	 *
	 *	@param	endTime			End time in milliseconds.
	 *
	 *	@return					Formatted elapsed time string.
	 */
	 
	public static String formatElapsedTime (long startTime, long endTime) {
		double time = (endTime - startTime)/1000.0;
		long seconds = Math.round(time);
		long hours = seconds/3600;
		seconds = seconds - 3600*hours;
		long minutes = seconds/60;
		seconds = seconds - 60*minutes;
		StringBuffer buf = new StringBuffer();
		if (hours > 0) {
			buf.append(hours);
			buf.append(hours == 1 ? " hour " : " hours ");
		}
		if (minutes > 0) {
			buf.append(minutes);
			buf.append(minutes == 1 ? " minute " : " minutes ");
		}
		buf.append(seconds);
		buf.append(seconds == 1 ? " second" : " seconds");
		return buf.toString();
	}
	
	/**	Reports the number of errors.
	 */
	 
	public static void reportNumErrors () {
		System.out.println(numErrors + 
			(numErrors == 1 ? " error" : " errors") +
			" reported");
	}
	
	/**	Recursively deletes a directory and its contents. 
	 *
	 *	@param	dir		Directory.
	 *
	 *	@throws Exception
	 */
	 
	private static void delete (File dir)
		throws Exception
	{
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.isDirectory()) {
				delete(file);
			} else {
				file.delete();
			}
		}
		dir.delete();
	}
	
	/**	Creates the temp dir.
	 *
	 *	@return		Path to temp dir.
	 *
	 *	@throws		Exception
	 */
	 
	public static String createTempDir () 
		throws Exception
	{
		File tempDir = new File(TEMP_DIR_PATH);
		if (!tempDir.exists()) tempDir.mkdir();
		return tempDir.getPath();
	}
	
	/**	Deletes the temp dir.
	 *
	 *	@throws Exception
	 */
	 
	public static void deleteTempDir ()
		throws Exception
	{
		File tempDir = new File(TEMP_DIR_PATH);
		delete(tempDir);
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

