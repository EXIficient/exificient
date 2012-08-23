/*
 * Copyright (C) 2007-2012 Siemens AG
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

package com.siemens.ct.exi.grammars;

import java.io.Serializable;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.context.GrammarContext;
import com.siemens.ct.exi.exceptions.UnsupportedOption;
import com.siemens.ct.exi.grammars.grammar.Document;
import com.siemens.ct.exi.grammars.grammar.Fragment;
import com.siemens.ct.exi.grammars.grammar.Grammar;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9
 */

public class SchemaInformedGrammars extends AbstractGrammars implements
		Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 7647530843802602241L;

	protected boolean builtInXMLSchemaTypesOnly = false;

	protected String schemaId;

	public SchemaInformedGrammars(GrammarContext grammarContext,
			Document document, Fragment fragment) {
		super(true, grammarContext);
		// set document & fragment grammar
		documentGrammar = document;
		fragmentGrammar = fragment;
	}

	public void setBuiltInXMLSchemaTypesOnly(boolean builtInXMLSchemaTypesOnly) {
		this.builtInXMLSchemaTypesOnly = builtInXMLSchemaTypesOnly;
		this.schemaId = Constants.EMPTY_STRING;
	}

	public final String getSchemaId() {
		return schemaId;
	}

	public void setSchemaId(String schemaId) throws UnsupportedOption {
		if (builtInXMLSchemaTypesOnly && !"".equals(schemaId)) {
			throw new UnsupportedOption(
					"XML Schema types only grammars do have schemaId == '' associated with it.");
		} else {
			if (schemaId == null || "".equals(schemaId)) {
				throw new UnsupportedOption(
						"Schema-informed grammars do have schemaId != '' && schemaId != null associated with it.");
			}
		}

		this.schemaId = schemaId;
	}

	public boolean isBuiltInXMLSchemaTypesOnly() {
		return builtInXMLSchemaTypesOnly;
	}

	public Grammar getFragmentGrammar() {
		return fragmentGrammar;
	}

}
