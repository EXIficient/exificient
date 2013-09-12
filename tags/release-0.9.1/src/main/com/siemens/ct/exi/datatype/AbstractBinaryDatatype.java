/*
 * Copyright (C) 2007-2012 Siemens AG
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

import com.siemens.ct.exi.context.EncoderContext;
import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.types.BuiltInType;
import com.siemens.ct.exi.values.AbstractBinaryValue;
import com.siemens.ct.exi.values.Value;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.1
 */

public abstract class AbstractBinaryDatatype extends AbstractDatatype {

	private static final long serialVersionUID = 774579535856394650L;

	protected byte[] bytes;

	public AbstractBinaryDatatype(BuiltInType binaryType, QName schemaType) {
		super(binaryType, schemaType);
		assert (binaryType == BuiltInType.BINARY_BASE64 || binaryType == BuiltInType.BINARY_HEX);
	}

	abstract protected boolean isValidString(String value);

	public boolean isValid(Value value) {
		if (value instanceof AbstractBinaryValue) {
			bytes = ((AbstractBinaryValue) value).toBytes();
			return true;
		} else {
			return isValidString(value.toString());
		}
	}

	public void writeValue(EncoderContext encoderContext,
			QNameContext qnContext, EncoderChannel valueChannel)
			throws IOException {
		valueChannel.encodeBinary(bytes);
	}
}