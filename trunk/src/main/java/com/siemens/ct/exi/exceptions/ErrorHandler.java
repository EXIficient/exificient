/*
 * Copyright (C) 2007-2015 Siemens AG
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
 * @version 0.9.4-SNAPSHOT
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
