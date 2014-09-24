/*
 * Copyright (C) 2007-2014 Siemens AG
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

package com.siemens.ct.exi.types;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.exceptions.EXIException;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.4-SNAPSHOT
 */

public abstract class AbstractTypeDecoder extends AbstractTypeCoder implements
		TypeDecoder {

	public AbstractTypeDecoder() throws EXIException {
		this(null, null);
	}

	public AbstractTypeDecoder(QName[] dtrMapTypes,
			QName[] dtrMapRepresentations) throws EXIException {
		super(dtrMapTypes, dtrMapRepresentations);
	}

}
