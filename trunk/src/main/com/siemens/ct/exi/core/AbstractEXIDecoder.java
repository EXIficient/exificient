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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.EXIDecoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.EventInformation;
import com.siemens.ct.exi.grammar.event.Attribute;
import com.siemens.ct.exi.grammar.event.AttributeNS;
import com.siemens.ct.exi.grammar.event.Characters;
import com.siemens.ct.exi.grammar.event.Event;
import com.siemens.ct.exi.grammar.event.EventType;
import com.siemens.ct.exi.grammar.rule.Rule;
import com.siemens.ct.exi.grammar.rule.SchemaInformedRule;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.types.BuiltIn;
import com.siemens.ct.exi.types.TypeDecoder;
import com.siemens.ct.exi.util.MethodsBag;
import com.siemens.ct.exi.values.QNameValue;
import com.siemens.ct.exi.values.StringValue;
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
	// protected DecoderBlock block;

	// Type Decoder (including string decoder etc.)
	protected TypeDecoder typeDecoder;

	// current values
	protected QName elementQName;
	protected String elementPrefix;
	protected QName attributeQName;
	protected String attributePrefix;
	protected Value attributeValue;
	protected QName xsiTypeQName;
	protected String xsiTypePrefix;
	protected boolean xsiNil;
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

	// namespaces/prefixes
	protected Map<String, String> createdPrefixes;
	protected int createdPfxCnt;

	public AbstractEXIDecoder(EXIFactory exiFactory) {
		super(exiFactory);

		createdPrefixes = new HashMap<String, String>();

		// init once
		typeDecoder = exiFactory.createTypeDecoder();
	}

	@Override
	protected void initForEachRun() throws EXIException, IOException {
		super.initForEachRun();

		createdPrefixes.clear();
		createdPfxCnt = 1;

		// clear string values etc.
		typeDecoder.clear();
	}

	protected void decodeEventCode() throws EXIException, IOException {
		// 1st level
		int codeLength = currentRule.get1stLevelEventCodeLength(fidelityOptions);
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
	}

	protected void updateInvalidValueAttribute() throws EXIException {
		SchemaInformedRule sir = (SchemaInformedRule) currentRule;

		int ec3AT;
		try {
			ec3AT = channel.decodeNBitUnsignedInteger(MethodsBag.getCodingLength(sir
					.getNumberOfSchemaDeviatedAttributes()));
		} catch (IOException e) {
			throw new EXIException(e);
		}

		if (ec3AT < (sir.getNumberOfSchemaDeviatedAttributes() - 1)) {
			// deviated attribute
			ec = ec3AT + sir.getLeastAttributeEventCode();
			EventInformation ei = currentRule.lookFor(ec);
			nextEvent = ei.event;
			nextRule = ei.next;
			// nextEventRule = currentRule.get1stLevelEventRule(ec);
		} else if (ec3AT == (sir.getNumberOfSchemaDeviatedAttributes() - 1)) {
			// ANY deviated attribute (no qname present)
			nextEventType = EventType.ATTRIBUTE_ANY_INVALID_VALUE;
		} else {
			throw new EXIException(
					"Error occured while decoding deviated attribute");
		}
	}

	protected int decode2ndLevelEventCode() throws EXIException, IOException {
		int ch2 = currentRule.get2ndLevelCharacteristics(fidelityOptions);
		int level2 = channel.decodeNBitUnsignedInteger(MethodsBag.getCodingLength(ch2));

		if (currentRule.get3rdLevelCharacteristics(fidelityOptions) > 0) {
			return (level2 < (ch2 - 1) ? level2 : Constants.NOT_FOUND);
		} else {
			return (level2 < ch2 ? level2 : Constants.NOT_FOUND);
		}
	}

	protected int decode3rdLevelEventCode() throws EXIException, IOException {
		int ch3 = currentRule.get3rdLevelCharacteristics(fidelityOptions);
		return channel.decodeNBitUnsignedInteger(MethodsBag.getCodingLength(ch3));
	}

	/*
	 * Handles and xsi:nil attributes
	 */
	static final Value XSD_BOOLEAN_TRUE_VALUE = new StringValue(Constants.XSD_BOOLEAN_TRUE_ARRAY);
	static final Value XSD_BOOLEAN_FALSE_VALUE = new StringValue(Constants.XSD_BOOLEAN_FALSE_ARRAY);
	
	protected void decodeAttributeXsiNilStructure() throws EXIException,
			IOException {
		xsiNil = channel.decodeBoolean();

		if (xsiNil && currentRule.isSchemaInformed()) { // jump to typeEmpty
			// replaceRuleAtTheTop(((SchemaInformedRule)currentRule).getTypeEmpty());
			currentRule = ((SchemaInformedRule)currentRule).getTypeEmpty();
		}

		attributeValue = xsiNil ? XSD_BOOLEAN_TRUE_VALUE
				: XSD_BOOLEAN_FALSE_VALUE;
		// attributePrefix = null;
	}

	/*
	 * Handles and xsi:type attributes
	 */
	protected void decodeAttributeXsiTypeStructure() throws EXIException,
			IOException {
		// xsiTypeQName = readLocalName(readUri());
		// xsiTypeQName = readLocalName(qnameDatatype.readUri(channel));
		xsiTypeQName = qnameDatatype.readLocalName(qnameDatatype.readUri(channel), channel);
		
		// handle type prefix
		xsiTypePrefix = qnameDatatype.decodeQNamePrefix(xsiTypeQName, channel);
		
		// update grammar according to given xsi:type
		SchemaInformedRule tg = grammar.getTypeGrammar(xsiTypeQName);

		// grammar exists ?
		if (tg != null) {
			// update current rule
			currentRule = tg;
		}

		attributeValue = new StringValue(getQualifiedName(xsiTypeQName, xsiTypePrefix));
		attributePrefix = null;
	}

	protected Datatype decodeAttributeStructure() throws EXIException,
			IOException {
		Attribute at = ((Attribute) nextEvent);
		//	qname 
		attributeQName = at.getQName();
		// handle attribute prefix
		attributePrefix = qnameDatatype.decodeQNamePrefix(attributeQName, channel);
		// update current rule
		currentRule = nextRule;
		return at.getDatatype();
	}

	protected Datatype decodeAttributeNSStructure() throws EXIException,
			IOException {
		// AttributeEventNS
		AttributeNS atNS = ((AttributeNS) nextEvent);
		// attributeQName = readLocalName(atNS.getNamespaceURI());
		attributeQName = qnameDatatype.readLocalName(atNS.getNamespaceURI(), channel);

		// handle attribute prefix
		attributePrefix = qnameDatatype.decodeQNamePrefix(attributeQName, channel);

		// update current rule
		currentRule = nextRule;

		// return atNS;
		return BuiltIn.DEFAULT_DATATYPE;
	}

	protected Datatype decodeAttributeInvalidValueStructure()
			throws EXIException, IOException {
		Attribute at = ((Attribute) nextEvent);
		//	qname 
		attributeQName = at.getQName();
		// handle attribute prefix
		attributePrefix = qnameDatatype.decodeQNamePrefix(attributeQName, channel);
		// update current rule
		currentRule = nextRule;

		return BuiltIn.DEFAULT_DATATYPE;
	}

	protected Datatype decodeAttributeAnyInvalidValueStructure()
			throws EXIException, IOException {
		decodeAttributeGenericStructureOnly();
		return BuiltIn.DEFAULT_DATATYPE;
	}

	
	QName xsiType = new QName(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,
			Constants.XSI_TYPE);
	
	protected Datatype decodeAttributeGenericStructure() throws EXIException,
			IOException {
		// decode structure
		decodeAttributeGenericStructureOnly();

		if(xsiType.equals(attributeQName)) {			
			attributeValue = qnameDatatype.readValue(channel, null, null);
			QName xsiType = ((QNameValue)attributeValue).toQName();
			
			SchemaInformedRule tg = grammar.getTypeGrammar(xsiType);
			// grammar exists ?
			if (tg != null) {
				// update grammar according to given xsi:type
				currentRule = tg;
			}
			
			return null;
		} else {
			// update current rule
			currentRule = nextRule;

			Attribute globalAT = grammar.getGlobalAttribute(attributeQName);
			
			return (globalAT == null) ? BuiltIn.DEFAULT_DATATYPE : globalAT.getDatatype();			
		}
	}

	protected Datatype decodeAttributeGenericUndeclaredStructure()
			throws EXIException, IOException {

		decodeAttributeGenericStructureOnly();

		// update grammar
		currentRule.learnAttribute(new Attribute(attributeQName));

		return BuiltIn.DEFAULT_DATATYPE;
	}

	private void decodeAttributeGenericStructureOnly() throws EXIException,
			IOException {
		// decode uri & local-name
		attributeQName = qnameDatatype.readLocalName(qnameDatatype.readUri(channel), channel);

		// handle attribute prefix
		attributePrefix = qnameDatatype.decodeQNamePrefix(attributeQName, channel);
	}

	protected Datatype decodeCharactersStructureOnly() throws EXIException {
		assert (nextEventType == EventType.CHARACTERS);
		// update current rule
		currentRule = nextRule;
		return ((Characters) nextEvent).getDatatype();
	}

	protected Datatype decodeCharactersGenericStructureOnly()
			throws EXIException {
		assert (nextEventType == EventType.CHARACTERS_GENERIC);
		// update current rule
		currentRule = nextRule;
		return BuiltIn.DEFAULT_DATATYPE;
	}

	protected Datatype decodeCharactersGenericUndeclaredStructureOnly()
			throws EXIException {
		assert (nextEventType == EventType.CHARACTERS_GENERIC_UNDECLARED);
		// learn character event ?
		currentRule.learnCharacters();
		// update current rule
		currentRule = currentRule.getElementContentRule();
		return BuiltIn.DEFAULT_DATATYPE;

	}

	
	
	protected String getQualifiedName(QName qname, String pfx) {
		String localName = qname.getLocalPart();
		String sqname;
		
		if (pfx == null) {
			
			String uri = qname.getNamespaceURI();
			if (uri.equals(XMLConstants.NULL_NS_URI) || uri.equals(namespaces
					.getURI(XMLConstants.DEFAULT_NS_PREFIX)) ) {
				// default namespace
				pfx = XMLConstants.DEFAULT_NS_PREFIX;
				sqname = localName;
			} else {
				if ((pfx = namespaces.getPrefix(uri)) == null) {
					// create unique prefix a la ns1, ns2, .. etc
					pfx = getUniquePrefix(uri);
					sqname = pfx + Constants.COLON + localName;
				} else {
					sqname = pfx.length() == 0 ? localName
							: (pfx + Constants.COLON + localName);
				}
			}
		} else {
			sqname = pfx.length() == 0 ? localName
					: (pfx + Constants.COLON + localName);
		}
	
		return sqname;
	}

	protected String getUniquePrefix(String uri) {
		String pfx;
		if ((pfx = createdPrefixes.get(uri)) != null) {
			// *re-use* previously created prefix
			if (namespaces.getPrefix(uri) == null) {
				// add to namespace context, if not already
				namespaces.declarePrefix(pfx, uri);
			}
		} else {
			do {
				pfx = "ns" + createdPfxCnt++;
			} while (namespaces.getURI(pfx) != null);

			namespaces.declarePrefix(pfx, uri);
			createdPrefixes.put(uri, pfx);
		}
		return pfx;
	}
	
	public QName getElementQName() {
		return elementQName;
	}

	public String getElementQNameAsString() {
		return getQualifiedName(elementQName,
				elementPrefix);
	}

	public QName getAttributeQName() {
		return attributeQName;
	}
	
	public String getAttributeQNameAsString() {
		return getQualifiedName(attributeQName,
				attributePrefix);
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
