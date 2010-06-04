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

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import com.siemens.ct.exi.CodingMode;
import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.core.container.PreReadValueContainer;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.event.Attribute;
import com.siemens.ct.exi.grammar.event.EventType;
import com.siemens.ct.exi.io.channel.BitDecoderChannel;
import com.siemens.ct.exi.io.channel.ByteDecoderChannel;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.types.BuiltIn;
import com.siemens.ct.exi.values.QNameValue;
import com.siemens.ct.exi.values.Value;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.4.20090414
 */

public class EXIDecoderReordered extends EXIDecoderInOrder {
	// store appearing event-types in right order
	protected List<EventType> eventTypes;
	protected int eventTypeIndex;

	// elements, attributes, characters, end elements
	protected List<QNameEntry> qnameEntries;
	protected int qnameEntryIndex;
	// docTypes
	protected List<DocTypeEntry> docTypeEntries;
	protected int docTypeEntryIndex;
	// entity references
	protected List<String> entityReferences;
	protected int entityReferenceIndex;
	// comments
	protected List<char[]> comments;
	protected int commentIndex;
	// namespaces
	protected List<NamespaceEntry> nsEntries;
	protected int nsEntryIndex;
	// processing instructions
	protected List<ProcessingEntry> processingEntries;
	protected int processingEntryIndex;

	// count value items
	protected int blockValues;

	boolean stillNoEndOfDocument = true;

	// store value events (qnames) in right order
	// plus necessary information to reconstruct value channels
	protected List<QName> valueQNames;
	protected Map<QName, Integer> occurrences;
	protected Map<QName, List<Datatype>> dataTypes;

	// Channel and Compression stuff
	protected CodingMode codingMode;

	// content values
	protected Map<QName, PreReadValueContainer> contentValues;

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

