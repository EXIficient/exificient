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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.core.CompileConfiguration;
import com.siemens.ct.exi.exceptions.XMLParsingException;
import com.siemens.ct.exi.util.datatype.XSDBoolean;

/*
 * Namespace (NS) and attribute (AT) events are encoded in a specific order
 * following the associated start element (SE) event. Namespace (NS) events are
 * encoded first, in document order, followed by the AT(xsi:type) event if
 * present, followed by the rest of the attribute (AT) events. When schemas are
 * used, attribute events occur in lexical order sorted first by qname localName
 * then by qname uri.
 * 
 */

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20080718
 */

public class SchemaInformedAttributeList extends AbstractAttributeList
{
	private List<String>	nsURIs		= new ArrayList<String> ( );
	private List<String>	nsPrefixes	= new ArrayList<String> ( );

	private XSDBoolean		bool		= XSDBoolean.newInstance ( );

	public SchemaInformedAttributeList ()
	{
		super ( );
	}

	@Override
	protected void addNS ( String namespaceUri, String pfx )
	{
		super.addNS ( namespaceUri, pfx );

		// update global uri+pfx (necessary for xsi:type)
		nsURIs.add ( namespaceUri );
		nsPrefixes.add ( pfx );
	}

	private void setXsiNil ( boolean nil )
	{
		hasXsiNil = true;
		xsiNil = nil;
	}

	private void setXsiType ( String xsiValue, Map<String, String> prefixMapping )
	{
		// update xsi:type
		if ( hasXsiType )
		{
			String xsiTypePrefix = null;

			int indexCol = xsiValue.indexOf ( Constants.COLON );
			if ( indexCol != -1 )
			{
				xsiTypeLocalName = xsiValue.substring ( indexCol + 1 );
				xsiTypePrefix = xsiValue.substring ( 0, indexCol );
			}
			else
			{
				xsiTypeLocalName = xsiValue;
				xsiTypePrefix = XMLConstants.DEFAULT_NS_PREFIX; // ""
			}
			xsiTypeRaw = xsiValue;

			// check prefix mapping
			if ( prefixMapping.containsKey ( xsiTypePrefix ) )
			{
				xsiTypeURI = prefixMapping.get ( xsiTypePrefix );
			}
			else if ( XMLConstants.DEFAULT_NS_PREFIX.equals ( xsiTypePrefix ) )
			{
				xsiTypeURI = XMLConstants.NULL_NS_URI;
			}
			else
			{
				throw new IllegalArgumentException ( "[ERROR] No URI mapping from xsi:type prefix '" + xsiTypePrefix
						+ "' found! \nConsider preserving namespaces and prefixes!" );
			}
		}
	}

	public void parse ( Attributes atts, Map<String, String> prefixMapping )
	{
		init ( );

		String xsiValue = null;

		for ( int i = 0; i < atts.getLength ( ); i++ )
		{
			String localName = atts.getLocalName ( i );
			String uri = atts.getURI ( i );

			String qname = atts.getQName ( i );

			// namespace declarations
			// if ( qname.startsWith ( XMLConstants.XMLNS_ATTRIBUTE ) )
			if ( localName.equals ( XMLConstants.XMLNS_ATTRIBUTE ) )
			{
				addNS ( atts.getValue ( i ), getXMLNamespacePrefix ( qname ) );
			}
			// xsi:*
			else if ( uri.equals ( XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI ) )
			{
				// xsi:type
				if ( localName.equals ( Constants.XSI_TYPE ) )
				{
					// Note: prefix to uri mapping is done later on
					hasXsiType = true;
					xsiValue = atts.getValue ( i );
				}
				// xsi:nil
				else if ( localName.equals ( Constants.XSI_NIL ) )
				{
					try
					{
						bool.parse ( atts.getValue ( i ) );
						setXsiNil ( bool.getBoolean ( ) );
					}
					catch ( XMLParsingException e )
					{
						// --> default attribute
						insertAttributeInSortedArray ( atts.getURI ( i ), atts.getLocalName ( i ), getPrefixOf ( atts,
								i ), atts.getValue ( i ) );
					}
				}
				else if ( localName.equals ( Constants.XSI_SCHEMA_LOCATION )
						&& !CompileConfiguration.PRESERVE_XSI_SCHEMA_LOCATION )
				{
					// prune xsi:schemaLocation
				}
				else
				{
					insertAttributeInSortedArray ( atts.getURI ( i ), atts.getLocalName ( i ), getPrefixOf ( atts, i ),
							atts.getValue ( i ) );
				}
			}
			// == Attribute (AT)
			// When schemas are used, attribute events occur in lexical order
			// sorted first by qname localName then by qname uri.
			else
			{
				insertAttributeInSortedArray ( atts.getURI ( i ), atts.getLocalName ( i ), getPrefixOf ( atts, i ),
						atts.getValue ( i ) );
			}
		}

		setXsiType ( xsiValue, prefixMapping );
	}

