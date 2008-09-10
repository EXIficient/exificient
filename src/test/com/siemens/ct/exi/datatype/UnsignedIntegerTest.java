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

package com.siemens.ct.exi.datatype;

import java.io.IOException;
import java.math.BigInteger;

import com.siemens.ct.exi.exceptions.XMLParsingException;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.util.datatype.XSDInteger;

public class UnsignedIntegerTest extends AbstractTestCase {

    public UnsignedIntegerTest(String testName)
    {
        super(testName);
    }

    public void testUnsignedInteger0() throws IOException
    {
    	//	Bit
    	getBitEncoder().encodeUnsignedInteger( 0 );
        assertTrue(getBitDecoder().decodeUnsignedInteger() == 0 );
        //	Byte
        getByteEncoder().encodeUnsignedInteger( 0 );
        assertTrue(getByteDecoder().decodeUnsignedInteger() == 0 );
    }

    public void testUnsignedInteger1() throws IOException
    {
    	//	Bit
    	getBitEncoder().encodeUnsignedInteger( 1 );
        assertTrue(getBitDecoder().decodeUnsignedInteger() == 1 );
        //	Byte
        getByteEncoder().encodeUnsignedInteger( 1 );
        assertTrue(getByteDecoder().decodeUnsignedInteger() == 1 );
    }

    public void testUnsignedInteger2() throws IOException 
    {
    	//	Bit
    	getBitEncoder().encodeUnsignedInteger( 2 );
        assertTrue(getBitDecoder().decodeUnsignedInteger() == 2 );
        //	Byte
        getByteEncoder().encodeUnsignedInteger( 2 );
        assertTrue(getByteDecoder().decodeUnsignedInteger() == 2 );
    }
    
    public void testUnsignedInteger128() throws IOException 
    {
    	//	Bit
    	getBitEncoder().encodeUnsignedInteger( 128 );
        assertTrue(getBitDecoder().decodeUnsignedInteger() == 128 );
        //	Byte
        getByteEncoder().encodeUnsignedInteger( 128 );
        assertTrue(getByteDecoder().decodeUnsignedInteger() == 128 );
    }
    
    public void testUnsignedInteger200() throws IOException 
    {
    	//	Bit
    	getBitEncoder().encodeUnsignedInteger( 200 );
        assertTrue(getBitDecoder().decodeUnsignedInteger() == 200 );
        //	Byte
        getByteEncoder().encodeUnsignedInteger( 200 );
        assertTrue(getByteDecoder().decodeUnsignedInteger() == 200 );
    }

    public void testUnsignedInteger2000() throws IOException 
    {
    	//	Bit
    	getBitEncoder().encodeUnsignedInteger( 2000 );
        assertTrue(getBitDecoder().decodeUnsignedInteger() == 2000 );
        //	Byte
        getByteEncoder().encodeUnsignedInteger( 2000 );
        assertTrue(getByteDecoder().decodeUnsignedInteger() == 2000 );
    }
    
    public void testUnsignedInteger20000() throws IOException 
    {
    	//	Bit
    	getBitEncoder().encodeUnsignedInteger( 20000 );
        assertTrue(getBitDecoder().decodeUnsignedInteger() == 20000 );
        //	Byte
        getByteEncoder().encodeUnsignedInteger( 20000 );
        assertTrue(getByteDecoder().decodeUnsignedInteger() == 20000 );
    }

    public void testUnsignedInteger200000() throws IOException 
    {
    	//	Bit
    	getBitEncoder().encodeUnsignedInteger( 200000 );
        assertTrue(getBitDecoder().decodeUnsignedInteger() == 200000 );
        //	Byte
        getByteEncoder().encodeUnsignedInteger( 20000 );
        assertTrue(getByteDecoder().decodeUnsignedInteger() == 20000 );
    }
    
