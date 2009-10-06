/*
 * Copyright (C) 2007-2009 Siemens AG
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
import com.siemens.ct.exi.grammar.event.EventType;
import com.siemens.ct.exi.io.channel.BitDecoderChannel;
import com.siemens.ct.exi.io.channel.ByteDecoderChannel;
import com.siemens.ct.exi.io.channel.DecoderChannel;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20090414
 */

public class EXIDecoderReordered extends EXIDecoderInOrder {
	// store appearing event-types in right order
	protected List<EventType> eventTypes;
	protected int eventIndex;

	// elements
	protected List<QNameEntry> elementEntries;
	protected int elementEntryIndex;
	// attributes
	protected List<QNameEntry> attributeEntries;
	protected int attributeEntryIndex;
	// docTypes
	protected List<DocTypeEntry> docTypeEntries;
	protected int docTypeEntryIndex;
	// entity references
	protected List<String> entityReferences;
	protected int currentEntityReferenceIndex;
	// comments
	protected List<char[]> comments;
	protected int commentsIndex;
	// namespaces
	protected List<NamespaceEntry> nsEntries;
	protected int nsEntryIndex;
	// processing instructions
	protected List<ProcessingEntry> processingEntries;
	protected int processingEntryIndex;

	// count value items
	protected int cntValues;

	// store value events (qnames) in right order
	// plus necessary information to reconstruct value channels
	protected List<QName> valueQNames;
	protected Map<QName, Integer> occurrences;
	protected Map<QName, List<Datatype>> dataTypes;

	// Channel and Compression stuff
	protected CodingMode codingMode;

	// content values
	protected Map<QName, PreReadValueContainer> contentValues;

	protected List<char[]> xsiValues;
	protected int xsiEntryIndex;

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
		// elements
		elementEntries = new ArrayList<QNameEntry>();
		// attributes
		attributeEntries = new ArrayList<QNameEntry>();
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

		xsiValues = new ArrayList<char[]>();

