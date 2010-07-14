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

package com.siemens.ct.exi.grammar.event;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.types.BuiltIn;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.5
 */

public class Attribute extends AbstractDatatypeEvent {
	protected QName qname;

	public Attribute(QName qname, QName valueType,
			Datatype datatype) {
		super(EventType.ATTRIBUTE, valueType, datatype);
		this.qname = qname;
	}

	public Attribute(QName qname) {
		this(qname, BuiltIn.DEFAULT_VALUE_NAME,
				BuiltIn.DEFAULT_DATATYPE);
	}
	
	public QName getQName() {
		return this.qname;
	}

	public String toString() {
		return super.toString() + "(" + qname.toString() + ")";
	}

	@Override
	public int hashCode() {
		return qname.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof Attribute) {
			Attribute otherAT = (Attribute) obj;
			return (qname.equals(otherAT.qname));
		} else {
			return false;
		}
	}

}
