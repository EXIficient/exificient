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

package com.siemens.ct.exi.grammars.grammar;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.grammars.event.EventType;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9
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

public class SchemaInformedElement extends AbstractSchemaInformedContent implements Cloneable {

	private static final long serialVersionUID = 7009002330388834813L;


	@Override
	public final boolean hasSecondOrThirdLevel(FidelityOptions fidelityOptions) {
		return (!fidelityOptions.isStrict());
	}
	
	public final int get2ndLevelCharacteristics(FidelityOptions fidelityOptions) {
		if(fidelityOptions.isStrict()) {
			return 0;
		} else {
			// EE?, SE(*), CH(*), ER?
			int ch2 = (this.hasEndElement ? 0 : 1) + 2 + (fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_DTD) ? 1 : 0);
			return get3rdLevelCharacteristics(fidelityOptions) > 0 ? ch2 + 1 : ch2;
		}
	}
	
	public final int get2ndLevelEventCode(EventType eventType,
			FidelityOptions fidelityOptions) {
		int ec2 = Constants.NOT_FOUND;
		if(!fidelityOptions.isStrict()) {
			switch(eventType) {
			case END_ELEMENT_UNDECLARED:
				ec2 += this.hasEndElement ? 0 : 1; // EE?
				break;
			case START_ELEMENT_GENERIC_UNDECLARED:
				ec2 += this.hasEndElement ? 0 : 1; // EE?
				ec2++; // SE(*)
				break;
			case CHARACTERS_GENERIC_UNDECLARED:
				ec2 += this.hasEndElement ? 0 : 1; // EE?
				ec2 += 2; // SE(*), CH(*)
				break;
			case ENTITY_REFERENCE:
				if(fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_DTD)) {
					ec2 += this.hasEndElement ? 0 : 1; // EE?
					ec2 += 2; // SE(*), CH(*)
					ec2++; // ER
				} 
				break;
			default:
				// no action	
			}
		}
		return ec2;
	}

	private static final EventType[] POSSIBLE_EVENTS = {EventType.END_ELEMENT_UNDECLARED, 
		EventType.START_ELEMENT_GENERIC_UNDECLARED, EventType.CHARACTERS_GENERIC_UNDECLARED,
		EventType.ENTITY_REFERENCE};
	
	public final EventType get2ndLevelEventType(int eventCode2,
			FidelityOptions fidelityOptions) {
		if(fidelityOptions.isStrict()) {
			// nothing..
			return null;
		} else {
			assert(eventCode2 >= 0);
			if(this.hasEndElement) {
				eventCode2++;
			}
			assert(eventCode2 < POSSIBLE_EVENTS.length);
			return POSSIBLE_EVENTS[eventCode2];
		}
	}

	@Override
	public SchemaInformedElement clone() {
		SchemaInformedElement clone = (SchemaInformedElement) super.clone();
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
