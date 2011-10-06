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
 * @version 0.8
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
	private final void readBuffer() throws IOException {
		if ((buffer = istream.read()) == -1) {
			throw new EOFException(
					"Premature EOS found while reading data.");
		}
		capacity = BUFFER_CAPACITY;
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
	 * Returns current byte buffer without actually reading data
	 * 
	 * @throws IOException
	 */
	public int lookAhead() throws IOException {
		if (capacity == 0) {
			readBuffer();	
		}
		return buffer;
	}

	/**
	 * Skip n bytes
	 * 
	 * @param n
	 * @throws IOException
	 */
	public void skip(long n) throws IOException {
		if (capacity == 0) {
			// aligned
			while (n != 0) {
				n -= istream.skip(n);
			}
		} else {
			// not aligned, grrr
			for (int i = 0; i < n; n++) {
				readBits(8);
			}
		}
	}

	/**
	 * Return next bit from underlying stream.
	 */
	public int readBit() throws IOException {
		if (capacity == 0) {
			readBuffer();	
		}
		return (buffer >> --capacity) & 0x1;
	}

	/**
	 * Read the next n bits and return the result as an integer.
	 * 
	 * @param n
	 *            The number of bits in the range [1,32].
	 */
	public int readBits(int n) throws IOException {
		assert (n > 0);

		if (n <= capacity) {
			// buffer already holds all necessary bits
			return (buffer >> (capacity -= n)) & (0xff >> (BUFFER_CAPACITY - n));
		} else {
			// get as many bits from buffer as possible
			int result = buffer & (0xff >> (BUFFER_CAPACITY - capacity));
			n -= capacity;
			capacity = 0;

			// possibly read whole bytes
			while (n > 7) {
				if (capacity == 0) {
					readBuffer();	
				}
				result = (result << BUFFER_CAPACITY) | buffer;
				n -= BUFFER_CAPACITY;
				capacity = 0;
			}

			// read the rest of the bits
			if (n > 0) {
				if (capacity == 0) {
					readBuffer();	
				}
				result = (result << n) | (buffer >>> (BUFFER_CAPACITY - n));
				capacity = BUFFER_CAPACITY - n;
			}

			return result;
		}
	}

	public void read(byte b[], int off, int len) throws IOException {
		if (len == 0) {

		} else if (capacity == 0) {
			// byte-aligned --> read all bytes at byte-border (at once?)
			int readBytes = 0;
			do {
				readBytes += istream.read(b, readBytes, len - readBytes);
			} while (readBytes < len);
		} else {
			int readBytes = 0;
			int shift1 = BUFFER_CAPACITY - capacity;
			int shift2 = capacity;
				
			// get all bits from current buffer
			int currentResult = buffer & (0xff >> shift1);
			
			// read (len-1) full bytes at once
			int lenMinusOne = len-1;
			byte fullBytes [] = new byte[lenMinusOne];
			
			do {
				readBytes += istream.read(fullBytes, readBytes, lenMinusOne-readBytes);
			} while(readBytes < lenMinusOne);
			
			//	shift full bytes to result array taking into account interleaving
			for(int i=0; i<lenMinusOne; i++) {
				// Note: byte may be negative --> 0xff & byte
				b[i] = (byte) ((currentResult << shift1) | ((0xff & fullBytes[i]) >>> shift2));
				currentResult = fullBytes[i] & (0xff >> shift1);
			}
			
			//	get ready for remaining trailing bits
			readBuffer();

			currentResult = (currentResult << shift1) | (buffer >>> shift2);
			b[off+len-1] = (byte) currentResult;
			
			capacity = shift2;	// new (old) capacity

			// for(int i=0; i<len; i++) {
			// b[off+i] = (byte) readBits(8);
			// }
		}
	}

	/**
	 * Read and return the next byte without discarding current buffer.
	 */
	public final int readDirectByte() throws IOException {
		return istream.read();
	}

}
