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

package com.siemens.ct.exi.core;

import java.util.zip.Deflater;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.2.20080718
 */

public class CompileConfiguration {
	public static final boolean PRESERVE_XSI_SCHEMA_LOCATION = false;

	/*
	 * TESTS (default all "false")
	 */
	public static final boolean NOT_USE_STRING_TABLE = false;

	/*
	 * Block / Channel settings
	 */
	// Maximal Number of Values (per Block / Channel)
	public static final int MAX_NUMBER_OF_VALUES = 100;

	/*
	 * Compression settings, level & nowrap
	 */
	public static final int COMPRESSION_LEVEL = Deflater.DEFAULT_COMPRESSION;
	// public static final int COMPRESSION_LEVEL = Deflater.BEST_SPEED;
	// public static final int COMPRESSION_LEVEL = Deflater.BEST_COMPRESSION;

	public static final boolean DEFLATE_NOWRAP = true;

}
