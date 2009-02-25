/*
 * Copyright (C) 2007, 2008 Siemens AG
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
import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.EXIEncoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.core.sax.NamespacePrefixLevels;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.datatype.encoder.TypeEncoder;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.exceptions.XMLParsingException;
import com.siemens.ct.exi.grammar.SchemaInformedGrammar;
import com.siemens.ct.exi.grammar.TypeGrammar;
import com.siemens.ct.exi.grammar.event.DatatypeEvent;
import com.siemens.ct.exi.grammar.event.EventType;
import com.siemens.ct.exi.grammar.rule.Rule;
import com.siemens.ct.exi.grammar.rule.SchemaInformedRule;
import com.siemens.ct.exi.io.block.EncoderBlock;
import com.siemens.ct.exi.util.MethodsBag;
import com.siemens.ct.exi.util.datatype.XSDBoolean;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.2.20081023
 */

public class DefaultEXIEncoder extends AbstractEXICoder implements
		EXIEncoder {
	protected EncoderBlock block;

	protected OutputStream os;

	// to parse raw nil value
	protected XSDBoolean nil;

	// namespace prefixes are related to elements
	protected NamespacePrefixLevels nsPrefixes;

	protected String lastSEprefix = null;

	public DefaultEXIEncoder(EXIFactory exiFactory) {
		super(exiFactory);

		nsPrefixes = new NamespacePrefixLevels();
		nil = XSDBoolean.newInstance();
	}

	@Override
	protected void initForEachRun() throws EXIException {
		super.initForEachRun();

		// re-set prefixes
		nsPrefixes.clear();

		// setup encoder-block
		this.block = exiFactory.createEncoderBlock(os);

		// possible root elements
		if (exiFactory.isFragment()) {
			// push stack with fragment grammar
			pushRule(grammar.getBuiltInFragmentGrammar());
		} else {
			// push stack with document grammar
			pushRule(grammar.getBuiltInDocumentGrammar());
		}
	}

	public void setOutput(OutputStream os, boolean exiBodyOnly)
			throws EXIException {
		this.os = os;

		if (!exiBodyOnly) {
			// EXI header
			EXIHeader.write(os);
		}
	}

	protected void encode1stLevelEventCode(int pos) throws EXIException {
		try {
			block.writeEventCode(pos, currentRule
					.get1stLevelEventCodeLength(fidelityOptions));
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	protected void encode2ndLevelEventCode(int pos) throws EXIException {
		try {
			// 1st level
			block.writeEventCode(currentRule.getNumberOfEvents(), currentRule
					.get1stLevelEventCodeLength(fidelityOptions));

			// 2nd level
			int ch2 = currentRule.get2ndLevelCharacteristics(fidelityOptions);
			assert (pos < ch2);

			block.writeEventCode(pos, MethodsBag.getCodingLength(ch2));
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	protected void encode3rdLevelEventCode(int pos) throws EXIException {
		try {
			// 1st level
			block.writeEventCode(currentRule.getNumberOfEvents(), currentRule
					.get1stLevelEventCodeLength(fidelityOptions));

			// 2nd level
			int ch2 = currentRule.get2ndLevelCharacteristics(fidelityOptions);
			block.writeEventCode(ch2 - 1, MethodsBag.getCodingLength(ch2));

			// 3rd level
			int ch3 = currentRule.get3rdLevelCharacteristics(fidelityOptions);
			assert (pos < ch3);
			block.writeEventCode(pos, MethodsBag.getCodingLength(ch3));
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	protected void encodeQName(String uri, String localName)
			throws EXIException {
		try {
			block.writeUri(uri);
			block.writeLocalName(localName, uri);
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	// Note: value needs to be type-checked beforehands
	protected void encodeTypeValidValue(int ec, String uri, String localName)
			throws EXIException {
		try {
			// encode EventCode
			encode1stLevelEventCode(ec);

			// value content
			block.writeTypeValidValue(uri, localName);
		} catch (Exception e) {
			throw new EXIException(e);
		}
	}

	protected void encodeTypeInvalidValueAttribute(int atEventCode, String uri,
			String localName, String value) throws EXIException {
		// encode 2nd level event-code
		encodeTypeInvalidAttributeSecondLevel();

		// calculate 3rd level event-code
		SchemaInformedRule schemaCurrentRule = (SchemaInformedRule) currentRule;
		int ec3 = atEventCode - schemaCurrentRule.getLeastAttributeEventCode();

		try {
			// encode 3rd level event-code
			block.writeEventCode(ec3, MethodsBag
					.getCodingLength(schemaCurrentRule
							.getNumberOfSchemaDeviatedAttributes()));

			// encode content as string
			block.writeValueAsString(uri, localName, value);
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	protected void encodeTypeInvalidAttributeSecondLevel() throws EXIException {
		int ec2ATdeviated = currentRule.get2ndLevelEventCode(
				EventType.ATTRIBUTE_INVALID_VALUE, fidelityOptions);

		// encode 2nd level event-code
		encode2ndLevelEventCode(ec2ATdeviated);
	}

	protected void encodeTypeInvalidXsiAttribute(String value)
			throws EXIException {
		// encode 2nd level event-code
		encodeTypeInvalidAttributeSecondLevel();

		// calculate 3rd level event-code
		SchemaInformedRule schemaCurrentRule = (SchemaInformedRule) currentRule;
		int ec3nil = schemaCurrentRule.getNumberOfSchemaDeviatedAttributes() - 1;

		try {
			// encode 3rd level event-code
			block.writeEventCode(ec3nil, MethodsBag
					.getCodingLength(schemaCurrentRule
							.getNumberOfSchemaDeviatedAttributes()));

			// encode content as string
			block.writeString(value);
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	protected void encodeGenericValue(int ec, String uri, String localName,
			String value) throws EXIException {
		// step forward in current rule (replace rule at the top)
		replaceRuleAtTheTop(currentRule.get1stLevelRule(ec));

		try {
			// content as string
			block.writeValueAsString(uri, localName, value);
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	protected void encodeUnexpectedAttribute(String uri, String localName,
			String value) throws EXIException {
		// encode expanded name
		encodeExpandedName(uri, localName);

		// encode content as string
		try {
			block.writeValueAsString(uri, localName, value);
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	protected void encodeUnexpectedAttributeAndLearn(String uri,
			String localName, String value) throws EXIException {
		// encode unexpected attribute
		encodeUnexpectedAttribute(uri, localName, value);

		// learn attribute event ?
		currentRule.learnAttribute(uri, localName);
	}

	protected void encodeExpandedName(String uri, String localName)
			throws EXIException {
		try {
			// encode expanded name (uri & localName)
			block.writeUri(uri);
			block.writeLocalName(localName, uri);
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	public void encodeStartDocument() throws EXIException {
		if (this.os == null) {
			throw new EXIException(
					"No valid EXI OutputStream set for encoding. Please use setOutput( ... )");
		}

		this.initForEachRun();

		// replaceRuleAtTheTop ( getCurrentRule ( ).stepForward ( isStartTagRule
		// ( ), EventType.START_DOCUMENT ) );
		replaceRuleAtTheTop(currentRule.get1stLevelRule(0));

	}

	public void encodeEndDocument() throws EXIException {
		int ec = currentRule.get1stLevelEventCode(eventED);

		if (ec == Constants.NOT_FOUND) {
			throw new EXIException("No EXI Event found for endDocument");
		} else {
			// encode EventCode
			this.encode1stLevelEventCode(ec);
		}

		try {
			// flush chunk(s) to bit/byte output stream
			block.flush();
			// block.close ( );
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	public void encodeStartElement(String uri, String localName)
			throws EXIException {
		// update lookup event
		eventSE.setNamespaceURI(uri);
		eventSE.setLocalPart(localName);

		int ec = currentRule.get1stLevelEventCode(eventSE);

		if (ec == Constants.NOT_FOUND) {
			// generic SE (on first level)
			int ecGeneric = currentRule.get1stLevelEventCode(eventSEg);

			if (ecGeneric == Constants.NOT_FOUND) {
				// Undeclared SE(*) can be found on 2nd level
				int ecSEundeclared = currentRule.get2ndLevelEventCode(
						EventType.START_ELEMENT_GENERIC_UNDECLARED,
						fidelityOptions);

				if (ecSEundeclared == Constants.NOT_FOUND) {
					// TODO skip element ?
					throw new IllegalArgumentException("SE " + uri + ":"
							+ localName);
				} else {
					// encode [undeclared] event-code
					encode2ndLevelEventCode(ecSEundeclared);

					// encode expanded name
					encodeExpandedName(uri, localName);

					// learn startElement event ?
					currentRule.learnStartElement(uri, localName);

					// step forward in current rule (replace rule at the top)
					replaceRuleAtTheTop(currentRule
							.getElementContentRuleForUndeclaredSE());

					// push next rule
					pushRule(uri, localName);
				}
			} else {
				// SE(*) on first level
				// encode EventCode
				encode1stLevelEventCode(ecGeneric);

				// encode expanded name
				encodeExpandedName(uri, localName);

				Rule tmpStorage = currentRule;

				// step forward in current rule (replace rule at the top)
				replaceRuleAtTheTop(currentRule.get1stLevelRule(ecGeneric));

				// push next rule
				pushRule(uri, localName);

				// learning in schema-less case
				tmpStorage.learnStartElement(uri, localName);
			}
		} else {
			// encode EventCode
			encode1stLevelEventCode(ec);

			// step forward in current rule (replace rule at the top)
			replaceRuleAtTheTop(currentRule.get1stLevelRule(ec));

			// push next rule
			pushRule(uri, localName);
		}

		// update scope
		pushScope(uri, localName);
	}

	public void encodeStartElement(String uri, String localName, String prefix)
			throws EXIException {
		nsPrefixes.addLevel();

		encodeStartElement(uri, localName);

		// prefix
		lastSEprefix = prefix;

		if (this.nsPrefixes.hasPrefixForURI(uri)) {
			// TODO *overlapping* prefixes: same namespace BUT different
			// prefixes
			// System.out.println ( "SE_PFX uri found for " + uri + " --> " +
			// prefix );
		} else {
			/*
			 * If there are no prefixes specified for the URI of the QName by
			 * preceding NS events in the EXI stream, the prefix is undefined.
			 * An undefined prefix is represented using zero bits (i.e.,
			 * omitted).
			 */
		}
	}

	public void encodeEndElement() throws EXIException {
		int ec = currentRule.get1stLevelEventCode(eventEE);

		// Special case: SAX does not inform about empty ("") CH events
		// --> if EE is not found check whether an empty CH event *helps*
		if (ec == Constants.NOT_FOUND) {
			int ecCH = currentRule.get1stLevelEventCode(eventCH);

			if (ecCH != Constants.NOT_FOUND
					&& block.isTypeValid(getDatatypeOfEvent(ecCH),
							Constants.EMPTY_STRING)) {

				// Yep, CH is successful

				// encode schema-valid content plus moves on in grammar
				encodeTypeValidValue(ecCH, getScopeURI(), getScopeLocalName());

				// step forward in current rule (replace rule at the top)
				replaceRuleAtTheTop(currentRule.get1stLevelRule(ecCH));

				// try the EE event once again
				ec = currentRule.get1stLevelEventCode(eventEE);
			}
		}

		if (ec == Constants.NOT_FOUND) {
			// Undeclared EE can be found on 2nd level
			int ecEEundeclared = currentRule.get2ndLevelEventCode(
					EventType.END_ELEMENT_UNDECLARED, fidelityOptions);

			if (ecEEundeclared == Constants.NOT_FOUND) {
				// TODO skip element ?
				throw new IllegalArgumentException("EE " + getScopeURI() + ":"
						+ getScopeLocalName());
			} else {
				// encode [undeclared] event-code
				encode2ndLevelEventCode(ecEEundeclared);

				// learn end-element event ?
				currentRule.learnEndElement();
			}
		} else {
			// encode EventCode
			encode1stLevelEventCode(ec);
		}

		// pop the rule from the top of the stack
		popRule();
		popScope();

		// TODO how to detect whether we deal with preserve prefix on or not in
		// a smart way
		if (lastSEprefix != null) {
			nsPrefixes.removeLevel();
		}
	}

	public void encodeNamespaceDeclaration(String uri, String prefix)
			throws EXIException {
		if (fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_PREFIX)) {
			try {
				// event code
				int ec2 = currentRule.get2ndLevelEventCode(
						EventType.NAMESPACE_DECLARATION, fidelityOptions);
				encode2ndLevelEventCode(ec2);

				// prefix mapping
				block.writeUri(uri);
				block.writePrefix(prefix, uri);

				// local-element-ns
				if (prefix.equals(lastSEprefix)) {
					// System.out.println ( "Prefix '" + prefix + "' is part of
					// an SE event followed by an associated NS event");
					block.writeBoolean(true);
				} else {
					block.writeBoolean(false);
				}
				nsPrefixes.addPrefix(uri, prefix);
			} catch (IOException e) {
				throw new EXIException(e);
			}
		}
	}

	public void encodeXsiType(String uri, String localName, String raw)
			throws EXIException {
		if (currentRule.isSchemaRule()) {
			int ec2 = currentRule.get2ndLevelEventCode(
					EventType.ATTRIBUTE_XSI_TYPE, fidelityOptions);

			if (ec2 == Constants.NOT_FOUND) {
				// schema deviation in strict mode ONLY
				assert (fidelityOptions.isStrict());

				String msg = "Skip unexpected type-cast, xsi:type ({" + uri
						+ "}" + localName;
				errorHandler.warning(new EXIException(msg));
			} else {
				// lookup type-grammar
				TypeGrammar tg = ((SchemaInformedGrammar) grammar)
						.getTypeGrammar(uri, localName);

				/*
				 * The value of each AT (xsi:type) event is represented as a
				 * QName (see 7.1.7 QName). If there is no namespace in scope
				 * for the specified qname prefix, the QName uri is set to empty
				 * ("") and the QName localName is set to the full lexical value
				 * of the QName, including the prefix.
				 */

				if (tg == null) {
					// TODO type unknown --> what to do ?

					// encode event-code
					encode2ndLevelEventCode(ec2);
					// type as qname
					encodeQName(Constants.EMPTY_STRING, raw);
				} else {
					// encode event-code
					encode2ndLevelEventCode(ec2);
					// type as qname
					encodeQName(uri, localName);

					// update grammar according to given xsi:type
					this.replaceRuleAtTheTop(tg.getType());

					this.pushScopeType(uri, localName);
				}
			}
		} else {
			// schema-less mode

			// AT(*) can be found on 2nd level
			int ecATundeclared = currentRule.get2ndLevelEventCode(
					EventType.ATTRIBUTE_GENERIC_UNDECLARED, fidelityOptions);

			if (ecATundeclared == Constants.NOT_FOUND) {
				// Warn encoder that the attribute is simply skipped
				String msg = "Skip AT xsi:type: " + raw;
				errorHandler.warning(new EXIException(msg));
			} else {
				// encode event-code
				encode2ndLevelEventCode(ecATundeclared);

				// TODO The value of each AT (xsi:type) event matching the AT(*)
				// terminal is represented as a QName (see 7.1.7 QName). If
				// there is
				// no namespace in scope for the specified qname prefix, the
				// QName
				// uri is set to empty ("") and the QName localName is set to
				// the
				// full lexical value of the QName, including the prefix.
				encodeUnexpectedAttribute(
						XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,
						Constants.XSI_TYPE, raw);
			}
		}
	}

	public void encodeXsiNil(String rawNil) throws EXIException {
		if (currentRule.isSchemaRule()) {
			// nillable ?
			int ec2 = currentRule.get2ndLevelEventCode(
					EventType.ATTRIBUTE_XSI_NIL, fidelityOptions);

			if (ec2 == Constants.NOT_FOUND) {
				// Warn encoder that the attribute is simply skipped
				String msg = "Skip AT xsi:nil=" + rawNil;
				errorHandler.warning(new EXIException(msg));
			} else {
				// schema-valid boolean ?
				try {
					nil.parse(rawNil);

					// encode event-code + nil value
					encode2ndLevelEventCode(ec2);
					try {
						block.writeBoolean(nil.getBoolean());
					} catch (IOException e) {
						throw new EXIException(e);
					}

					if (nil.getBoolean()) {
						replaceRuleAtTheTop(((SchemaInformedRule) currentRule)
								.getTypeEmpty());
					}

				} catch (XMLParsingException e) {
					// TODO If the value is not a schema-valid Boolean, the AT
					// (xsi:nil) event is represented by the AT(*)
					// [schema-invalid value] terminal
					encodeTypeInvalidXsiAttribute(rawNil);
				}
			}
		} else {
			// schema-less mode

			// AT(*) can be found on 2nd level
			int ecATundeclared = currentRule.get2ndLevelEventCode(
					EventType.ATTRIBUTE_GENERIC_UNDECLARED, fidelityOptions);

			if (ecATundeclared == Constants.NOT_FOUND) {
				// Warn encoder that the attribute is simply skipped
				String msg = "Skip AT xsi:nil";
				errorHandler.warning(new EXIException(msg));
			} else {
				// encode event-code
				encode2ndLevelEventCode(ecATundeclared);

				encodeUnexpectedAttribute(
						XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,
						Constants.XSI_NIL, rawNil);
			}
		}
	}

	public void encodeAttribute(final String uri, final String localName,
			String value) throws EXIException {
		eventAT.setNamespaceURI(uri);
		eventAT.setLocalPart(localName);

		int ec = currentRule.get1stLevelEventCode(eventAT);

		if (ec == Constants.NOT_FOUND) {
			// generic AT (on first level)
			int ecGeneric = currentRule.get1stLevelEventCode(eventATg);

			if (ecGeneric == Constants.NOT_FOUND) {
				// Undeclared AT(*) can be found on 2nd level
				int ecATundeclared = currentRule
						.get2ndLevelEventCode(
								EventType.ATTRIBUTE_GENERIC_UNDECLARED,
								fidelityOptions);

				if (ecATundeclared == Constants.NOT_FOUND) {
					// Warn encoder that the attribute is simply skipped
					// Note: should never happen except in strict mode
					assert (fidelityOptions.isStrict());
					String msg = "Skip AT " + uri + ":" + localName + " = "
							+ value + " (StrictMode="
							+ fidelityOptions.isStrict() + ")";
					errorHandler.warning(new EXIException(msg));
				} else {
					// encode event-code
					encode2ndLevelEventCode(ecATundeclared);

					encodeUnexpectedAttributeAndLearn(uri, localName, value);
				}
			} else {
				// encode EventCode
				encode1stLevelEventCode(ecGeneric);

				encodeUnexpectedAttributeAndLearn(uri, localName, value);

				// step forward in current rule (replace rule at the top)
				replaceRuleAtTheTop(currentRule.get1stLevelRule(ecGeneric));

			}
		} else {
			// attribute event found
			if (block.isTypeValid(getDatatypeOfEvent(ec), value)) {
				// encode schema-valid content plus moves on in grammar
				encodeTypeValidValue(ec, uri, localName);
			} else {
				// encode schema-invalid value AT
				encodeTypeInvalidValueAttribute(ec, uri, localName, value);
			}

			// step forward in current rule (replace rule at the top)
			replaceRuleAtTheTop(currentRule.get1stLevelRule(ec));
		}
	}

	public void encodeAttribute(String uri, String localName, String prefix,
			String value) throws EXIException {
		throw new RuntimeException("Encoding of AT prefix not yet implemented");
	}

	public void encodeCharacters(String chars) throws EXIException {
		try {
			int ec = currentRule.get1stLevelEventCode(eventCH);

			// valid value and valid event-code ?
			if (ec == Constants.NOT_FOUND
					|| !block.isTypeValid(getDatatypeOfEvent(ec), chars)) {
				// generic CH (on first level)
				int ecGeneric = currentRule.get1stLevelEventCode(eventCHg);

				if (ecGeneric == Constants.NOT_FOUND) {
					// Undeclared CH can be found on 2nd level
					int ecCHundeclared = currentRule.get2ndLevelEventCode(
							EventType.CHARACTERS_GENERIC_UNDECLARED,
							fidelityOptions);

					if (ecCHundeclared == Constants.NOT_FOUND) {
						// skip characters & throw warning
						String msg = "Skip CH: '" + chars + "'";
						errorHandler.warning(new EXIException(msg));
					} else {
						// encode [undeclared] event-code
						encode2ndLevelEventCode(ecCHundeclared);

						// learn characters event ?
						currentRule.learnCharacters();

						// content as string
						block.writeValueAsString(getScopeURI(),
								getScopeLocalName(), chars);

						// step forward in current rule (replace rule at the
						// top)
						replaceRuleAtTheTop(currentRule.getElementContentRule());
					}
				} else {
					// encode EventCode
					encode1stLevelEventCode(ecGeneric);

					// encode schema-invalid content as string plus moves on in
					// grammar
					encodeGenericValue(ecGeneric, getScopeURI(),
							getScopeLocalName(), chars);
				}
			} else {
				// right characters event found & data type-valid
				// --> encode EventCode, schema-valid content plus grammar moves
				// on
				encodeTypeValidValue(ec, getScopeURI(), getScopeLocalName());

				// step forward in current rule (replace rule at the top)
				replaceRuleAtTheTop(currentRule.get1stLevelRule(ec));
			}
		} catch (IOException e) {
			throw new EXIException(e);
		}

	}

	protected Datatype getDatatypeOfEvent(int eventCode) {
		assert (currentRule.get1stLevelEvent(eventCode) instanceof DatatypeEvent);

		return ((DatatypeEvent) currentRule.get1stLevelEvent(eventCode))
				.getDatatype();
	}

	public void encodeComment(char[] ch, int start, int length)
			throws EXIException {
		if (fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_COMMENT)) {
			try {
				// comments can be found on 3rd level
				int ec3 = currentRule.get3rdLevelEventCode(EventType.COMMENT,
						fidelityOptions);
				encode3rdLevelEventCode(ec3);

				// encode CM content
				block.writeString(new String(ch, start, length));

				// step forward (if not alreay content rule)
				replaceRuleAtTheTop(currentRule.getElementContentRule());
			} catch (IOException e) {
				throw new EXIException(e);
			}
		}
	}

	public void encodeProcessingInstruction(String target, String data)
			throws EXIException {
		if (fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_PI)) {
			try {
				// processing instructions can be found on 3rd level
				int ec3 = currentRule.get3rdLevelEventCode(
						EventType.PROCESSING_INSTRUCTION, fidelityOptions);
				encode3rdLevelEventCode(ec3);

				// encode PI content
				block.writeString(target);
				block.writeString(data);

				// step forward
				replaceRuleAtTheTop(currentRule.getElementContentRule());
			} catch (IOException e) {
				throw new EXIException(e);
			}
		}
	}

	/*
	 * SELF_CONTAINED
	 */

	TypeEncoder scTypeEncoder;
	Map<String, Map<String, Rule>> scRuntimeDispatcher;

	public int encodeStartFragmentSelfContained(String uri, String localName)
			throws EXIException {

		int skipBytesSC = -1;

		// SC Fragment
		int ec2 = currentRule.get2ndLevelEventCode(EventType.SELF_CONTAINED,
				fidelityOptions);

		if (ec2 == Constants.NOT_FOUND) {
			// throw error
			throw new EXIException(
					"SelfContained fragments need to be supported by EXI's Options. Please revise your configuration.");
		} else {
			this.encode2ndLevelEventCode(ec2);

			// 1. Save the string table, grammars, namespace prefixes and any
			// implementation-specific state learned while processing this EXI
			// Body.
			// 2. Initialize the string table, grammars, namespace prefixes and
			// any implementation-specific state learned while processing this
			// EXI Body to the state they held just prior to processing this EXI
			// Body.
			// 3. Skip to the next byte-aligned boundary in the stream.
			try {
				block.skipToNextByteBoundary();

				if (block.bytePositionSupported()) {
					skipBytesSC = block.getNumberOfBytes();
				}

			} catch (IOException e) {
				throw new EXIException(e);
			}
			// string tables
			scTypeEncoder = this.block.getTypeEncoder();
			TypeEncoder te = this.exiFactory.createTypeEncoder();
			this.exiFactory.getGrammar().populateStringTable(
					te.getStringTable());
			// runtime-rules
			scRuntimeDispatcher = this.runtimeDispatcher;
			this.runtimeDispatcher = new HashMap<String, Map<String, Rule>>();
			// TODO namespace prefixes

			// 4. Let qname be the qname of the SE event immediately preceding
			// this SC event.
			// 5. Let content be the sequence of events following this SC event
			// that match the grammar for element qname, up to and including the
			// terminating EE event.
			// 6. Evaluate the sequence of events (SD, SE(qname), content, ED)
			// according to the Fragment grammar.
			this.replaceRuleAtTheTop(grammar.getBuiltInFragmentGrammar());
			replaceRuleAtTheTop(currentRule.get1stLevelRule(0));
			this.encodeStartElement(uri, localName);
		}

		return skipBytesSC;
	}

	public void encodeEndFragmentSelfContained() throws EXIException {
		// close SC fragment

		int ec = currentRule.get1stLevelEventCode(eventED);

		if (ec == Constants.NOT_FOUND) {
			throw new EXIException("No EXI Event found for endDocument");
		} else {
			// encode EventCode
			this.encode1stLevelEventCode(ec);
		}
		this.popRule();

		// 7. Restore the string table, grammars, namespace prefixes and
		// implementation-specific state learned while processing this EXI
		// Body to that saved in step 1 above.
		this.block.setTypeEncoder(this.scTypeEncoder);
		this.runtimeDispatcher = this.scRuntimeDispatcher;
	}
}
