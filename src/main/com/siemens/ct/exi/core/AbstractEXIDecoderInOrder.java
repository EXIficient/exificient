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
import java.util.HashMap;
import java.util.Map;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.datatype.decoder.TypeDecoder;
import com.siemens.ct.exi.datatype.encoder.TypeEncoder;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.SchemaInformedGrammar;
import com.siemens.ct.exi.grammar.TypeGrammar;
import com.siemens.ct.exi.grammar.event.EventType;
import com.siemens.ct.exi.grammar.event.StartDocument;
import com.siemens.ct.exi.grammar.rule.Rule;
import com.siemens.ct.exi.grammar.rule.SchemaInformedRule;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.2.20081023
 */

public abstract class AbstractEXIDecoderInOrder extends AbstractEXIDecoder {
	public AbstractEXIDecoderInOrder(EXIFactory exiFactory) {
		super(exiFactory);
	}

	@Override
	protected void initForEachRun() throws EXIException {
		super.initForEachRun();

		nextEvent = new StartDocument();
		nextEventType = EventType.START_DOCUMENT;
	}

	public void inspectEvent() throws EXIException {
		decodeEventCode();
	}

	public boolean hasNextEvent() {
		return !(nextEventType == EventType.END_DOCUMENT && openRules.size() == 2);
		// return nextEventType != EventType.END_DOCUMENT;
	}

	public EventType getNextEventType() {
		return nextEventType;
	}

	public void decodeStartDocument() throws EXIException {
		decodeStartDocumentStructure();
	}

	public void decodeStartElement() throws EXIException {
		decodeStartElementStructure();
	}

	public void decodeStartElementGeneric() throws EXIException {
		decodeStartElementGenericStructure();
	}

	public void decodeStartElementGenericUndeclared() throws EXIException {
		decodeStartElementGenericUndeclaredStructure();
	}

	public void decodeNamespaceDeclaration() throws EXIException {
		decodeNamespaceDeclarationStructure();
	}

	public void decodeAttribute() throws EXIException {
		try {
			// decode attribute value
			attributeValue = block.readTypedValidValue(
					decodeAttributeStructure().getDatatype(), attributeURI,
					attributeLocalName);
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	public void decodeAttributeInvalidValue() throws EXIException {
		decodeAttributeStructure();

		try {
			// decode attribute value as string
			attributeValue = block.readValueAsString(attributeURI,
					attributeLocalName);
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	public void decodeAttributeGeneric() throws EXIException {
		try {
			decodeAttributeGenericStructure();

			// decode attribute value as string
			attributeValue = block.readValueAsString(attributeURI,
					attributeLocalName);
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	public void decodeAttributeGenericUndeclared() throws EXIException {
		try {
			decodeAttributeGenericUndeclaredStructure();

			// decode attribute value
			attributeValue = block.readValueAsString(attributeURI,
					attributeLocalName);
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	public void decodeXsiType() throws EXIException {
		decodeAttributeXsiType();

		// update grammar according to given xsi:type
		TypeGrammar tg = ((SchemaInformedGrammar) grammar).getTypeGrammar(
				this.xsiTypeUri, this.xsiTypeName);

		// type known ?
		if (tg != null) {
			this.replaceRuleAtTheTop(tg.getType());

			//
			this.pushScopeType(this.xsiTypeUri, this.xsiTypeName);
		}
	}

	public void decodeXsiNil() throws EXIException {
		decodeAttributeXsiNil();

		if (this.xsiNil) {
			// jump to typeEmpty
			if (currentRule instanceof SchemaInformedRule) {
				replaceRuleAtTheTop(((SchemaInformedRule) currentRule)
						.getTypeEmpty());
			} else {
				throw new EXIException("EXI, no typeEmpty for xsi:nil");
			}
		}
	}

	public void decodeXsiNilDeviation() throws EXIException {
		decodeAttributeXsiNilDeviation();
	}

	public void decodeCharacters() throws EXIException {
		try {
			characters = block.readTypedValidValue(decodeCharactersStructure()
					.getDatatype(), getScopeURI(), getScopeLocalName());
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	protected void decodeCharactersGenericValue() throws EXIException {
		try {
			characters = block.readValueAsString(getScopeURI(),
					getScopeLocalName());
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	public void decodeCharactersGeneric() throws EXIException {
		decodeCharactersGenericStructure();

		decodeCharactersGenericValue();
	}

	public void decodeCharactersGenericUndeclared() throws EXIException {
		decodeCharactersUndeclaredStructure();

		decodeCharactersGenericValue();
	}

	public void decodeEndElement() throws EXIException {
		decodeEndElementStructure();
	}

	public void decodeEndElementUndeclared() throws EXIException {
		decodeEndElementUndeclaredStructure();
	}

	public void decodeEndDocument() throws EXIException {
		decodeEndDocumentStructure();
	}

	public void decodeComment() throws EXIException {
		decodeCommentStructure();
	}

	public void decodeProcessingInstruction() throws EXIException {
		decodeProcessingInstructionStructure();
	}

	/*
	 * SELF_CONTAINED
	 */
	
	TypeDecoder scTypeDecoder;
	Map<String, Map<String, Rule>> scRuntimeDispatcher;
	
	public void decodeStartFragmentSelfContained() throws EXIException {
		try {
			// 1. Save the string table, grammars, namespace prefixes and any
			// implementation-specific state learned while processing this EXI
			// Body.
			// 2. Initialize the string table, grammars, namespace prefixes and
			// any implementation-specific state learned while processing this
			// EXI Body to the state they held just prior to processing this EXI
			// Body.
			// 3. Skip to the next byte-aligned boundary in the stream.
			block.skipToNextByteBoundary();
			//	string tables
			scTypeDecoder = this.block.getTypeDecoder();
			TypeEncoder te = this.exiFactory.createTypeEncoder();
			this.exiFactory.getGrammar().populateStringTable(te.getStringTable());
			//	runtime-rules
			scRuntimeDispatcher = this.runtimeDispatcher;
			this.runtimeDispatcher = new HashMap<String, Map<String, Rule>>();
			
			// 4. Let qname be the qname of the SE event immediately preceding
			// this SC event.
			// 5. Let content be the sequence of events following this SC event
			// that match the grammar for element qname, up to and including the
			// terminating EE event.
			// 6. Evaluate the sequence of events (SD, SE(qname), content, ED)
			// according to the Fragment grammar.
			this.replaceRuleAtTheTop(grammar.getBuiltInFragmentGrammar());
			replaceRuleAtTheTop(currentRule.get1stLevelRule(0));

			// inspect stream and detect next event
			inspectEvent();
			this.decodeStartElement();

			// remove the *duplicate* scope due to the additional SE
			this.popScope();
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}
	
	public void decodeEndFragmentSelfContained() throws EXIException {
		decodeEndDocument();
		
		// 7. Restore the string table, grammars, namespace prefixes and
		// implementation-specific state learned while processing this EXI
		// Body to that saved in step 1 above.
		this.block.setTypeDecoder(this.scTypeDecoder);
		this.runtimeDispatcher = this.scRuntimeDispatcher;
	}

}
