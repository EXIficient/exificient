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

package com.siemens.ct.exi.core;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.context.GrammarContext;
import com.siemens.ct.exi.context.GrammarUriContext;
import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.exceptions.UnsupportedOption;
import com.siemens.ct.exi.grammars.Grammars;
import com.siemens.ct.exi.grammars.grammar.Grammar;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;

/**
 * EXI Header (see http://www.w3.org/TR/exi/#header)
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.6-SNAPSHOT
 */

public abstract class AbstractEXIHeader {

	public static final String HEADER = "header";
	public static final String LESSCOMMON = "lesscommon";
	public static final String UNCOMMON = "uncommon";
	public static final String ALIGNMENT = "alignment";
	public static final String BYTE = "byte";
	public static final String PRE_COMPRESS = "pre-compress";
	public static final String SELF_CONTAINED = "selfContained";
	public static final String VALUE_MAX_LENGTH = "valueMaxLength";
	public static final String VALUE_PARTITION_CAPACITY = "valuePartitionCapacity";
	public static final String DATATYPE_REPRESENTATION_MAP = "datatypeRepresentationMap";
	public static final String PRESERVE = "preserve";
	public static final String DTD = "dtd";
	public static final String PREFIXES = "prefixes";
	public static final String LEXICAL_VALUES = "lexicalValues";
	public static final String COMMENTS = "comments";
	public static final String PIS = "pis";
	public static final String BLOCK_SIZE = "blockSize";
	public static final String COMMON = "common";
	public static final String COMPRESSION = "compression";
	public static final String FRAGMENT = "fragment";
	public static final String SCHEMA_ID = "schemaId";
	public static final String STRICT = "strict";
	public static final String PROFILE = "p";

	public static final int NUMBER_OF_DISTINGUISHING_BITS = 2;
	public static final int DISTINGUISHING_BITS_VALUE = 2;

	public static final int NUMBER_OF_FORMAT_VERSION_BITS = 4;
	public static final int FORMAT_VERSION_CONTINUE_VALUE = 15;

	protected EXIFactory headerFactory;
	
	protected EXIFactory getHeaderFactory() throws EXIException {
		if (headerFactory == null) {
			headerFactory = DefaultEXIFactory.newInstance();
			headerFactory.setGrammars(new EXIOptionsHeaderGrammars());
			headerFactory.setFidelityOptions(FidelityOptions.createStrict());
		}

		return headerFactory;
	}
	
	static class EXIOptionsHeaderGrammars implements Grammars {

		private static final long serialVersionUID = -8097814828362083123L;

		/* BEGIN GrammarContext ----- */
		final String ns0 = "";
		final QNameContext[] grammarQNames0 = {};
		final String[] grammarPrefixes0 = { "" };
		final GrammarUriContext guc0 = new GrammarUriContext(0, ns0,
				grammarQNames0, grammarPrefixes0);

		final String ns1 = "http://www.w3.org/XML/1998/namespace";
		final QNameContext qnc0 = new QNameContext(1, 0,
				new QName(ns1, "base"), 0);
		final QNameContext qnc1 = new QNameContext(1, 1, new QName(ns1, "id"),
				1);
		final QNameContext qnc2 = new QNameContext(1, 2,
				new QName(ns1, "lang"), 2);
		final QNameContext qnc3 = new QNameContext(1, 3,
				new QName(ns1, "space"), 3);
		final QNameContext[] grammarQNames1 = { qnc0, qnc1, qnc2, qnc3 };
		final String[] grammarPrefixes1 = { "xml" };
		final GrammarUriContext guc1 = new GrammarUriContext(1, ns1,
				grammarQNames1, grammarPrefixes1);

		final String ns2 = "http://www.w3.org/2001/XMLSchema-instance";
		final QNameContext qnc4 = new QNameContext(2, 0, new QName(ns2, "nil"),
				4);
		final QNameContext qnc5 = new QNameContext(2, 1,
				new QName(ns2, "type"), 5);
		final QNameContext[] grammarQNames2 = { qnc4, qnc5 };
		final String[] grammarPrefixes2 = { "xsi" };
		final GrammarUriContext guc2 = new GrammarUriContext(2, ns2,
				grammarQNames2, grammarPrefixes2);

