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

package com.siemens.ct.exi.datatype.encoder;

import java.io.IOException;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.core.NameContext;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.io.channel.EncoderChannel;

/**
 * When pattern facets are available in the schema datatype, Boolean datatype
 * representation is able to distinguish values not only arithmetically (0 or 1)
 * but also between lexical variances ("0", "1", "false" and "true"), and values
 * typed as Boolean are represented as n-bit unsigned integer (7.1.9 n-bit
 * Unsigned Integer), where n is two (2) and the value zero (0), one (1), two
 * (2) and three (3) each represents value "false", "0", "true" and "1".
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20081110
 */

public class BooleanPatternDatatypeEncoder extends AbstractDatatypeEncoder
		implements DatatypeEncoder {
	private int lastValidBooleanID;

	public BooleanPatternDatatypeEncoder(TypeEncoder typeEncoder) {
		super(typeEncoder);
	}

	public boolean isValid(Datatype datatype, String value) {
		if (value.equals(Constants.XSD_BOOLEAN_FALSE)) {
			lastValidBooleanID = 0;
		} else if (value.equals(Constants.XSD_BOOLEAN_0)) {
			lastValidBooleanID = 1;
		} else if (value.equals(Constants.XSD_BOOLEAN_TRUE)) {
			lastValidBooleanID = 2;
		} else if (value.equals(Constants.XSD_BOOLEAN_1)) {
			lastValidBooleanID = 3;
		} else {
			return false;
		}

		return true;
	}

//	public void writeValue(EncoderChannel valueChannel, String uri,
//			String localName) throws IOException {
//		valueChannel.encodeNBitUnsignedInteger(lastValidBooleanID, 2);
//	}
	
	public void writeValue(NameContext context, EncoderChannel valueChannel) throws IOException {
		valueChannel.encodeNBitUnsignedInteger(lastValidBooleanID, 2);
	}

}
