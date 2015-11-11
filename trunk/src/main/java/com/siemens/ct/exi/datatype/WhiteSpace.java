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

package com.siemens.ct.exi.datatype;

/**
 * WhiteSpace constrains the value space of types derived from string. The value
 * of whiteSpace must be one of {preserve, replace, collapse}. For all atomic
 * datatypes other than string (and types derived by restriction from it) the
 * value of whiteSpace is collapse. For string the default value of whiteSpace
 * is preserve. {@link}http://www.w3.org/TR/xmlschema-2/#rf-whiteSpace
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