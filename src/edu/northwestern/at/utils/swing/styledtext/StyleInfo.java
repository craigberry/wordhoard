package edu.northwestern.at.utils.swing.styledtext;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;

/**	Style information.
 *
 *	<p>This lightweight class wraps an array of {@link StyleRun} objects. It
 *	represents saved style information from an
 *	{@link edu.northwestern.at.utils.swing.XTextPane}.
 *
 *	<p>The intent is for this class and the {@link StyleRun} class to
 *	represent style information in a simple and efficient format which is
 *	neutral with respect to the type of client which may need to use the
 *	information (e.g., a Swing client vs. an HTML web client etc.), but which
 *	is easily converted to and from the formats used by the clients.
 *
 *	<p>The runs should be in non-decreasing order by starting offset. If
 *	a run intersects some other run it should either be for exactly the
 *	same range or the earlier run should completely contain the later run.
 *	That is, runs should not "overlap".
 */

public class StyleInfo implements Externalizable, Cloneable {

	/**	Serial version UID. */

	static final long serialVersionUID = 4722056890310561290L;

	/**	The array of style run information. */

	public StyleRun[] info;

	/**	Constructs a new empty StyleInfo object.
	 */

	public StyleInfo () {
	}

	/**	Constructs a new StyleInfo object from an arraylist of run info.
	 *
	 *	@param	list		Array list of style run information.
	 */

	public StyleInfo (ArrayList list) {
		int numRuns = list.size();
		if (numRuns == 0) return;
		info = new StyleRun[numRuns];
		list.toArray(info);
	}

	/**	Returns a string representation of the style information.
	 *
	 *	@return		The string representation.
	 */

	public String toString () {
		if (info == null) {
			return "null";
		} else {
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < info.length; i++)
				buf.append(info[i].toString() + "\n");
			return buf.toString();
		}
	}

	/**	Compares this style info to other style info.
	 *
	 *	@param	obj		The other style info.
	 *
	 *	@return			True if the two style infos are equal.
	 */

	public boolean equals (Object obj) {
		if (!(obj instanceof StyleInfo)) return false;
		StyleInfo other = (StyleInfo)obj;
		if (info == null) {
			return other.info == null;
		} else if (other.info == null) {
			return false;
		} else if (info.length != other.info.length) {
			return false;
		} else {
			for (int i = 0; i < info.length; i++) {
				if (!info[i].equals(other.info[i])) return false;
			}
			return true;
		}
	}

	/**	Writes the style information on serialization.
	 *
	 *	@param	out		Object output stream.
	 *
	 *	@throws	IOException	I/O error.
	 */

	public void writeExternal (ObjectOutput out)
		throws IOException
	{
		int numRuns = info == null ? 0 : info.length;
		out.writeInt(numRuns);
		for (int i = 0; i < numRuns; i++) {
			StyleRun run = info[i];
			out.writeInt(run.start);
			out.writeInt(run.end);
			out.writeInt(run.kind);
			out.writeInt(run.param);
		}
	}

	/**	Reads the style information on deserialization.
	 *
	 *	@param	in		Object input stream.
	 *
	 *	@throws	IOException	I/O error.
	 *
	 *	@throws	ClassNotFoundException	Class not found.
	 */

	public void readExternal (ObjectInput in)
		throws IOException, ClassNotFoundException
	{
		int numRuns = in.readInt();
		if (numRuns == 0) return;
		info = new StyleRun[numRuns];
		for (int i = 0; i < numRuns; i++) {
			int start = in.readInt();
			int end = in.readInt();
			int kind = in.readInt();
			int param = in.readInt();
			info[i] = new StyleRun(start, end, kind, param);
		}
	}

	/**	Writes the style information to a data output stream.
	 *
	 *	@param	out		Object output stream.
	 *
	 *	@throws	IOException	I/O error.
	 */

	public void writeExternal (DataOutputStream out)
		throws IOException
	{
		int numRuns = info == null ? 0 : info.length;
		out.writeInt(numRuns);
		for (int i = 0; i < numRuns; i++) {
			StyleRun run = info[i];
			out.writeInt(run.start);
			out.writeInt(run.end);
			out.writeInt(run.kind);
			out.writeInt(run.param);
		}
	}

	/**	Reads the style information from a data input stream.
	 *
	 *	@param	in		Object input stream.
	 *
	 *	@throws	IOException	I/O error.
	 *
	 *	@throws	ClassNotFoundException	Class not found.
	 */

	public void readExternal (DataInputStream in)
		throws IOException, ClassNotFoundException
	{
		int numRuns = in.readInt();
		if (numRuns == 0) return;
		info = new StyleRun[numRuns];
		for (int i = 0; i < numRuns; i++) {
			int start = in.readInt();
			int end = in.readInt();
			int kind = in.readInt();
			int param = in.readInt();
			info[i] = new StyleRun(start, end, kind, param);
		}
	}

	/**	Returns a copy of the style information.
	 *
	 *	@return		The copy.
	 */

	public Object clone () {
		StyleInfo result = new StyleInfo();
		if (info != null) {
			int numRuns = info.length;
			result.info = new StyleRun[numRuns];
			for (int i = 0; i < numRuns; i++)
				result.info[i] = (StyleRun)info[i].clone();
		}
		return result;
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

