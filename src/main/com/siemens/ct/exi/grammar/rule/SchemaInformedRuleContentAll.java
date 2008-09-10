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

package com.siemens.ct.exi.grammar.rule;

import com.siemens.ct.exi.grammar.event.EndElement;
import com.siemens.ct.exi.grammar.event.Event;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20080718
 */

public class SchemaInformedRuleContentAll extends SchemaInformedRuleElement
{
	static final Event END_ELEMENT = new EndElement ( );
	
	@Override
	public Rule get1stLevelRule ( int ec ) throws IndexOutOfBoundsException
	{
		return cloneWithoutGivenEventCode( ec );
	}
	
	protected SchemaInformedRuleContentAll cloneWithoutGivenEventCode( int ec )
	{
		Rule original = super.get1stLevelRule ( ec );
		
		SchemaInformedRuleContentAll clone = new SchemaInformedRuleContentAll();

		for ( int i=0; i<original.getNumberOfEvents ( ); i++)
		{
			if ( i != ec )
			{
				//clone.addRule ( original.getEventRuleAt ( i ).getEvent ( ), clone );
				clone.addRule ( original.get1stLevelEvent ( i ), clone );
			}
		}
		
		if ( clone.getNumberOfEvents ( ) == 0 )
		{
			//	add final EE event
			clone.addTerminalRule ( END_ELEMENT );
		}
		
		return clone;
	}
	
	public String toString( )
	{
		return "All" + super.toString ( );
	}

}
