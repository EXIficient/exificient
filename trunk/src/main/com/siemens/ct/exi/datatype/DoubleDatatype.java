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

import com.siemens.ct.exi.core.NameContext;
import com.siemens.ct.exi.datatype.charset.XSDDoubleCharacterSet;
import com.siemens.ct.exi.datatype.strings.StringDecoder;
import com.siemens.ct.exi.datatype.strings.StringEncoder;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.types.BuiltInType;
import com.siemens.ct.exi.util.ExpandedName;
import com.siemens.ct.exi.util.datatype.XSDDouble;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20080718
 */

public class DoubleDatatype extends AbstractDatatype {
	
	protected XSDDouble lastValidDouble = XSDDouble.newInstance();
	
	public DoubleDatatype(ExpandedName datatypeIdentifier) {
		super(BuiltInType.DOUBLE, datatypeIdentifier);
		this.rcs = new XSDDoubleCharacterSet();
	}
	
	public boolean isValid(String value) {
		return lastValidDouble.parse(value);
	}

	public void writeValue(EncoderChannel valueChannel, StringEncoder stringEncoder, NameContext context)
			throws IOException {
		valueChannel.encodeDouble(lastValidDouble.mantissa, lastValidDouble.exponent);
	}

	public char[] readValue(DecoderChannel valueChannel,
			StringDecoder stringDecoder, NameContext context)
			throws IOException {
		return valueChannel.decodeDoubleAsString();
	}
}