		codingMode = exiFactory.getCodingMode();
	}

	@Override
	protected void initForEachRun() throws EXIException, IOException {
		super.initForEachRun();

		// next event
		nextEvent = null;
		nextEventType = EventType.START_DOCUMENT;

		// events
		// events.clear();
		eventTypes.clear();
		eventIndex = 0;

		// elements
		elementEntries.clear();
		elementEntryIndex = 0;

		// attributes
		attributeEntries.clear();
		attributeEntryIndex = 0;

		// misc
		docTypeEntries.clear();
		docTypeEntryIndex = 0;
		entityReferences.clear();
		currentEntityReferenceIndex = 0;
		comments.clear();
		commentsIndex = 0;
		nsEntries.clear();
		nsEntryIndex = 0;
		processingEntries.clear();
		processingEntryIndex = 0;
		// count value items
		cntValues = 0;

		// contains value events (qnames) in right order
		// plus necessary information to reconstruct value channels
		valueQNames.clear();
		occurrences.clear();
		dataTypes.clear();

		// content values
		contentValues.clear();
		xsiValues.clear();
		xsiEntryIndex = 0;

		// pre-read structure and afterwards pre-read content (values)
		preReadStructure();
		preReadContent();
	}

	@Override
	public void setInputStream(InputStream is, boolean exiBodyOnly)
			throws EXIException, IOException {

		// buffer stream if not already
		// TODO is there a *nice* way to detect whether a stream is buffered
		// already
		if (!(is instanceof BufferedInputStream)) {
			this.is = new BufferedInputStream(is);
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
				// handle the case where fewer bytes were skipped than requested 
				if (skipped != bytesRead) {
					do {
						long skippedLoop = resettableInputStream.skip(bytesRead-skipped);
						if ( skippedLoop <= 0 ) {
							//	NOTE: If n is negative, no bytes are skipped
							throw new IOException("[EXI] Byte skipping impossible on given input stream");
						}
						skipped += skippedLoop;
						assert(skipped <= bytesRead);
					} while( skipped < bytesRead);
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

	protected void preReadStructure() throws EXIException, IOException {
		boolean stillInitializing = true;

		while (stillInitializing) {

			// events.add(nextEvent); // add event to array list
			// events.add(nextEventRule == null ? null :
			// nextEventRule.event); // add event to array list
			// eventRules.add(nextEventRule); // add event to array list
			eventTypes.add(nextEventType);

			switch (nextEventType) {
			case START_DOCUMENT:
				super.decodeStartDocument();
				break;
			case START_ELEMENT:
				super.decodeStartElement();
				
//				elementEntries.add(new QNameEntry(elementURI, elementLocalName,
//						elementPrefix));
				elementEntries.add(new QNameEntry(elementContext,
						elementPrefix));
				break;
			case START_ELEMENT_NS:
				super.decodeStartElementNS();
				elementEntries.add(new QNameEntry(elementContext,
						elementPrefix));
				break;
			case START_ELEMENT_GENERIC:
				super.decodeStartElementGeneric();
				elementEntries.add(new QNameEntry(elementContext,
						elementPrefix));
				break;
			case START_ELEMENT_GENERIC_UNDECLARED:
				super.decodeStartElementGenericUndeclared();
				elementEntries.add(new QNameEntry(elementContext,
						elementPrefix));
				break;
			case NAMESPACE_DECLARATION:
				super.decodeNamespaceDeclaration();
				nsEntries.add(new NamespaceEntry(nsURI, nsPrefix));
				break;
			case ATTRIBUTE_XSI_TYPE:
				decodeAttributeXsiTypeStructure();
				xsiValues.add(attributeValue);
				break;
			case ATTRIBUTE_XSI_NIL:
				decodeAttributeXsiNilStructure();
				xsiValues.add(attributeValue);
				break;
			case ATTRIBUTE:
				Datatype dtAT = decodeAttributeStructure();
				QName atContext = getAttributeContext(attributeURI,
						attributeLocalName);
				attributeEntries.add(new QNameEntry(atContext, attributePrefix));
				incrementValues(atContext, dtAT);
				break;
			case ATTRIBUTE_NS:
				dtAT = decodeAttributeNSStructure();
				atContext = getAttributeContext(attributeURI,
						attributeLocalName);
				attributeEntries.add(new QNameEntry(atContext, attributePrefix));
				incrementValues(atContext, dtAT);
				break;
			case ATTRIBUTE_INVALID_VALUE:
				dtAT = decodeAttributeInvalidValueStructure();
				atContext = getAttributeContext(attributeURI,
						attributeLocalName);
				attributeEntries.add(new QNameEntry(atContext, attributePrefix));
				incrementValues(atContext, dtAT);
				break;
			case ATTRIBUTE_ANY_INVALID_VALUE:
				dtAT = decodeAttributeAnyInvalidValueStructure();
				atContext = getAttributeContext(attributeURI,
						attributeLocalName);
				attributeEntries.add(new QNameEntry(atContext, attributePrefix));
				incrementValues(atContext, dtAT);
				break;
			case ATTRIBUTE_GENERIC:
				dtAT = decodeAttributeGenericStructure();
				atContext = getAttributeContext(attributeURI,
						attributeLocalName);
				attributeEntries.add(new QNameEntry(atContext, attributePrefix));
				incrementValues(atContext, dtAT);
				break;
			case ATTRIBUTE_GENERIC_UNDECLARED:
				dtAT = decodeAttributeGenericUndeclaredStructure();
				atContext = getAttributeContext(attributeURI,
						attributeLocalName);
				attributeEntries.add(new QNameEntry(atContext, attributePrefix));
				incrementValues(atContext, dtAT);
				break;
			case CHARACTERS:
				incrementValues(elementContext, decodeCharactersStructureOnly());
				break;
			case CHARACTERS_GENERIC:
				incrementValues(elementContext,
						decodeCharactersGenericStructureOnly());
				break;
			case CHARACTERS_GENERIC_UNDECLARED:
				incrementValues(elementContext,
						decodeCharactersGenericUndeclaredStructureOnly());
				break;
			case END_ELEMENT:
				super.decodeEndElement();
				break;
			case END_ELEMENT_UNDECLARED:
				super.decodeEndElementUndeclared();
				break;
			case END_DOCUMENT:
				super.decodeEndDocument();
				stillInitializing = false;
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

			// decode next EventCode
			this.decodeEventCode();
			// super.inspectEvent();
		}
	}

	protected void preReadContent() throws EXIException {
		try {
			if (cntValues <= Constants.MAX_NUMBER_OF_VALUES) {
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
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	protected static boolean areThereAnyLessEqualThan100(
			List<QName> qnames, Map<QName, Integer> occurrences) {
		for (int i = 0; i < qnames.size(); i++) {
			if (occurrences.get(qnames.get(i)) <= Constants.MAX_NUMBER_OF_VALUES) {
				return true;
			}
		}
		return false;
	}

	protected void setContentValues(DecoderChannel bdc,
			QName channelContext, int occs, List<Datatype> datatypes)
			throws IOException {

		assert (datatypes.size() == occs);
		char[][] decodedValues = new char[occs][];

		for (int k = 0; k < occs; k++) {
			Datatype dt = datatypes.get(k);
//			if (dt == null) {
//				assert (channelContext.getNamespaceURI()
//						.equals(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI));
//				assert (decodedValues[k] != null);
//			} else {
				decodedValues[k] = typeDecoder.readValue(dt, channelContext,
						bdc);
//			}
		}

		// set content value
		contentValues.put(channelContext, new PreReadValueContainer(
				decodedValues));
	}

	public boolean hasNext() {
		// return ( events.size() > ( currentEventIndex ) );
		// ED --> no next event
		return (eventTypes.size() > (eventIndex + 1));
		// return (events.size() > (eventIndex + 1));
		// return (eventRules.size() > (currentEventIndex + 1));
	}

	public EventType next() throws EXIException {
		// return eventTypes.get(currentEventIndex);
		return (nextEventType = eventTypes.get(eventIndex++));
		// nextEventType = eventTypes.get(currentEventIndex);
	}

	protected void incrementValues(QName valueContext, Datatype datatype) {
		cntValues++;

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
		QNameEntry se = elementEntries.get(elementEntryIndex++);

		elementURI = se.context.getNamespaceURI();
		elementLocalName = se.context.getLocalPart();
		elementPrefix = se.prefix;

		// this.elementContext = se.context;
		pushElementContext(se.context);
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

	public void decodeNamespaceDeclaration() throws EXIException {
		NamespaceEntry ns = nsEntries.get(nsEntryIndex++);
		nsURI = ns.namespaceURI;
		nsPrefix = ns.prefix;

		namespaces.declarePrefix(nsPrefix, nsURI);
	}

	@Override
	public void decodeAttributeXsiNil() throws EXIException, IOException {
		attributeURI = XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI;
		attributeLocalName = Constants.XSI_NIL;

		attributeValue = xsiValues.get(xsiEntryIndex++);
	}

	@Override
	public void decodeAttributeXsiType() throws EXIException, IOException {
		attributeURI = XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI;
		attributeLocalName = Constants.XSI_TYPE;

		attributeValue = xsiValues.get(xsiEntryIndex++);
	}
	
	@Override
	public void decodeAttribute() throws EXIException {
		QNameEntry at = attributeEntries.get(attributeEntryIndex++);

		attributeURI = at.context.getNamespaceURI();
		attributeLocalName = at.context.getLocalPart();
		attributePrefix = at.prefix;

		QName atContext = getAttributeContext(attributeURI,
				attributeLocalName);
		PreReadValueContainer vc = contentValues.get(atContext);
		assert (vc != null);
		attributeValue = vc.getNextContantValue();
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
	public void decodeAttributeAnyInvalidValue() throws EXIException, IOException {
		decodeAttribute();
	}

	@Override
	public void decodeAttributeGeneric() throws EXIException, IOException {
		decodeAttribute();
	}

	@Override
	public void decodeAttributeGenericUndeclared() throws EXIException, IOException {
		decodeAttribute();
	}


	@Override
	public void decodeCharacters() throws EXIException {
		PreReadValueContainer vc = contentValues.get(elementContext);
		assert (vc != null);
		characters = vc.getNextContantValue();
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

	@Override
	public void decodeEndElement() throws EXIException {
		popElementContext();
	}

	@Override
	public void decodeEndElementUndeclared() throws EXIException {
		decodeEndElement();
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
				.get(currentEntityReferenceIndex++);
	}

	public void decodeComment() throws EXIException {
		comment = comments.get(commentsIndex++);
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
//		final String namespaceURI;
//		final String localName;
		final QName context;
		final String prefix;

		// public QNameEntry(String namespaceURI, String localName, String prefix) {
		public QNameEntry(QName context, String prefix) {
			this.context = context;
//			this.namespaceURI = namespaceURI;
//			this.localName = localName;
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
