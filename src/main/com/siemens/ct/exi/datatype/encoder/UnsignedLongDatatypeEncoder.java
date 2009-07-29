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

import com.siemens.ct.exi.core.NameContext;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.io.channel.EncoderChannel;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20080718
 */

public class UnsignedLongDatatypeEncoder extends AbstractDatatypeEncoder
		implements DatatypeEncoder {
	private long lastUnsignedInteger;

	public UnsignedLongDatatypeEncoder(TypeEncoder typeEncoder) {
		super(typeEncoder);
	}

	public boolean isValid(Datatype datatype, String value) {
		try {
			lastUnsignedInteger = Long.parseLong(value);
			return (lastUnsignedInteger >= 0);
		} catch (NumberFormatException e) {
			return false;
		}
	}

//	public void writeValue(EncoderChannel valueChannel, String uri,
//			String localName) throws IOException {
//		valueChannel.encodeUnsignedLong(lastUnsignedInteger);
//	}

	public void writeValue(NameContext context, EncoderChannel valueChannel) throws IOException {
		valueChannel.encodeUnsignedLong(lastUnsignedInteger);
	}
}
