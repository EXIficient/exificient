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

package com.siemens.ct.exi.core.container;

import java.util.ArrayList;
import java.util.List;

import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.values.Value;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.6
 */

public class Context {
	
	final List<Value> values;
	final List<Datatype> valueDatatypes;
	
	public Context() {
		values = new ArrayList<Value>();
		valueDatatypes = new ArrayList<Datatype>();
	}
	
	public void addValue(Value value, Datatype datatype) {
		values.add(value);
		valueDatatypes.add(datatype);
	}
	
	public List<Value> getValues() {
		return values;
	}
	
	public List<Datatype> getValueDatatypes() {
		return valueDatatypes;
	}
}
