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
import java.io.OutputStream;
import java.util.Enumeration;

import javax.xml.XMLConstants;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.EXIEncoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.EventInformation;
import com.siemens.ct.exi.grammar.TypeGrammar;
import com.siemens.ct.exi.grammar.event.Attribute;
import com.siemens.ct.exi.grammar.event.DatatypeEvent;
import com.siemens.ct.exi.grammar.event.EventType;
import com.siemens.ct.exi.grammar.event.StartElement;
import com.siemens.ct.exi.grammar.rule.Rule;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.types.BuiltIn;
import com.siemens.ct.exi.types.TypeEncoder;
import com.siemens.ct.exi.util.MethodsBag;
import com.siemens.ct.exi.util.datatype.XSDBoolean;
import com.siemens.ct.exi.util.xml.QNameUtilities;

public abstract class AbstractEXIEncoder extends AbstractEXICoder implements
		EXIEncoder {

	// prefix of previous start element (relevant for preserving prefixes)
	protected boolean preservePrefix;
	protected String sePrefix = null;

	// to parse raw nil value
	protected XSDBoolean nil;

	// OutputStream & Channel
	protected EncoderChannel channel;
	protected OutputStream os;

	// Type Encoder (including string encoder etc.)
	protected TypeEncoder typeEncoder;

	public AbstractEXIEncoder(EXIFactory exiFactory) {
		super(exiFactory);

		// preserve prefixes
		preservePrefix = fidelityOptions
				.isFidelityEnabled(FidelityOptions.FEATURE_PREFIX);
		// xsi:nil
		nil = XSDBoolean.newInstance();

		// init once
		typeEncoder = exiFactory.createTypeEncoder();
	}

	@Override
	protected void initForEachRun() throws EXIException, IOException {
		super.initForEachRun();

		// clear string values etc.
		typeEncoder.clear();
	}

	public void setOutput(OutputStream os, boolean exiBodyOnly)
			throws EXIException {
		this.os = os;

		if (!exiBodyOnly) {
			// EXI header
			EXIHeader.write(os);
		}
	}

	/*
	 * Stream
	 */
	protected void flush() throws IOException {
		channel.flush();
	}

	/*
	 * Structure Channel
	 */

	protected void writeEventCode(int eventCode, int codeLength)
			throws IOException {
		channel.encodeNBitUnsignedInteger(eventCode, codeLength);
	}

	// PI, Comment, etc.
	protected void writeString(String text) throws IOException {
		channel.encodeString(text);
	}

	protected void writeUri(String uri) throws IOException {
		int nUri = MethodsBag.getCodingLength(uris.size() + 1); // numberEntries+1

		updateURIContext(uri);
		if (uriContext == null) {
			// string value was not found
			// ==> zero (0) as an n-nit unsigned integer
			// followed by uri encoded as string
			channel.encodeNBitUnsignedInteger(0, nUri);
			channel.encodeString(uri);
			// after encoding string value is added to table
			addURI(uri);
		} else {
			// string value found
			// ==> value(i+1) is encoded as n-bit unsigned integer
			channel.encodeNBitUnsignedInteger(uriContext.id + 1, nUri);
		}
	}

	protected void writeLocalName(String localName, String uri)
			throws IOException {
		Integer localNameID = uriContext.getLocalNameID(localName);

		if (localNameID == null) {
			// string value was not found in local partition
			// ==> string literal is encoded as a String
			// with the length of the string incremented by one
			channel.encodeUnsignedInteger(localName.length() + 1);
			channel.encodeStringOnly(localName);
			// After encoding the string value, it is added to the string
			// table partition and assigned the next available compact
			// identifier.
			uriContext.addLocalName(localName);
		} else {
			// string value found in local partition
			// ==> string value is represented as zero (0) encoded as an
			// Unsigned Integer followed by an the compact identifier of the
			// string value as an n-bit unsigned integer n is log2 m and m is
			// the number of entries in the string table partition
			channel.encodeUnsignedInteger(0);
			int n = MethodsBag.getCodingLength(uriContext.getLocalNameSize());
			channel.encodeNBitUnsignedInteger(localNameID, n);
		}
	}

	protected void writePrefix(String prefix, String uri) throws IOException {
		Integer pfxID = uriContext.getPrefixID(prefix);
		int nPfx = MethodsBag.getCodingLength(uriContext.getPrefixSize() + 1); // n-bit
		if (pfxID == null) {
			// string value was not found
			// ==> zero (0) as an n-bit unsigned integer
			// followed by pfx encoded as string
			channel.encodeNBitUnsignedInteger(0, nPfx);
			channel.encodeString(prefix);
			// after encoding string value is added to table
			uriContext.addPrefix(prefix);
		} else {
			// string value found
			// ==> value(i+1) is encoded as n-bit unsigned integer
			channel.encodeNBitUnsignedInteger(pfxID + 1, nPfx);
		}
	}

	protected EventInformation lookForAttribute(String uri, String localName) {
		EventInformation ei;

		// try to find declared AT(uri:localName)
		ei = currentRule.lookFor(EventType.ATTRIBUTE, uri, localName);

		if (ei == null) {
			// try to find declared AT(uri:*)
			ei = currentRule.lookFor(EventType.ATTRIBUTE_NS, uri);

			if (ei == null) {
				// try to find declared AT(*), generic AT on first level
				ei = currentRule.lookFor(EventType.ATTRIBUTE_GENERIC);
			}
		}

		return ei;
	}

	protected boolean isTypeValid(Datatype datatype, String value) {
		return typeEncoder.isValid(datatype, value);
	}

	protected abstract void writeValueTypeValid(Context valueContext)
			throws IOException;

	protected abstract void writeValueAsString(Context valueContext,
			String value) throws IOException;

	/*
	 * Event-Codes
	 */

	protected void encode1stLevelEventCode(int pos) throws IOException {
		writeEventCode(pos, currentRule
				.get1stLevelEventCodeLength(fidelityOptions));
	}

	protected void encode2ndLevelEventCode(int pos) throws IOException {
		// 1st level
		writeEventCode(currentRule.getNumberOfEvents(), currentRule
				.get1stLevelEventCodeLength(fidelityOptions));

		// 2nd level
		int ch2 = currentRule.get2ndLevelCharacteristics(fidelityOptions);
		assert (pos < ch2);

		writeEventCode(pos, MethodsBag.getCodingLength(ch2));
	}

	protected void encode3rdLevelEventCode(int pos) throws IOException {
		// 1st level
		writeEventCode(currentRule.getNumberOfEvents(), currentRule
				.get1stLevelEventCodeLength(fidelityOptions));

		// 2nd level
		int ch2 = currentRule.get2ndLevelCharacteristics(fidelityOptions);
		int ec2 = ch2 > 0 ? ch2 - 1 : 0; // any 2nd level events
		writeEventCode(ec2, MethodsBag.getCodingLength(ch2));

		// 3rd level
		int ch3 = currentRule.get3rdLevelCharacteristics(fidelityOptions);
		assert (pos < ch3);
		writeEventCode(pos, MethodsBag.getCodingLength(ch3));
	}

	protected void encodeQName(String uri, String localName) throws IOException {
		// encode expanded name (uri followed by localName)
		writeUri(uri);
		writeLocalName(localName, uri);
	}

	protected void encodeQNamePrefix(String uri, String prefix)
			throws IOException {
		if (preservePrefix) {
			@SuppressWarnings("unchecked")
			Enumeration<String> prefixes4GivenURI = this.namespaces
					.getPrefixes(uri);

			if (uri.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
				// default namespace
			} else if (prefixes4GivenURI.hasMoreElements()) {

				int numberOfPrefixes = 0;
				int id = Constants.NOT_FOUND;

				do {
					if (prefixes4GivenURI.nextElement().equals(prefix)) {
						id = numberOfPrefixes;
					}
					numberOfPrefixes++;
				} while (prefixes4GivenURI.hasMoreElements());

				if (numberOfPrefixes > 1) {
					if (id == Constants.NOT_FOUND) {
						// choose *one* prefix which gets modified by
						// local-element-ns anyway ?
						id = 0;
					}
					// overlapping URIs
					writeEventCode(id, MethodsBag
							.getCodingLength(numberOfPrefixes));
				}
			} else {
				/*
				 * If there are no prefixes specified for the URI of the QName
				 * by preceding NS events in the EXI stream, the prefix is
				 * undefined. An undefined prefix is represented using zero bits
				 * (i.e., omitted).
				 */
			}
		}
	}

	public void encodeStartDocument() throws EXIException, IOException {
		if (this.os == null) {
			throw new EXIException(
					"No valid EXI OutputStream set for encoding. Please use setOutput( ... )");
		}
		initForEachRun();

		EventInformation ei = currentRule.lookFor(EventType.START_DOCUMENT);

		// Note: no EventCode needs to be written since there is only
		// one choice
		if (ei == null) {
			throw new EXIException("No EXI Event found for startDocument");
		}

		replaceRuleAtTheTop(ei.next);
	}

	public void encodeEndDocument() throws EXIException, IOException {
		EventInformation ei = currentRule.lookFor(EventType.END_DOCUMENT);

		if (ei != null) {
			// encode EventCode
			encode1stLevelEventCode(ei.getEventCode());
		} else {
			throw new EXIException("No EXI Event found for endDocument");
		}

		// flush chunk(s) to bit/byte output stream
		flush();
	}

	public void encodeStartElement(String uri, String localName, String prefix)
			throws EXIException, IOException {

		sePrefix = prefix;
		EventInformation ei;

		Rule nextTopRule;

//		boolean isGenericSE = true;

		StartElement nextSE;

		if ((ei = currentRule.lookFor(EventType.START_ELEMENT, uri, localName)) != null) {
			assert (ei.event.isEventType(EventType.START_ELEMENT));
			// encode 1st level EventCode
			encode1stLevelEventCode(ei.getEventCode());
			// qname implicit by SE(qname) event
//			isGenericSE = false;
			// next rule at the top
			nextTopRule = ei.next;
			//	next SE ...
			nextSE = (StartElement) ei.event;
		} else if ((ei = currentRule.lookFor(EventType.START_ELEMENT_NS, uri)) != null) {
			assert (ei.event.isEventType(EventType.START_ELEMENT_NS));
			// encode 1st level EventCode
			encode1stLevelEventCode(ei.getEventCode());
			// encode local-name only
			writeLocalName(localName, uri);
			// next rule at the top
			nextTopRule = ei.next;
			//	next SE ...
			nextSE = getGenericStartElement(uri, localName);
		} else {
			// try SE(*), generic SE on first level
			if ((ei = currentRule.lookFor(EventType.START_ELEMENT_GENERIC)) != null) {
				assert (ei.event.isEventType(EventType.START_ELEMENT_GENERIC));
				// encode 1st level EventCode
				encode1stLevelEventCode(ei.getEventCode());
				// next rule at the top
				nextTopRule = ei.next;
			} else {
				// Undeclared SE(*) can be found on 2nd level
				int ecSEundeclared = currentRule.get2ndLevelEventCode(
						EventType.START_ELEMENT_GENERIC_UNDECLARED,
						fidelityOptions);

				if (ecSEundeclared == Constants.NOT_FOUND) {
					// Note: should never happen except in strict mode
					throw new EXIException("Unexpected SE {" + uri + "}"
							+ localName + ", " + exiFactory.toString());
				}
				// encode [undeclared] event-code
				encode2ndLevelEventCode(ecSEundeclared);
				// next rule at the top
				nextTopRule = currentRule.getElementContentRule();
			}
			// encode entire qualified name
			encodeQName(uri, localName);
			//	next SE ...
			nextSE = getGenericStartElement(uri, localName);
			// learning for built-in grammar,
			currentRule.learnStartElement(nextSE);
		}

		// prefix
		encodeQNamePrefix(uri, prefix);
		// step forward in current rule (replace rule at the top)
		replaceRuleAtTheTop(nextTopRule);
//		if (TEST_NEW) {
			// push context
			pushElementContext(nextSE);
			// update and push element rule
			pushElementRule(nextSE.getRule());
//		} else {
//			// push context
//			pushElementContext(uri, localName);
//			// update and push element rule
//			pushElementRule(isGenericSE);
//		}
	}

	public void encodeNamespaceDeclaration(String uri, String prefix)
			throws EXIException, IOException {
		namespaces.declarePrefix(prefix, uri);

		if (preservePrefix) {
			assert (sePrefix != null);

			// event code
			int ec2 = currentRule.get2ndLevelEventCode(
					EventType.NAMESPACE_DECLARATION, fidelityOptions);
			assert (currentRule.get2ndLevelEvent(ec2, fidelityOptions) == EventType.NAMESPACE_DECLARATION);
			encode2ndLevelEventCode(ec2);

			// prefix mapping
			writeUri(uri);
			writePrefix(prefix, uri);

			// local-element-ns
			channel.encodeBoolean(prefix.equals(sePrefix));
			// writeBoolean(prefix.equals(sePrefix));
		}
	}

	public void encodeEndElement() throws EXIException, IOException {
		// int ec = currentRule.get1stLevelEventCode(eventEE);
		EventInformation ei = currentRule.lookFor(EventType.END_ELEMENT);

		// Special case: SAX does not inform about empty ("") CH events
		// --> if EE is not found check whether an empty CH event *helps*
		if (ei == null && currentRule.lookFor(EventType.CHARACTERS) != null) {
			// encode empty characters first
			this.encodeCharacters(Constants.EMPTY_STRING);
			// try the EE event once again
			// ec = currentRule.get1stLevelEventCode(eventEE);
			ei = currentRule.lookFor(EventType.END_ELEMENT);
		}

		if (ei != null) {
			// encode EventCode
			encode1stLevelEventCode(ei.getEventCode());
		} else {
			// Undeclared EE can be found on 2nd level
			int ecEEundeclared = currentRule.get2ndLevelEventCode(
					EventType.END_ELEMENT_UNDECLARED, fidelityOptions);

			if (ecEEundeclared == Constants.NOT_FOUND) {
				// Note: should never happen except in strict mode
				throw new EXIException("Unexpected EE {" + elementContext
						+ ", " + exiFactory.toString());
			} else {
				// encode [undeclared] event-code
				encode2ndLevelEventCode(ecEEundeclared);
				// learn end-element event ?
				currentRule.learnEndElement();
			}
		}

		// pop the rule from the top of the stack
		popElementContext();
		popElementRule();
	}

	public void encodeXsiType(String raw) throws EXIException, IOException {
		int ec2 = currentRule.get2ndLevelEventCode(
				EventType.ATTRIBUTE_XSI_TYPE, fidelityOptions);

		if (ec2 != Constants.NOT_FOUND) {
			assert (currentRule.get2ndLevelEvent(ec2, fidelityOptions) == EventType.ATTRIBUTE_XSI_TYPE);
			// encode event-code, AT(xsi:type)
			encode2ndLevelEventCode(ec2);

			String xsiTypePrefix = QNameUtilities.getPrefixPart(raw);
			String xsiTypeURI = namespaces.getURI(xsiTypePrefix);
			String xsiTypeLocalName;
			/*
			 * The value of each AT (xsi:type) event is represented as a QName .
			 * If there is no namespace in scope for the specified qname prefix,
			 * the QName uri is set to empty ("") and the QName localName is set
			 * to the full lexical value of the QName, including the prefix.
			 */
			if (xsiTypeURI == null) {
				xsiTypeURI = XMLConstants.NULL_NS_URI;
				xsiTypeLocalName = raw;
			} else {
				xsiTypeLocalName = QNameUtilities.getLocalPart(raw);
			}
			// xsi:type "content" as qname
			encodeQName(xsiTypeURI, xsiTypeLocalName);
			// prefix
			encodeQNamePrefix(xsiTypeURI, xsiTypePrefix);

			// lookup type-grammar
			TypeGrammar tg = grammar.getTypeGrammar(xsiTypeURI,
					xsiTypeLocalName);

			// grammar exists ?
			if (tg != null) {
				// update grammar according to given xsi:type
				replaceRuleAtTheTop(tg.getType());
				// this.scopeTypeURI = xsiTypeURI;
				// this.scopeTypeLocalName = xsiTypeLocalName;
			}
		} else {
			// encode as any other attribute
			encodeAttribute(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,
					Constants.XSI_TYPE, "", raw);
		}
	}

	public void encodeXsiNil(String rawNil) throws EXIException, IOException {
		if (currentRule.isSchemaRule()) {
			if (nil.parse(rawNil)) {
				// schema-valid boolean
				int ec2 = currentRule.get2ndLevelEventCode(
						EventType.ATTRIBUTE_XSI_NIL, fidelityOptions);

				if (ec2 != Constants.NOT_FOUND) {
					// encode event-code only
					encode2ndLevelEventCode(ec2);
				} else {
					// encode event-code & qname
					ec2 = currentRule.get2ndLevelEventCode(
							EventType.ATTRIBUTE_GENERIC_UNDECLARED,
							fidelityOptions);
					assert (ec2 != Constants.NOT_FOUND);
					encode2ndLevelEventCode(ec2);
					// qualified name
					encodeQName(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,
							Constants.XSI_NIL);
					encodeQNamePrefix(
							XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "");
				}

				// encode nil value as Boolean
				channel.encodeBoolean(nil.getBoolean());

				if (nil.getBoolean()) { // jump to typeEmpty
					replaceRuleAtTheTop(currentRule.getTypeEmpty());
				}
			} else {
				// If the value is not a schema-valid Boolean, the
				// AT (xsi:nil) event is represented by the AT(*)
				// [schema-invalid value] terminal
				// encode invalid 2nd level AT event-code
				encodeAttributeAnySchemaInvalid(
						XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,
						Constants.XSI_NIL, "", rawNil);
			}
		} else {
			// encode as any other attribute
			encodeAttribute(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,
					Constants.XSI_NIL, "", rawNil);
		}
	}

	public void encodeAttribute(final String uri, final String localName,
			String prefix, String value) throws EXIException, IOException {
		EventInformation ei = this.lookForAttribute(uri, localName);

		if (ei != null) {
			EventType eventType = ei.event.getEventType();

			Datatype datatype;

			if (eventType == EventType.ATTRIBUTE) {
				datatype = ((DatatypeEvent) (ei.event)).getDatatype();
			} else {
				assert (eventType == EventType.ATTRIBUTE_NS || eventType == EventType.ATTRIBUTE_GENERIC);
				// global attribute ?
				Attribute globalAT = grammar.getGlobalAttribute(uri, localName);
				datatype = globalAT == null ? BuiltIn.DEFAULT_DATATYPE
						: globalAT.getDatatype();
			}

			boolean valid = false;

			// encode event-code
			if (isTypeValid(datatype, value)) {
				// schema-valid
				valid = true;
				encode1stLevelEventCode(ei.getEventCode());
			} else {
				// schema-invalid AT
				int ec2ATdeviated = currentRule.get2ndLevelEventCode(
						EventType.ATTRIBUTE_INVALID_VALUE, fidelityOptions);
				encode2ndLevelEventCode(ec2ATdeviated);
				// encode 3rd level event-code
				// AT specialty: calculate 3rd level attribute event-code
				int eventCode3 = ei.getEventCode()
						- currentRule.getLeastAttributeEventCode();
				writeEventCode(eventCode3, MethodsBag
						.getCodingLength(currentRule
								.getNumberOfSchemaDeviatedAttributes()));
			}

			// EventType.ATTRIBUTE --> qname already known
			if (eventType != EventType.ATTRIBUTE) {
				if (eventType == EventType.ATTRIBUTE_NS) {
					// encode localName only
					writeLocalName(localName, uri);
				} else {
					assert (eventType == EventType.ATTRIBUTE_GENERIC);
					// encode entire expanded name
					encodeQName(uri, localName);
				}
			}
			// prefix ?
			encodeQNamePrefix(uri, prefix);

			// at context
			NameContext atContext = getAttributeContext(uri, localName);
			// pushAttributeContext(uri, localName);
			if (valid) {
				// encode value type-aware
				writeValueTypeValid(atContext);
			} else {
				// encode content as string
				writeValueAsString(atContext, value);
			}
			// step forward in current rule (replace rule at the top)
			replaceRuleAtTheTop(ei.next);
		} else {
			// Undeclared AT(*) can be found on 2nd level
			int ecATundeclared = currentRule.get2ndLevelEventCode(
					EventType.ATTRIBUTE_GENERIC_UNDECLARED, fidelityOptions);

			if (ecATundeclared != Constants.NOT_FOUND) {
				// encode event-code
				encode2ndLevelEventCode(ecATundeclared);
				// encode unexpected attribute & learn attribute
				// event ?
				encodeQName(uri, localName);
				// prefix
				encodeQNamePrefix(uri, prefix);
				// at context
				// pushAttributeContext(uri, localName);
				// value as string
				writeValueAsString(getAttributeContext(uri, localName), value);
				currentRule.learnAttribute(uri, localName);
			} else {
				// Warn encoder that the attribute is simply skipped
				// Note: should never happen except in strict mode
				assert (fidelityOptions.isStrict());
				throwWarning("Skip AT " + localName);
			}
		}
	}

	protected void encodeAttributeAnySchemaInvalid(final String uri,
			final String localName, final String prefix, String value)
			throws IOException {
		if (fidelityOptions.isStrict()) {
			throwWarning("Prune AT" + localName);
		} else {
			// encode schema-invalid value AT
			int ec2ATdeviated = currentRule.get2ndLevelEventCode(
					EventType.ATTRIBUTE_INVALID_VALUE, fidelityOptions);
			encode2ndLevelEventCode(ec2ATdeviated);
			// encode 3rd level event-code
			int eventCode3 = currentRule.getNumberOfSchemaDeviatedAttributes() - 1;
			writeEventCode(eventCode3, MethodsBag.getCodingLength(currentRule
					.getNumberOfSchemaDeviatedAttributes()));
			// qname
			encodeQName(uri, localName);
			encodeQNamePrefix(uri, prefix);

			// at context
			// pushAttributeContext(uri, localName);

			// encode content as string
			writeValueAsString(getAttributeContext(uri, localName), value);

			// popAttributeContext();
		}
	}

	public void encodeCharacters(String chars) throws EXIException, IOException {

		EventInformation ei = currentRule.lookFor(EventType.CHARACTERS);

		// valid value and valid event-code ?
		if (ei != null
				&& isTypeValid(((DatatypeEvent) ei.event).getDatatype(), chars)) {
			// right characters event found & data type-valid
			// --> encode EventCode, schema-valid content plus grammar moves
			// on
			encode1stLevelEventCode(ei.getEventCode());
			writeValueTypeValid(elementContext);
			// step forward in current rule (replace rule at the top)
			replaceRuleAtTheTop(ei.next);
		} else {
			// generic CH (on first level)
			ei = currentRule.lookFor(EventType.CHARACTERS_GENERIC);

			if (ei != null) {
				// encode EventCode
				encode1stLevelEventCode(ei.getEventCode());
				// encode schema-invalid content as string plus moves on in
				// grammar
				replaceRuleAtTheTop(ei.next);
				writeValueAsString(elementContext, chars);
			} else {
				// Undeclared CH can be found on 2nd level
				int ecCHundeclared = currentRule.get2ndLevelEventCode(
						EventType.CHARACTERS_GENERIC_UNDECLARED,
						fidelityOptions);

				if (ecCHundeclared == Constants.NOT_FOUND) {
					// skip characters & throw warning
					throwWarning("Skip CH: '" + chars + "'");
				} else {
					// encode [undeclared] event-code
					encode2ndLevelEventCode(ecCHundeclared);
					// learn characters event ?
					currentRule.learnCharacters();
					// content as string
					writeValueAsString(elementContext, chars);
					// step forward in current rule
					replaceRuleAtTheTop(currentRule.getElementContentRule());
				}
			}
		}
	}

	public void encodeDocType(String name, String publicID, String systemID,
			String text) throws EXIException, IOException {
		if (fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_DTD)) {
			// DOCTYPE can be found on 2nd level
			int ec2 = currentRule.get2ndLevelEventCode(EventType.DOC_TYPE,
					fidelityOptions);
			encode2ndLevelEventCode(ec2);

			// name, public, system, text AS string
			writeString(name);
			writeString(publicID);
			writeString(systemID);
			writeString(text);
		}
	}

	public void encodeEntityReference(String name) throws EXIException,
			IOException {
		if (fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_DTD)) {
			// EntityReference can be found on 2nd level
			int ec2 = currentRule.get2ndLevelEventCode(
					EventType.ENTITY_REFERENCE, fidelityOptions);
			encode2ndLevelEventCode(ec2);

			// name AS string
			writeString(name);
		}
	}

	public void encodeComment(char[] ch, int start, int length)
			throws EXIException, IOException {
		if (fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_COMMENT)) {
			// comments can be found on 3rd level
			int ec3 = currentRule.get3rdLevelEventCode(EventType.COMMENT,
					fidelityOptions);
			encode3rdLevelEventCode(ec3);

			// encode CM content
			writeString(new String(ch, start, length));

			// step forward (if not alreay content rule)
			replaceRuleAtTheTop(currentRule.getElementContentRule());
		}
	}

	public void encodeProcessingInstruction(String target, String data)
			throws EXIException, IOException {
		if (fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_PI)) {
			// processing instructions can be found on 3rd level
			int ec3 = currentRule.get3rdLevelEventCode(
					EventType.PROCESSING_INSTRUCTION, fidelityOptions);
			encode3rdLevelEventCode(ec3);

			// encode PI content
			writeString(target);
			writeString(data);

			// step forward
			replaceRuleAtTheTop(currentRule.getElementContentRule());
		}
	}

	public void encodeEndFragmentSelfContained() throws EXIException {
		throw new RuntimeException("[EXI] SelfContained");
	}

	public int encodeStartFragmentSelfContained(String uri, String localName,
			String prefix) throws EXIException {
		throw new RuntimeException("[EXI] SelfContained");
	}

}
