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

package com.siemens.ct.exi.datatype.encoder;

import java.io.IOException;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.datatype.AbstractTypeCoder;
import com.siemens.ct.exi.datatype.strings.StringEncoder;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20080718
 */

public abstract class AbstractTypeEncoder extends AbstractTypeCoder implements
		TypeEncoder {

	public AbstractTypeEncoder(EXIFactory exiFactory) {
	}
	public void finish() throws IOException {
	}

	/*
	 * 
	 * 
	 * XXXXXXXXXXXX
	 */
	protected StringEncoder stringEncoder;

	public void setStringEncoder(StringEncoder stringEncoder) {
		this.stringEncoder = stringEncoder;
	}

	public StringEncoder getStringEncoder() {
		return stringEncoder;
	}


}
