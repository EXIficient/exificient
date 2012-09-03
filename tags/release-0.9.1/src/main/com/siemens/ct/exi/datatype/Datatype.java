/*
 * Copyright (C) 2007-2012 Siemens AG
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
import java.io.Serializable;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.context.DecoderContext;
import com.siemens.ct.exi.context.EncoderContext;
import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.datatype.charset.RestrictedCharacterSet;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.types.BuiltInType;
import com.siemens.ct.exi.values.Value;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.1
 */

public interface Datatype extends Serializable {

	// if no codec map is used
	public BuiltInType getBuiltInType();

	// used for dtr map
	public QName getSchemaType();

	// restricted character set
	public RestrictedCharacterSet getRestrictedCharacterSet();

	/*
	 * Encoder
	 */
	public boolean isValid(Value value);

	public void writeValue(EncoderContext encoderContext,
			QNameContext qnContext, EncoderChannel valueChannel)
			throws IOException;

	public boolean isValidRCS(Value value);

	public void writeValueRCS(RestrictedCharacterSetDatatype rcsEncoder,
			EncoderContext encoderContext, QNameContext qnContext,
			EncoderChannel valueChannel) throws IOException;

	/*
	 * Decoder
	 */
	public Value readValue(DecoderContext decoderContext,
			QNameContext qnContext, DecoderChannel valueChannel)
			throws IOException;

	public Value readValueRCS(RestrictedCharacterSetDatatype rcsDecoder,
			DecoderContext decoderContext, QNameContext qnContext,
			DecoderChannel valueChannel) throws IOException;
}
