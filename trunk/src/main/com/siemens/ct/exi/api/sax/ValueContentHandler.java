/*
 * Copyright (C) 2007-2011 Siemens AG
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

package com.siemens.ct.exi.api.sax;

import java.util.List;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.values.ListValue;
import com.siemens.ct.exi.values.StringValue;
import com.siemens.ct.exi.values.Value;

/**
 * De-Serializes EXI Values to ContentHandler events
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.8
 */

public class ValueContentHandler {

	final static int DEFAULT_CHAR_BUFFER_SIZE = 4096;
	protected char[] cbuffer = new char[DEFAULT_CHAR_BUFFER_SIZE];

	protected static final char[] XSD_DELIMTER = { Constants.XSD_LIST_DELIM_CHAR };

	protected ContentHandler contentHandler;

	public ValueContentHandler() {
	}

	protected final void ensureBufferCapacity(int reqSize) {
		if (reqSize > cbuffer.length) {
			int newSize = cbuffer.length;

			do {
				newSize = newSize << 2;
			} while (newSize < reqSize);

			cbuffer = new char[newSize];
		}
	}

	public void setContentHandler(ContentHandler handler) {
		this.contentHandler = handler;
	}

	public void reportCharacters(Value val) throws SAXException {
		// TODO binary, no need to expand cbuffer
		switch (val.getValueType()) {
		case STRING:
			StringValue sv = (StringValue) val;
			char[] chars = sv.toCharacters();
			contentHandler.characters(chars, 0, chars.length);
			break;
		case LIST:
			ListValue lv = (ListValue) val;
			List<Value> values = lv.toValues();
			if (values.size() > 0) {
				// all values except last item
				int vlenMinus1 = values.size() - 1;
				for (int i = 0; i < vlenMinus1; i++) {
					Value iVal = values.get(i);
					reportCharacters(iVal);
					// delimiter
					contentHandler.characters(XSD_DELIMTER, 0, 1);
				}

				// last item (no delimiter)
				Value lastVal = values.get(vlenMinus1);
				reportCharacters(lastVal);
			}
			break;
		default:
			int slen = val.getCharactersLength();
			ensureBufferCapacity(slen);

			// returns char array that contains value
			// Note: can be a different array than the one passed
			char[] sres = val.toCharacters(cbuffer, 0);

			contentHandler.characters(sres, 0, slen);
		}
	}

	public String reportAttributeString(Value val) {
		switch (val.getValueType()) {
		case STRING:
			return ((StringValue) val).toString();
		default:
			int slen = val.getCharactersLength();
			ensureBufferCapacity(slen);
			return val.toString(cbuffer, 0);
		}
	}

}
