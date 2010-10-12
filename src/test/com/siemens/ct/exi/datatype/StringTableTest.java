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

package com.siemens.ct.exi.datatype;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.namespace.QName;

import org.junit.Test;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.datatype.strings.BoundedStringDecoderImpl;
import com.siemens.ct.exi.datatype.strings.BoundedStringEncoderImpl;
import com.siemens.ct.exi.datatype.strings.StringDecoder;
import com.siemens.ct.exi.datatype.strings.StringDecoderImpl;
import com.siemens.ct.exi.datatype.strings.StringEncoder;
import com.siemens.ct.exi.datatype.strings.StringEncoderImpl;
import com.siemens.ct.exi.grammar.Grammar;
import com.siemens.ct.exi.grammar.GrammarTest;
import com.siemens.ct.exi.grammar.GrammarURIEntry;
import com.siemens.ct.exi.io.channel.BitDecoderChannel;
import com.siemens.ct.exi.io.channel.BitEncoderChannel;
import com.siemens.ct.exi.types.BuiltIn;
import com.siemens.ct.exi.types.StringTypeDecoder;
import com.siemens.ct.exi.types.StringTypeEncoder;

public class StringTableTest extends AbstractTestCase {

	@Test
	public void testStringTableValue0() throws IOException {
		QName context = new QName("el", "");
		String val1 = "val1";
		String val2 = "val2";
		String val3 = "val3";

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		/*
		 * Encode
		 */
		StringTypeEncoder tes = new StringTypeEncoder(new StringEncoderImpl());
		BitEncoderChannel bec = new BitEncoderChannel(baos);

		tes.isValid(BuiltIn.DEFAULT_DATATYPE, val1);
		tes.writeValue(context, bec);
		tes.isValid(BuiltIn.DEFAULT_DATATYPE, val2);
		tes.writeValue(context, bec);
		tes.isValid(BuiltIn.DEFAULT_DATATYPE, val1);
		tes.writeValue(context, bec);
		tes.isValid(BuiltIn.DEFAULT_DATATYPE, val3);
		tes.writeValue(context, bec);
		tes.isValid(BuiltIn.DEFAULT_DATATYPE, val3);
		tes.writeValue(context, bec);
		tes.isValid(BuiltIn.DEFAULT_DATATYPE, val3);
		tes.writeValue(context, bec);

		bec.flush();
		baos.flush();

		/*
		 * Decode
		 */
		StringTypeDecoder ddl = new StringTypeDecoder(new StringDecoderImpl());
		BitDecoderChannel bdc = new BitDecoderChannel(new ByteArrayInputStream(
				baos.toByteArray()));

		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, context, bdc)
				.toString().equals(val1));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, context, bdc)
				.toString().equals(val2));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, context, bdc)
				.toString().equals(val1));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, context, bdc)
				.toString().equals(val3));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, context, bdc)
				.toString().equals(val3));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, context, bdc)
				.toString().equals(val3));
	}

	@Test
	public void testStringTableValue1() throws IOException {
		QName c1 = new QName("el1", "");
		QName c2 = new QName("el2", "");
		QName c3 = new QName("el3", "");
		String val1 = "val1";
		String val2 = "val2";
		String val3 = "val3";

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		/*
		 * Encode
		 */
		StringTypeEncoder tes = new StringTypeEncoder(new StringEncoderImpl());
		BitEncoderChannel bec = new BitEncoderChannel(baos);

		tes.isValid(BuiltIn.DEFAULT_DATATYPE, val1);
		tes.writeValue(c1, bec);
		tes.isValid(BuiltIn.DEFAULT_DATATYPE, val2);
		tes.writeValue(c3, bec);
		tes.isValid(BuiltIn.DEFAULT_DATATYPE, val1);
		tes.writeValue(c2, bec);
		tes.isValid(BuiltIn.DEFAULT_DATATYPE, val3);
		tes.writeValue(c3, bec);
		tes.isValid(BuiltIn.DEFAULT_DATATYPE, val3);
		tes.writeValue(c1, bec);
		tes.isValid(BuiltIn.DEFAULT_DATATYPE, val3);
		tes.writeValue(c3, bec);

		bec.flush();
		baos.flush();

		/*
		 * Decode
		 */
		StringTypeDecoder ddl = new StringTypeDecoder(new StringDecoderImpl());
		BitDecoderChannel bdc = new BitDecoderChannel(new ByteArrayInputStream(
				baos.toByteArray()));

		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, c1, bdc).toString()
				.equals(val1));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, c3, bdc).toString()
				.equals(val2));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, c2, bdc).toString()
				.equals(val1));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, c3, bdc).toString()
				.equals(val3));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, c1, bdc).toString()
				.equals(val3));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, c3, bdc).toString()
				.equals(val3));
	}

	@Test
	public void testStringTableValue() throws IOException {

		QName ca1 = new QName("at1", "");

		QName cex2 = new QName("elx2", "");
		QName cex3 = new QName("elx3", "");

		QName cexx1 = new QName("elxx1", "");
		QName cexx2 = new QName("elxx2", "");

		String atCh1 = "at-ch1";
		String atCh2 = "at-ch2";
		String ch1 = "ch1";
		String ch2 = "ch2";
		String ch3 = "ch3";

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		/*
		 * Encode
		 */
		StringTypeEncoder tes = new StringTypeEncoder(new StringEncoderImpl());
		BitEncoderChannel bec = new BitEncoderChannel(baos);

		tes.isValid(BuiltIn.DEFAULT_DATATYPE, atCh1);
		tes.writeValue(ca1, bec);
		tes.isValid(BuiltIn.DEFAULT_DATATYPE, ch1);
		tes.writeValue(cexx1, bec);
		tes.isValid(BuiltIn.DEFAULT_DATATYPE, ch2);
		tes.writeValue(cexx2, bec);
		tes.isValid(BuiltIn.DEFAULT_DATATYPE, ch2);
		tes.writeValue(cex2, bec);
		tes.isValid(BuiltIn.DEFAULT_DATATYPE, ch3);
		tes.writeValue(cex3, bec);
		tes.isValid(BuiltIn.DEFAULT_DATATYPE, atCh2);
		tes.writeValue(ca1, bec);
		tes.isValid(BuiltIn.DEFAULT_DATATYPE, ch1);
		tes.writeValue(cexx1, bec);
		tes.isValid(BuiltIn.DEFAULT_DATATYPE, ch2);
		tes.writeValue(cexx2, bec);
		tes.isValid(BuiltIn.DEFAULT_DATATYPE, ch2);
		tes.writeValue(cex2, bec);
		tes.isValid(BuiltIn.DEFAULT_DATATYPE, ch3);
		tes.writeValue(cex2, bec);

		bec.flush();
		baos.flush();

		/*
		 * Decode
		 */
		StringTypeDecoder ddl = new StringTypeDecoder(new StringDecoderImpl());
		BitDecoderChannel bdc = new BitDecoderChannel(new ByteArrayInputStream(
				baos.toByteArray()));

		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, ca1, bdc).toString()
				.equals(atCh1));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, cexx1, bdc)
				.toString().equals(ch1));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, cexx2, bdc)
				.toString().equals(ch2));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, cex2, bdc)
				.toString().equals(ch2));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, cex3, bdc)
				.toString().equals(ch3));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, ca1, bdc).toString()
				.equals(atCh2));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, cexx1, bdc)
				.toString().equals(ch1));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, cexx2, bdc)
				.toString().equals(ch2));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, cex2, bdc)
				.toString().equals(ch2));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, cex2, bdc)
				.toString().equals(ch3));
	}

	@Test
	public void testStringTableValueMaxLength1() throws IOException {

		QName qa = new QName("a", "");
		QName qb = new QName("b", "");

		String s3 = "123";
		String s4 = "1234";
		String s5 = "12345";
		String s6 = "123456";
		String s7 = "1234567";

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		int valueMaxLength = 5;
		int valuePartitionCapacity = Constants.DEFAULT_VALUE_PARTITON_CAPACITY;

		Datatype dt = BuiltIn.DEFAULT_DATATYPE;
		/*
		 * Encode
		 */
		StringEncoder se = new BoundedStringEncoderImpl(valueMaxLength,
				valuePartitionCapacity);
		StringTypeEncoder tes = new StringTypeEncoder(se);
		BitEncoderChannel bec = new BitEncoderChannel(baos);


		// a: 123
		tes.isValid(dt, s3);
		tes.writeValue(qa, bec);
		// b: 1234
		tes.isValid(dt, s4);
		tes.writeValue(qb, bec);
		// a: 12345
		tes.isValid(dt, s5);
		tes.writeValue(qa, bec);
		// b: 123456 /* to large */
		assertFalse(se.isStringHit(qb, s6));
		tes.isValid(dt, s6);
		tes.writeValue(qb, bec);
		// a: 1234567 /* to large */
		assertFalse(se.isStringHit(qa, s7));
		tes.isValid(dt, s7);
		tes.writeValue(qa, bec);

		bec.flush();
		baos.flush();

		/*
		 * Decode
		 */
		StringDecoder sd = new BoundedStringDecoderImpl(valueMaxLength,
				valuePartitionCapacity);
		StringTypeDecoder ddl = new StringTypeDecoder(sd);
		BitDecoderChannel bdc = new BitDecoderChannel(new ByteArrayInputStream(
				baos.toByteArray()));

		assertTrue(ddl.readValue(dt, qa, bdc).toString().equals(s3));
		assertTrue(ddl.readValue(dt, qb, bdc).toString().equals(s4));
		assertTrue(ddl.readValue(dt, qa, bdc).toString().equals(s5));
		assertTrue(ddl.readValue(dt, qb, bdc).toString().equals(s6));
		assertTrue(ddl.readValue(dt, qa, bdc).toString().equals(s7));
	}
	
	@Test
	public void testStringTableValuePartitionCapacity() throws IOException {

		QName qa = new QName("a", "");
		QName qb = new QName("b", "");
		QName qc = new QName("c", "");

		String s1 = "1";
		String s2 = "12";
		String s3 = "123";
		String s4 = "1234";
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		int valueMaxLength = Constants.DEFAULT_VALUE_MAX_LENGTH;
		int valuePartitionCapacity = 3;

		Datatype dt = BuiltIn.DEFAULT_DATATYPE;
		/*
		 * Encode
		 */
		StringEncoder se = new BoundedStringEncoderImpl(valueMaxLength,
				valuePartitionCapacity);
		StringTypeEncoder tes = new StringTypeEncoder(se);
		BitEncoderChannel bec = new BitEncoderChannel(baos);


		// a: 1
		tes.isValid(dt, s1);
		tes.writeValue(qa, bec); // ["1"{a}]
		// b: 12
		tes.isValid(dt, s2);
		tes.writeValue(qb, bec);  // ["1"{a},"12"{b}]
		// a: 123
		tes.isValid(dt, s3);
		tes.writeValue(qa, bec); // ["1"{a},"12"{b},"123"{a}]
		// c: 123 /* global hit */
		tes.isValid(dt, s3);
		tes.writeValue(qc, bec); // ["1","12","123"]
		// c: 1234
		tes.isValid(dt, s4);
		tes.writeValue(qc, bec); // ["1234","12","123"]
		// a: 1 /* no local hit due to valuePartitionCapacity*/
		assertFalse(se.isStringHit(qa, s1));
		tes.isValid(dt, s1);
		tes.writeValue(qa, bec);	 // ["1234","1","123"]
		// c: 1 /* no local hit due to valuePartitionCapacity*/
		assertTrue(se.isStringHit(qc, s4));
		assertTrue(se.isStringHit(qa, s1));
		assertTrue(se.isStringHit(qc, s3));
		assertFalse(se.isStringHit(qc, s2));
		tes.isValid(dt, s2);
		tes.writeValue(qc, bec); 	 // ["1234","1","12"]
		assertTrue(se.isStringHit(qc, s4));
		assertTrue(se.isStringHit(qa, s1));
		assertTrue(se.isStringHit(qc, s2));
		
		bec.flush();
		baos.flush();

		/*
		 * Decode
		 */
		StringTypeDecoder ddl = new StringTypeDecoder(
				new BoundedStringDecoderImpl(valueMaxLength,
						valuePartitionCapacity));
		BitDecoderChannel bdc = new BitDecoderChannel(new ByteArrayInputStream(
				baos.toByteArray()));

		assertTrue(ddl.readValue(dt, qa, bdc).toString().equals(s1));
		assertTrue(ddl.readValue(dt, qb, bdc).toString().equals(s2));
		assertTrue(ddl.readValue(dt, qa, bdc).toString().equals(s3));
		assertTrue(ddl.readValue(dt, qc, bdc).toString().equals(s3)); /* global hit */
		assertTrue(ddl.readValue(dt, qc, bdc).toString().equals(s4)); /* replaces values */
		assertTrue(ddl.readValue(dt, qa, bdc).toString().equals(s1)); /* no local hit*/
		assertTrue(ddl.readValue(dt, qc, bdc).toString().equals(s2)); /* no local hit*/
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
		Grammar g = GrammarTest.getGrammarFromSchemaAsString(schema);
		GrammarURIEntry[] gue = g.getGrammarEntries();
		
		// a, b, globalAT, root
		String[] localNames0 = gue[0].localNames;
		assertTrue(localNames0.length == 4);
		assertTrue("a".equals(localNames0[0]));
		assertTrue("b".equals(localNames0[1]));
		assertTrue("globalAT".equals(localNames0[2]));
		assertTrue("root".equals(localNames0[3]));
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
		Grammar g = GrammarTest.getGrammarFromSchemaAsString(schema);
		GrammarURIEntry[] gue = g.getGrammarEntries();
		
		// root
		String[] localNames0 = gue[0].localNames;
		assertTrue(localNames0.length == 1);
		assertTrue("root".equals(localNames0[0]));
		
		// urn:bla
		assertTrue("urn:bla".equals(gue[4].uri));
		String[] localNames4 = gue[4].localNames;
		assertTrue(localNames4.length == 0);
		
		// urn:foo
		assertTrue("urn:foo".equals(gue[5].uri));
		String[] localNames5 = gue[5].localNames;
		assertTrue(localNames5.length == 0);
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
		Grammar g = GrammarTest.getGrammarFromSchemaAsString(schema);
		GrammarURIEntry[] gue = g.getGrammarEntries();
		
		// root
		String[] localNames0 = gue[0].localNames;
		assertTrue(localNames0.length == 1);
		assertTrue("root".equals(localNames0[0]));
		
		assertTrue(gue.length == 4);
	}

}
