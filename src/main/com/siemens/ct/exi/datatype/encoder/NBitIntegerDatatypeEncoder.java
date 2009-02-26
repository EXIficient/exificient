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

import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.datatype.DatatypeNBitInteger;
import com.siemens.ct.exi.exceptions.XMLParsingException;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.util.datatype.XSDInteger;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.2.20081111
 */

public class NBitIntegerDatatypeEncoder extends AbstractDatatypeEncoder
		implements DatatypeEncoder {
	private XSDInteger lastNBitInteger = XSDInteger.newInstance();
	private int valueToEncode;
	private int numberOfBits;

	public NBitIntegerDatatypeEncoder(TypeEncoder typeEncoder) {
		super(typeEncoder);
	}

	public boolean isValid(Datatype datatype, String value) {
		try {
			lastNBitInteger.parse(value);

			assert (datatype instanceof DatatypeNBitInteger);
			DatatypeNBitInteger nBitDT = (DatatypeNBitInteger) datatype;

			// check lower & upper bound
			if (lastNBitInteger.compareTo(nBitDT.getLowerBound()) >= 0
					&& lastNBitInteger.compareTo(nBitDT.getUpperBound()) <= 0) {
				// calculate offset & update value
				// Note: integer cast is possible since bounded range of integer
				// is 4095 or smaller
				valueToEncode = lastNBitInteger
						.subtract(nBitDT.getLowerBound()).getIntInteger();
				numberOfBits = nBitDT.getNumberOfBits();

				return true;
			} else {
				return false;
			}
		} catch (XMLParsingException e) {
			return false;
		}
	}

	public void writeValue(EncoderChannel valueChannel, String uri,
			String localName) throws IOException {
		valueChannel.encodeNBitUnsignedInteger(valueToEncode, numberOfBits);
	}
}
