/*
 * Copyright (C) 2007-2012 Siemens AG
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
 * @version 0.9
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

	protected final int ival;
	protected final long lval;
	protected final BigInteger bval;

	private IntegerValue(int ival) {
		super(ValueType.INTEGER_INT);
		this.ival = ival;
		
		this.lval = 0L;
		this.bval = null;
	}

	private IntegerValue(long lval) {
		super(ValueType.INTEGER_LONG);
		this.ival = 0;
		
		this.lval = lval;
		this.bval = null;
	}

	private IntegerValue(BigInteger bval) {
		super(ValueType.INTEGER_BIG);
		this.bval = bval;
		
		this.ival = 0;
		this.lval = 0L;
	}

	public int intValue() {
		switch (valueType) {
		case INTEGER_INT:
			return this.ival;
		case INTEGER_LONG:
			return (int)this.lval;
		case INTEGER_BIG:
			return bval.intValue();
		default:
			throw new RuntimeException("Unsupported Integer Type " + valueType);
		}
	}

	public long longValue() {
		switch (valueType) {
		case INTEGER_INT:
			return this.ival;
		case INTEGER_LONG:
			return this.lval;
		case INTEGER_BIG:
			return bval.longValue();
		default:
			throw new RuntimeException("Unsupported Integer Type " + valueType);
		}
	}

	public BigInteger bigIntegerValue() {
		switch (valueType) {
		case INTEGER_INT:
			return BigInteger.valueOf(this.ival);
		case INTEGER_LONG:
			return BigInteger.valueOf(this.lval);
		case INTEGER_BIG:
			return bval;
		default:
			throw new RuntimeException("Unsupported Integer Type " + valueType);
		}
	}

	public boolean isPositive() {
		switch (valueType) {
		case INTEGER_INT:
			return (this.ival >= 0);
		case INTEGER_LONG:
			return (this.lval >= 0);
		case INTEGER_BIG:
			return (this.bval.signum() != -1);
		default:
			return false;
		}
	}

	public IntegerValue add(IntegerValue val) {
		if (val._equals(ZERO)) {
			return this;
		}
		
		IntegerValue iv;

		switch (valueType) {
		case INTEGER_INT:
			switch (val.valueType) {
			case INTEGER_INT:
				iv = new IntegerValue(this.ival + val.ival);
				break;
			case INTEGER_LONG:
				iv = IntegerValue.valueOf(this.ival + val.lval);
				break;
			case INTEGER_BIG:
				iv = IntegerValue.valueOf(BigInteger.valueOf(this.ival).add(
						val.bval));
				break;
			default:
				iv = null;
				break;
			}
			break;
		case INTEGER_LONG:
			switch (val.valueType) {
			case INTEGER_INT:
				iv = new IntegerValue(this.lval + val.ival);
				break;
			case INTEGER_LONG:
				iv = IntegerValue.valueOf(this.lval + val.lval);
				break;
			case INTEGER_BIG:
				iv = IntegerValue.valueOf(BigInteger.valueOf(this.lval).add(
						val.bval));
				break;
			default:
				iv = null;	
				break;
			}
			break;
		case INTEGER_BIG:
			switch (val.valueType) {
			case INTEGER_INT:
				iv =new IntegerValue(this.bval.add(BigInteger
						.valueOf(val.ival)));
				break;
			case INTEGER_LONG:
				iv = IntegerValue.valueOf(this.bval.add(BigInteger
						.valueOf(val.lval)));
				break;
			case INTEGER_BIG:
				iv = IntegerValue.valueOf(this.bval.add(val.bval));
				break;
			default:
				iv = null;
				break;
			}
			break;
		default:
			throw new RuntimeException("Unsupported Integer Type " + valueType);
		}
		
		return iv;
	}

	public IntegerValue subtract(IntegerValue val) {
		if (val._equals(ZERO)) {
			return this;
		}

		IntegerValue iv;
		
		switch (valueType) {
		case INTEGER_INT:
			switch (val.valueType) {
			case INTEGER_INT:
				iv = new IntegerValue(this.ival - val.ival);
				break;
			case INTEGER_LONG:
				iv =IntegerValue.valueOf(this.ival - val.lval);
				break;
			case INTEGER_BIG:
				iv = IntegerValue.valueOf(BigInteger.valueOf(this.ival)
						.subtract(val.bval));
				break;
			default:
				iv = null;
				break;
			}
			break;
		case INTEGER_LONG:
			switch (val.valueType) {
			case INTEGER_INT:
				iv = new IntegerValue(this.lval - val.ival);
				break;
			case INTEGER_LONG:
				iv = IntegerValue.valueOf(this.lval - val.lval);
				break;
			case INTEGER_BIG:
				iv =IntegerValue.valueOf(BigInteger.valueOf(this.lval)
						.subtract(val.bval));
				break;
			default:
				iv = null;
				break;
			}
			break;
		case INTEGER_BIG:
			switch (val.valueType) {
			case INTEGER_INT:
				iv = new IntegerValue(this.bval.subtract(BigInteger
						.valueOf(val.ival)));
				break;
			case INTEGER_LONG:
				iv = IntegerValue.valueOf(this.bval.subtract(BigInteger
						.valueOf(val.lval)));
				break;
			case INTEGER_BIG:
				iv = IntegerValue.valueOf(this.bval.subtract(val.bval));
				break;
			default:
				iv = null;
				break;
			}
			break;
		default:
			throw new RuntimeException("Unsupported Integer Type " + valueType);
		}
		return iv;
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
			// long: -9223372036854775808 (len==20) ... 9223372036854775807
			// (len==19)
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

	public int getCharactersLength() {
		if (slen == -1) {
			switch (this.valueType) {
			case INTEGER_INT:
				if (ival == Integer.MIN_VALUE) {
					slen = MethodsBag.INTEGER_MIN_VALUE_CHARARRAY.length;
				} else {
					slen = MethodsBag.getStringSize(ival);
				}
				break;
			case INTEGER_LONG:
				if (lval == Long.MIN_VALUE) {
					slen = MethodsBag.LONG_MIN_VALUE_CHARARRAY.length;
				} else {
					slen = MethodsBag.getStringSize(lval);
				}
				break;
			case INTEGER_BIG:
				slen = bval.toString().length();
				break;
			default:
				slen = -1;
			}
		}
		return slen;
	}

	public void getCharacters(char[] cbuffer, int offset) {
		switch (this.valueType) {
		case INTEGER_INT:
			if (ival == Integer.MIN_VALUE) {
				// --> copy
				System.arraycopy(MethodsBag.INTEGER_MIN_VALUE_CHARARRAY, 0, cbuffer, offset, MethodsBag.INTEGER_MIN_VALUE_CHARARRAY.length);
			} else {
				assert (cbuffer.length >= getCharactersLength());
				MethodsBag.itos(ival, offset + getCharactersLength(), cbuffer);
			}
			break;
		case INTEGER_LONG:
			if (lval == Long.MIN_VALUE) {
				// --> copy
				System.arraycopy(MethodsBag.LONG_MIN_VALUE_CHARARRAY, 0, cbuffer, offset, MethodsBag.LONG_MIN_VALUE_CHARARRAY.length);
			} else {
				assert (cbuffer.length >= getCharactersLength());
				MethodsBag.itos(lval, offset + getCharactersLength(), cbuffer);
			}
			break;
		case INTEGER_BIG:
			String src = bval.toString();
			src.getChars(0, src.length(), cbuffer, 0);
			break;
		default:
			// return null;
		}
	}
	
	private final boolean _equals(IntegerValue o) {
		switch (this.valueType) {
		case INTEGER_INT:
			return (o.valueType == ValueType.INTEGER_INT && this.ival == o.ival);
		case INTEGER_LONG:
			return (o.valueType == ValueType.INTEGER_LONG && this.lval == o.lval);
		case INTEGER_BIG:
			return (o.valueType == ValueType.INTEGER_BIG
					&& this.bval.equals(o.bval));
		default:
			return false;
		}
	}

	
	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (o instanceof IntegerValue) {
			return _equals((IntegerValue) o);
		} else {
			IntegerValue iv = IntegerValue.parse(o.toString());
			return iv == null ? false : _equals(iv);
		}
	}
	
	@Override
	public int hashCode() {
		int hc = 0;
		switch (valueType) {
		case INTEGER_INT:
			hc = ival;
			break;
		case INTEGER_LONG:
			// Long hashCode: return (int)(value ^ (value >>> 32));
			hc = (int)(lval ^ (lval >>> 32));
			break;
		case INTEGER_BIG:
			hc = bval.hashCode();
			break;
		default:
			throw new RuntimeException("Unsupported Integer Type " + valueType);
		}
		return hc;
	}

	public int compareTo(IntegerValue o) {
		/*
		 * Returns a negative integer, zero, or a positive integer as this
		 * object is less than, equal to, or greater than the specified object.
		 */
		switch (this.valueType) {
		case INTEGER_INT:
			switch (o.valueType) {
			case INTEGER_INT:
				if (this.ival == o.ival) {
					return 0;
				} else if (this.ival < o.ival) {
					return -1;
				} else {
					return 1;
				}
			case INTEGER_LONG:
			case INTEGER_BIG:
				return -1;
			default:
				return -2;
			}
		case INTEGER_LONG:
			switch (o.valueType) {
			case INTEGER_INT:
				return 1;
			case INTEGER_LONG:
				if (this.lval == o.lval) {
					return 0;
				} else if (this.lval < o.lval) {
					return -1;
				} else {
					return 1;
				}
			case INTEGER_BIG:
				return -1;
			default:
				return -2;
			}
		case INTEGER_BIG:
			switch (o.valueType) {
			case INTEGER_INT:
			case INTEGER_LONG:
				return 1;
			case INTEGER_BIG:
				return this.bval.compareTo(o.bval);
			default:
				return -2;
			}
		default:
			return -2;
		}
	}


}
