/*
 * Copyright (C) 2007, 2008 Siemens AG
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

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

import com.siemens.ct.exi.datatype.BuiltIn;
import com.siemens.ct.exi.datatype.decoder.TypeDecoderLexical;
import com.siemens.ct.exi.datatype.encoder.TypeEncoderLexical;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;
import com.siemens.ct.exi.io.channel.BitDecoderChannel;
import com.siemens.ct.exi.io.channel.BitEncoderChannel;
import com.siemens.ct.exi.util.ExpandedName;

public class StringTableValueTest
{

	@Test
	public void testStringTableValue0 () throws IOException
	{
		ExpandedName qn = new ExpandedName( "", "el" );
		String val1 = "val1";
		String val2 = "val2";
		String val3 = "val3";
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		/*
		 * Encode
		 */
		//TypeEncoderLexical del = new TypeEncoderLexical( false );
		TypeEncoderLexical del = new TypeEncoderLexical( DefaultEXIFactory.newInstance ( ) );
		BitEncoderChannel bec = new BitEncoderChannel( baos );
		
//		DatatypeEvent dtEvent = new Attribute( qn, null, BuiltIn.DEFAULT_DATATYPE );
		
		del.isTypeValid ( BuiltIn.DEFAULT_DATATYPE, val1 );
		del.writeTypeValidValue ( bec, qn.getNamespaceURI ( ), qn.getLocalName ( ) );
		del.isTypeValid ( BuiltIn.DEFAULT_DATATYPE, val2 );
		del.writeTypeValidValue ( bec, qn.getNamespaceURI ( ), qn.getLocalName ( ) );
		del.isTypeValid ( BuiltIn.DEFAULT_DATATYPE, val1 );
		del.writeTypeValidValue ( bec, qn.getNamespaceURI ( ), qn.getLocalName ( )  );
		del.isTypeValid ( BuiltIn.DEFAULT_DATATYPE, val3 );
		del.writeTypeValidValue ( bec, qn.getNamespaceURI ( ), qn.getLocalName ( )  );
		del.isTypeValid ( BuiltIn.DEFAULT_DATATYPE, val3 );
		del.writeTypeValidValue ( bec, qn.getNamespaceURI ( ), qn.getLocalName ( )  );
		del.isTypeValid ( BuiltIn.DEFAULT_DATATYPE, val3 );
		del.writeTypeValidValue ( bec, qn.getNamespaceURI ( ), qn.getLocalName ( )  );
		
		bec.flush ( );
		baos.flush ( );
		
		/*
		 * Decode
		 */
		TypeDecoderLexical ddl = new TypeDecoderLexical( false );
		BitDecoderChannel bdc = new BitDecoderChannel( new ByteArrayInputStream( baos.toByteArray ( ) ) );
		
		assertTrue ( ddl.decodeValue ( BuiltIn.DEFAULT_DATATYPE, bdc, qn.getNamespaceURI ( ), qn.getLocalName ( ) ).equals (  val1 )  );
		assertTrue ( ddl.decodeValue ( BuiltIn.DEFAULT_DATATYPE, bdc, qn.getNamespaceURI ( ), qn.getLocalName ( ) ).equals (  val2 )  );
		assertTrue ( ddl.decodeValue ( BuiltIn.DEFAULT_DATATYPE, bdc, qn.getNamespaceURI ( ), qn.getLocalName ( ) ).equals (  val1 )  );
		assertTrue ( ddl.decodeValue ( BuiltIn.DEFAULT_DATATYPE, bdc, qn.getNamespaceURI ( ), qn.getLocalName ( ) ).equals (  val3 )  );
		assertTrue ( ddl.decodeValue ( BuiltIn.DEFAULT_DATATYPE, bdc, qn.getNamespaceURI ( ), qn.getLocalName ( ) ).equals (  val3 )  );
		assertTrue ( ddl.decodeValue ( BuiltIn.DEFAULT_DATATYPE, bdc, qn.getNamespaceURI ( ), qn.getLocalName ( ) ).equals (  val3 )  );
	}

	@Test
	public void testStringTableValue1 () throws IOException
	{
		ExpandedName qn1 = new ExpandedName( "", "el1" );
		ExpandedName qn2 = new ExpandedName( "", "el2" );
		ExpandedName qn3 = new ExpandedName( "", "el3" );
		String val1 = "val1";
		String val2 = "val2";
		String val3 = "val3";
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		/*
		 * Encode
		 */
		//TypeEncoderLexical del = new TypeEncoderLexical( true );
		TypeEncoderLexical del = new TypeEncoderLexical( DefaultEXIFactory.newInstance ( ) );
		BitEncoderChannel bec = new BitEncoderChannel( baos );
		
//		DatatypeEvent dtEvent = new Characters( null, BuiltIn.DEFAULT_DATATYPE );
		
		del.isTypeValid ( BuiltIn.DEFAULT_DATATYPE, val1 );
		del.writeTypeValidValue ( bec, qn1.getNamespaceURI ( ), qn1.getLocalName ( )  );
		del.isTypeValid ( BuiltIn.DEFAULT_DATATYPE, val2 );
		del.writeTypeValidValue ( bec, qn3.getNamespaceURI ( ), qn3.getLocalName ( )  );
		del.isTypeValid ( BuiltIn.DEFAULT_DATATYPE, val1 );
		del.writeTypeValidValue ( bec, qn2.getNamespaceURI ( ), qn2.getLocalName ( ) );
		del.isTypeValid ( BuiltIn.DEFAULT_DATATYPE, val3 );
		del.writeTypeValidValue ( bec, qn3.getNamespaceURI ( ), qn3.getLocalName ( ) );
		del.isTypeValid ( BuiltIn.DEFAULT_DATATYPE, val3 );
		del.writeTypeValidValue ( bec, qn1.getNamespaceURI ( ), qn1.getLocalName ( ) );
		del.isTypeValid ( BuiltIn.DEFAULT_DATATYPE, val3 );
		del.writeTypeValidValue ( bec, qn3.getNamespaceURI ( ), qn3.getLocalName ( ) );
		
		bec.flush ( );
		baos.flush ( );
		
		/*
		 * Decode
		 */
		TypeDecoderLexical ddl = new TypeDecoderLexical( true );
		BitDecoderChannel bdc = new BitDecoderChannel( new ByteArrayInputStream( baos.toByteArray ( ) ) );
		
		assertTrue ( ddl.decodeValue ( BuiltIn.DEFAULT_DATATYPE, bdc, qn1.getNamespaceURI ( ), qn1.getLocalName ( ) ).equals (  val1 )  );
		assertTrue ( ddl.decodeValue ( BuiltIn.DEFAULT_DATATYPE, bdc, qn3.getNamespaceURI ( ), qn3.getLocalName ( ) ).equals (  val2 )  );
		assertTrue ( ddl.decodeValue ( BuiltIn.DEFAULT_DATATYPE, bdc, qn2.getNamespaceURI ( ), qn2.getLocalName ( ) ).equals (  val1 )  );
		assertTrue ( ddl.decodeValue ( BuiltIn.DEFAULT_DATATYPE, bdc, qn3.getNamespaceURI ( ), qn3.getLocalName ( ) ).equals (  val3 )  );
		assertTrue ( ddl.decodeValue ( BuiltIn.DEFAULT_DATATYPE, bdc, qn1.getNamespaceURI ( ), qn1.getLocalName ( ) ).equals (  val3 )  );
		assertTrue ( ddl.decodeValue ( BuiltIn.DEFAULT_DATATYPE, bdc, qn3.getNamespaceURI ( ), qn3.getLocalName ( ) ).equals (  val3 )  );
	}

	@Test
	public void testStringTableValue () throws IOException
	{
		ExpandedName at1 = new ExpandedName( "", "at1" );
		
		ExpandedName elx2 = new ExpandedName( "", "elx2" );
		ExpandedName elx3 = new ExpandedName( "", "elx3" );
		
		ExpandedName elxx1 = new ExpandedName( "", "elxx1" );
		ExpandedName elxx2 = new ExpandedName( "", "elxx2" );
		
		String atCh1 = "at-ch1";
		String atCh2 = "at-ch2";
		String ch1 = "ch1";
		String ch2 = "ch2";
		String ch3 = "ch3";
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		/*
		 * Encode
		 */
		//TypeEncoderLexical del = new TypeEncoderLexical( false );
		TypeEncoderLexical del = new TypeEncoderLexical( DefaultEXIFactory.newInstance ( ) );
		BitEncoderChannel bec = new BitEncoderChannel( baos );
		

//		DatatypeEvent dtEvent = new Characters( null );
		
		del.isTypeValid ( BuiltIn.DEFAULT_DATATYPE, atCh1 );
		del.writeTypeValidValue ( bec, at1.getNamespaceURI ( ), at1.getLocalName ( )  );
		del.isTypeValid ( BuiltIn.DEFAULT_DATATYPE, ch1 );
		del.writeTypeValidValue ( bec, elxx1.getNamespaceURI ( ), elxx1.getLocalName ( )  );
		del.isTypeValid ( BuiltIn.DEFAULT_DATATYPE, ch2 );
		del.writeTypeValidValue ( bec, elxx2.getNamespaceURI ( ), elxx2.getLocalName ( ) );
		del.isTypeValid ( BuiltIn.DEFAULT_DATATYPE, ch2 );
		del.writeTypeValidValue ( bec, elx2.getNamespaceURI ( ), elx2.getLocalName ( ) );
		del.isTypeValid ( BuiltIn.DEFAULT_DATATYPE, ch3 );
		del.writeTypeValidValue ( bec, elx3.getNamespaceURI ( ), elx3.getLocalName ( ) );
		del.isTypeValid ( BuiltIn.DEFAULT_DATATYPE, atCh2 );
		del.writeTypeValidValue ( bec, at1.getNamespaceURI ( ), at1.getLocalName ( ) );
		del.isTypeValid ( BuiltIn.DEFAULT_DATATYPE, ch1 );
		del.writeTypeValidValue ( bec, elxx1.getNamespaceURI ( ), elxx1.getLocalName ( ) );
		del.isTypeValid ( BuiltIn.DEFAULT_DATATYPE, ch2 );
		del.writeTypeValidValue ( bec, elxx2.getNamespaceURI ( ), elxx2.getLocalName ( ) );
		del.isTypeValid ( BuiltIn.DEFAULT_DATATYPE, ch2 );
		del.writeTypeValidValue ( bec, elx2.getNamespaceURI ( ), elx2.getLocalName ( ) );
		del.isTypeValid ( BuiltIn.DEFAULT_DATATYPE, ch3 );
		del.writeTypeValidValue ( bec, elx2.getNamespaceURI ( ), elx2.getLocalName ( ) );
		
		bec.flush ( );
		baos.flush ( );
		
		/*
		 * Decode
		 */
		TypeDecoderLexical ddl = new TypeDecoderLexical( false);
		BitDecoderChannel bdc = new BitDecoderChannel( new ByteArrayInputStream( baos.toByteArray ( ) ) );
		
		assertTrue ( ddl.decodeValue ( BuiltIn.DEFAULT_DATATYPE, bdc, at1.getNamespaceURI ( ), at1.getLocalName ( ) ).equals (  atCh1 )  );
		assertTrue ( ddl.decodeValue ( BuiltIn.DEFAULT_DATATYPE, bdc, elxx1.getNamespaceURI ( ), elxx1.getLocalName ( ) ).equals (  ch1 )  );
		assertTrue ( ddl.decodeValue ( BuiltIn.DEFAULT_DATATYPE, bdc, elxx2.getNamespaceURI ( ), elxx2.getLocalName ( ) ).equals (  ch2 )  );
		assertTrue ( ddl.decodeValue ( BuiltIn.DEFAULT_DATATYPE, bdc, elx2.getNamespaceURI ( ), elx2.getLocalName ( ) ).equals (  ch2 )  );
		assertTrue ( ddl.decodeValue ( BuiltIn.DEFAULT_DATATYPE, bdc, elx3.getNamespaceURI ( ), elx3.getLocalName ( ) ).equals (  ch3 )  );
		assertTrue ( ddl.decodeValue ( BuiltIn.DEFAULT_DATATYPE, bdc, at1.getNamespaceURI ( ), at1.getLocalName ( ) ).equals (  atCh2 )  );
		assertTrue ( ddl.decodeValue ( BuiltIn.DEFAULT_DATATYPE, bdc, elxx1.getNamespaceURI ( ), elxx1.getLocalName ( ) ).equals (  ch1 )  );
		assertTrue ( ddl.decodeValue ( BuiltIn.DEFAULT_DATATYPE, bdc, elxx2.getNamespaceURI ( ), elxx2.getLocalName ( ) ).equals (  ch2 )  );
		assertTrue ( ddl.decodeValue ( BuiltIn.DEFAULT_DATATYPE, bdc, elx2.getNamespaceURI ( ), elx2.getLocalName ( ) ).equals (  ch2 )  );
		assertTrue ( ddl.decodeValue ( BuiltIn.DEFAULT_DATATYPE, bdc, elx2.getNamespaceURI ( ), elx2.getLocalName ( ) ).equals (  ch3 )  );
	}


	

}
