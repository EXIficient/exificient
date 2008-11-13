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

import java.io.ByteArrayInputStream;

import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTypeDefinition;

import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.XSDGrammarBuilder;
import com.siemens.ct.exi.util.datatype.DatetimeType;

public class DatatypeMappingTest extends AbstractTestCase
{
	public DatatypeMappingTest ( String testName )
	{
		super ( testName );
	}
	
	protected static Datatype getSimpleDatatypeFor( String schemaAsString, String typeName, String typeURI ) throws EXIException
	{
		XSDGrammarBuilder xsdGB = XSDGrammarBuilder.newInstance ( );
		ByteArrayInputStream bais = new ByteArrayInputStream ( schemaAsString.getBytes ( ) );
		XSModel xsModel = xsdGB.getXSModel ( bais );

		XSTypeDefinition td = xsModel.getTypeDefinition ( typeName, typeURI );

		assertTrue ( "SimpleType expected", td.getTypeCategory ( ) == XSTypeDefinition.SIMPLE_TYPE );

		Datatype dt = BuiltIn.getDatatype ( (XSSimpleTypeDefinition) td );
		
		return dt;
	}

	public void testBinary1 () throws Exception
	{
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>" +
		"  <xs:simpleType name='Binary'>" +
		"    <xs:restriction base='xs:base64Binary'>" +
		"    </xs:restriction>" + 
		"  </xs:simpleType>" +
		"</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor ( schemaAsString, "Binary", "" );

		assertTrue ( BuiltInType.BUILTIN_BINARY_BASE64 == dt.getDefaultBuiltInType ( ) );
		assertTrue ( BuiltIn.XSD_BASE64BINARY == dt.getDatatypeIdentifier ( ));
	}
	
	public void testBinary2 () throws Exception
	{
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>" +
		"  <xs:simpleType name='Binary'>" +
		"    <xs:restriction base='xs:hexBinary'>" +
		"    </xs:restriction>" + 
		"  </xs:simpleType>" +
		"</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor ( schemaAsString, "Binary", "" );

		assertTrue ( BuiltInType.BUILTIN_BINARY_HEX == dt.getDefaultBuiltInType ( ) );
		assertTrue ( BuiltIn.XSD_HEXBINARY == dt.getDatatypeIdentifier ( ));
	}
	
	
	public void testBooleanNoFacet () throws Exception
	{
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>" +
		"  <xs:simpleType name='Boolean'>" +
		"    <xs:restriction base='xs:boolean'>" +
		"    </xs:restriction>" + 
		"  </xs:simpleType>" +
		"</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor ( schemaAsString, "Boolean", "" );

		assertTrue ( BuiltInType.BUILTIN_BOOLEAN == dt.getDefaultBuiltInType ( ) );
		assertTrue ( BuiltIn.XSD_BOOLEAN == dt.getDatatypeIdentifier ( ));
	}

	public void testBooleanFacet1 () throws Exception
	{
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>" +
		"  <xs:simpleType name='Boolean01'>" +
		"    <xs:restriction base='xs:boolean'>" +
		"      <xs:pattern value='0' />" +
		"      <xs:pattern value='1' />" +
		"    </xs:restriction>" +
		"  </xs:simpleType>" +
		"</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor ( schemaAsString, "Boolean01", "" );

		assertTrue ( BuiltInType.BUILTIN_BOOLEAN_PATTERN == dt.getDefaultBuiltInType ( ) );
		assertTrue ( BuiltIn.XSD_BOOLEAN == dt.getDatatypeIdentifier ( ));
	}
	
