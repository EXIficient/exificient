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
 * @version 0.1.20081104
 */

public abstract class SchemaLessRuleContent extends AbstractSchemaLessRule
{
	protected static Map<FidelityOptions, List<EventType>>	optionsStartTag;
	protected static Map<FidelityOptions, List<EventType>>	optionsChildContent;

	static
	{
		optionsStartTag = new HashMap<FidelityOptions, List<EventType>> ( );
		optionsChildContent = new HashMap<FidelityOptions, List<EventType>> ( );
	}

	public int get1stLevelCharacteristics ( FidelityOptions fidelityOptions )
	{
		return getNumberOfEvents ( ) + ( hasSecondOrThirdLevel ( fidelityOptions ) ? 1 : 0 );
	}

	protected static List<EventType> get2ndLevelEventsStartTagItems ( FidelityOptions fidelityOptions )
	{
		if ( !optionsStartTag.containsKey ( fidelityOptions ) )
		{
			List<EventType> events = new ArrayList<EventType> ( );

			if ( !fidelityOptions.isStrict ( ) )
			{
				// extensibility: EE, AT(*)
				events.add ( EventType.END_ELEMENT_UNDECLARED );
				events.add ( EventType.ATTRIBUTE_GENERIC_UNDECLARED );

				// NS
				if ( fidelityOptions.isFidelityEnabled ( FidelityOptions.FEATURE_PREFIX ) )
				{
					events.add ( EventType.NAMESPACE_DECLARATION );
				}
				// SC
				if ( fidelityOptions.isFidelityEnabled ( FidelityOptions.FEATURE_SC ) )
				{
					events.add ( EventType.SELF_CONTAINED );
				}
			}

			optionsStartTag.put ( fidelityOptions, events );
		}

		return optionsStartTag.get ( fidelityOptions );
	}

	protected static List<EventType> get2ndLevelEventsChildContentItems ( FidelityOptions fidelityOptions )
	{
		if ( !optionsChildContent.containsKey ( fidelityOptions ) )
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

			optionsChildContent.put ( fidelityOptions, events );
		}

		return optionsChildContent.get ( fidelityOptions );
	}

}
