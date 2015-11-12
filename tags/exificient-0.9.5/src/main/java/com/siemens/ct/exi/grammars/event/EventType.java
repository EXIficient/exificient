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

package com.siemens.ct.exi.grammars.event;

/**
 * <p>
 * EventTypes used to represent XML information items in EXI (see <a
 * href="http://www.w3.org/TR/exi/#eventCodeAssignment"> EXI Event Code
 * Assignment</a>).</p>
 * 
 * <p>
 * Sort all productions with G i, j on the left hand side in the following
 * order:
 * </p>
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
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.5
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
	 * END_DOCUMENT
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
	PROCESSING_INSTRUCTION
}
