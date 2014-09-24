/*
 * Copyright (C) 2007-2014 Siemens AG
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

package com.siemens.ct.exi.types;

import java.io.ByteArrayInputStream;
import java.util.HashSet;
import java.util.Set;

import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTypeDefinition;

import com.siemens.ct.exi.context.GrammarContext;
import com.siemens.ct.exi.context.GrammarUriContext;
import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.datatype.AbstractTestCase;
import com.siemens.ct.exi.datatype.BooleanFacetDatatype;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.datatype.DatetimeDatatype;
import com.siemens.ct.exi.datatype.ListDatatype;
import com.siemens.ct.exi.datatype.RestrictedCharacterSetDatatype;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammars.Grammars;
import com.siemens.ct.exi.grammars.XSDGrammarsBuilder;
import com.siemens.ct.exi.grammars.event.StartElement;
import com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTagGrammar;
import com.siemens.ct.exi.values.StringValue;

public class DatatypeMappingTest extends AbstractTestCase {
	public DatatypeMappingTest(String testName) {
		super(testName);
	}

	protected static Grammars getGrammarFor(String schemaAsString)
			throws EXIException {
		XSDGrammarsBuilder xsdGB = XSDGrammarsBuilder.newInstance();
		ByteArrayInputStream bais = new ByteArrayInputStream(
				schemaAsString.getBytes());
		xsdGB.loadGrammars(bais);
		return xsdGB.toGrammars();
	}

	protected static Grammars getGrammar(String schemaAsString)
			throws EXIException {
		XSDGrammarsBuilder xsdGB = XSDGrammarsBuilder.newInstance();
		ByteArrayInputStream bais = new ByteArrayInputStream(
				schemaAsString.getBytes());
		xsdGB.loadGrammars(bais);
		return xsdGB.toGrammars();
	}

	public static Datatype getSimpleDatatypeFor(String schemaAsString,
			String typeName, String typeURI) throws EXIException {
		XSDGrammarsBuilder xsdGB = XSDGrammarsBuilder.newInstance();
		ByteArrayInputStream bais = new ByteArrayInputStream(
				schemaAsString.getBytes());
		xsdGB.loadGrammars(bais);
		xsdGB.toGrammars();

		XSModel xsModel = xsdGB.getXSModel();

		XSTypeDefinition td = xsModel.getTypeDefinition(typeName, typeURI);

		assertTrue("SimpleType expected",
				td.getTypeCategory() == XSTypeDefinition.SIMPLE_TYPE);

		Datatype dt = xsdGB.getDatatype((XSSimpleTypeDefinition) td);

		return dt;
	}

	public void testBinary1() throws Exception {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='Binary'>"
				+ "    <xs:restriction base='xs:base64Binary'>"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"Binary", "");

		assertTrue(BuiltInType.BINARY_BASE64 == dt.getBuiltInType());
		// assertTrue(BuiltIn.XSD_BASE64BINARY == dt.getDatatypeIdentifier());

//		assertTrue(dt.getRestrictedCharacterSet().equals(
//				new XSDBase64CharacterSet()));
	}

	public void testBinary2() throws Exception {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='Binary'>"
				+ "    <xs:restriction base='xs:hexBinary'>"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"Binary", "");

		assertTrue(BuiltInType.BINARY_HEX == dt.getBuiltInType());
		// assertTrue(BuiltIn.XSD_HEXBINARY == dt.getDatatypeIdentifier());

//		assertTrue(dt.getRestrictedCharacterSet().equals(
//				new XSDHexBinaryCharacterSet()));
	}

	public void testBooleanNoFacet() throws Exception {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='Boolean'>"
				+ "    <xs:restriction base='xs:boolean'>"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"Boolean", "");

		assertTrue(BuiltInType.BOOLEAN == dt.getBuiltInType());
		// assertTrue(BuiltIn.XSD_BOOLEAN == dt.getDatatypeIdentifier());

//		assertTrue(dt.getRestrictedCharacterSet().equals(
//				new XSDBooleanCharacterSet()));
	}

	public void testBooleanFacet1() throws Exception {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='Boolean01'>"
				+ "    <xs:restriction base='xs:boolean'>"
				+ "      <xs:pattern value='0' />"
				+ "      <xs:pattern value='1' />"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>" + "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"Boolean01", "");

		assertTrue(BuiltInType.BOOLEAN_FACET == dt.getBuiltInType());
		assertTrue(dt instanceof BooleanFacetDatatype);

//		assertTrue(dt.getRestrictedCharacterSet().equals(
//				new XSDBooleanCharacterSet()));
	}

	public void testDateTime1() throws Exception {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='DateTime'>"
				+ "    <xs:restriction base='xs:dateTime'>"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"DateTime", "");

		assertTrue(BuiltInType.DATETIME == dt.getBuiltInType());
		// assertTrue(BuiltIn.XSD_DATETIME == dt.getDatatypeIdentifier());

		DatetimeDatatype dtd = (DatetimeDatatype) dt;
		assertTrue(DateTimeType.dateTime == dtd.getDatetimeType());

//		assertTrue(dt.getRestrictedCharacterSet().equals(
//				new XSDDateTimeCharacterSet()));
	}

	public void testDateTime2() throws Exception {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='DateTime'>"
				+ "    <xs:restriction base='xs:gDay'>"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"DateTime", "");

		assertTrue(BuiltInType.DATETIME == dt.getBuiltInType());
		// assertTrue(BuiltIn.XSD_DATETIME == dt.getDatatypeIdentifier());

		DatetimeDatatype dtd = (DatetimeDatatype) dt;
		assertTrue(DateTimeType.gDay == dtd.getDatetimeType());

//		assertTrue(dt.getRestrictedCharacterSet().equals(
//				new XSDDateTimeCharacterSet()));
	}

	public void testFloat1() throws Exception {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='Float'>"
				+ "    <xs:restriction base='xs:float'>"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"Float", "");

		assertTrue(BuiltInType.FLOAT == dt.getBuiltInType());

//		assertTrue(dt.getRestrictedCharacterSet().equals(
//				new XSDDoubleCharacterSet()));
	}

	public void testDouble1() throws Exception {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='Double'>"
				+ "    <xs:restriction base='xs:double'>"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"Double", "");

		assertTrue(BuiltInType.FLOAT == dt.getBuiltInType());

//		assertTrue(dt.getRestrictedCharacterSet().equals(
//				new XSDDoubleCharacterSet()));
	}

	public void testInteger1() throws Exception {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='Integer'>"
				+ "    <xs:restriction base='xs:integer'>"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"Integer", "");

		assertTrue(BuiltInType.INTEGER == dt.getBuiltInType());
		// IntegerDatatype idt = (IntegerDatatype) dt;
		// assertTrue(idt.getIntegerType() == IntegerType.INTEGER_BIG);

//		assertTrue(dt.getRestrictedCharacterSet().equals(
//				new XSDIntegerCharacterSet()));
	}

	public void testInteger2() throws Exception {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='Integer'>"
				+ "    <xs:restriction base='xs:int'>"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"Integer", "");

		assertTrue(BuiltInType.INTEGER == dt.getBuiltInType());
		// IntegerDatatype idt = (IntegerDatatype) dt;
		// assertTrue(idt.getIntegerType() == IntegerType.INTEGER_32);

//		assertTrue(dt.getRestrictedCharacterSet().equals(
//				new XSDIntegerCharacterSet()));
	}

	public void testInteger3() throws Exception {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='Integer'>"
				+ "    <xs:restriction base='xs:negativeInteger'>"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"Integer", "");

		assertTrue(BuiltInType.INTEGER == dt.getBuiltInType());
		// IntegerDatatype idt = (IntegerDatatype) dt;
		// assertTrue(idt.getIntegerType() == IntegerType.INTEGER_BIG);

//		assertTrue(dt.getRestrictedCharacterSet().equals(
//				new XSDIntegerCharacterSet()));
	}

	public void testInteger4() throws Exception {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='Integer'>"
				+ "    <xs:restriction base='xs:long'>"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"Integer", "");

		assertTrue(BuiltInType.INTEGER == dt.getBuiltInType());
		// IntegerDatatype idt = (IntegerDatatype) dt;
		// assertTrue(idt.getIntegerType() == IntegerType.INTEGER_64);

//		assertTrue(dt.getRestrictedCharacterSet().equals(
//				new XSDIntegerCharacterSet()));
	}

	public void testUnsignedInteger1() throws Exception {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='UnsignedInteger'>"
				+ "    <xs:restriction base='xs:nonNegativeInteger'>"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"UnsignedInteger", "");

		assertTrue(BuiltInType.UNSIGNED_INTEGER == dt.getBuiltInType());
		// UnsignedIntegerDatatype uidt = (UnsignedIntegerDatatype) dt;
		// assertTrue(uidt.getIntegerType() ==
		// IntegerType.UNSIGNED_INTEGER_BIG);

//		assertTrue(dt.getRestrictedCharacterSet().equals(
//				new XSDIntegerCharacterSet()));
	}

	public void testUnsignedInteger2() throws Exception {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='UnsignedInteger'>"
				+ "    <xs:restriction base='xs:unsignedLong'>"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"UnsignedInteger", "");

		assertTrue(BuiltInType.UNSIGNED_INTEGER == dt.getBuiltInType());
		// UnsignedIntegerDatatype uidt = (UnsignedIntegerDatatype) dt;
		// assertTrue(uidt.getIntegerType() == IntegerType.UNSIGNED_INTEGER_64);

//		assertTrue(dt.getRestrictedCharacterSet().equals(
//				new XSDIntegerCharacterSet()));
	}

	public void testUnsignedInteger3() throws Exception {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='UnsignedInteger'>"
				+ "    <xs:restriction base='xs:unsignedInt'>"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"UnsignedInteger", "");

		assertTrue(dt.toString(),
				BuiltInType.UNSIGNED_INTEGER == dt.getBuiltInType());
		// UnsignedIntegerDatatype uidt = (UnsignedIntegerDatatype) dt;
		// assertTrue(uidt.getIntegerType() == IntegerType.UNSIGNED_INTEGER_32);

//		assertTrue(dt.getRestrictedCharacterSet().equals(
//				new XSDIntegerCharacterSet()));
	}

	public void testUnsignedInteger4() throws Exception {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='UnsignedInteger'>"
				+ "    <xs:restriction base='xs:unsignedShort'>"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"UnsignedInteger", "");

		assertTrue(dt.toString(),
				BuiltInType.UNSIGNED_INTEGER == dt.getBuiltInType());
		// UnsignedIntegerDatatype uidt = (UnsignedIntegerDatatype) dt;
		// assertTrue(uidt.getIntegerType() == IntegerType.UNSIGNED_INTEGER_16);

//		assertTrue(dt.getRestrictedCharacterSet().equals(
//				new XSDIntegerCharacterSet()));
	}

	public void testUnsignedInteger5() throws Exception {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='UnsignedInteger'>"
				+ "    <xs:restriction base='xs:positiveInteger'>"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"UnsignedInteger", "");
		assertTrue(BuiltInType.UNSIGNED_INTEGER == dt.getBuiltInType());
		// UnsignedIntegerDatatype uidt = (UnsignedIntegerDatatype) dt;
		// assertTrue(uidt.getIntegerType() ==
		// IntegerType.UNSIGNED_INTEGER_BIG);

//		assertTrue(dt.getRestrictedCharacterSet().equals(
//				new XSDIntegerCharacterSet()));
	}

	public void testUnsignedIntegerFacet1() throws Exception {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='UnsignedInteger'>"
				+ "    <xs:restriction base='xs:integer'>"
				+ "        <xs:minInclusive value='250' />"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"UnsignedInteger", "");

		assertTrue(dt.toString(),
				BuiltInType.UNSIGNED_INTEGER == dt.getBuiltInType());
		// UnsignedIntegerDatatype uidt = (UnsignedIntegerDatatype) dt;
		// assertTrue(uidt.getIntegerType() ==
		// IntegerType.UNSIGNED_INTEGER_BIG);

//		assertTrue(dt.getRestrictedCharacterSet().equals(
//				new XSDIntegerCharacterSet()));
	}

	public void testUnsignedIntegerFacet2() throws Exception {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='UnsignedInteger'>"
				+ "    <xs:restriction base='xs:long'>"
				+ "        <xs:minInclusive value='1' />"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"UnsignedInteger", "");

		assertTrue(dt.toString(),
				BuiltInType.UNSIGNED_INTEGER == dt.getBuiltInType());
		// UnsignedIntegerDatatype uidt = (UnsignedIntegerDatatype) dt;
		// assertTrue(uidt.getIntegerType() == IntegerType.UNSIGNED_INTEGER_64);

//		assertTrue(dt.getRestrictedCharacterSet().equals(
//				new XSDIntegerCharacterSet()));
	}

	public void testUnsignedIntegerFacet3() throws Exception {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='UnsignedInteger'>"
				+ "    <xs:restriction base='xs:int'>"
				+ "        <xs:minInclusive value='33' />"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"UnsignedInteger", "");

		assertTrue(dt.toString(),
				BuiltInType.UNSIGNED_INTEGER == dt.getBuiltInType());
		// UnsignedIntegerDatatype uidt = (UnsignedIntegerDatatype) dt;
		// assertTrue(uidt.getIntegerType() == IntegerType.UNSIGNED_INTEGER_32);

//		assertTrue(dt.getRestrictedCharacterSet().equals(
//				new XSDIntegerCharacterSet()));
	}

	public void testNBitInteger1() throws Exception {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='NBit'>"
				+ "    <xs:restriction base='xs:byte'>"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"NBit", "");

		assertTrue(BuiltInType.NBIT_UNSIGNED_INTEGER == dt.getBuiltInType());
		// NBitUnsignedIntegerDatatype nbdt = (NBitUnsignedIntegerDatatype) dt;
		// assertTrue(nbdt.getIntegerType() == IntegerType.INTEGER_8);

//		assertTrue(dt.getRestrictedCharacterSet().equals(
//				new XSDIntegerCharacterSet()));
	}

	public void testNBitInteger2() throws Exception {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='NBit'>"
				+ "    <xs:restriction base='xs:unsignedByte'>"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"NBit", "");

		assertTrue(BuiltInType.NBIT_UNSIGNED_INTEGER == dt.getBuiltInType());
		// NBitUnsignedIntegerDatatype nbdt = (NBitUnsignedIntegerDatatype) dt;
		// assertTrue(nbdt.getIntegerType() == IntegerType.UNSIGNED_INTEGER_8);

//		assertTrue(dt.getRestrictedCharacterSet().equals(
//				new XSDIntegerCharacterSet()));
	}

	public void testNBitIntegerFacet1() throws Exception {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='NBit'>"
				+ "    <xs:restriction base='xs:integer'>"
				+ "      <xs:minInclusive value='2' />"
				+ "      <xs:maxExclusive value='10'/>"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"NBit", "");

		assertTrue(BuiltInType.NBIT_UNSIGNED_INTEGER == dt.getBuiltInType());
		// NBitUnsignedIntegerDatatype nbdt = (NBitUnsignedIntegerDatatype) dt;
		// assertTrue(nbdt.getIntegerType() == IntegerType.INTEGER_BIG);

//		assertTrue(dt.getRestrictedCharacterSet().equals(
//				new XSDIntegerCharacterSet()));
	}

	public void testNBitIntegerFacet2() throws Exception {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='NBit'>"
				+ "    <xs:restriction base='xs:long'>"
				+ "      <xs:minInclusive value='2' />"
				+ "      <xs:maxExclusive value='10'/>"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"NBit", "");

		assertTrue(BuiltInType.NBIT_UNSIGNED_INTEGER == dt.getBuiltInType());
		// NBitUnsignedIntegerDatatype nbdt = (NBitUnsignedIntegerDatatype) dt;
		// assertTrue(nbdt.getIntegerType() == IntegerType.INTEGER_64);

//		assertTrue(dt.getRestrictedCharacterSet().equals(
//				new XSDIntegerCharacterSet()));
	}

	public void testNBitIntegerFacet3() throws Exception {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='NBit'>"
				+ "    <xs:restriction base='xs:int'>"
				+ "      <xs:minInclusive value='2' />"
				+ "      <xs:maxExclusive value='10'/>"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"NBit", "");

		assertTrue(BuiltInType.NBIT_UNSIGNED_INTEGER == dt.getBuiltInType());
		// NBitUnsignedIntegerDatatype nbdt = (NBitUnsignedIntegerDatatype) dt;
		// assertTrue(nbdt.getIntegerType() == IntegerType.INTEGER_32);

//		assertTrue(dt.getRestrictedCharacterSet().equals(
//				new XSDIntegerCharacterSet()));
	}

	public void testNBitIntegerFacet4() throws Exception {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='NBit'>"
				+ "    <xs:restriction base='xs:unsignedInt'>"
				+ "      <xs:minInclusive value='2' />"
				+ "      <xs:maxExclusive value='10'/>"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"NBit", "");

		assertTrue(BuiltInType.NBIT_UNSIGNED_INTEGER == dt.getBuiltInType());
		// NBitUnsignedIntegerDatatype nbdt = (NBitUnsignedIntegerDatatype) dt;
		// assertTrue(nbdt.getIntegerType() == IntegerType.UNSIGNED_INTEGER_32);

//		assertTrue(dt.getRestrictedCharacterSet().equals(
//				new XSDIntegerCharacterSet()));
	}

	public void testEnumeration1() throws Exception {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='Enumeration'>"
				+ "    <xs:restriction base='xs:string'>"
				+ "      <xs:enumeration value='Jan'/>"
				+ "      <xs:enumeration value='Feb'/>" +
				// et cetera
				"    </xs:restriction>" + "  </xs:simpleType>" + "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"Enumeration", "");

		assertTrue(BuiltInType.ENUMERATION == dt.getBuiltInType());

//		assertTrue(dt.getRestrictedCharacterSet().equals(
//				new XSDStringCharacterSet()));
	}

	public void testEnumeration2() throws Exception {
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

		assertTrue(BuiltInType.ENUMERATION == dt.getBuiltInType());
		assertTrue(dt.isValid(new StringValue("+0")));

//		assertTrue(dt.getRestrictedCharacterSet().equals(
//				new XSDIntegerCharacterSet()));

	}

	public void testList1() throws Exception {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='ListInt'>"
				+ "    <xs:list itemType='xs:int' />"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"ListInt", "");

		assertTrue(BuiltInType.LIST == dt.getBuiltInType());

		ListDatatype dtl = (ListDatatype) dt;

		assertTrue(BuiltInType.INTEGER == dtl.getListDatatype()
				.getBuiltInType());
		// IntegerDatatype idt = (IntegerDatatype) dtl.getListDatatype();
		// assertTrue(idt.getIntegerType() == IntegerType.INTEGER_32);

//		assertTrue(dt.getRestrictedCharacterSet().equals(
//				new XSDIntegerCharacterSet()));
	}

	public void testRestrictedCharSet1() throws Exception {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "    <xs:simpleType name='Letter'>"
				+ "      <xs:restriction base='xs:string'>"
				+ "      <xs:pattern value='[a-z]'/>"
				+ "      </xs:restriction>"
				+ "    </xs:simpleType>"
				+ "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"Letter", "");

		assertTrue(BuiltInType.RCS_STRING == dt.getBuiltInType());
		assertTrue(dt instanceof RestrictedCharacterSetDatatype);

		Set<Integer> codePoints = new HashSet<Integer>();
		for (int i = 'a'; i <= 'z'; i++) {
			codePoints.add(i);
		}
//		CodePointCharacterSet rcs = new CodePointCharacterSet(codePoints);
//		assertTrue(dt.getRestrictedCharacterSet().equals(rcs));
	}

	public void testRestrictedCharSet2() throws Exception {
		// uses xs:language but defines *own* pattern facet
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "    <xs:simpleType name='myLanguage'>"
				+ "      <xs:restriction base='xs:language'>"
				+ "      <xs:pattern value='[a-c]'/>"
				+ "      </xs:restriction>"
				+ "    </xs:simpleType>"
				+ "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"myLanguage", "");

		assertTrue(BuiltInType.RCS_STRING == dt.getBuiltInType());
		assertTrue(dt instanceof RestrictedCharacterSetDatatype);

		Set<Integer> codePoints = new HashSet<Integer>();
		codePoints.add((int) 'a');
		codePoints.add((int) 'b');
		codePoints.add((int) 'c');
//		CodePointCharacterSet rcs = new CodePointCharacterSet(codePoints);
//		assertTrue(dt.getRestrictedCharacterSet().equals(rcs));
	}

	public void testRestrictedCharSet3() throws Exception {
		// uses xs:language but defines *own* BUT same pattern facet
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "    <xs:simpleType name='myLanguage'>"
				+ "      <xs:restriction base='xs:language'>"
				+ "      <xs:pattern value='([a-zA-Z]{1,8})(-[a-zA-Z0-9]{1,8})*'/>"
				+ "      </xs:restriction>"
				+ "    </xs:simpleType>"
				+ "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"myLanguage", "");

		assertTrue(BuiltInType.RCS_STRING == dt.getBuiltInType());
		assertTrue(dt instanceof RestrictedCharacterSetDatatype);

		Set<Integer> codePoints = new HashSet<Integer>();
		codePoints.add((int) '-');
		for (int i = 'a'; i <= 'z'; i++) {
			codePoints.add(i);
		}
		for (int i = 'A'; i <= 'Z'; i++) {
			codePoints.add(i);
		}
		for (int i = '0'; i <= '9'; i++) {
			codePoints.add(i);
		}
//		CodePointCharacterSet rcs = new CodePointCharacterSet(codePoints);
//		assertTrue(dt.getRestrictedCharacterSet().equals(rcs));
	}

	public void testRestrictedCharSet4() throws Exception {
		// uses indirectly xs:language but defines *own* BUT same pattern facet
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "    <xs:simpleType name='myLanguageParent'>"
				+ "      <xs:restriction base='xs:language'>"
				+ "      <xs:pattern value='([a-zA-Z]{1,8})(-[a-zA-Z0-9]{1,8})*'/>"
				+ "      </xs:restriction>"
				+ "    </xs:simpleType>"
				+ "    <xs:simpleType name='myLanguage'>"
				+ "      <xs:restriction base='myLanguageParent' />"
				+ "    </xs:simpleType>" + "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"myLanguage", "");

		assertTrue(BuiltInType.RCS_STRING == dt.getBuiltInType());
		assertTrue(dt instanceof RestrictedCharacterSetDatatype);

		Set<Integer> codePoints = new HashSet<Integer>();
		codePoints.add((int) '-');
		for (int i = 'a'; i <= 'z'; i++) {
			codePoints.add(i);
		}
		for (int i = 'A'; i <= 'Z'; i++) {
			codePoints.add(i);
		}
		for (int i = '0'; i <= '9'; i++) {
			codePoints.add(i);
		}
//		CodePointCharacterSet rcs = new CodePointCharacterSet(codePoints);
//		assertTrue(dt.getRestrictedCharacterSet().equals(rcs));
	}
	
	
	// exi:string datatype does not have RCS in Preserve.LexicalValue mode
	public void testRestrictedCharSet5LexicalValues() throws Exception {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "    <xs:simpleType name='myRCS'>"
				+ "      <xs:restriction base='xs:language'>"
				+ "      <xs:pattern value='[a-z]{3}'/>"
				+ "      </xs:restriction>"
				+ "    </xs:simpleType>"
				+ "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"myRCS", "");

		assertTrue(BuiltInType.RCS_STRING == dt.getBuiltInType());
		assertTrue(dt instanceof RestrictedCharacterSetDatatype);

		Set<Integer> codePoints = new HashSet<Integer>();
		for (int i = 'a'; i <= 'z'; i++) {
			codePoints.add(i);
		}
//		CodePointCharacterSet rcs = new CodePointCharacterSet(codePoints);
//		assertTrue(dt.getRestrictedCharacterSet().equals(rcs));
//		
//		assertTrue(rcs.getCodingLength()==5);
//		
//		EXIFactory ef = DefaultEXIFactory.newInstance();
//		Grammars grs = GrammarFactory.newInstance().createGrammars(new ByteArrayInputStream(schemaAsString.getBytes()));
//		ef.setGrammars(grs);
//		ef.getFidelityOptions().setFidelity(FidelityOptions.FEATURE_LEXICAL_VALUE, true);
//		TypeEncoder te = ef.createTypeEncoder();
//		StringEncoder se = ef.createStringEncoder();
//		String value = "abcd";
//		te.isValid(dt, new StringValue(value));
//		QNameContext qnContext = new QNameContext(0, 0, new QName("test"), 0);
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		EncoderChannel valueChannel = new ByteEncoderChannel(baos);
//		te.writeValue(qnContext, valueChannel, se);
		
		
	}

	public void testNoRestrictedCharSet0() throws Exception {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "    <xs:simpleType name='myString'>"
				+ "      <xs:restriction base='xs:string'>"
				+ "      <xs:pattern value='[ABC&#x10FFF;]{1}'/>"
				+ "      </xs:restriction>"
				+ "    </xs:simpleType>"
				+ "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"myString", "");

		// NON BMP --> STRING
		assertTrue(BuiltInType.STRING == dt.getBuiltInType());

		// assertTrue(BuiltInType.RESTRICTED_CHARACTER_SET ==
		// dt.getBuiltInType());
		//
		// Set<Integer> codePoints = new HashSet<Integer>();
		// codePoints.add((int)'A');
		// codePoints.add((int)'B');
		// codePoints.add((int)'C');
		// CodePointCharacterSet rcs = new CodePointCharacterSet(codePoints);
		// assertTrue(dt.getRestrictedCharacterSet().equals(rcs));
	}

	public void testNoRestrictedCharSet1() throws Exception {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "    <xs:simpleType name='Foo'>"
				+ "      <xs:restriction base='xs:string'>"
				+ "      <xs:pattern value='\\c'/>"
				+ "      </xs:restriction>"
				+ "    </xs:simpleType>" + "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"Foo", "");

		assertTrue(BuiltInType.STRING == dt.getBuiltInType());

//		assertTrue(dt.getRestrictedCharacterSet().equals(
//				new XSDStringCharacterSet()));
	}

	public void testNoRestrictedCharSet2() throws Exception {
		/*
		 * NOTE: If the target datatype definition is a definition for a
		 * built-in datatypeXS2, there is no restricted character set for the
		 * string value. http://www.w3.org/TR/exi/#restrictedCharSet
		 */
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "    <xs:simpleType name='myLanguage'>"
				+ "      <xs:restriction base='xs:language' />"
				+ "    </xs:simpleType>" + "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"myLanguage", "");

		assertTrue("" + dt.getBuiltInType(),
				BuiltInType.STRING == dt.getBuiltInType());

