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

package com.siemens.ct.exi.grammar.event;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.datatype.Datatype;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.8
 */

public abstract class AbstractDatatypeEvent extends AbstractEvent implements
		DatatypeEvent {

	private static final long serialVersionUID = -975693882715012642L;

	protected final QName valueType;

	protected final Datatype datatype;

	public AbstractDatatypeEvent(EventType eventType, QName valueType,
			Datatype datatype) {
		super(eventType);
		this.valueType = valueType;
		this.datatype = datatype;
	}

	public QName getValueType() {
		return valueType;
	}

	public Datatype getDatatype() {
		return datatype;
	}

	@Override
	public String toString() {
		return super.toString() + "[" + datatype + "]";
	}
}
