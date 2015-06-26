/*
 * Copyright (C) 2007-2015 Siemens AG
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
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

import com.siemens.ct.exi.CodingMode;
import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.DecodingOptions;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.core.container.DocType;
import com.siemens.ct.exi.core.container.NamespaceDeclaration;
import com.siemens.ct.exi.core.container.PreReadValue;
import com.siemens.ct.exi.core.container.ProcessingInstruction;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.datatype.strings.StringDecoder;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammars.event.EventType;
import com.siemens.ct.exi.io.channel.ByteDecoderChannel;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.io.compression.EXIInflaterInputStream;
import com.siemens.ct.exi.types.BuiltIn;
import com.siemens.ct.exi.values.Value;

/**
 * EXI decoder for (pre-)compression streams.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.5-SNAPSHOT
 */

public class EXIBodyDecoderReordered extends AbstractEXIBodyDecoder {
	// store appearing event-types in right order
	protected List<EventType> eventTypes;
	protected int eventTypeIndex;

	// elements and end elements
	protected List<ElementContext> elementEntries;
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

	// count value items
	protected int blockValues;

	boolean stillNoEndOfDocument = true;

	ElementContext lastBlockElementContext;

	// Channel and Compression stuff
	protected CodingMode codingMode;

	// local element context (to avoid blockSize value problems)
	ElementContext currElementEntry;

	// content values
	protected Map<QNameContext, PreReadValue> contentValues;


	protected List<Value> xsiValues;
	protected int xsiValueIndex;
	protected List<String> xsiPrefixes;
	protected int xsiPrefixIndex;

	// deflate stuff
	protected Inflater inflater;
	// protected InputStream recentInflaterInputStream;
	
	// Note: Map needs to be sorted to retrieve correct channel order (e.g., LinkedHashMap)
	protected Map<QNameContext, List<Datatype>> channelDatatypes;
	
	protected Map<QNameContext, PreReadValue> preReadValues;
	
	protected boolean firstChannel;

	protected InputStream is;

	public EXIBodyDecoderReordered(EXIFactory exiFactory) throws EXIException {
		super(exiFactory);

		// events
		eventTypes = new ArrayList<EventType>();
		// element entries
		elementEntries = new ArrayList<ElementContext>();
		// qname entries
		qnameEntries = new ArrayList<QNameEntry>();
		// misc
		docTypeEntries = new ArrayList<DocType>();
		entityReferences = new ArrayList<char[]>();
		comments = new ArrayList<char[]>();
		nsEntries = new ArrayList<NamespaceDeclaration>();
		processingEntries = new ArrayList<ProcessingInstruction>();

		preReadValues = new HashMap<QNameContext, PreReadValue>();
		
		// Note: needs to be sorted map for channel order 
		channelDatatypes = new LinkedHashMap<QNameContext, List<Datatype>>();
		
		// content values
		contentValues = new HashMap<QNameContext, PreReadValue>();

		xsiValues = new ArrayList<Value>();
		xsiPrefixes = new ArrayList<String>();

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

		stillNoEndOfDocument = true;
		lastBlockElementContext = null;

		channelDatatypes.clear();
		
		// initialize block etc
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
		initCompressionBlock();

		// content values
		contentValues.clear();
		xsiValues.clear();
		xsiValueIndex = 0;
		xsiPrefixes.clear();
		xsiPrefixIndex = 0;
	}
	
	protected void initCompressionBlock() {
		// re-set all channels
		this.channelDatatypes.clear();
		
		this.preReadValues.clear();
	}
	
	protected void addDatatype(QNameContext qnc, Datatype d) {
		List<Datatype> ds = this.channelDatatypes.get(qnc);
		if(ds == null) {
			// create new list
			ds = new ArrayList<Datatype>();
			// add to map (sorted due to linked hashmap)
			channelDatatypes.put(qnc, ds);
		} 
		ds.add(d);
	}

