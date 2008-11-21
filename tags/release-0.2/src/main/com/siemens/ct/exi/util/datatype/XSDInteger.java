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

package com.siemens.ct.exi.util.datatype;

import java.math.BigInteger;

import com.siemens.ct.exi.exceptions.XMLParsingException;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20081111
 */

public class XSDInteger implements Comparable<XSDInteger> {
	private int iInt;
	private long lInt;
	private BigInteger bInt;

	private IntegerType type;

	private XSDInteger subtractValue;

	private XSDInteger() {
	}

	public static XSDInteger newInstance() {
		return new XSDInteger();
	}

	public void setToIntegerValue(int val) {
		iInt = val;
		type = IntegerType.INT_INTEGER;
	}

	public void parse(String s) throws XMLParsingException {
		// TODO check string-length and step forward directly to long etc.

		// integer value ?
		try {
			iInt = Integer.parseInt(s);
			type = IntegerType.INT_INTEGER;
		} catch (NumberFormatException e) {
			// long value ?
			try {
				lInt = Long.parseLong(s);
				type = IntegerType.LONG_INTEGER;
			} catch (NumberFormatException el) {
				// BigInteger ?
				try {
					bInt = new BigInteger(s);
					type = IntegerType.BIG_INTEGER;
				} catch (NumberFormatException eb) {
					// OK, seems to be a deviation
					throw new XMLParsingException("'" + s
							+ "' cannot be parsed as Integer");
				}
			}
		}
	}

	public boolean isNegative() {
		switch (type) {
		case INT_INTEGER:
			return (iInt < 0);
		case LONG_INTEGER:
			return (lInt < 0);
		case BIG_INTEGER:
			return (bInt.signum() < 0);
		default:
			throw new RuntimeException();
		}
	}

	public int getIntInteger() {
		return iInt;
	}

	public long getLongInteger() {
		return lInt;
	}

	public BigInteger getBigInteger() {
		return bInt;
	}

	public IntegerType getIntegerType() {
		return type;
	}

	public XSDInteger subtract(XSDInteger val) {
		if (subtractValue == null) {
			subtractValue = XSDInteger.newInstance();
		}

		// same types
		if (this.type == IntegerType.INT_INTEGER
				&& val.type == IntegerType.INT_INTEGER) {
			subtractValue.iInt = this.iInt - val.iInt;
			subtractValue.type = IntegerType.INT_INTEGER;

			return subtractValue;
		} else if (this.type == IntegerType.LONG_INTEGER
				&& val.type == IntegerType.LONG_INTEGER) {
			subtractValue.lInt = this.lInt - val.lInt;
			subtractValue.type = IntegerType.LONG_INTEGER;

			return subtractValue;
		} else if (this.type == IntegerType.BIG_INTEGER
				&& val.type == IntegerType.BIG_INTEGER) {
			subtractValue.bInt = this.bInt.subtract(val.bInt);
			subtractValue.type = IntegerType.BIG_INTEGER;

			return subtractValue;
		}
		// different types
		else {
			// rare case --> map to big integers
			BigInteger thiss = new BigInteger(this.toString());
			BigInteger vall = new BigInteger(val.toString());

			subtractValue.bInt = thiss.subtract(vall);
			subtractValue.type = IntegerType.BIG_INTEGER;
		}

		return subtractValue;
	}

	public int compareTo(XSDInteger other) {
		// same types
		if (this.type == IntegerType.INT_INTEGER
				&& other.type == IntegerType.INT_INTEGER) {
			return (this.iInt < other.iInt ? -1 : (this.iInt == other.iInt ? 0
					: 1));
		} else if (this.type == IntegerType.LONG_INTEGER
				&& other.type == IntegerType.LONG_INTEGER) {
			return (this.lInt < other.lInt ? -1 : (this.lInt == other.lInt ? 0
					: 1));
		} else if (this.type == IntegerType.BIG_INTEGER
				&& other.type == IntegerType.BIG_INTEGER) {
			return this.bInt.compareTo(other.bInt);
		}
		// different types
		else if (this.type == IntegerType.INT_INTEGER
				&& (other.type == IntegerType.LONG_INTEGER || other.type == IntegerType.BIG_INTEGER)) {
			return -1;
		} else if (this.type == IntegerType.LONG_INTEGER) {
			return (other.type == IntegerType.INT_INTEGER ? 1 : 0);
		} else {
			// this.type == IntegerType.BIG_INTEGER
			return 1;
		}
	}

	public String toString() {
		if (type == IntegerType.INT_INTEGER) {
			return this.iInt + "";
		} else if (type == IntegerType.LONG_INTEGER) {
			return this.lInt + "";
		} else {
			return this.bInt.toString();
		}
	}
}
