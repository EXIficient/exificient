/*
 * Copyright (C) 2007-2011 Siemens AG
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

package com.siemens.ct.exi.grammars.grammar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.grammars.event.Attribute;
import com.siemens.ct.exi.grammars.event.AttributeNS;
import com.siemens.ct.exi.grammars.event.Event;
import com.siemens.ct.exi.grammars.event.EventType;
import com.siemens.ct.exi.grammars.event.StartElement;
import com.siemens.ct.exi.grammars.event.StartElementNS;
import com.siemens.ct.exi.grammars.production.Production;
import com.siemens.ct.exi.grammars.production.SchemaInformedProduction;
import com.siemens.ct.exi.util.MethodsBag;
import com.siemens.ct.exi.util.sort.EventCodeAssignment;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.8
 */

public abstract class AbstractSchemaInformedGrammar extends AbstractGrammar implements
		SchemaInformedGrammar {

	private static final long serialVersionUID = -5145919918050815021L;

	// contains all necessary event information including event-codes
	Production[] containers = new Production[0];

	// event code lengths
	protected int codeLengthA; // 1st level only
	protected int codeLengthB; // 2nd OR 3rd level

	// EE present
	protected boolean hasEndElement = false;

	// protected SchemaInformedRule typeEmpty;

	/*
	 * schema-deviated attributes
	 */
	protected int leastAttributeEventCode = Constants.NOT_FOUND;
	protected int numberOfDeclaredAttributes = 0;

	public AbstractSchemaInformedGrammar() {
		super();
		init();
	}

	public AbstractSchemaInformedGrammar(String label) {
		super(label);
		init();
	}

	protected final boolean isTerminalRule() {
		return (this == END_RULE);
	}

	private void init() {
		containers = new Production[0];
	}

	public final boolean isSchemaInformed() {
		return true;
	}

	public abstract boolean hasSecondOrThirdLevel(FidelityOptions fidelityOptions);

	public int get1stLevelEventCodeLength(FidelityOptions fidelityOptions) {
		return (hasSecondOrThirdLevel(fidelityOptions) ? codeLengthB
				: codeLengthA);
	}

	public int getNumberOfDeclaredAttributes() {
		return numberOfDeclaredAttributes;
	}

	public int getLeastAttributeEventCode() {
		return leastAttributeEventCode;
	}

	public final int getNumberOfEvents() {
		return containers.length;
	}

	public void addProduction(Event event, Grammar rule) {
		if (isTerminalRule()) {
			// *end* in our context should really mean end ;-)
			throw new IllegalArgumentException(
					"EndGrammar can not have events attached");
		}

		// minor consistency check
		// TODO more
		if ((event.isEventType(EventType.END_ELEMENT)
				|| event.isEventType(EventType.ATTRIBUTE_GENERIC) || event
				.isEventType(EventType.START_ELEMENT_GENERIC))
				&& lookForEvent(event.getEventType()) != null) {
			// has already event --> nothing to do
			// System.err.println("Event " + event + " already present!");
		} else {
			if (event.isEventType(EventType.END_ELEMENT)) {
				hasEndElement = true;
			}

			// undecidable choice not allowed!!
			for (int i = 0; i < containers.length; i++) {
				Production ei = containers[i];
				if (ei.getEvent().equals(event)) {
					if (ei.getNextRule() != rule) {
						// if (rule.equals(ei.next)) {
						throw new IllegalArgumentException("Same event "
								+ event + " with indistinguishable 'next' grammar");
					}
				}
			}

			// construct new array and update event-codes etc.
			updateSortedEvents(event, rule);
		}
	}

	static EventCodeAssignment eventCodeAss = new EventCodeAssignment();

	protected void updateSortedEvents(Event newEvent, Grammar newRule) {
		// create sorted event list
		List<Event> sortedEvents = new ArrayList<Event>();
		// Set<Event> sortedEvents = new TreeSet<Event>();
		// add old events
		for (Production ei : containers) {
			sortedEvents.add(ei.getEvent());
		}
		// add new event
		sortedEvents.add(newEvent);
		// sort events
		Collections.sort(sortedEvents, eventCodeAss);

		// create new (sorted) container array
		Production[] newContainers = new Production[sortedEvents
				.size()];
		int eventCode = 0;
		boolean newOneAdded = false;

		for (Event ev : sortedEvents) {
			if (ev == newEvent) {
				newContainers[eventCode] = new SchemaInformedProduction(
						newRule, newEvent, eventCode);
				newOneAdded = true;
			} else {
				// update event-code only
				Production oldEI = containers[newOneAdded ? eventCode - 1
						: eventCode];
				newContainers[eventCode] = new SchemaInformedProduction(
						oldEI.getNextRule(), oldEI.getEvent(), eventCode);
			}
			eventCode++;
		}
		// re-set *old* array
		containers = newContainers;

		// calculate ahead of time two different first level code lengths
		codeLengthA = MethodsBag.getCodingLength(getNumberOfEvents());
		codeLengthB = MethodsBag.getCodingLength(getNumberOfEvents() + 1);

		// reset number of declared attributes and least attribute event-code
		leastAttributeEventCode = Constants.NOT_FOUND;
		numberOfDeclaredAttributes = 0;

		for (int i = 0; i < containers.length; i++) {
			Production er = containers[i];
			if (er.getEvent().isEventType(EventType.ATTRIBUTE)) {
				if (leastAttributeEventCode == Constants.NOT_FOUND) {
					// set least attribute
					leastAttributeEventCode = i;
				}
				// count all AT (qname)
				numberOfDeclaredAttributes++;
			}
		}

		// // add AT (*) [schema-invalid value]
		// numberOfDeclaredAttributes++;
	}

	public void joinRules(Grammar rule) {
		// add *new* events-rules
		for (int i = 0; i < rule.getNumberOfEvents(); i++) {
			Production ei = rule.lookFor(i);
			addProduction(ei.getEvent(), ei.getNextRule());
		}

	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('[');

		for (int i = 0; i < getNumberOfEvents(); i++) {
			sb.append(lookFor(i).getEvent().toString());
			if (i < (getNumberOfEvents() - 1)) {
				sb.append(", ");
			}
		}

		sb.append(']');

		return sb.toString();
	}

	@Override
	public SchemaInformedGrammar clone() {
		try {
			return (SchemaInformedGrammar) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException(e);
		}
	}

	public SchemaInformedGrammar duplicate() {
		return clone();
	}

	public Production lookForEvent(EventType eventType) {
		for (Production ei : containers) {
			if (ei.getEvent().isEventType(eventType)) {
				return ei;
			}
		}
		return null; // not found
	}

	public Production lookForStartElement(String namespaceURI,
			String localName) {
		for (Production ei : containers) {
			if (ei.getEvent().isEventType(EventType.START_ELEMENT)
					&& checkQualifiedName(((StartElement) ei.getEvent()).getQName(),
							namespaceURI, localName)) {
				return ei;
			}
		}
		return null; // not found
	}

	public Production lookForStartElementNS(String namespaceURI) {
		for (Production ei : containers) {
			if (ei.getEvent().isEventType(EventType.START_ELEMENT_NS)
					&& ((StartElementNS) ei.getEvent()).getNamespaceURI().equals(
							namespaceURI)) {
				return ei;
			}
		}
		return null; // not found
	}

	public Production lookForAttribute(String namespaceURI,
			String localName) {
		for (Production ei : containers) {
			if (ei.getEvent().isEventType(EventType.ATTRIBUTE)
					&& checkQualifiedName(((Attribute) ei.getEvent()).getQName(),
							namespaceURI, localName)) {
				return ei;
			}
		}
		return null; // not found
	}

	public Production lookForAttributeNS(String namespaceURI) {
		for (Production ei : containers) {
			if (ei.getEvent().isEventType(EventType.ATTRIBUTE_NS)
					&& ((AttributeNS) ei.getEvent()).getNamespaceURI().equals(
							namespaceURI)) {
				return ei;
			}
		}
		return null; // not found
	}

	// for decoder
	public final Production lookFor(int eventCode) {
		assert (eventCode >= 0 && eventCode < containers.length);
		return containers[eventCode];
	}

}
