/*
 * Copyright (C) 2007-2014 Siemens AG
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
import java.util.List;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.core.container.DocType;
import com.siemens.ct.exi.core.container.NamespaceDeclaration;
import com.siemens.ct.exi.core.container.ProcessingInstruction;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammars.event.EventType;
import com.siemens.ct.exi.values.Value;

/**
 * EXI decoder for bit or byte-aligned streams and possible self-contained
 * elements.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.3-SNAPSHOT
 */

public class EXIBodyDecoderInOrderSC extends EXIBodyDecoderInOrder {

	protected EXIBodyDecoderInOrderSC scDecoder;

	public EXIBodyDecoderInOrderSC(EXIFactory exiFactory) throws EXIException {
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
	public EventType next() throws EXIException, IOException {
		// return (scDecoder == null ? super.next() : scDecoder.next());
		if (scDecoder == null) {
			return super.next();
		} else {
			EventType et = scDecoder.next();
			if (et == EventType.END_DOCUMENT) {
				scDecoder.decodeEndDocument();
				// Skip to the next byte-aligned boundary in the stream if it is
				// not already at such a boundary
				this.channel.align();
				// indicate that SC portion is over
				scDecoder = null;
				popElement();
				et = super.next();
			}
			// return next();
			return et;
		}
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
	public QNameContext decodeStartElement() throws EXIException, IOException {
		if (scDecoder == null) {
			return super.decodeStartElement();
		} else {
			return scDecoder.decodeStartElement();
		}
	}

	@Override
	public void decodeStartSelfContainedFragment() throws EXIException,
			IOException {
		if (scDecoder == null) {
			// SC Factory & Decoder
			EXIFactory scEXIFactory = exiFactory.clone();
			// scEXIFactory.setEXIBodyOnly(true);
			scEXIFactory.setFragment(true);
			scDecoder = (EXIBodyDecoderInOrderSC) scEXIFactory
					.createEXIBodyDecoder();
			scDecoder.channel = this.channel;
			scDecoder.setErrorHandler(this.errorHandler);
			scDecoder.initForEachRun();

			// Skip to the next byte-aligned boundary in the stream if it is not
			// already at such a boundary
			this.channel.align();

			// Evaluate the sequence of events (SD, SE(qname), content, ED)
			// according to the Fragment grammar
			scDecoder.decodeStartDocument();
			// this.hasNext(); // decode next event
			EventType et = next();
			switch (et) {
			case START_ELEMENT:
			case START_ELEMENT_NS:
			case START_ELEMENT_GENERIC:
			case START_ELEMENT_GENERIC_UNDECLARED:
				scDecoder.decodeStartElement();
				break;
			default:
				throw new RuntimeException("[EXI] Unsupported EventType " + et
						+ " in SelfContained Element");
			}
		} else {
			//
			scDecoder.decodeStartSelfContainedFragment();
		}
	}

	@Override
	public QNameContext decodeEndElement() throws EXIException, IOException {
		if (scDecoder == null) {
			return super.decodeEndElement();
		} else {
			return scDecoder.decodeEndElement();
		}
	}

	@Override
	public String getElementPrefix() {
		return (scDecoder == null ? super.getElementPrefix() : scDecoder
				.getElementPrefix());
	}

	@Override
	public String getElementQNameAsString() {
		return (scDecoder == null ? super.getElementQNameAsString() : scDecoder
				.getElementQNameAsString());
	}

	@Override
	public QNameContext decodeAttributeXsiNil() throws EXIException,
			IOException {
		if (scDecoder == null) {
			return super.decodeAttributeXsiNil();
		} else {
			return scDecoder.decodeAttributeXsiNil();
		}
	}

	@Override
	public QNameContext decodeAttributeXsiType() throws EXIException,
			IOException {
		if (scDecoder == null) {
			return super.decodeAttributeXsiType();
		} else {
			return scDecoder.decodeAttributeXsiType();
		}
	}

	@Override
	public QNameContext decodeAttribute() throws EXIException, IOException {
		if (scDecoder == null) {
			return super.decodeAttribute();
		} else {
			return scDecoder.decodeAttribute();
		}
	}

	@Override
	public String getAttributePrefix() {
		return (scDecoder == null ? super.getAttributePrefix() : scDecoder
				.getAttributePrefix());
	}

	@Override
	public String getAttributeQNameAsString() {
		return (scDecoder == null ? super.getAttributeQNameAsString()
				: scDecoder.getAttributeQNameAsString());
	}

	@Override
	public Value getAttributeValue() {
		return (scDecoder == null ? super.getAttributeValue() : scDecoder
				.getAttributeValue());
	}

	@Override
	public List<NamespaceDeclaration> getDeclaredPrefixDeclarations() {
		if (scDecoder == null) {
			return super.getDeclaredPrefixDeclarations();
		} else {
			return scDecoder.getDeclaredPrefixDeclarations();
		}
	}

	@Override
	public NamespaceDeclaration decodeNamespaceDeclaration()
			throws EXIException, IOException {
		if (scDecoder == null) {
			return super.decodeNamespaceDeclaration();
		} else {
			return scDecoder.decodeNamespaceDeclaration();
		}
	}

	@Override
	public Value decodeCharacters() throws EXIException, IOException {
		if (scDecoder == null) {
			return super.decodeCharacters();
		} else {
			return scDecoder.decodeCharacters();
		}
	}

	@Override
	public DocType decodeDocType() throws EXIException, IOException {
		if (scDecoder == null) {
			return super.decodeDocType();
		} else {
			return scDecoder.decodeDocType();
		}
	}

	@Override
	public char[] decodeEntityReference() throws EXIException, IOException {
		if (scDecoder == null) {
			return super.decodeEntityReference();
		} else {
			return scDecoder.decodeEntityReference();
		}
	}

	@Override
	public char[] decodeComment() throws EXIException, IOException {
		if (scDecoder == null) {
			return super.decodeComment();
		} else {
			return scDecoder.decodeComment();
		}
	}

	@Override
	public ProcessingInstruction decodeProcessingInstruction()
			throws EXIException, IOException {
		if (scDecoder == null) {
			return super.decodeProcessingInstruction();
		} else {
			return scDecoder.decodeProcessingInstruction();
		}
	}
}
