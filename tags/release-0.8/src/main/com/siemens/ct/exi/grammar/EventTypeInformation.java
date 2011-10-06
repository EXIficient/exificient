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

package com.siemens.ct.exi.grammar;

import java.io.Serializable;

import com.siemens.ct.exi.grammar.event.EventType;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.8
 */

public class EventTypeInformation implements Serializable {

	private static final long serialVersionUID = -7078689920509278518L;

	// second level event code
	public final int eventCode2;
	// second level event type (e.g. undeclared SE or AT events etc.)
	public final EventType eventType;

	public EventTypeInformation(EventType eventType, int eventCode2) {
		this.eventType = eventType;
		this.eventCode2 = eventCode2;
	}

	@Override
	public String toString() {
		return "[" + eventCode2 + "] " + eventType;
	}
}