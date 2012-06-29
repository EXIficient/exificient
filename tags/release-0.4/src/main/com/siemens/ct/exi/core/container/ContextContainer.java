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

package com.siemens.ct.exi.core.container;

import java.util.ArrayList;
import java.util.List;

import com.siemens.ct.exi.datatype.Datatype;

public class ContextContainer {
	
	final List<String> values;
	final List<Datatype> valueDatatypes;
	
	public ContextContainer() {
		values = new ArrayList<String>();
		valueDatatypes = new ArrayList<Datatype>();
	}
	
	public void addValue(String value, Datatype datatype) {
		values.add(value);
		valueDatatypes.add(datatype);
	}
	
	public List<String> getValues() {
		return values;
	}
	
	public List<Datatype> getValueDatatypes() {
		return valueDatatypes;
	}
}