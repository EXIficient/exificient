/*
 * Copyright (C) 2007, 2008 Siemens AG
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

import com.siemens.ct.exi.grammar.event.EndDocument;
import com.siemens.ct.exi.grammar.event.StartDocument;
import com.siemens.ct.exi.grammar.event.StartElementGeneric;
import com.siemens.ct.exi.grammar.rule.Rule;
import com.siemens.ct.exi.grammar.rule.RuleDocEnd;
import com.siemens.ct.exi.grammar.rule.RuleDocument;
import com.siemens.ct.exi.grammar.rule.RuleFragment;
import com.siemens.ct.exi.grammar.rule.SchemaLessRuleDocContent;
import com.siemens.ct.exi.grammar.rule.SchemaLessRuleFragmentContent;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.2.20080718
 */

public class SchemaLessGrammar extends AbstractGrammar {
	public SchemaLessGrammar() {
		super(false);

		init();
	}

	private void init() {
		/*
		 * rule (DocEnd)
		 */
		builtInDocEndGrammar = new RuleDocEnd("DocEnd");
		builtInDocEndGrammar.addTerminalRule(new EndDocument());

		/*
		 * rule (DocContent)
		 */
		builtInDocContentGrammar = new SchemaLessRuleDocContent(
				builtInDocEndGrammar, "DocContent");

		// rule (DocContent), not schema informed --> level one
		builtInDocContentGrammar.addRule(new StartElementGeneric(),
				builtInDocEndGrammar);

		/*
		 * rule (Document)
		 */
		builtInDocumentGrammar = new RuleDocument(builtInDocContentGrammar,
				"Document");
		builtInDocumentGrammar.addRule(new StartDocument(),
				builtInDocContentGrammar);
	}

	public Rule getRule(ElementKey es) {
		return null;
	}

	public Rule getBuiltInFragmentGrammar() {
		// Note: create new instance since fragment content grammar may change
		// over time

		/*
		 * Fragment Content
		 */
		Rule builtInFragmentContentGrammar = new SchemaLessRuleFragmentContent();

		/*
		 * Fragment
		 */
		Rule builtInFragmentGrammar = new RuleFragment(
				builtInFragmentContentGrammar, "Fragment");
		builtInFragmentGrammar.addRule(new StartDocument(),
				builtInFragmentContentGrammar);

		return builtInFragmentGrammar;
	}

}