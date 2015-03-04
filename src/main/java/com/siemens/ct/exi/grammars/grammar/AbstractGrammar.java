/*
 * Copyright (C) 2007-2015 Siemens AG
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

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.5-SNAPSHOT
 */

import java.util.List;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.grammars.event.Attribute;
import com.siemens.ct.exi.grammars.event.EndElement;
import com.siemens.ct.exi.grammars.event.Event;
import com.siemens.ct.exi.grammars.event.EventType;
import com.siemens.ct.exi.grammars.event.StartElement;
import com.siemens.ct.exi.grammars.event.StartElementGeneric;
import com.siemens.ct.exi.grammars.production.Production;

public abstract class AbstractGrammar implements Grammar {

	private static final long serialVersionUID = -1626120406315756322L;

	protected static final SchemaInformedGrammar END_RULE = new SchemaInformedElement();
	static {
		END_RULE.setLabel("<END>");
	}

	protected static final Event START_ELEMENT_GENERIC = new StartElementGeneric();
	protected static final Event END_ELEMENT = new EndElement();

	protected String label = null;
	
	// EXI Profile
	protected int stopLearningContainerSize = Constants.NOT_FOUND;

	public AbstractGrammar() {
	}

	public AbstractGrammar(String label) {
		this();
		this.label = label;
	}

	public void addTerminalProduction(Event event) {
		assert (event.isEventType(EventType.END_ELEMENT) || event
				.isEventType(EventType.END_DOCUMENT));

		addProduction(event, END_RULE);
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
	
	public void stopLearning() {
	}
	
	public int learningStopped() {
		return stopLearningContainerSize;
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

	public EventType get3rdLevelEventType(int eventCode,
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

	public Grammar getElementContentGrammar() {
		return this;
	}

	// public Rule getElementContentRuleForUndeclaredSE() {
	// return this;
	// }

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof Grammar) {
			Grammar gr = (Grammar) obj;

			int numberOfEvents = gr.getNumberOfEvents();

			if (this.getNumberOfEvents() == numberOfEvents) {
				for (int i = 0; i < numberOfEvents; i++) {
					Production ei = gr.getProduction(i);
					// shallow check
					if (!ei.getEvent().equals(getProduction(i).getEvent())) {
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