	public void testDateTime1 () throws Exception
	{
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>" +
		"  <xs:simpleType name='DateTime'>" +
		"    <xs:restriction base='xs:dateTime'>" +
		"    </xs:restriction>" +
		"  </xs:simpleType>" +
		"</xs:schema>";
		
		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor ( schemaAsString, "DateTime", "" );

		assertTrue ( BuiltInType.BUILTIN_DATETIME == dt.getDefaultBuiltInType ( ) );
		assertTrue ( BuiltIn.XSD_DATETIME == dt.getDatatypeIdentifier ( ));
		
		DatatypeDatetime dtd = (DatatypeDatetime)dt;
		assertTrue ( DatetimeType.dateTime == dtd.getDatetimeType ( ) );	
	}
	
	
	public void testDateTime2 () throws Exception
	{
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>" +
		"  <xs:simpleType name='DateTime'>" +
		"    <xs:restriction base='xs:gDay'>" +
		"    </xs:restriction>" +
		"  </xs:simpleType>" +
		"</xs:schema>";
		
		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor ( schemaAsString, "DateTime", "" );

		assertTrue ( BuiltInType.BUILTIN_DATETIME == dt.getDefaultBuiltInType ( ) );
		assertTrue ( BuiltIn.XSD_DATETIME == dt.getDatatypeIdentifier ( ));
		
		DatatypeDatetime dtd = (DatatypeDatetime)dt;
		assertTrue ( DatetimeType.gDay == dtd.getDatetimeType ( ) );	
	}
	
	public void testFloat1 () throws Exception
	{
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>" +
		"  <xs:simpleType name='Float'>" +
		"    <xs:restriction base='xs:float'>" +
		"    </xs:restriction>" +
		"  </xs:simpleType>" +
		"</xs:schema>";
		
		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor ( schemaAsString, "Float", "" );

		assertTrue ( BuiltInType.BUILTIN_FLOAT == dt.getDefaultBuiltInType ( ) );
		assertTrue ( BuiltIn.XSD_DOUBLE == dt.getDatatypeIdentifier ( ));
	}
	
	public void testFloat2 () throws Exception
	{
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>" +
		"  <xs:simpleType name='Float'>" +
		"    <xs:restriction base='xs:double'>" +
		"    </xs:restriction>" +
		"  </xs:simpleType>" +
		"</xs:schema>";
		
		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor ( schemaAsString, "Float", "" );
		
		assertTrue ( BuiltInType.BUILTIN_FLOAT == dt.getDefaultBuiltInType ( ) );
		assertTrue ( BuiltIn.XSD_DOUBLE == dt.getDatatypeIdentifier ( ));
	}
	
	public void testInteger1 () throws Exception
	{
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>" +
		"  <xs:simpleType name='Integer'>" +
		"    <xs:restriction base='xs:integer'>" + 
		"    </xs:restriction>" +
		"  </xs:simpleType>" +
		"</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor ( schemaAsString, "Integer", "" );

		assertTrue ( BuiltInType.BUILTIN_INTEGER == dt.getDefaultBuiltInType ( ) );
		assertTrue ( BuiltIn.XSD_INTEGER == dt.getDatatypeIdentifier ( ));
	}
	
	public void testInteger2 () throws Exception
	{
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>" +
		"  <xs:simpleType name='Integer'>" +
		"    <xs:restriction base='xs:int'>" + 
		"    </xs:restriction>" +
		"  </xs:simpleType>" +
		"</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor ( schemaAsString, "Integer", "" );

		assertTrue ( BuiltInType.BUILTIN_INTEGER == dt.getDefaultBuiltInType ( ) );
		assertTrue ( BuiltIn.XSD_INTEGER == dt.getDatatypeIdentifier ( ));
	}
	
	public void testInteger3 () throws Exception
	{
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>" +
		"  <xs:simpleType name='Integer'>" +
		"    <xs:restriction base='xs:negativeInteger'>" + 
		"    </xs:restriction>" +
		"  </xs:simpleType>" +
		"</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor ( schemaAsString, "Integer", "" );

		assertTrue ( BuiltInType.BUILTIN_INTEGER == dt.getDefaultBuiltInType ( ) );
		assertTrue ( BuiltIn.XSD_INTEGER == dt.getDatatypeIdentifier ( ));
	}
	
