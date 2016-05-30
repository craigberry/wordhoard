package edu.northwestern.at.utils.tools;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;
import java.sql.*;

import edu.northwestern.at.utils.*;

/**	MySQL database structure comparison tool.
 *
 *	<p>Usage:
 * 
 *	<p><code>CompareDB url1 username1 password1 url2 username2 password2</code>
 *
 *	<p>url1, username1, password1 = URL, username, and password for first
 *	database.
 *
 *	<p>url2, username2, password2 = URL, username, and password for second
 *	database.
 *
 *	<p>This tool compares the structures of the two databases and prints
 *	a report on stdout. The following differences, if any, are reported:
 *
 *	<ul>
 *	<li>Tables that are in one but not the other database.
 *	<li>Columns that are in a table in one database but are not in the
 *		the same table in the other database.
 *	<li>Differences in the attributes of columns: type, whether or not
 *		null values are permitted, indexing attributes, default value, and
 *		MySQL "extra" information.
 *	</ul>
 */

public class CompareDB {

	/**	Set to false when and if any differences are detected. */

	private static boolean same = true;
	
	/**	A database and its connection. */
	
	private static class DatabaseConnection {
		Connection c;		// the connection
		String name;		// the database name
		private DatabaseConnection (Connection c, String name) {
			this.c = c;
			this.name = name;
		}
	}
	
	/**	Column information. */
	
	private static class ColumnInfo implements Comparable {
		String name;
		String type;
		String nullok;
		String key;
		String defaultval;
		String extra;
		public int compareTo (Object obj) {
			ColumnInfo other = (ColumnInfo)obj;
			return name.compareTo(other.name);
		}
		public boolean equals (Object obj) {
			ColumnInfo other = (ColumnInfo)obj;
			return name.equals(other.name) &&
				Compare.equals(type, other.type) &&
				Compare.equals(nullok, other.nullok) &&
				Compare.equals(key, other.key) &&
				Compare.equals(defaultval, other.defaultval) &&
				Compare.equals(extra, other.extra);
		}
		public String toString () {
			return "[Field=" + name + 
				", Type=" + type + 
				", Null=" + nullok + 
				", Key=" + key + 
				", Default=" + defaultval + 
				", Extra=" + extra + "]";
		}
	}
	
	/**	Gets a connection to a database.
	 *
	 *	@param	url			Database URL.
	 *
	 *	@param	username	Username.
	 *
	 *	@param	password	Password.
	 *
	 *	@return				Database connection.
	 *
	 *	@throws	Exception
	 */
	
	private static DatabaseConnection getConnection (String url,
		String username, String password) 
			throws Exception
	{
		Connection c = DriverManager.getConnection(url, username, password);
		int i = url.lastIndexOf("/");
		String name = url.substring(i+1);
		return new DatabaseConnection(c, name);
	}
	
	/**	Gets the tables in a database.
	 *
	 *	@param	c		Database connection.
	 *
	 *	@return			An array list of the table names in the database,
	 *					sorted in increasing alphabetical order.
	 *
	 *	@throws	Exception
	 */
	
	private static ArrayList getTables (Connection c)
		throws Exception
	{
		Statement s = c.createStatement();
		ResultSet r = s.executeQuery("show tables");
		ArrayList result = new ArrayList();
		while (r.next()) result.add(r.getString(1));
		s.close();
		Collections.sort(result);
		return result;
	}
	
	/**	Prints table differences. 
	 *
	 *	@param	tables1		List of table names in first database.
	 *
	 *	@param	name1		Name of the first database.
	 *
	 *	@param	tables2		List of table names in second database.
	 *
	 *	@param	name2		Name of the second database.
	 */
	
	private static void printTableDiffs (ArrayList tables1, 
		String name1, ArrayList tables2, String name2)
	{
		for (Iterator it = tables1.iterator(); it.hasNext(); ) {
			String t1 = (String)it.next();
			if (!tables2.contains(t1)) {
				System.out.println(
					t1 + ": table in " + name1 + " but not in " + 
					name2);
				same = false;
			}
		}
	}
	
