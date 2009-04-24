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

package com.siemens.ct.exi.grammar.rule;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20081008
 */

public interface SchemaInformedRule extends Rule {
	/*
	 * Add top rules (merge them)
	 */
	public void joinRules(Rule rule);

	/*
	 * first rule is different (namespace declaration, xsi:typ and xsi:nil)
	 */
	public void setFirstElementRule();

	/*
	 * sets whether type is castable
	 */
	public void setHasNamedSubtypes(boolean hasNamedSubtypes);

	/*
	 * sets whether element is nillable
	 */
	public void setNillable(boolean nillable, SchemaInformedRule typeEmpty);

	/*
	 * get ur-type
	 */
	public SchemaInformedRule getTypeEmpty();

	/*
	 * Label
	 */
	public void setLabel(String label);

	public String getLabel();

	/*
	 * resolves reachable event-rules & replaces Lambdas
	 */
	public void resolveLambdaTransitions();

	/*
	 * clones schema-informed rule
	 */
	public SchemaInformedRule duplicate();

}
