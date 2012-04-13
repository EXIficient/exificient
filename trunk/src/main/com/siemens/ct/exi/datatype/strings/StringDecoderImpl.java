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
import java.util.List;

import com.siemens.ct.exi.context.DecoderContext;
import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.util.MethodsBag;
import com.siemens.ct.exi.values.StringValue;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.8
 */

public class StringDecoderImpl implements StringDecoder {

	// global values (all)
	protected List<StringValue> globalValues;

	// indicate whether local value partitions are used
	protected boolean localValuePartitions;

	public StringDecoderImpl(boolean localValuePartitions) {
		this.localValuePartitions = localValuePartitions;
		globalValues = new ArrayList<StringValue>();
	}

	public StringValue readValue(DecoderContext coder, QNameContext context,
			DecoderChannel valueChannel) throws IOException {
		StringValue value;

		int i = valueChannel.decodeUnsignedInteger();

		switch (i) {
		case 0:
			// local value partition
			if (localValuePartitions) {
				value = this.readValueLocalHit(coder, context, valueChannel);
			} else {
				throw new IOException(
						"EXI stream contains local-value hit even though profile options indicate otherwise.");
			}
			break;
		case 1:
			// found in global value partition
			value = readValueGlobalHit(valueChannel);
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
				// associated "local" value string table partition and the
				// global
				// value string table partition.
				// addValue(context, value);
				this.addValue(coder, context, value);
			} else {
				value = StringCoder.EMPTY_STRING_VALUE;
			}
			break;
		}

		// System.out.println("value=" + new String(value));

		assert (value != null);
		return value;
	}

	public StringValue readValueLocalHit(DecoderContext coder,
			QNameContext context, DecoderChannel valueChannel)
			throws IOException {
		assert (localValuePartitions);
		int n = MethodsBag.getCodingLength(coder
				.getNumberOfStringValues(context));
		int localID = valueChannel.decodeNBitUnsignedInteger(n);
		return coder.getStringValue(context, localID);
	}

	public StringValue readValueGlobalHit(DecoderChannel valueChannel)
			throws IOException {
		int n = MethodsBag.getCodingLength(globalValues.size());
		int globalID = valueChannel.decodeNBitUnsignedInteger(n);
		return globalValues.get(globalID);
	}

	public void addValue(DecoderContext coder, QNameContext context,
			StringValue value) {
		// global
		assert (!globalValues.contains(value));
		globalValues.add(value);
		// local
		if (localValuePartitions) {
			coder.addStringValue(context, value);
		}
	}

	public void clear() {
		globalValues.clear();
	}

}