    public void testUnsignedInteger2000000() throws IOException 
    {
    	//	Bit
    	getBitEncoder().encodeUnsignedInteger( 2000000 );
        assertTrue(getBitDecoder().decodeUnsignedInteger() == 2000000 );
        //	Byte
        getByteEncoder().encodeUnsignedInteger( 20000 );
        assertTrue(getByteDecoder().decodeUnsignedInteger() == 20000 );
    }
    
    public void testUnsignedIntegerS0() throws IOException , XMLParsingException
    {
    	String s = "0";
//    	
//    	int i1 = MethodsBag.numberOfBitsToRepresent( Long.valueOf ( "0" ) );
//    	int i2 = MethodsBag.numberOfBitsToRepresent( Long.valueOf ( "2" ) );
//    	int i21230354 = MethodsBag.numberOfBitsToRepresent( Long.valueOf ( "21230354" ) );
//    	int imi = MethodsBag.numberOfBitsToRepresent( Long.valueOf ( Integer.MAX_VALUE ) );
//    	int ih = MethodsBag.numberOfBitsToRepresent( Long.valueOf ( "12678967543233" ) );
//    	
//    	
    	XSDInteger xmlInteger = XSDInteger.newInstance();
    	xmlInteger.parse ( s );
    	
    	//	Bit
    	getBitEncoder().encodeUnsignedInteger( xmlInteger );
        assertTrue(getBitDecoder().decodeUnsignedIntegerAsString().equals( s ) );
        //	Byte
        getByteEncoder().encodeUnsignedInteger( xmlInteger );
        assertTrue(getByteDecoder().decodeUnsignedIntegerAsString().equals( s ) );
    }
    
    public void testUnsignedIntegerS1() throws IOException , XMLParsingException
    {
    	String s = "1";
    	
    	XSDInteger xmlInteger = XSDInteger.newInstance();
    	xmlInteger.parse ( s );
    	
    	//	Bit
    	getBitEncoder().encodeUnsignedInteger( xmlInteger );
        assertTrue(getBitDecoder().decodeUnsignedIntegerAsString().equals( s ) );
        //	Byte
        getByteEncoder().encodeUnsignedInteger( xmlInteger );
        assertTrue(getByteDecoder().decodeUnsignedIntegerAsString().equals( s ) );
    }

    public void testUnsignedIntegerS329() throws IOException , XMLParsingException
    {
    	String s = "329";
    	
    	XSDInteger xmlInteger = XSDInteger.newInstance();
    	xmlInteger.parse ( s );
    	
    	//	Bit
    	getBitEncoder().encodeUnsignedInteger( xmlInteger );
    	String sDec = getBitDecoder().decodeUnsignedIntegerAsString();
        assertTrue( sDec.equals( s ) );
        //	Byte
        getByteEncoder().encodeUnsignedInteger( xmlInteger );
        assertTrue(getByteDecoder().decodeUnsignedIntegerAsString().equals( s ) );
    }
	
    public void testUnsignedIntegerS2147483647() throws IOException , XMLParsingException
    {
    	String s = "2147483647";
    	
    	XSDInteger xmlInteger = XSDInteger.newInstance();
    	xmlInteger.parse ( s );
    	
    	//	Bit
    	getBitEncoder().encodeUnsignedInteger( xmlInteger );
        assertTrue(getBitDecoder().decodeUnsignedIntegerAsString().equals( s ) );
        //	Byte
        getByteEncoder().encodeUnsignedInteger( xmlInteger );
        assertTrue(getByteDecoder().decodeUnsignedIntegerAsString().equals( s ) );
    }
 
    
    public void testUnsignedIntegerSBig1() throws IOException , XMLParsingException
    {
    	String s = "12678967543233";
    	
    	XSDInteger xmlInteger = XSDInteger.newInstance();
    	xmlInteger.parse ( s );
    	
    	//	Bit
    	getBitEncoder().encodeUnsignedInteger( xmlInteger );
    	String s1 = getBitDecoder().decodeUnsignedIntegerAsString();
        assertTrue( s + "!=" + s1 , s1.equals( s ) );
        //	Byte
        getByteEncoder().encodeUnsignedInteger( xmlInteger );
        assertTrue(getByteDecoder().decodeUnsignedIntegerAsString().equals( s ) );
    }
    
