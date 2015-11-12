/*
 * Copyright (c) 2007-2015 Siemens AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
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
 * @version 0.9.5
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
