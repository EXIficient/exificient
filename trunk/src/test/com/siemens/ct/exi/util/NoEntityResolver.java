/*
 * Copyright (C) 2007-2009 Siemens AG
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

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

public class NoEntityResolver implements EntityResolver {
	
	public InputSource resolveEntity(String publicId, String systemId) {
		return new InputSource(new ByteArrayInputStream(
				"<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
	}
}