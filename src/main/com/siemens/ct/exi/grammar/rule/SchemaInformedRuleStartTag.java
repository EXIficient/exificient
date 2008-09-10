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

import com.siemens.ct.exi.Constants;
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

/*
 * 
 * <Schema-informed Element Grammar>
 * 
 * EE	n.m
 * 
 * AT(xsi:type) Element i, 0	n.m
 * AT(xsi:nil) Element i, 0	n.(m+1)
 * 
 * AT (*) Element i, j	n.m
 * AT (qname 0 ) [schema-invalid value] Element i, j	n.(m+1).0
 * AT (qname 1 ) [schema-invalid value] Element i, j	n.(m+1).1
 * ...
 * AT (qname x-1 ) [schema-invalid value] Element i, j	n.(m+1).(x-1)
 * AT (*) [schema-invalid value] Element i, j	n.(m+1).(x)
 * 
 * NS Element i, 0	n.m
 * 
 * SC Fragment	n.m
 * 
 * // ----- //
 * 
 * SE (*) Element i, content2	n.m
 * CH [schema-invalid value ] Element i, content2	n.(m+1)
 * ER Element i, content2	n.(m+2)
 * CM Element i, content2	n.(m+3).0
 * PI Element i, content2	n.(m+3).1
 * 
 */
public class SchemaInformedRuleStartTag extends AbstractSchemaInformedRuleContent
{
	SchemaInformedRule elementContent2;
	
	public SchemaInformedRuleStartTag( SchemaInformedRule elementContent2 )
	{
		super();
		this.elementContent2 = elementContent2;
	}

	static HashMap<FidelityOptions, ArrayList<EventType>>	optionsStartTag;

	static
	{
		optionsStartTag = new HashMap<FidelityOptions, ArrayList<EventType>> ( );
	}
	
	protected static ArrayList<EventType> get2ndLevelEventsStartTagItems ( FidelityOptions fidelityOptions )
	{
		if ( !optionsStartTag.containsKey ( fidelityOptions ) )
		{
			ArrayList<EventType> events = new ArrayList<EventType> ( );

			if ( !fidelityOptions.isStrict ( ) )
			{
				// extensibility: AT(*)
				events.add ( EventType.ATTRIBUTE_GENERIC_UNDECLARED );
				
				//	TODO AT[schema-invalid]
				events.add ( EventType.ATTRIBUTE );
			}

			optionsStartTag.put ( fidelityOptions, events );
		}

		return optionsStartTag.get ( fidelityOptions );
	}
	
	

	
	@Override
	public boolean hasSecondOrThirdLevel( FidelityOptions fidelityOptions  )
	{
		boolean hasSoT = false;
		
		if ( fidelityOptions.isStrict ( ) )
		{
			if ( isFirstElementRule )
			{
				hasSoT = hasNamedSubtypes || isNillable;
			}
		}
		else
		{
			hasSoT = true;
		}
		
		return hasSoT;
	}
	
	public int get2ndLevelCharacteristics( FidelityOptions fidelityOptions )
	{
		int ch2 = 0;
		
		if ( fidelityOptions.isStrict ( ) )
		{
			if ( isFirstElementRule )
			{
				//	xsi:type
				ch2 += hasNamedSubtypes ? 1 : 0;
				//	xsi:nil
				ch2 += isNillable ? 1 : 0;				
			}
		}
		else
		{
			//	add EE (if not already)
			if ( ! contains ( END_ELEMENT_EVENT ) )
			{
				ch2++; 
			}
			
			if ( isFirstElementRule )
			{
				//	xsi:type & xsi:nil
				ch2 += 2;
			}
			
			//	AT items:
			//	AT(*) & AT[schema-invalid]
			ch2 += 2;
			
			if ( isFirstElementRule )
			{
				// NS
				if ( fidelityOptions.isFidelityEnabled ( FidelityOptions.FEATURE_PREFIX ) )
				{
					ch2++;
				}
				// SC
				if ( fidelityOptions.isFidelityEnabled ( FidelityOptions.FEATURE_SC ) )
				{
					ch2++;
				}				
			}
			
			//	content items only
			ch2 += get2ndLevelElementItems ( fidelityOptions ).size( );
			
			//	3rd level ?
			if ( get3rdLevelCharacteristics ( fidelityOptions ) > 0 )
			{
				ch2++;
			}
		}
		
		return ch2;
		
		// return FidelityOptionsUtilities.get2ndLevelCharacteristicsStartTag( fidelityOptions );
	}
	

