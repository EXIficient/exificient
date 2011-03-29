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
import com.siemens.ct.exi.datatype.RestrictedCharacterSetDatatype;
import com.siemens.ct.exi.datatype.strings.StringEncoder;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.values.Value;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.7
 */

public class LexicalTypeEncoder extends AbstractTypeEncoder {

	protected RestrictedCharacterSetDatatype rcsDatatype;
	protected Datatype lastDatatype;

	public LexicalTypeEncoder(StringEncoder stringEncoder) {
		super(stringEncoder);
		rcsDatatype = new RestrictedCharacterSetDatatype(null);
	}

	// public boolean isValid(Datatype datatype, String value) {
	// lastDatatype = datatype;
	// return datatype.isValidRCS(value);
	// }

	public boolean isValid(Datatype datatype, Value value) {
		lastDatatype = datatype;
		return datatype.isValidRCS(value.toString());
		// return this.isValid(datatype, value.toString());
	}

	public void writeValue(QName context, EncoderChannel valueChannel)
			throws IOException {
		lastDatatype.writeValueRCS(rcsDatatype, valueChannel, stringEncoder,
				context);
	}

}
