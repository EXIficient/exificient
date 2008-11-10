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

import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;

import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSFacet;
import org.apache.xerces.xs.XSMultiValueFacet;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTypeDefinition;

import com.siemens.ct.exi.util.ExpandedName;
import com.siemens.ct.exi.util.datatype.DatetimeType;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20081110
 */

public class BuiltIn
{
	// Binary
	protected static final ExpandedName				XSD_BASE64BINARY;
	protected static final ExpandedName				XSD_HEXBINARY;
	// Boolean
	public static final ExpandedName				XSD_BOOLEAN;
	// Date-Time
	protected static final ExpandedName				XSD_DATETIME;
	protected static final ExpandedName				XSD_TIME;
	protected static final ExpandedName				XSD_DATE;
	protected static final ExpandedName				XSD_GYEARMONTH;
	protected static final ExpandedName				XSD_GYEAR;
	protected static final ExpandedName				XSD_GMONTHDAY;
	protected static final ExpandedName				XSD_GDAY;
	protected static final ExpandedName				XSD_GMONTH;
	// Decimal
	protected static final ExpandedName				XSD_DECIMAL;
	// Float
	protected static final ExpandedName				XSD_FLOAT;
	protected static final ExpandedName				XSD_DOUBLE;
	// Integer
	protected static final ExpandedName				XSD_INTEGER;
	protected static final ExpandedName				XSD_NON_NEGATIVE_INTEGER;
	// String
	protected static final ExpandedName				XSD_STRING;
	//	
	protected static final ExpandedName				XSD_ANY_SIMPLE_TYPE;

	// default QName / BuiltInType / Datatype
	public static final ExpandedName				DEFAULT_VALUE_NAME;
	public static final BuiltInType					DEFAULT_BUILTIN;
	public static final Datatype					DEFAULT_DATATYPE;
	public static final Datatype					BOOLEAN_DATATYPE;

	// built-In mapping
	private static Map<ExpandedName, BuiltInType>	datatypeMapping;

