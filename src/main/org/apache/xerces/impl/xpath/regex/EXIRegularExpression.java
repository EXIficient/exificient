/*
 * Copyright (C) 2007-2010 Siemens AG
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

package org.apache.xerces.impl.xpath.regex;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.xerces.impl.xpath.regex.Token.CharToken;
import org.apache.xerces.impl.xpath.regex.Token.ClosureToken;
import org.apache.xerces.impl.xpath.regex.Token.ConcatToken;
import org.apache.xerces.impl.xpath.regex.Token.ParenToken;
import org.apache.xerces.impl.xpath.regex.Token.StringToken;
import org.apache.xerces.impl.xpath.regex.Token.UnionToken;

/**
 * XML Schema - Regular Expression parser <br />
 * 
 * Analyzes an XML Schema Pattern/Facet and returns an EXI related restricted
 * character set (if possible).
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.5
 */

// Useful XML Schema RegExpr Links
// http://www.xmlschemareference.com/regularExpression.html
// http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/#regexs
// http://www.unicode.org/Public/3.1-Update/UnicodeData-3.1.0.txt
public class EXIRegularExpression extends RegularExpression {

	// Java 1.5 supports Unicode 4.0
	public static final boolean USE_UNICODE_4_0 = true;

	// For stability and interoperability of restricted character sets across
	// different versions of the Unicode standard, certain pattern facets cannot
	// be used for deriving restricted character sets. In particular, pattern
	// facets that contain one or more category escapesXS2, category complement
	// escapesXS2 or multi-character escapesXS2 other than \s do not have
	// restricted character sets.
	public static final boolean USE_STABLE_ESCAPES_ONLY = true;

	/*
	 * If the resulting set of characters contains less than 256 characters and
	 * contains only BMP characters, the string value has a restricted character
	 * set
	 */
	public static final int MAX_NUMBER_OF_CHARACTERS = 255;

	private static final long serialVersionUID = 1L;

	protected Set<Integer> set;

	protected boolean isRestrictedSet;

	/*
	 * 
	 */
	// [29] Letters ::= 'L' [ultmo]?
	static String Letters = "L" + "[ultmo]?";
	// [30] Marks ::= 'M' [nce]?
	static String Marks = "M" + "[nce]?";
	// [31] Numbers ::= 'N' [dlo]?
	static String Numbers = "N" + "[dlo]?";
	// [32] Punctuation ::= 'P' [cdseifo]?
	static String Punctuation = "P" + "[cdseifo]?";
	// [33] Separators ::= 'Z' [slp]?
	static String Separators = "Z" + "[slp]?";
	// [34] Symbols ::= 'S' [mcko]?
	static String Symbols = "S" + "[mcko]?";
	// [35] Others ::= 'C' [cfon]?
	static String Others = "C" + "[cfon]?";
	// [28] IsCategory ::= Letters | Marks | Numbers | Punctuation | Separators
	// | Symbols | Others
	static String IsCategory = "(" + Letters + ")|(" + Marks + ")|("
			+ Numbers + ")|(" + Punctuation + ")|(" + Separators + ")|("
			+ Symbols + ")|(" + Others + ")";
	// [36] IsBlock ::= 'Is' [a-zA-Z0-9#x2D]+
	static String IsBlock = "Is" + "[a-zA-Z0-9#x2D]+";
	// [27] charProp ::= IsCategory | IsBlock
	static String charProp = "(" + IsCategory + ")|(" + IsBlock + ")";
	// [25] catEsc ::= '\p{' charProp '}'
	static String catEsc = "\\\\p\\{(" + charProp + ")\\}";
	// [26] complEsc ::= '\P{' charProp '}'
	static String complEsc = "\\\\P\\{(" + charProp + ")\\}";
	// [37] MultiCharEsc ::= '\' [sSiIcCdDwW]
	// NOTE: no \s
	static String MultiCharEsc2 = "\\\\" + "[SiIcCdDwW]";

