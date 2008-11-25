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

package com.siemens.ct.exi.datatype.encoder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.datatype.DatatypeRepresentation;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.util.ExpandedName;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20080718
 */

public class TypeEncoderDatatypeRespresentationMap extends AbstractTypeEncoder {
	// fallback type encoder
	private TypeEncoderTyped defaultEncoder;

	private Map<ExpandedName, DatatypeRepresentation> userDefinedDatatypeRepresentations;
	
	private boolean usePluggableCodec;

	private DatatypeRepresentation lastPluggableEncoder;

	public TypeEncoderDatatypeRespresentationMap(EXIFactory exiFactory) {
		// super( true );
		super(exiFactory);
		
		userDefinedDatatypeRepresentations = new HashMap<ExpandedName, DatatypeRepresentation>();

		// hand over "same" string table
		defaultEncoder = new TypeEncoderTyped(exiFactory, this.stringTable);
	}

	public void registerDatatypeRepresentation(
			DatatypeRepresentation datatypeRepresentation) {
		// pluggableCodecs.put ( datatypeIdentifier, datatypeEncoder );
		userDefinedDatatypeRepresentations.put(datatypeRepresentation
				.getSchemaDatatype(), datatypeRepresentation);
	}

	public boolean isTypeValid(Datatype datatype, String value) {
		if (userDefinedDatatypeRepresentations.containsKey(datatype
				.getDatatypeIdentifier())) {
			// use pluggable codecs
			usePluggableCodec = true;

			// System.out.println ( "[ENC] Pluggable Codec in use for '" + value
			// + "'!" );

			lastPluggableEncoder = userDefinedDatatypeRepresentations
					.get(datatype.getDatatypeIdentifier());
			return lastPluggableEncoder.isValid(datatype, value);
		} else {
			// use default EXI codecs
			usePluggableCodec = false;
			lastPluggableEncoder = null;

			return defaultEncoder.isTypeValid(datatype, value);
		}
	}

	// first isValueTypeValid has to be called
	public void writeTypeValidValue(EncoderChannel valueChannel, String uri,
			String localName) throws IOException {
		if (usePluggableCodec) {
			lastPluggableEncoder.writeValue(valueChannel, uri, localName);
		} else {
			defaultEncoder.writeTypeValidValue(valueChannel, uri, localName);
		}
	}

	@Override
	public void finish() throws IOException {
		// finalize all *used* pluggable codecs
		Iterator<DatatypeRepresentation> iter = userDefinedDatatypeRepresentations
				.values().iterator();

		while (iter.hasNext()) {
			iter.next().finish();
		}

	}

}
