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

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.datatype.RestrictedCharacterSet;
import com.siemens.ct.exi.datatype.stringtable.StringTableEncoder;
import com.siemens.ct.exi.io.channel.EncoderChannel;

/**
 * TODO Description
 * 
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20081117
 */

public class RestrictedCharacterSetDatatypeEncoder extends AbstractDatatypeEncoder implements DatatypeEncoder
{
	protected RestrictedCharacterSet	rcs;
	protected String					lastValidValue;

	public RestrictedCharacterSetDatatypeEncoder ( TypeEncoder typeEncoder, RestrictedCharacterSet rcs )
	{
		super ( typeEncoder );

		this.rcs = rcs;
	}

	public boolean isValid ( Datatype datatype, String value )
	{
		// Note: no validity check needed since any char-sequence can be encoded
		// due to fallback mechanism
		lastValidValue = value;
		return true;
	}

	public void writeValue ( EncoderChannel valueChannel, String uri, String localName ) throws IOException
	{
		if ( !typeEncoder.writeStringAsLocalHit ( valueChannel, uri, localName, lastValidValue ) )
		{
			// global-value hit ?
			if ( !typeEncoder.writeStringAsGlobalHit ( valueChannel, lastValidValue ) )
			{
				// mhh, it is a string-table miss ==> restricted character
				// string literal is encoded as a String with the length
				// incremented by two.
				int numberOfTuples = lastValidValue.length ( );

				valueChannel.encodeUnsignedInteger ( numberOfTuples + 2 );

				// number of bits
				int numberOfBits = rcs.getCodingLength ( );

				for ( int i = 0; i < numberOfTuples; i++ )
				{
					int code;
					char ch = lastValidValue.charAt ( i );
					if ( ( code = rcs.getCode ( ch ) ) == Constants.NOT_FOUND )
					{
						// indicate deviation
						valueChannel.encodeNBitUnsignedInteger ( rcs.size ( ), numberOfBits );

						// UCS code point of the character
						// TODO UTF-16 surrogate pair?
						valueChannel.encodeUnsignedInteger ( ch );
					}
					else
					{
						valueChannel.encodeNBitUnsignedInteger ( code, numberOfBits );
					}
				}

				// After encoding the string value, it is added to both the
				// associated "local" value string table partition and the
				// global value string table partition.
				StringTableEncoder stringTable = typeEncoder.getStringTable ( );
				stringTable.addLocalValue ( uri, localName, lastValidValue );
				stringTable.addGlobalValue ( lastValidValue );
			}
		}
	}
}
