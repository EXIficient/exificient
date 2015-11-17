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

package com.siemens.ct.exi.datatype.strings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.util.MethodsBag;
import com.siemens.ct.exi.values.StringValue;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.6-SNAPSHOT
 */

public class StringDecoderImpl  extends AbstractStringCoder implements StringDecoder {

	// global values (all)
	protected List<StringValue> globalValues;

	public StringDecoderImpl(boolean localValuePartitions) {
		this(localValuePartitions, DEFAULT_INITIAL_QNAME_LISTS);
	}
	
	public StringDecoderImpl(boolean localValuePartitions, int initialQNameLists) {
		super(localValuePartitions, initialQNameLists);
		globalValues = new ArrayList<StringValue>();
	}

	public StringValue readValue(QNameContext context,
			DecoderChannel valueChannel) throws IOException {
		StringValue value;

		int i = valueChannel.decodeUnsignedInteger();

		switch (i) {
		case 0:
			// local value partition
			if (localValuePartitions) {
				value = this.readValueLocalHit(context, valueChannel);
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
				this.addValue(context, value);
			} else {
				value = StringCoder.EMPTY_STRING_VALUE;
			}
			break;
		}

		// System.out.println("value=" + new String(value));

		assert (value != null);
		return value;
	}

	public StringValue readValueLocalHit(
			QNameContext qnc, DecoderChannel valueChannel)
			throws IOException {
		assert (localValuePartitions);
		int n = MethodsBag.getCodingLength(getNumberOfStringValues(qnc));
		int localID = valueChannel.decodeNBitUnsignedInteger(n);
		List<StringValue> lvs = localValues.get(qnc);
		assert(lvs != null);
		assert(localID < lvs.size());
		return lvs.get(localID);
	}

	public final StringValue readValueGlobalHit(DecoderChannel valueChannel)
			throws IOException {
		int numberBitsGlobal = MethodsBag.getCodingLength(globalValues.size());
		int globalID = valueChannel.decodeNBitUnsignedInteger(numberBitsGlobal);
		return globalValues.get(globalID);
	}

	public void addValue(QNameContext qnc,
			StringValue value) {
		// global
		assert (!globalValues.contains(value));
		globalValues.add(value);
		
		// local
		this.addLocalValue(qnc, value);
	}

	public void clear() {
		super.clear();
		globalValues.clear();
	}


}
