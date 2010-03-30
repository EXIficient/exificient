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
import java.io.OutputStream;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.EXIEncoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.EventInformation;
import com.siemens.ct.exi.grammar.event.Attribute;
import com.siemens.ct.exi.grammar.event.DatatypeEvent;
import com.siemens.ct.exi.grammar.event.EventType;
import com.siemens.ct.exi.grammar.event.StartElement;
import com.siemens.ct.exi.grammar.rule.Rule;
import com.siemens.ct.exi.grammar.rule.SchemaInformedRule;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.types.BuiltIn;
import com.siemens.ct.exi.types.TypeEncoder;
import com.siemens.ct.exi.util.MethodsBag;
import com.siemens.ct.exi.util.datatype.XSDBoolean;
import com.siemens.ct.exi.util.xml.QNameUtilities;

public abstract class AbstractEXIEncoder extends AbstractEXICoder implements
		EXIEncoder {

	// prefix of previous start element (relevant for preserving prefixes)
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

	// PI, Comment, etc.
	protected void writeString(String text) throws IOException {
		channel.encodeString(text);
	}

	protected EventInformation lookForAttribute(String uri, String localName) {
		EventInformation ei;

		// try to find declared AT(uri:localName)
		ei = currentRule.lookForAttribute(uri, localName);

		if (ei == null) {
			// try to find declared AT(uri:*)
			ei = currentRule.lookForAttributeNS(uri);

			if (ei == null) {
				// try to find declared AT(*), generic AT on first level
				ei = currentRule.lookForEvent(EventType.ATTRIBUTE_GENERIC);
			}
		}

		return ei;
	}

	protected boolean isTypeValid(Datatype datatype, String value) {
		return typeEncoder.isValid(datatype, value);
	}

	protected abstract void writeValueTypeValid(QName valueContext)
			throws IOException;

	protected abstract void writeValueAsString(QName valueContext, String value)
			throws IOException;

	/*
	 * Event-Codes
	 */

	protected void encode1stLevelEventCode(int pos) throws IOException {
		int codeLength = currentRule
				.get1stLevelEventCodeLength(fidelityOptions);
		if (codeLength > 0) {
			channel.encodeNBitUnsignedInteger(pos, codeLength);
		}
	}

	protected void encode2ndLevelEventCode(int pos) throws IOException {
		// 1st level
		channel.encodeNBitUnsignedInteger(currentRule.getNumberOfEvents(),
				currentRule.get1stLevelEventCodeLength(fidelityOptions));

		// 2nd level
		int ch2 = currentRule.get2ndLevelCharacteristics(fidelityOptions);
		assert (pos < ch2);

		channel.encodeNBitUnsignedInteger(pos, MethodsBag.getCodingLength(ch2));
	}

	protected void encode3rdLevelEventCode(int pos) throws IOException {
		// 1st level
		channel.encodeNBitUnsignedInteger(currentRule.getNumberOfEvents(),
				currentRule.get1stLevelEventCodeLength(fidelityOptions));

		// 2nd level
		int ch2 = currentRule.get2ndLevelCharacteristics(fidelityOptions);
		int ec2 = ch2 > 0 ? ch2 - 1 : 0; // any 2nd level events
		channel.encodeNBitUnsignedInteger(ec2, MethodsBag.getCodingLength(ch2));

		// 3rd level
		int ch3 = currentRule.get3rdLevelCharacteristics(fidelityOptions);
		assert (pos < ch3);
		channel.encodeNBitUnsignedInteger(pos, MethodsBag.getCodingLength(ch3));
	}

	public void encodeStartDocument() throws EXIException, IOException {
		if (this.os == null) {
			throw new EXIException(
					"No valid EXI OutputStream set for encoding. Please use setOutput( ... )");
		}
		initForEachRun();

		EventInformation ei = currentRule
				.lookForEvent(EventType.START_DOCUMENT);

		// Note: no EventCode needs to be written since there is only
		// one choice
		if (ei == null) {
			throw new EXIException("No EXI Event found for startDocument");
		}

		// update current rule
		currentRule = ei.next;
	}

	public void encodeEndDocument() throws EXIException, IOException {
		EventInformation ei = currentRule.lookForEvent(EventType.END_DOCUMENT);

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
		StartElement nextSE;

		if ((ei = currentRule.lookForStartElement(uri, localName)) != null) {
			assert (ei.event.isEventType(EventType.START_ELEMENT));
			// encode 1st level EventCode
			encode1stLevelEventCode(ei.getEventCode());
			// qname implicit by SE(qname) event, prefix only missing
			qnameDatatype.encodeQNamePrefix(uri, prefix, channel);
			// next rule at the top
			nextTopRule = ei.next;
			// next SE ...
			nextSE = (StartElement) ei.event;
		} else if ((ei = currentRule.lookForStartElementNS(uri)) != null) {
			assert (ei.event.isEventType(EventType.START_ELEMENT_NS));
			// encode 1st level EventCode
			encode1stLevelEventCode(ei.getEventCode());
			// encode local-name only
			// QName qname = writeLocalName(localName, uri);
			QName qname = qnameDatatype.writeLocalName(localName, uri, prefix,
					channel);
			// next rule at the top
			nextTopRule = ei.next;
			// next SE ...
			nextSE = getGenericStartElement(qname);
		} else {
			// try SE(*), generic SE on first level
			if ((ei = currentRule.lookForEvent(EventType.START_ELEMENT_GENERIC)) != null) {
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
			QName qname = qnameDatatype.encodeQName(uri, localName, prefix,
					channel);
			// next SE ...
			nextSE = getGenericStartElement(qname);
			// learning for built-in grammar,
			currentRule.learnStartElement(nextSE);
		}
		
		// push element
		pushElement(nextSE, nextTopRule);
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
			qnameDatatype.writeUri(uri, channel);
			qnameDatatype.writePrefix(prefix, uri, channel);

			// local-element-ns
			channel.encodeBoolean(prefix.equals(sePrefix));
		}
	}

	public void encodeEndElement() throws EXIException, IOException {
		// int ec = currentRule.get1stLevelEventCode(eventEE);
		EventInformation ei = currentRule.lookForEvent(EventType.END_ELEMENT);

		if (ei != null) {
			// encode EventCode (common case)
			encode1stLevelEventCode(ei.getEventCode());
		} else {
			// Check special case: SAX does not inform about empty ("") CH
			// events
			// --> if EE is not found check whether an empty CH event *helps*
			if (currentRule.lookForEvent(EventType.CHARACTERS) != null) {
				// encode empty characters first
				this.encodeCharacters(Constants.EMPTY_STRING);
				// try the EE event once again
				ei = currentRule.lookForEvent(EventType.END_ELEMENT);
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
		}

		// pop element from stack
		popElement();
	}

	public void encodeXsiType(String raw, String pfx) throws EXIException, IOException {
		int ec2 = currentRule.get2ndLevelEventCode(
				EventType.ATTRIBUTE_XSI_TYPE, fidelityOptions);

		if (ec2 != Constants.NOT_FOUND) {
			assert (currentRule.get2ndLevelEvent(ec2, fidelityOptions) == EventType.ATTRIBUTE_XSI_TYPE);

			// extract prefix
			String xsiTypePrefix = QNameUtilities.getPrefixPart(raw);
			// retrieve uri
			String xsiTypeURI = namespaces.getURI(xsiTypePrefix);

			/*
			 * The value of each AT (xsi:type) event is represented as a QName .
			 * If there is no namespace in scope for the specified qname prefix,
			 * the QName uri is set to empty ("") and the QName localName is set
			 * to the full lexical value of the QName, including the prefix.
			 */
			String xsiTypeLocalName;
			if (xsiTypeURI == null) {
				xsiTypeURI = XMLConstants.NULL_NS_URI;
				xsiTypeLocalName = raw;
			} else {
				xsiTypeLocalName = QNameUtilities.getLocalPart(raw);
			}

			QName xsiQName = new QName(xsiTypeURI, xsiTypeLocalName);
			SchemaInformedRule tg = grammar.getTypeGrammar(xsiQName);

			// strip use-less xsi:type casts, e.g., if we deal already with
			// the same type-grammar
			// Note: Preserve lexical value requires type cast
			if ((tg != null && currentRule != tg)
					|| fidelityOptions
							.isFidelityEnabled(FidelityOptions.FEATURE_LEXICAL_VALUE)) {
				// encode event-code, AT(xsi:type)
				encode2ndLevelEventCode(ec2);
				// prefix
				qnameDatatype.encodeQNamePrefix(
						XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,
						pfx, channel);
				// xsi:type "content" as qname
				qnameDatatype.encodeQName(xsiTypeURI, xsiTypeLocalName,
						xsiTypePrefix, channel);

				// grammar exists ?
				if (tg != null) {
					// update grammar according to given xsi:type
					currentRule = tg;
				}
			}
		} else {
			if (currentRule.isSchemaInformed()) {
				QName xsiQName = new QName(XMLConstants.NULL_NS_URI, raw);
				SchemaInformedRule tg = grammar.getTypeGrammar(xsiQName);

				// Note: cannot be encodes as any other attribute due to the
				// different channels in compression mode
				EventInformation ei = currentRule
						.lookForEvent(EventType.ATTRIBUTE_GENERIC);
				if (ei == null) {
					throw new EXIException("TypeCast " + raw
							+ " not encodable!");
				}
				encode1stLevelEventCode(ei.getEventCode());
				// qname
				qnameDatatype.encodeQName(
						XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,
						Constants.XSI_TYPE, "", channel);
				// qname typecast
				qnameDatatype.isValid(raw);
				qnameDatatype.writeValue(channel, null, null);

				// grammar exists ?
				if (tg != null) {
					// update grammar according to given xsi:type
					currentRule = tg;
				}
			} else {
				// encode as any other attribute
				encodeAttribute(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,
						Constants.XSI_TYPE, "", raw);
			}
		}
	}

	public void encodeXsiNil(String value, String pfx) throws EXIException,
			IOException {
		if (currentRule.isSchemaInformed()) {
			SchemaInformedRule siCurrentRule = (SchemaInformedRule) currentRule;
			if (nil.parse(value)) {
				// schema-valid boolean
				boolean bnil = nil.getBoolean();

				// strip use-less xsi:nil values
				if (bnil
						|| fidelityOptions
								.isFidelityEnabled(FidelityOptions.FEATURE_LEXICAL_VALUE)) {
					int ec2 = siCurrentRule.get2ndLevelEventCode(
							EventType.ATTRIBUTE_XSI_NIL, fidelityOptions);

					if (ec2 != Constants.NOT_FOUND) {
						// encode event-code only
						encode2ndLevelEventCode(ec2);
						// prefix
						qnameDatatype.encodeQNamePrefix(
								XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,
								pfx, channel);

						// encode nil value as Boolean
						channel.encodeBoolean(bnil);

						if (bnil) { // jump to typeEmpty
							// update current rule
							currentRule = siCurrentRule.getTypeEmpty();
						}
					} else {
						// try to encode xsi:nil as *normal* attribute
						encodeAttribute(
								XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,
								Constants.XSI_NIL, pfx, value);
					}
				}
			} else {
				// If the value is not a schema-valid Boolean, the
				// AT (xsi:nil) event is represented by the AT(*)
				// [schema-invalid value] terminal
				// encode invalid 2nd level AT event-code
				encodeAttributeAnySchemaInvalid(
						XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,
						Constants.XSI_NIL, pfx, value);
			}
		} else {
			// encode as any other attribute
			encodeAttribute(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,
					Constants.XSI_NIL, pfx, value);
		}
	}

	public void encodeAttribute(final String uri, final String localName,
			String prefix, String value) throws EXIException, IOException {
		EventInformation ei = this.lookForAttribute(uri, localName);

		if (ei != null) {
			EventType eventType = ei.event.getEventType();

			Datatype datatype;
			QName atContext;

			if (eventType == EventType.ATTRIBUTE) {
				Attribute at = (Attribute) (ei.event);
				datatype = at.getDatatype();
				atContext = at.getQName();
			} else {
				assert (eventType == EventType.ATTRIBUTE_NS || eventType == EventType.ATTRIBUTE_GENERIC);

				if (XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI.equals(uri)
						&& Constants.XSI_TYPE.equals(localName)) {
					// If qname is xsi:type, let target-type be the value of the
					// xsi:type attribute and assign it the QName datatype
					// representation
					datatype = this.qnameDatatype;
					atContext = null;
				} else {
					// global attribute ?
					// TODO avoid qname creation
					Attribute globalAT = grammar.getGlobalAttribute(new QName(
							uri, localName));
					datatype = globalAT == null ? BuiltIn.DEFAULT_DATATYPE
							: globalAT.getDatatype();
					atContext = null;
				}
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
				channel.encodeNBitUnsignedInteger(eventCode3, MethodsBag
						.getCodingLength(currentRule
								.getNumberOfSchemaDeviatedAttributes()));
			}

			// EventType.ATTRIBUTE --> qname already known
			if (eventType == EventType.ATTRIBUTE) {
				// qname implicit by AT(qname) event, prefix only missing
				qnameDatatype.encodeQNamePrefix(uri, prefix, channel);
			} else {
				if (eventType == EventType.ATTRIBUTE_NS) {
					// encode localName only
					atContext = qnameDatatype.writeLocalName(localName, uri,
							prefix, channel);
				} else {
					assert (eventType == EventType.ATTRIBUTE_GENERIC);
					// encode entire expanded name
					atContext = qnameDatatype.encodeQName(uri, localName,
							prefix, channel);
				}
			}

			if (valid) {
				// encode value type-aware
				writeValueTypeValid(atContext);
			} else {
				// encode content as string
				writeValueAsString(atContext, value);
			}
			// update current rule
			currentRule = ei.next;
		} else {
			// Undeclared AT(*) can be found on 2nd level
			int ecATundeclared = currentRule.get2ndLevelEventCode(
					EventType.ATTRIBUTE_GENERIC_UNDECLARED, fidelityOptions);

			if (ecATundeclared != Constants.NOT_FOUND) {
				// encode event-code
				encode2ndLevelEventCode(ecATundeclared);
				// encode unexpected attribute & learn attribute event ?
				QName atQName = qnameDatatype.encodeQName(uri, localName,
						prefix, channel);
				writeValueAsString(atQName, value);
				currentRule.learnAttribute(new Attribute(atQName));
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
			channel.encodeNBitUnsignedInteger(eventCode3, MethodsBag
					.getCodingLength(currentRule
							.getNumberOfSchemaDeviatedAttributes()));
			// qname
			QName atQName = qnameDatatype.encodeQName(uri, localName, prefix,
					channel);

			// encode content as string
			writeValueAsString(atQName, value);
		}
	}

	public void encodeCharacters(String chars) throws EXIException, IOException {

		EventInformation ei = currentRule.lookForEvent(EventType.CHARACTERS);

		// valid value and valid event-code ?
		if (ei != null
				&& isTypeValid(((DatatypeEvent) ei.event).getDatatype(), chars)) {
			// right characters event found & data type-valid
			// --> encode EventCode, schema-valid content plus grammar moves
			// on
			encode1stLevelEventCode(ei.getEventCode());
			writeValueTypeValid(elementContext.qname);
			// update current rule
			currentRule = ei.next;
		} else {
			// generic CH (on first level)
			ei = currentRule.lookForEvent(EventType.CHARACTERS_GENERIC);

			if (ei != null) {
				// encode EventCode
				encode1stLevelEventCode(ei.getEventCode());
				// encode schema-invalid content as string plus moves on in
				// grammar
				// update current rule
				currentRule = ei.next;
				writeValueAsString(elementContext.qname, chars);
			} else {
				// Note: Do we really want to encode any whitespace characters?
				String trim = chars.trim();
				if (fidelityOptions
						.isFidelityEnabled(FidelityOptions.FEATURE_LEXICAL_VALUE)
						|| trim.length() > 0) {
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
						writeValueAsString(elementContext.qname, chars);
						// update current rule
						currentRule = currentRule.getElementContentRule();
					}
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

			// update current rule
			currentRule = currentRule.getElementContentRule();
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

			// update current rule
			currentRule = currentRule.getElementContentRule();
		}
	}

}
