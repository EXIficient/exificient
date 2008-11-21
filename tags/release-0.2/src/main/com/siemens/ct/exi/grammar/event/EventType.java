/*
 * Copyright (C) 2007, 2008 Siemens AG
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

package com.siemens.ct.exi.grammar.event;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20080718
 */

public enum EventType {
	// 8.5.3.3 Event Code Assignment
	//	
	// Sort all productions with G i, j on the left hand side in the following
	// order:
	//
	// 1. All productions with AT(qname) on the right hand side sorted lexically
	// by qname localName, then by qname uri, followed by
	// 2. any production with AT(*) on the right hand side, followed by
	// 3. all productions with SE(qname) on the right hand side sorted in schema
	// order, followed by
	// 5. any production with EE on the right hand side, followed by
	// 4. any production with CH on the right hand side

	// TODO SE(qname) in schema order?

	// DO NOT CHANGE ORDER!!
	// !! Ordinal is used for sorting !!

	/*
	 * Order? SD || ED AT(), AT, ... SE(), SE, ... EE CH
	 */
	START_DOCUMENT, END_DOCUMENT,

	ATTRIBUTE_XSI_TYPE, /* 2nd level */
	ATTRIBUTE_XSI_NIL, ATTRIBUTE_XSI_NIL_DEVIATION, ATTRIBUTE, ATTRIBUTE_GENERIC, ATTRIBUTE_GENERIC_UNDECLARED, ATTRIBUTE_INVALID_VALUE,

	START_ELEMENT, START_ELEMENT_GENERIC, START_ELEMENT_GENERIC_UNDECLARED, /*
																			 * 2nd
																			 * level
																			 */

	END_ELEMENT, END_ELEMENT_UNDECLARED,

	CHARACTERS_GENERIC, CHARACTERS, CHARACTERS_GENERIC_UNDECLARED, /* 2nd level */

	NAMESPACE_DECLARATION, SELF_CONTAINED, COMMENT, PROCESSING_INSTRUCTION, DOC_TYPE, ENTITY_REFERENCE,

	LAMBDA;
}
