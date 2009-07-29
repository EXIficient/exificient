package com.siemens.ct.exi.datatype.strings;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.siemens.ct.exi.core.NameContext;
import com.siemens.ct.exi.core.container.ValueContainer;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.util.MethodsBag;

public class StringEncoderImpl implements StringEncoder {

	// strings
	protected Map<String, ValueContainer> stringValues;
	
	public StringEncoderImpl() {
		stringValues = new HashMap<String, ValueContainer>();
	}
	
	public void writeValueAsString(NameContext context, EncoderChannel valueChannel, String value)
			throws IOException {
		
		ValueContainer vc = stringValues.get(value);

		if (vc != null) {
			// hit
			if (vc.context == context) {
				/*
				 * local value hit ==> is represented as zero (0) encoded as an
				 * Unsigned Integer followed by the compact identifier of the
				 * string value in the "local" value partition
				 */
				valueChannel.encodeUnsignedInteger(0);
				int n = MethodsBag.getCodingLength(context.getLocalValueSize());
				valueChannel.encodeNBitUnsignedInteger(vc.localValueID, n);
				// System.out.println("ST LocalHit = '" + value + "' in " +
				// context);
			} else {
				/*
				 * global value hit ==> value is represented as one (1) encoded
				 * as an Unsigned Integer followed by the compact identifier of
				 * the String value in the global value partition.
				 */
				valueChannel.encodeUnsignedInteger(1);
				int n = MethodsBag.getCodingLength(getGlobalValueSize());
				valueChannel.encodeNBitUnsignedInteger(vc.globalValueID, n);
				// System.out.println("ST GlobalHit = '" + value +
				// "' globalID=" + vc.globalValueID);
			}
		} else {
			/*
			 * miss [not found in local nor in global value partition] ==>
			 * string literal is encoded as a String with the length incremented
			 * by two.
			 */
			// System.out.println("ST Miss \'" + value + "'");
			valueChannel.encodeUnsignedInteger(value.length() + 2);
			valueChannel.encodeStringOnly(value);
			// After encoding the string value, it is added to both the
			// associated "local" value string table partition and the
			// global value string table partition.
			addValue(context, value);
		}
	}

	// Restricted char set
	public boolean isStringHit(NameContext context, String value) throws IOException {
		return (stringValues.get(value) != null);
	}

	public void addValue(NameContext context, String value) {
		assert (!stringValues.containsKey(value));
		int globalID = stringValues.size();
		ValueContainer vc = new ValueContainer(context, globalID);
		stringValues.put(value, vc);
	}

//	public int getLocalValueSize(NameContext context) {
//		return context.localValueSize;
//	}

	public int getGlobalValueSize() {
		return stringValues.size();
	}

	public void clear() {
		stringValues.clear();
	}

}
