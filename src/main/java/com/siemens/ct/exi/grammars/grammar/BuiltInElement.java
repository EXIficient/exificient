/*
 * Copyright (C) 2007-2012 Siemens AG
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

import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.grammars.event.Attribute;
import com.siemens.ct.exi.grammars.event.EventType;
import com.siemens.ct.exi.grammars.event.StartElement;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.2-SNAPSHOT
 */

/*
 * <Built-in Element Grammar>
 * 
 * ElementContent : EE 0 ChildContentItems (1.0)
 * 
 * ChildContentItems (n.m) : SE () ElementContent n. m CH ElementContent n.(m+1)
 * ER ElementContent n.(m+2) CM ElementContent n.(m+3).0 PI ElementContent
 * n.(m+3).1
 */
public class BuiltInElement extends AbstractBuiltInContent {

	private static final long serialVersionUID = -787607643786879290L;

	protected BuiltInElement() {
		super();

		// EE on first level
		addProduction(END_ELEMENT, END_RULE);
	}
	
	public GrammarType getGrammarType() {
		return GrammarType.BUILT_IN_ELEMENT_CONTENT;
	}

	public int get2ndLevelEventCode(EventType eventType,
			FidelityOptions fidelityOptions) {
		return getEventCode(eventType,
				get2ndLevelEventsChildContentItems(fidelityOptions));
	}

	public EventType get2ndLevelEventType(int eventCode,
			FidelityOptions fidelityOptions) {
		return get2ndLevelEventsChildContentItems(fidelityOptions).get(
				eventCode);
	}

	public int get2ndLevelCharacteristics(FidelityOptions fidelityOptions) {
		// childContentItems only
		int ch2 = get2ndLevelEventsChildContentItems(fidelityOptions).size();

		// 3rd level ?
		if (get3rdLevelCharacteristics(fidelityOptions) > 0) {
			ch2++;
		}

		return ch2;
	}

	@Override
	public void learnStartElement(StartElement se) {
		addProduction(se, this);
	}

	/*
	 * Note: learnEndElement ( ) not necessary since EE is already present on
	 * first level for element-content-rules
	 */

	@Override
	public void learnAttribute(Attribute at) {
		// this should never happen!
		throw new IllegalArgumentException(
				"ElementContent Rule cannot learn AT events");
	}

//	@Override
//	public void learnCharacters() {
//		addRule(new Characters(BuiltIn.DEFAULT_VALUE_NAME,
//				BuiltIn.DEFAULT_DATATYPE), this);
//	}

}
