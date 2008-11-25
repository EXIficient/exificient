/*
 * Copyright (C) 2007, 2008 Siemens AG
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
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.datatype.decoder.TypeDecoder;
import com.siemens.ct.exi.io.channel.BitDecoderChannel;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.util.ExpandedName;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.2.20080718
 */

public class DecoderBitBlock extends AbstractDecoderBlock {
	private BitDecoderChannel bdc;

	public DecoderBitBlock(InputStream is, TypeDecoder typeDecoder)
			throws IOException {
		super(is, typeDecoder);
	}

	@Override
	protected void init() {
		this.bdc = new BitDecoderChannel(inputStream);
	}

	public DecoderChannel getStructureChannel() {
		return bdc;
	}

	public DecoderChannel getValueChannel(String namespaceURI, String localName) {
		return bdc;
	}

	public void reconstructChannels(int values, List<ExpandedName> valueQNames,
			Map<ExpandedName, List<Datatype>> dataTypes,
			Map<ExpandedName, Integer> occurrences) {
		// unused!
		throw new UnsupportedOperationException("Not allowed!");
	}
}
