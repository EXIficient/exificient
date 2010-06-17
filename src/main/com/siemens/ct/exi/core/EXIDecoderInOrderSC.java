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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.event.EventType;
import com.siemens.ct.exi.values.Value;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.4.20090414
 */

public class EXIDecoderInOrderSC extends EXIDecoderInOrder {

	protected EXIDecoderInOrderSC scDecoder;

	public EXIDecoderInOrderSC(EXIFactory exiFactory) throws EXIException {
		super(exiFactory);
		assert (fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_SC));
	}

	@Override
	protected void initForEachRun() throws EXIException, IOException {
		super.initForEachRun();

		// clear possibly remaining decoder
		scDecoder = null;
	}

	@Override
	public void setInputStream(InputStream is, boolean exiBodyOnly)
			throws EXIException, IOException {
		if (scDecoder == null) {
			super.setInputStream(is, exiBodyOnly);
		} else {
			System.err.println("TODO setInputStream");
		}
	}

	@Override
	public boolean hasNext() throws EXIException, IOException {
		if ( scDecoder == null ) {
			return super.hasNext();
		} else {
			boolean bool = scDecoder.hasNext();
			if ( this.scDecoder.nextEventType == EventType.END_DOCUMENT ) {		
				scDecoder.decodeEndDocument();
				// Skip to the next byte-aligned boundary in the stream if it is
				// not already at such a boundary
				this.channel.align();
				// indicate that SC portion is over
				scDecoder = null;
				popElement();
				return super.hasNext();
			}
			return bool;
		}
	}

	@Override
	public EventType next() throws EXIException {
		return (scDecoder == null ? super.next() : scDecoder.next());
	}

	@Override
	public void decodeStartDocument() throws EXIException {
		if (scDecoder == null) {
			super.decodeStartDocument();
		} else {
			scDecoder.decodeStartDocument();
		}
	}

	@Override
	public void decodeEndDocument() throws EXIException, IOException {
		if (scDecoder == null) {
			super.decodeEndDocument();
		} else {
			throw new RuntimeException("[EXI] SC not closed properly?");
		}
	}

	@Override
	public void decodeStartElement() throws EXIException, IOException {
		if (scDecoder == null) {
			super.decodeStartElement();
		} else {
			scDecoder.decodeStartElement();
		}
	}

	@Override
	public void decodeStartElementNS() throws EXIException, IOException {
		if (scDecoder == null) {
			super.decodeStartElementNS();
		} else {
			scDecoder.decodeStartElementNS();
		}
	}

	@Override
	public void decodeStartElementGeneric() throws EXIException, IOException {
		if (scDecoder == null) {
			super.decodeStartElementGeneric();
		} else {
			scDecoder.decodeStartElementGeneric();
		}
	}

	@Override
	public void decodeStartElementGenericUndeclared() throws EXIException,
			IOException {
		if (scDecoder == null) {
			super.decodeStartElementGenericUndeclared();
		} else {
			scDecoder.decodeStartElementGenericUndeclared();
		}
	}

	@Override
	public void decodeStartFragmentSelfContained() throws EXIException,
			IOException {
		if (scDecoder == null) {
			//	SC Factory & Decoder
			EXIFactory scEXIFactory = exiFactory.clone();
			scEXIFactory.setEXIBodyOnly(true);
			scEXIFactory.setFragment(true);
			scDecoder = (EXIDecoderInOrderSC) scEXIFactory.createEXIDecoder();
			scDecoder.channel = this.channel;
			scDecoder.setErrorHandler(this.errorHandler);
			scDecoder.initForEachRun();

			// Skip to the next byte-aligned boundary in the stream if it is not
			// already at such a boundary
			this.channel.align();

			// Evaluate the sequence of events (SD, SE(qname), content, ED)
			// according to the Fragment grammar
			scDecoder.decodeStartDocument();
			this.hasNext(); // decode next event
			EventType et = this.next();
			switch (et) {
			case START_ELEMENT:
				scDecoder.decodeStartElement();
				break;
			case START_ELEMENT_GENERIC:
				scDecoder.decodeStartElementGeneric();
				break;
			case START_ELEMENT_GENERIC_UNDECLARED:
				scDecoder.decodeStartElementGenericUndeclared();
				break;
			case START_ELEMENT_NS:
				scDecoder.decodeStartElementNS();
				break;
			default:
				throw new RuntimeException("[EXI] Unsupported EventType " + et
						+ " in SelfContained Element");
			}
		} else {
			// 
			scDecoder.decodeStartFragmentSelfContained();
		}
		

	}

	@Override
	public void decodeEndElement() throws EXIException, IOException {
		if (scDecoder == null) {
			super.decodeEndElement();
		} else {
			scDecoder.decodeEndElement();
		}
	}

	@Override
	public void decodeEndElementUndeclared() throws EXIException, IOException {
		if (scDecoder == null) {
			super.decodeEndElementUndeclared();
		} else {
			scDecoder.decodeEndElementUndeclared();
		}
	}

	@Override
	public void decodeAttributeXsiNil() throws EXIException, IOException {
		if (scDecoder == null) {
			super.decodeAttributeXsiNil();
		} else {
			scDecoder.decodeAttributeXsiNil();
		}
	}

	@Override
	public void decodeAttributeXsiType() throws EXIException, IOException {
		if (scDecoder == null) {
			super.decodeAttributeXsiType();
		} else {
			scDecoder.decodeAttributeXsiType();
		}
	}

	@Override
	public void decodeAttribute() throws EXIException, IOException {
		if (scDecoder == null) {
			super.decodeAttribute();
		} else {
			scDecoder.decodeAttribute();
		}
	}

	@Override
	public void decodeAttributeNS() throws EXIException, IOException {
		if (scDecoder == null) {
			super.decodeAttributeNS();
		} else {
			scDecoder.decodeAttributeNS();
		}
	}

	@Override
	public void decodeAttributeInvalidValue() throws EXIException, IOException {
		if (scDecoder == null) {
			super.decodeAttributeInvalidValue();
		} else {
			scDecoder.decodeAttributeInvalidValue();
		}
	}

	@Override
	public void decodeAttributeAnyInvalidValue() throws EXIException,
			IOException {
		if (scDecoder == null) {
			super.decodeAttributeAnyInvalidValue();
		} else {
			scDecoder.decodeAttributeAnyInvalidValue();
		}
	}

	@Override
	public void decodeAttributeGeneric() throws EXIException, IOException {
		if (scDecoder == null) {
			super.decodeAttributeGeneric();
		} else {
			scDecoder.decodeAttributeGeneric();
		}
	}

	@Override
	public void decodeAttributeGenericUndeclared() throws EXIException,
			IOException {
		if (scDecoder == null) {
			super.decodeAttributeGenericUndeclared();
		} else {
			scDecoder.decodeAttributeGenericUndeclared();
		}
	}


	@Override
	public List<PrefixMapping> getPrefixDeclarations() {
		if (scDecoder == null) {
			return super.getPrefixDeclarations();
		} else {
			return scDecoder.getPrefixDeclarations();
		}
	}
	
	@Override
	public void decodeNamespaceDeclaration() throws EXIException, IOException {
		if (scDecoder == null) {
			super.decodeNamespaceDeclaration();
		} else {
			scDecoder.decodeNamespaceDeclaration();
		}
	}

	@Override
	public void decodeCharacters() throws EXIException, IOException {
		if (scDecoder == null) {
			super.decodeCharacters();
		} else {
			scDecoder.decodeCharacters();
		}
	}

	@Override
	public void decodeCharactersGeneric() throws EXIException, IOException {
		if (scDecoder == null) {
			super.decodeCharactersGeneric();
		} else {
			scDecoder.decodeCharactersGeneric();
		}
	}

	@Override
	public void decodeCharactersGenericUndeclared() throws EXIException,
			IOException {
		if (scDecoder == null) {
			super.decodeCharactersGenericUndeclared();
		} else {
			scDecoder.decodeCharactersGenericUndeclared();
		}
	}

	@Override
	public void decodeDocType() throws EXIException, IOException {
		if (scDecoder == null) {
			super.decodeDocType();
		} else {
			scDecoder.decodeDocType();
		}
	}

	@Override
	public void decodeEntityReference() throws EXIException, IOException {
		if (scDecoder == null) {
			super.decodeEntityReference();
		} else {
			scDecoder.decodeEntityReference();
		}
	}

	@Override
	public void decodeComment() throws EXIException, IOException {
		if (scDecoder == null) {
			super.decodeComment();
		} else {
			scDecoder.decodeComment();
		}
	}

	@Override
	public void decodeProcessingInstruction() throws EXIException, IOException {
		if (scDecoder == null) {
			super.decodeProcessingInstruction();
		} else {
			scDecoder.decodeProcessingInstruction();
		}
	}

	@Override
	public QName getElementQName() {
		return (scDecoder == null ? super.getElementQName() : scDecoder
				.getElementQName());
	}
	
	@Override
	public String getStartElementQNameAsString() {
		return (scDecoder == null ? super.getStartElementQNameAsString() : scDecoder
				.getStartElementQNameAsString());
	}
	
	@Override
	public String getEndElementQNameAsString() {
		return (scDecoder == null ? super.getEndElementQNameAsString() : scDecoder
				.getEndElementQNameAsString());
	}

	@Override
	public QName getAttributeQName() {
		return (scDecoder == null ? super.getAttributeQName() : scDecoder
				.getAttributeQName());
	}
	
	@Override
	public String getAttributeQNameAsString() {
		return (scDecoder == null ? super.getAttributeQNameAsString() : scDecoder
				.getAttributeQNameAsString());
	}

	@Override
	public Value getAttributeValue() {
		return (scDecoder == null ? super.getAttributeValue() : scDecoder
				.getAttributeValue());
	}

	@Override
	public Value getCharactersValue() {
		return (scDecoder == null ? super.getCharactersValue() : scDecoder
				.getCharactersValue());
	}

	@Override
	public String getDocTypeName() {
		return (scDecoder == null ? super.getDocTypeName() : scDecoder
				.getDocTypeName());
	}

	@Override
	public String getDocTypePublicID() {
		return (scDecoder == null ? super.getDocTypePublicID() : scDecoder
				.getDocTypePublicID());
	}

	@Override
	public String getDocTypeSystemID() {
		return (scDecoder == null ? super.getDocTypeSystemID() : scDecoder
				.getDocTypeSystemID());
	}

	@Override
	public String getDocTypeText() {
		return (scDecoder == null ? super.getDocTypeText() : scDecoder
				.getDocTypeText());
	}

	@Override
	public String getEntityReferenceName() {
		return (scDecoder == null ? super.getEntityReferenceName() : scDecoder
				.getEntityReferenceName());
	}

	@Override
	public char[] getComment() {
		return (scDecoder == null ? super.getComment() : scDecoder.getComment());
	}

	@Override
	public String getPITarget() {
		return (scDecoder == null ? super.getPITarget() : scDecoder
				.getPITarget());
	}

	@Override
	public String getPIData() {
		return (scDecoder == null ? super.getPIData() : scDecoder.getPIData());
	}
}
