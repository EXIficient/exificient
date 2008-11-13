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

import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.datatype.RestrictedCharacterSet;
import com.siemens.ct.exi.exceptions.UnknownElementException;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.util.MethodsBag;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20080718
 */

public class RestrictedCharacterSetDatatypeDecoder extends AbstractDatatypeDecoder
{
	protected RestrictedCharacterSet rcs;
	
	public RestrictedCharacterSetDatatypeDecoder( RestrictedCharacterSet rcs )
	{
		super();
		
		this.rcs = rcs;
	}
	
	public String decodeValue ( TypeDecoder decoder, Datatype datatype, DecoderChannel dc, String namespaceURI, String localName  ) throws IOException
	{
		int numberOfTuples = dc.decodeUnsignedInteger ( );

		//	TODO calculate number of bits statically
		int numberOfBits =  MethodsBag.getCodingLength ( rcs.size ( ) );
		
		StringBuilder sb = new StringBuilder();
		
		try
		{
			for ( int i=0; i<numberOfTuples; i++ )
			{
				int code = dc.decodeNBitUnsignedInteger ( numberOfBits );
				sb.append ( rcs.getCharacter ( code ) );
			}
		}
		catch ( UnknownElementException e )
		{
			throw new IOException( e );
		}
		
		return sb.toString ( );
	}
}