	public EXIRegularExpression(String regex) {
		super(regex, "X");
		// init set
		set = new HashSet<Integer>();
		isRestrictedSet = true;

		if (USE_STABLE_ESCAPES_ONLY) {

			// To remove all non-BMP characters, the following should work:
			String sanitizedString = regex.replaceAll("[^\u0000-\uFFFF]", "");
			if (sanitizedString.length() != regex.length()) {
				isRestrictedSet = false;
				return;
			}

			// category escapes
			// http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/#nt-catEsc
			Pattern pCatEsc = Pattern.compile(catEsc);
			Matcher mCatEsc = pCatEsc.matcher(regex);
			if (mCatEsc.find()) {
				isRestrictedSet = false;
				return;
			}

			// category complement escapes
			// http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/#nt-complEsc
			Pattern pComplexEsc = Pattern.compile(complEsc);
			Matcher mComplexEsc = pComplexEsc.matcher(regex);
			if (mComplexEsc.find()) {
				isRestrictedSet = false;
				return;
			}

			// multi-character escapes other than \s
			// http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/#nt-MultiCharEsc
			Pattern pMultiCharEsc2 = Pattern.compile(MultiCharEsc2);
			Matcher mMultiCharEsc2 = pMultiCharEsc2.matcher(regex);
			while (mMultiCharEsc2.find()) {
				int start = mMultiCharEsc2.start();
				if (start > 0 && regex.charAt(start) == '\\' && regex.charAt(start-1) == '\\' ) {
					// two consecutive backslashes ONLY
				} else {
					isRestrictedSet = false;
					return;					
				}
			}

			// analyze set
			handleToken(this.tokentree);

		} else {

			// non-BMP characters ?
			final int lenChars = regex.length();
			for (int i = 0; i < lenChars; i++) {
				final char ch = regex.charAt(i);

				// Is this a UTF-16 surrogate pair?
				if (Character.isHighSurrogate(ch)) {
					isRestrictedSet = false;
					return;
				}
			}

			// analyze set
			handleToken(this.tokentree);

			if (!USE_UNICODE_4_0) {
				// detect whether we deal with BMP characters
				// and whether the characters are part of Unicode 3.1.0
				Set<Integer> toRemove = new HashSet<Integer>();
				for (Integer cp : set) {
					if (!Unicode_3_1_0_BMP.isRelevantCodepoint(cp)) {
						toRemove.add(cp);
						// isRestrictedSet = false;
						// return;
					}
				}
				for (Integer toRem : toRemove) {
					set.remove(toRem);
				}
			}
		}

	}

	protected void addChar(int cp) {
		set.add(cp);
	}

	public boolean isEntireSetOfXMLCharacters() {
		return set.isEmpty()
				|| !(isRestrictedSet && set.size() <= MAX_NUMBER_OF_CHARACTERS);
	}

	public Set<Integer> getCodePoints() {
		return set;
	}

	protected void handleToken(Token t) {
		// abort processing
		if (!isRestrictedSet || set.size() > MAX_NUMBER_OF_CHARACTERS) {
			return;
		}
		switch (t.type) {
		case Token.DOT:
			isRestrictedSet = false;
			break;
		case Token.CHAR:
		case Token.CHAR_FINAL_QUOTE:
		case Token.CHAR_INIT_QUOTE:
		case Token.CHAR_LETTER:
		case Token.CHAR_MARK:
		case Token.CHAR_NUMBER:
		case Token.CHAR_OTHER:
		case Token.CHAR_PUNCTUATION:
		case Token.CHAR_SEPARATOR:
		case Token.CHAR_SYMBOL:
			CharToken cht = (CharToken) t;
			addChar(cht.getChar());
			break;
		case Token.STRING:
			StringToken st = (StringToken) t;
			String str = st.getString();

			// final int lenChars = str.length();
			// for (int i = 0; i<lenChars; i++) {
			// final char ch = str.charAt(i);
			// // Is this a UTF-16 surrogate pair?
			// if (Character.isHighSurrogate(ch)) {
			// // use code-point and increment loop count (2 char's)
			// addChar(str.codePointAt(i++));
			// } else {
			// addChar(ch);
			// }
			// }

			final int L = str.codePointCount(0, str.length());
			// for (int i = 0; i < str.length(); i++) {
			for (int i = 0; i < L; i++) {
				addChar(str.codePointAt(i));
			}
			break;
		case Token.RANGE:
			RangeToken rt = (RangeToken) t;
			int[] ranges = rt.ranges;
			for (int k = 0; k < ranges.length; k += 2) {
				// abort processing due to huge range
				if ((ranges[k + 1] - ranges[k]) > MAX_NUMBER_OF_CHARACTERS) {
					isRestrictedSet = false;
					return;
				}
				for (int codePoint = ranges[k]; codePoint <= ranges[k + 1]; codePoint++) {
					addChar(codePoint);
				}
			}
			break;
		case Token.UNION: // X|Y|Z
		case Token.CONCAT: // XY
			if (t instanceof UnionToken) {
				UnionToken ut = (UnionToken) t;
				@SuppressWarnings({ "rawtypes" })
				Vector children = ut.children;
				for (int i = 0; i < children.size(); i++) {
					Token subToken = (Token) children.get(i);
					handleToken(subToken);
				}
			} else {
				assert (t instanceof ConcatToken);
				ConcatToken cot = (ConcatToken) t;
				handleToken(cot.child);
			}
			break;
		case Token.CLOSURE:
			ClosureToken clt = (ClosureToken) t; // "[0-9]{3}" or ".*" or ...
			handleToken(clt.child);
			break;
		case Token.PAREN:
			ParenToken pt = (ParenToken) t;// (X) or (?:X)
			handleToken(pt.child);
			break;
		case Token.EMPTY:
			break;
		default:
			throw new RuntimeException("[EXI] RegExprToken " + t
					+ " not handled!");
		}
	}

}
