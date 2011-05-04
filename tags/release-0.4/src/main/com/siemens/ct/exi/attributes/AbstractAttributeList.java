/*
 * Copyright (C) 2007-2010 Siemens AG
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

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.FidelityOptions;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.4.20090331
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

	// EXI FidelityOptions
	protected FidelityOptions fidelityOptions;
	protected boolean preserveSchemaLocation;
	protected boolean preservePrefixes;

	// xsi:type
	protected boolean hasXsiType;
	protected String xsiTypeRaw;

	// xsi:nil
	protected boolean hasXsiNil;
	protected String xsiNil;
	protected String xsiNilPrefix;

	// attributes
	protected List<String> attributeURI;
	protected List<String> attributeLocalName;
	protected List<String> attributeValue;
	protected List<String> attributePrefix;

	public AbstractAttributeList(FidelityOptions fidelityOptions) {
		// fidelityOptions
		this.fidelityOptions = fidelityOptions;
		preserveSchemaLocation = fidelityOptions
				.isFidelityEnabled(FidelityOptions.FEATURE_XSI_SCHEMALOCATION);
		preservePrefixes = fidelityOptions
				.isFidelityEnabled(FidelityOptions.FEATURE_PREFIX);

		// attributes
		attributeURI = new ArrayList<String>();
		attributeLocalName = new ArrayList<String>();
		attributeValue = new ArrayList<String>();
		attributePrefix = new ArrayList<String>();
	}

	protected void init() {
		hasXsiType = false;
		hasXsiNil = false;

		attributeURI.clear();
		attributeLocalName.clear();
		attributeValue.clear();
		attributePrefix.clear();
	}

	/*
	 * XSI-Type
	 */
	public boolean hasXsiType() {
		return hasXsiType;
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
	
	public String getXsiNilPrefix() {
		return this.xsiNilPrefix;
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

	private void setXsiNil(String rawNil, String xsiPrefix) {
		hasXsiNil = true;
		xsiNil = rawNil;
		this.xsiNilPrefix = xsiPrefix;
	}

	public void parse(Attributes atts) {

		init();

		xsiTypeRaw = null;

		for (int i = 0; i < atts.getLength(); i++) {
			String localName = atts.getLocalName(i);
			String uri = atts.getURI(i);

			// xsi:*
			if (uri.equals(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI)) {
				// xsi:type
				if (localName.equals(Constants.XSI_TYPE)) {
					// Note: prefix to uri mapping is done later on
					hasXsiType = true;
					xsiTypeRaw = atts.getValue(i);
				}
				// xsi:nil
				else if (localName.equals(Constants.XSI_NIL)) {
					setXsiNil(atts.getValue(i), getPrefixOf(atts, i));
				} else if ((localName.equals(Constants.XSI_SCHEMA_LOCATION) || localName
						.equals(Constants.XSI_NONAMESPACE_SCHEMA_LOCATION))
						&& !preserveSchemaLocation) {
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
	}

	public void parse(NamedNodeMap attributes) {
		init();

		xsiTypeRaw = null;

		for (int i = 0; i < attributes.getLength(); i++) {
			Node at = attributes.item(i);

			// NS
			if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI
					.equals(at.getNamespaceURI())) {
				// do not care about NS
			}
			// xsi:*
			else if (XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI.equals(at
					.getNamespaceURI())) {
				// xsi:type
				if (at.getLocalName().equals(Constants.XSI_TYPE)) {
					// Note: prefix to uri mapping is done later on
					hasXsiType = true;
					xsiTypeRaw = at.getNodeValue();
				}
				// xsi:nil
				else if (at.getLocalName().equals(Constants.XSI_NIL)) {
					setXsiNil(at.getNodeValue(), at.getPrefix());
				} else if ((at.getLocalName().equals(
						Constants.XSI_SCHEMA_LOCATION) || at.getLocalName()
						.equals(Constants.XSI_NONAMESPACE_SCHEMA_LOCATION))
						&& !preserveSchemaLocation) {
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