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

package com.siemens.ct.exi.datatype.charset;

import java.util.HashMap;
import java.util.Map;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.exceptions.UnknownElementException;
import com.siemens.ct.exi.util.MethodsBag;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20090421
 */

public abstract class AbstractRestrictedCharacterSet implements RestrictedCharacterSet {
	// #x9, HT (horizontal tab)
	// #xA, LF (line-feed)
	// #xD, CR (carriage-return)
	// #x20, SP (space)

	protected Map<Character, Integer> codeSet;
	protected Map<Integer, Character> characterSet;

	protected int size;
	protected int codingLength;

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

	protected AbstractRestrictedCharacterSet() {
		codeSet = new HashMap<Character, Integer>();
		characterSet = new HashMap<Integer, Character>();
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

}
