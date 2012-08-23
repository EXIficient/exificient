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

package com.siemens.ct.exi.datatype.strings;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.siemens.ct.exi.context.EncoderContext;
import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.util.MethodsBag;
import com.siemens.ct.exi.values.StringValue;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9
 */

public class StringEncoderImpl implements StringEncoder {

	// strings (all)
	protected Map<String, ValueContainer> stringValues;

	// indicate whether local value partitions are used
	protected boolean localValuePartitions;
	
	public StringEncoderImpl(boolean localValuePartitions) {
		this.localValuePartitions = localValuePartitions;
		stringValues = new HashMap<String, ValueContainer>();
	}

	public void writeValue(EncoderContext coder, QNameContext context,
			EncoderChannel valueChannel, String value) throws IOException {

		ValueContainer vc = stringValues.get(value);

		if (vc != null) {
			// hit
			if (localValuePartitions &&  vc.context.equals(context)) {
				/*
				 * local value hit ==> is represented as zero (0) encoded as an
				 * Unsigned Integer followed by the compact identifier of the
				 * string value in the "local" value partition
				 */
				valueChannel.encodeUnsignedInteger(0);
				int n = MethodsBag.getCodingLength(coder
						.getNumberOfStringValues(context));
				valueChannel.encodeNBitUnsignedInteger(vc.localValueID, n);
			} else {
				/*
				 * global value hit ==> value is represented as one (1) encoded
				 * as an Unsigned Integer followed by the compact identifier of
				 * the String value in the global value partition.
				 */
				valueChannel.encodeUnsignedInteger(1);
				// global value size
				int n = MethodsBag.getCodingLength(stringValues.size());
				valueChannel.encodeNBitUnsignedInteger(vc.globalValueID, n);
			}
		} else {
			/*
			 * miss [not found in local nor in global value partition] ==>
			 * string literal is encoded as a String with the length incremented
			 * by two.
			 */
			final int L = value.codePointCount(0, value.length());
			valueChannel.encodeUnsignedInteger(L + 2);
			/*
			 * If length L is greater than zero the string S is added
			 */
			if (L > 0) {
				valueChannel.encodeStringOnly(value);
				// After encoding the string value, it is added to both the
				// associated "local" value string table partition and the
				// global value string table partition.
				addValue(coder, context, value);
			}
		}

	}

	// Restricted char set
	public boolean isStringHit(String value) throws IOException {
		return (stringValues.get(value) != null);
	}

	public void addValue(EncoderContext coder, QNameContext context,
			String value) {
		assert (!stringValues.containsKey(value));

		ValueContainer vc = new ValueContainer(value, context,
				coder.getNumberOfStringValues(context), stringValues.size());

		// global context
		stringValues.put(value, vc);

		// local context
		if(localValuePartitions) {
			coder.addStringValue(context, new StringValue(value));
		}
	}

	public void clear() {
		stringValues.clear();
	}

	static class ValueContainer {

		public final String value;
		public final QNameContext context;
		public final int localValueID;
		public final int globalValueID;

		public ValueContainer(String value, QNameContext context, int localValueID,
				int globalValueID) {
			this.value = value;
			this.context = context;
			this.localValueID = localValueID;
			this.globalValueID = globalValueID;
		}

		@Override
		public String toString() {
			return "['" + value + "', " + context + "," + localValueID + "," + globalValueID
					+ "]";
		}
	}

}