		final String ns3 = "http://www.w3.org/2001/XMLSchema";
		final QNameContext qnc6 = new QNameContext(3, 0, new QName(ns3,
				"ENTITIES"), 6);
		final QNameContext qnc7 = new QNameContext(3, 1, new QName(ns3,
				"ENTITY"), 7);
		final QNameContext qnc8 = new QNameContext(3, 2, new QName(ns3, "ID"),
				8);
		final QNameContext qnc9 = new QNameContext(3, 3,
				new QName(ns3, "IDREF"), 9);
		final QNameContext qnc10 = new QNameContext(3, 4, new QName(ns3,
				"IDREFS"), 10);
		final QNameContext qnc11 = new QNameContext(3, 5, new QName(ns3,
				"NCName"), 11);
		final QNameContext qnc12 = new QNameContext(3, 6, new QName(ns3,
				"NMTOKEN"), 12);
		final QNameContext qnc13 = new QNameContext(3, 7, new QName(ns3,
				"NMTOKENS"), 13);
		final QNameContext qnc14 = new QNameContext(3, 8, new QName(ns3,
				"NOTATION"), 14);
		final QNameContext qnc15 = new QNameContext(3, 9,
				new QName(ns3, "Name"), 15);
		final QNameContext qnc16 = new QNameContext(3, 10, new QName(ns3,
				"QName"), 16);
		final QNameContext qnc17 = new QNameContext(3, 11, new QName(ns3,
				"anySimpleType"), 17);
		final QNameContext qnc18 = new QNameContext(3, 12, new QName(ns3,
				"anyType"), 18);
		final QNameContext qnc19 = new QNameContext(3, 13, new QName(ns3,
				"anyURI"), 19);
		final QNameContext qnc20 = new QNameContext(3, 14, new QName(ns3,
				"base64Binary"), 20);
		final QNameContext qnc21 = new QNameContext(3, 15, new QName(ns3,
				"boolean"), 21);
		final QNameContext qnc22 = new QNameContext(3, 16, new QName(ns3,
				"byte"), 22);
		final QNameContext qnc23 = new QNameContext(3, 17, new QName(ns3,
				"date"), 23);
		final QNameContext qnc24 = new QNameContext(3, 18, new QName(ns3,
				"dateTime"), 24);
		final QNameContext qnc25 = new QNameContext(3, 19, new QName(ns3,
				"decimal"), 25);
		final QNameContext qnc26 = new QNameContext(3, 20, new QName(ns3,
				"double"), 26);
		final QNameContext qnc27 = new QNameContext(3, 21, new QName(ns3,
				"duration"), 27);
		final QNameContext qnc28 = new QNameContext(3, 22, new QName(ns3,
				"float"), 28);
		final QNameContext qnc29 = new QNameContext(3, 23, new QName(ns3,
				"gDay"), 29);
		final QNameContext qnc30 = new QNameContext(3, 24, new QName(ns3,
				"gMonth"), 30);
		final QNameContext qnc31 = new QNameContext(3, 25, new QName(ns3,
				"gMonthDay"), 31);
		final QNameContext qnc32 = new QNameContext(3, 26, new QName(ns3,
				"gYear"), 32);
		final QNameContext qnc33 = new QNameContext(3, 27, new QName(ns3,
				"gYearMonth"), 33);
		final QNameContext qnc34 = new QNameContext(3, 28, new QName(ns3,
				"hexBinary"), 34);
		final QNameContext qnc35 = new QNameContext(3, 29,
				new QName(ns3, "int"), 35);
		final QNameContext qnc36 = new QNameContext(3, 30, new QName(ns3,
				"integer"), 36);
		final QNameContext qnc37 = new QNameContext(3, 31, new QName(ns3,
				"language"), 37);
		final QNameContext qnc38 = new QNameContext(3, 32, new QName(ns3,
				"long"), 38);
		final QNameContext qnc39 = new QNameContext(3, 33, new QName(ns3,
				"negativeInteger"), 39);
		final QNameContext qnc40 = new QNameContext(3, 34, new QName(ns3,
				"nonNegativeInteger"), 40);
		final QNameContext qnc41 = new QNameContext(3, 35, new QName(ns3,
				"nonPositiveInteger"), 41);
		final QNameContext qnc42 = new QNameContext(3, 36, new QName(ns3,
				"normalizedString"), 42);
		final QNameContext qnc43 = new QNameContext(3, 37, new QName(ns3,
				"positiveInteger"), 43);
		final QNameContext qnc44 = new QNameContext(3, 38, new QName(ns3,
				"short"), 44);
		final QNameContext qnc45 = new QNameContext(3, 39, new QName(ns3,
				"string"), 45);
		final QNameContext qnc46 = new QNameContext(3, 40, new QName(ns3,
				"time"), 46);
		final QNameContext qnc47 = new QNameContext(3, 41, new QName(ns3,
				"token"), 47);
		final QNameContext qnc48 = new QNameContext(3, 42, new QName(ns3,
				"unsignedByte"), 48);
		final QNameContext qnc49 = new QNameContext(3, 43, new QName(ns3,
				"unsignedInt"), 49);
		final QNameContext qnc50 = new QNameContext(3, 44, new QName(ns3,
				"unsignedLong"), 50);
		final QNameContext qnc51 = new QNameContext(3, 45, new QName(ns3,
				"unsignedShort"), 51);
		final QNameContext[] grammarQNames3 = { qnc6, qnc7, qnc8, qnc9, qnc10,
				qnc11, qnc12, qnc13, qnc14, qnc15, qnc16, qnc17, qnc18, qnc19,
				qnc20, qnc21, qnc22, qnc23, qnc24, qnc25, qnc26, qnc27, qnc28,
				qnc29, qnc30, qnc31, qnc32, qnc33, qnc34, qnc35, qnc36, qnc37,
				qnc38, qnc39, qnc40, qnc41, qnc42, qnc43, qnc44, qnc45, qnc46,
				qnc47, qnc48, qnc49, qnc50, qnc51 };
		final String[] grammarPrefixes3 = {};
		final GrammarUriContext guc3 = new GrammarUriContext(3, ns3,
				grammarQNames3, grammarPrefixes3);

