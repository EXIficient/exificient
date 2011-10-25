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

import javax.xml.namespace.QName;

import com.siemens.ct.exi.datatype.strings.StringDecoder;
import com.siemens.ct.exi.datatype.strings.StringDecoderImpl;
import com.siemens.ct.exi.datatype.strings.StringEncoder;
import com.siemens.ct.exi.datatype.strings.StringEncoderImpl;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.types.BuiltInType;
import com.siemens.ct.exi.types.DatatypeMappingTest;
import com.siemens.ct.exi.types.IntegerType;
import com.siemens.ct.exi.values.IntegerValue;
import com.siemens.ct.exi.values.StringValue;
import com.siemens.ct.exi.values.Value;

public class ListTest extends AbstractTestCase {

	public ListTest(String testName) {
		super(testName);
	}

	public void testListInteger1() throws IOException {
		StringValue s = new StringValue("100 34 56 -23 1567");
		ListDatatype ldtInteger = new ListDatatype(new IntegerDatatype(IntegerType.INTEGER_32, null), null);

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
	
	public void testListIntegerLexical1() throws IOException {
		StringValue s = new StringValue("100 34 56 -23 1567");
		ListDatatype ldtInteger = new ListDatatype(new IntegerDatatype(IntegerType.INTEGER_32, null), null);

		boolean valid = ldtInteger.isValidRCS(s);
		assertTrue(valid);

		StringEncoder stringEncoder = new StringEncoderImpl();
		StringDecoder stringDecoder = new StringDecoderImpl();
		QName context = new QName("", "intList"); 
		RestrictedCharacterSetDatatype rcsDatatype = new RestrictedCharacterSetDatatype(null);
		
		// Bit
		EncoderChannel bitEC = getBitEncoder();
		ldtInteger.writeValueRCS(rcsDatatype, bitEC, stringEncoder, context);
		bitEC.flush();
		Value v1 = ldtInteger.readValueRCS(rcsDatatype, getBitDecoder(), stringDecoder, context);
		assertTrue(s.equals(v1.toString()));

		// Byte
		EncoderChannel byteEC = getByteEncoder();
		ldtInteger.writeValueRCS(rcsDatatype, byteEC, stringEncoder, context);
		Value v2 = ldtInteger.readValueRCS(rcsDatatype, getByteDecoder(), stringDecoder, context);
		assertTrue(s.equals(v2.toString()));
	}
	
	// encodes special chars as well
	public void testListIntegerLexical2() throws IOException {
		char special = '\u03D7';
		StringValue s = new StringValue("100" + special);
		ListDatatype ldtInteger = new ListDatatype(new IntegerDatatype(IntegerType.INTEGER_32, null), null);

		boolean valid = ldtInteger.isValidRCS(s);
		assertTrue(valid);

		StringEncoder stringEncoder = new StringEncoderImpl();
		StringDecoder stringDecoder = new StringDecoderImpl();
		QName context = new QName("", "intList"); 
		RestrictedCharacterSetDatatype rcsDatatype = new RestrictedCharacterSetDatatype(null);
		
		// Bit
		EncoderChannel bitEC = getBitEncoder();
		ldtInteger.writeValueRCS(rcsDatatype, bitEC, stringEncoder, context);
		bitEC.flush();
		Value v1 = ldtInteger.readValueRCS(rcsDatatype, getBitDecoder(), stringDecoder, context);
		assertTrue(s.equals(v1.toString()));

		// Byte
		EncoderChannel byteEC = getByteEncoder();
		ldtInteger.writeValueRCS(rcsDatatype, byteEC, stringEncoder, context);
		Value v2 = ldtInteger.readValueRCS(rcsDatatype, getByteDecoder(), stringDecoder, context);
		assertTrue(s.equals(v2.toString()));
	}
	

	public void testListNBit1() throws IOException {
		StringValue s = new StringValue("+1 0 127 -127");
		String sRes = "1 0 127 -127";
		IntegerValue min = IntegerValue.valueOf(-128);
		IntegerValue max = IntegerValue.valueOf(127);
		ListDatatype ldtInteger = new ListDatatype(new NBitUnsignedIntegerDatatype(IntegerType.INTEGER_32, min, max, null), null);

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

	public void testListGMonthDayUnion1() throws IOException, EXIException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:simpleType name='gMonthDay'>"
				+ "  <xs:restriction base='xs:gMonthDay'>"
				+ "   <xs:enumeration value='--01-01'/>"
				+ "   <xs:enumeration value='--05-01'/>"
				+ "   <xs:enumeration value='--05-08'/>"
				+ "   <xs:enumeration value='--07-14'/>"
				+ "   <xs:enumeration value='--08-15'/>"
				+ "   <xs:enumeration value='--11-01'/>"
				+ "   <xs:enumeration value='--11-11'/>"
				+ "   <xs:enumeration value='--12-25'/>"
				+ "  </xs:restriction>"
				+ " </xs:simpleType>"
				+ ""
				+ "  <xs:simpleType name='List'>"
				+ "    <xs:list itemType='gMonthDay'/>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"List", "");

		assertTrue(dt.getBuiltInType() == BuiltInType.LIST);
		// EnumerationDatatype enumDt = (EnumerationDatatype) dt;

		assertTrue(dt.isValid(new StringValue("  --12-25  --08-15  --01-01  --07-14   ")));

		assertFalse(dt.isValid(new StringValue("00")));
	}
	
	public void testListFloat1() throws IOException, EXIException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='List'>"
				+ "    <xs:list itemType='xs:float'/>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"List", "");

		assertTrue(dt.getBuiltInType() == BuiltInType.LIST);
		// EnumerationDatatype enumDt = (EnumerationDatatype) dt;

		assertTrue(dt.isValid(new StringValue("  1e4 -10000 5.234e-2   ")));

		assertFalse(dt.isValid(new StringValue("bla")));
	}
	
	public void testListFloat2() throws IOException, EXIException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='List'>"
				+ "    <xs:list itemType='xs:float'/>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"List", "");

		assertTrue(dt.getBuiltInType() == BuiltInType.LIST);
		// EnumerationDatatype enumDt = (EnumerationDatatype) dt;

		assertTrue(dt.isValid(new StringValue("  1e4 -10000 5.234e-2 \n 11.22 \t\t 4 \r\n999  ")));

		assertFalse(dt.isValid(new StringValue("bla")));
	}

}