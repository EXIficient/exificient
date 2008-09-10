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

package com.siemens.ct.exi.grammar.rule;

import java.util.ArrayList;
import java.util.List;

import com.siemens.ct.exi.grammar.EventRule;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20080718
 */

public interface SchemaInformedRule extends Rule, Cloneable
{
	/*
	 * Add top rules (merge them)
	 */
	public void joinRules( Rule rule );
	
	public void setHasNamedSubtypes( boolean hasNamedSubtypes );

	public void setNillable( boolean nillable, SchemaInformedRule typeEmpty );
	
	public SchemaInformedRule getTypeEmpty();
	
	public void setFirstElementRule();
	
	
	/*
	 * Label
	 */
	public void setLabel ( String label );
	public String getLabel ();
	
	/*
	 * return reachable event-rules
	 */
	public void resolveLambdaTransitions( ArrayList<EventRule> reachableEventRules, ArrayList<Rule> alreadyHandledRules );
	
	public boolean isLambdaResolved( );
	
	//	temp
	public List<SchemaInformedRule> getLambdaRules( );
	
	public SchemaInformedRule clone();
	
}
