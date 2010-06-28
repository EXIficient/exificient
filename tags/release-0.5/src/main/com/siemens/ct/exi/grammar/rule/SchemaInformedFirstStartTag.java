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
import com.siemens.ct.exi.grammar.EventTypeInformation;
import com.siemens.ct.exi.grammar.event.EventType;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.4.2009101209
 */

public class SchemaInformedFirstStartTag extends SchemaInformedStartTag {

	public SchemaInformedFirstStartTag(SchemaInformedRule elementContent2) {
		super(elementContent2);
		isFirstElementRule = true;
	}
	
//	@Override
//	public void setFirstElementRule() {
//		this.isFirstElementRule = true;
//		
//	}

	
	@Override
	protected void buildEvents2(FidelityOptions fidelityOptions) {
		int eventCode2 = 0;
		
		if (fidelityOptions.isStrict()) {
				// xsi:type
				if (isTypeCastable) {
					events2.add(new EventTypeInformation(EventType.ATTRIBUTE_XSI_TYPE, eventCode2++));	
				}
				// xsi:nil
				if (isNillable) {
					events2.add(new EventTypeInformation(EventType.ATTRIBUTE_XSI_NIL, eventCode2++));
				}
		} else {
			// EE on second level necessary ?
			if (!hasEndElement) {
				events2.add(new EventTypeInformation(EventType.END_ELEMENT_UNDECLARED, eventCode2++));	
			}
			// xsi:type & xsi:nil ?
//			if (isFirstElementRule) {
				events2.add(new EventTypeInformation(EventType.ATTRIBUTE_XSI_TYPE, eventCode2++));
				events2.add(new EventTypeInformation(EventType.ATTRIBUTE_XSI_NIL, eventCode2++));
//			}
			// AT(*) & AT[schema-invalid]
			events2.add(new EventTypeInformation(EventType.ATTRIBUTE_GENERIC_UNDECLARED, eventCode2++));
			events2.add(new EventTypeInformation(EventType.ATTRIBUTE_INVALID_VALUE, eventCode2++));
			//	NS, SC
//			if (isFirstElementRule) {
				if (fidelityOptions .isFidelityEnabled(FidelityOptions.FEATURE_PREFIX)) {
					events2.add(new EventTypeInformation(EventType.NAMESPACE_DECLARATION, eventCode2++));
				}
				// SC
				if (fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_SC)) {
					events2.add(new EventTypeInformation(EventType.SELF_CONTAINED, eventCode2++));
				}
//			}
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
	public boolean hasSecondOrThirdLevel(FidelityOptions fidelityOptions) {
		return (  !fidelityOptions.isStrict() || isTypeCastable || isNillable  );
	}


	@Override
	public Rule getElementContentRule() {
		return elementContent2;
	}


//	@Override
//	public SchemaInformedFirstStartTag duplicate() {
//		SchemaInformedFirstStartTag clone = new SchemaInformedFirstStartTag(
//				elementContent2);
//		
//		//	duplicate top level
//		for (int i = 0; i < getNumberOfEvents(); i++) {
//			EventInformation ei = lookFor(i);
//			clone.addRule(ei.event, ei.next);
//		}
//
//		// nillable and type
//		clone.setTypeCastable(this.isTypeCastable);
//		clone.setNillable(this.isNillable, typeEmpty);
//		if (this.isFirstElementRule) {
//			clone.setFirstElementRule();	
//		}
//
//		return clone;
//	}
	
	public String toString() {
		String s = "StartTag";
		
			if (this.isTypeCastable) {
				s += "(xsi:type)";
			}
			if (this.isNillable) {
				s += "(xsi:nil)";
			}			
		
		return s + super.toString();
	}

}
