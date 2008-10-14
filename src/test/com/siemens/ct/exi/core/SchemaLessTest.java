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
        	encoder.encodeStartElement ( s1.getNamespaceURI ( ), s1.getLocalName ( ) );
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
        	assertTrue ( decoder.getElementURI ( ).equals ( s1.getNamespaceURI ( ) ) );
        	assertTrue ( decoder.getElementLocalName ( ).equals ( s1.getLocalName ( ) ) );
        	
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
        	encoder.encodeStartElement ( root.getNamespaceURI ( ), root.getLocalName ( ) );
        	
        		encoder.encodeStartElement ( el1.getNamespaceURI ( ), el1.getLocalName ( ) );
        			encoder.encodeCharacters ( ch1 );
        		encoder.encodeEndElement ( );
        		
        		encoder.encodeStartElement ( el2.getNamespaceURI ( ), el2.getLocalName ( ) );
        			encoder.encodeCharacters ( ch2 );
        		encoder.encodeEndElement ( );
        		
        		encoder.encodeStartElement ( el3.getNamespaceURI ( ), el3.getLocalName ( ) );
        			encoder.encodeCharacters ( ch3 );
        		encoder.encodeEndElement ( );
        		
        		encoder.encodeStartElement ( el2.getNamespaceURI ( ), el2.getLocalName ( ) );
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
        	assertTrue ( decoder.getElementURI ( ).equals ( root.getNamespaceURI ( ) ) );
        	assertTrue ( decoder.getElementLocalName ( ).equals ( root.getLocalName ( ) ) );
        	
        		decoder.inspectEvent ( );
        		assertTrue ( decoder.getNextEventType ( ) == EventType.START_ELEMENT_GENERIC_UNDECLARED );
        		decoder.decodeStartElementGenericUndeclared ( );
            	assertTrue ( decoder.getElementURI ( ).equals ( el1.getNamespaceURI ( ) ) );
            	assertTrue ( decoder.getElementLocalName ( ).equals ( el1.getLocalName ( ) ) );
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
            	assertTrue ( decoder.getElementURI ( ).equals ( el2.getNamespaceURI ( ) ) );
            	assertTrue ( decoder.getElementLocalName ( ).equals ( el2.getLocalName ( ) ) );
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
            	assertTrue ( decoder.getElementURI ( ).equals ( el3.getNamespaceURI ( ) ) );
            	assertTrue ( decoder.getElementLocalName ( ).equals ( el3.getLocalName ( ) ) );
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
            	assertTrue ( decoder.getElementURI ( ).equals ( el2.getNamespaceURI ( ) ) );
            	assertTrue ( decoder.getElementLocalName ( ).equals ( el2.getLocalName ( ) ) );
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
	    	encoder.encodeStartElement ( root.getNamespaceURI ( ), root.getLocalName ( ) );
	    	encoder.encodeAttribute ( at1.getNamespaceURI ( ), at1.getLocalName ( ), atCh1 );
	    	
	    		encoder.encodeStartElement ( el1.getNamespaceURI ( ), el1.getLocalName ( ) );
	    			encoder.encodeCharacters ( ch1 );
	    		encoder.encodeEndElement ( );
	    		
	    		encoder.encodeStartElement ( el2.getNamespaceURI ( ), el2.getLocalName ( ) );
	    		encoder.encodeAttribute ( at1.getNamespaceURI ( ), at1.getLocalName ( ), atCh1 );
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
        	assertTrue ( decoder.getElementURI ( ).equals ( root.getNamespaceURI ( ) ) );
        	assertTrue ( decoder.getElementLocalName ( ).equals ( root.getLocalName ( ) ) );
	    	
	    	decoder.inspectEvent ( );
	    	assertTrue ( decoder.getNextEventType ( ) == EventType.ATTRIBUTE_GENERIC_UNDECLARED );
	    	decoder.decodeAttributeGenericUndeclared ( );
        	assertTrue ( decoder.getAttributeURI ( ).equals (at1.getNamespaceURI ( ) ) );
        	assertTrue ( decoder.getAttributeLocalName ( ).equals ( at1.getLocalName ( ) ) );
	    	assertTrue ( decoder.getAttributeValue ( ).equals ( atCh1 ) );
	    	
	    		decoder.inspectEvent ( );
	    		assertTrue ( decoder.getNextEventType ( ) == EventType.START_ELEMENT_GENERIC_UNDECLARED );
	    		decoder.decodeStartElementGenericUndeclared ( );
            	assertTrue ( decoder.getElementURI ( ).equals ( el1.getNamespaceURI ( ) ) );
            	assertTrue ( decoder.getElementLocalName ( ).equals ( el1.getLocalName ( ) ) );
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
            	assertTrue ( decoder.getElementURI ( ).equals ( el2.getNamespaceURI ( ) ) );
            	assertTrue ( decoder.getElementLocalName ( ).equals ( el2.getLocalName ( ) ) );
	    		decoder.inspectEvent ( );
		    	assertTrue ( decoder.getNextEventType ( ) == EventType.ATTRIBUTE_GENERIC_UNDECLARED );
		    	decoder.decodeAttributeGenericUndeclared ( );
	        	assertTrue ( decoder.getAttributeURI ( ).equals (at1.getNamespaceURI ( ) ) );
	        	assertTrue ( decoder.getAttributeLocalName ( ).equals ( at1.getLocalName ( ) ) );
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
	    	encoder.encodeStartElement ( root.getNamespaceURI ( ), root.getLocalName ( ) );
	    	
	    		encoder.encodeStartElement ( el1.getNamespaceURI ( ), el1.getLocalName ( ) );
	    		encoder.encodeAttribute ( at1.getNamespaceURI ( ), at1.getLocalName ( ), atCh1 );
	    			encoder.encodeStartElement ( elx1.getNamespaceURI ( ), elx1.getLocalName ( ) );
	    			
		    			encoder.encodeStartElement ( elxx1.getNamespaceURI ( ), elxx1.getLocalName ( ) );
	    					encoder.encodeCharacters ( ch1 );
	    				encoder.encodeEndElement ( );
		    			encoder.encodeStartElement ( elxx2.getNamespaceURI ( ), elxx2.getLocalName ( ) );
    						encoder.encodeCharacters ( ch2 );
    					encoder.encodeEndElement ( );	    			
    					
	    			encoder.encodeEndElement ( );
	    			encoder.encodeStartElement ( elx2.getNamespaceURI ( ), elx2.getLocalName ( ) );
    					encoder.encodeCharacters ( ch2 );
    				encoder.encodeEndElement ( );
    				encoder.encodeStartElement ( elx3.getNamespaceURI ( ), elx3.getLocalName ( ) );
						encoder.encodeCharacters ( ch3 );
					encoder.encodeEndElement ( );
	    		encoder.encodeEndElement ( );

	    		encoder.encodeStartElement ( el1.getNamespaceURI ( ), el1.getLocalName ( ));
	    		encoder.encodeAttribute ( at1.getNamespaceURI ( ), at1.getLocalName ( ), atCh2 );
	    			encoder.encodeStartElement ( elx1.getNamespaceURI ( ), elx1.getLocalName ( ) );
	    			
		    			encoder.encodeStartElement ( elxx1.getNamespaceURI ( ), elxx1.getLocalName ( ) );
	    					encoder.encodeCharacters ( ch1 );
	    				encoder.encodeEndElement ( );
		    			encoder.encodeStartElement ( elxx2.getNamespaceURI ( ), elxx2.getLocalName ( ) );
    						encoder.encodeCharacters ( ch2 );
    					encoder.encodeEndElement ( );	    			
    					
	    			encoder.encodeEndElement ( );
	    			encoder.encodeStartElement ( elx2.getNamespaceURI ( ), elx2.getLocalName ( ) );
    					encoder.encodeCharacters ( ch2 );
    				encoder.encodeEndElement ( );
    				encoder.encodeStartElement ( elx3.getNamespaceURI ( ), elx3.getLocalName ( ) );
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
        	assertTrue ( decoder.getElementURI ( ).equals ( root.getNamespaceURI ( ) ) );
        	assertTrue ( decoder.getElementLocalName ( ).equals ( root.getLocalName ( ) ) );
	    	
	    		/*
	    		 * first el1
	    		 */
	    		decoder.inspectEvent ( );
	    		assertTrue ( decoder.getNextEventType ( ) == EventType.START_ELEMENT_GENERIC_UNDECLARED );
	    		decoder.decodeStartElementGenericUndeclared ( );
	        	assertTrue ( decoder.getElementURI ( ).equals ( el1.getNamespaceURI ( ) ) );
	        	assertTrue ( decoder.getElementLocalName ( ).equals ( el1.getLocalName ( ) ) );

	    		decoder.inspectEvent ( );
		    	assertTrue ( decoder.getNextEventType ( ) == EventType.ATTRIBUTE_GENERIC_UNDECLARED );
		    	decoder.decodeAttributeGenericUndeclared ( );
		    	assertTrue ( decoder.getAttributeURI ( ).equals (at1.getNamespaceURI ( ) ) );
	        	assertTrue ( decoder.getAttributeLocalName ( ).equals ( at1.getLocalName ( ) ) );
		    	assertTrue ( decoder.getAttributeValue ( ).equals ( atCh1 ) );
	    		
		    		decoder.inspectEvent ( );
		    		assertTrue ( decoder.getNextEventType ( ) == EventType.START_ELEMENT_GENERIC_UNDECLARED );
		    		decoder.decodeStartElementGenericUndeclared ( );
		        	assertTrue ( decoder.getElementURI ( ).equals ( elx1.getNamespaceURI ( ) ) );
		        	assertTrue ( decoder.getElementLocalName ( ).equals ( elx1.getLocalName ( ) ) );

		    			decoder.inspectEvent ( );
			    		assertTrue ( decoder.getNextEventType ( ) == EventType.START_ELEMENT_GENERIC_UNDECLARED );
			    		decoder.decodeStartElementGenericUndeclared ( );
			        	assertTrue ( decoder.getElementURI ( ).equals ( elxx1.getNamespaceURI ( ) ) );
			        	assertTrue ( decoder.getElementLocalName ( ).equals ( elxx1.getLocalName ( ) ) );
		    		
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
			        	assertTrue ( decoder.getElementURI ( ).equals ( elxx2.getNamespaceURI ( ) ) );
			        	assertTrue ( decoder.getElementLocalName ( ).equals ( elxx2.getLocalName ( ) ) );
			    			
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
		        	assertTrue ( decoder.getElementURI ( ).equals ( elx2.getNamespaceURI ( ) ) );
		        	assertTrue ( decoder.getElementLocalName ( ).equals ( elx2.getLocalName ( ) ) );
		    		
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
		        	assertTrue ( decoder.getElementURI ( ).equals ( elx3.getNamespaceURI ( ) ) );
		        	assertTrue ( decoder.getElementLocalName ( ).equals ( elx3.getLocalName ( ) ) );
		    		
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
	        	assertTrue ( decoder.getElementURI ( ).equals ( el1.getNamespaceURI ( ) ) );
	        	assertTrue ( decoder.getElementLocalName ( ).equals ( el1.getLocalName ( ) ) );

	    		decoder.inspectEvent ( );
		    	assertTrue ( decoder.getNextEventType ( ) == EventType.ATTRIBUTE );
		    	decoder.decodeAttribute ( );
		    	assertTrue ( decoder.getAttributeURI ( ).equals (at1.getNamespaceURI ( ) ) );
	        	assertTrue ( decoder.getAttributeLocalName ( ).equals ( at1.getLocalName ( ) ) );
		    	assertTrue ( decoder.getAttributeValue ( ).equals ( atCh2 ) );
	    		
		    		decoder.inspectEvent ( );
		    		assertTrue ( decoder.getNextEventType ( ) == EventType.START_ELEMENT );
		    		decoder.decodeStartElement ( );
		        	assertTrue ( decoder.getElementURI ( ).equals ( elx1.getNamespaceURI ( ) ) );
		        	assertTrue ( decoder.getElementLocalName ( ).equals ( elx1.getLocalName ( ) ) );

		    			decoder.inspectEvent ( );
			    		assertTrue ( decoder.getNextEventType ( ) == EventType.START_ELEMENT );
			    		decoder.decodeStartElement ( );
			        	assertTrue ( decoder.getElementURI ( ).equals ( elxx1.getNamespaceURI ( ) ) );
			        	assertTrue ( decoder.getElementLocalName ( ).equals ( elxx1.getLocalName ( ) ) );
		    		
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
			        	assertTrue ( decoder.getElementURI ( ).equals ( elxx2.getNamespaceURI ( ) ) );
			        	assertTrue ( decoder.getElementLocalName ( ).equals ( elxx2.getLocalName ( ) ) );
			    			
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
		        	assertTrue ( decoder.getElementURI ( ).equals ( elx2.getNamespaceURI ( ) ) );
		        	assertTrue ( decoder.getElementLocalName ( ).equals ( elx2.getLocalName ( ) ) );
		    		
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
		        	assertTrue ( decoder.getElementURI ( ).equals ( elx3.getNamespaceURI ( ) ) );
		        	assertTrue ( decoder.getElementLocalName ( ).equals ( elx3.getLocalName ( ) ) );
		    		
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