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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.datatype.BuiltIn;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.GrammarSchemaInformed;
import com.siemens.ct.exi.grammar.TypeGrammar;
import com.siemens.ct.exi.grammar.event.Attribute;
import com.siemens.ct.exi.grammar.event.AttributeNS;
import com.siemens.ct.exi.grammar.event.Characters;
import com.siemens.ct.exi.grammar.event.Event;
import com.siemens.ct.exi.grammar.event.EventType;
import com.siemens.ct.exi.grammar.event.StartDocument;
import com.siemens.ct.exi.grammar.event.StartElement;
import com.siemens.ct.exi.grammar.event.StartElementNS;
import com.siemens.ct.exi.grammar.rule.SchemaInformedRule;
import com.siemens.ct.exi.util.ExpandedName;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20090414
 */

public class EXIDecoderReordered extends AbstractEXIDecoder {
	// store appearing events in right order
	protected List<Event> events;
	protected List<EventType> eventTypes;
	protected int currentEventIndex;

	// elements
	protected List<String> elementURIs;
	protected int currentElementURIIndex;
	protected List<String> elementLocalNames;
	protected int currentElementLocalNameIndex;
	protected List<String> elementPrefixes;
	protected int currentElementPrefixIndex;
	// attributes
	protected List<String> attributeURIs;
	protected int currentAttributeURIIndex;
	protected List<String> attributeLocalNames;
	protected int currentAttributeLocalNameIndex;
	protected List<String> attributePrefixes;
	protected int currentAttributePrefixIndex;
	// xsi
	protected List<String> xsiTypeUris;
	protected List<String> xsiTypeNames;
	protected int currentXsiTypeIndex;
	protected List<Boolean> xsiNils;
	protected int currentXsiNilsIndex;
	// misc
	protected List<String> docTypes;
	protected int currentDocTypeIndex;

	protected List<String> entityReferences;
	protected int currentEntityReferenceIndex;

	protected List<String> comments;
	protected int currentCommentsIndex;

	protected List<String> uris;
	protected List<String> prefixes;
	protected int currentNamespacesIndex;

	protected List<String> piTargets;
	protected List<String> piDatas;
	protected int currentProcessingIntructionsIndex;

	// count value items
	protected int cntValues;

	// store value events (qnames) in right order
	// plus necessary information to reconstruct value channels
	protected List<ExpandedName> valueQNames;
	protected Map<ExpandedName, Integer> occurrences;
	protected Map<ExpandedName, List<Datatype>> dataTypes;

	public EXIDecoderReordered(EXIFactory exiFactory) {
		super(exiFactory);

		// events
		events = new ArrayList<Event>();
		eventTypes = new ArrayList<EventType>();

		// elements
		elementURIs = new ArrayList<String>();
		elementLocalNames = new ArrayList<String>();
		elementPrefixes = new ArrayList<String>();
		// attributes
		attributeURIs = new ArrayList<String>();
		attributeLocalNames = new ArrayList<String>();
		attributePrefixes = new ArrayList<String>();
		// xsi
		xsiTypeUris = new ArrayList<String>();
		xsiTypeNames = new ArrayList<String>();
		xsiNils = new ArrayList<Boolean>();
		// misc
		docTypes = new ArrayList<String>();
		entityReferences = new ArrayList<String>();
		comments = new ArrayList<String>();
		uris = new ArrayList<String>();
		prefixes = new ArrayList<String>();
		piTargets = new ArrayList<String>();
		piDatas = new ArrayList<String>();

		// value events
		valueQNames = new ArrayList<ExpandedName>();
		occurrences = new HashMap<ExpandedName, Integer>();
		dataTypes = new HashMap<ExpandedName, List<Datatype>>();
	}

