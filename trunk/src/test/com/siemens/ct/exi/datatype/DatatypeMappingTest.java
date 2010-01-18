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

import java.io.ByteArrayInputStream;

import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTypeDefinition;

import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.XSDGrammarBuilder;
import com.siemens.ct.exi.types.BuiltIn;
import com.siemens.ct.exi.types.BuiltInType;
import com.siemens.ct.exi.util.datatype.DatetimeType;

public class DatatypeMappingTest extends AbstractTestCase {
	public DatatypeMappingTest(String testName) {
		super(testName);
	}

	protected static Datatype getSimpleDatatypeFor(String schemaAsString,
			String typeName, String typeURI) throws EXIException {
		XSDGrammarBuilder xsdGB = XSDGrammarBuilder.newInstance();
		ByteArrayInputStream bais = new ByteArrayInputStream(schemaAsString
				.getBytes());
		xsdGB.loadGrammar(bais);
		XSModel xsModel = xsdGB.getXSModel();

		XSTypeDefinition td = xsModel.getTypeDefinition(typeName, typeURI);

		assertTrue("SimpleType expected",
				td.getTypeCategory() == XSTypeDefinition.SIMPLE_TYPE);

		Datatype dt = BuiltIn.getDatatype((XSSimpleTypeDefinition) td);

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

		assertTrue(BuiltInType.BINARY_BASE64 == dt.getDefaultBuiltInType());
		// assertTrue(BuiltIn.XSD_BASE64BINARY == dt.getDatatypeIdentifier());
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

		assertTrue(BuiltInType.BINARY_HEX == dt.getDefaultBuiltInType());
		// assertTrue(BuiltIn.XSD_HEXBINARY == dt.getDatatypeIdentifier());
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

		assertTrue(BuiltInType.BOOLEAN == dt.getDefaultBuiltInType());
		// assertTrue(BuiltIn.XSD_BOOLEAN == dt.getDatatypeIdentifier());
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

		assertTrue(BuiltInType.BOOLEAN_PATTERN == dt.getDefaultBuiltInType());
		// assertTrue(BuiltIn.XSD_BOOLEAN == dt.getDatatypeIdentifier());
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

		assertTrue(BuiltInType.DATETIME == dt.getDefaultBuiltInType());
		// assertTrue(BuiltIn.XSD_DATETIME == dt.getDatatypeIdentifier());

		DatetimeDatatype dtd = (DatetimeDatatype) dt;
		assertTrue(DatetimeType.dateTime == dtd.getDatetimeType());
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

		assertTrue(BuiltInType.DATETIME == dt.getDefaultBuiltInType());
		// assertTrue(BuiltIn.XSD_DATETIME == dt.getDatatypeIdentifier());

		DatetimeDatatype dtd = (DatetimeDatatype) dt;
		assertTrue(DatetimeType.gDay == dtd.getDatetimeType());
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

		assertTrue(BuiltInType.FLOAT == dt.getDefaultBuiltInType());
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

		assertTrue(BuiltInType.DOUBLE == dt.getDefaultBuiltInType());
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

		assertTrue(BuiltInType.BIG_INTEGER == dt.getDefaultBuiltInType());
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

		assertTrue(BuiltInType.INTEGER == dt.getDefaultBuiltInType());
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

		assertTrue(BuiltInType.BIG_INTEGER == dt.getDefaultBuiltInType());
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

		assertTrue(BuiltInType.LONG == dt.getDefaultBuiltInType());
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

		assertTrue(BuiltInType.UNSIGNED_BIG_INTEGER == dt
				.getDefaultBuiltInType());
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

		assertTrue(BuiltInType.UNSIGNED_LONG == dt.getDefaultBuiltInType());
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

		assertTrue(dt.toString(), BuiltInType.UNSIGNED_LONG == dt
				.getDefaultBuiltInType());
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

		assertTrue(dt.toString(), BuiltInType.UNSIGNED_INTEGER == dt
				.getDefaultBuiltInType());
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

		assertTrue(dt.toString(), BuiltInType.UNSIGNED_BIG_INTEGER == dt
				.getDefaultBuiltInType());
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

		assertTrue(dt.toString(), BuiltInType.UNSIGNED_LONG == dt
				.getDefaultBuiltInType());
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

		assertTrue(dt.toString(), BuiltInType.UNSIGNED_INTEGER == dt
				.getDefaultBuiltInType());
	}

