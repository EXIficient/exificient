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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.CodingMode;
import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.core.container.DocType;
import com.siemens.ct.exi.core.container.NamespaceDeclaration;
import com.siemens.ct.exi.core.container.PreReadValue;
import com.siemens.ct.exi.core.container.ProcessingInstruction;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.event.Attribute;
import com.siemens.ct.exi.grammar.event.EventType;
import com.siemens.ct.exi.io.channel.ByteDecoderChannel;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.types.BuiltIn;
import com.siemens.ct.exi.values.QNameValue;
import com.siemens.ct.exi.values.Value;

/**
 * EXI decoder for (pre-)compression streams.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.5
 */

public class EXIBodyDecoderReordered extends AbstractEXIBodyDecoder {
	// store appearing event-types in right order
	protected List<EventType> eventTypes;
	protected int eventTypeIndex;

	// elements and end elements
	protected List<ElementEntry> elementEntries;
	protected int elementEntryIndex;	
	// attributes and character value entries 
	protected List<QNameEntry> qnameEntries;
	protected int qnameEntryIndex;
	// docTypes
	protected List<DocType> docTypeEntries;
	protected int docTypeEntryIndex;
	// entity references
	protected List<char[]> entityReferences;
	protected int entityReferenceIndex;
	// comments
	protected List<char[]> comments;
	protected int commentIndex;
	// namespaces
	protected List<NamespaceDeclaration> nsEntries;
	protected int nsEntryIndex;
	// processing instructions
	protected List<ProcessingInstruction> processingEntries;
	protected int processingEntryIndex;
	
	String elementQNameAsString;
	List<String> endElementQNames;

	// count value items
	protected int blockValues;

	boolean stillNoEndOfDocument = true;
	
	ElementContext lastBlockElementContext;

	// store value events (qnames) in right order
	// plus necessary information to reconstruct value channels
	protected List<QName> valueQNames;
	protected Map<QName, Integer> occurrences;
	protected Map<QName, List<Datatype>> dataTypes;

	// Channel and Compression stuff
	protected CodingMode codingMode;

	// content values
	protected Map<QName, PreReadValue> contentValues;

	protected List<Value> xsiValues;
	protected int xsiValueIndex;
	protected List<String> xsiPrefixes;
	protected int xsiPrefixIndex;

	// deflate stuff
	protected InputStream resettableInputStream;
	protected InflaterInputStream recentInflaterInputStream;
	protected long bytesRead;
	protected Inflater inflater;

	protected boolean firstChannel;
	
	protected InputStream is;

	public EXIBodyDecoderReordered(EXIFactory exiFactory) throws EXIException {
		super(exiFactory);

		// events
		eventTypes = new ArrayList<EventType>();
		// element entries
		elementEntries = new ArrayList<ElementEntry>();
		// qname entries
		qnameEntries = new ArrayList<QNameEntry>();
		// misc
		docTypeEntries = new ArrayList<DocType>();
		entityReferences = new ArrayList<char[]>();
		comments = new ArrayList<char[]>();
		nsEntries = new ArrayList<NamespaceDeclaration>();
		processingEntries = new ArrayList<ProcessingInstruction>();
		// value events
		valueQNames = new ArrayList<QName>();
		occurrences = new HashMap<QName, Integer>();
		dataTypes = new HashMap<QName, List<Datatype>>();

		// content values
		contentValues = new HashMap<QName, PreReadValue>();

		xsiValues = new ArrayList<Value>();
		xsiPrefixes = new ArrayList<String>();
		
		endElementQNames = new ArrayList<String>();

		codingMode = exiFactory.getCodingMode();
	}

	@Override
	protected void initForEachRun() throws EXIException, IOException {

		super.initForEachRun();

		// next event
		nextEvent = null;
		nextEventType = EventType.START_DOCUMENT;

		// element entries
		elementEntries.clear();
		elementEntryIndex = 0;		
		// qname entries
		qnameEntries.clear();
		qnameEntryIndex = 0;
		
		// misc
		docTypeEntries.clear();
		docTypeEntryIndex = 0;
		entityReferences.clear();
		entityReferenceIndex = 0;
		comments.clear();
		commentIndex = 0;
		nsEntries.clear();
		nsEntryIndex = 0;
		processingEntries.clear();
		processingEntryIndex = 0;
		
		endElementQNames.clear();
		
		stillNoEndOfDocument = true;
		lastBlockElementContext = null;
		
		// initialize block
		initBlock();

		// pre-read first block structure and afterwards pre-read content (values)
		preReadBlockStructure();
		preReadBlockContent();
	}
	
