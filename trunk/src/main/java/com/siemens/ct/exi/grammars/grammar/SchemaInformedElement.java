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
 * @version 0.9.5-SNAPSHOT
 */

/*
 * 
 * <Schema-informed Element Grammar>
 * 
 * EE n.m
 * 
 * Element i, j : SE () Element i, j n.m CH [schema-invalid value ] Element i, j
 * n.(m+1) ER Element i, j n.(m+2) CM Element i, j n.(m+3).0 PI Element i, j
 * n.(m+3).1
 */

public class SchemaInformedElement extends AbstractSchemaInformedContent implements Cloneable {

	private static final long serialVersionUID = 7009002330388834813L;

	public GrammarType getGrammarType() {
		return GrammarType.SCHEMA_INFORMED_ELEMENT_CONTENT;
	}

	@Override
	public SchemaInformedElement clone() {
		SchemaInformedElement clone = (SchemaInformedElement) super.clone();
		return clone;
	}

	public String toString() {
		return "Element" + super.toString();
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof SchemaInformedElement && super.equals(obj));
	}

}
