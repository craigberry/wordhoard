package edu.northwestern.at.utils.sys;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import javax.jnlp.*;
import java.net.*;

import com.apple.eio.*;

import edu.northwestern.at.utils.*;

/**	Web Start Manager.
 *
 *	<p>This static class manages the interface to the Web Start / JNLP API,
 *	and provides some similar services when the JNLP environment is not
 *	available.
 */

public class WebStart {

	/**	The JNLP basic service, or null if none. */

	private static BasicService bs;

	/**	The JNLP persistence service, or null if none. */

	private static PersistenceService ps;

	/**	The codebase string. */

	private static String codebase;

	/**	Static intializer. */

	static {
		try {
			bs = (BasicService)ServiceManager.lookup(
				"javax.jnlp.BasicService");
			codebase = bs.getCodeBase().toString();
			ps = (PersistenceService)ServiceManager.lookup(
				"javax.jnlp.PersistenceService");
		} catch (Throwable e) {
			bs = null;
			ps = null;
		}
	}

	/**	Sets the codebase.
	 *
	 *	<p>If we are running in a JNLP environment this method does
	 *	nothing - in that case the codebase is obtained from the JNLP
	 *	basic services.
	 *
	 *	@param	codebase		The codebase.
	 */

	public static void setCodebase (String codebase) {
		if (WebStart.codebase == null) WebStart.codebase = codebase;
	}

	/**	Gets a muffin.
	 *
	 *	@param	key			Muffin key.
	 *
	 *	@return				Muffin value, or null if we are not running
	 *						in a JNLP environment.
	 */

	public static Object getMuffin (String key) {
		if (ps == null) return null;
		try {
			URL url = new URL(codebase + key);
			FileContents fc = ps.get(url);
			InputStream is = fc.getInputStream();
			ObjectInputStream ois = new ObjectInputStream(is);
			Object result = ois.readObject();
			ois.close();
			return result;
		} catch (Exception e) {
//			e.printStackTrace();
			return null;
		}
	}

	/**	Puts a muffin.
	 *
	 *	<p>Does nothing if we are not running in a JNLP environment.
	 *
	 *	@param	key			Muffin key.
	 *
	 *	@param	obj			Muffin value.
	 */

	public static void putMuffin (String key, Object obj) {
		if (ps == null) return;
		try {
			URL url = new URL(codebase + key);
			try {
				ps.create(url, 2000);
			} catch (java.io.IOException e) {
				// muffin already exists
			}
			FileContents fc = ps.get(url);
			OutputStream os = fc.getOutputStream(true);
			ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.writeObject(obj);
			oos.close();
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}

	/**	Directs a browser to show a URL.
	 *
	 *	<p>If we are not running in a JNLP environment, we use alternate
	 *	techniques on Mac OS X and on Windows.
	 *
	 *	@param	url		The URL.
	 */

	public static void showDocument (URL url) {
		if (bs == null) {
			if (Env.MACOSX) {
				try {
					FileManager.openURL(url.toString());
				} catch (IOException e) {
				}
			} else if (Env.WINDOWSOS) {
				BrowserControl.openURL(url.toString());
			}
		} else {
			bs.showDocument(url);
		}
	}

	/**	Directs a browser to show a URL.
	 *
	 *	<p>If we are not running in a JNLP environment, we use alternate
	 *	techniques on Mac OS X and on Windows.
	 *
	 *	@param	urlStr	The URL string.
	 */

	public static void showDocument (String urlStr) {
		try {
			showDocument(new URL(urlStr));
		} catch (MalformedURLException e) {
		}
	}

	/**	Directs a browser to show a page relative to the codebase.
	 *
	 *	<p>If we are not running in a JNLP environment, we use alternate
	 *	techniques on Mac OS X and on Windows.
	 *
	 *	@param	relUrlStr	Relative URL string (relative to the codebase).
	 */

	public static void showRelativeDocument (String relUrlStr) {
		showDocument(codebase + relUrlStr);
	}

	/** Hides the default no-arg constructor. */

	private WebStart () {
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

