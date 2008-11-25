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

package com.siemens.ct.exi.datatype.decoder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.datatype.DatatypeRepresentation;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.util.ExpandedName;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.2.20081105
 */

public class TypeDecoderDatatypeRepresentationMap extends AbstractTypeDecoder
		implements TypeDecoderRepresentationMap {
	// fallback type decoder
	private TypeDecoderTyped defaultDecoder;

	private Map<ExpandedName, DatatypeDecoder> userDefinedDatatypeRepresentations;

	public TypeDecoderDatatypeRepresentationMap(EXIFactory exiFactory) {
		super(exiFactory);

		defaultDecoder = new TypeDecoderTyped(exiFactory);
		userDefinedDatatypeRepresentations = new HashMap<ExpandedName, DatatypeDecoder>();
	}

	public void registerDatatypeRepresentation(
			DatatypeRepresentation datatypeRepresentation) {
		userDefinedDatatypeRepresentations.put(datatypeRepresentation
				.getSchemaDatatype(), datatypeRepresentation);
	}

	public String readTypeValidValue(Datatype datatype, DecoderChannel dc,
			final String namespaceURI, final String localName)
			throws IOException {
		if (userDefinedDatatypeRepresentations.containsKey(datatype
				.getDatatypeIdentifier())) {
			// System.out.println ( "[DEC] Pluggable Codec in use!" );
			DatatypeDecoder dec = userDefinedDatatypeRepresentations
					.get(datatype.getDatatypeIdentifier());
			return dec.decodeValue(this, datatype, dc, namespaceURI, localName);
		} else {
			return defaultDecoder.readTypeValidValue(datatype, dc,
					namespaceURI, localName);
		}
	}

}
