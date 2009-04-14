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

package com.siemens.ct.exi.datatype.encoder;

import java.io.IOException;
import java.util.StringTokenizer;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.datatype.DatatypeList;
import com.siemens.ct.exi.io.channel.EncoderChannel;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20081110
 */

public class ListDatatypeEncoder extends AbstractDatatypeEncoder implements
		DatatypeEncoder {
	protected Datatype listDatatype;
	protected TypeEncoder listTypeEncoder = null;
	protected EXIFactory exiFactory;
	protected int numberOfEnumeratedTypes;
	protected String lastValidValue;

	public ListDatatypeEncoder(TypeEncoder typeEncoder, EXIFactory exiFactory) {
		super(typeEncoder);

		this.exiFactory = exiFactory;
	}

	public boolean isValid(Datatype datatype, String value) {
		// setup (list)typeEncoder if not already
		if (listTypeEncoder == null) {
			// Note: initialization in constructor causes never ending calls!
			listTypeEncoder = exiFactory.createTypeEncoder();
		}

		// check first (no cache for writeValue used)
		listDatatype = ((DatatypeList) datatype).getListDatatype();

		// iterate over all tokens
		StringTokenizer st = new StringTokenizer(value,
				Constants.XSD_LIST_DELIM);
		numberOfEnumeratedTypes = 0;

		while (st.hasMoreTokens()) {
			if (!listTypeEncoder.isTypeValid(listDatatype, st.nextToken())) {
				// invalid --> abort process
				return false;
			}
			numberOfEnumeratedTypes++;
		}

		lastValidValue = value;
		return true;
	}

	public void writeValue(EncoderChannel valueChannel, String uri,
			String localName) throws IOException {
		/*
		 * check AGAIN & write to stream
		 */

		// length prefixed sequence of values
		valueChannel.encodeUnsignedInteger(numberOfEnumeratedTypes);

		// iterate over all tokens
		StringTokenizer st = new StringTokenizer(lastValidValue,
				Constants.XSD_LIST_DELIM);

		while (st.hasMoreTokens()) {
			// Note: assumption that is valid (was already checked!)
			listTypeEncoder.isTypeValid(listDatatype, st.nextToken());
			listTypeEncoder.writeTypeValidValue(valueChannel, uri, localName);
		}
	}
}
