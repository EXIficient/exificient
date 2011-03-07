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
 * @version 0.6
 */

public class EXIBodyDecoderReordered extends AbstractEXIBodyDecoder {
	// store appearing event-types in right order
	protected List<EventType> eventTypes;
	protected int eventTypeIndex;

	// elements and end elements
	// protected List<ElementEntry> elementEntries;
	protected List<ElementContext> startElementEntries;
	protected int startElementEntryIndex;
	protected List<EndElementEntry> endElementEntries;
	protected int endElementEntryIndex;
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
		// elementEntries = new ArrayList<ElementEntry>();
		startElementEntries = new ArrayList<ElementContext>();
		endElementEntries = new ArrayList<EndElementEntry>();
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
		startElementEntries.clear();
		startElementEntryIndex = 0;
		endElementEntries.clear();
		endElementEntryIndex = 0;
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

		// pre-read first block structure and afterwards pre-read content
		// (values)
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
	public void setInputStream(InputStream is) throws EXIException, IOException {
		this.is = is;

		firstChannel = true;
		channel = getNextChannel();

		initForEachRun();
	}

	public void setInputChannel(DecoderChannel channel) throws EXIException,
			IOException {
		throw new RuntimeException(
				"[EXI] Reorderd EXI Body decoder needs to be set via setInputStream(...)");
	}

	public DecoderChannel getNextChannel() throws IOException {
		
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

	protected void updateAttributeToXsiType() throws EXIException, IOException {
		eventTypes.set(eventTypes.size() - 1, EventType.ATTRIBUTE_XSI_TYPE);
		// value content
		decodeAttributeXsiTypeStructure();
		xsiValues.add(attributeValue);
		assert (attributeValue instanceof QNameValue);
		// prefix
		xsiPrefixes.add(attributePrefix);	
	}

	protected void handleSpecialAttributeCases() throws EXIException,
			IOException {
		if (XSI_TYPE.equals(attributeQName)) {
			// xsi:type
			updateAttributeToXsiType();
		} else if (XSI_NIL.equals(attributeQName)
				&& currentRule.isSchemaInformed()) {
			// xsi:nil
			eventTypes.set(eventTypes.size() - 1, EventType.ATTRIBUTE_XSI_NIL);
			// value content
			decodeAttributeXsiNilStructure();
			xsiValues.add(attributeValue);
			// prefix
			xsiPrefixes.add(attributePrefix);
		} else {
			// global attribute or default datatype
			Attribute globalAT;
			Datatype dt = BuiltIn.DEFAULT_DATATYPE;
			if (currentRule.isSchemaInformed()
					&& (globalAT = grammar.getGlobalAttribute(attributeQName)) != null) {
				dt = globalAT.getDatatype();
			}
			// qnameEntries.add(new QNameEntry(attributeQName, attributePrefix));
			addQNameEntry(new QNameEntry(attributeQName, attributePrefix));
			incrementValues(attributeQName, dt);
		}
	}

	protected void preReadBlockStructure() throws EXIException, IOException {

		boolean stillBlockReadable = true;
		boolean deferredStartElement = false;

		ElementContext eeBefore;
		
		if (lastBlockElementContext != null) {
			this.elementContext = lastBlockElementContext;
		}

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
					// No Attribute or NS event --> start deferred element with
					// prefix
					startElementEntries.add(this.elementContext);
					deferredStartElement = false;
				}
			}

			// add event to array list
			eventTypes.add(nextEventType);