	protected void initBlock() {
		// count value items
		blockValues = 0;

		// events
		eventTypes.clear();
		eventTypeIndex = 0;

		// contains value events (qnames) in right order
		// plus necessary information to reconstruct value channels
		valueQNames.clear();
		occurrences.clear();
		dataTypes.clear();

		// content values
		contentValues.clear();
		xsiValues.clear();
		xsiValueIndex = 0;
		xsiPrefixes.clear();
		xsiPrefixIndex = 0;
	}

	// @Override
	public void setInputStream(InputStream is)
			throws EXIException, IOException {
		
		// buffer stream if not already
		// TODO is there a *nice* way to detect whether a stream is buffered
		// already
//		if (!(is instanceof BufferedInputStream)) {
//			is = new BufferedInputStream(is);
//		} else {
			this.is = is;
//		}
		
		firstChannel = true;
		 channel = getNextChannel();
		// setInputChannel();
		 
		 initForEachRun();
	}
	
	public void setInputChannel(DecoderChannel channel)
	throws EXIException, IOException {
		throw new RuntimeException("[EXI] Reorderd EXI Body decoder needs to be set via setInputStream(...)");
//		this.channel = channel;
//		firstChannel = true;
//
//		initForEachRun();
	}

	public DecoderChannel getNextChannel() throws IOException {
		// System.out.println("getNextChannel()");
		
		if (codingMode == CodingMode.COMPRESSION) {
			if (firstChannel) {
				bytesRead = 0;
				resettableInputStream = new BufferedInputStream(is);
				resettableInputStream.mark(Integer.MAX_VALUE);
				// initialize inflater
				inflater = new Inflater(true);
				firstChannel = false;
			} else {
				if (!inflater.finished()) {
					// TODO [Warning] Inflater not finished, what is the reason
					// for that ?
					// Something todo with
					// http://forums.sun.com/thread.jspa?threadID=713598 ???

					while (!inflater.finished()) {
						recentInflaterInputStream.read();
					}
				}

				// update new byte position
				bytesRead += inflater.getBytesRead();

				// reset byte position
				resettableInputStream.reset();
				long skipped = resettableInputStream.skip(bytesRead);
				// handle the case where fewer bytes were skipped than
				// requested 
				if (skipped != bytesRead) {
					do {
						long skippedLoop = resettableInputStream.skip(bytesRead
								- skipped);
						if (skippedLoop <= 0) {
							// NOTE: If n is negative, no bytes are skipped
							throw new IOException(
									"[EXI] Byte skipping impossible on given input stream");
						}
						skipped += skippedLoop;
						assert (skipped <= bytesRead);
					} while (skipped < bytesRead);
				}

				// reset inflater
				inflater.reset();
			}

			recentInflaterInputStream = new InflaterInputStream(
					resettableInputStream, inflater);
			return new ByteDecoderChannel(recentInflaterInputStream);
		} else {
			assert (codingMode == CodingMode.PRE_COMPRESSION);
			if (firstChannel) {
				// create once a decoder channel
				channel = new ByteDecoderChannel(this.is);
				firstChannel = false;
			}
			// there is just one channel
			return channel;
		}
	}

