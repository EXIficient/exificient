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

package com.siemens.ct.exi.datatype.decoder;

import java.io.IOException;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.datatype.BuiltInType;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.datatype.DatatypeList;
import com.siemens.ct.exi.io.channel.DecoderChannel;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20080718
 */

public class ListDatatypeDecoder extends AbstractDatatypeDecoder
{
	public String decodeValue ( TypeDecoder decoder, Datatype datatype, DecoderChannel dc, String namespaceURI, String localName  ) throws IOException
	{
		DatatypeList list = (DatatypeList)datatype;
		
		return decode( decoder, dc, list, namespaceURI, localName  );
	}


	private String decode( TypeDecoder decoder, DecoderChannel dc, DatatypeList list, String namespaceURI, String localName  ) throws IOException
	{
		int len = dc.decodeUnsignedInteger ( );
		Datatype listDatatype = list.getListDatatype ( );
		DatatypeDecoder datatypeDecoder =  getDatatypeDecoder( listDatatype.getDefaultBuiltInType ( ) );
		
		//String sResult = "";
		StringBuffer sResult = new StringBuffer();
		for ( int i=0; i<len; i++) {
			//sResult += datatypeDecoder.decodeValue ( decoder, listDatatype, dc, localName );
			//sResult += Constants.XSD_LIST_DELIM;
			sResult.append ( datatypeDecoder.decodeValue ( decoder, listDatatype, dc, namespaceURI, localName  ) );
			sResult.append( Constants.XSD_LIST_DELIM );
		}
		
		//return sResult;
		return sResult.toString ( );
	}
	
	
	private static DatatypeDecoder getDatatypeDecoder ( BuiltInType builtInTypeList )
	{
		DatatypeDecoder datatypeDecoder;

		switch ( builtInTypeList )
		{
			case BUILTIN_BINARY:
				datatypeDecoder = new BinaryDatatypeDecoder ( );
				break;
			case BUILTIN_BOOLEAN:
				datatypeDecoder = new BooleanDatatypeDecoder ( );
				break;
			case BUILTIN_DECIMAL:
				datatypeDecoder = new DecimalDatatypeDecoder ( );
				break;
			case BUILTIN_FLOAT:
				datatypeDecoder = new FloatDatatypeDecoder ( );
				break;
			case BUILTIN_INTEGER:
				datatypeDecoder = new IntegerDatatypeDecoder ( );
				break;
			case BUILTIN_UNSIGNED_INTEGER:
				datatypeDecoder = new UnsignedIntegerDatatypeDecoder ( );
				break;
			case BUILTIN_DATETIME:
				datatypeDecoder = new DatetimeDatatypeDecoder ( );
				break;
			case BUILTIN_ENUMERATION:
				datatypeDecoder = new EnumerationDatatypeDecoder ( );
				break;
			case BUILTIN_STRING:
				datatypeDecoder = new StringDatatypeDecoder ( );
				break;
			default:
				throw new RuntimeException ( "Unknown BuiltIn Type" );
		}

		return datatypeDecoder;
	}
}