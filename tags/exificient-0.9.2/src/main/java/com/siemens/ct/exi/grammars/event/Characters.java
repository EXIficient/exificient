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

package com.siemens.ct.exi.grammars.event;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.2
 */

import com.siemens.ct.exi.datatype.Datatype;

public class Characters extends AbstractDatatypeEvent {

	private static final long serialVersionUID = -3710413955014691193L;

	public Characters(Datatype datatype) {
		super(EventType.CHARACTERS, datatype);
	}

	public boolean equals(Object o) {
		if (super.equals(o)) {
			// event-type is ok already
			Characters ch = (Characters) o;
//			return (this.valueType.equals(ch.valueType) && this.datatype
//					.getBuiltInType().equals(ch.getDatatype().getBuiltInType()));
			return this.datatype
					.getBuiltInType().equals(ch.getDatatype().getBuiltInType());
		} else {
			return false;
		}
	}
}
