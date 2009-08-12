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

package com.siemens.ct.exi.core;

import java.util.List;

import com.siemens.ct.exi.grammar.rule.Rule;
import com.siemens.ct.exi.util.ExpandedName;
import com.siemens.ct.exi.util.ExpandedNameComparator;

public class NameContext {
	// 
	protected final String localName;
	protected final String namespaceURI;

	// string tables
	public final int localNameID;

	// unique schema-rule
	protected Rule schemaRule;

	// ambiguous schema-rule
	protected Rule[] schemaRules;
	protected ExpandedName[][] schemaRuleScopes;

	protected Rule elementFragmentStartTag;

	public NameContext(int localNameID, String localName, String namespaceURI) {
		this.localNameID = localNameID;
		this.localName = localName;
		this.namespaceURI = namespaceURI;
	}

	public String getLocalName() {
		return localName;
	}

	public String getNamespaceURI() {
		return namespaceURI;
	}

	public void setAmbiguousRules(Rule[] rules, ExpandedName[][] scopes) {
		this.schemaRules = rules;
		this.schemaRuleScopes = scopes;
	}

	public Rule getGlobalRule() {
		if (schemaRules != null) {
			for (int i = 0; i < schemaRuleScopes.length; i++) {
				ExpandedName[] ens = schemaRuleScopes[i];
				if(ens.length == 0) {
					return schemaRules[i];
				}
			}
		}
		return null;
	}
	
	public Rule getScopeRule(List<NameContext> scope) {
		if (schemaRules != null) {
			for (int i = 0; i < schemaRuleScopes.length; i++) {
				ExpandedName[] ens = schemaRuleScopes[i];
				
				int ensIndex = ens.length-1;
				
				if (ensIndex >= 0) {
					int diff = scope.size() - ens.length;
					
					//	right element context
					assert(scope.get(ensIndex+diff).getNamespaceURI().equals(this.namespaceURI));
					assert(scope.get(ensIndex+diff).getLocalName().equals(this.localName));
					
					int scopeIndex = ensIndex+diff-1;
					
					boolean OK = true;
					while(ensIndex >= 0 && scopeIndex > 0 && OK)  {
						ExpandedName en = ens[ensIndex];
						NameContext s = scope.get(scopeIndex);
						OK = ExpandedNameComparator.compare(en.getNamespaceURI(), en.getLocalName(), s.getNamespaceURI(), s.getLocalName()) == 0;
						ensIndex--;
						scopeIndex--;
					}
					
					if ( (ensIndex == -1 || scopeIndex == 0 ) && OK) {
						return schemaRules[i];
					}	
				}
			}
		}

		return null;
	}

	public void setUniqueSchemaRule(Rule schemaRule) {
		this.schemaRule = schemaRule;
	}

	public Rule getUniqueSchemaRule() {
		return this.schemaRule;
	}

	public void setSchemaInformedElementFragmentGrammar(
			Rule elementFragmentStartTag) {
		this.elementFragmentStartTag = elementFragmentStartTag;
	}

	public Rule getSchemaInformedElementFragmentGrammar() {
		return elementFragmentStartTag;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof NameContext) {
			NameContext other = (NameContext) o;
			return (localName.equals(other.localName) && namespaceURI
					.equals(other.namespaceURI));
		}
		return false;
	}

	/*
	 * This method returns the hash code value as an integer and is supported
	 * for the benefit of hashing based collection classes such as Hashtable,
	 * HashMap, HashSet etc
	 * 
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public final int hashCode() {
		return namespaceURI.hashCode() ^ localName.hashCode();
	}

	public String toString() {
		return "{" + namespaceURI + "}" + localName + "(" + localNameID + ")";
	}
}
