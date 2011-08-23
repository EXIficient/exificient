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
import java.io.InputStream;
import java.util.List;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.CodingMode;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.core.container.DocType;
import com.siemens.ct.exi.core.container.NamespaceDeclaration;
import com.siemens.ct.exi.core.container.ProcessingInstruction;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.event.Attribute;
import com.siemens.ct.exi.grammar.event.EventType;
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
 * @version 0.7
 */

public class EXIBodyDecoderInOrder extends AbstractEXIBodyDecoder {

	public EXIBodyDecoderInOrder(EXIFactory exiFactory) throws EXIException {
		super(exiFactory);
	}

	public void setInputStream(InputStream is) throws EXIException, IOException {

//		// buffer stream if not already
//		// TODO is there a *nice* way to detect whether a stream is buffered
//		// already
//		if (!(is instanceof BufferedInputStream)) {
//			is = new BufferedInputStream(is);
//		}

		CodingMode codingMode = exiFactory.getCodingMode();

		// setup data-stream only
		if (codingMode == CodingMode.BIT_PACKED) {
			// create new bit-aligned channel
			setInputChannel(new BitDecoderChannel(is));
		} else {
			assert (codingMode == CodingMode.BYTE_PACKED);
			// create new byte-aligned channel
			setInputChannel(new ByteDecoderChannel(is));
		}

		initForEachRun();
	}

	public void setInputChannel(DecoderChannel decoderChannel)
			throws EXIException, IOException {
		this.channel = decoderChannel;
		// this.is = decoderChannel.geInputStream();

		initForEachRun();
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

	public QName decodeStartElement() throws EXIException, IOException {
		return decodeStartElementStructure();
	}

	public QName decodeStartElementNS() throws EXIException, IOException {
		return decodeStartElementNSStructure();
	}

	public QName decodeStartElementGeneric() throws EXIException, IOException {
		return decodeStartElementGenericStructure();
	}

	public QName decodeStartElementGenericUndeclared() throws EXIException,
			IOException {
		return decodeStartElementGenericUndeclaredStructure();
	}

	public QName decodeEndElement() throws EXIException, IOException {
		return decodeEndElementStructure().qname;
	}

	public QName decodeEndElementUndeclared() throws EXIException, IOException {
		return decodeEndElementUndeclaredStructure().qname;
	}

	public List<NamespaceDeclaration> getDeclaredPrefixDeclarations() {
		return elementContext.nsDeclarations;
	}

	public String getElementPrefix() {
		return this.elementContext.prefix;
	}

	public String getElementQNameAsString() {
		return this.elementContext.getQNameAsString();
	}

	public NamespaceDeclaration decodeNamespaceDeclaration()
			throws EXIException, IOException {
		return decodeNamespaceDeclarationStructure();
	}

	public QName decodeAttributeXsiNil() throws EXIException, IOException {
		assert (nextEventType == EventType.ATTRIBUTE_XSI_NIL);
		decodeAttributeXsiNilStructure();

		return attributeQName;
	}

	public QName decodeAttributeXsiType() throws EXIException, IOException {
		assert (nextEventType == EventType.ATTRIBUTE_XSI_TYPE);
		decodeAttributeXsiTypeStructure();
//		handleAttributeXsiTypeValue();
		
		return attributeQName;
	}

	protected void readAttributeContent(Datatype dt) throws IOException {
		attributeValue = typeDecoder.readValue(dt, attributeQName, channel);
	}

	public QName decodeAttribute() throws EXIException, IOException {
		// structure & content
		Datatype dt = decodeAttributeStructure();

		if (attributeQName.equals(XSI_TYPE)) {
			decodeAttributeXsiTypeStructure();
//			handleAttributeXsiTypeValue();
		} else {
			readAttributeContent(dt);
		}
		return attributeQName;
	}

	public QName decodeAttributeInvalidValue() throws EXIException, IOException {
		// structure
		decodeAttributeStructure();

		// Note: attribute content datatype is not the right one (invalid)
		readAttributeContent(BuiltIn.DEFAULT_DATATYPE);
		return attributeQName;
	}

	public QName decodeAttributeAnyInvalidValue() throws EXIException,
			IOException {
		// structure
		decodeAttributeAnyInvalidValueStructure();
		// content
		readAttributeContent(BuiltIn.DEFAULT_DATATYPE);
		return attributeQName;
	}

	protected void readAttributeContent() throws IOException, EXIException {
		if (XSI_TYPE.equals(attributeQName)) {
			decodeAttributeXsiTypeStructure();
//			handleAttributeXsiTypeValue();
		} else if (XSI_NIL.equals(attributeQName)
				&& currentRule.isSchemaInformed()) {
			decodeAttributeXsiNilStructure();
		} else {
			Attribute globalAT;
			Datatype dt = BuiltIn.DEFAULT_DATATYPE;
			if (currentRule.isSchemaInformed()
					&& (globalAT = grammar.getGlobalAttribute(attributeQName)) != null) {
				dt = globalAT.getDatatype();
			}
			readAttributeContent(dt);
		}
	}

	public QName decodeAttributeNS() throws EXIException, IOException {
		// structure
		decodeAttributeNSStructure();
		// content
		readAttributeContent();
		return attributeQName;
	}

	public QName decodeAttributeGeneric() throws EXIException, IOException {
		// structure
		decodeAttributeGenericStructure();
		// content
		readAttributeContent();
		return attributeQName;
	}

	public QName decodeAttributeGenericUndeclared() throws EXIException,
			IOException {
		// structure
		decodeAttributeGenericUndeclaredStructure();
		// content
		readAttributeContent();
		return attributeQName;
	}

	public Value decodeCharacters() throws EXIException, IOException {
		// structure & content
		return typeDecoder.readValue(decodeCharactersStructure(),
				elementContext.qname, channel);
	}

	public Value decodeCharactersGeneric() throws EXIException, IOException {
		// structure
		decodeCharactersGenericStructure();
		// content
		return typeDecoder.readValue(BuiltIn.DEFAULT_DATATYPE,
				elementContext.qname, channel);
	}

	public Value decodeCharactersGenericUndeclared() throws EXIException,
			IOException {
		// structure
		decodeCharactersGenericUndeclaredStructure();
		// content
		return typeDecoder.readValue(BuiltIn.DEFAULT_DATATYPE,
				elementContext.qname, channel);
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
