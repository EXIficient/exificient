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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.grammar.EventRule;
import com.siemens.ct.exi.grammar.event.Event;
import com.siemens.ct.exi.grammar.event.EventType;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20080924
 */

public abstract class AbstractSchemaInformedRule extends AbstractRule implements SchemaInformedRule
{
	protected Map<Event, Integer>		hmEventCodes;
	protected Map<Integer, Event>		hmEvents;
	protected Map<Integer, Rule>		hmRules;

	protected boolean					lambdasResolved		= false;

	protected boolean					hasNamedSubtypes	= false;

	protected boolean					isNillable			= false;
	protected SchemaInformedRule		typeEmpty;

	protected boolean					isFirstElementRule	= false;

	protected List<SchemaInformedRule>	lambdaTransitions;

	public AbstractSchemaInformedRule ()
	{
		super ( );
		init ( );
	}

	public AbstractSchemaInformedRule ( String label )
	{
		super ( label );
		init ( );
	}

	private void init ()
	{
		hmEventCodes = new HashMap<Event, Integer> ( );
		hmEvents = new HashMap<Integer, Event> ( );
		hmRules = new HashMap<Integer, Rule> ( );

		lambdaTransitions = new ArrayList<SchemaInformedRule> ( );
	}
	
	public final boolean isSchemaRule ()
	{
		return true;
	}

	public int get1stLevelEventCode ( Event event, FidelityOptions fidelityOptions )
	{
		Integer i;
		return ( ( i = hmEventCodes.get ( event ) ) == null ? Constants.NOT_FOUND : i );
	}

	public Event get1stLevelEvent ( int eventCode )
	{
		return hmEvents.get ( eventCode );
	}

	public void setHasNamedSubtypes ( boolean hasNamedSubtypes )
	{
		this.hasNamedSubtypes = hasNamedSubtypes;
	}

	public void setNillable ( boolean nil, SchemaInformedRule typeEmpty )
	{
		this.isNillable = nil;
		this.typeEmpty = typeEmpty;
	}

	public SchemaInformedRule getTypeEmpty ()
	{
		return this.typeEmpty;
	}

	public void setFirstElementRule ()
	{
		isFirstElementRule = true;
	}

	public List<SchemaInformedRule> getLambdaRules ()
	{
		return this.lambdaTransitions;
	}

	public boolean isLambdaResolved ()
	{
		return this.lambdasResolved;
	}

	public void resolveLambdaTransitions ( ArrayList<EventRule> reachableEventRules, ArrayList<Rule> alreadyHandledRules )
	{
		// abort
		if ( alreadyHandledRules.contains ( this ) )
		{
			return;
		}
		alreadyHandledRules.add ( this );

		// add all direct event-rules
		for ( int i = 0; i < getNumberOfEvents ( ); i++ )
		{
			reachableEventRules.add ( getEventRuleAt ( i ) );
		}

		// fetch all indirect rules via lambda
		if ( !lambdaTransitions.isEmpty ( ) )
		{
			Iterator<SchemaInformedRule> iterLambdas = lambdaTransitions.iterator ( );

			while ( iterLambdas.hasNext ( ) )
			{
				SchemaInformedRule lambdaRule = iterLambdas.next ( );

				lambdaRule.resolveLambdaTransitions ( reachableEventRules, alreadyHandledRules );
			}
		}

		// update entries?
		// only first item is of interest
		if ( alreadyHandledRules.get ( 0 ) == this )
		{
			//	add if not already present
			for( int i=0; i < reachableEventRules.size ( ); i++ )
			{
				EventRule er = reachableEventRules.get ( i );
				if ( ! contains ( er.getEvent ( ) ) )
				{
					updateSortedEventRules ( er.getEvent ( ), er.getRule ( ) );
				}
			}

			// clear lambda transitions
			lambdaTransitions.clear ( );
			lambdasResolved = true;

			// resolve all other targets (if not already)
			// go over all target rules and resolve lambdas
			for ( int i = 0; i < getNumberOfEvents ( ); i++ )
			{
				SchemaInformedRule sir = this.getRuleAt ( i );

				if ( !sir.isLambdaResolved ( ) )
				{
					sir.resolveLambdaTransitions ( new ArrayList<EventRule> ( ), new ArrayList<Rule> ( ) );
				}
			}
		}
	}

	public int getNumberOfEvents ()
	{
		return this.hmEvents.size ( );
	}

	protected Event getEventAt ( int ec ) throws IndexOutOfBoundsException
	{
		return hmEvents.get ( ec );
	}

