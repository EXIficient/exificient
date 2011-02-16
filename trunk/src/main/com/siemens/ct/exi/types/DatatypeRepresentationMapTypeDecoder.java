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

package com.siemens.ct.exi.types;

import java.io.IOException;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.datatype.strings.StringDecoder;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.Grammar;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.values.Value;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.6
 */

public class DatatypeRepresentationMapTypeDecoder extends
		AbstractRepresentationMapTypeCoder implements TypeDecoder {

	// fallback type decoder
	protected TypedTypeDecoder defaultDecoder;

	protected StringDecoder stringDecoder;

	public DatatypeRepresentationMapTypeDecoder(StringDecoder stringDecoder,
			QName[] dtrMapTypes, QName[] dtrMapRepresentations, Grammar grammar)
			throws EXIException {
		super(dtrMapTypes, dtrMapRepresentations, grammar);
		this.stringDecoder = stringDecoder;

		// hand over "same" string table
		defaultDecoder = new TypedTypeDecoder(stringDecoder);
	}

	public StringDecoder getStringDecoder() {
		return stringDecoder;
	}

	public void clear() {
		stringDecoder.clear();
	}

	public Value readValue(Datatype datatype, QName context,
			DecoderChannel valueChannel) throws IOException {
		QName schemaType = datatype.getSchemaType();
		Datatype recentDtrDataype = dtrMap.get(schemaType);
		if (recentDtrDataype == null) {
			return defaultDecoder.readValue(datatype, context, valueChannel);
		} else {
			return recentDtrDataype.readValue(valueChannel, stringDecoder,
					context);
		}
	}

}
