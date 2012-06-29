/*
 * Copyright (C) 2007-2011 Siemens AG
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

package com.siemens.ct.exi.datatype;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.namespace.QName;

import org.junit.Test;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.context.DecoderContext;
import com.siemens.ct.exi.context.DecoderContextImpl;
import com.siemens.ct.exi.context.EncoderContext;
import com.siemens.ct.exi.context.EncoderContextImpl;
import com.siemens.ct.exi.context.GrammarContext;
import com.siemens.ct.exi.context.GrammarUriContext;
import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.datatype.strings.StringDecoder;
import com.siemens.ct.exi.datatype.strings.StringEncoder;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammars.GrammarTest;
import com.siemens.ct.exi.grammars.Grammars;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;
import com.siemens.ct.exi.io.channel.BitDecoderChannel;
import com.siemens.ct.exi.io.channel.BitEncoderChannel;
import com.siemens.ct.exi.types.BuiltIn;
import com.siemens.ct.exi.types.TypeDecoder;
import com.siemens.ct.exi.types.TypeEncoder;
import com.siemens.ct.exi.values.StringValue;

public class StringTableTest extends AbstractTestCase {

	@Test
	public void testStringTableValue0() throws IOException, EXIException {
		QName context = new QName("el");
		
		//EvolvingUriContext uc = new RuntimeEvolvingUriContext(0, "");
		QNameContext qncContext = new QNameContext(0, 0, context, 0);
		
		String val1 = "val1";
		String val2 = "val2";
		String val3 = "val3";

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		EXIFactory exiFactory = DefaultEXIFactory.newInstance();
		GrammarContext grammarContext = exiFactory.getGrammars().getGrammarContext();

		/*
		 * Encode
		 */
		StringEncoder stringEncoder = exiFactory.createStringEncoder();
		EncoderContext encoderContext = new EncoderContextImpl(grammarContext, stringEncoder);
		TypeEncoder tes = exiFactory.createTypeEncoder(); //  new StringTypeEncoder();
		BitEncoderChannel bec = new BitEncoderChannel(baos);

		tes.isValid(BuiltIn.DEFAULT_DATATYPE, new StringValue(val1));
		tes.writeValue(encoderContext, qncContext, bec);
		tes.isValid(BuiltIn.DEFAULT_DATATYPE, new StringValue(val2));
		tes.writeValue(encoderContext, qncContext, bec);
		tes.isValid(BuiltIn.DEFAULT_DATATYPE, new StringValue(val1));
		tes.writeValue(encoderContext, qncContext, bec);
		tes.isValid(BuiltIn.DEFAULT_DATATYPE, new StringValue(val3));
		tes.writeValue(encoderContext, qncContext, bec);
		tes.isValid(BuiltIn.DEFAULT_DATATYPE, new StringValue(val3));
		tes.writeValue(encoderContext, qncContext, bec);
		tes.isValid(BuiltIn.DEFAULT_DATATYPE, new StringValue(val3));
		tes.writeValue(encoderContext, qncContext, bec);

		bec.flush();
		baos.flush();

		/*
		 * Decode
		 */
		TypeDecoder ddl = exiFactory.createTypeDecoder(); //  new StringTypeDecoder(new StringDecoderImpl());
		StringDecoder stringDecoder = exiFactory.createStringDecoder();
		DecoderContext decoderContext = new DecoderContextImpl(grammarContext, stringDecoder);
		
		BitDecoderChannel bdc = new BitDecoderChannel(new ByteArrayInputStream(
				baos.toByteArray()));

		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, decoderContext, qncContext, bdc)
				.toString().equals(val1));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, decoderContext, qncContext, bdc)
				.toString().equals(val2));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, decoderContext, qncContext, bdc)
				.toString().equals(val1));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, decoderContext, qncContext, bdc)
				.toString().equals(val3));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, decoderContext, qncContext, bdc)
				.toString().equals(val3));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, decoderContext, qncContext, bdc)
				.toString().equals(val3));
	}

	@Test
	public void testStringTableValue1() throws IOException, EXIException {
		QName c1 = new QName("el1");
		QName c2 = new QName("el2");
		QName c3 = new QName("el3");
		
		// EvolvingUriContext uc = new RuntimeEvolvingUriContext(0, "");
		int namespaceUriID = 0;
		QNameContext qncC1 = new QNameContext(namespaceUriID, 0, c1, 0);
		QNameContext qncC2 = new QNameContext(namespaceUriID, 1, c2, 1);
		QNameContext qncC3 = new QNameContext(namespaceUriID, 2, c3, 2);
		
		String val1 = "val1";
		String val2 = "val2";
		String val3 = "val3";

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		EXIFactory exiFactory = DefaultEXIFactory.newInstance();
		GrammarContext grammarContext = exiFactory.getGrammars().getGrammarContext();

		/*
		 * Encode
		 */
		StringEncoder stringEncoder = exiFactory.createStringEncoder();
		EncoderContext encoderContext = new EncoderContextImpl(grammarContext, stringEncoder);
		TypeEncoder tes = exiFactory.createTypeEncoder(); //  new StringTypeEncoder();		
		// StringTypeEncoder tes = new StringTypeEncoder(new StringEncoderImpl());
		
		BitEncoderChannel bec = new BitEncoderChannel(baos);

		tes.isValid(BuiltIn.DEFAULT_DATATYPE, new StringValue(val1));
		tes.writeValue(encoderContext, qncC1, bec);
		tes.isValid(BuiltIn.DEFAULT_DATATYPE, new StringValue(val2));
		tes.writeValue(encoderContext, qncC3, bec);
		tes.isValid(BuiltIn.DEFAULT_DATATYPE, new StringValue(val1));
		tes.writeValue(encoderContext, qncC2, bec);
		tes.isValid(BuiltIn.DEFAULT_DATATYPE, new StringValue(val3));
		tes.writeValue(encoderContext, qncC3, bec);
		tes.isValid(BuiltIn.DEFAULT_DATATYPE, new StringValue(val3));
		tes.writeValue(encoderContext, qncC1, bec);
		tes.isValid(BuiltIn.DEFAULT_DATATYPE, new StringValue(val3));
		tes.writeValue(encoderContext, qncC3, bec);

		bec.flush();
		baos.flush();
		
		/*
		 * Decode
		 */
		TypeDecoder ddl = exiFactory.createTypeDecoder(); 
		StringDecoder stringDecoder = exiFactory.createStringDecoder();
		DecoderContext decoderContext = new DecoderContextImpl(grammarContext, stringDecoder);
		// StringTypeDecoder ddl = new StringTypeDecoder(new StringDecoderImpl());
		
		BitDecoderChannel bdc = new BitDecoderChannel(new ByteArrayInputStream(
				baos.toByteArray()));

		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, decoderContext, qncC1, bdc).toString()
				.equals(val1));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, decoderContext, qncC3, bdc).toString()
				.equals(val2));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, decoderContext, qncC2, bdc).toString()
				.equals(val1));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, decoderContext, qncC3, bdc).toString()
				.equals(val3));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, decoderContext, qncC1, bdc).toString()
				.equals(val3));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, decoderContext, qncC3, bdc).toString()
				.equals(val3));
	}

	@Test
	public void testStringTableValue() throws IOException, EXIException {

		QName ca1 = new QName("at1");

		QName cex2 = new QName("elx2");
		QName cex3 = new QName("elx3");

		QName cexx1 = new QName("elxx1");
		QName cexx2 = new QName("elxx2");
		
		// EvolvingUriContext uc = new RuntimeEvolvingUriContext(0, "");
		int namespaceUriID = 0;
		QNameContext qncCa1 = new QNameContext(namespaceUriID, 0, ca1, 0);
		QNameContext qncCex2 = new QNameContext(namespaceUriID, 1, cex2, 1);
		QNameContext qncCex3 = new QNameContext(namespaceUriID, 2, cex3, 2);
		QNameContext qncCexx1 = new QNameContext(namespaceUriID, 2, cexx1, 3);
		QNameContext qncCexx2 = new QNameContext(namespaceUriID, 2, cexx2, 4);

		String atCh1 = "at-ch1";
		String atCh2 = "at-ch2";
		String ch1 = "ch1";
		String ch2 = "ch2";
		String ch3 = "ch3";

		EXIFactory exiFactory = DefaultEXIFactory.newInstance();
		GrammarContext grammarContext = exiFactory.getGrammars().getGrammarContext();
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		/*
		 * Encode
		 */
		StringEncoder stringEncoder = exiFactory.createStringEncoder();
		EncoderContext encoderContext = new EncoderContextImpl(grammarContext, stringEncoder);
		TypeEncoder tes = exiFactory.createTypeEncoder(); //  new StringTypeEncoder();	
		// StringTypeEncoder tes = new StringTypeEncoder(new StringEncoderImpl());
		
		BitEncoderChannel bec = new BitEncoderChannel(baos);

		tes.isValid(BuiltIn.DEFAULT_DATATYPE, new StringValue(atCh1));
		tes.writeValue(encoderContext, qncCa1, bec);
		tes.isValid(BuiltIn.DEFAULT_DATATYPE, new StringValue(ch1));
		tes.writeValue(encoderContext, qncCexx1, bec);
		tes.isValid(BuiltIn.DEFAULT_DATATYPE, new StringValue(ch2));
		tes.writeValue(encoderContext, qncCexx2, bec);
		tes.isValid(BuiltIn.DEFAULT_DATATYPE, new StringValue(ch2));
		tes.writeValue(encoderContext, qncCex2, bec);
		tes.isValid(BuiltIn.DEFAULT_DATATYPE, new StringValue(ch3));
		tes.writeValue(encoderContext, qncCex3, bec);
		tes.isValid(BuiltIn.DEFAULT_DATATYPE, new StringValue(atCh2));
		tes.writeValue(encoderContext, qncCa1, bec);
		tes.isValid(BuiltIn.DEFAULT_DATATYPE, new StringValue(ch1));
		tes.writeValue(encoderContext, qncCexx1, bec);
		tes.isValid(BuiltIn.DEFAULT_DATATYPE, new StringValue(ch2));
		tes.writeValue(encoderContext, qncCexx2, bec);
		tes.isValid(BuiltIn.DEFAULT_DATATYPE, new StringValue(ch2));
		tes.writeValue(encoderContext, qncCex2, bec);
		tes.isValid(BuiltIn.DEFAULT_DATATYPE, new StringValue(ch3));
		tes.writeValue(encoderContext, qncCex2, bec);

		bec.flush();
		baos.flush();
		
		/*
		 * Decode
		 */
		TypeDecoder ddl = exiFactory.createTypeDecoder(); 
		StringDecoder stringDecoder = exiFactory.createStringDecoder();
		DecoderContext decoderContext = new DecoderContextImpl(grammarContext, stringDecoder);
		// StringTypeDecoder ddl = new StringTypeDecoder(new StringDecoderImpl());
		
		BitDecoderChannel bdc = new BitDecoderChannel(new ByteArrayInputStream(
				baos.toByteArray()));

		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, decoderContext, qncCa1, bdc).toString()
				.equals(atCh1));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, decoderContext, qncCexx1, bdc)
				.toString().equals(ch1));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, decoderContext, qncCexx2, bdc)
				.toString().equals(ch2));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, decoderContext, qncCex2, bdc)
				.toString().equals(ch2));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, decoderContext, qncCex3, bdc)
				.toString().equals(ch3));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, decoderContext, qncCa1, bdc).toString()
				.equals(atCh2));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, decoderContext, qncCexx1, bdc)
				.toString().equals(ch1));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, decoderContext, qncCexx2, bdc)
				.toString().equals(ch2));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, decoderContext, qncCex2, bdc)
				.toString().equals(ch2));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, decoderContext, qncCex2, bdc)
				.toString().equals(ch3));
	}

	@Test
	public void testStringTableValueMaxLength1() throws IOException, EXIException {

		QName qa = new QName("a");
		QName qb = new QName("b");
		
		// EvolvingUriContext uc = new RuntimeEvolvingUriContext(0, "");
		int namespaceUriID = 0;
		QNameContext qncA = new QNameContext(namespaceUriID, 0, qa, 0);
		QNameContext qncB = new QNameContext(namespaceUriID, 1, qb, 1);

		String s3 = "123";
		String s4 = "1234";
		String s5 = "12345";
		String s6 = "123456";
		String s7 = "1234567";

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		int valueMaxLength = 5;
		// int valuePartitionCapacity = Constants.DEFAULT_VALUE_PARTITON_CAPACITY;

		Datatype dt = BuiltIn.DEFAULT_DATATYPE;
		
		EXIFactory exiFactory = DefaultEXIFactory.newInstance();
		exiFactory.setValueMaxLength(valueMaxLength);
		GrammarContext grammarContext = exiFactory.getGrammars().getGrammarContext();
		
		/*
		 * Encode
		 */
		StringEncoder stringEncoder = exiFactory.createStringEncoder();
		EncoderContext encoderContext = new EncoderContextImpl(grammarContext, stringEncoder);
		TypeEncoder tes = exiFactory.createTypeEncoder();
//		StringEncoder se = new BoundedStringEncoderImpl(valueMaxLength,
//				valuePartitionCapacity);
//		StringTypeEncoder tes = new StringTypeEncoder(se);
		
		BitEncoderChannel bec = new BitEncoderChannel(baos);

		// a: 123
		tes.isValid(dt, new StringValue(s3));
		tes.writeValue(encoderContext, qncA, bec);
		// b: 1234
		tes.isValid(dt, new StringValue(s4));
		tes.writeValue(encoderContext, qncB, bec);
		// a: 12345
		tes.isValid(dt, new StringValue(s5));
		tes.writeValue(encoderContext, qncA, bec);
		// b: 123456 /* to large */
		assertFalse(stringEncoder.isStringHit(s6));
		tes.isValid(dt, new StringValue(s6));
		tes.writeValue(encoderContext, qncB, bec);
		// a: 1234567 /* to large */
		assertFalse(stringEncoder.isStringHit(s7));
		tes.isValid(dt, new StringValue(s7));
		tes.writeValue(encoderContext, qncA, bec);

		bec.flush();
		baos.flush();
		

		/*
		 * Decode
		 */
		TypeDecoder ddl = exiFactory.createTypeDecoder(); 
		StringDecoder stringDecoder = exiFactory.createStringDecoder();
		DecoderContext decoderContext = new DecoderContextImpl(grammarContext, stringDecoder);
//		StringDecoder sd = new BoundedStringDecoderImpl(valueMaxLength,
//				valuePartitionCapacity);
//		StringTypeDecoder ddl = new StringTypeDecoder(sd);
		
		BitDecoderChannel bdc = new BitDecoderChannel(new ByteArrayInputStream(
				baos.toByteArray()));

		assertTrue(ddl.readValue(dt, decoderContext, qncA, bdc).toString().equals(s3));
		assertTrue(ddl.readValue(dt, decoderContext, qncB, bdc).toString().equals(s4));
		assertTrue(ddl.readValue(dt, decoderContext, qncA, bdc).toString().equals(s5));
		assertTrue(ddl.readValue(dt, decoderContext, qncB, bdc).toString().equals(s6));
		assertTrue(ddl.readValue(dt, decoderContext, qncA, bdc).toString().equals(s7));
	}
	
	@Test
	public void testStringTableValuePartitionCapacity() throws IOException, EXIException {

		QName qa = new QName("a");
		QName qb = new QName("b");
		QName qc = new QName("c");
		
		// EvolvingUriContext uc = new RuntimeEvolvingUriContext(0, "");
		int namespaceUriID = 0;
		QNameContext qncQa = new QNameContext(namespaceUriID, 0, qa, 0);
		QNameContext qncQb = new QNameContext(namespaceUriID, 1, qb, 1);
		QNameContext qncQc = new QNameContext(namespaceUriID, 2, qc, 2);

		String s1 = "1";
		String s2 = "12";
		String s3 = "123";
		String s4 = "1234";
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		int valueMaxLength = Constants.DEFAULT_VALUE_MAX_LENGTH;
		int valuePartitionCapacity = 3;
		
		EXIFactory exiFactory = DefaultEXIFactory.newInstance();
		exiFactory.setValueMaxLength(valueMaxLength);
		exiFactory.setValuePartitionCapacity(valuePartitionCapacity);
		GrammarContext grammarContext = exiFactory.getGrammars().getGrammarContext();

		Datatype dt = BuiltIn.DEFAULT_DATATYPE;
		/*
		 * Encode
		 */
		StringEncoder stringEncoder = exiFactory.createStringEncoder();
		EncoderContext encoderContext = new EncoderContextImpl(grammarContext, stringEncoder);
		TypeEncoder tes = exiFactory.createTypeEncoder();
//		StringEncoder se = new BoundedStringEncoderImpl(valueMaxLength,
//				valuePartitionCapacity);
//		StringTypeEncoder tes = new StringTypeEncoder(se);
		
		
		BitEncoderChannel bec = new BitEncoderChannel(baos);


		// a: 1
		tes.isValid(dt, new StringValue(s1));
		tes.writeValue(encoderContext, qncQa, bec); // ["1"{a}]
		// b: 12
		tes.isValid(dt, new StringValue(s2));
		tes.writeValue(encoderContext, qncQb, bec);  // ["1"{a},"12"{b}]
		// a: 123
		tes.isValid(dt, new StringValue(s3));
		tes.writeValue(encoderContext, qncQa, bec); // ["1"{a},"12"{b},"123"{a}]
		// c: 123 /* global hit */
		tes.isValid(dt, new StringValue(s3));
		tes.writeValue(encoderContext, qncQc, bec); // ["1","12","123"]
		// c: 1234
		tes.isValid(dt, new StringValue(s4));
		tes.writeValue(encoderContext, qncQc, bec); // ["1234","12","123"]
		// a: 1 /* no local hit due to valuePartitionCapacity*/
		assertFalse(stringEncoder.isStringHit(s1));
		tes.isValid(dt, new StringValue(s1));
		tes.writeValue(encoderContext, qncQa, bec);	 // ["1234","1","123"]
		// c: 1 /* no local hit due to valuePartitionCapacity*/
		assertTrue(stringEncoder.isStringHit(s4));
		assertTrue(stringEncoder.isStringHit(s1));
		assertTrue(stringEncoder.isStringHit(s3));
		assertFalse(stringEncoder.isStringHit(s2));
		tes.isValid(dt, new StringValue(s2));
		tes.writeValue(encoderContext, qncQc, bec); 	 // ["1234","1","12"]
		assertTrue(stringEncoder.isStringHit(s4));
		assertTrue(stringEncoder.isStringHit(s1));
		assertTrue(stringEncoder.isStringHit(s2));
		
		bec.flush();
		baos.flush();

		/*
		 * Decode
		 */
		TypeDecoder ddl = exiFactory.createTypeDecoder(); 
		StringDecoder stringDecoder = exiFactory.createStringDecoder();
		DecoderContext decoderContext = new DecoderContextImpl(grammarContext, stringDecoder);
//		StringTypeDecoder ddl = new StringTypeDecoder(
//				new BoundedStringDecoderImpl(valueMaxLength,
//						valuePartitionCapacity));
		
		BitDecoderChannel bdc = new BitDecoderChannel(new ByteArrayInputStream(
				baos.toByteArray()));

		assertTrue(ddl.readValue(dt, decoderContext, qncQa, bdc).toString().equals(s1));
		assertTrue(ddl.readValue(dt, decoderContext, qncQb, bdc).toString().equals(s2));
		assertTrue(ddl.readValue(dt, decoderContext, qncQa, bdc).toString().equals(s3));
		assertTrue(ddl.readValue(dt, decoderContext, qncQc, bdc).toString().equals(s3)); /* global hit */
		assertTrue(ddl.readValue(dt, decoderContext, qncQc, bdc).toString().equals(s4)); /* replaces values */
		assertTrue(ddl.readValue(dt, decoderContext, qncQa, bdc).toString().equals(s1)); /* no local hit*/
		assertTrue(ddl.readValue(dt, decoderContext, qncQc, bdc).toString().equals(s2)); /* no local hit*/
	}

	@Test
	public void testStringTableGlobalAttribute() throws Exception {
		String schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
			+ " <xs:element name='root'>"
			+ "  <xs:complexType>"
			+ "   <xs:sequence >"
			+ "    <xs:element name='a' type='xs:string' /> "
			+ "    <xs:element name='b' type='xs:string' /> "
			+ "   </xs:sequence>" + "  </xs:complexType>"
			+ " </xs:element>"
			+ " <xs:attribute name='globalAT' type='xs:integer' />"
			+ "</xs:schema>";
		Grammars g = GrammarTest.getGrammarFromSchemaAsString(schema);
		GrammarContext gc = g.getGrammarContext();
//		GrammarURIEntry[] gue = g.getGrammarEntries();
		
		// a, b, globalAT, root
		// String[] localNames0 = gue[0].localNames;
		GrammarUriContext uc0 = gc.getGrammarUriContext(0);
		assertTrue(uc0.getNumberOfQNames() == 4);
		
		assertTrue(uc0.getQNameContext(0).getLocalName().equals("a"));
		assertTrue(uc0.getQNameContext(1).getLocalName().equals("b"));
		assertTrue(uc0.getQNameContext(2).getLocalName().equals("globalAT"));
		assertTrue(uc0.getQNameContext(3).getLocalName().equals("root"));
	}

	@Test
	public void testStringTableAnyAttributeElement() throws Exception {
		String schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
			+ " <xs:element name='root'>"
			+ "  <xs:complexType>"
			+ "   <xs:sequence >"
			+ "    <xs:any namespace='urn:bla' />"
			+ "   </xs:sequence>"
			+ "   <xs:anyAttribute namespace='urn:foo'/>"
			+ "  </xs:complexType>"
			+ " </xs:element>"
			+ "</xs:schema>";
		Grammars g = GrammarTest.getGrammarFromSchemaAsString(schema);
		GrammarContext gc = g.getGrammarContext();
//		GrammarURIEntry[] gue = g.getGrammarEntries();
		
		
		
		// root
		GrammarUriContext uc0 = gc.getGrammarUriContext(0);
		assertTrue(uc0.getNamespaceUri().equals(""));
//		String[] localNames0 = gue[0].localNames;
		assertTrue(uc0.getNumberOfQNames() == 1);
		assertTrue(uc0.getQNameContext(0).getLocalName().equals("root"));
		
		// urn:bla
		GrammarUriContext uc4 = gc.getGrammarUriContext(4);
		assertTrue(uc4.getNamespaceUri().equals("urn:bla"));
		// String[] localNames4 = gue[4].localNames;
		assertTrue(uc4.getNumberOfQNames() == 0);
		
		// urn:foo
		GrammarUriContext uc5 = gc.getGrammarUriContext(5);
		assertTrue(uc5.getNamespaceUri().equals("urn:foo"));
		// String[] localNames5 = gue[5].localNames;
		assertTrue(uc5.getNumberOfQNames() == 0);
	}
	
	@Test
	public void testStringTableAnyOther() throws Exception {
		String schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
			+ " <xs:element name='root'>"
			+ "  <xs:complexType>"
			+ "   <xs:sequence >"
			+ "    <xs:any namespace='##other' />"
			+ "   </xs:sequence>"
			+ "  </xs:complexType>"
			+ " </xs:element>"
			+ "</xs:schema>";
		Grammars g = GrammarTest.getGrammarFromSchemaAsString(schema);
		// GrammarURIEntry[] gue = g.getGrammarEntries();
		GrammarContext gc = g.getGrammarContext();
		
		// root
		GrammarUriContext uc0 = gc.getGrammarUriContext(0);
		// String[] localNames0 = gue[0].localNames;
		assertTrue(uc0.getNumberOfQNames() == 1);
		assertTrue(uc0.getQNameContext(0).getLocalName().equals("root"));
		
		assertTrue(gc.getNumberOfGrammarUriContexts() == 4);
	}

}
