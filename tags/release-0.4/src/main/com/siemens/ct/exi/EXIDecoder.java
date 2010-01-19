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
import java.io.InputStream;

import org.xml.sax.helpers.NamespaceSupport;

import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.event.EventType;

/**
 * Internal EXI Decoder interface to transform an EXI stream back to XML Infoset
 * entities.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.4.20090414
 */

public interface EXIDecoder {
	public void setInputStream(InputStream is, boolean exiBodyOnly)
			throws EXIException, IOException;

	/**
	 * Reports whether an additional EXI event is available.
	 * 
	 * @return <tt>true</tt> if the stream has more events.
	 * 
	 * @throws EXIException
	 * @throws IOException
	 */
	public boolean hasNext() throws EXIException, IOException;

	/**
	 * Reports the next available EXI event-type
	 * 
	 * @return <code>EventType</code> for next EXI event
	 */
	public EventType next() throws EXIException;

	/**
	 * Initializes the beginning of a set of XML events
	 * 
	 * @throws EXIException
	 */
	public void decodeStartDocument() throws EXIException, IOException;

	/**
	 * Finalizes the end of a set of XML events
	 * 
	 * @throws EXIException
	 */
	public void decodeEndDocument() throws EXIException, IOException;

	/**
	 * Reads EXI start element.
	 * 
	 * <p>
	 * Start element appearing as expected event.
	 * </p>
	 * 
	 * @throws EXIException
	 * @throws IOException
	 */
	public void decodeStartElement() throws EXIException, IOException;

	/**
	 * Reads start element where only the URI is known.
	 * 
	 * <p>
	 * Expected start element with given namespaceURI
	 * </p>
	 * 
	 * @throws EXIException
	 * @throws IOException
	 */
	public void decodeStartElementNS() throws EXIException, IOException;

	/**
	 * Reads generic start element.
	 * 
	 * <p>
	 * Expected generic start element
	 * </p>
	 * 
	 * @throws EXIException
	 * @throws IOException
	 */
	public void decodeStartElementGeneric() throws EXIException, IOException;

	/**
	 * Parses unexpected start element.
	 * 
	 * @throws EXIException
	 * @throws IOException
	 */
	public void decodeStartElementGenericUndeclared() throws EXIException, IOException;

	/**
	 * Reads EXI a self-contained start element
	 * 
	 * @throws EXIException
	 * @throws IOException
	 */
	public void decodeStartFragmentSelfContained() throws EXIException, IOException;

	/**
	 * Reads EXI end element
	 * 
	 * @throws EXIException
	 * @throws IOException
	 */
	public void decodeEndElement() throws EXIException, IOException;

	/**
	 * Reads unexpected EXI end element.
	 * 
	 * @throws EXIException
	 * @throws IOException
	 */
	public void decodeEndElementUndeclared() throws EXIException, IOException;

//	/**
//	 * Reads an end element part of self-contained fragments
//	 * 
//	 * @throws EXIException
//	 * @throws IOException
//	 */
//	public void decodeEndFragmentSelfContained() throws EXIException, IOException;

	/**
	 * Parses xsi:nil attribute
	 * 
	 * @throws EXIException
	 * @throws IOException
	 */
	public void decodeAttributeXsiNil() throws EXIException, IOException;
	
	/**
	 * Parses xsi:type attribute
	 * 
	 * @throws EXIException
	 * @throws IOException
	 */
	public void decodeAttributeXsiType() throws EXIException, IOException;
	
	/**
	 * Parses attribute
	 * 
	 * @throws EXIException
	 * @throws IOException
	 */
	public void decodeAttribute() throws EXIException, IOException;

	/**
	 * Parses expected attribute with given namespaceURI
	 * 
	 * @throws EXIException
	 * @throws IOException
	 */
	public void decodeAttributeNS() throws EXIException, IOException;

	/**
	 * Parses expected attribute with schema-invalid value (qname given)
	 * 
	 * @throws EXIException
	 * @throws IOException
	 */
	public void decodeAttributeInvalidValue() throws EXIException, IOException;

	/**
	 * Parses expected attribute with schema-invalid value (NO qname given)
	 * 
	 * @throws EXIException
	 * @throws IOException
	 */
	public void decodeAttributeAnyInvalidValue() throws EXIException, IOException;

	/**
	 * Parses expected generic attribute.
	 * 
	 * @throws EXIException
	 * @throws IOException
	 */
	public void decodeAttributeGeneric() throws EXIException, IOException;

