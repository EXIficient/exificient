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

package com.siemens.ct.exi.io.compression;

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import java.util.zip.ZipException;

/**
 * An inflate stream with the ability to pushback data which the inflater read
 * beyond.
 * <p>
 * Note: Code borrowed from java.util.zip.InflaterInputStream and pushback
 * support added.
 * </p>
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.5
 */

public class EXIInflaterInputStream extends FilterInputStream {
	/**
	 * Decompressor for this stream.
	 */
	protected Inflater inf;

	/**
	 * Input buffer for decompression.
	 */
	protected byte[] buf;

	/**
	 * Length of input buffer.
	 */
	protected int len;

	private boolean closed = false;
	// this flag is set to true after EOF has reached
	private boolean reachEOF = false;

	/**
	 * Check to make sure that this stream has not been closed
	 */
	private void ensureOpen() throws IOException {
		if (closed) {
			throw new IOException("Stream closed");
		}
	}

	/**
	 * Creates a new input stream with the specified decompressor and buffer
	 * size.
	 * 
	 * @param in
	 *            the input stream
	 * @param inf
	 *            the decompressor ("inflater")
	 * @param size
	 *            the input buffer size
	 * @exception IllegalArgumentException
	 *                if size is &le; 0
	 */
	public EXIInflaterInputStream(PushbackInputStream in, Inflater inf, int size) {
		super(in);
		if (in == null || inf == null) {
			throw new NullPointerException();
		} else if (size <= 0) {
			throw new IllegalArgumentException("buffer size <= 0");
		}
		this.inf = inf;
		buf = new byte[size];
	}

	public void pushbackAndReset() throws IOException {
//		boolean needsInput = inf.needsInput();
//		boolean needsDictionary = inf.needsDictionary();
//		System.out.println("available " + this.in.available());
//		System.out.println("finished " + inf.finished() + ", closed " + closed + ", needsInput " + needsInput + ",  needsDictionary " + needsDictionary + ", Remaining " + inf.getRemaining() + ", reachEOF " + reachEOF);
		while(!inf.finished() && this.in.available() > 0) {
			this.read(); // discard
		}
		
		int rem = inf.getRemaining();
		if(rem > 0) {
			// System.out.println("pushback " + rem + " bytes");
			((PushbackInputStream) in).unread(buf, len - rem, rem);			
		}

		inf.reset();
	}

	// /**
	// * Creates a new input stream with the specified decompressor and a
	// * default buffer size.
	// *
	// * @param in
	// * the input stream
	// * @param inf
	// * the decompressor ("inflater")
	// */
	// public EXIInflaterInputStream(InputStream in, Inflater inf) {
	// this(in, inf, 512);
	// }

	boolean usesDefaultInflater = false;

	// /**
	// * Creates a new input stream with a default decompressor and buffer
	// * size.
	// *
	// * @param in
	// * the input stream
	// */
	// public EXIInflaterInputStream(InputStream in) {
	// this(in, new Inflater());
	// usesDefaultInflater = true;
	// }

	private byte[] singleByteBuf = new byte[1];

	/**
	 * Reads a byte of uncompressed data. This method will block until enough
	 * input is available for decompression.
	 * 
	 * @return the byte read, or -1 if end of compressed input is reached
	 * @exception IOException
	 *                if an I/O error has occurred
	 */
	public int read() throws IOException {
		ensureOpen();
		return read(singleByteBuf, 0, 1) == -1 ? -1 : singleByteBuf[0] & 0xff;
	}

	/**
	 * Reads uncompressed data into an array of bytes. This method will block
	 * until some input can be decompressed.
	 * 
	 * @param b
	 *            the buffer into which the data is read
	 * @param off
	 *            the start offset of the data
	 * @param len
	 *            the maximum number of bytes read
	 * @return the actual number of bytes read, or -1 if the end of the
	 *         compressed input is reached or a preset dictionary is needed
	 * @exception ZipException
	 *                if a ZIP format error has occurred
	 * @exception IOException
	 *                if an I/O error has occurred
	 */
	public int read(byte[] b, int off, int len) throws IOException {
		ensureOpen();
		if ((off | len | (off + len) | (b.length - (off + len))) < 0) {
			throw new IndexOutOfBoundsException();
		} else if (len == 0) {
			return 0;
		}
		try {
			int n;
			while ((n = inf.inflate(b, off, len)) == 0) {
				if (inf.finished() || inf.needsDictionary()) {
					reachEOF = true;
					return -1;
				}
				if (inf.needsInput()) {
					fill();
				}
			}
			return n;
		} catch (DataFormatException e) {
			String s = e.getMessage();
			throw new ZipException(s != null ? s : "Invalid ZLIB data format");
		}
	}

