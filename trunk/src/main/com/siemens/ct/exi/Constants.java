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

package com.siemens.ct.exi;

import javax.xml.XMLConstants;

/**
 * This class contains useful constants to the entire EXI project
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.5
 */

public interface Constants {

	/**
	 * <p>
	 * W3C EXI Namespace URI
	 * </p>
	 * 
	 * <p>
	 * Defined to be "<code>http://www.w3.org/2009/exi</code>".
	 */
	public static final String W3C_EXI_NS_URI = "http://www.w3.org/2009/exi";
	
	/**
	 * Initial Entries in String Table Partitions
	 */
	/* "", empty string */
	public static final String[] PREFIXES_EMPTY = { "" };
	public static final String[] LOCAL_NAMES_EMPTY = { };
	/* "http://www.w3.org/XML/1998/namespace" */
	public static String[] PREFIXES_XML = { "xml" };
	public static String[] LOCAL_NAMES_XML = { "base", "id", "lang", "space" };
	/* "http://www.w3.org/2001/XMLSchema-instance", xsi */
	public static String[] PREFIXES_XSI = { "xsi" };
	public static String[] LOCAL_NAMES_XSI = { "nil", "type" };
	/* "http://www.w3.org/2001/XMLSchema", xsd */
	public static String[] PREFIXES_XSD = {};
	public static String[] LOCAL_NAMES_XSD = { "ENTITIES", "ENTITY", "ID", "IDREF",
			"IDREFS", "NCName", "NMTOKEN", "NMTOKENS", "NOTATION",
			"Name", "QName", "anySimpleType", "anyType", "anyURI",
			"base64Binary", "boolean", "byte", "date", "dateTime",
			"decimal", "double", "duration", "float", "gDay", "gMonth",
			"gMonthDay", "gYear", "gYearMonth", "hexBinary", "int",
			"integer", "language", "long", "negativeInteger",
			"nonNegativeInteger", "nonPositiveInteger",
			"normalizedString", "positiveInteger", "short", "string",
			"time", "token", "unsignedByte", "unsignedInt",
			"unsignedLong", "unsignedShort" };
	
	/**
	 * 
	 */
	public static final String EMPTY_STRING = "";

	public static final String XSI_SCHEMA_LOCATION = "schemaLocation";
	public static final String XSI_NONAMESPACE_SCHEMA_LOCATION = "noNamespaceSchemaLocation";

	public static final String XML_PFX = XMLConstants.XML_NS_PREFIX; // "xml";
	public static final String XSI_PFX = "xsi";
	public static final String XSI_TYPE = "type";
	public static final String XSI_NIL = "nil";

	public static final String COLON = ":";

	public static final String XSD_LIST_DELIM = " ";
	public static final char XSD_LIST_DELIM_CHAR = ' ';

	public static final String CDATA_START = "<![CDATA[";
	public static final char[] CDATA_START_ARRAY = CDATA_START.toCharArray();
	public static final String CDATA_END = "]]>";
	public static final char[] CDATA_END_ARRAY = CDATA_END.toCharArray();

	public static final String XSD_ANY_TYPE = "anyType";

	public static final String XSD_BOOLEAN_TRUE = "true";
	public static final String XSD_BOOLEAN_1 = "1";
	public static final String XSD_BOOLEAN_FALSE = "false";
	public static final String XSD_BOOLEAN_0 = "0";

	public static final char[] XSD_BOOLEAN_TRUE_ARRAY = XSD_BOOLEAN_TRUE
			.toCharArray();
	public static final char[] XSD_BOOLEAN_1_ARRAY = XSD_BOOLEAN_1
			.toCharArray();
	public static final char[] XSD_BOOLEAN_FALSE_ARRAY = XSD_BOOLEAN_FALSE
			.toCharArray();
	public static final char[] XSD_BOOLEAN_0_ARRAY = XSD_BOOLEAN_0
			.toCharArray();

	public static final char[] DECODED_BOOLEAN_TRUE = XSD_BOOLEAN_1_ARRAY;
	public static final char[] DECODED_BOOLEAN_FALSE = XSD_BOOLEAN_0_ARRAY;

	public static final int NOT_FOUND = -1;

	/*
	 * Block & Channel settings Maximal Number of Values (per Block / Channel)
	 */
	public static final int MAX_NUMBER_OF_VALUES = 100;
	public static final int DEFAULT_BLOCK_SIZE = 1000000;

	/*
	 * StringTable settings
	 */
	public static final int DEFAULT_VALUE_MAX_LENGTH = -1; // unbounded
	public static final int DEFAULT_VALUE_PARTITON_CAPACITY = -1; // unbounded

	/*
	 * Float & Double Values
	 */
	public static final String FLOAT_INFINITY = "INF";
	public static final String FLOAT_MINUS_INFINITY = "-INF";
	public static final String FLOAT_NOT_A_NUMBER = "NaN";

	public static final char[] FLOAT_INFINITY_CHARARRAY = FLOAT_INFINITY
			.toCharArray();
	public static final char[] FLOAT_MINUS_INFINITY_CHARARRAY = FLOAT_MINUS_INFINITY
			.toCharArray();
	public static final char[] FLOAT_NOT_A_NUMBER_CHARARRAY = FLOAT_NOT_A_NUMBER
			.toCharArray();

	public static final int FLOAT_SPECIAL_VALUES = -16384; // -(2^14)
	public static final int FLOAT_MANTISSA_INFINITY = 1;
	public static final int FLOAT_MANTISSA_MINUS_INFINITY = -1;
	public static final int FLOAT_MANTISSA_NOT_A_NUMBER = 0;

	public static final long FLOAT_EXPONENT_MIN_RANGE = -16383; // - (2^14-1)
	public static final long FLOAT_EXPONENT_MAX_RANGE = 16383; // 2^14-1
	public static final long FLOAT_MANTISSA_MIN_RANGE = -9223372036854775808L; // -
																				// (2^63)
	public static final long FLOAT_MANTISSA_MAX_RANGE = 9223372036854775807L; // 2^63-1

}
