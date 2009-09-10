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

import com.siemens.ct.exi.core.Context;
import com.siemens.ct.exi.datatype.strings.StringDecoderImpl;
import com.siemens.ct.exi.datatype.strings.StringEncoderImpl;
import com.siemens.ct.exi.grammar.event.Attribute;
import com.siemens.ct.exi.grammar.event.StartElement;
import com.siemens.ct.exi.io.channel.BitDecoderChannel;
import com.siemens.ct.exi.io.channel.BitEncoderChannel;
import com.siemens.ct.exi.types.BuiltIn;
import com.siemens.ct.exi.types.StringTypeDecoder;
import com.siemens.ct.exi.types.StringTypeEncoder;

public class StringTableValueTest extends AbstractTestCase  {

	@Test
	public void testStringTableValue0() throws IOException {
		Context context = new StartElement("el", "");
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

		assertTrue(equals(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, context, bdc), val1));
		assertTrue(equals(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, context, bdc), val2));
		assertTrue(equals(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, context, bdc), val1));
		assertTrue(equals(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, context, bdc), val3));
		assertTrue(equals(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, context, bdc), val3));
		assertTrue(equals(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, context, bdc), val3));
	}

	@Test
	public void testStringTableValue1() throws IOException {		
		Context c1 = new StartElement("el1", "");
		Context c2 = new StartElement("el2", "");
		Context c3 = new StartElement("el3", "");
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

		assertTrue(equals(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, c1, bdc), val1));
		assertTrue(equals(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, c3, bdc), val2));
		assertTrue(equals(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, c2, bdc), val1));
		assertTrue(equals(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, c3, bdc), val3));
		assertTrue(equals(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, c1, bdc), val3));
		assertTrue(equals(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, c3, bdc), val3));
	}

	@Test
	public void testStringTableValue() throws IOException {

		Context ca1 = new Attribute("at1", "");

		Context cex2 = new StartElement("elx2", "");
		Context cex3 = new StartElement("elx3", "");
		
		Context cexx1 = new StartElement("elxx1", "");
		Context cexx2 = new StartElement("elxx2", "");

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
		tes.setStringEncoder(new StringEncoderImpl());
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

		assertTrue(equals(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, ca1, bdc), atCh1));
		assertTrue(equals(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, cexx1, bdc), ch1));
		assertTrue(equals(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, cexx2, bdc), ch2));
		assertTrue(equals(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, cex2, bdc), ch2));
		assertTrue(equals(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, cex3, bdc), ch3));
		assertTrue(equals(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, ca1, bdc), atCh2));
		assertTrue(equals(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, cexx1, bdc), ch1));
		assertTrue(equals(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, cexx2, bdc), ch2));
		assertTrue(equals(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, cex2, bdc), ch2));
		assertTrue(equals(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, cex2, bdc), ch3));
	}

}
