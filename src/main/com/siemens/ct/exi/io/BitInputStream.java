/*
 * Copyright (C) 2007, 2008 Siemens AG
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

package com.siemens.ct.exi.io;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Read bits and bytes from an underlying input stream.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20081014
 */

final public class BitInputStream {
	public static final int BUFFER_CAPACITY = 8;

	/**
	 * Used buffer capacity in bits.
	 */
	private int capacity = 0;

	/**
	 * Internal buffer represented as an int. Only the least significant byte is
	 * used. An int is used instead of a byte int-to-byte conversions in the VM.
	 */
	private int buffer = 0;

	/**
	 * Underlying input stream.
	 */
	private InputStream istream;

	/**
	 * Construct an instance of this class from an input stream.
	 */
	public BitInputStream(InputStream istream) {
		this.istream = istream;
	}

	/**
	 * Resets this instance and sets a new underlying input stream. This method
	 * allows instances of this class to be re-used. The resulting state after
	 * calling this method is identical to that of a newly created instance.
	 */
	public void setInputStream(InputStream istream) {
		this.istream = istream;
		buffer = capacity = 0;
	}

	/**
	 * If buffer is empty, read byte from underlying stream.
	 */
	private void readBuffer() throws IOException {
		if (capacity == 0) {
			if ((buffer = istream.read()) == -1) {
				throw new EOFException(
						"Premature EOS found while reading data.");
			}
			capacity = BUFFER_CAPACITY;
		}
	}

	/**
	 * Discard any bits currently in the buffer to byte-align stream
	 */
	public void align() throws IOException {
		if (capacity != 0) {
			capacity = 0;
		}
	}

	/**
	 * Return next bit from underlying stream.
	 */
	public int readBit() throws IOException {
		readBuffer();
		return (buffer >>> --capacity) & 0x1;
	}

	/**
	 * Read the next n bits and return the result as an integer.
	 * 
	 * @param n
	 *            The number of bits in the range [1,32].
	 */
	public int readBits(int n) throws IOException {
		assert (n > 0);

		readBuffer();
		if (n <= capacity) {
			// buffer already holds all necessary bits
			capacity -= n;
			return (buffer >>> capacity) & (0xff >> (BUFFER_CAPACITY - n));
		} else {
			// get as many bits from buffer as possible
			int result = buffer & (0xff >> (BUFFER_CAPACITY - capacity));
			n -= capacity;
			capacity = 0;

			// possibly read whole bytes
			while (n >= 8) {
				readBuffer();
				result = (result << BUFFER_CAPACITY) | buffer;
				n -= BUFFER_CAPACITY;
				capacity = 0;
			}

			// read the rest of the bits
			if (n > 0) {
				readBuffer();
				result = (result << n) | (buffer >>> (BUFFER_CAPACITY - n));
				capacity = BUFFER_CAPACITY - n;
			}

			return result;
		}
	}

	/**
	 * Read and return the next byte without discarding current buffer.
	 */
	public int readDirectByte() throws IOException {
		return istream.read();
	}

}
