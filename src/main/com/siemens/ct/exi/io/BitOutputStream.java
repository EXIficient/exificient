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

import java.io.IOException;
import java.io.OutputStream;

/**
 * Write bits and bytes to an underlying output stream.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.6
 */

public class BitOutputStream extends OutputStream {
	public final static int BITS_IN_BYTE = 8;

	/**
	 * Internal buffer represented as an int. Only the least significant byte is
	 * used. An int is used instead of a byte int-to-byte conversions in the VM.
	 */
	protected int buffer = 0;

	/**
	 * Unused capacity of the internal buffer in bits.
	 */
	protected int capacity = BITS_IN_BYTE;

	/**
	 * Underlying output stream to which bits and bytes are written.
	 */
	private OutputStream ostream;

	/**
	 * Constructs an instance of this class.
	 */
	public BitOutputStream(OutputStream ostream) {
		this.ostream = ostream;
	}

	/**
	 * Returns a reference to underlying output stream.
	 */
	public OutputStream getUnderlyingOutputStream() {
		return ostream;
	}

	/**
	 * If buffer is full, write it out and reset internal state.
	 */
	protected void flushBuffer() throws IOException {
		if (capacity == 0) {
			ostream.write(buffer);
			capacity = BITS_IN_BYTE;
			buffer = 0;
		}
	}

	/**
	 * Returns true if stream is on a byte boundary, i.e. if no bits have been
	 * buffered since the last byte was written to underlying stream.
	 */
	public boolean isByteAligned() {
		return (capacity == BITS_IN_BYTE);
	}

	/**
	 * Returns the number of bits that haven't been flushed. When the buffer is
	 * full, it is automatically flushed, so the number returned by this method
	 * is in [0, 7].
	 */
	public int getBitsInBuffer() {
		return (BITS_IN_BYTE - capacity);
	}

	/**
	 * If there are some unwritten bits, pad them if necessary and write them
	 * out. Note that this method does flush the underlying stream.
	 */
	public void flush() throws IOException {
		align();
		ostream.flush();
	}

	/**
	 * If there are some unwritten bits, pad them if necessary and write them
	 * out.
	 */
	public void align() throws IOException {
		if (capacity < BITS_IN_BYTE) {
			ostream.write(buffer << capacity);
			capacity = BITS_IN_BYTE;
			buffer = 0;
		}
	}

	/**
	 * Write a single bit 0.
	 */
	public void writeBit0() throws IOException {
		buffer <<= 1;
		capacity--;
		flushBuffer();
	}

	/**
	 * Write a single bit 1.
	 */
	public void writeBit1() throws IOException {
		buffer = (buffer << 1) | 0x1;
		capacity--;
		flushBuffer();
	}

	/**
	 * Write the least significant bit of parameter b into the internal buffer,
	 * flushing it if necessary.
	 */
	protected void writeBit(int b) throws IOException {
		buffer = (buffer << 1) | (b & 0x1);
		capacity--;
		flushBuffer();
	}

	/**
	 * Write the n least significant bits of parameter b starting with the most
	 * significant, i.e. from left to right.
	 */
	public void writeBits(int b, int n) throws IOException {
		if (n <= capacity) {
			// all bits fit into the current buffer
			buffer = (buffer << n) | (b & (0xff >> (BITS_IN_BYTE - n)));
			capacity -= n;
			if (capacity == 0) {
				ostream.write(buffer);
				capacity = BITS_IN_BYTE;
			}
		} else {
			// fill as many bits into buffer as possible
			buffer = (buffer << capacity)
					| ((b >>> (n - capacity)) & (0xff >> (BITS_IN_BYTE - capacity)));
			n -= capacity;
			ostream.write(buffer);

			// possibly write whole bytes
			while (n >= 8) {
				n -= 8;
				ostream.write(b >>> n);
			}

			// put the rest of bits into the buffer
			buffer = b; // Note: the high bits will be shifted out during
			// further filling
			capacity = BITS_IN_BYTE - n;
		}
	}

	/**
	 * Ignore current buffer, and write a byte directly to the underlying
	 * stream.
	 */
	protected void writeDirectByte(int b) throws IOException {
		ostream.write(b);
	}

	/**
	 * Ignore current buffer, and write a sequence of bytes directly to the
	 * underlying stream.
	 */
	protected void writeDirectBytes(byte[] b, int off, int len)
			throws IOException {
		ostream.write(b, off, len);
	}

	@Override
	public void write(int b) throws IOException {
		this.writeBits(b, 8);

	}
}
