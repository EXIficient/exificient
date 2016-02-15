/*
 * Copyright (c) 2007-2015 Siemens AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */

package com.siemens.ct.exi.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.EXIBodyEncoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.EncodingOptions;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.attributes.AttributeList;
import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.core.container.NamespaceDeclaration;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.datatype.WhiteSpace;
import com.siemens.ct.exi.datatype.strings.StringCoder;
import com.siemens.ct.exi.datatype.strings.StringEncoder;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammars.event.Attribute;
import com.siemens.ct.exi.grammars.event.AttributeNS;
import com.siemens.ct.exi.grammars.event.Characters;
import com.siemens.ct.exi.grammars.event.DatatypeEvent;
import com.siemens.ct.exi.grammars.event.EventType;
import com.siemens.ct.exi.grammars.event.StartElement;
import com.siemens.ct.exi.grammars.event.StartElementNS;
import com.siemens.ct.exi.grammars.grammar.Grammar;
import com.siemens.ct.exi.grammars.grammar.GrammarType;
import com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTagGrammar;
import com.siemens.ct.exi.grammars.grammar.SchemaInformedGrammar;
import com.siemens.ct.exi.grammars.production.Production;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.types.BuiltIn;
import com.siemens.ct.exi.types.TypeEncoder;
import com.siemens.ct.exi.util.MethodsBag;
import com.siemens.ct.exi.util.xml.QNameUtilities;
import com.siemens.ct.exi.values.QNameValue;
import com.siemens.ct.exi.values.StringValue;
import com.siemens.ct.exi.values.Value;
import com.siemens.ct.exi.values.ValueType;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.6-SNAPSHOT
 */

