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

package com.siemens.ct.exi.core.container;

import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.values.Value;

public class ValueAndDatatype {
	public final Value value;
	public final Datatype datatype;
	
	public ValueAndDatatype(Value value, Datatype datatype) {
		this.value = value;
		this.datatype = datatype;
	}
	
	@Override
	public String toString() {
		return "\"" + value + "\" AS " + datatype;
	}
}
