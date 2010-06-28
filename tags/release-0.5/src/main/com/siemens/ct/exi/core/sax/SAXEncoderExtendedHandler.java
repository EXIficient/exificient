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

package com.siemens.ct.exi.core.sax;

import java.io.OutputStream;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.util.xml.QNameUtilities;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.4.20090414
 */

public class SAXEncoderExtendedHandler extends SAXEncoder {

	// preserve options
	protected boolean preserveDTD;

	// DOC_TYPE
	protected String docTypeName;
	protected String docTypePublicID;
	protected String docTypeSystemID;
	protected String docTypeText;
	protected boolean noEntityReference;

	public SAXEncoderExtendedHandler(EXIFactory factory, OutputStream os) throws EXIException {
		super(factory, os);

		preserveDTD = factory.getFidelityOptions().isFidelityEnabled(
				FidelityOptions.FEATURE_DTD);
	}

	@Override
	public void startDocument() throws SAXException {
		// init
		noEntityReference = true;
		// normal stuff
		super.startDocument();
	}

	@Override
	public void startElement(String uri, String local, String raw,
			Attributes attributes) throws SAXException {
		try {
			// prefix aware
			startElementPfx(uri, local, QNameUtilities.getPrefixPart(raw),
					attributes);
		} catch (Exception e) {
			throw new SAXException("startElement: " + raw, e);
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (noEntityReference) {
			super.characters(ch, start, length);
		}
		// new String(ch, start, length);
	}

	/*
	 * ======================================================================
	 * Interface LexicalHandler
	 * ======================================================================
	 */
	public void comment(char[] ch, int start, int length) throws SAXException {
		try {
			checkPendingChars();
			encoder.encodeComment(ch, start, length);
		} catch (Exception e) {
			throw new SAXException("comment", e);
		}
	}

	public void startCDATA() throws SAXException {
		// <![CDATA[
		super.characters(Constants.CDATA_START_ARRAY, 0, Constants.CDATA_START_ARRAY.length);
	}

	public void endCDATA() throws SAXException {
		// ]]>
		super.characters(Constants.CDATA_END_ARRAY, 0, Constants.CDATA_END_ARRAY.length);
	}

	public void startDTD(String name, String publicId, String systemId)
			throws SAXException {
		try {
			checkPendingChars();
			docTypeName = name;
			docTypePublicID = publicId == null ? "" : publicId;
			docTypeSystemID = systemId == null ? "" : systemId;
			docTypeText = "";
		} catch (Exception e) {
			throw new SAXException("startDTD", e);
		}
	}

	public void endDTD() throws SAXException {
		try {
			encoder.encodeDocType(docTypeName, docTypePublicID,
					docTypeSystemID, docTypeText);
		} catch (Exception e) {
			throw new SAXException("endDTD", e);
		}
	}

	public void startEntity(String name) throws SAXException {
		try {
			// &amp; --> name="amp"
			if (preserveDTD) {
				checkPendingChars();
				noEntityReference = false;
			}
		} catch (Exception e) {
			throw new SAXException("startEntity", e);
		}
	}

	public void endEntity(String name) throws SAXException {
		try {
			if (noEntityReference == false) {
				// entity reference
				// external DTD subset --> "[dtd]".
				if (!name.equals("[dtd]")) {
					encoder.encodeEntityReference(name);
				}
				noEntityReference = true;
			}
		} catch (Exception e) {
			throw new SAXException("endEntity " + name, e);
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
		// e.g. <!ELEMENT Hello (#PCDATA)>
		// --> name == Hello && model == (#PCDATA) <--
		docTypeText += "<!ELEMENT " + name + " " + model + "> ";
	}

	public void attributeDecl(String eName, String aName, String type,
			String mode, String value) throws SAXException {
		// e.g. <!ATTLIST TVSCHEDULE NAME CDATA #REQUIRED>
		docTypeText += "<!ATTLIST " + eName + " " + aName + " " + type + " "
				+ mode + "> ";
	}

	public void internalEntityDecl(String name, String value)
			throws SAXException {
		// e.g. <!ENTITY eacute "é&#xE9;">
		docTypeText += "<!ENTITY " + name + " \"" + value + "\"> ";
	}

	public void externalEntityDecl(String name, String publicId, String systemId)
			throws SAXException {
	}

}
