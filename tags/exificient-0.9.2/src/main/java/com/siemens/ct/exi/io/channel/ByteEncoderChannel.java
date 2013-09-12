/*
 * Copyright (C) 2007-2012 Siemens AG
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

import java.io.IOException;
import java.io.OutputStream;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.2
 */

final public class ByteEncoderChannel extends AbstractEncoderChannel implements
		EncoderChannel {
	private final OutputStream os;
	protected int len = 0;

	/**
	 * Construct a byte aligned encoder from output stream.
	 */
	public ByteEncoderChannel(OutputStream os) {
		this.os = os;
	}

	public OutputStream getOutputStream() {
		return os;
	}

	public int getLength() {
		return len;
	}

	public void flush() throws IOException {
		os.flush();
	}

	public void align() throws IOException {
		// already byte aligned
	}

	public void encode(int b) throws IOException {
		os.write(b);
		len++;
	}

	public void encode(byte b[], int off, int len) throws IOException {
		os.write(b, off, len);
		this.len += len;
	}

	/**
	 * Encode a single boolean value. A false value is encoded as byte 0 and
	 * true value is encode as byte 1.
	 */
	public void encodeBoolean(boolean b) throws IOException,
			IllegalArgumentException {
		encode(b ? 1 : 0);
	}

	/**
	 * Encode n-bit unsigned integer using the minimum number of bytes required
	 * to store n bits. The n least significant bits of parameter b starting
	 * with the most significant, i.e. from left to right.
	 */
	public void encodeNBitUnsignedInteger(int b, int n) throws IOException {
		if (b < 0 || n < 0) {
			throw new IllegalArgumentException(
					"Negative value as unsigned integer!");
		}
		assert (b >= 0);
		assert (n >= 0);

		if (n == 0) {
			// 0 bytes
		} else if (n < 9) {
			// 1 byte
			encode(b & 0xff);
		} else if (n < 17) {
			// 2 bytes
			encode(b & 0x00ff);
			encode((b & 0xff00) >> 8);
		} else if (n < 25) {
			// 3 bytes
			encode(b & 0x0000ff);
			encode((b & 0x00ff00) >> 8);
			encode((b & 0xff0000) >> 16);
		} else if (n < 33) {
			// 4 bytes
			encode(b & 0x000000ff);
			encode((b & 0x0000ff00) >> 8);
			encode((b & 0x00ff0000) >> 16);
			encode((b & 0xff000000) >> 24);
		} else {
			throw new RuntimeException(
					"Currently not more than 4 Bytes allowed for NBitUnsignedInteger!");
		}
	}

}
