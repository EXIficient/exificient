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
import com.siemens.ct.exi.values.Value;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.4.20080718
 */

public class BooleanDatatype extends AbstractDatatype {
	
	protected boolean bool;
	
	public BooleanDatatype(QName datatypeIdentifier) {
		super(BuiltInType.BOOLEAN, datatypeIdentifier);
		this.rcs = new XSDBooleanCharacterSet();
	}

	public boolean isValid(String value) {
		value = value.trim();
		boolean validValue = true;
		if (value.equals(Constants.XSD_BOOLEAN_0)
				|| value.equals(Constants.XSD_BOOLEAN_FALSE)) {
			bool = false;
			
		} else if (value.equals(Constants.XSD_BOOLEAN_1)
				|| value.equals(Constants.XSD_BOOLEAN_TRUE)) {
			bool = true;
		} else {
			validValue = false;
		}
		return validValue;
	}
	
	@Override
	public boolean isValidRCS(String value) {
		// Note: boolean really needs to do a check since it can be used for xsi:nil
		super.isValidRCS(value);
		return isValid(value);
	}

	public boolean getBoolean() {
		return bool;
	}
	
	public void writeValue(EncoderChannel valueChannel, StringEncoder stringEncoder, QName context)
			throws IOException {
		valueChannel.encodeBoolean(bool);
		// valueChannel.encodeBoolean(lastValidBoolean);
	}

	public Value readValue(DecoderChannel valueChannel,
			StringDecoder stringDecoder, QName context)
			throws IOException {
		return valueChannel.decodeBooleanValue();
	}
}