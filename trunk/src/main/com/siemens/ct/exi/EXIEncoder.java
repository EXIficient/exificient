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

package com.siemens.ct.exi;

import java.io.IOException;
import java.io.OutputStream;

import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.exceptions.ErrorHandler;

/**
 * Internal EXI Encoder interface to transform XML events to an EXI stream.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.5
 */

public interface EXIEncoder {
	
	public void setOutput(OutputStream os, boolean exiBodyOnly)
			throws EXIException;

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
	 * Supplies the end tag of an element.
	 * 
	 * @throws EXIException
	 * @throws IOException
	 */
	public void encodeEndElement() throws EXIException, IOException;

//	/**
//	 * Supplies the end tag of an SC fragment.
//	 * 
//	 * @throws EXIException
//	 * @throws IOException
//	 */
//	public void encodeEndFragmentSelfContained() throws EXIException,
//			IOException;

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
			String value) throws EXIException, IOException;

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
	 * @param val
	 * @param pfx
	 * @throws EXIException
	 * @throws IOException
	 */
	public void encodeXsiNil(String val, String pfx) throws EXIException, IOException;

	/**
	 * Supplies an xsi:type case.
	 * 
	 * @param xsiTypeRaw  xsi:type value
	 * @param pfx
	 * @throws EXIException
	 * @throws IOException
	 */
	public void encodeXsiType(String xsiTypeRaw, String pfx) throws EXIException,
			IOException;

	/**
	 * Supplies a comment as corresponding characters.
	 * 
	 * <p>
	 * Note that ignorable whitespace and significant whitespace are also
	 * reported as Character events.
	 * </p>
	 * 
	 * @param chars
	 * @throws EXIException
	 * @throws IOException
	 */
	public void encodeCharacters(String chars) throws EXIException, IOException;

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
