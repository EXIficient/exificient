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

package com.siemens.ct.exi.util.datatype;

import java.math.BigInteger;

import com.siemens.ct.exi.exceptions.XMLParsingException;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20080718
 */

public class XSDInteger
{	
	private int			iInt;
	private long		lInt;
	private BigInteger	bInt;

	private IntegerType	type;

	private XSDInteger ( )
	{
	}
	
	public static XSDInteger newInstance ()
	{
		return new XSDInteger ( );
	}
	
	public void setToIntegerZero( )
	{
		iInt = 0;
		type = IntegerType.INT_INTEGER;
	}
	
	
	public void parse ( String s ) throws XMLParsingException
	{
		// TODO trim integer string if necessary!
		//s.trim ( );

		// integer value ?
		try
		{
			iInt = Integer.parseInt ( s );
			type = IntegerType.INT_INTEGER;
		}
		catch ( NumberFormatException e )
		{
			// long value ?
			try
			{
				lInt = Long.parseLong ( s );
				type = IntegerType.LONG_INTEGER;
			}
			catch ( NumberFormatException el )
			{
				// BigInteger ?
				try
				{
					bInt = new BigInteger ( s );
					type = IntegerType.BIG_INTEGER;
				}
				catch ( NumberFormatException eb )
				{
					// OK, seems to be a deviation
					throw new XMLParsingException( "'" + s + "' cannot be parsed as Integer");
				}
			}	
		}
	}
	
	public boolean isNegative()
	{
		switch( getIntegerType ( ) )
		{
			case INT_INTEGER:
				return( iInt < 0 );
			case LONG_INTEGER:
				return( lInt < 0 );
			case BIG_INTEGER:
				return( bInt.signum ( ) < 0 );
			default:
				throw new RuntimeException();
		}
	}

	public int getIntInteger ()
	{
		return iInt;
	}

	public long getLongInteger ()
	{
		return lInt;
	}

	public BigInteger getBigInteger ()
	{
		return bInt;
	}

	public IntegerType getIntegerType ()
	{
		return type;
	}
}