			switch (nextEventType) {
			case START_DOCUMENT:
				decodeStartDocumentStructure();
				break;
			case START_ELEMENT:
				decodeStartElementStructure();
				deferredStartElement = true;
				break;
			case START_ELEMENT_NS:
				decodeStartElementNSStructure();
				deferredStartElement = true;
				break;
			case START_ELEMENT_GENERIC:
				decodeStartElementGenericStructure();
				deferredStartElement = true;
				break;
			case START_ELEMENT_GENERIC_UNDECLARED:
				decodeStartElementGenericUndeclaredStructure();
				deferredStartElement = true;
				break;
			case NAMESPACE_DECLARATION:
				nsEntries.add(decodeNamespaceDeclarationStructure());
				break;
			case ATTRIBUTE_XSI_TYPE:
				decodeAttributeXsiTypeStructure();
				xsiPrefixes.add(attributePrefix);
				xsiValues.add(attributeValue);
				break;
			case ATTRIBUTE_XSI_NIL:
				decodeAttributeXsiNilStructure();
				xsiPrefixes.add(attributePrefix);
				xsiValues.add(attributeValue);
				break;
			case ATTRIBUTE:
				Datatype dtAT = decodeAttributeStructure();
				if (attributeQName.equals(XSI_TYPE)) {
					updateAttributeToXsiType();
				} else {
					addQNameEntry(new QNameEntry(attributeQName, attributePrefix));
					incrementValues(attributeQName, dtAT);
				}
				break;
			case ATTRIBUTE_INVALID_VALUE:
				decodeAttributeStructure();
				addQNameEntry(new QNameEntry(attributeQName, attributePrefix));
				incrementValues(attributeQName, BuiltIn.DEFAULT_DATATYPE);
				break;
			case ATTRIBUTE_ANY_INVALID_VALUE:
				decodeAttributeAnyInvalidValueStructure();
				addQNameEntry(new QNameEntry(attributeQName, attributePrefix));
				incrementValues(attributeQName, BuiltIn.DEFAULT_DATATYPE);
				break;
			case ATTRIBUTE_NS:
				decodeAttributeNSStructure();
				// special cases
				handleSpecialAttributeCases();
				break;
			case ATTRIBUTE_GENERIC:
				decodeAttributeGenericStructure();
				// special cases
				handleSpecialAttributeCases();
				break;
			case ATTRIBUTE_GENERIC_UNDECLARED:
				decodeAttributeGenericUndeclaredStructure();
				// special cases
				handleSpecialAttributeCases();
				break;
			case CHARACTERS:
				incrementValues(elementContext.qname,
						decodeCharactersStructure());
				addQNameEntry(new QNameEntry(elementContext.qname, null));
				break;
			case CHARACTERS_GENERIC:
				decodeCharactersGenericStructure();
				incrementValues(elementContext.qname, BuiltIn.DEFAULT_DATATYPE);
				addQNameEntry(new QNameEntry(elementContext.qname, null));
				break;
			case CHARACTERS_GENERIC_UNDECLARED:
				decodeCharactersGenericUndeclaredStructure();
				incrementValues(elementContext.qname, BuiltIn.DEFAULT_DATATYPE);
				addQNameEntry(new QNameEntry(elementContext.qname, null));
				break;
			case END_ELEMENT:
				eeBefore = decodeEndElementStructure();
				endElementEntries.add(new EndElementEntry(eeBefore, this.elementContext));
				break;
			case END_ELEMENT_UNDECLARED:
				eeBefore = decodeEndElementUndeclaredStructure();
				endElementEntries.add(new EndElementEntry(eeBefore, this.elementContext));
				break;
			case END_DOCUMENT:
				decodeEndDocumentStructure();
				stillNoEndOfDocument = false;
				continue;
			case DOC_TYPE:
				docTypeEntries.add(decodeDocTypeStructure());
				break;
			case ENTITY_REFERENCE:
				entityReferences.add(decodeEntityReferenceStructure());
				break;
			case COMMENT:
				comments.add(decodeCommentStructure());
				break;
			case PROCESSING_INSTRUCTION:
				processingEntries.add(decodeProcessingInstructionStructure());
				break;
			default:
				throw new RuntimeException("Unknown Event " + nextEventType);
			}

			// still events in this block ?
			if (blockValues == exiFactory.getBlockSize()) {
				// NO events any more
				stillBlockReadable = false;

				lastBlockElementContext = this.elementContext;

				// Note: NS mapping has be done before opening a new block
				assert (deferredStartElement == false);
			} else {
				// decode next EventCode
				decodeEventCode();
			}

		}

