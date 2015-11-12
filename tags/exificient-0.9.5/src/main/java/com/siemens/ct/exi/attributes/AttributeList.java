/*
 * Copyright (c) 2007-2015 Siemens AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */

package com.siemens.ct.exi.attributes;

import javax.xml.namespace.QName;

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
 * @version 0.9.5
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
	
	public void addAttribute(QName at, String value);
	
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
