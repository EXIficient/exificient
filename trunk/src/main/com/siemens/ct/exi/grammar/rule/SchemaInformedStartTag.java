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

import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.grammar.EventInformation;
import com.siemens.ct.exi.grammar.EventTypeInformation;
import com.siemens.ct.exi.grammar.SchemaInformedEventInformation;
import com.siemens.ct.exi.grammar.event.EventType;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.8
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
		implements SchemaInformedStartTagRule, Cloneable {

	private static final long serialVersionUID = -674782327638586700L;

	protected SchemaInformedRule elementContent2;

	public SchemaInformedStartTag(SchemaInformedRule elementContent2) {
		super();
		this.elementContent2 = elementContent2;
	}

	@Override
	protected void buildEvents2(FidelityOptions fidelityOptions) {
		if (!fidelityOptions.isStrict()) {
			int eventCode2 = 0;

			// EE on second level necessary ?
			if (!hasEndElement) {
				events2.add(new EventTypeInformation(
						EventType.END_ELEMENT_UNDECLARED, eventCode2++));
			}
			// AT(*) & AT[schema-invalid]
			events2.add(new EventTypeInformation(
					EventType.ATTRIBUTE_GENERIC_UNDECLARED, eventCode2++));
			events2.add(new EventTypeInformation(
					EventType.ATTRIBUTE_INVALID_VALUE, eventCode2++));
			// extensibility: SE(*), CH(*)
			events2.add(new EventTypeInformation(
					EventType.START_ELEMENT_GENERIC_UNDECLARED, eventCode2++));
			events2.add(new EventTypeInformation(
					EventType.CHARACTERS_GENERIC_UNDECLARED, eventCode2++));
			// ER
			if (fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_DTD)) {
				events2.add(new EventTypeInformation(
						EventType.ENTITY_REFERENCE, eventCode2++));
			}
		}

		fidelityOptions2 = fidelityOptions;
	}

	@Override
	public boolean hasSecondOrThirdLevel(FidelityOptions fidelityOptions) {
		return (!fidelityOptions.isStrict());
	}

	@Override
	public Rule getElementContentRule() {
		return elementContent2;
	}

	@Override
	public SchemaInformedStartTag clone() {
		// SchemaInformedStartTag clone = new SchemaInformedStartTag(elementContent2);
		SchemaInformedStartTag clone = (SchemaInformedStartTag) super.clone();
		
		// remove self-references
		for(int i=0; i<clone.containers.length; i++) {
			EventInformation ei = clone.containers[i];
			if (ei.next == this) {
				clone.containers[i] = new SchemaInformedEventInformation(clone, ei.event, i);
			}
			
		}
		
		return clone;
		
		
	}

	public String toString() {
		String s = "StartTag";
		return s + super.toString();
	}

}
