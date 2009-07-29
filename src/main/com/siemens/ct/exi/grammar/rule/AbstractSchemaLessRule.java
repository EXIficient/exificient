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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.grammar.EventInformation;
import com.siemens.ct.exi.grammar.EventInformationSchemaLess;
import com.siemens.ct.exi.grammar.event.Event;
import com.siemens.ct.exi.util.MethodsBag;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20080919
 */

public abstract class AbstractSchemaLessRule extends AbstractRule implements
		SchemaLessRule {
	
	protected List<EventInformation> containers;
	protected int eventCount;

	public AbstractSchemaLessRule() {
		super();
		containers = new ArrayList<EventInformation>();
		eventCount = 0;
	}

	public final boolean isSchemaRule() {
		return false;
	}
	
	public boolean hasSecondOrThirdLevel(FidelityOptions fidelityOptions) {
		return true;
	}
	
	public Rule getTypeEmpty() {
		return this;
	}

	public int get1stLevelEventCodeLength(FidelityOptions fidelityOptions) {
	 		return (hasSecondOrThirdLevel(fidelityOptions) ? MethodsBag
	 				.getCodingLength(eventCount + 1) : MethodsBag
	 				.getCodingLength(eventCount));
	}


	public int getNumberOfEvents() {
		return containers.size();
	}

	/*
	 * a leading rule for performance reason is added to the tail
	 */
	public void addRule(Event event, Rule rule) {
		assert (!isTerminalRule());
		assert (!this.contains(event));

		containers.add(new EventInformationSchemaLess(this, rule, event, getNumberOfEvents()));
		eventCount = containers.size();
	}

	protected boolean contains(Event event) {
		Iterator<EventInformation> iter = containers.iterator();
		
		while(iter.hasNext()) {
			if( iter.next().event.equals(event) ) {
				return true;
			}
		}
		
		return false;
	}

	public int getNumberOfSchemaDeviatedAttributes() {
		throw new RuntimeException(
				"Schema-related attribute dealing in schema-less case");
	}

	public int getLeastAttributeEventCode() {
		throw new RuntimeException(
				"Schema-related attribute dealing in schema-less case");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String s = this.getLabel() + "//" + "\t";

		if (this.isTerminalRule()) {
			s += "<END_RULE>";
		} else {
			s += "[";

			for (int ec = 0; ec < this.getNumberOfEvents(); ec++) {
				s += "," + lookFor(ec).event;
			}

			s += "]";
		}

		return s;
	}
	
	
	
	// for encoder
	public EventInformation lookFor( Event event ) {
		for (int i=0; i<containers.size(); i++) {
			EventInformation rc = containers.get(i);
			if (rc.event.equals(event)) {
				return rc;
			}
		}
		
		//	nothing found
		return null;
	}
	
	//	for decoder
	public EventInformation lookFor( int eventCode ) {
		assert(eventCode >= 0 && eventCode < containers.size());
		return containers.get(getNumberOfEvents() - 1 - eventCode);
	}

}
