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

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.grammar.event.EndDocument;
import com.siemens.ct.exi.grammar.event.EventType;
import com.siemens.ct.exi.grammar.event.StartElementGeneric;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20080718
 */

/*
 * <Schema-informed Fragment Grammar>
 * 
 * FragmentContent : SE (F 0) FragmentContent 0 SE (F 1) FragmentContent 1 ...
 * SE (F n-1) FragmentContent n-1 ED n SE () FragmentContent (n+1).0 CM
 * FragmentContent (n+1).1.0 PI FragmentContent (n+1).1.1
 */

public class SchemaInformedFragmentContent extends
		AbstractSchemaInformedRule {

	public SchemaInformedFragmentContent(String label) {
		super(label);
		//		SE(*) --> FragmentContent
		this.addRule(new StartElementGeneric(), this);
		addTerminalRule(new EndDocument());
	}

	public String toString() {
		return "FragmentContent" + super.toString();
	}
	
	public EventType get2ndLevelEvent(int eventCode,
			FidelityOptions fidelityOptions) {
		return null;
	}
	
	public int get2ndLevelEventCode(EventType eventType,
			FidelityOptions fidelityOptions) {
		// No events on 2nd level
		return Constants.NOT_FOUND;
	}
	
	public int get2ndLevelCharacteristics(FidelityOptions fidelityOptions) {
		return get3rdLevelCharacteristics(fidelityOptions) > 0 ? 1 : 0;
	}


	@Override
	public boolean hasSecondOrThirdLevel(FidelityOptions fidelityOptions) {
		return get2ndLevelCharacteristics(fidelityOptions) > 0;
	}

}
