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
 * @version 0.2.20080718
 */
public abstract class AbstractStringTable implements StringTableCommon {

	/*
	 * pre-populate URI partition
	 */
	protected void initURI(StringTablePartition uriPartition,
			boolean isSchemaInformed) {
		uriPartition.add(Constants.EMPTY_STRING); /* empty string */
		uriPartition.add(XMLConstants.XML_NS_URI);
		uriPartition.add(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);

		if (isSchemaInformed) {
			uriPartition.add(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		}
	}

	/*
	 * pre-populate the initial set of prefix partitions
	 */
	protected void initPrefixEmpty(StringTablePartition prefixPartitionEmpty) {
		prefixPartitionEmpty.add(Constants.EMPTY_STRING); /* empty string */
	}

	protected void initPrefixXML(StringTablePartition prefixPartitionXML) {
		prefixPartitionXML.add(Constants.XML_PFX);
	}

	protected void initPrefixXSI(StringTablePartition prefixPartitionXSI) {
		prefixPartitionXSI.add(Constants.XSI_PFX);
	}

	/*
	 * pre-populate the initial set of local name partitions
	 */
	protected void initLocalNameXML(StringTablePartition localNamePartitionXML) {
		localNamePartitionXML.add("space");
		localNamePartitionXML.add("lang");
		localNamePartitionXML.add("id");
		localNamePartitionXML.add("base");
	}

	protected void initLocalNameXSI(StringTablePartition localNamePartitionXSI) {
		localNamePartitionXSI.add("type");
		localNamePartitionXSI.add("nil");
	}

	protected void initLocalNameXSD(StringTablePartition localNamePartitionXSD) {
		localNamePartitionXSD.add("anyType");
		localNamePartitionXSD.add("anySimpleType");
		localNamePartitionXSD.add("string");
		localNamePartitionXSD.add("normalizedString");
		localNamePartitionXSD.add("token");
		localNamePartitionXSD.add("language");
		localNamePartitionXSD.add("Name");
		localNamePartitionXSD.add("NCName");
		localNamePartitionXSD.add("ID");
		localNamePartitionXSD.add("IDREF");
		localNamePartitionXSD.add("IDREFS");
		localNamePartitionXSD.add("ENTITY");
		localNamePartitionXSD.add("ENTITIES");
		localNamePartitionXSD.add("NMTOKEN");
		localNamePartitionXSD.add("NMTOKENS");
		localNamePartitionXSD.add("duration");
		localNamePartitionXSD.add("dateTime");
		localNamePartitionXSD.add("time");
		localNamePartitionXSD.add("date");
		localNamePartitionXSD.add("gYearMonth");
		localNamePartitionXSD.add("gYear");
		localNamePartitionXSD.add("gMonthDay");
		localNamePartitionXSD.add("gDay");
		localNamePartitionXSD.add("gMonth");
		localNamePartitionXSD.add("boolean");
		localNamePartitionXSD.add("base64Binary");
		localNamePartitionXSD.add("hexBinary");
		localNamePartitionXSD.add("float");
		localNamePartitionXSD.add("double");
		localNamePartitionXSD.add("anyURI");
		localNamePartitionXSD.add("QName");
		localNamePartitionXSD.add("NOTATION");
		localNamePartitionXSD.add("decimal");
		localNamePartitionXSD.add("integer");
		localNamePartitionXSD.add("nonPositiveInteger");
		localNamePartitionXSD.add("negativeInteger");
		localNamePartitionXSD.add("long");
		localNamePartitionXSD.add("int");
		localNamePartitionXSD.add("short");
		localNamePartitionXSD.add("byte");
		localNamePartitionXSD.add("nonNegativeInteger");
		localNamePartitionXSD.add("positiveInteger");
		localNamePartitionXSD.add("unsignedLong");
		localNamePartitionXSD.add("unsignedInt");
		localNamePartitionXSD.add("unsignedShort");
		localNamePartitionXSD.add("unsignedByte");
	}

}
