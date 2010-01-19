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

package com.siemens.ct.exi.core;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.CodingMode;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.io.channel.BitEncoderChannel;
import com.siemens.ct.exi.io.channel.ByteEncoderChannel;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.4.20090414
 */

public class EXIEncoderInOrder extends AbstractEXIEncoder {

	public EXIEncoderInOrder(EXIFactory exiFactory) {
		super(exiFactory);
	}

	@Override
	public void setOutput(OutputStream os, boolean exiBodyOnly)
			throws EXIException {
		super.setOutput(os, exiBodyOnly);

		if (exiFactory.getCodingMode() == CodingMode.BIT_PACKED) {
			channel = new BitEncoderChannel(os);
		} else {
			assert (exiFactory.getCodingMode() == CodingMode.BYTE_PACKED);
			channel = new ByteEncoderChannel(os);
		}
	}
	
	@Override
	protected void writeValueTypeValid(QName valueContext)
			throws IOException {
		typeEncoder.writeValue(valueContext, channel);
	}

	@Override
	protected void writeValueAsString(QName valueContext, String value)
			throws IOException {
		typeEncoder.getStringEncoder().writeValue(valueContext, channel, value);
	}


}
