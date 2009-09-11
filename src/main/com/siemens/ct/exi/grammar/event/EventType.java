/*
 * Copyright (C) 2007-2009 Siemens AG
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
 * <p>
 * EventTypes used to represent XML information items in EXI (see <a
 * href="http://www.w3.org/TR/exi/#eventCodeAssignment"> EXI Event Code
 * Assignment</a>).
 * 
 * <br/>
 * 
 * <p>
 * Sort all productions with G i, j on the left hand side in the following
 * order:
 * <ol>
 * <li>all productions with AT(qname) on the right hand side sorted lexically by
 * qname localName, then by qname uri, followed by</li>
 * <li>all productions with AT(uri : *) on the right hand side sorted lexically
 * by uri, followed by</li>
 * <li>any production with AT(*) on the right hand side, followed by</li>
 * <li>all productions with SE(qname) on the right hand side sorted in schema
 * order, followed by</li>
 * <li>all productions with SE(uri : *) on the right hand side sorted in schema
 * order, followed by</li>
 * <li>any production with SE(*) on the right hand side, followed by</li>
 * <li>any production with EE on the right hand side, followed by</li>
 * <li>any production with CH on the right hand side.</li>
 * </ol>
 * </p>
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20090421
 */

// DO NOT CHANGE ORDER!!
// !! Ordinal is used for sorting !!
public enum EventType {
	/*
	 * start document
	 */
	START_DOCUMENT,
	/*
	 * Special XSI Attributes
	 */
	ATTRIBUTE_XSI_TYPE, // xsi:type
	ATTRIBUTE_XSI_NIL, // xsi:nil
	/*
	 * Attributes
	 */
	ATTRIBUTE, // schema-declared
	ATTRIBUTE_NS, // schema-declared but URI only
	ATTRIBUTE_GENERIC, // schema-declared but generic
	ATTRIBUTE_INVALID_VALUE, // schema-declared with qname but invalid
	ATTRIBUTE_ANY_INVALID_VALUE, // schema-declared but invalid
	ATTRIBUTE_GENERIC_UNDECLARED, // schema-undeclared
	/*
	 * StartElements
	 */
	START_ELEMENT, // schema-declared
	START_ELEMENT_NS, // schema-declared but URI only
	START_ELEMENT_GENERIC, // schema-declared but generic
	START_ELEMENT_GENERIC_UNDECLARED, // schema-undeclared
	/*
	 * EndElements
	 */
	END_ELEMENT, // schema-declared
	END_ELEMENT_UNDECLARED, // schema-undeclared
	/*
	 * Characters
	 */
	CHARACTERS, // schema-declared
	CHARACTERS_GENERIC, // schema-declared
	CHARACTERS_GENERIC_UNDECLARED, // schema-undeclared
	/*
	 * 	END_DOCUMENT
	 */
	END_DOCUMENT,
	/*
	 * Features & Fidelity Options
	 */
	DOC_TYPE, // doc-type part of DocContent only
	NAMESPACE_DECLARATION, // ns declaration
	SELF_CONTAINED, // start of self-contained fragment
	ENTITY_REFERENCE, // entity reference
	COMMENT, // 
	PROCESSING_INSTRUCTION,
}
