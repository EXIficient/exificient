/*
 * Copyright (C) 2007, 2008 Siemens AG
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

import java.io.InputStream;

import org.xml.sax.SAXException;

import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.event.EventType;

/**
 * Internal EXI Decoder interface to transform an EXI stream back to XML Infoset
 * entities.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.2.20081031
 */

public interface EXIDecoder {
	public void setInputStream(InputStream is, boolean exiHeader) throws EXIException;

	/**
	 * Reports whether an additional EXI event is available.
	 * 
	 * @return <tt>true</tt> if the stream has more events.
	 */
	public boolean hasNextEvent();

	/**
	 * Reports the next available EXI event-type
	 * 
	 * @return <code>EventType</code> for next EXI event
	 */
	public EventType getNextEventType();

	/**
	 * Sniffs EXI stream for reporting next event.
	 * 
	 * @throws EXIException
	 */
	public void inspectEvent() throws EXIException;

	/**
	 * Initializes the beginning of a set of XML events
	 * 
	 * @throws EXIException
	 */
	public void decodeStartDocument() throws EXIException;

	/**
	 * Finalizes the end of a set of XML events
	 * 
	 * @throws SAXException
	 * @throws EXIException
	 */
	public void decodeEndDocument() throws EXIException;

	/**
	 * Reads EXI start element.
	 * 
	 * <p>
	 * Start element appearing as expected event.
	 * </p>
	 * 
	 * @throws SAXException
	 * @throws EXIException
	 */
	public void decodeStartElement() throws SAXException, EXIException;

	/**
	 * Reads generic start element.
	 * 
	 * <p>
	 * Expected generic start element
	 * </p>
	 * 
	 * @throws EXIException
	 */
	public void decodeStartElementGeneric() throws EXIException;

	/**
	 * Parses unexpected start element.
	 * 
	 * @throws EXIException
	 */
	public void decodeStartElementGenericUndeclared() throws EXIException;

	/**
	 * Reads EXI end element.
	 * 
	 * @throws EXIException
	 */
	public void decodeEndElement() throws EXIException;

	/**
	 * Reads unexpected EXI end element.
	 * 
	 * @throws EXIException
	 */
	public void decodeEndElementUndeclared() throws EXIException;

	/**
	 * Parses expected attribute.
	 * 
	 * @throws EXIException
	 */
	public void decodeAttribute() throws EXIException;

	/**
	 * Parses expected attribute with schema-invalid value
	 * 
	 * @throws EXIException
	 */
	public void decodeAttributeInvalidValue() throws EXIException;

	/**
	 * Parses expected generic attribute.
	 * 
	 * @throws EXIException
	 */
	public void decodeAttributeGeneric() throws EXIException;

	/**
	 * Parses unexpected attribute.
	 * 
	 * @throws EXIException
	 */
	public void decodeAttributeGenericUndeclared() throws EXIException;

	/**
	 * Parses namespace declaration retrieving associated URI and prefix.
	 * 
	 * @throws EXIException
	 */
	public void decodeNamespaceDeclaration() throws EXIException;

	/**
	 * Reads xsi:type from EXI stream.
	 * 
	 * @throws EXIException
	 */
	public void decodeXsiType() throws EXIException;

	/**
	 * Reads xsi:nil from EXI stream.
	 * 
	 * @throws EXIException
	 */
	public void decodeXsiNil() throws EXIException;

	public void decodeXsiNilDeviation() throws EXIException;

	/**
	 * Decodes expected characters.
	 * 
	 * @throws EXIException
	 */
	public void decodeCharacters() throws EXIException;

	/**
	 * Decodes generic characters.
	 * 
	 * @throws EXIException
	 */
	public void decodeCharactersGeneric() throws EXIException;

	/**
	 * Decodes unexpected (generic) characters.
	 * 
	 * @throws EXIException
	 */
	public void decodeCharactersGenericUndeclared() throws EXIException;

	/**
	 * Parses comment with associated characters.
	 * 
	 * @throws EXIException
	 */
	public void decodeComment() throws EXIException;

	/**
	 * Parses processing instruction with associated target and data.
	 * 
	 * @throws EXIException
	 */
	public void decodeProcessingInstruction() throws EXIException;

	/**
	 * Provides current scope URI.
	 * 
	 * @return <code>String</code> for URI
	 */
	public String getScopeURI();

	/**
	 * Provides current scope local-name.
	 * 
	 * @return <code>String</code> for name
	 */
	public String getScopeLocalName();

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
	 * Provides attribute value
	 * 
	 * @return <code>String</code> for attribute value
	 */
	public String getAttributeValue();

	/**
	 * Provides xsi:ytpe namespace
	 * 
	 * @return <code>String</code> for type URI
	 */
	public String getXsiTypeUri();

	/**
	 * Provides xsi:type name
	 * 
	 * @return <code>String</code> for type name
	 */
	public String getXsiTypeName();

	/**
	 * Provides xsi:nil value
	 * 
	 * @return <tt>true</tt> for xsi nil
	 */
	public boolean getXsiNil();

	public String getXsiNilDeviation();

	/**
	 * Provides characters as well as significant/insignificant whitespace
	 * characters
	 * 
	 * @return <code>String</code> for characters
	 */
	public String getCharacters();

	/**
	 * Provides comment text.
	 * 
	 * @return <code>String</code> for comment text
	 */
	public String getComment();

	/**
	 * Provides URI of namespace declaration.
	 * 
	 * @return <code>String</code> for NS uri
	 */
	public String getNSUri();

	/**
	 * Provides prefix of namespace declaration.
	 * 
	 * @return <code>String</code> for NS prefix
	 */
	public String getNSPrefix();

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