	/**
	 * Returns 0 after EOF has been reached, otherwise always return 1.
	 * <p>
	 * Programs should not count on this method to return the actual number of
	 * bytes that could be read without blocking.
	 * 
	 * @return 1 before EOF and 0 after EOF.
	 * @exception IOException
	 *                if an I/O error occurs.
	 * 
	 */
	public int available() throws IOException {
		ensureOpen();
		if (reachEOF) {
			return 0;
		} else {
			return 1;
		}
	}

	private byte[] b = new byte[512];

	/**
	 * Skips specified number of bytes of uncompressed data.
	 * 
	 * @param n
	 *            the number of bytes to skip
	 * @return the actual number of bytes skipped.
	 * @exception IOException
	 *                if an I/O error has occurred
	 * @exception IllegalArgumentException
	 *                if n &lt; 0
	 */
	public long skip(long n) throws IOException {
		if (n < 0) {
			throw new IllegalArgumentException("negative skip length");
		}
		ensureOpen();
		int max = (int) Math.min(n, Integer.MAX_VALUE);
		int total = 0;
		while (total < max) {
			int len = max - total;
			if (len > b.length) {
				len = b.length;
			}
			len = read(b, 0, len);
			if (len == -1) {
				reachEOF = true;
				break;
			}
			total += len;
		}
		return total;
	}

	/**
	 * Closes this input stream and releases any system resources associated
	 * with the stream.
	 * 
	 * @exception IOException
	 *                if an I/O error has occurred
	 */
	public void close() throws IOException {
		if (!closed) {
			if (usesDefaultInflater)
				inf.end();
			in.close();
			closed = true;
		}
	}

	/**
	 * Fills input buffer with more data to decompress.
	 * 
	 * @exception IOException
	 *                if an I/O error has occurred
	 */
	protected void fill() throws IOException {
		ensureOpen();
		len = in.read(buf, 0, buf.length);
		if (len == -1) {
			throw new EOFException("Unexpected end of ZLIB input stream");
		}
		inf.setInput(buf, 0, len);
	}

	/**
	 * Tests if this input stream supports the <code>mark</code> and
	 * <code>reset</code> methods. The <code>markSupported</code> method of
	 * <code>InflaterInputStream</code> returns <code>false</code>.
	 * 
	 * @return a <code>boolean</code> indicating if this stream type supports
	 *         the <code>mark</code> and <code>reset</code> methods.
	 * @see java.io.InputStream#mark(int)
	 * @see java.io.InputStream#reset()
	 */
	public boolean markSupported() {
		return false;
	}

	/**
	 * Marks the current position in this input stream.
	 * 
	 * <p>
	 * The <code>mark</code> method of <code>InflaterInputStream</code> does
	 * nothing.
	 * 
	 * @param readlimit
	 *            the maximum limit of bytes that can be read before the mark
	 *            position becomes invalid.
	 * @see java.io.InputStream#reset()
	 */
	public synchronized void mark(int readlimit) {
	}

	/**
	 * Repositions this stream to the position at the time the <code>mark</code>
	 * method was last called on this input stream.
	 * 
	 * <p>
	 * The method <code>reset</code> for class <code>InflaterInputStream</code>
	 * does nothing except throw an <code>IOException</code>.
	 * 
	 * @exception IOException
	 *                if this method is invoked.
	 * @see java.io.InputStream#mark(int)
	 * @see java.io.IOException
	 */
	public synchronized void reset() throws IOException {
		throw new IOException("mark/reset not supported");
	}
}