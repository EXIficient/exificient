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

package com.siemens.ct.exi.util.xml;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20081111
 */

public class XMLWhitespace {
	public static final char WS_SPACE = ' ';
	public static final char WS_NL = '\n';
	public static final char WS_CR = '\r';
	public static final char WS_TAB = '\t';

	public static int getLeadingWhitespaces(final char[] ch, int start,
			final int length) {
		final int end = start + length;
		int leadingWS = 0;

		while (start < end && isWhiteSpace(ch[start])) {
			start++;
			leadingWS++;
		}

		return leadingWS;
	}

	public static int getTrailingWhitespaces(final char[] ch, final int start,
			final int length) {
		int pos = start + length - 1;
		int trailingWS = 0;

		while (pos >= start && isWhiteSpace(ch[pos])) {
			pos--;
			trailingWS++;
		}

		return trailingWS;
	}

	public static boolean isWhiteSpaceOnly(final char[] ch, int start,
			final int length) {
		if (!isWhiteSpace(ch[start]))
			return false;

		final int end = start + length;
		while (++start < end && isWhiteSpace(ch[start])) {
		}

		return start == end;
	}

	public static boolean isWhiteSpaceOnly(String chars) {
		if (!isWhiteSpace(chars.charAt(0))) {
			return false;
		}

		final int end = chars.length();
		int start = 1;
		while (start < end && isWhiteSpace(chars.charAt(start++))) {
		}

		return start == end;
	}

	public static boolean isWhiteSpace(char c) {
		return (c == WS_SPACE || c == WS_NL || c == WS_CR || c == WS_TAB);
	}
}
