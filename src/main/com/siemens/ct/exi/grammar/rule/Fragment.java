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

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.grammar.event.EventType;
import com.siemens.ct.exi.grammar.event.StartDocument;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.6
 */

/*
 * Fragment : SD FragmentContent 0
 */
public class Fragment extends AbstractSchemaInformedRule {
	
	private static final long serialVersionUID = 4883701889805280947L;
	
	Rule fragmentContent;
	
	public Fragment(Rule fragmentContent) {
		this.fragmentContent = fragmentContent;
		addRule(new StartDocument(), fragmentContent);
	}
	
	public Fragment(Rule fragmentContent, String label) {
		this(fragmentContent);
		this.setLabel(label);
	}

	public String toString() {
		return "Fragment" + super.toString();
	}
	
	public int get2ndLevelEventCode(EventType eventType,
			FidelityOptions fidelityOptions) {
		return Constants.NOT_FOUND;
	}

	public EventType get2ndLevelEvent(int eventCode,
			FidelityOptions fidelityOptions) {
		return null;
	}

	public int get2ndLevelCharacteristics(FidelityOptions fidelityOptions) {
		return 0;
	}

	public int get3rdLevelCharacteristics(FidelityOptions fidelityOptions) {
		return 0;
	}

	@Override
	public boolean hasSecondOrThirdLevel(FidelityOptions fidelityOptions) {
		return false;
	}
}
