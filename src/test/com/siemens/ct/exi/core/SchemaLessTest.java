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

package com.siemens.ct.exi.core;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

import org.xml.sax.SAXException;

import com.siemens.ct.exi.CodingMode;
import com.siemens.ct.exi.EXIDecoder;
import com.siemens.ct.exi.EXIEncoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.event.EventType;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;
import com.siemens.ct.exi.util.ExpandedName;

public class SchemaLessTest extends TestCase {

    public SchemaLessTest(String testName)
    {
        super(testName);
    }

    public void testSchemaLess0() throws IOException, SAXException, EXIException
    {
    	EXIFactory factory = DefaultEXIFactory.newInstance();
    	
    	factory.setFidelityOptions ( FidelityOptions.createDefault( ) );
    	factory.setCodingMode ( CodingMode.BIT_PACKED );
    	//factory.setGrammar ( GrammarFactory.getSchemaLessGrammar ( ) );
    	
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	ExpandedName s1 = new ExpandedName( "", "el1" );
    	
    	//	encoder
    	{
        	EXIEncoder encoder = factory.createEXIEncoder ( );
        	encoder.setOutput ( baos );
        	encoder.encodeStartDocument ( );
        	encoder.encodeStartElement ( s1.namespaceURI, s1.localName );
        	encoder.encodeEndElement ( );
        	encoder.encodeEndDocument ( );    		
    	}
    	
    	//	decoder
    	{	
        	EXIDecoder decoder = factory.createEXIDecoder ( );
        	decoder.setInputStream ( new ByteArrayInputStream( baos.toByteArray ( ) ) );
        	
        	assertTrue ( decoder.getNextEventType ( ) == EventType.START_DOCUMENT );
        	decoder.decodeStartDocument ( );
        	
        	decoder.inspectEvent ( );
        	assertTrue ( decoder.getNextEventType ( ) == EventType.START_ELEMENT_GENERIC );
        	decoder.decodeStartElementGeneric ( );
        	assertTrue ( decoder.getElementURI ( ).equals ( s1.namespaceURI ) );
        	assertTrue ( decoder.getElementLocalName ( ).equals ( s1.localName ) );
        	
        	decoder.inspectEvent ( );
        	assertTrue ( decoder.getNextEventType ( ) == EventType.END_ELEMENT_UNDECLARED );
        	decoder.decodeEndElement ( );
        	
        	decoder.inspectEvent ( );
        	assertTrue ( decoder.getNextEventType ( ) == EventType.END_DOCUMENT );
        	decoder.decodeEndDocument ( );
    	}
    }
 
