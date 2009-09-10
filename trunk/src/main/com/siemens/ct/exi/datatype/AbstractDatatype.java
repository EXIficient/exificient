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

import com.siemens.ct.exi.core.Context;
import com.siemens.ct.exi.datatype.charset.RestrictedCharacterSet;
import com.siemens.ct.exi.datatype.strings.StringDecoder;
import com.siemens.ct.exi.datatype.strings.StringEncoder;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.types.BuiltInType;
import com.siemens.ct.exi.util.ExpandedName;

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
	protected ExpandedName datatypeIdentifier;
	// restricted char set
	protected RestrictedCharacterSet rcs;
	protected String lastRCSValue;

	public AbstractDatatype(BuiltInType builtInType,
			ExpandedName datatypeIdentifier) {
		this.defaultbuiltInType = builtInType;
		this.datatypeIdentifier = datatypeIdentifier;
	}

	public BuiltInType getDefaultBuiltInType() {
		return defaultbuiltInType;
	}

	public ExpandedName getDatatypeIdentifier() {
		return datatypeIdentifier;
	}
	
	public RestrictedCharacterSet getRestrictedCharacterSet() {
		return rcs;
	}

	public boolean equals(Object o) {
		if (o instanceof Datatype) {
			return (this.getDefaultBuiltInType() == ((Datatype) o)
					.getDefaultBuiltInType());
		} else {
			return false;
		}
	}
	
	public boolean isValidRCS(String value) {
		lastRCSValue = value;
		return true;
	}
	
	public void writeValueRCS(RestrictedCharacterSetDatatype rcsEncoder, EncoderChannel valueChannel, StringEncoder stringEncoder, Context context) throws IOException {
		rcsEncoder.setRestrictedCharacterSet(rcs);
		rcsEncoder.isValid(lastRCSValue);
		rcsEncoder.writeValue(valueChannel, stringEncoder, context);
	}
	
	public char[] readValueRCS(RestrictedCharacterSetDatatype rcsDecoder, DecoderChannel valueChannel, StringDecoder stringDecoder, Context context) throws IOException {
		rcsDecoder.setRestrictedCharacterSet(rcs);
		return rcsDecoder.readValue(valueChannel, stringDecoder, context);
	}

	public String toString() {
		return defaultbuiltInType.toString();
	}
}
