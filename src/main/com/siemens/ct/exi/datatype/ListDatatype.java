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

package com.siemens.ct.exi.datatype;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

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
 * @version 0.8
 */

public class ListDatatype extends AbstractDatatype {

	private static final long serialVersionUID = 1329094446386886766L;

	private Datatype listDatatype;

	ListValue listValues;

	public ListDatatype(Datatype listDatatype, QName schemaType) {
		super(BuiltInType.LIST, schemaType);

		this.rcs = listDatatype.getRestrictedCharacterSet();

		if (listDatatype.getBuiltInType() == BuiltInType.LIST) {
			throw new IllegalArgumentException();
		}

		this.listDatatype = listDatatype;
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
			if (this.listDatatype.getBuiltInType() == lv.getListDatatype().getBuiltInType()) {
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

	public void writeValue(EncoderChannel valueChannel,
			StringEncoder stringEncoder, QName context) throws IOException {

		// length prefixed sequence of values
		List<Value> values = listValues.toValues();
		valueChannel.encodeUnsignedInteger(values.size());

		// iterate over all tokens
		for (Value v : values) {
			boolean valid = listDatatype.isValid(v);
			if (!valid) {
				throw new RuntimeException("ListValue is not valid, " + v);
			}
			listDatatype.writeValue(valueChannel, stringEncoder, context);
		}
	}

	public Value readValue(DecoderChannel valueChannel,
			StringDecoder stringDecoder, QName context) throws IOException {

		int len = valueChannel.decodeUnsignedInteger();
		List<Value> values = new ArrayList<Value>(len);

		for (int i = 0; i < len; i++) {
			values.add(listDatatype.readValue(valueChannel, stringDecoder,
					context));
		}

		return new ListValue(values, listDatatype);
	}
}