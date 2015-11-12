/*
 * Copyright (c) 2007-2015 Siemens AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */

package com.siemens.ct.exi.values;

import java.io.IOException;
import java.util.StringTokenizer;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.datatype.Datatype;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.5
 */

public class ListValue extends AbstractValue {

	private static final long serialVersionUID = -8991265913614252729L;

	protected final Value[] values;
	protected final Datatype listDatatype;
	protected final int numberOfValues;

	public ListValue(Value[] values, Datatype listDatatype) {
		super(ValueType.LIST);
		this.values = values;
		this.numberOfValues = values.length;
		this.listDatatype = listDatatype;
	}
	
	public int getNumberOfValues() {
		return numberOfValues;
	}

	public Value[] toValues() throws IOException {		
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
