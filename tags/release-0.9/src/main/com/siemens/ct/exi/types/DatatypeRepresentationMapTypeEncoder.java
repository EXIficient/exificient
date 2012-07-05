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

import com.siemens.ct.exi.context.EncoderContext;
import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammars.Grammars;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.values.Value;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9
 */

public class DatatypeRepresentationMapTypeEncoder extends
		AbstractRepresentationMapTypeCoder implements TypeEncoder {

	// fallback type encoder
	protected TypeEncoder defaultEncoder;

	public DatatypeRepresentationMapTypeEncoder(TypeEncoder defaultEncoder,
			QName[] dtrMapTypes, QName[] dtrMapRepresentations, Grammars grammar)
			throws EXIException {
		super(dtrMapTypes, dtrMapRepresentations, grammar);

		// hand over "default" encoder
		this.defaultEncoder = defaultEncoder;
	}

	public boolean isValid(Datatype datatype, Value value) {
		QName schemaType = datatype.getSchemaType();
		recentDtrDataype = dtrMap.get(schemaType);

		return defaultEncoder.isValid(recentDtrDataype == null ? datatype
				: recentDtrDataype, value);
	}

	public void writeValue(EncoderContext encoderContext,
			QNameContext qnContext, EncoderChannel valueChannel)
			throws IOException {
		defaultEncoder.writeValue(encoderContext, qnContext, valueChannel);
	}

}
