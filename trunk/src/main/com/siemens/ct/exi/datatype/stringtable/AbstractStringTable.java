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

package com.siemens.ct.exi.datatype.stringtable;

import javax.xml.XMLConstants;

import com.siemens.ct.exi.Constants;

/**
 * This class contains the full collection of EXI string table partitions.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20080718
 */
public abstract class AbstractStringTable implements StringTableCommon {

	/*
	 * pre-populate partitions
	 */
	protected void initPartitions(boolean isSchemaInformed) {
		/*
		 * 	"", empty string
		 */
		addURI(Constants.EMPTY_STRING); 
		addPrefix(Constants.EMPTY_STRING, Constants.EMPTY_STRING);
		
		/*
		 * "http://www.w3.org/XML/1998/namespace"
		 */
		addURI(XMLConstants.XML_NS_URI);
		addPrefix(XMLConstants.XML_NS_URI, Constants.XML_PFX);
		addLocalName(XMLConstants.XML_NS_URI, "space");
		addLocalName(XMLConstants.XML_NS_URI, "lang");
		addLocalName(XMLConstants.XML_NS_URI, "id");
		addLocalName(XMLConstants.XML_NS_URI, "base");
		
		/*
		 *  "http://www.w3.org/2001/XMLSchema-instance", xsi
		 */
		addURI(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
		addPrefix(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, Constants.XSI_PFX);
		addLocalName(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type");
		addLocalName(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "nil");
		
		/*
		 *  "http://www.w3.org/2001/XMLSchema", xsd
		 */
		if (isSchemaInformed) {
			addURI(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			addLocalName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "anyType");
			addLocalName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "anySimpleType");
			addLocalName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "string");
			addLocalName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "normalizedString");
			addLocalName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "token");
			addLocalName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "language");
			addLocalName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "Name");
			addLocalName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "NCName");
			addLocalName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "ID");
			addLocalName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "IDREF");
			addLocalName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "IDREFS");
			addLocalName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "ENTITY");
			addLocalName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "ENTITIES");
			addLocalName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "NMTOKEN");
			addLocalName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "NMTOKENS");
			addLocalName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "duration");
			addLocalName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "dateTime");
			addLocalName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "time");
			addLocalName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "date");
			addLocalName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "gYearMonth");
			addLocalName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "gYear");
			addLocalName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "gMonthDay");
			addLocalName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "gDay");
			addLocalName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "gMonth");
			addLocalName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "boolean");
			addLocalName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "base64Binary");
			addLocalName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "hexBinary");
			addLocalName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "float");
			addLocalName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "double");
			addLocalName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "anyURI");
			addLocalName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "QName");
			addLocalName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "NOTATION");
			addLocalName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "decimal");
			addLocalName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "integer");
			addLocalName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "nonPositiveInteger");
			addLocalName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "negativeInteger");
			addLocalName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "long");
			addLocalName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "int");
			addLocalName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "short");
			addLocalName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "byte");
			addLocalName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "nonNegativeInteger");
			addLocalName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "positiveInteger");
			addLocalName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "unsignedLong");
			addLocalName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "unsignedInt");
			addLocalName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "unsignedShort");
			addLocalName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "unsignedByte");
		}
	}
}
