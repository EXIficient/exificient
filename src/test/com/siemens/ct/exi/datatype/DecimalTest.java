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
import java.math.BigDecimal;

import com.siemens.ct.exi.exceptions.XMLParsingException;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.util.datatype.XSDDecimal;

public class DecimalTest extends AbstractTestCase
{
	private XSDDecimal	decimal = XSDDecimal.newInstance ( );

    public DecimalTest(String testName)
    {
        super(testName);
    }

    
    public void testDecimal0() throws IOException, XMLParsingException
    {
    	String s = "-1.23";
    	decimal.parse ( s );
    	
    	//	Bit
    	EncoderChannel bitEC = getBitEncoder();
    	bitEC.encodeDecimal( decimal );
    	bitEC.flush();
    	assertTrue(getBitDecoder().decodeDecimal().equals( new BigDecimal( s ) ) );
        //	Byte
        getByteEncoder().encodeDecimal( decimal );
        assertTrue(getByteDecoder().decodeDecimal().equals( new BigDecimal( s ) ) );
    }
    
    public void testDecimal1() throws IOException, XMLParsingException
    {
    	String s = "12678967.543233";
    	decimal.parse ( s );
    	
    	//	Bit
    	EncoderChannel bitEC = getBitEncoder();
    	bitEC.encodeDecimal( decimal );
    	bitEC.flush();
    	BigDecimal bdBit = getBitDecoder().decodeDecimal();
    	assertTrue( bdBit + "!=" + new BigDecimal( s ), bdBit.equals( new BigDecimal( s ) ) );
        //	Byte
        getByteEncoder().encodeDecimal( decimal );
        assertTrue(getByteDecoder().decodeDecimal().equals( new BigDecimal( s ) ) );
    }
    
    public void testDecimal2() throws IOException, XMLParsingException
    {
    	String s = "+100000.0012";
    	decimal.parse ( s );
    	
    	//	Bit
    	EncoderChannel bitEC = getBitEncoder();
    	bitEC.encodeDecimal( decimal );
    	bitEC.flush();
    	assertTrue( getBitDecoder().decodeDecimal().equals( new BigDecimal( s ) ) );
        //	Byte
        getByteEncoder().encodeDecimal( decimal );
        assertTrue(getByteDecoder().decodeDecimal().equals( new BigDecimal( s ) ) );
    }
    
    public void testDecimal3() throws IOException, XMLParsingException
    {
    	String s = "210";
    	decimal.parse ( s );
    	
    	//	Bit
    	EncoderChannel bitEC = getBitEncoder();
    	bitEC.encodeDecimal( decimal );
    	bitEC.flush();
    	BigDecimal bdBit = getBitDecoder().decodeDecimal();
    	assertTrue( bdBit + "!=" + new BigDecimal( s + ".0"  ), bdBit.equals( new BigDecimal( s + ".0" ) ) );
        //	Byte
        getByteEncoder().encodeDecimal( decimal );
        assertTrue( getByteDecoder().decodeDecimal().equals( new BigDecimal( s + ".0" ) ) );
    }
    
    public void testDecimal4() throws IOException, XMLParsingException
    {
    	String s = "380";
    	decimal.parse ( s );
    	
    	//	Bit
    	EncoderChannel bitEC = getBitEncoder();
    	bitEC.encodeDecimal( decimal );
    	bitEC.flush();
    	assertTrue( getBitDecoder().decodeDecimalAsString().equals( s + ".0" ) );
        //	Byte
        getByteEncoder().encodeDecimal( decimal );
        assertTrue( getByteDecoder().decodeDecimalAsString().equals( s + ".0" ) );
    }

    public void testDecimal5() throws IOException, XMLParsingException
    {
    	String s = "0.001359";
    	decimal.parse ( s );
    	
    	//	Bit
    	EncoderChannel bitEC = getBitEncoder();
    	bitEC.encodeDecimal( decimal );
    	bitEC.flush();
    	assertTrue( getBitDecoder().decodeDecimalAsString().equals( s ) );
        //	Byte
        getByteEncoder().encodeDecimal( decimal );
        assertTrue( getByteDecoder().decodeDecimalAsString().equals( s ) );
    }
    
