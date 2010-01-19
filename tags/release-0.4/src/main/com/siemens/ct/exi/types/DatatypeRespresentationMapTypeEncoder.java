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

package com.siemens.ct.exi.types;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.datatype.strings.StringEncoder;
import com.siemens.ct.exi.io.channel.EncoderChannel;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.4.20080718
 */

public class DatatypeRespresentationMapTypeEncoder extends AbstractTypeEncoder {
//	// fallback type encoder
	@SuppressWarnings("unused")
	private TypedTypeEncoder defaultEncoder;
//	private boolean usePluggableCodec;
//	private DatatypeRepresentation lastPluggableEncoder;

	
	private Map<QName, DatatypeRepresentation> userDefinedDatatypeRepresentations;


	public DatatypeRespresentationMapTypeEncoder(StringEncoder stringEncoder) {
		// super( true );
		super(stringEncoder);

		userDefinedDatatypeRepresentations = new HashMap<QName, DatatypeRepresentation>();

		// hand over "same" string table
		// defaultEncoder = new TypeEncoderTyped(exiFactory, this.stringTable);
		// defaultEncoder = new TypeEncoderTyped(exiFactory, null);
		defaultEncoder = new TypedTypeEncoder(stringEncoder);
	}

	public void registerDatatypeRepresentation(
			DatatypeRepresentation datatypeRepresentation) {
		// pluggableCodecs.put ( datatypeIdentifier, datatypeEncoder );
		userDefinedDatatypeRepresentations.put(datatypeRepresentation
				.getSchemaDatatype(), datatypeRepresentation);
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
	
	
	public boolean isValid(Datatype datatype, String value) {
		throw new RuntimeException("TODO TyoeEncoder DatatypeRespresentationMap");
	}
	
	public void writeValue(QName context, EncoderChannel valueChannel) throws IOException {
		throw new RuntimeException("TODO TyoeEncoder DatatypeRespresentationMap");
	}

}
