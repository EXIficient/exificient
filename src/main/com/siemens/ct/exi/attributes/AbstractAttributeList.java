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

import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.EncodingOptions;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.core.container.NamespaceDeclaration;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9
 */

public abstract class AbstractAttributeList implements AttributeList {
	public static final int XMLNS_PFX_START = XMLConstants.XMLNS_ATTRIBUTE
			.length() + 1;

	// options
	protected boolean preserveSchemaLocation;
	protected boolean preservePrefixes;

	// xsi:type
	protected boolean hasXsiType;
	protected String xsiTypeRaw;
	protected String xsiTypePrefix;

	// xsi:nil
	protected boolean hasXsiNil;
	protected String xsiNil;
	protected String xsiNilPrefix;

	// attributes
	protected List<String> attributeURI;
	protected List<String> attributeLocalName;
	protected List<String> attributeValue;
	protected List<String> attributePrefix;
	
	// NS, prefix mappings
	protected List<NamespaceDeclaration> nsDecls;

	public AbstractAttributeList(EXIFactory exiFactory) {
		// options
		preserveSchemaLocation = exiFactory.getEncodingOptions()
				.isOptionEnabled(EncodingOptions.INCLUDE_XSI_SCHEMALOCATION);
		preservePrefixes = exiFactory.getFidelityOptions().isFidelityEnabled(
				FidelityOptions.FEATURE_PREFIX);

		// attributes
		attributeURI = new ArrayList<String>();
		attributeLocalName = new ArrayList<String>();
		attributeValue = new ArrayList<String>();
		attributePrefix = new ArrayList<String>();
		// prefix to NS mappings
		nsDecls = new ArrayList<NamespaceDeclaration>();
	}

	public void clear() {
		hasXsiType = false;
		hasXsiNil = false;

		attributeURI.clear();
		attributeLocalName.clear();
		attributeValue.clear();
		attributePrefix.clear();
		
		xsiTypeRaw = null;
		
		nsDecls.clear();
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

	public String getXsiTypePrefix() {
		return this.xsiTypePrefix;
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

	private void setXsiType(String rawType, String xsiPrefix) {
		this.hasXsiType = true;
		this.xsiTypeRaw = rawType;
		this.xsiTypePrefix = xsiPrefix;
	}

	private void setXsiNil(String rawNil, String xsiPrefix) {
		this.hasXsiNil = true;
		this.xsiNil = rawNil;
		this.xsiNilPrefix = xsiPrefix;
	}

//	public void parse(Attributes atts) {
//		clear();
//
//		for (int i = 0; i < atts.getLength(); i++) {
//			String localName = atts.getLocalName(i);
//			String uri = atts.getURI(i);
//
//			// xsi:*
//			if (uri.equals(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI)) {
//				// xsi:type
//				if (localName.equals(Constants.XSI_TYPE)) {
//					// Note: prefix to uri mapping is done later on
//					setXsiType(atts.getValue(i), getPrefixOf(atts, i));
//				}
//				// xsi:nil
//				else if (localName.equals(Constants.XSI_NIL)) {
//					setXsiNil(atts.getValue(i), getPrefixOf(atts, i));
//				} else if ((localName.equals(Constants.XSI_SCHEMA_LOCATION) || localName
//						.equals(Constants.XSI_NONAMESPACE_SCHEMA_LOCATION))
//						&& !preserveSchemaLocation) {
//					// prune xsi:schemaLocation
//				} else {
//					insertAttribute(atts.getURI(i), atts.getLocalName(i),
//							getPrefixOf(atts, i), atts.getValue(i));
//				}
//			}
//			// == Attribute (AT)
//			// When schemas are used, attribute events occur in lexical order
//			// sorted first by qname localName then by qname uri.
//			else {
//				insertAttribute(atts.getURI(i), atts.getLocalName(i),
//						getPrefixOf(atts, i), atts.getValue(i));
//			}
//		}
//	}
	
	
	public void addNamespaceDeclaration(String uri, String pfx) {
		this.nsDecls.add(new NamespaceDeclaration(uri, pfx));
	}
	
	
	public int getNumberOfNamespaceDeclarations() {
		return nsDecls.size();
	}
	
	public NamespaceDeclaration getNamespaceDeclaration(int index) {
		assert(index >= 0 && index < nsDecls.size());
		return nsDecls.get(index);
	}
	
	public void addAttribute(String uri, String localName, String pfx, String value) {
		// xsi:*
		if (uri.equals(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI)) {
			// xsi:type
			if (localName.equals(Constants.XSI_TYPE)) {
				// Note: prefix to uri mapping is done later on
				setXsiType(value, pfx);
			}
			// xsi:nil
			else if (localName.equals(Constants.XSI_NIL)) {
				setXsiNil(value, pfx);
			} else if ((localName.equals(Constants.XSI_SCHEMA_LOCATION) || localName
					.equals(Constants.XSI_NONAMESPACE_SCHEMA_LOCATION))
					&& !preserveSchemaLocation) {
				// prune xsi:schemaLocation
			} else {
				insertAttribute(uri, localName, pfx, value);
			}
		}
		// == Attribute (AT)
		// When schemas are used, attribute events occur in lexical order
		// sorted first by qname localName then by qname uri.
		else {
			insertAttribute(uri, localName, pfx, value);
		}
	}
	

//	public void parse(NamedNodeMap attributes) {
//		clear();
//
//		for (int i = 0; i < attributes.getLength(); i++) {
//			Node at = attributes.item(i);
//
//			// NS
//			if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI
//					.equals(at.getNamespaceURI())) {
//				// do not care about NS
//			}
//			// xsi:*
//			else if (XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI.equals(at
//					.getNamespaceURI())) {
//				// xsi:type
//				if (at.getLocalName().equals(Constants.XSI_TYPE)) {
//					// Note: prefix to uri mapping is done later on
//					setXsiType(at.getNodeValue(), at.getPrefix());
//				}
//				// xsi:nil
//				else if (at.getLocalName().equals(Constants.XSI_NIL)) {
//					setXsiNil(at.getNodeValue(), at.getPrefix());
//				} else if ((at.getLocalName().equals(
//						Constants.XSI_SCHEMA_LOCATION) || at.getLocalName()
//						.equals(Constants.XSI_NONAMESPACE_SCHEMA_LOCATION))
//						&& !preserveSchemaLocation) {
//					// prune xsi:schemaLocation
//				} else {
//					insertAttribute(
//							at.getNamespaceURI() == null ? XMLConstants.NULL_NS_URI
//									: at.getNamespaceURI(),
//							at.getLocalName(),
//							at.getPrefix() == null ? XMLConstants.DEFAULT_NS_PREFIX
//									: at.getPrefix(), at.getNodeValue());
//				}
//			} else {
//				insertAttribute(
//						at.getNamespaceURI() == null ? XMLConstants.NULL_NS_URI
//								: at.getNamespaceURI(), at.getLocalName(),
//						at.getPrefix() == null ? XMLConstants.DEFAULT_NS_PREFIX
//								: at.getPrefix(), at.getNodeValue());
//			}
//		}
//	}

	abstract protected void insertAttribute(String uri, String localName,
			String pfx, String value);

	
//	static final class PrefixMapping {
//		final String prefix;
//		final String uri;
//
//		public PrefixMapping(String prefix, String uri) {
//			this.prefix = prefix;
//			this.uri = uri;
//		}
//	}
}
