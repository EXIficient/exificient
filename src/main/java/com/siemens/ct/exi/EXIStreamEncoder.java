/*
 * Copyright (c) 2007-2015 Siemens AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
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
 * @version 0.9.5
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
