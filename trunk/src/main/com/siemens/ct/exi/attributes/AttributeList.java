/*
 * Copyright (C) 2007-2011 Siemens AG
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

import com.siemens.ct.exi.core.container.NamespaceDeclaration;

/**
 * Namespace (NS) and attribute (AT) events are encoded in a specific order
 * following the associated start element (SE) event. Namespace (NS) events are
 * encoded first, in document order, followed by the AT(xsi:type) event if
 * present, followed by the rest of the attribute (AT) events. When schemas are
 * used, attribute events occur in lexical order sorted first by qname localName
 * then by qname uri.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.7
 */

public interface AttributeList {
	
	public void clear();

	/*
	 * NS
	 */
	public void addNamespaceDeclaration(String uri, String pfx);
	
	public int getNumberOfNamespaceDeclarations();
	
	public NamespaceDeclaration getNamespaceDeclaration(int index);
	
	/*
	 * Any attribute other than NS
	 */
	public void addAttribute(String uri, String localName, String pfx, String value);
	
	/*
	 * XSI-Type
	 */
	public boolean hasXsiType();

	public String getXsiTypeRaw();

	public String getXsiTypePrefix();

	/*
	 * XSI-Nil
	 */
	public boolean hasXsiNil();

	public String getXsiNil();

	public String getXsiNilPrefix();

	/*
	 * Attributes
	 */
	public int getNumberOfAttributes();

	public String getAttributeURI(int index);

	public String getAttributeLocalName(int index);

	public String getAttributeValue(int index);

	public String getAttributePrefix(int index);

}
