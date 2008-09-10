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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.grammar.event.EventType;


/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20080718
 */

public abstract class AbstractSchemaInformedRuleContent extends AbstractSchemaInformedRule implements Cloneable
{
	protected static Map<FidelityOptions, List<EventType>>	optionsElement;
	
	
	public AbstractSchemaInformedRuleContent( )
	{
		super( );
	}
	
	static
	{
		optionsElement = new HashMap<FidelityOptions, List<EventType>> ( );
	}
	
	protected static List<EventType> get2ndLevelElementItems ( FidelityOptions fidelityOptions )
	{
		if ( !optionsElement.containsKey ( fidelityOptions ) )
		{
			List<EventType> events = new ArrayList<EventType> ( );

			if ( !fidelityOptions.isStrict ( ) )
			{
				// extensibility: SE(*), CH
				events.add ( EventType.START_ELEMENT_GENERIC_UNDECLARED );
				events.add ( EventType.CHARACTERS_GENERIC_UNDECLARED );

				// ER
				if ( fidelityOptions.isFidelityEnabled ( FidelityOptions.FEATURE_DTD ) )
				{
					events.add ( EventType.ENTITY_REFERENCE );
				}

			}

			optionsElement.put ( fidelityOptions, events );
		}

		return optionsElement.get ( fidelityOptions );
	}
	
	
	public int get1stLevelCharacteristics( FidelityOptions fidelityOptions )
	{
		return getNumberOfEvents ( ) + ( hasSecondOrThirdLevel( fidelityOptions ) ? 1 : 0 );
	}
	
	@Override
	public String toString()
	{
		String s = super.toString ( );
		
		if ( isFirstElementRule )
		{
			if ( this.hasNamedSubtypes )
			{
				s += "(xsi:type)";
			}
			
			if ( this.isNillable )
			{
				s += "(xsi:nil)";
			}			
		}
		
		return s;
	}
}
