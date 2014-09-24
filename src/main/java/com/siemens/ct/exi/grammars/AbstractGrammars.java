/*
 * Copyright (C) 2007-2014 Siemens AG
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

import com.siemens.ct.exi.context.GrammarContext;
import com.siemens.ct.exi.grammars.grammar.Grammar;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.4-SNAPSHOT
 */

public abstract class AbstractGrammars implements Grammars {

	private static final long serialVersionUID = 1328500655881102889L;

//	protected Grammar urTypeGrammar;

	/*
	 * Document and Fragment Grammars
	 */
	protected Grammar documentGrammar;
	protected Grammar fragmentGrammar;

	private final GrammarContext grammarContext;

	private final boolean isSchemaInformed;

	public AbstractGrammars(boolean isSchemaInformed,
			GrammarContext grammarContext) {
		this.isSchemaInformed = isSchemaInformed;
		this.grammarContext = grammarContext;
	}

	public GrammarContext getGrammarContext() {
		return this.grammarContext;
	}

	public boolean isSchemaInformed() {
		return isSchemaInformed;
	}

//	public Grammar getUrTypeGrammar() {
//		if (urTypeGrammar == null) {
//			urTypeGrammar = XSDGrammarsBuilder.getUrTypeRule();
//		}
//
//		return urTypeGrammar;
//	}

	public Grammar getDocumentGrammar() {
		return documentGrammar;
	}

}