	protected SchemaInformedRule getRuleAt ( int ec ) throws IndexOutOfBoundsException
	{
		return (SchemaInformedRule) hmRules.get ( ec );
	}

	protected EventRule getEventRuleAt ( int ec ) throws IndexOutOfBoundsException
	{
		return new EventRule ( getEventAt ( ec ), getRuleAt ( ec ) );
	}

	public Rule get1stLevelRule ( int ec ) throws IndexOutOfBoundsException
	{
		return this.hmRules.get ( ec );
	}

	protected boolean contains ( Event event )
	{
		return ( getInternalEventId ( event ) != Constants.NOT_FOUND );
	}

	protected int getInternalEventId ( Event event )
	{
		for ( int id = 0; id < getNumberOfEvents ( ); id++ )
		{
			if ( getEventRuleAt ( id ).getEvent ( ).equals ( event ) )
			{
				return id;
			}
		}

		return Constants.NOT_FOUND;
	}

	public void addRule ( Event event, Rule rule )
	{
		if ( isTerminalRule ( ) )
		{
			// *end* in our context should really mean end ;-)
			throw new IllegalArgumentException ( "EndRule can not have events attached" );
		}

		if ( event.isEventType ( EventType.LAMBDA ) )
		{
			// collect all lambda transitions
			assert ( rule instanceof SchemaInformedRule );

			lambdasResolved = false;

			lambdaTransitions.add ( (SchemaInformedRule) rule );
		}
		else
		{
			// undecidable choice not allowed
			if ( contains ( event ) )
			{
				checkUndecidableEvent( event );
			}
			else
			{
				// construct new event array and update event-codes
				updateSortedEventRules ( event, rule );
			}
		}
	}

	protected void updateSortedEventRules ( Event newEvent, Rule newRule )
	{
		Set<EventRule> sorted = new TreeSet<EventRule>();

		// *old* events
		for ( int i = 0; i < getNumberOfEvents ( ); i++ )
		{
			EventRule curr = getEventRuleAt ( i );
			sorted.add ( curr );
		}
		
		//  *new* event
		sorted.add ( new EventRule ( newEvent, newRule ) );

		// reset all maps
		hmEventCodes.clear ( );
		hmEvents.clear ( );
		hmRules.clear ( );

		//	set new event-code etc.
		int ec = 0;
		for (Iterator<EventRule> i = sorted.iterator(); i.hasNext(); )
		{
			EventRule er = i.next();

			hmEventCodes.put ( er.getEvent ( ), ec );
			hmEvents.put ( ec, er.getEvent ( ) );
			hmRules.put ( ec, er.getRule ( ) );

			ec++;
		}
	}
	
	private void checkUndecidableEvent( Event event )
	{
		int id = getInternalEventId ( event );
		EventRule er = this.getEventRuleAt ( id );

		//	NOT same event ? -> throw error
		if ( ! event.equals ( er.getEvent ( ) ) )
		{
			throw new IllegalArgumentException ( "Illegal Duplicate Event: " + event + " for " + this );
		}		
	}

	public void joinRules ( Rule rule )
	{
		// add *new* events-rules
		for ( int i = 0; i < rule.getNumberOfEvents ( ); i++ )
		{
			Event event = rule.get1stLevelEvent ( i );
			Rule r = rule.get1stLevelRule ( i );

			if ( contains ( event ) )
			{
				checkUndecidableEvent( event );
			}
			else
			{
				updateSortedEventRules ( event, r );
			}
		}

		// lambda transitions
		if ( rule instanceof AbstractSchemaInformedRule )
		{
			// TODO find something better
			AbstractSchemaInformedRule siRule = (AbstractSchemaInformedRule) rule;
			if ( siRule.lambdaTransitions != null && siRule.lambdaTransitions.size ( ) > 0 )
			{
				for ( int i = 0; i < siRule.lambdaTransitions.size ( ); i++ )
				{
					this.lambdaTransitions.add ( siRule.lambdaTransitions.get ( i ) );
				}
			}
		}

	}

	@Override
	public String toString ()
	{
		StringBuffer sb = new StringBuffer ( );
		sb.append ( "SchemaInformedRule" );
		sb.append ( '[' );

		for ( int i = 0; i < getNumberOfEvents ( ); i++ )
		{
			sb.append ( this.hmEvents.get ( i ).toString ( ) );
			if ( i < ( getNumberOfEvents ( ) - 1 ) )
			{
				sb.append ( ", " );
			}
		}

		sb.append ( ']' );

		return sb.toString ( );
	}

	@Override
	public SchemaInformedRule clone ()
	{
		return this;
	}

}
