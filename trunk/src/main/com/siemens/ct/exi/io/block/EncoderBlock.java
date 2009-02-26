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

package com.siemens.ct.exi.io.block;

import java.io.IOException;

import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.datatype.encoder.TypeEncoder;

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
	 * Stream
	 */
	public void flush() throws IOException;

	public void close() throws IOException;
	
	/*
	 * Self-Contained
	 */
	public TypeEncoder getTypeEncoder();
	
	public void skipToNextByteBoundary() throws IOException;
	
	public boolean bytePositionSupported();
	
	public int getBytePosition();
	
}