	public void testNBitInteger1() throws Exception {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='NBit'>"
				+ "    <xs:restriction base='xs:unsignedByte'>"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"NBit", "");

		assertTrue(BuiltInType.NBIT_INTEGER == dt.getDefaultBuiltInType());
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

		assertTrue(BuiltInType.NBIT_BIG_INTEGER == dt.getDefaultBuiltInType());
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

		assertTrue(BuiltInType.NBIT_LONG == dt.getDefaultBuiltInType());
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

		assertTrue(BuiltInType.NBIT_INTEGER == dt.getDefaultBuiltInType());
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

		assertTrue(BuiltInType.ENUMERATION == dt.getDefaultBuiltInType());
	}

	public void testList1() throws Exception {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='ListInt'>"
				+ "    <xs:list itemType='xs:int' />"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"ListInt", "");

		assertTrue(BuiltInType.LIST == dt.getDefaultBuiltInType());

		ListDatatype dtl = (ListDatatype) dt;

		assertTrue(BuiltInType.INTEGER == dtl.getListDatatype()
				.getDefaultBuiltInType());
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

		assertTrue(BuiltInType.RESTRICTED_CHARACTER_SET == dt
				.getDefaultBuiltInType());
	}
	
	public void testRestrictedCharSet2() throws Exception {
		//	uses xs:language but defines *own* pattern facet
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "    <xs:simpleType name='myLanguage'>"
				+ "      <xs:restriction base='xs:language'>"
				+ "      <xs:pattern value='[a-c]'/>"
				+ "      </xs:restriction>"
				+ "    </xs:simpleType>"
				+ "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"myLanguage", "");

		assertTrue(BuiltInType.RESTRICTED_CHARACTER_SET == dt
				.getDefaultBuiltInType());
	}
	
	public void testRestrictedCharSet3() throws Exception {
		//	uses xs:language but defines *own* BUT same pattern facet
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "    <xs:simpleType name='myLanguage'>"
				+ "      <xs:restriction base='xs:language'>"
				+ "      <xs:pattern value='([a-zA-Z]{1,8})(-[a-zA-Z0-9]{1,8})*'/>"
				+ "      </xs:restriction>"
				+ "    </xs:simpleType>"
				+ "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"myLanguage", "");

		assertTrue(BuiltInType.RESTRICTED_CHARACTER_SET == dt
				.getDefaultBuiltInType());
	}
	
	public void testRestrictedCharSet4() throws Exception {
		//	uses indirectly xs:language but defines *own* BUT same pattern facet
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "    <xs:simpleType name='myLanguageParent'>"
				+ "      <xs:restriction base='xs:language'>"
				+ "      <xs:pattern value='([a-zA-Z]{1,8})(-[a-zA-Z0-9]{1,8})*'/>"
				+ "      </xs:restriction>"
				+ "    </xs:simpleType>"
				+ "    <xs:simpleType name='myLanguage'>"
				+ "      <xs:restriction base='myLanguageParent' />"
				+ "    </xs:simpleType>"
				+ "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"myLanguage", "");

		assertTrue(BuiltInType.RESTRICTED_CHARACTER_SET == dt
				.getDefaultBuiltInType());
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

		assertTrue(BuiltInType.STRING == dt.getDefaultBuiltInType());
	}

	public void testNoRestrictedCharSet2() throws Exception {
		/*
		 * NOTE: If the target datatype definition is a definition for a
		 * built-in datatypeXS2, there is no restricted character set for the
		 * string value. 
		 * http://www.w3.org/TR/exi/#restrictedCharSet
		 */
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "    <xs:simpleType name='myLanguage'>"
				+ "      <xs:restriction base='xs:language' />"
				+ "    </xs:simpleType>"
				+ "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"myLanguage", "");

		assertTrue("" + dt.getDefaultBuiltInType(), BuiltInType.STRING == dt.getDefaultBuiltInType());
	}

}