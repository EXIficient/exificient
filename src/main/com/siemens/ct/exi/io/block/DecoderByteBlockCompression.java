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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import com.siemens.ct.exi.core.CompileConfiguration;
import com.siemens.ct.exi.datatype.decoder.TypeDecoder;
import com.siemens.ct.exi.io.channel.ByteDecoderChannel;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20080718
 */

public class DecoderByteBlockCompression extends
		AbstractDecoderByteBlockChannelized {
	protected InputStream resettableInputStream;
	private InflaterInputStream recentInflaterInputStream;

	private long bytesRead = 0;

	protected Inflater inflater;

	public DecoderByteBlockCompression(InputStream is, TypeDecoder typeDecoder)
			throws IOException {
		super(is, typeDecoder);
	}

	protected void init() throws IOException {
		if (false) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int b;
			while ((b = inputStream.read()) != -1) {
				baos.write(b);
			}
			baos.flush();

			resettableInputStream = new ByteArrayInputStream(baos.toByteArray());
		} else {
			resettableInputStream = new BufferedInputStream(inputStream);
			resettableInputStream.mark(Integer.MAX_VALUE);
		}

		structureChannel = getNextChannel();
	}

	protected ByteDecoderChannel getNextChannel() throws IOException {
		return new ByteDecoderChannel(getStream());
	}

	protected InputStream getStream() throws IOException {

		if (inflater == null) {
			// first stream (initialize inflater)
			inflater = new Inflater(CompileConfiguration.DEFLATE_NOWRAP);
		} else {
			if (!inflater.finished()) {
				// [Warning] Inflater not finished

				while (!inflater.finished()) {
					recentInflaterInputStream.read();
				}
			}

			// update new byte position
			bytesRead += inflater.getBytesRead();

			// reset byte position
			resettableInputStream.reset();
			resettableInputStream.skip(bytesRead);

			// reset inflater
			inflater.reset();
		}

		recentInflaterInputStream = new InflaterInputStream(
				resettableInputStream, inflater);

		return recentInflaterInputStream;
	}

}
