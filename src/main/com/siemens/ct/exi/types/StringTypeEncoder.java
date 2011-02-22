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

package com.siemens.ct.exi.types;

import java.io.IOException;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.datatype.strings.StringEncoder;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.values.Value;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.6
 */

public class StringTypeEncoder extends AbstractTypeEncoder {

	String lastValidValue;

	public StringTypeEncoder(StringEncoder stringEncoder) {
		super(stringEncoder);
	}
	
//	public boolean isValid(Datatype datatype, String value) {
//		lastValidValue = value;
//		return true;
//	}
	
	public boolean isValid(Datatype datatype, Value value) {
		lastValidValue = value.toString();
		return true;
//		return this.isValid(datatype, value.toString());
	}
	
	public void writeValue(QName context, EncoderChannel valueChannel) throws IOException {
		stringEncoder.writeValue(context, valueChannel, lastValidValue);
	}

}