    public void testSchemaLess1() throws IOException, SAXException, EXIException
    {
    	EXIFactory factory = DefaultEXIFactory.newInstance();
    	
    	factory.setFidelityOptions ( FidelityOptions.createDefault( ) );
    	factory.setCodingMode ( CodingMode.BIT_PACKED );
    	//factory.setGrammar ( GrammarFactory.getSchemaLessGrammar ( ) );
    	
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	ExpandedName root = new ExpandedName( "", "root" );
    	ExpandedName el1 = new ExpandedName( "", "el1" );
    	ExpandedName el2 = new ExpandedName( "", "el2" );
    	ExpandedName el3 = new ExpandedName( "", "el3" );
    	String ch1 = "a";
    	String ch2 = "b";
    	String ch3 = "c";
    	
    	
    	//	encoder
    	{
        	EXIEncoder encoder = factory.createEXIEncoder ( );
        	encoder.setOutput ( baos );
        	
        	encoder.encodeStartDocument ( );
        	encoder.encodeStartElement ( root.namespaceURI, root.localName );
        	
        		encoder.encodeStartElement ( el1.namespaceURI, el1.localName );
        			encoder.encodeCharacters ( ch1 );
        		encoder.encodeEndElement ( );
        		
        		encoder.encodeStartElement ( el2.namespaceURI, el2.localName );
        			encoder.encodeCharacters ( ch2 );
        		encoder.encodeEndElement ( );
        		
        		encoder.encodeStartElement ( el3.namespaceURI, el3.localName );
        			encoder.encodeCharacters ( ch3 );
        		encoder.encodeEndElement ( );
        		
        		encoder.encodeStartElement ( el2.namespaceURI, el2.localName );
    				encoder.encodeCharacters ( ch1 );
    			encoder.encodeEndElement ( );
        		
        	encoder.encodeEndElement ( );
        	encoder.encodeEndDocument ( );
    	}

    	baos.flush ( );
    	
    	//	decoder
    	{
        	EXIDecoder decoder = factory.createEXIDecoder ( );
        	decoder.setInputStream ( new ByteArrayInputStream( baos.toByteArray ( ) ) );
        	decoder.decodeStartDocument ( );
        	
        	decoder.inspectEvent ( );
        	assertTrue ( decoder.getNextEventType ( ) == EventType.START_ELEMENT_GENERIC );
        	decoder.decodeStartElementGeneric ( );
        	assertTrue ( decoder.getElementURI ( ).equals ( root.namespaceURI ) );
        	assertTrue ( decoder.getElementLocalName ( ).equals ( root.localName ) );
        	
        		decoder.inspectEvent ( );
        		assertTrue ( decoder.getNextEventType ( ) == EventType.START_ELEMENT_GENERIC_UNDECLARED );
        		decoder.decodeStartElementGenericUndeclared ( );
            	assertTrue ( decoder.getElementURI ( ).equals ( el1.namespaceURI ) );
            	assertTrue ( decoder.getElementLocalName ( ).equals ( el1.localName ) );
        			decoder.inspectEvent ( );
        			assertTrue ( decoder.getNextEventType ( ) == EventType.CHARACTERS_GENERIC_UNDECLARED );
        			decoder.decodeCharactersGenericUndeclared ( );
        			assertTrue ( decoder.getCharacters ( ).equals ( ch1 ) );
        		decoder.inspectEvent ( );
        		assertTrue ( decoder.getNextEventType ( ) == EventType.END_ELEMENT );
        		decoder.decodeEndElement ( );
        		
        		decoder.inspectEvent ( );
        		assertTrue ( decoder.getNextEventType ( ) == EventType.START_ELEMENT_GENERIC_UNDECLARED );
        		decoder.decodeStartElementGenericUndeclared ( );
            	assertTrue ( decoder.getElementURI ( ).equals ( el2.namespaceURI ) );
            	assertTrue ( decoder.getElementLocalName ( ).equals ( el2.localName ) );
        			decoder.inspectEvent ( );
        			assertTrue ( decoder.getNextEventType ( ) == EventType.CHARACTERS_GENERIC_UNDECLARED );
	    			decoder.decodeCharactersGenericUndeclared ( );
	    			assertTrue ( decoder.getCharacters ( ).equals ( ch2 ) );
    			decoder.inspectEvent ( );
	    		assertTrue ( decoder.getNextEventType ( ) == EventType.END_ELEMENT );
        		decoder.decodeEndElement ( );
        		
        		decoder.inspectEvent ( );
        		assertTrue ( decoder.getNextEventType ( ) == EventType.START_ELEMENT_GENERIC_UNDECLARED );
        		decoder.decodeStartElementGenericUndeclared ( );
            	assertTrue ( decoder.getElementURI ( ).equals ( el3.namespaceURI ) );
            	assertTrue ( decoder.getElementLocalName ( ).equals ( el3.localName ) );
        			decoder.inspectEvent ( );
        			assertTrue ( decoder.getNextEventType ( ) == EventType.CHARACTERS_GENERIC_UNDECLARED );
        			decoder.decodeCharactersGenericUndeclared ( );
	    			assertTrue ( decoder.getCharacters ( ).equals ( ch3 ) );
    			decoder.inspectEvent ( );
    			assertTrue ( decoder.getNextEventType ( ) == EventType.END_ELEMENT );
        		decoder.decodeEndElement ( );
        		
        		decoder.inspectEvent ( );
        		assertTrue ( decoder.getNextEventType ( ) == EventType.START_ELEMENT );
        		decoder.decodeStartElement ( );
            	assertTrue ( decoder.getElementURI ( ).equals ( el2.namespaceURI ) );
            	assertTrue ( decoder.getElementLocalName ( ).equals ( el2.localName ) );
        			decoder.inspectEvent ( );
        			assertTrue ( decoder.getNextEventType ( ) == EventType.CHARACTERS );
        			decoder.decodeCharacters ( );
	    			assertTrue ( decoder.getCharacters ( ).equals ( ch1 ) );
    			decoder.inspectEvent ( );
    			assertTrue ( decoder.getNextEventType ( ) == EventType.END_ELEMENT );
        		decoder.decodeEndElement ( );
        		
        	decoder.inspectEvent ( );
        	assertTrue ( decoder.getNextEventType ( ) == EventType.END_ELEMENT );
        	decoder.decodeEndElement ( );
        	
        	decoder.inspectEvent ( );
        	assertTrue ( decoder.getNextEventType ( ) == EventType.END_DOCUMENT );
        	decoder.decodeEndDocument ( );
    	}
    }