	// @Override
	public void setInputStream(InputStream is) throws EXIException, IOException {
		updateInputStream(is);

		initForEachRun();
	}
	
	EXIInflaterInputStream inflaterInputStream;
	
	public void updateInputStream(InputStream is) throws EXIException, IOException {
		this.is = is;
		if(!(this.is instanceof PushbackInputStream)) {
			 this.is = new PushbackInputStream(is, DecodingOptions.PUSHBACK_BUFFER_SIZE);
		}
		inflaterInputStream = null;

		firstChannel = true;
		inflater = new Inflater(true);
		channel = getNextChannel();
	}

	public void setInputChannel(DecoderChannel channel) throws EXIException,
			IOException {
		throw new RuntimeException(
				"[EXI] Reorderd EXI Body decoder needs to be set via setInputStream(...)");
	}
	
	public void updateInputChannel(DecoderChannel decoderChannel)
			throws EXIException, IOException {
		throw new RuntimeException(
				"[EXI] Reorderd EXI Body decoder needs to be set via updateInputStream(...)");
	}
	
	
	private void readjustInputStream(InputStream is) throws IOException {
		assert((codingMode == CodingMode.COMPRESSION));
		if (inflaterInputStream != null) {
			// inflater reads beyond deflate stream, reset position
			// Note: pushback needs to be called given that it resets inflater
			inflaterInputStream.pushbackAndReset();
		}
	}

	public DecoderChannel getNextChannel() throws IOException {

		if (codingMode == CodingMode.COMPRESSION) {
			// readjust channel of previous inflate streams
			readjustInputStream(is);
			
			inflaterInputStream = new EXIInflaterInputStream((PushbackInputStream) is, inflater, DecodingOptions.PUSHBACK_BUFFER_SIZE);
			return new ByteDecoderChannel(inflaterInputStream);
			
//			 return new ByteDecoderChannel(new InflaterInputStream(is, inflater, inputBufferSize));
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
		// prefix
		xsiPrefixes.add(attributePrefix);
	}

	protected void handleSpecialAttributeCases() throws EXIException,
			IOException {
		if (getXsiTypeContext().equals(attributeQNameContext)) {
			// xsi:type
			updateAttributeToXsiType();
		} else if (getXsiNilContext().equals(
				attributeQNameContext)
				&& getCurrentGrammar().isSchemaInformed()) {
			// xsi:nil
			eventTypes.set(eventTypes.size() - 1, EventType.ATTRIBUTE_XSI_NIL);
			// value content
			decodeAttributeXsiNilStructure();
			xsiValues.add(attributeValue);
			// prefix
			xsiPrefixes.add(attributePrefix);
		} else {
			// global attribute or default datatype
			Datatype dt = BuiltIn.DEFAULT_DATATYPE;

			if (getCurrentGrammar().isSchemaInformed()
					&& attributeQNameContext.getGlobalAttribute() != null) {
				dt = attributeQNameContext.getGlobalAttribute().getDatatype();
			}

			addQNameEntry(new QNameEntry(attributeQNameContext, attributePrefix));
			incrementValues(attributeQNameContext, dt);
		}
	}

	protected void preReadBlockStructure() throws EXIException, IOException {

		boolean stillBlockReadable = true;
		boolean deferredStartElement = false;

		if (lastBlockElementContext != null) {
			updateElementContext(lastBlockElementContext);
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
					elementEntries.add(getElementContext());
					deferredStartElement = false;
				default:
					// no action
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
				if (getXsiTypeContext().equals(
						this.attributeQNameContext)) {
					updateAttributeToXsiType();
				} else {
					addQNameEntry(new QNameEntry(this.attributeQNameContext,
							attributePrefix));
					incrementValues(this.attributeQNameContext, dtAT);
				}
				break;
			case ATTRIBUTE_INVALID_VALUE:
				decodeAttributeStructure();
				addQNameEntry(new QNameEntry(attributeQNameContext,
						attributePrefix));
				incrementValues(attributeQNameContext, BuiltIn.DEFAULT_DATATYPE);
				break;
			case ATTRIBUTE_ANY_INVALID_VALUE:
				decodeAttributeAnyInvalidValueStructure();
				addQNameEntry(new QNameEntry(attributeQNameContext,
						attributePrefix));
				incrementValues(attributeQNameContext, BuiltIn.DEFAULT_DATATYPE);
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
				QNameContext qnc = getElementContext().qnameContext;
				incrementValues(qnc, decodeCharactersStructure());
				addQNameEntry(new QNameEntry(qnc, null));
				break;
			case CHARACTERS_GENERIC:
				decodeCharactersGenericStructure();
				qnc = getElementContext().qnameContext;
				incrementValues(qnc, BuiltIn.DEFAULT_DATATYPE);
				addQNameEntry(new QNameEntry(qnc, null));
				break;
			case CHARACTERS_GENERIC_UNDECLARED:
				decodeCharactersGenericUndeclaredStructure();
				qnc = getElementContext().qnameContext;
				incrementValues(qnc, BuiltIn.DEFAULT_DATATYPE);
				addQNameEntry(new QNameEntry(qnc, null));
				break;
			case END_ELEMENT:
				decodeEndElementStructure();
				elementEntries.add(getElementContext());
				break;
			case END_ELEMENT_UNDECLARED:
				decodeEndElementUndeclaredStructure();
				elementEntries.add(getElementContext());
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

				lastBlockElementContext = getElementContext();

				// Note: NS mapping has be done before opening a new block
				assert (deferredStartElement == false);
			} else {
				// decode next EventCode
				decodeEventCode();
			}

		}

		// System.out.println("No more blockvalues " + blockValues + " after " +
		// qnameEntries.get(qnameEntries.size()-1) );
		updateElementContext(lastBlockElementContext);
	}
	

