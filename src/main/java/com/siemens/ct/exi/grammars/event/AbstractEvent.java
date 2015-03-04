/*
 * Copyright (C) 2007-2015 Siemens AG
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

package com.siemens.ct.exi.grammars.event;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.4-SNAPSHOT
 */

public abstract class AbstractEvent implements Event {

	private static final long serialVersionUID = -3334801751013361193L;

	protected final EventType eventType;

	public AbstractEvent(EventType eventType) {
		this.eventType = eventType;
	}

	public final EventType getEventType() {
		return eventType;
	}

	public boolean isEventType(EventType type) {
		return (type == eventType);
	}

	public String toString() {
		return eventType.toString();
	}

	@Override
	public int hashCode() {
		return eventType.ordinal();
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof AbstractEvent) {
			return eventType == ((AbstractEvent) obj).eventType;
			// return ((Event) obj).isEventType(getEventType());
		} else {
			return false;
		}
	}
}
