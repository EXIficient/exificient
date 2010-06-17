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
import java.io.OutputStream;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.exceptions.ErrorHandler;
import com.siemens.ct.exi.grammar.event.EventType;

/**
 * Encoder for SELF_CONTAINED elements
 * 
 * <p>
 * All productions of the form LeftHandSide : SC Fragment are evaluated as
 * follows:
 * </p>
 * <ol>
 * <li>Save the string table, grammars and any implementation-specific state
 * learned while processing this EXI Body.</li>
 * <li>Initialize the string table, grammars and any implementation-specific
 * state learned while processing this EXI Body to the state they held just
 * prior to processing this EXI Body.</li>
 * <li>Skip to the next byte-aligned boundary in the stream if it is not already
 * at such a boundary.</li>
 * <li>Let qname be the qname of the SE event immediately preceding this SC
 * event.</li>
 * <li>Let content be the sequence of events following this SC event that match
 * the grammar for element qname, up to and including the terminating EE event.</li>
 * <li>Evaluate the sequence of events (SD, SE(qname), content, ED) according to
 * the Fragment grammar.</li>
 * <li>Skip to the next byte-aligned boundary in the stream if it is not already
 * at such a boundary.</li>
 * <li>Restore the string table, grammars and implementation-specific state
 * learned while processing this EXI Body to that saved in step 1 above.</li>
 * </ol>
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.4.20090414
 */

public class EXIEncoderInOrderSC extends EXIEncoderInOrder {

	protected EXIEncoderInOrderSC scEncoder;

	public EXIEncoderInOrderSC(EXIFactory exiFactory) throws EXIException {
		super(exiFactory);
	}

	@Override
	protected void initForEachRun() throws EXIException, IOException {
		super.initForEachRun();

		// clear possibly remaining encoder
		scEncoder = null;
	}

	@Override
	public void setOutput(OutputStream os, boolean exiBodyOnly)
			throws EXIException {
		if (scEncoder == null) {
			super.setOutput(os, exiBodyOnly);
		} else {
			scEncoder.setOutput(os, exiBodyOnly);
		}
	}

	@Override
	public void setErrorHandler(ErrorHandler errorHandler) {
		if (scEncoder == null) {
			super.setErrorHandler(errorHandler);
		} else {
			scEncoder.setErrorHandler(errorHandler);
		}
	}

	@Override
	public void encodeStartDocument() throws EXIException, IOException {
		if (scEncoder == null) {
			super.encodeStartDocument();
		} else {
			scEncoder.encodeStartDocument();
		}
	}

	@Override
	public void encodeEndDocument() throws EXIException, IOException {
		if (scEncoder == null) {
			super.encodeEndDocument();
		} else {
			scEncoder.encodeEndDocument();
		}
	}

	@Override
	public void encodeStartElement(String uri, String localName, String prefix)
			throws EXIException, IOException {
		QName qname;
		// business as usual
		if (scEncoder == null) {
			super.encodeStartElement(uri, localName, prefix);
			qname = elementContext.qname;
			
			// start SC fragment ?
			if (exiFactory.isSelfContainedElement(qname)) {
				int ec2 = currentRule.get2ndLevelEventCode(EventType.SELF_CONTAINED, fidelityOptions);
				encode2ndLevelEventCode(ec2);
				
				//	SC Factory & Encoder
				EXIFactory scEXIFactory = exiFactory.clone();
				scEXIFactory.setEXIBodyOnly(true);
				scEXIFactory.setFragment(true);
				scEncoder = (EXIEncoderInOrderSC) scEXIFactory.createEXIEncoder();
				scEncoder.os = this.os; // needs to be unequal null
				scEncoder.channel = this.channel;
				scEncoder.setErrorHandler(this.errorHandler);

				// Skip to the next byte-aligned boundary in the stream if it is not
				// already at such a boundary
				this.channel.align();

				// Evaluate the sequence of events (SD, SE(qname), content, ED)
				// according to the Fragment grammar
				scEncoder.encodeStartDocument();
				scEncoder.encodeStartElementNoSC(uri, localName, prefix);
				// from now on content events are forwarded to the scEncoder
			}
		} else {
			scEncoder.encodeStartElement(uri, localName, prefix);
			qname = scEncoder.elementContext.qname;
		}
	}
	
