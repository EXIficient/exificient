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

import javax.xml.namespace.QName;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.grammars.event.EventType;
import com.siemens.ct.exi.grammars.production.Production;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.5-SNAPSHOT
 */

public class SchemaInformedFirstStartTag extends SchemaInformedStartTag
		implements SchemaInformedFirstStartTagGrammar, Cloneable {

	private static final long serialVersionUID = -6071059051303822226L;

	// subtype (xsi:type) OR nillable (xsi:nill) ?
	protected boolean isTypeCastable = false;
	protected boolean isNillable = false;
	protected SchemaInformedFirstStartTagGrammar typeEmpty;

	protected QName typeName = null;

	public SchemaInformedFirstStartTag() {
		super();
	}
	
	public SchemaInformedFirstStartTag(SchemaInformedGrammar elementContent2) {
		super(elementContent2);
	}
	
	public GrammarType getGrammarType() {
		return GrammarType.SCHEMA_INFORMED_FIRST_START_TAG_CONTENT;
	}

	public SchemaInformedFirstStartTag(SchemaInformedStartTagGrammar startTag) {
		this((SchemaInformedGrammar) startTag.getElementContentGrammar());

		// clone top level
		for (int i = 0; i < startTag.getNumberOfEvents(); i++) {
			Production ei = startTag.getProduction(i);
			// remove self-reference
			Grammar next = ei.getNextGrammar();
			if (next == startTag) {
				next = this;
			}
			this.addProduction(ei.getEvent(), next);
		}
	}

	public QName getTypeName() {
		return this.typeName;
	}

	public void setTypeName(QName typeName) {
		this.typeName = typeName;
	}

	public void setTypeCastable(boolean isTypeCastable) {
		this.isTypeCastable = isTypeCastable;
	}

	public boolean isTypeCastable() {
		return isTypeCastable;
	}

	public void setNillable(boolean isNillable) {
		this.isNillable = isNillable;
	}

	public boolean isNillable() {
		return isNillable;
	}

	public void setTypeEmpty(SchemaInformedFirstStartTagGrammar typeEmpty) {
		this.typeEmpty = typeEmpty;
	}

	public SchemaInformedFirstStartTagGrammar getTypeEmpty() {
		return this.typeEmpty;
	}
	
	@Override
	protected int getNumberOf2ndLevelEvents(FidelityOptions fidelityOptions) {
		// EE?, AT(*), AT(schema-invalid), SE(*), CH(*), ER?
		int ev2 =  super.getNumberOf2ndLevelEvents(fidelityOptions);
		ev2 += 2; // xsi:type and xsi:nil
		// NS, SC
		if (fidelityOptions
				.isFidelityEnabled(FidelityOptions.FEATURE_PREFIX)) {
			ev2++;
		}
		if (fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_SC)) {
			ev2++;
		}
		
		return ev2;
	}
	
	@Override
	public final int get2ndLevelCharacteristics(FidelityOptions fidelityOptions) {
		if(fidelityOptions.isStrict()) {
			return (isTypeCastable ? 1 : 0) + (isNillable ? 1 : 0);
		} else {
			int ch2 = getNumberOf2ndLevelEvents(fidelityOptions);
			return get3rdLevelCharacteristics(fidelityOptions) > 0 ? ch2 + 1 : ch2;
		}
	}
	
	@Override
	public final int get2ndLevelEventCode(EventType eventType,
			FidelityOptions fidelityOptions) {
		int ec2 = Constants.NOT_FOUND;
		if(fidelityOptions.isStrict()) {			
			switch(eventType) {
			case ATTRIBUTE_XSI_TYPE:
				ec2 += this.isTypeCastable ? 1 : 0; // AT(xsi:type)
				break;
			case ATTRIBUTE_XSI_NIL:
				if(isNillable) {
					ec2 += this.isTypeCastable ? 1 : 0; // AT(xsi:type)
					ec2++; // AT(xsi:nil)
				}
				break;
			default:
				// no action	
			}
		} else {
			switch(eventType) {
			case END_ELEMENT_UNDECLARED:
				ec2 += this.hasEndElement ? 0 : 1; // EE?
				break;
			case ATTRIBUTE_XSI_TYPE:
				ec2 += this.hasEndElement ? 0 : 1; // EE?
				ec2 += 1; // AT(xsi:type)
				break;
			case ATTRIBUTE_XSI_NIL:
				ec2 += this.hasEndElement ? 0 : 1; // EE?
				ec2 += 2; // AT(xsi:type), AT(xsi:nil)
				break;
			case ATTRIBUTE_GENERIC_UNDECLARED:
				ec2 += this.hasEndElement ? 0 : 1; // EE?
				ec2 += 3; // AT(xsi:type), AT(xsi:nil), AT(*)
				break;
			case ATTRIBUTE_INVALID_VALUE:
				ec2 += this.hasEndElement ? 0 : 1; // EE?
				ec2 += 4; // AT(xsi:type), AT(xsi:nil), AT(*), AT(invalid)
				break;
			case NAMESPACE_DECLARATION:
				if(fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_PREFIX)) {
					ec2 += this.hasEndElement ? 0 : 1; // EE?
					ec2 += 4; // AT(xsi:type), AT(xsi:nil), AT(*), AT(invalid)
					ec2++; // NS
				}
				break;
			case SELF_CONTAINED:
				if(fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_SC)) {
					ec2 += this.hasEndElement ? 0 : 1; // EE?
					ec2 += 4; // AT(xsi:type), AT(xsi:nil), AT(*), AT(invalid)
					ec2 += fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_PREFIX) ? 1 : 0; // NS
					ec2++; // SC
				}
				break;
			case START_ELEMENT_GENERIC_UNDECLARED:
				ec2 += this.hasEndElement ? 0 : 1; // EE?
				ec2 += 4; // AT(xsi:type), AT(xsi:nil), AT(*), AT(invalid)
				ec2 += fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_PREFIX) ? 1 : 0; // NS
				ec2 += fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_SC) ? 1 : 0; // SC
				ec2++; // SE(*)
				break;
			case CHARACTERS_GENERIC_UNDECLARED:
				ec2 += this.hasEndElement ? 0 : 1; // EE?
				ec2 += 4; // AT(xsi:type), AT(xsi:nil), AT(*), AT(invalid)
				ec2 += fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_PREFIX) ? 1 : 0; // NS
				ec2 += fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_SC) ? 1 : 0; // SC
				ec2 += 2; // SE(*), CH(*)
				break;
			case ENTITY_REFERENCE:
				if(fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_DTD)) {
					ec2 += this.hasEndElement ? 0 : 1; // EE?
					ec2 += 4; // AT(xsi:type), AT(xsi:nil), AT(*), AT(invalid)
					ec2 += fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_PREFIX) ? 1 : 0; // NS
					ec2 += fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_SC) ? 1 : 0; // SC
					ec2 += 2; // SE(*), CH(*)
					ec2 += fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_DTD) ? 1 : 0; // ER
				} 
				break;
			default:
				// no action	
			}
		}
		
		return ec2;
	}
	
	
	private static final EventType[] POSSIBLE_EVENTS = {EventType.END_ELEMENT_UNDECLARED, EventType.ATTRIBUTE_XSI_TYPE, EventType.ATTRIBUTE_XSI_NIL,
		EventType.ATTRIBUTE_GENERIC_UNDECLARED, EventType.ATTRIBUTE_INVALID_VALUE, EventType.NAMESPACE_DECLARATION,
		EventType.SELF_CONTAINED, EventType.START_ELEMENT_GENERIC_UNDECLARED, EventType.CHARACTERS_GENERIC_UNDECLARED,
		EventType.ENTITY_REFERENCE};
	
	@Override
	public final EventType get2ndLevelEventType(int eventCode2,
			FidelityOptions fidelityOptions) {
		if(fidelityOptions.isStrict()) {
			switch(eventCode2) {
			case 0:
				if(this.isTypeCastable) {
					return EventType.ATTRIBUTE_XSI_TYPE;
				} else {
					return EventType.ATTRIBUTE_XSI_NIL;
				}
				// break;
			case 1:
				if(this.isTypeCastable) {
					return EventType.ATTRIBUTE_XSI_NIL;
				} 
			}
		} else {
			assert(eventCode2 >= 0);			
			if(this.hasEndElement) {
				eventCode2++;
			}
			switch(eventCode2) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
				return POSSIBLE_EVENTS[eventCode2];
			default:
				// NS?, SC?, SE(*), CH(*), ER
				if (!fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_PREFIX) ) {
					eventCode2++;
				}
				switch(eventCode2) {
				case 5:
					return POSSIBLE_EVENTS[eventCode2];
				default:
					if (!fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_SC) ) {
						eventCode2++;
					}
					switch(eventCode2) {
					case 6:
					case 7:
					case 8:
					case 9:
						return POSSIBLE_EVENTS[eventCode2]; // SC, SE(*), CH(*), ER
					}
				}
				
			}
		}
		return null;
	}

	@Override
	public final boolean hasSecondOrThirdLevel(FidelityOptions fidelityOptions) {
		// Note: in non-STRICT xsi:nil and type is always present
		return (isTypeCastable || isNillable || !fidelityOptions.isStrict());
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if (obj instanceof SchemaInformedFirstStartTag) {
			SchemaInformedFirstStartTag other = (SchemaInformedFirstStartTag) obj;
			if (this.isTypeCastable == other.isTypeCastable
					&& this.isNillable == other.isNillable
					&& super.equals(other)) {
				return true;
			}
		}

		return false;
	}
	
	@Override
	public int hashCode() {
		return (isTypeCastable ? 1 : 0) ^ (isNillable ? 1 : 0) ^ super.hashCode();
	}


	public String toString() {
		String s = "First";

		if (this.isTypeCastable) {
			s += "(xsi:type)";
		}
		if (this.isNillable) {
			s += "(xsi:nil)";
		}

		return s + super.toString();
	}

}
