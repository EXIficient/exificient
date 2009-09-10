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

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.core.Context;
import com.siemens.ct.exi.datatype.charset.RestrictedCharacterSet;
import com.siemens.ct.exi.datatype.strings.StringDecoder;
import com.siemens.ct.exi.datatype.strings.StringEncoder;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.types.BuiltInType;
import com.siemens.ct.exi.util.ExpandedName;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20090428
 */

public class RestrictedCharacterSetDatatype extends AbstractDatatype {

	protected String lastValidValue;

	public RestrictedCharacterSetDatatype(ExpandedName datatypeIdentifier,
			RestrictedCharacterSet rcs) {
		this(datatypeIdentifier);
		this.rcs = rcs;
	}

	public RestrictedCharacterSetDatatype(ExpandedName datatypeIdentifier) {
		super(BuiltInType.RESTRICTED_CHARACTER_SET, datatypeIdentifier);
	}

	public void setRestrictedCharacterSet(RestrictedCharacterSet rcs) {
		this.rcs = rcs;
	}

	public RestrictedCharacterSet getRestrictedCharacterSet() {
		return rcs;
	}

	public boolean isValid(String value) {
		// Note: no validity check needed since any char-sequence can be encoded
		// due to fallback mechanism
		lastValidValue = value;
		return true;
	}

	public void writeValue(EncoderChannel valueChannel,
			StringEncoder stringEncoder, Context context) throws IOException {

		if (stringEncoder.isStringHit(context, lastValidValue)) {
			stringEncoder.writeValue(context, valueChannel, lastValidValue);
		} else {
			// NO local or global value hit
			// string-table miss ==> restricted character
			// string literal is encoded as a String with the length
			// incremented by two.
			int numberOfTuples = lastValidValue.length();

			valueChannel.encodeUnsignedInteger(numberOfTuples + 2);

			// number of bits
			int numberOfBits = rcs.getCodingLength();

			for (int i = 0; i < numberOfTuples; i++) {
				int codePoint = lastValidValue.codePointAt(i);
				int code = rcs.getCode(codePoint);
				if (code == Constants.NOT_FOUND) {
					// indicate deviation
					valueChannel.encodeNBitUnsignedInteger(rcs.size(),
							numberOfBits);

					valueChannel.encodeUnsignedInteger(codePoint);
				} else {
					valueChannel.encodeNBitUnsignedInteger(code, numberOfBits);
				}
			}

			// After encoding the string value, it is added to both the
			// associated "local" value string table partition and the
			// global value string table partition.
			stringEncoder.addValue(context, lastValidValue);
		}
	}

	public char[] readValue(DecoderChannel valueChannel,
			StringDecoder stringDecoder, Context context) throws IOException {
		char[] value;

		int i = valueChannel.decodeUnsignedInteger();

		if (i == 0) {
			// local value partition
			value = stringDecoder.readValueLocalHit(context, valueChannel);
		} else if (i == 1) {
			// found in global value partition
			value = stringDecoder.readValueGlobalHit(context, valueChannel);
		} else {
			// not found in global value (and local value) partition
			// ==> restricted character string literal is encoded as a String
			// with the length incremented by two.
			int slen = i - 2;

			// number of bits
			int numberOfBits = rcs.getCodingLength();
			int size = rcs.size();

			value = new char[slen];

			for (int k = 0; k < slen; k++) {
				int code = valueChannel.decodeNBitUnsignedInteger(numberOfBits);
				int codePoint;
				if (code == size) {
					// deviation
					codePoint = valueChannel.decodeUnsignedInteger();
				} else {
					codePoint = rcs.getCodePoint(code);
				}

				Character.toChars(codePoint, value, k);

			}

			// After encoding the string value, it is added to both the
			// associated "local" value string table partition and the global
			// value string table partition.
			stringDecoder.addValue(context, value);
		}

		return value;
	}

}