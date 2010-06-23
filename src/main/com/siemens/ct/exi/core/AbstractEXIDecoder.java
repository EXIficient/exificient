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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import com.siemens.ct.exi.CodingMode;
import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.EXIDecoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.FidelityOptions;
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
import com.siemens.ct.exi.grammar.rule.SchemaInformedRule;
import com.siemens.ct.exi.io.channel.BitDecoderChannel;
import com.siemens.ct.exi.io.channel.ByteDecoderChannel;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.types.BuiltIn;
import com.siemens.ct.exi.types.TypeDecoder;
import com.siemens.ct.exi.util.MethodsBag;
import com.siemens.ct.exi.values.BooleanValue;
import com.siemens.ct.exi.values.QNameValue;
import com.siemens.ct.exi.values.Value;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.4.20090414
 */

public abstract class AbstractEXIDecoder extends AbstractEXICoder implements
		EXIDecoder {

	// next event
	protected Event nextEvent;
	protected Rule nextRule;
	protected EventType nextEventType;
	protected int ec;

	// decoder stream
	protected InputStream is;
	protected DecoderChannel channel;

	// namespaces/prefixes
	Map<String, String> uriToPrefix;
	protected int createdPfxCnt;

	// Type Decoder (including string decoder etc.)
	protected TypeDecoder typeDecoder;

	// current values
	protected QName elementQName;
	protected String elementPrefix;
	protected QName attributeQName;
	protected String attributePrefix;
	protected Value attributeValue;
	protected Value characters;
	protected String docTypeName;
	protected String docTypePublicID;
	protected String docTypeSystemID;
	protected String docTypeText;
	protected String entityReferenceName;
	protected char[] comment;
	protected String nsURI;
	protected String nsPrefix;
	protected String piTarget;
	protected String piData;

	public AbstractEXIDecoder(EXIFactory exiFactory) throws EXIException {
		super(exiFactory);

		// namespaces/prefixes
		uriToPrefix = new HashMap<String, String>();

		// init once
		typeDecoder = exiFactory.createTypeDecoder();
	}

	@Override
	protected void initForEachRun() throws EXIException, IOException {
		super.initForEachRun();

		// namespaces/prefixes
		initPrefixes();

		// clear string values etc.
		typeDecoder.clear();
	}

	protected void initPrefixes() {
		uriToPrefix.clear();
		// default NS
		uriToPrefix.put(XMLConstants.NULL_NS_URI,
				XMLConstants.DEFAULT_NS_PREFIX);
		// "http://www.w3.org/XML/1998/namespace"
		uriToPrefix.put(XMLConstants.XML_NS_URI, XMLConstants.XML_NS_PREFIX);
		// "http://www.w3.org/1999/xhtml"
		uriToPrefix.put("http://www.w3.org/1999/xhtml", "");
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

	protected final void decodeEventCode() throws EXIException, IOException {
		// 1st level
		int codeLength = currentRule
				.get1stLevelEventCodeLength(fidelityOptions);
		ec = codeLength > 0 ? channel.decodeNBitUnsignedInteger(codeLength) : 0;

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
				// nextEventRule = null;
			} else {
				nextEventType = currentRule.get2ndLevelEvent(ec2,
						fidelityOptions);

				if (nextEventType == EventType.ATTRIBUTE_INVALID_VALUE) {
					updateInvalidValueAttribute();
				} else {
					// un-set event
					nextEvent = null;
					nextRule = null;
					// nextEventRule = null;
				}
			}
		}

		// if (openElement) {
		// switch (nextEventType) {
		// /* ELEMENT CONTENT EVENTS */
		// case START_ELEMENT:
		// case START_ELEMENT_NS:
		// case START_ELEMENT_GENERIC:
		// case START_ELEMENT_GENERIC_UNDECLARED:
		// case END_ELEMENT:
		// case END_ELEMENT_UNDECLARED:
		// case CHARACTERS:
		// case CHARACTERS_GENERIC:
		// case CHARACTERS_GENERIC_UNDECLARED:
		// case DOC_TYPE:
		// case ENTITY_REFERENCE:
		// case COMMENT:
		// case PROCESSING_INSTRUCTION:
		// // handle pfx mapping for element
		// checkPrefixMapping(elementPrefix, elementQName.getNamespaceURI());
		// openElement = false;
		// }
		// }

		// if (nextEventType != EventType.NAMESPACE_DECLARATION && nextEventType
		// != EventType.START_DOCUMENT ) {
		// finalizeOpenElement();
		// }
	}

	public List<PrefixMapping> getPrefixDeclarations() {
		// handle remaining pfx mapping for element
		if (elementPrefix == null) {
			checkPrefixMapping(elementQName.getNamespaceURI());
		}

		return elementContext.prefixDeclarations;
	}

	// boolean openElement;
	//	
	// protected void finalizeOpenElement() {
	// // qname as string
	// if (openElement) {
	// String sqname = getQualifiedName(elementQName,
	// elementPrefix);
	// setQNameAsString(sqname);
	//			
	// openElement = false;
	// }
	// }

	protected void updateInvalidValueAttribute() throws EXIException {
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

	/*
	 * Handles and xsi:nil attributes
	 */
	protected void decodeAttributeXsiNilStructure() throws EXIException,
			IOException {
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
			currentRule = ((SchemaInformedRule) currentRule).getTypeEmpty();
		}
	}

	/*
	 * Handles and xsi:type attributes
	 */
	protected void decodeAttributeXsiTypeStructure() throws EXIException,
			IOException {
		// type qname & prefix
		QName xsiTypeQName;
		attributeValue = typeDecoder
				.readValue(qnameDatatype, XSI_TYPE, channel);
		if (!preservePrefix) {
			checkPrefixMapping(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);	
		}

		if (attributeValue instanceof QNameValue) {
			QNameValue qnv = (QNameValue) attributeValue;
			xsiTypeQName = qnv.toQName();
			String pfx = checkPrefixMapping(xsiTypeQName.getNamespaceURI());
			if (qnv.getPrefix() == null ) {
				attributeValue = new QNameValue(qnv.toQName(), pfx);
			}
		} else {
			// parse string value again (lexical value mode)
			qnameDatatype.isValid(attributeValue.toString());
			xsiTypeQName = qnameDatatype.getQName();
		}

		// update grammar according to given xsi:type
		SchemaInformedRule tg = grammar.getTypeGrammar(xsiTypeQName);

		// grammar exists ?
		if (tg != null) {
			// update current rule
			currentRule = tg;
		}
	}

	protected Datatype decodeAttributeStructure() throws EXIException,
			IOException {
		Attribute at = ((Attribute) nextEvent);
		// qname
		attributeQName = at.getQName();
		// handle attribute prefix
		attributePrefix = qnameDatatype.decodeQNamePrefix(attributeQName,
				channel);
		if (attributePrefix == null) {
			attributePrefix = checkPrefixMapping(attributeQName
					.getNamespaceURI());
		}
		// update current rule
		currentRule = nextRule;
		return at.getDatatype();
	}

	protected void decodeAttributeNSStructure() throws EXIException,
			IOException {
		// AttributeEventNS
		AttributeNS atNS = ((AttributeNS) nextEvent);
		attributeQName = qnameDatatype.readLocalName(atNS.getNamespaceURI(),
				channel);
		// handle attribute prefix
		attributePrefix = qnameDatatype.decodeQNamePrefix(attributeQName,
				channel);
		if (attributePrefix == null) {
			attributePrefix = checkPrefixMapping(attributeQName
					.getNamespaceURI());
		}
		// update current rule
		currentRule = nextRule;
	}

	protected void decodeAttributeAnyInvalidValueStructure()
			throws EXIException, IOException {
		decodeAttributeGenericStructureOnly();
	}

	protected void decodeAttributeGenericStructure() throws EXIException,
			IOException {
		// decode structure
		decodeAttributeGenericStructureOnly();
		// update current rule
		currentRule = nextRule;
	}

	protected void decodeAttributeGenericUndeclaredStructure()
			throws EXIException, IOException {
		decodeAttributeGenericStructureOnly();
		// update grammar
		currentRule.learnAttribute(new Attribute(attributeQName));
	}

	private void decodeAttributeGenericStructureOnly() throws EXIException,
			IOException {
		// decode uri & local-name
		attributeQName = qnameDatatype.readLocalName(qnameDatatype
				.readUri(channel), channel);
		// handle attribute prefix
		attributePrefix = qnameDatatype.decodeQNamePrefix(attributeQName,
				channel);
		if (attributePrefix == null) {
			attributePrefix = checkPrefixMapping(attributeQName
					.getNamespaceURI());
		}
	}

	protected Datatype decodeCharactersStructureOnly() throws EXIException {
		assert (nextEventType == EventType.CHARACTERS);
		// update current rule
		currentRule = nextRule;
		return ((Characters) nextEvent).getDatatype();
	}

	protected void decodeCharactersGenericStructureOnly() throws EXIException {
		assert (nextEventType == EventType.CHARACTERS_GENERIC);
		// update current rule
		currentRule = nextRule;
	}

	protected void decodeCharactersGenericUndeclaredStructureOnly()
			throws EXIException {
		assert (nextEventType == EventType.CHARACTERS_GENERIC_UNDECLARED);
		// learn character event ?
		currentRule.learnCharacters();
		// update current rule
		currentRule = currentRule.getElementContentRule();

	}

	protected String getQualifiedName(QName qname, String pfx) {
		if (pfx == null) {
			assert (!fidelityOptions
					.isFidelityEnabled(FidelityOptions.FEATURE_PREFIX));
			pfx = checkPrefixMapping(qname.getNamespaceURI());
		}

		String localName = qname.getLocalPart();
		return pfx.length() == 0 ? localName
				: (pfx + Constants.COLON + localName);
	}

	protected final String checkPrefixMapping(String uri) {
		assert (!fidelityOptions
				.isFidelityEnabled(FidelityOptions.FEATURE_PREFIX));
		String pfx = uriToPrefix.get(uri);
		if (pfx == null) {
			pfx = "ns" + createdPfxCnt++;
			uriToPrefix.put(uri, pfx);
			declarePrefix(pfx, uri);
		}

		return pfx;
	}

	protected final void undeclarePrefixes() {
		if (elementContext.prefixDeclarations != null) {
			for (PrefixMapping pm : elementContext.prefixDeclarations) {
				uriToPrefix.remove(pm.uri);
			}
		}
	}
	
	
	/* ================================= */
	
	
	public void decodeStartDocument() throws EXIException {
		// update current rule
		currentRule = currentRule.lookFor(ec).next;
	}
	
	
	public void decodeStartElement() throws EXIException, IOException {
		assert (nextEventType == EventType.START_ELEMENT);
		// StartElement
		StartElement se = ((StartElement) nextEvent);
		// store SE qname
		elementQName = se.getQName();
		// handle element prefixes
		elementPrefix = qnameDatatype.decodeQNamePrefix(elementQName, channel);
		// push element
		pushElement(se, nextRule);
	}
	
	public void decodeStartElementNS() throws EXIException, IOException {
		assert (nextEventType == EventType.START_ELEMENT_NS);
		// StartElementNS
		StartElementNS seNS = ((StartElementNS) nextEvent);
		// decode local-name
		elementQName = qnameDatatype.readLocalName(seNS.getNamespaceURI(),
				channel);
		// handle element prefixes
		elementPrefix = qnameDatatype.decodeQNamePrefix(elementQName, channel);
		// next SE ...
		StartElement nextSE = getGenericStartElement(elementQName);
		// push element
		pushElement(nextSE, nextRule);
	}

	public void decodeStartElementGeneric() throws EXIException, IOException {
		assert (nextEventType == EventType.START_ELEMENT_GENERIC);
		// decode uri & local-name
		elementQName = qnameDatatype.readQName(channel);
		// handle element prefixes
		elementPrefix = qnameDatatype.decodeQNamePrefix(elementQName, channel);

		// next SE ...
		StartElement nextSE = getGenericStartElement(elementQName);
		// learn start-element ?
		currentRule.learnStartElement(nextSE);
		// push element
		pushElement(nextSE, nextRule.getElementContentRule());;
	}

	
	public void decodeStartElementGenericUndeclared() throws EXIException,
	IOException {
		assert (nextEventType == EventType.START_ELEMENT_GENERIC_UNDECLARED);
		// decode uri & local-name
		elementQName = qnameDatatype.readQName(channel);
		// handle element prefixes
		elementPrefix = qnameDatatype.decodeQNamePrefix(elementQName, channel);
		// next SE ...
		StartElement nextSE = getGenericStartElement(elementQName);
		// learn start-element ?
		currentRule.learnStartElement(nextSE);
		// push element
		pushElement(nextSE, currentRule.getElementContentRule());
	}
	
	public void decodeNamespaceDeclaration() throws EXIException, IOException {
		// prefix mapping
		nsURI = qnameDatatype.readUri(channel);
		// nsPrefix = readPrefix(nsURI);
		nsPrefix = qnameDatatype.readPrefix(nsURI, channel);
		boolean local_element_ns = channel.decodeBoolean();
		if (local_element_ns) {
			this.elementPrefix = nsPrefix;
		}
		// NS
		declarePrefix(nsPrefix, nsURI);
	}
	

	public void decodeAttributeXsiNil() throws EXIException, IOException {
		assert (nextEventType == EventType.ATTRIBUTE_XSI_NIL);
		attributeQName = XSI_NIL;
		attributePrefix = qnameDatatype.decodeQNamePrefix(XSI_NIL, channel);

		decodeAttributeXsiNilStructure();
	}

	public void decodeAttributeXsiType() throws EXIException, IOException {
		assert (nextEventType == EventType.ATTRIBUTE_XSI_TYPE);
		attributeQName = XSI_TYPE;
		attributePrefix = qnameDatatype.decodeQNamePrefix(XSI_TYPE, channel);

		decodeAttributeXsiTypeStructure();
	}

	protected void readAttributeContent(Datatype dt) throws IOException {
		attributeValue = typeDecoder.readValue(dt, attributeQName, channel);
	}

	public void decodeAttribute() throws EXIException, IOException {
		// structure & content
		readAttributeContent(decodeAttributeStructure());
	}

	public void decodeAttributeInvalidValue() throws EXIException, IOException {
		// structure
		decodeAttributeStructure();
		// Note: attribute content datatype is not the right one (invalid)
		readAttributeContent(BuiltIn.DEFAULT_DATATYPE);
	}

	public void decodeAttributeAnyInvalidValue() throws EXIException,
			IOException {
		// structure
		decodeAttributeAnyInvalidValueStructure();
		// content
		readAttributeContent(BuiltIn.DEFAULT_DATATYPE);
	}

	protected void readAttributeContent() throws IOException, EXIException {
		if (XSI_TYPE.equals(attributeQName)) {
			decodeAttributeXsiTypeStructure();
		} else if (XSI_NIL.equals(attributeQName)
				&& currentRule.isSchemaInformed()) {
			decodeAttributeXsiNilStructure();
		} else {
			Attribute globalAT;
			Datatype dt = BuiltIn.DEFAULT_DATATYPE;
			if ( currentRule.isSchemaInformed() && ( globalAT = grammar.getGlobalAttribute(attributeQName) ) != null ) {
				dt = globalAT.getDatatype();
			}
			readAttributeContent(dt);
		}
	}

	public void decodeAttributeNS() throws EXIException, IOException {
		// structure
		decodeAttributeNSStructure();
		// content
		readAttributeContent();
	}

	public void decodeAttributeGeneric() throws EXIException, IOException {
		// structure
		decodeAttributeGenericStructure();
		// content
		readAttributeContent();
	}

	public void decodeAttributeGenericUndeclared() throws EXIException,
			IOException {
		// structure
		decodeAttributeGenericUndeclaredStructure();
		// content
		readAttributeContent();
	}

	public void decodeCharacters() throws EXIException, IOException {
		// structure & content
		characters = typeDecoder.readValue(decodeCharactersStructureOnly(),
				elementContext.qname, channel);
	}

	public void decodeCharactersGeneric() throws EXIException, IOException {
		// structure
		decodeCharactersGenericStructureOnly();
		// content
		characters = typeDecoder.readValue(BuiltIn.DEFAULT_DATATYPE,
				elementContext.qname, channel);
	}

	public void decodeCharactersGenericUndeclared() throws EXIException,
			IOException {
		// structure
		decodeCharactersGenericUndeclaredStructureOnly();
		// content
		characters = typeDecoder.readValue(BuiltIn.DEFAULT_DATATYPE,
				elementContext.qname, channel);
	}

	public void decodeEndElement() throws EXIException, IOException {
		// save ee information before popping context
		elementQName = elementContext.qname;
		// NS
		undeclarePrefixes();
		// pop element
		popElement();
	}

	public void decodeEndElementUndeclared() throws EXIException, IOException {
		// save ee information before popping context
		elementQName = elementContext.qname;
		// NS
		undeclarePrefixes();
		// learn end-element event ?
		currentRule.learnEndElement();
		// pop element
		popElement();
	}

	public void decodeEndDocument() throws EXIException, IOException {
		// assert (elementContextStack.size() == 1);
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
	
	/* ================================= */

	public QName getElementQName() {
		return elementQName;
	}

	public String getStartElementQNameAsString() {
		String sqname = getQualifiedName(elementQName, elementPrefix);
		setQNameAsString(sqname);
		return sqname;
	}

	public String getEndElementQNameAsString() {
		return getQNameAsString();
	}

	public QName getAttributeQName() {
		return attributeQName;
	}

	public String getAttributeQNameAsString() {
		return getQualifiedName(attributeQName, attributePrefix);
	}

	public Value getAttributeValue() {
		return attributeValue;
	}

	public Value getCharactersValue() {
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

	public char[] getComment() {
		return comment;
	}

	public String getPITarget() {
		return piTarget;
	}

	public String getPIData() {
		return piData;
	}

	public void decodeStartFragmentSelfContained() throws EXIException,
			IOException {
		throw new RuntimeException("[EXI] SelfContained");
	}

}
