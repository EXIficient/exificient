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

import javax.xml.namespace.QName;

import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.grammar.EventInformation;
import com.siemens.ct.exi.grammar.EventTypeInformation;
import com.siemens.ct.exi.grammar.event.EventType;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.7
 */

public class SchemaInformedFirstStartTag extends SchemaInformedStartTag
		implements SchemaInformedFirstStartTagRule {

	private static final long serialVersionUID = -6071059051303822226L;

	// subtype (xsi:type) OR nillable (xsi:nill) ?
	protected boolean isTypeCastable = false;
	protected boolean isNillable = false;
	protected SchemaInformedFirstStartTagRule typeEmpty;

	protected QName typeName = null;

	public SchemaInformedFirstStartTag(SchemaInformedRule elementContent2) {
		super(elementContent2);
	}

	public SchemaInformedFirstStartTag(SchemaInformedStartTagRule startTag) {
		this((SchemaInformedRule) startTag.getElementContentRule());

		// clone top level
		for (int i = 0; i < startTag.getNumberOfEvents(); i++) {
			EventInformation ei = startTag.lookFor(i);
			// remove self-reference
			Rule next = ei.next;
			if (next == startTag) {
				next = this;
			}
			this.addRule(ei.event, next);
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

	public void setTypeEmpty(SchemaInformedFirstStartTagRule typeEmpty) {
		this.typeEmpty = typeEmpty;
	}

	public SchemaInformedFirstStartTagRule getTypeEmpty() {
		return this.typeEmpty;
	}

	@Override
	protected void buildEvents2(FidelityOptions fidelityOptions) {
		int eventCode2 = 0;

		if (fidelityOptions.isStrict()) {
			// xsi:type
			if (isTypeCastable) {
				events2.add(new EventTypeInformation(
						EventType.ATTRIBUTE_XSI_TYPE, eventCode2++));
			}
			// xsi:nil
			if (isNillable) {
				events2.add(new EventTypeInformation(
						EventType.ATTRIBUTE_XSI_NIL, eventCode2++));
			}
		} else {
			// EE on second level necessary ?
			if (!hasEndElement) {
				events2.add(new EventTypeInformation(
						EventType.END_ELEMENT_UNDECLARED, eventCode2++));
			}
			// xsi:type & xsi:nil (for first startTag rule)
			events2.add(new EventTypeInformation(EventType.ATTRIBUTE_XSI_TYPE,
					eventCode2++));
			events2.add(new EventTypeInformation(EventType.ATTRIBUTE_XSI_NIL,
					eventCode2++));
			// AT(*) & AT[schema-invalid]
			events2.add(new EventTypeInformation(
					EventType.ATTRIBUTE_GENERIC_UNDECLARED, eventCode2++));
			events2.add(new EventTypeInformation(
					EventType.ATTRIBUTE_INVALID_VALUE, eventCode2++));
			// NS, SC (for first startTag rule)
			if (fidelityOptions
					.isFidelityEnabled(FidelityOptions.FEATURE_PREFIX)) {
				events2.add(new EventTypeInformation(
						EventType.NAMESPACE_DECLARATION, eventCode2++));
			}
			if (fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_SC)) {
				events2.add(new EventTypeInformation(EventType.SELF_CONTAINED,
						eventCode2++));
			}
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
	public final boolean hasSecondOrThirdLevel(FidelityOptions fidelityOptions) {
		// Note: in non-STRICT xsi:nil and type is always present
		return (isTypeCastable || isNillable || !fidelityOptions.isStrict());
//		if (fidelityOptions.isStrict()) {
//			return (isTypeCastable || isNillable);
//		} else {
//			return true;
//		}
	}

	@Override
	public boolean equals(Object obj) {
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
	public SchemaInformedFirstStartTag clone() {
		SchemaInformedFirstStartTag clone = new SchemaInformedFirstStartTag(
				this.elementContent2);

		// duplicate top level
		for (int i = 0; i < getNumberOfEvents(); i++) {
			EventInformation ei = lookFor(i);
			// remove self-reference
			Rule next = ei.next;
			if (next == this) {
				next = clone;
			}
			clone.addRule(ei.event, next);
		}

		// nillable and type
		clone.setTypeCastable(this.isTypeCastable);
		clone.setTypeEmpty(this.typeEmpty);
		clone.setNillable(this.isNillable);
		clone.setTypeName(this.typeName);

		return clone;
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
