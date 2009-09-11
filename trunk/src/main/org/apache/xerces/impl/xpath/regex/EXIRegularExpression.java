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

package org.apache.xerces.impl.xpath.regex;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

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
 * @version 0.3.20090909
 */

// Useful XML Schema RegExpr Links
// http://www.xmlschemareference.com/regularExpression.html
// http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/#regexs

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

	public EXIRegularExpression(String regex) {
		super(regex, "X");
		// init set
		set = new HashSet<Integer>();
		isRestrictedSet = true;
		// analyze set
		handleToken(this.tokentree, set);
	}


	// TODO detect whether we have ONLY BMP characters
	public boolean isEntireSetOfXMLCharacters() {
		return !(isRestrictedSet && set.size() <= MAX_NUMBER_OF_CHARACTERS);
	}

	public Set<Integer> getCodePoints() {
		return set;
	}

	protected void handleToken(Token t, Set<Integer> s) {
		// abort processing
		if (!isRestrictedSet || s.size() > MAX_NUMBER_OF_CHARACTERS) {
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
			s.add(cht.getChar());
			break;
		case Token.STRING:
			StringToken st = (StringToken) t;
			String str = st.getString();
			for (int i = 0; i < str.length(); i++) {
				s.add(str.codePointAt(i));
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
					s.add(codePoint);
				}
			}
			break;
		case Token.UNION: // X|Y|Z
		case Token.CONCAT: // XY
			if (t instanceof UnionToken) {
				UnionToken ut = (UnionToken) t;
				@SuppressWarnings("unchecked")
				Vector children = ut.children;
				for (int i = 0; i < children.size(); i++) {
					Token subToken = (Token) children.get(i);
					handleToken(subToken, s);
				}
			} else {
				assert (t instanceof ConcatToken);
				ConcatToken cot = (ConcatToken) t;
				handleToken(cot.child, s);
			}
			break;
		case Token.CLOSURE:
			ClosureToken clt = (ClosureToken) t; // "[0-9]{3}" or ".*" or ...
			handleToken(clt.child, s);
			break;
		case Token.PAREN:
			ParenToken pt = (ParenToken) t;// (X) or (?:X)
			handleToken(pt.child, s);
			break;
		case Token.EMPTY:
			break;
		default:
			throw new RuntimeException("[EXI] RegExprToken " + t
					+ " not handled!");
		}
	}

}
