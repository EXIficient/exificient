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

import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.grammar.event.EventType;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.2.20080910
 */

/*
 * 
 * <Schema-informed Element Grammar>
 * 
 * EE n.m
 * 
 * Element i, j : SE () Element i, j n.m CH [schema-invalid value ] Element i, j
 * n.(m+1) ER Element i, j n.(m+2) CM Element i, j n.(m+3).0 PI Element i, j
 * n.(m+3).1
 */

public class RuleElementSchemaInformed extends
		AbstractSchemaInformedRuleContent {
	public String toString() {
		return "SIContent" + super.toString();
	}

	public int get2ndLevelCharacteristics(FidelityOptions fidelityOptions) {
		int ch2;

		// EE on second level necessary ?
		if (contains(END_ELEMENT_EVENT)) {
			// childContentItems only
			ch2 = get2ndLevelElementItems(fidelityOptions).size();
		} else {
			// EE + childContentItems
			ch2 = get2ndLevelElementItems(fidelityOptions).size() + 1;
		}

		// 3rd level ?
		if (get3rdLevelCharacteristics(fidelityOptions) > 0) {
			ch2++;
		}

		return ch2;
	}

	public int get2ndLevelEventCode(EventType eventType,
			FidelityOptions fidelityOptions) {
		int ec;

		// EE on second level ?
		if (contains(END_ELEMENT_EVENT)) {
			ec = getEventCode(eventType,
					get2ndLevelElementItems(fidelityOptions));
		} else {
			if (eventType == EventType.END_ELEMENT_UNDECLARED) {
				ec = 0;
			} else {
				ec = 1 + getEventCode(eventType,
						get2ndLevelElementItems(fidelityOptions));
			}

		}

		return ec;
	}

	public EventType get2ndLevelEvent(int eventCode,
			FidelityOptions fidelityOptions) {
		assert (eventCode >= 0);

		// EE on second level ?
		if (contains(END_ELEMENT_EVENT)) {
			return get2ndLevelElementItems(fidelityOptions).get(eventCode);
		} else {
			if (eventCode == 0) {
				return EventType.END_ELEMENT_UNDECLARED;
			} else {
				return get2ndLevelElementItems(fidelityOptions).get(
						eventCode - 1);
			}
		}
	}

	@Override
	public RuleElementSchemaInformed duplicate() {
		RuleElementSchemaInformed clone = new RuleElementSchemaInformed();

		clone.joinRules(this);

		return clone;
	}

}
