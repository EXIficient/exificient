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

package com.siemens.ct.exi.datatype;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.Constants;
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
 * @version 0.5
 */

public class ListDatatype extends AbstractDatatype {

	private static final long serialVersionUID = 1329094446386886766L;

	private Datatype listDatatype;

	protected List<Value> values;

	public ListDatatype(Datatype listDatatype, QName schemaType) {
		super(BuiltInType.LIST, schemaType);

		values = new ArrayList<Value>();

		this.rcs = listDatatype.getRestrictedCharacterSet();

		if (listDatatype.getBuiltInType() == BuiltInType.LIST) {
			throw new IllegalArgumentException();
		}

		this.listDatatype = listDatatype;
	}

	public Datatype getListDatatype() {
		return listDatatype;
	}

	public boolean isValid(String value) {
		// iterate over all tokens
		StringTokenizer st = new StringTokenizer(value,
				Constants.XSD_LIST_DELIM);
		values.clear();

		while (st.hasMoreTokens()) {
			if (listDatatype.isValid(st.nextToken())) {
				values.add(listDatatype.getValue());
			} else {
				// invalid --> abort process
				return false;
			}
		}
		return true;
	}
	
	public boolean isValid(Value value) {
		if (value instanceof ListValue) {
			values = ((ListValue) value).toValues();
			return true;			
		} else {
			return false;
		}
	}
	

	public Value getValue() {
		return new ListValue(values);
	}

	@Override
	public boolean isValidRCS(String value) {
		return super.isValidRCS(value);
	}

	public void writeValue(EncoderChannel valueChannel,
			StringEncoder stringEncoder, QName context) throws IOException {
		
		// length prefixed sequence of values
		valueChannel.encodeUnsignedInteger(values.size());

		// iterate over all tokens
		for(Value v : values) {
			boolean valid = listDatatype.isValid(v);
			if (!valid){
				throw new RuntimeException("ListValue is not valid, " + v);
			}
			listDatatype.writeValue(valueChannel, stringEncoder, context);
		}
	}

	@Override
	public void writeValueRCS(RestrictedCharacterSetDatatype rcsEncoder,
			EncoderChannel valueChannel, StringEncoder stringEncoder,
			QName context) throws IOException {
		
		StringTokenizer st = new StringTokenizer(this.lastRCSValue,
				Constants.XSD_LIST_DELIM);
		
		// length prefixed sequence of values
		valueChannel.encodeUnsignedInteger(st.countTokens());

		// iterate over all tokens
		rcsEncoder.setRestrictedCharacterSet(rcs);

		while (st.hasMoreTokens()) {
			rcsEncoder.isValid(st.nextToken());
			rcsEncoder.writeValue(valueChannel, stringEncoder, context);
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

		return new ListValue(values);
	}

	@Override
	public Value readValueRCS(RestrictedCharacterSetDatatype rcsDecoder,
			DecoderChannel valueChannel, StringDecoder stringDecoder,
			QName context) throws IOException {
		int len = valueChannel.decodeUnsignedInteger();
		List<Value> values = new ArrayList<Value>(len);

		rcsDecoder.setRestrictedCharacterSet(rcs);

		for (int i = 0; i < len; i++) {
			values.add(rcsDecoder.readValue(valueChannel, stringDecoder,
					context));
		}

		return new ListValue(values);
	}
}