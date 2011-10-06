/*
 * Copyright (C) 2007-2011 Siemens AG
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

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import com.siemens.ct.exi.datatype.BooleanDatatype;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.datatype.StringDatatype;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.8
 */

public class BuiltIn {

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
	public static final QName XSD_NON_NEGATIVE_INTEGER = new QName(
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
	public static final QName XSD_QNAME = new QName(
			XMLConstants.W3C_XML_SCHEMA_NS_URI, "QName");
	public static final QName XSD_NOTATION = new QName(
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

	
}
