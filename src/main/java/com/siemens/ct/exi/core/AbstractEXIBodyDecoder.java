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
import java.util.Iterator;

import javax.xml.XMLConstants;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.EXIBodyDecoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.context.GrammarContext;
import com.siemens.ct.exi.context.GrammarUriContext;
import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.core.container.DocType;
import com.siemens.ct.exi.core.container.NamespaceDeclaration;
import com.siemens.ct.exi.core.container.ProcessingInstruction;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.datatype.strings.StringDecoder;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammars.event.Attribute;
import com.siemens.ct.exi.grammars.event.AttributeNS;
import com.siemens.ct.exi.grammars.event.Characters;
import com.siemens.ct.exi.grammars.event.Event;
import com.siemens.ct.exi.grammars.event.EventType;
import com.siemens.ct.exi.grammars.event.StartElement;
import com.siemens.ct.exi.grammars.event.StartElementNS;
import com.siemens.ct.exi.grammars.grammar.Grammar;
import com.siemens.ct.exi.grammars.grammar.GrammarType;
import com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTagGrammar;
import com.siemens.ct.exi.grammars.grammar.SchemaInformedGrammar;
import com.siemens.ct.exi.grammars.production.Production;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.types.BuiltIn;
import com.siemens.ct.exi.types.TypeDecoder;
import com.siemens.ct.exi.util.MethodsBag;
import com.siemens.ct.exi.util.xml.QNameUtilities;
import com.siemens.ct.exi.values.BooleanValue;
import com.siemens.ct.exi.values.QNameValue;
import com.siemens.ct.exi.values.Value;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.6-SNAPSHOT
 */

