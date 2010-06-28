/*
 * Copyright (C) 2007-2010 Siemens AG
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

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.4.20081014
 */

public abstract class AbstractGrammar implements Grammar {

	protected GrammarURIEntry[] grammarEntries;

	/*
	 * Built-in Grammars
	 */
	protected Rule builtInDocumentGrammar;

	private final boolean isSchemaInformed;

	public AbstractGrammar(boolean isSchemaInformed) {
		this.isSchemaInformed = isSchemaInformed;
	}

	public boolean isSchemaInformed() {
		return isSchemaInformed;
	}

	public Rule getBuiltInDocumentGrammar() {
		return builtInDocumentGrammar;
	}

	public GrammarURIEntry[] getGrammarEntries() {
		return grammarEntries;
	}
}
