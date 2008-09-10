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

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20080718
 */

public class ValueList
{
	public static final int DEFAULT_INITIAL_CAPACITY = 32;
	
	protected int count = 0;
	
	protected String[] values;
	
	public ValueList()
	{
		values = new String[ DEFAULT_INITIAL_CAPACITY ];
	}
	
	public int size()
	{
		return count;
	}
	
	public void add( final String value )
	{
		if ( count == values.length )
		{
			//	extend array
			extendArray();
		}
		
		values[ count++ ] = value;
	}

	protected void extendArray()
	{
		String[] newValues = new String[ count << 2 ];
		
		System.arraycopy ( values, 0, newValues, 0, count );
		
		values = newValues;
	}
	
	public String getValue( final int index )
	{
		return values[ index ];
	}

}
