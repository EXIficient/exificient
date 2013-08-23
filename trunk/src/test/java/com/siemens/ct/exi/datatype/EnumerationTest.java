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

import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.types.BuiltInType;
import com.siemens.ct.exi.types.DatatypeMappingTest;
import com.siemens.ct.exi.values.StringValue;

public class EnumerationTest extends AbstractTestCase {

	public void testEnumerationStringRec() throws IOException, EXIException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "    <xs:simpleType name='tPredefinedBasicTypeEnum'>"
				+ "        <xs:restriction base='xs:Name'>"
				+ "            <xs:enumeration value='BOOLEAN'/>"
				+ "            <xs:enumeration value='INT8'/>"
				+ "            <xs:enumeration value='INT16'/>"
				+ "            <xs:enumeration value='INT24'/>"
				+ "            <xs:enumeration value='INT32'/>"
				+ "            <xs:enumeration value='INT64'/>"
				+ "        </xs:restriction>"
				+ "    </xs:simpleType>"
				+ "    <xs:simpleType name='tBasicTypeEnum'>"
				+ "        <xs:restriction base='tPredefinedBasicTypeEnum'/>"
				+ "    </xs:simpleType>" + "</xs:schema>";
	
		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"tBasicTypeEnum", "");
	
		assertTrue(dt.getBuiltInType() == BuiltInType.ENUMERATION);
	
		assertTrue(dt.isValid(new StringValue("BOOLEAN")));
		assertTrue(dt.isValid(new StringValue("INT8")));
		assertTrue(dt.isValid(new StringValue("INT16")));
		assertTrue(dt.isValid(new StringValue("INT24")));
		assertTrue(dt.isValid(new StringValue("INT32")));
		assertTrue(dt.isValid(new StringValue("INT64")));
	
		assertFalse(dt.isValid(new StringValue("00")));
		assertFalse(dt.isValid(new StringValue("bla")));
	
		EnumerationDatatype enumDt = (EnumerationDatatype) dt;
		assertTrue(enumDt.getDatatypeID() == DatatypeID.exi_string);
		// assertTrue(enumDt.getEnumValueBuiltInType() == BuiltInType.STRING);
	}

	public void testEnumerationListOfQNames1() throws IOException, EXIException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema' xmlns:bla='urn:bla'>"
				+ "  <xs:simpleType name='Enumeration'>"
				+ "    <xs:restriction base='qnameList'>"
				+ "      <xs:enumeration value='Hooo'/>"
				+ "      <xs:enumeration value='bla:Foo' />"
				+ "   </xs:restriction>"
				+ "  </xs:simpleType>"
				+ ""
				+ "  <xs:simpleType name='qnameList'>"
				+ "     <xs:list itemType='xs:QName'/>"
				+ "  </xs:simpleType>"
				+ "" + "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"Enumeration", "");

		// EXI errata item
		assertTrue(dt.getBuiltInType() == BuiltInType.LIST);
		ListDatatype listDt = (ListDatatype) dt;
		assertTrue(listDt.getListDatatype().getBuiltInType() == BuiltInType.STRING);

		assertTrue(dt.isValid(new StringValue("Hooo")));
		assertTrue(dt.isValid(new StringValue("bla:Foo")));

		assertTrue(dt.isValid(new StringValue("Baxslax")));
		assertTrue(dt.isValid(new StringValue("-123")));

		// assertTrue(dt.getBuiltInType() == BuiltInType.ENUMERATION);
		//
		// assertTrue(dt.isValid(new StringValue("Hooo")));
		// assertTrue(dt.isValid(new StringValue("bla:Foo")));
		//
		// assertFalse(dt.isValid(new StringValue("dd.doo")));
		//
		// EnumerationDatatype enumDt = (EnumerationDatatype) dt;
		// assertTrue(enumDt.getEnumValueBuiltInType() == BuiltInType.LIST);
	}

	public void testEnumerationListOfInts1() throws IOException, EXIException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='Enumeration'>"
				+ "    <xs:restriction base='intList'>"
				+ "      <xs:enumeration value='1 2 3'/>"
				+ "      <xs:enumeration value='3 4 5' />"
				+ "   </xs:restriction>"
				+ "  </xs:simpleType>"
				+ ""
				+ "  <xs:simpleType name='intList'>"
				+ "     <xs:list itemType='xs:integer'/>"
				+ "  </xs:simpleType>" + "" + "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"Enumeration", "");

		// EXI errata item
		assertTrue(dt.getBuiltInType() == BuiltInType.LIST);
		ListDatatype listDt = (ListDatatype) dt;
		assertTrue(listDt.getListDatatype().getBuiltInType() == BuiltInType.INTEGER);

		assertTrue(dt.isValid(new StringValue("1 2 3")));
		assertTrue(dt.isValid(new StringValue("3 4 5")));
		assertTrue(dt.isValid(new StringValue(" 3 4 5 ")));

		assertTrue(dt.isValid(new StringValue(" 123 456 789  "))); // any other
																	// int value

		assertFalse(dt.isValid(new StringValue("xx xx")));

		// assertTrue(dt.getBuiltInType() == BuiltInType.ENUMERATION);
		//
		// assertTrue(dt.isValid(new StringValue("1 2 3")));
		// assertTrue(dt.isValid(new StringValue("3 4 5")));
		// assertTrue(dt.isValid(new StringValue(" 3 4 5 ")));
		//
		// assertFalse(dt.isValid(new StringValue("5 6")));
		//
		// EnumerationDatatype enumDt = (EnumerationDatatype) dt;
		// assertTrue(enumDt.getEnumValueBuiltInType() == BuiltInType.LIST);
	}

	public EnumerationTest(String testName) {
		super(testName);
	}

	public void testEnumerationUnion1() throws IOException, EXIException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='union'>"
				+ "    <xs:union memberTypes='xs:integer xs:time'/>"
				+ "  </xs:simpleType>"
				+ ""
				+ "  <xs:simpleType name='Enumeration'>"
				+ "    <xs:restriction base='union'>"
				+ "      <xs:enumeration value='10'/>"
				+ "      <xs:enumeration value='12:32:00'/>"
				+ "      <xs:enumeration value='588'/>"
				+ "   </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"Enumeration", "");

		// Note: Enumeration schema types derived from others by union, QName or
		// Notation are processed by their respective built-in EXI datatype
		// representations
		assertTrue(dt.getBuiltInType() == BuiltInType.STRING);

		// assertTrue(dt.getBuiltInType() == BuiltInType.ENUMERATION);
		// // EnumerationDatatype enumDt = (EnumerationDatatype) dt;
		//
		// assertTrue(dt.isValid("+10"));
		// assertTrue(dt.isValid("12:32:00"));
		// assertTrue(dt.isValid("+588"));
		//
		// assertFalse(dt.isValid("00"));
		// assertFalse(dt.isValid("12:32:12"));
	}

	public void testEnumerationQName1() throws IOException, EXIException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema' xmlns:bla='urn:bla'>"
				+ "  <xs:simpleType name='Enumeration'>"
				+ "    <xs:restriction base='xs:QName'>"
				+ "      <xs:enumeration value='Ho'/>"
				+ "      <xs:enumeration value='bla:Uuu'/>"
				+ "   </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"Enumeration", "");

		// Note: Enumeration schema types derived from others by union, QName or
		// Notation are processed by their respective built-in EXI datatype
		// representations
		assertTrue(dt.getBuiltInType() == BuiltInType.STRING);

		// assertTrue(dt.getBuiltInType() == BuiltInType.ENUMERATION);
	}

	public void testEnumerationInteger1() throws IOException, EXIException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='Enumeration'>"
				+ "    <xs:restriction base='xs:integer'>"
				+ "      <xs:enumeration value='0'/>"
				+ "      <xs:enumeration value='1'/>"
				+ "      <xs:enumeration value='2'/>"
				+ "      <xs:enumeration value='3'/>" +
				// et cetera
				"    </xs:restriction>" + "  </xs:simpleType>" + "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"Enumeration", "");

		assertTrue(dt.getBuiltInType() == BuiltInType.ENUMERATION);
		// EnumerationDatatype enumDt = (EnumerationDatatype) dt;

		assertTrue(dt.isValid(new StringValue("+0")));

		assertFalse(dt.isValid(new StringValue("+4")));
		assertFalse(dt.isValid(new StringValue("-3")));
	}

	public void testEnumerationFloat1() throws IOException, EXIException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='Enumeration'>"
				+ "    <xs:restriction base='xs:float'>"
				+ "      <xs:enumeration value='1.5'/>"
				+ "      <xs:enumeration value='25'/>"
				+ "   </xs:restriction>"
				+ "  </xs:simpleType>" + "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"Enumeration", "");

		assertTrue(dt.getBuiltInType() == BuiltInType.ENUMERATION);
		// EnumerationDatatype enumDt = (EnumerationDatatype) dt;

		assertTrue(dt.isValid(new StringValue(" 1.5")));
		assertTrue(dt.isValid(new StringValue("15E-1")));
		assertTrue(dt.isValid(new StringValue(" 25 ")));
		assertTrue(dt.isValid(new StringValue(" 25E0 ")));

		assertFalse(dt.isValid(new StringValue("00")));
		assertFalse(dt.isValid(new StringValue("bla")));
	}

	public void testEnumerationFloat2() throws IOException, EXIException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='Enumeration'>"
				+ "    <xs:restriction base='xs:float'>"
				+ "      <xs:enumeration value='1.0'/>"
				+ "      <xs:enumeration value='30.000'/>"
				+ "   </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"Enumeration", "");

		assertTrue(dt.getBuiltInType() == BuiltInType.ENUMERATION);
		// EnumerationDatatype enumDt = (EnumerationDatatype) dt;

		assertTrue(dt.isValid(new StringValue("1.000")));
		assertTrue(dt.isValid(new StringValue("1.0e0")));
		assertTrue(dt.isValid(new StringValue("3E1")));

		assertFalse(dt.isValid(new StringValue("00")));
		assertFalse(dt.isValid(new StringValue("bla")));
	}

	public void testEnumerationGMonthDay1() throws IOException, EXIException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='Enumeration'>"
				+ "    <xs:restriction base='xs:gMonthDay'>"
				+ "      <xs:enumeration value='--01-01'/>"
				+ "      <xs:enumeration value='--05-01'/>"
				+ "      <xs:enumeration value='--05-08'/>"
				+ "      <xs:enumeration value='--07-14'/>"
				+ "      <xs:enumeration value='--08-15'/>"
				+ "      <xs:enumeration value='--11-01'/>"
				+ "      <xs:enumeration value='--11-11'/>"
				+ "      <xs:enumeration value='--12-25'/>"
				+ "   </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"Enumeration", "");

		assertTrue(dt.getBuiltInType() == BuiltInType.ENUMERATION);
		// EnumerationDatatype enumDt = (EnumerationDatatype) dt;

		assertTrue(dt.isValid(new StringValue("--01-01")));
		assertTrue(dt.isValid(new StringValue("--05-01")));
		assertTrue(dt.isValid(new StringValue("--07-14")));
		assertTrue(dt.isValid(new StringValue("--11-11")));
		assertTrue(dt.isValid(new StringValue("--12-25")));

		assertFalse(dt.isValid(new StringValue("00")));
		assertFalse(dt.isValid(new StringValue("bla")));
	}

}