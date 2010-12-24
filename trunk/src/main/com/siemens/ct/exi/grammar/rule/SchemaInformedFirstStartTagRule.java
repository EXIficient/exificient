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

package com.siemens.ct.exi.grammar.rule;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.6
 */

/*
 * first rule is different (namespace declaration, xsi:type and xsi:nil)
 */
public interface SchemaInformedFirstStartTagRule extends SchemaInformedStartTagRule {

	/*
	 * sets whether type is castable
	 */
	public void setTypeCastable(boolean hasNamedSubtypes);

	public boolean isTypeCastable();
	
	/*
	 * sets whether element is nillable
	 */
	public void setNillable(boolean nillable);
	
	public boolean isNillable();
	
	
	
	public void setTypeEmpty(SchemaInformedFirstStartTagRule typeEmpty);

	public SchemaInformedFirstStartTagRule getTypeEmpty();
	
}
