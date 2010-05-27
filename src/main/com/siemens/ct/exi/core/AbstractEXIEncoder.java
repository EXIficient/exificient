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

public abstract class AbstractEXIEncoder extends AbstractEXICoder implements
		EXIEncoder {

	// prefix of previous start element (relevant for preserving prefixes)
	protected String sePrefix = null;

	// OutputStream & Channel
	protected EncoderChannel channel;
	protected OutputStream os;

	// Type Encoder (including string encoder etc.)
	protected TypeEncoder typeEncoder;

	public AbstractEXIEncoder(EXIFactory exiFactory) {
		super(exiFactory);
		
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

	protected boolean isTypeValid(Datatype datatype, String value) {
		return typeEncoder.isValid(datatype, value);
	}

	protected abstract void writeValue(QName valueContext) throws IOException;

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

	public void encodeXsiType(String raw, String pfx) throws EXIException,
			IOException {
		/*
		 * The value of each AT (xsi:type) event is represented as a QName.
		 */
		typeEncoder.isValid(qnameDatatype, raw);
		
		QName xsiQName = qnameDatatype.getQName();
		SchemaInformedRule tg = grammar.getTypeGrammar(xsiQName);

		int ec2 = currentRule.get2ndLevelEventCode(
				EventType.ATTRIBUTE_XSI_TYPE, fidelityOptions);

		if (ec2 != Constants.NOT_FOUND) {
			assert (currentRule.get2ndLevelEvent(ec2, fidelityOptions) == EventType.ATTRIBUTE_XSI_TYPE);

			// encode event-code, AT(xsi:type)
			encode2ndLevelEventCode(ec2);
			// prefix
			qnameDatatype.encodeQNamePrefix(
					XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, pfx, channel);
		} else {
			// Note: cannot be encoded as any other attribute due to the
			// different channels in compression mode
			EventInformation ei = currentRule
					.lookForEvent(EventType.ATTRIBUTE_GENERIC);

			if (ei != null) {
				encode1stLevelEventCode(ei.getEventCode());
			} else {
				ec2 = currentRule
						.get2ndLevelEventCode(
								EventType.ATTRIBUTE_GENERIC_UNDECLARED,
								fidelityOptions);
				if (ec2 != Constants.NOT_FOUND) {
					encode2ndLevelEventCode(ec2);
					currentRule.learnAttribute(new Attribute(XSI_TYPE));
				} else {
					throw new EXIException("TypeCast " + raw
							+ " not encodable!");
				}
			}
			// xsi:type as qname
			qnameDatatype.encodeQName(
					XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,
					Constants.XSI_TYPE, pfx, channel);
		}

		// xsi:type value "content" as qname
		typeEncoder.writeValue(XSI_TYPE, channel);
		
		// grammar exists ?
		if (tg != null) {
			// update grammar according to given xsi:type
			currentRule = tg;
		}
	}

	public void encodeXsiNil(String value, String pfx) throws EXIException,
			IOException {
		if (currentRule.isSchemaInformed()) {
			SchemaInformedRule siCurrentRule = (SchemaInformedRule) currentRule;
			
			if (typeEncoder.isValid(booleanDatatype, value)) {
				// schema-valid boolean
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
					typeEncoder.writeValue(XSI_NIL, channel);
					
					if (booleanDatatype.getBoolean()) { // jump to typeEmpty
						// update current rule
						currentRule = siCurrentRule.getTypeEmpty();
					}
				} else {
					EventInformation ei = currentRule
							.lookForEvent(EventType.ATTRIBUTE_GENERIC);
					if (ei != null) {
						encode1stLevelEventCode(ei.getEventCode());
						// qname & prefix
						qnameDatatype
								.encodeQName(
										XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,
										Constants.XSI_NIL, pfx, channel);
						
						// encode nil value as Boolean
						typeEncoder.writeValue(XSI_NIL, channel);
						
						if (booleanDatatype.getBoolean()) { // jump to typeEmpty
							// update current rule
							currentRule = siCurrentRule.getTypeEmpty();
						}
					} else {
						throw new EXIException("Attribute xsi=nil='"
								+ value + "' cannot be encoded!");
					}
				}
			} else {
				// If the value is not a schema-valid Boolean, the
				// AT (xsi:nil) event is represented by
				// the AT (*) [untyped value] terminal
				encodeSchemaInvalidAttributeEventCode(currentRule
						.getNumberOfDeclaredAttributes());
				qnameDatatype.encodeQName(
						XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,
						Constants.XSI_NIL, pfx, channel);
				Datatype datatype = BuiltIn.DEFAULT_DATATYPE;
				isTypeValid(datatype, value);
				writeValue(XSI_NIL);
			}
		} else {
			// encode as any other attribute
			encodeAttribute(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,
					Constants.XSI_NIL, pfx, value);
		}
	}

	protected void encodeSchemaInvalidAttributeEventCode(int eventCode3)
			throws IOException {
		// schema-invalid AT
		int ec2ATdeviated = currentRule.get2ndLevelEventCode(
				EventType.ATTRIBUTE_INVALID_VALUE, fidelityOptions);
		encode2ndLevelEventCode(ec2ATdeviated);
		// encode 3rd level event-code
		// AT specialty: calculate 3rd level attribute event-code
		// int eventCode3 = ei.getEventCode()
		// - currentRule.getLeastAttributeEventCode();
		channel.encodeNBitUnsignedInteger(eventCode3,
				MethodsBag.getCodingLength(currentRule
						.getNumberOfDeclaredAttributes() + 1));
	}

	public void encodeAttribute(final String uri, final String localName,
			String prefix, String value) throws EXIException, IOException {
		EventInformation ei;
		Datatype datatype;
		QName atContext;
		Rule next;

		if ((ei = currentRule.lookForAttribute(uri, localName)) != null) {
			// declared AT(uri:localName)
			Attribute at = (Attribute) (ei.event);
			atContext = at.getQName();
			datatype = at.getDatatype();
			next = ei.next;
			if (isTypeValid(datatype, value)) {
				encode1stLevelEventCode(ei.getEventCode());
				qnameDatatype.encodeQNamePrefix(uri, prefix, channel);
			} else {
				// AT specialty: calculate 3rd level attribute event-code
				int eventCode3 = ei.getEventCode()
						- currentRule.getLeastAttributeEventCode();
				encodeSchemaInvalidAttributeEventCode(eventCode3);
				qnameDatatype.encodeQNamePrefix(uri, prefix, channel);
				datatype = BuiltIn.DEFAULT_DATATYPE;
				isTypeValid(datatype, value);
			}
		} else if ((ei = currentRule.lookForAttributeNS(uri)) != null
				|| (ei = currentRule.lookForEvent(EventType.ATTRIBUTE_GENERIC)) != null) {
			// declared AT(uri:*) OR declared AT(*)
			atContext = new QName(uri, localName);
			next = ei.next;
			/*
			 * If a global attribute definition exists for qname, let
			 * global-type be the datatype of the global attribute.
			 */
			Attribute globalAT = grammar.getGlobalAttribute(atContext);
			if (globalAT != null) {
				datatype = globalAT.getDatatype();
				if (isTypeValid(datatype, value)) {
					/*
					 * If the attribute value can be represented using the
					 * datatype representation associated with global-type, it
					 * SHOULD be represented using the datatype representation
					 * associated with global-type (see 7. Representing Event
					 * Content).
					 */
					encode1stLevelEventCode(ei.getEventCode());
				} else {
					/*
					 * If the attribute value is not represented using the
					 * datatype representation associated with global-type,
					 * represent the attribute event using the AT (*) [untyped
					 * value] terminal (see 8.5.4.4 Undeclared Productions).
					 */
					// AT (*) [untyped value] Element i, j n.(m+1).(x)
					// x represents the number of attributes declared in the
					// schema for this context
					encodeSchemaInvalidAttributeEventCode(currentRule
							.getNumberOfDeclaredAttributes());
					datatype = BuiltIn.DEFAULT_DATATYPE;
					isTypeValid(datatype, value);
				}
			} else {
				// NO global attribute
				encode1stLevelEventCode(ei.getEventCode());
				datatype = BuiltIn.DEFAULT_DATATYPE;
				isTypeValid(datatype, value);
			}
			// qname
			if (ei.event.isEventType(EventType.ATTRIBUTE_GENERIC)) {
				qnameDatatype.encodeQName(uri, localName, prefix, channel);
			} else {
				assert (ei.event.isEventType(EventType.ATTRIBUTE_NS));
				qnameDatatype.writeLocalName(localName, uri, prefix, channel);
			}

		} else {
			// Undeclared AT(*) can be found on 2nd level
			next = currentRule;

			int ecATundeclared = currentRule.get2ndLevelEventCode(
					EventType.ATTRIBUTE_GENERIC_UNDECLARED, fidelityOptions);

			if (ecATundeclared == Constants.NOT_FOUND) {
				assert (fidelityOptions.isStrict());
				throw new EXIException("Attribute '" + localName
						+ "' cannot be encoded!");
			}
			assert (ecATundeclared != Constants.NOT_FOUND);

			Attribute globalAT;
			if (currentRule.isSchemaInformed()
					&& (globalAT = grammar.getGlobalAttribute(new QName(uri,
							localName))) != null) {
				datatype = globalAT.getDatatype();
				if (isTypeValid(datatype, value)) {
					/*
					 * If the attribute value can be represented using the
					 * datatype representation associated with global-type, it
					 * SHOULD be represented using the datatype representation
					 * associated with global-type (see 7. Representing Event
					 * Content).
					 */
					// encode event-code
					encode2ndLevelEventCode(ecATundeclared);
				} else {
					/*
					 * If the attribute value is not represented using the
					 * datatype representation associated with global-type,
					 * represent the attribute event using the AT (*) [untyped
					 * value] terminal (see 8.5.4.4 Undeclared Productions).
					 */
					encodeSchemaInvalidAttributeEventCode(currentRule
							.getNumberOfDeclaredAttributes());
					datatype = BuiltIn.DEFAULT_DATATYPE;
					isTypeValid(datatype, value);
				}
				atContext = qnameDatatype.encodeQName(uri, localName, prefix,
						channel);
			} else {
				// schema-less
				// encode event-code
				encode2ndLevelEventCode(ecATundeclared);
				// encode unexpected attribute & learn attribute event ?
				atContext = qnameDatatype.encodeQName(uri, localName, prefix,
						channel);
				currentRule.learnAttribute(new Attribute(atContext));
				// datatype value
				datatype = BuiltIn.DEFAULT_DATATYPE;
				isTypeValid(datatype, value);
			}
		}

		// so far: event-code has been written & datatype is settled
		// the actual value is still missing
		assert (datatype != null);
		assert (atContext != null);
		writeValue(atContext);

		// update current rule
		currentRule = next;
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
			writeValue(elementContext.qname);
			// update current rule
			currentRule = ei.next;
		} else {
			// generic CH (on first level)
			ei = currentRule.lookForEvent(EventType.CHARACTERS_GENERIC);

			if (ei != null) {
				// encode EventCode
				encode1stLevelEventCode(ei.getEventCode());
				// encode schema-invalid content as string
				isTypeValid(BuiltIn.DEFAULT_DATATYPE, chars);
				writeValue(elementContext.qname);
				// writeValueAsString(elementContext.qname, chars);
				// update current rule
				currentRule = ei.next;
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
						if (exiFactory.isFragment() && trim.length() == 0) {
							// skip empty characters in "outer" fragment element throw warning
							throwWarning("Skip CH: '" + chars + "'");
						} else {
							assert (fidelityOptions.isStrict());
							throw new EXIException("Characters '" + chars
										+ "' cannot be encoded!");	
						}
					} else {
						// encode [undeclared] event-code
						encode2ndLevelEventCode(ecCHundeclared);
						// learn characters event ?
						currentRule.learnCharacters();
						// content as string
						// writeValueAsString(elementContext.qname, chars);
						isTypeValid(BuiltIn.DEFAULT_DATATYPE, chars);
						writeValue(elementContext.qname);
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
