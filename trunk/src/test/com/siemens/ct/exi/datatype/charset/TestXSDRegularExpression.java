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

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.datatype.AbstractTestCase;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.exceptions.UnknownElementException;

public class TestXSDRegularExpression extends AbstractTestCase  {

	protected static XSDRegularExpression xsdRegexp = XSDRegularExpression.newInstance();
	
	public TestXSDRegularExpression(String testName) {
		super(testName);
	}
	

	
	public void testPattern1() throws EXIException {
		String regex = "[A-Z][A-Z][A-Z]";// e.g. "ABC"
		xsdRegexp.analyze(regex);
		
		assertFalse(xsdRegexp.isEntireSetOfXMLCharacters());
		
		RestrictedCharacterSet rcs = xsdRegexp.getRestrictedCharacterSet();
		assertTrue(rcs.getCode('A')==0);
		assertTrue(rcs.getCode('L')==11);
		assertTrue(rcs.getCode('Z')==25);
		assertTrue(rcs.getCodingLength()==5);
	}
	
	public void testPattern2() throws EXIException {
		String regex = "[0-9][0-9][0-9][0-9][0-9]"; // e.g. "12345"
		xsdRegexp.analyze(regex);
		
		assertFalse(xsdRegexp.isEntireSetOfXMLCharacters());
		
		RestrictedCharacterSet rcs = xsdRegexp.getRestrictedCharacterSet();
		assertTrue(rcs.getCode('0')==0);
		assertTrue(rcs.getCode('5')==5);
		assertTrue(rcs.getCode('9')==9);
		assertTrue(rcs.getCodingLength()==4);
	}
	
	public void testPattern3() throws EXIException {
		// THREE of the LOWERCASE OR UPPERCASE letters from a to z
		String regex = "[a-zA-Z][a-zA-Z][a-zA-Z]"; // e.g. "aXy"
		xsdRegexp.analyze(regex);
		
		assertFalse(xsdRegexp.isEntireSetOfXMLCharacters());
		
		RestrictedCharacterSet rcs = xsdRegexp.getRestrictedCharacterSet();
		assertTrue(rcs.getCode('0')==Constants.NOT_FOUND);
		assertTrue(rcs.getCode('A')==0);
		assertTrue(rcs.getCode('a')==26);
		assertTrue(rcs.getCodingLength()==6);
	}
	
	
	public void testPattern4() throws EXIException {
		// THREE of the LOWERCASE OR UPPERCASE letters from a to z
		String regex = "[\\-a-zA-Z]"; // e.g. "-" "B"
		xsdRegexp.analyze(regex);
		
		assertFalse(xsdRegexp.isEntireSetOfXMLCharacters());
		
		RestrictedCharacterSet rcs = xsdRegexp.getRestrictedCharacterSet();
		assertTrue(rcs.getCode('0')==Constants.NOT_FOUND);
		assertTrue(rcs.getCode('-')==0);
		assertTrue(rcs.getCode('A')==1);
		assertTrue(rcs.getCode('a')==27);
		assertTrue(rcs.getCode('z')==52);
		assertTrue(rcs.getCodingLength()==6);
	}
	
	public void testPattern5() throws EXIException {
		// ONE of the following letters: x, y, OR z:
		String regex = "[xyz]"; // e.g. "x" "y" "z"
		xsdRegexp.analyze(regex);
		
		assertFalse(xsdRegexp.isEntireSetOfXMLCharacters());
		
		RestrictedCharacterSet rcs = xsdRegexp.getRestrictedCharacterSet();
		assertTrue(rcs.getCharacter(0)=='x');
		assertTrue(rcs.getCharacter(1)=='y');
		assertTrue(rcs.getCharacter(2)=='z');
		assertTrue(rcs.getCodingLength()==2);
	}
	
