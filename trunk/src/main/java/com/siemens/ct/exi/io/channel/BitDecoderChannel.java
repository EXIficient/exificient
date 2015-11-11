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
import java.io.InputStream;

import com.siemens.ct.exi.io.BitInputStream;

/**
 * Simple datatype decoder based on an underlying <code>BitInputStream</code>.
 * Reading a single bit from the underlying stream involves several VM
 * operations. Thus, whenever possible, whole bytes should be read instead.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.5-SNAPSHOT
 */

public class BitDecoderChannel extends AbstractDecoderChannel implements
		DecoderChannel {
	/**
	 * Underlying bit input stream from which bits and bytes are read.
	 */
	protected BitInputStream istream;

	/**
	 * Construct a decoder from input stream
	 * 
	 * @param is input stream
	 */
	public BitDecoderChannel(InputStream is) {
		this.istream = new BitInputStream(is);
	}

	public final int decode() throws IOException {
		return istream.read();
	}

	public void align() throws IOException {
		istream.align();
	}

	public int lookAhead() throws IOException {
		return istream.lookAhead();
	}

	public void skip(long n) throws IOException {
		istream.skip(n);
	}

	/**
	 * Decodes and returns an n-bit unsigned integer.
	 */
	public final int decodeNBitUnsignedInteger(int n) throws IOException {
		assert (n >= 0);
		return (n == 0 ? 0 : istream.readBits(n));
	}

	/**
	 * Decode a single boolean value. The value false is represented by the bit
	 * 0, and the value true is represented by the bit 1.
	 */
	public boolean decodeBoolean() throws IOException {
		return (istream.readBit() == 1);
	}
	
	/**
	 * Decode a binary value as a length-prefixed sequence of octets.
	 */
	public byte[] decodeBinary() throws IOException {
		final int length = decodeUnsignedInteger();
		byte[] result = new byte[length];
		
		istream.read(result, 0, length);
		return result;
	}

}