	@Override
	protected void initForEachRun() throws EXIException {
		super.initForEachRun();

		// next event
		nextEvent = new StartDocument();
		nextEventType = EventType.START_DOCUMENT;

		// events
		events.clear();
		eventTypes.clear();
		currentEventIndex = 0;

		// elements
		elementURIs.clear();
		currentElementURIIndex = 0;
		elementLocalNames.clear();
		currentElementLocalNameIndex = 0;
		elementPrefixes.clear();
		currentElementPrefixIndex = 0;
		// attributes
		attributeURIs.clear();
		currentAttributeURIIndex = 0;
		attributeLocalNames.clear();
		currentAttributeLocalNameIndex = 0;
		attributePrefixes.clear();
		currentAttributePrefixIndex = 0;
		// xsi
		xsiNils.clear();
		currentXsiNilsIndex = 0;
		// misc
		docTypes.clear();
		currentDocTypeIndex = 0;
		entityReferences.clear();
		currentEntityReferenceIndex = 0;
		comments.clear();
		currentCommentsIndex = 0;

		uris.clear();
		prefixes.clear();
		currentNamespacesIndex = 0;

		piTargets.clear();
		piDatas.clear();
		currentProcessingIntructionsIndex = 0;

		// count value items
		cntValues = 0;

		// store value events (qnames) in right order
		// plus necessary information to reconstruct value channels
		valueQNames.clear();
		occurrences.clear();
		dataTypes.clear();

		// possible root elements
		if (this.exiFactory.isFragment()) {
			// push stack with document grammar
			pushRule(grammar.getBuiltInFragmentGrammar());
		} else {
			// push stack with document grammar
			pushRule(grammar.getBuiltInDocumentGrammar());
		}

		// pre-read structure
		initStructure();
	}

	protected void initStructure() throws EXIException {
		try {
			boolean stillInitializing = true;

			while (stillInitializing) {
				// System.out.println( "NextEvent: " + nextEvent + " --> " +
				// nextEventType );

				events.add(nextEvent); // add event to array list
				eventTypes.add(nextEventType);

				switch (nextEventType) {
				case START_DOCUMENT:
					decodeStartDocumentInternal();
					break;
				case START_ELEMENT:
					decodeStartElementInternal();
					break;
				case START_ELEMENT_NS:
					decodeStartElementNSInternal();
					break;
				case START_ELEMENT_GENERIC:
					decodeStartElementGenericInternal();
					break;
				case START_ELEMENT_GENERIC_UNDECLARED:
					decodeStartElementUndeclaredInternal();
					break;
				case NAMESPACE_DECLARATION:
					decodeNamespaceDeclarationInternal();
					break;
				case ATTRIBUTE:
					decodeAttributeInternal();
					break;
				case ATTRIBUTE_NS:
					decodeAttributeNSInternal();
					break;
				case ATTRIBUTE_INVALID_VALUE:
					decodeAttributeInvalidValueInternal();
					break;
				case ATTRIBUTE_ANY_INVALID_VALUE:
					decodeAttributeAnyInvalidValueInternal();
					break;
				case ATTRIBUTE_GENERIC:
					decodeAttributeGenericInternal();
					break;
				case ATTRIBUTE_GENERIC_UNDECLARED:
					decodeAttributeGenericUndeclaredInternal();
					break;
				case ATTRIBUTE_XSI_TYPE:
					decodeAttributeXsiTypeInternal();
					break;
				case ATTRIBUTE_XSI_NIL:
					decodeAttributeXsiNilInternal();
					break;
				case CHARACTERS:
					decodeCharactersInternal();
					break;
				case CHARACTERS_GENERIC:
					decodeCharactersGenericInternal();
					break;
				case CHARACTERS_GENERIC_UNDECLARED:
					decodeCharactersUndeclaredInternal();
					break;
				case END_ELEMENT:
					decodeEndElementInternal();
					break;
				case END_ELEMENT_UNDECLARED:
					decodeEndElementUndeclaredInternal();
					break;
				case END_DOCUMENT:
					decodeEndDocumentInternal();
					stillInitializing = false;
					continue;
					// break;
				case DOC_TYPE:
					decodeDocTypeInternal();
					break;
				case ENTITY_REFERENCE:
					decodeEntityReferenceInternal();
					break;
				case COMMENT:
					decodeCommentInternal();
					break;
				case PROCESSING_INSTRUCTION:
					decodeProcessingInstructionInternal();
					break;
				default:
					throw new RuntimeException("Unknown Event " + nextEventType);
				}

				// decode next EventCode
				decodeEventCode();

			}

			// System.out.println( "Read all events ahead! EventSize: " +
			// events.size() + " & Values: " + cntValues );

			block.reconstructChannels(cntValues, valueQNames, dataTypes,
					occurrences);
		} catch (IOException e) {
			throw new EXIException(e);
		}

	}

