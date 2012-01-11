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

package com.siemens.ct.exi.datatype;

import java.io.IOException;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.context.DecoderContext;
import com.siemens.ct.exi.context.EncoderContext;
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
 * @version 0.8
 */

public class RestrictedCharacterSetDatatype extends AbstractDatatype {

	private static final long serialVersionUID = -6098764255799006920L;

	protected String lastValidValue;

	public RestrictedCharacterSetDatatype(RestrictedCharacterSet rcs,
			QName schemaType) {
		this(schemaType);
		this.rcs = rcs;
	}

	public RestrictedCharacterSetDatatype(QName schemaType) {
		super(BuiltInType.RCS_STRING, schemaType);
	}

	public void setRestrictedCharacterSet(RestrictedCharacterSet rcs) {
		this.rcs = rcs;
	}

	public RestrictedCharacterSet getRestrictedCharacterSet() {
		return rcs;
	}

	public boolean isValid(Value value) {
		// Note: no validity check needed since any char-sequence can be encoded
		// due to fallback mechanism
		lastValidValue = value.toString();
		return true;
	}

	public void writeValue(EncoderContext encoderContext,
			QNameContext qnContext, EncoderChannel valueChannel)
			throws IOException {
		StringEncoder stringEncoder = encoderContext.getStringEncoder();
		if (stringEncoder.isStringHit(lastValidValue)) {
			stringEncoder.writeValue(encoderContext, qnContext, valueChannel,
					lastValidValue);
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
				stringEncoder.addValue(encoderContext, qnContext,
						lastValidValue);
			}
		}
	}

	public Value readValue(DecoderContext decoderContext,
			QNameContext qnContext, DecoderChannel valueChannel)
			throws IOException {

		StringDecoder stringDecoder = decoderContext.getStringDecoder();
		StringValue value;

		int i = valueChannel.decodeUnsignedInteger();

		if (i == 0) {
			// local value partition
			value = stringDecoder.readValueLocalHit(decoderContext, qnContext,
					valueChannel);
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
				stringDecoder.addValue(decoderContext, qnContext, value);
			} else {
				value = StringCoder.EMPTY_STRING_VALUE;
			}
		}

		return value;
	}

}