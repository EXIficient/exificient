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
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.grammar.event.Attribute;
import com.siemens.ct.exi.grammar.event.StartDocument;
import com.siemens.ct.exi.grammar.event.StartElement;
import com.siemens.ct.exi.grammar.rule.DocEnd;
import com.siemens.ct.exi.grammar.rule.Document;
import com.siemens.ct.exi.grammar.rule.Fragment;
import com.siemens.ct.exi.grammar.rule.Rule;
import com.siemens.ct.exi.grammar.rule.SchemaInformedDocContent;
import com.siemens.ct.exi.grammar.rule.SchemaInformedFragmentContent;
import com.siemens.ct.exi.util.sort.LexicographicSort;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20081016
 */

public class SchemaInformedGrammar extends AbstractGrammar {

	protected List<StartElement> globalElements; // subset of entire set of
													// element

	protected Map<QName, TypeGrammar> grammarTypes;

	protected Attribute[] globalAttributes;

	protected Rule builtInFragmentGrammar;

	protected SchemaInformedGrammar(URIEntry[] schemaEntries,
			List<StartElement> fragmentElements, List<StartElement> globalElements) {
		super(true);

		this.schemaEntries = schemaEntries;
		this.globalElements = globalElements;
		// this.namedElements = elements;

		// init document & fragment grammar
		initDocumentGrammar();
		initFragmentGrammar(fragmentElements);

		// allocate memory
		grammarTypes = new HashMap<QName, TypeGrammar>();
		globalAttributes = new Attribute[0];
	}

	protected void setTypeGrammars(Map<QName, TypeGrammar> grammarTypes) {
		this.grammarTypes = grammarTypes;
	}

	public TypeGrammar getTypeGrammar(String namespaceURI, String name) {
		assert (namespaceURI != null && name != null);

		QName en = new QName(namespaceURI, name);
		return grammarTypes.get(en);
	}

	public StartElement getGlobalElement(String namespaceURI, String localName) {
		for (StartElement globalElement : globalElements) {
			QName qname = globalElement.getQName();
			if (LexicographicSort.compare(qname.getNamespaceURI(),
					qname.getLocalPart(), namespaceURI, localName) == 0) {
				return globalElement;
			}
		}

		return null;
	}

	protected void setGlobalAttributes(Attribute[] globalAttributes) {
		assert (globalAttributes != null);
		this.globalAttributes = globalAttributes;
	}

	public Attribute getGlobalAttribute(String namespaceURI, String name) {
		for (Attribute at : globalAttributes) {
			QName qname = at.getQName();
			if (namespaceURI.equals(qname.getNamespaceURI())
					&& name.equals(qname.getLocalPart())) {
				return at;
			}
		}
		return null;
	}

	protected void initDocumentGrammar() {
		// Note: Schema-informed document grammar does NOT change over time!
		/*
		 * Global elements declared in the schema. G 0, G 1, ... G n-1 represent
		 * all the qnames of global elements sorted lexicographically, first by
		 * localName, then by uri.
		 * http://www.w3.org/TR/exi/#informedDocGrammars
		 */
		// DocEnd rule
		Rule builtInDocEndGrammar = new DocEnd("DocEnd");
		// DocContent rule
		Rule builtInDocContentGrammar = new SchemaInformedDocContent(
				builtInDocEndGrammar, "DocContent");
		// DocContent rule & add global elements (sorted)
		for (StartElement globalElement : globalElements) {
			builtInDocContentGrammar.addRule(globalElement,
					builtInDocEndGrammar);
		}
		// Document rule
		builtInDocumentGrammar = new Document(builtInDocContentGrammar,
				"Document");
	}

	protected void initFragmentGrammar(List<StartElement> namedElements) {
		// Note: Schema-informed fragment grammar does NOT change over time!
		/*
		 * FragmentContent grammar represents the number of unique element
		 * qnames declared in the schema sorted lexicographically, first by
		 * localName, then by uri.
		 * http://www.w3.org/TR/exi/#informedElementFragGrammar
		 */
		/*
		 * Fragment Content
		 */
		Rule builtInFragmentContentGrammar = new SchemaInformedFragmentContent(
				"FragmentContent");
		for (StartElement namedElement : namedElements) {
			builtInFragmentContentGrammar.addRule(namedElement,
					builtInFragmentContentGrammar);
		}

		/*
		 * Fragment
		 */
		builtInFragmentGrammar = new Fragment(builtInFragmentContentGrammar,
				"Fragment");
		builtInFragmentGrammar.addRule(new StartDocument(),
				builtInFragmentContentGrammar);
	}

	public Rule getBuiltInFragmentGrammar() {
		return builtInFragmentGrammar;
	}

}
