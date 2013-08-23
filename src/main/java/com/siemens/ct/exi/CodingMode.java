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

package com.siemens.ct.exi;

import java.util.zip.Deflater;

/**
 * The alignment option is used to control the alignment of event codes and
 * content items.
 * 
 * <p>
 * The value is one of bit-packed, byte-alignment, pre-compression, or
 * compression.
 * </p>
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.1
 */

public enum CodingMode {
	/**
	 * Alignment option value bit-packed indicates that the the event codes and
	 * associated content are packed in bits without any paddings in-between.
	 */
	BIT_PACKED(false, false),

	/**
	 * Alignment option value byte-alignment indicates that the event codes and
	 * associated content are aligned on byte boundaries. While byte-alignment
	 * generally results in EXI streams of larger sizes compared with their
	 * bit-packed equivalents, byte-alignment may provide a help in some use
	 * cases that involve frequent copying of large arrays of scalar data
	 * directly out of the stream. It can also make it possible to work with
	 * data in-place and can make it easier to debug encoded data by allowing
	 * items on aligned boundaries to be easily located in the stream.
	 */
	BYTE_PACKED(false, false),

	/**
	 * Alignment option value pre-compression alignment indicates that all steps
	 * involved in compression are to be done with the exception of the final
	 * step of applying the DEFLATE algorithm.
	 */
	PRE_COMPRESSION(false, true),

	/**
	 * The compression option is used to increase compactness using additional
	 * computational resources (DEFLATE algorithm).
	 */
	COMPRESSION(true, true);

	private final boolean usesDeflate;
	private final boolean usesRechanneling;

	private int deflateLevel = Deflater.DEFAULT_COMPRESSION;

	private CodingMode(boolean deflate, boolean rechanneling) {
		usesDeflate = deflate;
		usesRechanneling = rechanneling;
	}

	public boolean usesDeflate() {
		return usesDeflate;
	}

	public int getDeflateLevel() {
		assert (usesDeflate);
		return deflateLevel;
	}

	public void setDeflateLevel(int deflateLevel) {
		assert (usesDeflate);
		this.deflateLevel = deflateLevel;
	}

	public boolean usesRechanneling() {
		return usesRechanneling;
	}
}
