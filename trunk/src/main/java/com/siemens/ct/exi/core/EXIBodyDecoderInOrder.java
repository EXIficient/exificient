/*
 * Copyright (c) 2007-2015 Siemens AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */

package com.siemens.ct.exi.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.siemens.ct.exi.CodingMode;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.core.container.DocType;
import com.siemens.ct.exi.core.container.NamespaceDeclaration;
import com.siemens.ct.exi.core.container.ProcessingInstruction;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammars.event.EventType;
import com.siemens.ct.exi.io.channel.BitDecoderChannel;
import com.siemens.ct.exi.io.channel.ByteDecoderChannel;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.types.BuiltIn;
import com.siemens.ct.exi.values.Value;

/**
 * EXI decoder for bit or byte-aligned streams.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.5-SNAPSHOT
 */

public class EXIBodyDecoderInOrder extends AbstractEXIBodyDecoder {

	public EXIBodyDecoderInOrder(EXIFactory exiFactory) throws EXIException {
		super(exiFactory);
	}

	public void setInputStream(InputStream is) throws EXIException, IOException {
		updateInputStream(is);
		
		initForEachRun();
	}

	public void setInputChannel(DecoderChannel decoderChannel)
			throws EXIException, IOException {
		updateInputChannel(decoderChannel);

		initForEachRun();
	}
	
	public void updateInputStream(InputStream is) throws EXIException, IOException {
		CodingMode codingMode = exiFactory.getCodingMode();

		// setup data-stream only
		if (codingMode == CodingMode.BIT_PACKED) {
			// create new bit-aligned channel
			updateInputChannel(new BitDecoderChannel(is));
		} else {
			assert (codingMode == CodingMode.BYTE_PACKED);
			// create new byte-aligned channel
			updateInputChannel(new ByteDecoderChannel(is));
		}
	}
	
	public void updateInputChannel(DecoderChannel decoderChannel)
			throws EXIException, IOException {
		this.channel = decoderChannel;
	}

	public DecoderChannel getChannel() {
		return this.channel;
	}

	@Override
	protected void initForEachRun() throws EXIException, IOException {
		super.initForEachRun();

		nextEvent = null;
		nextEventType = EventType.START_DOCUMENT;
	}

	public EventType next() throws EXIException, IOException {
		return nextEventType == EventType.END_DOCUMENT ? null
				: decodeEventCode();
	}

	public void decodeStartDocument() throws EXIException {
		decodeStartDocumentStructure();
	}

	public void decodeEndDocument() throws EXIException, IOException {
		decodeEndDocumentStructure();
	}

	public QNameContext decodeStartElement() throws EXIException, IOException {
		switch (this.nextEventType) {
		case START_ELEMENT:
			return decodeStartElementStructure();
			// break;
		case START_ELEMENT_NS:
			return decodeStartElementNSStructure();
			// break;
		case START_ELEMENT_GENERIC:
			return decodeStartElementGenericStructure();
			// break;
		case START_ELEMENT_GENERIC_UNDECLARED:
			return decodeStartElementGenericUndeclaredStructure();
			// break;
		default:
			throw new EXIException("Invalid decode state: "
					+ this.nextEventType);
		}
	}

	public QNameContext decodeEndElement() throws EXIException, IOException {
		ElementContext ec;
		switch (this.nextEventType) {
		case END_ELEMENT:
			ec = decodeEndElementStructure();
			break;
		case END_ELEMENT_UNDECLARED:
			ec = decodeEndElementUndeclaredStructure();
			break;
		default:
			throw new EXIException("Invalid decode state: "
					+ this.nextEventType);
		}
		return ec.qnameContext;
	}

	public List<NamespaceDeclaration> getDeclaredPrefixDeclarations() {
		return getElementContext().nsDeclarations;
	}

	public String getElementPrefix() {
		return this.getElementContext().getPrefix();
	}

	public String getElementQNameAsString() {
		return this.getElementContext().getQNameAsString();
	}

	public NamespaceDeclaration decodeNamespaceDeclaration()
			throws EXIException, IOException {
		return decodeNamespaceDeclarationStructure();
	}

