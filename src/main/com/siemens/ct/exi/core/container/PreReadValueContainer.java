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


package com.siemens.ct.exi.core.container;

public class PreReadValueContainer {
	
	protected char[][] contentValues;
	protected int index;
	
	public PreReadValueContainer(char[][] contentValues) {
		setValues(contentValues);
	}
	
	protected void setValues(char[][] contentValues) {
		this.contentValues = contentValues;
		this.index = 0; 
	}
	
	public char[][] getValues() {
		return contentValues;
	}
	
	
	public char[] getNextContantValue() {
		return contentValues[index++];
	}
}
