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

package com.siemens.ct.exi.core;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.CodingMode;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.io.channel.BitEncoderChannel;
import com.siemens.ct.exi.io.channel.ByteEncoderChannel;
import com.siemens.ct.exi.io.channel.EncoderChannel;

/**
 * EXI Body encoder for bit or byte-aligned streams.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.6
 */

public class EXIBodyEncoderInOrder extends AbstractEXIBodyEncoder {

	public EXIBodyEncoderInOrder(EXIFactory exiFactory) throws EXIException {
		super(exiFactory);
	}

	public void setOutputStream(OutputStream os)
			throws EXIException, IOException {
		
		CodingMode codingMode = exiFactory.getCodingMode();

		// setup data-stream only
		if (codingMode == CodingMode.BIT_PACKED) {
			// create new bit-aligned channel
			setOutputChannel(new BitEncoderChannel(os));
		} else {
			assert (codingMode == CodingMode.BYTE_PACKED);
			// create new byte-aligned channel
			setOutputChannel(new ByteEncoderChannel(os));
		}
	}

	public void setOutputChannel(EncoderChannel encoderChannel) {
		this.channel = encoderChannel;
	}

	@Override
	protected void writeValue(QName valueContext) throws IOException {
		typeEncoder.writeValue(valueContext, channel);
	}

}