	protected void handleSpecialAttributeCases() throws EXIException,
			IOException {
		if (XSI_TYPE.equals(attributeQName)) {
			// xsi:type
			eventTypes.set(eventTypes.size() - 1, EventType.ATTRIBUTE_XSI_TYPE);
			// value content
			decodeAttributeXsiTypeStructure();			
			xsiValues.add(attributeValue);
			assert (attributeValue instanceof QNameValue);
//			attributePrefix = qnameDatatype.decodeQNamePrefix(XSI_TYPE, channel);
			// TODO prefix
			xsiPrefixes.add(attributePrefix);
//			xsiPrefixes.add(namespaces.getPrefix(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI));
		} else if (XSI_NIL.equals(attributeQName)
				&& currentRule.isSchemaInformed()) {
			// xsi:nil
			eventTypes.set(eventTypes.size() - 1, EventType.ATTRIBUTE_XSI_NIL);
			// value content
			decodeAttributeXsiNilStructure();
			xsiValues.add(attributeValue);
			// TODO prefix
			xsiPrefixes.add(attributePrefix);
			// xsiPrefixes.add(namespaces.getPrefix(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI));
		} else {
			// global attribute or default datatype
			Attribute globalAT;
			Datatype dt = BuiltIn.DEFAULT_DATATYPE;
			if ( currentRule.isSchemaInformed() && ( globalAT = grammar.getGlobalAttribute(attributeQName) ) != null ) {
				dt = globalAT.getDatatype();
			}
			qnameEntries.add(new QNameEntry(attributeQName, attributePrefix));
			incrementValues(attributeQName, dt);
		}
	}
	
	
	protected void preReadBlockStructure() throws EXIException, IOException {

		boolean stillBlockReadable = true;
		boolean deferredStartElement = false;
		
		if (lastBlockElementContext != null) {
			this.elementContext = lastBlockElementContext;
		}
		
		QName startBlockElementQName = this.elementQName;
		String startBlockElementPrefix = this.elementPrefix;
		
		while (stillNoEndOfDocument && stillBlockReadable) {
			
			if (deferredStartElement) {
				switch (nextEventType) {
				/* ATTRIBUTE EVENTS that come after NS */
				case ATTRIBUTE:
				case ATTRIBUTE_INVALID_VALUE:
				case ATTRIBUTE_ANY_INVALID_VALUE:
				case ATTRIBUTE_GENERIC:
				case ATTRIBUTE_NS:
				case ATTRIBUTE_GENERIC_UNDECLARED:
				/* ELEMENT CONTENT EVENTS */
				case START_ELEMENT:
				case START_ELEMENT_NS:
				case START_ELEMENT_GENERIC:
				case START_ELEMENT_GENERIC_UNDECLARED:
				case END_ELEMENT:
				case END_ELEMENT_UNDECLARED:
				case CHARACTERS:
				case CHARACTERS_GENERIC:
				case CHARACTERS_GENERIC_UNDECLARED:
				case DOC_TYPE:
				case ENTITY_REFERENCE:
				case COMMENT:
				case PROCESSING_INSTRUCTION:
					// No Attribute or NS event --> start deferred element with prefix
					if (elementPrefix == null) {
						elementPrefix = checkPrefixMapping(elementContext.qname.getNamespaceURI());	
					}
					elementEntries.add(new ElementEntry(elementContext, elementPrefix));
					deferredStartElement = false;
				}
			}			
			
			// add event to array list
			eventTypes.add(nextEventType);

			switch (nextEventType) {
			case START_DOCUMENT:
				super.decodeStartDocument();
				break;
			case START_ELEMENT:
				super.decodeStartElement();
				deferredStartElement = true;
				break;
			case START_ELEMENT_NS:
				super.decodeStartElementNS();
				deferredStartElement = true;
				break;
			case START_ELEMENT_GENERIC:
				super.decodeStartElementGeneric();
				deferredStartElement = true;
				break;
			case START_ELEMENT_GENERIC_UNDECLARED:
				super.decodeStartElementGenericUndeclared();
				deferredStartElement = true;
				break;
			case NAMESPACE_DECLARATION:
				nsEntries.add(super.decodeNamespaceDeclaration());
				break;
			case ATTRIBUTE_XSI_TYPE:
				attributePrefix = qnameDatatype.decodeQNamePrefix(XSI_TYPE,
						channel);
				xsiPrefixes.add(attributePrefix);
				decodeAttributeXsiTypeStructure();
				xsiValues.add(attributeValue);
				break;
			case ATTRIBUTE_XSI_NIL:
				attributePrefix = qnameDatatype.decodeQNamePrefix(XSI_NIL,
						channel);
				xsiPrefixes.add(attributePrefix);
				decodeAttributeXsiNilStructure();
				xsiValues.add(attributeValue);
				break;
			case ATTRIBUTE:
				Datatype dtAT = decodeAttributeStructure();
				qnameEntries
						.add(new QNameEntry(attributeQName, attributePrefix));
				incrementValues(attributeQName, dtAT);
				break;
			case ATTRIBUTE_INVALID_VALUE:
				decodeAttributeStructure();
				qnameEntries
						.add(new QNameEntry(attributeQName, attributePrefix));
				incrementValues(attributeQName, BuiltIn.DEFAULT_DATATYPE);
				break;
			case ATTRIBUTE_ANY_INVALID_VALUE:
				decodeAttributeAnyInvalidValueStructure();
				qnameEntries
						.add(new QNameEntry(attributeQName, attributePrefix));
				incrementValues(attributeQName, BuiltIn.DEFAULT_DATATYPE);
				break;
			case ATTRIBUTE_NS:
				decodeAttributeNSStructure();
				// special cases
				handleSpecialAttributeCases();
				break;
			case ATTRIBUTE_GENERIC:
				// structure
				decodeAttributeGenericStructure();
				// special cases
				handleSpecialAttributeCases();
				break;
			case ATTRIBUTE_GENERIC_UNDECLARED:
				// structure
				decodeAttributeGenericUndeclaredStructure();
				// special cases
				handleSpecialAttributeCases();
				break;
			case CHARACTERS:
				incrementValues(elementContext.qname,decodeCharactersStructureOnly());
				// incrementValues(elementQName,decodeCharactersStructureOnly());
				qnameEntries.add(new QNameEntry(elementContext.qname, null));
				break;
			case CHARACTERS_GENERIC:
				decodeCharactersGenericStructureOnly();
				incrementValues(elementContext.qname, BuiltIn.DEFAULT_DATATYPE);
				qnameEntries.add(new QNameEntry(elementContext.qname, null));
				break;
			case CHARACTERS_GENERIC_UNDECLARED:
				decodeCharactersGenericUndeclaredStructureOnly();
				incrementValues(elementContext.qname, BuiltIn.DEFAULT_DATATYPE);
				qnameEntries.add(new QNameEntry(elementContext.qname, null));
				break;
			case END_ELEMENT:
				if (elementPrefix == null) {
					elementPrefix = checkPrefixMapping(elementContext.qname.getNamespaceURI());	
				}
				elementEntries.add(new ElementEntry(elementContext, elementPrefix));
				super.decodeEndElement();
				break;
			case END_ELEMENT_UNDECLARED:
				if (elementPrefix == null) {
					elementPrefix = checkPrefixMapping(elementContext.qname.getNamespaceURI());	
				}
				elementEntries.add(new ElementEntry(elementContext, elementPrefix));
				super.decodeEndElementUndeclared();
				break;
			case END_DOCUMENT:
				super.decodeEndDocument();
				stillNoEndOfDocument = false;
				continue;
			case DOC_TYPE:
				docTypeEntries.add(super.decodeDocType());
				break;
			case ENTITY_REFERENCE:
				entityReferences.add(super.decodeEntityReference());
				break;
			case COMMENT:
				comments.add(super.decodeComment());
				break;
			case PROCESSING_INSTRUCTION:
				processingEntries.add(super.decodeProcessingInstruction());
				break;
			default:
				throw new RuntimeException("Unknown Event " + nextEventType);
			}
			
			//	still events in this block ?
			if (blockValues == exiFactory.getBlockSize()) {
				// NO events any more
				stillBlockReadable = false;
				
				lastBlockElementContext = this.elementContext;
				
				// Note: NS mapping has be done before opening a new block
				assert(deferredStartElement == false);
			} else {
				// decode next EventCode
				decodeEventCode();		
			}
			
		}
		
		// System.out.println("No more blockvalues " +  blockValues + " after " + qnameEntries.get(qnameEntries.size()-1) );
		this.elementContext = lastBlockElementContext;
		this.elementQName = startBlockElementQName;
		this.elementPrefix = startBlockElementPrefix;

	}

