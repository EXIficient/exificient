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

import javax.xml.namespace.QName;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.EXIBodyDecoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.context.CoderContext;
import com.siemens.ct.exi.context.DecoderContext;
import com.siemens.ct.exi.context.DecoderContextImpl;
import com.siemens.ct.exi.context.EvolvingUriContext;
import com.siemens.ct.exi.context.GrammarContext;
import com.siemens.ct.exi.context.GrammarUriContext;
import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.core.container.DocType;
import com.siemens.ct.exi.core.container.NamespaceDeclaration;
import com.siemens.ct.exi.core.container.ProcessingInstruction;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammars.event.Attribute;
import com.siemens.ct.exi.grammars.event.AttributeNS;
import com.siemens.ct.exi.grammars.event.Characters;
import com.siemens.ct.exi.grammars.event.Event;
import com.siemens.ct.exi.grammars.event.EventType;
import com.siemens.ct.exi.grammars.event.StartElement;
import com.siemens.ct.exi.grammars.event.StartElementNS;
import com.siemens.ct.exi.grammars.grammar.Grammar;
import com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTagGrammar;
import com.siemens.ct.exi.grammars.grammar.SchemaInformedGrammar;
import com.siemens.ct.exi.grammars.production.Production;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.types.BuiltIn;
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
 * @version 0.8
 */

