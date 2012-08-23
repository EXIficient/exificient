/*
 * Copyright (C) 2007-2012 Siemens AG
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
 * @version 0.9
 */

public interface EXIBodyEncoder {

	public void setOutputStream(OutputStream os) throws EXIException,
			IOException;

	public void setOutputChannel(EncoderChannel channel) throws EXIException,
			IOException;

	/**
	 * Flushes (possibly) remaining bit(s) to output stream
	 * 
	 * @throws IOException
	 */
	public void flush() throws IOException;

	/**
	 * 
	 * @param errorHandler
	 */
	public void setErrorHandler(ErrorHandler errorHandler);

	/**
	 * Reports the beginning of a set of XML events
	 * 
	 * @throws EXIException
	 * @throws IOException
	 */
	public void encodeStartDocument() throws EXIException, IOException;

	/**
	 * Reports the end of a set of XML events.
	 * 
	 * @throws EXIException
	 * @throws IOException
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
	 * @param uri
	 * @param localName
	 * @param prefix
	 *            (can be null according to fidelity options)
	 * 
	 * @throws EXIException
	 * @throws IOException
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
	 * @throws EXIException
	 * @throws IOException
	 */
	public void encodeStartElement(QName se) throws EXIException, IOException;

	/**
	 * Supplies the end tag of an element.
	 * 
	 * @throws EXIException
	 * @throws IOException
	 */
	public void encodeEndElement() throws EXIException, IOException;

	/**
	 * Supplies a list of namespace declarations, xsi:type and xsi:nil values and the remaining attributes.
	 * 
	 * @param attributes
	 * 
	 * @throws EXIException
	 * @throws IOException
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
	 * @param uri
	 * @param localName
	 * @param prefix
	 *            (can be null according to fidelity options)
	 * @param value
	 * @throws EXIException
	 * @throws IOException
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
	 * @param value
	 * 
	 * @throws EXIException
	 * @throws IOException
	 */
	public void encodeAttribute(QName at, Value value) throws EXIException,
			IOException;

	/**
	 * Namespaces are reported as a discrete Namespace event.
	 * 
	 * @param uri
	 * @param prefix
	 * @throws EXIException
	 * @throws IOException
	 */
	public void encodeNamespaceDeclaration(String uri, String prefix)
			throws EXIException, IOException;

	/**
	 * Supplies an xsi:nil attribute.
	 * 
	 * @param nil
	 * @param pfx
	 * @throws EXIException
	 * @throws IOException
	 */
	public void encodeAttributeXsiNil(Value nil, String pfx)
			throws EXIException, IOException;

	/**
	 * Supplies an xsi:type case.
	 * 
	 * @param type xsi:type value
	 * @param pfx
	 * @throws EXIException
	 * @throws IOException
	 */
	public void encodeAttributeXsiType(Value type, String pfx)
			throws EXIException, IOException;

	/**
	 * Supplies characters value.
	 * 
	 * @param chars
	 * @throws EXIException
	 * @throws IOException
	 */
	public void encodeCharacters(Value chars) throws EXIException, IOException;

	/**
	 * Supplies content items to represent a DOCTYPE definition
	 * 
	 * @param name
	 * @param publicID
	 * @param systemID
	 * @param text
	 * @throws EXIException
	 * @throws IOException
	 */
	public void encodeDocType(String name, String publicID, String systemID,
			String text) throws EXIException, IOException;

	/**
	 * Supplies the name of an entity reference
	 * 
	 * @param name
	 * @throws EXIException
	 * @throws IOException
	 */
	public void encodeEntityReference(String name) throws EXIException,
			IOException;

	/**
	 * Supplies the text of a comment.
	 * 
	 * @param ch
	 * @param start
	 * @param length
	 * @throws EXIException
	 * @throws IOException
	 */
	public void encodeComment(char[] ch, int start, int length)
			throws EXIException, IOException;

	/**
	 * Supplies the target and data for an underlying processing instruction.
	 * 
	 * @param target
	 * @param data
	 * @throws EXIException
	 * @throws IOException
	 */
	public void encodeProcessingInstruction(String target, String data)
			throws EXIException, IOException;
}
