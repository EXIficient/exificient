package com.siemens.ct.exi.datatype.charset;

import java.util.Set;
import java.util.TreeSet;

import com.siemens.ct.exi.exceptions.EXIException;

/**
 * XML Schema - Regular Expressions parser <br />
 * 
 * Analyzes an XML Schema Pattern/Facet and returns an EXI related restricted
 * character set (if possible).
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20090421
 */

/*
 * Useful links: http://www.xmlschemareference.com/regularExpression.html
 * http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/#regexs
 */
public class XSDRegularExpression {

	protected Set<Character> charSet;
	protected boolean startRange;
	protected boolean startCount;
	protected char lastChar;
	protected char preLastChar;

	protected String charProp;

	protected boolean entireSetOfXMLCharacters;

	protected XSDRegularExpression() {
		charSet = new TreeSet<Character>();
	}

	public static XSDRegularExpression newInstance() {
		return new XSDRegularExpression();
	}

	public void analyze(String regex) throws EXIException {
		walk(regex);
	}

	public boolean isEntireSetOfXMLCharacters() {
		return entireSetOfXMLCharacters;
	}

	public RestrictedCharacterSet getRestrictedCharacterSet()
			throws EXIException {
		if (entireSetOfXMLCharacters) {
			throw new EXIException(
					"EXI-regexp, no restricted character set available");
		}
		return new CharacterSet(charSet);
	}

	protected void walk(String regex) throws EXIException {
		if (regex == null || regex.length() == 0) {
			throw new EXIException("EXI-regexp, invalid exp: " + regex);
		} else {
			charSet.clear();
			startRange = false;
			startCount = false;
			entireSetOfXMLCharacters = false;
			lastChar = '?';
			preLastChar = '?';

			walk(regex, 0);

			/*
			 * If the resulting character set contains less than 255 characters,
			 * the string value has a restricted character set and each
			 * character is represented using an n-bit Unsigned Integer
			 */
			if (entireSetOfXMLCharacters || charSet.size() >= 255) {
				charSet.clear();
				entireSetOfXMLCharacters = true;
			}

			// System.out.println(regex + " --> " + charSet);
		}
	}

