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

package com.siemens.ct.exi.core;

import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.EXIBodyDecoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.core.container.DocType;
import com.siemens.ct.exi.core.container.NamespaceDeclaration;
import com.siemens.ct.exi.core.container.ProcessingInstruction;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.EventInformation;
import com.siemens.ct.exi.grammar.event.Attribute;
import com.siemens.ct.exi.grammar.event.AttributeNS;
import com.siemens.ct.exi.grammar.event.Characters;
import com.siemens.ct.exi.grammar.event.Event;
import com.siemens.ct.exi.grammar.event.EventType;
import com.siemens.ct.exi.grammar.event.StartElement;
import com.siemens.ct.exi.grammar.event.StartElementNS;
import com.siemens.ct.exi.grammar.rule.Rule;
import com.siemens.ct.exi.grammar.rule.SchemaInformedFirstStartTagRule;
import com.siemens.ct.exi.grammar.rule.SchemaInformedRule;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.types.TypeDecoder;
import com.siemens.ct.exi.util.MethodsBag;
import com.siemens.ct.exi.util.xml.QNameUtilities;
import com.siemens.ct.exi.values.BooleanValue;
import com.siemens.ct.exi.values.QNameValue;
import com.siemens.ct.exi.values.Value;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.6
 */

public abstract class AbstractEXIBodyDecoder extends AbstractEXIBodyCoder
		implements EXIBodyDecoder {

	protected final EXIHeaderDecoder exiHeader;

	// next event
	protected Event nextEvent;
	protected Rule nextRule;
	protected EventType nextEventType;

	// decoder stream
	protected DecoderChannel channel;

	// namespaces/prefixes
	protected int createdPfxCnt;

	// Type Decoder (including string decoder etc.)
	protected TypeDecoder typeDecoder;

	// current AT values
	protected QName attributeQName;
	protected String attributePrefix;
	protected Value attributeValue;

	public AbstractEXIBodyDecoder(EXIFactory exiFactory) throws EXIException {
		super(exiFactory);

		exiHeader = new EXIHeaderDecoder();
	}

	@Override
	protected void initFactoryInformation() throws EXIException {
		super.initFactoryInformation();

		typeDecoder = exiFactory.createTypeDecoder();
	}

	@Override
	protected void initForEachRun() throws EXIException, IOException {
		super.initForEachRun();

		// namespaces/prefixes
		createdPfxCnt = 0;

		// clear string values etc.
		typeDecoder.clear();
	}

	protected final EventType decodeEventCode() throws EXIException,
			IOException {
		// 1st level
		int codeLength = currentRule
				.get1stLevelEventCodeLength(fidelityOptions);
		int ec = codeLength == 0 ? 0 : channel
				.decodeNBitUnsignedInteger(codeLength);

		assert (ec >= 0);

		if (ec < currentRule.getNumberOfEvents()) {
			// 1st level
			EventInformation ei = currentRule.lookFor(ec);
			nextEvent = ei.event;
			nextRule = ei.next;
			nextEventType = nextEvent.getEventType();
		} else {
			// 2nd level ?
			int ec2 = decode2ndLevelEventCode();

			if (ec2 == Constants.NOT_FOUND) {
				// 3rd level
				int ec3 = decode3rdLevelEventCode();
				nextEventType = currentRule.get3rdLevelEvent(ec3,
						fidelityOptions);

				// un-set event
				nextEvent = null;
				nextRule = null;
			} else {
				nextEventType = currentRule.get2ndLevelEvent(ec2,
						fidelityOptions);

				if (nextEventType == EventType.ATTRIBUTE_INVALID_VALUE) {
					updateInvalidValueAttribute(ec);
				} else {
					// un-set event
					nextEvent = null;
					nextRule = null;
				}
			}
		}

		return nextEventType;
	}

	public String getAttributePrefix() {
		return attributePrefix;
	}

	public String getAttributeQNameAsString() {
		return QNameUtilities.getQualifiedName(attributeQName.getLocalPart(),
				attributePrefix);
	}

	public Value getAttributeValue() {
		return attributeValue;
	}

	protected void updateInvalidValueAttribute(int ec) throws EXIException {
		SchemaInformedRule sir = (SchemaInformedRule) currentRule;

		int ec3AT;
		try {
			ec3AT = channel.decodeNBitUnsignedInteger(MethodsBag
					.getCodingLength(sir.getNumberOfDeclaredAttributes() + 1));
		} catch (IOException e) {
			throw new EXIException(e);
		}

		if (ec3AT < (sir.getNumberOfDeclaredAttributes())) {
			// deviated attribute
			ec = ec3AT + sir.getLeastAttributeEventCode();
			EventInformation ei = currentRule.lookFor(ec);
			nextEvent = ei.event;
			nextRule = ei.next;
			// nextEventRule = currentRule.get1stLevelEventRule(ec);
		} else if (ec3AT == (sir.getNumberOfDeclaredAttributes())) {
			// ANY deviated attribute (no qname present)
			nextEventType = EventType.ATTRIBUTE_ANY_INVALID_VALUE;
		} else {
			throw new EXIException(
					"Error occured while decoding deviated attribute");
		}
	}

	protected int decode2ndLevelEventCode() throws EXIException, IOException {
		int ch2 = currentRule.get2ndLevelCharacteristics(fidelityOptions);
		int level2 = channel.decodeNBitUnsignedInteger(MethodsBag
				.getCodingLength(ch2));

		if (currentRule.get3rdLevelCharacteristics(fidelityOptions) > 0) {
			return (level2 < (ch2 - 1) ? level2 : Constants.NOT_FOUND);
		} else {
			return (level2 < ch2 ? level2 : Constants.NOT_FOUND);
		}
	}

	protected int decode3rdLevelEventCode() throws EXIException, IOException {
		int ch3 = currentRule.get3rdLevelCharacteristics(fidelityOptions);
		return channel.decodeNBitUnsignedInteger(MethodsBag
				.getCodingLength(ch3));
	}

	protected final void decodeStartDocumentStructure() throws EXIException {
		// update current rule
		currentRule = currentRule.lookFor(0).next;
	}

	protected final void decodeEndDocumentStructure() throws EXIException,
			IOException {
		// assert (elementContextStack.size() == 1);
	}

	protected final QName decodeStartElementStructure() throws IOException {
		assert (nextEventType == EventType.START_ELEMENT);
		// StartElement
		StartElement se = ((StartElement) nextEvent);
		// push element
		pushElement(se, nextRule);
		// handle element prefix
		handleElementPrefix();

		return se.getQName();
	}

	protected final QName decodeStartElementNSStructure() throws IOException {
		assert (nextEventType == EventType.START_ELEMENT_NS);
		// StartElementNS
		StartElementNS seNS = ((StartElementNS) nextEvent);
		// decode local-name
		QName elementQName = qnameDatatype.decodeLocalName(
				seNS.getNamespaceURI(), channel);
		// next SE ...
		StartElement nextSE = getGenericStartElement(elementQName);
		// push element
		pushElement(nextSE, nextRule);
		// handle element prefix
		handleElementPrefix();

		return elementQName;
	}

	protected final QName decodeStartElementGenericStructure()
			throws IOException {
		assert (nextEventType == EventType.START_ELEMENT_GENERIC);
		// decode uri & local-name
		QName elementQName = qnameDatatype.decodeQName(channel);
		// next SE ...
		StartElement nextSE = getGenericStartElement(elementQName);
		// learn start-element ?
		currentRule.learnStartElement(nextSE);
		// push element
		pushElement(nextSE, nextRule.getElementContentRule());
		// handle element prefix
		handleElementPrefix();

		return elementQName;
	}

	protected final QName decodeStartElementGenericUndeclaredStructure()
			throws IOException {
		assert (nextEventType == EventType.START_ELEMENT_GENERIC_UNDECLARED);
		// decode uri & local-name
		QName elementQName = qnameDatatype.decodeQName(channel);

		// next SE ...
		StartElement nextSE = getGenericStartElement(elementQName);
		// learn start-element ?
		currentRule.learnStartElement(nextSE);
		// push element
		pushElement(nextSE, currentRule.getElementContentRule());
		// handle element prefix
		handleElementPrefix();

		return elementQName;
	}

	protected final ElementContext decodeEndElementStructure()
			throws EXIException, IOException {
		return popElement();
	}

	protected final ElementContext decodeEndElementUndeclaredStructure()
			throws EXIException, IOException {
		// learn end-element event ?
		currentRule.learnEndElement();
		// pop element
		return popElement();
	}

	/*
	 * Handles and xsi:nil attributes
	 */
	protected final void decodeAttributeXsiNilStructure() throws EXIException,
			IOException {
		attributeQName = XSI_NIL;
		// handle AT prefix
		handleAttributePrefix();

		attributeValue = typeDecoder.readValue(booleanDatatype, XSI_NIL,
				channel);
		if (!preservePrefix) {
			checkPrefixMapping(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
		}
		boolean xsiNil;

		if (attributeValue instanceof BooleanValue) {
			BooleanValue bv = (BooleanValue) attributeValue;
			xsiNil = bv.toBoolean();
		} else {
			// parse string value again (lexical value mode)
			booleanDatatype.isValid(attributeValue.toString());
			xsiNil = booleanDatatype.getBoolean();
		}

		if (xsiNil && currentRule.isSchemaInformed()) {
			// jump to typeEmpty
			currentRule = ((SchemaInformedFirstStartTagRule) currentRule)
					.getTypeEmpty();
		}
	}

	/*
	 * Handles and xsi:type attributes
	 */
	protected final void decodeAttributeXsiTypeStructure() throws EXIException,
			IOException {
		attributeQName = XSI_TYPE;
		// handle AT prefix
		handleAttributePrefix();

		if (this.preserveLexicalValues) {
			attributeValue = typeDecoder.readValue(qnameDatatype, XSI_TYPE,
					channel);
		} else {
			// typed
			attributeValue = qnameDatatype.readValue(channel, null, XSI_TYPE);
		}

		// type qname
		QName xsiTypeQName;
		if (attributeValue instanceof QNameValue) {
			QNameValue qnv = (QNameValue) attributeValue;
			xsiTypeQName = qnv.toQName();
			if (!preservePrefix) {
				String pfx = checkPrefixMapping(xsiTypeQName.getNamespaceURI());
				attributeValue = new QNameValue(qnv.toQName(), pfx);
			}
		} else {			
			// parse string value again (lexical value mode)
			if (qnameDatatype.isValid(attributeValue.toString())) {
				xsiTypeQName = qnameDatatype.getQName();
				if (!preservePrefix) {
					String pfx = qnameDatatype.getPrefix();

					declarePrefix(pfx, xsiTypeQName.getNamespaceURI());
					attributeValue = new QNameValue(xsiTypeQName, pfx);
				}
			} else {
				throw new EXIException("[EXI] no valid xsi:type='"
						+ attributeValue + "'");
			}
		}

		// update grammar according to given xsi:type
		SchemaInformedRule tg = grammar.getTypeGrammar(xsiTypeQName);

		// grammar exists ?
		if (tg != null) {
			// update current rule
			currentRule = tg;
		}
	}

	protected final void handleElementPrefix() throws IOException {
		if (preservePrefix) {
			elementContext.prefix = qnameDatatype.decodeQNamePrefix(
					elementContext.qname.getNamespaceURI(), channel);
			// Note: IF elementPrefix is still null it will be determined by a
			// subsequently following NS event
		} else {
			// determine element prefix
			elementContext.prefix = checkPrefixMapping(elementContext.qname
					.getNamespaceURI());
		}
	}

	protected final void handleAttributePrefix() throws IOException {
		if (preservePrefix) {
			attributePrefix = qnameDatatype.decodeQNamePrefix(
					attributeQName.getNamespaceURI(), channel);
		} else {
			attributePrefix = checkPrefixMapping(attributeQName
					.getNamespaceURI());
		}
	}

	private final String checkPrefixMapping(String uri) {
		assert (!preservePrefix);
		String pfx = getPrefix(uri);

		if (pfx == null) {
			// TODO: Use default namespace prefix for first uri?
			pfx = "ns" + createdPfxCnt++;
			declarePrefix(pfx, uri);
		}

		return pfx;
	}

	protected final Datatype decodeAttributeStructure() throws EXIException,
			IOException {
		Attribute at = ((Attribute) nextEvent);
		// qname
		attributeQName = at.getQName();
		// handle attribute prefix
		handleAttributePrefix();

		// update current rule
		currentRule = nextRule;

		return at.getDatatype();
	}

	protected final void decodeAttributeNSStructure() throws EXIException,
			IOException {
		// AttributeEventNS
		AttributeNS atNS = ((AttributeNS) nextEvent);
		attributeQName = qnameDatatype.decodeLocalName(atNS.getNamespaceURI(),
				channel);
		// handle attribute prefix
		handleAttributePrefix();
		// update current rule
		currentRule = nextRule;
	}

	protected final void decodeAttributeAnyInvalidValueStructure()
			throws EXIException, IOException {
		decodeAttributeGenericStructureOnly();
	}

	protected final void decodeAttributeGenericStructure() throws EXIException,
			IOException {
		// decode structure
		decodeAttributeGenericStructureOnly();

		// update current rule
		currentRule = nextRule;
	}

	protected final void decodeAttributeGenericUndeclaredStructure()
			throws EXIException, IOException {
		decodeAttributeGenericStructureOnly();

		// update grammar
		if (attributeQName.equals(XSI_TYPE)) {
			currentRule.learnAttribute(new Attribute(XSI_TYPE, null,
					qnameDatatype));
		} else {
			currentRule.learnAttribute(new Attribute(attributeQName));
		}
	}

	private final void decodeAttributeGenericStructureOnly()
			throws EXIException, IOException {
		// decode uri & local-name
		attributeQName = qnameDatatype.decodeLocalName(
				qnameDatatype.decodeUri(channel), channel);
		// handle attribute prefix
		handleAttributePrefix();
	}

	protected final Datatype decodeCharactersStructure() throws EXIException {
		assert (nextEventType == EventType.CHARACTERS);
		// update current rule
		currentRule = nextRule;
		return ((Characters) nextEvent).getDatatype();
	}

	protected final void decodeCharactersGenericStructure() throws EXIException {
		assert (nextEventType == EventType.CHARACTERS_GENERIC);
		// update current rule
		currentRule = nextRule;
	}

	protected final void decodeCharactersGenericUndeclaredStructure()
			throws EXIException {
		assert (nextEventType == EventType.CHARACTERS_GENERIC_UNDECLARED);
		// learn character event ?
		currentRule.learnCharacters();
		// update current rule
		currentRule = currentRule.getElementContentRule();
	}

	protected final NamespaceDeclaration decodeNamespaceDeclarationStructure()
			throws EXIException, IOException {
		// prefix mapping
		int uriID = qnameDatatype.decodeUri(channel);
		String nsURI = qnameDatatype.getUriForID(uriID);

		String nsPrefix = qnameDatatype.decodeNamespacePrefix(uriID, channel);

		boolean local_element_ns = channel.decodeBoolean();
		if (local_element_ns) {
			elementContext.prefix = nsPrefix;
		}
		// NS
		NamespaceDeclaration nsDecl = new NamespaceDeclaration(nsURI, nsPrefix);
		declarePrefix(nsDecl);
		return nsDecl;
	}

	protected final char[] decodeEntityReferenceStructure()
			throws EXIException, IOException {
		// decode name AS string
		char[] er = channel.decodeString();
		// update current rule
		currentRule = currentRule.getElementContentRule();
		return er;
	}

	protected final char[] decodeCommentStructure() throws EXIException,
			IOException {
		char[] comment = channel.decodeString();
		// update current rule
		currentRule = currentRule.getElementContentRule();
		return comment;
	}

	protected final ProcessingInstruction decodeProcessingInstructionStructure()
			throws EXIException, IOException {
		// target & data
		String piTarget = new String(channel.decodeString());
		String piData = new String(channel.decodeString());
		// update current rule
		currentRule = currentRule.getElementContentRule();
		return new ProcessingInstruction(piTarget, piData);
	}

	protected final DocType decodeDocTypeStructure() throws EXIException,
			IOException {
		// decode name, public, system, text AS string
		char[] name = channel.decodeString();
		char[] publicID = channel.decodeString();
		char[] systemID = channel.decodeString();
		char[] text = channel.decodeString();
		return new DocType(name, publicID, systemID, text);
	}

	/* ================================= */

	public void decodeStartSelfContainedFragment() throws EXIException,
			IOException {
		throw new RuntimeException("[EXI] SelfContained");
	}

}
