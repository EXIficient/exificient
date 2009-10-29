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

import javax.xml.namespace.QName;

import org.junit.Test;

import com.siemens.ct.exi.datatype.strings.StringDecoderImpl;
import com.siemens.ct.exi.datatype.strings.StringEncoderImpl;
import com.siemens.ct.exi.io.channel.BitDecoderChannel;
import com.siemens.ct.exi.io.channel.BitEncoderChannel;
import com.siemens.ct.exi.types.BuiltIn;
import com.siemens.ct.exi.types.StringTypeDecoder;
import com.siemens.ct.exi.types.StringTypeEncoder;

public class StringTableValueTest extends AbstractTestCase  {

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

		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, context, bdc).toString().equals(val1));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, context, bdc).toString().equals(val2));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, context, bdc).toString().equals(val1));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, context, bdc).toString().equals(val3));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, context, bdc).toString().equals(val3));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, context, bdc).toString().equals(val3));
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

		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, c1, bdc).toString().equals(val1));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, c3, bdc).toString().equals(val2));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, c2, bdc).toString().equals(val1));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, c3, bdc).toString().equals(val3));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, c1, bdc).toString().equals(val3));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, c3, bdc).toString().equals(val3));
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

		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, ca1, bdc).toString().equals(atCh1));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, cexx1, bdc).toString().equals(ch1));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, cexx2, bdc).toString().equals(ch2));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, cex2, bdc).toString().equals(ch2));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, cex3, bdc).toString().equals(ch3));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, ca1, bdc).toString().equals(atCh2));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, cexx1, bdc).toString().equals(ch1));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, cexx2, bdc).toString().equals(ch2));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, cex2, bdc).toString().equals(ch2));
		assertTrue(ddl.readValue(BuiltIn.DEFAULT_DATATYPE, cex2, bdc).toString().equals(ch3));
	}

}
