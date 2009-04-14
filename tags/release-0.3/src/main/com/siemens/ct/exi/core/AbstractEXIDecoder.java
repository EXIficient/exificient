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
import java.util.Enumeration;

import javax.xml.XMLConstants;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.EXIDecoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.event.Attribute;
import com.siemens.ct.exi.grammar.event.Characters;
import com.siemens.ct.exi.grammar.event.Event;
import com.siemens.ct.exi.grammar.event.EventType;
import com.siemens.ct.exi.grammar.event.StartElement;
import com.siemens.ct.exi.grammar.rule.Rule;
import com.siemens.ct.exi.grammar.rule.SchemaInformedRule;
import com.siemens.ct.exi.io.block.DecoderBlock;
import com.siemens.ct.exi.io.channel.BitDecoderChannel;
import com.siemens.ct.exi.util.MethodsBag;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20090414
 */

public abstract class AbstractEXIDecoder extends AbstractEXICoder implements
		EXIDecoder {
	// next event
	protected Event nextEvent;
	protected EventType nextEventType;
	protected int ec;

	// decoder stream
	protected InputStream is;
	protected DecoderBlock block;

	// current values
	protected String elementURI;
	protected String elementLocalName;
	protected String elementPrefix;
	protected String attributeURI;
	protected String attributeLocalName;
	protected String attributePrefix;

	protected String attributeValue;
	protected String xsiTypeUri;
	protected String xsiTypeName;
	protected boolean xsiNil;
	protected String xsiNilDeviation;
	protected String characters;
	protected String docTypeName;
	protected String docTypePublicID;
	protected String docTypeSystemID;
	protected String docTypeText;
	protected String entityReferenceName;
	protected String comment;
	protected String nsURI;
	protected String nsPrefix;
	protected String piTarget;
	protected String piData;

	// namespaces/prefixes
	protected boolean preservePrefixes;

	public AbstractEXIDecoder(EXIFactory exiFactory) {
		super(exiFactory);

		preservePrefixes = exiFactory.getFidelityOptions().isFidelityEnabled(
				FidelityOptions.FEATURE_PREFIX);
	}

	@Override
	protected void initForEachRun() throws EXIException {
		super.initForEachRun();

		try {
			block = exiFactory.createDecoderBlock(is);
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	public void setInputStream(InputStream is, boolean exiBodyOnly)
			throws EXIException {
		this.is = is;

		// buffer stream if not already
		// TODO is there a *nice* way to detect whether a stream is buffered
		if (!(is instanceof BufferedInputStream)) {
			this.is = new BufferedInputStream(is);
		}

		if (!exiBodyOnly) {
			// parse header (bit-wise BUT byte padded!)
			BitDecoderChannel headerChannel = new BitDecoderChannel(is);
			EXIHeader.parse(headerChannel);
		}

		initForEachRun();
	}

	protected void decodeEventCode() throws EXIException {
		ec = decode1stLevelEventCode();

		if (ec == Constants.NOT_FOUND) {
			// 2nd level ?
			int ec2 = decode2ndLevelEventCode();

			if (ec2 == Constants.NOT_FOUND) {
				// 3rd level
				int ec3 = decode3rdLevelEventCode();
				nextEventType = currentRule.get3rdLevelEvent(ec3,
						fidelityOptions);

				// un-set event
				nextEvent = null;
			} else {
				nextEventType = currentRule.get2ndLevelEvent(ec2,
						fidelityOptions);

				if (nextEventType == EventType.ATTRIBUTE_INVALID_VALUE) {
					updateInvalidValueAttribute();
				} else {
					// un-set event
					nextEvent = null;
				}
			}
		} else {
			nextEvent = currentRule.get1stLevelEvent(ec);
			nextEventType = nextEvent.getEventType();
		}
	}

	protected void updateInvalidValueAttribute() throws EXIException {
		SchemaInformedRule sir = (SchemaInformedRule) currentRule;

		int ec3AT;
		try {
			ec3AT = block.readEventCode(MethodsBag.getCodingLength(sir
					.getNumberOfSchemaDeviatedAttributes()));
		} catch (IOException e) {
			throw new EXIException(e);
		}

		if (ec3AT < (sir.getNumberOfSchemaDeviatedAttributes() - 1)) {
			// deviated attribute
			ec = ec3AT + sir.getLeastAttributeEventCode();
			nextEvent = currentRule.get1stLevelEvent(ec);
		} else if (ec3AT == (sir.getNumberOfSchemaDeviatedAttributes() - 1)) {
			// deviated xsi:nil
			nextEventType = EventType.ATTRIBUTE_XSI_NIL_DEVIATION;
		} else {
			throw new EXIException(
					"Error occured while decoding deviated attribute");
		}
	}

	protected int decode1stLevelEventCode() throws EXIException {
		try {
			int level1 = block.readEventCode(currentRule
					.get1stLevelEventCodeLength(fidelityOptions));

			if (currentRule.hasSecondOrThirdLevel(fidelityOptions)) {
				// not strict
				return (level1 == currentRule.getNumberOfEvents() ? Constants.NOT_FOUND
						: level1);
			} else {
				// strict (in the sense of no 2nd event-code level)
				return level1;
			}
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	protected int decode2ndLevelEventCode() throws EXIException {
		try {
			int ch2 = currentRule.get2ndLevelCharacteristics(fidelityOptions);
			int level2 = block.readEventCode(MethodsBag.getCodingLength(ch2));

			if (currentRule.get3rdLevelCharacteristics(fidelityOptions) > 0) {
				return (level2 < (ch2 - 1) ? level2 : Constants.NOT_FOUND);
			} else {
				return (level2 < ch2 ? level2 : Constants.NOT_FOUND);
			}
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	protected int decode3rdLevelEventCode() throws EXIException {
		try {
			int ch3 = currentRule.get3rdLevelCharacteristics(fidelityOptions);
			return block.readEventCode(MethodsBag.getCodingLength(ch3));
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	protected void decodeStartDocumentStructure() throws EXIException {
		// step forward
		replaceRuleAtTheTop(currentRule.get1stLevelRule(ec));
	}

	protected String decodeQNamePrefix(String uri) throws EXIException {
		try {
			String prefix = null;
			if (preservePrefixes) {
				@SuppressWarnings("unchecked")
				Enumeration<String> validPrefixes = namespaces.getPrefixes(uri);

				if (validPrefixes.hasMoreElements()) {
					int numberOfPrefixes = 0;
					do {
						validPrefixes.nextElement();
						numberOfPrefixes++;
					} while (validPrefixes.hasMoreElements());

					if (numberOfPrefixes > 1) {
						int id;

						id = block.readEventCode(MethodsBag
								.getCodingLength(numberOfPrefixes));

						@SuppressWarnings("unchecked")
						Enumeration<String> validPrefixes2 = namespaces
								.getPrefixes(uri);
						while (id != 0) {
							validPrefixes2.nextElement();
							id--;
						}
						prefix = validPrefixes2.nextElement();

					}
				} else {
					// no previous NS mapping in charge
				}
			}
			return prefix;
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	protected void decodeStartElementStructure() throws EXIException {
		// StartEvent
		this.elementURI = ((StartElement) nextEvent).getNamespaceURI();
		this.elementLocalName = ((StartElement) nextEvent).getLocalPart();

		// handle element prefixes
		elementPrefix = decodeQNamePrefix(this.elementURI);

		// step forward in current rule (replace rule at the top)
		replaceRuleAtTheTop(currentRule.get1stLevelRule(ec));

		// update grammars etc.
		pushRule(elementURI, elementLocalName);
		pushScope(elementURI, elementLocalName);
	}

	protected void decodeStartElementGenericStructure() throws EXIException {
		// decode uri & local-name
		decodeStartElementExpandedName();

		// handle element prefixes
		elementPrefix = decodeQNamePrefix(elementURI);

		Rule tmpStorage = currentRule;

		// step forward in current rule (replace rule at the top)
		replaceRuleAtTheTop(currentRule.get1stLevelRule(ec));

		// learn start-element ?
		tmpStorage.learnStartElement(elementURI, elementLocalName);

		// update grammars etc.
		pushRule(elementURI, elementLocalName);
		pushScope(elementURI, elementLocalName);
	}

	protected void decodeStartElementGenericUndeclaredStructure()
			throws EXIException {
		// decode uri & local-name
		decodeStartElementExpandedName();

		// handle element prefixes
		elementPrefix = decodeQNamePrefix(this.elementURI);

		// learn start-element ?
		currentRule.learnStartElement(elementURI, elementLocalName);

		// step forward in current rule (replace rule at the top)
		replaceRuleAtTheTop(currentRule.getElementContentRuleForUndeclaredSE());

		// update grammars etc.
		pushRule(elementURI, elementLocalName);
		pushScope(elementURI, elementLocalName);
	}

	protected void decodeStartElementExpandedName() throws EXIException {
		try {
			// decode uri & local-name
			this.elementURI = block.readUri();
			this.elementLocalName = block.readLocalName(elementURI);
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	@Override
	protected void pushScope(String uri, String localName) {
		super.pushScope(uri, localName);

		// reset local-element-ns prefix
		elementPrefix = null;
	}

	protected void decodeNamespaceDeclarationStructure() throws EXIException {
		try {
			// prefix mapping
			nsURI = block.readUri();
			nsPrefix = block.readPrefix(nsURI);
			boolean local_element_ns = block.readBoolean();
			if (local_element_ns) {
				this.elementPrefix = nsPrefix;
			}

			namespaces.declarePrefix(nsPrefix, nsURI);
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	protected Attribute decodeAttributeStructure() throws EXIException {
		// Attribute
		Attribute at = ((Attribute) nextEvent);
		this.attributeURI = at.getNamespaceURI();
		this.attributeLocalName = at.getLocalPart();

		// handle attribute prefix
		attributePrefix = decodeQNamePrefix(this.attributeURI);

		// step forward in current rule (replace rule at the top)
		replaceRuleAtTheTop(currentRule.get1stLevelRule(ec));

		return at;
	}

	protected void decodeAttributeGenericStructure() throws EXIException {
		// decode structure
		decodeAttributeGenericUndeclaredStructure();

		// handle attribute prefix
		attributePrefix = decodeQNamePrefix(this.attributeURI);

		// step forward in current rule (replace rule at the top)
		replaceRuleAtTheTop(currentRule.get1stLevelRule(ec));
	}

	protected void decodeAttributeGenericUndeclaredStructure()
			throws EXIException {
		try {
			// decode uri & local-name
			this.attributeURI = block.readUri();
			this.attributeLocalName = block.readLocalName(attributeURI);

			// handle attribute prefix
			attributePrefix = decodeQNamePrefix(this.attributeURI);

			if (attributeURI
					.equals(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI)
					&& (attributeLocalName.equals(Constants.XSI_NIL) || attributeLocalName
							.equals(Constants.XSI_TYPE))) {
				// no learning for xsi:type or xsi:nil
			} else {
				// update grammar
				currentRule.learnAttribute(attributeURI, attributeLocalName);
			}
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	protected void decodeAttributeXsiType() throws EXIException {
		try {
			// decode type
			xsiTypeUri = block.readUri();
			xsiTypeName = block.readLocalName(xsiTypeUri);
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	protected void decodeAttributeXsiNil() throws EXIException {
		try {
			// decode nil
			this.xsiNil = block.readBoolean();
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	protected void decodeAttributeXsiNilDeviation() throws EXIException {
		try {
			// decode nil as string
			this.xsiNilDeviation = block.readString();
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	protected Characters decodeCharactersStructure() throws IOException {
		// step forward
		replaceRuleAtTheTop(currentRule.get1stLevelRule(ec));

		return (Characters) nextEvent;
	}

	protected void decodeCharactersGenericStructure() {
		replaceRuleAtTheTop(currentRule.get1stLevelRule(ec));
	}

	protected void decodeCharactersUndeclaredStructure() {
		// learn character event ?
		currentRule.learnCharacters();

		// step forward in current rule (replace rule at the top)
		replaceRuleAtTheTop(currentRule.getElementContentRule());
	}

	protected void decodeEndElementStructure() {
		// pop top rule & scope
		popRule();
		popScope();
	}

	protected void decodeEndElementUndeclaredStructure() {
		// learn end-element event ?
		currentRule.learnEndElement();

		// pop stack items
		decodeEndElementStructure();
	}

	protected void decodeEndDocumentStructure() throws EXIException {
		popRule();
	}

	protected void decodeDocTypeStructure() throws EXIException {
		try {
			// decode name, public, system, text AS string
			docTypeName = block.readString();
			docTypePublicID = block.readString();
			docTypeSystemID = block.readString();
			docTypeText = block.readString();
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	protected void decodeEntityReferenceStructure() throws EXIException {
		try {
			// decode name AS string
			entityReferenceName = block.readString();
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	protected void decodeCommentStructure() throws EXIException {
		try {
			comment = block.readString();

			// step forward
			replaceRuleAtTheTop(currentRule.getElementContentRule());
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	protected void decodeProcessingInstructionStructure() throws EXIException {
		try {
			// target & data
			piTarget = block.readString();
			piData = block.readString();

			// step forward
			replaceRuleAtTheTop(currentRule.getElementContentRule());
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	public String getElementURI() {
		return elementURI;
	}

	public String getElementLocalName() {
		return elementLocalName;
	}

	public String getElementPrefix() {
		return elementPrefix;
	}

	public String getAttributeURI() {
		return attributeURI;
	}

	public String getAttributeLocalName() {
		return attributeLocalName;
	}
	
	public String getAttributePrefix() {
		return attributePrefix;
	}

	public String getAttributeValue() {
		return attributeValue;
	}

	public String getXsiTypeUri() {
		return this.xsiTypeUri;
	}

	public String getXsiTypeName() {
		return this.xsiTypeName;
	}

	public boolean getXsiNil() {
		return xsiNil;
	}

	public String getXsiNilDeviation() {
		return xsiNilDeviation;
	}

	public String getCharacters() {
		return characters;
	}

	public String getDocTypeName() {
		return docTypeName;
	}

	public String getDocTypePublicID() {
		return docTypePublicID;
	}

	public String getDocTypeSystemID() {
		return docTypeSystemID;
	}

	public String getDocTypeText() {
		return docTypeText;
	}

	public String getEntityReferenceName() {
		return entityReferenceName;
	}

	public String getComment() {
		return comment;
	}

	public String getNSUri() {
		return nsURI;
	}

	public String getNSPrefix() {
		return nsPrefix;
	}

	public String getPITarget() {
		return piTarget;
	}

	public String getPIData() {
		return piData;
	}

}
