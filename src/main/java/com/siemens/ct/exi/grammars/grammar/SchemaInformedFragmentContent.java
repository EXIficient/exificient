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

package com.siemens.ct.exi.grammars.grammar;


/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.6-SNAPSHOT
 */

/*
 * <Schema-informed Fragment Grammar>
 * 
 * FragmentContent : SE (F 0) FragmentContent 0 SE (F 1) FragmentContent 1 ...
 * SE (F n-1) FragmentContent n-1 ED n SE () FragmentContent (n+1).0 CM
 * FragmentContent (n+1).1.0 PI FragmentContent (n+1).1.1
 */

public class SchemaInformedFragmentContent extends AbstractSchemaInformedGrammar {

	private static final long serialVersionUID = 2041418874823084368L;

	public SchemaInformedFragmentContent() {
		super();
	}

	public SchemaInformedFragmentContent(String label) {
		this();
		setLabel(label);
	}
	
	public GrammarType getGrammarType() {
		return GrammarType.SCHEMA_INFORMED_FRAGMENT_CONTENT;
	}

	public String toString() {
		return "FragmentContent" + super.toString();
	}

}
