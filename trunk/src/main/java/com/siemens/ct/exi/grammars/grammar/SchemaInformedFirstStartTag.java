/*
 * Copyright (C) 2007-2015 Siemens AG
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

package com.siemens.ct.exi.grammars.grammar;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.grammars.production.Production;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.5-SNAPSHOT
 */

public class SchemaInformedFirstStartTag extends SchemaInformedStartTag
		implements SchemaInformedFirstStartTagGrammar, Cloneable {

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

	public QName getTypeName() {
		return this.typeName;
	}

	public void setTypeName(QName typeName) {
		this.typeName = typeName;
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
		return this.typeEmpty;
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
