/*
 * Copyright (C) 2007-2014 Siemens AG
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FragmentUtilities {
	public static InputStream getSurroundingRootInputStream(InputStream is)
			throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		baos.write("<root>".getBytes());

		int b;
		while ((b = is.read()) != -1) {
			baos.write(b);
		}

		baos.write("</root>".getBytes());
		baos.flush();

		return new ByteArrayInputStream(baos.toByteArray());
	}
}