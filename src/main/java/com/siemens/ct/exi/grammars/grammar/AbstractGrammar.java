/*
 * Copyright (c) 2007-2015 Siemens AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */

package com.siemens.ct.exi.grammars.grammar;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.5
 */

import java.util.List;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.Constants;
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
