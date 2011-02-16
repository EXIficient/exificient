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

package com.siemens.ct.exi.io.channel;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.6
 */

public class ByteDecoderChannel extends AbstractDecoderChannel implements
		DecoderChannel {
	protected InputStream is;

	public ByteDecoderChannel(InputStream istream) {
		is = new BufferedInputStream(istream);
	}

	public InputStream getInputStream() {
		return is;
	}

	public int decode() throws IOException {
		return is.read();
	}
	
	public void align() throws IOException {
	}
	
	public void skip(long n) throws IOException {
		while(n != 0) {
			n -= is.skip(n);
		}
	}
	

	/**
	 * Decode a single boolean value. The value false is represented by the byte
	 * 0, and the value true is represented by the byte 1.
	 */
	public boolean decodeBoolean() throws IOException {
		return (is.read() == 0 ? false : true);
	}
	
	/**
	 * Decode an arbitrary precision non negative integer using a sequence of
	 * octets. The most significant bit of the last octet is set to zero to
	 * indicate sequence termination. Only seven bits per octet are used to
	 * store the integer's value.
	 */
	public int decodeUnsignedInteger() throws IOException {
		int result = 0;

		// 0XXXXXXX ... 1XXXXXXX 1XXXXXXX
		// int multiplier = 1;
		int mShift = 0;
		int b;
		
		do {
			// 1. Read the next octet
			b = decode();
			// 2. Multiply the value of the unsigned number represented by
			// the 7
			// least significant
			// bits of the octet by the current multiplier and add the
			// result to
			// the current value.
			// result += (b & 127) * multiplier;
			result += (b & 127) << mShift;
			// 3. Multiply the multiplier by 128
			// multiplier = multiplier << 7;
			mShift += 7;
			// 4. If the most significant bit of the octet was 1, go back to
			// step 1
		} while ((b >>> 7) == 1);

		return result;
	}
	
	protected long decodeUnsignedLong() throws IOException {
		long lResult = 0L;
		int mShift = 0;
		int b;

		do {
			b = decode();
			lResult += ((long) (b & 127)) << mShift;
			mShift += 7;
		} while ((b >>> 7) == 1);

		return lResult;
	}
	

	/**
	 * Decode a binary value as a length-prefixed sequence of octets.
	 */
	public byte[] decodeBinary() throws IOException {
		final int length = decodeUnsignedInteger();
		byte[] result = new byte[length];
		
		int readBytes = is.read(result);
		if(readBytes < length) {
			//	special case: not all bytes are read 
			while( (readBytes += is.read(result, readBytes, length-readBytes)) < length ) {
			}
		}
		
		return result;
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
			result += (is.read() << bitsRead);
			bitsRead += 8;
		}
		return result;
	}

}
