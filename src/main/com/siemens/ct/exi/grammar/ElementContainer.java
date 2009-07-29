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

import com.siemens.ct.exi.grammar.rule.Rule;
import com.siemens.ct.exi.util.ExpandedName;

public class ElementContainer {
	// identifier
	protected final ExpandedName ename;
	// unique schema-rule
	protected Rule schemaRule;
	// ambiguous schema-rules
	protected Rule[] schemaRules;
	protected ExpandedName[][] schemaRuleScopes;
	// fragment rule for multiple (same) qnames
	protected Rule elementFragmentGrammar;

	public ElementContainer(ExpandedName ename) {
		this.ename = ename;
	}
	
	public ExpandedName getExpandedName() {
		return ename;
	}

	/*
	 * rules section
	 */
	public void setUniqueRule(Rule schemaRule) {
		this.schemaRule = schemaRule;
	}
	
	public Rule getUniqueRule() {
		return schemaRule;
	}

	public void setAmbiguousRules(Rule[] rules, ExpandedName[][] scopes) {
		this.schemaRules = rules;
		this.schemaRuleScopes = scopes;
	}
	
	public Rule[] getAmbiguousRules() {
		return schemaRules;
	}

	public ExpandedName[][] getAmbiguousScopes() {
		return schemaRuleScopes;
	}
	

	public void setSchemaInformedElementFragmentGrammar(
			Rule elementFragmentGrammar) {
		this.elementFragmentGrammar = elementFragmentGrammar;
	}
	
	public Rule getSchemaInformedElementFragmentGrammar() {
		return elementFragmentGrammar;
	}

	@Override
	public String toString() {
		return ename.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof ElementContainer) {
			ElementContainer other = (ElementContainer)o;
			return (ename.compareTo(other.ename) == 0);
		}
		return false;
	}

}
