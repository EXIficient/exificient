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

package com.siemens.ct.exi.types;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.xerces.impl.xpath.regex.EXIRegularExpression;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSMultiValueFacet;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTypeDefinition;

import com.siemens.ct.exi.datatype.BigIntegerDatatype;
import com.siemens.ct.exi.datatype.BinaryBase64Datatype;
import com.siemens.ct.exi.datatype.BinaryHexDatatype;
import com.siemens.ct.exi.datatype.BooleanDatatype;
import com.siemens.ct.exi.datatype.BooleanPatternDatatype;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.datatype.DatetimeDatatype;
import com.siemens.ct.exi.datatype.DecimalDatatype;
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
import com.siemens.ct.exi.datatype.charset.CodePointCharacterSet;
import com.siemens.ct.exi.datatype.charset.RestrictedCharacterSet;
import com.siemens.ct.exi.values.DateTimeType;
import com.siemens.ct.exi.values.Value;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.5
 */

public class BuiltIn {

	public static final int MAX_BOUNDED_NBIT_INTEGER_RANGE = 4096;

	/*
	 * Binary
	 */
	public static final QName XSD_BASE64BINARY = new QName(
			XMLConstants.W3C_XML_SCHEMA_NS_URI, "base64Binary");
	public static final QName XSD_HEXBINARY = new QName(
			XMLConstants.W3C_XML_SCHEMA_NS_URI, "hexBinary");
	/*
	 * Boolean
	 */
	public static final QName XSD_BOOLEAN = new QName(
			XMLConstants.W3C_XML_SCHEMA_NS_URI, "boolean");
	/*
	 * Date-Time
	 */
	public static final QName XSD_DATETIME = new QName(
			XMLConstants.W3C_XML_SCHEMA_NS_URI, "dateTime");
	public static final QName XSD_TIME = new QName(
			XMLConstants.W3C_XML_SCHEMA_NS_URI, "time");
	public static final QName XSD_DATE = new QName(
			XMLConstants.W3C_XML_SCHEMA_NS_URI, "date");
	public static final QName XSD_GYEARMONTH = new QName(
			XMLConstants.W3C_XML_SCHEMA_NS_URI, "gYearMonth");
	public static final QName XSD_GYEAR = new QName(
			XMLConstants.W3C_XML_SCHEMA_NS_URI, "gYear");
	public static final QName XSD_GMONTHDAY = new QName(
			XMLConstants.W3C_XML_SCHEMA_NS_URI, "gMonthDay");
	public static final QName XSD_GDAY = new QName(
			XMLConstants.W3C_XML_SCHEMA_NS_URI, "gDay");
	public static final QName XSD_GMONTH = new QName(
			XMLConstants.W3C_XML_SCHEMA_NS_URI, "gMonth");

	/*
	 * Decimal
	 */
	public static final QName XSD_DECIMAL = new QName(
			XMLConstants.W3C_XML_SCHEMA_NS_URI, "decimal");
	/*
	 * Float
	 */
	public static final QName XSD_FLOAT = new QName(
			XMLConstants.W3C_XML_SCHEMA_NS_URI, "float");
	public static final QName XSD_DOUBLE = new QName(
			XMLConstants.W3C_XML_SCHEMA_NS_URI, "double");
	/*
	 * Integer
	 */
	public static final QName XSD_INTEGER = new QName(
			XMLConstants.W3C_XML_SCHEMA_NS_URI, "integer");
	protected static final QName XSD_NON_NEGATIVE_INTEGER = new QName(
			XMLConstants.W3C_XML_SCHEMA_NS_URI, "nonNegativeInteger");
	/*
	 * String
	 */
	public static final QName XSD_STRING = new QName(
			XMLConstants.W3C_XML_SCHEMA_NS_URI, "string");
	//	
	public static final QName XSD_ANY_SIMPLE_TYPE = new QName(
			XMLConstants.W3C_XML_SCHEMA_NS_URI, "anySimpleType");

	/*
	 * Misc
	 */
	protected static final QName XSD_QNAME = new QName(
			XMLConstants.W3C_XML_SCHEMA_NS_URI, "QName");
	protected static final QName XSD_NOTATION = new QName(
			XMLConstants.W3C_XML_SCHEMA_NS_URI, "Notation");
	
