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

import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.util.datatype.XSDBase64;

public class BinaryTest extends AbstractTestCase
{

	public BinaryTest ( String testName )
	{
		super ( testName );
	}

	public void testHexBinaryAsString0FB7 () throws IOException
	{
		try
		{
			String src = "0FB7";

			XSDBase64 b64 = XSDBase64.newInstance ( );
			b64.parse ( src );

			// Bit
			EncoderChannel bitEC = getBitEncoder ( );
			bitEC.encodeBinary ( b64 );
			bitEC.flush ( );
			String d1 = getBitDecoder ( ).decodeBinaryAsString ( );
			assertTrue ( d1.equals ( src ) );
			// Byte
			getByteEncoder ( ).encodeBinary ( b64 );
			assertTrue ( getByteDecoder ( ).decodeBinaryAsString ( ).equals ( src ) );
		}
		catch ( Exception e )
		{
			fail ( );
		}

	}

	public void testBase64BinaryAsString0 () throws IOException
	{
		try
		{
			String src = "ZHM=";

			XSDBase64 b64 = XSDBase64.newInstance ( );
			b64.parse ( src );

			// Bit
			EncoderChannel bitEC = getBitEncoder ( );
			bitEC.encodeBinary ( b64 );
			bitEC.flush ( );
			assertTrue ( getBitDecoder ( ).decodeBinaryAsString ( ).equals ( src ) );
			// Byte
			getByteEncoder ( ).encodeBinary ( b64 );
			assertTrue ( getByteDecoder ( ).decodeBinaryAsString ( ).equals ( src ) );
		}
		catch ( Exception e )
		{
			fail ( );
		}
	}

	public void testBase64BinaryAsString1 () throws IOException
	{
		try
		{
			String src = "RGFzIGlzIGphIGVpbiBmZXN0ZXIgQmxlZHNpbm4sIHdlaWwgVW1sYXV0ZSB3aWUg9iB1bmQg/CBtYWNoZW4gU2lubiwgd2llIGF1Y2ggZWluIHNjaGFyZmVzIN8u";
			// String src = "R0lGODlhAgSzzs7O3t7e";

			XSDBase64 b64 = XSDBase64.newInstance ( );
			b64.parse ( src );

			// Bit
			EncoderChannel bitEC = getBitEncoder ( );
			bitEC.encodeBinary ( b64 );
			bitEC.flush ( );
			String d1 = getBitDecoder ( ).decodeBinaryAsString ( );
			assertTrue ( d1.equals ( src ) );
			// Byte
			getByteEncoder ( ).encodeBinary ( b64 );
			assertTrue ( getByteDecoder ( ).decodeBinaryAsString ( ).equals ( src ) );
		}
		catch ( Exception e )
		{
			fail ( );
		}
	}

	public void testBase64BinaryAsString2 () throws IOException
	{
		try
		{
			String src = "SMOkdHRlbiBIw7x0ZSBlaW4gw58gaW0gTmFtZW4sIHfDpHJlbiBzaWUgbcO2Z2xpY2hlcndlaXNlIGtlaW5lIEjDvHRlIG1laHIsDQpzb25kZXJuIEjDvMOfZS4NCg==";

			XSDBase64 b64 = XSDBase64.newInstance ( );
			b64.parse ( src );

			// Bit
			EncoderChannel bitEC = getBitEncoder ( );
			bitEC.encodeBinary ( b64 );
			bitEC.flush ( );
			assertTrue ( getBitDecoder ( ).decodeBinaryAsString ( ).equals ( src ) );
			// Byte
			getByteEncoder ( ).encodeBinary ( b64 );
			assertTrue ( getByteDecoder ( ).decodeBinaryAsString ( ).equals ( src ) );
		}
		catch ( Exception e )
		{
			fail ( );
		}
	}

	public void testBase64BinarySpaces1 () throws IOException
	{
		try
		{
			String s1 = "ZH";	
			String s2 = "M=";
			String src = s1 + "  " + s2 + "\n";
			

			XSDBase64 b64 = XSDBase64.newInstance ( );
			b64.parse ( src );

			// Bit
			EncoderChannel bitEC = getBitEncoder ( );
			bitEC.encodeBinary ( b64 );
			bitEC.flush ( );
			String d1 = getBitDecoder ( ).decodeBinaryAsString ( );
			assertTrue ( d1.equals ( s1 + s2 ) );
			// Byte
			getByteEncoder ( ).encodeBinary ( b64 );
			assertTrue ( getByteDecoder ( ).decodeBinaryAsString ( ).equals ( s1 + s2 ) );
		}
		catch ( Exception e )
		{
			fail ( );
		}
	}
	
	public void testBase64BinarySpaces2 () throws IOException
	{
		try
		{
			String s1 = "R0lGODdhWAK+ov////v7++fn58DAwI6Ojl5eXjExMQMDAyxYAr5AA/8Iutz+MMpJq7046827/2Ao";
			String sE = "\n \n ";	
			String s2 = "jmRpnmiqPsKxvvBqCIxgxHg=";
			String src = s1 + sE + s2;
			

			XSDBase64 b64 = XSDBase64.newInstance ( );
			b64.parse ( src );

			// Bit
			EncoderChannel bitEC = getBitEncoder ( );
			bitEC.encodeBinary ( b64 );
			bitEC.flush ( );
			String d1 = getBitDecoder ( ).decodeBinaryAsString ( );
			assertTrue ( d1.equals ( s1 + s2 ) );
			// Byte
			getByteEncoder ( ).encodeBinary ( b64 );
			assertTrue ( getByteDecoder ( ).decodeBinaryAsString ( ).equals ( s1 + s2 ) );
		}
		catch ( Exception e )
		{
			fail ( );
		}
	}

	public void testBinaryFailure () throws IOException
	{
		try
		{
			String src = "*invalid-bit*";

			XSDBase64 b64 = XSDBase64.newInstance ( );
			b64.parse ( src );

			fail ( );

		}
		catch ( Exception e )
		{
			// ok
		}
	}

}