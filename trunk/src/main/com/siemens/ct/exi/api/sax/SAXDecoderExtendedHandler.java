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

package com.siemens.ct.exi.api.sax;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Result;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.core.container.DocType;
import com.siemens.ct.exi.exceptions.EXIException;

/**
 * Parses EXI stream to SAX events with extended features such as entity
 * references.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.5
 */

public class SAXDecoderExtendedHandler extends SAXDecoder implements
		DeclHandler {

	protected Map<String, String> entities;

	protected boolean dummyEntityWritten;

	// default =!= true
	protected static final boolean disable_ouput_escaping = true;

	public SAXDecoderExtendedHandler(EXIFactory exiFactory) throws EXIException {
		super(exiFactory);

		entities = new HashMap<String, String>();
	}

	@Override
	protected void initForEachRun() {
		super.initForEachRun();

		entities.clear();
		dummyEntityWritten = false;
	}

	@Override
	protected void handleDocType(DocType docType) throws SAXException, IOException {
		if (lexicalHandler != null) {
			String publicID = docType.publicID.length == 0 ? null
					: new String(docType.publicID);
			String systemID = docType.systemID.length == 0 ? null
					: new String(docType.systemID);
			
			// start DTD
			lexicalHandler.startDTD(new String(docType.name), publicID,
					systemID);

			// parse DTD context and register declaration-handler
			// TODO not sure whether there is a better way to do DTD
			String docTypeText = new String(docType.text);
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();
			xmlReader.setProperty(
					"http://xml.org/sax/properties/declaration-handler", this);
			Reader r = new StringReader("<!DOCTYPE foo [" + docTypeText + " ]>"
					+ "<foo/>");
			xmlReader.parse(new InputSource(r));

			// end DTD
			lexicalHandler.endDTD();
		}
	}

	@Override
	protected void handleEntityReference(char[] erName) throws SAXException {
		if (lexicalHandler != null) {
			String entityReferenceName = new String(erName);

			if (disable_ouput_escaping) {
				char[] entity = ("&" + entityReferenceName + ";").toCharArray();
				contentHandler.processingInstruction(
						Result.PI_DISABLE_OUTPUT_ESCAPING, "");
				contentHandler.characters(entity, 0, entity.length);
				contentHandler.processingInstruction(
						Result.PI_ENABLE_OUTPUT_ESCAPING, "");
			} else {
				// start entity
				lexicalHandler.startEntity(entityReferenceName);

				char[] entity;

				// check predefined entities in XML
				if (entityReferenceName.equals("quot")) {
					// quot " U+0022 (34)
					entity = Character.toChars(34);
				} else if (entityReferenceName.equals("amp")) {
					// amp & U+0026 (38)
					entity = Character.toChars(38);
				} else if (entityReferenceName.equals("apos")) {
					// apos ' U+0027 (39)
					entity = Character.toChars(39);
				} else if (entityReferenceName.equals("lt")) {
					// lt < U+003C (60)
					entity = Character.toChars(60);
				} else if (entityReferenceName.equals("gt")) {
					// gt > U+003E (62)
					entity = Character.toChars(62);
				} else {
					// local declaration ?
					if (entities != null
							&& entities.containsKey(entityReferenceName)) {
						entity = entities.get(entityReferenceName)
								.toCharArray();
					} else {
						entity = ("?" + entityReferenceName + "?")
								.toCharArray();
						System.err
								.println("Failed to resolve entity resolution for "
										+ entityReferenceName);
					}
				}

				contentHandler.characters(entity, 0, entity.length);

				// end entity
				lexicalHandler.endEntity(entityReferenceName);
			}
		}
	}

	@Override
	protected void handleComment(char[] comment) throws SAXException {
		if (lexicalHandler != null) {
			// char[] comment = decoder.getComment();
			lexicalHandler.comment(comment, 0, comment.length);
		}
	}

	/*
	 * ======================================================================
	 * Interface DeclHandler
	 * ======================================================================
	 */
	public void attributeDecl(String name, String name2, String type,
			String mode, String value) throws SAXException {
		if (declarationHandler != null) {
			checkDummyEntity();
			declarationHandler.attributeDecl(name, name2, type, mode, value);
		} else if (contentHandler instanceof DeclHandler) {
			checkDummyEntity();
			DeclHandler dh = (DeclHandler) contentHandler;
			dh.attributeDecl(name, name2, type, mode, value);
		}
	}

	public void elementDecl(String name, String model) throws SAXException {
		if (declarationHandler != null) {
			checkDummyEntity();
			declarationHandler.elementDecl(name, model);
		} else if (contentHandler instanceof DeclHandler) {
			checkDummyEntity();
			DeclHandler dh = (DeclHandler) contentHandler;
			dh.elementDecl(name, model);
		}
	}

	public void externalEntityDecl(String name, String publicId, String systemId)
			throws SAXException {
		if (declarationHandler != null) {
			declarationHandler.externalEntityDecl(name, publicId, systemId);
		} else if (contentHandler instanceof DeclHandler) {
			DeclHandler dh = (DeclHandler) contentHandler;
			dh.externalEntityDecl(name, publicId, systemId);
		}
	}

	public void internalEntityDecl(String name, String value)
			throws SAXException {
		if (!disable_ouput_escaping) {
			entities.put(name, value);
		}

		if (declarationHandler != null) {
			declarationHandler.internalEntityDecl(name, value);
		} else if (contentHandler instanceof DeclHandler) {
			DeclHandler dh = (DeclHandler) contentHandler;
			dh.internalEntityDecl(name, value);
		}
	}

	/*
	 * The default JAXP transformer seems to need at least one
	 * "internalEntityDecl" to close the DTD properly!?!
	 */
	protected void checkDummyEntity() throws SAXException {
		if (!dummyEntityWritten) {
			internalEntityDecl("dummyEntity", "dummyValue");
			dummyEntityWritten = true;
		}
	}
}
