/*
 * Copyright (C) 2007-2010 Siemens AG
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
import com.siemens.ct.exi.grammar.EventInformation;
import com.siemens.ct.exi.grammar.EventTypeInformation;
import com.siemens.ct.exi.grammar.event.EventType;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20080910
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

public class SchemaInformedElement extends
		AbstractSchemaInformedContent {

	@Override
	protected void buildEvents2(FidelityOptions fidelityOptions) {
		if (fidelityOptions.isStrict()) {
			//	STRICT element grammars do not dispose of any second level events
		} else {
			int eventCode2 = 0;
			// EE on second level necessary ?
			if (!hasEndElement) {
				events2.add(new EventTypeInformation(EventType.END_ELEMENT_UNDECLARED, eventCode2++));
			}
			// extensibility: SE(*), CH(*)
			events2.add(new EventTypeInformation(EventType.START_ELEMENT_GENERIC_UNDECLARED, eventCode2++));
			events2.add(new EventTypeInformation(EventType.CHARACTERS_GENERIC_UNDECLARED, eventCode2++));
			// ER
			if (fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_DTD)) {
				events2.add(new EventTypeInformation(EventType.ENTITY_REFERENCE, eventCode2++));
			}
		}
		
		fidelityOptions2 = fidelityOptions;
	}

	@Override
	public SchemaInformedElement duplicate() {
		SchemaInformedElement clone = new SchemaInformedElement();

		//	duplicate top level only
		for (int i = 0; i < getNumberOfEvents(); i++) {
			EventInformation ei = lookFor(i);
			clone.addRule(ei.event, ei.next);
		}

		return clone;
	}
	
	
	public String toString() {
		return "Element" + super.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof SchemaInformedElement && super.equals(obj));
	}

}
