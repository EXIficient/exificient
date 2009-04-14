/*
 * Copyright (C) 2007-2009 Siemens AG
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

package com.siemens.ct.exi.io.block;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;

import com.siemens.ct.exi.datatype.encoder.TypeEncoder;
import com.siemens.ct.exi.util.CompressionUtilities;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20080718
 */

public class EncoderByteBlockCompression extends
		AbstractEncoderByteBlockChannelized {
	private DeflaterOutputStream lastStream;

	public EncoderByteBlockCompression(OutputStream outputStream,
			TypeEncoder typeEncoder) {
		super(outputStream, typeEncoder);
	}

	@Override
	protected OutputStream getStream() throws IOException {

		// lastStream = new DeflaterOutputStream( outputStream, new Deflater(
		// Configuration.COMPRESSION_LEVEL ) );
		// return ( lastStream = BlockFactory.createDeflaterOutputStream (
		// outputStream ) );
		return (lastStream = CompressionUtilities
				.createDeflaterOutputStream(outputStream));
	}

	@Override
	protected void finalizeStream() throws IOException {
		lastStream.finish();
		lastStream.flush();
	}

}
