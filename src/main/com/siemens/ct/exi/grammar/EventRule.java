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

package com.siemens.ct.exi.grammar;

import com.siemens.ct.exi.grammar.event.Event;
import com.siemens.ct.exi.grammar.rule.Rule;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20080718
 */

public class EventRule implements Comparable<EventRule>
{
	private final Event	event;
	private final Rule	rule;

	public EventRule ( Event event, Rule rule )
	{
		assert ( event != null );
		assert ( rule != null );

		this.event = event;
		this.rule = rule;
	}

	public Event getEvent ()
	{
		return this.event;
	}

	public Rule getRule ()
	{
		return this.rule;
	}

	public boolean isEndRule ()
	{
		return this.rule.isTerminalRule ( );
	}

	public String toString ()
	{
		String s = "";

		s += event + " -> " + rule;

		return s;
	}

	@Override
	public boolean equals( Object obj )
	{
		if ( obj instanceof EventRule )
		{
			EventRule er = (EventRule)obj;
			return ( this.event.equals ( er.getEvent ( ) ) && this.rule.equals ( er.getRule ( ) ) );
		}
		
		return false;
	}
	
	public int compareTo ( EventRule o )
	{
		//	compare event only
		return ( getEvent ( ).compareTo ( o.getEvent ( ) ) );
	}
}
