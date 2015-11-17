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

import java.io.IOException;
import java.io.OutputStream;

/**
 * Write bits and bytes to an underlying output stream.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.6-SNAPSHOT
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
	 * Fully-written bytes 
	 */
	protected int len;

	/**
	 * Constructs an instance of this class.
	 * 
	 * @param ostream output stream
	 */
	public BitOutputStream(OutputStream ostream) {
		this.ostream = ostream;
		this.len = 0;
	}

	/**
	 * Returns a reference to underlying output stream.
	 * 
	 * @return underlying output stream
	 */
	public OutputStream getUnderlyingOutputStream() {
		return ostream;
	}
	
	/**
	 * Returns the number of bytes written.
	 * 
	 * @return number of bytes
	 */
	public int getLength() {
		return len;
	}

	/**
	 * If buffer is full, write it out and reset internal state.
	 * 
	 * @throws IOException IO exception
	 */
	protected void flushBuffer() throws IOException {
		if (capacity == 0) {
			ostream.write(buffer);
			capacity = BITS_IN_BYTE;
			buffer = 0;
			len++;
		}
	}

	/**
	 * Returns true if stream is on a byte boundary, i.e. if no bits have been
	 * buffered since the last byte was written to underlying stream.
	 * 
	 * @return whether stream is aligned
	 */
	public boolean isByteAligned() {
		return (capacity == BITS_IN_BYTE);
	}

	/**
	 * Returns the number of bits that haven't been flushed. When the buffer is
	 * full, it is automatically flushed, so the number returned by this method
	 * is in [0, 7].
	 * 
	 * @return bits in buffer
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
	 * 
	 * @throws IOException IO exception
	 */
	public void align() throws IOException {
		if (capacity < BITS_IN_BYTE) {
			ostream.write(buffer << capacity);
			capacity = BITS_IN_BYTE;
			buffer = 0;
			len++;
		}
	}

	/**
	 * Write a single bit 0.
	 * 
	 * @throws IOException IO exception
	 */
	public void writeBit0() throws IOException {
		buffer <<= 1;
		capacity--;
		flushBuffer();
	}

	/**
	 * Write a single bit 1.
	 * 
	 * @throws IOException IO exception
	 */
	public void writeBit1() throws IOException {
		buffer = (buffer << 1) | 0x1;
		capacity--;
		flushBuffer();
	}

	/**
	 * Write the least significant bit of parameter b into the internal buffer,
	 * flushing it if necessary.
	 * 
	 * @param b bit
	 * @throws IOException IO exception
	 */
	protected void writeBit(int b) throws IOException {
		buffer = (buffer << 1) | (b & 0x1);
		capacity--;
		flushBuffer();
	}

	/**
	 * Write the n least significant bits of parameter b starting with the most
	 * significant, i.e. from left to right.
	 * 
	 * @param b bits
	 * @param n number of bits
	 * @throws IOException IO exception
	 */
	public void writeBits(int b, int n) throws IOException {
		if (n <= capacity) {
			// all bits fit into the current buffer
			buffer = (buffer << n) | (b & (0xff >> (BITS_IN_BYTE - n)));
			capacity -= n;
			if (capacity == 0) {
				ostream.write(buffer);
				capacity = BITS_IN_BYTE;
				len++;
			}
		} else {
			// fill as many bits into buffer as possible
			buffer = (buffer << capacity)
					| ((b >>> (n - capacity)) & (0xff >> (BITS_IN_BYTE - capacity)));
			n -= capacity;
			ostream.write(buffer);
			len++;

			// possibly write whole bytes
			while (n >= 8) {
				n -= 8;
				ostream.write(b >>> n);
				len++;
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
	 * 
	 * @param b byte
	 * @throws IOException IO exception
	 */
	protected void writeDirectByte(int b) throws IOException {
		ostream.write(b);
		len++;
	}

	/**
	 * Ignore current buffer, and write a sequence of bytes directly to the
	 * underlying stream.
	 * 
	 * @param b byte array
	 * @param off byte array offset
	 * @param len byte array length
	 * @throws IOException IO exception
	 */
	protected void writeDirectBytes(byte[] b, int off, int len)
			throws IOException {
		ostream.write(b, off, len);
		len += len;
	}

	@Override
	public void write(int b) throws IOException {
		this.writeBits(b, 8);

	}
}
