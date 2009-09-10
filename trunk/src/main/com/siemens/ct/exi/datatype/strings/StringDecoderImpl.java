package com.siemens.ct.exi.datatype.strings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.siemens.ct.exi.core.Context;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.util.MethodsBag;

public class StringDecoderImpl implements StringDecoder {

	// global values (all)
	List<char[]> globalValues;

	// local values (per context)
	protected Map<Context, List<char[]>> localValues;

	public StringDecoderImpl() {
		globalValues = new ArrayList<char[]>();
		localValues = new HashMap<Context, List<char[]>>();
	}

	public char[] readValue(Context context, DecoderChannel valueChannel)
			throws IOException {
		char[] value;

		int i = valueChannel.decodeUnsignedInteger();

		if (i == 0) {
			// local value partition
			value = readValueLocalHit(context, valueChannel);
		} else if (i == 1) {
			// found in global value partition
			value = readValueGlobalHit(context, valueChannel);
		} else {
			// not found in global value (and local value) partition
			// ==> string literal is encoded as a String with the length
			// incremented by two.
			value = valueChannel.decodeStringOnly(i - 2);
			// After encoding the string value, it is added to both the
			// associated "local" value string table partition and the global
			// value string table partition.
			addValue(context, value);
		}

		// System.out.println("value=" + new String(value));

		assert (value != null);
		return value;
	}

	public char[] readValueLocalHit(Context context,
			DecoderChannel valueChannel) throws IOException {
		List<char[]> localChars = localValues.get(context);
		int n = MethodsBag.getCodingLength(localChars.size());
		int localID = valueChannel.decodeNBitUnsignedInteger(n);
		return localChars.get(localID);
	}
	
	public char[] readValueGlobalHit(Context context,
			DecoderChannel valueChannel) throws IOException {
		int n = MethodsBag.getCodingLength(globalValues.size());
		int globalID = valueChannel.decodeNBitUnsignedInteger(n);
		return globalValues.get(globalID);
	}

	public void addValue(Context context, char[] value) {
		// global
		assert (!globalValues.contains(value));
		globalValues.add(value);
		// local
		List<char[]> lvs = localValues.get(context);
		if (lvs == null) {
			lvs = new ArrayList<char[]>();
			localValues.put(context, lvs);
		}
		assert (!lvs.contains(value));
		lvs.add(value);
	}

	public void clear() {
		globalValues.clear();
		localValues.clear();
	}

}
