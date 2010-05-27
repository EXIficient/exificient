/*
 * Copyright (C) 2007-2010 Siemens AG
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
import com.siemens.ct.exi.values.Value;

public class ListTest extends AbstractTestCase {
	
	public ListTest(String testName) {
		super(testName);
	}

	public void testListInteger1() throws IOException {
		String s = "100 34 56 -23 1567";
		ListDatatype ldtInteger = new ListDatatype(new IntegerDatatype(null));
		
		boolean valid = ldtInteger.isValid(s);
		assertTrue(valid);
		
		// Bit
		EncoderChannel bitEC = getBitEncoder();
		ldtInteger.writeValue(bitEC, null, null);
		bitEC.flush();
		Value v1 = ldtInteger.readValue(getBitDecoder(), null, null);
		assertTrue(s.equals(v1.toString()));
		
		// Byte
		EncoderChannel byteEC = getByteEncoder();
		ldtInteger.writeValue(byteEC, null, null);
		Value v2 = ldtInteger.readValue(getByteDecoder(), null, null);
		assertTrue(s.equals(v2.toString()));
	}
	
	public void testListNBit1() throws IOException {
		String s = "+1 0 127 -127";
		String sRes = "1 0 127 -127";
		ListDatatype ldtInteger = new ListDatatype(new NBitIntegerDatatype(null, -128, 127));
		
		boolean valid = ldtInteger.isValid(s);
		assertTrue(valid);
		
		// Bit
		EncoderChannel bitEC = getBitEncoder();
		ldtInteger.writeValue(bitEC, null, null);
		bitEC.flush();
		Value v1 = ldtInteger.readValue(getBitDecoder(), null, null);
		assertTrue(sRes.equals(v1.toString()));
		
		// Byte
		EncoderChannel byteEC = getByteEncoder();
		ldtInteger.writeValue(byteEC, null, null);
		Value v2 = ldtInteger.readValue(getByteDecoder(), null, null);
		assertTrue(sRes.equals(v2.toString()));
	}


}