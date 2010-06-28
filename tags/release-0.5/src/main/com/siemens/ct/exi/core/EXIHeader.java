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

package com.siemens.ct.exi.core;

import java.io.IOException;
import java.io.OutputStream;

import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.io.channel.BitDecoderChannel;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.4.20080718
 */

public class EXIHeader {
	public static final int NUMBER_OF_DISTINGUISHING_BITS = 2;
	public static final int DISTINGUISHING_BITS_VALUE = 2;

	public static final int NUMBER_OF_FORMAT_VERSION_BITS = 4;
	public static final int FORMAT_VERSION_CONTINUE_VALUE = 15;

	public EXIHeader() {
	}

	public static void write(OutputStream os) throws EXIException {
		try {
			// TODO EXI Header options and so forth

			// 10 0 10000
			int b = 0x90;
			os.write(b);
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	public static void parse(BitDecoderChannel headerChannel)
			throws EXIException {
		try {
			// An EXI header starts with Distinguishing Bits part, which is a
			// two
			// bit field 1 0
			if (headerChannel
					.decodeNBitUnsignedInteger(NUMBER_OF_DISTINGUISHING_BITS) != DISTINGUISHING_BITS_VALUE) {
				throw new IOException(
						"No valid EXI document according distinguishing bits");
			}

			// Presence Bit for EXI Options
			boolean presenceOptions = headerChannel.decodeBoolean();

			// EXI Format Version (1 4+)

			// The first bit of the version field indicates whether the version
			// is a
			// preview or final version of the EXI format.
			// A value of 0 indicates this is a final version and a value of 1
			// indicates this is a preview version.
			@SuppressWarnings("unused")
			boolean previewVersion = headerChannel.decodeBoolean();

			// one or more 4-bit unsigned integers represent the version number
			// 1. Read next 4 bits as an unsigned integer value.
			// 2. Add the value that was just read to the version number.
			// 3. If the value is 15, go to step 1, otherwise (i.e. the value
			// being
			// in the range of 0-14),
			// use the current value of the version number as the EXI version
			// number.
			int value;
			int version = 0;
			do {
				value = headerChannel
						.decodeNBitUnsignedInteger(NUMBER_OF_FORMAT_VERSION_BITS);
				version += value;
			} while (value == FORMAT_VERSION_CONTINUE_VALUE);

			// [EXI Options] ?
			if (presenceOptions) {
				throw new RuntimeException(
						"[EXI Header] Options section not implemented yet");
			}

			// skip padding bits
			headerChannel.align();
		} catch (IOException e) {
			throw new EXIException(e);
		}

	}

}
