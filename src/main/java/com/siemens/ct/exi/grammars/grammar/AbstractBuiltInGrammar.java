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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.grammars.event.Attribute;
import com.siemens.ct.exi.grammars.event.Event;
import com.siemens.ct.exi.grammars.event.EventType;
import com.siemens.ct.exi.grammars.event.StartElement;
import com.siemens.ct.exi.grammars.production.Production;
import com.siemens.ct.exi.grammars.production.SchemaLessProduction;
import com.siemens.ct.exi.util.MethodsBag;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.6-SNAPSHOT
 */

public abstract class AbstractBuiltInGrammar extends AbstractGrammar implements
		BuiltInGrammar {

	private static final long serialVersionUID = -4412097592336436189L;

	protected List<Production> containers;
	// Note: BuiltInDocContent and BuiltInFragmentContent do not use this variable
	protected int ec1Length = -1;

	public AbstractBuiltInGrammar() {
		super();
		containers = new ArrayList<Production>();
		ec1Length = 0;
	}
	
	public boolean hasEndElement() {
		return false;
	}
	
	@Override
	public void stopLearning() {
		if(stopLearningContainerSize == Constants.NOT_FOUND) {
			stopLearningContainerSize = containers.size();
		}
	}

	public final boolean isSchemaInformed() {
		return false;
	}

	public Grammar getTypeEmpty() {
		return this;
	}
	
	public int get1stLevelEventCodeLength(boolean withFidelityOptionsOrNonStrict) {
		// Note: Exception BuiltInDocContent and BuiltInFragmentContent
		return this.ec1Length;
	}
	

	public int getNumberOfEvents() {
		return containers.size();
	}

	/*
	 * a leading rule for performance reason is added to the tail
	 */
	public void addProduction(Event event, Grammar grammar) {

		containers.add(new SchemaLessProduction(this, grammar, event,
				getNumberOfEvents()));
		// pre-calculate count for log2 (Note: always 2nd level productions available)
		// Note: BuiltInDocContent and BuiltInFragmentContent do not use this variable
		this.ec1Length = MethodsBag.getCodingLength(containers.size() + 1);
	}

	protected boolean contains(Event event) {
		Iterator<Production> iter = containers.iterator();

		while (iter.hasNext()) {
			if (iter.next().getEvent().equals(event)) {
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
		StringBuilder sb = new StringBuilder(this.getLabel() + "//" + "\t");

		sb.append("[");
		for (int ec = 0; ec < this.getNumberOfEvents(); ec++) {
			sb.append("," + getProduction(ec).getEvent());
		}
		sb.append("]");

		return sb.toString();
	}

	public Production getProduction(EventType eventType) {
		for (int i = 0; i < containers.size(); i++) {
			Production ei = containers.get(i);
			if (ei.getEvent().isEventType(eventType)) {
				if(!isExiProfilGhostNode(ei)) {
					return ei;
				}
			}
		}
		
		return null; // not found
	}
	
	private final boolean isExiProfilGhostNode(Production ei) {
		if(stopLearningContainerSize == Constants.NOT_FOUND) {
			// no learning-stop at all
			return false;
		} else {
			return ( ei.getEventCode() < (getNumberOfEvents() - this.stopLearningContainerSize) );		
		}
	}

	public Production getStartElementProduction(String namespaceURI,
			String localName) {
		for (int i = 0; i < containers.size(); i++) {
			Production ei = containers.get(i);
			if (ei.getEvent().isEventType(EventType.START_ELEMENT)
					&& checkQualifiedName(((StartElement) ei.getEvent()).getQName(),
							namespaceURI, localName)) {
				if(!isExiProfilGhostNode(ei)) {
					return ei;
				}
			}
		}
		return null; // not found
	}

	public Production getStartElementNSProduction(String namespaceURI) {
		return null; // not found
	}

	public Production getAttributeProduction(String namespaceURI,
			String localName) {
		for (int i = 0; i < containers.size(); i++) {
			Production ei = containers.get(i);
			if (ei.getEvent().isEventType(EventType.ATTRIBUTE)
					&& checkQualifiedName(((Attribute) ei.getEvent()).getQName(),
							namespaceURI, localName)) {
				if(!isExiProfilGhostNode(ei)) {
					return ei;
				}
			}
		}
		return null; // not found
	}

	public Production getAttributeNSProduction(String namespaceURI) {
		return null; // not found
	}

	// for decoder
	public Production getProduction(int eventCode) {
		assert (eventCode >= 0 && eventCode < containers.size());
		return containers.get(getNumberOfEvents() - 1 - eventCode);
	}

}
