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

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.grammars.event.EventType;
import com.siemens.ct.exi.grammars.production.Production;
import com.siemens.ct.exi.grammars.production.SchemaInformedProduction;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.2-SNAPSHOT
 */

/*
 * 
 * <Schema-informed Element Grammar>
 * 
 * EE n.m
 * 
 * AT(xsi:type) Element i, 0 n.m AT(xsi:nil) Element i, 0 n.(m+1)
 * 
 * AT () Element i, j n.m AT (qname 0 ) [schema-invalid value] Element i, j
 * n.(m+1).0 AT (qname 1 ) [schema-invalid value] Element i, j n.(m+1).1 ... AT
 * (qname x-1 ) [schema-invalid value] Element i, j n.(m+1).(x-1) AT ()
 * [schema-invalid value] Element i, j n.(m+1).(x)
 * 
 * NS Element i, 0 n.m
 * 
 * SC Fragment n.m // ----- //
 * 
 * SE () Element i, content2 n.m CH [schema-invalid value ] Element i, content2
 * n.(m+1) ER Element i, content2 n.(m+2) CM Element i, content2 n.(m+3).0 PI
 * Element i, content2 n.(m+3).1
 */
public class SchemaInformedStartTag extends AbstractSchemaInformedContent
		implements SchemaInformedStartTagGrammar, Cloneable {

	private static final long serialVersionUID = -674782327638586700L;

	protected Grammar elementContent2;

	public SchemaInformedStartTag() {
		super();
	}
	
	public SchemaInformedStartTag(SchemaInformedGrammar elementContent2) {
		this();
		this.elementContent2 = elementContent2;
	}
	
	public GrammarType getGrammarType() {
		return GrammarType.SCHEMA_INFORMED_START_TAG_CONTENT;
	}
	
	protected int getNumberOf2ndLevelEvents(FidelityOptions fidelityOptions) {
		// EE?, AT(*), AT(schema-invalid), SE(*), CH(*), ER?
		return (this.hasEndElement ? 0 : 1) + 4 + (fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_DTD) ? 1 : 0);
	}
	
	public int get2ndLevelCharacteristics(FidelityOptions fidelityOptions) {
		if(fidelityOptions.isStrict()) {
			return 0;
		} else {
			// EE?, AT(*), AT(schema-invalid), SE(*), CH(*), ER?
			int ch2 = getNumberOf2ndLevelEvents(fidelityOptions);
			return get3rdLevelCharacteristics(fidelityOptions) > 0 ? ch2 + 1 : ch2;
		}
	}
	
	
	public int get2ndLevelEventCode(EventType eventType,
			FidelityOptions fidelityOptions) {
		int ec2 = Constants.NOT_FOUND;
		if(!fidelityOptions.isStrict()) {
			switch(eventType) {
			case END_ELEMENT_UNDECLARED:
				ec2 += this.hasEndElement ? 0 : 1; // EE?
				break;
			case ATTRIBUTE_GENERIC_UNDECLARED:
				ec2 += this.hasEndElement ? 0 : 1; // EE?
				ec2++; // AT(*)
				break;
			case ATTRIBUTE_INVALID_VALUE:
				ec2 += this.hasEndElement ? 0 : 1; // EE?
				ec2 += 2; // AT(*), AT(invalid)
				break;
			case START_ELEMENT_GENERIC_UNDECLARED:
				ec2 += this.hasEndElement ? 0 : 1; // EE?
				ec2 += 2; // AT(*), AT(invalid)
				ec2++; // SE(*)
				break;
			case CHARACTERS_GENERIC_UNDECLARED:
				ec2 += this.hasEndElement ? 0 : 1; // EE?
				ec2 += 2; // AT(*), AT(invalid)
				ec2 += 2; // SE(*), CH(*)
				break;
			case ENTITY_REFERENCE:
				if(fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_DTD)) {
					ec2 += this.hasEndElement ? 0 : 1; // EE?
					ec2 += 2; // AT(*), AT(invalid)
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
		EventType.ATTRIBUTE_GENERIC_UNDECLARED, EventType.ATTRIBUTE_INVALID_VALUE,
		EventType.START_ELEMENT_GENERIC_UNDECLARED, EventType.CHARACTERS_GENERIC_UNDECLARED,
		EventType.ENTITY_REFERENCE};
	
	public EventType get2ndLevelEventType(int eventCode2,
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
	public boolean hasSecondOrThirdLevel(FidelityOptions fidelityOptions) {
		return (!fidelityOptions.isStrict());
	}
	
	
	public void setElementContentGrammar(Grammar elementContent2) {
		this.elementContent2 = elementContent2;
	}

	@Override
	public Grammar getElementContentGrammar() {
		return elementContent2;
	}

	@Override
	public SchemaInformedStartTag clone() {
		// SchemaInformedStartTag clone = new SchemaInformedStartTag(elementContent2);
		SchemaInformedStartTag clone = (SchemaInformedStartTag) super.clone();
		
		// remove self-references
		for(int i=0; i<clone.containers.length; i++) {
			Production ei = clone.containers[i];
			if (ei.getNextGrammar() == this) {
				clone.containers[i] = new SchemaInformedProduction(clone, ei.getEvent(), i);
			}
			
		}
		
		return clone;
		
		
	}

	public String toString() {
		String s = "StartTag";
		return s + super.toString();
	}

}