	protected Event stepToNextEvent() {
		return events.get(currentEventIndex++);
	}

	public void inspectEvent() throws EXIException {
		// already checked event in structure stream
	}

	public boolean hasNextEvent() {
		// return ( events.size() > ( currentEventIndex ) );
		// ED --> no next event
		return (events.size() > (currentEventIndex + 1));
	}

	public EventType getNextEventType() {
		return eventTypes.get(currentEventIndex);
	}

	protected void incrementValues(ExpandedName qnameConent, Datatype datatype) {
		cntValues++;

		if (valueQNames.contains(qnameConent)) {
			occurrences.put(qnameConent, occurrences.get(qnameConent) + 1);
		} else {
			// new
			occurrences.put(qnameConent, 1);
			dataTypes.put(qnameConent, new ArrayList<Datatype>());
			valueQNames.add(qnameConent);
		}

		dataTypes.get(qnameConent).add(datatype);
	}

	protected void decodeStartDocumentInternal() throws EXIException {
		decodeStartDocumentStructure();
	}

	public void decodeStartDocument() throws EXIException {
		Event ev = stepToNextEvent();
		assert (ev.isEventType(EventType.START_DOCUMENT));
	}

	protected void decodeStartElementInternal() throws EXIException {
		decodeStartElementStructure();

		elementPrefixes.add(this.elementPrefix);
	}

	public void decodeStartElement() throws EXIException {
		// update element content
		StartElement se = ((StartElement) stepToNextEvent());
		this.elementURI = se.getNamespaceURI();
		this.elementLocalName = se.getLocalPart();
		this.elementPrefix = elementPrefixes.get(currentElementPrefixIndex++);

		pushScope(elementURI, elementLocalName);
	}

	protected void decodeStartElementGenericInternal() throws EXIException {
		decodeStartElementGenericStructure();

		elementURIs.add(elementURI);
		elementLocalNames.add(elementLocalName);
		elementPrefixes.add(this.elementPrefix);
	}

	protected void decodeStartElementNSInternal() throws EXIException {
		decodeStartElementNSStructure();

		elementLocalNames.add(this.elementLocalName);
		elementPrefixes.add(this.elementPrefix);
	}

	public void decodeStartElementNS() throws EXIException {
		StartElementNS seNS = ((StartElementNS) stepToNextEvent());
		this.elementURI = seNS.getNamespaceURI();
		this.elementLocalName = elementLocalNames
				.get(currentElementLocalNameIndex++);
		this.elementPrefix = elementPrefixes.get(currentElementPrefixIndex++);

		pushScope(elementURI, elementLocalName);
	}

	public void decodeStartElementGeneric() throws EXIException {
		stepToNextEvent();

		// update element content
		this.elementURI = elementURIs.get(currentElementURIIndex++);
		this.elementLocalName = elementLocalNames
				.get(currentElementLocalNameIndex++);
		this.elementPrefix = elementPrefixes.get(currentElementPrefixIndex++);

		pushScope(elementURI, elementLocalName);
	}

	protected void decodeStartElementUndeclaredInternal() throws EXIException {
		decodeStartElementGenericUndeclaredStructure();

		elementURIs.add(elementURI);
		elementLocalNames.add(elementLocalName);
		elementPrefixes.add(this.elementPrefix);
	}

	public void decodeStartElementGenericUndeclared() throws EXIException {
		stepToNextEvent();

		// update element content
		this.elementURI = elementURIs.get(currentElementURIIndex++);
		this.elementLocalName = elementLocalNames
				.get(currentElementLocalNameIndex++);
		this.elementPrefix = elementPrefixes.get(currentElementPrefixIndex++);

		pushScope(elementURI, elementLocalName);
	}

	protected void decodeNamespaceDeclarationInternal() throws EXIException {
		decodeNamespaceDeclarationStructure();
		uris.add(nsURI);
		prefixes.add(nsPrefix);
	}