		final String ns4 = "http://www.w3.org/2009/exi";
		final QNameContext qnc52 = new QNameContext(4, 0, new QName(ns4,
				"alignment"), 52);
		final QNameContext qnc53 = new QNameContext(4, 1, new QName(ns4,
				"base64Binary"), 53);
		final QNameContext qnc54 = new QNameContext(4, 2, new QName(ns4,
				"blockSize"), 54);
		final QNameContext qnc55 = new QNameContext(4, 3, new QName(ns4,
				"boolean"), 55);
		final QNameContext qnc56 = new QNameContext(4, 4,
				new QName(ns4, "byte"), 56);
		final QNameContext qnc57 = new QNameContext(4, 5, new QName(ns4,
				"comments"), 57);
		final QNameContext qnc58 = new QNameContext(4, 6, new QName(ns4,
				"common"), 58);
		final QNameContext qnc59 = new QNameContext(4, 7, new QName(ns4,
				"compression"), 59);
		final QNameContext qnc60 = new QNameContext(4, 8, new QName(ns4,
				"datatypeRepresentationMap"), 60);
		final QNameContext qnc61 = new QNameContext(4, 9,
				new QName(ns4, "date"), 61);
		final QNameContext qnc62 = new QNameContext(4, 10, new QName(ns4,
				"dateTime"), 62);
		final QNameContext qnc63 = new QNameContext(4, 11, new QName(ns4,
				"decimal"), 63);
		final QNameContext qnc64 = new QNameContext(4, 12, new QName(ns4,
				"double"), 64);
		final QNameContext qnc65 = new QNameContext(4, 13,
				new QName(ns4, "dtd"), 65);
		final QNameContext qnc66 = new QNameContext(4, 14, new QName(ns4,
				"fragment"), 66);
		final QNameContext qnc67 = new QNameContext(4, 15, new QName(ns4,
				"gDay"), 67);
		final QNameContext qnc68 = new QNameContext(4, 16, new QName(ns4,
				"gMonth"), 68);
		final QNameContext qnc69 = new QNameContext(4, 17, new QName(ns4,
				"gMonthDay"), 69);
		final QNameContext qnc70 = new QNameContext(4, 18, new QName(ns4,
				"gYear"), 70);
		final QNameContext qnc71 = new QNameContext(4, 19, new QName(ns4,
				"gYearMonth"), 71);
		final QNameContext qnc72 = new QNameContext(4, 20, new QName(ns4,
				"header"), 72);
		final QNameContext qnc73 = new QNameContext(4, 21, new QName(ns4,
				"hexBinary"), 73);
		final QNameContext qnc74 = new QNameContext(4, 22, new QName(ns4,
				"ieeeBinary32"), 74);
		final QNameContext qnc75 = new QNameContext(4, 23, new QName(ns4,
				"ieeeBinary64"), 75);
		final QNameContext qnc76 = new QNameContext(4, 24, new QName(ns4,
				"integer"), 76);
		final QNameContext qnc77 = new QNameContext(4, 25, new QName(ns4,
				"lesscommon"), 77);
		final QNameContext qnc78 = new QNameContext(4, 26, new QName(ns4,
				"lexicalValues"), 78);
		final QNameContext qnc79 = new QNameContext(4, 27,
				new QName(ns4, "pis"), 79);
		final QNameContext qnc80 = new QNameContext(4, 28, new QName(ns4,
				"pre-compress"), 80);
		final QNameContext qnc81 = new QNameContext(4, 29, new QName(ns4,
				"prefixes"), 81);
		final QNameContext qnc82 = new QNameContext(4, 30, new QName(ns4,
				"preserve"), 82);
		final QNameContext qnc83 = new QNameContext(4, 31, new QName(ns4,
				"schemaId"), 83);
		final QNameContext qnc84 = new QNameContext(4, 32, new QName(ns4,
				"selfContained"), 84);
		final QNameContext qnc85 = new QNameContext(4, 33, new QName(ns4,
				"strict"), 85);
		final QNameContext qnc86 = new QNameContext(4, 34, new QName(ns4,
				"string"), 86);
		final QNameContext qnc87 = new QNameContext(4, 35, new QName(ns4,
				"time"), 87);
		final QNameContext qnc88 = new QNameContext(4, 36, new QName(ns4,
				"uncommon"), 88);
		final QNameContext qnc89 = new QNameContext(4, 37, new QName(ns4,
				"valueMaxLength"), 89);
		final QNameContext qnc90 = new QNameContext(4, 38, new QName(ns4,
				"valuePartitionCapacity"), 90);
		final QNameContext[] grammarQNames4 = { qnc52, qnc53, qnc54, qnc55,
				qnc56, qnc57, qnc58, qnc59, qnc60, qnc61, qnc62, qnc63, qnc64,
				qnc65, qnc66, qnc67, qnc68, qnc69, qnc70, qnc71, qnc72, qnc73,
				qnc74, qnc75, qnc76, qnc77, qnc78, qnc79, qnc80, qnc81, qnc82,
				qnc83, qnc84, qnc85, qnc86, qnc87, qnc88, qnc89, qnc90 };
		final String[] grammarPrefixes4 = {};
		final GrammarUriContext guc4 = new GrammarUriContext(4, ns4,
				grammarQNames4, grammarPrefixes4);