public abstract class AbstractEXIBodyEncoder extends AbstractEXIBodyCoder
		implements EXIBodyEncoder {

	protected final EXIHeaderEncoder exiHeader;

	/** prefix of previous start element (relevant for preserving prefixes) */
	protected String sePrefix = null;

	/** Output Channel */
	protected EncoderChannel channel;

	/** Type Encoder */
	protected final TypeEncoder typeEncoder;

	/** String Encoder */
	protected final StringEncoder stringEncoder;

	/** Encoding options */
	protected final EncodingOptions encodingOptions;

	/** buffers character values before flushing them out */
	protected List<Value> bChars;

	/** The xml:space attribute is defined (default false) */
	protected boolean isXmlSpacePreserve;

	/** contains last event type */
	protected EventType lastEvent;

	public AbstractEXIBodyEncoder(EXIFactory exiFactory) throws EXIException {
		super(exiFactory);
		this.exiHeader = new EXIHeaderEncoder();

		// encoder stuff
		typeEncoder = exiFactory.createTypeEncoder();
		stringEncoder = exiFactory.createStringEncoder();
		encodingOptions = exiFactory.getEncodingOptions();
		bChars = new ArrayList<Value>();
	}

	@Override
	protected void initForEachRun() throws EXIException, IOException {
		super.initForEachRun();

		learnedProductions = 0;
		stringEncoder.clear();
		bChars.clear();
		isXmlSpacePreserve = false;
	}

	protected QNameContext encodeQName(String namespaceUri, String localName,
			EncoderChannel channel) throws IOException {
		// uri
		RuntimeUriContext ruc = encodeUri(namespaceUri, channel);

		// local-name
		return encodeLocalName(localName, ruc, channel);
	}

	protected RuntimeUriContext encodeUri(final String namespaceUri,
			EncoderChannel channel) throws IOException {
		int numberBitsUri = MethodsBag.getCodingLength(getNumberOfUris() + 1); // numberEntries+1
		RuntimeUriContext ruc = this.getUri(namespaceUri);

		if (ruc == null) {
			// uri string value was not found
			// ==> zero (0) as an n-nit unsigned integer
			// followed by uri encoded as string
			channel.encodeNBitUnsignedInteger(0, numberBitsUri);
			channel.encodeString(namespaceUri);
			// after encoding string value is added to table
			ruc = this.addUri(namespaceUri);
		} else {
			// string value found
			// ==> value(i+1) is encoded as n-bit unsigned integer
			channel.encodeNBitUnsignedInteger(ruc.namespaceUriID + 1,
					numberBitsUri);
		}

		return ruc;
	}

	protected void encodeQNamePrefix(QNameContext qnContext, String prefix,
			EncoderChannel channel) throws IOException {
		int namespaceUriID = qnContext.getNamespaceUriID();

		if (namespaceUriID == 0) {
			// XMLConstants.NULL_NS_URI
			// default namespace --> DEFAULT_NS_PREFIX
		} else {
			RuntimeUriContext ruc = this.getUri(namespaceUriID);
			int numberOfPrefixes = ruc.getNumberOfPrefixes();
			if (numberOfPrefixes > 1) {
				int pfxID = ruc.getPrefixID(prefix);
				if (pfxID == Constants.NOT_FOUND) {
					// choose *one* prefix which gets modified by
					// local-element-ns anyway ?
					pfxID = 0;
				}

				// overlapping URIs
				channel.encodeNBitUnsignedInteger(pfxID,
						MethodsBag.getCodingLength(numberOfPrefixes));
			} else {
				/*
				 * #1# Possibility If there are no prefixes specified for the
				 * URI of the QName by preceding NS events in the EXI stream,
				 * the prefix is undefined. An undefined prefix is represented
				 * using zero bits (i.e., omitted).
				 * 
				 * #2# Possibility If there is only one prefix, the prefix is
				 * implicit
				 */
			}
		}
	}

	protected QNameContext encodeLocalName(String localName,
			RuntimeUriContext ruc, EncoderChannel channel) throws IOException {

		// look for localNameID
		QNameContext qnc = ruc.getQNameContext(localName);

		if (qnc == null) {
			// string value was not found in local partition
			// ==> string literal is encoded as a String
			// with the length of the string incremented by one
			channel.encodeUnsignedInteger(localName.length() + 1);
			channel.encodeStringOnly(localName);
			// After encoding the string value, it is added to the string
			// table partition and assigned the next available compact
			// identifier.
			qnc = ruc.addQNameContext(localName);
		} else {
			// string value found in local partition
			// ==> string value is represented as zero (0) encoded as an
			// Unsigned Integer followed by an the compact identifier of the
			// string value as an n-bit unsigned integer n is log2 m and m is
			// the number of entries in the string table partition
			channel.encodeUnsignedInteger(0);
			int n = MethodsBag.getCodingLength(ruc.getNumberOfQNames());
			channel.encodeNBitUnsignedInteger(qnc.getLocalNameID(), n);
		}

		return qnc;
	}

	protected void encodeNamespacePrefix(RuntimeUriContext uriContext,
			String prefix, EncoderChannel channel) throws IOException {

		int nPfx = MethodsBag
				.getCodingLength(uriContext.getNumberOfPrefixes() + 1); // n-bit
		int pfxID = uriContext.getPrefixID(prefix);

		if (pfxID == Constants.NOT_FOUND) {
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
		int codeLength = fidelityOptions
				.get1stLevelEventCodeLength(getCurrentGrammar());
		if (codeLength > 0) {
			channel.encodeNBitUnsignedInteger(pos, codeLength);
		}
	}

	protected void encode2ndLevelEventCode(int pos) throws IOException {
		// 1st level
		final Grammar currentGrammar = getCurrentGrammar();
		channel.encodeNBitUnsignedInteger(currentGrammar.getNumberOfEvents(),
				fidelityOptions.get1stLevelEventCodeLength(currentGrammar));

		// 2nd level
		// int ch2 = currentGrammar.get2ndLevelCharacteristics(fidelityOptions);
		int ch2 = fidelityOptions.get2ndLevelCharacteristics(currentGrammar);
		assert (pos < ch2);

		channel.encodeNBitUnsignedInteger(pos, MethodsBag.getCodingLength(ch2));
	}

	protected void encode3rdLevelEventCode(int pos) throws IOException {
		// 1st level
		final Grammar currentGrammar = getCurrentGrammar();
		channel.encodeNBitUnsignedInteger(currentGrammar.getNumberOfEvents(),
				fidelityOptions.get1stLevelEventCodeLength(currentGrammar));

		// 2nd level
		// int ch2 = currentGrammar.get2ndLevelCharacteristics(fidelityOptions);
		int ch2 = fidelityOptions.get2ndLevelCharacteristics(currentGrammar);
		int ec2 = ch2 > 0 ? ch2 - 1 : 0; // any 2nd level events
		channel.encodeNBitUnsignedInteger(ec2, MethodsBag.getCodingLength(ch2));

		// 3rd level
		int ch3 = fidelityOptions.get3rdLevelCharacteristics();

		assert (pos < ch3);
		channel.encodeNBitUnsignedInteger(pos, MethodsBag.getCodingLength(ch3));
	}

	public void encodeStartDocument() throws EXIException, IOException {
		if (this.channel == null) {
			throw new EXIException(
					"No valid EXI OutputStream set for encoding. Please use setOutput( ... )");
		}
		initForEachRun();

		Production ei = getCurrentGrammar().getProduction(
				EventType.START_DOCUMENT);

		// Note: no EventCode needs to be written since there is only
		// one choice
		if (ei == null) {
			throw new EXIException("No EXI Event found for startDocument");
		}

		// update current rule
		updateCurrentRule(ei.getNextGrammar());

		lastEvent = EventType.START_DOCUMENT;
	}

	public void encodeEndDocument() throws EXIException, IOException {
		checkPendingCharacters(EventType.END_DOCUMENT);

		Production ei = getCurrentGrammar().getProduction(
				EventType.END_DOCUMENT);

		if (ei != null) {
			// encode EventCode
			encode1stLevelEventCode(ei.getEventCode());
		} else {
			throw new EXIException("No EXI Event found for endDocument");
		}

		lastEvent = EventType.END_DOCUMENT;
	}

	public void encodeStartElement(QName se) throws EXIException, IOException {
		encodeStartElement(se.getNamespaceURI(), se.getLocalPart(),
				se.getPrefix());
	}

	public void encodeStartElement(String uri, String localName, String prefix)
			throws EXIException, IOException {
		checkPendingCharacters(EventType.START_ELEMENT);

		sePrefix = prefix;
		Production ei;

		Grammar updContextRule;
		StartElement nextSE;

		Grammar currentGrammar = getCurrentGrammar();
		if ((ei = currentGrammar.getStartElementProduction(uri, localName)) != null) {
			assert (ei.getEvent().isEventType(EventType.START_ELEMENT));
			// encode 1st level EventCode
			encode1stLevelEventCode(ei.getEventCode());
			// next SE ...
			nextSE = (StartElement) ei.getEvent();
			// qname implicit by SE(qname) event, prefix only missing
			if (preservePrefix) {
				encodeQNamePrefix(nextSE.getQNameContext(), prefix, channel);
			}
			// next context rule
			updContextRule = ei.getNextGrammar();

		} else if ((ei = currentGrammar.getStartElementNSProduction(uri)) != null) {
			assert (ei.getEvent().isEventType(EventType.START_ELEMENT_NS));
			// encode 1st level EventCode
			encode1stLevelEventCode(ei.getEventCode());

			StartElementNS seNS = (StartElementNS) ei.getEvent();
			RuntimeUriContext uc = getUri(seNS.getNamespaceUriID());

			// encode local-name (and prefix)
			QNameContext qnc = encodeLocalName(localName, uc, channel);
			if (preservePrefix) {
				encodeQNamePrefix(qnc, prefix, channel);
			}

			// next context rule
			updContextRule = ei.getNextGrammar();
			// next SE ...
			nextSE = getGlobalStartElement(qnc);
		} else {
			// try SE(*), generic SE on first level
			if ((ei = currentGrammar
					.getProduction(EventType.START_ELEMENT_GENERIC)) != null) {
				assert (ei.getEvent()
						.isEventType(EventType.START_ELEMENT_GENERIC));
				// encode 1st level EventCode
				encode1stLevelEventCode(ei.getEventCode());

				// next context rule
				updContextRule = ei.getNextGrammar();
			} else {
				// Undeclared SE(*) can be found on 2nd level
				int ecSEundeclared = fidelityOptions.get2ndLevelEventCode(
						EventType.START_ELEMENT_GENERIC_UNDECLARED,
						currentGrammar);

				if (ecSEundeclared == Constants.NOT_FOUND) {
					// Note: should never happen except in strict mode
					throw new EXIException("Unexpected SE {" + uri + "}"
							+ localName + ", " + exiFactory.toString());
				}

				// limit grammar learning ?
				switch (this.limitGrammars()) {
				case XSI_TYPE:
					this.insertXsiTypeAnyType();
					currentGrammar = getCurrentGrammar();
					// encode 1st level EventCode
					ei = currentGrammar
							.getProduction(EventType.START_ELEMENT_GENERIC);
					assert (ei != null);
					encode1stLevelEventCode(ei.getEventCode());
					// next context rule
					updContextRule = ei.getNextGrammar();
					break;
				case GHOST_PRODUCTION:
				default:
					// encode [undeclared] event-code
					encode2ndLevelEventCode(ecSEundeclared);
					// next context rule
					updContextRule = currentGrammar.getElementContentGrammar();
					break;
				}
			}

			// encode entire qualified name
			QNameContext qnc = encodeQName(uri, localName, channel);
			if (preservePrefix) {
				encodeQNamePrefix(qnc, prefix, channel);
			}

			// next SE ...
			nextSE = getGlobalStartElement(qnc);

			// learning for built-in grammar (here and not as part of
			// SE_Undecl(*) because of FragmentContent!)
			currentGrammar.learnStartElement(nextSE);
			this.productionLearningCounting(currentGrammar);
		}

		// push element
		pushElement(updContextRule, nextSE);

		lastEvent = EventType.START_ELEMENT;
	}

	private enum ProfileDisablingMechanism {
		/** no disabling */
		NONE,
		/**
		 * preferred mechanism: xsi:type attribute inserted. Note: current
		 * grammar changes & only possible right after SE
		 */
		XSI_TYPE,
		/** 2nd mechanism: production has been inserted that is not usable */
		GHOST_PRODUCTION
	}

	private final void productionLearningCounting(Grammar g) {
		if (limitGrammarLearning) {
			// Note: no counting for schema-informed grammars and
			// BuiltInFragmentGrammar
			if (maxBuiltInProductions >= 0
					&& !g.isSchemaInformed()
					&& g.getGrammarType() != GrammarType.BUILT_IN_FRAGMENT_CONTENT) {
				learnedProductions++;
			}
		}
	}

	// Note: returns ACTION
	private final ProfileDisablingMechanism limitGrammars()
			throws EXIException, IOException {
		ProfileDisablingMechanism retVal = ProfileDisablingMechanism.NONE;

		Grammar currGrammar = getCurrentGrammar();

		if (limitGrammarLearning && grammar.isSchemaInformed()
				&& !currGrammar.isSchemaInformed()) {

			// number of built-in grammars reached
			if (maxBuiltInElementGrammars != -1) {
				int csize = runtimeGlobalElements.size();
				if (csize > maxBuiltInElementGrammars) {
					if (currGrammar.getNumberOfEvents() == 0) {
						// new grammar that hits bound
						retVal = ProfileDisablingMechanism.XSI_TYPE;
					} else if (isBuiltInStartTagGrammarWithAtXsiTypeOnly(currGrammar)) {
						// previous type cast
						retVal = ProfileDisablingMechanism.XSI_TYPE;
					}
				}
			}

			// number of productions reached?
			if (this.maxBuiltInProductions != -1
					&& retVal == ProfileDisablingMechanism.NONE
					&& learnedProductions >= this.maxBuiltInProductions) {
				// bound reached
				if (this.lastEvent == EventType.START_ELEMENT
						|| this.lastEvent == EventType.NAMESPACE_DECLARATION) {
					// First mean possible: Insert xsi:type
					retVal = ProfileDisablingMechanism.XSI_TYPE;
				} else {
					// Only 2nd mean possible: use ghost productions
					retVal = ProfileDisablingMechanism.GHOST_PRODUCTION;
					currGrammar.stopLearning();
				}
			}

		}

		return retVal;
	}

	private final void insertXsiTypeAnyType() throws EXIException, IOException {
		String pfx = null;
		if (this.preservePrefix) {
			// XMLConstants.W3C_XML_SCHEMA_NS_URI ==
			// "http://www.w3.org/2001/XMLSchema"
			pfx = this.getPrefix(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			if (pfx == null) {
				// no prefixes for XSD haven been declared so far
				pfx = "xsdP";
				this.encodeNamespaceDeclaration(
						XMLConstants.W3C_XML_SCHEMA_NS_URI, pfx);
			}
		}
		QNameValue type = new QNameValue(XMLConstants.W3C_XML_SCHEMA_NS_URI,
				"anyType", pfx);

		// needed to avoid grammar learning
		this.encodeAttributeXsiType(type, pfx, true);
	}

	public void encodeNamespaceDeclaration(String uri, String prefix)
			throws EXIException, IOException {
		declarePrefix(prefix, uri);

		if (preservePrefix) {
			assert (sePrefix != null);

			// event code
			final Grammar currentGrammar = getCurrentGrammar();
			int ec2 = fidelityOptions.get2ndLevelEventCode(
					EventType.NAMESPACE_DECLARATION, currentGrammar);
			assert (fidelityOptions.get2ndLevelEventType(ec2, currentGrammar) == EventType.NAMESPACE_DECLARATION);
			encode2ndLevelEventCode(ec2);

			// prefix mapping
			RuntimeUriContext euc = encodeUri(uri, channel);
			encodeNamespacePrefix(euc, prefix, channel);

			// local-element-ns
			channel.encodeBoolean(prefix.equals(sePrefix));

			lastEvent = EventType.NAMESPACE_DECLARATION;
		}
	}

	public void encodeEndElement() throws EXIException, IOException {
		checkPendingCharacters(EventType.END_ELEMENT);

		Grammar currentGrammar = getCurrentGrammar();
		Production ei = currentGrammar.getProduction(EventType.END_ELEMENT);

		if (ei != null) {
			// encode EventCode (common case)
			encode1stLevelEventCode(ei.getEventCode());
		} else {
			// Undeclared EE can be found on 2nd level
			int ecEEundeclared = fidelityOptions.get2ndLevelEventCode(
					EventType.END_ELEMENT_UNDECLARED, currentGrammar);

			if (ecEEundeclared == Constants.NOT_FOUND) {
				// Should only happen in STRICT mode
				// Special case: SAX does not inform about empty ("") CH events
				try {
					this.encodeCharactersForce(StringCoder.EMPTY_STRING_VALUE);
					currentGrammar = getCurrentGrammar();
					ei = currentGrammar.getProduction(EventType.END_ELEMENT);
					encode1stLevelEventCode(ei.getEventCode());
				} catch (Exception e) {
					// Note: should never happen except in strict mode
					throw new EXIException("Unexpected EE {"
							+ getElementContext() + ", "
							+ exiFactory.toString());
				}
			} else {
				// limit grammar learning ?
				switch (this.limitGrammars()) {
				case XSI_TYPE:
					this.insertXsiTypeAnyType();
					currentGrammar = getCurrentGrammar();
					// encode 1st level EventCode
					ei = currentGrammar
							.getProduction(EventType.END_ELEMENT);
					assert (ei != null);
					encode1stLevelEventCode(ei.getEventCode());
					break;
				case GHOST_PRODUCTION:
				default:
					// encode [undeclared] event-code
					encode2ndLevelEventCode(ecEEundeclared);
					// learn end-element event ?
					currentGrammar.learnEndElement();
					this.productionLearningCounting(currentGrammar);
					break;
				}
			}
		}

		// pop element from stack
		ElementContext ec = popElement();

		// make sure to adapt xml:space behavior
		if (ec.isXmlSpacePreserve() != null) {
			// check in the hierarchy whether there is xml:space present OR
			// "default"
			boolean isOtherPreserve = false;
			for (int i = this.elementContextStackIndex; i >= 0; i--) {
				Boolean isP = this.elementContextStack[i].isXmlSpacePreserve();
				if (isP != null) {
					isOtherPreserve = isP;
					break;
				}
			}
			this.isXmlSpacePreserve = isOtherPreserve;
		}

		lastEvent = EventType.END_ELEMENT;
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
		boolean force2ndLevelProduction = false;
		if (this.limitGrammars() == ProfileDisablingMechanism.XSI_TYPE) {
			force2ndLevelProduction = true;
		}
		this.encodeAttributeXsiType(type, pfx, force2ndLevelProduction);
	}

	private void encodeAttributeXsiType(Value type, String pfx,
			final boolean force2ndLevelProduction) throws EXIException,
			IOException {
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

		final Grammar currentGrammar = getCurrentGrammar();

		int ec2 = fidelityOptions.get2ndLevelEventCode(
				EventType.ATTRIBUTE_XSI_TYPE, currentGrammar);

		if (ec2 != Constants.NOT_FOUND) {

			assert (fidelityOptions.get2ndLevelEventType(ec2, currentGrammar) == EventType.ATTRIBUTE_XSI_TYPE);

			// encode event-code, AT(xsi:type)
			encode2ndLevelEventCode(ec2);
			// prefix
			if (this.preservePrefix) {
				encodeQNamePrefix(getXsiTypeContext(), pfx, channel);
			}

		} else {
			// Note: cannot be encoded as any other attribute due to the
			// different channels in compression mode

			// try first (learned) xsi:type attribute
			Production ei;
			if (force2ndLevelProduction) {
				// only 2nd level of interest
				ei = null;
			} else {
				ei = currentGrammar.getAttributeProduction(
						XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,
						Constants.XSI_TYPE);
			}

			if (ei != null) {
				encode1stLevelEventCode(ei.getEventCode());
			} else {
				// try generic attribute
				if (!force2ndLevelProduction) {
					ei = currentGrammar
							.getProduction(EventType.ATTRIBUTE_GENERIC);
				}

				if (ei != null) {
					encode1stLevelEventCode(ei.getEventCode());
				} else {
					ec2 = fidelityOptions.get2ndLevelEventCode(
							EventType.ATTRIBUTE_GENERIC_UNDECLARED,
							currentGrammar);
					if (ec2 != Constants.NOT_FOUND) {
						encode2ndLevelEventCode(ec2);
						QNameContext qncType = getXsiTypeContext();

						if (limitGrammarLearning) {
							// 3.2 Grammar Learning Disabling Parameters
							// - In particular, the AT(xsi:type) productions
							// that
							// would be inserted in
							// grammars that would be instantiated after the
							// maximumNumberOfBuiltInElementGrammars
							// threshold are not counted.
							if (runtimeGlobalElements.size() > maxBuiltInElementGrammars
									&& currentGrammar.getNumberOfEvents() == 0) {
								// can't evolve anymore
								currentGrammar.stopLearning();
							} else {
								this.productionLearningCounting(currentGrammar);
							}
						}
						currentGrammar.learnAttribute(new Attribute(qncType));
					} else {
						throw new EXIException("TypeCast " + type
								+ " not encodable!");
					}
				}
				// xsi:type as qname
				QNameContext qncType = getXsiTypeContext();
				encodeQName(qncType.getNamespaceUri(), qncType.getLocalName(),
						channel);

				if (this.preservePrefix) {
					encodeQNamePrefix(qncType, pfx, channel);
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
			typeEncoder.writeValue(getXsiTypeContext(), channel, stringEncoder);

			RuntimeUriContext uc = getUri(qnameURI);
			if (uc != null) {
				qncType = uc.getQNameContext(qnameLocalName);
			} else {
				qncType = null;
			}
		} else {
			// typed
			qncType = encodeQName(qnameURI, qnameLocalName, channel);

			if (preservePrefix) {
				encodeQNamePrefix(qncType, qnamePrefix, channel);
			}
		}

		// grammar exists ?
		if (qncType != null && qncType.getTypeGrammar() != null) {
			// update grammar according to given xsi:type
			updateCurrentRule(qncType.getTypeGrammar());
		}

		lastEvent = EventType.ATTRIBUTE_XSI_TYPE;
	}

	public void encodeAttributeXsiNil(Value nil, String pfx)
			throws EXIException, IOException {
		final Grammar currentGrammar = getCurrentGrammar();
		if (currentGrammar.isSchemaInformed()) {
			SchemaInformedGrammar siCurrentRule = (SchemaInformedGrammar) currentGrammar;

			if (booleanDatatype.isValid(nil)) {

				// Note: in some cases we can simply skip the xsi:nil event
				if (!preserveLexicalValues
						&& !booleanDatatype.getBoolean()
						&& !this.encodingOptions
								.isOptionEnabled(EncodingOptions.INCLUDE_INSIGNIFICANT_XSI_NIL)) {
					return;
				}

				// schema-valid boolean
				int ec2 = fidelityOptions.get2ndLevelEventCode(
						EventType.ATTRIBUTE_XSI_NIL, siCurrentRule);

				if (ec2 != Constants.NOT_FOUND) {
					// encode event-code only
					encode2ndLevelEventCode(ec2);
					// prefix
					if (this.preservePrefix) {
						encodeQNamePrefix(getXsiNilContext(), pfx, channel);
					}

					// encode nil value "content" as Boolean
					if (preserveLexicalValues) {
						// as string
						typeEncoder.isValid(booleanDatatype, nil);
						typeEncoder.writeValue(getXsiTypeContext(), channel,
								stringEncoder);
					} else {
						// typed
						booleanDatatype
								.writeValue(null, channel, stringEncoder);
					}

					if (booleanDatatype.getBoolean()) { // jump to typeEmpty
						// update current rule
						updateCurrentRule(((SchemaInformedFirstStartTagGrammar) siCurrentRule)
								.getTypeEmpty());
					}
				} else {
					Production ei = currentGrammar
							.getProduction(EventType.ATTRIBUTE_GENERIC);
					if (ei != null) {
						encode1stLevelEventCode(ei.getEventCode());
						// qname & prefix
						RuntimeUriContext euc = encodeUri(
								XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,
								channel);
						encodeLocalName(Constants.XSI_NIL, euc, channel);
						if (this.preservePrefix) {
							encodeQNamePrefix(getXsiNilContext(), pfx, channel);
						}

						// encode nil value "content" as Boolean
						if (preserveLexicalValues) {
							// as string
							typeEncoder.isValid(booleanDatatype, nil);
							// typeEncoder.writeValue(XSI_NIL, channel);
							typeEncoder.writeValue(getXsiNilContext(), channel,
									stringEncoder);
						} else {
							// typed
							booleanDatatype.writeValue(null, channel,
									stringEncoder);
						}

						if (booleanDatatype.getBoolean()) { // jump to typeEmpty
							// update current rule
							updateCurrentRule(((SchemaInformedFirstStartTagGrammar) siCurrentRule)
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
				SchemaInformedGrammar sig = (SchemaInformedGrammar) currentGrammar;
				encodeSchemaInvalidAttributeEventCode(sig
						.getNumberOfDeclaredAttributes());
				RuntimeUriContext euc = encodeUri(
						XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, channel);
				encodeLocalName(Constants.XSI_NIL, euc, channel);
				if (this.preservePrefix) {
					encodeQNamePrefix(getXsiNilContext(), pfx, channel);
				}

				Datatype datatype = BuiltIn.DEFAULT_DATATYPE;
				isTypeValid(datatype, nil);
				this.writeValue(getXsiTypeContext());
			}
		} else {
			// encode as any other attribute
			encodeAttribute(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,
					Constants.XSI_NIL, pfx, nil);
		}

		lastEvent = EventType.ATTRIBUTE_XSI_NIL;
	}

	protected void encodeSchemaInvalidAttributeEventCode(int eventCode3)
			throws IOException {
		final Grammar currentGrammar = getCurrentGrammar();
		// schema-invalid AT
		int ec2ATdeviated = fidelityOptions.get2ndLevelEventCode(
				EventType.ATTRIBUTE_INVALID_VALUE, currentGrammar);
		encode2ndLevelEventCode(ec2ATdeviated);
		// encode 3rd level event-code
		// AT specialty: calculate 3rd level attribute event-code
		// int eventCode3 = ei.getEventCode()
		// - currentRule.getLeastAttributeEventCode();
		SchemaInformedGrammar sig = (SchemaInformedGrammar) currentGrammar;
		channel.encodeNBitUnsignedInteger(eventCode3, MethodsBag
				.getCodingLength(sig.getNumberOfDeclaredAttributes() + 1));
	}

	public void encodeAttribute(QName at, Value value) throws EXIException,
			IOException {
		encodeAttribute(at.getNamespaceURI(), at.getLocalPart(),
				at.getPrefix(), value);
	}

	public void encodeAttribute(final String uri, final String localName,
			String prefix, Value value) throws EXIException, IOException {
		Production ei;
		QNameContext qnc;
		Grammar next;

		Grammar currentGrammar = getCurrentGrammar();
		if ((ei = currentGrammar.getAttributeProduction(uri, localName)) != null) {
			// declared AT(uri:localName)
			Attribute at = (Attribute) (ei.getEvent());
			qnc = at.getQNameContext();

			if (isTypeValid(at.getDatatype(), value)) {
				encode1stLevelEventCode(ei.getEventCode());
			} else {
				// AT specialty: calculate 3rd level attribute event-code
				SchemaInformedGrammar sig = (SchemaInformedGrammar) currentGrammar;
				int eventCode3 = ei.getEventCode()
						- sig.getLeastAttributeEventCode();
				encodeSchemaInvalidAttributeEventCode(eventCode3);
				isTypeValid(BuiltIn.DEFAULT_DATATYPE, value);
			}
			next = ei.getNextGrammar();
		} else {

			switch (this.limitGrammars()) {
			case XSI_TYPE:
				this.insertXsiTypeAnyType();
				currentGrammar = this.getCurrentGrammar();
				break;
			case GHOST_PRODUCTION:
			default:
				/* no special action */
				break;
			}

			ei = currentGrammar.getAttributeNSProduction(uri);
			if (ei == null) {
				ei = currentGrammar.getProduction(EventType.ATTRIBUTE_GENERIC);
				if (ei == null) {
					// Undeclared AT(*) can be found on 2nd level
				}
			}

			Attribute globalAT;

			if (currentGrammar.isSchemaInformed()
					&& (globalAT = getGlobalAttribute(uri, localName)) != null) {
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
						this.encodeAttributeEventCodeUndeclared(currentGrammar,
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
					SchemaInformedGrammar sig = (SchemaInformedGrammar) currentGrammar;
					encodeSchemaInvalidAttributeEventCode(sig
							.getNumberOfDeclaredAttributes());
					isTypeValid(BuiltIn.DEFAULT_DATATYPE, value);
				}

				if (ei == null
						|| ei.getEvent().isEventType(
								EventType.ATTRIBUTE_GENERIC)) {
					// (un)declared AT(*)
					qnc = this.encodeQName(uri, localName, channel);
					next = ei == null ? currentGrammar : ei.getNextGrammar();
				} else {
					// declared AT(uri:*)
					AttributeNS atNS = (AttributeNS) ei.getEvent();
					// localname only
					RuntimeUriContext uc = getUri(atNS.getNamespaceUriID());
					qnc = encodeLocalName(localName, uc, channel);
					next = ei.getNextGrammar();
				}

			} else {
				// no schema-informed grammar --> default datatype in any case
				// NO global attribute --> default datatype
				isTypeValid(BuiltIn.DEFAULT_DATATYPE, value);

				if (ei == null) {
					// Undeclared AT(*), 2nd level

					qnc = encodeUndeclaredAT(currentGrammar, uri, localName); // ,
																				// ghostProduction);
					next = currentGrammar;
				} else {
					// Declared AT(uri:*) or AT(*) on 1st level
					qnc = encodeDeclaredAT(ei, uri, localName);
					next = ei.getNextGrammar();
				}
			}
		}

		// prefix
		assert (qnc != null);
		if (preservePrefix) {
			this.encodeQNamePrefix(qnc, prefix, channel);
		}

		// so far: event-code has been written & datatype is settled
		// the actual value is still missing
		this.writeValue(qnc);

		// update current rule
		assert (next != null);
		updateCurrentRule(next);

		if (value.getValueType() == ValueType.STRING
				&& XMLConstants.XML_NS_URI.equals(uri)) {
			ElementContext ec = this.getElementContext();
			if ("preserve".equals(value.toString())) {
				this.isXmlSpacePreserve = true;
				ec.setXmlSpacePreserve(Boolean.TRUE);
			} else if ("default".equals(value.toString())) {
				this.isXmlSpacePreserve = false;
				ec.setXmlSpacePreserve(Boolean.FALSE);
			}

		}

		lastEvent = EventType.ATTRIBUTE;
	}

	private void encodeAttributeEventCodeUndeclared(Grammar currentGrammar,
			String localName) throws IOException, EXIException {
		int ecATundeclared = fidelityOptions.get2ndLevelEventCode(
				EventType.ATTRIBUTE_GENERIC_UNDECLARED, currentGrammar);

		if (ecATundeclared == Constants.NOT_FOUND) {
			assert (fidelityOptions.isStrict());
			throw new EXIException("Attribute '" + localName
					+ "' cannot be encoded!");
		}
		assert (ecATundeclared != Constants.NOT_FOUND);
		// encode event-code
		encode2ndLevelEventCode(ecATundeclared);
	}

	private QNameContext encodeDeclaredAT(Production ei, String uri,
			String localName) throws IOException {
		// eventCode
		encode1stLevelEventCode(ei.getEventCode());

		QNameContext qnc;
		if (ei.getEvent().isEventType(EventType.ATTRIBUTE_NS)) {
			// declared AT(uri:*)
			AttributeNS atNS = (AttributeNS) ei.getEvent();
			// localname only
			RuntimeUriContext uc = getUri(atNS.getNamespaceUriID());
			qnc = encodeLocalName(localName, uc, channel);

		} else {
			// declared AT(*)
			qnc = encodeQName(uri, localName, channel);
		}
		return qnc;
	}

	private QNameContext encodeUndeclaredAT(Grammar currentGrammar, String uri,
			String localName) throws EXIException, IOException {

		// event-code
		encodeAttributeEventCodeUndeclared(currentGrammar, localName);

		// qualified name
		QNameContext qnc = this.encodeQName(uri, localName, channel);

		// learn attribute event
		currentGrammar.learnAttribute(new Attribute(qnc));
		this.productionLearningCounting(currentGrammar);

		return qnc;
	}

	protected Attribute getGlobalAttribute(String uri, String localName) {
		RuntimeUriContext uc = this.getUri(uri);
		if (uc != null) {
			return getGlobalAttribute(uc, localName);
		}

		return null;
	}

	protected Attribute getGlobalAttribute(RuntimeUriContext uc,
			String localName) {
		assert (uc != null);
		QNameContext qnc = uc.getQNameContext(localName);
		if (qnc != null) {
			return qnc.getGlobalAttribute();
		}

		return null;
	}

	// private final boolean doTokenizeCHs = false; // TEST
	//
	// if (doTokenizeCHs) {
	// String delimiters;
	// // delimiters = "/#"; // uri delimiters
	// delimiters = " ,.-\t\n\r"; // text delimiters
	// String s = chars.toString();
	//
	// // remove leading and trailing whitespaces
	// s = s.trim();
	//
	// // remove duplicates whitespaces ?
	// // \s matches a space, tab, new line, carriage return, form feed or
	// vertical tab
	// // + says "one or more of those"
	//
	// s = s.replaceAll("\\s+", " ");
	//
	// List<String> uToks = getTokens(s, delimiters);
	// for(String ut : uToks) {
	// encodeCharactersForce(new StringValue(ut));
	// }
	// }
	//
	// @SuppressWarnings("unused")
	// public static List<String> getTokens(String tokenString, String
	// delimiters) {
	// StringTokenizer st = new StringTokenizer(tokenString, delimiters, true);
	// ArrayList<String> uToks = new ArrayList<String>();
	// if(st.countTokens() > 1) {
	//
	// boolean prevWasDelimiter = false;
	//
	// while (st.hasMoreTokens()) {
	// String t = st.nextToken();
	//
	// // collapse contiguous delimiters
	// if(uToks.size() == 0) {
	// // first, just add
	// uToks.add(t);
	// prevWasDelimiter = delimiters.contains(t);
	// } else {
	// // all others
	// if(delimiters.contains(t)) {
	// // 't' is delimiter
	// // if(prevWasDelimiter) {
	// // combine them
	// uToks.set(uToks.size()-1, uToks.get(uToks.size()-1)+t);
	// // } else {
	// // // new token
	// // uToks.add(t);
	// // }
	// prevWasDelimiter = true;
	// } else {
	// // 't' is not a delimiter
	// // if(prevWasDelimiter) {
	// // if(t.length() > 4) {
	// // new token
	// uToks.add(t);
	// // } else {
	// // // combine them
	// // uToks.set(uToks.size()-1, uToks.get(uToks.size()-1)+t);
	// // }
	// prevWasDelimiter = false;
	// }
	// }
	//
	// }
	// } else {
	// uToks.add(tokenString);
	// }
	//
	// if(false) {
	// System.out.println("-------------------");
	// for(String t : uToks) {
	// System.out.println("<\"" + t + "\">");
	// }
	// }
	//
	// return uToks;
	// }
	//
	//
	// public static List<String> getUriTokens(String uri) {
	// StringTokenizer st = new StringTokenizer(uri, "/#", true);
	// ArrayList<String> uToks = new ArrayList<String>();
	// if (st.countTokens() > 1) {
	// int nextIndex = 0;
	// while (st.hasMoreTokens()) {
	// String s = st.nextToken();
	// if ("/".equals(s)) {
	// // is delimiter
	// assert (uToks.size() > (nextIndex - 1));
	// uToks.set(nextIndex - 1, uToks.get(nextIndex - 1) + s);
	// } else if ("#".equals(s)) {
	// // is delimiter
	// assert (uToks.size() > (nextIndex - 1));
	// uToks.set(nextIndex - 1, uToks.get(nextIndex - 1) + s);
	// } else {
	// if (uToks.size() > nextIndex) {
	// // Already there
	// uToks.set(nextIndex - 1, uToks.get(nextIndex - 1) + s);
	// } else {
	// // new entry
	// uToks.add(s);
	// nextIndex++;
	// }
	// }
	// // System.out.println("\t" + s);
	// }
	// } else {
	// uToks.add(uri);
	// }
	//
	// return uToks;
	// }

	// returns null if no CH datatype is available or schema-less
	private WhiteSpace getDatatypeWhiteSpace() {
		Grammar currGr = this.getCurrentGrammar();
		if (currGr.isSchemaInformed() && currGr.getNumberOfEvents() > 0) {
			Production prod = currGr.getProduction(0);
			if (prod.getEvent().getEventType() == EventType.CHARACTERS) {
				Characters ch = (Characters) prod.getEvent();
				return ch.getDatatype().getWhiteSpace();
			}
		}

		return null;
	}

	static void replace(char[] chars, int len) {
		// replace
		// All occurrences of #x9 (tab), #xA (line feed) and #xD (carriage
		// return) are replaced with #x20 (space)
		for (int i = 0; i < len; i++) {
			if (chars[i] == '\t' || chars[i] == '\n' || chars[i] == '\r') {
				chars[i] = ' ';
			}
		}
	}

	static void shiftLeft(char[] chars, int pos, int len) {
		System.arraycopy(chars, pos + 1, chars, pos, len - 1);
	}

	// returns new length
	static int collapse(char[] chars, int len) {
		// collapse
		// After the processing implied by replace, contiguous sequences of
		// #x20's are collapsed to a single #x20, and leading and trailing
		// #x20's are removed.
		replace(chars, len);

		int newLen = len;

		// contiguous sequences of #x20's are collapsed to a single #x20
		if (newLen > 1) {
			int i = 0;
			while (i < (newLen - 1)) {
				char thisChar = chars[i];
				char nextChar = chars[i + 1];
				if (thisChar == ' ' && nextChar == ' ') {
					// eliminate one space
					shiftLeft(chars, i, newLen - i);
					newLen--;
				} else {
					i++;
				}
			}
		}

		// leading and trailing #x20's are removed
		newLen = trimSpaces(chars, newLen);

		return newLen;
	}

	// returns new length
	static int trimSpaces(char[] chars, int len) {
		// leading and trailing #x20's are removed
		int newLen = len;

		// leading
		while (newLen > 0 && chars[0] == ' ') {
			// eliminate one leading space
			shiftLeft(chars, 0, newLen);
			newLen--;
		}
		// trailing
		int i = newLen - 1;
		while (i >= 0 && chars[i] == ' ') {
			// eliminate one trailing space
			newLen--;
			i--;
		}

		return newLen;
	}

	static boolean isWS(char c) {
		return (c == ' ' || c == '\n' || c == '\r' || c == '\t');
	}

	static boolean isSolelyWS(char[] chars, int len) {
		for (int i = 0; i < len; i++) {
			if (!isWS(chars[i])) {
				return false;
			}
		}
		return true;
	}

	static int trimWS(char[] chars, int len) {
		// leading and trailing whitespaces are removed
		int newLen = len;

		// leading
		while (newLen > 0 && isWS(chars[0])) {
			// eliminate one leading space
			shiftLeft(chars, 0, newLen);
			newLen--;
		}
		// trailing
		int i = newLen - 1;
		while (i >= 0 && isWS(chars[i])) {
			// eliminate one trailing space
			newLen--;
			i--;
		}

		return newLen;
	}

	/** character buffer for CH trimming, replacing, collapsing */
	private char[] cbuffer;

	private int modeValuesToCBuffer() {
		int len = 0;
		for (int i = 0; i < bChars.size(); i++) {
			len += bChars.get(i).getCharactersLength();
		}
		if (cbuffer == null || cbuffer.length < len) {
			cbuffer = new char[len];
		}
		int pos = 0;
		for (int i = 0; i < bChars.size(); i++) {
			Value v = bChars.get(i);
			v.getCharacters(cbuffer, pos);
			pos += v.getCharactersLength();
		}
		assert (len == pos);
		return len;
	}

	protected void checkPendingCharacters(EventType nextEvent)
			throws EXIException, IOException {
		final int numberOfValues = bChars.size();
		if (numberOfValues > 0) {
			if (numberOfValues == 1
					&& bChars.get(0).getValueType() != ValueType.STRING) {
				// typed data uses its own whitespace rules
				encodeCharactersForce(bChars.get(0));
			} else {
				// else: string or multiple typed values
				WhiteSpace ws = getDatatypeWhiteSpace();
				// Don't we want to prune insignificant whitespace characters
				if (!(preserveLexicalValues || this.isXmlSpacePreserve || ws == WhiteSpace.preserve)) {
					int len = modeValuesToCBuffer();
					if (ws == WhiteSpace.replace) {
						// replace
						// All occurrences of #x9 (tab), #xA (line feed) and #xD
						// (carriage return) are replaced with #x20 (space)
						replace(cbuffer, len);
					} else if (ws == WhiteSpace.collapse) {
						// collapse
						// After the processing implied by replace, contiguous
						// sequences of #x20's are collapsed to a single #x20,
						// and leading and trailing #x20's are removed.
						replace(cbuffer, len);
						len = collapse(cbuffer, len);
					} else {
						// schema-less, no datatype
						// https://lists.w3.org/Archives/Public/public-exi/2015Oct/0008.html
						// If it is schema-less:
						// - Simple data (data between s+e) are all preserved.
						// - For complex data (data between s+s, e+s, e+e), it
						// is same as schema-informed case.
						if ((this.lastEvent == EventType.START_ELEMENT
								|| this.lastEvent == EventType.ATTRIBUTE
								|| this.lastEvent == EventType.ATTRIBUTE_XSI_NIL
								|| this.lastEvent == EventType.ATTRIBUTE_XSI_TYPE || this.lastEvent == EventType.NAMESPACE_DECLARATION)
								&& (nextEvent == EventType.END_ELEMENT
										|| nextEvent == EventType.COMMENT
										|| nextEvent == EventType.PROCESSING_INSTRUCTION || nextEvent == EventType.DOC_TYPE)) {
							// simple data --> preserve
						} else {
							// For complex data (data between s+s, e+s, e+e),
							// whitespaces nodes (i.e.
							// strings that consist solely of whitespaces) are
							// removed
							if (isSolelyWS(cbuffer, len)) {
								len = 0;
							}
							// trim?
							// len = trimWS(cbuffer, len);
						}
					}
					if (len == 0) {
						// --> omit empty string
					} else {
						StringValue sv = new StringValue(new String(cbuffer, 0,
								len));
						encodeCharactersForce(sv);
					}
				} else {
					// preserve data as is
					if (numberOfValues == 1) {
						encodeCharactersForce(bChars.get(0));
					} else {
						// collapse all events to a single one (not very
						// efficient in most of the cases)
						int len = modeValuesToCBuffer();
						StringValue sv = new StringValue(new String(cbuffer, 0,
								len));
						encodeCharactersForce(sv);
					}
				}
			}
			bChars.clear();
		}
	}

	public void encodeCharacters(Value chars) throws EXIException, IOException {
		bChars.add(chars);
	}

	protected void encodeCharactersForce(Value chars) throws EXIException,
			IOException {

		Grammar currentGrammar = getCurrentGrammar();
		Production ei = currentGrammar.getProduction(EventType.CHARACTERS);

		// valid value and valid event-code ?
		if (ei != null
				&& isTypeValid(((DatatypeEvent) ei.getEvent()).getDatatype(),
						chars)) {
			// right characters event found & data type-valid
			// --> encode EventCode, schema-valid content plus grammar moves
			// on
			encode1stLevelEventCode(ei.getEventCode());
			// writeValue(getElementContext().eqname.getQName());
			writeValue(getElementContext().qnameContext);
			// update current rule
			updateCurrentRule(ei.getNextGrammar());
		} else {
			// generic CH (on first level)
			ei = currentGrammar.getProduction(EventType.CHARACTERS_GENERIC);

			if (ei != null) {
				// encode EventCode
				encode1stLevelEventCode(ei.getEventCode());
				// encode schema-invalid content as string
				isTypeValid(BuiltIn.DEFAULT_DATATYPE, chars);
				writeValue(getElementContext().qnameContext);
				// update current rule
				updateCurrentRule(ei.getNextGrammar());
			} else {
				// Undeclared CH can be found on 2nd level
				int ecCHundeclared = fidelityOptions
						.get2ndLevelEventCode(
								EventType.CHARACTERS_GENERIC_UNDECLARED,
								currentGrammar);

				if (ecCHundeclared == Constants.NOT_FOUND) {
					if (exiFactory.isFragment()) {
						// characters in "outer" fragment element
						throwWarning("Skip CH: '" + chars + "'");
					} else if (!this.isXmlSpacePreserve
							&& fidelityOptions.isStrict()
							&& chars.toString().trim().length() == 0) {
						// empty characters in STRICT
						throwWarning("Skip CH: '" + chars + "'");
					} else {
						throw new EXIException("Characters '" + chars
								+ "' cannot be encoded!");
					}
				} else {
					Grammar updContextRule;

					// limit grammar learning ?
					switch (this.limitGrammars()) {
					case XSI_TYPE:
						this.insertXsiTypeAnyType();
						currentGrammar = getCurrentGrammar();
						// encode 1st level EventCode
						ei = currentGrammar
								.getProduction(EventType.CHARACTERS_GENERIC);
						assert (ei != null);
						encode1stLevelEventCode(ei.getEventCode());
						// next rule
						updContextRule = ei.getNextGrammar();
						break;
					case GHOST_PRODUCTION:
					default:
						// encode [undeclared] event-code
						encode2ndLevelEventCode(ecCHundeclared);
						// learn characters event ?
						currentGrammar.learnCharacters();
						this.productionLearningCounting(currentGrammar);
						// next rule
						updContextRule = currentGrammar
								.getElementContentGrammar();
						break;
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
			checkPendingCharacters(EventType.DOC_TYPE);

			// DOCTYPE can be found on 2nd level
			int ec2 = fidelityOptions.get2ndLevelEventCode(EventType.DOC_TYPE,
					getCurrentGrammar());
			encode2ndLevelEventCode(ec2);

			// name, public, system, text AS string
			writeString(name);
			writeString(publicID);
			writeString(systemID);
			writeString(text);
		}
	}

	private final void doLimitGrammarLearningForErCmPi() throws EXIException,
			IOException {
		switch (this.limitGrammars()) {
		case XSI_TYPE:
			this.insertXsiTypeAnyType();
			break;
		default:
			/* no special action */
			break;
		}
	}

	public void encodeEntityReference(String name) throws EXIException,
			IOException {
		if (fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_DTD)) {
			checkPendingCharacters(EventType.ENTITY_REFERENCE);

			// grammar learning restricting (if necessary)
			doLimitGrammarLearningForErCmPi();

			// EntityReference can be found on 2nd level
			Grammar currentGrammar = getCurrentGrammar();
			int ec2 = fidelityOptions.get2ndLevelEventCode(
					EventType.ENTITY_REFERENCE, currentGrammar);
			encode2ndLevelEventCode(ec2);

			// name AS string
			writeString(name);

			// update current rule
			updateCurrentRule(currentGrammar.getElementContentGrammar());
		}
	}

	public void encodeComment(char[] ch, int start, int length)
			throws EXIException, IOException {
		if (fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_COMMENT)) {
			checkPendingCharacters(EventType.COMMENT);

			// grammar learning restricting (if necessary)
			doLimitGrammarLearningForErCmPi();

			// comments can be found on 3rd level
			final Grammar currentGrammar = getCurrentGrammar();
			int ec3 = fidelityOptions.get3rdLevelEventCode(EventType.COMMENT);
			encode3rdLevelEventCode(ec3);

			// encode CM content
			writeString(new String(ch, start, length));

			// update current rule
			updateCurrentRule(currentGrammar.getElementContentGrammar());
		}
	}

	public void encodeProcessingInstruction(String target, String data)
			throws EXIException, IOException {
		if (fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_PI)) {
			checkPendingCharacters(EventType.PROCESSING_INSTRUCTION);

			// grammar learning restricting (if necessary)
			doLimitGrammarLearningForErCmPi();

			// processing instructions can be found on 3rd level
			final Grammar currentGrammar = getCurrentGrammar();
			int ec3 = fidelityOptions
					.get3rdLevelEventCode(EventType.PROCESSING_INSTRUCTION);
			encode3rdLevelEventCode(ec3);

			// encode PI content
			writeString(target);
			writeString(data);

			// update current rule
			updateCurrentRule(currentGrammar.getElementContentGrammar());
		}
	}

}
