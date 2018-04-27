/*
 * Copyright (c) 2007-2018 Siemens AG
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

package com.siemens.ct.exi.main.api.sax;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.siemens.ct.exi.core.EXIFactory;
import com.siemens.ct.exi.core.EncodingOptions;
import com.siemens.ct.exi.core.FidelityOptions;
import com.siemens.ct.exi.core.exceptions.EXIException;
import com.siemens.ct.exi.core.util.xml.QNameUtilities;

/**
 * Serializes SAX events (also entity references and such) to EXI stream.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 1.0.1-SNAPSHOT
 */

public class SAXEncoderExtendedHandler extends SAXEncoder {

	// preserve options
	protected final boolean preserveDTD;
	protected final boolean preserveComment;
	protected final boolean preservePrefix;

	// DOC_TYPE
	protected String docTypeName;
	protected String docTypePublicID;
	protected String docTypeSystemID;
	protected String docTypeText;
	protected boolean entityReferenceRange;
	protected boolean dtdRange;

	/*
	 * retain entity reference handling (TRUE -> does not resolve entity
	 * references)
	 */
	protected boolean retainEntityReference;

	public SAXEncoderExtendedHandler(EXIFactory factory)
			throws EXIException {
		super(factory);

		FidelityOptions fo = factory.getFidelityOptions();
		preserveDTD = fo.isFidelityEnabled(FidelityOptions.FEATURE_DTD);
		preserveComment = fo.isFidelityEnabled(FidelityOptions.FEATURE_COMMENT);
		preservePrefix = fo.isFidelityEnabled(FidelityOptions.FEATURE_PREFIX);

		retainEntityReference = factory.getEncodingOptions().isOptionEnabled(
				EncodingOptions.RETAIN_ENTITY_REFERENCE);
	}

	@Override
	public void startDocument() throws SAXException {
		// init
		entityReferenceRange = false;
		dtdRange = false;
		// normal stuff
		super.startDocument();
	}

	@Override
	public void startElement(String uri, String local, String raw,
			Attributes attributes) throws SAXException {
		try {
			String prefix = null;
			// prefix awareness?
			if (preservePrefix) {
				prefix = QNameUtilities.getPrefixPart(raw);
			}
			startElementPfx(uri, local, prefix, attributes);
		} catch (Exception e) {
			throw new SAXException("startElement: " + raw, e);
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (preserveDTD && entityReferenceRange) {
			if (retainEntityReference) {
				// do nothing, ER has its own EXI event
			} else {
				// encode entity reference as characters
				super.characters(ch, start, length);
			}
		} else {
			super.characters(ch, start, length);
		}
	}

	/*
	 * ======================================================================
	 * Interface LexicalHandler
	 * ======================================================================
	 */
	public void comment(char[] ch, int start, int length) throws SAXException {
		if (dtdRange) {
			// DTD section
			if (preserveDTD) {
				this.docTypeText += "<!--" + new String(ch, start, length)
						+ "-->";
			}
		} else {
			if (preserveComment) {
				try {
					encoder.encodeComment(ch, start, length);
				} catch (Exception e) {
					throw new SAXException("comment", e);
				}
			}
		}
	}

	public void processingInstruction(String target, String data)
			throws SAXException {
		try {
			if (dtdRange) {
				// DTD section
				if (preserveDTD) {
					this.docTypeText += "<?" + target + " " + data + "?>";
				}
			} else {
				encoder.encodeProcessingInstruction(target, data);
			}
		} catch (Exception e) {
			throw new SAXException("processingInstruction", e);
		}
	}

	public void startCDATA() throws SAXException {
		// <![CDATA[
		// super.characters(Constants.CDATA_START_ARRAY, 0,
		// Constants.CDATA_START_ARRAY.length);
	}

	public void endCDATA() throws SAXException {
		// ]]>
		// super.characters(Constants.CDATA_END_ARRAY, 0,
		// Constants.CDATA_END_ARRAY.length);
	}

	public void startDTD(String name, String publicId, String systemId)
			throws SAXException {
		try {
			if (preserveDTD) {
				docTypeName = name;
				docTypePublicID = publicId == null ? "" : publicId;
				docTypeSystemID = systemId == null ? "" : systemId;
				docTypeText = "";
			}
			dtdRange = true;

		} catch (Exception e) {
			throw new SAXException("startDTD", e);
		}
	}

	public void endDTD() throws SAXException {
		try {
			if (preserveDTD) {
				encoder.encodeDocType(docTypeName, docTypePublicID,
						docTypeSystemID, docTypeText);
				// System.out.println("DAPE encode DTD text = " + docTypeText);
			}
			dtdRange = false;
		} catch (Exception e) {
			throw new SAXException("endDTD", e);
		}
	}

	public void startEntity(String name) throws SAXException {
		if (preserveDTD) {
			if (retainEntityReference) {
				try {
					// &amp; --> name="amp"
					// checkPendingChars();
				} catch (Exception e) {
					throw new SAXException("startEntity", e);
				}
			}
		}
		entityReferenceRange = true;
	}

	public void endEntity(String name) throws SAXException {
		try {
			if (preserveDTD && entityReferenceRange) {
				if (retainEntityReference) {
					/*
					 * General entities are reported with their regular names,
					 * parameter entities have '%' prepended to their names, and
					 * the external DTD subset has the pseudo-entity name
					 * "[dtd]".
					 */
					if (name.startsWith("%") || name.equals("[dtd]")) {
						// do nothing
					} else {
						encoder.encodeEntityReference(name);
					}
				}
			}
			entityReferenceRange = false;

		} catch (Exception e) {
			throw new SAXException("endEntity " + name, e);
		}
	}

	public void skippedEntity(String name) throws SAXException {
		try {
			if (preserveDTD) {
				encoder.encodeEntityReference(name);
			}
		} catch (Exception e) {
			throw new SAXException("skippedEntity " + name, e);
		}
	}

	/*
	 * ======================================================================
	 * Interface DTDHandler
	 * ======================================================================
	 */
	public void notationDecl(String name, String publicId, String systemId)
			throws SAXException {
	}

	public void unparsedEntityDecl(String name, String publicId,
			String systemId, String notationName) throws SAXException {
	}

	/*
	 * ======================================================================
	 * Interface DeclHandler
	 * ======================================================================
	 */
	public void elementDecl(String name, String model) throws SAXException {
		if (preserveDTD) {
			// e.g. <!ELEMENT Hello (#PCDATA)>
			// --> name == Hello && model == (#PCDATA) <--
			docTypeText += "<!ELEMENT " + name + " " + model + "> ";
		}
	}

	public void attributeDecl(String eName, String aName, String type,
			String mode, String value) throws SAXException {
		if (preserveDTD) {
			// e.g. <!ATTLIST TVSCHEDULE NAME CDATA #REQUIRED>
			docTypeText += "<!ATTLIST " + eName + " " + aName + " " + type
					+ " " + mode + "> ";
		}
	}

	public void internalEntityDecl(String name, String value)
			throws SAXException {
		if (preserveDTD) {
			// e.g. <!ENTITY eacute "&#xE9;">
			docTypeText += "<!ENTITY " + name + " \"" + value + "\"> ";
		}
	}

	public void externalEntityDecl(String name, String publicId, String systemId)
			throws SAXException {
	}

}