	protected void walk(String regex, int pos) throws EXIException {

		char c = regex.charAt(pos);

		// EXI specification excerpt:
		// based on empirical observations that are introduced here to identify
		// certain regexps that are not subject to the computation of charsets.
		// If any atom itself is or contains one of the following character
		// groups directly or indirectly, the charset of the whole regexp is
		// defined to be the entire set of XML characters.
		// 
		// * All multi-character escapesXS2 (including meta-character '.')
		// except for '\s' and '\d'.
		// * All category escapesXS2 that carry one of the following character
		// properties.
		// o All category names that are of the forms: 'L'[ulo]?, 'M'[n]?, 'N',
		// 'P', 'Z', 'S'[mo]? or 'C'[o]? .
		// o The following block names: Ethiopic,
		// UnifiedCanadianAboriginalSyllabics, CJKUnifiedIdeographs,
		// CJKCompatibilityIdeographs, ArabicPresentationForms-A,
		// CJKUnifiedIdeographsExtensionA, YiSyllables, HangulSyllables and
		// PrivateUse.
		// * complEscXS2 (examples of which are '\P{ L }' and '\P{ N }' ).
		// * negCharGroupXS2 as indicated by meta-character '^'. See [XSD:15].

		if (lastChar == '\\') {
			// "\" Precedes a metacharacter (to specify THAT character)
			if (c == '\\' || c == '.' || c == '?' || c == '*' || c == '+'
					|| c == '-' || c == '|' || c == '^' || c == '(' || c == ')'
					|| c == '{' || c == '}' || c == '[' || c == ']') {
				// add THAT character to set
				addChar(c);

				if (c == '\\') {
					// override current char to avoid interpreting next char as
					// metachar again
					c = ' ';
				}
			}
			/*
			 * Single Character Escape Sequence
			 */
			else if (c == 'n') {
				// \n New line character (&#xA;): line feed
				addChar('\n');
			} else if (c == 'r') {
				// \r Return character (&#xD;): carriage return
				addChar('\r');
			} else if (c == 't') {
				// \t Tab character (&#x9;)
				addChar('\t');
			}
			/*
			 * Multiple Character Escape Sequences
			 * http://www.w3.org/TR/2000/WD-xml-2e-20000814#CharClasses
			 */
			else if (c == 's') {
				// \s Whitespace, specifically '&#20;' (space), '\t' (tab), '\n'
				// (newline) and '\r' (return).
				// [#x20\t\n\r]
				addChar(' ');
				addChar('\t');
				addChar('\n');
				addChar('\r');
			} else if (c == 'd') {
				// \d Any Decimal digit. A shortcut for '\p{Nd}'.
				// equivalent to the class [0-9]
				inclusiveCharacterCategories("Nd");
			} else if (c == 'S') {
				// \S Any character except those matched by '\s'.
				entireSetOfXMLCharacters = true;
			} else if (c == 'i') {
				// \i The first character in an XML identifier. Specifically,
				// any
				// letter, the character '_', or the character ':', See the XML
				// Recommendation for the complex specification of a letter.
				// This
				// character represents a subset of letter that might appear in
				// '\c'.
				// the set of initial name characters, those ·match·ed by Letter
				// | '_' | ':'
				entireSetOfXMLCharacters = true;
			} else if (c == 'I') {
				// \I Any character except those matched by '\i'.
				entireSetOfXMLCharacters = true;
			} else if (c == 'c') {
				// \c Any character that might appear in the built-in NMTOKEN
				// datatype. See the XML Recommendation for the complex
				// specification of a NameChar.
				entireSetOfXMLCharacters = true;
			} else if (c == 'C') {
				// \C Any character except those matched by '\c'.
				entireSetOfXMLCharacters = true;
			} else if (c == 'D') {
				// \D Any character except those matched by '\d'.
				entireSetOfXMLCharacters = true;
			} else if (c == 'w') {
				// \w Any character that might appear in a word. A shortcut for
				// '[#X0000-#x10FFFF]-[\p{P}\p{Z}\p{C}]' (all characters except
				// the
				// set of "punctuation", "separator", and "other" characters).
				// equivalent to the class [a-zA-Z0-9_].
				entireSetOfXMLCharacters = true;
			} else if (c == 'W') {
				// \W Any character except those matched by '\w'.
				entireSetOfXMLCharacters = true;
			}
			/*
			 * Character Categories
			 */
			else if (c == 'p') {
				// \p{?} inclusive character category
				// inclusive character category
				// e.g. an inclusive character category that represents any
				// uppercase letter looks like the following: \p{Lu}
				charProp = "";

				pos++;
				c = regex.charAt(pos);
				assert (c == '{');

				while ((c = regex.charAt(pos + 1)) != '}') {
					charProp += c;
					pos++;
				}
				inclusiveCharacterCategories(charProp);
			} else if (c == 'P') {
				// \P{?} exclusive character category
				// exclusive character category
				// e.g. an exclusive category that represents any character
				// except an uppercase letter looks like the following: \P{Lu}
				entireSetOfXMLCharacters = true;
			}
			/*
			 * XML Pattern invalid ?
			 */
			else {
				throw new EXIException("EXI-regexp, Unknown escape character: "
						+ c);
			}
		} else {
			// no preceding "\"
			if (c == '.') {
				// . Any character except '\n' (newline) and '\r' (return).
				// . Match any character as defined by The Unicode Standard
				// a.c "aXc", "a9c"
				entireSetOfXMLCharacters = true;
			} else
			/*
			 * Metacharacters
			 */
			if (c == '\\' || c == '?' || c == '*' || c == '+' || c == '|'
					|| c == '(' || c == ')') {
				// \ escape character ?
				// ? Zero or one occurrences. ab?c "ac" "abc"
				// * Zero or more occurrences. ab*c "ac" "abc" "abbbbbc"
				// + One or more occurrences. ab+c "abc" "abbbbbc"
				// | The "or" operator ab|cd "ab" "cd"
				// ( Start grouping. a(b|c)d "abd" "acd"
				// ) End grouping. a(b|c)d "abd" "acd"
			} else if (c == '^') {
				// ^
				// negCharGroupXS2 as indicated by meta-character '^'. See
				// [XSD:15].
				entireSetOfXMLCharacters = true;
			} else if (c == '[') {
				// [ Start range. xx[A-Z]*xx "xxABCDxx"
				startRange = true;
			} else if (c == ']') {
				// ] End range. xx[A-Z]*xx "xxABCDxx"
				startRange = false;
			} else if (startRange && c == '-') {
				// - range hyphen
			} else if (c == '{') {
				startCount = true;
			} else if (c == '}') {
				startCount = false;
			} else {
				if (startCount) {
					// do not add character --> is part of a counter e.g. ?{8}
				} else if (startRange
						&& (lastChar == '-' && preLastChar != '\\')) {
					// walk from range start to current char
					for (char i = (char) (preLastChar + 1); i <= c; i = (char) (i + 1)) {
						addChar(i);
					}
				} else {
					addChar(c);
				}
			}
		}

		preLastChar = lastChar;
		lastChar = c;

		// keep on going ?
		if (!entireSetOfXMLCharacters && regex.length() > (pos + 1)) {
			walk(regex, pos + 1);
		}
	}

