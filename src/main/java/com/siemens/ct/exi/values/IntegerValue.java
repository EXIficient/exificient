/*
 * Copyright (c) 2007-2015 Siemens AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */

package com.siemens.ct.exi.values;

import java.math.BigInteger;

import com.siemens.ct.exi.util.MethodsBag;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.6-SNAPSHOT
 */

public class IntegerValue extends AbstractValue implements
		Comparable<IntegerValue> {

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
	
	protected final IntegerValueType iValType;

	private IntegerValue(int ival) {
		super(ValueType.INTEGER);
		
		this.ival = ival;
		this.iValType = IntegerValueType.INT;
		
		this.lval = 0L;
		this.bval = null;
	}
	
	public IntegerValueType getIntegerValueType() {
		return iValType;
	}

	private IntegerValue(long lval) {
		super(ValueType.INTEGER);
		
		this.ival = 0;
		this.iValType = IntegerValueType.LONG;
		
		this.lval = lval;
		this.bval = null;
	}

	private IntegerValue(BigInteger bval) {
		super(ValueType.INTEGER);
		
		this.bval = bval;
		this.iValType = IntegerValueType.BIG;
		
		this.ival = 0;
		this.lval = 0L;
	}

	public int intValue() {
		switch (iValType) {
		case INT:
			return this.ival;
		case LONG:
			return (int)this.lval;
		case BIG:
			return bval.intValue();
		default:
			throw new RuntimeException("Unsupported Integer Type " + valueType);
		}
	}

	public long longValue() {
		switch (iValType) {
		case INT:
			return this.ival;
		case LONG:
			return this.lval;
		case BIG:
			return bval.longValue();
		default:
			throw new RuntimeException("Unsupported Integer Type " + valueType);
		}
	}

	public BigInteger bigIntegerValue() {
		switch (iValType) {
		case INT:
			return BigInteger.valueOf(this.ival);
		case LONG:
			return BigInteger.valueOf(this.lval);
		case BIG:
			return bval;
		default:
			throw new RuntimeException("Unsupported Integer Type " + valueType);
		}
	}

	public boolean isPositive() {
		switch (iValType) {
		case INT:
			return (this.ival >= 0);
		case LONG:
			return (this.lval >= 0);
		case BIG:
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

		switch (iValType) {
		case INT:
			switch (val.iValType) {
			case INT:
				iv = new IntegerValue(this.ival + val.ival);
				break;
			case LONG:
				iv = IntegerValue.valueOf(this.ival + val.lval);
				break;
			case BIG:
				iv = IntegerValue.valueOf(BigInteger.valueOf(this.ival).add(
						val.bval));
				break;
			default:
				iv = null;
				break;
			}
			break;
		case LONG:
			switch (val.iValType) {
			case INT:
				iv = new IntegerValue(this.lval + val.ival);
				break;
			case LONG:
				iv = IntegerValue.valueOf(this.lval + val.lval);
				break;
			case BIG:
				iv = IntegerValue.valueOf(BigInteger.valueOf(this.lval).add(
						val.bval));
				break;
			default:
				iv = null;	
				break;
			}
			break;
		case BIG:
			switch (val.iValType) {
			case INT:
				iv =new IntegerValue(this.bval.add(BigInteger
						.valueOf(val.ival)));
				break;
			case LONG:
				iv = IntegerValue.valueOf(this.bval.add(BigInteger
						.valueOf(val.lval)));
				break;
			case BIG:
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
		
		switch (iValType) {
		case INT:
			switch (val.iValType) {
			case INT:
				iv = new IntegerValue(this.ival - val.ival);
				break;
			case LONG:
				iv =IntegerValue.valueOf(this.ival - val.lval);
				break;
			case BIG:
				iv = IntegerValue.valueOf(BigInteger.valueOf(this.ival)
						.subtract(val.bval));
				break;
			default:
				iv = null;
				break;
			}
			break;
		case LONG:
			switch (val.iValType) {
			case INT:
				iv = new IntegerValue(this.lval - val.ival);
				break;
			case LONG:
				iv = IntegerValue.valueOf(this.lval - val.lval);
				break;
			case BIG:
				iv =IntegerValue.valueOf(BigInteger.valueOf(this.lval)
						.subtract(val.bval));
				break;
			default:
				iv = null;
				break;
			}
			break;
		case BIG:
			switch (val.iValType) {
			case INT:
				iv = new IntegerValue(this.bval.subtract(BigInteger
						.valueOf(val.ival)));
				break;
			case LONG:
				iv = IntegerValue.valueOf(this.bval.subtract(BigInteger
						.valueOf(val.lval)));
				break;
			case BIG:
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
			switch (this.iValType) {
			case INT:
				if (ival == Integer.MIN_VALUE) {
					slen = MethodsBag.INTEGER_MIN_VALUE_CHARARRAY.length;
				} else {
					slen = MethodsBag.getStringSize(ival);
				}
				break;
			case LONG:
				if (lval == Long.MIN_VALUE) {
					slen = MethodsBag.LONG_MIN_VALUE_CHARARRAY.length;
				} else {
					slen = MethodsBag.getStringSize(lval);
				}
				break;
			case BIG:
				slen = bval.toString().length();
				break;
			default:
				slen = -1;
			}
		}
		
		return slen;
	}

	public void getCharacters(char[] cbuffer, int offset) {
		switch (this.iValType) {
		case INT:
			if (ival == Integer.MIN_VALUE) {
				// --> copy
				System.arraycopy(MethodsBag.INTEGER_MIN_VALUE_CHARARRAY, 0, cbuffer, offset, MethodsBag.INTEGER_MIN_VALUE_CHARARRAY.length);
			} else {
				assert (cbuffer.length >= getCharactersLength());
				MethodsBag.itos(ival, offset + getCharactersLength(), cbuffer);
			}
			break;
		case LONG:
			if (lval == Long.MIN_VALUE) {
				// --> copy
				System.arraycopy(MethodsBag.LONG_MIN_VALUE_CHARARRAY, 0, cbuffer, offset, MethodsBag.LONG_MIN_VALUE_CHARARRAY.length);
			} else {
				assert (cbuffer.length >= getCharactersLength());
				MethodsBag.itos(lval, offset + getCharactersLength(), cbuffer);
			}
			break;
		case BIG:
			String src = bval.toString();
			src.getChars(0, src.length(), cbuffer, 0);
			break;
		default:
			// return null;
		}
	}
	
	private final boolean _equals(IntegerValue o) {
		switch (this.iValType) {
		case INT:
			return (o.iValType == IntegerValueType.INT && this.ival == o.ival);
		case LONG:
			return (o.iValType == IntegerValueType.LONG && this.lval == o.lval);
		case BIG:
			return (o.iValType == IntegerValueType.BIG
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
		switch (iValType) {
		case INT:
			hc = ival;
			break;
		case LONG:
			// Long hashCode: return (int)(value ^ (value >>> 32));
			hc = (int)(lval ^ (lval >>> 32));
			break;
		case BIG:
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
		switch (this.iValType) {
		case INT:
			switch (o.iValType) {
			case INT:
				if (this.ival == o.ival) {
					return 0;
				} else if (this.ival < o.ival) {
					return -1;
				} else {
					return 1;
				}
			case LONG:
			case BIG:
				return -1;
			default:
				return -2;
			}
		case LONG:
			switch (o.iValType) {
			case INT:
				return 1;
			case LONG:
				if (this.lval == o.lval) {
					return 0;
				} else if (this.lval < o.lval) {
					return -1;
				} else {
					return 1;
				}
			case BIG:
				return -1;
			default:
				return -2;
			}
		case BIG:
			switch (o.iValType) {
			case INT:
			case LONG:
				return 1;
			case BIG:
				return this.bval.compareTo(o.bval);
			default:
				return -2;
			}
		default:
			return -2;
		}
	}

}
