/*
 * Copyright (C) 2007-2011 Siemens AG
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
 * @version 0.9
 */

/*
 * Fragment : SD FragmentContent 0
 */
public class Fragment extends AbstractSchemaInformedGrammar {

	private static final long serialVersionUID = 4883701889805280947L;

	public Fragment() {
		super();
	}

	public Fragment(String label) {
		this();
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
	public final boolean hasSecondOrThirdLevel(FidelityOptions fidelityOptions) {
		return false;
	}
}
