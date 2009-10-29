/*
 * Copyright (C) 2007-2009 Siemens AG
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
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20080718
 */

public class ListDatatype extends AbstractDatatype {
	
	private Datatype listDatatype;
	
	protected int numberOfEnumeratedTypes;
	protected String lastValidValue;

	public ListDatatype(Datatype listDatatype) {
		super(BuiltInType.LIST, null);
		
		this.rcs = listDatatype.getRestrictedCharacterSet();

		if (listDatatype.getDefaultBuiltInType() == BuiltInType.LIST) {
			throw new IllegalArgumentException();
		}

		this.listDatatype = listDatatype;
		
		// sResult = new StringBuilder();
	}

	public Datatype getListDatatype() {
		return listDatatype;
	}
	
	public boolean isValid(String value) {
		// iterate over all tokens
		StringTokenizer st = new StringTokenizer(value,
				Constants.XSD_LIST_DELIM);
		numberOfEnumeratedTypes = 0;

		while (st.hasMoreTokens()) {
			if (!listDatatype.isValid(st.nextToken())) {
				// invalid --> abort process
				return false;
			}
			numberOfEnumeratedTypes++;
		}

		lastValidValue = value;
		return true;
	}
	
	@Override
	public boolean isValidRCS(String value) {
		StringTokenizer st = new StringTokenizer(value,
				Constants.XSD_LIST_DELIM);
		numberOfEnumeratedTypes = st.countTokens();
		return super.isValidRCS(value);
	}
	

	public void writeValue(EncoderChannel valueChannel, StringEncoder stringEncoder, QName context)
			throws IOException {
			/*
			 * Needs to check AGAIN & writes to stream
			 */
			// length prefixed sequence of values
			valueChannel.encodeUnsignedInteger(numberOfEnumeratedTypes);

			// iterate over all tokens
			StringTokenizer st = new StringTokenizer(lastValidValue,
					Constants.XSD_LIST_DELIM);

			while (st.hasMoreTokens()) {
				// Note: assumption that is valid (was already checked!)
				//	Nevertheless isValid method needs to be called!
				listDatatype.isValid(st.nextToken());
				listDatatype.writeValue(valueChannel, stringEncoder, context);
			}
	}
	
	@Override
	public void writeValueRCS(RestrictedCharacterSetDatatype rcsEncoder, EncoderChannel valueChannel, StringEncoder stringEncoder, QName context) throws IOException {
		// length prefixed sequence of values
		valueChannel.encodeUnsignedInteger(numberOfEnumeratedTypes);
		
		// iterate over all tokens
		StringTokenizer st = new StringTokenizer(this.lastRCSValue,
				Constants.XSD_LIST_DELIM);

		rcsEncoder.setRestrictedCharacterSet(rcs);
		
		while (st.hasMoreTokens()) {
			rcsEncoder.isValid(st.nextToken());
			rcsEncoder.writeValue(valueChannel, stringEncoder, context);
		}
	}

	public Value readValue(DecoderChannel valueChannel,
			StringDecoder stringDecoder, QName context)
			throws IOException {
		int len = valueChannel.decodeUnsignedInteger();
		

//		char[][] itemValues = new char[len][];
		
//		int stringSize = 0;

		Value[] values = new Value[len];
		
		for (int i = 0; i < len; i++) {
			values[i] = listDatatype.readValue(valueChannel, stringDecoder, context);
//			char[] itemValue  = listDatatype.readValue(valueChannel, stringDecoder, context).toCharacters();
//			itemValues[i] = itemValue;
//			stringSize += itemValue.length + 1;// value & delim
		}
		
		return new ListValue(values);
		
//		char[] ca = new char[stringSize];
//		
//		return getValue(ca , itemValues);
	}
	
//	private static final Value getValue(char[] ca , char[][] itemValues) {
//		int caIndex = 0;
//		for (int i = 0; i < itemValues.length; i++) {
//			char[] itemValue = itemValues[i];
//			System.arraycopy(itemValue, 0, ca, caIndex, itemValue.length);
//			caIndex += itemValue.length;
//			ca[caIndex++] = Constants.XSD_LIST_DELIM_CHAR;
//		}
//
//		return new StringValue(ca);
//	}
	
	@Override
	public Value readValueRCS(RestrictedCharacterSetDatatype rcsDecoder,
			DecoderChannel valueChannel, StringDecoder stringDecoder,
			QName context) throws IOException {
		int len = valueChannel.decodeUnsignedInteger();
		
		rcsDecoder.setRestrictedCharacterSet(rcs);
		
		Value[] values = new Value[len];
		
		for (int i = 0; i < len; i++) {
			values[i] = rcsDecoder.readValue(valueChannel, stringDecoder, context);
		}
		
		return new ListValue(values);
		
		
//		char[][] itemValues = new char[len][];
//		int stringSize = 0;
//
//		for (int i = 0; i < len; i++) {
//			char[] itemValue  = rcsDecoder.readValue(valueChannel, stringDecoder, context).toCharacters();
//			itemValues[i] = itemValue;
//			stringSize += itemValue.length + 1;
//		}
//		
//		char[] ca = new char[stringSize];
//		
//		return getValue(ca , itemValues);
	}
}