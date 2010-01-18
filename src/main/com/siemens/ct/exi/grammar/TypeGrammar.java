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

import com.siemens.ct.exi.grammar.rule.SchemaInformedRule;

/**
 * Given an XML Schema type definition T i , two type grammars are created,
 * which are denoted by Type i and TypeEmpty i . Type i is a grammar that fully
 * reflects the type definition of T i , whereas TypeEmpty i is a grammar that
 * accepts only the attribute uses and attribute wildcards of T i , if any.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20080822
 */

public class TypeGrammar {
	protected final SchemaInformedRule type;
	protected final SchemaInformedRule typeEmpty;

	public TypeGrammar(SchemaInformedRule type, SchemaInformedRule typeEmpty) {
		this.type = type;
		this.typeEmpty = typeEmpty;
	}

	public SchemaInformedRule getType() {
		return type;
	}

	public SchemaInformedRule getTypeEmpty() {
		return typeEmpty;
	}
}
