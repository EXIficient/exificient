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

import com.siemens.ct.exi.io.BitOutputStream;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.6-SNAPSHOT
 */

public class BitEncoderChannel extends AbstractEncoderChannel implements
		EncoderChannel {
	
	/**
	 * Underlying bit output stream to which bits and bytes are written.
	 */
	protected BitOutputStream ostream;

	/**
	 * Construct an encoder from output stream.
	 * 
	 * @param ostream output stream
	 */
	public BitEncoderChannel(OutputStream ostream) {
		this.ostream = new BitOutputStream(ostream);
	}

	public OutputStream getOutputStream() {
		return ostream;
	}

	public int getLength() {
		return ostream.getLength();
	}

	/**
	 * Flush underlying bit output stream.
	 */
	public void flush() throws IOException {
		ostream.flush();
	}

	public void align() throws IOException {
		ostream.align();
	}

	public void encode(int b) throws IOException {
		ostream.writeBits(b, 8);
	}

	public void encode(byte b[], int off, int len) throws IOException {
		// TODO write whole bytes (if possible)
		for (int i = off; i < (off + len); i++) {
			ostream.writeBits(b[i], 8);
		}
	}

	/**
	 * Encode n-bit unsigned integer. The n least significant bits of parameter
	 * b starting with the most significant, i.e. from left to right.
	 */
	public void encodeNBitUnsignedInteger(int b, int n) throws IOException {
		if (b < 0 || n < 0) {
			throw new IllegalArgumentException(
					"Encode negative value as unsigned integer is invalid!");
		}
		assert (b >= 0);
		assert (n >= 0);

		ostream.writeBits(b, n);
	}

	/**
	 * Encode a single boolean value. A false value is encoded as bit 0 and true
	 * value is encode as bit 1.
	 */
	public void encodeBoolean(boolean b) throws IOException {
		if (b) {
			ostream.writeBit1();
		} else {
			ostream.writeBit0();
		}

		// ostream.writeBit(b ? 1 : 0);
	}
}
