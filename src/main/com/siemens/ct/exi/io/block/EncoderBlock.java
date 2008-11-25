/*
 * Copyright (C) 2007, 2008 Siemens AG
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

package com.siemens.ct.exi.io.block;

import java.io.IOException;

import com.siemens.ct.exi.datatype.Datatype;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.2.20080718
 */

public interface EncoderBlock {
	public void writeEventCode(int eventCode, int codeLength)
			throws IOException;

	/*
	 * Structure Channel
	 */
	// PI, Comment, etc.
	public void writeString(String text) throws IOException;

	public void writeUri(String uri) throws IOException;

	public void writeLocalName(String localName, String uri) throws IOException;

	public void writePrefix(String prefix, String uri) throws IOException;

	// xsi:nil, local-element-ns
	public void writeBoolean(boolean b) throws IOException;

	/*
	 * Value Channel
	 */
	public boolean isTypeValid(Datatype datatype, String value);

	public void writeTypeValidValue(final String uri, final String localName)
			throws IOException;

	public void writeValueAsString(String uri, String localName, String value)
			throws IOException;

	/*
	 * 8.2.1 Structure Channel
	 * 
	 * The structure channel of each block defines the overall order and
	 * structure of the events in that block. It contains the event codes and
	 * associated content for each event in the block, except for Attribute (AT)
	 * and Character (CH) values, which are stored in the value channels. In
	 * addition, there are two attribute events whose values are stored in the
	 * structure channel instead of in value channels, which are xsi:nil and
	 * xsi:type attributes that match a schema-informed grammar production.
	 * These attribute events are intrinsic to the grammar system thus are
	 * essential in processing the structure channel because their values affect
	 * the grammar to be used for processing the rest of the elements on which
	 * they appear. All event codes and content in the structure stream occur in
	 * the same order as they occur in the EXI event sequence.
	 */
	// public abstract EncoderChannel getStructureChannel();

	/*
	 * 8.2.2 Value Channels The values of the Attribute (AT) and Character (CH)
	 * events in each block are organized into separate channels based on the
	 * qname of the associated attribute or element. Specifically, the value of
	 * each Attribute (AT) event is placed in the channel identified by the
	 * qname of the Attribute and the value of each Character (CH) event is
	 * placed in the channel identified by the qname of its parent Start Element
	 * (SE) event. Each block contains exactly one channel for each distinct
	 * element or attribute qname that occurs in the block. The values in each
	 * channel occur in the order they occur in the EXI event sequence.
	 */
	// public EncoderChannel getValueChannel( String uri, String localName );

	public void flush() throws IOException;

	public void close() throws IOException;
}
