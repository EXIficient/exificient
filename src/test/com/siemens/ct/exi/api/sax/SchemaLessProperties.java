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

package com.siemens.ct.exi.api.sax;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;

import javax.xml.transform.sax.SAXResult;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.siemens.ct.exi.api.sax.EXIResult;
import com.siemens.ct.exi.util.xml.SAXWriter;


public class SchemaLessProperties extends AbstractProperties
{
	
	private void encodeSchemaLessToEXI( OutputStream osEXI ) throws Exception
	{
		//	start encoding process
		XMLReader xmlReader = XMLReaderFactory.createXMLReader();
		
		SAXResult saxResult = new EXIResult( osEXI, factory );
		xmlReader.setContentHandler( saxResult.getHandler ( ) );
		
		xmlReader.parse( new InputSource( new StringReader( xml ) ) );
	}
	
	private String decodeSchemaLessToXML( InputStream isEXI ) throws IOException, SAXException
	{
		XMLReader exiReader = factory.createEXIReader( );
		SAXWriter xmlWriter = new SAXWriter ( );
		ByteArrayOutputStream xmlDecoded = new ByteArrayOutputStream();
		xmlWriter.setOutput ( xmlDecoded, "UTF8" );
		exiReader.setContentHandler( xmlWriter );
		exiReader.parse( new InputSource( isEXI ) );
		
		String sXMLDecoded = xmlDecoded.toString ( );
		
		return sXMLDecoded;
	}
	

	
	private void startTest( ) throws Exception
	{
		//	encode
		ByteArrayOutputStream osEXI = new ByteArrayOutputStream();
		encodeSchemaLessToEXI ( osEXI );
		
		//	reverse streams
		ByteArrayInputStream isEXI = new ByteArrayInputStream( osEXI.toByteArray ( ) );
		
		//	decode
		String sXMLDecoded = decodeSchemaLessToXML ( isEXI );
		
		//	equal ?
		isXMLEqual( sXMLDecoded );
	}
	
	public void testSimple1() throws Exception
	{
		xml = SIMPLE_XML;
		
		startTest( );
	}
	
	public void testUnexpectedRoot() throws Exception
	{
		xml = UNEXPECTED_ROOT_XML;
		
		startTest( );
	}
	
	public void testXsiType() throws Exception
	{
		xml= XSI_TYPE_XML;
		
		startTest(  );
	}

}
