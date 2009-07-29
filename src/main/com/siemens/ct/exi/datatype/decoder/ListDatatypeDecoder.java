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

package com.siemens.ct.exi.datatype.decoder;

import java.io.IOException;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.datatype.DatatypeList;
import com.siemens.ct.exi.io.channel.DecoderChannel;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20081112
 */

public class ListDatatypeDecoder extends AbstractDatatypeDecoder {
	protected EXIFactory exiFactory;
	protected TypeDecoder listTypeDecoder = null;

	protected StringBuilder sResult;

	public ListDatatypeDecoder(EXIFactory exiFactory) {
		super();
		this.exiFactory = exiFactory;

		sResult = new StringBuilder();
	}

	public char[] decodeValue(TypeDecoder decoder, Datatype datatype,
			DecoderChannel dc, String namespaceURI, String localName)
			throws IOException {
		// setup (list)typeEncoder if not already
		if (listTypeDecoder == null) {
			// Note: initialization in constructor causes never ending calls!
			listTypeDecoder = exiFactory.createTypeDecoder();
			listTypeDecoder.setStringTable(decoder.getStringTable());
		}

		Datatype listDatatype = ((DatatypeList) datatype).getListDatatype();

		int len = dc.decodeUnsignedInteger();

		sResult.setLength(0);

		for (int i = 0; i < len; i++) {
			sResult.append(listTypeDecoder.readTypeValidValue(listDatatype, dc,
					namespaceURI, localName));
			sResult.append(Constants.XSD_LIST_DELIM);
		}

		// return sResult.toString();
		return sResult.toString().toCharArray();
	}
}
