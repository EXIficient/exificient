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

package com.siemens.ct.exi.util.xml;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Method;

import javax.xml.XMLConstants;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.DefaultHandler2;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;

/**
 * A SAX2 writer.
 * 
 * <p>
 * SAX callbacks print out an XML document.
 * </p>
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20080828
 */

public class SAXWriter extends DefaultHandler2 implements LexicalHandler
{
	/** XML decl already written */
	private boolean			xmlDeclarationAlreadyWritten	= false;

	/** Print writer. */
	protected PrintWriter	out;

	/** Element depth. */
	protected int			elementDepth;

	/** Document locator. */
	protected Locator		documentLocator;

	/** Processing XML 1.1 document. */
	protected boolean		isXML11;

	/** In CDATA section. */
	protected boolean		isInCDATA;
	
	/** */
	private AttributesImpl		prefixMappings;

	/** Default constructor. */
	public SAXWriter ()
	{
		prefixMappings		= new AttributesImpl ( );
	}

	/** Sets the output stream for printing. */
	public void setOutput ( OutputStream stream, String encoding ) throws UnsupportedEncodingException
	{
		if ( encoding == null )
		{
			encoding = "UTF8";
		}

		Writer writer = new OutputStreamWriter ( stream, encoding );
		out = new PrintWriter ( writer );
	}

	/** Sets the output writer. */
	public void setOutput ( Writer writer )
	{
		out = writer instanceof PrintWriter ? (PrintWriter) writer : new PrintWriter ( writer );
	}

	/** Set Document Locator. */
	public void setDocumentLocator ( Locator locator )
	{
		documentLocator = locator;
	}
	
	/** Set whether XML declaration needs to be written. Per Default XML
	 * declaration will be written */
	public void setBypassXMLDeclaration( boolean bypass )
	{
		xmlDeclarationAlreadyWritten = bypass;
	}

	/** Start document. */
	public void startDocument () throws SAXException
	{
		elementDepth = 0;
		isXML11 = false;
		isInCDATA = false;
	}

	/** Processing instruction. */
	public void processingInstruction ( String target, String data ) throws SAXException
	{
		if ( elementDepth > 0 )
		{
			out.print ( "<?" );
			out.print ( target );
			if ( data != null && data.length ( ) > 0 )
			{
				out.print ( ' ' );
				out.print ( data );
			}
			out.print ( "?>" );
			out.flush ( );
		}
	}