	protected void addChar(char c) {
		if (!charSet.contains(c)) {
			charSet.add(c);
		}
	}

	protected void inclusiveCharacterCategories(String charProp)
			throws EXIException {
		// inclusive character category
		// e.g. an inclusive character category that represents any
		// uppercase letter looks like the following: \p{Lu}

		if ("Nd".equals(charProp)) {
			addChar('0');
			addChar('1');
			addChar('2');
			addChar('3');
			addChar('4');
			addChar('5');
			addChar('6');
			addChar('7');
			addChar('8');
			addChar('9');
		} else {
			throw new EXIException("EXI-regexp, \\p{" + charProp
					+ "} not handled yet!");
		}

		// http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/#dt-ccescat

		// L Letter, Any
		// Lu Letter, Uppercase
		// Ll Letter, Lowercase
		// Lt Letter, Titlecase
		// Lm Letter, Modifier
		// Lo Letter, Other
		// L Letter, uppercase, lowercase, and titlecase letters (Lu, Ll, and
		// Lt) Optional in The Unicode Standard; not supported by the Schema
		// Recommendation.
		// M Mark, Any
		// Mn Mark, Nonspacing
		// Mc Mark, Spacing Combining
		// Me Mark, Enclosing
		// N Number, Any
		// Nd Number, Decimal Digit
		// Nl Number, Letter
		// No Number, Other
		// P Punctuation, Any
		// Pc Punctuation, Connector
		// Pd Punctuation, Dash
		// Ps Punctuation, Open
		// Pe Punctuation, Close
		// Pi Punctuation, Initial quote (may behave like Ps or Pe, depending on
		// usage)
		// Pf Punctuation, Final quote (may behave like Ps or Pe, depending on
		// usage)
		// Po Punctuation, Other
		// S Symbol, Any
		// Sm Symbol, Math
		// Sc Symbol, Currency
		// Sk Symbol, Modifier
		// So Symbol, Other
		// Z Separator, Any
		// Zs Separator, Space
		// Zl Separator, Line
		// Zp Separator, Paragraph
		// C Other, Any
		// Cc Other, Control
		// Cf Other, Format
		// Cs Other, Surrogate (not supported by Schema Recommendation).
		// Explicitly not supported by Schema Recommendation.
		// Co Other, Private Use
		// Cn Other, Not Assigned (no characters in the file have this
		// property).

	}

}
