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

package com.siemens.ct.exi.util;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9
 */

public class MethodsBag {

	// ///////////////////////////////////////////////////////
	//
	// C O D I N G _ L E N G T H Operations
	//
	// ///////////////////////////////////////////////////////

	static final int[] smallLengths = new int[] { 0, 0, 1, 2, 2, 3, 3, 3, 3, 4,
			4, 4, 4, 4, 4, 4, 4 };

	/**
	 * Returns the least number of 7 bit-blocks that is needed to represent the
	 * int <param>n</param>. Returns 1 if <param>n</param> is 0.
	 * 
	 * @param n
	 *            integer value
	 * 
	 */
	public static int numberOf7BitBlocksToRepresent(final int n) {
		assert (n >= 0);

		// 7 bits
		if (n < 128) {
			return 1;
		}
		// 14 bits
		else if (n < 16384) {
			return 2;
		}
		// 21 bits
		else if (n < 2097152) {
			return 3;
		}
		// 28 bits
		else if (n < 268435456) {
			return 4;
		}
		// 35 bits
		else {
			// int, 32 bits
			return 5;
		}
	}

	/**
	 * Returns the least number of 7 bit-blocks that is needed to represent the
	 * long <param>l</param>. Returns 1 if <param>l</param> is 0.
	 * 
	 * @param l
	 *            long value
	 * 
	 */
	public static int numberOf7BitBlocksToRepresent(final long l) {
		if (l < 0xffffffff) {
			return numberOf7BitBlocksToRepresent((int) l);
		}
		// 35 bits
		else if (l < 0x800000000L) {
			return 5;
		}
		// 42 bits
		else if (l < 0x40000000000L) {
			return 6;
		}
		// 49 bits
		else if (l < 0x2000000000000L) {
			return 7;
		}
		// 56 bits
		else if (l < 0x100000000000000L) {
			return 8;
		}
		// 63 bits
		else if (l < 0x8000000000000000L) {
			return 9;
		}
		// 70 bits
		else {
			// long, 64 bits
			return 10;
		}
	}

	static final public int getCodingLength(final int characteristics) {
		if (characteristics < 17) {
			return smallLengths[characteristics];
		} else if (characteristics < 33) {
			// 17 .. 32
			return 5;
		} else if (characteristics < 65) {
			// 33 .. 64
			return 6;
		} else if (characteristics < 129) {
			// 65 .. 128
			return 7;
		} else {
			return (int) Math.ceil(Math.log((double) (characteristics))
					/ Math.log(2.0));
		}
	}

	// ///////////////////////////////////////////////////////
	//
	// I N T E G E R & L O N G _ T O _ S T R I N G Operations
	//
	// ///////////////////////////////////////////////////////
	public final static char[] INTEGER_MIN_VALUE_CHARARRAY = "-2147483648"
			.toCharArray();
	public final static char[] LONG_MIN_VALUE_CHARARRAY = "-9223372036854775808"
			.toCharArray();

	final static char[] DigitOnes = { '0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0',
			'1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3',
			'4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2',
			'3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8',
			'9', };

	final static char[] DigitTens = { '0', '0', '0', '0', '0', '0', '0', '0',
			'0', '0', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '2',
			'2', '2', '2', '2', '2', '2', '2', '2', '2', '3', '3', '3', '3',
			'3', '3', '3', '3', '3', '3', '4', '4', '4', '4', '4', '4', '4',
			'4', '4', '4', '5', '5', '5', '5', '5', '5', '5', '5', '5', '5',
			'6', '6', '6', '6', '6', '6', '6', '6', '6', '6', '7', '7', '7',
			'7', '7', '7', '7', '7', '7', '7', '8', '8', '8', '8', '8', '8',
			'8', '8', '8', '8', '9', '9', '9', '9', '9', '9', '9', '9', '9',
			'9', };