	public QNameContext decodeAttributeXsiNil() throws EXIException,
			IOException {
		assert (nextEventType == EventType.ATTRIBUTE_XSI_NIL);
		decodeAttributeXsiNilStructure();

		// return attributeQName;
		return this.attributeQNameContext;
	}

	public QNameContext decodeAttributeXsiType() throws EXIException,
			IOException {
		assert (nextEventType == EventType.ATTRIBUTE_XSI_TYPE);
		decodeAttributeXsiTypeStructure();

		return this.attributeQNameContext;
	}

	protected void readAttributeContent(Datatype dt) throws IOException {
		attributeValue = typeDecoder.readValue(dt, attributeQNameContext,
				channel, stringDecoder);
	}

	protected void readAttributeContent() throws IOException, EXIException {
		if (attributeQNameContext.getNamespaceUriID() == getXsiTypeContext().getNamespaceUriID()) {
			int localNameID = attributeQNameContext.getLocalNameID();
			if (localNameID == getXsiTypeContext()
					.getLocalNameID()) {
				decodeAttributeXsiTypeStructure();
			} else if (localNameID == getXsiTypeContext()
					.getLocalNameID() && getCurrentGrammar().isSchemaInformed()) {
				decodeAttributeXsiNilStructure();
			} else {
				readAttributeContent(BuiltIn.DEFAULT_DATATYPE);
			}
		} else {
			// Attribute globalAT;
			Datatype dt = BuiltIn.DEFAULT_DATATYPE;

			if (getCurrentGrammar().isSchemaInformed()
					&& attributeQNameContext.getGlobalAttribute() != null) {
				dt = attributeQNameContext.getGlobalAttribute().getDatatype();
			}

			readAttributeContent(dt);
		}
	}

	public QNameContext decodeAttribute() throws EXIException, IOException {
		switch (this.nextEventType) {
		case ATTRIBUTE:
			Datatype dt = decodeAttributeStructure();
			if (this.attributeQNameContext.equals(getXsiTypeContext())) {
				decodeAttributeXsiTypeStructure();
			} else {
				readAttributeContent(dt);
			}
			break;
		case ATTRIBUTE_NS:
			decodeAttributeNSStructure();
			readAttributeContent();
			break;
		case ATTRIBUTE_GENERIC:
			decodeAttributeGenericStructure();
			readAttributeContent();
			break;
		case ATTRIBUTE_GENERIC_UNDECLARED:
			decodeAttributeGenericUndeclaredStructure();
			readAttributeContent();
			break;
		case ATTRIBUTE_INVALID_VALUE:
			decodeAttributeStructure();
			// Note: attribute content datatype is not the right one (invalid)
			readAttributeContent(BuiltIn.DEFAULT_DATATYPE);
			break;
		case ATTRIBUTE_ANY_INVALID_VALUE:
			decodeAttributeAnyInvalidValueStructure();
			readAttributeContent(BuiltIn.DEFAULT_DATATYPE);
			break;
		default:
			throw new EXIException("Invalid decode state: "
					+ this.nextEventType);
		}

		return attributeQNameContext;
	}

	public Value decodeCharacters() throws EXIException, IOException {
		Datatype dt;
		switch (this.nextEventType) {
		case CHARACTERS:
			dt = decodeCharactersStructure();
			break;
		case CHARACTERS_GENERIC:
			decodeCharactersGenericStructure();
			dt = BuiltIn.DEFAULT_DATATYPE;
			break;
		case CHARACTERS_GENERIC_UNDECLARED:
			decodeCharactersGenericUndeclaredStructure();
			dt = BuiltIn.DEFAULT_DATATYPE;
			break;
		default:
			throw new EXIException("Invalid decode state: "
					+ this.nextEventType);
		}

		// structure & content
		return typeDecoder.readValue(dt, getElementContext().qnameContext,
				channel, stringDecoder);
	}
	
	public char[] decodeEntityReference() throws EXIException, IOException {
		return decodeEntityReferenceStructure();
	}

	public char[] decodeComment() throws EXIException, IOException {
		return decodeCommentStructure();
	}

	public ProcessingInstruction decodeProcessingInstruction()
			throws EXIException, IOException {
		return decodeProcessingInstructionStructure();
	}

	public DocType decodeDocType() throws EXIException, IOException {
		return decodeDocTypeStructure();
	}

}
