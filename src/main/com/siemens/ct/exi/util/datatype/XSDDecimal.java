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
 * @version 0.1.20081014
 */

public class XSDDecimal
{
	private boolean		sign;
	private XSDInteger	integral		= XSDInteger.newInstance ( );
	private XSDInteger	revFractional	= XSDInteger.newInstance ( );

	private XSDDecimal ()
	{
	}

	public static XSDDecimal newInstance ()
	{
		return new XSDDecimal ( );
	}

	public boolean getSign ()
	{
		return sign;
	}

	public XSDInteger getIntegral ()
	{
		return integral;
	}

	public XSDInteger getReverseFractional ()
	{
		return revFractional;
	}

	public void parse ( String decimal ) throws XMLParsingException
	{
		try
		{
			// --- handle sign
			sign = false; // default

			if ( decimal.charAt ( 0 ) == '-' )
			{
				sign = true;
				decimal = decimal.substring ( 1 );
			}
			else if ( decimal.charAt ( 0 ) == '+' )
			{
				// sign = false;
				decimal = decimal.substring ( 1 );
			}
		}
		catch ( StringIndexOutOfBoundsException e )
		{
			throw new XMLParsingException ( e.getMessage ( ) );
		}

		// --- handle decimal point
		final int decPoint = decimal.indexOf ( '.' );

		if ( decPoint == -1 )
		{
			// no decimal point at all
			integral.parse ( decimal );
			revFractional.setToIntegerZero ( );
		}
		else
		{
			integral.parse ( decimal.substring ( 0, decPoint ) );
			revFractional.parse ( new StringBuffer ( decimal.substring ( decPoint + 1, decimal.length ( ) ) )
					.reverse ( ).toString ( ) );
		}

	}
}
