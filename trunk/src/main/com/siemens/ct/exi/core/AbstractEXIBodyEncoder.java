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
import com.siemens.ct.exi.EXIBodyEncoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.EncodingOptions;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.attributes.AttributeList;
import com.siemens.ct.exi.context.CoderContext;
import com.siemens.ct.exi.context.EncoderContext;
import com.siemens.ct.exi.context.EncoderContextImpl;
import com.siemens.ct.exi.context.EvolvingUriContext;
import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.context.UriContext;
import com.siemens.ct.exi.core.container.NamespaceDeclaration;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.datatype.strings.StringCoder;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.EventInformation;
import com.siemens.ct.exi.grammar.event.Attribute;
import com.siemens.ct.exi.grammar.event.AttributeNS;
import com.siemens.ct.exi.grammar.event.DatatypeEvent;
import com.siemens.ct.exi.grammar.event.EventType;
import com.siemens.ct.exi.grammar.event.StartElement;
import com.siemens.ct.exi.grammar.event.StartElementNS;
import com.siemens.ct.exi.grammar.rule.Rule;
import com.siemens.ct.exi.grammar.rule.SchemaInformedFirstStartTagRule;
import com.siemens.ct.exi.grammar.rule.SchemaInformedRule;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.types.BuiltIn;
import com.siemens.ct.exi.types.BuiltInType;
import com.siemens.ct.exi.types.TypeEncoder;
import com.siemens.ct.exi.util.MethodsBag;
import com.siemens.ct.exi.util.xml.QNameUtilities;
import com.siemens.ct.exi.values.QNameValue;
import com.siemens.ct.exi.values.StringValue;
import com.siemens.ct.exi.values.Value;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.8
 */

