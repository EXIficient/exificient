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

package com.siemens.ct.exi.io.channel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20080718
 */

final public class ByteEncoderChannelChannelized extends AbstractEncoderChannel implements EncoderChannelChannelized
{
	private final ByteArrayOutputStream baos;
	protected final ByteEncoderChannel bec;
	
    /**
     * Construct a byte aligned encoder from output stream.
     */
    public ByteEncoderChannelChannelized( )
    {
    	baos = new ByteArrayOutputStream();
    	bec = new ByteEncoderChannel( baos );
    }
    
    public void flush() throws IOException {
        bec.flush();
    }
    
    public void encode( int b ) throws IOException
    {
    	bec.encode ( b );
    }
    
    public void encode ( byte b[], int off, int len ) throws IOException 
    {
    	bec.encode ( b, off, len );
    }
    
    /**
     * Encode a single boolean value. A false value is encoded as byte 0 and
     * true value is encode as byte 1.
     */
    public void encodeBoolean( boolean b ) throws IOException, IllegalArgumentException
    {
    	bec.encodeBoolean( b );
    }
    
    /**
     * Encode n-bit unsigned integer using the minimum number of bytes required
     * to store n bits. The n least significant bits of parameter b starting
     * with the most significant, i.e. from left to right.
     */
    public void encodeNBitUnsignedInteger( int b, int n ) throws IOException
    {
    	bec.encodeNBitUnsignedInteger( b, n );
    }

    
    public byte[] toByteArray( ) throws IOException 
    {
    	flush();
    	return baos.toByteArray();
    }

	public OutputStream getOutputStream ()
	{
		return baos;
	}



    

}
