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

import java.io.IOException;
import java.util.List;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.core.container.DocType;
import com.siemens.ct.exi.core.container.NamespaceDeclaration;
import com.siemens.ct.exi.core.container.ProcessingInstruction;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.event.EventType;
import com.siemens.ct.exi.values.Value;

/**
 * EXI decoder for bit or byte-aligned streams and possible self-contained
 * elements.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.6
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

//	@Override
//	protected final void handleElementPrefix() throws IOException {
//		if (scDecoder == null) {
//			super.handleElementPrefix();
//		} else {
//			scDecoder.handleElementPrefix();
//		}
//	}
//	
//	@Override
//	protected final void handleAttributePrefix() throws IOException {
//		if (scDecoder == null) {
//			super.handleAttributePrefix();
//		} else {
//			scDecoder.handleAttributePrefix();
//		}
//	}
	
	// @Override
	// public void setInputStream(InputStream is)
	// throws EXIException, IOException {
	// if (scDecoder == null) {
	// super.setInputStream(is, exiBodyOnly);
	// } else {
	// System.err.println("TODO setInputStream");
	// }
	// }

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
	public QName decodeStartElement() throws EXIException, IOException {
		if (scDecoder == null) {
			return super.decodeStartElement();
		} else {
			return scDecoder.decodeStartElement();
		}
	}

	@Override
	public QName decodeStartElementNS() throws EXIException, IOException {
		if (scDecoder == null) {
			return super.decodeStartElementNS();
		} else {
			return scDecoder.decodeStartElementNS();
		}
	}

	@Override
	public QName decodeStartElementGeneric() throws EXIException, IOException {
		if (scDecoder == null) {
			return super.decodeStartElementGeneric();
		} else {
			return scDecoder.decodeStartElementGeneric();
		}
	}

	@Override
	public QName decodeStartElementGenericUndeclared() throws EXIException,
			IOException {
		if (scDecoder == null) {
			return super.decodeStartElementGenericUndeclared();
		} else {
			return scDecoder.decodeStartElementGenericUndeclared();
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
			scDecoder.decodeStartSelfContainedFragment();
		}
	}

	@Override
	public QName decodeEndElement() throws EXIException, IOException {
		if (scDecoder == null) {
			return super.decodeEndElement();
		} else {
			return scDecoder.decodeEndElement();
		}
	}

	@Override
	public QName decodeEndElementUndeclared() throws EXIException, IOException {
		if (scDecoder == null) {
			return super.decodeEndElementUndeclared();
		} else {
			return scDecoder.decodeEndElementUndeclared();
		}
	}

	@Override
	public QName decodeAttributeXsiNil() throws EXIException, IOException {
		if (scDecoder == null) {
			return super.decodeAttributeXsiNil();
		} else {
			return scDecoder.decodeAttributeXsiNil();
		}
	}

	@Override
	public QName decodeAttributeXsiType() throws EXIException, IOException {
		if (scDecoder == null) {
			return super.decodeAttributeXsiType();
		} else {
			return scDecoder.decodeAttributeXsiType();
		}
	}

	@Override
	public QName decodeAttribute() throws EXIException, IOException {
		if (scDecoder == null) {
			return super.decodeAttribute();
		} else {
			return scDecoder.decodeAttribute();
		}
	}

	@Override
	public QName decodeAttributeNS() throws EXIException, IOException {
		if (scDecoder == null) {
			return super.decodeAttributeNS();
		} else {
			return scDecoder.decodeAttributeNS();
		}
	}

	@Override
	public QName decodeAttributeInvalidValue() throws EXIException, IOException {
		if (scDecoder == null) {
			return super.decodeAttributeInvalidValue();
		} else {
			return scDecoder.decodeAttributeInvalidValue();
		}
	}

	@Override
	public QName decodeAttributeAnyInvalidValue() throws EXIException,
			IOException {
		if (scDecoder == null) {
			return super.decodeAttributeAnyInvalidValue();
		} else {
			return scDecoder.decodeAttributeAnyInvalidValue();
		}
	}

	@Override
	public QName decodeAttributeGeneric() throws EXIException, IOException {
		if (scDecoder == null) {
			return super.decodeAttributeGeneric();
		} else {
			return scDecoder.decodeAttributeGeneric();
		}
	}

	@Override
	public QName decodeAttributeGenericUndeclared() throws EXIException,
			IOException {
		if (scDecoder == null) {
			return super.decodeAttributeGenericUndeclared();
		} else {
			return scDecoder.decodeAttributeGenericUndeclared();
		}
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
	public Value decodeCharactersGeneric() throws EXIException, IOException {
		if (scDecoder == null) {
			return super.decodeCharactersGeneric();
		} else {
			return scDecoder.decodeCharactersGeneric();
		}
	}

	@Override
	public Value decodeCharactersGenericUndeclared() throws EXIException,
			IOException {
		if (scDecoder == null) {
			return super.decodeCharactersGenericUndeclared();
		} else {
			return scDecoder.decodeCharactersGenericUndeclared();
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

//	@Override
//	public String getStartElementQNameAsString() {
//		return (scDecoder == null ? super.getStartElementQNameAsString()
//				: scDecoder.getStartElementQNameAsString());
//	}
//
//	@Override
//	public String getEndElementQNameAsString() {
//		return (scDecoder == null ? super.getEndElementQNameAsString()
//				: scDecoder.getEndElementQNameAsString());
//	}
//
//	@Override
//	public String getAttributeQNameAsString() {
//		return (scDecoder == null ? super.getAttributeQNameAsString()
//				: scDecoder.getAttributeQNameAsString());
//	}
	
	@Override
	public String getElementPrefix() {
		return (scDecoder == null ? super.getElementPrefix()
				: scDecoder.getElementPrefix());
	}
	
	@Override
	public String getAttributePrefix() {
		return (scDecoder == null ? super.getAttributePrefix()
				: scDecoder.getAttributePrefix());
	}

	@Override
	public Value getAttributeValue() {
		return (scDecoder == null ? super.getAttributeValue() : scDecoder
				.getAttributeValue());
	}
}
