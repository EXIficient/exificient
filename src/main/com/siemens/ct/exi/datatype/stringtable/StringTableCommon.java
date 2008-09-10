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

public interface StringTableCommon
{   
	/**
	 * Add a URI to the URI table.
	 * @param uri - URI to be added.
	 */
	public void addURI ( String uri );
	
	/**
	 * Get the size of the URI table.
	 */
	public int getURITableSize( );
	
	
	/**
	 * Add a prefix to the namespace prefix table.
	 * @param uri - Namespace URI.
	 * @param prefix - Namespace prefix to be added.
	 */
	public void addPrefix ( String uri, String prefix );

	
	/**
	 * Get the size of the prefix table partition identified by URI.
	 * @param uri - Namespace URI.
	 * @return - Current number of items (strings) in the table.
	 */
	public int getPrefixTableSize ( String uri );
	

	/**
	 * Add a name to the local name table.
	 * @param uri - Namespace URI.
	 * @param name - Name to be added.
	 */
	public void addLocalName ( String uri, String name );
	
	/**
	 * Get size of the local name table partition for a given URI.
	 * @param uri - Namespace URI.
	 * @return - Number of items (strings) in the table.
	 */
	public int getLocalNameTableSize ( String uri );
	
	/**
	 * Add a value to the local value table.
	 * @param uri - Namespace URI
	 * @param local - local-name
	 * @param value - String value to be added.
	 */
	public void addLocalValue ( String uri, String local, String value );
	
	//	TODO addLocalValue + addGlobalValue could be merged --> addValue
	
	/**
	 * Get size of the localName table partition identified by QName.
	 * @param uri - Namespace URI
	 * @param local - local-name
	 * @return - Number of items (strings) in table.
	 */
	public int getLocalValueTableSize ( String uri, String local );

	/**
	 * Add a string value to the global value table.
	 * @param value - Value to be added.
	 */
	public void addGlobalValue ( String value );
	
	/**
	 * Get the current size the global value table.
	 * @return Number of items (strings) in the table.
	 */
	public int getGlobalValueTableSize ( );

	
	
}
