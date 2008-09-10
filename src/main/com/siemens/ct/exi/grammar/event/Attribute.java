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

package com.siemens.ct.exi.grammar.event;

import javax.xml.XMLConstants;

import com.siemens.ct.exi.datatype.BuiltIn;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.util.ExpandedName;
import com.siemens.ct.exi.util.ExpandedNameComparable;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20080718
 */

public class Attribute extends AbstractDatatypeEvent implements ExpandedNameComparable
{
	private String	namespaceURI;
	private String	localPart;
	
	public Attribute ( String uri, String localName, ExpandedName valueType, Datatype datatype )
	{
		super ( "AT", valueType, datatype );
		eventType = EventType.ATTRIBUTE;
		
		this.namespaceURI = uri == null ? XMLConstants.NULL_NS_URI : uri;
		this.localPart = localName;
	}
	
	public Attribute ( String uri, String localName )
	{
		this( uri, localName, BuiltIn.DEFAULT_VALUE_NAME, BuiltIn.DEFAULT_DATATYPE  );
	}

	public String getNamespaceURI ()
	{
		return namespaceURI;
	}

	public void setNamespaceURI ( String namespaceURI )
	{
		this.namespaceURI = namespaceURI;
	}

	public String getLocalPart ()
	{
		return localPart;
	}
	
	public void setLocalPart ( String localPart )
	{
		this.localPart = localPart;
	}

	public String toString ()
	{
		return "AT({" + namespaceURI + "}" + localPart + ")";
	}
	
	@Override
	public int hashCode ()
	{
		//return ( super.hashCode ( ) ^ namespaceURI.hashCode ( ) ^ localPart.hashCode ( ) );
		return ( eventType.ordinal ( ) ^ namespaceURI.hashCode ( ) ^ localPart.hashCode ( ) );
	}

	public boolean equals ( Object obj )
	{
		if ( obj instanceof Attribute )
		{
			return ( 0 == compareTo ( ( (Attribute) obj ).getNamespaceURI ( ), ( (Attribute) obj ).getLocalPart ( ) ) );
			}
		else
		{
			return false;
		}
	}

	public int compareTo ( String uri, String localName )
	{
		// first local-part and then uri
		final int c1 = localPart.compareTo ( localName );
		return ( c1 == 0 ? namespaceURI.compareTo ( uri ) : c1 );
	}

}