    public void testDecimal6() throws IOException, XMLParsingException
    {
    	String s = "110.74080";
    	decimal.parse ( s );
    	
    	//	Bit
    	EncoderChannel bitEC = getBitEncoder();
    	bitEC.encodeDecimal( decimal );
    	bitEC.flush();
    	String sBit = getBitDecoder().decodeDecimalAsString();
    	assertTrue( sBit + "!=" + "110.7408", sBit.equals( "110.7408" ) );
        //	Byte
        getByteEncoder().encodeDecimal( decimal );
        assertTrue( getByteDecoder().decodeDecimalAsString().equals( "110.7408" ) );
    }

    public void testDecimal7() throws IOException, XMLParsingException
    {
    	String s = "55000.0";
    	decimal.parse ( s );
    	
    	//	Bit
    	EncoderChannel bitEC = getBitEncoder();
    	bitEC.encodeDecimal( decimal );
    	bitEC.flush();
    	DecoderChannel bitDC = getBitDecoder();
    	assertTrue( bitDC.decodeDecimalAsString().equals( s ) );
        //	Byte
    	EncoderChannel byteEC = getByteEncoder();
    	byteEC.encodeDecimal( decimal );
        DecoderChannel byteDC = getByteDecoder();
        assertTrue( byteDC.decodeDecimalAsString().equals( s ) );
    }
    
//    public void testDecimal8() throws IOException
//    {
//    	String s1 = "55000.0";
//    	String s2 = "44000.0";
//    	
//    	//	Bit
//    	EncoderChannel bitEC = getBitEncoder();
//    	bitEC.encodeDecimal( s1 );
//    	bitEC.encodeDecimal( s2 );
//    	bitEC.flush();
//    	DecoderChannel bitDC = getBitDecoder();
//    	assertTrue( bitDC.decodeDecimalAsString().equals( s1 ) );
//    	assertTrue( bitDC.decodeDecimalAsString().equals( s2 ) );
//        //	Byte
//    	
//    	EncoderChannel byteEC = getByteEncoder();
//    	byteEC.encodeDecimal( s1 );
//    	byteEC.encodeDecimal( s2 );
//    	ByteDecoderChannel  byteDC = (ByteDecoderChannel)getByteDecoder();
//    	SkippableDecoderChannel sdc = new SkippableByteDecoderChannel( byteDC.getInputStream ( ) );
//        
//        assertTrue( sdc.decodeDecimalAsString().equals( s1 ) );
//        assertTrue( sdc.decodeDecimalAsString().equals( s2 ) );
//    }
    
    	
    public void testDecimal9() throws IOException, XMLParsingException
    {
    	String s = "3.141592653589";
    	decimal.parse ( s );
    	
    	//	Bit
    	EncoderChannel bitEC = getBitEncoder();
    	bitEC.encodeDecimal( decimal );
    	bitEC.flush();
    	String sBit = getBitDecoder().decodeDecimalAsString();
    	assertTrue( sBit + "!=" + s, sBit.equals( s ) );
        //	Byte
        getByteEncoder().encodeDecimal( decimal );
        assertTrue( getByteDecoder().decodeDecimalAsString().equals( s ) );
    }
    
    public void testDecimalBig1() throws IOException, XMLParsingException
    {
    	String s = "36.087139166666670000000000000000001";
    	decimal.parse ( s );
    	
    	//	Bit
    	EncoderChannel bitEC = getBitEncoder();
    	bitEC.encodeDecimal( decimal );
    	bitEC.flush();
    	String sBit = getBitDecoder().decodeDecimalAsString();
    	assertTrue( sBit + "!=" + s, sBit.equals( s ) );
        //	Byte
        getByteEncoder().encodeDecimal( decimal );
        assertTrue( getByteDecoder().decodeDecimalAsString().equals( s ) );
    }

}