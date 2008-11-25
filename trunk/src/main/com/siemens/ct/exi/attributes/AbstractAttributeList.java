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

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.2.20081016
 */

/*
 * Namespace (NS) and attribute (AT) events are encoded in a specific order
 * following the associated start element (SE) event. Namespace (NS) events are
 * encoded first, in document order, followed by the AT(xsi:type) event if
 * present, followed by the rest of the attribute (AT) events. When schemas are
 * used, attribute events occur in lexical order sorted first by qname localName
 * then by qname uri.
 */

public abstract class AbstractAttributeList implements AttributeList {
	public static final int XMLNS_PFX_START = XMLConstants.XMLNS_ATTRIBUTE
			.length() + 1;

	// xsi:type
	protected boolean hasXsiType;
	protected String xsiTypeURI;
	protected String xsiTypeLocalName;
	protected String xsiTypeRaw;

	// xsi:nil
	protected boolean hasXsiNil;
	protected String xsiNil;

	// namespace declarations
	protected List<String> namespaceURI;
	protected List<String> namespacePrefix;
	private List<String> nsURIs;
	private List<String> nsPrefixes;

	// attributes
	protected List<String> attributeURI;
	protected List<String> attributeLocalName;
	protected List<String> attributeValue;
	protected List<String> attributePrefix;

	public AbstractAttributeList() {
		// namespace declarations
		namespaceURI = new ArrayList<String>();
		namespacePrefix = new ArrayList<String>();
		nsURIs = new ArrayList<String>();
		nsPrefixes = new ArrayList<String>();

		// attributes
		attributeURI = new ArrayList<String>();
		attributeLocalName = new ArrayList<String>();
		attributeValue = new ArrayList<String>();
		attributePrefix = new ArrayList<String>();
	}

	protected void init() {
		hasXsiType = false;
		hasXsiNil = false;

		namespaceURI.clear();
		namespacePrefix.clear();

		attributeURI.clear();
		attributeLocalName.clear();
		attributeValue.clear();
		attributePrefix.clear();
	}

	// "xmlns:foo" --> "foo"
	protected static String getXMLNamespacePrefix(String qname) {
		return (qname.length() > XMLNS_PFX_START ? qname
				.substring(XMLNS_PFX_START) : "");
	}

	/*
	 * XSI-Type
	 */
	public boolean hasXsiType() {
		return hasXsiType;
	}

	public String getXsiTypeURI() {
		return xsiTypeURI;
	}

	public String getXsiTypeLocalName() {
		return xsiTypeLocalName;
	}

	public String getXsiTypeRaw() {
		return xsiTypeRaw;
	}

	/*
	 * XSI-Nil
	 */
	public boolean hasXsiNil() {
		return hasXsiNil;
	}

	public String getXsiNil() {
		return xsiNil;
	}

	/*
	 * Namespace Declarations
	 */
	public int getNumberOfNamespaceDeclarations() {
		return namespaceURI.size();
	}

	public String getNamespaceDeclarationURI(int index) {
		return namespaceURI.get(index);
	}

	public String getNamespaceDeclarationPrefix(int index) {
		return namespacePrefix.get(index);
	}

	/*
	 * Attributes
	 */
	public int getNumberOfAttributes() {
		return attributeURI.size();
	}

	public String getAttributeURI(int index) {
		return attributeURI.get(index);
	}

	public String getAttributeLocalName(int index) {
		return attributeLocalName.get(index);
	}

	public String getAttributeValue(int index) {
		return attributeValue.get(index);
	}

	public String getAttributePrefix(int index) {
		return attributePrefix.get(index);
	}

	/*
	 * 
	 */

	protected void addNS(String namespaceUri, String pfx) {
		namespaceURI.add(namespaceUri);
		namespacePrefix.add(pfx);

		// update global uri+pfx (necessary for xsi:type)
		nsURIs.add(namespaceUri);
		nsPrefixes.add(pfx);
	}

	private void setXsiNil(String rawNil) {
		hasXsiNil = true;
		xsiNil = rawNil;
	}

