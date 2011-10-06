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

package com.siemens.ct.exi.grammar.rule;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.8
 */

import java.util.List;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.grammar.EventInformation;
import com.siemens.ct.exi.grammar.event.Attribute;
import com.siemens.ct.exi.grammar.event.EndElement;
import com.siemens.ct.exi.grammar.event.Event;
import com.siemens.ct.exi.grammar.event.EventType;
import com.siemens.ct.exi.grammar.event.StartElement;
import com.siemens.ct.exi.grammar.event.StartElementGeneric;

public abstract class AbstractRule implements Rule {

	private static final long serialVersionUID = -1626120406315756322L;

	protected static final SchemaInformedRule END_RULE = new SchemaInformedElement();
	static {
		END_RULE.setLabel("<END>");
	}

	protected static final Event START_ELEMENT_GENERIC = new StartElementGeneric();
	protected static final Event END_ELEMENT = new EndElement();

	protected String label = null;

	public AbstractRule() {
	}

	public AbstractRule(String label) {
		this();
		this.label = label;
	}

	public void addTerminalRule(Event event) {
		assert (event.isEventType(EventType.END_ELEMENT) || event
				.isEventType(EventType.END_DOCUMENT));

		addRule(event, END_RULE);
	}

	// public boolean isFirstElementRule() {
	// return false;
	// }

	/*
	 * Do NOT learn per default (non-Javadoc)
	 * 
	 * @see
	 * com.siemens.exi.grammar.rule.Rule#learnStartElement(javax.xml.namespace
	 * .QName)
	 */
	public void learnStartElement(StartElement se) {
	}

	public void learnEndElement() {
	}

	public void learnAttribute(Attribute at) {
	}

	public void learnCharacters() {
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		if (this.label != null && !this.label.equals("")) {
			return this.label;
		} else {
			return this.getClass().getSimpleName() + "[" + this.hashCode()
					+ "]";
		}
	}

	public int get3rdLevelCharacteristics(FidelityOptions fidelityOptions) {
		int ch3 = 0;

		if (!fidelityOptions.isStrict()) {
			// CM
			if (fidelityOptions
					.isFidelityEnabled(FidelityOptions.FEATURE_COMMENT)) {
				ch3++;
			}
			// PI
			if (fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_PI)) {
				ch3++;
			}
		}

		return ch3;
	}

	public int get3rdLevelEventCode(EventType eventType,
			FidelityOptions fidelityOptions) {
		int ec3 = Constants.NOT_FOUND;

		if (!fidelityOptions.isStrict()) {
			// CM
			if (fidelityOptions
					.isFidelityEnabled(FidelityOptions.FEATURE_COMMENT)) {
				if (EventType.COMMENT == eventType) {
					ec3 = 0;
				} else if (EventType.PROCESSING_INSTRUCTION == eventType) {
					ec3 = 1;
				}
			} else if (fidelityOptions
					.isFidelityEnabled(FidelityOptions.FEATURE_PI)) {
				if (EventType.PROCESSING_INSTRUCTION == eventType) {
					ec3 = 0;
				}
			}
		}

		return ec3;
	}

	public EventType get3rdLevelEvent(int eventCode,
			FidelityOptions fidelityOptions) {
		if (eventCode == 0) {
			if (fidelityOptions
					.isFidelityEnabled(FidelityOptions.FEATURE_COMMENT)) {
				return EventType.COMMENT;
			} else {
				return EventType.PROCESSING_INSTRUCTION;
			}
		} else {
			return EventType.PROCESSING_INSTRUCTION;
		}
	}

	protected static int getEventCode(EventType eventType,
			List<EventType> events) {
		for (int i = 0; i < events.size(); i++) {
			if (events.get(i).equals(eventType)) {
				return i;
			}
		}

		return Constants.NOT_FOUND;
	}

	public Rule getElementContentRule() {
		return this;
	}

	// public Rule getElementContentRuleForUndeclaredSE() {
	// return this;
	// }

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof Rule) {
			Rule r = (Rule) obj;

			int numberOfEvents = r.getNumberOfEvents();

			if (this.getNumberOfEvents() == numberOfEvents) {
				for (int i = 0; i < numberOfEvents; i++) {
					EventInformation ei = r.lookFor(i);
					// shallow check
					if (!ei.event.equals(lookFor(i).event)) {
						return false;
					}
				}

				return true;
			}
		}

		return false;
	}

	public int hashCode() {
		return super.hashCode();
	}

	protected static boolean checkQualifiedName(QName c, String namespaceURI,
			String localName) {
		return (c.getLocalPart().equals(localName) && c.getNamespaceURI()
				.equals(namespaceURI));
	}

}