	public void testSchemaLess2() throws IOException, SAXException, EXIException
	{
    	EXIFactory factory = DefaultEXIFactory.newInstance();
    	
    	factory.setFidelityOptions ( FidelityOptions.createDefault( ) );
    	factory.setCodingMode ( CodingMode.BIT_PACKED );
    	//factory.setGrammar ( GrammarFactory.getSchemaLessGrammar ( ) );
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ExpandedName root = new ExpandedName( "", "root" );
		ExpandedName el1 = new ExpandedName( "", "el1" );
		ExpandedName el2 = new ExpandedName( "", "el2" );
		String ch1 = "a";
		String ch2 = "b";
		
		ExpandedName at1 = new ExpandedName( "", "at1" );
		String atCh1 = "at-ch";
		
		
		//	encoder
		{
	    	EXIEncoder encoder = factory.createEXIEncoder ( );
	    	encoder.setOutput ( baos );
	    	
	    	encoder.encodeStartDocument ( );
	    	encoder.encodeStartElement ( root.namespaceURI, root.localName );
	    	encoder.encodeAttribute ( at1.namespaceURI, at1.localName, atCh1 );
	    	
	    		encoder.encodeStartElement ( el1.namespaceURI, el1.localName );
	    			encoder.encodeCharacters ( ch1 );
	    		encoder.encodeEndElement ( );
	    		
	    		encoder.encodeStartElement ( el2.namespaceURI, el2.localName );
	    		encoder.encodeAttribute ( at1.namespaceURI, at1.localName, atCh1 );
	    			encoder.encodeCharacters ( ch2 );
	    		encoder.encodeEndElement ( );
	    		
	    	encoder.encodeEndElement ( );
	    	encoder.encodeEndDocument ( );
		}
	
		baos.flush ( );
		
		//	decoder
		{
	    	EXIDecoder decoder = factory.createEXIDecoder ( );
	    	decoder.setInputStream ( new ByteArrayInputStream( baos.toByteArray ( ) ) );
	    	decoder.decodeStartDocument ( );
	    	
	    	decoder.inspectEvent ( );
	    	assertTrue ( decoder.getNextEventType ( ) == EventType.START_ELEMENT_GENERIC );
	    	decoder.decodeStartElementGeneric ( );
        	assertTrue ( decoder.getElementURI ( ).equals ( root.namespaceURI ) );
        	assertTrue ( decoder.getElementLocalName ( ).equals ( root.localName ) );
	    	
	    	decoder.inspectEvent ( );
	    	assertTrue ( decoder.getNextEventType ( ) == EventType.ATTRIBUTE_GENERIC_UNDECLARED );
	    	decoder.decodeAttributeGeneric ( );
        	assertTrue ( decoder.getAttributeURI ( ).equals (at1.namespaceURI ) );
        	assertTrue ( decoder.getAttributeLocalName ( ).equals ( at1.localName ) );
	    	assertTrue ( decoder.getAttributeValue ( ).equals ( atCh1 ) );
	    	
	    		decoder.inspectEvent ( );
	    		assertTrue ( decoder.getNextEventType ( ) == EventType.START_ELEMENT_GENERIC_UNDECLARED );
	    		decoder.decodeStartElementGenericUndeclared ( );
            	assertTrue ( decoder.getElementURI ( ).equals ( el1.namespaceURI ) );
            	assertTrue ( decoder.getElementLocalName ( ).equals ( el1.localName ) );
	    			decoder.inspectEvent ( );
	    			assertTrue ( decoder.getNextEventType ( ) == EventType.CHARACTERS_GENERIC_UNDECLARED );
	    			decoder.decodeCharactersGenericUndeclared ( );
	    			assertTrue ( decoder.getCharacters ( ).equals ( ch1 ) );
    			decoder.inspectEvent ( );
	    		assertTrue ( decoder.getNextEventType ( ) == EventType.END_ELEMENT );
	    		decoder.decodeEndElement ( );
	    		
	    		decoder.inspectEvent ( );
	    		assertTrue ( decoder.getNextEventType ( ) == EventType.START_ELEMENT_GENERIC_UNDECLARED );
	    		decoder.decodeStartElementGenericUndeclared ( );
            	assertTrue ( decoder.getElementURI ( ).equals ( el2.namespaceURI ) );
            	assertTrue ( decoder.getElementLocalName ( ).equals ( el2.localName ) );
	    		decoder.inspectEvent ( );
		    	assertTrue ( decoder.getNextEventType ( ) == EventType.ATTRIBUTE_GENERIC_UNDECLARED );
		    	decoder.decodeAttributeGeneric ( );
	        	assertTrue ( decoder.getAttributeURI ( ).equals (at1.namespaceURI ) );
	        	assertTrue ( decoder.getAttributeLocalName ( ).equals ( at1.localName ) );
		    	assertTrue ( decoder.getAttributeValue ( ).equals ( atCh1 ) );
		    	
		    		decoder.inspectEvent ( );
	    			assertTrue ( decoder.getNextEventType ( ) == EventType.CHARACTERS_GENERIC_UNDECLARED );
	    			decoder.decodeCharactersGenericUndeclared ( );
	    			assertTrue ( decoder.getCharacters ( ).equals ( ch2 ) );
	    		decoder.inspectEvent ( );
	    		assertTrue ( decoder.getNextEventType ( ) == EventType.END_ELEMENT );
	    		decoder.decodeEndElement ( );
	    		
	    	decoder.inspectEvent ( );
	    	assertTrue ( decoder.getNextEventType ( ) == EventType.END_ELEMENT );
	    	decoder.decodeEndElement ( );
	    	
	    	decoder.inspectEvent ( );
	    	assertTrue ( decoder.getNextEventType ( ) == EventType.END_DOCUMENT );
	    	decoder.decodeEndDocument ( );
		}
	}

