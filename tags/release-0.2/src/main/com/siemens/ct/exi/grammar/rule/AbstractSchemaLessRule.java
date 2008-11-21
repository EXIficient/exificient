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
import java.util.List;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.grammar.event.Event;
import com.siemens.ct.exi.util.MethodsBag;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20080919
 */

public abstract class AbstractSchemaLessRule extends AbstractRule implements
		SchemaLessRule {
	protected List<Event> events;
	protected List<Rule> rules;

	public AbstractSchemaLessRule() {
		super();
		init();
	}

	private void init() {
		events = new ArrayList<Event>();
		rules = new ArrayList<Rule>();
	}

	public final boolean isSchemaRule() {
		return false;
	}

	protected int getInternalIndex(int eventCode) {
		return (getNumberOfEvents() - 1 - eventCode);
	}

	protected int getEventCode(int internalIndex) {
		return (getNumberOfEvents() - 1 - internalIndex);
	}

	public int get1stLevelEventCodeLength(FidelityOptions fidelityOptions) {
		return (hasSecondOrThirdLevel(fidelityOptions) ? MethodsBag
				.getCodingLength(events.size() + 1) : MethodsBag
				.getCodingLength(events.size()));
	}

	public int get1stLevelEventCode(Event event) {
		for (int i = 0; i < events.size(); i++) {
			if (events.get(i).equals(event)) {
				return (getEventCode(i));
			}
		}

		return Constants.NOT_FOUND;
	}

	public Event get1stLevelEvent(int eventCode) {
		return (events.get(getInternalIndex(eventCode)));
	}

	public int getNumberOfEvents() {
		assert (events.size() == rules.size());

		return events.size();
	}

	/*
	 * a leading rule for performance reason is added to the tail
	 */
	public void addRule(Event event, Rule rule) {
		assert (!isTerminalRule());
		assert (!this.contains(event));

		events.add(event);
		rules.add(rule);
	}

	public Rule get1stLevelRule(int ec) throws IndexOutOfBoundsException {
		return rules.get(getInternalIndex(ec));
	}

	protected boolean contains(Event event) {
		for (int i = 0; i < events.size(); i++) {
			if (events.get(i).equals(event)) {
				return true;
			}
		}

		return false;
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
				s += "," + this.get1stLevelEvent(ec);
			}

			s += "]";
		}

		return s;
	}

}
