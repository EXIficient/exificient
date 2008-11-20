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

package com.siemens.ct.exi.grammar.event;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20080718
 */

public abstract class AbstractEvent implements Event {
	protected String grammarNotation;

	protected EventType eventType;

	public AbstractEvent(String grammarNotation) {
		this.grammarNotation = grammarNotation;
	}

	public EventType getEventType() {
		return eventType;
	}

	public boolean isEventType(EventType type) {
		return (type == eventType);
	}

	public String toString() {
		return grammarNotation;
	}

	@Override
	public int hashCode() {
		return eventType.ordinal();
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof Event) {
			return ((Event) obj).isEventType(getEventType());
		} else {
			return false;
		}
	}

	public int compareTo(Event o) {
		if (this.getEventType().ordinal() == o.getEventType().ordinal()) {
			// same event-type --> further checking necessary
			switch (o.getEventType()) {
			case START_ELEMENT:
				// CASE 1: first is smallest etc. (schema order)
				return +1;

				// // CASE 2: lexical order
				// StartElement otherSE = (StartElement) o;
				// return ( ( (StartElement) this ).compareTo (
				// otherSE.getNamespaceURI ( ), otherSE.getLocalPart ( ) ) );
			case ATTRIBUTE:
				Attribute otherAT = (Attribute) o;
				return (((Attribute) this).compareTo(otherAT.getNamespaceURI(),
						otherAT.getLocalPart()));
			default:
				// default for all other events
				return 0;
			}
		} else if (this.getEventType().ordinal() < o.getEventType().ordinal()) {
			return -1;
		} else {
			return +1;
		}
	}
}
