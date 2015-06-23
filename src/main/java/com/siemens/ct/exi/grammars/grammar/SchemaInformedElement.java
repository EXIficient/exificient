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
