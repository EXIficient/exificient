/*
 * Copyright (C) 2007-2009 Siemens AG
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

import java.io.OutputStream;

import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.exceptions.ErrorHandler;

/**
 * Internal EXI Encoder interface to transform XML events to an EXI stream.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.2.20080818
 */

public interface EXIEncoder {
	public void setOutput(OutputStream os, boolean exiBodyOnly)
			throws EXIException;

	public void setErrorHandler(ErrorHandler errorHandler);

	/**
	 * Reports the beginning of a set of XML events
	 * 
	 * @throws EXIException
	 */
	public void encodeStartDocument() throws EXIException;

	/**
	 * Reports the end of a set of XML events.
	 * 
	 * @throws EXIException
	 */
	public void encodeEndDocument() throws EXIException;

	/**
	 * Supplies the start of an element.
	 * 
	 * <p>
	 * Provides access to the namespace URI, and local name of the start tag.
	 * </p>
	 * 
	 * @param uri
	 * @param localName
	 * @throws EXIException
	 */
	public void encodeStartElement(String uri, String localName)
			throws EXIException;

	/**
	 * Supplies the start of a self-contained element. Self contained elements
	 * may be read independently from the rest of the EXI body, allowing them to
	 * be indexed for random access.
	 * 
	 * <p>
	 * Provides access to the namespace URI, and local name of the start tag.
	 * </p>
	 * 
	 * @param uri
	 * @param localName
	 * @return byte where the selfContained fragments starts or -1 if not retrievable
	 * @throws EXIException
	 */
	public int encodeStartFragmentSelfContained(String uri, String localName)
			throws EXIException;
	
	/**
	 * Supplies the mapping between a given URI and its prefix.
	 * 
	 * @param uri
	 * @param prefix
	 * @throws EXIException
	 */
	public void encodeStartElementPrefixMapping(String uri, String prefix)
			throws EXIException;

	/**
	 * Supplies the end tag of an element.
	 * 
	 * @throws EXIException
	 */
	public void encodeEndElement() throws EXIException;
	

	/**
	 * Supplies the end tag of an SC fragment.
	 * 
	 * @throws EXIException
	 */
	public void encodeEndFragmentSelfContained() throws EXIException;

	/**
	 * Supplies an attribute.
	 * 
	 * <p>
	 * Provides access to the namespace URI, local name, and value of the
	 * attribute.
	 * </p>
	 * 
	 * @param uri
	 * @param localName
	 * @param value
	 * @throws EXIException
	 */
	public void encodeAttribute(String uri, String localName, String value)
			throws EXIException;

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
	 * @param value
	 * @throws EXIException
	 */
	public void encodeAttribute(String uri, String localName, String prefix,
			String value) throws EXIException;

	/**
	 * Namespaces are reported as a discrete Namespace event.
	 * 
	 * @param uri
	 * @param prefix
	 * @throws EXIException
	 */
	public void encodeNamespaceDeclaration(String uri, String prefix)
			throws EXIException;

	/**
	 * Supplies an xsi:nil attribute.
	 * 
	 * @param nil
	 * @throws EXIException
	 */
	public void encodeXsiNil(String nil) throws EXIException;

	/**
	 * Supplies an xsi:type case.
	 * 
	 * @param uri
	 * @param localName
	 * @param raw
	 * @throws EXIException
	 */
	public void encodeXsiType(String uri, String localName, String raw)
			throws EXIException;

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
	 */
	public void encodeCharacters(String chars) throws EXIException;

	/**
	 * Supplies the text of a comment.
	 * 
	 * @param ch
	 * @param start
	 * @param length
	 * @throws EXIException
	 */
	public void encodeComment(char[] ch, int start, int length)
			throws EXIException;

	/**
	 * Supplies the target and data for an underlying processing instruction.
	 * 
	 * @param target
	 * @param data
	 * @throws EXIException
	 */
	public void encodeProcessingInstruction(String target, String data)
			throws EXIException;
}
