/*
 * Copyright (C) 2007-2009 Siemens AG
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

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;

import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSMultiValueFacet;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTypeDefinition;

import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.datatype.BigIntegerDatatype;
import com.siemens.ct.exi.datatype.BinaryBase64Datatype;
import com.siemens.ct.exi.datatype.BinaryHexDatatype;
import com.siemens.ct.exi.datatype.BooleanDatatype;
import com.siemens.ct.exi.datatype.BooleanPatternDatatype;
import com.siemens.ct.exi.datatype.DatetimeDatatype;
import com.siemens.ct.exi.datatype.DecimalDatatype;
import com.siemens.ct.exi.datatype.DoubleDatatype;
import com.siemens.ct.exi.datatype.EnumerationDatatype;
import com.siemens.ct.exi.datatype.FloatDatatype;
import com.siemens.ct.exi.datatype.IntegerDatatype;
import com.siemens.ct.exi.datatype.ListDatatype;
import com.siemens.ct.exi.datatype.LongDatatype;
import com.siemens.ct.exi.datatype.NBitBigIntegerDatatype;
import com.siemens.ct.exi.datatype.NBitIntegerDatatype;
import com.siemens.ct.exi.datatype.NBitLongDatatype;
import com.siemens.ct.exi.datatype.RestrictedCharacterSetDatatype;
import com.siemens.ct.exi.datatype.StringDatatype;
import com.siemens.ct.exi.datatype.UnsignedBigIntegerDatatype;
import com.siemens.ct.exi.datatype.UnsignedIntegerDatatype;
import com.siemens.ct.exi.datatype.UnsignedLongDatatype;
import com.siemens.ct.exi.datatype.charset.RestrictedCharacterSet;
import com.siemens.ct.exi.datatype.charset.XSDRegularExpression;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.util.ExpandedName;
import com.siemens.ct.exi.util.datatype.DatetimeType;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20090421
 */

public class BuiltIn {
	
	enum IntegerType {
		INT, LONG, BIG_INTEGER
	}
	
	// Binary
	protected static final ExpandedName XSD_BASE64BINARY;
	protected static final ExpandedName XSD_HEXBINARY;
	// Boolean
	public static final ExpandedName XSD_BOOLEAN;
	// Date-Time
	protected static final ExpandedName XSD_DATETIME;
	protected static final ExpandedName XSD_TIME;
	protected static final ExpandedName XSD_DATE;
	protected static final ExpandedName XSD_GYEARMONTH;
	protected static final ExpandedName XSD_GYEAR;
	protected static final ExpandedName XSD_GMONTHDAY;
	protected static final ExpandedName XSD_GDAY;
	protected static final ExpandedName XSD_GMONTH;
	// Decimal
	protected static final ExpandedName XSD_DECIMAL;
	// Float
	protected static final ExpandedName XSD_FLOAT;
	protected static final ExpandedName XSD_DOUBLE;
	// Integer
	protected static final ExpandedName XSD_INTEGER;
	protected static final ExpandedName XSD_NON_NEGATIVE_INTEGER;
	// String
	protected static final ExpandedName XSD_STRING;
	//	
	protected static final ExpandedName XSD_ANY_SIMPLE_TYPE;

	// default QName / BuiltInType / Datatype
	public static final ExpandedName DEFAULT_VALUE_NAME;
	public static final BuiltInType DEFAULT_BUILTIN;
	public static final Datatype DEFAULT_DATATYPE;
	public static final Datatype BOOLEAN_DATATYPE;

	// built-In mapping
	protected static Map<ExpandedName, ExpandedName> datatypeMapping;

	// regular expression parser
	protected static XSDRegularExpression xsdRegexp;

