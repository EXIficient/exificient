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
import com.siemens.ct.exi.grammar.event.EndDocument;
import com.siemens.ct.exi.grammar.event.EventType;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.5
 */

/*
 * DocEnd : ED 0 CM DocEnd 1.0 PI DocEnd 1.1
 */
public class DocEnd extends AbstractSchemaInformedRule {

	private static final long serialVersionUID = -3648891485531691554L;

	public DocEnd() {
		super();
		//	add EE rule
		addTerminalRule(new EndDocument());
	}

	public DocEnd(String label) {
		this();
		setLabel(label);
	}

	public String toString() {
		return "DocEnd" + super.toString();
	}
	
	@Override
	public boolean hasSecondOrThirdLevel(FidelityOptions fidelityOptions) {
		// has second or third level (CM or PI)
		if (fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_COMMENT)
				|| fidelityOptions
						.isFidelityEnabled(FidelityOptions.FEATURE_PI)) {
			return true;
		} else {
			return false;
		}
	}

	public int get2ndLevelEventCode(EventType eventType,
			FidelityOptions fidelityOptions) {
		return Constants.NOT_FOUND;
	}

	public EventType get2ndLevelEvent(int eventCode,
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