	static
	{
		/*
		 * Datatype names
		 */
		// Binary
		XSD_BASE64BINARY = new ExpandedName ( XMLConstants.W3C_XML_SCHEMA_NS_URI, "base64Binary" );
		XSD_HEXBINARY = new ExpandedName ( XMLConstants.W3C_XML_SCHEMA_NS_URI, "hexBinary" );
		// Boolean
		XSD_BOOLEAN = new ExpandedName ( XMLConstants.W3C_XML_SCHEMA_NS_URI, "boolean" );
		// Date-Time
		XSD_DATETIME = new ExpandedName ( XMLConstants.W3C_XML_SCHEMA_NS_URI, "dateTime" );
		XSD_TIME = new ExpandedName ( XMLConstants.W3C_XML_SCHEMA_NS_URI, "time" );
		XSD_DATE = new ExpandedName ( XMLConstants.W3C_XML_SCHEMA_NS_URI, "date" );
		XSD_GYEARMONTH = new ExpandedName ( XMLConstants.W3C_XML_SCHEMA_NS_URI, "gYearMonth" );
		XSD_GYEAR = new ExpandedName ( XMLConstants.W3C_XML_SCHEMA_NS_URI, "gYear" );
		XSD_GMONTHDAY = new ExpandedName ( XMLConstants.W3C_XML_SCHEMA_NS_URI, "gMonthDay" );
		XSD_GDAY = new ExpandedName ( XMLConstants.W3C_XML_SCHEMA_NS_URI, "gDay" );
		XSD_GMONTH = new ExpandedName ( XMLConstants.W3C_XML_SCHEMA_NS_URI, "gMonth" );
		// Decimal
		XSD_DECIMAL = new ExpandedName ( XMLConstants.W3C_XML_SCHEMA_NS_URI, "decimal" );
		// Float
		XSD_FLOAT = new ExpandedName ( XMLConstants.W3C_XML_SCHEMA_NS_URI, "float" );
		XSD_DOUBLE = new ExpandedName ( XMLConstants.W3C_XML_SCHEMA_NS_URI, "double" );
		// Integer
		XSD_INTEGER = new ExpandedName ( XMLConstants.W3C_XML_SCHEMA_NS_URI, "integer" );
		XSD_NON_NEGATIVE_INTEGER = new ExpandedName ( XMLConstants.W3C_XML_SCHEMA_NS_URI, "nonNegativeInteger" );
		// String
		XSD_STRING = new ExpandedName ( XMLConstants.W3C_XML_SCHEMA_NS_URI, "string" );
		//	
		XSD_ANY_SIMPLE_TYPE = new ExpandedName ( XMLConstants.W3C_XML_SCHEMA_NS_URI, "anySimpleType" );
		// default
		DEFAULT_VALUE_NAME = XSD_STRING;
		DEFAULT_BUILTIN = BuiltInType.BUILTIN_STRING;
		DEFAULT_DATATYPE = new DatatypeString ( DEFAULT_VALUE_NAME );
		BOOLEAN_DATATYPE = new DatatypeString ( XSD_BOOLEAN );

		/*
		 * Datatype mappings
		 */
		datatypeMapping = new HashMap<ExpandedName, BuiltInType> ( );
		// Binary
		datatypeMapping.put ( XSD_BASE64BINARY, BuiltInType.BUILTIN_BINARY );
		datatypeMapping.put ( XSD_HEXBINARY, BuiltInType.BUILTIN_BINARY );
		// Boolean
		datatypeMapping.put ( XSD_BOOLEAN, BuiltInType.BUILTIN_BOOLEAN );
		// Date-Time
		datatypeMapping.put ( XSD_DATETIME, BuiltInType.BUILTIN_DATETIME );
		datatypeMapping.put ( XSD_TIME, BuiltInType.BUILTIN_DATETIME );
		datatypeMapping.put ( XSD_DATE, BuiltInType.BUILTIN_DATETIME );
		datatypeMapping.put ( XSD_GYEARMONTH, BuiltInType.BUILTIN_DATETIME );
		datatypeMapping.put ( XSD_GYEAR, BuiltInType.BUILTIN_DATETIME );
		datatypeMapping.put ( XSD_GMONTHDAY, BuiltInType.BUILTIN_DATETIME );
		datatypeMapping.put ( XSD_GDAY, BuiltInType.BUILTIN_DATETIME );
		datatypeMapping.put ( XSD_GMONTH, BuiltInType.BUILTIN_DATETIME );
		// Decimal
		datatypeMapping.put ( XSD_DECIMAL, BuiltInType.BUILTIN_DECIMAL );
		// Double/Float
		datatypeMapping.put ( XSD_FLOAT, BuiltInType.BUILTIN_FLOAT );
		datatypeMapping.put ( XSD_DOUBLE, BuiltInType.BUILTIN_FLOAT );
		// Integer
		datatypeMapping.put ( XSD_INTEGER, BuiltInType.BUILTIN_INTEGER );
		datatypeMapping.put ( XSD_NON_NEGATIVE_INTEGER, BuiltInType.BUILTIN_UNSIGNED_INTEGER );
		// String
		datatypeMapping.put ( XSD_STRING, DEFAULT_BUILTIN );
		// unknown
		datatypeMapping.put ( XSD_ANY_SIMPLE_TYPE, DEFAULT_BUILTIN );
	}

	public static Datatype getDatatype ( XSSimpleTypeDefinition std )
	{
		Datatype datatype;

		// is list ?
		if ( std.getVariety ( ) == XSSimpleTypeDefinition.VARIETY_LIST )
		{
			XSSimpleTypeDefinition listSTD = std.getItemType ( );

			Datatype dtList = getDatatype ( listSTD );

			datatype = new DatatypeList ( dtList, getDatatypeIdentifier ( listSTD ) );
		}
		// is enumeration ?
		else if ( std.isDefinedFacet ( XSSimpleTypeDefinition.FACET_ENUMERATION ) )
		{
			datatype = getEnumerationDatatype ( std );
		}
		else
		{
			datatype = getPrimitiveDatatype ( std );
		}

		return datatype;
	}

