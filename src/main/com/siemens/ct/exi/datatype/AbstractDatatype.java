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

package com.siemens.ct.exi.datatype;

import com.siemens.ct.exi.util.ExpandedName;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20080718
 */

public abstract class AbstractDatatype implements Datatype
{
	// default built-in datatype (no codec map used)
	protected BuiltInType	defaultbuiltInType;
	//	for codec map (pluggable codecs)
	protected ExpandedName datatypeIdentifier;

	public AbstractDatatype ( BuiltInType builtInType, ExpandedName datatypeIdentifier )
	{
		this.defaultbuiltInType = builtInType;
		this.datatypeIdentifier = datatypeIdentifier;
	}

	public BuiltInType getDefaultBuiltInType ()
	{
		return defaultbuiltInType;
	}

	public boolean isDefaultBuiltInType ( BuiltInType type )
	{
		return ( defaultbuiltInType == type );
	}
	
	public ExpandedName getDatatypeIdentifier() 
	{
		return datatypeIdentifier;
	}
	
	public boolean equals( Object o )
	{
		if ( o instanceof Datatype )
		{
			return ( this.getDefaultBuiltInType ( ) == ((Datatype)o).getDefaultBuiltInType ( ) );
		}
		else
		{
			return false;
		}
	}
}
