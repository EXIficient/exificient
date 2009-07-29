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

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.grammar.event.EventType;
import com.siemens.ct.exi.grammar.event.StartElementGeneric;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20080718
 */

/*
 * <Built-in Document Grammar>
 * 
 * DocContent : SE (G 0) DocEnd 0 SE (G 1) DocEnd 1 ... SE (G n-1) DocEnd n-1 SE
 * () DocEnd n.0 DT DocContent n.1 CM DocContent n.2.0 PI DocContent n.2.1
 */

public class RuleDocContentSchemaInformed extends AbstractSchemaInformedRule {
	protected Rule docEnd;

	public RuleDocContentSchemaInformed(Rule docEnd) {
		this.docEnd = docEnd;
		//	SE(*) --> DocEnd
		this.addRule(new StartElementGeneric(), docEnd);
	}
	
	public RuleDocContentSchemaInformed(Rule docEnd, String label) {
		this(docEnd);
		this.setLabel(label);
	}

	public String toString() {
		return "DocContent" + super.toString();
	}
	

	public int get2ndLevelEventCode(EventType eventType,
			FidelityOptions fidelityOptions) {
			if (eventType == EventType.DOC_TYPE
				&& fidelityOptions
						.isFidelityEnabled(FidelityOptions.FEATURE_DTD)) {
				return 0;
		}

		return Constants.NOT_FOUND;
	}

	public EventType get2ndLevelEvent(int eventCode,
			FidelityOptions fidelityOptions) {
		if (eventCode == 0
				&& fidelityOptions
						.isFidelityEnabled(FidelityOptions.FEATURE_DTD)) {
			return EventType.DOC_TYPE;
		}

		return null;
	}

	public int get2ndLevelCharacteristics(FidelityOptions fidelityOptions) {
		int ch2 = fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_DTD) ? 1
				: 0;

		ch2 += get3rdLevelCharacteristics(fidelityOptions) > 0 ? 1 : 0;

		return ch2;
	}

//	public Rule getElementContentRule() {
//		System.out.println("docEnd Move");
//		// return this.docEnd;
//		return this;
//	}
	
//	public Rule getElementContentRuleForUndeclaredSE() {
//		return this.docEnd;
//	}
	

	@Override
	public boolean hasSecondOrThirdLevel(FidelityOptions fidelityOptions) {
		return get2ndLevelCharacteristics(fidelityOptions) > 0;
	}

}
