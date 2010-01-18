/*
 * Copyright (C) 2007-2010 Siemens AG
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

package com.siemens.ct.exi.datatype.strings;

import java.io.IOException;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.values.Value;

public interface StringDecoder extends StringCoder {

	public void addValue(QName context, Value value);

	public Value readValue(QName context, DecoderChannel channel)
			throws IOException;

	public Value readValueLocalHit(QName context,
			DecoderChannel valueChannel) throws IOException;

	public Value readValueGlobalHit(QName context,
			DecoderChannel valueChannel) throws IOException;

}
