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

package com.siemens.ct.exi.datatype;

/**
 * WhiteSpace constrains the value space of types derived from string. The value
 * of whiteSpace must be one of {preserve, replace, collapse}. For all atomic
 * datatypes other than string (and types derived by restriction from it) the
 * value of whiteSpace is collapse. For string the default value of whiteSpace
 * is preserve. {@link http://www.w3.org/TR/xmlschema-2/#rf-whiteSpace}
 */
public enum WhiteSpace {
	/**
	 * No normalization is done, the value is not changed (this is the behavior
	 * required by [XML 1.0 (Second Edition)] for element content)
	 */
	preserve,
	/**
	 * All occurrences of #x9 (tab), #xA (line feed) and #xD (carriage return)
	 * are replaced with #x20 (space)
	 */
	replace,
	/**
	 * After the processing implied by replace, contiguous sequences of #x20's
	 * are collapsed to a single #x20, and leading and trailing #x20's are
	 * removed.
	 */
	collapse
	/*
	 * Note: The notation #xA used here represents the Universal Character Set
	 * (UCS) code point hexadecimal A (line feed), which is denoted by U+000A.
	 * This notation is to be distinguished from &#xA;, which is the XML
	 * character reference to that same UCS code point.
	 */
}