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

package com.siemens.ct.exi.core;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.siemens.ct.exi.CodingMode;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.event.EventType;
import com.siemens.ct.exi.io.channel.BitDecoderChannel;
import com.siemens.ct.exi.io.channel.ByteDecoderChannel;
import com.siemens.ct.exi.io.channel.DecoderChannel;

/**
 * EXI decoder for bit or byte-aligned streams.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.6
 */

public class EXIBodyDecoderInOrder extends AbstractEXIBodyDecoder {

	public EXIBodyDecoderInOrder(EXIFactory exiFactory) throws EXIException {
		super(exiFactory);
	}

	public void setInputStream(InputStream is) throws EXIException, IOException {

		// buffer stream if not already
		// TODO is there a *nice* way to detect whether a stream is buffered
		// already
		if (!(is instanceof BufferedInputStream)) {
			is = new BufferedInputStream(is);
		}

		CodingMode codingMode = exiFactory.getCodingMode();

		// setup data-stream only
		if (codingMode == CodingMode.BIT_PACKED) {
			// create new bit-aligned channel
			setInputChannel(new BitDecoderChannel(is));
		} else {
			assert (codingMode == CodingMode.BYTE_PACKED);
			// create new byte-aligned channel
			setInputChannel(new ByteDecoderChannel(is));
		}

		initForEachRun();
	}

	public void setInputChannel(DecoderChannel decoderChannel)
			throws EXIException, IOException {
		this.channel = decoderChannel;
		// this.is = decoderChannel.geInputStream();

		initForEachRun();
	}

	public DecoderChannel getChannel() {
		return this.channel;
	}

	@Override
	protected void initForEachRun() throws EXIException, IOException {
		super.initForEachRun();

		nextEvent = null;
		nextEventType = EventType.START_DOCUMENT;
	}

	public EventType next() throws EXIException, IOException {
		if (nextEventType == EventType.END_DOCUMENT) {
			// last was ED --> no more events
			return null;
		} else {
			// decode event code
			decodeEventCode();
			return nextEventType;
		}
	}

}