	protected void preReadBlockContent() throws EXIException {
		try {
			if (blockValues <= Constants.MAX_NUMBER_OF_VALUES) {
				// single compressed stream (included structure)
				for (int i = 0; i < valueQNames.size(); i++) {
					QName channelContext = valueQNames.get(i);
					int occs = occurrences.get(channelContext);
					List<Datatype> datatypes = dataTypes.get(channelContext);
					setContentValues(channel, channelContext, occs, datatypes);
				}
			} else {
				// first stream structure (already read)

				// second stream (if any), values <= 100
				if (areThereAnyLessEqualThan100(valueQNames, occurrences)) {
					DecoderChannel bdcLessEqual100 = getNextChannel();
					for (int i = 0; i < valueQNames.size(); i++) {
						QName channelContext = valueQNames.get(i);
						int occs = occurrences.get(channelContext);
						if (occs <= Constants.MAX_NUMBER_OF_VALUES) {
							List<Datatype> datatypes = dataTypes
									.get(channelContext);
							setContentValues(bdcLessEqual100, channelContext,
									occs, datatypes);
						}
					}
				}

				// proper stream for greater100
				for (int i = 0; i < valueQNames.size(); i++) {
					QName channelContext = valueQNames.get(i);
					int occs = occurrences.get(channelContext);
					if (occs > Constants.MAX_NUMBER_OF_VALUES) {
						DecoderChannel bdcGreater100 = getNextChannel();
						List<Datatype> datatypes = dataTypes
								.get(channelContext);
						setContentValues(bdcGreater100, channelContext, occs,
								datatypes);
					}
				}
			}
			
			// System.out.println("Block read finished");
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	protected static boolean areThereAnyLessEqualThan100(List<QName> qnames,
			Map<QName, Integer> occurrences) {
		for (int i = 0; i < qnames.size(); i++) {
			if (occurrences.get(qnames.get(i)) <= Constants.MAX_NUMBER_OF_VALUES) {
				return true;
			}
		}
		return false;
	}

	protected void setContentValues(DecoderChannel bdc, QName channelContext,
			int occs, List<Datatype> datatypes) throws IOException {

		assert (datatypes.size() == occs);
		Value[] decodedValues = new Value[occs];

		for (int k = 0; k < occs; k++) {
			Datatype dt = datatypes.get(k);
			decodedValues[k] = typeDecoder.readValue(dt, channelContext, bdc);
		}

		// set content value
		contentValues.put(channelContext, new PreReadValue(
				decodedValues));
	}
	
	private final void setupNewBlock() throws IOException, EXIException {
		// System.out.println("TODO 0, read next block");
		// System.out.println("Next Value " + blockValues);
		
		initBlock();
		
		channel = getNextChannel();
		
		// decode next EventCode
		decodeEventCode();	
		
		preReadBlockStructure();
		preReadBlockContent();
	}
	
	protected Value getNextContentValue(QName qname) throws EXIException, IOException {
//		if ( stillNoEndOfDocument &&  blockValues == 0 ) {
//			// read next block
//			setupNewBlock();
//		}
		blockValues--;
		
		assert (contentValues.get(qname) != null);
		Value v = contentValues.get(qname).getNextContantValue();

//		if ( stillNoEndOfDocument &&  --blockValues == 0 ) {
//			// read next block
//			setupNewBlock();
//		}
		
		return v;
	}

//	public boolean hasNext() throws IOException, EXIException {
//		if (stillNoEndOfDocument && blockValues == 0) {
//			// read next block
//			setupNewBlock();
//		}
//		return (stillNoEndOfDocument || eventTypes.size() > eventTypeIndex);
//	}

	public EventType next() throws EXIException, IOException {
		// return (nextEventType = eventTypes.get(eventTypeIndex++));
		if (stillNoEndOfDocument && blockValues == 0) {
			// read next block
			setupNewBlock();
		}
		if (stillNoEndOfDocument || eventTypes.size() > eventTypeIndex ) {
			return eventTypes.get(eventTypeIndex++);
		} else {
			return null;
		}
		// return (stillNoEndOfDocument || eventTypes.size() > eventTypeIndex);
	}

	protected void incrementValues(QName valueContext, Datatype datatype) {
		blockValues++;

		if (valueQNames.contains(valueContext)) {
			occurrences.put(valueContext, occurrences.get(valueContext) + 1);
		} else {
			// new
			occurrences.put(valueContext, 1);
			dataTypes.put(valueContext, new ArrayList<Datatype>());
			valueQNames.add(valueContext);
		}

		dataTypes.get(valueContext).add(datatype);
	}

	@Override
	public void decodeStartDocument() {
		// re-set namespaces
		initPrefixes();
	}
	
	@Override
	protected void initPrefixes() {
		super.initPrefixes();
		createdPfxCnt = 1;
	}
	
	@Override
	public QName decodeStartElement() throws IOException, EXIException {
		ElementEntry se = elementEntries.get(elementEntryIndex++);
		this.elementContext = se.context;
		this.elementQName = elementContext.qname;
		this.elementPrefix = se.prefix;
		return this.elementQName;
	}

	@Override
	public QName decodeStartElementNS() throws IOException, EXIException {
		return decodeStartElement();
	}

	@Override
	public QName decodeStartElementGeneric() throws IOException, EXIException {
		return decodeStartElement();
	}

	@Override
	public QName decodeStartElementGenericUndeclared() throws IOException, EXIException {
		return decodeStartElement();
	}
	

	@Override
	public String getStartElementQNameAsString() {
		String sqname = getQualifiedName(elementQName, elementPrefix);
		
//		String qname = super.getStartElementQNameAsString();
		endElementQNames.add(sqname);
		return sqname;
//		// return null;
//		return elementQNameAsString;
	}
	

	@Override
	public QName decodeEndElement() throws EXIException {
		ElementEntry ee = elementEntries.get(elementEntryIndex++);
		this.elementContext = ee.context;
		this.elementQName = elementContext.qname;
		
		// NS context
		undeclarePrefixes();
		
		return this.elementQName;
	}

	@Override
	public QName decodeEndElementUndeclared() throws EXIException {
		return decodeEndElement();
	}
	
	
	@Override
	public String getEndElementQNameAsString() {
		// return elementQNameAsString;
		// remove last item
		return endElementQNames.remove(endElementQNames.size()-1);
	}

	public NamespaceDeclaration decodeNamespaceDeclaration() throws EXIException {
		NamespaceDeclaration ns = nsEntries.get(nsEntryIndex++);
		// NS
		this.declarePrefix(ns.prefix, ns.namespaceURI);
		return ns;
	}

	@Override
	public QName decodeAttributeXsiNil() throws EXIException, IOException {
		attributeQName = XSI_NIL;
		attributePrefix = xsiPrefixes.get(xsiPrefixIndex++);
		attributeValue = xsiValues.get(xsiValueIndex++);
		return attributeQName;
	}

	@Override
	public QName decodeAttributeXsiType() throws EXIException, IOException {
		attributeQName = XSI_TYPE;
		attributePrefix = xsiPrefixes.get(xsiPrefixIndex++);
		attributeValue = xsiValues.get(xsiValueIndex++);
		return attributeQName;
	}

	@Override
	public QName decodeAttribute() throws EXIException, IOException {
		QNameEntry at = qnameEntries.get(qnameEntryIndex++);

		attributeQName = at.qname;
		attributePrefix = at.prefix;
		
		if (attributePrefix == null) {
			attributePrefix = checkPrefixMapping(attributeQName.getNamespaceURI());	
		}

		attributeValue = getNextContentValue(attributeQName);
		return attributeQName;
	}

	@Override
	public QName decodeAttributeNS() throws EXIException, IOException {
		return decodeAttribute();
	}

	@Override
	public QName decodeAttributeInvalidValue() throws EXIException, IOException {
		return decodeAttribute();
	}

	@Override
	public QName decodeAttributeAnyInvalidValue() throws EXIException,
			IOException {
		return decodeAttribute();
	}

	@Override
	public QName decodeAttributeGeneric() throws EXIException, IOException {
		return decodeAttribute();
	}

	@Override
	public QName decodeAttributeGenericUndeclared() throws EXIException,
			IOException {
		return decodeAttribute();
	}

	@Override
	public Value decodeCharacters() throws EXIException, IOException {
		QNameEntry ch = qnameEntries.get(qnameEntryIndex++);
		return getNextContentValue(ch.qname);
	}

	@Override
	public Value decodeCharactersGeneric() throws EXIException, IOException {
		return decodeCharacters();
	}

	@Override
	public Value decodeCharactersGenericUndeclared() throws EXIException,
			IOException {
		return decodeCharacters();
	}

	public void decodeEndDocument() throws EXIException {
	}

	public DocType decodeDocType() throws EXIException {
		return docTypeEntries.get(docTypeEntryIndex++);
	}

	public char[] decodeEntityReference() throws EXIException {
		return entityReferences.get(entityReferenceIndex++);
	}

	public char[] decodeComment() throws EXIException {
		return comments.get(commentIndex++);
	}

	public ProcessingInstruction decodeProcessingInstruction() throws EXIException {
		return processingEntries.get(processingEntryIndex++);
	}

	/*
	 * Pre-Read Entries
	 */
	static class ElementEntry {
		final ElementContext context;
		final String prefix;
		
		String sqname;
		public ElementEntry(ElementContext context, String prefix) {
			this.context = context;
			this.prefix = prefix;
		}
	}
	static class QNameEntry {
		final QName qname;
		final String prefix;
		
		String sqname;

		public QNameEntry(QName qname, String prefix) {
			this.qname = qname;
			this.prefix = prefix;
		}
	}

}
