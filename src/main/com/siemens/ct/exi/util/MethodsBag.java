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

package com.siemens.ct.exi.util;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20080908
 */

public class MethodsBag
{
	/**
	 * Returns the least number of 7 bit-blocks that is needed to represent the
	 * int <param>n</param>. Returns 1 if <param>n</param> is 0.
	 * 
	 * @param n integer value
	 * 
	 */
	public static int numberOf7BitBlocksToRepresent ( final int n )
	{
		assert ( n >= 0 );

		// 7 bits
		if ( n < 128 )
		{
			return 1;
		}
		// 14 bits
		else if ( n < 16384 )
		{
			return 2;
		}
		// 21 bits
		else if ( n < 2097152 )
		{
			return 3;
		}
		// 28 bits
		else if ( n < 268435456 )
		{
			return 4;
		}
		// 35 bits
		else
		{
			// int, 32 bits
			return 5;
		}
	}

	/**
	 * Returns the least number of 7 bit-blocks that is needed to represent the
	 * long <param>l</param>. Returns 1 if <param>l</param> is 0.
	 * 
	 * @param l long value
	 * 
	 */
	public static int numberOf7BitBlocksToRepresent ( final long l )
	{
		if ( l < 0xffffffff )
		{
			return numberOf7BitBlocksToRepresent ( (int) l );
		}
		// 35 bits
		else if ( l < 0x800000000L )
		{
			return 5;
		}
		// 42 bits
		else if ( l < 0x40000000000L )
		{
			return 6;
		}
		// 49 bits
		else if ( l < 0x2000000000000L )
		{
			return 7;
		}
		// 56 bits
		else if ( l < 0x100000000000000L )
		{
			return 8;
		}
		// 63 bits
		else if ( l < 0x8000000000000000L )
		{
			return 9;
		}
		// 70 bits
		else
		{
			// long, 64 bits
			return 10;
		}
	}

	
	static final public int getCodingLength ( final int characteristics )
	{
		assert ( characteristics > 0 );

		if ( characteristics < 2 )
		{
			// 1
			return 0;
		}
		else if ( characteristics < 3 )
		{
			// 2
			return 1;
		}
		else if ( characteristics < 5 )
		{
			// 3 .. 4
			return 2;
		}
		else if ( characteristics < 9 )
		{
			// 5 .. 8
			return 3;
		}
		else if ( characteristics < 17 )
		{
			// 9 .. 16
			return 4;
		}
		else if ( characteristics < 33 )
		{
			// 17 .. 32
			return 5;
		}
		else if ( characteristics < 65 )
		{
			// 33 .. 64
			return 6;
		}
		else if ( characteristics < 129 )
		{
			// 65 .. 128
			return 7;
		}
		else
		{
			return (int) Math.ceil ( Math.log ( (double) ( characteristics ) ) / Math.log ( 2.0 ) );
		}
	}

}
