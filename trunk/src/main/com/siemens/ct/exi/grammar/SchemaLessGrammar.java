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

import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.grammar.event.Attribute;
import com.siemens.ct.exi.grammar.event.StartDocument;
import com.siemens.ct.exi.grammar.event.StartElement;
import com.siemens.ct.exi.grammar.rule.DocEnd;
import com.siemens.ct.exi.grammar.rule.Document;
import com.siemens.ct.exi.grammar.rule.Fragment;
import com.siemens.ct.exi.grammar.rule.Rule;
import com.siemens.ct.exi.grammar.rule.SchemaInformedRule;
import com.siemens.ct.exi.grammar.rule.SchemaLessDocContent;
import com.siemens.ct.exi.grammar.rule.SchemaLessFragmentContent;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.4.20090423
 */

public class SchemaLessGrammar extends AbstractGrammar {
	public SchemaLessGrammar() {
		super(false);

		grammarEntries = new GrammarURIEntry[3];

		// "", empty string
		grammarEntries[0] = new GrammarURIEntry(Constants.EMPTY_STRING,
				Constants.LOCAL_NAMES_EMPTY, Constants.PREFIXES_EMPTY);

		// "http://www.w3.org/XML/1998/namespace"
		grammarEntries[1] = new GrammarURIEntry(XMLConstants.XML_NS_URI,
				Constants.LOCAL_NAMES_XML, Constants.PREFIXES_XML);

		// "http://www.w3.org/2001/XMLSchema-instance", xsi
		grammarEntries[2] = new GrammarURIEntry(
				XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,
				Constants.LOCAL_NAMES_XSI, Constants.PREFIXES_XSI);
		;

		init();
	}

	private void init() {
		// DocEnd rule
		Rule builtInDocEndGrammar = new DocEnd("DocEnd");
		// DocContent rule
		Rule builtInDocContentGrammar = new SchemaLessDocContent(
				builtInDocEndGrammar, "DocContent");
		// Document rule
		builtInDocumentGrammar = new Document(builtInDocContentGrammar,
				"Document");
	}

	/*
	 * Note: create new instance since fragment content grammar may have been
	 * changed over time
	 */
	public Rule getBuiltInFragmentGrammar() {
		/*
		 * Fragment Content
		 */
		Rule builtInFragmentContentGrammar = new SchemaLessFragmentContent();

		/*
		 * Fragment
		 */
		Rule builtInFragmentGrammar = new Fragment(
				builtInFragmentContentGrammar, "Fragment");
		builtInFragmentGrammar.addRule(new StartDocument(),
				builtInFragmentContentGrammar);

		return builtInFragmentGrammar;
	}

	public Attribute getGlobalAttribute(QName qname) {
		return null;
	}

	public SchemaInformedRule getTypeGrammar(QName qname) {
		// no type grammar available
		return null;
	}

	public List<QName> getSimpleTypeSubtypes(QName type) {
		// no type nor sub-types
		return null;
	}

	public StartElement getGlobalElement(QName qname) {
		return null;
	}
}