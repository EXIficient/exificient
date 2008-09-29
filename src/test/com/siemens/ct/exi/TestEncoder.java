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
import java.io.FileOutputStream;
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

class MyEntityResolver implements EntityResolver
{
	public InputSource resolveEntity ( String publicId, String systemId )
	{
		return new InputSource ( new ByteArrayInputStream ( "<?xml version='1.0' encoding='UTF-8'?>".getBytes ( ) ) );
	}
}

public class TestEncoder extends AbstractTestCoder
{

	static protected XMLReader	parser;

	static
	{
		try
		{

			parser = XMLReaderFactory.createXMLReader ( );
			// *skip* resolving entities like DTDs 
			parser.setEntityResolver ( new MyEntityResolver ( ) );
		}
		catch ( SAXException e )
		{
			e.printStackTrace ( );
		}
	}

	public static void encodeTo ( EXIFactory ef, InputStream xmlInput, OutputStream exiOuput ) throws Exception
	{
		// set EXI as content & lexical handler
		SAXResult saxResult = new EXIResult ( exiOuput, ef );
		parser.setContentHandler ( saxResult.getHandler ( ) );

		try
		{
			// set LexicalHandler
			parser.setProperty ( "http://xml.org/sax/properties/lexical-handler", saxResult.getLexicalHandler ( ) );
		}
		catch ( SAXNotRecognizedException e )
		{
		}

		parser.parse ( new InputSource ( xmlInput ) );
	}

	public static void main ( String[] args ) throws Exception
	{
		// get factory
		EXIFactory ef = getQuickTestEXIactory ( );

		// XML input stream
		InputStream xmlInput = new BufferedInputStream (
				new FileInputStream ( QuickTestConfiguration.getXmlLocation ( ) ) );

		// EXI output stream
		File f = new File ( QuickTestConfiguration.getExiLocation ( ) );

		File path = f.getParentFile ( );
		if ( !path.exists ( ) )
		{
			path.mkdirs ( );
		}

		// OutputStream encodedOutput = new FileOutputStream ( f );
		OutputStream encodedOutput = new BufferedOutputStream ( new FileOutputStream ( f ) );

		encodeTo ( ef, xmlInput, encodedOutput );

		encodedOutput.flush ( );

		System.out.println ( "[ENC] " + QuickTestConfiguration.getXmlLocation ( ) + " --> "
				+ QuickTestConfiguration.getExiLocation ( ) );
	}
}
