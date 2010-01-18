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

import java.io.IOException;
import java.io.OutputStream;

import com.siemens.ct.exi.io.BitOutputStream;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20080718
 */
public class BitEncoderChannel extends AbstractEncoderChannel implements
		EncoderChannel {
	/**
	 * Underlying bit output stream to which bits and bytes are written.
	 */
	protected BitOutputStream ostream;

	/**
	 * Construct an encoder from output stream.
	 */
	public BitEncoderChannel(OutputStream ostream) {
		this.ostream = new BitOutputStream(ostream);
	}

	public OutputStream getOutputStream() {
		return ostream;
	}

	/**
	 * Flush underlying bit output stream.
	 */
	public void flush() throws IOException {
		ostream.flush();
	}
	
	public void align() throws IOException {
		ostream.align();
	}

	public void encode(int b) throws IOException {
		ostream.writeBits(b, 8);
	}

	public void encode(byte b[], int off, int len) throws IOException {
		// TODO write whole bytes (if possible)
		for (int i = off; i < (off + len); i++) {
			ostream.writeBits(b[i], 8);
		}
	}

	/**
	 * Encode n-bit unsigned integer. The n least significant bits of parameter
	 * b starting with the most significant, i.e. from left to right.
	 */
	public void encodeNBitUnsignedInteger(int b, int n) throws IOException {
		if (b < 0 || n < 0) {
			throw new IllegalArgumentException(
					"Encode negative value as unsigned integer is invalid!");
		}
		assert (b >= 0);
		assert (n >= 0);

		ostream.writeBits(b, n);
	}

	/**
	 * Encode a single boolean value. A false value is encoded as bit 0 and true
	 * value is encode as bit 1.
	 */
	public void encodeBoolean(boolean b) throws IOException {
		if (b) {
			ostream.writeBit1();
		} else {
			ostream.writeBit0();
		}

		// ostream.writeBit(b ? 1 : 0);
	}
}
