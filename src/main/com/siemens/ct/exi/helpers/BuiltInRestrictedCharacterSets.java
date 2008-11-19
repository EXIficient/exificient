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

package com.siemens.ct.exi.helpers;

import java.util.HashMap;
import java.util.Map;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.datatype.RestrictedCharacterSet;
import com.siemens.ct.exi.exceptions.UnknownElementException;
import com.siemens.ct.exi.util.MethodsBag;
import com.siemens.ct.exi.util.xml.XMLWhitespace;

public class BuiltInRestrictedCharacterSets implements RestrictedCharacterSet {
	// #x9, HT (horizontal tab)
	// #xA, LF (line-feed)
	// #xD, CR (carriage-return)
	// #x20, SP (space)

	protected Map<Character, Integer> codeSet;
	protected Map<Integer, Character> characterSet;

	protected int size;
	protected int codingLength;

	protected BuiltInRestrictedCharacterSets() {
		codeSet = new HashMap<Character, Integer>();
		characterSet = new HashMap<Integer, Character>();
	}

	/*
	 * xsd:base64Binary { #x9, #xA, #xD, #x20, +, /, [0-9], =, [A-Z], [a-z] }
	 */
	public static RestrictedCharacterSet newXSDBase64BinaryInstance() {
		BuiltInRestrictedCharacterSets rcs = new BuiltInRestrictedCharacterSets();

		// #x9, #xA, #xD, #x20
		rcs.addValue(XMLWhitespace.WS_TAB);
		rcs.addValue(XMLWhitespace.WS_NL);
		rcs.addValue(XMLWhitespace.WS_CR);
		rcs.addValue(XMLWhitespace.WS_SPACE);
		// +, /
		rcs.addValue('+');
		rcs.addValue('/');
		// [0-9]
		for (int i = '0'; i <= '9'; i++) {
			rcs.addValue((char) i);
		}
		// =
		rcs.addValue('=');
		// [A-Z]
		for (int i = 'A'; i <= 'Z'; i++) {
			rcs.addValue((char) i);
		}
		// [a-z]
		for (int i = 'a'; i <= 'z'; i++) {
			rcs.addValue((char) i);
		}

		return rcs;
	}

	/*
	 * xsd:hexBinary { #x9, #xA, #xD, #x20, [0-9], [A-F], [a-f] }
	 */
	public static RestrictedCharacterSet newXSDHexBinaryInstance() {
		BuiltInRestrictedCharacterSets rcs = new BuiltInRestrictedCharacterSets();

		// #x9, #xA, #xD, #x20
		rcs.addValue(XMLWhitespace.WS_TAB);
		rcs.addValue(XMLWhitespace.WS_NL);
		rcs.addValue(XMLWhitespace.WS_CR);
		rcs.addValue(XMLWhitespace.WS_SPACE);
		// [0-9]
		for (int i = '0'; i <= '9'; i++) {
			rcs.addValue((char) i);
		}
		// [A-F]
		for (int i = 'A'; i <= 'F'; i++) {
			rcs.addValue((char) i);
		}
		// [a-f]
		for (int i = 'a'; i <= 'f'; i++) {
			rcs.addValue((char) i);
		}

		return rcs;
	}

	/*
	 * xsd:boolean { #x9, #xA, #xD, #x20, 0, 1, a, e, f, l, r, s, t, u }
	 */
	public static RestrictedCharacterSet newXSDBooleanInstance() {
		BuiltInRestrictedCharacterSets rcs = new BuiltInRestrictedCharacterSets();

		// #x9, #xA, #xD, #x20
		rcs.addValue(XMLWhitespace.WS_TAB);
		rcs.addValue(XMLWhitespace.WS_NL);
		rcs.addValue(XMLWhitespace.WS_CR);
		rcs.addValue(XMLWhitespace.WS_SPACE);
		// 0, 1
		rcs.addValue('0');
		rcs.addValue('1');
		// a, e, f, l, r, s, t, u
		rcs.addValue('a');
		rcs.addValue('e');
		rcs.addValue('f');
		rcs.addValue('l');
		rcs.addValue('r');
		rcs.addValue('s');
		rcs.addValue('t');
		rcs.addValue('u');

		return rcs;
	}

	/*
	 * xsd:dateTime { #x9, #xA, #xD, #x20, +, -, ., [0-9], :, T, Z }
	 */
	public static RestrictedCharacterSet newXSDDateTimeInstance() {
		BuiltInRestrictedCharacterSets rcs = new BuiltInRestrictedCharacterSets();

		// #x9, #xA, #xD, #x20
		rcs.addValue(XMLWhitespace.WS_TAB);
		rcs.addValue(XMLWhitespace.WS_NL);
		rcs.addValue(XMLWhitespace.WS_CR);
		rcs.addValue(XMLWhitespace.WS_SPACE);
		// +, -, .
		rcs.addValue('+');
		rcs.addValue('-');
		rcs.addValue('.');
		// [0-9]
		for (int i = '0'; i <= '9'; i++) {
			rcs.addValue((char) i);
		}
		// :, T, Z
		rcs.addValue(':');
		rcs.addValue('T');
		rcs.addValue('Z');

		return rcs;
	}

