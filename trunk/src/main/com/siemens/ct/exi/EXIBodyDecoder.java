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

package com.siemens.ct.exi;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.core.container.DocType;
import com.siemens.ct.exi.core.container.NamespaceDeclaration;
import com.siemens.ct.exi.core.container.ProcessingInstruction;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.event.EventType;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.values.Value;

/**
 * Internal EXI Decoder interface to transform an EXI stream back to XML Infoset
 * entities.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.6
 */

public interface EXIBodyDecoder {
	
	public void setInputStream(InputStream is) throws EXIException, IOException;

	public void setInputChannel(DecoderChannel channel) throws EXIException, IOException;
	
	/**
	 * Reports the next available EXI event-type or <code>null</code> if no more
	 * EXI event is available.
	 * 
	 * @return <code>EventType</code> for next EXI event
	 */
	public EventType next() throws EXIException, IOException;

	/**
	 * Indicates the beginning of a set of XML events
	 * 
	 * @throws EXIException
	 */
	public void decodeStartDocument() throws EXIException, IOException;

	/**
	 * Indicates the end of a set of XML events
	 * 
	 * @throws EXIException
	 */
	public void decodeEndDocument() throws EXIException, IOException;

	/**
	 * Reads EXI start element and returns qualified name.
	 * 
	 * <p>
	 * Start element appearing as expected event.
	 * </p>
	 * 
	 * @return <code>QName</code> for qualified name
	 * 
	 * @throws EXIException
	 * @throws IOException
	 */
	public QName decodeStartElement() throws EXIException, IOException;

	/**
	 * Reads start element where only the URI is known and returns qualified
	 * name.
	 * 
	 * <p>
	 * Expected start element with given namespaceURI
	 * </p>
	 * 
	 * @return <code>QName</code> for qualified name
	 * 
	 * @throws EXIException
	 * @throws IOException
	 */
	public QName decodeStartElementNS() throws EXIException, IOException;

	/**
	 * Reads generic start element and returns qualified name.
	 * 
	 * <p>
	 * Expected generic start element
	 * </p>
	 * 
	 * @return <code>QName</code> for qualified name
	 * 
	 * @throws EXIException
	 * @throws IOException
	 */
	public QName decodeStartElementGeneric() throws EXIException, IOException;

	/**
	 * Parses unexpected start element and returns qualified name.
	 * 
	 * @return <code>QName</code> for qualified name
	 * 
	 * @throws EXIException
	 * @throws IOException
	 */
	public QName decodeStartElementGenericUndeclared() throws EXIException,
			IOException;

	/**
	 * Returns qualified name for current start element name as String.
	 * 
	 * <p>
	 * QName ::= PrefixedName | UnprefixedName <br />
	 * PrefixedName ::= Prefix ':' LocalPart <br />
	 * UnprefixedName ::= LocalPart
	 * </p>
	 * 
	 * @return <code>String</code> for qname
	 */
	public String getStartElementQNameAsString();

	/**
	 * Reads EXI a self-contained start element.
	 * 
	 * @throws EXIException
	 * @throws IOException
	 */
	public void decodeStartSelfContainedFragment() throws EXIException,
			IOException;

	/**
	 * Reads EXI end element and returns qualified name.
	 * 
	 * @return <code>QName</code> for qualified name
	 * 
	 * @throws EXIException
	 * @throws IOException
	 */
	public QName decodeEndElement() throws EXIException, IOException;

	/**
	 * Reads unexpected EXI end element and returns qualified name.
	 * 
	 * @return <code>QName</code> for qualified name
	 * 
	 * @throws EXIException
	 * @throws IOException
	 */
	public QName decodeEndElementUndeclared() throws EXIException, IOException;

	/**
	 * Returns qualified name for end element name as String (the one previously
	 * created for SE event)
	 * 
	 * <p>
	 * see getStartElementQNameAsString()
	 * </p>
	 * 
	 * @return <code>String</code> for qname
	 */
	public String getEndElementQNameAsString();

	/**
	 * Parses xsi:nil attribute
	 * 
	 * @throws EXIException
	 * @throws IOException
	 */
	public QName decodeAttributeXsiNil() throws EXIException, IOException;

	/**
	 * Parses xsi:type attribute
	 * 
	 * @return <code>QName</code> for qualified name
	 * 
	 * @throws EXIException
	 * @throws IOException
	 */
	public QName decodeAttributeXsiType() throws EXIException, IOException;

	/**
	 * Parses attribute and returns qualified name.
	 * 
	 * @return <code>QName</code> for qname
	 * 
	 * @throws EXIException
	 * @throws IOException
	 */
	public QName decodeAttribute() throws EXIException, IOException;

