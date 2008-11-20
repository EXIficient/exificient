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

package com.siemens.ct.exi.core.sax;

import com.siemens.ct.exi.EXIEncoder;
import com.siemens.ct.exi.exceptions.EXIException;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20080908
 */

public class WSCharactersEncoder extends CharactersEncoder {
	protected final EXIEncoder encoder;
	protected final StringBuilder chars;

	public WSCharactersEncoder(EXIEncoder encoder, StringBuilder chars) {
		this.encoder = encoder;
		this.chars = chars;
	}

	public void checkPendingChars() throws EXIException {
		if (chars.length() > 0) {
			encoder.encodeCharacters(chars.toString());
			chars.setLength(0);
		}
	}

}
