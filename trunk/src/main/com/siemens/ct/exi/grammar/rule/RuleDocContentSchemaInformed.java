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

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.grammar.event.Event;
import com.siemens.ct.exi.grammar.event.EventType;
import com.siemens.ct.exi.grammar.event.StartElementGeneric;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.2.20080718
 */

/*
 * <Built-in Document Grammar>
 * 
 * DocContent : SE (G 0) DocEnd 0 SE (G 1) DocEnd 1 ... SE (G n-1) DocEnd n-1 SE
 * () DocEnd n.0 DT DocContent n.1 CM DocContent n.2.0 PI DocContent n.2.1
 */

public class RuleDocContentSchemaInformed extends AbstractSchemaInformedRule {
	protected Rule docEnd;

	protected final Event seGeneric;

	public RuleDocContentSchemaInformed(Rule docEnd, String label) {
		super(label);

		this.docEnd = docEnd;
		this.seGeneric = new StartElementGeneric();
	}

	public RuleDocContentSchemaInformed(Rule docEnd) {
		this.docEnd = docEnd;
		this.seGeneric = new StartElementGeneric();
	}

	@Override
	public Rule get1stLevelRule(int ec) throws IndexOutOfBoundsException {
		if (ec == getNumberOfEvents()) {
			return docEnd;
		} else {
			return super.get1stLevelRule(ec);
		}
	}

	public Event get1stLevelEvent(int eventCode) {
		if (eventCode == getNumberOfEvents()) {
			return seGeneric;
		} else {
			return super.get1stLevelEvent(eventCode);
		}
	}

	public int get2ndLevelEventCode(EventType eventType,
			FidelityOptions fidelityOptions) {
		if (eventType == EventType.START_ELEMENT_GENERIC_UNDECLARED) {
			return 0;
		} else if (eventType == EventType.DOC_TYPE
				&& fidelityOptions
						.isFidelityEnabled(FidelityOptions.FEATURE_DTD)) {
			return 1;
		}

		return Constants.NOT_FOUND;
	}

	public EventType get2ndLevelEvent(int eventCode,
			FidelityOptions fidelityOptions) {
		if (eventCode == 0) {
			return EventType.START_ELEMENT_GENERIC_UNDECLARED;
		} else if (eventCode == 1
				&& fidelityOptions
						.isFidelityEnabled(FidelityOptions.FEATURE_DTD)) {
			return EventType.DOC_TYPE;
		}

		return null;
	}

	public int get2ndLevelCharacteristics(FidelityOptions fidelityOptions) {
		int ch2 = 1; // SE(*), in any case e.g. type-cast possible
		ch2 += fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_DTD) ? 1
				: 0;

		ch2 += get3rdLevelCharacteristics(fidelityOptions) > 0 ? 1 : 0;

		return ch2;
	}

	@Override
	public boolean hasSecondOrThirdLevel(FidelityOptions fidelityOptions) {
		// DocContent contains in any case SE(*) (even in strict mode)
		return true;
	}

	public Rule getElementContentRuleForUndeclaredSE() {
		return this.docEnd;
	}

}