	/**
	 * All possible chars for representing a number as a String
	 */
	final static char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
			'9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
			'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
			'z' };

	final static int[] sizeTable = { 9, 99, 999, 9999, 99999, 999999, 9999999,
			99999999, 999999999, Integer.MAX_VALUE };

	// Requires positive x
	final static int stringSize(int x) {
		for (int i = 0;; i++)
			if (x <= sizeTable[i])
				return i + 1;
	}

	// Requires positive x
	final static int stringSize(long x) {
		long p = 10;
		for (int i = 1; i < 19; i++) {
			if (x < p)
				return i;
			p = 10 * p;
		}
		return 19;
	}

	public static final int getStringSize(int i) {
		return (i < 0) ? stringSize(-i) + 1 : stringSize(i);
	}

	public static final int getStringSize(long l) {
		if (l == Long.MIN_VALUE) {
			// -9223372036854775808
			return 20;
		} else {
			return (l < 0) ? stringSize(-l) + 1 : stringSize(l);
		}
	}

	/**
	 * Places characters representing the integer i into the character array
	 * buf. The characters are placed into the buffer backwards starting with
	 * the least significant digit at the specified index (exclusive), and
	 * working backwards from there.
	 * 
	 * Will fail if i == Integer.MIN_VALUE
	 */
	public final static void itos(int i, int index, char[] buf) {
		assert (!(i == Integer.MIN_VALUE));

		int q, r;
		char sign = 0;

		if (i < 0) {
			sign = '-';
			i = -i;
		}

		// Generate two digits per iteration
		while (i >= 65536) {
			q = i / 100;
			// really: r = i - (q * 100);
			r = i - ((q << 6) + (q << 5) + (q << 2));
			i = q;
			buf[--index] = DigitOnes[r];
			buf[--index] = DigitTens[r];
		}

		// Fall thru to fast mode for smaller numbers
		// assert(i <= 65536, i);
		for (;;) {
			q = (i * 52429) >>> (16 + 3);
			r = i - ((q << 3) + (q << 1)); // r = i-(q*10) ...
			buf[--index] = digits[r];
			i = q;
			if (i == 0)
				break;
		}
		if (sign != 0) {
			buf[--index] = sign;
		}
	}

	/**
	 * Places characters representing the integer i into the character array
	 * buf. The characters are placed into the buffer backwards starting with
	 * the least significant digit at the specified index (exclusive), and
	 * working backwards from there.
	 * 
	 */
	public final static void itos(long i, int index, char[] buf) {
		if (i == Long.MIN_VALUE) {
			// -9223372036854775808
			buf[--index] = '8';
			buf[--index] = '0';
			buf[--index] = '8';
			buf[--index] = '5';
			buf[--index] = '7';
			buf[--index] = '7';
			buf[--index] = '4';
			buf[--index] = '5';
			buf[--index] = '8';
			buf[--index] = '6';
			buf[--index] = '3';
			buf[--index] = '0';
			buf[--index] = '2';
			buf[--index] = '7';
			buf[--index] = '3';
			buf[--index] = '3';
			buf[--index] = '2';
			buf[--index] = '2';
			buf[--index] = '9';
			buf[--index] = '-';
			return;
		}
		assert (!(i == Long.MIN_VALUE));

		long q;
		int r;
		char sign = 0;

		if (i < 0) {
			sign = '-';
			i = -i;
		}

		// Get 2 digits/iteration using longs until quotient fits into an int
		while (i > Integer.MAX_VALUE) {
			q = i / 100;
			// really: r = i - (q * 100);
			r = (int) (i - ((q << 6) + (q << 5) + (q << 2)));
			i = q;
			buf[--index] = DigitOnes[r];
			buf[--index] = DigitTens[r];
		}

		// Get 2 digits/iteration using ints
		int q2;
		int i2 = (int) i;
		while (i2 >= 65536) {
			q2 = i2 / 100;
			// really: r = i2 - (q * 100);
			r = i2 - ((q2 << 6) + (q2 << 5) + (q2 << 2));
			i2 = q2;
			buf[--index] = DigitOnes[r];
			buf[--index] = DigitTens[r];
		}

		// Fall thru to fast mode for smaller numbers
		// assert(i2 <= 65536, i2);
		for (;;) {
			q2 = (i2 * 52429) >>> (16 + 3);
			r = i2 - ((q2 << 3) + (q2 << 1)); // r = i2-(q2*10) ...
			buf[--index] = digits[r];
			i2 = q2;
			if (i2 == 0)
				break;
		}
		if (sign != 0) {
			buf[--index] = sign;
		}
	}

	/**
	 * Places characters representing the integer i into the character array buf
	 * in reverse order.
	 * 
	 * Will fail if i < 0 (zero)
	 */
	public final static int itosReverse(int i, int index, char[] buf) {
		assert (i >= 0);
		int q, r;
		int posChar = index;

		// Generate two digits per iteration
		while (i >= 65536) {
			q = i / 100;
			// really: r = i - (q * 100);
			r = i - ((q << 6) + (q << 5) + (q << 2));
			i = q;
			buf[posChar++] = DigitOnes[r];
			buf[posChar++] = DigitTens[r];
		}

		// Fall thru to fast mode for smaller numbers
		// assert(i <= 65536, i);
		for (;;) {
			q = (i * 52429) >>> (16 + 3);
			r = i - ((q << 3) + (q << 1)); // r = i-(q*10) ...
			buf[posChar++] = digits[r];
			i = q;
			if (i == 0)
				break;
		}

		return (posChar - index); // number of written chars
	}

	/**
	 * Places characters representing the integer i into the character array buf
	 * in reverse order.
	 * 
	 * Will fail if i < 0 (zero)
	 */
	public final static void itosReverse(long i, int index, char[] buf) {
		assert (i >= 0);
		long q;
		int r;

		// Get 2 digits/iteration using longs until quotient fits into an int
		while (i > Integer.MAX_VALUE) {
			q = i / 100;
			// really: r = i - (q * 100);
			r = (int) (i - ((q << 6) + (q << 5) + (q << 2)));
			i = q;
			buf[index++] = DigitOnes[r];
			buf[index++] = DigitTens[r];
		}

		// Get 2 digits/iteration using ints
		int q2;
		int i2 = (int) i;
		while (i2 >= 65536) {
			q2 = i2 / 100;
			// really: r = i2 - (q * 100);
			r = i2 - ((q2 << 6) + (q2 << 5) + (q2 << 2));
			i2 = q2;
			buf[index++] = DigitOnes[r];
			buf[index++] = DigitTens[r];
		}

		// Fall thru to fast mode for smaller numbers
		// assert(i2 <= 65536, i2);
		for (;;) {
			q2 = (i2 * 52429) >>> (16 + 3);
			r = i2 - ((q2 << 3) + (q2 << 1)); // r = i2-(q2*10) ...
			buf[index++] = digits[r];
			i2 = q2;
			if (i2 == 0)
				break;
		}
	}

}
