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

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20081031
 */

import java.util.List;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.grammar.event.EndElement;
import com.siemens.ct.exi.grammar.event.Event;
import com.siemens.ct.exi.grammar.event.EventType;

public abstract class AbstractRule implements Rule
{
	protected static final SchemaInformedRule	END_RULE	= new RuleDocEnd ( );
	
	protected static final EndElement END_ELEMENT_EVENT = new EndElement();
	
	private String label = null;
	
	public AbstractRule( )
	{
	}
	
	public AbstractRule( String label )
	{
		this();
		this.label = label;
	}
	
	
	public void addTerminalRule ( Event event  )
	{
		assert( event.isEventType ( EventType.END_ELEMENT ) || event.isEventType ( EventType.END_DOCUMENT ) );
		
		addRule ( event, END_RULE );
	}
	
	public boolean isTerminalRule()
	{
		return ( this == END_RULE );
	}
	
	
	/*
	 * Do NOT learn per default
	 * (non-Javadoc)
	 * @see com.siemens.exi.grammar.rule.Rule#learnStartElement(javax.xml.namespace.QName)
	 */
	public void learnStartElement( String uri, String localName ) 
	{
	}
	public void learnEndElement( ) 
	{
	}
	public void learnAttribute( String uri, String localName ) 
	{
	}
	public void learnCharacters( ) 
	{
	}

	
	public void setLabel(String label)
	{
		this.label = label;
	}
	
	public String getLabel()
	{
		if ( this.label != null && ! this.label.equals("") ) {
			return this.label;
		} else {
			//return this.getClass().getName() + "[" + this.hashCode() + "]";
			return this.getClass().getSimpleName() + "[" + this.hashCode() + "]";
		}
	}

	
	
	public int get3rdLevelCharacteristics( FidelityOptions fidelityOptions )
	{
		int ch3 = 0;
		
		if ( ! fidelityOptions.isStrict( ) )
		{
			// CM
			if ( fidelityOptions.isFidelityEnabled ( FidelityOptions.FEATURE_COMMENT ) )
			{
				ch3++;
			}
			// PI
			if ( fidelityOptions.isFidelityEnabled ( FidelityOptions.FEATURE_PI ) )
			{
				ch3++;
			}
		}
		
		return ch3;
	}
	
	public int get3rdLevelEventCode( EventType eventType, FidelityOptions fidelityOptions )
	{
		int ec3 = Constants.NOT_FOUND;
		
		if ( ! fidelityOptions.isStrict( ) )
		{
			// CM
			if ( fidelityOptions.isFidelityEnabled ( FidelityOptions.FEATURE_COMMENT ) )
			{
				if ( EventType.COMMENT == eventType)
				{
					ec3 = 0;
				}
				else if ( EventType.PROCESSING_INSTRUCTION == eventType)
				{
					ec3 = 1;
				}
			} else if ( fidelityOptions.isFidelityEnabled ( FidelityOptions.FEATURE_PI ) )
			{
				if ( EventType.PROCESSING_INSTRUCTION == eventType)
				{
					ec3 = 0;
				}
			}
		}
		
		return ec3;
	}
	
	public EventType get3rdLevelEvent( int eventCode, FidelityOptions fidelityOptions )
	{
		if ( eventCode == 0 )
		{
			if ( fidelityOptions.isFidelityEnabled ( FidelityOptions.FEATURE_COMMENT ) )
			{
				return EventType.COMMENT;
			}
			else
			{
				return EventType.PROCESSING_INSTRUCTION;
			}
		}
		else
		{
			return EventType.PROCESSING_INSTRUCTION;
		}
	}


	protected boolean hasSecondOrThirdLevel( FidelityOptions fidelityOptions  )
	{
		return ( ! fidelityOptions.isStrict ( ) );
	}
	

	protected static int getEventCode ( EventType eventType, List<EventType> events )
	{
		for ( int i = 0; i < events.size ( ); i++ )
		{
			if ( events.get ( i ).equals ( eventType ) )
			{
				return i;
			}
		}

		return Constants.NOT_FOUND;
	}
	
	public Rule getElementContentRule()
	{
		return this;
	}
	
	public Rule getElementContentRuleForUndeclaredSE()
	{
		return this;
	}

	
	@Override
	public boolean equals ( Object obj )
	{
		if ( this == obj )
		{
			return true;
		}
		else if ( obj instanceof Rule )
		{
			Rule r = (Rule)obj;
			
			int numberOfEvents = r.getNumberOfEvents ( );
			
			if ( this.getNumberOfEvents ( ) == numberOfEvents )
			{
				for ( int i=0; i<numberOfEvents; i++ )
				{
					//EventRule er = r.getEventRuleAt( i );
					Event ev = r.get1stLevelEvent ( i );
					
					//	shallow check
					//if ( ! er.equals ( this.getEventRuleAt ( i ) ) )
					if ( ! ev.equals ( this.get1stLevelEvent ( i )  ) )
					{
						return false;
					}
				}
				
				return true;
			}
			
			return false;

		}
		
		return false;
		
	}
	
	
	
}
