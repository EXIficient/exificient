/*
 * Copyright (C) 2007-2012 Siemens AG
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

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.datatype.strings.StringDecoder;
import com.siemens.ct.exi.datatype.strings.StringEncoder;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.types.BuiltInType;
import com.siemens.ct.exi.types.DatatypeMappingTest;
import com.siemens.ct.exi.values.IntegerValue;
import com.siemens.ct.exi.values.ListValue;
import com.siemens.ct.exi.values.StringValue;
import com.siemens.ct.exi.values.Value;
import com.siemens.ct.exi.values.ValueType;

public class ListTest extends AbstractTestCase {

	public ListTest(String testName) {
		super(testName);
	}
	
	public void testListInteger1() throws IOException {
		StringValue s = new StringValue("100 34 56 -23 1567");
		ListDatatype ldtInteger = new ListDatatype(new IntegerDatatype(null), null);

		boolean valid = ldtInteger.isValid(s);
		assertTrue(valid);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		ldtInteger.writeValue(null, bitEC, null);
		bitEC.flush();
		Value v1 = ldtInteger.readValue(null, getBitDecoder(), null);
		assertTrue(v1.getValueType() == ValueType.LIST);
		ListValue lv1 = (ListValue) v1;
		assertTrue(s.equals(lv1.toString()));

		// Byte
		EncoderChannel byteEC = getByteEncoder();
		ldtInteger.writeValue(null, byteEC, null);
		Value v2 = ldtInteger.readValue(null, getByteDecoder(), null);
		assertTrue(v2.getValueType() == ValueType.LIST);
		ListValue lv2 = (ListValue) v2;
		assertTrue(s.equals(lv2.toString()));
	}
	
	public void testListIntegerLexical1() throws IOException, EXIException {
		StringValue s = new StringValue("100 34 56 -23 1567");
		ListDatatype ldtInteger = new ListDatatype(new IntegerDatatype(null), null);

		boolean valid = ldtInteger.isValidRCS(s);
		assertTrue(valid);

		EXIFactory exiFactory = DefaultEXIFactory.newInstance();
		exiFactory.setFidelityOptions(FidelityOptions.createAll());
//		GrammarContext grammarContext = exiFactory.getGrammars().getGrammarContext();
		
		StringEncoder stringEncoder = exiFactory.createStringEncoder(); //  new StringEncoderImpl();
		// EncoderContext encoderContext = new EncoderContextImpl(grammarContext, stringEncoder);
		
		StringDecoder stringDecoder = exiFactory.createStringDecoder(); // new StringDecoderImpl();
		// DecoderContext decoderContext = new DecoderContextImpl(grammarContext, stringDecoder);
		
//		StringEncoder stringEncoder = new StringEncoderImpl();
//		StringDecoder stringDecoder = new StringDecoderImpl();
		QName context = new QName("", "intList"); 
		// EvolvingUriContext uc = new RuntimeEvolvingUriContext(0, "");
		QNameContext qncContext = new QNameContext(0, 0, context, 0);
		
		RestrictedCharacterSetDatatype rcsDatatype = new RestrictedCharacterSetDatatype(null);
		
		// Bit
		EncoderChannel bitEC = getBitEncoder();
		ldtInteger.writeValueRCS(rcsDatatype, qncContext, bitEC, stringEncoder);
		bitEC.flush();
		Value v1 = ldtInteger.readValueRCS(rcsDatatype, qncContext, getBitDecoder(), stringDecoder);
		assertTrue(s.equals(v1.toString()));

		// Byte
		EncoderChannel byteEC = getByteEncoder();
		ldtInteger.writeValueRCS(rcsDatatype, qncContext, byteEC, stringEncoder);
		Value v2 = ldtInteger.readValueRCS(rcsDatatype, qncContext,  getByteDecoder(), stringDecoder);
		assertTrue(s.equals(v2.toString()));
	}
	
	// encodes special chars as well
	public void testListIntegerLexical2() throws IOException, EXIException {
		char special = '\u03D7';
		StringValue s = new StringValue("100" + special);
		ListDatatype ldtInteger = new ListDatatype(new IntegerDatatype(null), null);

		boolean valid = ldtInteger.isValidRCS(s);
		assertTrue(valid);
		
		EXIFactory exiFactory = DefaultEXIFactory.newInstance();
		exiFactory.setFidelityOptions(FidelityOptions.createAll());
//		GrammarContext grammarContext = exiFactory.getGrammars().getGrammarContext();
		

		StringEncoder stringEncoder = exiFactory.createStringEncoder(); //  new StringEncoderImpl();
		// EncoderContext encoderContext = new EncoderContextImpl(grammarContext, stringEncoder);
		
		StringDecoder stringDecoder = exiFactory.createStringDecoder(); // new StringDecoderImpl();
		// DecoderContext decoderContext = new DecoderContextImpl(grammarContext, stringDecoder);
		
		QName context = new QName("", "intList"); 
		// EvolvingUriContext uc = new RuntimeEvolvingUriContext(0, "");
		QNameContext qncContext = new QNameContext(0, 0, context, 0);
		
		RestrictedCharacterSetDatatype rcsDatatype = new RestrictedCharacterSetDatatype(null);
		
		// Bit
		EncoderChannel bitEC = getBitEncoder();
		ldtInteger.writeValueRCS(rcsDatatype, qncContext, bitEC, stringEncoder);
		bitEC.flush();
		Value v1 = ldtInteger.readValueRCS(rcsDatatype, qncContext, getBitDecoder(), stringDecoder);
		assertTrue(s.equals(v1.toString()));

		// Byte
		EncoderChannel byteEC = getByteEncoder();
		ldtInteger.writeValueRCS(rcsDatatype, qncContext, byteEC, stringEncoder);
		Value v2 = ldtInteger.readValueRCS(rcsDatatype, qncContext, getByteDecoder(), stringDecoder);
		assertTrue(s.equals(v2.toString()));
	}
	

	public void testListNBit1() throws IOException {
		StringValue s = new StringValue("+1 0 127 -127");
		String sRes = "1 0 127 -127";
		IntegerValue min = IntegerValue.valueOf(-128);
		IntegerValue max = IntegerValue.valueOf(127);
		ListDatatype ldtInteger = new ListDatatype(new NBitUnsignedIntegerDatatype(min, max, null), null);

		boolean valid = ldtInteger.isValid(s);
		assertTrue(valid);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		ldtInteger.writeValue(null, bitEC, null);
		bitEC.flush();
		Value v1 = ldtInteger.readValue(null, getBitDecoder(), null);
		assertTrue(v1.getValueType() == ValueType.LIST);
		ListValue lv1 = (ListValue) v1;
		assertTrue(sRes.equals(lv1.toString()));

		// Byte
		EncoderChannel byteEC = getByteEncoder();
		ldtInteger.writeValue(null, byteEC, null);
		Value v2 = ldtInteger.readValue(null, getByteDecoder(), null);
		assertTrue(v2.getValueType() == ValueType.LIST);
		ListValue lv2 = (ListValue) v2;
		assertTrue(sRes.equals(lv2.toString()));
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
	
	public void testListEnum1() throws IOException, EXIException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:simpleType name='listOfIDsEnum'>"
				+ "  <xs:restriction>"
				+ "    <xs:simpleType>"
				+ "      <xs:list>"
				+ "        <xs:simpleType>"
				+ "          <xs:restriction base='xs:ID'/>"
				+ "        </xs:simpleType>"
				+ "      </xs:list>"
				+ "    </xs:simpleType>"
				+ "    <xs:enumeration value='AB BC CD'/>"
				+ "    <xs:enumeration value='EF FG GH'/>"
				+ "    <xs:enumeration value='IJ JK KL'/>"
				+ "  </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"listOfIDsEnum", "");

		assertTrue(dt.getBuiltInType() == BuiltInType.LIST);
		ListDatatype listDt = (ListDatatype) dt;
		assertTrue(listDt.getListDatatype().getBuiltInType() == BuiltInType.STRING);

		assertTrue(dt.isValid(new StringValue("  AB  BC  CD   ")));
		assertTrue(dt.isValid(new StringValue("  AB  BC     ")));
		assertTrue(dt.isValid(new StringValue("  KL   ")));

		// assertFalse(dt.isValid(new StringValue("00")));
		assertTrue(dt.isValid(new StringValue("00")));
	}
	
	public void testListEnum2() throws IOException, EXIException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:simpleType name='listOfIntsEnum'>"
				+ "  <xs:restriction>"
				+ "    <xs:simpleType>"
				+ "      <xs:list>"
				+ "        <xs:simpleType>"
				+ "          <xs:restriction base='xs:int'/>"
				+ "        </xs:simpleType>"
				+ "      </xs:list>"
				+ "    </xs:simpleType>"
				+ "    <xs:enumeration value='1 2 3'/>"
				+ "    <xs:enumeration value='4 5 6'/>"
				+ "    <xs:enumeration value='7 8 9'/>"
				+ "  </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"listOfIntsEnum", "");

		assertTrue(dt.getBuiltInType() == BuiltInType.LIST);
		ListDatatype listDt = (ListDatatype) dt;
		assertTrue(listDt.getListDatatype().getBuiltInType() == BuiltInType.INTEGER);
		
		assertTrue(dt.isValid(new StringValue("  1  2  3   ")));
		assertTrue(dt.isValid(new StringValue("  4  5   6  ")));
		assertTrue(dt.isValid(new StringValue("  9   ")));
		assertTrue(dt.isValid(new StringValue(" 123")));
		
		assertFalse(dt.isValid(new StringValue("XXX")));
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