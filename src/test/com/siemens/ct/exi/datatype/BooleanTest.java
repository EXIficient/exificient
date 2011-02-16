/*
 * Copyright (C) 2007-2011 Siemens AG
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

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.values.BooleanValue;
import com.siemens.ct.exi.values.Value;

public class BooleanTest extends AbstractTestCase {
	
	public BooleanTest(String testName) {
		super(testName);
	}

	public void testBoolean0() throws IOException {
		String s = "0";
		
		Datatype bool = new BooleanDatatype(null);
		boolean valid =  bool.isValid(s);
		assertTrue(valid);
		
		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bool.writeValue(bitEC, null, null);
		bitEC.flush();
		Value val1 = bool.readValue(getBitDecoder(), null, null);
		assertTrue(Constants.DECODED_BOOLEAN_FALSE.equals(val1.toString()));
		// Byte
		bool.writeValue(getByteEncoder(), null, null);
		Value val2 = bool.readValue(getBitDecoder(), null, null);
		assertTrue(Constants.DECODED_BOOLEAN_FALSE.equals(val2.toString()));
	}
	
	public void testBoolean1() throws IOException {
		String s = "1";
		
		Datatype bool = new BooleanDatatype(null);
		boolean valid =  bool.isValid(s);
		assertTrue(valid);
		
		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bool.writeValue(bitEC, null, null);
		bitEC.flush();
		Value val1 = bool.readValue(getBitDecoder(), null, null);
		assertTrue(Constants.DECODED_BOOLEAN_TRUE.equals(val1.toString()));
		// Byte
		bool.writeValue(getByteEncoder(), null, null);
		Value val2 = bool.readValue(getBitDecoder(), null, null);
		assertTrue(Constants.DECODED_BOOLEAN_TRUE.equals(val2.toString()));
	}
	
	public void testBooleanFalse() throws IOException {
		String s = "false";
		boolean v = false;
		
		Datatype bool = new BooleanDatatype(null);
		boolean valid =  bool.isValid(s);
		assertTrue(valid);
		
		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bool.writeValue(bitEC, null, null);
		bitEC.flush();
		BooleanValue val1 = (BooleanValue) bool.readValue(getBitDecoder(), null, null);
		assertTrue(v == val1.toBoolean());
		// Byte
		bool.writeValue(getByteEncoder(), null, null);
		BooleanValue val2 = (BooleanValue) bool.readValue(getBitDecoder(), null, null);
		assertTrue(v == val2.toBoolean() );
	}
	
	public void testBooleanTrue() throws IOException {
		String s = "true";
		boolean v = true;
		
		Datatype bool = new BooleanDatatype(null);
		boolean valid =  bool.isValid(s);
		assertTrue(valid);
		
		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bool.writeValue(bitEC, null, null);
		bitEC.flush();
		BooleanValue val1 = (BooleanValue) bool.readValue(getBitDecoder(), null, null);
		assertTrue(v == val1.toBoolean());
		// Byte
		bool.writeValue(getByteEncoder(), null, null);
		BooleanValue val2 = (BooleanValue) bool.readValue(getBitDecoder(), null, null);
		assertTrue(v == val2.toBoolean());
	}
	
	public void testBooleanFalsePatternFalse() throws IOException {
		String s = "false";
		
		Datatype bool = new BooleanPatternDatatype(null);
		boolean valid =  bool.isValid(s);
		assertTrue(valid);
		
		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bool.writeValue(bitEC, null, null);
		bitEC.flush();
		Value val1 = bool.readValue(getBitDecoder(), null, null);
		assertTrue(s.equals(val1.toString()));
		// Byte
		bool.writeValue(getByteEncoder(), null, null);
		Value val2 = bool.readValue(getByteDecoder(), null, null);
		assertTrue(s.equals(val2.toString()));
	}
	
	public void testBooleanTruePatternFalse() throws IOException {
		String s = "true";
		
		Datatype bool = new BooleanPatternDatatype(null);
		boolean valid =  bool.isValid(s);
		assertTrue(valid);
		
		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bool.writeValue(bitEC, null, null);
		bitEC.flush();
		Value val1 = bool.readValue(getBitDecoder(), null, null);
		assertTrue(s.equals(val1.toString()));
		// Byte
		bool.writeValue(getByteEncoder(), null, null);
		Value val2 = bool.readValue(getByteDecoder(), null, null);
		assertTrue(s.equals(val2.toString()));
	}
	
	public void testBooleanFailure1() throws IOException {
		String s = "00";
		
		Datatype bool = new BooleanDatatype(null);
		boolean valid =  bool.isValid(s);
		assertFalse(valid);
	}
	
	public void testBooleanFailure2() throws IOException {
		String s = "fAlse";
		
		Datatype bool = new BooleanDatatype(null);
		boolean valid =  bool.isValid(s);
		assertFalse(valid);
	}

}