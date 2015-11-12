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

import com.siemens.ct.exi.context.GrammarContext;
import com.siemens.ct.exi.grammars.grammar.Grammar;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.5
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
