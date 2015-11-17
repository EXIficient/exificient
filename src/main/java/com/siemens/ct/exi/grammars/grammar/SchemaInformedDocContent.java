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
 * <Built-in Document Grammar>
 * 
 * DocContent : SE (G 0) DocEnd 0 SE (G 1) DocEnd 1 ... SE (G n-1) DocEnd n-1 SE
 * () DocEnd n.0 DT DocContent n.1 CM DocContent n.2.0 PI DocContent n.2.1
 */

public class SchemaInformedDocContent extends AbstractSchemaInformedGrammar {

	private static final long serialVersionUID = -2644676723844219418L;

	public SchemaInformedDocContent() {
		super();
	}

	public SchemaInformedDocContent(String label) {
		this(); // docEnd);
		this.setLabel(label);
	}
	
	public GrammarType getGrammarType() {
		return GrammarType.SCHEMA_INFORMED_DOC_CONTENT;
	}

	public String toString() {
		return "DocContent" + super.toString();
	}
}