//		 System.out.println("No more blockvalues " + blockValues + " after " +
//		 qnameEntries.get(qnameEntries.size()-1) );
		this.elementContext = lastBlockElementContext;

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
		contentValues.put(channelContext, new PreReadValue(decodedValues));
	}

	private final void setupNewBlock() throws IOException, EXIException {
		initBlock();

		channel = getNextChannel();

		// decode next EventCode
		decodeEventCode();

		preReadBlockStructure();
		preReadBlockContent();
	}

	protected Value getNextContentValue(QName qname) throws EXIException,
			IOException {
		
		blockValues--;

		assert (contentValues.get(qname) != null);
		Value v = contentValues.get(qname).getNextContantValue();

		return v;
	}

	public EventType next() throws EXIException, IOException {
		if (stillNoEndOfDocument && blockValues == 0) {
			// read next block
			setupNewBlock();
		}
		
		if (stillNoEndOfDocument || eventTypes.size() > eventTypeIndex) {
			return eventTypes.get(eventTypeIndex++);
		} else {
			return null;
		}
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

	public void decodeStartDocument() {
	}

	
	public QName decodeStartElement() throws IOException, EXIException {
		this.elementContext = startElementEntries.get(startElementEntryIndex++);
		return elementContext.qname;
	}

	public QName decodeStartElementNS() throws IOException, EXIException {
		return decodeStartElement();
	}

	public QName decodeStartElementGeneric() throws IOException, EXIException {
		return decodeStartElement();
	}

	public QName decodeStartElementGenericUndeclared() throws IOException,
			EXIException {
		return decodeStartElement();
	}

	public QName decodeEndElement() throws EXIException {
		EndElementEntry eeEntry = endElementEntries.get(endElementEntryIndex++);
		this.elementContext = eeEntry.after;
		return eeEntry.before.qname;
	}

	public QName decodeEndElementUndeclared() throws EXIException {
		return decodeEndElement();
	}

	public NamespaceDeclaration decodeNamespaceDeclaration()
			throws EXIException {
		return nsEntries.get(nsEntryIndex++);
	}

	public QName decodeAttributeXsiNil() throws EXIException, IOException {
		attributeQName = XSI_NIL;
		attributePrefix = xsiPrefixes.get(xsiPrefixIndex++);
		attributeValue = xsiValues.get(xsiValueIndex++);
		return attributeQName;
	}

	public QName decodeAttributeXsiType() throws EXIException, IOException {
		attributeQName = XSI_TYPE;
		attributePrefix = xsiPrefixes.get(xsiPrefixIndex++);
		attributeValue = xsiValues.get(xsiValueIndex++);
		
		this.handleAttributeXsiTypeValue();
		return attributeQName;
	}

	protected final void addQNameEntry(QNameEntry qne) {
		qnameEntries.add(qne);
	}
	
	protected final QNameEntry  getNextQNameEntry() {
		return qnameEntries.get(qnameEntryIndex++);
	}
	
	public QName decodeAttribute() throws EXIException, IOException {
		QNameEntry at = getNextQNameEntry();

		attributeQName = at.qname;
		attributePrefix = at.prefix;
		attributeValue = getNextContentValue(attributeQName);
		return attributeQName;
	}

	public QName decodeAttributeNS() throws EXIException, IOException {
		return decodeAttribute();
	}

	public QName decodeAttributeInvalidValue() throws EXIException, IOException {
		return decodeAttribute();
	}

	public QName decodeAttributeAnyInvalidValue() throws EXIException,
			IOException {
		return decodeAttribute();
	}

	public QName decodeAttributeGeneric() throws EXIException, IOException {
		return decodeAttribute();
	}

	public QName decodeAttributeGenericUndeclared() throws EXIException,
			IOException {
		return decodeAttribute();
	}

	public Value decodeCharacters() throws EXIException, IOException {
		QNameEntry ch = getNextQNameEntry();
		Value chVal =  getNextContentValue(ch.qname);
		return chVal;
	}

	public Value decodeCharactersGeneric() throws EXIException, IOException {
		return decodeCharacters();
	}

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

	public ProcessingInstruction decodeProcessingInstruction()
			throws EXIException {
		return processingEntries.get(processingEntryIndex++);
	}

	
	static class EndElementEntry {
		final ElementContext before;
		final ElementContext after;
		public EndElementEntry(ElementContext before, ElementContext after) {
			this.before = before;
			this.after = after;
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
