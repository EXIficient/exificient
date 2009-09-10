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

import com.siemens.ct.exi.core.Context;
import com.siemens.ct.exi.datatype.charset.XSDBooleanCharacterSet;
import com.siemens.ct.exi.datatype.strings.StringDecoder;
import com.siemens.ct.exi.datatype.strings.StringEncoder;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.types.BuiltInType;
import com.siemens.ct.exi.util.ExpandedName;
import com.siemens.ct.exi.util.datatype.XSDBoolean;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20080718
 */

public class BooleanDatatype extends AbstractDatatype {
	
	protected XSDBoolean lastValidBoolean = XSDBoolean.newInstance();
	
	public BooleanDatatype(ExpandedName datatypeIdentifier) {
		super(BuiltInType.BOOLEAN, datatypeIdentifier);
		this.rcs = new XSDBooleanCharacterSet();
	}

	public boolean isValid(String value) {
		return lastValidBoolean.parse(value);
	}

	public void writeValue(EncoderChannel valueChannel, StringEncoder stringEncoder, Context context)
			throws IOException {
		valueChannel.encodeBoolean(lastValidBoolean);
	}

	public char[] readValue(DecoderChannel valueChannel,
			StringDecoder stringDecoder, Context context)
			throws IOException {
		return valueChannel.decodeBooleanAsString();
	}
}