    public void testUnsignedIntegerSBig2() throws IOException , XMLParsingException
    {
    	BigInteger bi = BigInteger.valueOf ( Long.MAX_VALUE );
    	bi = bi.add ( BigInteger.valueOf ( 1 ) );

    	String s = bi.toString ( );
    	
    	//9223372036854775808
    	XSDInteger xmlInteger = XSDInteger.newInstance();
    	xmlInteger.parse ( s );
    	
    	//	Bit
    	getBitEncoder().encodeUnsignedInteger( xmlInteger );
    	String s1 = getBitDecoder().decodeUnsignedIntegerAsString();
        assertTrue( s + "!=" + s1 , s1.equals( s ) );
        //	Byte
        getByteEncoder().encodeUnsignedInteger( xmlInteger );
        assertTrue(getByteDecoder().decodeUnsignedIntegerAsString().equals( s ) );
    }
    
    public void testUnsignedIntegerSBig3() throws IOException , XMLParsingException
    {
    	String s = "87139166666670000000000000000001";
    	
    	XSDInteger xmlInteger = XSDInteger.newInstance();
    	xmlInteger.parse ( s );
    	
    	//	Bit
    	getBitEncoder().encodeUnsignedInteger( xmlInteger );
    	String s1 = getBitDecoder().decodeUnsignedIntegerAsString();
        assertTrue( s + "!=" + s1 , s1.equals( s ) );
        //	Byte
        getByteEncoder().encodeUnsignedInteger( xmlInteger );
        assertTrue(getByteDecoder().decodeUnsignedIntegerAsString().equals( s ) );
    }
    
    public void testUnsignedIntegerSBig4() throws IOException , XMLParsingException
    {
    	String s = "0008713916666667000000000000000000";
    	String s2 =   "8713916666667000000000000000000";
    	
    	XSDInteger xmlInteger = XSDInteger.newInstance();
    	xmlInteger.parse ( s );
    	
    	//	Bit
    	getBitEncoder().encodeUnsignedInteger( xmlInteger );
    	String s1 = getBitDecoder().decodeUnsignedIntegerAsString();
        assertTrue( s2 + "!=" + s1 , s1.equals( s2 ) );
        //	Byte
        getByteEncoder().encodeUnsignedInteger( xmlInteger );
        assertTrue(getByteDecoder().decodeUnsignedIntegerAsString().equals( s2 ) );
    }
    
    
    
    
    public void testUnsignedIntegerSFailure() throws IOException , XMLParsingException
    {
    	String s = "-123";

    	XSDInteger xmlInteger = XSDInteger.newInstance();
    	xmlInteger.parse ( s );
    	
    	try {
			//	Bit
			getBitEncoder().encodeUnsignedInteger( xmlInteger );
			fail( "Negative values accepted" );
		} catch (RuntimeException e) {
			//	ok
		}
    }
    
    public void testUnsignedIntegerSequence() throws IOException 
    {
    	//	Bit / Byte
    	EncoderChannel ecBit = getBitEncoder();
    	EncoderChannel ecByte = getByteEncoder();
        for (int i = 0; i < 100000; i++)
        {
            ecBit.encodeUnsignedInteger( i );
            ecByte.encodeUnsignedInteger( i );
        }

        DecoderChannel dcBit = getBitDecoder();
        DecoderChannel dcByte = getByteDecoder();
        for (int i = 0; i < 100000; i++)
        {
            assertEquals(dcBit.decodeUnsignedInteger(), i);
            assertEquals(dcByte.decodeUnsignedInteger(), i);
        }
    }

}