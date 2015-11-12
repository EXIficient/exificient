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

package com.siemens.ct.exi.exceptions;

/**
 * Basic interface for EXI error handlers.
 * 
 * <p>
 * If a EXI application needs to implement customized error handling, it must
 * implement this interface and then register an instance.
 * </p>
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.5
 */

public interface ErrorHandler {
	/**
	 * Receive notification of a warning.
	 * 
	 * <p>
	 * Parsers will use this method to report conditions that are not errors.
	 * </p>
	 * 
	 * 
	 * @param exception
	 *            The warning information encapsulated in a EXI exception.
	 */
	public void warning(EXIException exception);

	/**
	 * Receive notification of a recoverable error.
	 * 
	 * @param exception
	 *            The error information encapsulated in a EXI exception.
	 */
	public void error(EXIException exception);
}
