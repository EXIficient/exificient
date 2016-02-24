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

import javax.xml.namespace.QName;

import com.siemens.ct.exi.grammars.production.Production;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.6-SNAPSHOT
 */

public class SchemaInformedFirstStartTag extends SchemaInformedStartTag
		implements SchemaInformedFirstStartTagGrammar, Cloneable {

	private static final boolean USE_RUNTIME_EMPTY_TYPE = true;
	
	private static final long serialVersionUID = -6071059051303822226L;

	// subtype (xsi:type) OR nillable (xsi:nill) ?
	protected boolean isTypeCastable = false;
	protected boolean isNillable = false;
	protected SchemaInformedFirstStartTagGrammar typeEmpty;

	protected QName typeName = null;

	public SchemaInformedFirstStartTag() {
		super();
	}
	
	public SchemaInformedFirstStartTag(SchemaInformedGrammar elementContent2) {
		super(elementContent2);
	}
	
	public GrammarType getGrammarType() {
		return GrammarType.SCHEMA_INFORMED_FIRST_START_TAG_CONTENT;
	}

	public SchemaInformedFirstStartTag(SchemaInformedStartTagGrammar startTag) {
		this((SchemaInformedGrammar) startTag.getElementContentGrammar());

		// clone top level
		for (int i = 0; i < startTag.getNumberOfEvents(); i++) {
			Production ei = startTag.getProduction(i);
			// remove self-reference
			Grammar next = ei.getNextGrammar();
			if (next == startTag) {
				next = this;
			}
			this.addProduction(ei.getEvent(), next);
		}
	}

	public void setTypeCastable(boolean isTypeCastable) {
		this.isTypeCastable = isTypeCastable;
	}

	public boolean isTypeCastable() {
		return isTypeCastable;
	}

	public void setNillable(boolean isNillable) {
		this.isNillable = isNillable;
	}

	public boolean isNillable() {
		return isNillable;
	}

	public void setTypeEmpty(SchemaInformedFirstStartTagGrammar typeEmpty) {
		this.typeEmpty = typeEmpty;
	}
	
	

	

	public SchemaInformedFirstStartTagGrammar getTypeEmpty() {
		if(USE_RUNTIME_EMPTY_TYPE) {
			return (SchemaInformedFirstStartTagGrammar) getTypeEmptyInternal();
		} else {
			return this.typeEmpty;
		}
		
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if (obj instanceof SchemaInformedFirstStartTag) {
			SchemaInformedFirstStartTag other = (SchemaInformedFirstStartTag) obj;
			if (this.isTypeCastable == other.isTypeCastable
					&& this.isNillable == other.isNillable
					&& super.equals(other)) {
				return true;
			}
		}

		return false;
	}
	
	@Override
	public int hashCode() {
		return (isTypeCastable ? 1 : 0) ^ (isNillable ? 1 : 0) ^ super.hashCode();
	}


	public String toString() {
		String s = "First";

		if (this.isTypeCastable) {
			s += "(xsi:type)";
		}
		if (this.isNillable) {
			s += "(xsi:nil)";
		}

		return s + super.toString();
	}

}
