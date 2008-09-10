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

package com.siemens.ct.exi.util;

import javax.xml.XMLConstants;

/**
 * Definition: An expanded name is a pair consisting of a namespace name and a
 * local name (see <a href="http://www.w3.org/TR/xml-names11/#dt-expname">W3C
 * ExpandedName</a>)
 * 
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20080718
 */

public class ExpandedName implements Comparable<ExpandedName>, ExpandedNameComparable
{
	public final String	namespaceURI;
	public final String	localName;

	public ExpandedName ( String namespaceURI, String localName )
	{
		// URI
		this.namespaceURI = ( namespaceURI == null ? XMLConstants.NULL_NS_URI : namespaceURI );

		// LocalName
		assert ( localName != null );
		if ( localName == null )
		{
			throw new IllegalArgumentException ( "ExpandedNames localName is not allowed to be null!" );
		}

		this.localName = localName;
	}

	public boolean equals ( Object o )
	{
		if ( o instanceof ExpandedName )
		{
			return ( compareTo ( (ExpandedName) o ) == 0 );
		}
		return false;
	}

	/*
	 * This method returns the hash code value as an integer and is supported
	 * for the benefit of hashing based collection classes such as Hashtable,
	 * HashMap, HashSet etc
	 * 
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public final int hashCode ()
	{
		return namespaceURI.hashCode ( ) ^ localName.hashCode ( );
	}

	/*
	 * EXI#s lexical order: sorted first by qname's local-name then by qname's
	 * URI
	 */
	public int compareTo ( ExpandedName o )
	{
		int cLocalPart = localName.compareTo ( o.localName );
		return ( cLocalPart == 0 ? namespaceURI.compareTo ( o.namespaceURI ) : cLocalPart );
	}

	public int compareTo ( String namespaceURI, String localName )
	{
		int cLocalPart = this.localName.compareTo ( localName );
		return ( cLocalPart == 0 ? this.namespaceURI.compareTo ( namespaceURI ) : cLocalPart );
	}

	public String toString ()
	{
		return ( "{" + namespaceURI + "}" + localName );
	}

}
