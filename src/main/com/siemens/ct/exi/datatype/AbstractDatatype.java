/*
 * Copyright (C) 2007-2009 Siemens AG
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

import com.siemens.ct.exi.datatype.charset.RestrictedCharacterSet;
import com.siemens.ct.exi.datatype.strings.StringDecoder;
import com.siemens.ct.exi.datatype.strings.StringEncoder;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.types.BuiltInType;
import com.siemens.ct.exi.values.Value;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20081111
 */

public abstract class AbstractDatatype implements Datatype {
	// default built-in datatype (no codec map used)
	protected BuiltInType defaultbuiltInType;
	// for codec map (pluggable codecs)
	protected QName datatypeIdentifier;
	// restricted char set
	protected RestrictedCharacterSet rcs;
	protected String lastRCSValue;

	public AbstractDatatype(BuiltInType builtInType,
			QName datatypeIdentifier) {
		this.defaultbuiltInType = builtInType;
		this.datatypeIdentifier = datatypeIdentifier;
	}

	public BuiltInType getDefaultBuiltInType() {
		return defaultbuiltInType;
	}

	public QName getDatatypeIdentifier() {
		return datatypeIdentifier;
	}
	
	public RestrictedCharacterSet getRestrictedCharacterSet() {
		return rcs;
	}

	public boolean equals(Object o) {
		if (o instanceof Datatype) {
			return (defaultbuiltInType == ((Datatype) o)
					.getDefaultBuiltInType());
		} else {
			return false;
		}
	}
	
	public int hashCode() {
		return defaultbuiltInType.ordinal();
	}
	
	public boolean isValidRCS(String value) {
		lastRCSValue = value;
		return true;
	}
	
	public void writeValueRCS(RestrictedCharacterSetDatatype rcsEncoder, EncoderChannel valueChannel, StringEncoder stringEncoder, QName context) throws IOException {
		rcsEncoder.setRestrictedCharacterSet(rcs);
		rcsEncoder.isValid(lastRCSValue);
		rcsEncoder.writeValue(valueChannel, stringEncoder, context);
	}
	
	public Value readValueRCS(RestrictedCharacterSetDatatype rcsDecoder, DecoderChannel valueChannel, StringDecoder stringDecoder, QName context) throws IOException {
		rcsDecoder.setRestrictedCharacterSet(rcs);
		return rcsDecoder.readValue(valueChannel, stringDecoder, context);
	}

	public String toString() {
		return defaultbuiltInType.toString();
	}
}
