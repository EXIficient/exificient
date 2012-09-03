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

package com.siemens.ct.exi.values;

import java.util.StringTokenizer;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.datatype.Datatype;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.1
 */

public class ListValue extends AbstractValue {

	private static final long serialVersionUID = -8991265913614252729L;

	protected final Value[] values;
	protected final Datatype listDatatype;

	public ListValue(Value[] values, Datatype listDatatype) {
		super(ValueType.LIST);
		this.values = values;
		this.listDatatype = listDatatype;
	}

	public Value[] toValues() {
		return values;
	}
	
	public Datatype getListDatatype() {
		return listDatatype;
	}

	public int getCharactersLength() {
		if (slen == -1) {
			slen = values.length > 0 ? (values.length - 1) : 0; // (n-1)
																// delimiters
			int vlen = values.length;
			for (int i = 0; i < vlen; i++) {
				slen += values[i].getCharactersLength();
			}
		}
		return slen;
	}

	public void getCharacters(char[] cbuffer, int offset) {
		if (values.length > 0) {
			// fill buffer (except last item)
			Value iVal;
			int vlenMinus1 = values.length - 1;
			for (int i = 0; i < vlenMinus1; i++) {
				iVal = values[i];
				iVal.getCharacters(cbuffer, offset);
				offset += iVal.getCharactersLength();
				cbuffer[offset++] = Constants.XSD_LIST_DELIM_CHAR;
			}

			// last item (no delimiter)
			iVal = values[vlenMinus1];
			iVal.getCharacters(cbuffer, offset);
		}
	}

	
	public static ListValue parse(String value, Datatype listDatatype) {
		// iterate over all tokens
		StringTokenizer st = new StringTokenizer(value);
		Value[] values = new Value[st.countTokens()];
		int index = 0;
		while (st.hasMoreTokens()) {
			Value nextToken = new StringValue(st.nextToken());
			if (listDatatype.isValid(nextToken)) {
				values[index++] = nextToken;
			} else {
				// invalid --> abort process
				return null;
			}
		}
		
		return new ListValue(values, listDatatype);
	}
	

	
	
	
	protected final boolean _equals(ListValue o) {
		// datatype
		if ( listDatatype.getBuiltInType() != o.listDatatype.getBuiltInType() ) {
			return false;
		}
		// values
		if (values.length == o.values.length) {
			for (int i = 0; i < values.length; i++) {
				if (!values[i].equals(o.values[i])) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (o instanceof ListValue) {
			return _equals((ListValue) o);
		} else {
			ListValue lv = ListValue.parse(o.toString(), this.listDatatype);
			return lv == null ? false : _equals(lv);
		}
	}
	
	@Override
	public int hashCode() {
		int hc = 0;
		for(Value val : values) {
			hc = (hc * 31) ^ val.hashCode();
		}
		return hc;
	}

}
