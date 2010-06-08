package com.siemens.ct.exi.datatype.strings;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.util.MethodsBag;

public class StringEncoderImpl implements StringEncoder {

	// strings (all)
	protected Map<String, ValueContainer> stringValues;

	// localValue counter
	protected Map<QName, Integer> localValueSize;

	public StringEncoderImpl() {
		stringValues = new HashMap<String, ValueContainer>();
		localValueSize = new HashMap<QName, Integer>();
	}

	public void writeValue(QName context, EncoderChannel valueChannel,
			String value) throws IOException {

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
				int n = MethodsBag.getCodingLength(localValueSize.get(context));
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
			int L = value.length();
			valueChannel.encodeUnsignedInteger(L + 2);
			/*
			 * If length L is greater than zero the string S is added 
			 */
			if (L > 0) {
				valueChannel.encodeStringOnly(value);
				// After encoding the string value, it is added to both the
				// associated "local" value string table partition and the
				// global value string table partition.
				addValue(context, value);	
			}
		}
	}

	// Restricted char set
	public boolean isStringHit(QName context, String value) throws IOException {
		return (stringValues.get(value) != null);
	}

	public void addValue(QName context, String value) {
		assert (!stringValues.containsKey(value));
		int globalID = stringValues.size();

		// local value counter
		int localValCnt = updateLocalValueCount(context);

		ValueContainer vc = new ValueContainer(context, localValCnt, globalID);
		stringValues.put(value, vc);
	}
	
	/**
	 * Returns next localValueID
	 * 
	 * @param context
	 * @return
	 */
	protected final int updateLocalValueCount(QName context) {
		// local value count
		Integer cnt = localValueSize.get(context);
		if (cnt == null) {
			cnt = 0;
		}
		localValueSize.put(context, cnt + 1);
		
		return cnt;
	}

	public void clear() {
		stringValues.clear();
		localValueSize.clear();
	}
	
	class ValueContainer {
		
		public final QName context;
		public final int localValueID;
		public final int globalValueID;

		public ValueContainer(QName context, int localValueID, int globalValueID) {
			this.context = context;
			this.localValueID = localValueID;
			this.globalValueID = globalValueID;
		}
		
		@Override
		public String toString() {
			return "[" + context +"," + localValueID+ "," + globalValueID + "]";
		}
	}

}
