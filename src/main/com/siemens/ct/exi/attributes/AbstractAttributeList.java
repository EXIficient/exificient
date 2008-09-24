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

import javax.xml.XMLConstants;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20080718
 */

public abstract class AbstractAttributeList implements AttributeList
{
	public static final int	XMLNS_PFX_START	= XMLConstants.XMLNS_ATTRIBUTE.length ( ) + 1;

	// xsi:type
	protected boolean		hasXsiType;
	protected String		xsiTypeURI;
	protected String		xsiTypeLocalName;
	protected String		xsiTypeRaw;

	// xsi:nil
	protected boolean		hasXsiNil;
	protected String		xsiNil;

	// namespace declarations
	protected List<String>	namespaceURI;
	protected List<String>	namespacePrefix;

	// attributes
	protected List<String>	attributeURI;
	protected List<String>	attributeLocalName;
	protected List<String>	attributeValue;
	protected List<String>	attributePrefix;

	public AbstractAttributeList ()
	{
		// namespace declarations
		namespaceURI = new ArrayList<String> ( );
		namespacePrefix = new ArrayList<String> ( );

		// attributes
		attributeURI = new ArrayList<String> ( );
		attributeLocalName = new ArrayList<String> ( );
		attributeValue = new ArrayList<String> ( );
		attributePrefix = new ArrayList<String> ( );
	}

	protected void init ()
	{
		hasXsiType = false;
		hasXsiNil = false;

		namespaceURI.clear ( );
		namespacePrefix.clear ( );

		attributeURI.clear ( );
		attributeLocalName.clear ( );
		attributeValue.clear ( );
		attributePrefix.clear ( );
	}

	// "xmlns:foo" --> "foo"
	protected static String getXMLNamespacePrefix ( String qname )
	{
		return ( qname.length ( ) > XMLNS_PFX_START ? qname.substring ( XMLNS_PFX_START ) : "" );
	}

	/*
	 * XSI-Type
	 */
	public boolean hasXsiType ()
	{
		return hasXsiType;
	}

	public String getXsiTypeURI ()
	{
		return xsiTypeURI;
	}

	public String getXsiTypeLocalName ()
	{
		return xsiTypeLocalName;
	}

	public String getXsiTypeRaw ()
	{
		return xsiTypeRaw;
	}

	/*
	 * XSI-Nil
	 */
	public boolean hasXsiNil ()
	{
		return hasXsiNil;
	}

	public String getXsiNil ()
	{
		return xsiNil;
	}

	/*
	 * Namespace Declarations
	 */
	public int getNumberOfNamespaceDeclarations ()
	{
		return namespaceURI.size ( );
	}

	public String getNamespaceDeclarationURI ( int index )
	{
		return namespaceURI.get ( index );
	}

	public String getNamespaceDeclarationPrefix ( int index )
	{
		return namespacePrefix.get ( index );
	}

	protected void addNS ( String namespaceUri, String pfx )
	{
		namespaceURI.add ( namespaceUri );
		namespacePrefix.add ( pfx );
	}

	/*
	 * Attributes
	 */
	public int getNumberOfAttributes ()
	{
		return attributeURI.size ( );
	}

	public String getAttributeURI ( int index )
	{
		return attributeURI.get ( index );
	}

	public String getAttributeLocalName ( int index )
	{
		return attributeLocalName.get ( index );
	}

	public String getAttributeValue ( int index )
	{
		return attributeValue.get ( index );
	}

	public String getAttributePrefix ( int index )
	{
		return attributePrefix.get ( index );
	}
}
