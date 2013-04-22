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

import java.io.IOException;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.datatype.charset.RestrictedCharacterSet;
import com.siemens.ct.exi.datatype.strings.StringDecoder;
import com.siemens.ct.exi.datatype.strings.StringEncoder;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.types.BuiltInType;
import com.siemens.ct.exi.values.Value;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.1
 */

public abstract class AbstractDatatype implements Datatype {

	private static final long serialVersionUID = 682257950812949619L;

	// default built-in datatype (no dtr map used)
	protected final BuiltInType builtInType;

	// for codec map
	protected final QName schemaType;

	// restricted char set
	protected RestrictedCharacterSet rcs;
	protected Value lastRCSValue;

	public AbstractDatatype() {
		this(null, null);
	}

	public AbstractDatatype(BuiltInType builtInType, QName schemaType) {
		this.builtInType = builtInType;
		this.schemaType = schemaType;
	}

	public BuiltInType getBuiltInType() {
		return builtInType;
	}

	public QName getSchemaType() {
		return schemaType;
	}

	public RestrictedCharacterSet getRestrictedCharacterSet() {
		return rcs;
	}

	public boolean equals(Object o) {
		if (o instanceof Datatype) {
			return (builtInType == ((Datatype) o).getBuiltInType());
		} else {
			return false;
		}
	}

	public int hashCode() {
		return builtInType.ordinal();
	}

	public boolean isValidRCS(Value value) {
		lastRCSValue = value;
		return true;
	}

	public void writeValueRCS(RestrictedCharacterSetDatatype rcsEncoder,
			QNameContext qnContext, EncoderChannel valueChannel,
			StringEncoder stringEncoder) throws IOException {
		rcsEncoder.setRestrictedCharacterSet(rcs);
		rcsEncoder.isValid(lastRCSValue);
		rcsEncoder.writeValue(qnContext, valueChannel, stringEncoder);
	}

	public Value readValueRCS(RestrictedCharacterSetDatatype rcsDecoder,
			QNameContext qnContext, DecoderChannel valueChannel,
			StringDecoder stringDecoder) throws IOException {
		rcsDecoder.setRestrictedCharacterSet(rcs);
		return rcsDecoder.readValue(qnContext, valueChannel, stringDecoder);
	}

	public String toString() {
		return builtInType.toString();
	}
}