	public void decodeNamespaceDeclaration() throws EXIException {
		Event ev = stepToNextEvent();

		assert (ev.isEventType(EventType.NAMESPACE_DECLARATION));

		nsURI = uris.get(currentNamespacesIndex);
		nsPrefix = prefixes.get(currentNamespacesIndex++);
	}

	protected void decodeAttributeInternal() throws EXIException {
		Attribute at = decodeAttributeStructure();

		attributePrefixes.add(this.attributePrefix);

		incrementValues(new ExpandedName(at.getNamespaceURI(), at
				.getLocalPart()), at.getDatatype());
	}

	public void decodeAttribute() throws EXIException {
		Attribute at = (Attribute) stepToNextEvent();
		this.attributeURI = at.getNamespaceURI();
		this.attributeLocalName = at.getLocalPart();
		this.attributePrefix = attributePrefixes
				.get(currentAttributePrefixIndex++);

		try {
			// decode attribute value
			this.attributeValue = block.readTypedValidValue(at.getDatatype(),
					attributeURI, attributeLocalName);
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	protected void decodeAttributeNSInternal() throws EXIException {
		decodeAttributeNSStructure();

		attributeLocalNames.add(attributeLocalName);
		attributePrefixes.add(this.attributePrefix);

		incrementValues(new ExpandedName(attributeURI, attributeLocalName),
				BuiltIn.DEFAULT_DATATYPE);
	}

	public void decodeAttributeNS() throws EXIException {
		try {
			AttributeNS atNS = (AttributeNS) stepToNextEvent();
			this.attributeURI = atNS.getNamespaceURI();
			this.attributeLocalName = attributeLocalNames
					.get(currentAttributeLocalNameIndex++);
			this.attributePrefix = attributePrefixes
					.get(currentAttributePrefixIndex++);

			// decode attribute value
			attributeValue = block.readValueAsString(attributeURI,
					attributeLocalName);
		} catch (IOException e) {
			throw new EXIException();
		}
	}

	protected void decodeAttributeInvalidValueInternal() throws EXIException {
		Attribute at = decodeAttributeStructure();

		attributePrefixes.add(this.attributePrefix);

		incrementValues(new ExpandedName(at.getNamespaceURI(), at
				.getLocalPart()), BuiltIn.DEFAULT_DATATYPE);
	}

	public void decodeAttributeInvalidValue() throws EXIException {
		try {

			Attribute at = (Attribute) stepToNextEvent();
			this.attributeURI = at.getNamespaceURI();
			this.attributeLocalName = at.getLocalPart();
			this.attributePrefix = attributePrefixes
					.get(currentAttributePrefixIndex++);

			// decode attribute value as string
			this.attributeValue = block.readValueAsString(attributeURI,
					attributeLocalName);
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	protected void decodeAttributeAnyInvalidValueInternal() throws EXIException {
		decodeAttributeAnyInvalidStructure();

		attributeURIs.add(attributeURI);
		attributeLocalNames.add(attributeLocalName);
		attributePrefixes.add(this.attributePrefix);

		incrementValues(new ExpandedName(attributeURI, attributeLocalName),
				BuiltIn.DEFAULT_DATATYPE);
	}

	public void decodeAttributeAnyInvalidValue() throws EXIException {
		try {
			stepToNextEvent();

			this.attributeURI = attributeURIs.get(currentAttributeURIIndex++);
			this.attributeLocalName = attributeLocalNames
					.get(currentAttributeLocalNameIndex++);
			this.attributePrefix = attributePrefixes
					.get(currentAttributePrefixIndex++);

			// decode attribute value
			attributeValue = block.readValueAsString(attributeURI,
					attributeLocalName);
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	protected void decodeAttributeGenericInternal() throws EXIException {
		decodeAttributeGenericStructure();

		attributeURIs.add(attributeURI);
		attributeLocalNames.add(attributeLocalName);
		attributePrefixes.add(this.attributePrefix);

		eventAT.setNamespaceURI(this.attributeURI);
		eventAT.setLocalPart(this.attributeLocalName);
		Datatype globalType = grammar.getGlobalAttributeDatatype(eventAT);
			
		if (globalType != null) {
			incrementValues(new ExpandedName(attributeURI, attributeLocalName),
					globalType);
		} else {
			// decode attribute value as string ??
			// attributeValue = block.readValueAsString(attributeURI,
			// attributeLocalName);
			throw new EXIException("No global datatype found for "
					+ eventAT);
		}
	}

	public void decodeAttributeGeneric() throws EXIException {
		try {
			stepToNextEvent();

			this.attributeURI = attributeURIs.get(currentAttributeURIIndex++);
			this.attributeLocalName = attributeLocalNames
					.get(currentAttributeLocalNameIndex++);
			this.attributePrefix = attributePrefixes
					.get(currentAttributePrefixIndex++);

			eventAT.setNamespaceURI(this.attributeURI);
			eventAT.setLocalPart(this.attributeLocalName);

			Datatype globalType = grammar.getGlobalAttributeDatatype(eventAT);

			if (globalType != null) {
				attributeValue = block.readTypedValidValue(globalType,
						attributeURI, attributeLocalName);
			} else {
				// decode attribute value as string ??
				// attributeValue = block.readValueAsString(attributeURI,
				// attributeLocalName);
				throw new EXIException("No global datatype found for "
						+ eventAT);
			}
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	protected void decodeAttributeGenericUndeclaredInternal()
			throws EXIException {
		decodeAttributeGenericUndeclaredStructure();

		attributeURIs.add(attributeURI);
		attributeLocalNames.add(attributeLocalName);
		attributePrefixes.add(this.attributePrefix);

		incrementValues(new ExpandedName(attributeURI, attributeLocalName),
				BuiltIn.DEFAULT_DATATYPE);
	}

	public void decodeAttributeGenericUndeclared() throws EXIException {
		try {
			stepToNextEvent();

			this.attributeURI = attributeURIs.get(currentAttributeURIIndex++);
			this.attributeLocalName = attributeLocalNames
					.get(currentAttributeLocalNameIndex++);
			this.attributePrefix = attributePrefixes
					.get(currentAttributePrefixIndex++);

			// decode attribute value
			attributeValue = block.readValueAsString(attributeURI,
					attributeLocalName);
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	protected void decodeAttributeXsiTypeInternal() throws EXIException {
		decodeAttributeXsiType();

		// update grammar according to given xsi:type
		TypeGrammar tg = ((GrammarSchemaInformed) grammar).getTypeGrammar(
				this.xsiTypeUri, this.xsiTypeName);

		// type known ?
		if (tg != null) {
			this.replaceRuleAtTheTop(tg.getType());

			//
			this.pushScopeType(this.xsiTypeUri, this.xsiTypeName);
		}

		// xsiType
		xsiTypeUris.add(xsiTypeUri);
		xsiTypeNames.add(xsiTypeName);
	}

	public void decodeXsiType() throws EXIException {
		stepToNextEvent();

		xsiTypeUri = xsiTypeUris.get(currentXsiTypeIndex);
		xsiTypeName = xsiTypeNames.get(currentXsiTypeIndex++);
	}

	protected void decodeAttributeXsiNilInternal() throws EXIException {
		decodeAttributeXsiNil();

		if (xsiNil) {
			if (currentRule instanceof SchemaInformedRule) {
				replaceRuleAtTheTop(((SchemaInformedRule) currentRule)
						.getTypeEmpty());
			} else {

			}
		}

		// xsiNil
		xsiNils.add(xsiNil);
	}

	public void decodeXsiNil() throws EXIException {
		stepToNextEvent();

		xsiNil = xsiNils.get(currentXsiNilsIndex++);
	}

	protected void decodeCharactersInternal() throws EXIException {
		try {
			Characters ch = decodeCharactersStructure();

			incrementValues(
					new ExpandedName(getScopeURI(), getScopeLocalName()), ch
							.getDatatype());
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	public void decodeCharacters() throws EXIException {
		try {
			Characters ch = (Characters) stepToNextEvent();

			characters = block.readTypedValidValue(ch.getDatatype(),
					getScopeURI(), getScopeLocalName());
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	protected void decodeCharactersGenericInternal() throws EXIException {
		decodeCharactersGenericStructure();

		// incrementValues( new ExpandedName( elementURI, elementLocalName ),
		// BuiltIn.DEFAULT_DATATYPE );
		incrementValues(new ExpandedName(getScopeURI(), getScopeLocalName()),
				BuiltIn.DEFAULT_DATATYPE);
	}

	public void decodeCharactersGeneric() throws EXIException {
		try {
			stepToNextEvent();

			// characters = block.readValueAsString ( elementURI,
			// elementLocalName );
			characters = block.readValueAsString(getScopeURI(),
					getScopeLocalName());
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	protected void decodeCharactersUndeclaredInternal() throws EXIException {
		decodeCharactersUndeclaredStructure();

		incrementValues(new ExpandedName(getScopeURI(), getScopeLocalName()),
				BuiltIn.DEFAULT_DATATYPE);
	}

	public void decodeCharactersGenericUndeclared() throws EXIException {
		try {
			stepToNextEvent();

			characters = block.readValueAsString(getScopeURI(),
					getScopeLocalName());
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	protected void decodeEndElementInternal() throws EXIException {
		decodeEndElementStructure();
	}

	public void decodeEndElement() throws EXIException {
		// Event ev = stepToNextEvent ( );
		stepToNextEvent();

		popScope();
		// assert ( ev.isEventType( EventType.END_ELEMENT ) );
	}

	protected void decodeEndElementUndeclaredInternal() throws EXIException {
		decodeEndElementUndeclaredStructure();
	}

	public void decodeEndElementUndeclared() throws EXIException {
		decodeEndElement();
	}

	protected void decodeEndDocumentInternal() throws EXIException {
		decodeEndDocumentStructure();
	}

	public void decodeEndDocument() throws EXIException {
		Event ev = stepToNextEvent();

		assert (ev.isEventType(EventType.END_DOCUMENT));
	}

	protected void decodeDocTypeInternal() throws EXIException {
		decodeDocTypeStructure();

		// DOCTYPE
		docTypes.add(docTypeName);
		docTypes.add(docTypePublicID);
		docTypes.add(docTypeSystemID);
		docTypes.add(docTypeText);
	}

	public void decodeDocType() throws EXIException {
		Event ev = stepToNextEvent();
		assert (ev.isEventType(EventType.DOC_TYPE));

		docTypeName = docTypes.get(currentDocTypeIndex++);
		docTypePublicID = docTypes.get(currentDocTypeIndex++);
		docTypeSystemID = docTypes.get(currentDocTypeIndex++);
		docTypeText = docTypes.get(currentDocTypeIndex++);
	}

	protected void decodeEntityReferenceInternal() throws EXIException {
		decodeEntityReferenceStructure();

		// entity reference
		entityReferences.add(entityReferenceName);
	}

	public void decodeEntityReference() throws EXIException {
		Event ev = stepToNextEvent();
		assert (ev.isEventType(EventType.ENTITY_REFERENCE));

		entityReferenceName = entityReferences
				.get(currentEntityReferenceIndex++);
	}

	protected void decodeCommentInternal() throws EXIException {
		decodeCommentStructure();

		// Comment
		comments.add(comment);
	}

	public void decodeComment() throws EXIException {
		Event ev = stepToNextEvent();

		assert (ev.isEventType(EventType.COMMENT));

		comment = comments.get(currentCommentsIndex++);
	}

	protected void decodeProcessingInstructionInternal() throws EXIException {
		decodeProcessingInstructionStructure();

		// ProcessingInstruction (Target, Data)
		piTargets.add(piTarget);
		piDatas.add(piData);
	}

	public void decodeProcessingInstruction() throws EXIException {
		Event ev = stepToNextEvent();

		assert (ev.isEventType(EventType.PROCESSING_INSTRUCTION));

		piTarget = piTargets.get(currentProcessingIntructionsIndex);
		piData = piDatas.get(currentProcessingIntructionsIndex++);
	}

	public void decodeStartFragmentSelfContained() throws EXIException {
		throw new EXIException(
				"SelfContained does NOT support reordered channels such as used in Compression and Pre-Compression mode");
	}

	public void decodeEndFragmentSelfContained() throws EXIException {
		throw new EXIException(
				"SelfContained does NOT support reordered channels such as used in Compression and Pre-Compression mode");
	}

}
