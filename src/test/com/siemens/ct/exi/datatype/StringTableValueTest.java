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

package com.siemens.ct.exi.datatype;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.core.NameContext;
import com.siemens.ct.exi.core.URIContext;
import com.siemens.ct.exi.datatype.decoder.TypeDecoderString;
import com.siemens.ct.exi.datatype.encoder.TypeEncoderString;
import com.siemens.ct.exi.datatype.strings.StringEncoderImpl;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;
import com.siemens.ct.exi.io.channel.BitDecoderChannel;
import com.siemens.ct.exi.io.channel.BitEncoderChannel;
import com.siemens.ct.exi.util.ExpandedName;

public class StringTableValueTest extends AbstractTestCase  {

	@Test
	public void testStringTableValue0() throws IOException {
		ExpandedName qn = new ExpandedName("", "el");
		NameContext context = new NameContext("el", new URIContext("", 0));
		String val1 = "val1";
		String val2 = "val2";
		String val3 = "val3";

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		EXIFactory exiFactory = DefaultEXIFactory.newInstance();

		/*
		 * Encode
		 */
		TypeEncoderString tes = new TypeEncoderString(exiFactory);
		tes.setStringEncoder(new StringEncoderImpl());
		BitEncoderChannel bec = new BitEncoderChannel(baos);

		tes.isTypeValid(BuiltIn.DEFAULT_DATATYPE, val1);
		tes.writeTypeValidValue(context, bec);
		tes.isTypeValid(BuiltIn.DEFAULT_DATATYPE, val2);
		tes.writeTypeValidValue(context, bec);
		tes.isTypeValid(BuiltIn.DEFAULT_DATATYPE, val1);
		tes.writeTypeValidValue(context, bec);
		tes.isTypeValid(BuiltIn.DEFAULT_DATATYPE, val3);
		tes.writeTypeValidValue(context, bec);
		tes.isTypeValid(BuiltIn.DEFAULT_DATATYPE, val3);
		tes.writeTypeValidValue(context, bec);
		tes.isTypeValid(BuiltIn.DEFAULT_DATATYPE, val3);
		tes.writeTypeValidValue(context, bec);

		bec.flush();
		baos.flush();

		/*
		 * Decode
		 */
		TypeDecoderString ddl = new TypeDecoderString(exiFactory);
		BitDecoderChannel bdc = new BitDecoderChannel(new ByteArrayInputStream(
				baos.toByteArray()));

		assertTrue(equals(ddl.readTypeValidValue(BuiltIn.DEFAULT_DATATYPE, bdc,
				qn.getNamespaceURI(), qn.getLocalName()), val1));
		assertTrue(equals(ddl.readTypeValidValue(BuiltIn.DEFAULT_DATATYPE, bdc,
				qn.getNamespaceURI(), qn.getLocalName()), val2));
		assertTrue(equals(ddl.readTypeValidValue(BuiltIn.DEFAULT_DATATYPE, bdc,
				qn.getNamespaceURI(), qn.getLocalName()), val1));
		assertTrue(equals(ddl.readTypeValidValue(BuiltIn.DEFAULT_DATATYPE, bdc,
				qn.getNamespaceURI(), qn.getLocalName()), val3));
		assertTrue(equals(ddl.readTypeValidValue(BuiltIn.DEFAULT_DATATYPE, bdc,
				qn.getNamespaceURI(), qn.getLocalName()), val3));
		assertTrue(equals(ddl.readTypeValidValue(BuiltIn.DEFAULT_DATATYPE, bdc,
				qn.getNamespaceURI(), qn.getLocalName()), val3));
	}

