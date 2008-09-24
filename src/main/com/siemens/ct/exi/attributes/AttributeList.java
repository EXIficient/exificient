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

import java.util.Map;

import org.w3c.dom.NamedNodeMap;
import org.xml.sax.Attributes;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20080919
 */

public interface AttributeList
{
	/*
	 * Initialization Method
	 */
	//	SAX
	public void parse ( Attributes attributes, Map<String, String> prefixMapping );
	//	DOM
	public void parse( NamedNodeMap attributes );


	/*
	 * XSI-Type
	 */
	public boolean hasXsiType ();
	public String getXsiTypeURI ();
	public String getXsiTypeLocalName ();
	public String getXsiTypeRaw ();

	/*
	 * XSI-Nil
	 */
	public boolean hasXsiNil ();
	public String getXsiNil ();

	/*
	 * Namespace Declarations
	 */
	public int getNumberOfNamespaceDeclarations ();
	public String getNamespaceDeclarationURI ( int index );
	public String getNamespaceDeclarationPrefix ( int index );

	
	/*
	 * Attributes
	 */
	public int getNumberOfAttributes ();
	public String getAttributeURI ( int index );
	public String getAttributeLocalName ( int index );
	public String getAttributeValue ( int index );
	public String getAttributePrefix ( int index );


}
