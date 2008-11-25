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

package com.siemens.ct.exi.io.block;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.util.ExpandedName;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.2.20080718
 */

public interface DecoderBlock {
	public void reconstructChannels(int values, List<ExpandedName> valueQNames,
			Map<ExpandedName, List<Datatype>> dataTypes,
			Map<ExpandedName, Integer> occurrences) throws IOException;

	public int readEventCode(int codeLength) throws IOException;

	// PI, Comment, PI, etc.
	public String readString() throws IOException;

	public String readUri() throws IOException;

	public String readLocalName(String uri) throws IOException;

	public String readPrefix(String uri) throws IOException;

	public boolean readBoolean() throws IOException;

	/*
	 * Value Channels
	 */
	public String readTypedValidValue(Datatype datatype, String namespaceURI,
			String localName) throws IOException;

	public String readValueAsString(String namespaceURI, String localName)
			throws IOException;
}