	public void testPattern6() throws EXIException {
		// zero or more occurrences of lowercase letters from a to z:
		String regex = "([a-z])*";
		xsdRegexp.analyze(regex);
		
		assertFalse(xsdRegexp.isEntireSetOfXMLCharacters());
		
		RestrictedCharacterSet rcs = xsdRegexp.getRestrictedCharacterSet();
		assertTrue(rcs.getCharacter(0)=='a');
		assertTrue(rcs.getCode('c')==2);
		assertTrue(rcs.getCharacter(25)=='z');
		assertTrue(rcs.getCodingLength()==5);
	}
	
	public void testPattern7() throws EXIException {
		// For example, "sToP" will be validated by this pattern, but not "Stop"
		// or "STOP" or "stop":
		String regex = "([a-z][A-Z])+";
		xsdRegexp.analyze(regex);
		
		assertFalse(xsdRegexp.isEntireSetOfXMLCharacters());
		
		RestrictedCharacterSet rcs = xsdRegexp.getRestrictedCharacterSet();
		assertTrue(rcs.getCharacter(0)=='A');
		assertTrue(rcs.getCharacter(25)=='Z');
		assertTrue(rcs.getCode('c')==28);
		assertTrue(rcs.getCodingLength()==6);
	}
	
	public void testPattern8() throws EXIException {
		// "gender" with a restriction. The only acceptable value is male OR
		// female:
		String regex = "male|female";
		xsdRegexp.analyze(regex);
		
		assertFalse(xsdRegexp.isEntireSetOfXMLCharacters());
		
		RestrictedCharacterSet rcs = xsdRegexp.getRestrictedCharacterSet();
		assertTrue(rcs.getCharacter(0)=='a');
		assertTrue(rcs.getCode('c')==Constants.NOT_FOUND);
		assertTrue(rcs.getCodingLength()==3);
		
		try {
			rcs.getCharacter(25);
			fail("");
		} catch (UnknownElementException e) {
		}
	}
	
	public void testPattern9() throws EXIException {
		// "password" with a restriction. There must be exactly eight characters
		// in a row and those characters must be lowercase or uppercase letters
		// from a to z, or a number from 0 to 9:
		String regex = "[a-zA-Z0-9]{8}";
		xsdRegexp.analyze(regex);
		
		assertFalse(xsdRegexp.isEntireSetOfXMLCharacters());
		
		RestrictedCharacterSet rcs = xsdRegexp.getRestrictedCharacterSet();
		assertTrue(rcs.getCharacter(0)=='0');
		assertTrue(rcs.getCharacter(25)=='P');
		assertTrue(rcs.getCode('c')==38);
		assertTrue(rcs.getCodingLength()==6);
	}

	public void testPattern10() throws EXIException {
		// 8 restricted digits
		String regex = "[2-4]{8}";
		xsdRegexp.analyze(regex);
		
		assertFalse(xsdRegexp.isEntireSetOfXMLCharacters());
		
		RestrictedCharacterSet rcs = xsdRegexp.getRestrictedCharacterSet();
		assertTrue(rcs.getCharacter(0)=='2');
		assertTrue(rcs.getCode('4')==2);
		assertTrue(rcs.getCodingLength()==2);
	}
	
	public void testPattern11() throws EXIException {
		// 111 restricted digits
		String regex = "[3-9]{111}";
		xsdRegexp.analyze(regex);
		
		assertFalse(xsdRegexp.isEntireSetOfXMLCharacters());
		
		RestrictedCharacterSet rcs = xsdRegexp.getRestrictedCharacterSet();
		assertTrue(rcs.getCharacter(1)=='4');
		assertTrue(rcs.getCode('4')==1);
		assertTrue(rcs.getCodingLength()==3);
	}
	
	public void testPattern12() throws EXIException {
		// zzzzz...
		String regex = "z*";
		xsdRegexp.analyze(regex);
		
		assertFalse(xsdRegexp.isEntireSetOfXMLCharacters());
		
		RestrictedCharacterSet rcs = xsdRegexp.getRestrictedCharacterSet();
		assertTrue(rcs.getCharacter(0)=='z');
		assertTrue(rcs.getCode('z')==0);
		assertTrue(rcs.getCode('?')==Constants.NOT_FOUND);
		assertTrue(rcs.getCodingLength()==1);
	}
	
