/*
 * Copyright (C) 2007-2015 Siemens AG
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

import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.values.StringValue;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.5-SNAPSHOT
 */

public interface StringDecoder extends StringCoder {

	public void addValue(QNameContext qnContext,
			StringValue value);

	public StringValue readValue(
			QNameContext qnContext, DecoderChannel channel) throws IOException;

	public StringValue readValueLocalHit(
			QNameContext qnContext, DecoderChannel valueChannel)
			throws IOException;

	public StringValue readValueGlobalHit(DecoderChannel valueChannel)
			throws IOException;

}
