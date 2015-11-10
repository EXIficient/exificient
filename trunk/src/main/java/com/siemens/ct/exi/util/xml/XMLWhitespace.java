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

package com.siemens.ct.exi.util.xml;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.5-SNAPSHOT
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
