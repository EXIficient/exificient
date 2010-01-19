/*
 * Copyright (C) 2007-2010 Siemens AG
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
import com.siemens.ct.exi.datatype.charset.XSDBooleanCharacterSet;
import com.siemens.ct.exi.datatype.strings.StringDecoder;
import com.siemens.ct.exi.datatype.strings.StringEncoder;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.types.BuiltInType;
import com.siemens.ct.exi.values.BooleanValue;
import com.siemens.ct.exi.values.Value;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.4.20081110
 */

public class BooleanPatternDatatype extends AbstractDatatype {
	
	private int lastValidBooleanID;
	
	public BooleanPatternDatatype(QName datatypeIdentifier) {
		super(BuiltInType.BOOLEAN_PATTERN, datatypeIdentifier);
		this.rcs = new XSDBooleanCharacterSet();
	}
	
	public boolean isValid(String value) {
		boolean retValue = true;
		
		if (value.equals(Constants.XSD_BOOLEAN_FALSE)) {
			lastValidBooleanID = 0;
		} else if (value.equals(Constants.XSD_BOOLEAN_0)) {
			lastValidBooleanID = 1;
		} else if (value.equals(Constants.XSD_BOOLEAN_TRUE)) {
			lastValidBooleanID = 2;
		} else if (value.equals(Constants.XSD_BOOLEAN_1)) {
			lastValidBooleanID = 3;
		} else {
			retValue = false;
		}

		return retValue;
	}

	public void writeValue(EncoderChannel valueChannel, StringEncoder stringEncoder, QName context)
			throws IOException {
		valueChannel.encodeNBitUnsignedInteger(lastValidBooleanID, 2);
	}

	public Value readValue(DecoderChannel valueChannel,
			StringDecoder stringDecoder, QName context)
			throws IOException {
		int booleanID = valueChannel.decodeNBitUnsignedInteger(2);
		return new BooleanValue(booleanID);
	}
}