	private static BuiltInType getBuiltInDatatype ( XSSimpleTypeDefinition std, ExpandedName primitive )
	{
		// built-in type
		BuiltInType builtIn;

		if ( primitive.equals ( XSD_DECIMAL ) )
		{
			// check whether on the "way up" (nonNegative) integer appears -->
			// (Unsigned)Integer
			XSTypeDefinition xmlSchemaType = std;

			while ( xmlSchemaType != null
					&& ! ( xmlSchemaType.getName ( ) != null && ( XSD_INTEGER.equals ( getName ( xmlSchemaType ) ) || XSD_NON_NEGATIVE_INTEGER
							.equals ( getName ( xmlSchemaType ) ) ) ) )
			{
				xmlSchemaType = xmlSchemaType.getBaseType ( );
			}

			if ( xmlSchemaType == null )
			{
				builtIn = BuiltInType.BUILTIN_DECIMAL;
			}
			else if ( XSD_INTEGER.equals ( getName ( xmlSchemaType ) ) )
			{
				// facets ? (integer with minInclusive or minExclusive facet
				// value of 0 or above)
				if ( isUnsignedIntegerFacet ( std ) )
				{
					builtIn = BuiltInType.BUILTIN_UNSIGNED_INTEGER;
				}
				else
				{
					builtIn = BuiltInType.BUILTIN_INTEGER;
				}
			}
			else if ( XSD_NON_NEGATIVE_INTEGER.equals ( getName ( xmlSchemaType ) ) )
			{
				builtIn = BuiltInType.BUILTIN_UNSIGNED_INTEGER;
			}
			else
			{
				throw new RuntimeException ( );
			}
		}
		else if ( primitive.equals ( XSD_BOOLEAN ) && std.isDefinedFacet ( XSSimpleTypeDefinition.FACET_PATTERN ) )
		{
			builtIn = BuiltInType.BUILTIN_BOOLEAN_PATTERN;
		}
		else
		{
			builtIn = getBuiltInOfPrimitiveMapping ( primitive );
		}

		return builtIn;
	}

	private static boolean isUnsignedIntegerFacet ( XSSimpleTypeDefinition std )
	{
		boolean isUnsigned = false;

		// facets ? (integer with minInclusive or minExclusive facet value of 0
		// or above)
		if ( std.isDefinedFacet ( XSSimpleTypeDefinition.FACET_MININCLUSIVE )
				|| std.isDefinedFacet ( XSSimpleTypeDefinition.FACET_MINEXCLUSIVE ) )
		{
			XSObjectList facets = std.getFacets ( );

			for ( int i = 0; i < facets.getLength ( ); i++ )
			{
				XSFacet facet = (XSFacet) facets.item ( i );

				if ( facet.getFacetKind ( ) == XSSimpleTypeDefinition.FACET_MININCLUSIVE )
				{
					String sValueIncl = facet.getLexicalFacetValue ( );
					// if ( Integer.parseInt ( sValueIncl ) >= 0 )
					if ( Long.parseLong ( sValueIncl ) >= 0 )
					{
						isUnsigned = true;
					}
				}
				if ( facet.getFacetKind ( ) == XSSimpleTypeDefinition.FACET_MINEXCLUSIVE )
				{
					String sValueExcl = facet.getLexicalFacetValue ( );
					// if ( Integer.parseInt ( sValueExcl ) >= 0 )
					if ( Long.parseLong ( sValueExcl ) >= 0 )
					{
						isUnsigned = true;
					}
					// System.out.println ( sValue );
				}
			}
		}

		return isUnsigned;
	}

	private static ExpandedName getName ( XSTypeDefinition type )
	{
		return new ExpandedName ( type.getNamespace ( ), type.getName ( ) );
	}