	/**
	 * Parses unexpected attribute.
	 * 
	 * @throws EXIException
	 * @throws IOException
	 */
	public void decodeAttributeGenericUndeclared() throws EXIException, IOException;

	/**
	 * Parses namespace declaration retrieving associated URI and prefix.
	 * 
	 * @throws EXIException
	 * @throws IOException
	 */
	public void decodeNamespaceDeclaration() throws EXIException, IOException;

	/**
	 * Decodes characters
	 * 
	 * @throws EXIException
	 * @throws IOException
	 */
	public void decodeCharacters() throws EXIException, IOException;

	/**
	 * Decodes generic characters.
	 * 
	 * @throws EXIException
	 * @throws IOException
	 */
	public void decodeCharactersGeneric() throws EXIException, IOException;

	/**
	 * Decodes unexpected (generic) characters.
	 * 
	 * @throws EXIException
	 * @throws IOException
	 */
	public void decodeCharactersGenericUndeclared() throws EXIException, IOException;

	/**
	 * Parses DOCTYPE with information items
	 * 
	 * @throws EXIException
	 * @throws IOException
	 */
	public void decodeDocType() throws EXIException, IOException;

	/**
	 * Parses EntityReference
	 * 
	 * @throws EXIException
	 */
	public void decodeEntityReference() throws EXIException, IOException;

	/**
	 * Parses comment with associated characters.
	 * 
	 * @throws EXIException
	 * @throws IOException
	 */
	public void decodeComment() throws EXIException, IOException;

	/**
	 * Parses processing instruction with associated target and data.
	 * 
	 * @throws EXIException
	 * @throws IOException
	 */
	public void decodeProcessingInstruction() throws EXIException, IOException;


	// ////////////////////////////////////////////////////////////////
	//
	// fetching values
	// 
	// ////////////////////////////////////////////////////////////////

	/**
	 * Provides (last) element namespace.
	 * 
	 * @return <code>String</code> for element URI
	 */
	public String getElementURI();

	/**
	 * Provides (last) element local-name
	 * 
	 * @return <code>String</code> for element name
	 */
	public String getElementLocalName();

	/**
	 * Returns qualified name for element name
	 * 
	 * @return <code>String</code> for qname
	 */
	public String getElementQName();

	/**
	 * Provides (last) attribute namespace
	 * 
	 * @return <code>String</code> for attribute URI
	 */
	public String getAttributeURI();

	/**
	 * Provides (last) attribute local-name
	 * 
	 * @return <code>String</code> for attribute name
	 */
	public String getAttributeLocalName();

	/**
	 * Returns qualified name for (last) attribute
	 * 
	 * @return <code>String</code> for qname
	 */
	public String getAttributeQName();

	/**
	 * Provides attribute value
	 * 
	 * @return <code>String</code> for attribute value
	 */
	public String getAttributeValue();

	/**
	 * Provides characters as well as significant/insignificant whitespace
	 * characters
	 * 
	 * @return <code>char[]</code> for characters
	 */
	// public String getCharacters();
	public char[] getCharacters();

	/**
	 * Provides DOCTYPE name.
	 * 
	 * @return <code>String</code> for DOCTYPE name
	 */
	public String getDocTypeName();

	/**
	 * Provides DOCTYPE public ID.
	 * 
	 * @return <code>String</code> for DOCTYPE public ID
	 */
	public String getDocTypePublicID();

	/**
	 * Provides DOCTYPE system ID.
	 * 
	 * @return <code>String</code> for DOCTYPE system ID
	 */
	public String getDocTypeSystemID();

	/**
	 * Provides DOCTYPE text.
	 * 
	 * @return <code>String</code> for DOCTYPE text
	 */
	public String getDocTypeText();

	/**
	 * Provides ENTITY_REFERENCE name.
	 * 
	 * @return <code>String</code> for DOCTYPE name
	 */
	public String getEntityReferenceName();

	/**
	 * Provides comment text.
	 * 
	 * @return <code>String</code> for comment text
	 */
	public char[] getComment();

	/**
	 * Provides namespace support.
	 * 
	 * @return <code>NamespaceSupport</code> for prefix mapping
	 */
	public NamespaceSupport getNamespaces();

	/**
	 * Provides processing instructions target.
	 * 
	 * @return <code>String</code> for PI target
	 */
	public String getPITarget();

	/**
	 * Provides processing instructions data.
	 * 
	 * @return <code>String</code> for PI data
	 */
	public String getPIData();

}
