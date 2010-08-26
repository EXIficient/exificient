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

package com.siemens.ct.exi.values;

import org.apache.xerces.impl.dv.util.HexBin;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.5
 */

public class BinaryHexValue extends AbstractBinaryValue {

	private static final long serialVersionUID = 4914135099644891193L;

	public BinaryHexValue(byte[] bytes) {
		super(bytes);
	}

	public static byte[] parse(String val) {
		return HexBin.decode(val);
	}

	protected void initString() {
		sValue = HexBin.encode(bytes);
		slen = sValue.length();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof BinaryHexValue) {
			return _equals(((BinaryHexValue) o).bytes);
		} else if (o instanceof String) {
			byte[] b = BinaryHexValue.parse((String) o);
			if (b == null) {
				return false;
			} else {
				return _equals(b);
			}
		} else {
			return false;
		}
	}

}