	/*
	 * xsd:decimal { #x9, #xA, #xD, #x20, +, -, ., [0-9] }
	 */
	public static RestrictedCharacterSet newXSDDecimalInstance() {
		BuiltInRestrictedCharacterSets rcs = new BuiltInRestrictedCharacterSets();

		// #x9, #xA, #xD, #x20
		rcs.addValue(XMLWhitespace.WS_TAB);
		rcs.addValue(XMLWhitespace.WS_NL);
		rcs.addValue(XMLWhitespace.WS_CR);
		rcs.addValue(XMLWhitespace.WS_SPACE);
		// +, -, .
		rcs.addValue('+');
		rcs.addValue('-');
		rcs.addValue('.');
		// [0-9]
		for (int i = '0'; i <= '9'; i++) {
			rcs.addValue((char) i);
		}

		return rcs;
	}

	/*
	 * xsd:double { #x9, #xA, #xD, #x20, +, -, ., [0-9], E, F, I, N, a, e }
	 */
	public static RestrictedCharacterSet newXSDDoubleInstance() {
		BuiltInRestrictedCharacterSets rcs = new BuiltInRestrictedCharacterSets();

		// #x9, #xA, #xD, #x20
		rcs.addValue(XMLWhitespace.WS_TAB);
		rcs.addValue(XMLWhitespace.WS_NL);
		rcs.addValue(XMLWhitespace.WS_CR);
		rcs.addValue(XMLWhitespace.WS_SPACE);
		// +, -, .
		rcs.addValue('+');
		rcs.addValue('-');
		rcs.addValue('.');
		// [0-9]
		for (int i = '0'; i <= '9'; i++) {
			rcs.addValue((char) i);
		}
		// E, F, I, N, a, e
		rcs.addValue('E');
		rcs.addValue('F');
		rcs.addValue('I');
		rcs.addValue('N');
		rcs.addValue('a');
		rcs.addValue('e');

		return rcs;
	}

	/*
	 * xsd:integer { #x9, #xA, #xD, #x20, +, -, [0-9] }
	 */
	public static RestrictedCharacterSet newXSDIntegerInstance() {
		BuiltInRestrictedCharacterSets rcs = new BuiltInRestrictedCharacterSets();

		// #x9, #xA, #xD, #x20
		rcs.addValue(XMLWhitespace.WS_TAB);
		rcs.addValue(XMLWhitespace.WS_NL);
		rcs.addValue(XMLWhitespace.WS_CR);
		rcs.addValue(XMLWhitespace.WS_SPACE);
		// +, -
		rcs.addValue('+');
		rcs.addValue('-');
		// [0-9]
		for (int i = '0'; i <= '9'; i++) {
			rcs.addValue((char) i);
		}

		return rcs;
	}

	public char getCharacter(int code) throws UnknownElementException {
		Character character = characterSet.get(code);

		if (character == null) {
			throw new UnknownElementException(
					"Unknown RestrictedCharacterSet code: " + code);
		} else {
			return character;
		}
	}

	public int getCode(char c) {
		Integer code = codeSet.get(c);

		return (code == null ? Constants.NOT_FOUND : code);
	}

	public int size() {
		return size;
	}

	public int getCodingLength() {
		return codingLength;
	}

	protected void addValue(char c) {
		codeSet.put(c, codeSet.size());
		characterSet.put(characterSet.size(), c);

		// adjust size / codingLength
		assert (codeSet.size() == characterSet.size());
		size = codeSet.size();
		codingLength = MethodsBag.getCodingLength(size + 1);
	}

	/*
	 * TODO: If the restricted character set for a datatype contains at least
	 * 255 characters or contains non-BMP characters, the character set of the
	 * datatype is not restricted and can be omitted from further consideration.
	 */

	/*
	 * The characters in the restricted character set are sorted by UCS [ISO/IEC
	 * 10646] code point and represented by integer values in the range (0 ...
	 * N-1) according to their ordinal position in the set. Characters that are
	 * not in this set are represented by the integer N followed by the UCS code
	 * point of the character represented as an Unsigned Integer.
	 */

	// public static void main ( String[] args )
	// {
	// RestrictedCharacterSet rcsBoolean =
	// RestrictedCharacterSet.newXSDBooleanInstance ( );
	// RestrictedCharacterSet rcsBase64Binary =
	// RestrictedCharacterSet.newXSDBase64BinaryInstance ( );
	//
	// // String s = " \r  \n true \t  XX";
	// String s = " \r  \n true \t";
	//
	// int i = 0;
	// int code = 0;
	//
	// while ( code != Constants.NOT_FOUND && i < s.length ( ) )
	// {
	// char c = s.charAt ( i );
	// code = rcsBoolean.getCode ( c );
	// System.out.println ( "'" + c + "' --> " + code );
	// i++;
	// }
	//
	// if ( code == Constants.NOT_FOUND )
	// {
	// // is NOT valid
	// System.out.println ( s + " # is NOT valid" );
	// }
	// else
	// {
	// // is valid
	// System.out.println ( s + " # is valid" );
	// }
	// }
}
