/*
 * Copyright (C) 2007-2011 Siemens AG
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

import java.math.BigInteger;

import com.siemens.ct.exi.util.MethodsBag;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.6
 */

public class IntegerValue extends AbstractValue implements
		Comparable<IntegerValue> {

	private static final long serialVersionUID = -7715640034582532707L;

	public static final IntegerValue ZERO = new IntegerValue(0);

	public static final BigInteger INTEGER_MIN_VALUE = BigInteger
			.valueOf(Integer.MIN_VALUE);
	public static final BigInteger INTEGER_MAX_VALUE = BigInteger
			.valueOf(Integer.MAX_VALUE);

	public static final BigInteger LONG_MIN_VALUE = BigInteger
			.valueOf(Long.MIN_VALUE);
	public static final BigInteger LONG_MAX_VALUE = BigInteger
			.valueOf(Long.MAX_VALUE);

	protected int ival;
	protected long lval;
	protected BigInteger bval;

	private IntegerValue(int ival) {
		super(ValueType.INT_INTEGER);
		this.ival = ival;
	}

	private IntegerValue(long lval) {
		super(ValueType.LONG_INTEGER);
		this.lval = lval;
	}

	private IntegerValue(BigInteger bval) {
		super(ValueType.BIG_INTEGER);
		this.bval = bval;
	}

	public int intValue() {
		return this.ival;
	}

	public long longValue() {
		return this.lval;
	}

	public BigInteger bigIntegerValue() {
		return this.bval;
	}

	public boolean isPositive() {
		switch (valueType) {
		case INT_INTEGER:
			return (this.ival >= 0);
		case LONG_INTEGER:
			return (this.lval >= 0);
		case BIG_INTEGER:
			return (this.bval.signum() != -1);
		}
		return false;
	}

	public IntegerValue add(IntegerValue val) {
		if (val._equals(ZERO)) {
			return this;
		}

		switch (valueType) {
		case INT_INTEGER:
			switch (val.valueType) {
			case INT_INTEGER:
				return new IntegerValue(this.ival + val.ival);
			case LONG_INTEGER:
				return IntegerValue.valueOf(this.ival + val.lval);
			case BIG_INTEGER:
				return IntegerValue.valueOf(BigInteger.valueOf(this.ival).add(
						val.bval));
			}
		case LONG_INTEGER:
			switch (val.valueType) {
			case INT_INTEGER:
				return new IntegerValue(this.lval + val.ival);
			case LONG_INTEGER:
				return IntegerValue.valueOf(this.lval + val.lval);
			case BIG_INTEGER:
				return IntegerValue.valueOf(BigInteger.valueOf(this.lval).add(
						val.bval));
			}
		case BIG_INTEGER:
			switch (val.valueType) {
			case INT_INTEGER:
				return new IntegerValue(this.bval.add(BigInteger
						.valueOf(val.ival)));
			case LONG_INTEGER:
				return IntegerValue.valueOf(this.bval.add(BigInteger
						.valueOf(val.lval)));
			case BIG_INTEGER:
				return IntegerValue.valueOf(this.bval.add(val.bval));
			}
		}
		return null;
	}

	public IntegerValue subtract(IntegerValue val) {
		if (val._equals(ZERO)) {
			return this;
		}

		switch (valueType) {
		case INT_INTEGER:
			switch (val.valueType) {
			case INT_INTEGER:
				return new IntegerValue(this.ival - val.ival);
			case LONG_INTEGER:
				return IntegerValue.valueOf(this.ival - val.lval);
			case BIG_INTEGER:
				return IntegerValue.valueOf(BigInteger.valueOf(this.ival)
						.subtract(val.bval));
			}
		case LONG_INTEGER:
			switch (val.valueType) {
			case INT_INTEGER:
				return new IntegerValue(this.lval - val.ival);
			case LONG_INTEGER:
				return IntegerValue.valueOf(this.lval - val.lval);
			case BIG_INTEGER:
				return IntegerValue.valueOf(BigInteger.valueOf(this.lval)
						.subtract(val.bval));
			}
		case BIG_INTEGER:
			switch (val.valueType) {
			case INT_INTEGER:
				return new IntegerValue(this.bval.subtract(BigInteger
						.valueOf(val.ival)));
			case LONG_INTEGER:
				return IntegerValue.valueOf(this.bval.subtract(BigInteger
						.valueOf(val.lval)));
			case BIG_INTEGER:
				return IntegerValue.valueOf(this.bval.subtract(val.bval));
			}
		}
		return null;
	}

	public static IntegerValue valueOf(int ival) {
		return new IntegerValue(ival);
	}

	public static IntegerValue valueOf(long lval) {
		// fits into int ?
		if (lval < Integer.MIN_VALUE || lval > Integer.MAX_VALUE) {
			return new IntegerValue(lval);
		} else {
			return new IntegerValue((int) lval);
		}
	}

	public static IntegerValue valueOf(BigInteger bval) {
		// fits into int ?
		if (bval.compareTo(INTEGER_MIN_VALUE) == -1
				|| bval.compareTo(INTEGER_MAX_VALUE) == +1) {
			// fits into long?
			if (bval.compareTo(LONG_MIN_VALUE) == -1
					|| bval.compareTo(LONG_MAX_VALUE) == +1) {
				return new IntegerValue(bval);
			} else {
				return new IntegerValue(bval.longValue());
			}
		} else {
			return new IntegerValue(bval.intValue());
		}
	}
	

	protected static String getAdjustedValue(String value) {
		// trim leading and trailing whitespaces
		value = value.trim();
		// remove leading sign '+'
		if (value.length() > 0 && value.charAt(0) == '+') {
			value = value.substring(1);
		}
		return value;
	}

	public static IntegerValue parse(String value) {
		try {
			value = getAdjustedValue(value);
			int len = value.length();
			// int: -2147483648 (len==11) ... 2147483647 (len==10)
			// long: -9223372036854775808 (len==20) ... 9223372036854775807 (len==19)
			if (len > 0) {
				if (value.charAt(0) == '-') {
					// negative
					if (len < 11) {
						// int
						return new IntegerValue(Integer.parseInt(value));
					} else if (len < 20) {
						// long
						return new IntegerValue(Long.parseLong(value));
					} else {
						// big integer
						return new IntegerValue(new BigInteger(value));
					}
				} else {
					// positive
					if (len < 10) {
						// int
						return new IntegerValue(Integer.parseInt(value));
					} else if (len < 19) {
						// long
						return new IntegerValue(Long.parseLong(value));
					} else {
						// big integer
						return new IntegerValue(new BigInteger(value));
					}
				}
			} else {
				return null;
			}
		} catch (NumberFormatException e) {
			return null;
		}
	}

	// public int toInteger() {
	// return ival;
	// }

	public int getCharactersLength() {
		if (slen == -1) {
			switch (this.valueType) {
			case INT_INTEGER:
				if (ival == Integer.MIN_VALUE) {
					slen = MethodsBag.INTEGER_MIN_VALUE_CHARARRAY.length;
				} else {
					slen = MethodsBag.getStringSize(ival);
				}
				break;
			case LONG_INTEGER:
				if (lval == Long.MIN_VALUE) {
					slen = MethodsBag.LONG_MIN_VALUE_CHARARRAY.length;
				} else {
					slen = MethodsBag.getStringSize(lval);
				}
				break;
			case BIG_INTEGER:
				slen = bval.toString().length();
				break;
			default:
				slen = -1;
			}
		}
		return slen;
	}

	public char[] toCharacters(char[] cbuffer, int offset) {
		switch (this.valueType) {
		case INT_INTEGER:
			if (ival == Integer.MIN_VALUE) {
				return MethodsBag.INTEGER_MIN_VALUE_CHARARRAY;
			} else {
				assert (cbuffer.length >= getCharactersLength());
				MethodsBag.itos(ival, offset + getCharactersLength(), cbuffer);
				return cbuffer;
			}
		case LONG_INTEGER:
			if (lval == Long.MIN_VALUE) {
				return MethodsBag.LONG_MIN_VALUE_CHARARRAY;
			} else {
				assert (cbuffer.length >= getCharactersLength());
				MethodsBag.itos(lval, offset + getCharactersLength(), cbuffer);
				return cbuffer;
			}
		case BIG_INTEGER:
			// TODO look for a more suitable way, big integer
			return bval.toString().toCharArray();
			// char[] bi = bval.toString().toCharArray();
			// System.arraycopy(bi, 0, cbuffer, offset, bi.length);
		default:
			return null;
		}
	}

	private final boolean _equals(IntegerValue o) {
		switch (this.valueType) {
		case INT_INTEGER:
			if (o.valueType == ValueType.INT_INTEGER && this.ival == o.ival) {
				return true;
			} else {
				return false;
			}
		case LONG_INTEGER:
			if (o.valueType == ValueType.LONG_INTEGER && this.lval == o.lval) {
				return true;
			} else {
				return false;
			}
		case BIG_INTEGER:
			if (o.valueType == ValueType.BIG_INTEGER
					&& this.bval.equals(o.lval)) {
				return true;
			} else {
				return false;
			}
		default:
			return false;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof IntegerValue) {
			return _equals((IntegerValue) o);
		} else if (o instanceof String || o instanceof StringValue) {
			IntegerValue i = IntegerValue.parse(o.toString());
			if (i == null) {
				return false;
			} else {
				return this._equals(i);
			}
		} else {
			return false;
		}
	}

	public int compareTo(IntegerValue o) {
		/*
		 * Returns a negative integer, zero, or a positive integer as this
		 * object is less than, equal to, or greater than the specified object.
		 */
		switch (this.valueType) {
		case INT_INTEGER:
			switch (o.valueType) {
			case INT_INTEGER:
				if (this.ival == o.ival) {
					return 0;
				} else if (this.ival < o.ival) {
					return -1;
				} else {
					return 1;
				}
			case LONG_INTEGER:
			case BIG_INTEGER:
				return -1;
			}
		case LONG_INTEGER:
			switch (o.valueType) {
			case INT_INTEGER:
				return 1;
			case LONG_INTEGER:
				if (this.lval == o.lval) {
					return 0;
				} else if (this.lval < o.lval) {
					return -1;
				} else {
					return 1;
				}
			case BIG_INTEGER:
				return -1;
			}
		case BIG_INTEGER:
			switch (o.valueType) {
			case INT_INTEGER:
			case LONG_INTEGER:
				return 1;
			case BIG_INTEGER:
				return this.bval.compareTo(o.bval);
			}
		default:
			return -2;
		}
	}

}