public abstract class AbstractEXIBodyDecoder extends AbstractEXIBodyCoder
		implements EXIBodyDecoder {

	// next event
	protected Event nextEvent;
	protected Grammar nextGrammar;
	protected EventType nextEventType;

	// decoder stream
	protected DecoderChannel channel;

	// namespaces/prefixes
	protected final int numberOfUriContexts;

	// Type Decoder
	protected final TypeDecoder typeDecoder;

	// String Decoder
	protected final StringDecoder stringDecoder;
	
	// current AT values
	protected QNameContext attributeQNameContext;
	protected String attributePrefix;
	protected Value attributeValue;

	public AbstractEXIBodyDecoder(EXIFactory exiFactory) throws EXIException {
		super(exiFactory);

		// decoder stuff
		typeDecoder = exiFactory.createTypeDecoder();
		stringDecoder = exiFactory.createStringDecoder();

		numberOfUriContexts = this.grammar.getGrammarContext()
				.getNumberOfGrammarUriContexts();
	}

	@Override
	protected final void pushElement(Grammar updContextGrammar, StartElement se) {
		super.pushElement(updContextGrammar, se);
		if (!preservePrefix && this.elementContextStackIndex == 1) {
			// Note: can be done several times due to multiple root elements in
			// fragments
			GrammarContext gc = this.grammar.getGrammarContext();
			for (int i = 2; i < gc.getNumberOfGrammarUriContexts(); i++) {
				GrammarUriContext guc = gc.getGrammarUriContext(i);
				String pfx = guc.getDefaultPrefix();
				declarePrefix(pfx, guc.getNamespaceUri());
			}
		}
	}

	@Override
	protected void initForEachRun() throws EXIException, IOException {
		super.initForEachRun();

		stringDecoder.clear();
	}
	

	protected QNameContext decodeQName(DecoderChannel channel) throws IOException {
		// decode uri & local-name
		return decodeLocalName(decodeUri(channel), channel);
	}

	protected RuntimeUriContext decodeUri(DecoderChannel channel)
			throws IOException {
		int numberBitsUri = MethodsBag.getCodingLength(getNumberOfUris() + 1); // numberEntries+1
		int uriID = channel.decodeNBitUnsignedInteger(numberBitsUri);

		RuntimeUriContext uc;

		if (uriID == 0) {
			// string value was not found
			// ==> zero (0) as an n-nit unsigned integer
			// followed by uri encoded as string
			String uri = new String(channel.decodeString());
			// after encoding string value is added to table
			uc = addUri(uri);
		} else {
			// string value found
			// ==> value(i+1) is encoded as n-bit unsigned integer
			uc = getUri(--uriID);
		}

		return uc;
	}

	protected QNameContext decodeLocalName(RuntimeUriContext uc,
			DecoderChannel channel) throws IOException {

		int length = channel.decodeUnsignedInteger();

		QNameContext qnc;

		if (length > 0) {
			// string value was not found in local partition
			// ==> string literal is encoded as a String
			// with the length of the string incremented by one
			String localName = new String(channel.decodeStringOnly(length - 1));
			// After encoding the string value, it is added to the string table
			// partition and assigned the next available compact identifier.
			qnc = uc.addQNameContext(localName);
		} else {
			// string value found in local partition
			// ==> string value is represented as zero (0) encoded as an
			// Unsigned Integer
			// followed by an the compact identifier of the string value as an
			// n-bit unsigned integer
			// n is log2 m and m is the number of entries in the string table
			// partition
			int n = MethodsBag.getCodingLength(uc.getNumberOfQNames());
			int localNameID = channel.decodeNBitUnsignedInteger(n);
			qnc = uc.getQNameContext(localNameID);
		}

		return qnc;
	}

	protected String decodeQNamePrefix(RuntimeUriContext uc, DecoderChannel channel)
			throws IOException {

		String prefix = null;

		if (uc.namespaceUriID == 0) {
			// XMLConstants.DEFAULT_NS_PREFIX
			prefix = XMLConstants.NULL_NS_URI;
		} else {
			int numberOfPrefixes = uc.getNumberOfPrefixes();
			if (numberOfPrefixes > 0) {
				int id = 0;
				if (numberOfPrefixes > 1) {
					id = channel.decodeNBitUnsignedInteger(MethodsBag
							.getCodingLength(numberOfPrefixes));
				}
				// prefix = prefixes.get(id);
				prefix = uc.getPrefix(id);
			} else {
				// no previous NS mapping in charge
				// Note: should only happen for SE events where NS appears
				// afterwards
			}
		}

		return prefix;
	}

	protected String decodeNamespacePrefix(RuntimeUriContext uc,
			DecoderChannel channel) throws IOException {
		String prefix;

		int nPfx = MethodsBag.getCodingLength(uc.getNumberOfPrefixes() + 1); // n-bit
		int pfxID = channel.decodeNBitUnsignedInteger(nPfx);

		if (pfxID == 0) {
			// string value was not found
			// ==> zero (0) as an n-nit unsigned integer
			// followed by pfx encoded as string
			prefix = new String(channel.decodeString());
			// after decoding pfx value is added to table
			uc.addPrefix(prefix);
		} else {
			// string value found
			// ==> value(i+1) is encoded as n-bit unsigned integer
			prefix = uc.getPrefix(pfxID - 1);
		}

		return prefix;
	}
	
	
	

	protected final EventType decodeEventCode() throws EXIException,
			IOException {
		// 1st level
		final Grammar currentGrammar = getCurrentGrammar();
		int codeLength = fidelityOptions
				.get1stLevelEventCodeLength(currentGrammar);
		int ec = channel.decodeNBitUnsignedInteger(codeLength);

		assert (ec >= 0);

		if (ec < currentGrammar.getNumberOfEvents()) {
			// 1st level
			Production ei = currentGrammar.getProduction(ec);
			nextEvent = ei.getEvent();
			nextGrammar = ei.getNextGrammar();
			nextEventType = nextEvent.getEventType();
		} else {
			// 2nd level ?
			int ec2 = decode2ndLevelEventCode();

			if (ec2 == Constants.NOT_FOUND) {
				// 3rd level
				int ec3 = decode3rdLevelEventCode();
				nextEventType = fidelityOptions.get3rdLevelEventType(ec3);

				// un-set event
				nextEvent = null;
				nextGrammar = null;
			} else {
				nextEventType = fidelityOptions.get2ndLevelEventType(ec2,
						currentGrammar);

				if (nextEventType == EventType.ATTRIBUTE_INVALID_VALUE) {
					updateInvalidValueAttribute(ec);
				} else {
					// un-set event
					nextEvent = null;
					nextGrammar = null;
				}
			}
		}

		return nextEventType;
	}

	public String getAttributePrefix() {
		return attributePrefix;
	}

	public String getAttributeQNameAsString() {
		if (this.preservePrefix) {
			return QNameUtilities.getQualifiedName(
					attributeQNameContext.getLocalName(), this.attributePrefix);
		} else {
			return attributeQNameContext.getDefaultQNameAsString();
		}
	}

	public Value getAttributeValue() {
		return attributeValue;
	}

	protected void updateInvalidValueAttribute(int ec) throws EXIException {
		SchemaInformedGrammar sir = (SchemaInformedGrammar) getCurrentGrammar();

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
			Production ei = sir.getProduction(ec);
			nextEvent = ei.getEvent();
			nextGrammar = ei.getNextGrammar();
		} else if (ec3AT == (sir.getNumberOfDeclaredAttributes())) {
			// ANY deviated attribute (no qname present)
			nextEventType = EventType.ATTRIBUTE_ANY_INVALID_VALUE;
		} else {
			throw new EXIException(
					"Error occured while decoding deviated attribute");
		}
	}

	protected int decode2ndLevelEventCode() throws EXIException, IOException {
		final Grammar currentGrammar = getCurrentGrammar();
		// int ch2 = currentGrammar.get2ndLevelCharacteristics(fidelityOptions);
		int ch2 = fidelityOptions.get2ndLevelCharacteristics(currentGrammar);
		int level2 = channel.decodeNBitUnsignedInteger(MethodsBag
				.getCodingLength(ch2));

		int ch3= fidelityOptions.get3rdLevelCharacteristics();
		
		if (ch3 > 0) {
			return (level2 < (ch2 - 1) ? level2 : Constants.NOT_FOUND);
		} else {
			return (level2 < ch2 ? level2 : Constants.NOT_FOUND);
		}
	}

	protected int decode3rdLevelEventCode() throws EXIException, IOException {
		int ch3 = fidelityOptions.get3rdLevelCharacteristics();

		return channel.decodeNBitUnsignedInteger(MethodsBag
				.getCodingLength(ch3));
	}

	protected final void decodeStartDocumentStructure() throws EXIException {
		// update current rule
		updateCurrentRule(getCurrentGrammar().getProduction(0).getNextGrammar());
	}

	protected final void decodeEndDocumentStructure() throws EXIException,
			IOException {
		// Debug check for EXI profile stream consistency ?
		if(this.limitGrammarLearning) {
			if(this.maxBuiltInElementGrammars != -1) {
				// count grammars that evolved with other than AT(xsi:type)
				int evolvedGrs = 0;
				
				Iterator<StartElement> iterSEs = runtimeGlobalElements.values().iterator();
				while(iterSEs.hasNext()) {
					StartElement se = iterSEs.next();
					Grammar stg = se.getGrammar();
					assert(stg.getGrammarType() == GrammarType.BUILT_IN_START_TAG_CONTENT);
					Grammar ecg = stg.getElementContentGrammar();
					assert(ecg.getGrammarType() == GrammarType.BUILT_IN_ELEMENT_CONTENT);
					
					if(ecg.getNumberOfEvents() != 1) {
						// BuiltIn Element Content grammar has EE per default
						evolvedGrs++;
					} else {
						if(stg.getNumberOfEvents() > 1) {
							evolvedGrs++;
						} else if (stg.getNumberOfEvents() == 1) {
							// check for AT(xsi:type)
							if(!isBuiltInStartTagGrammarWithAtXsiTypeOnly(stg) ) {
								evolvedGrs++;
							}
						}
					}
				}
				
				if(evolvedGrs > maxBuiltInElementGrammars) {
					throw new RuntimeException("EXI profile stream does not respect parameter maxBuiltInElementGrammars. Expected " + maxBuiltInElementGrammars + " but was " + evolvedGrs);
				}	
			}
			
			// TODO how to detect ghost nodes that are never used
//			if(false && this.maxBuiltInProductions != -1) {
//				System.err.println("prods " + this.maxBuiltInProductions);
//				// count learned productions
//				int learnedProds = 0;
//				
//				Iterator<StartElement> iterSEs = runtimeGlobalElements.values().iterator();
//				while(iterSEs.hasNext()) {
//					StartElement se = iterSEs.next();
//					Grammar stg = se.getGrammar();
//					assert(stg.getGrammarType() == GrammarType.BUILT_IN_START_TAG_CONTENT);
//					Grammar ecg = stg.getElementContentGrammar();
//					assert(ecg.getGrammarType() == GrammarType.BUILT_IN_ELEMENT_CONTENT);
//					
//					int ls;
//					
//					if((ls = stg.learningStopped()) != Constants.NOT_FOUND) {
//						// learning stopped
//						learnedProds += stg.getNumberOfEvents() - ls;
//					} else {
//						if(isBuiltInStartTagGrammarWithAtXsiTypeOnly(stg) ) {
//							// AT(xsi:type) does not count
//						} else {
//							learnedProds += stg.getNumberOfEvents();
//						}
//					}
//					
//					if((ls = ecg.learningStopped()) != Constants.NOT_FOUND) {
//						// learning stopped
//						learnedProds += ecg.getNumberOfEvents() - ls;
//					} else {
//						learnedProds += ecg.getNumberOfEvents() - 1; // EE
//					}
//				}
//
//				if(learnedProds > maxBuiltInProductions) {
//					throw new RuntimeException("EXI profile stream does not respect parameter maxBuiltInProductions. Expected " + maxBuiltInProductions + " but was " + learnedProds);
//				}	
//			}
		}
		
	}

	protected final QNameContext decodeStartElementStructure()
			throws IOException {
		assert (nextEventType == EventType.START_ELEMENT);
		// StartElement
		StartElement se = ((StartElement) nextEvent);
		// push element
		pushElement(nextGrammar, se);
		// handle element prefix
		QNameContext qnc = se.getQNameContext();
		handleElementPrefix(qnc);

		return qnc;
	}

	protected final QNameContext decodeStartElementNSStructure()
			throws IOException {
		assert (nextEventType == EventType.START_ELEMENT_NS);
		// StartElementNS
		StartElementNS seNS = ((StartElementNS) nextEvent);
		// decode local-name
		RuntimeUriContext uc = getUri(seNS
				.getNamespaceUriID());
		QNameContext qnc = this.decodeLocalName(uc, channel);

		// next SE ...
		StartElement nextSE = getGlobalStartElement(qnc);

		// push element
		pushElement(nextGrammar, nextSE);
		// handle element prefix
		handleElementPrefix(qnc);

		return qnc;
	}

	protected final QNameContext decodeStartElementGenericStructure()
			throws IOException {
		assert (nextEventType == EventType.START_ELEMENT_GENERIC);
		// decode uri & local-name
		QNameContext qnc = this.decodeQName(channel);

		// next SE ...
		StartElement nextSE = getGlobalStartElement(qnc);

		// learn start-element, necessary for FragmentContent grammar
		getCurrentGrammar().learnStartElement(nextSE);
		// push element
		pushElement(nextGrammar.getElementContentGrammar(), nextSE);

		// handle element prefix
		handleElementPrefix(qnc);

		return qnc;
	}

	protected final QNameContext decodeStartElementGenericUndeclaredStructure()
			throws IOException {
		assert (nextEventType == EventType.START_ELEMENT_GENERIC_UNDECLARED);
		// decode uri & local-name
		QNameContext qnc = this.decodeQName(channel);

		// next SE ...
		StartElement nextSE = getGlobalStartElement(qnc);

		// learn start-element ?
		final Grammar currentGrammar = getCurrentGrammar();
		currentGrammar.learnStartElement(nextSE);

		// push element
		pushElement(currentGrammar.getElementContentGrammar(), nextSE);

		// handle element prefix
		handleElementPrefix(qnc);

		return qnc;
	}

	protected final ElementContext decodeEndElementStructure()
			throws EXIException, IOException {
		return popElement();
	}

	protected final ElementContext decodeEndElementUndeclaredStructure()
			throws EXIException, IOException {
		// learn end-element event ?
		getCurrentGrammar().learnEndElement();
		// pop element
		return popElement();
	}

	/*
	 * Handles and xsi:nil attributes
	 */
	protected final void decodeAttributeXsiNilStructure() throws EXIException,
			IOException {
		attributeQNameContext = getXsiNilContext();
		// handle AT prefix
		handleAttributePrefix(attributeQNameContext);

		if (preserveLexicalValues) {
			// as String
			attributeValue = typeDecoder.readValue(booleanDatatype,
					getXsiNilContext(), channel,
					stringDecoder);
		} else {
			// as Boolean
			attributeValue = booleanDatatype.readValue(null, channel,
					stringDecoder);
		}

		boolean xsiNil;

		if (attributeValue instanceof BooleanValue) {
			BooleanValue bv = (BooleanValue) attributeValue;
			xsiNil = bv.toBoolean();
		} else {
			// parse string value again (lexical value mode)
			booleanDatatype.isValid(attributeValue);
			xsiNil = booleanDatatype.getBoolean();
		}

		final Grammar currentGrammar = getCurrentGrammar();
		if (xsiNil && currentGrammar.isSchemaInformed()) {
			// jump to typeEmpty
			updateCurrentRule(((SchemaInformedFirstStartTagGrammar) currentGrammar)
					.getTypeEmpty());
		}
	}

	/*
	 * Handles and xsi:type attributes
	 */
	protected final void decodeAttributeXsiTypeStructure() throws EXIException,
			IOException {
		attributeQNameContext = getXsiTypeContext();
		// handle AT prefix
		handleAttributePrefix(attributeQNameContext);

		QNameContext qncType = null;

		// read xsi:type content
		if (this.preserveLexicalValues) {
			// assert(preservePrefix); // Note: requirement
			attributeValue = typeDecoder.readValue(BuiltIn.DEFAULT_DATATYPE,
					getXsiTypeContext(), channel,
					stringDecoder);
			String sType = attributeValue.toString();
			// extract prefix
			String qncTypePrefix = QNameUtilities.getPrefixPart(sType);

			// URI
			String qnameURI = getURI(qncTypePrefix);

			RuntimeUriContext uc = getUri(qnameURI);
			if (uc != null) {
				// local-name
				String qnameLocalName = QNameUtilities.getLocalPart(sType);
				qncType = uc.getQNameContext(qnameLocalName);
			}
		} else {
			// typed
			qncType = decodeQName(channel);
			String qncTypePrefix;
			if (preservePrefix) {
				qncTypePrefix = decodeQNamePrefix(getUri(qncType.getNamespaceUriID()), channel);
			} else {
				checkDefaultPrefixNamespaceDeclaration(qncType);
				qncTypePrefix = qncType.getDefaultPrefix();
			}
			attributeValue = new QNameValue(qncType.getNamespaceUri(),
					qncType.getLocalName(), qncTypePrefix);
		}

		// update grammar according to given xsi:type
		if (qncType != null && qncType.getTypeGrammar() != null) {
			// update current rule
			updateCurrentRule(qncType.getTypeGrammar());
		}
	}

	protected final void handleElementPrefix(QNameContext qnc)
			throws IOException {
		String pfx;
		if (preservePrefix) {
			pfx = decodeQNamePrefix(
					getUri(qnc.getNamespaceUriID()),
					channel);
			// Note: IF elementPrefix is still null it will be determined by a
			// subsequently following NS event
		} else {
			// element prefix
			checkDefaultPrefixNamespaceDeclaration(qnc);
			pfx = qnc.getDefaultPrefix();
		}
		getElementContext().setPrefix(pfx);
	}

	protected final void handleAttributePrefix(QNameContext qnc)
			throws IOException {
		if (preservePrefix) {
			attributePrefix = decodeQNamePrefix(
					getUri(qnc.getNamespaceUriID()),
					channel);
		} else {
			checkDefaultPrefixNamespaceDeclaration(qnc);
			attributePrefix = qnc.getDefaultPrefix();
		}
	}

	protected final void checkDefaultPrefixNamespaceDeclaration(QNameContext qnc) {
		assert (!preservePrefix);

		if (qnc.getNamespaceUriID() < numberOfUriContexts) {
			// schema-known grammar uris/prefixes have been declared in root
			// element
		} else {
			String uri = qnc.getNamespaceUri();
			String pfx = getPrefix(uri);

			if (pfx == null) {
				pfx = qnc.getDefaultPrefix();
				declarePrefix(pfx, uri);
			}

			assert (qnc.getDefaultPrefix().equals(pfx));
		}
	}

	protected final Datatype decodeAttributeStructure() throws EXIException,
			IOException {
		Attribute at = ((Attribute) nextEvent);
		// qname
		attributeQNameContext = at.getQNameContext();
		// handle attribute prefix
		handleAttributePrefix(attributeQNameContext);

		// update current rule
		updateCurrentRule(nextGrammar);

		return at.getDatatype();
	}

	protected final void decodeAttributeNSStructure() throws EXIException,
			IOException {
		// AttributeEventNS
		AttributeNS atNS = ((AttributeNS) nextEvent);
		RuntimeUriContext uc = getUri(atNS
				.getNamespaceUriID());
		attributeQNameContext = decodeLocalName(uc, channel);

		// handle attribute prefix
		handleAttributePrefix(attributeQNameContext);
		// update current rule
		updateCurrentRule(nextGrammar);
	}

	protected final void decodeAttributeAnyInvalidValueStructure()
			throws EXIException, IOException {
		decodeAttributeGenericStructureOnly();
	}

	protected final void decodeAttributeGenericStructure() throws EXIException,
			IOException {
		// decode structure
		decodeAttributeGenericStructureOnly();

		// update current rule
		updateCurrentRule(nextGrammar);
	}

	protected final void decodeAttributeGenericUndeclaredStructure()
			throws EXIException, IOException {
		decodeAttributeGenericStructureOnly();
		getCurrentGrammar()
				.learnAttribute(new Attribute(attributeQNameContext));
	}

	private final void decodeAttributeGenericStructureOnly()
			throws EXIException, IOException {
		// decode uri & local-name
		this.attributeQNameContext = decodeQName(channel);

		// handle attribute prefix
		handleAttributePrefix(attributeQNameContext);
	}

	protected final Datatype decodeCharactersStructure() throws EXIException {
		assert (nextEventType == EventType.CHARACTERS);
		// update current rule
		updateCurrentRule(nextGrammar);
		return ((Characters) nextEvent).getDatatype();
	}

	protected final void decodeCharactersGenericStructure() throws EXIException {
		assert (nextEventType == EventType.CHARACTERS_GENERIC);
		// update current rule
		updateCurrentRule(nextGrammar);
	}

	protected final void decodeCharactersGenericUndeclaredStructure()
			throws EXIException {
		assert (nextEventType == EventType.CHARACTERS_GENERIC_UNDECLARED);
		// learn character event ?
		final Grammar currentGrammar = getCurrentGrammar();
		currentGrammar.learnCharacters();
		// update current rule
		updateCurrentRule(currentGrammar.getElementContentGrammar());
	}

	protected final NamespaceDeclaration decodeNamespaceDeclarationStructure()
			throws EXIException, IOException {
		// prefix mapping
		RuntimeUriContext euc = decodeUri(channel);
		String nsPrefix = decodeNamespacePrefix(euc, channel);

		boolean local_element_ns = channel.decodeBoolean();
		if (local_element_ns) {
			getElementContext().setPrefix(nsPrefix);
		}
		// NS
		NamespaceDeclaration nsDecl = new NamespaceDeclaration(
				euc.getNamespaceUri(), nsPrefix);
		declarePrefix(nsDecl);
		return nsDecl;
	}

	protected final char[] decodeEntityReferenceStructure()
			throws EXIException, IOException {
		// decode name AS string
		char[] er = channel.decodeString();
		// update current rule
		updateCurrentRule(getCurrentGrammar().getElementContentGrammar());
		return er;
	}

	protected final char[] decodeCommentStructure() throws EXIException,
			IOException {
		char[] comment = channel.decodeString();
		// update current rule
		updateCurrentRule(getCurrentGrammar().getElementContentGrammar());
		return comment;
	}

	protected final ProcessingInstruction decodeProcessingInstructionStructure()
			throws EXIException, IOException {
		// target & data
		String piTarget = new String(channel.decodeString());
		String piData = new String(channel.decodeString());
		// update current rule
		updateCurrentRule(getCurrentGrammar().getElementContentGrammar());
		return new ProcessingInstruction(piTarget, piData);
	}

	protected final DocType decodeDocTypeStructure() throws EXIException,
			IOException {
		// decode name, public, system, text AS string
		char[] name = channel.decodeString();
		char[] publicID = channel.decodeString();
		char[] systemID = channel.decodeString();
		char[] text = channel.decodeString();
		return new DocType(name, publicID, systemID, text);
	}

	/* ================================= */

	public void decodeStartSelfContainedFragment() throws EXIException,
			IOException {
		throw new RuntimeException("[EXI] SelfContained");
	}

}
