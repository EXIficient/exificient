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

import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.datatype.TypeCoder;
import com.siemens.ct.exi.datatype.stringtable.StringTableEncoder;
import com.siemens.ct.exi.io.channel.EncoderChannel;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.2.20080718
 */

public interface TypeEncoder extends TypeCoder {

	public StringTableEncoder getStringTable();

	public void setStringTable(StringTableEncoder stringTable);

	/*
	 * Value Channel
	 */
	public boolean isTypeValid(Datatype datatype, String value);

	public void writeTypeValidValue(EncoderChannel valueChannel, String uri,
			String localName) throws IOException;

	public void writeValueAsString(EncoderChannel valueChannel, String uri,
			String localName, String invalidValue) throws IOException;

	public boolean writeStringAsLocalHit(EncoderChannel valueChannel,
			String uri, String localName, final String value)
			throws IOException;

	public boolean writeStringAsGlobalHit(EncoderChannel valueChannel,
			String value) throws IOException;

	public void writeStringAsMiss(EncoderChannel valueChannel, String uri,
			String localName, String value) throws IOException;

	/*
	 * Flush
	 */
	public void finish() throws IOException;

}
