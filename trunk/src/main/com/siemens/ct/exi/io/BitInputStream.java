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
 * @version 0.6
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
	 *  ???
	 */
	private int mask = 0xFF;

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
		if (capacity == 0) {
			if ((buffer = istream.read()) == -1) {
				throw new EOFException(
						"Premature EOS found while reading data.");
			}
			capacity = BUFFER_CAPACITY;
			mask = 0xFF;
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
	 * Returns current byte buffer without actually reading data
	 * 
	 * @throws IOException
	 */
	public int lookAhead() throws IOException {
		readBuffer();
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
			//	aligned
			while(n != 0) {
				n -= istream.skip(n);
			}
		} else {
			// not aligned, grrr
			for(int i=0; i<n; n++) {
				readBits(8);
			}
		}
	}

	/**
	 * Return next bit from underlying stream.
	 */
	public int readBit() throws IOException {
		readBuffer();
		// return (buffer >>> --capacity) & 0x1;
		mask = mask >>> 1;
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

		readBuffer();
		int result;
		
		if (n <= capacity) {		
			// buffer already holds all necessary bits
			capacity -= n;
			result = ((buffer & mask ) >> capacity);
			mask = mask >>> n;
		} else {
			// get as many bits from buffer as possible
			n -= capacity;
			capacity = 0;
			result = (buffer & mask);
			
			// possibly read whole bytes
			while (n >= 8) {
				n -= BUFFER_CAPACITY;
				result = (result << 8) | readDirectByte();
			}

			// read remaining bits
			if (n > 0) {
				readBuffer();
				capacity -= n;
				result = (result << n) | ((buffer & mask ) >> capacity);
				mask = mask >>> n;
			}
		}
		return result;
	}
	public void read(byte b[], int off, int len) throws IOException {
		if ( len == 0) {
			
		} else if ( capacity == 0 ) {
			// byte-aligned --> read all bytes at byte-border (at once?)
			int readBytes = 0;
			do {
				readBytes += istream.read(b, readBytes, len-readBytes);
			} while(readBytes < len);
		} else {
			// get as many bits from buffer as possible
			int remBits = 8 - capacity;
			int b1 = (buffer & mask);
			
			// read whole bytes
			for(int i=0; i<(len-1); i++) {
				int b2 = readDirectByte();
				b[off+i] =(byte) ( (b1 << remBits) | ( b2 >>> capacity) );
				b1 = b2;
			}
			
			capacity = 0;

			// read remaining bits
			readBuffer();
			capacity -= remBits;
			b[off+len-1] = (byte)( (b1 << remBits) | ((buffer & mask ) >> capacity));
			mask = mask >>> remBits;
			
//			for(int i=0; i<len; i++) {
//				b[off+i] = (byte) readBits(8);
//			}
		}
	}

	/**
	 * Read and return the next byte without discarding current buffer.
	 */
	public final int readDirectByte() throws IOException {
		return istream.read();
	}

}
