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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

import com.siemens.ct.exi.core.EXIHeaderDecoder;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.io.channel.BitDecoderChannel;

/**
 * An EXI stream is an EXI header followed by an EXI body. The EXI body carries
 * the content of the document, while the EXI header communicates the options
 * used for encoding the EXI body.
 * 
 * <p>
 * Note: In case of multiple EXI documents with EXI Compression mode in one
 * stream or subsequent data in the stream/file one needs to provide the input
 * stream as PushbackInputStream with the size of at least
 * 
 * DecodingOptions.PUSHBACK_BUFFER_SIZE. The reason for doing so is that the
 * Java inflater sometimes reads beyond the EXI channel/block and
 * PushbackInputStream allows us to push back this data so that it is not lost.
 * </p>
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.5-SNAPSHOT
 */

public class EXIStreamDecoder {

	protected final EXIHeaderDecoder exiHeader;
	protected EXIBodyDecoder exiBody;
	protected final EXIFactory noOptionsFactory;

	public EXIStreamDecoder(EXIFactory noOptionsFactory) throws EXIException {
		exiHeader = new EXIHeaderDecoder();
		// assume the default factory
		exiBody = noOptionsFactory.createEXIBodyDecoder();
		this.noOptionsFactory = noOptionsFactory;
	}

	public EXIBodyDecoder getBodyOnlyDecoder(InputStream is)
			throws EXIException, IOException {
		is = checkBufferedAndPushbackStream(is);
		exiBody.setInputStream(is);
		return exiBody;
	}

	public EXIBodyDecoder decodeHeader(InputStream is) throws EXIException,
			IOException {
		is = checkBufferedAndPushbackStream(is);
		// read header
		BitDecoderChannel headerChannel = new BitDecoderChannel(is);
		EXIFactory exiFactory = exiHeader
				.parse(headerChannel, noOptionsFactory);

		// update body decoder if EXI options tell to do so
		if (exiFactory != noOptionsFactory) {
			// exiBody = noOptionsFactory.createEXIBodyDecoder();
			exiBody = exiFactory.createEXIBodyDecoder();
		}
		// setup data-stream for body
		if (exiFactory.getCodingMode() == CodingMode.BIT_PACKED) {
			// bit-packed re-uses the header channel
			exiBody.setInputChannel(headerChannel);
		} else {
			exiBody.setInputStream(is);
		}

		return exiBody;
	}

	/**
	 * 
	 * @param is
	 *            input stream
	 * @return input stream that is buffered with <code>markSupported</code> or
	 *         same stream if those properties are given already
	 */
	private InputStream checkBufferedAndPushbackStream(InputStream is) {
		if (is instanceof PushbackInputStream) {
			// push back stream, don't do anything
		} else if (!(is instanceof BufferedInputStream || is instanceof ByteArrayInputStream)) {
			// buffer stream if not already
			// TODO is there a *nice* way to detect whether a stream is buffered
			is = new BufferedInputStream(is);
		}

		return is;
	}
}