	private static Datatype getPrimitiveDatatype ( XSSimpleTypeDefinition std )
	{
		// primitive
		ExpandedName primitive = getPrimitive ( std );

		// builtIn type
		BuiltInType builtIn = getBuiltInDatatype ( std, primitive );

		// datatype identifier
		ExpandedName datatypeIdentifier = getDatatypeIdentifier ( std );

		Datatype datatype;

		switch ( builtIn )
		{
			case BUILTIN_BINARY:
				datatype = new DatatypeBinary ( datatypeIdentifier );
				break;
			case BUILTIN_BOOLEAN:
				datatype = new DatatypeBoolean ( datatypeIdentifier );
				break;
			case BUILTIN_BOOLEAN_PATTERN:
				datatype = new DatatypeBooleanPattern ( datatypeIdentifier );
				break;
			case BUILTIN_DATETIME:
				if ( XSD_DATETIME.equals ( primitive ) )
				{
					datatype = new DatatypeDatetime ( DatetimeType.dateTime, datatypeIdentifier );
				}
				else if ( XSD_TIME.equals ( primitive ) )
				{
					datatype = new DatatypeDatetime ( DatetimeType.time, datatypeIdentifier );
				}
				else if ( XSD_DATE.equals ( primitive ) )
				{
					datatype = new DatatypeDatetime ( DatetimeType.date, datatypeIdentifier );
				}
				else if ( XSD_GYEARMONTH.equals ( primitive ) )
				{
					datatype = new DatatypeDatetime ( DatetimeType.gYearMonth, datatypeIdentifier );
				}
				else if ( XSD_GYEAR.equals ( primitive ) )
				{
					datatype = new DatatypeDatetime ( DatetimeType.gYear, datatypeIdentifier );
				}
				else if ( XSD_GMONTHDAY.equals ( primitive ) )
				{
					datatype = new DatatypeDatetime ( DatetimeType.gMonthDay, datatypeIdentifier );
				}
				else if ( XSD_GDAY.equals ( primitive ) )
				{
					datatype = new DatatypeDatetime ( DatetimeType.gDay, datatypeIdentifier );
				}
				else if ( XSD_GMONTH.equals ( primitive ) )
				{
					datatype = new DatatypeDatetime ( DatetimeType.gMonth, datatypeIdentifier );
				}
				else
				{
					throw new RuntimeException ( );
				}
				break;
			case BUILTIN_DECIMAL:
				datatype = new DatatypeDecimal ( datatypeIdentifier );
				break;
			case BUILTIN_FLOAT:
				datatype = new DatatypeFloat ( datatypeIdentifier );
				break;
			case BUILTIN_INTEGER:
				datatype = new DatatypeInteger ( datatypeIdentifier );
				break;
			case BUILTIN_UNSIGNED_INTEGER:
				datatype = new DatatypeUnsignedInteger ( datatypeIdentifier );
				break;
			case BUILTIN_STRING:
				datatype = new DatatypeString ( datatypeIdentifier );
				break;
			default:
				throw new RuntimeException ( );
		}

		return datatype;
	}

	private static Datatype getEnumerationDatatype ( XSSimpleTypeDefinition std )
	{
		// primitive
		ExpandedName primitive = BuiltIn.getPrimitive ( std );

		// enum datatype
		BuiltInType enumDatatype = BuiltIn.getBuiltInDatatype ( std, primitive );

		// Enumeration Value List
		StringList enumList = null;
		if ( std.isDefinedFacet ( XSSimpleTypeDefinition.FACET_ENUMERATION ) )
		{
			XSObjectList facetList = std.getMultiValueFacets ( );
			for ( int i = 0; i < facetList.getLength ( ); i++ )
			{
				XSObject facet = facetList.item ( i );
				if ( facet.getType ( ) == XSConstants.MULTIVALUE_FACET )
				{
					XSMultiValueFacet enumer = (XSMultiValueFacet) facet;
					if ( enumer.getFacetKind ( ) == XSSimpleTypeDefinition.FACET_ENUMERATION )
					{
						enumList = enumer.getLexicalFacetValues ( );
					}
				}
			}
		}

		if ( enumList == null )
		{
			throw new RuntimeException ( "Enumeration value list problem for " + std );
		}

		// datatype identifier
		ExpandedName datatypeIdentifier = getDatatypeIdentifier ( std );

		return new DatatypeEnumeration ( enumDatatype, enumList, datatypeIdentifier );
	}

	private static ExpandedName getDatatypeIdentifier ( XSSimpleTypeDefinition std )
	{
		if ( std.getAnonymous ( ) )
		{
			// null,#AnonType_bla
			String s = std.toString ( );
			String localName = s.substring ( s.indexOf ( ',' ) + 1 );
			return new ExpandedName ( std.getNamespace ( ), localName );
		}
		else
		{
			return new ExpandedName ( std.getNamespace ( ), std.getName ( ) );
		}

	}

	private static ExpandedName getPrimitive ( XSSimpleTypeDefinition std )
	{
		ExpandedName primitiveQName;
		XSSimpleTypeDefinition primitiveType = std.getPrimitiveType ( );

		if ( primitiveType == null )
		{
			// TODO correct ?
			primitiveQName = XSD_ANY_SIMPLE_TYPE;
		}
		else
		{
			primitiveQName = new ExpandedName ( primitiveType.getNamespace ( ), primitiveType.getName ( ) );
		}

		return primitiveQName;
	}

	private static BuiltInType getBuiltInOfPrimitiveMapping ( ExpandedName qnamePrimitive )
	{
		if ( datatypeMapping.containsKey ( qnamePrimitive ) )
		{
			return datatypeMapping.get ( qnamePrimitive );
		}
		else
		{
			return BuiltIn.DEFAULT_BUILTIN;
		}
	}
}