	protected void encodeStartElementNoSC(String uri, String localName, String prefix)
	throws EXIException, IOException {
		super.encodeStartElement(uri, localName, prefix);
	}

	@Override
	public void encodeEndElement() throws EXIException, IOException {
		if (scEncoder == null) {
			super.encodeEndElement();
		} else {
			//	fetch qname before EE
			QName qname = scEncoder.elementContext.qname;
			// EE
			scEncoder.encodeEndElement();
			if ( getElementContextQName().equals(qname) ) {
				// end SC fragment
				scEncoder.encodeEndDocument();
				// Skip to the next byte-aligned boundary in the stream if it is
				// not already at such a boundary
				this.channel.align();
				// indicate that SC portion is over
				scEncoder = null;
				super.popElement();
			}
			
			// NOTE: NO outer EE
			// Spec says "Evaluate the sequence of events (SD, SE(qname), content, ED) .."
			// e.g., "sc" is self-Contained element
			// Sequence: <sc>foo</sc>
			// --> SE(sc) --> SC --> SD --> SE(sc) --> CH --> EE --> ED
			// content == SE(sc) --> CH --> EE
			
		}
	}

	@Override
	public void encodeAttribute(String uri, String localName, String prefix,
			String value) throws EXIException, IOException {
		if (scEncoder == null) {
			super.encodeAttribute(uri, localName, prefix, value);
		} else {
			scEncoder.encodeAttribute(uri, localName, prefix, value);
		}
	}

	@Override
	public void encodeNamespaceDeclaration(String uri, String prefix)
			throws EXIException, IOException {
		if (scEncoder == null) {
			super.encodeNamespaceDeclaration(uri, prefix);
		} else {
			scEncoder.encodeNamespaceDeclaration(uri, prefix);
		}
	}

	@Override
	public void encodeXsiNil(String value, String pfx) throws EXIException, IOException {
		if (scEncoder == null) {
			super.encodeXsiNil(value, pfx);
		} else {
			scEncoder.encodeXsiNil(value, pfx);
		}
	}

	@Override
	public void encodeXsiType(String xsiTypeRaw, String pfx) throws EXIException,
			IOException {
		if (scEncoder == null) {
			super.encodeXsiType(xsiTypeRaw, pfx);
		} else {
			scEncoder.encodeXsiType(xsiTypeRaw, pfx);
		}
	}

	@Override
	public void encodeCharacters(String chars) throws EXIException, IOException {
		if (scEncoder == null) {
			super.encodeCharacters(chars);
		} else {
			scEncoder.encodeCharacters(chars);
		}
	}

	@Override
	public void encodeDocType(String name, String publicID, String systemID,
			String text) throws EXIException, IOException {
		if (scEncoder == null) {
			super.encodeDocType(name, publicID, systemID, text);
		} else {
			scEncoder.encodeDocType(name, publicID, systemID, text);
		}
	}

	@Override
	public void encodeEntityReference(String name) throws EXIException,
			IOException {
		if (scEncoder == null) {
			super.encodeEntityReference(name);
		} else {
			scEncoder.encodeEntityReference(name);
		}
	}

	@Override
	public void encodeComment(char[] ch, int start, int length)
			throws EXIException, IOException {
		if (scEncoder == null) {
			super.encodeComment(ch, start, length);
		} else {
			scEncoder.encodeComment(ch, start, length);
		}
	}

	@Override
	public void encodeProcessingInstruction(String target, String data)
			throws EXIException, IOException {
		if (scEncoder == null) {
			super.encodeProcessingInstruction(target, data);
		} else {
			scEncoder.encodeProcessingInstruction(target, data);
		}
	}
}
