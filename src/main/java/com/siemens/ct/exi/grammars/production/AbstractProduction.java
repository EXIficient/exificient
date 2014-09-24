/*
 * Copyright (C) 2007-2014 Siemens AG
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

package com.siemens.ct.exi.grammars.production;

import java.io.Serializable;

import com.siemens.ct.exi.grammars.event.Event;
import com.siemens.ct.exi.grammars.grammar.Grammar;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.4-SNAPSHOT
 */

public abstract class AbstractProduction implements Production, Serializable {

	private static final long serialVersionUID = -6237642832111152869L;

	protected final Grammar next;
	protected final int eventCode;
	protected final Event event;

	public AbstractProduction(Grammar next, Event event, int eventCode) {
		this.next = next;
		this.event = event;
		this.eventCode = eventCode;
	}

	abstract public int getEventCode();
	
	public Event getEvent() {
		return event;
	}
	
	public Grammar getNextGrammar() {
		return next;
	}
	

	@Override
	public String toString() {
		return "[" + eventCode + "] " + event + " -> " + next;
	}
}