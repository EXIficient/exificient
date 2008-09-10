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

package com.siemens.ct.exi.attributes;

import java.util.Iterator;
import java.util.Map;

import javax.xml.XMLConstants;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;

import com.siemens.ct.exi.util.xml.QNameUtilities;


/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20080718
 */

public class SchemaLessAttributeList extends AbstractAttributeList 
{
	public SchemaLessAttributeList ()
	{
		super();
	}

	public void parse ( Attributes atts, Map<String, String> prefixMapping )
	{
		init ( );
		
		//	NS (prefix mapping)
		Iterator<String> iter = prefixMapping.keySet().iterator();
		while ( iter.hasNext() )
		{
			String pfx = iter.next();
			String uri = prefixMapping.get( pfx );
			addNS( uri, pfx );
		}
		
		
		for ( int i = 0; i < atts.getLength ( ); i++ )
		{
			final String qname = atts.getQName ( i );
			
			// == NamespaceDeclaration (NS)
			// Namespace (NS) events
			//	Note: atts.getURI ( i ) & atts.getValue ( i ) do not contain any information ("")
			if ( qname.startsWith ( XMLConstants.XMLNS_ATTRIBUTE ) )
			{
				addNS( atts.getValue ( i ), getXMLNamespacePrefix ( qname ) );
			}
			// == Attribute (AT(xsi:type))
			// == Attribute (AT(xsi:nil))
			// == Attribute (AT)
			else
			{
				int lengthDifference = qname.length ( ) - atts.getLocalName ( i ).length ( );
				String pfx = ( lengthDifference == 0 ? XMLConstants.DEFAULT_NS_PREFIX : qname.substring ( 0, lengthDifference - 1 ));
				
				String localName = atts.getLocalName ( i );
				String uri = atts.getURI ( i );
				

				if ( localName.length ( ) == 0 )
				{
					//	no namespace-aware parser ?
					localName = QNameUtilities.getLocalPart ( qname );
					
					if ( prefixMapping.containsKey ( pfx ) )
					{
						uri = prefixMapping.get ( pfx );
					}
					else
					{
						uri = XMLConstants.NULL_NS_URI;
					}
				}
				
				addAttribute( uri, localName, pfx, atts.getValue ( i ) );
			}
		}
	}
	
	protected void addAttribute( String uri, String localName, String pfx, String value )
	{
		attributeURI.add ( uri );
		attributeLocalName.add ( localName );
		attributePrefix.add ( pfx );
		attributeValue.add ( value );
	}
	
	public void parse( NamedNodeMap attributes )
	{
		init ( );
		
		for ( int i = 0; i < attributes.getLength ( ); i++ )
		{
			Node at = attributes.item ( i );
			
			//	NS
			if ( XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals ( at.getNamespaceURI ( ) ) )
			{
				String pfx = at.getPrefix ( ) == null ? XMLConstants.DEFAULT_NS_PREFIX : at.getLocalName ( );
				addNS( at.getNodeValue ( ), pfx );
			}
			else
			{
				String ns = at.getNamespaceURI ( ) == null ? XMLConstants.NULL_NS_URI : at.getNamespaceURI ( );
				String pfx = at.getPrefix ( ) == null ? XMLConstants.DEFAULT_NS_PREFIX : at.getPrefix ( );
				addAttribute ( ns, at.getLocalName ( ), pfx, at.getNodeValue ( ) );
			}
		}
	}

}
