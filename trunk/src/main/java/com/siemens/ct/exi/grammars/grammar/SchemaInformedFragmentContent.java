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

package com.siemens.ct.exi.grammars.grammar;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.grammars.event.EventType;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.4-SNAPSHOT
 */

/*
 * <Schema-informed Fragment Grammar>
 * 
 * FragmentContent : SE (F 0) FragmentContent 0 SE (F 1) FragmentContent 1 ...
 * SE (F n-1) FragmentContent n-1 ED n SE () FragmentContent (n+1).0 CM
 * FragmentContent (n+1).1.0 PI FragmentContent (n+1).1.1
 */

public class SchemaInformedFragmentContent extends AbstractSchemaInformedGrammar {

	private static final long serialVersionUID = 2041418874823084368L;

	public SchemaInformedFragmentContent() {
		super();
	}

	public SchemaInformedFragmentContent(String label) {
		this();
		setLabel(label);
	}
	
	public GrammarType getGrammarType() {
		return GrammarType.SCHEMA_INFORMED_FRAGMENT_CONTENT;
	}

	public String toString() {
		return "FragmentContent" + super.toString();
	}

	public EventType get2ndLevelEventType(int eventCode,
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
	public final boolean hasSecondOrThirdLevel(FidelityOptions fidelityOptions) {
		return get2ndLevelCharacteristics(fidelityOptions) > 0;
	}

}