	/**	Gets the columns in a table.
	 *
	 *	@param	c		Database connection.
	 *
	 *	@param	t		Table name.
	 *
	 *	@return			Array list of column information.
	 *
	 *	@throws	Exception
	 */
	
	private static ArrayList getColumns (Connection c, String t)
		throws Exception
	{
		Statement s = c.createStatement();
		ResultSet r = s.executeQuery("show columns from `" + t + "`");
		ArrayList result = new ArrayList();
		while (r.next()) {
			ColumnInfo info = new ColumnInfo();
			info.name = r.getString(1);
			info.type = r.getString(2);
			info.nullok = r.getString(3);
			info.key = r.getString(4);
			info.defaultval = r.getString(5);
			info.extra = r.getString(6);
			result.add(info);
		}
		s.close();
		Collections.sort(result);
		return result;
	}
	
	/**	Prints column differences. 
	 *
	 *	@param	cols1		List of column information from first database.
	 *
	 *	@param	name1		Name of the first database.
	 *
	 *	@param	cols1		List of column information from second database.
	 *
	 *	@param	name2		Name of the second database.
	 */
	
	private static void printColDiffs (ArrayList cols1, String name1,
		ArrayList cols2, String name2)
	{
		for (Iterator it1 = cols1.iterator(); it1.hasNext(); ) {
			ColumnInfo info1 = (ColumnInfo)it1.next();
			boolean ok = false;
			for (Iterator it2 = cols2.iterator(); it2.hasNext(); ) {
				ColumnInfo info2 = (ColumnInfo)it2.next();
				if (info1.name.equals(info2.name)) {
					ok = true;
					break;
				}
			}
			if (!ok) {
				System.out.println("   " +
					info1.name + ": column in " + name1 + 
					" but not in " + name2);
				same = false;
			}
		}
	}
	
	/**	Compares the databases.
	 *
	 *	@param	args		Command-line arguments.
	 *
	 *	@throws Exception
	 */
	 
	private static void compare (String[] args) 
		throws Exception
	{
		if (args.length != 6) {
			System.err.println(
				"Usage: CompareDB url1 username1 password1 " +
					"url2 username2 password2");
			System.exit(1);
		}
	
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		DatabaseConnection c1 = getConnection(args[0], args[1], args[2]);
		DatabaseConnection c2 = getConnection(args[3], args[4], args[5]);
			
		ArrayList tables1 = getTables(c1.c);
		ArrayList tables2 = getTables(c2.c);
		
		printTableDiffs (tables1, c1.name, tables2, c2.name);
		printTableDiffs (tables2, c2.name, tables1, c1.name);
		
		for (Iterator it = tables1.iterator(); it.hasNext(); ) {
			String t1 = (String)it.next();
			if (!tables2.contains(t1)) continue;
			System.out.println("Comparing table " + t1);
			ArrayList cols1 = getColumns(c1.c, t1);
			ArrayList cols2 = getColumns(c2.c, t1);
			printColDiffs(cols1, c1.name, cols2, c2.name);
			printColDiffs(cols2, c2.name, cols1, c1.name);
			for (Iterator it1 = cols1.iterator(); it1.hasNext(); ) {
				ColumnInfo info1 = (ColumnInfo)it1.next();
				for (Iterator it2 = cols2.iterator(); it2.hasNext(); ) {
					ColumnInfo info2 = (ColumnInfo)it2.next();
					if (info1.name.equals(info2.name)) {
						if (!info1.equals(info2)) {
							System.out.println("   " + info1.name +
								": column attributes differ:");
							System.out.println("      " + c1.name + ": " +
								info1.toString());
							System.out.println("      " + c2.name + ": " +
								info2.toString());
						}
					}
				}
			}
		}
		
		c1.c.close();
		c2.c.close();
		
		System.out.println();
		if (same) {
			System.out.println("Database structures are identical");
		} else {
			System.out.println("Database structures are different");
		}
		System.out.println();
	}
	
	/**	The main program.
	 *
	 *	@param	args		Command-line arguments.
	 */
	
	public static void main (String[] args) {
		try {
			compare(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**	Hides the default no-arg constructor.
	 */
	 
	private CompareDB () {
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

