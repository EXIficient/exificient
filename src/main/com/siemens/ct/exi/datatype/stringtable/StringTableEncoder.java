/*
 * Copyright (C) 2007, 2008 Siemens AG
 *
 * This program and its interfaces are free software;
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.siemens.ct.exi.datatype.stringtable;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20080718
 */

public interface StringTableEncoder extends StringTableCommon {

	/**
	 * Get the ID (index) of the given URI in the URI table.
	 * 
	 * @param uri
	 *            - URI value to look up.
	 * @return Index of the item or -1 if not found.
	 */
	public int getURIID(String uri);

	/**
	 * Get identifier for a given URI/prefix pair.
	 * 
	 * @param uri
	 *            - Namespace URI.
	 * @param prefix
	 *            - Namespace prefix.
	 * @return - Index (ID) of prefix in table.
	 */
	public int getPrefixID(String uri, String prefix);

	/**
	 * Get identifier for a given URI/localName pair.
	 * 
	 * @param uri
	 *            - Namespace URI.
	 * @param name
	 *            - Local name.
	 * @return - Index (ID) of prefix in table.
	 */
	public int getLocalNameID(String uri, String name);

	/**
	 * Get identifier for a given QName/localValue pair.
	 * 
	 * @param uri
	 *            - namespace URI
	 * @param local
	 *            - local-name
	 * @param value
	 *            - String value to look up.
	 * @return - Index (ID) of value in table.
	 */
	public int getLocalValueID(String uri, String local, String value);

	/**
	 * Get the ID (index) of the given value in the global value table.
	 * 
	 * @param value
	 *            - Value to look up.
	 * @return Index of the item or -1 if not found.
	 */
	public int getGlobalValueID(String value);
}
