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

package com.siemens.ct.exi.context;

import java.io.IOException;

import com.siemens.ct.exi.datatype.strings.StringEncoder;
import com.siemens.ct.exi.io.channel.EncoderChannel;

public interface EncoderContext extends CoderContext {

	public StringEncoder getStringEncoder();

	public QNameContext encodeQName(String namespaceUri, String localName,
			EncoderChannel channel) throws IOException;

	public QNameContext encodeLocalName(String localName,
			EvolvingUriContext uc, EncoderChannel channel) throws IOException;

	public EvolvingUriContext encodeUri(final String namespaceUri,
			EncoderChannel channel) throws IOException;

	public void encodeQNamePrefix(QNameContext qnContext, String prefix,
			EncoderChannel channel) throws IOException;

	public void encodeNamespacePrefix(EvolvingUriContext uriContext,
			String prefix, EncoderChannel channel) throws IOException;
}