public abstract class AbstractEXIBodyEncoder extends AbstractEXIBodyCoder
		implements EXIBodyEncoder {

	protected final EXIHeaderEncoder exiHeader;

	/** prefix of previous start element (relevant for preserving prefixes) */
	protected String sePrefix = null;

	/** Output Channel */
	protected EncoderChannel channel;

	/** Type Encoder (including string encoder etc.) */
	protected TypeEncoder typeEncoder;

	/** Encoding options */
	protected EncodingOptions encodingOptions;

	/** Encoder Context */
	EncoderContext encoderContext;

	static final boolean LIMIT_GRAMMAR_LEARNING = false;
	
	public AbstractEXIBodyEncoder(EXIFactory exiFactory) throws EXIException {
		super(exiFactory);
		this.exiHeader = new EXIHeaderEncoder();
	}

	@Override
	protected void initFactoryInformation() throws EXIException {
		super.initFactoryInformation();

		typeEncoder = exiFactory.createTypeEncoder();
		encodingOptions = exiFactory.getEncodingOptions();

		this.encoderContext = new EncoderContextImpl(exiFactory.getGrammar()
				.getGrammarContext(), exiFactory.createStringEncoder());
	}

	@Override
	protected void initForEachRun() throws EXIException, IOException {
		super.initForEachRun();

		encoderContext.clear();
	}

	protected CoderContext getCoderContext() {
		return this.encoderContext;
	}

	/*
	 * Stream
	 */
	public void flush() throws IOException {
		channel.flush();
	}

	/*
	 * Structure Channel
	 */

	// PI, Comment, etc.
	protected void writeString(String text) throws IOException {
		channel.encodeString(text);
	}

	protected boolean isTypeValid(Datatype datatype, Value value) {
		return typeEncoder.isValid(datatype, value);
	}

	protected abstract void writeValue(QNameContext valueContext)
			throws IOException;

	/*
	 * Event-Codes
	 */

	protected void encode1stLevelEventCode(int pos) throws IOException {
		int codeLength = getCurrentRule().get1stLevelEventCodeLength(
				fidelityOptions);
		if (codeLength > 0) {
			channel.encodeNBitUnsignedInteger(pos, codeLength);
		}
	}

	protected void encode2ndLevelEventCode(int pos) throws IOException {
		// 1st level
		final Rule currentRule = getCurrentRule();
		channel.encodeNBitUnsignedInteger(currentRule.getNumberOfEvents(),
				currentRule.get1stLevelEventCodeLength(fidelityOptions));

		// 2nd level
		int ch2 = currentRule.get2ndLevelCharacteristics(fidelityOptions);
		assert (pos < ch2);

		channel.encodeNBitUnsignedInteger(pos, MethodsBag.getCodingLength(ch2));
	}

	protected void encode3rdLevelEventCode(int pos) throws IOException {
		// 1st level
		final Rule currentRule = getCurrentRule();
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
		if (this.channel == null) {
			throw new EXIException(
					"No valid EXI OutputStream set for encoding. Please use setOutput( ... )");
		}
		initForEachRun();

		EventInformation ei = getCurrentRule().lookForEvent(
				EventType.START_DOCUMENT);

		// Note: no EventCode needs to be written since there is only
		// one choice
		if (ei == null) {
			throw new EXIException("No EXI Event found for startDocument");
		}

		// update current rule
		updateCurrentRule(ei.next);
	}

	public void encodeEndDocument() throws EXIException, IOException {
		EventInformation ei = getCurrentRule().lookForEvent(
				EventType.END_DOCUMENT);

		if (ei != null) {
			// encode EventCode
			encode1stLevelEventCode(ei.getEventCode());
		} else {
			throw new EXIException("No EXI Event found for endDocument");
		}
	}

	public void encodeStartElement(QName se) throws EXIException, IOException {
		encodeStartElement(se.getNamespaceURI(), se.getLocalPart(),
				se.getPrefix());
	}

	public void encodeStartElement(String uri, String localName, String prefix)
			throws EXIException, IOException {

		sePrefix = prefix;
		EventInformation ei;

		Rule updContextRule;
		StartElement nextSE;

		Rule currentRule = getCurrentRule();
		if ((ei = currentRule.lookForStartElement(uri, localName)) != null) {
			assert (ei.event.isEventType(EventType.START_ELEMENT));
			// encode 1st level EventCode
			encode1stLevelEventCode(ei.getEventCode());
			// next SE ...
			nextSE = (StartElement) ei.event;
			// qname implicit by SE(qname) event, prefix only missing
			if (preservePrefix) {
				encoderContext.encodeQNamePrefix(nextSE.getQNameContext(),
						prefix, channel);
			}
			// next context rule
			updContextRule = ei.next;

		} else if ((ei = currentRule.lookForStartElementNS(uri)) != null) {
			assert (ei.event.isEventType(EventType.START_ELEMENT_NS));
			// encode 1st level EventCode
			encode1stLevelEventCode(ei.getEventCode());

			StartElementNS seNS = (StartElementNS) ei.event;
			EvolvingUriContext uc = encoderContext.getUriContext(seNS
					.getNamespaceUriID());

			// encode local-name (and prefix)
			QNameContext qnc = encoderContext.encodeLocalName(localName, uc,
					channel);
			if (preservePrefix) {
				encoderContext.encodeQNamePrefix(qnc, prefix, channel);
			}

			// next context rule
			updContextRule = ei.next;
			// next SE ...
			nextSE = encoderContext.getGlobalStartElement(qnc);
		} else {
			// try SE(*), generic SE on first level
			if ((ei = currentRule.lookForEvent(EventType.START_ELEMENT_GENERIC)) != null) {
				assert (ei.event.isEventType(EventType.START_ELEMENT_GENERIC));
				// encode 1st level EventCode
				encode1stLevelEventCode(ei.getEventCode());

				// next context rule
				updContextRule = ei.next;
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
				
				// limit grammar learning ?
				if(limitGrammarLearning()) {
					// encode 1st level EventCode
					currentRule = getCurrentRule();
					ei = currentRule.lookForEvent(EventType.START_ELEMENT_GENERIC);
					assert(ei != null);
					encode1stLevelEventCode(ei.getEventCode());

					// next context rule
					updContextRule = ei.next;
				} else {
					// encode [undeclared] event-code
					encode2ndLevelEventCode(ecSEundeclared);
					
					// next context rule
					updContextRule = currentRule.getElementContentRule();
				}
			}

			// encode entire qualified name
			QNameContext qnc = encoderContext.encodeQName(uri, localName,
					channel);
			if (preservePrefix) {
				encoderContext.encodeQNamePrefix(qnc, prefix, channel);
			}

			// next SE ...
			nextSE = encoderContext.getGlobalStartElement(qnc);

			// learning for built-in grammar (here ant not as part of SE_Undecl(*) because of FragmentContent!)
			currentRule.learnStartElement(nextSE);
		}

		// push element
		pushElement(updContextRule, nextSE);
	}
	
	
	// Note: returning TRUE means currentRule has been changed!
	private boolean limitGrammarLearning() throws EXIException, IOException {
		if(LIMIT_GRAMMAR_LEARNING && grammar.isSchemaInformed() && !getCurrentRule().isSchemaInformed()) {
			
			String pfx = null;
			if (this.preservePrefix) {
				// XMLConstants.W3C_XML_SCHEMA_NS_URI == "http://www.w3.org/2001/XMLSchema"
				EvolvingUriContext euc = encoderContext.getUriContext(3);
				int numberOfPrefixes = euc.getNumberOfPrefixes();
				if(numberOfPrefixes > 0) {
					pfx = euc.getPrefix(0);
				}
			}
			QNameValue type = new QNameValue(XMLConstants.W3C_XML_SCHEMA_NS_URI, "anyType", pfx);
			
			// needed to avoid grammar learning
			this.encodeAttributeXsiType(type, pfx);
			
			return true;
		} else {
			return false;
		}
		
	}

	public void encodeNamespaceDeclaration(String uri, String prefix)
			throws EXIException, IOException {
		declarePrefix(prefix, uri);

		if (preservePrefix) {
			assert (sePrefix != null);

			// event code
			final Rule currentRule = getCurrentRule();
			int ec2 = currentRule.get2ndLevelEventCode(
					EventType.NAMESPACE_DECLARATION, fidelityOptions);
			assert (currentRule.get2ndLevelEvent(ec2, fidelityOptions) == EventType.NAMESPACE_DECLARATION);
			encode2ndLevelEventCode(ec2);

			// prefix mapping
			EvolvingUriContext euc = encoderContext.encodeUri(uri, channel);
			encoderContext.encodeNamespacePrefix(euc, prefix, channel);

			// local-element-ns
			channel.encodeBoolean(prefix.equals(sePrefix));
		}
	}

	public void encodeEndElement() throws EXIException, IOException {
		Rule currentRule = getCurrentRule();
		EventInformation ei = currentRule.lookForEvent(EventType.END_ELEMENT);

		if (ei != null) {
			// encode EventCode (common case)
			encode1stLevelEventCode(ei.getEventCode());
		} else {
			// Check special case: SAX does not inform about empty ("") CH
			// events
			// --> if EE is not found check whether an empty CH event *helps*
			if ((ei = currentRule.lookForEvent(EventType.CHARACTERS)) != null) {
				BuiltInType bit = ((DatatypeEvent) ei.event).getDatatype()
						.getBuiltInType();
				switch (bit) {
				/* empty values possible */
				case BINARY_BASE64:
				case BINARY_HEX:
				case STRING:
				case LIST:
				case RCS_STRING:
					if ((ei = ei.next.lookForEvent(EventType.END_ELEMENT)) != null) {
						// encode empty characters first
						this.encodeCharactersForce(StringCoder.EMPTY_STRING_VALUE);
						// try EE again
					}
					break;
				/* no empty value possible */
				default:
					ei = null;
				}
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
					throw new EXIException("Unexpected EE {"
							+ getElementContext() + ", "
							+ exiFactory.toString());
				} else {
					// limit grammar learning ?
					if(limitGrammarLearning()) {
						// encode 1st level EventCode
						currentRule = getCurrentRule();
						ei = currentRule.lookForEvent(EventType.END_ELEMENT);
						assert(ei != null);
						encode1stLevelEventCode(ei.getEventCode());
					} else {
						// encode [undeclared] event-code
						encode2ndLevelEventCode(ecEEundeclared);
						// learn end-element event ?
						currentRule.learnEndElement();	
					}
				}
			}
		}

		// pop element from stack
		popElement();
	}

	public void encodeAttributeList(AttributeList attributes)
			throws EXIException, IOException {
		// 1. NS
		for (int i = 0; i < attributes.getNumberOfNamespaceDeclarations(); i++) {
			NamespaceDeclaration ns = attributes.getNamespaceDeclaration(i);
			this.encodeNamespaceDeclaration(ns.namespaceURI, ns.prefix);
		}

		// 2. XSI-Type
		if (attributes.hasXsiType()) {
			encodeAttributeXsiType(new StringValue(attributes.getXsiTypeRaw()),
					attributes.getXsiTypePrefix());
		}

		// 3. XSI-Nil
		if (attributes.hasXsiNil()) {
			encodeAttributeXsiNil(new StringValue(attributes.getXsiNil()),
					attributes.getXsiNilPrefix());
		}

		// 4. Remaining Attributes
		for (int i = 0; i < attributes.getNumberOfAttributes(); i++) {
			encodeAttribute(attributes.getAttributeURI(i),
					attributes.getAttributeLocalName(i),
					attributes.getAttributePrefix(i), new StringValue(
							attributes.getAttributeValue(i)));
		}
	}

	public void encodeAttributeXsiType(Value type, String pfx)
			throws EXIException, IOException {
		/*
		 * The value of each AT (xsi:type) event is represented as a QName.
		 */
		String qnamePrefix;
		String qnameURI;
		String qnameLocalName;

		if (type instanceof QNameValue) {
			QNameValue qv = ((QNameValue) type);
			qnameURI = qv.getNamespaceUri();
			qnamePrefix = qv.getPrefix();
			qnameLocalName = qv.getLocalName();
		} else {
			String sType = type.toString();
			// extract prefix
			qnamePrefix = QNameUtilities.getPrefixPart(sType);
			// String
			qnameURI = getURI(qnamePrefix);

			/*
			 * If there is no namespace in scope for the specified qname prefix,
			 * the QName uri is set to empty ("") and the QName localName is set
			 * to the full lexical value of the QName, including the prefix.
			 */
			if (qnameURI == null) {
				/* uri in scope for prefix */
				qnameURI = XMLConstants.NULL_NS_URI;
				qnameLocalName = sType;
			} else {
				qnameLocalName = QNameUtilities.getLocalPart(sType);
			}
		}

		final Rule currentRule = getCurrentRule();

		// // Note: in some cases we can simply skip the xsi:type event
		// if (!preserveLexicalValues
		// && tg != null
		// && currentRule.isSchemaInformed()
		// && tg.getTypeName() != null
		// && tg.getTypeName().equals(
		// ((SchemaInformedFirstStartTagRule) currentRule)
		// .getTypeName())
		// && !this.encodingOptions
		// .isOptionEnabled(EncodingOptions.INCLUDE_INSIGNIFICANT_XSI_TYPE)
		// ) {
		// return;
		// }

		int ec2 = currentRule.get2ndLevelEventCode(
				EventType.ATTRIBUTE_XSI_TYPE, fidelityOptions);

		if (ec2 != Constants.NOT_FOUND) {

			assert (currentRule.get2ndLevelEvent(ec2, fidelityOptions) == EventType.ATTRIBUTE_XSI_TYPE);

			// encode event-code, AT(xsi:type)
			encode2ndLevelEventCode(ec2);
			// prefix
			if (this.preservePrefix) {
				encoderContext.encodeQNamePrefix(
						encoderContext.getXsiTypeContext(), pfx, channel);
			}

		} else {
			// Note: cannot be encoded as any other attribute due to the
			// different channels in compression mode

			// try first (learned) xsi:type attribute
			EventInformation ei = currentRule.lookForAttribute(
					XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,
					Constants.XSI_TYPE);

			if (ei != null) {
				encode1stLevelEventCode(ei.getEventCode());
			} else {
				// try generic attribute
				ei = currentRule.lookForEvent(EventType.ATTRIBUTE_GENERIC);

				if (ei != null) {
					encode1stLevelEventCode(ei.getEventCode());
				} else {
					ec2 = currentRule.get2ndLevelEventCode(
							EventType.ATTRIBUTE_GENERIC_UNDECLARED,
							fidelityOptions);
					if (ec2 != Constants.NOT_FOUND) {
						encode2ndLevelEventCode(ec2);
						QNameContext qncType = encoderContext
								.getXsiTypeContext();
						currentRule.learnAttribute(new Attribute(qncType));
					} else {
						throw new EXIException("TypeCast " + type
								+ " not encodable!");
					}
				}
				// xsi:type as qname
				QNameContext qncType = encoderContext.getXsiTypeContext();
				encoderContext.encodeQName(qncType.getNamespaceUri(),
						qncType.getLocalName(), channel);

				if (this.preservePrefix) {
					encoderContext.encodeQNamePrefix(qncType, pfx, channel);
				}
			}
		}

		// write xsi:type value "content" as qname
		QNameContext qncType;
		if (preserveLexicalValues) {
			// Note: IF xsi:type values are encoded as String, prefixes need to
			// be preserved as well!
			if (qnamePrefix.length() > 0 && !preservePrefix) {
				throw new EXIException(
						"[EXI] xsi:type='"
								+ type
								+ "' not encodable. Preserve lexicalValues requires prefixes preserved as well!");
			}

			// as string
			typeEncoder.isValid(BuiltIn.DEFAULT_DATATYPE, type);
			typeEncoder.writeValue(encoderContext,
					encoderContext.getXsiTypeContext(), channel);

			EvolvingUriContext uc = encoderContext.getUriContext(qnameURI);
			if (uc != null) {
				qncType = uc.getQNameContext(qnameLocalName);
			} else {
				qncType = null;
			}
		} else {
			// typed
			qncType = encoderContext.encodeQName(qnameURI, qnameLocalName,
					channel);

			if (preservePrefix) {
				encoderContext.encodeQNamePrefix(qncType, qnamePrefix, channel);
			}
		}

		// grammar exists ?
		if (qncType != null && qncType.getTypeGrammar() != null) {
			// update grammar according to given xsi:type
			updateCurrentRule(qncType.getTypeGrammar());
		}
	}

	public void encodeAttributeXsiNil(Value nil, String pfx)
			throws EXIException, IOException {
		final Rule currentRule = getCurrentRule();
		if (currentRule.isSchemaInformed()) {
			SchemaInformedRule siCurrentRule = (SchemaInformedRule) currentRule;

			if (booleanDatatype.isValid(nil)) {

				// Note: in some cases we can simply skip the xsi:nil event
				if (!preserveLexicalValues
						&& !booleanDatatype.getBoolean()
						&& !this.encodingOptions
								.isOptionEnabled(EncodingOptions.INCLUDE_INSIGNIFICANT_XSI_NIL)) {
					return;
				}

				// schema-valid boolean
				int ec2 = siCurrentRule.get2ndLevelEventCode(
						EventType.ATTRIBUTE_XSI_NIL, fidelityOptions);

				if (ec2 != Constants.NOT_FOUND) {
					// encode event-code only
					encode2ndLevelEventCode(ec2);
					// prefix
					if (this.preservePrefix) {
						encoderContext
								.encodeQNamePrefix(
										encoderContext.getXsiNilContext(), pfx,
										channel);
					}

					// encode nil value "content" as Boolean
					if (preserveLexicalValues) {
						// as string
						typeEncoder.isValid(booleanDatatype, nil);
						typeEncoder.writeValue(encoderContext,
								encoderContext.getXsiTypeContext(), channel);
					} else {
						// typed
						booleanDatatype.writeValue(encoderContext, null,
								channel);
					}

					if (booleanDatatype.getBoolean()) { // jump to typeEmpty
						// update current rule
						updateCurrentRule(((SchemaInformedFirstStartTagRule) siCurrentRule)
								.getTypeEmpty());
					}
				} else {
					EventInformation ei = currentRule
							.lookForEvent(EventType.ATTRIBUTE_GENERIC);
					if (ei != null) {
						encode1stLevelEventCode(ei.getEventCode());
						// qname & prefix
						EvolvingUriContext euc = encoderContext.encodeUri(
								XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,
								channel);
						encoderContext.encodeLocalName(Constants.XSI_NIL, euc,
								channel);
						if (this.preservePrefix) {
							encoderContext.encodeQNamePrefix(
									encoderContext.getXsiNilContext(), pfx,
									channel);
						}

						// encode nil value "content" as Boolean
						if (preserveLexicalValues) {
							// as string
							typeEncoder.isValid(booleanDatatype, nil);
							// typeEncoder.writeValue(XSI_NIL, channel);
							typeEncoder.writeValue(encoderContext,
									encoderContext.getXsiNilContext(), channel);
						} else {
							// typed
							booleanDatatype.writeValue(encoderContext, null,
									channel);
						}

						if (booleanDatatype.getBoolean()) { // jump to typeEmpty
							// update current rule
							updateCurrentRule(((SchemaInformedFirstStartTagRule) siCurrentRule)
									.getTypeEmpty());
						}
					} else {
						throw new EXIException("Attribute xsi=nil='" + nil
								+ "' cannot be encoded!");
					}
				}
			} else {
				// If the value is not a schema-valid Boolean, the
				// AT (xsi:nil) event is represented by
				// the AT (*) [untyped value] terminal
				encodeSchemaInvalidAttributeEventCode(currentRule
						.getNumberOfDeclaredAttributes());
				EvolvingUriContext euc = encoderContext.encodeUri(
						XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, channel);
				encoderContext.encodeLocalName(Constants.XSI_NIL, euc, channel);
				if (this.preservePrefix) {
					encoderContext.encodeQNamePrefix(
							encoderContext.getXsiNilContext(), pfx, channel);
				}

				Datatype datatype = BuiltIn.DEFAULT_DATATYPE;
				isTypeValid(datatype, nil);
				this.writeValue(encoderContext.getXsiTypeContext());
			}
		} else {
			// encode as any other attribute
			encodeAttribute(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,
					Constants.XSI_NIL, pfx, nil);
		}
	}

	protected void encodeSchemaInvalidAttributeEventCode(int eventCode3)
			throws IOException {
		final Rule currentRule = getCurrentRule();
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

	public void encodeAttribute(QName at, Value value) throws EXIException,
			IOException {
		encodeAttribute(at.getNamespaceURI(), at.getLocalPart(),
				at.getPrefix(), value);
	}

	public void encodeAttribute(final String uri, final String localName,
			String prefix, Value value) throws EXIException, IOException {
		EventInformation ei;
		QNameContext qnc;
		Rule next;

		Rule currentRule = getCurrentRule();
		if ((ei = currentRule.lookForAttribute(uri, localName)) != null) {
			// declared AT(uri:localName)
			Attribute at = (Attribute) (ei.event);
			qnc = at.getQNameContext();

			if (isTypeValid(at.getDatatype(), value)) {
				encode1stLevelEventCode(ei.getEventCode());
			} else {
				// AT specialty: calculate 3rd level attribute event-code
				int eventCode3 = ei.getEventCode()
						- currentRule.getLeastAttributeEventCode();
				encodeSchemaInvalidAttributeEventCode(eventCode3);
				isTypeValid(BuiltIn.DEFAULT_DATATYPE, value);
			}
			next = ei.next;
		} else {
			ei = currentRule.lookForAttributeNS(uri);
			if (ei == null) {
				ei = currentRule.lookForEvent(EventType.ATTRIBUTE_GENERIC);
				if (ei == null) {
					// Undeclared AT(*) can be found on 2nd level
				}
			}

			Attribute globalAT;

			if (currentRule.isSchemaInformed()
					&& (globalAT = getGlobalAttribute(uri, localName)) != null) {
				assert (ei != null);
				/*
				 * In a schema-informed grammar, all productions of the form
				 * LeftHandSide : AT (*) are evaluated as follows:
				 * 
				 * Let qname be the qname of the attribute matched by AT (*) If
				 * a global attribute definition exists for qname, let
				 * global-type be the datatype of the global attribute.
				 */
				if (isTypeValid(globalAT.getDatatype(), value)) {
					/*
					 * If the attribute value can be represented using the
					 * datatype representation associated with global-type, it
					 * SHOULD be represented using the datatype representation
					 * associated with global-type (see 7. Representing Event
					 * Content).
					 */
					if (ei == null) {
						this.encodeAttributeEventCodeUndeclared(currentRule,
								localName);
					} else {
						encode1stLevelEventCode(ei.getEventCode());
					}
				} else {
					/*
					 * If the attribute value is not represented using the
					 * datatype representation associated with global-type,
					 * represent the attribute event using the AT (*) [untyped
					 * value] terminal (see 8.5.4.4 Undeclared Productions).
					 */
					/*
					 * AT (*) [untyped value] Element i, j n.(m+1).(x) x
					 * represents the number of attributes declared in the
					 * schema for this context
					 */
					encodeSchemaInvalidAttributeEventCode(currentRule
							.getNumberOfDeclaredAttributes());
					isTypeValid(BuiltIn.DEFAULT_DATATYPE, value);
				}

				if (ei == null
						|| ei.event.isEventType(EventType.ATTRIBUTE_GENERIC)) {
					// (un)declared AT(*)
					qnc = this.encoderContext.encodeQName(uri, localName,
							channel);
					next = ei == null ? currentRule : ei.next;
				} else {
					// declared AT(uri:*)
					AttributeNS atNS = (AttributeNS) ei.event;
					// localname only
					EvolvingUriContext uc = encoderContext.getUriContext(atNS
							.getNamespaceUriID());
					qnc = this.encoderContext.encodeLocalName(localName, uc,
							channel);
					next = ei.next;
				}

			} else {
				// no schema-informed grammar --> default datatype in any case
				// NO global attribute --> default datatype
				isTypeValid(BuiltIn.DEFAULT_DATATYPE, value);

				if (ei == null) {
					// Undeclared AT(*), 2nd level
					if(this.limitGrammarLearning()) {
						currentRule = this.getCurrentRule();
						
						ei = currentRule.lookForEvent(EventType.ATTRIBUTE_GENERIC);
						assert(ei != null);
						// event-code
						encode1stLevelEventCode(ei.getEventCode());
						// string value
						isTypeValid(BuiltIn.DEFAULT_DATATYPE, value);

						// qualified name
						qnc = this.encoderContext.encodeQName(uri, localName,
								channel);
						
						next = ei.next;
					} else {
						qnc = encodeUndeclaredAT(currentRule, uri, localName);
						next = currentRule;	
					}
				} else {
					// Declared AT(uri:*) or AT(*) on 1st level
					qnc = encodeDeclaredAT(ei, uri, localName);
					next = ei.next;
				}
			}
		}

		// prefix
		assert (qnc != null);
		if (preservePrefix) {
			this.encoderContext.encodeQNamePrefix(qnc, prefix, channel);
		}

		// so far: event-code has been written & datatype is settled
		// the actual value is still missing
		this.writeValue(qnc);

		// update current rule
		assert (next != null);
		updateCurrentRule(next);
	}

	private void encodeAttributeEventCodeUndeclared(Rule currentRule,
			String localName) throws IOException, EXIException {
		int ecATundeclared = currentRule.get2ndLevelEventCode(
				EventType.ATTRIBUTE_GENERIC_UNDECLARED, fidelityOptions);

		if (ecATundeclared == Constants.NOT_FOUND) {
			assert (fidelityOptions.isStrict());
			throw new EXIException("Attribute '" + localName
					+ "' cannot be encoded!");
		}
		assert (ecATundeclared != Constants.NOT_FOUND);
		// encode event-code
		encode2ndLevelEventCode(ecATundeclared);
	}

	private QNameContext encodeDeclaredAT(EventInformation ei, String uri,
			String localName) throws IOException {
		// eventCode
		encode1stLevelEventCode(ei.getEventCode());

		QNameContext qnc;
		if (ei.event.isEventType(EventType.ATTRIBUTE_NS)) {
			// declared AT(uri:*)
			AttributeNS atNS = (AttributeNS) ei.event;
			// localname only
			EvolvingUriContext uc = encoderContext.getUriContext(atNS
					.getNamespaceUriID());
			qnc = this.encoderContext.encodeLocalName(localName, uc, channel);

		} else {
			// declared AT(*)
			qnc = this.encoderContext.encodeQName(uri, localName, channel);
		}
		return qnc;
	}

	private QNameContext encodeUndeclaredAT(Rule currentRule, String uri,
			String localName) throws EXIException, IOException {

		// event-code
		encodeAttributeEventCodeUndeclared(currentRule, localName);

		// qualified name
		QNameContext qnc = this.encoderContext.encodeQName(uri, localName,
				channel);

		// learn attribute event
		currentRule.learnAttribute(new Attribute(qnc));

		return qnc;
	}

	protected Attribute getGlobalAttribute(String uri, String localName) {
		UriContext uc = this.encoderContext.getUriContext(uri);
		if (uc != null) {
			return getGlobalAttribute(uc, localName);
		}

		return null;
	}

	protected Attribute getGlobalAttribute(UriContext uc, String localName) {
		assert (uc != null);
		QNameContext qnc = uc.getQNameContext(localName);
		if (qnc != null) {
			return qnc.getGlobalAttribute();
		}

		return null;
	}

	public void encodeCharacters(Value chars) throws EXIException, IOException {
		// Don't we want to prune insignificant whitespace characters
		if (!preserveLexicalValues) {
			String tchars = chars.toString().trim();
			if (tchars.length() == 0) {
				return;
			}
		}

		encodeCharactersForce(chars);
	}

	public void encodeCharactersForce(Value chars) throws EXIException,
			IOException {

		Rule currentRule = getCurrentRule();
		EventInformation ei = currentRule.lookForEvent(EventType.CHARACTERS);

		// valid value and valid event-code ?
		if (ei != null
				&& isTypeValid(((DatatypeEvent) ei.event).getDatatype(), chars)) {
			// right characters event found & data type-valid
			// --> encode EventCode, schema-valid content plus grammar moves
			// on
			encode1stLevelEventCode(ei.getEventCode());
			// writeValue(getElementContext().eqname.getQName());
			writeValue(getElementContext().qnameContext);
			// update current rule
			updateCurrentRule(ei.next);
		} else {
			// generic CH (on first level)
			ei = currentRule.lookForEvent(EventType.CHARACTERS_GENERIC);

			if (ei != null) {
				// encode EventCode
				encode1stLevelEventCode(ei.getEventCode());
				// encode schema-invalid content as string
				isTypeValid(BuiltIn.DEFAULT_DATATYPE, chars);
				writeValue(getElementContext().qnameContext);
				// update current rule
				updateCurrentRule(ei.next);
			} else {
				// Undeclared CH can be found on 2nd level
				int ecCHundeclared = currentRule.get2ndLevelEventCode(
						EventType.CHARACTERS_GENERIC_UNDECLARED,
						fidelityOptions);

				if (ecCHundeclared == Constants.NOT_FOUND) {
					if (exiFactory.isFragment()) {
						// characters in "outer" fragment element
						throwWarning("Skip CH: '" + chars + "'");
					} else if (fidelityOptions.isStrict()
							&& chars.toString().trim().length() == 0) {
						// empty characters in STRICT
						throwWarning("Skip CH: '" + chars + "'");
					} else {
						throw new EXIException("Characters '" + chars
								+ "' cannot be encoded!");
					}
				} else {
					Rule updContextRule;
					
					// limit grammar learning ?
					if(limitGrammarLearning()) {
						// encode 1st level EventCode
						currentRule = getCurrentRule();
						ei = currentRule.lookForEvent(EventType.CHARACTERS_GENERIC);
						assert(ei != null);
						encode1stLevelEventCode(ei.getEventCode());
						// next rule
						updContextRule = ei.next;
					} else {
						// encode [undeclared] event-code
						encode2ndLevelEventCode(ecCHundeclared);
						// learn characters event ?
						currentRule.learnCharacters();
						// next rule
						updContextRule = currentRule.getElementContentRule();
					}
					
					// content as string
					isTypeValid(BuiltIn.DEFAULT_DATATYPE, chars);
					writeValue(getElementContext().qnameContext);
					// update current rule
					updateCurrentRule(updContextRule);
				}
			}
		}
	}
	
	public void encodeDocType(String name, String publicID, String systemID,
			String text) throws EXIException, IOException {
		if (fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_DTD)) {
			// DOCTYPE can be found on 2nd level
			int ec2 = getCurrentRule().get2ndLevelEventCode(EventType.DOC_TYPE,
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
			final Rule currentRule = getCurrentRule();
			int ec2 = currentRule.get2ndLevelEventCode(
					EventType.ENTITY_REFERENCE, fidelityOptions);
			encode2ndLevelEventCode(ec2);

			// name AS string
			writeString(name);

			// update current rule
			updateCurrentRule(currentRule.getElementContentRule());
		}
	}

	public void encodeComment(char[] ch, int start, int length)
			throws EXIException, IOException {
		if (fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_COMMENT)) {
			// comments can be found on 3rd level
			final Rule currentRule = getCurrentRule();
			int ec3 = currentRule.get3rdLevelEventCode(EventType.COMMENT,
					fidelityOptions);
			encode3rdLevelEventCode(ec3);

			// encode CM content
			writeString(new String(ch, start, length));

			// update current rule
			updateCurrentRule(currentRule.getElementContentRule());
		}
	}

	public void encodeProcessingInstruction(String target, String data)
			throws EXIException, IOException {
		if (fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_PI)) {
			// processing instructions can be found on 3rd level
			final Rule currentRule = getCurrentRule();
			int ec3 = currentRule.get3rdLevelEventCode(
					EventType.PROCESSING_INSTRUCTION, fidelityOptions);
			encode3rdLevelEventCode(ec3);

			// encode PI content
			writeString(target);
			writeString(data);

			// update current rule
			updateCurrentRule(currentRule.getElementContentRule());
		}
	}

}
