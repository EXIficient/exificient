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

import java.util.HashMap;
import java.util.Map;

import com.siemens.ct.exi.grammar.event.Attribute;
import com.siemens.ct.exi.grammar.event.StartDocument;
import com.siemens.ct.exi.grammar.event.StartElement;
import com.siemens.ct.exi.grammar.rule.Rule;
import com.siemens.ct.exi.grammar.rule.RuleDocContentSchemaInformed;
import com.siemens.ct.exi.grammar.rule.RuleDocEnd;
import com.siemens.ct.exi.grammar.rule.RuleDocument;
import com.siemens.ct.exi.grammar.rule.RuleFragment;
import com.siemens.ct.exi.grammar.rule.RuleFragmentContentSchemaInformed;
import com.siemens.ct.exi.util.ExpandedName;
import com.siemens.ct.exi.util.ExpandedNameComparator;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20081016
 */

public class GrammarSchemaInformed extends AbstractGrammar {
	
	protected ExpandedName[] globalElements;	//	subset of entire set of element

	protected Map<ExpandedName, TypeGrammar> grammarTypes;

	protected Attribute[] globalAttributes;

	protected Rule builtInFragmentGrammar;

	protected GrammarSchemaInformed(SchemaEntry[] schemaEntries, ElementContainer[] elements,
			ExpandedName[] globalElements) {
		super(true);
		
		this.schemaEntries = schemaEntries;
		this.globalElements = globalElements;
		this.namedElements = elements;

		// init document & fragment grammar
		initDocumentGrammar(globalElements);
		initFragmentGrammar(elements);

		// allocate memory
		grammarTypes = new HashMap<ExpandedName, TypeGrammar>();
		globalAttributes = new Attribute[0];
	}


	protected void setTypeGrammars(Map<ExpandedName, TypeGrammar> grammarTypes) {
		this.grammarTypes = grammarTypes;
	}

	public TypeGrammar getTypeGrammar(String namespaceURI, String name) {
		assert(namespaceURI != null && name != null);
		
		ExpandedName en = new ExpandedName(namespaceURI, name);
		return grammarTypes.get(en);
	}

	
	public ElementContainer getNamedElement(final String namespaceURI, final String localName) {
		for (ElementContainer namedElement: namedElements) {
			if (ExpandedNameComparator.compare(namedElement.ename.getNamespaceURI(), namedElement.ename.getLocalName(), namespaceURI, localName) == 0 ) {
				return namedElement;
			}
		}
		return null;
	}
	
	public int getNumberOfGlobalElements() {
		return globalElements.length;
	}
	public boolean isGlobalElement(String namespaceURI, String localName) {
		for(ExpandedName globalElement : globalElements) {
			if (ExpandedNameComparator.compare(globalElement.getNamespaceURI(), globalElement.getLocalName(), namespaceURI, localName) == 0 ) {
				return true;
			}
		}
		return false;
	}
	
	public ExpandedName[] getGlobalElements() {
		return globalElements;
	}

	protected void setGlobalAttributes(Attribute[] globalAttributes) {
		assert(globalAttributes!=null);
		this.globalAttributes = globalAttributes;
	}

	
	public Attribute getGlobalAttribute(String namespaceURI, String name) {
		for(Attribute at : globalAttributes) {
			if (namespaceURI.equals(at.getNamespaceURI()) && name.equals(at.getLocalName())) {
				return at;
			}
		}
		return null;
	}

	protected void initDocumentGrammar(ExpandedName[] globalElements) {
		// DocEnd rule
		Rule builtInDocEndGrammar = new RuleDocEnd("DocEnd");
		// DocContent rule
		Rule builtInDocContentGrammar = new RuleDocContentSchemaInformed(
				builtInDocEndGrammar, "DocContent");
		// DocContent rule & add global elements
		for (ExpandedName globalElement : globalElements) {
			StartElement se = new StartElement(globalElement.getNamespaceURI(),
					globalElement.getLocalName());
			builtInDocContentGrammar.addRule(se, builtInDocEndGrammar);
		}
		// Document rule
		builtInDocumentGrammar = new RuleDocument(builtInDocContentGrammar,
				"Document");
	}

	protected void initFragmentGrammar(ElementContainer[] namedElements) {
		// Note: Schema-informed fragment grammar does NOT change over time!
		/*
		 * FragmentContent grammar represents the number of unique element
		 * qnames declared in the schema sorted lexicographically, first by
		 * localName, then by uri.
		 */
		/*
		 * Fragment Content
		 */
		Rule builtInFragmentContentGrammar = new RuleFragmentContentSchemaInformed(
				"FragmentContent");

		for (ElementContainer namedElement : namedElements) {
			ExpandedName ename = namedElement.ename;
			StartElement se = new StartElement(ename.getNamespaceURI(),
					ename.getLocalName());
			builtInFragmentContentGrammar.addRule(se,
					builtInFragmentContentGrammar);
		}

		/*
		 * Fragment
		 */
		builtInFragmentGrammar = new RuleFragment(
				builtInFragmentContentGrammar, "Fragment");
		builtInFragmentGrammar.addRule(new StartDocument(),
				builtInFragmentContentGrammar);
	}

	public Rule getBuiltInFragmentGrammar() {
		return builtInFragmentGrammar;
	}

}