	protected void preReadBlockContent() throws EXIException {
		try {
			Iterator<QNameContext> iterCh = this.channelDatatypes.keySet().iterator();
			
			if (blockValues <= Constants.MAX_NUMBER_OF_VALUES) {
				// single compressed stream (includes structure)
				
				while(iterCh.hasNext()) {
					QNameContext o = iterCh.next();
					List<Datatype> lds = this.channelDatatypes.get(o);
					Value[] contentValues = readValues(lds, o, channel, stringDecoder);
					PreReadValue prv = new PreReadValue(contentValues);
					this.preReadValues.put(o, prv);
				}
			} else {
				// first stream structure (already read)
				// second stream (if any), values <= 100

				DecoderChannel bdcLessEqual100 = null;

				while(iterCh.hasNext()) {
					QNameContext o = iterCh.next();
					List<Datatype> lds = this.channelDatatypes.get(o);
					if (lds.size() <= Constants.MAX_NUMBER_OF_VALUES) {
						if (bdcLessEqual100 == null) {
							bdcLessEqual100 = getNextChannel();
						}
						Value[] contentValues = readValues(lds, o, bdcLessEqual100, stringDecoder);
						PreReadValue prv = new PreReadValue(contentValues);
						this.preReadValues.put(o, prv);
					}
				}

				// proper stream for greater100
				iterCh = this.channelDatatypes.keySet().iterator();
				while(iterCh.hasNext()) {
					QNameContext o = iterCh.next();
					List<Datatype> lds = this.channelDatatypes.get(o);
					if (lds.size() > Constants.MAX_NUMBER_OF_VALUES) {
						DecoderChannel bdcGreater100 = getNextChannel();
						Value[] contentValues = readValues(lds, o, bdcGreater100, stringDecoder);
						PreReadValue prv = new PreReadValue(contentValues);
						this.preReadValues.put(o, prv);
					}
				}

			}
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}
	
	
	private Value[] readValues(List<Datatype> lds, QNameContext o, DecoderChannel valueChannel, StringDecoder stringDecoder) throws IOException {
		Value[] contentValues = new Value[lds.size()];
		for (int i = 0; i < lds.size(); i++) {
			contentValues[i] = typeDecoder.readValue(
					lds.get(i), o, valueChannel, stringDecoder);
		}
		
		return contentValues;
	}

	protected void setContentValues(DecoderChannel bdc,
			QNameContext channelContext, int occs, List<Datatype> datatypes)
			throws IOException {

		assert (datatypes.size() == occs);
		Value[] decodedValues = new Value[occs];

		for (int k = 0; k < occs; k++) {
			Datatype dt = datatypes.get(k);
			decodedValues[k] = typeDecoder.readValue(dt, channelContext, bdc,
					stringDecoder);
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

	protected Value getNextContentValue(QNameContext qname)
			throws EXIException, IOException {

		blockValues--;
		
		Value v = this.preReadValues.get(qname).getNextContantValue();

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

	// protected void incrementValues(QName valueContext, Datatype datatype) {
	protected void incrementValues(QNameContext valueContext, Datatype datatype) {
		blockValues++;

		this.addDatatype(valueContext, datatype);
	}

	public void decodeStartDocument() {
	}

	protected final ElementContext setNextElemementEntry() {
		return (currElementEntry = elementEntries.get(elementEntryIndex++));
	}

	public QNameContext decodeStartElement() throws IOException, EXIException {
		return setNextElemementEntry().qnameContext;
	}

	public QNameContext decodeEndElement() throws EXIException {
		// before
		QNameContext eeBefore = currElementEntry.qnameContext;
		// after
		setNextElemementEntry();

		return eeBefore;
	}

	public List<NamespaceDeclaration> getDeclaredPrefixDeclarations() {
		return currElementEntry.nsDeclarations;
	}

	public String getElementPrefix() {
		return currElementEntry.getPrefix();
	}

	public String getElementQNameAsString() {
		return currElementEntry.getQNameAsString();
	}

	public NamespaceDeclaration decodeNamespaceDeclaration()
			throws EXIException {
		return nsEntries.get(nsEntryIndex++);
	}

	public QNameContext decodeAttributeXsiNil() throws EXIException,
			IOException {
		this.attributeQNameContext = getXsiNilContext();
		attributePrefix = xsiPrefixes.get(xsiPrefixIndex++);
		attributeValue = xsiValues.get(xsiValueIndex++);
		return attributeQNameContext;
	}

	public QNameContext decodeAttributeXsiType() throws EXIException,
			IOException {
		this.attributeQNameContext = getXsiTypeContext();
		attributePrefix = xsiPrefixes.get(xsiPrefixIndex++);
		attributeValue = xsiValues.get(xsiValueIndex++);

		return attributeQNameContext;
	}

	protected final void addQNameEntry(QNameEntry qne) {
		qnameEntries.add(qne);
	}

	protected final QNameEntry getNextQNameEntry() {
		return qnameEntries.get(qnameEntryIndex++);
	}

	public QNameContext decodeAttribute() throws EXIException, IOException {
		QNameEntry at = getNextQNameEntry();

		this.attributeQNameContext = at.qnContext;
		attributePrefix = at.prefix;
		attributeValue = getNextContentValue(attributeQNameContext);

		return attributeQNameContext;
	}

	public Value decodeCharacters() throws EXIException, IOException {
		QNameEntry ch = getNextQNameEntry();
		Value chVal = getNextContentValue(ch.qnContext);
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
		if (codingMode == CodingMode.COMPRESSION) {
			// Note: in many cases not needed (e.g., if no more EXI documents in stream )
			// fix input stream so that another process can read data at the right position...
			try {
				readjustInputStream(is);
			} catch (IOException e) {
				throw new EXIException(e);
			}
		}
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
		final QNameContext qnContext;
		final String prefix;

		public QNameEntry(QNameContext qnContext, String prefix) {
			this.qnContext = qnContext;
			this.prefix = prefix;
		}
	}

}
