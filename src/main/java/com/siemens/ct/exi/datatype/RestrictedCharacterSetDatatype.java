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

package com.siemens.ct.exi.datatype;

import java.io.IOException;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.datatype.charset.RestrictedCharacterSet;
import com.siemens.ct.exi.datatype.strings.StringCoder;
import com.siemens.ct.exi.datatype.strings.StringDecoder;
import com.siemens.ct.exi.datatype.strings.StringEncoder;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.types.BuiltInType;
import com.siemens.ct.exi.values.StringValue;
import com.siemens.ct.exi.values.Value;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.6-SNAPSHOT
 */

public class RestrictedCharacterSetDatatype extends AbstractDatatype {

	private static final long serialVersionUID = -6098764255799006920L;

	protected String lastValidValue;
	protected RestrictedCharacterSet rcs;

	public RestrictedCharacterSetDatatype(RestrictedCharacterSet rcs,
			QNameContext schemaType) {
		// default whiteSpace facet for string is preserve
		this(rcs, schemaType, WhiteSpace.preserve);
	}
	
	public RestrictedCharacterSetDatatype(RestrictedCharacterSet rcs,
			QNameContext schemaType, WhiteSpace whiteSpace) {
		super(BuiltInType.RCS_STRING, schemaType);
		this.rcs = rcs;
		this.whiteSpace = whiteSpace;
	}
	
	public RestrictedCharacterSet getRestrictedCharacterSet() {
		return this.rcs;
	}
	
	public DatatypeID getDatatypeID() {
		return DatatypeID.exi_string;
	}

	public boolean isValid(Value value) {
		// Note: no validity check needed since any char-sequence can be encoded
		// due to fallback mechanism
		lastValidValue = value.toString();
		return true;
	}
	
	public void writeValue(QNameContext qnContext, EncoderChannel valueChannel,
			StringEncoder stringEncoder) throws IOException {
		if (stringEncoder.isStringHit(lastValidValue)) {
			stringEncoder.writeValue(qnContext, valueChannel, lastValidValue);
		} else {
			// NO local or global value hit
			// string-table miss ==> restricted character
			// string literal is encoded as a String with the length
			// incremented by two.
			final int L = lastValidValue.length();

			valueChannel.encodeUnsignedInteger(L + 2);

			/*
			 * If length L is greater than zero the string S is added
			 */
			if (L > 0) {
				// number of bits
				int numberOfBits = rcs.getCodingLength();

				for (int i = 0; i < L; i++) {
					int codePoint = lastValidValue.codePointAt(i);
					int code = rcs.getCode(codePoint);
					if (code == Constants.NOT_FOUND) {
						// indicate deviation
						valueChannel.encodeNBitUnsignedInteger(rcs.size(),
								numberOfBits);

						valueChannel.encodeUnsignedInteger(codePoint);
					} else {
						valueChannel.encodeNBitUnsignedInteger(code,
								numberOfBits);
					}
				}

				// After encoding the string value, it is added to both the
				// associated "local" value string table partition and the
				// global value string table partition.
				stringEncoder.addValue(qnContext, lastValidValue);
			}
		}
	}

	public Value readValue(QNameContext qnContext, DecoderChannel valueChannel,
			StringDecoder stringDecoder) throws IOException {

		StringValue value;

		int i = valueChannel.decodeUnsignedInteger();

		if (i == 0) {
			// local value partition
			value = stringDecoder.readValueLocalHit(qnContext, valueChannel);
		} else if (i == 1) {
			// found in global value partition
			value = stringDecoder.readValueGlobalHit(valueChannel);
		} else {
			// not found in global value (and local value) partition
			// ==> restricted character string literal is encoded as a String
			// with the length incremented by two.
			int L = i - 2;

			/*
			 * If length L is greater than zero the string S is added
			 */
			if (L > 0) {
				// number of bits
				int numberOfBits = rcs.getCodingLength();
				int size = rcs.size();

				char[] cValue = new char[L];
				value = new StringValue(cValue);

				for (int k = 0; k < L; k++) {
					int code = valueChannel
							.decodeNBitUnsignedInteger(numberOfBits);
					int codePoint;
					if (code == size) {
						// deviation
						codePoint = valueChannel.decodeUnsignedInteger();
					} else {
						codePoint = rcs.getCodePoint(code);
					}

					Character.toChars(codePoint, cValue, k);
				}

				// After encoding the string value, it is added to both the
				// associated "local" value string table partition and the
				// global
				// value string table partition.
				stringDecoder.addValue(qnContext, value);
			} else {
				value = StringCoder.EMPTY_STRING_VALUE;
			}
		}

		return value;
	}
	
	@Override
	public boolean equals(Object o) {
		if(super.equals(o) && o instanceof RestrictedCharacterSetDatatype ) {
			RestrictedCharacterSetDatatype r = (RestrictedCharacterSetDatatype) o;
			return (this.rcs.equals(r.rcs));
		}
		return false;
	}

}