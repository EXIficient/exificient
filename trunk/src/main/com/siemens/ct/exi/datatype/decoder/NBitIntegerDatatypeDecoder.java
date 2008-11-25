/*
 * Copyright (C) 2007, 2008 Siemens AG
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

package com.siemens.ct.exi.datatype.decoder;

import java.io.IOException;

import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.datatype.DatatypeNBitInteger;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.util.datatype.XSDInteger;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.2.20081111
 */

public class NBitIntegerDatatypeDecoder extends AbstractDatatypeDecoder {
	private XSDInteger decodedValue = XSDInteger.newInstance();

	public String decodeValue(TypeDecoder decoder, Datatype datatype,
			DecoderChannel dc, String namespaceURI, String localName)
			throws IOException {
		assert (datatype instanceof DatatypeNBitInteger);
		DatatypeNBitInteger nBitDT = (DatatypeNBitInteger) datatype;

		// inverse decoded value
		decodedValue.setToIntegerValue((-1)
				* dc.decodeNBitUnsignedInteger(nBitDT.getNumberOfBits()));

		// add offset again (lower bound)
		return nBitDT.getLowerBound().subtract(decodedValue).toString();
	}
}