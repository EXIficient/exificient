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

import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

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
 * @version 0.9.6-SNAPSHOT
 */

public class AttributeListImpl implements AttributeList {
	public static final int XMLNS_PFX_START = XMLConstants.XMLNS_ATTRIBUTE
			.length() + 1;

	// options
	final protected boolean isSchemaInformed;
	final protected boolean isCanonical;
	final protected boolean preserveSchemaLocation;
	final protected boolean preservePrefixes;

	// xsi:type
	protected boolean hasXsiType;
	protected String xsiTypeRaw;
	protected String xsiTypePrefix;

	// xsi:nil
	protected boolean hasXsiNil;
	protected String xsiNil;
	protected String xsiNilPrefix;

	// attributes
	final protected List<String> attributeURI;
	final protected List<String> attributeLocalName;
	final protected List<String> attributeValue;
	final protected List<String> attributePrefix;

	// NS, prefix mappings
	protected List<NamespaceDeclaration> nsDecls;

	public AttributeListImpl(EXIFactory exiFactory) {
		// options
		isSchemaInformed = exiFactory.getGrammars().isSchemaInformed();
		isCanonical = exiFactory.getEncodingOptions().isOptionEnabled(
				EncodingOptions.CANONICAL_EXI)
				|| exiFactory.getEncodingOptions().isOptionEnabled(
						EncodingOptions.CANONICAL_EXI_WITHOUT_EXI_OPTIONS);
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

	// public void parse(Attributes atts) {
	// clear();
	//
	// for (int i = 0; i < atts.getLength(); i++) {
	// String localName = atts.getLocalName(i);
	// String uri = atts.getURI(i);
	//
	// // xsi:*
	// if (uri.equals(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI)) {
	// // xsi:type
	// if (localName.equals(Constants.XSI_TYPE)) {
	// // Note: prefix to uri mapping is done later on
	// setXsiType(atts.getValue(i), getPrefixOf(atts, i));
	// }
	// // xsi:nil
	// else if (localName.equals(Constants.XSI_NIL)) {
	// setXsiNil(atts.getValue(i), getPrefixOf(atts, i));
	// } else if ((localName.equals(Constants.XSI_SCHEMA_LOCATION) || localName
	// .equals(Constants.XSI_NONAMESPACE_SCHEMA_LOCATION))
	// && !preserveSchemaLocation) {
	// // prune xsi:schemaLocation
	// } else {
	// insertAttribute(atts.getURI(i), atts.getLocalName(i),
	// getPrefixOf(atts, i), atts.getValue(i));
	// }
	// }
	// // == Attribute (AT)
	// // When schemas are used, attribute events occur in lexical order
	// // sorted first by qname localName then by qname uri.
	// else {
	// insertAttribute(atts.getURI(i), atts.getLocalName(i),
	// getPrefixOf(atts, i), atts.getValue(i));
	// }
	// }
	// }

	public void addNamespaceDeclaration(String uri, String pfx) {
		// Canonical EXI defines that namespace declarations MUST be sorted
		// lexicographically according to the NS prefix
		if (this.nsDecls.size() == 0 || !this.isCanonical) {
			this.nsDecls.add(new NamespaceDeclaration(uri, pfx));
		} else {
			// sort
			int i = this.getNumberOfNamespaceDeclarations();

			// greater ?
			while (i > 0 && isGreaterNS(i - 1, pfx)) {
				// move right
				i--;
			}

			// update position i
			this.nsDecls.add(i, new NamespaceDeclaration(uri, pfx));
		}

	}

	public int getNumberOfNamespaceDeclarations() {
		return nsDecls.size();
	}

	public NamespaceDeclaration getNamespaceDeclaration(int index) {
		assert (index >= 0 && index < nsDecls.size());
		return nsDecls.get(index);
	}

	public void addAttribute(QName at, String value) {
		addAttribute(at.getNamespaceURI(), at.getLocalPart(), at.getPrefix(),
				value);
	}

	public void addAttribute(String uri, String localName, String pfx,
			String value) {
		uri = uri == null ? XMLConstants.NULL_NS_URI : uri;
		pfx = pfx == null ? XMLConstants.DEFAULT_NS_PREFIX : pfx;

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

	protected void insertAttribute(String uri, String localName, String pfx,
			String value) {
		if (this.isSchemaInformed || this.isCanonical) {
			// sorted attributes
			int i = this.getNumberOfAttributes();

			// greater ?
			while (i > 0 && isGreaterAttribute(i - 1, uri, localName)) {
				// move right
				i--;
			}

			// update position i
			attributeURI.add(i, uri);
			attributeLocalName.add(i, localName);
			attributePrefix.add(i, pfx);
			attributeValue.add(i, value);
		} else {
			// attribute order does not matter
			attributeURI.add(uri);
			attributeLocalName.add(localName);
			attributePrefix.add(pfx);
			attributeValue.add(value);
		}
	}

	protected final boolean isGreaterAttribute(int attributeIndex, String uri,
			String localName) {

		int compLocalName = getAttributeLocalName(attributeIndex).compareTo(
				localName);

		if (compLocalName > 0) {
			// localName is greater
			return true;
		} else if (compLocalName < 0) {
			// localName is smaller
			return false;
		} else {
			// localName's are equal
			return (getAttributeURI(attributeIndex).compareTo(uri) > 0);
		}
	}

	protected final boolean isGreaterNS(int nsIndex, String prefix) {

		int compPrefix = getNamespaceDeclaration(nsIndex).prefix
				.compareTo(prefix);

		if (compPrefix > 0) {
			// prefix is greater
			return true;
		} else {
			// prefix is smaller (or equal)
			return false;
		}
	}

}
