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

package com.siemens.ct.exi.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

import org.custommonkey.xmlunit.XMLTestCase;
import org.xml.sax.InputSource;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.QuickTestConfiguration;
import com.siemens.ct.exi.TestDecoder;
import com.siemens.ct.exi.TestEncoder;
import com.siemens.ct.exi.grammar.Grammar;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;
import com.siemens.ct.exi.util.FragmentUtilities;
import com.siemens.ct.exi.util.xml.SAXWriter;

public abstract class AbstractTestCase extends XMLTestCase
{
	protected Vector<TestCaseOption> testCaseOptions = new Vector<TestCaseOption>();
	protected GrammarFactory grammarFactory = GrammarFactory.newInstance ( );
	
	public AbstractTestCase ( String s )
	{
		super ( s );
	}
	
	public static SAXWriter getXMLWriter( OutputStream os ) throws UnsupportedEncodingException, FileNotFoundException
	{
		SAXWriter xmlWriter = new SAXWriter();
		xmlWriter.setOutput( os, "UTF8");
    	
    	return xmlWriter;
	}
	
	private void _testOption( TestCaseOption tco ) throws Exception
	{
		if ( tco.isSchemaInformedOnly ( ) && tco.getSchemaLocation ( ) == null )
		{
			return;
		}
		
		//	exi factory
		EXIFactory ef = DefaultEXIFactory.newInstance ( );
		ef.setCodingMode ( tco.getCodingMode ( ) );
		ef.setFidelityOptions ( tco.getFidelityOptions ( ) );
		ef.setFragment ( tco.isFragments ( ) );
		//	schema-informed grammar ?
		if ( tco.getSchemaLocation ( ) != null )
		{
			Grammar grammar = grammarFactory.createGrammar ( tco.getSchemaLocation ( ) );
			ef.setGrammar ( grammar );
		}
		
		//	XML input stream
		InputStream xmlInput = new FileInputStream( QuickTestConfiguration.getXmlLocation ( ) );
		//	EXI output stream
		ByteArrayOutputStream encodedOutput = new ByteArrayOutputStream();
		
		//	-> encode
		TestEncoder testEncoder = new TestEncoder();
		testEncoder.encodeTo ( ef, xmlInput, encodedOutput );
		
		//	EXI input stream
		ByteArrayInputStream exiDocument =  new ByteArrayInputStream( encodedOutput.toByteArray ( ) );
		//	decoded XML
		ByteArrayOutputStream xmlOutput = new ByteArrayOutputStream();
		
		//	<-- decode
		TestDecoder testDecoder = new TestDecoder();
		testDecoder.decodeTo ( ef, exiDocument, xmlOutput );

		//	check XML validity
		if ( tco.isXmlEqual ( ) )
		{
			//Reader control = new FileReader( QuickTestConfiguration.getXmlLocation ( ) );
			InputStream control = new FileInputStream( QuickTestConfiguration.getXmlLocation ( ) );
			//Reader test = new StringReader( xmlOutput.toString ( ) );
			InputStream test = new ByteArrayInputStream( xmlOutput.toByteArray ( ) );
			
			if ( ef.isFragment ( ) )
			{
				//	surround with root element for equality check
				control = FragmentUtilities.getSurroundingRootInputStream ( control );
				test = FragmentUtilities.getSurroundingRootInputStream ( test );
			}
			
			assertXMLEqual ( new InputSource( control ), new InputSource( test ) );
		}
		else
		{
			//	TODO	DOCTYPE ?
			//assertXMLValid ( decodedXML );
		}
	}
	
	
	protected void _test( ) throws Exception
	{
		//	schema-less
		_test( null );
		
		//	schema-informed
		_test( QuickTestConfiguration.getXsdLocation ( ) );
	}


	
	private void _test( String schemaLocation ) throws Exception
	{
		//	test options
		for ( int i=0; i<testCaseOptions.size ( ); i++)
		{
			TestCaseOption tco = testCaseOptions.get ( i );
			//	update schema
			tco.setSchemaLocation ( schemaLocation );
			try
			{
				_testOption( tco );
			}
			catch ( Exception e )
			{
				throw new Exception( e.getLocalizedMessage ( ) + " [" + tco.toString ( ) + "]", e );
			}
		}
	}
	
	
}
