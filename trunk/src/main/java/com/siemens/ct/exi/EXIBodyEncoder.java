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

package com.siemens.ct.exi;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.attributes.AttributeList;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.exceptions.ErrorHandler;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.values.Value;

/**
 * Internal EXI Encoder interface to transform XML events to an EXI stream.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.5-SNAPSHOT
 */

public interface EXIBodyEncoder {

	public void setOutputStream(OutputStream os) throws EXIException,
			IOException;

	public void setOutputChannel(EncoderChannel channel) throws EXIException,
			IOException;

	/**
	 * Flushes (possibly) remaining bit(s) to output stream
	 * 
	 * @throws IOException IO exception
	 */
	public void flush() throws IOException;

	/**
	 * 
	 * @param errorHandler error handler
	 */
	public void setErrorHandler(ErrorHandler errorHandler);

	/**
	 * Reports the beginning of a set of XML events
	 * 
	 * @throws EXIException EXI exception
	 * @throws IOException IO exception
	 */
	public void encodeStartDocument() throws EXIException, IOException;

	/**
	 * Reports the end of a set of XML events.
	 * 
	 * @throws EXIException EXI exception
	 * @throws IOException IO exception
	 */
	public void encodeEndDocument() throws EXIException, IOException;

	/**
	 * Supplies the start of an element.
	 * 
	 * <p>
	 * Provides access to the namespace URI, local name , and prefix
	 * representation of the start tag.
	 * </p>
	 * 
	 * @param uri element namespace URI
	 * @param localName element local-name
	 * @param prefix element prefix
	 *            (can be null according to fidelity options)
	 * 
	 * @throws EXIException EXI exception
	 * @throws IOException IO exception
	 */
	public void encodeStartElement(String uri, String localName, String prefix)
			throws EXIException, IOException;

	/**
	 * Supplies the start of an element.
	 * 
	 * <p>
	 * Provides access to the namespace URI, local name , and prefix
	 * representation of the start tag.
	 * </p>
	 * 
	 * @param se
	 *            start element's qname
	 * 
	 * @throws EXIException EXI exception
	 * @throws IOException IO exception
	 */
	public void encodeStartElement(QName se) throws EXIException, IOException;

	/**
	 * Supplies the end tag of an element.
	 * 
	 * @throws EXIException EXI exception
	 * @throws IOException IO exception
	 */
	public void encodeEndElement() throws EXIException, IOException;

	/**
	 * Supplies a list of namespace declarations, xsi:type and xsi:nil values and the remaining attributes.
	 * 
	 * @param attributes list of attributes
	 * 
	 * @throws EXIException EXI exception
	 * @throws IOException IO exception
	 */
	public void encodeAttributeList(AttributeList attributes) throws EXIException, IOException;
	
	/**
	 * Supplies an attribute.
	 * 
	 * <p>
	 * Provides access to the namespace URI, local name, prefix, and value of
	 * the attribute.
	 * </p>
	 * 
	 * @param uri attribute namespace URI
	 * @param localName attribute local-name
	 * @param prefix attribute prefix
	 *            (can be null according to fidelity options)
	 * @param value attribute value
	 * 
	 * @throws EXIException EXI exception
	 * @throws IOException IO exception
	 */
	public void encodeAttribute(String uri, String localName, String prefix,
			Value value) throws EXIException, IOException;

	/**
	 * Supplies an attribute with the according value.
	 * 
	 * <p>
	 * Provides access to the namespace URI, local name, prefix, and value of
	 * the attribute.
	 * </p>
	 * 
	 * @param at
	 *            attribute's qname
	 * @param value attribute value
	 * 
	 * @throws EXIException EXI exception
	 * @throws IOException IO exception
	 */
	public void encodeAttribute(QName at, Value value) throws EXIException,
			IOException;

	/**
	 * Namespaces are reported as a discrete Namespace event.
	 * 
	 * @param uri namespace URI
	 * @param prefix namespace prefix
	 * 
	 * @throws EXIException EXI exception
	 * @throws IOException IO exception
	 */
	public void encodeNamespaceDeclaration(String uri, String prefix)
			throws EXIException, IOException;

	/**
	 * Supplies an xsi:nil attribute.
	 * 
	 * @param nil xsi:nil value
	 * @param pfx xsi:nil prefix
	 * 
	 * @throws EXIException EXI exception
	 * @throws IOException IO exception
	 */
	public void encodeAttributeXsiNil(Value nil, String pfx)
			throws EXIException, IOException;

	/**
	 * Supplies an xsi:type case.
	 * 
	 * @param type xsi:type value
	 * @param pfx xsi:type prefix
	 * 
	 * @throws EXIException EXI exception
	 * @throws IOException IO exception
	 */
	public void encodeAttributeXsiType(Value type, String pfx)
			throws EXIException, IOException;

	/**
	 * Supplies characters as Value.
	 * 
	 * @param chars character values
	 * 
	 * @throws EXIException EXI exception
	 * @throws IOException IO exception
	 */
	public void encodeCharacters(Value chars) throws EXIException, IOException;

	/**
	 * Supplies content items to represent a DOCTYPE definition
	 * 
	 * @param name doc-type name
	 * @param publicID doc-type publicID
	 * @param systemID doc-type systemID
	 * @param text doc-type test
	 * 
	 * @throws EXIException EXI exception
	 * @throws IOException IO exception
	 */
	public void encodeDocType(String name, String publicID, String systemID,
			String text) throws EXIException, IOException;

	/**
	 * Supplies the name of an entity reference
	 * 
	 * @param name entity reference name
	 * 
	 * @throws EXIException EXI exception
	 * @throws IOException IO exception
	 */
	public void encodeEntityReference(String name) throws EXIException,
			IOException;

	/**
	 * Supplies the text of a comment.
	 * 
	 * @param ch comment character array
	 * @param start comment character array start
	 * @param length comment character array length
	 * 
	 * @throws EXIException EXI exception
	 * @throws IOException IO exception
	 */
	public void encodeComment(char[] ch, int start, int length)
			throws EXIException, IOException;

	/**
	 * Supplies the target and data for an underlying processing instruction.
	 * 
	 * @param target processing instruction target
	 * @param data processing instruction data
	 * 
	 * @throws EXIException EXI exception
	 * @throws IOException IO exception
	 */
	public void encodeProcessingInstruction(String target, String data)
			throws EXIException, IOException;
}
