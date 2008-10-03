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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.sax.SAXResult;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.siemens.ct.exi.api.sax.EXIResult;
import com.siemens.ct.exi.util.FragmentUtilities;
import com.siemens.ct.exi.util.SkipRootElementXMLReader;


public class TestEncoder extends AbstractTestCoder
{
	protected XMLReader getXMLReader( ) throws SAXException
	{
		//	create xml reader
		XMLReader xmlReader = XMLReaderFactory.createXMLReader ( );
		// *skip* resolving entities like DTDs 
		xmlReader.setEntityResolver ( new MyEntityResolver ( ) );
		
		return xmlReader;
	}
	
	protected XMLReader updateXMLReaderToFragment( XMLReader xmlReader ) throws IOException
	{	
		//	skip root element when passing infoset to EXI encoder
		return new SkipRootElementXMLReader( xmlReader );
	}
	
	protected InputStream updateInputStreamToFragment( InputStream xmlInput ) throws IOException
	{
		//	surround fragment section with *root* element 
		//	(necessary for xml reader to avoid messages like "root element must be well-formed")
		return FragmentUtilities.getSurroundingRootInputStream ( xmlInput );
	}

	public void encodeTo ( EXIFactory ef, InputStream xmlInput, OutputStream exiOuput ) throws Exception
	{
		//	XML reader
		XMLReader xmlReader = getXMLReader( );
		
		// set EXI as content & lexical handler
		SAXResult saxResult = new EXIResult ( exiOuput, ef );
		xmlReader.setContentHandler ( saxResult.getHandler ( ) );
		
		try
		{
			// set LexicalHandler
			xmlReader.setProperty ( "http://xml.org/sax/properties/lexical-handler", saxResult.getLexicalHandler ( ) );
		}
		catch ( SAXNotRecognizedException e )
		{
		}
		
		if ( ef.isFragment ( ) )
		{
			xmlInput = updateInputStreamToFragment( xmlInput );
			xmlReader = updateXMLReaderToFragment( xmlReader );
		}
			
		xmlReader.parse ( new InputSource ( xmlInput ) );
	}
	
	protected static OutputStream getOutputStream( String exiLocation ) throws FileNotFoundException
	{
		File fileEXI = new File( exiLocation );
		
		File path = fileEXI.getParentFile ( );
		if ( !path.exists ( ) )
		{
			path.mkdirs ( );
		}

		return new BufferedOutputStream ( new FileOutputStream( fileEXI ) );
	}

	public static void main ( String[] args ) throws Exception
	{
		//	create test-encoder
		TestEncoder testEncoder = new TestEncoder();
		
		// get factory
		EXIFactory ef = testEncoder.getQuickTestEXIactory ( );

		// XML input stream
		InputStream xmlInput = new BufferedInputStream (
				new FileInputStream ( QuickTestConfiguration.getXmlLocation ( ) ) );

		// EXI output stream
		OutputStream encodedOutput = getOutputStream ( QuickTestConfiguration.getExiLocation ( ) );
		
		//	encode to EXI
		testEncoder.encodeTo ( ef, xmlInput, encodedOutput );

		encodedOutput.flush ( );

		System.out.println ( "[ENC] " + QuickTestConfiguration.getXmlLocation ( ) + " --> "
				+ QuickTestConfiguration.getExiLocation ( ) );
	}
}

class MyEntityResolver implements EntityResolver
{
	public InputSource resolveEntity ( String publicId, String systemId )
	{
		return new InputSource ( new ByteArrayInputStream ( "<?xml version='1.0' encoding='UTF-8'?>".getBytes ( ) ) );
	}
}
