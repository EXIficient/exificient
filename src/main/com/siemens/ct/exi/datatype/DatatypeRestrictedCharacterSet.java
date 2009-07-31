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
import com.siemens.ct.exi.core.NameContext;
import com.siemens.ct.exi.datatype.charset.RestrictedCharacterSet;
import com.siemens.ct.exi.datatype.strings.StringEncoder;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.util.ExpandedName;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20090428
 */

public class DatatypeRestrictedCharacterSet extends AbstractDatatype {

	protected String lastValidValue;

	public DatatypeRestrictedCharacterSet(ExpandedName datatypeIdentifier, RestrictedCharacterSet rcs) {
		this(datatypeIdentifier);
		this.rcs = rcs;
	}
	
	public DatatypeRestrictedCharacterSet(ExpandedName datatypeIdentifier) {
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

	public void writeValue(EncoderChannel valueChannel, StringEncoder stringEncoder, NameContext context)
			throws IOException {
		
		if (stringEncoder.isStringHit(context, lastValidValue)) {
			stringEncoder.writeValueAsString(context, valueChannel, lastValidValue);
		} else {
			//	NO local or global value hit
			// string-table miss ==> restricted character
			// string literal is encoded as a String with the length
			// incremented by two.
			int numberOfTuples = lastValidValue.length();

			valueChannel.encodeUnsignedInteger(numberOfTuples + 2);

			// number of bits
			int numberOfBits = rcs.getCodingLength();

			for (int i = 0; i < numberOfTuples; i++) {
				int code;
				char ch = lastValidValue.charAt(i);
				if ((code = rcs.getCode(ch)) == Constants.NOT_FOUND) {
					// indicate deviation
					valueChannel.encodeNBitUnsignedInteger(rcs.size(),
							numberOfBits);

					// UCS code point of the character
					// TODO UTF-16 surrogate pair
					valueChannel.encodeUnsignedInteger(ch);
				} else {
					valueChannel.encodeNBitUnsignedInteger(code,
							numberOfBits);
				}
			}

			// After encoding the string value, it is added to both the
			// associated "local" value string table partition and the
			// global value string table partition.
			stringEncoder.addValue(context, lastValidValue);
		}
	}

	public void writeValueRCS(EncoderChannel valueChannel,
			StringEncoder stringEncoder, NameContext context)
			throws IOException {
		// TODO Auto-generated method stub
		
		throw new RuntimeException("dsadasdas");
		
	}

}