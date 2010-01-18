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

package com.siemens.ct.exi.grammar;

import javax.xml.XMLConstants;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.grammar.rule.Rule;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20081014
 */

public abstract class AbstractGrammar implements Grammar {

	protected GrammarURIEntry[] grammarEntries;

	/*
	 * Built-in Grammars
	 */
	protected Rule builtInDocumentGrammar;

	private final boolean isSchemaInformed;

	public AbstractGrammar(boolean isSchemaInformed) {
		this.isSchemaInformed = isSchemaInformed;
	}
	
	protected static GrammarURIEntry getURIEntryForEmpty() {
		/*
		 * "", empty string
		 */
		String[] localNamesEmpty = { };
		String[] prefixesEmpty = { "" };
		return new GrammarURIEntry(Constants.EMPTY_STRING,
				localNamesEmpty, prefixesEmpty);
	}
	
	protected static GrammarURIEntry getURIEntryForXML() {
		/*
		 * "http://www.w3.org/XML/1998/namespace"
		 */
		String[] localNamesXML = { "base", "id", "lang", "space" };
		String[] prefixesXML = { "xml" };
		return new GrammarURIEntry(XMLConstants.XML_NS_URI,
				localNamesXML, prefixesXML);
	}
	
	protected static GrammarURIEntry getURIEntryForXSI() {
		/*
		 * "http://www.w3.org/2001/XMLSchema-instance", xsi
		 */
		String[] localNamesXSI = { "nil", "type" };
		String[] prefixesXSI = { "xsi" };
		return new GrammarURIEntry(
				XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, localNamesXSI, prefixesXSI);
	}
	
	protected static GrammarURIEntry getURIEntryForXSD() {
		/*
		 * "http://www.w3.org/2001/XMLSchema", xsd
		 */
		String[] localNamesXSD = { "ENTITIES", "ENTITY", "ID", "IDREF",
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
		assert (localNamesXSD.length == 46);
		String[] prefixesXSD = {};
		return new GrammarURIEntry(
				XMLConstants.W3C_XML_SCHEMA_NS_URI, localNamesXSD, prefixesXSD);
	}

	public boolean isSchemaInformed() {
		return isSchemaInformed;
	}

	public Rule getBuiltInDocumentGrammar() {
		return builtInDocumentGrammar;
	}

	public GrammarURIEntry[] getGrammarEntries() {
		return grammarEntries;
	}
}