	/*
	 * default QName / BuiltInType / Datatype
	 */
	public static final QName DEFAULT_VALUE_NAME = XSD_STRING;
	public static final BuiltInType DEFAULT_BUILTIN = BuiltInType.STRING;
	public static final Datatype DEFAULT_DATATYPE = new StringDatatype(
			DEFAULT_VALUE_NAME);
	public static final Datatype BOOLEAN_DATATYPE = new BooleanDatatype(
			XSD_BOOLEAN);

	// built-In mapping
	protected static final Map<QName, QName> datatypeMapping;

	/*
	 * Datatype mappings
	 */
	static {
		datatypeMapping = new HashMap<QName, QName>();
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
		datatypeMapping.put(XSD_FLOAT, XSD_FLOAT);
		datatypeMapping.put(XSD_DOUBLE, XSD_DOUBLE);
		// Integer
		datatypeMapping.put(XSD_INTEGER, XSD_INTEGER);
		// String
		datatypeMapping.put(XSD_STRING, XSD_STRING);
		// unknown
		datatypeMapping.put(XSD_ANY_SIMPLE_TYPE, XSD_STRING);
	}

	public static Datatype getDatatype(XSSimpleTypeDefinition std)
 {
		Datatype datatype = null;

		// used for dtr map
		QName schemaType = getSchemaType(std);

		// is list ?
		if (std.getVariety() == XSSimpleTypeDefinition.VARIETY_LIST) {
			XSSimpleTypeDefinition listSTD = std.getItemType();

			Datatype dtList = getDatatype(listSTD);

			datatype = new ListDatatype(dtList, schemaType);
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
						// TODO enumeration not type-castable !?
						XSSimpleTypeDefinition stdEnum = (XSSimpleTypeDefinition) std
								.getBaseType();

						// Value[] values = new Value[enumList.getLength()];

						/*
						 * Exceptions are for schema types derived from others
						 * by union and their subtypes, QName or Notation and
						 * types derived therefrom by restriction. The values of
						 * such types are processed by their respective built-in
						 * EXI datatype representations instead of being
						 * represented as enumerations.
						 */

						if (stdEnum.getVariety() == XSSimpleTypeDefinition.VARIETY_UNION) {
							datatype = new StringDatatype(schemaType);
							// XSObjectList unionMemberTypes = stdEnum
							// .getMemberTypes();
							//
							// for (int k = 0; k < enumList.getLength(); k++) {
							// String sEnumValue = enumList.item(k);
							//
							// Datatype dtEnumValues = null;
							// boolean valid = false;
							// int j = 0;
							//
							// while (!valid
							// && j < unionMemberTypes.getLength()) {
							// XSSimpleTypeDefinition stdEnumUnion =
							// (XSSimpleTypeDefinition) unionMemberTypes
							// .item(j);
							// dtEnumValues = getDatatype(stdEnumUnion);
							// valid = dtEnumValues.isValid(sEnumValue);
							//
							// j++;
							// }
							//
							// if (!valid) {
							// throw new RuntimeException(
							// "No valid enumeration value '"
							// + sEnumValue + "', "
							// + stdEnum);
							// }
							// values[k] = dtEnumValues.getValue();
							// }
						} else if ( XSD_QNAME.equals(getSchemaType(stdEnum)) || XSD_NOTATION.equals(getSchemaType(stdEnum)) ) {
							datatype = new StringDatatype(schemaType);
						} else {

							Datatype dtEnumValues = getDatatype(stdEnum);
							Value[] values = new Value[enumList.getLength()];

							for (int k = 0; k < enumList.getLength(); k++) {
								String sEnumValue = enumList.item(k);
								boolean valid = dtEnumValues
										.isValid(sEnumValue);
								if (!valid) {
									throw new RuntimeException(
											"No valid enumeration value '"
													+ sEnumValue + "', "
													+ stdEnum);
								}
								values[k] = dtEnumValues.getValue();
							}

							datatype = new EnumerationDatatype(values, dtEnumValues.getBuiltInType(),
									schemaType);
						}
					}
					// else {
					// System.err.println("XSMultiValueFacet " +
					// enumer.getFacetKind());
					// // throw new RuntimeException("XSMultiValueFacet " +
					// enumer.getFacetKind());
					// }
				}
			}
		} else {
			datatype = getDatatypeOfType(std, schemaType);
		}

		return datatype;
	}

	private static QName getXMLSchemaDatatype(XSSimpleTypeDefinition std) {
		// primitive
		QName primitive = getPrimitive(std);

		QName exiDatatypeID;

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

	private static QName getSchemaType(XSSimpleTypeDefinition std) {
		// used for dtr map
		// Note: if type is anonymous the "closest" type is used as schema-type
		String name, uri;
		if (std.getAnonymous()) {
			XSTypeDefinition baseType = std;
			do {
				baseType = baseType.getBaseType();
			} while (baseType == null || baseType.getAnonymous());
			uri = baseType.getNamespace();
			name = baseType.getName();
		} else {
			uri = std.getNamespace();
			name = std.getName();
		}

		return new QName(uri, name);
	}

	private static Datatype getIntegerDatatype(XSSimpleTypeDefinition std,
			QName schemaType) {
		/*
		 * detect base integer type (e.g. int, long, BigInteger)
		 */
		// walk up the hierarchy till we find xsd simple integer types
		XSTypeDefinition xsdSTD = std;
		while (!XMLConstants.W3C_XML_SCHEMA_NS_URI
				.equals(xsdSTD.getNamespace())) {
			xsdSTD = xsdSTD.getBaseType();
		}

		// set appropriate integer type
		BuiltInType intType;
		// big
		if (xsdSTD.getName().equals("integer")
				|| xsdSTD.getName().equals("nonPositiveInteger")
				|| xsdSTD.getName().equals("negativeInteger")) {
			intType = BuiltInType.INTEGER_BIG;
		}
		// unsigned big
		else if (xsdSTD.getName().equals("nonNegativeInteger")
				|| xsdSTD.getName().equals("positiveInteger")) {
			intType = BuiltInType.UNSIGNED_INTEGER_BIG;
		}
		// int 64
		else if (xsdSTD.getName().equals("long")) {
			intType = BuiltInType.INTEGER_64;
		}
		// unsigned int 64
		else if (xsdSTD.getName().equals("unsignedLong")) {
			intType = BuiltInType.UNSIGNED_INTEGER_64;
		}
		// int 32
		else if (xsdSTD.getName().equals("int")) {
			intType = BuiltInType.INTEGER_32;
		}
		// unsigned int 32
		else if (xsdSTD.getName().equals("unsignedInt")) {
			intType = BuiltInType.UNSIGNED_INTEGER_32;
		}
		// int 16
		else if (xsdSTD.getName().equals("short")
				|| xsdSTD.getName().equals("byte")) {
			intType = BuiltInType.INTEGER_16;
		}
		// unsigned int 16
		else if (xsdSTD.getName().equals("unsignedShort")
				|| xsdSTD.getName().equals("unsignedByte")) {
			intType = BuiltInType.UNSIGNED_INTEGER_16;
		}
		// ERROR ??
		else {
			throw new RuntimeException("Unexpected Integer Type: " + xsdSTD);
		}

		/*
		 * identify lower & upper bound
		 */
		BigInteger min = new BigInteger(
				"-9999999999999999999999999999999999999999");
		BigInteger max = new BigInteger(
				"9999999999999999999999999999999999999999");
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
		// max - min + 1 --- e.g., [-1 .. -1] = 3 OR [2 .. 4] = 3
		BigInteger boundedRange = max.subtract(min).add(BigInteger.ONE);

		/*
		 * Set-up appropriate datatype
		 */
		Datatype datatype;

		if (boundedRange.compareTo(BigInteger
				.valueOf(MAX_BOUNDED_NBIT_INTEGER_RANGE)) <= 0) {
			/*
			 * When the bounded range of integer is 4095 or smaller as
			 * determined by the values of minInclusiveXS2, minExclusiveXS2,
			 * maxInclusiveXS2 and maxExclusiveXS2 facets, use n-bit Unsigned
			 * Integer representation.
			 */
			switch (intType) {
			case UNSIGNED_INTEGER_BIG:
			case INTEGER_BIG:
			case UNSIGNED_INTEGER_64:
				// big
				assert (max.subtract(min).add(BigInteger.ONE)
						.equals(boundedRange));
				datatype = new NBitBigIntegerDatatype(min, max, schemaType);
				break;
			case INTEGER_64:
			case UNSIGNED_INTEGER_32:
				// long
				assert ((max.longValue() - min.longValue() + 1) == boundedRange
						.intValue());
				datatype = new NBitLongDatatype(min.longValue(), max
						.longValue(), schemaType);
				break;
			case INTEGER_32:
			case UNSIGNED_INTEGER_16:
			case INTEGER_16:
				// int
				assert ((max.intValue() - min.intValue() + 1) == boundedRange
						.intValue());
				datatype = new NBitIntegerDatatype(min.intValue(), max
						.intValue(), schemaType);
				break;
			default:
				throw new RuntimeException("Unexpected n-Bit Integer Type: "
						+ intType);
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

			/*
			 * update int-type according to facet restrictions, val >= 0
			 */
			if (intType == BuiltInType.INTEGER_BIG) {
				intType = BuiltInType.UNSIGNED_INTEGER_BIG;
			} else if (intType == BuiltInType.INTEGER_64) {
				intType = BuiltInType.UNSIGNED_INTEGER_64;
			} else if (intType == BuiltInType.INTEGER_32) {
				intType = BuiltInType.UNSIGNED_INTEGER_32;
			} else if (intType == BuiltInType.INTEGER_16) {
				intType = BuiltInType.UNSIGNED_INTEGER_16;
			}

			switch (intType) {
			case UNSIGNED_INTEGER_BIG:
			case UNSIGNED_INTEGER_64:
				// big
				datatype = new UnsignedBigIntegerDatatype(intType, schemaType);
				break;
			case UNSIGNED_INTEGER_32:
				// long
				datatype = new UnsignedLongDatatype(intType, schemaType);
				break;
			case UNSIGNED_INTEGER_16:
				// int
				datatype = new UnsignedIntegerDatatype(intType, schemaType);
				break;
			default:
				throw new RuntimeException("Unexpected Unsigned Integer Type: "
						+ intType);
			}
		} else {
			/*
			 * Otherwise, use Integer representation.
			 */
			switch (intType) {
			case INTEGER_BIG:
				// big
				datatype = new BigIntegerDatatype(intType, schemaType);
				break;
			case INTEGER_64:
				// long
				datatype = new LongDatatype(intType, schemaType);
				break;
			case INTEGER_32:
			case INTEGER_16:
				// int
				datatype = new IntegerDatatype(intType, schemaType);
				break;
			default:
				throw new RuntimeException("Unexpected Integer Type: "
						+ intType);
			}
		}

		return datatype;
	}

	private static QName getName(XSTypeDefinition type) {
		return new QName(type.getNamespace(), type.getName());
	}

	private static Datatype getDatatypeOfType(XSSimpleTypeDefinition std,
			QName schemaType) {
		Datatype datatype;

		// 
		QName schemaDatatype = getXMLSchemaDatatype(std);

		if (XSD_BASE64BINARY.equals(schemaDatatype)) {
			datatype = new BinaryBase64Datatype(schemaType);
		} else if (XSD_HEXBINARY.equals(schemaDatatype)) {
			datatype = new BinaryHexDatatype(schemaType);
		} else if (XSD_BOOLEAN.equals(schemaDatatype)) {
			if (std.isDefinedFacet(XSSimpleTypeDefinition.FACET_PATTERN)) {
				datatype = new BooleanPatternDatatype(schemaType);
			} else {
				datatype = new BooleanDatatype(schemaType);
			}
		} else if (XSD_DATETIME.equals(schemaDatatype)) {
			QName primitive = BuiltIn.getPrimitive(std);

			if (XSD_DATETIME.equals(primitive)) {
				datatype = new DatetimeDatatype(DateTimeType.dateTime,
						schemaType);
			} else if (XSD_TIME.equals(primitive)) {
				datatype = new DatetimeDatatype(DateTimeType.time, schemaType);
			} else if (XSD_DATE.equals(primitive)) {
				datatype = new DatetimeDatatype(DateTimeType.date, schemaType);
			} else if (XSD_GYEARMONTH.equals(primitive)) {
				datatype = new DatetimeDatatype(DateTimeType.gYearMonth,
						schemaType);
			} else if (XSD_GYEAR.equals(primitive)) {
				datatype = new DatetimeDatatype(DateTimeType.gYear, schemaType);
			} else if (XSD_GMONTHDAY.equals(primitive)) {
				datatype = new DatetimeDatatype(DateTimeType.gMonthDay,
						schemaType);
			} else if (XSD_GDAY.equals(primitive)) {
				datatype = new DatetimeDatatype(DateTimeType.gDay, schemaType);
			} else if (XSD_GMONTH.equals(primitive)) {
				datatype = new DatetimeDatatype(DateTimeType.gMonth, schemaType);
			} else {
				throw new RuntimeException();
			}
		} else if (XSD_DECIMAL.equals(schemaDatatype)) {
			datatype = new DecimalDatatype(schemaType);
		} else if (XSD_FLOAT.equals(schemaDatatype)) {
			datatype = new FloatDatatype(BuiltInType.FLOAT, schemaType);
		} else if (XSD_DOUBLE.equals(schemaDatatype)) {
			datatype = new FloatDatatype(BuiltInType.DOUBLE, schemaType);
		} else if (XSD_INTEGER.equals(schemaDatatype)) {
			// returns integer type (nbit, unsigned, int) according to facets
			datatype = BuiltIn.getIntegerDatatype(std, schemaType);
		} else {
			// XSD_STRING with or without pattern
			if (std.isDefinedFacet(XSSimpleTypeDefinition.FACET_PATTERN)) {
				StringList sl = std.getLexicalPattern();

				if (isBuiltInTypeFacet(std, sl.getLength())) {
					// *normal* string
					datatype = new StringDatatype(schemaType);
				} else {
					// analyze most-derived datatype facet only
					String regexPattern = sl.item(0);
					EXIRegularExpression re = new EXIRegularExpression(
							regexPattern);

					if (re.isEntireSetOfXMLCharacters()) {
						// *normal* string
						datatype = new StringDatatype(schemaType);
					} else {
						// restricted char set
						RestrictedCharacterSet rcs = new CodePointCharacterSet(
								re.getCodePoints());
						datatype = new RestrictedCharacterSetDatatype(rcs,
								schemaType);
					}
				}
			} else {
				datatype = new StringDatatype(schemaType);
			}
		}

		return datatype;
	}

	private static boolean isBuiltInTypeFacet(XSSimpleTypeDefinition std,
			int patternListLength) {
		// Note: only the most derived type is of interest
		XSSimpleTypeDefinition baseType = (XSSimpleTypeDefinition) std
				.getBaseType();
		boolean isBuiltInTypeFacet;

		if (baseType == null
				|| !baseType
						.isDefinedFacet(XSSimpleTypeDefinition.FACET_PATTERN)) {
			// check std type
			isBuiltInTypeFacet = XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(std
					.getNamespace());
		} else {
			if (baseType.getLexicalPattern().getLength() < patternListLength) {
				/*
				 * --> std defines the last pattern (check whether it is a
				 * built-in type)
				 */
				isBuiltInTypeFacet = XMLConstants.W3C_XML_SCHEMA_NS_URI
						.equals(std.getNamespace());
			} else {
				// call again base type
				isBuiltInTypeFacet = isBuiltInTypeFacet(baseType,
						patternListLength);
			}
		}

		return isBuiltInTypeFacet;
	}

	private static QName getPrimitive(XSSimpleTypeDefinition std) {
		QName primitiveQName;
		XSSimpleTypeDefinition primitiveType = std.getPrimitiveType();

		if (primitiveType == null) {
			// TODO correct ?
			primitiveQName = XSD_ANY_SIMPLE_TYPE;
		} else {
			primitiveQName = new QName(primitiveType.getNamespace(),
					primitiveType.getName());
		}

		return primitiveQName;
	}

	private static QName getBuiltInOfPrimitiveMapping(QName qnamePrimitive) {
		if (datatypeMapping.containsKey(qnamePrimitive)) {
			return datatypeMapping.get(qnamePrimitive);
		} else {
			return DEFAULT_VALUE_NAME;
		}
	}

}
