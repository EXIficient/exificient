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

import com.siemens.ct.exi.exceptions.XMLParsingException;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20081009
 */

public class XSDFloat
{
	public static final long	FLOAT_SPECIAL_VALUES	= -16384;	// -(2^14)
	public static final long	MANTISSA_INFINITY		= 1;
	public static final long	MANTISSA_MINUS_INFINITY	= -1;
	public static final long	MANTISSA_NOT_A_NUMBER	= 0;

	public long	iMantissa;
	public long	iExponent;
	
	private XSDFloat ( )
	{
	}
	
	public static XSDFloat newInstance()
	{
		return new XSDFloat();
	}

	public void parse ( String s ) throws XMLParsingException
	{
		// s = s.trim ( );

		if ( s.equals ( "INF" ) )
		{
			iMantissa = MANTISSA_INFINITY;
			iExponent = FLOAT_SPECIAL_VALUES;
		}
		else if ( s.equals ( "-INF" ) )
		{
			iMantissa = MANTISSA_MINUS_INFINITY;
			iExponent = FLOAT_SPECIAL_VALUES;
		}
		else if ( s.equals ( "NaN" ) )
		{
			iMantissa = MANTISSA_NOT_A_NUMBER;
			iExponent = FLOAT_SPECIAL_VALUES;
		}
		else
		{
			StringBuffer sb = new StringBuffer ( s );

			int additionalExponent = 0;
			int possibleIndexOfE = sb.indexOf ( "E" );
			if ( possibleIndexOfE < 0 )
			{
				possibleIndexOfE = sb.indexOf ( "e" );
			}

			if ( possibleIndexOfE >= 0 )
			{
				additionalExponent = Integer.parseInt ( sb.substring ( possibleIndexOfE + 1, sb
						.length ( ) ) );
				sb.delete ( possibleIndexOfE, sb.length ( ) );
			}

			int decimalPointLoc = sb.indexOf ( "." );
			if ( decimalPointLoc < 0 )
			{
				// eg. "12"
				// encode mantissa and exponent
				iMantissa = Long.parseLong ( sb.toString ( ) );
				iExponent = 0;
			}
			else
			{
				// remove trailing zeros (evtl. decimal point)
				int index = sb.length ( ) - 1;
				while ( sb.length ( ) > 1 && sb.charAt ( index ) == '0' )
				{ // stop latest at '.'
					sb.deleteCharAt ( index );
					index--;
				}

				// exponent
				int decimalDigits = sb.length ( ) - ( decimalPointLoc + 1 );
				iExponent = ( -1 ) * decimalDigits + additionalExponent;

				// mantissa
				String sBeforeDP = sb.substring ( 0, decimalPointLoc );
				String sAfterDP = sb.substring ( decimalPointLoc + 1, sb.length ( ) );
				iMantissa = Long.parseLong ( sBeforeDP + sAfterDP );
			}
		}
	}
}