		final GrammarUriContext[] grammarUriContexts = { guc0, guc1, guc2,
				guc3, guc4 };
		final GrammarContext gc = new GrammarContext(grammarUriContexts, 91);
		/* END GrammarContext ----- */

		/* BEGIN Grammars ----- */
		com.siemens.ct.exi.grammars.grammar.Document g0 = new com.siemens.ct.exi.grammars.grammar.Document();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedDocContent g1 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedDocContent();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g2 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g3 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g4 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g5 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g6 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g7 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g8 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g9 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g10 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g11 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g12 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g13 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g14 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g15 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g16 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g17 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g18 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g19 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g20 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g21 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g22 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g23 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g24 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g25 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g26 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g27 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g28 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g29 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g30 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g31 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g32 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g33 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g34 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g35 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g36 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g37 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
		com.siemens.ct.exi.grammars.grammar.DocEnd g38 = new com.siemens.ct.exi.grammars.grammar.DocEnd();
		com.siemens.ct.exi.grammars.grammar.Fragment g39 = new com.siemens.ct.exi.grammars.grammar.Fragment();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedFragmentContent g40 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFragmentContent();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g41 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g42 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g43 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g44 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g45 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g46 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g47 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g48 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g49 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g50 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g51 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g52 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g53 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g54 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g55 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g56 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g57 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g58 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g59 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g60 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g61 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g62 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g63 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g64 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g65 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g66 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g67 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g68 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g69 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g70 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g71 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g72 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g73 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g74 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g75 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g76 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
		com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g77 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
		/* END Grammars ----- */

		com.siemens.ct.exi.grammars.event.StartElement globalSE72 = new com.siemens.ct.exi.grammars.event.StartElement(
				qnc72, g2);

		protected String schemaId = "http://www.w3.org/2009/exi";