//		assertTrue(dt.getRestrictedCharacterSet().equals(
//				new XSDStringCharacterSet()));
	}

	public void testPositiveInteger1() throws Exception {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "    <xs:simpleType name='myPositiveInteger'>"
				+ "      <xs:restriction base='xs:positiveInteger' />"
				+ "    </xs:simpleType>" + "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"myPositiveInteger", "");

		assertTrue("" + dt.getBuiltInType(),
				BuiltInType.UNSIGNED_INTEGER == dt.getBuiltInType());
		// UnsignedIntegerDatatype uidt = (UnsignedIntegerDatatype) dt;
		// assertTrue(uidt.getIntegerType() ==
		// IntegerType.UNSIGNED_INTEGER_BIG);

		Grammars grammar = getGrammar(schemaAsString);
		GrammarContext gc = grammar.getGrammarContext();
		QNameContext qncMyPositiveInteger = gc.getGrammarUriContext("")
				.getQNameContext("myPositiveInteger");
		// SchemaInformedFirstStartTagRule r = (SchemaInformedFirstStartTagRule)
		// grammar
		// .getTypeGrammar(new QName("", "myPositiveInteger"));
		SchemaInformedFirstStartTagGrammar r = qncMyPositiveInteger
				.getTypeGrammar();
		assertTrue(!r.isTypeCastable());
		assertTrue(!r.isNillable());

//		assertTrue(dt.getRestrictedCharacterSet().equals(
//				new XSDIntegerCharacterSet()));
	}

	public void testNonPositiveInteger1() throws Exception {
		/*
		 * Known Xerces Bug !? xs:nonPositiveInteger not type-castable
		 */
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "    <xs:element name='myNonPositiveInteger' type='xs:nonPositiveInteger' />"
				+ "</xs:schema>";

		Grammars grammar = getGrammar(schemaAsString);
		// GrammarURIEntry[] gues = grammar.getGrammarEntries();
		GrammarContext gc = grammar.getGrammarContext();
		GrammarUriContext guc0 = gc.getGrammarUriContext(0);
		QNameContext qnc = guc0.getQNameContext("myNonPositiveInteger");

		StartElement se = qnc.getGlobalStartElement();
		// StartElement se = grammar.getGlobalElement(new
		// QName("","myNonPositiveInteger"));
		// StartElement se =
		// grammar.getGlobalElement(XSDGrammarBuilder.getEfficientQName(gues,
		// "", "myNonPositiveInteger"));

		SchemaInformedFirstStartTagGrammar r = (SchemaInformedFirstStartTagGrammar) se
				.getGrammar();
		assertTrue(r.isTypeCastable());
		assertTrue(!r.isNillable());
	}

	public void testNonPositiveInteger2() throws Exception {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "    <xs:simpleType name='myNonPositiveInteger'>"
				+ "      <xs:restriction base='xs:nonPositiveInteger' />"
				+ "    </xs:simpleType>" + "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"myNonPositiveInteger", "");

		assertTrue("" + dt.getBuiltInType(),
				BuiltInType.INTEGER == dt.getBuiltInType());
		// IntegerDatatype idt = (IntegerDatatype) dt;
		// assertTrue(idt.getIntegerType() == IntegerType.INTEGER_BIG);

		Grammars grammar = getGrammar(schemaAsString);
		GrammarContext gc = grammar.getGrammarContext();
		QNameContext myNonPositiveInteger = gc.getGrammarUriContext("")
				.getQNameContext("myNonPositiveInteger");
		// SchemaInformedFirstStartTagRule r = (SchemaInformedFirstStartTagRule)
		// grammar
		// .getTypeGrammar(new QName("", "myNonPositiveInteger"));
		SchemaInformedFirstStartTagGrammar r = myNonPositiveInteger
				.getTypeGrammar();
		assertTrue(!r.isTypeCastable());
		assertTrue(!r.isNillable());

//		assertTrue(dt.getRestrictedCharacterSet().equals(
//				new XSDIntegerCharacterSet()));
	}

}