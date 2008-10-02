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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.util.xml.SAXWriter;

public class TestDecoder extends AbstractTestCoder
{
	public static SAXWriter getXMLWriter( OutputStream os ) throws UnsupportedEncodingException, FileNotFoundException
	{
		SAXWriter xmlWriter = new SAXWriter();
		xmlWriter.setOutput( os, "UTF8");
    	
    	return xmlWriter;
	}
	
	public void decodeTo( EXIFactory ef, InputStream exiDocument, OutputStream xmlOutput ) throws IOException, SAXException
	{
		SAXWriter saxWriter = getXMLWriter( xmlOutput );
		saxWriter.setBypassXMLDeclaration ( ef.isFragment ( ) );
		XMLReader reader = ef.createEXIReader( );
		reader.setContentHandler ( saxWriter );
		reader.setErrorHandler( saxWriter );
		reader.parse ( new InputSource( exiDocument ) );
	}
	
	public static void main ( String[] args ) throws Exception
	{
		//	create test-decoder
		TestDecoder testDecoder = new TestDecoder();
		
		//	get factory
		EXIFactory ef = testDecoder.getQuickTestEXIactory();
		
		//	exi document
		InputStream exiDocument = new FileInputStream( QuickTestConfiguration.getExiLocation ( ) );
		
		//	decoded xml output
		String decodedXMLLocation = QuickTestConfiguration.getExiLocation ( ) + ".xml";
		OutputStream xmlOutput = new FileOutputStream( decodedXMLLocation );
		
		//	decode EXI to XML
		testDecoder.decodeTo( ef, exiDocument, xmlOutput );
		
		System.out.println( "[DEC] " + QuickTestConfiguration.getExiLocation ( ) + " --> " + decodedXMLLocation );
	}

}
