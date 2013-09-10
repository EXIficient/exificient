/*
 * Copyright (C) 2007-2012 Siemens AG
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
 * @version 0.9.1
 */

/*
 * DocEnd : ED 0 CM DocEnd 1.0 PI DocEnd 1.1
 */
public class DocEnd extends AbstractSchemaInformedGrammar {

	private static final long serialVersionUID = -3648891485531691554L;

	public DocEnd() {
		super();
	}

	public DocEnd(String label) {
		this();
		setLabel(label);
	}

	public String toString() {
		return "DocEnd" + super.toString();
	}

	@Override
	public final boolean hasSecondOrThirdLevel(FidelityOptions fidelityOptions) {
		// has second or third level (CM or PI)
		return (fidelityOptions
				.isFidelityEnabled(FidelityOptions.FEATURE_COMMENT) || fidelityOptions
				.isFidelityEnabled(FidelityOptions.FEATURE_PI));
	}

	public int get2ndLevelEventCode(EventType eventType,
			FidelityOptions fidelityOptions) {
		return Constants.NOT_FOUND;
	}

	public EventType get2ndLevelEventType(int eventCode,
			FidelityOptions fidelityOptions) {
		return null;
	}

	/*
	 * Note: The following two grammars result in the same bit sequence,
	 * therefore the second variant is used to keep code simple!! DocEnd : ED 0
	 * CM DocEnd 1.0 PI DocEnd 1.1
	 * 
	 * DocEnd : ED 0 CM DocEnd 1.0.0 PI DocEnd 1.0.1
	 */
	public int get2ndLevelCharacteristics(FidelityOptions fidelityOptions) {
		int ch2 = 0;

		if (get3rdLevelCharacteristics(fidelityOptions) > 0) {
			ch2++;
		}

		return ch2;
	}

}