	public int get2ndLevelEventCode ( EventType eventType, FidelityOptions fidelityOptions )
	{
		int eventCode2 = Constants.NOT_FOUND;
		
		if ( fidelityOptions.isStrict ( ) )
		{
			if ( eventType == EventType.ATTRIBUTE_XSI_TYPE && hasNamedSubtypes )
			{
				eventCode2 = 0;
			}
			else if ( eventType == EventType.ATTRIBUTE_XSI_NIL && isNillable )
			{
				eventCode2 = hasNamedSubtypes ? 1 : 0;
			}
		}
		else
		{
			//	EE
			if ( eventType == EventType.END_ELEMENT_UNDECLARED )
			{
				eventCode2 = 0; 	
			}
			else
			{
				// no EE on first level --> on 2nd level
				int addToEC2 = contains ( END_ELEMENT_EVENT ) ? 0 : 1;
			
				//	xsi:type & xsi:nil
				if ( eventType == EventType.ATTRIBUTE_XSI_TYPE )
				{
					eventCode2 = 0;
				}
				else if ( eventType == EventType.ATTRIBUTE_XSI_NIL )
				{
					eventCode2 = 1;
				}
				else
				{
					//	firstElement ? xsi:type & xsi:nil
					addToEC2 += isFirstElementRule ? 2 : 0;
					
					//	AT(*)
					if ( eventType == EventType.ATTRIBUTE_GENERIC_UNDECLARED )
					{
						eventCode2 = 0;
					}
					//	AT[schema-invalid]
					else if ( eventType == EventType.ATTRIBUTE)
					{
						eventCode2 = 1;
					}
					else
					{
						//	AT(*) & AT[schema-invalid]
						addToEC2 += 2;
						
						//	NS
						if ( eventType == EventType.NAMESPACE_DECLARATION )
						{
							eventCode2 = 0;
						}
						//	SC
						else if ( eventType == EventType.SELF_CONTAINED )
						{
							eventCode2 =  fidelityOptions.isFidelityEnabled ( FidelityOptions.FEATURE_PREFIX ) ? 1 : 0;
						}
						else
						{
							if ( isFirstElementRule )
							{
								// NS & SC
								addToEC2 += fidelityOptions.isFidelityEnabled ( FidelityOptions.FEATURE_PREFIX ) ? 1 : 0;
								addToEC2 += fidelityOptions.isFidelityEnabled ( FidelityOptions.FEATURE_SC ) ? 1 : 0;				
							}

							//	content item ?
							eventCode2 = getEventCode( eventType, get2ndLevelElementItems ( fidelityOptions ) );
						}
					}
				}
				
				eventCode2 += ( eventCode2 == Constants.NOT_FOUND ) ? 0 : addToEC2;
			}
		}
		
		return eventCode2;
		
		//	return FidelityOptionsUtilities.getPositionAtSecondLevelStartTag( fidelityOptions, eventType );
	}
	
	public EventType get2ndLevelEvent ( int eventCode, FidelityOptions fidelityOptions )
	{
		EventType eventType = null;
		
		if ( fidelityOptions.isStrict ( ) )
		{
			if ( eventCode == 0 )
			{
				eventType = ( hasNamedSubtypes ? EventType.ATTRIBUTE_XSI_TYPE : EventType.ATTRIBUTE_XSI_NIL );
			}
			else if ( eventCode == 1 && isNillable )
			{
				eventType = EventType.ATTRIBUTE_XSI_NIL;
			}
		}
		else
		{
			//	no EE on first level --> on 2nd level
			boolean hasEEon2ndLevel = ! contains ( END_ELEMENT_EVENT );
			
			if ( hasEEon2ndLevel && eventCode == 0 )
			{
				eventType = EventType.END_ELEMENT_UNDECLARED; 	
			}
			else
			{
				// no EE on first level --> on 2nd level
				eventCode -= hasEEon2ndLevel ? 1 : 0;
			
				//	xsi:type & xsi:nil
				if ( isFirstElementRule && eventCode == 0 )
				{
					eventType = EventType.ATTRIBUTE_XSI_TYPE ;
				}
				else if ( isFirstElementRule && eventCode == 1 )
				{
					eventType = EventType.ATTRIBUTE_XSI_NIL;
				}
				else
				{
					eventCode -= isFirstElementRule ? 2 : 0;
					
					//	AT(*)
					if ( eventCode == 0 )
					{
						eventType = EventType.ATTRIBUTE_GENERIC_UNDECLARED;
					}
					//	AT[schema-invalid]
					else if ( eventCode == 1 )
					{
						eventType = EventType.ATTRIBUTE;
					}
					else
					{
						//	AT(*) & AT[schema-invalid]
						eventCode -= 2;
						
						//	NS
						//if ( isFirstElementRule && ( eventCode - minusNS_and_SC + 1 ) == 0 )
						if ( isFirstElementRule && eventCode== 0 )
						{
							eventType = EventType.NAMESPACE_DECLARATION;
						}
						//	SC
						//else if ( isFirstElementRule && ( eventCode - minusNS_and_SC + 1 ) == 1 )
						else if ( isFirstElementRule && eventCode  == 1 )
						
						{
							eventType = EventType.SELF_CONTAINED;
						}
						else
						{
							int minusNS_and_SC = 0;
							if ( isFirstElementRule )
							{
								minusNS_and_SC += fidelityOptions.isFidelityEnabled ( FidelityOptions.FEATURE_PREFIX ) ? 1 : 0;
								minusNS_and_SC += fidelityOptions.isFidelityEnabled ( FidelityOptions.FEATURE_SC ) ? 1 : 0;				
							}
							
							// NS & SC
							eventCode -= minusNS_and_SC;
							
							//	content item ?
							// childContent events
							eventType = get2ndLevelElementItems ( fidelityOptions ).get( eventCode );
						}
					}
				}
			}
		}
		
		return eventType;
		//	return FidelityOptionsUtilities.getEventTypeFor2ndLevelPositionStartTag( fidelityOptions, eventCode );
	}
	
	@Override
	public Rule getElementContentRule ( )
	{
		return elementContent2;
	}
	
	public Rule getElementContentRuleForUndeclaredSE()
	{
		return getElementContentRule( );
	}
	
	@Override
	public SchemaInformedRuleStartTag clone()
	{	
		SchemaInformedRuleStartTag clone = new SchemaInformedRuleStartTag( elementContent2 );		
		clone.joinRules( this );
		
		//	nillabale and type
		clone.setHasNamedSubtypes ( this.hasNamedSubtypes );
		clone.setNillable ( this.isNillable, typeEmpty );
		
		return clone;
	}
}
