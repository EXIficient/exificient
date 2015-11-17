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

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.6-SNAPSHOT
 */

public class ByteDecoderChannel extends AbstractDecoderChannel implements
		DecoderChannel {
	
	protected InputStream is;

	public ByteDecoderChannel(InputStream istream) {
		this.is = istream;
	}

	public InputStream getInputStream() {
		return is;
	}

	public int decode() throws IOException {
		int b = is.read();
		if(b == -1) {
			throw new EOFException("Premature EOS found while reading data.");	
		}
		return b;
	}

	public void align() throws IOException {
	}

	public void skip(long n) throws IOException {
		while (n != 0) {
			n -= is.skip(n);
		}
	}

	/**
	 * Decodes and returns an n-bit unsigned integer using the minimum number of
	 * bytes required for n bits.
	 */
	public int decodeNBitUnsignedInteger(int n) throws IOException {
		assert (n >= 0);

		int bitsRead = 0;
		int result = 0;

		while (bitsRead < n) {
			// result = (result << 8) | is.read();
			result += (decode() << bitsRead);
			bitsRead += 8;
		}
		return result;
	}

	/**
	 * Decode a single boolean value. The value false is represented by the byte
	 * 0, and the value true is represented by the byte 1.
	 */
	public boolean decodeBoolean() throws IOException {
		return (decode() == 0 ? false : true);
	}

	/**
	 * Decode a binary value as a length-prefixed sequence of octets.
	 */
	public byte[] decodeBinary() throws IOException {
		final int length = decodeUnsignedInteger();
		byte[] result = new byte[length];
		
		int readBytes = 0;
		while(readBytes < length) {
			int len = is.read(result, readBytes, length - readBytes);
			if(len == -1) {
				throw new EOFException("Premature EOS found while reading data.");
			}
			readBytes += len;
		}

//		int readBytes = is.read(result);
//		if (readBytes < length) {
//			// special case: not all bytes are read
//			while ((readBytes += is.read(result, readBytes, length - readBytes)) < length) {
//			}
//		}

		return result;
	}

}
