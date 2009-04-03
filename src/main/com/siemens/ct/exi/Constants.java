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

package com.siemens.ct.exi;

import javax.xml.XMLConstants;

/**
 * This class contains useful constants to the entire EXI project
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.2.20090324
 */

public interface Constants {
	public static final String EMPTY_STRING = "";

	public static final String XSI_SCHEMA_LOCATION = "schemaLocation";
	public static final String XSI_NONAMESPACE_SCHEMA_LOCATION = "noNamespaceSchemaLocation ";

	public static final String XML_PFX = XMLConstants.XML_NS_PREFIX; // "xml";
	public static final String XSI_PFX = "xsi";
	public static final String XSI_TYPE = "type";
	public static final String XSI_NIL = "nil";

	public static final String COLON = ":";

	public static final String XSD_LIST_DELIM = " ";

	public static final String XSD_ANY_TYPE = "anyType";

	public static final String XSD_BOOLEAN_TRUE = "true";
	public static final String XSD_BOOLEAN_1 = "1";
	public static final String XSD_BOOLEAN_FALSE = "false";
	public static final String XSD_BOOLEAN_0 = "0";

	public static final String DECODED_BOOLEAN_TRUE = XSD_BOOLEAN_TRUE;
	public static final String DECODED_BOOLEAN_FALSE = XSD_BOOLEAN_FALSE;

	public static final int NOT_FOUND = -1;

}
