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

package com.siemens.ct.exi.io.channel;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.5
 */

final public class ByteEncoderChannel extends AbstractEncoderChannel implements
		EncoderChannel {
	
	private final OutputStream os;
	protected int len;

	/**
	 * Construct a byte aligned encoder from output stream.
	 * 
	 * @param os output stream
	 */
	public ByteEncoderChannel(OutputStream os) {
		this.os = os;
		len = 0;
	}

	public OutputStream getOutputStream() {
		return os;
	}
	
	public int getLength() {
		return len;
	}

	public void flush() throws IOException {
		os.flush();
	}

	public void align() throws IOException {
		// already byte aligned
	}

	public void encode(int b) throws IOException {
		os.write(b);
		len++;
	}

	public void encode(byte b[], int off, int len) throws IOException {
		os.write(b, off, len);
		this.len += len;
	}

	/**
	 * Encode a single boolean value. A false value is encoded as byte 0 and
	 * true value is encode as byte 1.
	 */
	public void encodeBoolean(boolean b) throws IOException,
			IllegalArgumentException {
		encode(b ? 1 : 0);
	}

	/**
	 * Encode n-bit unsigned integer using the minimum number of bytes required
	 * to store n bits. The n least significant bits of parameter b starting
	 * with the most significant, i.e. from left to right.
	 */
	public void encodeNBitUnsignedInteger(int b, int n) throws IOException {
		if (b < 0 || n < 0) {
			throw new IllegalArgumentException(
					"Negative value as unsigned integer!");
		}
		assert (b >= 0);
		assert (n >= 0);

		if (n == 0) {
			// 0 bytes
		} else if (n < 9) {
			// 1 byte
			encode(b & 0xff);
		} else if (n < 17) {
			// 2 bytes
			encode(b & 0x00ff);
			encode((b & 0xff00) >> 8);
		} else if (n < 25) {
			// 3 bytes
			encode(b & 0x0000ff);
			encode((b & 0x00ff00) >> 8);
			encode((b & 0xff0000) >> 16);
		} else if (n < 33) {
			// 4 bytes
			encode(b & 0x000000ff);
			encode((b & 0x0000ff00) >> 8);
			encode((b & 0x00ff0000) >> 16);
			encode((b & 0xff000000) >> 24);
		} else {
			throw new RuntimeException(
					"Currently not more than 4 Bytes allowed for NBitUnsignedInteger!");
		}
	}

}
