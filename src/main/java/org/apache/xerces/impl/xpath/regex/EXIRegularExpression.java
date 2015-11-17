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
 * XML Schema - Regular Expression parser <br>
 * 
 * Analyzes an XML Schema Pattern/Facet and returns an EXI related restricted
 * character set (if possible).
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.6-SNAPSHOT
 */

// Useful XML Schema RegExpr Links
// http://www.xmlschemareference.com/regularExpression.html
// http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/#regexs
// http://www.unicode.org/Public/3.1-Update/UnicodeData-3.1.0.txt
public class EXIRegularExpression extends RegularExpression {

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
	static String IsCategory = "(" + Letters + ")|(" + Marks + ")|(" + Numbers
			+ ")|(" + Punctuation + ")|(" + Separators + ")|(" + Symbols
			+ ")|(" + Others + ")";
	// [36] IsBlock ::= 'Is' [a-zA-Z0-9#x2D]+
	// #x2D == '-'
	static String IsBlock = "Is" + "[a-zA-Z0-9-]+";
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
			if (start > 0 && regex.charAt(start) == '\\'
					&& regex.charAt(start - 1) == '\\') {
				// two consecutive backslashes ONLY
			} else {
				isRestrictedSet = false;
				return;
			}
		}

		// analyze set
		handleToken(this.tokentree);
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
	
	@Override
    public boolean equals(Object obj) {
		if ( super.equals(obj) ) {
			if (!(obj instanceof EXIRegularExpression))
	            return false;
	        EXIRegularExpression r = (EXIRegularExpression)obj;
	        return (this.set.equals(r.set));
		} else {
			return false;
		}
		
		
//        if (obj == null)  return false;
//        if (!(obj instanceof EXIRegularExpression))
//            return false;
//        EXIRegularExpression r = (EXIRegularExpression)obj;
//        return this.regex.equals(r.regex) && this.options == r.options;
    }
	
	@Override
    public int hashCode() {
		return super.hashCode() ^ set.hashCode();
//		return set.hashCode();
    }
    
    

}
