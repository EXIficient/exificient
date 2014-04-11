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

package com.siemens.ct.exi.attributes;

import com.siemens.ct.exi.EXIFactory;

/**
 * Factory for creating attribute lists according to EXI Factory.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.3-SNAPSHOT
 */

public class AttributeFactory {
	protected AttributeFactory() {
	}

	public static AttributeFactory newInstance() {
		return new AttributeFactory();
	}

	public AttributeList createAttributeListInstance(EXIFactory exiFactory) {
		if (exiFactory.getGrammars().isSchemaInformed()) {
			return new AttributeListSchemaInformed(exiFactory);
		} else {
			return new AttributeListSchemaLess(exiFactory);
		}
	}
}