	public void testPattern13() throws EXIException {
		//	
		String regex = "\\s";
		xsdRegexp.analyze(regex);
		
		assertFalse(xsdRegexp.isEntireSetOfXMLCharacters());
		
		RestrictedCharacterSet rcs = xsdRegexp.getRestrictedCharacterSet();
		assertTrue(rcs.getCharacter(0)=='\t');
		assertTrue(rcs.getCode(' ')==3);
		assertTrue(rcs.getCode('?')==Constants.NOT_FOUND);
		assertTrue(rcs.getCodingLength()==3);
	}
	
	public void testPattern14() throws EXIException {
		// Number decimal digit
		String regex = "\\d";
		xsdRegexp.analyze(regex);
		
		assertFalse(xsdRegexp.isEntireSetOfXMLCharacters());
		
		RestrictedCharacterSet rcs = xsdRegexp.getRestrictedCharacterSet();
		assertTrue(rcs.getCharacter(0)=='0');
		assertTrue(rcs.getCode('3')==3);
		assertTrue(rcs.getCode('?')==Constants.NOT_FOUND);
		assertTrue(rcs.getCodingLength()==4);
	}
	
	public void testPattern15() throws EXIException {
		// Number decimal digit
		String regex = "\\p{Nd}";
		xsdRegexp.analyze(regex);
		
		assertFalse(xsdRegexp.isEntireSetOfXMLCharacters());
		
		RestrictedCharacterSet rcs = xsdRegexp.getRestrictedCharacterSet();
		assertTrue(rcs.getCharacter(1)=='1');
		assertTrue(rcs.getCode('6')==6);
		assertTrue(rcs.getCode('?')==Constants.NOT_FOUND);
		assertTrue(rcs.getCodingLength()==4);
	}
	
	public void testPattern16() throws EXIException {
		//	
		String regex = "\\i\\c*";
		xsdRegexp.analyze(regex);
		
		assertTrue(xsdRegexp.isEntireSetOfXMLCharacters());
	}
	
	public void testPattern17() throws EXIException {
		// complexEsc \P{ L }
		String regex = "abc\\P{L}def";
		xsdRegexp.analyze(regex);
		
		assertTrue(xsdRegexp.isEntireSetOfXMLCharacters());
	}
	
	public void testPattern18() throws EXIException {
		// language --> ([a-zA-Z]{1,8})(-[a-zA-Z0-9]{1,8})*
		String regex = "([a-zA-Z]{1,8})(-[a-zA-Z0-9]{1,8})*";
		xsdRegexp.analyze(regex);
		
		assertFalse(xsdRegexp.isEntireSetOfXMLCharacters());
		
		RestrictedCharacterSet rcs = xsdRegexp.getRestrictedCharacterSet();
		assertTrue(rcs.getCharacter(0)=='-');
		assertTrue(rcs.getCharacter(1)=='0');
		assertTrue(rcs.getCode('8')==9);
		assertTrue(rcs.getCode('?')==Constants.NOT_FOUND);
		assertTrue(rcs.getCodingLength()==6);
	}
	
	public void testPattern19() throws EXIException {
		// Social Security Number v1
		// Matches: 078-05-1120
		String regex = "[0-9]{3}-[0-9]{2}-[0-9]{4}";
		xsdRegexp.analyze(regex);
		
		assertFalse(xsdRegexp.isEntireSetOfXMLCharacters());
		
		RestrictedCharacterSet rcs = xsdRegexp.getRestrictedCharacterSet();
		assertTrue(rcs.getCharacter(0)=='-');
		assertTrue(rcs.getCharacter(1)=='0');
		assertTrue(rcs.getCode('8')==9);
		assertTrue(rcs.getCode('?')==Constants.NOT_FOUND);
		assertTrue(rcs.getCodingLength()==4);
	}
	
