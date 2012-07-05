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

package com.siemens.ct.exi.context;

import java.io.IOException;

import com.siemens.ct.exi.core.container.PreReadValue;
import com.siemens.ct.exi.datatype.strings.StringDecoder;
import com.siemens.ct.exi.io.channel.DecoderChannel;

public interface DecoderContext extends CoderContext {

	public StringDecoder getStringDecoder();

	public void setPreReadValue(QNameContext qnc, PreReadValue prrReadValue);

	public QNameContext decodeQName(DecoderChannel channel) throws IOException;

	public EvolvingUriContext decodeUri(DecoderChannel channel)
			throws IOException;

	public QNameContext decodeLocalName(EvolvingUriContext uc,
			DecoderChannel channel) throws IOException;

	public String decodeQNamePrefix(UriContext uc, DecoderChannel channel)
			throws IOException;

	public String decodeNamespacePrefix(EvolvingUriContext uc,
			DecoderChannel channel) throws IOException;
}
