/*
 * Copyright (C) 2007-2014 Siemens AG
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
 * @version 0.9.3-SNAPSHOT
 */

public enum GrammarType {
	/* Root grammars*/
	DOCUMENT,
	FRAGMENT,
	DOC_END,
	/* Schema-informed Document and Fragment Grammars */
	SCHEMA_INFORMED_DOC_CONTENT,
	SCHEMA_INFORMED_FRAGMENT_CONTENT,
	/* Schema-informed Element and Type Grammars */
	SCHEMA_INFORMED_FIRST_START_TAG_CONTENT,
	SCHEMA_INFORMED_START_TAG_CONTENT,
	SCHEMA_INFORMED_ELEMENT_CONTENT,
	/* Built-in Document and Fragment Grammars */
	BUILT_IN_DOC_CONTENT,
	BUILT_IN_FRAGMENT_CONTENT,
	/* Built-in Element Grammars */
	BUILT_IN_START_TAG_CONTENT,
	BUILT_IN_ELEMENT_CONTENT,
}
