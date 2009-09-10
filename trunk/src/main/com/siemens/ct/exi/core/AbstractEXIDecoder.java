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

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.EXIDecoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.EventInformation;
import com.siemens.ct.exi.grammar.TypeGrammar;
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
	protected String elementURI;
	protected String elementLocalName;
	protected String elementPrefix;
	protected String attributeURI;
	protected String attributeLocalName;
	protected String attributePrefix;
	protected char[] attributeValue;
	protected String xsiTypeURI;
	protected String xsiTypeLocalName;
	protected boolean xsiNil;
	protected char[] characters;
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
	protected boolean preservePrefixes;
	protected Map<String, String> createdPrefixes;
	protected int createdPfxCnt;

	public AbstractEXIDecoder(EXIFactory exiFactory) {
		super(exiFactory);

		preservePrefixes = exiFactory.getFidelityOptions().isFidelityEnabled(
				FidelityOptions.FEATURE_PREFIX);
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

	protected int readEventCode(int codeLength) throws IOException {
		return channel.decodeNBitUnsignedInteger(codeLength);
	}

	protected String readUri() throws IOException {
		int nUri = MethodsBag.getCodingLength(uris.size() + 1); // numberEntries+1
		int uriID = channel.decodeNBitUnsignedInteger(nUri);

		String uri;

		if (uriID == 0) {
			// string value was not found
			// ==> zero (0) as an n-nit unsigned integer
			// followed by uri encoded as string
			uri = new String(channel.decodeString());
			// after encoding string value is added to table
			addURI(uri);
		} else {
			// string value found
			// ==> value(i+1) is encoded as n-bit unsigned integer
			uri = uris.get(uriID - 1).namespaceURI;
			updateURIContext(uri);
		}

		return uri;
	}

	protected String readLocalName(String uri) throws IOException {

		int length = channel.decodeUnsignedInteger();

		String localName;
		if (length > 0) {
			// string value was not found in local partition
			// ==> string literal is encoded as a String
			// with the length of the string incremented by one
			localName = new String(channel.decodeStringOnly(length - 1));
			// After encoding the string value, it is added to the string table
			// partition and assigned the next available compact identifier.
			uriContext.addLocalName(localName);
		} else {
			// string value found in local partition
			// ==> string value is represented as zero (0) encoded as an
			// Unsigned Integer
			// followed by an the compact identifier of the string value as an
			// n-bit unsigned integer
			// n is log2 m and m is the number of entries in the string table
			// partition
			int n = MethodsBag.getCodingLength(uriContext.getLocalNameSize());
			int localNameID = channel.decodeNBitUnsignedInteger(n);
			localName = uriContext.getNameContext(localNameID).localName;
		}

		return localName;
	}

	protected String readPrefix(String uri) throws IOException {
		String prefix;

		int nPfx = MethodsBag.getCodingLength(uriContext.getPrefixSize() + 1); // n-bit
		int pfxID = channel.decodeNBitUnsignedInteger(nPfx);
		if (pfxID == 0) {
			// string value was not found
			// ==> zero (0) as an n-nit unsigned integer
			// followed by pfx encoded as string
			prefix = new String(channel.decodeString());
			// after decoding pfx value is added to table
			uriContext.addPrefix(prefix);
		} else {
			// string value found
			// ==> value(i+1) is encoded as n-bit unsigned integer
			prefix = uriContext.getPrefix(pfxID - 1);
		}

		return prefix;
	}

	protected void decodeEventCode() throws EXIException, IOException {
		// 1st level
		ec = readEventCode(currentRule
				.get1stLevelEventCodeLength(fidelityOptions));

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
			ec3AT = readEventCode(MethodsBag.getCodingLength(sir
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
		int level2 = readEventCode(MethodsBag.getCodingLength(ch2));

		if (currentRule.get3rdLevelCharacteristics(fidelityOptions) > 0) {
			return (level2 < (ch2 - 1) ? level2 : Constants.NOT_FOUND);
		} else {
			return (level2 < ch2 ? level2 : Constants.NOT_FOUND);
		}
	}

	protected int decode3rdLevelEventCode() throws EXIException, IOException {
		int ch3 = currentRule.get3rdLevelCharacteristics(fidelityOptions);
		return readEventCode(MethodsBag.getCodingLength(ch3));
	}

	protected String decodeQNamePrefix(String uri) throws EXIException,
			IOException {
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

					id = readEventCode(MethodsBag
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
	}

	protected void decodeStartElementExpandedName() throws EXIException,
			IOException {
		// decode uri & local-name
		this.elementURI = readUri();
		this.elementLocalName = readLocalName(elementURI);
	}

	/*
	 * Handles and xsi:nil attributes
	 */
	protected void decodeAttributeXsiNilStructure() throws EXIException,
			IOException {
		// assert
		// (attributeURI.equals(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI));
		// assert (attributeLocalName.equals(Constants.XSI_NIL));

		xsiNil = channel.decodeBoolean();

		if (xsiNil) { // jump to typeEmpty
			replaceRuleAtTheTop(currentRule.getTypeEmpty());
		}

		attributeValue = xsiNil ? Constants.XSD_BOOLEAN_TRUE_ARRAY
				: Constants.XSD_BOOLEAN_FALSE_ARRAY;
		attributePrefix = null;
	}

	/*
	 * Handles and xsi:type attributes
	 */
	protected void decodeAttributeXsiTypeStructure() throws EXIException,
			IOException {

		// assert (attributeURI
		// .equals(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI));
		// assert (attributeLocalName.equals(Constants.XSI_TYPE));

		xsiTypeURI = readUri();
		xsiTypeLocalName = readLocalName(xsiTypeURI);

		// update grammar according to given xsi:type
		TypeGrammar tg = grammar.getTypeGrammar(xsiTypeURI, xsiTypeLocalName);

		// grammar exists ?
		if (tg != null) {
			replaceRuleAtTheTop(tg.getType());
		}

		attributeValue = getQualifiedName(xsiTypeURI, xsiTypeLocalName,
				attributePrefix).toCharArray();
		attributePrefix = null;
	}

	protected Datatype decodeAttributeStructure() throws EXIException,
			IOException {
		Attribute at = ((Attribute) nextEvent);
		this.attributeURI = at.getNamespaceURI();
		this.attributeLocalName = at.getLocalName();
		// handle attribute prefix
		attributePrefix = decodeQNamePrefix(this.attributeURI);
		// step forward in current rule (replace rule at the top)
		replaceRuleAtTheTop(nextRule);
		return at.getDatatype();
	}

	protected Datatype decodeAttributeNSStructure() throws EXIException,
			IOException {
		// AttributeEventNS
		AttributeNS atNS = ((AttributeNS) nextEvent);
		// AttributeNS atNS = ((AttributeNS) nextEventRule.event);
		this.attributeURI = atNS.getNamespaceURI();
		// decode local-name
		this.attributeLocalName = readLocalName(attributeURI);

		// handle attribute prefix
		attributePrefix = decodeQNamePrefix(this.attributeURI);

		// step forward in current rule (replace rule at the top)
		replaceRuleAtTheTop(nextRule);

		// return atNS;
		return BuiltIn.DEFAULT_DATATYPE;
	}

	protected Datatype decodeAttributeInvalidValueStructure()
			throws EXIException, IOException {
		// decodeAttributeStructure();

		Attribute at = ((Attribute) nextEvent);
		this.attributeURI = at.getNamespaceURI();
		this.attributeLocalName = at.getLocalName();
		// handle attribute prefix
		attributePrefix = decodeQNamePrefix(this.attributeURI);
		// step forward in current rule (replace rule at the top)
		replaceRuleAtTheTop(nextRule);

		return BuiltIn.DEFAULT_DATATYPE;
	}

	protected Datatype decodeAttributeAnyInvalidValueStructure()
			throws EXIException, IOException {
		// decodeAttributeGenericUndeclaredStructure();

		decodeAttributeGenericStructureOnly();
		return BuiltIn.DEFAULT_DATATYPE;
	}

	protected Datatype decodeAttributeGenericStructure() throws EXIException,
			IOException {
		// decode structure
		decodeAttributeGenericStructureOnly();
		// decodeAttributeGenericUndeclaredStructure();

		// step forward in current rule (replace rule at the top)
		replaceRuleAtTheTop(nextRule);

		Attribute globalAT = grammar.getGlobalAttribute(attributeURI,
				attributeLocalName);
		if (globalAT == null) {
			return BuiltIn.DEFAULT_DATATYPE;
		} else {
			return globalAT.getDatatype();
		}
	}

	protected Datatype decodeAttributeGenericUndeclaredStructure()
			throws EXIException, IOException {

		decodeAttributeGenericStructureOnly();

		// update grammar
		currentRule.learnAttribute(attributeURI, attributeLocalName);

		return BuiltIn.DEFAULT_DATATYPE;
	}

	private void decodeAttributeGenericStructureOnly() throws EXIException,
			IOException {
		// decode uri & local-name
		this.attributeURI = readUri();
		this.attributeLocalName = readLocalName(attributeURI);

		// handle attribute prefix
		attributePrefix = decodeQNamePrefix(this.attributeURI);
	}

	protected Datatype decodeCharactersStructureOnly() throws EXIException {
		assert (nextEventType == EventType.CHARACTERS);
		replaceRuleAtTheTop(nextRule);
		return ((Characters) nextEvent).getDatatype();
	}

	protected Datatype decodeCharactersGenericStructureOnly()
			throws EXIException {
		assert (nextEventType == EventType.CHARACTERS_GENERIC);
		replaceRuleAtTheTop(nextRule);
		return BuiltIn.DEFAULT_DATATYPE;
	}

	protected Datatype decodeCharactersGenericUndeclaredStructureOnly()
			throws EXIException {
		assert (nextEventType == EventType.CHARACTERS_GENERIC_UNDECLARED);
		// learn character event ?
		currentRule.learnCharacters();
		// step forward in current rule (replace rule at the top)
		replaceRuleAtTheTop(currentRule.getElementContentRule());
		return BuiltIn.DEFAULT_DATATYPE;

	}

	protected String getQualifiedName(String attributeURI,
			String attributeLocalName, String pfx) {
		if (pfx == null) {
			if (attributeURI.equals(namespaces
					.getURI(XMLConstants.DEFAULT_NS_PREFIX))
					|| attributeURI.equals(XMLConstants.NULL_NS_URI)) {
				// default namespace
				pfx = XMLConstants.DEFAULT_NS_PREFIX;
			} else if ((pfx = namespaces.getPrefix(attributeURI)) == null) {
				// create unique prefix
				pfx = this.getUniquePrefix(attributeURI);
			}
		}

		return (pfx.length() == 0 ? attributeLocalName
				: (pfx + Constants.COLON + attributeLocalName));
	}

	protected String getUniquePrefix(String uri) {
		String pfx;
		if (createdPrefixes.containsKey(uri)) {
			// *re-use* previous created prefix
			pfx = createdPrefixes.get(uri);
			// add to namespace context, if not already
			if (namespaces.getPrefix(uri) == null) {
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

	public String getElementURI() {
		return elementURI;
	}

	public String getElementLocalName() {
		return elementLocalName;
	}

	public String getElementQName() {
		return this.getQualifiedName(elementURI, elementLocalName,
				elementPrefix);
	}

	public String getAttributeURI() {
		return attributeURI;
	}

	public String getAttributeLocalName() {
		return attributeLocalName;
	}

	public String getAttributeQName() {
		return this.getQualifiedName(attributeURI, attributeLocalName,
				attributePrefix);
	}

	public char[] getAttributeValue() {
		return attributeValue;
	}

	public char[] getCharacters() {
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

	public void decodeEndFragmentSelfContained() throws EXIException,
			IOException {
		throw new RuntimeException("[EXI] SelfContained");
	}

	public void decodeStartFragmentSelfContained() throws EXIException,
			IOException {
		throw new RuntimeException("[EXI] SelfContained");
	}

}
