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

package com.siemens.ct.exi.grammar.event;

import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.util.ExpandedName;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20080718
 */

public abstract class AbstractDatatypeEvent extends AbstractEvent implements DatatypeEvent
{
	//private QName valueType;
	private final ExpandedName valueType;
	
	private final Datatype datatype;

	public AbstractDatatypeEvent( String grammarNotation, ExpandedName valueType, Datatype datatype )
	{
		super( grammarNotation );
		this.valueType = valueType;
		this.datatype = datatype;
	}
	
	public ExpandedName getValueType()
	{
		return valueType;
	}

	public Datatype getDatatype()
	{
		return datatype;
	}

}
