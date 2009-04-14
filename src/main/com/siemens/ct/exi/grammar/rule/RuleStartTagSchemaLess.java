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

import java.util.List;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.datatype.BuiltIn;
import com.siemens.ct.exi.grammar.event.Attribute;
import com.siemens.ct.exi.grammar.event.Characters;
import com.siemens.ct.exi.grammar.event.EndElement;
import com.siemens.ct.exi.grammar.event.EventType;
import com.siemens.ct.exi.grammar.event.StartElement;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20080718
 */

/*
 * <Built-in Element Grammar>
 * 
 * StartTagContent : EE 0.0 AT () StartTagContent 0.1 NS StartTagContent 0.2
 * ChildContentItems (0.3)
 * 
 * ChildContentItems (n.m) : SE () ElementContent n. m CH ElementContent n.(m+1)
 * ER ElementContent n.(m+2) CM ElementContent n.(m+3).0 PI ElementContent
 * n.(m+3).1
 */

public class RuleStartTagSchemaLess extends RuleContentSchemaLess {
	protected RuleElementSchemaLess elementContent;

	public RuleStartTagSchemaLess() {
		super();

		// initialize elementContent
		elementContent = new RuleElementSchemaLess();
	}

	public int get2ndLevelEventCode(EventType eventType,
			FidelityOptions fidelityOptions) {
		List<EventType> startTagContent = get2ndLevelEventsStartTagItems(fidelityOptions);
		int ec = getEventCode(eventType, startTagContent);

		if (ec == Constants.NOT_FOUND) {
			ec = getEventCode(eventType,
					get2ndLevelEventsChildContentItems(fidelityOptions));

			if (ec != Constants.NOT_FOUND) {
				ec += startTagContent.size();
			}
		}

		return ec;
	}

	public EventType get2ndLevelEvent(int eventCode,
			FidelityOptions fidelityOptions) {
		EventType eventType = null;

		List<EventType> startTagContent = get2ndLevelEventsStartTagItems(fidelityOptions);

		if (eventCode < startTagContent.size()) {
			// in startTag events
			eventType = startTagContent.get(eventCode);
		} else {
			// childContent events
			eventType = get2ndLevelEventsChildContentItems(fidelityOptions)
					.get(eventCode - startTagContent.size());
		}

		return eventType;
	}

	public int get2ndLevelCharacteristics(FidelityOptions fidelityOptions) {
		// startTagContent
		int ch2 = get2ndLevelEventsStartTagItems(fidelityOptions).size();
		// + childContentItems
		ch2 += get2ndLevelEventsChildContentItems(fidelityOptions).size();
		// 3rd level ?
		if (get3rdLevelCharacteristics(fidelityOptions) > 0) {
			ch2++;
		}

		return ch2;
	}

	@Override
	public Rule getElementContentRule() {
		// this is a *StartTag* Rule --> return element content rule
		return elementContent;
	}

	@Override
	public Rule getElementContentRuleForUndeclaredSE() {
		return getElementContentRule();
	}

	@Override
	public void learnStartElement(String uri, String localName) {
		addRule(new StartElement(uri, localName), getElementContentRule());
	}

	@Override
	public void learnEndElement() {
		addTerminalRule(new EndElement());
	}

	@Override
	public void learnAttribute(String uri, String localName) {
		addRule(new Attribute(uri, localName), this);
	}

	@Override
	public void learnCharacters() {
		addRule(new Characters(BuiltIn.DEFAULT_VALUE_NAME,
				BuiltIn.DEFAULT_DATATYPE), getElementContentRule());
	}
}
