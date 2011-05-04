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

import com.siemens.ct.exi.values.BinaryValue;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.4.20081014
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
	

	/**
	 * Decode a single boolean value. The value false is represented by the byte
	 * 0, and the value true is represented by the byte 1.
	 */
	public boolean decodeBoolean() throws IOException {
		return (is.read() == 0 ? false : true);
	}

	/**
	 * Decode a binary value as a length-prefixed sequence of octets.
	 */
	public BinaryValue decodeBinary() throws IOException {
		final int length = decodeUnsignedInteger();
		byte[] result = new byte[length];
		
		int readBytes = is.read(result);
		if(readBytes < length) {
			//	special case: not all bytes are read 
			while( (readBytes += is.read(result, readBytes, length-readBytes)) < length ) {
			}
		}
		
		return new BinaryValue(result);
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