	@Test
	public void testStringTableValue1() throws IOException {
		URIContext uc = new URIContext("", 0);
		
		ExpandedName qn1 = new ExpandedName("", "el1");
		NameContext c1 = new NameContext("el1", uc);
		ExpandedName qn2 = new ExpandedName("", "el2");
		NameContext c2 = new NameContext("el2", uc);
		ExpandedName qn3 = new ExpandedName("", "el3");
		NameContext c3 = new NameContext("el3", uc);
		String val1 = "val1";
		String val2 = "val2";
		String val3 = "val3";

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		EXIFactory exiFactory = DefaultEXIFactory.newInstance();

		/*
		 * Encode
		 */
		TypeEncoderString tes = new TypeEncoderString(exiFactory);
		tes.setStringEncoder(new StringEncoderImpl());
		BitEncoderChannel bec = new BitEncoderChannel(baos);

		tes.isTypeValid(BuiltIn.DEFAULT_DATATYPE, val1);
		tes.writeTypeValidValue(c1, bec);
		tes.isTypeValid(BuiltIn.DEFAULT_DATATYPE, val2);
		tes.writeTypeValidValue(c3, bec);
		tes.isTypeValid(BuiltIn.DEFAULT_DATATYPE, val1);
		tes.writeTypeValidValue(c2, bec);
		tes.isTypeValid(BuiltIn.DEFAULT_DATATYPE, val3);
		tes.writeTypeValidValue(c3, bec);
		tes.isTypeValid(BuiltIn.DEFAULT_DATATYPE, val3);
		tes.writeTypeValidValue(c1, bec);
		tes.isTypeValid(BuiltIn.DEFAULT_DATATYPE, val3);
		tes.writeTypeValidValue(c3, bec);

		bec.flush();
		baos.flush();

		/*
		 * Decode
		 */
		TypeDecoderString ddl = new TypeDecoderString(exiFactory);
		BitDecoderChannel bdc = new BitDecoderChannel(new ByteArrayInputStream(
				baos.toByteArray()));

		assertTrue(equals(ddl.readTypeValidValue(BuiltIn.DEFAULT_DATATYPE, bdc,
				qn1.getNamespaceURI(), qn1.getLocalName()), val1));
		assertTrue(equals(ddl.readTypeValidValue(BuiltIn.DEFAULT_DATATYPE, bdc,
				qn3.getNamespaceURI(), qn3.getLocalName()), val2));
		assertTrue(equals(ddl.readTypeValidValue(BuiltIn.DEFAULT_DATATYPE, bdc,
				qn2.getNamespaceURI(), qn2.getLocalName()), val1));
		assertTrue(equals(ddl.readTypeValidValue(BuiltIn.DEFAULT_DATATYPE, bdc,
				qn3.getNamespaceURI(), qn3.getLocalName()), val3));
		assertTrue(equals(ddl.readTypeValidValue(BuiltIn.DEFAULT_DATATYPE, bdc,
				qn1.getNamespaceURI(), qn1.getLocalName()), val3));
		assertTrue(equals(ddl.readTypeValidValue(BuiltIn.DEFAULT_DATATYPE, bdc,
				qn3.getNamespaceURI(), qn3.getLocalName()), val3));
	}