	private void setXsiType(String xsiValue, Map<String, String> prefixMapping) {
		// update xsi:type
		if (hasXsiType) {
			String xsiTypePrefix = null;

			int indexCol = xsiValue.indexOf(Constants.COLON);
			if (indexCol != -1) {
				xsiTypeLocalName = xsiValue.substring(indexCol + 1);
				xsiTypePrefix = xsiValue.substring(0, indexCol);
			} else {
				xsiTypeLocalName = xsiValue;
				xsiTypePrefix = XMLConstants.DEFAULT_NS_PREFIX; // ""
			}
			xsiTypeRaw = xsiValue;

			// check prefix mapping
			if (prefixMapping.containsKey(xsiTypePrefix)) {
				xsiTypeURI = prefixMapping.get(xsiTypePrefix);
			} else if (XMLConstants.DEFAULT_NS_PREFIX.equals(xsiTypePrefix)) {
				xsiTypeURI = XMLConstants.NULL_NS_URI;
			} else {
				/*
				 * If there is no namespace in scope for the specified qname
				 * prefix, the QName uri is set to empty ("") and the QName
				 * localName is set to the full lexical value of the QName,
				 * including the prefix.
				 */
				xsiTypeURI = null;
				xsiTypeURI = null;

				// throw new IllegalArgumentException ( "[ERROR] No URI mapping
				// from xsi:type prefix '" + xsiTypePrefix
				// + "' found! \nConsider preserving namespaces and prefixes!"
				// );
			}
		}
	}

	public void parse(Attributes atts, Map<String, String> prefixMapping) {
		init();

		String xsiValue = null;

		for (int i = 0; i < atts.getLength(); i++) {
			String localName = atts.getLocalName(i);
			String uri = atts.getURI(i);

			String qname = atts.getQName(i);

			// namespace declarations
			// if ( qname.startsWith ( XMLConstants.XMLNS_ATTRIBUTE ) )
			if (localName.equals(XMLConstants.XMLNS_ATTRIBUTE)) {
				addNS(atts.getValue(i), getXMLNamespacePrefix(qname));
			}
			// xsi:*
			else if (uri.equals(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI)) {
				// xsi:type
				if (localName.equals(Constants.XSI_TYPE)) {
					// Note: prefix to uri mapping is done later on
					hasXsiType = true;
					xsiValue = atts.getValue(i);
				}
				// xsi:nil
				else if (localName.equals(Constants.XSI_NIL)) {
					setXsiNil(atts.getValue(i));
				} else if (localName.equals(Constants.XSI_SCHEMA_LOCATION)
						&& !CompileConfiguration.PRESERVE_XSI_SCHEMA_LOCATION) {
					// prune xsi:schemaLocation
				} else {
					insertAttribute(atts.getURI(i), atts.getLocalName(i),
							getPrefixOf(atts, i), atts.getValue(i));
				}
			}
			// == Attribute (AT)
			// When schemas are used, attribute events occur in lexical order
			// sorted first by qname localName then by qname uri.
			else {
				insertAttribute(atts.getURI(i), atts.getLocalName(i),
						getPrefixOf(atts, i), atts.getValue(i));
			}
		}

		setXsiType(xsiValue, prefixMapping);
	}

	public void parse(NamedNodeMap attributes) {
		init();

		String xsiValue = null;

		for (int i = 0; i < attributes.getLength(); i++) {
			Node at = attributes.item(i);

			// NS
			if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI
					.equals(at.getNamespaceURI())) {
				String pfx = at.getPrefix() == null ? XMLConstants.DEFAULT_NS_PREFIX
						: at.getLocalName();
				addNS(at.getNodeValue(), pfx);
			}
			// xsi:*
			else if (XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI.equals(at
					.getNamespaceURI())) {
				// xsi:type
				if (at.getLocalName().equals(Constants.XSI_TYPE)) {
					// Note: prefix to uri mapping is done later on
					hasXsiType = true;
					xsiValue = at.getNodeValue();
				}
				// xsi:nil
				else if (at.getLocalName().equals(Constants.XSI_NIL)) {
					setXsiNil(at.getNodeValue());
				} else if (at.getLocalName().equals(
						Constants.XSI_SCHEMA_LOCATION)
						&& !CompileConfiguration.PRESERVE_XSI_SCHEMA_LOCATION) {
					// prune xsi:schemaLocation
				} else {
					insertAttribute(
							at.getNamespaceURI() == null ? XMLConstants.NULL_NS_URI
									: at.getNamespaceURI(),
							at.getLocalName(),
							at.getPrefix() == null ? XMLConstants.DEFAULT_NS_PREFIX
									: at.getPrefix(), at.getNodeValue());
				}
			} else {
				insertAttribute(
						at.getNamespaceURI() == null ? XMLConstants.NULL_NS_URI
								: at.getNamespaceURI(), at.getLocalName(),
						at.getPrefix() == null ? XMLConstants.DEFAULT_NS_PREFIX
								: at.getPrefix(), at.getNodeValue());
			}
		}

		setXsiType(xsiValue, null);
	}

	private String getPrefixOf(Attributes atts, int index) {
		String qname = atts.getQName(index);
		String localName = atts.getLocalName(index);

		int lengthDifference = qname.length() - localName.length();
		return (lengthDifference == 0 ? XMLConstants.DEFAULT_NS_PREFIX : qname
				.substring(0, lengthDifference - 1));
	}

	abstract protected void insertAttribute(String uri, String localName,
			String pfx, String value);

}
