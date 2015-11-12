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
 * @version 0.9.5
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
	 * 
	 * @param istream input stream
	 */
	public BitInputStream(InputStream istream) {
		this.istream = istream;
	}

	/**
	 * Resets this instance and sets a new underlying input stream. This method
	 * allows instances of this class to be re-used. The resulting state after
	 * calling this method is identical to that of a newly created instance.
	 * 
	 * @param istream input stream
	 */
	public void setInputStream(InputStream istream) {
		this.istream = istream;
		buffer = capacity = 0;
	}
	
	// read direct byte
	private final int readDirectByte() throws IOException {
		int b;
		if ((b = istream.read()) == -1) {
			throw new EOFException("Premature EOS found while reading data.");
		}
		return b;
	}

	/**
	 * If buffer is empty, read byte from underlying stream.
	 */
	private final void readBuffer() throws IOException {
		buffer = readDirectByte();
		capacity = BUFFER_CAPACITY;
	}

	/**
	 * Discard any bits currently in the buffer to byte-align stream
	 * 
	 * @throws IOException IO exception
	 */
	public void align() throws IOException {
		if (capacity != 0) {
			capacity = 0;
		}
	}

	/**
	 * Returns current byte buffer without actually reading data
	 * 
	 * @throws IOException IO exception
	 * @return lookAhead byte
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
	 * @param n bytes
	 * @throws IOException IO exception
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
	 * 
	 * @throws IOException IO exception
	 * @return read bit
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
	 *            
	 * @throws IOException IO exception
	 * @return nbit value
	 */
	public int readBits(int n) throws IOException {
		assert (n > 0);
		int result;

		if (n <= capacity) {
			// buffer already holds all necessary bits
			result = (buffer >> (capacity -= n))
					& (0xff >> (BUFFER_CAPACITY - n));
		} else if (capacity == 0 && n == BUFFER_CAPACITY) {
			// possible to read direct byte, nothing else to do
			result = readDirectByte();
		} else {
			// get as many bits from buffer as possible
			result = buffer & (0xff >> (BUFFER_CAPACITY - capacity));
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
				result = (result << n) | (buffer >> (capacity = (BUFFER_CAPACITY - n)));
			}
		}

		return result;
	}

	/**
	 * Reads one byte (8 bits) of data from the input stream
	 * 
	 * @return next byte as int
	 * @throws IOException IO exception
	 */
	public final int read() throws IOException {
		// possible to read direct byte?
		return (capacity == 0) ? readDirectByte() : this
				.readBits(BUFFER_CAPACITY);
	}

	public void read(byte b[], int off, final int len) throws IOException {
		assert (len >= 0);

		if (len == 0) {
			/* nothing to do */
		} else if (capacity == 0) {
			// byte-aligned --> read all bytes at byte-border (at once?)
			int readBytes = 0;
			do {
				int br = istream.read(b, readBytes, len - readBytes);
				if(br == -1) {
					throw new EOFException("Premature EOS found while reading data.");
				}
				readBytes += br;
			} while (readBytes < len);
		} else {
			final int shift = BUFFER_CAPACITY - capacity;
			
			for(int i=0; i<len; i++) {
				b[i] = (byte) ((buffer << shift) | ((buffer = readDirectByte()) >> capacity));
			}

		}
	}
}