		public EXIOptionsHeaderGrammars() {

			/* BEGIN GlobalElements ----- */
			qnc72.setGlobalStartElement(globalSE72);
			/* END GlobalElements ----- */

			/* BEGIN GlobalAttributes ----- */
			/* END GlobalAttributes ----- */

			/* BEGIN TypeGrammar ----- */
			qnc6.setTypeGrammar(g41);
			qnc7.setTypeGrammar(g43);
			qnc8.setTypeGrammar(g43);
			qnc9.setTypeGrammar(g43);
			qnc10.setTypeGrammar(g41);
			qnc11.setTypeGrammar(g43);
			qnc12.setTypeGrammar(g43);
			qnc13.setTypeGrammar(g41);
			qnc14.setTypeGrammar(g43);
			qnc15.setTypeGrammar(g43);
			qnc16.setTypeGrammar(g43);
			qnc17.setTypeGrammar(g43);
			qnc18.setTypeGrammar(g44);
			qnc19.setTypeGrammar(g43);
			qnc20.setTypeGrammar(g46);
			qnc21.setTypeGrammar(g48);
			qnc22.setTypeGrammar(g50);
			qnc23.setTypeGrammar(g52);
			qnc24.setTypeGrammar(g54);
			qnc25.setTypeGrammar(g56);
			qnc26.setTypeGrammar(g58);
			qnc27.setTypeGrammar(g43);
			qnc28.setTypeGrammar(g58);
			qnc29.setTypeGrammar(g60);
			qnc30.setTypeGrammar(g62);
			qnc31.setTypeGrammar(g64);
			qnc32.setTypeGrammar(g66);
			qnc33.setTypeGrammar(g68);
			qnc34.setTypeGrammar(g70);
			qnc35.setTypeGrammar(g72);
			qnc36.setTypeGrammar(g72);
			qnc37.setTypeGrammar(g43);
			qnc38.setTypeGrammar(g72);
			qnc39.setTypeGrammar(g72);
			qnc40.setTypeGrammar(g12);
			qnc41.setTypeGrammar(g72);
			qnc42.setTypeGrammar(g43);
			qnc43.setTypeGrammar(g12);
			qnc44.setTypeGrammar(g72);
			qnc45.setTypeGrammar(g43);
			qnc46.setTypeGrammar(g74);
			qnc47.setTypeGrammar(g43);
			qnc48.setTypeGrammar(g76);
			qnc49.setTypeGrammar(g12);
			qnc50.setTypeGrammar(g12);
			qnc51.setTypeGrammar(g12);
			qnc53.setTypeGrammar(g46);
			qnc55.setTypeGrammar(g48);
			qnc61.setTypeGrammar(g52);
			qnc62.setTypeGrammar(g54);
			qnc63.setTypeGrammar(g56);
			qnc64.setTypeGrammar(g58);
			qnc67.setTypeGrammar(g60);
			qnc68.setTypeGrammar(g62);
			qnc69.setTypeGrammar(g64);
			qnc70.setTypeGrammar(g66);
			qnc71.setTypeGrammar(g68);
			qnc73.setTypeGrammar(g70);
			qnc74.setTypeGrammar(g58);
			qnc75.setTypeGrammar(g58);
			qnc76.setTypeGrammar(g72);
			qnc86.setTypeGrammar(g43);
			qnc87.setTypeGrammar(g74);
			/* END TypeGrammar ----- */

			/* BEGIN Grammar Events ----- */
			g0.addProduction(
					new com.siemens.ct.exi.grammars.event.StartDocument(), g1);
			g1.addProduction(globalSE72, g38);
			g1.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElementGeneric(),
					g38);
			g2.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc77,
							g3), g29);
			g2.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc58,
							g30), g36);
			g2.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc85,
							g6), g8);
			g2.addProduction(
					new com.siemens.ct.exi.grammars.event.EndElement(), g7);
			g3.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc88,
							g4), g20);
			g3.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc82,
							g21), g27);
			g3.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc54,
							g12), g8);
			g3.addProduction(
					new com.siemens.ct.exi.grammars.event.EndElement(), g7);
			g4.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc52,
							g5), g10);
			g4.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc84,
							g6), g11);
			g4.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc89,
							g12), g14);
			g4.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc90,
							g12), g15);
			g4.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc60,
							g16), g15);
			g4.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElementGeneric(),
					g19);
			g4.addProduction(
					new com.siemens.ct.exi.grammars.event.EndElement(), g7);
			g5.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc56,
							g6), g8);
			g5.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc80,
							g6), g8);
			g6.addProduction(
					new com.siemens.ct.exi.grammars.event.EndElement(), g7);
			g8.addProduction(
					new com.siemens.ct.exi.grammars.event.EndElement(), g7);
			g9.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc56,
							g6), g8);
			g9.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc80,
							g6), g8);
			g10.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc84,
							g6), g11);
			g10.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc89,
							g12), g14);
			g10.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc90,
							g12), g15);
			g10.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc60,
							g16), g15);
			g10.addProduction(
					new com.siemens.ct.exi.grammars.event.EndElement(), g7);
			g11.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc89,
							g12), g14);
			g11.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc90,
							g12), g15);
			g11.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc60,
							g16), g15);
			g11.addProduction(
					new com.siemens.ct.exi.grammars.event.EndElement(), g7);
			g12.addProduction(new com.siemens.ct.exi.grammars.event.Characters(
					new com.siemens.ct.exi.datatype.UnsignedIntegerDatatype(
							qnc49)), g8);
			g13.addProduction(new com.siemens.ct.exi.grammars.event.Characters(
					new com.siemens.ct.exi.datatype.UnsignedIntegerDatatype(
							qnc49)), g8);
			g14.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc90,
							g12), g15);
			g14.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc60,
							g16), g15);
			g14.addProduction(
					new com.siemens.ct.exi.grammars.event.EndElement(), g7);
			g15.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc60,
							g16), g15);
			g15.addProduction(
					new com.siemens.ct.exi.grammars.event.EndElement(), g7);
			g16.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElementGeneric(),
					g17);
			g17.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElementGeneric(),
					g8);
			g18.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElementGeneric(),
					g17);
			g19.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc52,
							g5), g10);
			g19.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc84,
							g6), g11);
			g19.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc89,
							g12), g14);
			g19.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc90,
							g12), g15);
			g19.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc60,
							g16), g15);
			g19.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElementGeneric(),
					g19);
			g19.addProduction(
					new com.siemens.ct.exi.grammars.event.EndElement(), g7);
			g20.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc82,
							g21), g27);
			g20.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc54,
							g12), g8);
			g20.addProduction(
					new com.siemens.ct.exi.grammars.event.EndElement(), g7);
			g21.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc65,
							g6), g22);
			g21.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc81,
							g6), g23);
			g21.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc78,
							g6), g24);
			g21.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc57,
							g6), g25);
			g21.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc79,
							g6), g8);
			g21.addProduction(
					new com.siemens.ct.exi.grammars.event.EndElement(), g7);
			g22.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc81,
							g6), g23);
			g22.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc78,
							g6), g24);
			g22.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc57,
							g6), g25);
			g22.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc79,
							g6), g8);
			g22.addProduction(
					new com.siemens.ct.exi.grammars.event.EndElement(), g7);
			g23.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc78,
							g6), g24);
			g23.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc57,
							g6), g25);
			g23.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc79,
							g6), g8);
			g23.addProduction(
					new com.siemens.ct.exi.grammars.event.EndElement(), g7);
			g24.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc57,
							g6), g25);
			g24.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc79,
							g6), g8);
			g24.addProduction(
					new com.siemens.ct.exi.grammars.event.EndElement(), g7);
			g25.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc79,
							g6), g8);
			g25.addProduction(
					new com.siemens.ct.exi.grammars.event.EndElement(), g7);
			g26.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc65,
							g6), g22);
			g26.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc81,
							g6), g23);
			g26.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc78,
							g6), g24);
			g26.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc57,
							g6), g25);
			g26.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc79,
							g6), g8);
			g26.addProduction(
					new com.siemens.ct.exi.grammars.event.EndElement(), g7);
			g27.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc54,
							g12), g8);
			g27.addProduction(
					new com.siemens.ct.exi.grammars.event.EndElement(), g7);
			g28.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc88,
							g4), g20);
			g28.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc82,
							g21), g27);
			g28.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc54,
							g12), g8);
			g28.addProduction(
					new com.siemens.ct.exi.grammars.event.EndElement(), g7);
			g29.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc58,
							g30), g36);
			g29.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc85,
							g6), g8);
			g29.addProduction(
					new com.siemens.ct.exi.grammars.event.EndElement(), g7);
			g30.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc59,
							g6), g31);
			g30.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc66,
							g6), g32);
			g30.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc83,
							g33), g8);
			g30.addProduction(
					new com.siemens.ct.exi.grammars.event.EndElement(), g7);
			g31.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc66,
							g6), g32);
			g31.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc83,
							g33), g8);
			g31.addProduction(
					new com.siemens.ct.exi.grammars.event.EndElement(), g7);
			g32.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc83,
							g33), g8);
			g32.addProduction(
					new com.siemens.ct.exi.grammars.event.EndElement(), g7);
			g33.addProduction(new com.siemens.ct.exi.grammars.event.Characters(
					new com.siemens.ct.exi.datatype.StringDatatype(qnc45)), g8);
			g34.addProduction(new com.siemens.ct.exi.grammars.event.Characters(
					new com.siemens.ct.exi.datatype.StringDatatype(qnc45)), g8);
			g35.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc59,
							g6), g31);
			g35.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc66,
							g6), g32);
			g35.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc83,
							g33), g8);
			g35.addProduction(
					new com.siemens.ct.exi.grammars.event.EndElement(), g7);
			g36.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc85,
							g6), g8);
			g36.addProduction(
					new com.siemens.ct.exi.grammars.event.EndElement(), g7);
			g37.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc77,
							g3), g29);
			g37.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc58,
							g30), g36);
			g37.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc85,
							g6), g8);
			g37.addProduction(
					new com.siemens.ct.exi.grammars.event.EndElement(), g7);
			g38.addProduction(
					new com.siemens.ct.exi.grammars.event.EndDocument(), g7);
			g39.addProduction(
					new com.siemens.ct.exi.grammars.event.StartDocument(), g40);
			g40.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc52,
							g5), g40);
			g40.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc54,
							g12), g40);
			g40.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc56,
							g6), g40);
			g40.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc57,
							g6), g40);
			g40.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc58,
							g30), g40);
			g40.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc59,
							g6), g40);
			g40.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc60,
							g16), g40);
			g40.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc65,
							g6), g40);
			g40.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc66,
							g6), g40);
			g40.addProduction(globalSE72, g40);
			g40.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc77,
							g3), g40);
			g40.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc78,
							g6), g40);
			g40.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc79,
							g6), g40);
			g40.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc80,
							g6), g40);
			g40.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc81,
							g6), g40);
			g40.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc82,
							g21), g40);
			g40.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc83,
							g33), g40);
			g40.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc84,
							g6), g40);
			g40.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc85,
							g6), g40);
			g40.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc88,
							g4), g40);
			g40.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc89,
							g12), g40);
			g40.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElement(qnc90,
							g12), g40);
			g40.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElementGeneric(),
					g40);
			g40.addProduction(
					new com.siemens.ct.exi.grammars.event.EndDocument(), g7);
			g41.addProduction(
					new com.siemens.ct.exi.grammars.event.Characters(
							new com.siemens.ct.exi.datatype.ListDatatype(
									new com.siemens.ct.exi.datatype.StringDatatype(
											qnc7), qnc6)), g8);
			g42.addProduction(
					new com.siemens.ct.exi.grammars.event.Characters(
							new com.siemens.ct.exi.datatype.ListDatatype(
									new com.siemens.ct.exi.datatype.StringDatatype(
											qnc7), qnc6)), g8);
			g43.addProduction(new com.siemens.ct.exi.grammars.event.Characters(
					new com.siemens.ct.exi.datatype.StringDatatype(qnc7)), g8);
			g44.addProduction(
					new com.siemens.ct.exi.grammars.event.AttributeGeneric(),
					g44);
			g44.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElementGeneric(),
					g45);
			g44.addProduction(
					new com.siemens.ct.exi.grammars.event.EndElement(), g7);
			g44.addProduction(
					new com.siemens.ct.exi.grammars.event.CharactersGeneric(),
					g45);
			g45.addProduction(
					new com.siemens.ct.exi.grammars.event.StartElementGeneric(),
					g45);
			g45.addProduction(
					new com.siemens.ct.exi.grammars.event.EndElement(), g7);
			g45.addProduction(
					new com.siemens.ct.exi.grammars.event.CharactersGeneric(),
					g45);
			g46.addProduction(
					new com.siemens.ct.exi.grammars.event.Characters(
							new com.siemens.ct.exi.datatype.BinaryBase64Datatype(
									qnc20)), g8);
			g47.addProduction(
					new com.siemens.ct.exi.grammars.event.Characters(
							new com.siemens.ct.exi.datatype.BinaryBase64Datatype(
									qnc20)), g8);
			g48.addProduction(new com.siemens.ct.exi.grammars.event.Characters(
					new com.siemens.ct.exi.datatype.BooleanDatatype(qnc21)), g8);
			g49.addProduction(new com.siemens.ct.exi.grammars.event.Characters(
					new com.siemens.ct.exi.datatype.BooleanDatatype(qnc21)), g8);
			g50.addProduction(
					new com.siemens.ct.exi.grammars.event.Characters(
							new com.siemens.ct.exi.datatype.NBitUnsignedIntegerDatatype(
									com.siemens.ct.exi.values.IntegerValue
											.valueOf(-128),
									com.siemens.ct.exi.values.IntegerValue
											.valueOf(127), qnc22)), g8);
			g51.addProduction(
					new com.siemens.ct.exi.grammars.event.Characters(
							new com.siemens.ct.exi.datatype.NBitUnsignedIntegerDatatype(
									com.siemens.ct.exi.values.IntegerValue
											.valueOf(-128),
									com.siemens.ct.exi.values.IntegerValue
											.valueOf(127), qnc22)), g8);
			g52.addProduction(
					new com.siemens.ct.exi.grammars.event.Characters(
							new com.siemens.ct.exi.datatype.DatetimeDatatype(
									com.siemens.ct.exi.types.DateTimeType.date,
									qnc23)), g8);
			g53.addProduction(
					new com.siemens.ct.exi.grammars.event.Characters(
							new com.siemens.ct.exi.datatype.DatetimeDatatype(
									com.siemens.ct.exi.types.DateTimeType.date,
									qnc23)), g8);
			g54.addProduction(new com.siemens.ct.exi.grammars.event.Characters(
					new com.siemens.ct.exi.datatype.DatetimeDatatype(
							com.siemens.ct.exi.types.DateTimeType.dateTime,
							qnc24)), g8);
			g55.addProduction(new com.siemens.ct.exi.grammars.event.Characters(
					new com.siemens.ct.exi.datatype.DatetimeDatatype(
							com.siemens.ct.exi.types.DateTimeType.dateTime,
							qnc24)), g8);
			g56.addProduction(new com.siemens.ct.exi.grammars.event.Characters(
					new com.siemens.ct.exi.datatype.DecimalDatatype(qnc25)), g8);
			g57.addProduction(new com.siemens.ct.exi.grammars.event.Characters(
					new com.siemens.ct.exi.datatype.DecimalDatatype(qnc25)), g8);
			g58.addProduction(new com.siemens.ct.exi.grammars.event.Characters(
					new com.siemens.ct.exi.datatype.FloatDatatype(qnc26)), g8);
			g59.addProduction(new com.siemens.ct.exi.grammars.event.Characters(
					new com.siemens.ct.exi.datatype.FloatDatatype(qnc26)), g8);
			g60.addProduction(
					new com.siemens.ct.exi.grammars.event.Characters(
							new com.siemens.ct.exi.datatype.DatetimeDatatype(
									com.siemens.ct.exi.types.DateTimeType.gDay,
									qnc29)), g8);
			g61.addProduction(
					new com.siemens.ct.exi.grammars.event.Characters(
							new com.siemens.ct.exi.datatype.DatetimeDatatype(
									com.siemens.ct.exi.types.DateTimeType.gDay,
									qnc29)), g8);
			g62.addProduction(
					new com.siemens.ct.exi.grammars.event.Characters(
							new com.siemens.ct.exi.datatype.DatetimeDatatype(
									com.siemens.ct.exi.types.DateTimeType.gMonth,
									qnc30)), g8);
			g63.addProduction(
					new com.siemens.ct.exi.grammars.event.Characters(
							new com.siemens.ct.exi.datatype.DatetimeDatatype(
									com.siemens.ct.exi.types.DateTimeType.gMonth,
									qnc30)), g8);
			g64.addProduction(new com.siemens.ct.exi.grammars.event.Characters(
					new com.siemens.ct.exi.datatype.DatetimeDatatype(
							com.siemens.ct.exi.types.DateTimeType.gMonthDay,
							qnc31)), g8);
			g65.addProduction(new com.siemens.ct.exi.grammars.event.Characters(
					new com.siemens.ct.exi.datatype.DatetimeDatatype(
							com.siemens.ct.exi.types.DateTimeType.gMonthDay,
							qnc31)), g8);
			g66.addProduction(
					new com.siemens.ct.exi.grammars.event.Characters(
							new com.siemens.ct.exi.datatype.DatetimeDatatype(
									com.siemens.ct.exi.types.DateTimeType.gYear,
									qnc32)), g8);
			g67.addProduction(
					new com.siemens.ct.exi.grammars.event.Characters(
							new com.siemens.ct.exi.datatype.DatetimeDatatype(
									com.siemens.ct.exi.types.DateTimeType.gYear,
									qnc32)), g8);
			g68.addProduction(new com.siemens.ct.exi.grammars.event.Characters(
					new com.siemens.ct.exi.datatype.DatetimeDatatype(
							com.siemens.ct.exi.types.DateTimeType.gYearMonth,
							qnc33)), g8);
			g69.addProduction(new com.siemens.ct.exi.grammars.event.Characters(
					new com.siemens.ct.exi.datatype.DatetimeDatatype(
							com.siemens.ct.exi.types.DateTimeType.gYearMonth,
							qnc33)), g8);
			g70.addProduction(new com.siemens.ct.exi.grammars.event.Characters(
					new com.siemens.ct.exi.datatype.BinaryHexDatatype(qnc34)),
					g8);
			g71.addProduction(new com.siemens.ct.exi.grammars.event.Characters(
					new com.siemens.ct.exi.datatype.BinaryHexDatatype(qnc34)),
					g8);
			g72.addProduction(new com.siemens.ct.exi.grammars.event.Characters(
					new com.siemens.ct.exi.datatype.IntegerDatatype(qnc35)), g8);
			g73.addProduction(new com.siemens.ct.exi.grammars.event.Characters(
					new com.siemens.ct.exi.datatype.IntegerDatatype(qnc35)), g8);
			g74.addProduction(
					new com.siemens.ct.exi.grammars.event.Characters(
							new com.siemens.ct.exi.datatype.DatetimeDatatype(
									com.siemens.ct.exi.types.DateTimeType.time,
									qnc46)), g8);
			g75.addProduction(
					new com.siemens.ct.exi.grammars.event.Characters(
							new com.siemens.ct.exi.datatype.DatetimeDatatype(
									com.siemens.ct.exi.types.DateTimeType.time,
									qnc46)), g8);
			g76.addProduction(
					new com.siemens.ct.exi.grammars.event.Characters(
							new com.siemens.ct.exi.datatype.NBitUnsignedIntegerDatatype(
									com.siemens.ct.exi.values.IntegerValue
											.valueOf(0),
									com.siemens.ct.exi.values.IntegerValue
											.valueOf(255), qnc48)), g8);
			g77.addProduction(
					new com.siemens.ct.exi.grammars.event.Characters(
							new com.siemens.ct.exi.datatype.NBitUnsignedIntegerDatatype(
									com.siemens.ct.exi.values.IntegerValue
											.valueOf(0),
									com.siemens.ct.exi.values.IntegerValue
											.valueOf(255), qnc48)), g8);
			/* END Grammar Events ----- */

			/* BEGIN FirstStartGrammar ----- */
			g2.setElementContentGrammar(g37);
			g3.setElementContentGrammar(g28);
			g4.setElementContentGrammar(g19);
			g5.setElementContentGrammar(g9);
			g6.setElementContentGrammar(g8);
			g12.setElementContentGrammar(g13);
			g16.setElementContentGrammar(g18);
			g21.setElementContentGrammar(g26);
			g30.setElementContentGrammar(g35);
			g33.setElementContentGrammar(g34);
			g33.setNillable(true);
			g41.setElementContentGrammar(g42);
			g43.setElementContentGrammar(g34);
			g44.setElementContentGrammar(g45);
			g46.setElementContentGrammar(g47);
			g48.setElementContentGrammar(g49);
			g50.setElementContentGrammar(g51);
			g52.setElementContentGrammar(g53);
			g54.setElementContentGrammar(g55);
			g56.setElementContentGrammar(g57);
			g58.setElementContentGrammar(g59);
			g60.setElementContentGrammar(g61);
			g62.setElementContentGrammar(g63);
			g64.setElementContentGrammar(g65);
			g66.setElementContentGrammar(g67);
			g68.setElementContentGrammar(g69);
			g70.setElementContentGrammar(g71);
			g72.setElementContentGrammar(g73);
			g74.setElementContentGrammar(g75);
			g76.setElementContentGrammar(g77);
			/* END FirstStartGrammar ----- */

		}

		public boolean isSchemaInformed() {
			return true;
		}

		public String getSchemaId() {
			return schemaId;
		}

		public void setSchemaId(String schemaId) throws UnsupportedOption {
			this.schemaId = schemaId;
		}

		public boolean isBuiltInXMLSchemaTypesOnly() {
			return false;
		}

		public Grammar getDocumentGrammar() {
			return g0;
		}

		public Grammar getFragmentGrammar() {
			return g39;
		}

		public GrammarContext getGrammarContext() {
			return gc;
		}

	}

}