	public void testUnsignedInteger1 () throws Exception
	{
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>" +
		"  <xs:simpleType name='UnsignedInteger'>" +
		"    <xs:restriction base='xs:nonNegativeInteger'>" + 
		"    </xs:restriction>" +
		"  </xs:simpleType>" +
		"</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor ( schemaAsString, "UnsignedInteger", "" );

		assertTrue ( BuiltInType.BUILTIN_UNSIGNED_INTEGER == dt.getDefaultBuiltInType ( ) );
		assertTrue ( BuiltIn.XSD_INTEGER == dt.getDatatypeIdentifier ( ));
	}
	
	public void testUnsignedIntegerFacet1 () throws Exception
	{
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>" +
		"  <xs:simpleType name='UnsignedInteger'>" +
		"    <xs:restriction base='xs:integer'>" + 
		"        <xs:minInclusive value='250' />" + 
		"    </xs:restriction>" +
		"  </xs:simpleType>" +
		"</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor ( schemaAsString, "UnsignedInteger", "" );

		assertTrue ( BuiltInType.BUILTIN_UNSIGNED_INTEGER == dt.getDefaultBuiltInType ( ) );
		assertTrue ( BuiltIn.XSD_INTEGER == dt.getDatatypeIdentifier ( ));
	}
	
	
	public void testNBitInteger1 () throws Exception
	{
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>" +
		"  <xs:simpleType name='NBit'>" +
		"    <xs:restriction base='xs:unsignedByte'>" + 
		"    </xs:restriction>" +
		"  </xs:simpleType>" +
		"</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor ( schemaAsString, "NBit", "" );

		assertTrue ( BuiltInType.BUILTIN_NBIT_INTEGER == dt.getDefaultBuiltInType ( ) );
		assertTrue ( BuiltIn.XSD_INTEGER == dt.getDatatypeIdentifier ( ));
	}


	public void testNBitIntegerFacet1 () throws Exception
	{
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>" +
		"  <xs:simpleType name='NBit'>" +
		"    <xs:restriction base='xs:integer'>" + 
		"      <xs:minInclusive value='2' />" + 
		"      <xs:maxExclusive value='10'/>" + 
		"    </xs:restriction>" +
		"  </xs:simpleType>" +
		"</xs:schema>";

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor ( schemaAsString, "NBit", "" );

		assertTrue ( BuiltInType.BUILTIN_NBIT_INTEGER == dt.getDefaultBuiltInType ( ) );
		assertTrue ( BuiltIn.XSD_INTEGER == dt.getDatatypeIdentifier ( ));
	}
	
	public void testEnumeration1 () throws Exception
	{
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>" +
		"  <xs:simpleType name='Enumeration'>" +
		"    <xs:restriction base='xs:string'>" +
		"      <xs:enumeration value='Jan'/>" + 
		"      <xs:enumeration value='Feb'/>" + 
		//	et cetera
		"    </xs:restriction>" + 
		"  </xs:simpleType>" +
		"</xs:schema>";
		
		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor ( schemaAsString, "Enumeration", "" );

		assertTrue ( BuiltInType.BUILTIN_ENUMERATION == dt.getDefaultBuiltInType ( ) );
	}
	
	public void testList1 () throws Exception
	{
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>" +
		"  <xs:simpleType name='ListInt'>" +
		"    <xs:list itemType='xs:int' />" +
		"  </xs:simpleType>" +
		"</xs:schema>";
		
		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor ( schemaAsString, "ListInt", "" );

		assertTrue ( BuiltInType.BUILTIN_LIST == dt.getDefaultBuiltInType ( ) );
		
		DatatypeList dtl = (DatatypeList)dt;
		
		assertTrue ( BuiltInType.BUILTIN_INTEGER == dtl.getListDatatype ( ).getDefaultBuiltInType ( ) );
	}
}