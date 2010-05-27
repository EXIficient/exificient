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

import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.types.BuiltInType;

public class EnumerationTest extends AbstractTestCase {

	public EnumerationTest(String testName) {
		super(testName);
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

		assertTrue(dt.getDefaultBuiltInType() == BuiltInType.ENUMERATION);
		// EnumerationDatatype enumDt = (EnumerationDatatype) dt;

		assertTrue(dt.isValid("+0"));

		assertFalse(dt.isValid("+4"));
		assertFalse(dt.isValid("-3"));
	}
	
	public void testEnumerationFloat1() throws IOException, EXIException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='Enumeration'>"
				+ "    <xs:restriction base='xs:float'>"
				+ "      <xs:enumeration value='1.5'/>"
				+ "      <xs:enumeration value='25'/>"
				+ "   </xs:restriction>" + "  </xs:simpleType>" + "</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"Enumeration", "");

		assertTrue(dt.getDefaultBuiltInType() == BuiltInType.ENUMERATION);
		// EnumerationDatatype enumDt = (EnumerationDatatype) dt;

		assertTrue(dt.isValid(" 1.5"));
		assertTrue(dt.isValid("15E-1"));
		assertTrue(dt.isValid(" 25 "));
		assertTrue(dt.isValid(" 25E0 "));

		assertFalse(dt.isValid("00"));
		assertFalse(dt.isValid("bla"));
	}

}