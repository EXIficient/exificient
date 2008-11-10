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

package com.siemens.ct.exi.datatype.encoder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.datatype.BuiltInType;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.datatype.DatatypeList;
import com.siemens.ct.exi.io.channel.EncoderChannel;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20081110
 */

public class ListDatatypeEncoder extends AbstractDatatypeEncoder implements DatatypeEncoder
{
	HashMap<Integer, DatatypeEncoder>	hmEncoders				= new HashMap<Integer, DatatypeEncoder> ( );
	HashMap<Integer, String>			hmToken					= new HashMap<Integer, String> ( );				;

	private ArrayList<DatatypeEncoder>	lastDatatypeEncoders	= new ArrayList<DatatypeEncoder> ( );
	private ArrayList<String>			lastTokens				= new ArrayList<String> ( );
	private BuiltInType					lastBuiltInTypeList;
	private Datatype					datatypeList;

	private boolean						lastValidTokens;

	
	public ListDatatypeEncoder( TypeEncoder typeEncoder )
	{
		super( typeEncoder );
	}
	
	
	public boolean isValid ( Datatype datatype, String value )
	{
		try
		{
			datatypeList = ( (DatatypeList) datatype ).getListDatatype ( );
			lastBuiltInTypeList = datatypeList.getDefaultBuiltInType ( );

			// setup new array lists for encoders and tokens
			lastDatatypeEncoders.clear ( );
			lastTokens.clear ( );

			StringTokenizer st = new StringTokenizer ( value, Constants.XSD_LIST_DELIM );
//			if ( CompileConfiguration.LOGGING_ON )
//			{
//				System.out.println ( "TokenSize: " + st.countTokens ( ) );
//			}
			lastValidTokens = true;

			while ( lastValidTokens && st.hasMoreTokens ( ) )
			{
				String token = st.nextToken ( );

				lastTokens.add ( token );
//				if ( CompileConfiguration.LOGGING_ON )
//				{
//					System.out.println ( "Token: " + token );
//				}

				DatatypeEncoder de = getDatatypeEncoder ( lastBuiltInTypeList );
				lastDatatypeEncoders.add ( de );

				lastValidTokens = de.isValid ( datatypeList, token );

			}

			return lastValidTokens;
		}
		catch ( RuntimeException e )
		{
			return false;
		}
	}

//	public boolean isValid ( Datatype datatype, char[] ch, int start, int length )
//	{
//		return isValid ( datatype, new String ( ch, start, length ) );
//	}

	public void writeValue ( EncoderChannel valueChannel, String uri, String localName ) throws IOException
	//public void writeValue ( EncoderChannel valueChannel ) throws IOException
	{
		assert ( lastDatatypeEncoders.size ( ) == lastTokens.size ( ) );

		// length prefixed sequence of values
		valueChannel.encodeUnsignedInteger ( lastDatatypeEncoders.size ( ) );

		for ( int i = 0; i < lastDatatypeEncoders.size ( ); i++ )
		{
			lastDatatypeEncoders.get ( i ).writeValue ( valueChannel, uri, localName );
			//lastDatatypeEncoders.get ( i ).writeValue ( valueChannel );
		}

	}

	private DatatypeEncoder getDatatypeEncoder ( BuiltInType builtInTypeList )
	{
		DatatypeEncoder datatypeEncoder;

		switch ( builtInTypeList )
		{
			case BUILTIN_BINARY:
				datatypeEncoder = new BinaryDatatypeEncoder ( typeEncoder );
				break;
			case BUILTIN_BOOLEAN:
				datatypeEncoder = new BooleanDatatypeEncoder ( typeEncoder );
				break;
			case BUILTIN_BOOLEAN_PATTERN:
				datatypeEncoder = new BooleanPatternDatatypeEncoder ( typeEncoder );
				break;
			case BUILTIN_DECIMAL:
				datatypeEncoder = new DecimalDatatypeEncoder ( typeEncoder );
				break;
			case BUILTIN_FLOAT:
				datatypeEncoder = new FloatDatatypeEncoder ( typeEncoder );
				break;
			case BUILTIN_INTEGER:
				datatypeEncoder = new IntegerDatatypeEncoder ( typeEncoder );
				break;
			case BUILTIN_UNSIGNED_INTEGER:
				datatypeEncoder = new UnsignedIntegerDatatypeEncoder ( typeEncoder );
				break;
			case BUILTIN_DATETIME:
				datatypeEncoder = new DatetimeDatatypeEncoder ( typeEncoder );
				break;
			case BUILTIN_ENUMERATION:
				datatypeEncoder = new EnumerationDatatypeEncoder ( typeEncoder );
				break;
			case BUILTIN_STRING:
				datatypeEncoder = new StringDatatypeEncoder ( typeEncoder );
				break;
			default:
				throw new RuntimeException ( "Unknown BuiltIn Type" );
		}

		return datatypeEncoder;
	}

}
