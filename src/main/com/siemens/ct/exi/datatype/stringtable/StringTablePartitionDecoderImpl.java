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

package com.siemens.ct.exi.datatype.stringtable;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20080718
 */

public class StringTablePartitionDecoderImpl implements StringTablePartitionDecoder
{
	private static final boolean TEST_DECODER_PERFORMANCE = true;
	
	/**
	 * The contents of the table.
	 */
	protected Map<Integer, String>	hmValue;
	
	protected ValueList valueList;

	/**
	 * Create a new string table.
	 */
	public StringTablePartitionDecoderImpl ()
	{
		if ( TEST_DECODER_PERFORMANCE )
		{
			valueList= new ValueList();
		}
		else
		{
			hmValue = new HashMap<Integer, String> ( );
		}
	}

	/**
	 * Add a record to the table
	 * 
	 * @param value -
	 *            value to insert.
	 */
	public void add ( final String value )
	{
		if ( TEST_DECODER_PERFORMANCE )
		{
			valueList.add ( value );
		}
		else
		{
			//	TODO remove autoboxing
			hmValue.put ( hmValue.size ( ), value );
		}
	}

	/**
	 * Get the current size (number of strings) in the table.
	 */
	public int getSize ()
	{
		if ( TEST_DECODER_PERFORMANCE )
		{
			return valueList.size ( );
		}
		else
		{
			return hmValue.size ( );
		}
	}


	/**
	 * Get string at given index.
	 */
	public String getValue ( int index )
	{
		if ( TEST_DECODER_PERFORMANCE )
		{
			return valueList.getValue ( index );
		}
		else
		{
			return hmValue.get ( index );	
		}
	}

}
