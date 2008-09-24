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

package com.siemens.ct.exi.core.sax;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.xml.XMLConstants;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.EXIDecoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.event.EventType;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20080718
 */

public class SAXDecoder implements XMLReader
// , ErrorHandler
{

	private EXIDecoder			decoder;

	private ContentHandler		contentHandler;
	private DTDHandler			dtdHandler;

	private ErrorHandler		errorHandler;

	private static final String	ATTRIBUTE_TYPE	= "CDATA";
	private static final String	COLON			= ":";

	private AttributesImpl		attributes		= new AttributesImpl ( );

	// namespace prefixes are related to elements
	NamespacePrefixLevels		nsPrefixes;

	private String				deferredStartElementUri;
	private String				deferredStartElementLocalName;

	public SAXDecoder ( EXIFactory exiFactory )
	{
		this.decoder = exiFactory.createEXIDecoder ( );

		// this.setErrorHandler( this );
	}

	private void init ()
	{
		// deferred elements
		deferredStartElementUri = null;
		deferredStartElementLocalName = null;

		// prefixes
		nsPrefixes = new NamespacePrefixLevels ( );
	}

	private String getQName ( String uri, String localName )
	{
		String pfx;

		// check whether prefix exists
		if ( nsPrefixes.hasPrefixForURI ( uri ) )
		{
			pfx = this.nsPrefixes.getPrefix ( uri );
		}
		else
		{
			nsPrefixes.createPrefix ( uri );
			pfx = this.nsPrefixes.getPrefix ( uri );

			//this.addNamespaceDeclarationToAttributes ( uri, pfx );
		}

		return ( pfx.length ( ) == 0 ? localName : ( pfx + COLON + localName ) );
	}

//	private void addNamespaceDeclarationToAttributes ( String uri, String pfx )
//	{
//		String nsSQName = pfx.equals ( "" ) ? XMLConstants.XMLNS_ATTRIBUTE : XMLConstants.XMLNS_ATTRIBUTE + COLON + pfx;
//		attributes.addAttribute ( "", "", nsSQName, ATTRIBUTE_TYPE, uri );
//	}

	private void checkDeferredStartElement () throws SAXException
	{
		if ( deferredStartElementUri != null )
		{
			// start SAX startElement
			String qName = getQName ( deferredStartElementUri, deferredStartElementLocalName );

			// prefix mapping
			startPrefixMappings();
			
			contentHandler.startElement ( deferredStartElementUri, deferredStartElementLocalName, qName, attributes );
			// clear information
			deferredStartElementUri = null;
			deferredStartElementLocalName = null;
			attributes.clear ( );
		}
	}
	
	private void startPrefixMappings() throws SAXException
	{
		PrefixMapping pfxMap = nsPrefixes.getCurrentMapping ( );

		for ( Iterator<String> namespace = pfxMap.mapping.keySet ( ).iterator ( ); namespace.hasNext ( ); )
		{
			String uri = namespace.next ( );
			String prefix = pfxMap.mapping.get ( uri );
			contentHandler.startPrefixMapping ( prefix, uri );
			// System.out.println ( "startPrefixMapping: " + prefix + " --> "+  uri );
		}
	}
	

	private void endPrefixMappings() throws SAXException
	{
		PrefixMapping pfxMap = nsPrefixes.getCurrentMapping ( );

		for ( Iterator<String> namespace = pfxMap.mapping.keySet ( ).iterator ( ); namespace.hasNext ( ); )
		{
			String uri = namespace.next ( );
			String prefix = pfxMap.mapping.get ( uri );
			contentHandler.endPrefixMapping ( prefix );
			// System.out.println ( "endPrefixMapping: " + prefix + " --> "+  uri );
		}
	}

	private void parseEXI ( InputSource inputSource ) throws SAXException, IOException, EXIException
	{
		// setup (bit) input stream
		InputStream inputStream = inputSource.getByteStream ( );
		decoder.setInputStream ( inputStream );

		if ( contentHandler == null )
		{
			throw new SAXException ( "No content handler set!" );
		}

		// init
		init ( );

		// first event ( SD ? )
		decoder.inspectEvent ( );

		while ( decoder.hasNextEvent ( ) )
		{	
			switch ( decoder.getNextEventType ( ) )
			{
				case START_DOCUMENT:
					decoder.decodeStartDocument ( );
					contentHandler.startDocument ( );
					break;
				case START_ELEMENT:
					decoder.decodeStartElement ( );
					// check whether a preceding start element is still deferred
					checkDeferredStartElement ( );
					// set new deferred start element
					deferredStartElementUri = decoder.getElementURI ( );
					deferredStartElementLocalName = decoder.getElementLocalName ( );
					nsPrefixes.addLevel ( );
					break;
				case START_ELEMENT_GENERIC:
					decoder.decodeStartElementGeneric ( );
					// check whether a preceding start element is still deferred
					checkDeferredStartElement ( );
					// set new deferred start element
					deferredStartElementUri = decoder.getElementURI ( );
					deferredStartElementLocalName = decoder.getElementLocalName ( );
					nsPrefixes.addLevel ( );
					break;
				case START_ELEMENT_GENERIC_UNDECLARED:
					decoder.decodeStartElementGenericUndeclared ( );
					// check whether a preceding start element is still deferred
					checkDeferredStartElement ( );
					// set new deferred start element
					deferredStartElementUri = decoder.getElementURI ( );
					deferredStartElementLocalName = decoder.getElementLocalName ( );
					nsPrefixes.addLevel ( );
					break;
				case NAMESPACE_DECLARATION:
					decoder.decodeNamespaceDeclaration ( );
					nsPrefixes.addPrefix ( decoder.getNSUri ( ), decoder.getNSPrefix ( ) );
					// addNamespaceDeclarationToAttributes ( decoder.getNSUri ( ), decoder.getNSPrefix ( ) );
					break;
				case ATTRIBUTE:
					decoder.decodeAttribute ( );
					String atQName = this.getQName ( decoder.getAttributeURI ( ), decoder.getAttributeLocalName ( ) );
					attributes.addAttribute ( decoder.getAttributeURI ( ), decoder.getAttributeLocalName ( ), atQName,
							ATTRIBUTE_TYPE, decoder.getAttributeValue ( ) );
					break;
				case ATTRIBUTE_GENERIC:
				case ATTRIBUTE_GENERIC_UNDECLARED:
					if ( decoder.getNextEventType ( ) ==  EventType.ATTRIBUTE_GENERIC )
					{
						decoder.decodeAttributeGeneric ( );
					}
					else if ( decoder.getNextEventType ( ) ==  EventType.ATTRIBUTE_GENERIC_UNDECLARED )
					{
						decoder.decodeAttributeGenericUndeclared ( );
					}

					atQName = this.getQName ( decoder.getAttributeURI ( ), decoder.getAttributeLocalName ( ) );

					attributes.addAttribute ( decoder.getAttributeURI ( ), decoder.getAttributeLocalName ( ), atQName,
							ATTRIBUTE_TYPE, decoder.getAttributeValue ( ) );
					break;
				case ATTRIBUTE_XSI_TYPE:
					decoder.decodeXsiType ( );

					String xsiTypeValue = getQName ( decoder.getXsiTypeUri ( ), decoder.getXsiTypeName ( ) );

					atQName = this.getQName ( XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, Constants.XSI_TYPE );

					attributes.addAttribute ( XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, Constants.XSI_TYPE, atQName,
							ATTRIBUTE_TYPE, xsiTypeValue );
					break;
				case ATTRIBUTE_XSI_NIL:
					decoder.decodeXsiNil ( );

					String xsiNilValue = decoder.getXsiNil ( ) ? Constants.DECODED_BOOLEAN_TRUE
							: Constants.DECODED_BOOLEAN_FALSE;
					atQName = this.getQName ( XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, Constants.XSI_NIL );

					attributes.addAttribute ( XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, Constants.XSI_NIL, atQName,
							ATTRIBUTE_TYPE, xsiNilValue );
					break;
				case CHARACTERS:
					decoder.decodeCharacters ( );
					// check whether a preceding start element is still deferred
					checkDeferredStartElement ( );
					// start sax characters event
					contentHandler.characters ( decoder.getCharacters ( ).toCharArray ( ), 0, decoder.getCharacters ( )
							.length ( ) );
					break;
				case CHARACTERS_GENERIC:
					decoder.decodeCharactersGeneric ( );
					// check whether a preceding start element is still deferred
					checkDeferredStartElement ( );
					// start sax characters event
					contentHandler.characters ( decoder.getCharacters ( ).toCharArray ( ), 0, decoder.getCharacters ( )
							.length ( ) );
					break;
				case CHARACTERS_GENERIC_UNDECLARED:
					decoder.decodeCharactersGenericUndeclared ( );
					// check whether a preceding start element is still deferred
					checkDeferredStartElement ( );
					// start sax characters event
					contentHandler.characters ( decoder.getCharacters ( ).toCharArray ( ), 0, decoder.getCharacters ( )
							.length ( ) );
					break;
				case END_ELEMENT:
				case END_ELEMENT_UNDECLARED:
					// fetch scope before popping rule etc.?
					String eeUri = decoder.getScopeURI ( );
					String eeLocalName = decoder.getScopeLocalName ( );
					// decode
					decoder.decodeEndElement ( );
					// check whether a preceding start element is still deferred
					checkDeferredStartElement ( );
					// start sax end element
					String endQName = getQName ( eeUri, eeLocalName );
					contentHandler.endElement ( eeUri, eeLocalName, endQName );
					
					//	prefix mapping
					endPrefixMappings();
					nsPrefixes.removeLevel ( );
					break;
				// case END_DOCUMENT:
				// Note: done outside
				case COMMENT:
					decoder.decodeComment ( );
					if ( contentHandler instanceof LexicalHandler )
					{
						String comment = decoder.getComment ( );
						( (LexicalHandler) contentHandler ).comment ( comment.toCharArray ( ), 0, comment.length ( ) );
					}
					break;
				case PROCESSING_INSTRUCTION:
					decoder.decodeProcessingInstruction ( );
					contentHandler.processingInstruction ( decoder.getPITarget ( ), decoder.getPIData ( ) );
					break;
				default:
					// ERROR
					throw new IllegalArgumentException ( "Unknown event while decoding! " + decoder.getNextEventType ( ) );
			}

			// inspect stream whether there is still content
			decoder.inspectEvent ( );
		}

		// case END_DOCUMENT
		decoder.decodeEndDocument ( );
		contentHandler.endDocument ( );

	}

	/*
	 * XML READER INTERFACE
	 * 
	 */

	public ContentHandler getContentHandler ()
	{
		return this.contentHandler;
	}

	public DTDHandler getDTDHandler ()
	{
		return this.dtdHandler;
	}

	public EntityResolver getEntityResolver ()
	{
		return null;
	}

	public ErrorHandler getErrorHandler ()
	{
		return this.errorHandler;
	}

	public boolean getFeature ( String name ) throws SAXNotRecognizedException, SAXNotSupportedException
	{
		return false;
	}

	public Object getProperty ( String name ) throws SAXNotRecognizedException, SAXNotSupportedException
	{
		return null;
	}

	public void parse ( String systemId ) throws IOException, SAXException
	{
		FileInputStream fis = new FileInputStream ( systemId );
		InputSource is = new InputSource ( fis );
		this.parse ( is );
	}

	public void parse ( InputSource inputSource ) throws IOException, SAXException
	{
		assert ( inputSource != null );
		assert ( decoder != null );

		try
		{
			parseEXI ( inputSource );
		}
		catch ( RuntimeException e )
		{
			// e.printStackTrace ( );
			// TODO elaborate a way of supporting meaningful EXI exceptions
			throw new SAXException ( "[EXI] Error while parsing an EXI document", e );
		}
		catch ( EXIException e )
		{
			throw new SAXException ( e );
		}
	}

	public void setContentHandler ( ContentHandler handler )
	{
		this.contentHandler = handler;
	}

	public void setDTDHandler ( DTDHandler handler )
	{
		this.dtdHandler = handler;
	}

	public void setEntityResolver ( EntityResolver resolver )
	{

	}

	public void setErrorHandler ( ErrorHandler handler )
	{
		this.errorHandler = handler;
	}

	public void setFeature ( String name, boolean value ) throws SAXNotRecognizedException, SAXNotSupportedException
	{

	}

	public void setProperty ( String name, Object value ) throws SAXNotRecognizedException, SAXNotSupportedException
	{

	}

	//	
	// /*
	// * ErrorHandler
	// *
	// */
	// public void warning( SAXParseException e ) {
	// if ( CompileConfiguration.LOGGING_ON ) {
	// Log.log(Level.WARNING, "[SAX Warning] " + e.getMessage());
	// //this.errorHandler.warning( e );
	// }
	// }
	// public void error( SAXParseException e ) {
	// if ( CompileConfiguration.LOGGING_ON ) {
	// Log.log(Level.WARNING, "[SAX Error] " + e.getMessage());
	// }
	// }
	// public void fatalError( SAXParseException e ) {
	// if ( CompileConfiguration.LOGGING_ON ) {
	// Log.log(Level.WARNING, "[SAX FatalError] " + e.getMessage());
	// }
	// }

}
