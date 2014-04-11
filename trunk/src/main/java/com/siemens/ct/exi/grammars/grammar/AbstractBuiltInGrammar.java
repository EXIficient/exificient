/*
 * Copyright (C) 2007-2014 Siemens AG
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.FidelityOptions;
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
 * @version 0.9.3-SNAPSHOT
 */

public abstract class AbstractBuiltInGrammar extends AbstractGrammar implements
		BuiltInGrammar {

	private static final long serialVersionUID = -4412097592336436189L;

	protected List<Production> containers;
	// Note: BuiltInDocContent and BuiltInFragmentContent do not use this variable
	protected int ec1Length;

	public AbstractBuiltInGrammar() {
		super();
		containers = new ArrayList<Production>();
		ec1Length = 0;
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

	public boolean hasSecondOrThirdLevel(FidelityOptions fidelityOptions) {
		return true;
	}

	public Grammar getTypeEmpty() {
		return this;
	}

	public int get1stLevelEventCodeLength(FidelityOptions fidelityOptions) {
		return this.ec1Length;
	}

	public int getNumberOfEvents() {
		return containers.size();
	}

	/*
	 * a leading rule for performance reason is added to the tail
	 */
	public final void addProduction(Event event, Grammar grammar) {

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

	public int getNumberOfDeclaredAttributes() {
		throw new RuntimeException(
				"Schema-related attribute dealing in schema-less case");
	}

	public int getLeastAttributeEventCode() {
		throw new RuntimeException(
				"Schema-related attribute dealing in schema-less case");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer(this.getLabel() + "//" + "\t");

		sb.append("[");
		for (int ec = 0; ec < this.getNumberOfEvents(); ec++) {
			sb.append("," + getProduction(ec).getEvent());
		}
		sb.append("]");

		return sb.toString();
	}

	public Production getProduction(EventType eventType) {
		for (Production ei : containers) {
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
		for (Production ei : containers) {
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
		for (Production ei : containers) {
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
