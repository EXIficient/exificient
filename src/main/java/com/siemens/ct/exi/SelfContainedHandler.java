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

package com.siemens.ct.exi;

import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.io.channel.EncoderChannel;

/**
 * The class allows you to keep track of selfContained elements.
 * 
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.5-SNAPSHOT
 */

public interface SelfContainedHandler {

	/**
	 * Receive notification of SC element. 
	 * 
	 * @param uri namespaceURI of of SC element
	 * @param localName local-name of SC element
	 * @param channel encoder channel
	 */
	public void scElement(String uri, String localName, EncoderChannel channel) throws EXIException;

}