	public void parse ( NamedNodeMap attributes )
	{
		init ( );

		String xsiValue = null;

		for ( int i = 0; i < attributes.getLength ( ); i++ )
		{
			Node at = attributes.item ( i );

			// NS
			if ( XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals ( at.getNamespaceURI ( ) ) )
			{
				String pfx = at.getPrefix ( ) == null ? XMLConstants.DEFAULT_NS_PREFIX : at.getLocalName ( );
				addNS ( at.getNodeValue ( ), pfx );
			}
			// xsi:*
			else if ( XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI.equals ( at.getNamespaceURI ( ) ) )
			{
				// xsi:type
				if ( at.getLocalName ( ).equals ( Constants.XSI_TYPE ) )
				{
					// Note: prefix to uri mapping is done later on
					hasXsiType = true;
					xsiValue = at.getNodeValue ( );
				}
				// xsi:nil
				else if ( at.getLocalName ( ).equals ( Constants.XSI_NIL ) )
				{
					try
					{
						bool.parse ( at.getNodeValue ( ) );
						setXsiNil ( bool.getBoolean ( ) );
					}
					catch ( XMLParsingException e )
					{
						// --> default attribute
						insertAttributeInSortedArray ( at.getNamespaceURI ( ) == null ? XMLConstants.NULL_NS_URI : at
								.getNamespaceURI ( ), at.getLocalName ( ),
								at.getPrefix ( ) == null ? XMLConstants.DEFAULT_NS_PREFIX : at.getPrefix ( ), at
										.getNodeValue ( ) );
					}
				}
				else if ( at.getLocalName ( ).equals ( Constants.XSI_SCHEMA_LOCATION )
						&& !CompileConfiguration.PRESERVE_XSI_SCHEMA_LOCATION )
				{
					// prune xsi:schemaLocation
				}
				else
				{
					insertAttributeInSortedArray ( at.getNamespaceURI ( ) == null ? XMLConstants.NULL_NS_URI : at
							.getNamespaceURI ( ), at.getLocalName ( ),
							at.getPrefix ( ) == null ? XMLConstants.DEFAULT_NS_PREFIX : at.getPrefix ( ), at
									.getNodeValue ( ) );
				}
			}
			else
			{
				insertAttributeInSortedArray ( at.getNamespaceURI ( ) == null ? XMLConstants.NULL_NS_URI : at
						.getNamespaceURI ( ), at.getLocalName ( ),
						at.getPrefix ( ) == null ? XMLConstants.DEFAULT_NS_PREFIX : at.getPrefix ( ), at
								.getNodeValue ( ) );
			}
		}

		setXsiType ( xsiValue, null );
	}

	private String getPrefixOf ( Attributes atts, int index )
	{
		String qname = atts.getQName ( index );
		String localName = atts.getLocalName ( index );

		int lengthDifference = qname.length ( ) - localName.length ( );
		return ( lengthDifference == 0 ? XMLConstants.DEFAULT_NS_PREFIX : qname.substring ( 0, lengthDifference - 1 ) );
	}

	/*
	 * *Inserting an item into a sorted list*
	 * http://www.brpreiss.com/books/opus5/html/page192.html
	 */
	protected void insertAttributeInSortedArray ( String uri, String localName, String pfx, String value )
	{
		// int i = numberOfAttributes;
		int i = this.getNumberOfAttributes ( );

		// greater ?
		while ( i > 0 && isGreaterAttribute ( i - 1, uri, localName ) )
		{
			// move right
			i--;
		}

		// update position i
		attributeURI.add ( i, uri );
		attributeLocalName.add ( i, localName );
		attributePrefix.add ( i, pfx );
		attributeValue.add ( i, value );
	}

	protected boolean isGreaterAttribute ( int attributeIndex, String uri, String localName )
	{

		if ( getAttributeLocalName ( attributeIndex ).compareTo ( localName ) > 0 )
		{
			return true;
		}
		else
		{

			return ( getAttributeURI ( attributeIndex ).compareTo ( uri ) > 0 );
		}
	}

}
