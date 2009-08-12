/*
 * Copyright (C) 2007-2009 Siemens AG
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

import com.siemens.ct.exi.CodingMode;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.event.EventType;
import com.siemens.ct.exi.grammar.event.StartElement;
import com.siemens.ct.exi.grammar.event.StartElementNS;
import com.siemens.ct.exi.grammar.rule.Rule;
import com.siemens.ct.exi.io.channel.BitDecoderChannel;
import com.siemens.ct.exi.io.channel.ByteDecoderChannel;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20090414
 */

public class EXIDecoderInOrder extends AbstractEXIDecoder {

	public EXIDecoderInOrder(EXIFactory exiFactory) {
		super(exiFactory);
	}

	public void setInputStream(InputStream is, boolean exiBodyOnly)
			throws EXIException, IOException {
		
		// buffer stream if not already
		// TODO is there a *nice* way to detect whether a stream is buffered already
		if (!(is instanceof BufferedInputStream)) {
			this.is = new BufferedInputStream(is);
		}

		//	header
		if (!exiBodyOnly) {
			// parse header (bit-wise)
			BitDecoderChannel headerChannel = new BitDecoderChannel(is);
			EXIHeader.parse(headerChannel);
		}
		
		// body
		if (exiFactory.getCodingMode()== CodingMode.BIT_PACKED) {
			channel = new BitDecoderChannel(is);
		} else {
			assert (exiFactory.getCodingMode() == CodingMode.BYTE_PACKED);
			channel = new ByteDecoderChannel(is);
		}
		
		initForEachRun();
	}

	@Override
	protected void initForEachRun() throws EXIException {
		super.initForEachRun();

		nextEvent = null;
		nextEventType = EventType.START_DOCUMENT;
	}

	public boolean hasNext() throws EXIException {
		// decode event code
		decodeEventCode();

		return (nextEventType != EventType.END_DOCUMENT);
	}

	public EventType next() throws EXIException {
		return nextEventType;
	}

	public void decodeStartDocument() throws EXIException {
		// step forward
		replaceRuleAtTheTop(currentRule.lookFor(ec).next);

		// try to use "default" schema URI in default namespace
		if (!fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_PREFIX)) {
			if (grammar.isSchemaInformed()) {
				if (uris.size() > 4) {
					URIContext uc = uris.get(4);
					namespaces.declarePrefix("", uc.namespaceURI);
				}
			}
		}
	}

	public void decodeStartElement() throws EXIException {
		try {
			// reset local-element-ns prefix
			elementPrefix = null;

			boolean isGenericSE = true;

			if (nextEventType == EventType.START_ELEMENT) {
				// StartEvent
				isGenericSE = false;
				StartElement se = ((StartElement) nextEvent);
				this.elementURI = se.getNamespaceURI();
				this.elementLocalName = se.getLocalName();
				// handle element prefixes
				elementPrefix = decodeQNamePrefix(this.elementURI);
				// step forward in current rule (replace rule at the top)
				replaceRuleAtTheTop(nextRule);
			} else if (nextEventType == EventType.START_ELEMENT_NS) {
				// StartEventNS
				StartElementNS seNS = ((StartElementNS) nextEvent);
				this.elementURI = seNS.getNamespaceURI();
				// decode local-name
				this.elementLocalName = readLocalName(elementURI);
				// handle element prefixes
				elementPrefix = decodeQNamePrefix(this.elementURI);
				// step forward in current rule (replace rule at the top)
				replaceRuleAtTheTop(nextRule);
			} else if (nextEventType == EventType.START_ELEMENT_GENERIC) {
				// decode uri & local-name
				decodeStartElementExpandedName();
				// handle element prefixes
				elementPrefix = decodeQNamePrefix(elementURI);
				Rule tmpStorage = currentRule;
				// step forward in current rule (replace rule at the top)
				replaceRuleAtTheTop(nextRule);
				// learn start-element ?
				tmpStorage.learnStartElement(elementURI, elementLocalName);
			} else {
				assert (nextEventType == EventType.START_ELEMENT_GENERIC_UNDECLARED);
				// decode uri & local-name
				decodeStartElementExpandedName();
				// handle element prefixes
				elementPrefix = decodeQNamePrefix(this.elementURI);
				// learn start-element ?
				currentRule.learnStartElement(elementURI, elementLocalName);
				// step forward in current rule (replace rule at the top)
				replaceRuleAtTheTop(currentRule.getElementContentRule());
			}

			pushElementContext(elementURI, elementLocalName);
			pushElementRule(isGenericSE);
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	public void decodeNamespaceDeclaration() throws EXIException {
		try {
			// prefix mapping
			nsURI = readUri();
			nsPrefix = readPrefix(nsURI);
			boolean local_element_ns = channel.decodeBoolean();
			if (local_element_ns) {
				this.elementPrefix = nsPrefix;
			}

			namespaces.declarePrefix(nsPrefix, nsURI);
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	public void decodeAttribute() throws EXIException {
		try {
			// structure
			Datatype dtAT = decodeAttributeStructureOnly();
			// content
			if (dtAT == null) {
				// xsi cases --> it has been already taken care of
			} else {
				pushAttributeContext(attributeURI, attributeLocalName);
				attributeValue = typeDecoder.readValue(dtAT, context, channel);
				popAttributeContext();
			}
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	public void decodeCharacters() throws EXIException {
		try {
			// structure
			Datatype dtCH = decodeCharactersStructureOnly();
			// content
			characters = typeDecoder.readValue(dtCH, context, channel);
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	public void decodeEndElement() throws EXIException {
		// set ee information before popping context
		this.elementURI = context.namespaceURI;
		this.elementLocalName = context.localName;

		if (nextEventType == EventType.END_ELEMENT_UNDECLARED) {
			// learn end-element event ?
			currentRule.learnEndElement();
		}

		// pop stack items
		popElementContext();
		popElementRule();
	}

	public void decodeEndDocument() throws EXIException {
		assert (this.openRules.size() == 1);
	}

	public void decodeDocType() throws EXIException {
		try {
			// decode name, public, system, text AS string
			docTypeName = new String(channel.decodeString());
			docTypePublicID = new String(channel.decodeString());
			docTypeSystemID = new String(channel.decodeString());
			docTypeText = new String(channel.decodeString());
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	public void decodeEntityReference() throws EXIException {
		try {
			// decode name AS string
			entityReferenceName = new String(channel.decodeString());
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	public void decodeComment() throws EXIException {
		try {
			comment = channel.decodeString();

			// step forward
			replaceRuleAtTheTop(currentRule.getElementContentRule());
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	public void decodeProcessingInstruction() throws EXIException {
		try {
			// target & data
			piTarget = new String(channel.decodeString());
			piData = new String(channel.decodeString());

			// step forward
			replaceRuleAtTheTop(currentRule.getElementContentRule());
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

}
