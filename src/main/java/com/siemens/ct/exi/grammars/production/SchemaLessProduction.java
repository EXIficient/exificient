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

import com.siemens.ct.exi.grammars.event.Event;
import com.siemens.ct.exi.grammars.grammar.Grammar;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.3-SNAPSHOT
 */

public class SchemaLessProduction extends AbstractProduction {

	private static final long serialVersionUID = 2559182995076922136L;

	protected final Grammar father;

	public SchemaLessProduction(Grammar father, Grammar next, Event event,
			int eventCode) {
		super(next, event, eventCode);
		this.father = father;
	}

	@Override
	public int getEventCode() {
		// internal eventCodes in schema-less do have the reverse order
		return father.getNumberOfEvents() - 1 - this.eventCode;

	}

}
