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

package com.siemens.ct.exi.core;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import com.siemens.ct.exi.CodingMode;
import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.event.EventType;
import com.siemens.ct.exi.grammar.event.StartElement;
import com.siemens.ct.exi.grammar.event.StartElementNS;
import com.siemens.ct.exi.io.channel.BitDecoderChannel;
import com.siemens.ct.exi.io.channel.ByteDecoderChannel;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.4.20090414
 */

public class EXIDecoderInOrder extends AbstractEXIDecoder {

	public EXIDecoderInOrder(EXIFactory exiFactory) {
		super(exiFactory);
	}

	public void setInputStream(InputStream is, boolean exiBodyOnly)
			throws EXIException, IOException {

		// buffer stream if not already
		// TODO is there a *nice* way to detect whether a stream is buffered
		// already
		if (!(is instanceof BufferedInputStream)) {
			this.is = is = new BufferedInputStream(is);
		}

		// header
		if (!exiBodyOnly) {
			// parse header (bit-wise)
			BitDecoderChannel headerChannel = new BitDecoderChannel(is);
			EXIHeader.parse(headerChannel);
		}

		// body
		if (exiFactory.getCodingMode() == CodingMode.BIT_PACKED) {
			channel = new BitDecoderChannel(is);
		} else {
			assert (exiFactory.getCodingMode() == CodingMode.BYTE_PACKED);
			channel = new ByteDecoderChannel(is);
		}

		initForEachRun();
	}

	@Override
	protected void initForEachRun() throws EXIException, IOException {
		super.initForEachRun();

		nextEvent = null;
		nextEventType = EventType.START_DOCUMENT;
	}

	public boolean hasNext() throws EXIException, IOException {
		// decode event code
		decodeEventCode();

		return (nextEventType != EventType.END_DOCUMENT);
	}

	public EventType next() throws EXIException {
		return nextEventType;
	}

