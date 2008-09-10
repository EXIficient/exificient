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

import com.siemens.ct.exi.Constants;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20080718
 */

public class StringTableEncoderImplNoGlobalValues extends StringTableEncoderImpl
{

	public StringTableEncoderImplNoGlobalValues ( boolean isSchemaInformed )
	{
		super ( isSchemaInformed );
	}

	/*
	 * Note:
	 * 
	 * EXI compression changes the order in which event codes and values are
	 * read and written to and from an EXI stream. Implementations must encode
	 * and decode values in this revised order so order sensitive constructs
	 * like the string table (see 7.3 String Table) work properly.
	 * 
	 * TODO currently global values switched off
	 */
	@Override
	public int getGlobalValueID ( String value )
	{
		return Constants.NOT_FOUND;
	}

}