	public void testPattern20() throws EXIException {
		// Social Security Number v2
		// Matches: 078-05-1120 | 078 05 1120 | 078051120
		String regex = "[0-9]{3}(-| )?[0-9]{2}(-| )?[0-9]{4}";
		xsdRegexp.analyze(regex);
		
		assertFalse(xsdRegexp.isEntireSetOfXMLCharacters());
		
		RestrictedCharacterSet rcs = xsdRegexp.getRestrictedCharacterSet();
		assertTrue(rcs.getCharacter(0)==' ');
		assertTrue(rcs.getCharacter(1)=='-');
		assertTrue(rcs.getCharacter(2)=='0');
		assertTrue(rcs.getCode('8')==10);
		assertTrue(rcs.getCode('?')==Constants.NOT_FOUND);
		assertTrue(rcs.getCodingLength()==4);
	}

	
	public void testPattern21() throws EXIException {
		// Partial Pattern in OpenOffice
		// ([$]?([^\. ']+|'[^']+'))
		String regex = "([$]?([^\\. ']+|'[^']+'))";
		xsdRegexp.analyze(regex);
		
		//	TODO.. what is it ?
		assertTrue(xsdRegexp.isEntireSetOfXMLCharacters());
	}
	
	public void testPattern22() throws EXIException {
		// Partial Pattern in OpenOffice
		// ?\.[$]?[A-Z]+[$]?[0-9]+
		String regex = "?\\.[$]?[A-Z]+[$]?[0-9]+";
		xsdRegexp.analyze(regex);
		
		assertFalse(xsdRegexp.isEntireSetOfXMLCharacters());
	}
	
	
	public void testPattern23() throws EXIException {
		// Pattern in OpenOffice
		// ([$]?([^\. ']+|'[^']+'))?\.[$]?[A-Z]+[$]?[0-9]+
		String regex = "([$]?([^\\. ']+|'[^']+'))?\\.[$]?[A-Z]+[$]?[0-9]+";
		xsdRegexp.analyze(regex);
		
		//	TODO.. what is it ?
		assertTrue(xsdRegexp.isEntireSetOfXMLCharacters());
	}
	
	public void testPatternSubtraction1() throws EXIException {
		// [A-Z-[C-X]]
		// means: A, B, Y, Z 
		String regex = "[A-Z-[C-X]]";
		xsdRegexp.analyze(regex);
		
		assertFalse(xsdRegexp.isEntireSetOfXMLCharacters());
		
		RestrictedCharacterSet rcs = xsdRegexp.getRestrictedCharacterSet();
		assertTrue(rcs.getCharacter(0)=='A');
		assertTrue(rcs.getCharacter(1)=='B');
		assertTrue(rcs.size()==4);
		assertTrue(rcs.getCharacter(2)=='Y');
		assertTrue(rcs.getCharacter(3)=='Z');
		assertTrue(rcs.getCode('?')==Constants.NOT_FOUND);
		assertTrue(rcs.getCodingLength()==3);
	}
	
	public void testPatternSubtraction2() throws EXIException {
		// [A-Z-[C-X-[M-N]]]*
		// means: A,B,M,N,Y,Z 
		String regex = "[A-Z-[C-X-[M-N]]]*";
		xsdRegexp.analyze(regex);
		
		assertFalse(xsdRegexp.isEntireSetOfXMLCharacters());
		
		RestrictedCharacterSet rcs = xsdRegexp.getRestrictedCharacterSet();
		assertTrue(rcs.getCharacter(0)=='A');
		assertTrue(rcs.getCharacter(1)=='B');
		assertTrue(rcs.getCharacter(2)=='M');
		assertTrue(rcs.getCharacter(3)=='N');
		assertTrue(rcs.getCharacter(4)=='Y');
		assertTrue(rcs.getCharacter(5)=='Z');
		assertTrue(rcs.size()==6);
		assertTrue(rcs.getCode('?')==Constants.NOT_FOUND);
		assertTrue(rcs.getCodingLength()==3);
	}
	
	
	public void testPatternInvalid1() throws EXIException {
		try {
			// non-sense
			String regex = "[bla{4}";
			xsdRegexp.analyze(regex);
			fail();
		} catch (EXIException e) {
			//	an exception for invalid regex is expected
		}
	}

}