	public void decodeStartDocument() throws EXIException {
		// update current rule
		currentRule = currentRule.lookFor(ec).next;

		// try to use "default" schema URI in default namespace
		if (!fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_PREFIX) && !exiFactory.isFragment()) {
			if (grammar.isSchemaInformed()) {
				if (runtimeURIEntries.size() > 4) {
					RuntimeURIEntry uc = runtimeURIEntries.get(4);
					namespaces.declarePrefix("", uc.namespaceURI);
					
					for(int i=5; i<runtimeURIEntries.size(); i++) {
						uc = runtimeURIEntries.get(i);
						@SuppressWarnings("unused")
						String pfx = this.getUniquePrefix(uc.namespaceURI);
						// namespaces.declarePrefix(pfx, uc.namespaceURI);
					}
				}
				// else if (uris.size() == 4) {
				// namespaces.declarePrefix("", "");
				// }
			}
		}
	}

	public void decodeStartElement() throws EXIException, IOException {
		assert (nextEventType == EventType.START_ELEMENT);
		// StartElement
		StartElement se = ((StartElement) nextEvent);
		//	store SE qname 
		elementQName = se.getQName();
		// handle element prefixes
		elementPrefix = decodeQNamePrefix(elementQName);
		// push element
		pushElement(se, nextRule);
	}

	public void decodeStartElementNS() throws EXIException, IOException {
		assert (nextEventType == EventType.START_ELEMENT_NS);
		// StartElementNS
		StartElementNS seNS = ((StartElementNS) nextEvent);
		// this.elementURI = seNS.getNamespaceURI();
		// decode local-name
		elementQName = readLocalName(seNS.getNamespaceURI());
		// handle element prefixes
		elementPrefix = decodeQNamePrefix(elementQName);
		// next SE ...
		StartElement nextSE = getGenericStartElement(elementQName);
		// push element
		pushElement(nextSE, nextRule);
	}

	public void decodeStartElementGeneric() throws EXIException, IOException {
		assert (nextEventType == EventType.START_ELEMENT_GENERIC);
		// decode uri & local-name
		decodeStartElementExpandedName();
		// handle element prefixes
		elementPrefix = decodeQNamePrefix(elementQName);
		
		// next SE ...
		StartElement nextSE = getGenericStartElement(elementQName);
		// learn start-element ?
		currentRule.learnStartElement(nextSE);
		// push element
		pushElement(nextSE, nextRule.getElementContentRule());
	}

	public void decodeStartElementGenericUndeclared() throws EXIException,
			IOException {
		assert (nextEventType == EventType.START_ELEMENT_GENERIC_UNDECLARED);
		// decode uri & local-name
		decodeStartElementExpandedName();
		// handle element prefixes
		elementPrefix = decodeQNamePrefix(elementQName);
		// next SE ...
		StartElement nextSE = getGenericStartElement(elementQName);
		// learn start-element ?
		currentRule.learnStartElement(nextSE);
		// push element
		pushElement(nextSE, currentRule.getElementContentRule());
	}

	public void decodeNamespaceDeclaration() throws EXIException, IOException {
		// prefix mapping
		nsURI = readUri();
		nsPrefix = readPrefix(nsURI);
		boolean local_element_ns = channel.decodeBoolean();
		if (local_element_ns) {
			this.elementPrefix = nsPrefix;
		}

		namespaces.declarePrefix(nsPrefix, nsURI);
	}

	static final QName xsiNilQName = new QName(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, Constants.XSI_NIL);
	static final QName xsiTypeQName = new QName(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, Constants.XSI_TYPE);
	
	public void decodeAttributeXsiNil() throws EXIException, IOException {
		assert (nextEventType == EventType.ATTRIBUTE_XSI_NIL);
//		attributeURI = XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI;
//		attributeLocalName = Constants.XSI_NIL;
		attributeQName = xsiNilQName;
		attributePrefix = decodeQNamePrefix(xsiNilQName);
		
		decodeAttributeXsiNilStructure();
	}

	public void decodeAttributeXsiType() throws EXIException, IOException {
		assert (nextEventType == EventType.ATTRIBUTE_XSI_TYPE);
//		attributeURI = XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI;
//		attributeLocalName = Constants.XSI_TYPE;
		attributeQName = xsiTypeQName;
		attributePrefix = decodeQNamePrefix(xsiTypeQName);
		
		decodeAttributeXsiTypeStructure();
	}

	protected void readAttributeContent(Datatype dt) throws IOException {
		attributeValue = typeDecoder.readValue(dt, attributeQName, channel);
	}

	public void decodeAttribute() throws EXIException, IOException {
		// structure & content
		readAttributeContent(decodeAttributeStructure());
	}

	public void decodeAttributeNS() throws EXIException, IOException {
		// structure & content
		readAttributeContent(decodeAttributeNSStructure());
	}

	public void decodeAttributeInvalidValue() throws EXIException, IOException {
		// structure & content
		readAttributeContent(decodeAttributeInvalidValueStructure());
	}

	public void decodeAttributeAnyInvalidValue() throws EXIException,
			IOException {
		// structure & content
		readAttributeContent(decodeAttributeAnyInvalidValueStructure());
	}

	public void decodeAttributeGeneric() throws EXIException, IOException {
		// structure & content
		readAttributeContent(decodeAttributeGenericStructure());
	}

	public void decodeAttributeGenericUndeclared() throws EXIException,
			IOException {
		// structure & content
		readAttributeContent(decodeAttributeGenericUndeclaredStructure());
	}

	public void decodeCharacters() throws EXIException, IOException {
		// structure & content
		characters = typeDecoder.readValue(decodeCharactersStructureOnly(),
				elementContext.qname, channel);
	}

	public void decodeCharactersGeneric() throws EXIException, IOException {
		// structure & content
		characters = typeDecoder
				.readValue(decodeCharactersGenericStructureOnly(),
						elementContext.qname, channel);
	}

	public void decodeCharactersGenericUndeclared() throws EXIException,
			IOException {
		// structure & content
		characters = typeDecoder.readValue(
				decodeCharactersGenericUndeclaredStructureOnly(),
				elementContext.qname, channel);
	}

	public void decodeEndElement() throws EXIException, IOException {
		// save ee information before popping context
		elementQName = elementContext.qname;

		// pop element
		popElement();
	}

	public void decodeEndElementUndeclared() throws EXIException, IOException {
		// save ee information before popping context
		elementQName = elementContext.qname;

		// learn end-element event ?
		currentRule.learnEndElement();

		// pop element
		popElement();
	}

	public void decodeEndDocument() throws EXIException, IOException {
//		assert (elementContextStack.size() == 1);
	}

	public void decodeDocType() throws EXIException, IOException {
		// decode name, public, system, text AS string
		docTypeName = new String(channel.decodeString());
		docTypePublicID = new String(channel.decodeString());
		docTypeSystemID = new String(channel.decodeString());
		docTypeText = new String(channel.decodeString());
	}

	public void decodeEntityReference() throws EXIException, IOException {
		// decode name AS string
		entityReferenceName = new String(channel.decodeString());
	}

	public void decodeComment() throws EXIException, IOException {
		comment = channel.decodeString();
		// update current rule
		currentRule = currentRule.getElementContentRule();
	}

	public void decodeProcessingInstruction() throws EXIException, IOException {
		// target & data
		piTarget = new String(channel.decodeString());
		piData = new String(channel.decodeString());
		// update current rule
		currentRule = currentRule.getElementContentRule();
	}

}
