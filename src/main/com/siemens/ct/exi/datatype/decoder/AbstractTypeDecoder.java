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

import com.siemens.ct.exi.datatype.AbstractTypeCoder;
import com.siemens.ct.exi.datatype.stringtable.StringTableDecoder;
import com.siemens.ct.exi.datatype.stringtable.StringTableDecoderImpl;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.io.channel.PreReadByteDecoderChannel;
import com.siemens.ct.exi.util.MethodsBag;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20080718
 */

public abstract class AbstractTypeDecoder extends AbstractTypeCoder implements TypeDecoder 
{
	//	EXI string table(s)
	protected StringTableDecoder stringTable;
	
	public StringTableDecoder getStringTable ()
	{
		return stringTable;
	}
	
	public AbstractTypeDecoder( boolean isSchemaInformed  )
	{
		stringTable = new StringTableDecoderImpl( isSchemaInformed );
	}
	
	public String decodeUri ( DecoderChannel dc ) throws IOException
	{
		String uri;
		// uri
		int mUri = stringTable.getURITableSize ( ); // number of entries
		int nUri = MethodsBag.getCodingLength ( mUri + 1 ); // n-bit
		int uriID = dc.decodeNBitUnsignedInteger ( nUri );
		if ( uriID == 0 )
		{
			// string value was not found
			// ==> zero (0) as an n-nit unsigned integer
			// followed by uri encoded as string
			uri = dc.decodeString ( );
			// after encoding string value is added to table
			stringTable.addURI ( uri );
		}
		else
		{
			// string value found
			// ==> value(i+1) is encoded as n-bit unsigned integer
			uri = stringTable.getURIValue ( uriID - 1 );
		}

		return uri;
	}

	public String decodePrefix ( DecoderChannel dc, String uri ) throws IOException
	{

		String prefix;

		int mPfx = stringTable.getPrefixTableSize ( uri ); // number of entries
		int nPfx = MethodsBag.getCodingLength ( mPfx + 1 ); // n-bit
		int pfxID = dc.decodeNBitUnsignedInteger ( nPfx );
		if ( pfxID == 0 )
		{
			// string value was not found
			// ==> zero (0) as an n-nit unsigned integer
			// followed by pfx encoded as string
			prefix = dc.decodeString ( );
			// after decoding pfx value is added to table
			stringTable.addPrefix ( uri, prefix );
		}
		else
		{
			// string value found
			// ==> value(i+1) is encoded as n-bit unsigned integer
			prefix = stringTable.getPrefixValue ( uri, pfxID - 1 );
		}

		return prefix;
	}

	public String decodeLocalName ( DecoderChannel dc, String uri ) throws IOException
	{

		String localName;
		int length = dc.decodeUnsignedInteger ( );

		if ( length > 0 )
		{
			// string value was not found in local partition
			// ==> string literal is encoded as a String
			// with the length of the string incremented by one
			length--;
			localName = dc.decodeStringOnly ( length );
			// After encoding the string value, it is added to the string table
			// partition and assigned the next available compact identifier.
			stringTable.addLocalName ( uri, localName );
		}
		else
		{
			// string value found in local partition
			// ==> string value is represented as zero (0) encoded as an
			// Unsigned Integer
			// followed by an the compact identifier of the string value as an
			// n-bit unsigned integer
			// n is log2 m and m is the number of entries in the string table
			// partition
			int n = MethodsBag.getCodingLength ( stringTable.getLocalNameTableSize ( uri ) );
			int localNameID = dc.decodeNBitUnsignedInteger ( n );
			localName = stringTable.getLocalNameValue ( uri, localNameID );
		}

		return localName;
	}

	public String decodeValueAsString ( DecoderChannel dc, final String namespaceURI, final String localName )
			throws IOException
	{
		String value;

		//	TODO modify (better way!??!)
		if ( dc instanceof PreReadByteDecoderChannel )
		{
			//	values already decoded (just fetch them again!)
			value = ((PreReadByteDecoderChannel)dc).getNextValue ( );
		}
		else
		{
			int i = dc.decodeUnsignedInteger ( );
			
			if ( i == 0 )
			{
				// found in local value partition
				// ==> string value is represented as zero (0) encoded as an
				// Unsigned Integer
				// followed by the compact identifier of the string value in the
				// "local" value partition		
				int n = MethodsBag.getCodingLength ( stringTable
						.getLocalValueTableSize ( namespaceURI, localName ) );
				int localID = dc.decodeNBitUnsignedInteger ( n );
				
				value = stringTable.getLocalValue ( namespaceURI, localName, localID );
			}
			else if ( i == 1 )
			{
				// found in global value partition
				// TODO text in format spec is NOT clear!!
				// ==> When a string value is not found in the global value
				// partition,
				// but not in the "local" value partition, the String value is
				// represented as one (1) encoded as an Unsigned Integer
				// followed by the compact identifier of the String value in the
				// global value partition.
				int globalID;
				int n = MethodsBag.getCodingLength ( stringTable.getGlobalValueTableSize ( ) );
				globalID = dc.decodeNBitUnsignedInteger ( n );
				
				value = stringTable.getGlobalValue ( globalID );
			}
			else
			{
				// not found in global value (and local value) partition
				// ==> string literal is encoded as a String with the length
				// incremented by two.
				value = dc.decodeStringOnly ( i - 2 );
				// After encoding the string value, it is added to both the
				// associated
				// "local" value string table partition and the global value string
				// table partition.
				stringTable.addLocalValue ( namespaceURI, localName, value );
				stringTable.addGlobalValue ( value );
			}			
		}

		assert ( value != null );

		return value;
	}

}
