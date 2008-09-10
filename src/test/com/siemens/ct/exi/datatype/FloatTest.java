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

import com.siemens.ct.exi.exceptions.XMLParsingException;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.util.datatype.XSDFloat;

public class FloatTest extends AbstractTestCase
{
	XSDFloat fl = XSDFloat.newInstance();

    public FloatTest(String testName)
    {
        super(testName);
    }

    public void testFloatNaN() throws IOException, XMLParsingException
    {
    	String s = "NaN";
    	fl.parse ( s );
    	
    	//	Bit
    	EncoderChannel bitEC = getBitEncoder();
    	bitEC.encodeFloat( fl );
    	bitEC.flush();
        assertTrue(getBitDecoder().decodeFloatAsString().equals( s ) );
        //	Byte
        getByteEncoder().encodeFloat( fl );
        assertTrue(getByteDecoder().decodeFloatAsString().equals( s ) );
    }

    public void testFloatINF() throws IOException, XMLParsingException
    {
    	String s = "INF";
    	fl.parse ( s );
    	
    	//	Bit
    	EncoderChannel bitEC = getBitEncoder();
    	bitEC.encodeFloat( fl );
    	bitEC.flush();
        assertTrue(getBitDecoder().decodeFloatAsString().equals( s ) );
        //	Byte
        getByteEncoder().encodeFloat( fl );
        assertTrue(getByteDecoder().decodeFloatAsString().equals( s ) );
    }
    
    public void testFloatMINF() throws IOException, XMLParsingException
    {
    	String s = "-INF";
    	fl.parse ( s );
    	
    	//	Bit
    	EncoderChannel bitEC = getBitEncoder();
    	bitEC.encodeFloat( fl );
    	bitEC.flush();
        assertTrue(getBitDecoder().decodeFloatAsString().equals( s ) );
        //	Byte
        getByteEncoder().encodeFloat( fl );
        assertTrue(getByteDecoder().decodeFloatAsString().equals( s ) );
    }

    //	, , ,  , 
//    public void testFloat0() throws IOException
//    {
//    	String s = "-1E4";
//    	
//    	//	Bit
//    	EncoderChannel bitEC = getBitEncoder();
//    	bitEC.encodeFloat( s );
//    	bitEC.flush();
//        assertTrue(getBitDecoder().decodeFloatAsString().equals( s ) );
//        //	Byte
//        getByteEncoder().encodeFloat( s );
//        assertTrue(getByteDecoder().decodeFloatAsString().equals( s ) );
//    }
    
    public void testFloat1() throws IOException, XMLParsingException
    {
    	String s = "1267.43233E12";
    	fl.parse ( s );
    	
    	//	Bit
    	EncoderChannel bitEC = getBitEncoder();
    	bitEC.encodeFloat( fl );
    	bitEC.flush();
        assertTrue(getBitDecoder().decodeFloatAsString().equals( "126743233E7" ) );
        //	Byte
        getByteEncoder().encodeFloat( fl );
        assertTrue(getByteDecoder().decodeFloatAsString().equals( "126743233E7" ) );
    }

    
    public void testFloat2() throws IOException, XMLParsingException
    {
    	String s = "12.78e-2";
    	fl.parse ( s );
    	
    	//	Bit
    	EncoderChannel bitEC = getBitEncoder();
    	bitEC.encodeFloat( fl );
    	bitEC.flush();
        assertTrue(getBitDecoder().decodeFloatAsString().equals( "1278E-4" ) );
        //	Byte
        getByteEncoder().encodeFloat( fl );
        assertTrue(getByteDecoder().decodeFloatAsString().equals( "1278E-4" ) );
    }
    
    
    public void testFloat3() throws IOException, XMLParsingException
    {
    	String s = "12";
    	fl.parse ( s );
    	
    	//	Bit
    	EncoderChannel bitEC = getBitEncoder();
    	bitEC.encodeFloat( fl );
    	bitEC.flush();
        assertTrue(getBitDecoder().decodeFloatAsString().equals( "12E0" ) );
        //	Byte
        getByteEncoder().encodeFloat( fl );
        assertTrue(getByteDecoder().decodeFloatAsString().equals( "12E0" ) );
    }
    
    public void testFloat4() throws IOException, XMLParsingException
    {
    	String s = "0";
    	fl.parse ( s );
    	
    	//	Bit
    	EncoderChannel bitEC = getBitEncoder();
    	bitEC.encodeFloat( fl );
    	bitEC.flush();
        assertTrue(getBitDecoder().decodeFloatAsString().equals( "0E0" ) );
        //	Byte
        getByteEncoder().encodeFloat( fl );
        assertTrue(getByteDecoder().decodeFloatAsString().equals( "0E0" ) );
    }
    
    public void testFloat5() throws IOException, XMLParsingException
    {
    	String s = "-0";
    	fl.parse ( s );
    	
    	//	Bit
    	EncoderChannel bitEC = getBitEncoder();
    	bitEC.encodeFloat( fl );
    	bitEC.flush();
        assertTrue(getBitDecoder().decodeFloatAsString().equals( "0E0" ) );
        //	Byte
        getByteEncoder().encodeFloat( fl );
        assertTrue(getByteDecoder().decodeFloatAsString().equals( "0E0" ) );
    }
    
    public void testFloat6() throws IOException, XMLParsingException
    {
    	String s = "-1";
    	fl.parse ( s );
    	
    	//	Bit
    	EncoderChannel bitEC = getBitEncoder();
    	bitEC.encodeFloat( fl );
    	bitEC.flush();
        assertTrue(getBitDecoder().decodeFloatAsString().equals( "-1E0" ) );
        //	Byte
        getByteEncoder().encodeFloat( fl );
        assertTrue(getByteDecoder().decodeFloatAsString().equals( "-1E0" ) );
    }
    
    public void testFloat7() throws IOException, XMLParsingException
    {
    	String s = "119.9999999999999929";
    	fl.parse ( s );
    	
    	//	Bit
    	EncoderChannel bitEC = getBitEncoder();
    	bitEC.encodeFloat( fl );
    	bitEC.flush();
        assertTrue(getBitDecoder().decodeFloatAsString().equals( "1199999999999999929E-16" ) );
        //	Byte
        getByteEncoder().encodeFloat( fl );
        assertTrue(getByteDecoder().decodeFloatAsString().equals( "1199999999999999929E-16" ) );
    }
    
}