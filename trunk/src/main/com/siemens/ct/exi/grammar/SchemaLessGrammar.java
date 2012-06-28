/*
 * Copyright (C) 2007-2011 Siemens AG
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
import javax.xml.namespace.QName;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.context.GrammarContext;
import com.siemens.ct.exi.context.GrammarUriContext;
import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.exceptions.UnsupportedOption;
import com.siemens.ct.exi.grammar.event.EndDocument;
import com.siemens.ct.exi.grammar.event.StartDocument;
import com.siemens.ct.exi.grammar.rule.DocEnd;
import com.siemens.ct.exi.grammar.rule.Document;
import com.siemens.ct.exi.grammar.rule.Fragment;
import com.siemens.ct.exi.grammar.rule.Rule;
import com.siemens.ct.exi.grammar.rule.SchemaLessDocContent;
import com.siemens.ct.exi.grammar.rule.SchemaLessFragmentContent;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.8
 */

public class SchemaLessGrammar extends AbstractGrammar {

	private static final long serialVersionUID = -6969262948282161888L;

	static {
		GrammarUriContext[] grammarUriContextsX = new GrammarUriContext[3];
		int qNameID = 0;
		// 0
		{
			int namespaceUriID = 0;
			QNameContext[] grammarQNames0 = new QNameContext[Constants.LOCAL_NAMES_EMPTY.length];
			grammarUriContextsX[namespaceUriID] = new GrammarUriContext(
					namespaceUriID, Constants.EMPTY_STRING, grammarQNames0,
					Constants.PREFIXES_EMPTY);
		}
		// 1
		{
			int namespaceUriID = 1;
			QNameContext[] grammarQNames1 = new QNameContext[Constants.LOCAL_NAMES_XML.length];
			for (int i = 0; i < grammarQNames1.length; i++) {
				grammarQNames1[i] = new QNameContext(namespaceUriID, i,
						new QName(XMLConstants.XML_NS_URI,
								Constants.LOCAL_NAMES_XML[i]), qNameID++);
			}
			grammarUriContextsX[namespaceUriID] = new GrammarUriContext(
					namespaceUriID, XMLConstants.XML_NS_URI, grammarQNames1,
					Constants.PREFIXES_XML);
		}
		// 2
		{
			int namespaceUriID = 2;
			QNameContext[] grammarQNames2 = new QNameContext[Constants.LOCAL_NAMES_XSI.length];
			for (int i = 0; i < grammarQNames2.length; i++) {
				grammarQNames2[i] = new QNameContext(namespaceUriID, i,
						new QName(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,
								Constants.LOCAL_NAMES_XSI[i]), qNameID++);
			}
			grammarUriContextsX[namespaceUriID] = new GrammarUriContext(
					namespaceUriID,
					XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,
					grammarQNames2, Constants.PREFIXES_XSI);
		}

		SCHEMA_LESS_GRAMMAR_CONTEXT = new GrammarContext(grammarUriContextsX,
				qNameID);
	}
	private static final GrammarContext SCHEMA_LESS_GRAMMAR_CONTEXT;

	public SchemaLessGrammar() {
		super(false, SCHEMA_LESS_GRAMMAR_CONTEXT);
		init();
	}

	private void init() {
		// DocEnd rule
		DocEnd builtInDocEndGrammar = new DocEnd("DocEnd");
		builtInDocEndGrammar.addTerminalRule(new EndDocument());
		// DocContent rule
		Rule builtInDocContentGrammar = new SchemaLessDocContent(
				builtInDocEndGrammar, "DocContent");
		// Document rule
		documentGrammar = new Document("Document");
		documentGrammar.addRule(new StartDocument(), builtInDocContentGrammar);
	}

	public final boolean isBuiltInXMLSchemaTypesOnly() {
		return false;
	}

	public final String getSchemaId() {
		return null;
	}

	public void setSchemaId(String schemaId) throws UnsupportedOption {
		if (schemaId != null) {
			throw new UnsupportedOption(
					"Schema-less grammars do have schemaId == null associated with it.");
		}
	}

	/*
	 * Note: create new instance since fragment content grammar may have been
	 * changed over time
	 */
	public Rule getFragmentGrammar() {
		/*
		 * Fragment Content
		 */
		Rule builtInFragmentContentGrammar = new SchemaLessFragmentContent();

		/*
		 * Fragment
		 */
		fragmentGrammar = new Fragment("Fragment");
		fragmentGrammar.addRule(new StartDocument(), builtInFragmentContentGrammar);

		return fragmentGrammar;
	}

}