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

package com.siemens.ct.exi.datatype.strings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.util.MethodsBag;
import com.siemens.ct.exi.values.StringValue;
import com.siemens.ct.exi.values.Value;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.6
 */

public class StringDecoderImpl implements StringDecoder {

	// global values (all)
	protected List<Value> globalValues;

	// local values (per context)
	protected Map<QName, List<Value>> localValues;
	
	public StringDecoderImpl() {
		globalValues = new ArrayList<Value>();
		localValues = new HashMap<QName, List<Value>>();
	}

	private static final Value EMPTY_STRING_VALUE = new StringValue("");
	
	public Value readValue(QName context, DecoderChannel valueChannel)
			throws IOException {
		Value value;
		
		int i = valueChannel.decodeUnsignedInteger();
		
		switch(i) {
		case 0:
			// local value partition
			value = readValueLocalHit(context, valueChannel);
			break;
		case 1:
			// found in global value partition
			value = readValueGlobalHit(context, valueChannel);
			break;
		default:
			// not found in global value (and local value) partition
			// ==> string literal is encoded as a String with the length
			// incremented by two.
			int L = i - 2;
			/*
			 * If length L is greater than zero the string S is added 
			 */
			if (L > 0) {
				value = new StringValue(valueChannel.decodeStringOnly(L));
				// After encoding the string value, it is added to both the
				// associated "local" value string table partition and the global
				// value string table partition.
				addValue(context, value);	
			} else {
				value = EMPTY_STRING_VALUE;
			}
			break;
		}

		// System.out.println("value=" + new String(value));

		assert (value != null);
		return value;
	}

	public Value readValueLocalHit(QName context,
			DecoderChannel valueChannel) throws IOException {
		List<Value> localChars = localValues.get(context);
		int n = MethodsBag.getCodingLength(localChars.size());
		int localID = valueChannel.decodeNBitUnsignedInteger(n);
		return localChars.get(localID);
	}
	
	public Value readValueGlobalHit(QName context,
			DecoderChannel valueChannel) throws IOException {
		int n = MethodsBag.getCodingLength(globalValues.size());
		int globalID = valueChannel.decodeNBitUnsignedInteger(n);
		return globalValues.get(globalID);
	}

	public void addValue(QName context, Value value) {
		// global
		assert (!globalValues.contains(value));
		globalValues.add(value);
		// local
		// updateLocalValues(context, value);
		List<Value> lvs = localValues.get(context);
		if (lvs == null) {
			lvs = new ArrayList<Value>();
			localValues.put(context, lvs);
		}
		assert (!lvs.contains(value));
		lvs.add(value);
	}
	
//	protected void updateLocalValues(QName context, Value value) {
//		List<Value> lvs = localValues.get(context);
//		if (lvs == null) {
//			lvs = new ArrayList<Value>();
//			localValues.put(context, lvs);
//		}
//		assert (!lvs.contains(value));
//		lvs.add(value);
//	}

	public void clear() {
		globalValues.clear();
		localValues.clear();
	}

}
