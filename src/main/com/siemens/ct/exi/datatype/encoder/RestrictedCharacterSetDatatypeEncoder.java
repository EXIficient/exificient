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
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.util.MethodsBag;

/**
 * TODO Description
 * 
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20081112
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
		int i = 0;
		int code = 0;

		while ( code != Constants.NOT_FOUND && i < value.length ( ) )
		{
			char c = value.charAt ( i );
			code = rcs.getCode ( c );
			i++;
		}

		lastValidValue = value;
		return ( code == Constants.NOT_FOUND ? false : true );
	}

	public void writeValue ( EncoderChannel valueChannel, String uri, String localName ) throws IOException
	{
		//	TODO check whether storing the result or doing it once again is better
		//	Note: Currently we check validity again
		
		int numberOfTuples = lastValidValue.length ( );
		
		valueChannel.encodeUnsignedInteger ( numberOfTuples );
		
		//	TODO number of bits done statically
		int numberOfBits =  MethodsBag.getCodingLength ( rcs.size ( ) );
		
		for ( int i=0; i<numberOfTuples; i++ )
		{
			valueChannel.encodeNBitUnsignedInteger ( rcs.getCode ( lastValidValue.charAt ( i ) ), numberOfBits );	
		}
	}
}
