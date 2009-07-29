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

package com.siemens.ct.exi.grammar;

import com.siemens.ct.exi.grammar.event.Attribute;
import com.siemens.ct.exi.grammar.event.StartDocument;
import com.siemens.ct.exi.grammar.rule.Rule;
import com.siemens.ct.exi.grammar.rule.RuleDocContentSchemaLess;
import com.siemens.ct.exi.grammar.rule.RuleDocEnd;
import com.siemens.ct.exi.grammar.rule.RuleDocument;
import com.siemens.ct.exi.grammar.rule.RuleFragment;
import com.siemens.ct.exi.grammar.rule.RuleFragmentContentSchemaLess;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20090423
 */

public class GrammarSchemaLess extends AbstractGrammar {
	public GrammarSchemaLess() {
		super(false);
		
		this.schemaEntries = new SchemaEntry[0];
		this.namedElements = new ElementContainer[0];

		init();
	}

	private void init() {
		// DocEnd rule
		Rule builtInDocEndGrammar = new RuleDocEnd("DocEnd");
		// DocContent rule
		Rule builtInDocContentGrammar = new RuleDocContentSchemaLess(
				builtInDocEndGrammar, "DocContent");
		// Document rule
		builtInDocumentGrammar = new RuleDocument(builtInDocContentGrammar,
				"Document");
	}

	/*
	 * Note: create new instance since fragment content grammar may have
	 * been changed over time
	 */
	public Rule getBuiltInFragmentGrammar() {
		/*
		 * Fragment Content
		 */
		Rule builtInFragmentContentGrammar = new RuleFragmentContentSchemaLess();

		/*
		 * Fragment
		 */
		Rule builtInFragmentGrammar = new RuleFragment(
				builtInFragmentContentGrammar, "Fragment");
		builtInFragmentGrammar.addRule(new StartDocument(),
				builtInFragmentContentGrammar);

		return builtInFragmentGrammar;
	}

	public Attribute getGlobalAttribute(String namespaceURI, String name) {
		return null;
	}

	public TypeGrammar getTypeGrammar(String namespaceURI, String name) {
		// no type grammar available
		return null;
	}

	public int getNumberOfGlobalElements() {
		return 0;
	}
	public boolean isGlobalElement(String namespaceURI, String localName) {
		return false;
	}

	public ElementContainer getNamedElement(String namespaceURI,
			String localName) {
		// no schema information
		return null;
	}

}