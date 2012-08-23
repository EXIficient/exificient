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

package com.siemens.ct.exi.datatype;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import junit.framework.TestCase;

import com.siemens.ct.exi.io.channel.BitDecoderChannel;
import com.siemens.ct.exi.io.channel.BitEncoderChannel;
import com.siemens.ct.exi.io.channel.ByteDecoderChannel;
import com.siemens.ct.exi.io.channel.ByteEncoderChannel;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.io.channel.EncoderChannel;

public abstract class AbstractTestCase extends TestCase {

	private ByteArrayOutputStream bitBaos;
	private ByteArrayOutputStream baos;

	public AbstractTestCase() {
	}

	public AbstractTestCase(String name) {
		super(name);
	}

	/*
	 * Bit - Mode
	 */
	protected OutputStream getBitOutputStream() {
		bitBaos = new ByteArrayOutputStream();
		return bitBaos;
	}

	protected InputStream getBitInputStream() throws IOException {
		bitBaos.flush();
		return new ByteArrayInputStream(bitBaos.toByteArray());
	}

	protected EncoderChannel getBitEncoder() {
		return new BitEncoderChannel(getBitOutputStream());
	}

	protected DecoderChannel getBitDecoder() throws IOException {
		return new BitDecoderChannel(getBitInputStream());
	}

	/*
	 * Byte - Mode
	 */
	protected EncoderChannel getByteEncoder() {
		this.baos = new ByteArrayOutputStream();
		return new ByteEncoderChannel(baos);
	}

	protected DecoderChannel getByteDecoder() throws IOException {
		return new ByteDecoderChannel(new ByteArrayInputStream(baos
				.toByteArray()));
	}

}