	/**
	 * Parses expected attribute with given namespaceURI and returns qualified
	 * name.
	 * 
	 * @return <code>QName</code> for qualified name
	 * 
	 * @throws EXIException
	 * @throws IOException
	 */
	public QName decodeAttributeNS() throws EXIException, IOException;

	/**
	 * Parses expected attribute with schema-invalid value (qname given) and
	 * returns qualified name.
	 * 
	 * @return <code>QName</code> for qualified name
	 * 
	 * @throws EXIException
	 * @throws IOException
	 */
	public QName decodeAttributeInvalidValue() throws EXIException, IOException;

	/**
	 * Parses expected attribute with schema-invalid value (NO qname given) and
	 * returns qualified name.
	 * 
	 * @return <code>QName</code> for qualified name
	 * 
	 * @throws EXIException
	 * @throws IOException
	 */
	public QName decodeAttributeAnyInvalidValue() throws EXIException,
			IOException;

	/**
	 * Parses expected generic attribute and returns qualified name.
	 * 
	 * @return <code>QName</code> for qualified name
	 * 
	 * @throws EXIException
	 * @throws IOException
	 */
	public QName decodeAttributeGeneric() throws EXIException, IOException;

	/**
	 * Parses unexpected attribute.
	 * 
	 * @return <code>QName</code> for qualified name
	 * 
	 * @throws EXIException
	 * @throws IOException
	 */
	public QName decodeAttributeGenericUndeclared() throws EXIException,
			IOException;

	/**
	 * Returns qualified name for (last) attribute as String
	 * 
	 * <p>
	 * QName ::= PrefixedName | UnprefixedName <br />
	 * PrefixedName ::= Prefix ':' LocalPart <br />
	 * UnprefixedName ::= LocalPart
	 * </p>
	 * 
	 * @return <code>String</code> for qname
	 */
	public String getAttributeQNameAsString();

	/**
	 * Provides attribute value
	 * 
	 * @return <code>Value</code> for attribute value
	 */
	public Value getAttributeValue();

	/**
	 * Parses namespace declaration retrieving associated URI and prefix.
	 * 
	 * @return <code>NamespaceDeclaration</code> ns declaration
	 * 
	 * @throws EXIException
	 * @throws IOException
	 */
	public NamespaceDeclaration decodeNamespaceDeclaration()
			throws EXIException, IOException;

	/**
	 * Prefix declarations for current context (element)
	 * 
	 * @return list or null if no mappings are available
	 */
	public List<NamespaceDeclaration> getDeclaredPrefixDeclarations();

	/**
	 * Recently undeclared prefix declarations for popped (element) context
	 * 
	 * @return list or null if no mappings are available
	 */
	public List<NamespaceDeclaration> getUndeclaredPrefixDeclarations();

	/**
	 * Decodes characters and reports them.
	 * 
	 * @return <code>Value</code> for XML characters item
	 * 
	 * @throws EXIException
	 * @throws IOException
	 */
	public Value decodeCharacters() throws EXIException, IOException;

	/**
	 * Decodes generic characters.
	 * 
	 * @return <code>Value</code> for XML characters item
	 * 
	 * @throws EXIException
	 * @throws IOException
	 */
	public Value decodeCharactersGeneric() throws EXIException, IOException;

	/**
	 * Decodes unexpected (generic) characters and reports them.
	 * 
	 * @return <code>Value</code> for XML characters item
	 * 
	 * @throws EXIException
	 * @throws IOException
	 */
	public Value decodeCharactersGenericUndeclared() throws EXIException,
			IOException;

	/**
	 * Parses DOCTYPE with information items (name, publicID, systemID, text).
	 * 
	 * @return <code>DocType</code> for DOCTYPE information items
	 * 
	 * @throws EXIException
	 * @throws IOException
	 */
	public DocType decodeDocType() throws EXIException, IOException;

	/**
	 * Parses EntityReference and returns ER name.
	 * 
	 * @return <code>String</code> for ER name
	 * 
	 * @throws EXIException
	 */
	public char[] decodeEntityReference() throws EXIException, IOException;

	/**
	 * Parses comment with associated characters and provides comment text.
	 * 
	 * @return <code>String</code> for comment text
	 * 
	 * @throws EXIException
	 * @throws IOException
	 */
	public char[] decodeComment() throws EXIException, IOException;

	/**
	 * Parses processing instruction with associated target and data.
	 * 
	 * @return <code>String</code> for PI target and data
	 * 
	 * @throws EXIException
	 * @throws IOException
	 */
	public ProcessingInstruction decodeProcessingInstruction()
			throws EXIException, IOException;

}