public abstract class AbstractEXIBodyDecoder extends AbstractEXIBodyCoder
		implements EXIBodyDecoder {

	protected final EXIHeaderDecoder exiHeader;

	// next event
	protected Event nextEvent;
	protected Grammar nextRule;
	protected EventType nextEventType;

	// decoder stream
	protected DecoderChannel channel;

	// namespaces/prefixes
	protected int createdPfxCnt;
	protected boolean todoDefaultPrefixMapping;

	// Type Decoder (including string decoder etc.)
	protected TypeDecoder typeDecoder;

	// current AT values
	protected QNameContext attributeQNameContext;
	protected String attributePrefix;
	protected Value attributeValue;

	// Decoder Context
	DecoderContext decoderContext;

	public AbstractEXIBodyDecoder(EXIFactory exiFactory) throws EXIException {
		super(exiFactory);
		exiHeader = new EXIHeaderDecoder();
	}

	@Override
	protected void initFactoryInformation() throws EXIException {
		super.initFactoryInformation();

		typeDecoder = exiFactory.createTypeDecoder();
		decoderContext = new DecoderContextImpl(exiFactory.getGrammars()
				.getGrammarContext(), exiFactory.createStringDecoder());
	}

	public CoderContext getCoderContext() {
		return this.decoderContext;
	}

	@Override
	protected void pushElement(Grammar updContextRule, StartElement se) {
		super.pushElement(updContextRule, se);
		if (todoDefaultPrefixMapping && !preservePrefix) {
			GrammarContext gc = this.grammar.getGrammarContext();
			for (int i = 2; i < gc.getNumberOfGrammarUriContexts(); i++) {
				GrammarUriContext guc = gc.getGrammarUriContext(i);
				String pfx;
				if (guc.getNumberOfPrefixes() > 0) {
					pfx = guc.getPrefix(0);
				} else {
					pfx = "ns" + i;
				}
				declarePrefix(pfx, guc.getNamespaceUri());
			}

			// createdPfxCnt += gues.length;
			createdPfxCnt += gc.getNumberOfGrammarUriContexts();
			todoDefaultPrefixMapping = false;
		}
	}

	@Override
	protected void initForEachRun() throws EXIException, IOException {
		super.initForEachRun();

		// namespaces/prefixes
		createdPfxCnt = 0;
		todoDefaultPrefixMapping = true;

		// clear string values etc.
		decoderContext.clear();
	}

	protected final EventType decodeEventCode() throws EXIException,
			IOException {
		// 1st level
		final Grammar currentRule = getCurrentRule();
		int codeLength = currentRule
				.get1stLevelEventCodeLength(fidelityOptions);
		int ec = codeLength == 0 ? 0 : channel
				.decodeNBitUnsignedInteger(codeLength);

		assert (ec >= 0);

		if (ec < currentRule.getNumberOfEvents()) {
			// 1st level
			Production ei = currentRule.lookFor(ec);
			nextEvent = ei.getEvent();
			nextRule = ei.getNextRule();
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
		if (this.preservePrefix) {
			return QNameUtilities.getQualifiedName(
					attributeQNameContext.getLocalName(), this.attributePrefix);
		} else {
			return attributeQNameContext.getDefaultQNameAsString();
		}
	}

	public Value getAttributeValue() {
		return attributeValue;
	}

	protected void updateInvalidValueAttribute(int ec) throws EXIException {
		SchemaInformedGrammar sir = (SchemaInformedGrammar) getCurrentRule();

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
			Production ei = sir.lookFor(ec);
			nextEvent = ei.getEvent();
			nextRule = ei.getNextRule();
		} else if (ec3AT == (sir.getNumberOfDeclaredAttributes())) {
			// ANY deviated attribute (no qname present)
			nextEventType = EventType.ATTRIBUTE_ANY_INVALID_VALUE;
		} else {
			throw new EXIException(
					"Error occured while decoding deviated attribute");
		}
	}

	protected int decode2ndLevelEventCode() throws EXIException, IOException {
		final Grammar currentRule = getCurrentRule();
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
		int ch3 = getCurrentRule().get3rdLevelCharacteristics(fidelityOptions);
		return channel.decodeNBitUnsignedInteger(MethodsBag
				.getCodingLength(ch3));
	}

	protected final void decodeStartDocumentStructure() throws EXIException {
		// update current rule
		updateCurrentRule(getCurrentRule().lookFor(0).getNextRule());
	}

	protected final void decodeEndDocumentStructure() throws EXIException,
			IOException {
	}

	protected final QName decodeStartElementStructure() throws IOException {
		assert (nextEventType == EventType.START_ELEMENT);
		// StartElement
		StartElement se = ((StartElement) nextEvent);
		// push element
		pushElement(nextRule, se);
		// handle element prefix
		handleElementPrefix(se.getQNameContext()); // .getUriContext());

		return se.getQName();
	}

	protected final QName decodeStartElementNSStructure() throws IOException {
		assert (nextEventType == EventType.START_ELEMENT_NS);
		// StartElementNS
		StartElementNS seNS = ((StartElementNS) nextEvent);
		// decode local-name
		EvolvingUriContext uc = decoderContext.getUriContext(seNS
				.getNamespaceUriID());
		QNameContext qnc = this.decoderContext.decodeLocalName(uc, channel);

		// next SE ...
		StartElement nextSE = decoderContext.getGlobalStartElement(qnc);

		// push element
		pushElement(nextRule, nextSE);
		// handle element prefix
		handleElementPrefix(qnc);

		return qnc.getQName();
	}

	protected final QName decodeStartElementGenericStructure()
			throws IOException {
		assert (nextEventType == EventType.START_ELEMENT_GENERIC);
		// decode uri & local-name
		QNameContext qnc = this.decoderContext.decodeQName(channel);

		// next SE ...
		StartElement nextSE = decoderContext.getGlobalStartElement(qnc);

		// learn start-element ?
		getCurrentRule().learnStartElement(nextSE);
		// push element
		pushElement(nextRule.getElementContent(), nextSE);

		// handle element prefix
		handleElementPrefix(qnc);

		return qnc.getQName();
	}

	protected final QName decodeStartElementGenericUndeclaredStructure()
			throws IOException {
		assert (nextEventType == EventType.START_ELEMENT_GENERIC_UNDECLARED);
		// decode uri & local-name
		QNameContext qnc = this.decoderContext.decodeQName(channel);

		// next SE ...
		StartElement nextSE = decoderContext.getGlobalStartElement(qnc);

		// learn start-element ?
		final Grammar currentRule = getCurrentRule();
		currentRule.learnStartElement(nextSE);

		// push element
		pushElement(currentRule.getElementContent(), nextSE);

		// handle element prefix
		handleElementPrefix(qnc);

		return qnc.getQName();
	}

	protected final ElementContext decodeEndElementStructure()
			throws EXIException, IOException {
		return popElement();
	}

	protected final ElementContext decodeEndElementUndeclaredStructure()
			throws EXIException, IOException {
		// learn end-element event ?
		getCurrentRule().learnEndElement();
		// pop element
		return popElement();
	}

	/*
	 * Handles and xsi:nil attributes
	 */
	protected final void decodeAttributeXsiNilStructure() throws EXIException,
			IOException {
		// attributeEnhancedQName = XSI_NIL_ENHANCED;
		attributeQNameContext = decoderContext.getXsiNilContext();
		// handle AT prefix
		handleAttributePrefix(attributeQNameContext);

		// attributeValue = typeDecoder.readValue(booleanDatatype, XSI_NIL,
		// channel);
		if (preserveLexicalValues) {
			// as String
			attributeValue = typeDecoder.readValue(booleanDatatype,
					decoderContext, decoderContext.getXsiNilContext(), channel);
		} else {
			// as Boolean
			attributeValue = booleanDatatype.readValue(decoderContext, null,
					channel);
		}

		boolean xsiNil;

		if (attributeValue instanceof BooleanValue) {
			BooleanValue bv = (BooleanValue) attributeValue;
			xsiNil = bv.toBoolean();
		} else {
			// parse string value again (lexical value mode)
			booleanDatatype.isValid(attributeValue);
			xsiNil = booleanDatatype.getBoolean();
		}

		final Grammar currentRule = getCurrentRule();
		if (xsiNil && currentRule.isSchemaInformed()) {
			// jump to typeEmpty
			updateCurrentRule(((SchemaInformedFirstStartTagGrammar) currentRule)
					.getTypeEmpty());
		}
	}

	/*
	 * Handles and xsi:type attributes
	 */
	protected final void decodeAttributeXsiTypeStructure() throws EXIException,
			IOException {
		attributeQNameContext = decoderContext.getXsiTypeContext();
		// handle AT prefix
		handleAttributePrefix(attributeQNameContext);

		QNameContext qncType = null;

		// read xsi:type content
		if (this.preserveLexicalValues) {
			// assert(preservePrefix); // Note: requirement
			attributeValue = typeDecoder
					.readValue(BuiltIn.DEFAULT_DATATYPE, decoderContext,
							decoderContext.getXsiTypeContext(), channel);
			String sType = attributeValue.toString();
			// extract prefix
			String qncTypePrefix = QNameUtilities.getPrefixPart(sType);

			// URI
			String qnameURI = getURI(qncTypePrefix);

			EvolvingUriContext uc = decoderContext.getUriContext(qnameURI);
			if (uc != null) {
				// local-name
				String qnameLocalName = QNameUtilities.getLocalPart(sType);
				qncType = uc.getQNameContext(qnameLocalName);
			}
		} else {
			// typed
			qncType = decoderContext.decodeQName(channel);
			String qncTypePrefix;
			if (preservePrefix) {
				qncTypePrefix = decoderContext.decodeQNamePrefix(decoderContext
						.getUriContext(qncType.getNamespaceUriID()), channel);
			} else {
				qncTypePrefix = checkPrefixMapping(qncType.getNamespaceUri());
			}
			attributeValue = new QNameValue(qncType.getNamespaceUri(),
					qncType.getLocalName(), qncTypePrefix);
		}

		// update grammar according to given xsi:type
		if (qncType != null && qncType.getTypeGrammar() != null) {
			// update current rule
			updateCurrentRule(qncType.getTypeGrammar());
		}
	}

	protected final void handleElementPrefix(QNameContext qnc)
			throws IOException {
		if (preservePrefix) {
			getElementContext().prefix = decoderContext.decodeQNamePrefix(
					decoderContext.getUriContext(qnc.getNamespaceUriID()),
					channel);
			// Note: IF elementPrefix is still null it will be determined by a
			// subsequently following NS event
		} else {
			// determine element prefix
			getElementContext().prefix = checkPrefixMapping(qnc
					.getNamespaceUri());
		}
	}

	protected final void handleAttributePrefix(QNameContext qnc)
			throws IOException {
		if (preservePrefix) {
			attributePrefix = decoderContext.decodeQNamePrefix(
					decoderContext.getUriContext(qnc.getNamespaceUriID()),
					channel);
		} else {
			attributePrefix = checkPrefixMapping(qnc.getNamespaceUri());
		}
	}

	private final String checkPrefixMapping(String uri) {
		assert (!preservePrefix);

		String pfx = getPrefix(uri);

		if (pfx == null) {
			pfx = "ns" + createdPfxCnt++;
			declarePrefix(pfx, uri);
		}

		return pfx;
	}

	protected final Datatype decodeAttributeStructure() throws EXIException,
			IOException {
		Attribute at = ((Attribute) nextEvent);
		// qname
		attributeQNameContext = at.getQNameContext();
		// handle attribute prefix
		handleAttributePrefix(attributeQNameContext);

		// update current rule
		updateCurrentRule(nextRule);

		return at.getDatatype();
	}

	protected final void decodeAttributeNSStructure() throws EXIException,
			IOException {
		// AttributeEventNS
		AttributeNS atNS = ((AttributeNS) nextEvent);
		EvolvingUriContext uc = decoderContext.getUriContext(atNS
				.getNamespaceUriID());
		attributeQNameContext = decoderContext.decodeLocalName(uc, channel);

		// handle attribute prefix
		handleAttributePrefix(attributeQNameContext);
		// update current rule
		updateCurrentRule(nextRule);
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
		updateCurrentRule(nextRule);
	}

	protected final void decodeAttributeGenericUndeclaredStructure()
			throws EXIException, IOException {
		decodeAttributeGenericStructureOnly();
		getCurrentRule().learnAttribute(new Attribute(attributeQNameContext));
	}

	private final void decodeAttributeGenericStructureOnly()
			throws EXIException, IOException {
		// decode uri & local-name
		this.attributeQNameContext = decoderContext.decodeQName(channel);

		// handle attribute prefix
		handleAttributePrefix(attributeQNameContext);
	}

	protected final Datatype decodeCharactersStructure() throws EXIException {
		assert (nextEventType == EventType.CHARACTERS);
		// update current rule
		updateCurrentRule(nextRule);
		return ((Characters) nextEvent).getDatatype();
	}

	protected final void decodeCharactersGenericStructure() throws EXIException {
		assert (nextEventType == EventType.CHARACTERS_GENERIC);
		// update current rule
		updateCurrentRule(nextRule);
	}

	protected final void decodeCharactersGenericUndeclaredStructure()
			throws EXIException {
		assert (nextEventType == EventType.CHARACTERS_GENERIC_UNDECLARED);
		// learn character event ?
		final Grammar currentRule = getCurrentRule();
		currentRule.learnCharacters();
		// update current rule
		updateCurrentRule(currentRule.getElementContent());
	}

	protected final NamespaceDeclaration decodeNamespaceDeclarationStructure()
			throws EXIException, IOException {
		// prefix mapping
		EvolvingUriContext euc = decoderContext.decodeUri(channel);
		String nsPrefix = decoderContext.decodeNamespacePrefix(euc, channel);

		boolean local_element_ns = channel.decodeBoolean();
		if (local_element_ns) {
			getElementContext().prefix = nsPrefix;
		}
		// NS
		NamespaceDeclaration nsDecl = new NamespaceDeclaration(
				euc.getNamespaceUri(), nsPrefix);
		declarePrefix(nsDecl);
		return nsDecl;
	}

	protected final char[] decodeEntityReferenceStructure()
			throws EXIException, IOException {
		// decode name AS string
		char[] er = channel.decodeString();
		// update current rule
		updateCurrentRule(getCurrentRule().getElementContent());
		return er;
	}

	protected final char[] decodeCommentStructure() throws EXIException,
			IOException {
		char[] comment = channel.decodeString();
		// update current rule
		updateCurrentRule(getCurrentRule().getElementContent());
		return comment;
	}

	protected final ProcessingInstruction decodeProcessingInstructionStructure()
			throws EXIException, IOException {
		// target & data
		String piTarget = new String(channel.decodeString());
		String piData = new String(channel.decodeString());
		// update current rule
		updateCurrentRule(getCurrentRule().getElementContent());
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