	static {
		/*
		 * Datatype names
		 */
		// Binary
		XSD_BASE64BINARY = new ExpandedName(XMLConstants.W3C_XML_SCHEMA_NS_URI,
				"base64Binary");
		XSD_HEXBINARY = new ExpandedName(XMLConstants.W3C_XML_SCHEMA_NS_URI,
				"hexBinary");
		// Boolean
		XSD_BOOLEAN = new ExpandedName(XMLConstants.W3C_XML_SCHEMA_NS_URI,
				"boolean");
		// Date-Time
		XSD_DATETIME = new ExpandedName(XMLConstants.W3C_XML_SCHEMA_NS_URI,
				"dateTime");
		XSD_TIME = new ExpandedName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "time");
		XSD_DATE = new ExpandedName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "date");
		XSD_GYEARMONTH = new ExpandedName(XMLConstants.W3C_XML_SCHEMA_NS_URI,
				"gYearMonth");
		XSD_GYEAR = new ExpandedName(XMLConstants.W3C_XML_SCHEMA_NS_URI,
				"gYear");
		XSD_GMONTHDAY = new ExpandedName(XMLConstants.W3C_XML_SCHEMA_NS_URI,
				"gMonthDay");
		XSD_GDAY = new ExpandedName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "gDay");
		XSD_GMONTH = new ExpandedName(XMLConstants.W3C_XML_SCHEMA_NS_URI,
				"gMonth");
		// Decimal
		XSD_DECIMAL = new ExpandedName(XMLConstants.W3C_XML_SCHEMA_NS_URI,
				"decimal");
		// Float
		XSD_FLOAT = new ExpandedName(XMLConstants.W3C_XML_SCHEMA_NS_URI,
				"float");
		XSD_DOUBLE = new ExpandedName(XMLConstants.W3C_XML_SCHEMA_NS_URI,
				"double");
		// Integer
		XSD_INTEGER = new ExpandedName(XMLConstants.W3C_XML_SCHEMA_NS_URI,
				"integer");
		XSD_NON_NEGATIVE_INTEGER = new ExpandedName(
				XMLConstants.W3C_XML_SCHEMA_NS_URI, "nonNegativeInteger");
		// String
		XSD_STRING = new ExpandedName(XMLConstants.W3C_XML_SCHEMA_NS_URI,
				"string");
		//	
		XSD_ANY_SIMPLE_TYPE = new ExpandedName(
				XMLConstants.W3C_XML_SCHEMA_NS_URI, "anySimpleType");
		// default
		DEFAULT_VALUE_NAME = XSD_STRING;
		DEFAULT_BUILTIN = BuiltInType.STRING;
		DEFAULT_DATATYPE = new StringDatatype(DEFAULT_VALUE_NAME);
		BOOLEAN_DATATYPE = new StringDatatype(XSD_BOOLEAN);

		/*
		 * Datatype mappings
		 */
		datatypeMapping = new HashMap<ExpandedName, ExpandedName>();
		// Binary
		datatypeMapping.put(XSD_BASE64BINARY, XSD_BASE64BINARY);
		datatypeMapping.put(XSD_HEXBINARY, XSD_HEXBINARY);
		// Boolean
		datatypeMapping.put(XSD_BOOLEAN, XSD_BOOLEAN);
		// Date-Time
		datatypeMapping.put(XSD_DATETIME, XSD_DATETIME);
		datatypeMapping.put(XSD_TIME, XSD_DATETIME);
		datatypeMapping.put(XSD_DATE, XSD_DATETIME);
		datatypeMapping.put(XSD_GYEARMONTH, XSD_DATETIME);
		datatypeMapping.put(XSD_GYEAR, XSD_DATETIME);
		datatypeMapping.put(XSD_GMONTHDAY, XSD_DATETIME);
		datatypeMapping.put(XSD_GDAY, XSD_DATETIME);
		datatypeMapping.put(XSD_GMONTH, XSD_DATETIME);
		// Decimal
		datatypeMapping.put(XSD_DECIMAL, XSD_DECIMAL);
		// Double/Float
		// datatypeMapping.put(XSD_FLOAT, XSD_DOUBLE);
		datatypeMapping.put(XSD_FLOAT, XSD_FLOAT);
		datatypeMapping.put(XSD_DOUBLE, XSD_DOUBLE);
		// Integer
		datatypeMapping.put(XSD_INTEGER, XSD_INTEGER);
		// String
		datatypeMapping.put(XSD_STRING, XSD_STRING);
		// unknown
		datatypeMapping.put(XSD_ANY_SIMPLE_TYPE, XSD_STRING);

		// regular expressions
		xsdRegexp = XSDRegularExpression.newInstance();
	}

	public static Datatype getDatatype(XSSimpleTypeDefinition std)
			throws EXIException {
		Datatype datatype = null;

		// is list ?
		if (std.getVariety() == XSSimpleTypeDefinition.VARIETY_LIST) {
			XSSimpleTypeDefinition listSTD = std.getItemType();

			Datatype dtList = getDatatype(listSTD);

			datatype = new ListDatatype(dtList);
		}
		// is enumeration ?
		else if (std.isDefinedFacet(XSSimpleTypeDefinition.FACET_ENUMERATION)) {
			// datatype = getDatatypeOfEnumeration ( std );
			XSObjectList facetList = std.getMultiValueFacets();
			for (int i = 0; i < facetList.getLength(); i++) {
				XSObject facet = facetList.item(i);
				if (facet.getType() == XSConstants.MULTIVALUE_FACET) {
					XSMultiValueFacet enumer = (XSMultiValueFacet) facet;
					if (enumer.getFacetKind() == XSSimpleTypeDefinition.FACET_ENUMERATION) {
						StringList enumList = enumer.getLexicalFacetValues();
						// // TODO enumeration not type-castable !?
						// ExpandedName datatypeIdentifier = null;
						// BuiltInType enumDatatype = null;
						datatype = new EnumerationDatatype(enumList);
					}
				}
			}
		} else {
			datatype = getDatatypeOfType(std);
		}

		return datatype;
	}

	private static ExpandedName getXMLSchemaDatatype(XSSimpleTypeDefinition std) {
		// primitive
		ExpandedName primitive = getPrimitive(std);

		ExpandedName exiDatatypeID;

		if (primitive.equals(XSD_DECIMAL)) {
			// check whether on the "way up" (nonNegative) integer appears -->
			// (Unsigned)Integer
			XSTypeDefinition xmlSchemaType = std;

			while (xmlSchemaType != null
					&& !(xmlSchemaType.getName() != null && (XSD_INTEGER
							.equals(getName(xmlSchemaType)) || XSD_NON_NEGATIVE_INTEGER
							.equals(getName(xmlSchemaType))))) {
				xmlSchemaType = xmlSchemaType.getBaseType();
			}

			if (xmlSchemaType == null) {
				// xsd:decimal
				exiDatatypeID = XSD_DECIMAL;
			} else {
				// xsd:integer
				exiDatatypeID = XSD_INTEGER;
			}
		} else {
			exiDatatypeID = getBuiltInOfPrimitiveMapping(primitive);
		}

		return exiDatatypeID;
	}

	private static Datatype getIntegerDatatype(XSSimpleTypeDefinition std,
			ExpandedName datatypeID) {
		/*
		 * detect base integer type (e.g. int, long, BigInteger)
		 */
		IntegerType intType;

		// walk up the hierarchy till we find xsd simple integer types
		XSTypeDefinition xsdSTD = std;
		while (!XMLConstants.W3C_XML_SCHEMA_NS_URI
				.equals(xsdSTD.getNamespace())) {
			xsdSTD = xsdSTD.getBaseType();
		}
		// set appropriate integer type
		if (xsdSTD.getName().equals("integer")
				|| xsdSTD.getName().equals("nonPositiveInteger")
				|| xsdSTD.getName().equals("negativeInteger")
				|| xsdSTD.getName().equals("nonNegativeInteger")
				|| xsdSTD.getName().equals("positiveInteger")) {
			// BigInteger
			intType = IntegerType.BIG_INTEGER;
		} else if (xsdSTD.getName().equals("long")
				|| xsdSTD.getName().equals("unsignedLong")
				|| xsdSTD.getName().equals("unsignedInt")) {
			// long
			intType = IntegerType.LONG;
		} else {
			// int
			intType = IntegerType.INT;
		}

		/*
		 * identify lower & upper bound
		 */
		BigInteger min = new BigInteger("-9999999999999999999999999999999999999999");
		BigInteger max = new BigInteger("9999999999999999999999999999999999999999");
		// minimum
		if (std.isDefinedFacet(XSSimpleTypeDefinition.FACET_MININCLUSIVE)) {
			String sMinInclusive = std
					.getLexicalFacetValue(XSSimpleTypeDefinition.FACET_MININCLUSIVE);
			min = min.max(new BigInteger(sMinInclusive));
		}
		if (std.isDefinedFacet(XSSimpleTypeDefinition.FACET_MINEXCLUSIVE)) {
			String sMinExclusive = std
					.getLexicalFacetValue(XSSimpleTypeDefinition.FACET_MINEXCLUSIVE);
			min = min.max((new BigInteger(sMinExclusive)).add(BigInteger.ONE));
		}
		// maximum
		if (std.isDefinedFacet(XSSimpleTypeDefinition.FACET_MAXINCLUSIVE)) {
			String sMaxInclusive = std
					.getLexicalFacetValue(XSSimpleTypeDefinition.FACET_MAXINCLUSIVE);
			max = max.min(new BigInteger(sMaxInclusive));
		}
		if (std.isDefinedFacet(XSSimpleTypeDefinition.FACET_MAXEXCLUSIVE)) {
			String sMaxExclusive = std
					.getLexicalFacetValue(XSSimpleTypeDefinition.FACET_MAXEXCLUSIVE);
			max = max.min((new BigInteger(sMaxExclusive))
					.subtract(BigInteger.ONE));
		}
		// ( max >= min)
		assert (max.compareTo(min) >= 0);

		/*
		 * calculate bounded range;
		 */
		BigInteger boundedRange;
		// max < 0
		if (max.compareTo(BigInteger.ZERO) == -1) {
			// max & min negative
			boundedRange = min.abs().subtract(max.abs()).add(BigInteger.ONE);
		} else {
			// max positive
			if (min.compareTo(BigInteger.ZERO) == -1) {
				// min negative
				boundedRange = max.add(min.abs());
			} else {
				// min positive
				boundedRange = max.abs().subtract(min).add(BigInteger.ONE);
			}
		}

		/*
		 * Set-up appropriate datatype
		 */
		Datatype datatype;

		if (boundedRange.compareTo(BigInteger.valueOf(4095)) <= 0) {
			/*
			 * When the bounded range of integer is 4095 or smaller as
			 * determined by the values of minInclusiveXS2, minExclusiveXS2,
			 * maxInclusiveXS2 and maxExclusiveXS2 facets, use n-bit Unsigned
			 * Integer representation.
			 */
			if (intType == IntegerType.BIG_INTEGER) {
				datatype = new NBitBigIntegerDatatype(datatypeID, min, max,
						boundedRange.intValue());
			} else if (intType == IntegerType.LONG) {
				datatype = new NBitLongDatatype(datatypeID, min.longValue(),
						max.longValue(), boundedRange.intValue());
			} else {
				assert ((intType == IntegerType.INT));
				datatype = new NBitIntegerDatatype(datatypeID, min.intValue(),
						max.intValue(), boundedRange.intValue());
			}
		} else if (min.signum() >= 0) {
			/*
			 * Otherwise, when the integer satisfies one of the followings, use
			 * Unsigned Integer representation.
			 * 
			 * + It is nonNegativeInteger. + Either minInclusiveXS2 facet is
			 * specified with a value equal to or greater than 0, or
			 * minExclusiveXS2 facet is specified with a value equal to or
			 * greater than -1.
			 */
			if (intType == IntegerType.BIG_INTEGER) {
				datatype = new UnsignedBigIntegerDatatype(datatypeID);
			} else if (intType == IntegerType.LONG) {
				datatype = new UnsignedLongDatatype(datatypeID);
			} else {
				assert ((intType == IntegerType.INT));
				datatype = new UnsignedIntegerDatatype(datatypeID);
			}
		} else {
			/*
			 * Otherwise, use Integer representation.
			 */
			if (intType == IntegerType.BIG_INTEGER) {
				datatype = new BigIntegerDatatype(datatypeID);
			} else if (intType == IntegerType.LONG) {
				datatype = new LongDatatype(datatypeID);
			} else {
				assert ((intType == IntegerType.INT));
				datatype = new IntegerDatatype(datatypeID);
			}
		}

		return datatype;
	}

	private static ExpandedName getName(XSTypeDefinition type) {
		return new ExpandedName(type.getNamespace(), type.getName());
	}

	private static Datatype getDatatypeOfType(XSSimpleTypeDefinition std)
			throws EXIException {
		Datatype datatype;

		ExpandedName schemaDatatype = getXMLSchemaDatatype(std);

		ExpandedName datatypeID = null;
		if (!std.getAnonymous()) {
			datatypeID = new ExpandedName(std.getNamespace(), std.getName());
		}

		if (XSD_BASE64BINARY.equals(schemaDatatype)) {
			datatype = new BinaryBase64Datatype(datatypeID);
		} else if (XSD_HEXBINARY.equals(schemaDatatype)) {
			datatype = new BinaryHexDatatype(datatypeID);
		} else if (XSD_BOOLEAN.equals(schemaDatatype)) {
			if (std.isDefinedFacet(XSSimpleTypeDefinition.FACET_PATTERN)) {
				datatype = new BooleanPatternDatatype(datatypeID);
			} else {
				datatype = new BooleanDatatype(datatypeID);
			}
		} else if (XSD_DATETIME.equals(schemaDatatype)) {
			ExpandedName primitive = BuiltIn.getPrimitive(std);

			if (XSD_DATETIME.equals(primitive)) {
				datatype = new DatetimeDatatype(DatetimeType.dateTime,
						datatypeID);
			} else if (XSD_TIME.equals(primitive)) {
				datatype = new DatetimeDatatype(DatetimeType.time, datatypeID);
			} else if (XSD_DATE.equals(primitive)) {
				datatype = new DatetimeDatatype(DatetimeType.date, datatypeID);
			} else if (XSD_GYEARMONTH.equals(primitive)) {
				datatype = new DatetimeDatatype(DatetimeType.gYearMonth,
						datatypeID);
			} else if (XSD_GYEAR.equals(primitive)) {
				datatype = new DatetimeDatatype(DatetimeType.gYear, datatypeID);
			} else if (XSD_GMONTHDAY.equals(primitive)) {
				datatype = new DatetimeDatatype(DatetimeType.gMonthDay,
						datatypeID);
			} else if (XSD_GDAY.equals(primitive)) {
				datatype = new DatetimeDatatype(DatetimeType.gDay, datatypeID);
			} else if (XSD_GMONTH.equals(primitive)) {
				datatype = new DatetimeDatatype(DatetimeType.gMonth, datatypeID);
			} else {
				throw new RuntimeException();
			}
		} else if (XSD_DECIMAL.equals(schemaDatatype)) {
			datatype = new DecimalDatatype(datatypeID);
		} else if (XSD_FLOAT.equals(schemaDatatype)) {
			datatype = new FloatDatatype(datatypeID);
		} else if (XSD_DOUBLE.equals(schemaDatatype)) {
			datatype = new DoubleDatatype(datatypeID);
		} else if (XSD_INTEGER.equals(schemaDatatype)) {
			// returns integer type (nbit, unsigned, int) according to facets
			datatype = BuiltIn.getIntegerDatatype(std, datatypeID);
		} else {
			// XSD_STRING with or without pattern
			if (std.isDefinedFacet(XSSimpleTypeDefinition.FACET_PATTERN)) {
				StringList sl = std.getLexicalPattern();
				if (sl.getLength() > 1) {
					// TODO Multiple patterns
					// System.out.println("ToDo: Multiple patterns for " + std);
					// assert (sl.getLength() == 1); // why multiple ?
				}
				xsdRegexp.analyze(sl.item(0));
				if (xsdRegexp.isEntireSetOfXMLCharacters()) {
					// *normal* string
					datatype = new StringDatatype(datatypeID);
				} else {
					// restricted char set
					RestrictedCharacterSet rcs = xsdRegexp
							.getRestrictedCharacterSet();
					datatype = new RestrictedCharacterSetDatatype(datatypeID,
							rcs);
				}
			} else {
				datatype = new StringDatatype(datatypeID);
			}
		}

		return datatype;
	}

	private static ExpandedName getPrimitive(XSSimpleTypeDefinition std) {
		ExpandedName primitiveQName;
		XSSimpleTypeDefinition primitiveType = std.getPrimitiveType();

		if (primitiveType == null) {
			// TODO correct ?
			primitiveQName = XSD_ANY_SIMPLE_TYPE;
		} else {
			primitiveQName = new ExpandedName(primitiveType.getNamespace(),
					primitiveType.getName());
		}

		return primitiveQName;
	}

	private static ExpandedName getBuiltInOfPrimitiveMapping(
			ExpandedName qnamePrimitive) {
		if (datatypeMapping.containsKey(qnamePrimitive)) {
			return datatypeMapping.get(qnamePrimitive);
		} else {
			return DEFAULT_VALUE_NAME;
		}
	}
	
}
