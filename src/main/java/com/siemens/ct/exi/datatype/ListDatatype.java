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

package com.siemens.ct.exi.datatype;

import java.io.IOException;

import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.datatype.strings.StringDecoder;
import com.siemens.ct.exi.datatype.strings.StringEncoder;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.types.BuiltInType;
import com.siemens.ct.exi.values.ListValue;
import com.siemens.ct.exi.values.Value;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.6-SNAPSHOT
 */

public class ListDatatype extends AbstractDatatype {

	private static final long serialVersionUID = 1329094446386886766L;

	private Datatype listDatatype;

	ListValue listValues;

	public ListDatatype(Datatype listDatatype, QNameContext schemaType) {
		super(BuiltInType.LIST, schemaType);

		if (listDatatype.getBuiltInType() == BuiltInType.LIST) {
			throw new IllegalArgumentException();
		}

		this.listDatatype = listDatatype;
	}
	
	public DatatypeID getDatatypeID() {
		return listDatatype.getDatatypeID();
	}

	public Datatype getListDatatype() {
		return listDatatype;
	}

	protected boolean isValidString(String value) {
		listValues = ListValue.parse(value, listDatatype);
		return listValues != null;
	}

	public boolean isValid(Value value) {
		if (value instanceof ListValue) {
			ListValue lv = (ListValue) value;
			if (this.listDatatype.getBuiltInType() == lv.getListDatatype()
					.getBuiltInType()) {
				this.listValues = lv;
				return true;
			} else {
				listValues = null;
				return false;
			}
		} else {
			return isValidString(value.toString());
		}
	}

	public void writeValue(QNameContext qnContext, EncoderChannel valueChannel,
			StringEncoder stringEncoder) throws IOException {

		// length prefixed sequence of values
		Value[] values = listValues.toValues();
		valueChannel.encodeUnsignedInteger(values.length);

		// iterate over all tokens
		for (int i = 0; i < values.length; i++) {
			Value v = values[i];
			boolean valid = listDatatype.isValid(v);
			if (!valid) {
				throw new RuntimeException("ListValue is not valid, " + v);
			}
			listDatatype.writeValue(qnContext, valueChannel, stringEncoder);
		}
	}

	public Value readValue(QNameContext qnContext, DecoderChannel valueChannel,
			StringDecoder stringDecoder) throws IOException {

		int len = valueChannel.decodeUnsignedInteger();
		
		Value[]  values = new Value[len];
		for (int i = 0; i < len; i++) {
			values[i] = listDatatype.readValue(qnContext, valueChannel,
					stringDecoder);
		}
		Value retVal = new ListValue(values, listDatatype);

		return retVal;
	}
	
	@Override
	public boolean equals(Object o) {
		if(super.equals(o) && o instanceof ListDatatype ) {
			ListDatatype l = (ListDatatype) o;
			return (this.listDatatype.equals(l.listDatatype));
		}
		return false;
	}
}