	private void xmlDeclaration ()
	{
		if ( xmlDeclarationAlreadyWritten )
		{
			return;
		}

		if ( elementDepth == 0 )
		{
			if ( documentLocator != null )
			{
				isXML11 = "1.1".equals ( getVersion ( ) );
				documentLocator = null;
			}

			// The XML declaration cannot be printed in startDocument because
			// the version reported by the Locator cannot be relied on until
			// after
			// the XML declaration in the instance document has been read.
			if ( isXML11 )
			{
				out.println ( "<?xml version=\"1.1\" encoding=\"UTF-8\"?>" );
			}
			else
			{
				out.println ( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" );
			}
			out.flush ( );

			xmlDeclarationAlreadyWritten = true;
		}
	}

	public void startPrefixMapping ( String prefix, String uri ) throws SAXException
	{
		String nsSQName = prefix.equals ( "" ) ? XMLConstants.XMLNS_ATTRIBUTE : XMLConstants.XMLNS_ATTRIBUTE + ":" + prefix;
		prefixMappings.addAttribute ( "", "", nsSQName, "CDATA", uri );
	}

	public void endPrefixMapping ( String prefix ) throws SAXException
	{
		// no op
	}

	/** Start element. */
	public void startElement ( String uri, String local, String raw, Attributes attrs ) throws SAXException
	{
		// Root Element
		this.xmlDeclaration ( );

		elementDepth++;
		out.print ( '<' );
		out.print ( raw );
		
		//	namespaces
		for ( int i = 0; i < prefixMappings.getLength ( ); i++ )
		{
			writeAttribute( prefixMappings, i );
		}
		prefixMappings.clear ( );
		
		//	attributes
		if ( attrs != null )
		{
			for ( int i = 0; i < attrs.getLength ( ); i++ )
			{
				writeAttribute( attrs, i );
			}
		}

		out.print ( '>' );
		out.flush ( );
	}
	
	private void writeAttribute( Attributes attrs, int index )
	{
		out.print ( ' ' );
		out.print ( attrs.getQName ( index ) );
		out.print ( "=\"" );
		normalizeAndPrint ( attrs.getValue ( index ), true );
		out.print ( '"' );
	}

	/** Characters. */
	public void characters ( char ch[], int start, int length ) throws SAXException
	{
		if ( !isInCDATA )
		{
			normalizeAndPrint ( ch, start, length, false );
		}
		else
		{
			for ( int i = 0; i < length; ++i )
			{
				out.print ( ch[start + i] );
			}
		}
		out.flush ( );
	}

	/** Ignorable whitespace. */
	public void ignorableWhitespace ( char ch[], int start, int length ) throws SAXException
	{
		characters ( ch, start, length );
		out.flush ( );
	}

	/** End element. */
	public void endElement ( String uri, String local, String raw ) throws SAXException
	{
		elementDepth--;
		out.print ( "</" );
		out.print ( raw );
		out.print ( '>' );
		out.flush ( );
	}

	//
	// ErrorHandler methods
	//

	/** Warning. */
	public void warning ( SAXParseException ex ) throws SAXException
	{
		printError ( "Warning", ex );
	}

	/** Error. */
	public void error ( SAXParseException ex ) throws SAXException
	{
		printError ( "Error", ex );
	}

	/** Fatal error. */
	public void fatalError ( SAXParseException ex ) throws SAXException
	{
		printError ( "Fatal Error", ex );
		throw ex;
	}

	//
	// LexicalHandler methods
	//

	/** Start DTD. */
	public void startDTD ( String name, String publicId, String systemId ) throws SAXException
	{
	}

	/** End DTD. */
	public void endDTD () throws SAXException
	{
	}

	/** Start entity. */
	public void startEntity ( String name ) throws SAXException
	{
	}

	/** End entity. */
	public void endEntity ( String name ) throws SAXException
	{
	}

	/** Start CDATA section. */
	public void startCDATA () throws SAXException
	{
		out.print ( "<![CDATA[" );
		isInCDATA = true;
	}

	/** End CDATA section. */
	public void endCDATA () throws SAXException
	{
		isInCDATA = false;
		out.print ( "]]>" );
	}

	/** Comment. */
	public void comment ( char ch[], int start, int length ) throws SAXException
	{
		xmlDeclaration ( );

		if ( elementDepth >= 0 )
		{
			out.print ( "<!--" );
			for ( int i = 0; i < length; ++i )
			{
				out.print ( ch[start + i] );
			}
			out.print ( "-->" );
			out.flush ( );
		}
	}

	/** Normalizes and prints the given string. */
	protected void normalizeAndPrint ( String s, boolean isAttValue )
	{
		int len = ( s != null ) ? s.length ( ) : 0;
		for ( int i = 0; i < len; i++ )
		{
			char c = s.charAt ( i );
			normalizeAndPrint ( c, isAttValue );
		}
	}

	/** Normalizes and prints the given array of characters. */
	protected void normalizeAndPrint ( char[] ch, int offset, int length, boolean isAttValue )
	{
		for ( int i = 0; i < length; i++ )
		{
			normalizeAndPrint ( ch[offset + i], isAttValue );
		}
	}

	/** Normalizes and print the given character. */
	protected void normalizeAndPrint ( char c, boolean isAttValue )
	{
		switch ( c )
		{
			case '<':
			{
				out.print ( "&lt;" );
				break;
			}
			case '>':
			{
				out.print ( "&gt;" );
				break;
			}
			case '&':
			{
				out.print ( "&amp;" );
				break;
			}
			case '"':
			{
				// A '"' that appears in character data
				// does not need to be escaped.
				if ( isAttValue )
				{
					out.print ( "&quot;" );
				}
				else
				{
					out.print ( "\"" );
				}
				break;
			}
			case '\r':
			{
				// If CR is part of the document's content, it
				// must not be printed as a literal otherwise
				// it would be normalized to LF when the document
				// is reparsed.
				out.print ( "&#xD;" );
				break;
			}
			default:
			{
				// In XML 1.1, control chars in the ranges [#x1-#x1F, #x7F-#x9F]
				// must be escaped.
				//
				// Escape space characters that would be normalized to #x20 in
				// attribute values
				// when the document is reparsed.
				//
				// Escape NEL (0x85) and LSEP (0x2028) that appear in content
				// if the document is XML 1.1, since they would be normalized to
				// LF
				// when the document is reparsed.
				if ( isXML11
						&& ( ( c >= 0x01 && c <= 0x1F && c != 0x09 && c != 0x0A ) || ( c >= 0x7F && c <= 0x9F ) || c == 0x2028 )
						|| isAttValue && ( c == 0x09 || c == 0x0A ) )
				{
					out.print ( "&#x" );
					out.print ( Integer.toHexString ( c ).toUpperCase ( ) );
					out.print ( ";" );
				}
				else
				{
					out.print ( c );
				}
			}
		}
	}

	/** Prints the error message. */
	protected void printError ( String type, SAXParseException ex )
	{
		System.err.print ( "[" );
		System.err.print ( type );
		System.err.print ( "] " );
		String systemId = ex.getSystemId ( );
		if ( systemId != null )
		{
			int index = systemId.lastIndexOf ( '/' );
			if ( index != -1 )
				systemId = systemId.substring ( index + 1 );
			System.err.print ( systemId );
		}
		System.err.print ( ':' );
		System.err.print ( ex.getLineNumber ( ) );
		System.err.print ( ':' );
		System.err.print ( ex.getColumnNumber ( ) );
		System.err.print ( ": " );
		System.err.print ( ex.getMessage ( ) );
		System.err.println ( );
		System.err.flush ( );
	}

	/** Extracts the XML version from the Locator. */
	protected String getVersion ()
	{
		if ( documentLocator == null )
		{
			return null;
		}
		String version = null;
		Method getXMLVersion = null;
		try
		{
			getXMLVersion = documentLocator.getClass ( ).getMethod ( "getXMLVersion", new Class[] {} );
			// If Locator implements Locator2, this method will exist.
			if ( getXMLVersion != null )
			{
				version = (String) getXMLVersion.invoke ( documentLocator, (Object[]) null );
			}
		}
		catch ( Exception e )
		{
			// Either this locator object doesn't have
			// this method, or we're on an old JDK.
		}
		return version;
	}
}
