/*
 * Copyright (C) 2007-2014 Siemens AG
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

package com.siemens.ct.exi;

import java.io.IOException;
import java.io.OutputStream;

import com.siemens.ct.exi.core.EXIHeaderEncoder;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.io.channel.BitEncoderChannel;

/**
 * An EXI stream is an EXI header followed by an EXI body. The EXI body carries
 * the content of the document, while the EXI header communicates the options
 * used for encoding the EXI body.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.4-SNAPSHOT
 */

public class EXIStreamEncoder {

	protected final EXIHeaderEncoder exiHeader;
	protected final EXIBodyEncoder exiBody;
	protected final EXIFactory exiFactory;

	public EXIStreamEncoder(EXIFactory exiFactory) throws EXIException {
		this.exiFactory = exiFactory;
		exiHeader = new EXIHeaderEncoder();
		exiBody = exiFactory.createEXIBodyEncoder();
	}

	public EXIBodyEncoder encodeHeader(OutputStream os)
			throws EXIException, IOException {
		// setup & write header
		BitEncoderChannel headerChannel = new BitEncoderChannel(os);
		exiHeader.write(headerChannel, exiFactory);

		// setup data-stream for body
		if (exiFactory.getCodingMode() == CodingMode.BIT_PACKED) {
			// bit-packed re-uses the header channel
			exiBody.setOutputChannel(headerChannel);
		} else {
			exiBody.setOutputStream(os);
		}
		return exiBody;
	}
}
