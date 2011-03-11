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

package com.siemens.ct.exi;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.siemens.ct.exi.core.EXIHeaderDecoder;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.io.channel.BitDecoderChannel;

/**
 * An EXI stream is an EXI header followed by an EXI body. The EXI body carries
 * the content of the document, while the EXI header communicates the options
 * used for encoding the EXI body.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.6
 */

public class EXIStreamDecoder {

	protected final EXIHeaderDecoder exiHeader;

	public EXIStreamDecoder() throws EXIException {
		exiHeader = new EXIHeaderDecoder();
	}

	public EXIBodyDecoder decodeHeader(EXIFactory noOptionsFactory,
			InputStream is) throws EXIException, IOException {

		// buffer stream if not already
		// TODO is there a *nice* way to detect whether a stream is buffered
		// already
		if (!(is instanceof BufferedInputStream)) {
			is = new BufferedInputStream(is);
		}
		// is = new BufferedInputStream(is);

		// read header
		BitDecoderChannel headerChannel = new BitDecoderChannel(is);
		EXIFactory exiFactory = exiHeader
				.parse(headerChannel, noOptionsFactory);

		// setup data-stream for body and create decoder
		EXIBodyDecoder exiBody = exiFactory.createEXIBodyDecoder();
		if (exiFactory.getCodingMode() == CodingMode.BIT_PACKED) {
			// bit-packed re-uses the header channel
			exiBody.setInputChannel(headerChannel);
		} else {
			exiBody.setInputStream(is);
		}
		return exiBody;
	}
}