	public EXIDecoderReordered(EXIFactory exiFactory) {
		super(exiFactory);

		// events
		eventTypes = new ArrayList<EventType>();
		// qname entries
		qnameEntries = new ArrayList<QNameEntry>();
		// misc
		docTypeEntries = new ArrayList<DocTypeEntry>();
		entityReferences = new ArrayList<String>();
		comments = new ArrayList<char[]>();
		nsEntries = new ArrayList<NamespaceEntry>();
		processingEntries = new ArrayList<ProcessingEntry>();
		// value events
		valueQNames = new ArrayList<QName>();
		occurrences = new HashMap<QName, Integer>();
		dataTypes = new HashMap<QName, List<Datatype>>();

		// content values
		contentValues = new HashMap<QName, PreReadValueContainer>();

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

	@Override
	public void setInputStream(InputStream is, boolean exiBodyOnly)
			throws EXIException, IOException {

		// buffer stream if not already
		// TODO is there a *nice* way to detect whether a stream is buffered
		// already
		if (!(is instanceof BufferedInputStream)) {
			this.is = new BufferedInputStream(is);
		} else {
			this.is = is;
		}

		// header
		if (!exiBodyOnly) {
			// parse header (bit-wise)
			BitDecoderChannel headerChannel = new BitDecoderChannel(is);
			EXIHeader.parse(headerChannel);
		}

		// body (structure)
		firstChannel = true;
		channel = getNextChannel();

		initForEachRun();
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

	protected void handleSpecialAttributeCases() throws EXIException,
			IOException {
		if (XSI_TYPE.equals(attributeQName)) {
			// xsi:type
			eventTypes.set(eventTypes.size() - 1, EventType.ATTRIBUTE_XSI_TYPE);
			// value content
			decodeAttributeXsiTypeStructure();
			xsiValues.add(attributeValue);
			assert (attributeValue instanceof QNameValue);
			// TODO prefix
			xsiPrefixes.add(namespaces
					.getPrefix(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI));
		} else if (XSI_NIL.equals(attributeQName)
				&& currentRule.isSchemaInformed()) {
			// xsi:nil
			eventTypes.set(eventTypes.size() - 1, EventType.ATTRIBUTE_XSI_NIL);
			// value content
			decodeAttributeXsiNilStructure();
			xsiValues.add(attributeValue);
			// TODO prefix
			xsiPrefixes.add(namespaces
					.getPrefix(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI));
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
		
		// TODO where does data go that come in between a block !?
		while (stillNoEndOfDocument && stillBlockReadable) {
			
			// add event to array list
			eventTypes.add(nextEventType);

			switch (nextEventType) {
			case START_DOCUMENT:
				super.decodeStartDocument();
				break;
			case START_ELEMENT:
				super.decodeStartElement();
				qnameEntries.add(new QNameEntry(elementContext.qname,
						elementPrefix));
				break;
			case START_ELEMENT_NS:
				super.decodeStartElementNS();
				qnameEntries.add(new QNameEntry(elementContext.qname,
						elementPrefix));
				break;
			case START_ELEMENT_GENERIC:
				super.decodeStartElementGeneric();
				qnameEntries.add(new QNameEntry(elementContext.qname,
						elementPrefix));
				break;
			case START_ELEMENT_GENERIC_UNDECLARED:
				super.decodeStartElementGenericUndeclared();
				qnameEntries.add(new QNameEntry(elementContext.qname,
						elementPrefix));
				break;
			case NAMESPACE_DECLARATION:
				super.decodeNamespaceDeclaration();
				nsEntries.add(new NamespaceEntry(nsURI, nsPrefix));
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

				// qnameEntries.add(new QNameEntry(attributeQName,
				// attributePrefix));
				// incrementValues(attributeQName, BuiltIn.DEFAULT_DATATYPE);
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
				incrementValues(elementContext.qname,
						decodeCharactersStructureOnly());
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
				super.decodeEndElement();
				qnameEntries.add(new QNameEntry(elementQName, elementPrefix));
				break;
			case END_ELEMENT_UNDECLARED:
				super.decodeEndElementUndeclared();
				qnameEntries.add(new QNameEntry(elementQName, elementPrefix));
				break;
			case END_DOCUMENT:
				super.decodeEndDocument();
				stillNoEndOfDocument = false;
				continue;
			case DOC_TYPE:
				super.decodeDocType();
				docTypeEntries.add(new DocTypeEntry(docTypeName,
						docTypePublicID, docTypeSystemID, docTypeText));
				break;
			case ENTITY_REFERENCE:
				super.decodeEntityReference();
				entityReferences.add(entityReferenceName);
				break;
			case COMMENT:
				super.decodeComment();
				comments.add(comment);
				break;
			case PROCESSING_INSTRUCTION:
				super.decodeProcessingInstruction();
				processingEntries.add(new ProcessingEntry(piTarget, piData));
				break;
			default:
				throw new RuntimeException("Unknown Event " + nextEventType);
			}
			
			//	still events in this block ?
			if (blockValues == exiFactory.getBlockSize()) {
				// NO events any more
				stillBlockReadable = false;
			} else {
				// decode next EventCode
				decodeEventCode();		
			}
			
		}
		
		// System.out.println("No more blockvalues " +  blockValues + " after " + qnameEntries.get(qnameEntries.size()-1).context );
		
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
		contentValues.put(channelContext, new PreReadValueContainer(
				decodedValues));
	}
	
	protected Value getNextContentValue(QName qname) throws EXIException, IOException {
		assert (contentValues.get(qname) != null);
		Value v = contentValues.get(qname).getNextContantValue();
		
		if ( stillNoEndOfDocument &&  --blockValues == 0 ) {
			// read next block
			// System.out.println("TODO 0, read next block");
			// System.out.println("Next Value " + blockValues + " \t " + v + " after " + qname);
			
			initBlock();
			
			channel = getNextChannel();
			
			// decode next EventCode
			decodeEventCode();	
			
			preReadBlockStructure();
			preReadBlockContent();
		}

		return v;
	}

	public boolean hasNext() {
		return (eventTypes.size() > (eventTypeIndex + 1));
	}

	public EventType next() throws EXIException {
		return (nextEventType = eventTypes.get(eventTypeIndex++));
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
	}

	@Override
	public void decodeStartElement() {
		QNameEntry se = qnameEntries.get(qnameEntryIndex++);
		this.elementQName = se.context;

		elementPrefix = se.prefix;
	}

	@Override
	public void decodeStartElementNS() {
		decodeStartElement();
	}

	@Override
	public void decodeStartElementGeneric() {
		decodeStartElement();
	}

	@Override
	public void decodeStartElementGenericUndeclared() {
		decodeStartElement();
	}

	@Override
	public void decodeEndElement() throws EXIException {
		QNameEntry ee = qnameEntries.get(qnameEntryIndex++);
		this.elementQName = ee.context;
	}

	@Override
	public void decodeEndElementUndeclared() throws EXIException {
		decodeEndElement();
	}

	public void decodeNamespaceDeclaration() throws EXIException {
		NamespaceEntry ns = nsEntries.get(nsEntryIndex++);
		nsURI = ns.namespaceURI;
		nsPrefix = ns.prefix;

		namespaces.declarePrefix(nsPrefix, nsURI);
	}

	@Override
	public void decodeAttributeXsiNil() throws EXIException, IOException {
		attributeQName = XSI_NIL;

		attributePrefix = xsiPrefixes.get(xsiPrefixIndex++);
		attributeValue = xsiValues.get(xsiValueIndex++);
	}

	@Override
	public void decodeAttributeXsiType() throws EXIException, IOException {
		attributeQName = XSI_TYPE;

		attributePrefix = xsiPrefixes.get(xsiPrefixIndex++);
		attributeValue = xsiValues.get(xsiValueIndex++);
	}

	@Override
	public void decodeAttribute() throws EXIException, IOException {
		QNameEntry at = qnameEntries.get(qnameEntryIndex++);

		attributeQName = at.context;
		attributePrefix = at.prefix;

//		PreReadValueContainer vc = contentValues.get(attributeQName);
//		assert (vc != null);
//		attributeValue = vc.getNextContantValue();
		attributeValue = getNextContentValue(attributeQName);
	}

	@Override
	public void decodeAttributeNS() throws EXIException, IOException {
		decodeAttribute();
	}

	@Override
	public void decodeAttributeInvalidValue() throws EXIException, IOException {
		decodeAttribute();
	}

	@Override
	public void decodeAttributeAnyInvalidValue() throws EXIException,
			IOException {
		decodeAttribute();
	}

	@Override
	public void decodeAttributeGeneric() throws EXIException, IOException {
		decodeAttribute();
	}

	@Override
	public void decodeAttributeGenericUndeclared() throws EXIException,
			IOException {
		decodeAttribute();
	}

	@Override
	public void decodeCharacters() throws EXIException, IOException {
		QNameEntry ch = qnameEntries.get(qnameEntryIndex++);

		// PreReadValueContainer vc = contentValues.get(ch.context);
		// assert (vc != null);
		// characters = vc.getNextContantValue();
		characters = getNextContentValue(ch.context);
	}

	@Override
	public void decodeCharactersGeneric() throws EXIException, IOException {
		decodeCharacters();
	}

	@Override
	public void decodeCharactersGenericUndeclared() throws EXIException,
			IOException {
		decodeCharacters();
	}

	public void decodeEndDocument() throws EXIException {
	}

	public void decodeDocType() throws EXIException {
		DocTypeEntry dt = docTypeEntries.get(docTypeEntryIndex++);

		docTypeName = dt.name;
		docTypePublicID = dt.publicID;
		docTypeSystemID = dt.systemID;
		docTypeText = dt.text;
	}

	public void decodeEntityReference() throws EXIException {
		entityReferenceName = entityReferences
				.get(entityReferenceIndex++);
	}

	public void decodeComment() throws EXIException {
		comment = comments.get(commentIndex++);
	}

	public void decodeProcessingInstruction() throws EXIException {
		ProcessingEntry pi = processingEntries.get(processingEntryIndex++);

		piTarget = pi.target;
		piData = pi.data;
	}

	/*
	 * Pre-Read Entries
	 */
	static class QNameEntry {
		final QName context;
		final String prefix;

		public QNameEntry(QName context, String prefix) {
			this.context = context;
			this.prefix = prefix;
		}
	}

	static class NamespaceEntry {
		final String namespaceURI;
		final String prefix;

		public NamespaceEntry(String namespaceURI, String prefix) {
			this.namespaceURI = namespaceURI;
			this.prefix = prefix;
		}
	}

	static class DocTypeEntry {
		final String name;
		final String publicID;
		final String systemID;
		final String text;

		public DocTypeEntry(String name, String publicID, String systemID,
				String text) {
			this.name = name;
			this.publicID = publicID;
			this.systemID = systemID;
			this.text = text;
		}
	}

	static class ProcessingEntry {
		final String target;
		final String data;

		public ProcessingEntry(String target, String data) {
			this.target = target;
			this.data = data;
		}
	}

}