	@Test
	public void testStringTableValue() throws IOException {
		URIContext uc = new URIContext("", 0);
		
		ExpandedName at1 = new ExpandedName("", "at1");
		NameContext ca1 = new NameContext("at1", uc);

		ExpandedName elx2 = new ExpandedName("", "elx2");
		NameContext cex2 = new NameContext("elx2", uc);
		ExpandedName elx3 = new ExpandedName("", "elx3");
		NameContext cex3 = new NameContext("elx3", uc);
		
		ExpandedName elxx1 = new ExpandedName("", "elxx1");
		NameContext cexx1 = new NameContext("elxx1", uc);
		ExpandedName elxx2 = new ExpandedName("", "elxx2");
		NameContext cexx2 = new NameContext("elxx2", uc);

		String atCh1 = "at-ch1";
		String atCh2 = "at-ch2";
		String ch1 = "ch1";
		String ch2 = "ch2";
		String ch3 = "ch3";

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		EXIFactory exiFactory = DefaultEXIFactory.newInstance();

		/*
		 * Encode
		 */
		TypeEncoderString tes = new TypeEncoderString(exiFactory);
		tes.setStringEncoder(new StringEncoderImpl());
		BitEncoderChannel bec = new BitEncoderChannel(baos);

		tes.isTypeValid(BuiltIn.DEFAULT_DATATYPE, atCh1);
		tes.writeTypeValidValue(ca1, bec);
		tes.isTypeValid(BuiltIn.DEFAULT_DATATYPE, ch1);
		tes.writeTypeValidValue(cexx1, bec);
		tes.isTypeValid(BuiltIn.DEFAULT_DATATYPE, ch2);
		tes.writeTypeValidValue(cexx2, bec);
		tes.isTypeValid(BuiltIn.DEFAULT_DATATYPE, ch2);
		tes.writeTypeValidValue(cex2, bec);
		tes.isTypeValid(BuiltIn.DEFAULT_DATATYPE, ch3);
		tes.writeTypeValidValue(cex3, bec);
		tes.isTypeValid(BuiltIn.DEFAULT_DATATYPE, atCh2);
		tes.writeTypeValidValue(ca1, bec);
		tes.isTypeValid(BuiltIn.DEFAULT_DATATYPE, ch1);
		tes.writeTypeValidValue(cexx1, bec);
		tes.isTypeValid(BuiltIn.DEFAULT_DATATYPE, ch2);
		tes.writeTypeValidValue(cexx2, bec);
		tes.isTypeValid(BuiltIn.DEFAULT_DATATYPE, ch2);
		tes.writeTypeValidValue(cex2, bec);
		tes.isTypeValid(BuiltIn.DEFAULT_DATATYPE, ch3);
		tes.writeTypeValidValue(cex2, bec);

		bec.flush();
		baos.flush();

		/*
		 * Decode
		 */
		TypeDecoderString ddl = new TypeDecoderString(exiFactory);
		BitDecoderChannel bdc = new BitDecoderChannel(new ByteArrayInputStream(
				baos.toByteArray()));

		assertTrue(equals(ddl.readTypeValidValue(BuiltIn.DEFAULT_DATATYPE, bdc,
				at1.getNamespaceURI(), at1.getLocalName()), atCh1));
		assertTrue(equals(ddl.readTypeValidValue(BuiltIn.DEFAULT_DATATYPE, bdc,
				elxx1.getNamespaceURI(), elxx1.getLocalName()), ch1));
		assertTrue(equals(ddl.readTypeValidValue(BuiltIn.DEFAULT_DATATYPE, bdc,
				elxx2.getNamespaceURI(), elxx2.getLocalName()), ch2));
		assertTrue(equals(ddl.readTypeValidValue(BuiltIn.DEFAULT_DATATYPE, bdc,
				elx2.getNamespaceURI(), elx2.getLocalName()), ch2));
		assertTrue(equals(ddl.readTypeValidValue(BuiltIn.DEFAULT_DATATYPE, bdc,
				elx3.getNamespaceURI(), elx3.getLocalName()), ch3));
		assertTrue(equals(ddl.readTypeValidValue(BuiltIn.DEFAULT_DATATYPE, bdc,
				at1.getNamespaceURI(), at1.getLocalName()), atCh2));
		assertTrue(equals(ddl.readTypeValidValue(BuiltIn.DEFAULT_DATATYPE, bdc,
				elxx1.getNamespaceURI(), elxx1.getLocalName()), ch1));
		assertTrue(equals(ddl.readTypeValidValue(BuiltIn.DEFAULT_DATATYPE, bdc,
				elxx2.getNamespaceURI(), elxx2.getLocalName()), ch2));
		assertTrue(equals(ddl.readTypeValidValue(BuiltIn.DEFAULT_DATATYPE, bdc,
				elx2.getNamespaceURI(), elx2.getLocalName()), ch2));
		assertTrue(equals(ddl.readTypeValidValue(BuiltIn.DEFAULT_DATATYPE, bdc,
				elx2.getNamespaceURI(), elx2.getLocalName()), ch3));
	}

}
