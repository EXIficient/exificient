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

import com.siemens.ct.exi.core.NameContext;
import com.siemens.ct.exi.io.channel.EncoderChannel;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20090421
 */

public class TypeEncoderLexical extends AbstractTypeEncoder {
	
	protected DatatypeRestrictedCharacterSet rcsDatatype;
	protected Datatype lastDatatype;
	
	public TypeEncoderLexical() {
		super();
		rcsDatatype = new DatatypeRestrictedCharacterSet(null);
	}

	public boolean isValid(Datatype datatype, String value) {
		lastDatatype = datatype;
		return datatype.isValidRCS(value);
	}
	
	public void writeValue(NameContext context, EncoderChannel valueChannel) throws IOException {
		lastDatatype.writeValueRCS(rcsDatatype, valueChannel, stringEncoder, context);
	}
}
