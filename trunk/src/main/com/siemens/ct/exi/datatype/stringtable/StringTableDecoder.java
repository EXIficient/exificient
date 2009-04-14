/*
 * Copyright (C) 2007-2009 Siemens AG
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
 * @version 0.3.20080718
 */

public interface StringTableDecoder extends StringTableCommon {
	/**
	 * Get the URI string value using the ID as the key.
	 * 
	 * @param id
	 *            - Identifier of URI in table.
	 * @return URI string value or null if URI not found.
	 */
	public String getURIValue(int id);

	/**
	 * Get string value of prefix given URI and prefix ID.
	 * 
	 * @param uri
	 *            - Namespace URI.
	 * @param id
	 *            - Namespace identifier (table index).
	 * @return - Prefix value or null if not found.
	 */
	public String getPrefixValue(String uri, int id);

	/**
	 * Get string value of local name given URI and ID.
	 * 
	 * @param uri
	 *            - Namespace URI.
	 * @param id
	 *            - Identifier (table index) of local name.
	 * @return - Local name or null if not found.
	 */
	public String getLocalNameValue(String uri, int id);

	/**
	 * Get string value of local value given QName and ID.
	 * 
	 * @param uri
	 *            - namespace URI
	 * @param local
	 *            - local-name
	 * @param id
	 *            - Identifier (table index) of local name.
	 * @return - Local value or null if not found.
	 */
	public String getLocalValue(String uri, String local, int id);

	/**
	 * Get global value using the ID as the key.
	 * 
	 * @param id
	 *            - Identifier of global value in table.
	 * @return String value or null if not found.
	 */
	public String getGlobalValue(int id);
}
