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

package com.siemens.ct.exi.datatype;

import java.io.IOException;
import java.util.StringTokenizer;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.core.NameContext;
import com.siemens.ct.exi.datatype.strings.StringDecoder;
import com.siemens.ct.exi.datatype.strings.StringEncoder;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.types.BuiltInType;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20080718
 */

public class ListDatatype extends AbstractDatatype {
	
	private Datatype listDatatype;
	
	protected int numberOfEnumeratedTypes;
	protected String lastValidValue;
	
	protected StringBuilder sResult;

	public ListDatatype(Datatype listDatatype) {
		super(BuiltInType.LIST, null);
		
		this.rcs = listDatatype.getRestrictedCharacterSet();

		if (listDatatype.getDefaultBuiltInType() == BuiltInType.LIST) {
			throw new IllegalArgumentException();
		}

		this.listDatatype = listDatatype;
		
		sResult = new StringBuilder();
	}

	public Datatype getListDatatype() {
		return listDatatype;
	}
	
	public boolean isValid(String value) {
		// iterate over all tokens
		StringTokenizer st = new StringTokenizer(value,
				Constants.XSD_LIST_DELIM);
		numberOfEnumeratedTypes = 0;

		while (st.hasMoreTokens()) {
			if (!listDatatype.isValid(st.nextToken())) {
				// invalid --> abort process
				return false;
			}
			numberOfEnumeratedTypes++;
		}

		lastValidValue = value;
		return true;
	}
	
	@Override
	public boolean isValidRCS(String value) {
		StringTokenizer st = new StringTokenizer(value,
				Constants.XSD_LIST_DELIM);
		numberOfEnumeratedTypes = st.countTokens();
		return super.isValidRCS(value);
	}
	

	public void writeValue(EncoderChannel valueChannel, StringEncoder stringEncoder, NameContext context)
			throws IOException {
			/*
			 * Needs to check AGAIN & writes to stream
			 */
			// length prefixed sequence of values
			valueChannel.encodeUnsignedInteger(numberOfEnumeratedTypes);

			// iterate over all tokens
			StringTokenizer st = new StringTokenizer(lastValidValue,
					Constants.XSD_LIST_DELIM);

			while (st.hasMoreTokens()) {
				// Note: assumption that is valid (was already checked!)
				//	Nevertheless isValid method needs to be called!
				listDatatype.isValid(st.nextToken());
				listDatatype.writeValue(valueChannel, stringEncoder, context);
			}
	}
	
	@Override
	public void writeValueRCS(RestrictedCharacterSetDatatype rcsEncoder, EncoderChannel valueChannel, StringEncoder stringEncoder, NameContext context) throws IOException {
		// length prefixed sequence of values
		valueChannel.encodeUnsignedInteger(numberOfEnumeratedTypes);
		
		// iterate over all tokens
		StringTokenizer st = new StringTokenizer(this.lastRCSValue,
				Constants.XSD_LIST_DELIM);

		rcsEncoder.setRestrictedCharacterSet(rcs);
		
		while (st.hasMoreTokens()) {
			rcsEncoder.isValid(st.nextToken());
			rcsEncoder.writeValue(valueChannel, stringEncoder, context);
		}
	}

	public char[] readValue(DecoderChannel valueChannel,
			StringDecoder stringDecoder, NameContext context)
			throws IOException {
		int len = valueChannel.decodeUnsignedInteger();

		sResult.setLength(0);

		for (int i = 0; i < len; i++) {
			sResult.append(listDatatype.readValue(valueChannel, stringDecoder, context));
			sResult.append(Constants.XSD_LIST_DELIM);
		}

		return sResult.toString().toCharArray();
	}
	
	@Override
	public char[] readValueRCS(RestrictedCharacterSetDatatype rcsDecoder,
			DecoderChannel valueChannel, StringDecoder stringDecoder,
			NameContext context) throws IOException {
		int len = valueChannel.decodeUnsignedInteger();
		
		rcsDecoder.setRestrictedCharacterSet(rcs);
		
		sResult.setLength(0);
		
		for (int i = 0; i < len; i++) {
			sResult.append(rcsDecoder.readValue(valueChannel, stringDecoder, context));
			sResult.append(Constants.XSD_LIST_DELIM);
		}
		
		return sResult.toString().toCharArray();
	}
}