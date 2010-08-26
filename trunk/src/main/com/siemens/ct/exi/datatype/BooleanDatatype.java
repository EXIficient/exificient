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

import com.siemens.ct.exi.datatype.charset.XSDBooleanCharacterSet;
import com.siemens.ct.exi.datatype.strings.StringDecoder;
import com.siemens.ct.exi.datatype.strings.StringEncoder;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.types.BuiltInType;
import com.siemens.ct.exi.values.BooleanValue;
import com.siemens.ct.exi.values.Value;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.5
 */

public class BooleanDatatype extends AbstractDatatype {
	
	private static final long serialVersionUID = -6150310956233103627L;
	
	protected Boolean bool;
	
	public BooleanDatatype(QName schemaType) {
		super(BuiltInType.BOOLEAN, schemaType);
		this.rcs = new XSDBooleanCharacterSet();
	}

	public boolean isValid(String value) {
		bool = BooleanValue.parse(value);
		return (bool != null);
	}
	
	public boolean isValid(Value value) {
		if (value instanceof BooleanValue) {
			bool = ((BooleanValue) value).toBoolean();
			return true;			
		} else {
			return false;
		}
	}
	
	public Value getValue() {
		return new BooleanValue(bool);
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
	}

	public Value readValue(DecoderChannel valueChannel,
			StringDecoder stringDecoder, QName context)
			throws IOException {
		return valueChannel.decodeBooleanValue();
	}
}