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
import java.util.List;
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
		
//		qualifiedNames.clear();
	}

	protected String readUri() throws IOException {
		int nUri = MethodsBag.getCodingLength(runtimeURIEntries.size() + 1); // numberEntries+1
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
			uri = runtimeURIEntries.get(uriID - 1).namespaceURI;
			updateURIContext(uri);
		}

		return uri;
	}

	protected QName readLocalName(String uri) throws IOException {
		updateURIContext(uri);
		int length = channel.decodeUnsignedInteger();

		QName qname;
		if (length > 0) {
			// string value was not found in local partition
			// ==> string literal is encoded as a String
			// with the length of the string incremented by one
			String localName = new String(channel.decodeStringOnly(length - 1));
			// After encoding the string value, it is added to the string table
			// partition and assigned the next available compact identifier.
			qname = uriContext.addLocalName(localName);
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
			qname = uriContext.getNameContext(localNameID);
		}

		return qname;
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

	protected String decodeQNamePrefix(QName qname) throws EXIException, IOException {
		if (preservePrefix) {
			String prefix = null;
			String uri = qname.getNamespaceURI();
			if (uri.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
				prefix = XMLConstants.NULL_NS_URI;
			} else {			
				updateURIContext(uri);
				List<String> prefixes = uriContext.prefixes;
				int numberOfPrefixes = prefixes.size();

				if (numberOfPrefixes > 0) {
					int id = 0;
					if (numberOfPrefixes > 1 ) {
						id = channel.decodeNBitUnsignedInteger(MethodsBag
								.getCodingLength(numberOfPrefixes));
					}
					prefix = prefixes.get(id);
				} else {
					// no previous NS mapping in charge
				}
			
			}
			return prefix;
		} else {
			return null;
		}
	}

	protected void decodeStartElementExpandedName() throws EXIException,
			IOException {
		// decode uri & local-name
		this.elementQName = this.readLocalName(readUri());
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
		xsiTypeQName = readLocalName(readUri());
		
		// handle type prefix
		xsiTypePrefix = decodeQNamePrefix(xsiTypeQName);
		
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
		attributePrefix = decodeQNamePrefix(attributeQName);
		// update current rule
		currentRule = nextRule;
		return at.getDatatype();
	}

	protected Datatype decodeAttributeNSStructure() throws EXIException,
			IOException {
		// AttributeEventNS
		AttributeNS atNS = ((AttributeNS) nextEvent);
		attributeQName = readLocalName(atNS.getNamespaceURI());

		// handle attribute prefix
		attributePrefix = decodeQNamePrefix(attributeQName);

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
		attributePrefix = decodeQNamePrefix(attributeQName);
		// update current rule
		currentRule = nextRule;

		return BuiltIn.DEFAULT_DATATYPE;
	}

	protected Datatype decodeAttributeAnyInvalidValueStructure()
			throws EXIException, IOException {
		decodeAttributeGenericStructureOnly();
		return BuiltIn.DEFAULT_DATATYPE;
	}

	protected Datatype decodeAttributeGenericStructure() throws EXIException,
			IOException {
		// decode structure
		decodeAttributeGenericStructureOnly();

		// update current rule
		currentRule = nextRule;

		Attribute globalAT = grammar.getGlobalAttribute(attributeQName);
		
		return (globalAT == null) ? BuiltIn.DEFAULT_DATATYPE : globalAT.getDatatype();
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
		attributeQName = readLocalName(readUri());

		// handle attribute prefix
		attributePrefix = decodeQNamePrefix(attributeQName);
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

	
//	Map<QName, String> qualifiedNames = new HashMap<QName, String>();
	
	protected String getQualifiedName(QName qname, String pfx) {
		String localName = qname.getLocalPart();
		String sqname;
		
		if (pfx == null) {
//			if ( (sqname = qualifiedNames.get(qname)) != null) {
//				return sqname;
//			}
			
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
			
//			qualifiedNames.put(qname, sqname);
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

//	public void decodeEndFragmentSelfContained() throws EXIException,
//			IOException {
//		throw new RuntimeException("[EXI] SelfContained");
//	}

	public void decodeStartFragmentSelfContained() throws EXIException,
			IOException {
		throw new RuntimeException("[EXI] SelfContained");
	}

}
