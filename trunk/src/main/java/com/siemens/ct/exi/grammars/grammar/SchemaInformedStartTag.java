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

import com.siemens.ct.exi.grammars.production.Production;
import com.siemens.ct.exi.grammars.production.SchemaInformedProduction;

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
 * AT(xsi:type) Element i, 0 n.m AT(xsi:nil) Element i, 0 n.(m+1)
 * 
 * AT () Element i, j n.m AT (qname 0 ) [schema-invalid value] Element i, j
 * n.(m+1).0 AT (qname 1 ) [schema-invalid value] Element i, j n.(m+1).1 ... AT
 * (qname x-1 ) [schema-invalid value] Element i, j n.(m+1).(x-1) AT ()
 * [schema-invalid value] Element i, j n.(m+1).(x)
 * 
 * NS Element i, 0 n.m
 * 
 * SC Fragment n.m // ----- //
 * 
 * SE () Element i, content2 n.m CH [schema-invalid value ] Element i, content2
 * n.(m+1) ER Element i, content2 n.(m+2) CM Element i, content2 n.(m+3).0 PI
 * Element i, content2 n.(m+3).1
 */
public class SchemaInformedStartTag extends AbstractSchemaInformedContent
		implements SchemaInformedStartTagGrammar, Cloneable {

	private static final long serialVersionUID = -674782327638586700L;

	protected Grammar elementContent2;

	public SchemaInformedStartTag() {
		super();
	}
	
	public SchemaInformedStartTag(SchemaInformedGrammar elementContent2) {
		this();
		this.elementContent2 = elementContent2;
	}
	
	public GrammarType getGrammarType() {
		return GrammarType.SCHEMA_INFORMED_START_TAG_CONTENT;
	}
	
	public void setElementContentGrammar(Grammar elementContent2) {
		this.elementContent2 = elementContent2;
	}

	@Override
	public Grammar getElementContentGrammar() {
		return elementContent2;
	}

	@Override
	public SchemaInformedStartTag clone() {
		// SchemaInformedStartTag clone = new SchemaInformedStartTag(elementContent2);
		SchemaInformedStartTag clone = (SchemaInformedStartTag) super.clone();
		
		// remove self-references
		for(int i=0; i<clone.containers.length; i++) {
			Production ei = clone.containers[i];
			if (ei.getNextGrammar() == this) {
				clone.containers[i] = new SchemaInformedProduction(clone, ei.getEvent(), i);
			}
			
		}
		
		return clone;
		
		
	}

	public String toString() {
		String s = "StartTag";
		return s + super.toString();
	}

}
