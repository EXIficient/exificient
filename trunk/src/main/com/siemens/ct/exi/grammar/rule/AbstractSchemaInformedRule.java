/*
 * Copyright (C) 2007-2009 Siemens AG
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

import java.util.Set;
import java.util.TreeSet;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.grammar.EventInformation;
import com.siemens.ct.exi.grammar.SchemaInformedEventInformation;
import com.siemens.ct.exi.grammar.event.Event;
import com.siemens.ct.exi.grammar.event.EventType;
import com.siemens.ct.exi.util.MethodsBag;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20081009
 */

public abstract class AbstractSchemaInformedRule extends AbstractRule implements
		SchemaInformedRule {
	
	//	contains all necessary event information including event-codes
	EventInformation[] containers = new EventInformation[0];

	//	event code lengths
	protected int codeLengthA;	//	1st level only
	protected int codeLengthB;	//	2nd OR 3rd level

	//	subtype (xsi:type) OR nillable (xsi:nill) ?
	protected boolean isTypeCastable = false;
	protected boolean isNillable = false;
	//	EE present
	protected boolean hasEndElement = false;

	protected SchemaInformedRule typeEmpty;

	/*
	 * schema-deviated attributes
	 */
	protected int leastAttributeEventCode = Constants.NOT_FOUND;
	protected int numberDeviatedAttributes = 0;

	// protected List<SchemaInformedRule> lambdaTransitions;

	public AbstractSchemaInformedRule() {
		super();
		init();
	}

	public AbstractSchemaInformedRule(String label) {
		super(label);
		init();
	}

	private void init() {
		containers = new EventInformation[0];
	}

	public final boolean isSchemaRule() {
		return true;
	}

	public boolean hasSecondOrThirdLevel(FidelityOptions fidelityOptions) {
		return (!fidelityOptions.isStrict());
	}

	public int get1stLevelEventCodeLength(FidelityOptions fidelityOptions) {
		return (hasSecondOrThirdLevel(fidelityOptions) ? codeLengthB
				: codeLengthA);
	}

	public void setTypeCastable(boolean isTypeCastable) {
		this.isTypeCastable = isTypeCastable;
	}

	public void setNillable(boolean nil, SchemaInformedRule typeEmpty) {
		this.isNillable = nil;
		this.typeEmpty = typeEmpty;
	}

	public Rule getTypeEmpty() {
		return this.typeEmpty;
	}

	public int getNumberOfSchemaDeviatedAttributes() {
		return numberDeviatedAttributes;
	}

	public int getLeastAttributeEventCode() {
		return leastAttributeEventCode;
	}

	public int getNumberOfEvents() {
		return containers.length;
	}

	public void addRule(Event event, Rule rule) {
		if (isTerminalRule()) {
			// *end* in our context should really mean end ;-)
			throw new IllegalArgumentException(
					"EndRule can not have events attached");
		}
		
		if (event.isEventType(EventType.END_ELEMENT)) {
			hasEndElement = true;
		}

		// undecidable choice not allowed!!
		for(int i=0; i<containers.length; i++) {
			EventInformation ei = containers[i];
			if(ei.event.equals(event)) {
				if(ei.next != rule) {
					throw new IllegalArgumentException(
					"Same event " + event + " with indistinguishable 'next' rule");
				}
			}
		}
		
		// construct new array and update event-codes etc.
		updateSortedEventRules(event, rule);
	}

	protected void updateSortedEventRules(Event newEvent, Rule newRule) {
		//	create sorted list (events only)
		Set<Event> sortedEvents = new TreeSet<Event>();
		// add old events
		for(EventInformation ei : containers) {
			sortedEvents.add(ei.event);
		}
		// add new event
		sortedEvents.add(newEvent);

		//	create new (sorted) container array
		EventInformation[] newContainers = new EventInformation[sortedEvents.size()];
		int eventCode = 0;
		boolean newOneAdded = false;
		
		for(Event ev : sortedEvents) {
			if(ev == newEvent) {
				newContainers[eventCode] = new SchemaInformedEventInformation(newRule,
						newEvent, eventCode);
				newOneAdded = true;
			} else {
				//	update event-code only
				EventInformation oldEI = containers[newOneAdded ? eventCode - 1 : eventCode];
				newContainers[eventCode] = new SchemaInformedEventInformation(oldEI.next,
						oldEI.event, eventCode);
			}
			eventCode++;
		}
		//	re-set *old* array
		containers = newContainers;
		
		//	calculate ahead of time two different first level code lengths
		codeLengthA = MethodsBag.getCodingLength(getNumberOfEvents());
		codeLengthB = MethodsBag.getCodingLength(getNumberOfEvents() + 1);

		// reset least-attribute & number of deviated attributes
		leastAttributeEventCode = Constants.NOT_FOUND;
		numberDeviatedAttributes = 0;

		for (int i = 0; i < containers.length; i++) {
			EventInformation er = containers[i];
			if (er.event.isEventType(EventType.ATTRIBUTE)) {
				if (leastAttributeEventCode == Constants.NOT_FOUND) {
					// set least attribute
					leastAttributeEventCode = i;
				}
				// count all AT (qname) [schema-invalid value]
				numberDeviatedAttributes++;
			}

		}
		// add AT (*) [schema-invalid value]
		numberDeviatedAttributes++;
	}

//	private void checkUndecidableEvent(Event event) {
//		int id = getInternalEventId(event);
//		EventRule er = this.getEventRuleAt(id);
//
//		// NOT same event ? -> throw error
//		if (!event.equals(er.event)) {
//			throw new IllegalArgumentException("Illegal Duplicate Event: "
//					+ event + " for " + this);
//		}
//	}

	public void joinRules(Rule rule) {
		// add *new* events-rules
		for (int i = 0; i < rule.getNumberOfEvents(); i++) {
			EventInformation ei = rule.lookFor(i);
			addRule(ei.event, ei.next);
		}

	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('[');

		for (int i = 0; i < getNumberOfEvents(); i++) {
			sb.append(lookFor(i).event.toString());
			if (i < (getNumberOfEvents() - 1)) {
				sb.append(", ");
			}
		}

		sb.append(']');

		return sb.toString();
	}

	public SchemaInformedRule duplicate() {
		return this;
	}


	// for encoder
	public EventInformation lookFor(Event event) {
		for (int i = 0; i < containers.length; i++) {
			EventInformation rc = containers[i];
			if (rc.event.equals(event)) {
				return rc;
			}
		}

		// nothing found
		return null;
	}

	// for decoder
	public EventInformation lookFor(int eventCode) {
		assert (eventCode >= 0 && eventCode < containers.length);
		return containers[eventCode];
	}

	public void setFirstElementRule() {
		throw new RuntimeException(
				"Not allowed to set first element, only in StartTag");
	}

}