	public void testSchemaLess3() throws IOException, SAXException, EXIException
	{
    	EXIFactory factory = DefaultEXIFactory.newInstance();
    	
    	factory.setFidelityOptions ( FidelityOptions.createDefault( ) );
    	factory.setCodingMode ( CodingMode.BIT_PACKED );
    	//factory.setGrammar ( GrammarFactory.getSchemaLessGrammar ( ) );
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ExpandedName root = new ExpandedName( "", "root" );
		ExpandedName el1 = new ExpandedName( "", "el1" );
		ExpandedName elx1 = new ExpandedName( "", "elx1" );
		ExpandedName elx2 = new ExpandedName( "", "elx2" );
		ExpandedName elx3 = new ExpandedName( "", "elx3" );
		
		ExpandedName elxx1 = new ExpandedName( "", "elxx1" );
		ExpandedName elxx2 = new ExpandedName( "", "elxx2" );
		
		String ch1 = "a";
		String ch2 = "b";
		String ch3 = "c";
		
		ExpandedName at1 = new ExpandedName( "", "at1" );
		String atCh1 = "at-ch1";
		String atCh2 = "at-ch2";
		
		
		//	encoder
		{
	    	EXIEncoder encoder = factory.createEXIEncoder ( );
	    	encoder.setOutput ( baos );
	    	
	    	encoder.encodeStartDocument ( );
	    	encoder.encodeStartElement ( root.namespaceURI, root.localName );
	    	
	    		encoder.encodeStartElement ( el1.namespaceURI, el1.localName );
	    		encoder.encodeAttribute ( at1.namespaceURI, at1.localName, atCh1 );
	    			encoder.encodeStartElement ( elx1.namespaceURI, elx1.localName );
	    			
		    			encoder.encodeStartElement ( elxx1.namespaceURI, elxx1.localName );
	    					encoder.encodeCharacters ( ch1 );
	    				encoder.encodeEndElement ( );
		    			encoder.encodeStartElement ( elxx2.namespaceURI, elxx2.localName );
    						encoder.encodeCharacters ( ch2 );
    					encoder.encodeEndElement ( );	    			
    					
	    			encoder.encodeEndElement ( );
	    			encoder.encodeStartElement ( elx2.namespaceURI, elx2.localName );
    					encoder.encodeCharacters ( ch2 );
    				encoder.encodeEndElement ( );
    				encoder.encodeStartElement ( elx3.namespaceURI, elx3.localName );
						encoder.encodeCharacters ( ch3 );
					encoder.encodeEndElement ( );
	    		encoder.encodeEndElement ( );

	    		encoder.encodeStartElement ( el1.namespaceURI, el1.localName);
	    		encoder.encodeAttribute ( at1.namespaceURI, at1.localName, atCh2 );
	    			encoder.encodeStartElement ( elx1.namespaceURI, elx1.localName );
	    			
		    			encoder.encodeStartElement ( elxx1.namespaceURI, elxx1.localName );
	    					encoder.encodeCharacters ( ch1 );
	    				encoder.encodeEndElement ( );
		    			encoder.encodeStartElement ( elxx2.namespaceURI, elxx2.localName );
    						encoder.encodeCharacters ( ch2 );
    					encoder.encodeEndElement ( );	    			
    					
	    			encoder.encodeEndElement ( );
	    			encoder.encodeStartElement ( elx2.namespaceURI, elx2.localName );
    					encoder.encodeCharacters ( ch2 );
    				encoder.encodeEndElement ( );
    				encoder.encodeStartElement ( elx3.namespaceURI, elx3.localName );
						encoder.encodeCharacters ( ch3 );
					encoder.encodeEndElement ( );
	    		encoder.encodeEndElement ( );
	    		
	    	encoder.encodeEndElement ( );
	    	encoder.encodeEndDocument ( );
		}
	
		baos.flush ( );
		
		//	decoder
		{
	    	EXIDecoder decoder = factory.createEXIDecoder ( );
	    	decoder.setInputStream ( new ByteArrayInputStream( baos.toByteArray ( ) ) );
	    	
	    	assertTrue ( decoder.getNextEventType ( ) == EventType.START_DOCUMENT );
	    	decoder.decodeStartDocument ( );
	    	
	    	decoder.inspectEvent ( );
	    	assertTrue ( decoder.getNextEventType ( ) == EventType.START_ELEMENT_GENERIC );
	    	decoder.decodeStartElementGeneric ( );
        	assertTrue ( decoder.getElementURI ( ).equals ( root.namespaceURI ) );
        	assertTrue ( decoder.getElementLocalName ( ).equals ( root.localName ) );
	    	
	    		/*
	    		 * first el1
	    		 */
	    		decoder.inspectEvent ( );
	    		assertTrue ( decoder.getNextEventType ( ) == EventType.START_ELEMENT_GENERIC_UNDECLARED );
	    		decoder.decodeStartElementGenericUndeclared ( );
	        	assertTrue ( decoder.getElementURI ( ).equals ( el1.namespaceURI ) );
	        	assertTrue ( decoder.getElementLocalName ( ).equals ( el1.localName ) );

	    		decoder.inspectEvent ( );
		    	assertTrue ( decoder.getNextEventType ( ) == EventType.ATTRIBUTE_GENERIC_UNDECLARED );
		    	decoder.decodeAttributeGeneric ( );
		    	assertTrue ( decoder.getAttributeURI ( ).equals (at1.namespaceURI ) );
	        	assertTrue ( decoder.getAttributeLocalName ( ).equals ( at1.localName ) );
		    	assertTrue ( decoder.getAttributeValue ( ).equals ( atCh1 ) );
	    		
		    		decoder.inspectEvent ( );
		    		assertTrue ( decoder.getNextEventType ( ) == EventType.START_ELEMENT_GENERIC_UNDECLARED );
		    		decoder.decodeStartElementGenericUndeclared ( );
		        	assertTrue ( decoder.getElementURI ( ).equals ( elx1.namespaceURI ) );
		        	assertTrue ( decoder.getElementLocalName ( ).equals ( elx1.localName ) );

		    			decoder.inspectEvent ( );
			    		assertTrue ( decoder.getNextEventType ( ) == EventType.START_ELEMENT_GENERIC_UNDECLARED );
			    		decoder.decodeStartElementGenericUndeclared ( );
			        	assertTrue ( decoder.getElementURI ( ).equals ( elxx1.namespaceURI ) );
			        	assertTrue ( decoder.getElementLocalName ( ).equals ( elxx1.localName ) );
		    		
			    			decoder.inspectEvent ( );
			    			assertTrue ( decoder.getNextEventType ( ) == EventType.CHARACTERS_GENERIC_UNDECLARED );
			    			decoder.decodeCharactersGenericUndeclared ( );
			    			assertTrue ( decoder.getCharacters ( ).equals ( ch1 ) );

			    		decoder.inspectEvent ( );
			    		assertTrue ( decoder.getNextEventType ( ) == EventType.END_ELEMENT );
			    		decoder.decodeEndElement ( );

			    		decoder.inspectEvent ( );
			    		assertTrue ( decoder.getNextEventType ( ) == EventType.START_ELEMENT_GENERIC_UNDECLARED );
			    		decoder.decodeStartElementGenericUndeclared ( );
			        	assertTrue ( decoder.getElementURI ( ).equals ( elxx2.namespaceURI ) );
			        	assertTrue ( decoder.getElementLocalName ( ).equals ( elxx2.localName ) );
			    			
			    			decoder.inspectEvent ( );
			    			assertTrue ( decoder.getNextEventType ( ) == EventType.CHARACTERS_GENERIC_UNDECLARED );
			    			decoder.decodeCharactersGenericUndeclared ( );
			    			assertTrue ( decoder.getCharacters ( ).equals ( ch2 ) );
			    			
			    		decoder.inspectEvent ( );
			    		assertTrue ( decoder.getNextEventType ( ) == EventType.END_ELEMENT );
			    		decoder.decodeEndElement ( );
			    		
			    	decoder.inspectEvent ( );
		    		assertTrue ( decoder.getNextEventType ( ) == EventType.END_ELEMENT );
		    		decoder.decodeEndElement ( );
		    		
		    		decoder.inspectEvent ( );
		    		assertTrue ( decoder.getNextEventType ( ) == EventType.START_ELEMENT_GENERIC_UNDECLARED );
		    		decoder.decodeStartElementGenericUndeclared ( );
		        	assertTrue ( decoder.getElementURI ( ).equals ( elx2.namespaceURI ) );
		        	assertTrue ( decoder.getElementLocalName ( ).equals ( elx2.localName ) );
		    		
		    			decoder.inspectEvent ( );
		    			assertTrue ( decoder.getNextEventType ( ) == EventType.CHARACTERS_GENERIC_UNDECLARED );
		    			decoder.decodeCharactersGenericUndeclared ( );
		    			assertTrue ( decoder.getCharacters ( ).equals ( ch2 ) );    		
			    			
		    		decoder.inspectEvent ( );
		    		assertTrue ( decoder.getNextEventType ( ) == EventType.END_ELEMENT );
		    		decoder.decodeEndElement ( );
			    		
		    		decoder.inspectEvent ( );
		    		assertTrue ( decoder.getNextEventType ( ) == EventType.START_ELEMENT_GENERIC_UNDECLARED );
		    		decoder.decodeStartElementGenericUndeclared ( );
		        	assertTrue ( decoder.getElementURI ( ).equals ( elx3.namespaceURI ) );
		        	assertTrue ( decoder.getElementLocalName ( ).equals ( elx3.localName ) );
		    		
		    			decoder.inspectEvent ( );
		    			assertTrue ( decoder.getNextEventType ( ) == EventType.CHARACTERS_GENERIC_UNDECLARED );
		    			decoder.decodeCharactersGenericUndeclared ( );
		    			assertTrue ( decoder.getCharacters ( ).equals ( ch3 ) );		    		
			    			
		    		decoder.inspectEvent ( );
		    		assertTrue ( decoder.getNextEventType ( ) == EventType.END_ELEMENT );
		    		decoder.decodeEndElement ( );
			    
		    	decoder.inspectEvent ( );
	    		assertTrue ( decoder.getNextEventType ( ) == EventType.END_ELEMENT );
	    		decoder.decodeEndElement ( );
	    		
	    		/*
	    		 * second el1
	    		 */
	    		//	still generic start element, because first el1 was a StartTag rule, this is a Content rule
	    		decoder.inspectEvent ( );
	    		assertTrue ( decoder.getNextEventType ( ) == EventType.START_ELEMENT_GENERIC_UNDECLARED );
	    		decoder.decodeStartElementGenericUndeclared ( );
	        	assertTrue ( decoder.getElementURI ( ).equals ( el1.namespaceURI ) );
	        	assertTrue ( decoder.getElementLocalName ( ).equals ( el1.localName ) );

	    		decoder.inspectEvent ( );
		    	assertTrue ( decoder.getNextEventType ( ) == EventType.ATTRIBUTE );
		    	decoder.decodeAttribute ( );
		    	assertTrue ( decoder.getAttributeURI ( ).equals (at1.namespaceURI ) );
	        	assertTrue ( decoder.getAttributeLocalName ( ).equals ( at1.localName ) );
		    	assertTrue ( decoder.getAttributeValue ( ).equals ( atCh2 ) );
	    		
		    		decoder.inspectEvent ( );
		    		assertTrue ( decoder.getNextEventType ( ) == EventType.START_ELEMENT );
		    		decoder.decodeStartElement ( );
		        	assertTrue ( decoder.getElementURI ( ).equals ( elx1.namespaceURI ) );
		        	assertTrue ( decoder.getElementLocalName ( ).equals ( elx1.localName ) );

		    			decoder.inspectEvent ( );
			    		assertTrue ( decoder.getNextEventType ( ) == EventType.START_ELEMENT );
			    		decoder.decodeStartElement ( );
			        	assertTrue ( decoder.getElementURI ( ).equals ( elxx1.namespaceURI ) );
			        	assertTrue ( decoder.getElementLocalName ( ).equals ( elxx1.localName ) );
		    		
			    			decoder.inspectEvent ( );
			    			assertTrue ( decoder.getNextEventType ( ) == EventType.CHARACTERS );
			    			decoder.decodeCharacters ( );
			    			assertTrue ( decoder.getCharacters ( ).equals ( ch1 ) );
			    			
			    		decoder.inspectEvent ( );
			    		assertTrue ( decoder.getNextEventType ( ) == EventType.END_ELEMENT );
			    		decoder.decodeEndElement ( );

			    		decoder.inspectEvent ( );
			    		assertTrue ( decoder.getNextEventType ( ) == EventType.START_ELEMENT );
			    		decoder.decodeStartElement ( );
			        	assertTrue ( decoder.getElementURI ( ).equals ( elxx2.namespaceURI ) );
			        	assertTrue ( decoder.getElementLocalName ( ).equals ( elxx2.localName ) );
			    			
			    			decoder.inspectEvent ( );
			    			assertTrue ( decoder.getNextEventType ( ) == EventType.CHARACTERS );
			    			decoder.decodeCharacters ( );
			    			assertTrue ( decoder.getCharacters ( ).equals ( ch2 ) );
			    			
			    		decoder.inspectEvent ( );
			    		assertTrue ( decoder.getNextEventType ( ) == EventType.END_ELEMENT );
			    		decoder.decodeEndElement ( );
			    	
			    	decoder.inspectEvent ( );
		    		assertTrue ( decoder.getNextEventType ( ) == EventType.END_ELEMENT );
		    		decoder.decodeEndElement ( );
		    		
		    		decoder.inspectEvent ( );
		    		assertTrue ( decoder.getNextEventType ( ) == EventType.START_ELEMENT );
		    		decoder.decodeStartElement ( );
		        	assertTrue ( decoder.getElementURI ( ).equals ( elx2.namespaceURI ) );
		        	assertTrue ( decoder.getElementLocalName ( ).equals ( elx2.localName ) );
		    		
		    			decoder.inspectEvent ( );
		    			assertTrue ( decoder.getNextEventType ( ) == EventType.CHARACTERS );
		    			decoder.decodeCharacters ( );
		    			assertTrue ( decoder.getCharacters ( ).equals ( ch2 ) );    		
			    			
	    			decoder.inspectEvent ( );
		    		assertTrue ( decoder.getNextEventType ( ) == EventType.END_ELEMENT );
		    		decoder.decodeEndElement ( );
			    		
		    		decoder.inspectEvent ( );
		    		assertTrue ( decoder.getNextEventType ( ) == EventType.START_ELEMENT );
		    		decoder.decodeStartElement ( );
		        	assertTrue ( decoder.getElementURI ( ).equals ( elx3.namespaceURI ) );
		        	assertTrue ( decoder.getElementLocalName ( ).equals ( elx3.localName ) );
		    		
		    			decoder.inspectEvent ( );
		    			assertTrue ( decoder.getNextEventType ( ) == EventType.CHARACTERS );
		    			decoder.decodeCharacters ( );
		    			assertTrue ( decoder.getCharacters ( ).equals ( ch3 ) );		    		
			    		
	    			decoder.inspectEvent ( );
		    		assertTrue ( decoder.getNextEventType ( ) == EventType.END_ELEMENT );
		    		decoder.decodeEndElement ( );
			    		
	    		decoder.inspectEvent ( );
	    		assertTrue ( decoder.getNextEventType ( ) == EventType.END_ELEMENT );
	    		decoder.decodeEndElement ( );

	    	decoder.inspectEvent ( );
	    	assertTrue ( decoder.getNextEventType ( ) == EventType.END_ELEMENT );
	    	decoder.decodeEndElement ( );
	    	
	    	decoder.inspectEvent ( );
	    	assertTrue ( decoder.getNextEventType ( ) == EventType.END_DOCUMENT );
	    	decoder.decodeEndDocument ( );
		}
	}
    
}