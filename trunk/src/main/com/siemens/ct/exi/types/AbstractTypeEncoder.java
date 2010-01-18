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

	protected StringEncoder stringEncoder;
	
	public AbstractTypeEncoder(StringEncoder stringEncoder) {
		this.stringEncoder = stringEncoder;
	}
	
	public void finish() throws IOException {
	}
	
	public void clear() {
		stringEncoder.clear();
	}

	/*
	 * 
	 */
	public void setStringEncoder(StringEncoder stringEncoder) {
		this.stringEncoder = stringEncoder;
	}

	public StringEncoder getStringEncoder() {
		return stringEncoder;
	}

}
