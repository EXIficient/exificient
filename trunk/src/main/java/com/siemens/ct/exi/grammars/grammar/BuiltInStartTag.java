/*
 * Copyright (C) 2007-2015 Siemens AG
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

import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.grammars.event.Attribute;
import com.siemens.ct.exi.grammars.event.StartElement;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.5-SNAPSHOT
 */

/*
 * <Built-in Element Grammar>
 * 
 * StartTagContent : EE 0.0 AT () StartTagContent 0.1 NS StartTagContent 0.2
 * ChildContentItems (0.3)
 * 
 * ChildContentItems (n.m) : SE () ElementContent n. m CH ElementContent n.(m+1)
 * ER ElementContent n.(m+2) CM ElementContent n.(m+3).0 PI ElementContent
 * n.(m+3).1
 */

public class BuiltInStartTag extends AbstractBuiltInContent {

	private static final long serialVersionUID = -4877451908590620943L;

	protected BuiltInElement elementContent;

	protected boolean learnedEE = false;
	protected boolean learnedXsiType = false;
	
	public BuiltInStartTag() {
		super();

		// initialize elementContent
		elementContent = new BuiltInElement();
	}
	
	@Override
	public boolean hasEndElement() {
		return learnedEE;
	}
	
	public GrammarType getGrammarType() {
		return GrammarType.BUILT_IN_START_TAG_CONTENT;
	}

	@Override
	public Grammar getElementContentGrammar() {
		// this is a *StartTag* Rule --> return element content rule
		return elementContent;
	}

	@Override
	// public void learnStartElement(String uri, String localName) {
	public void learnStartElement(StartElement se) {
		// addRule(new StartElement(uri, localName), getElementContentRule());
		addProduction(se, getElementContentGrammar());
	}

	@Override
	public void learnEndElement() {
		/*
		 * If a production EE with an event code of length 1 does not exist in
		 * the current element grammar create one add the production created
		 */
		if (!learnedEE) {
			addTerminalProduction(END_ELEMENT);
			learnedEE = true;
		}
	}

	@Override
	public void learnAttribute(Attribute at) {
		// Errata, xsi:type not learned			
		QNameContext qnc = at.getQNameContext();
		if(qnc.getNamespaceUriID() == 2 && qnc.getLocalNameID() == 1) {
			if(!learnedXsiType) {
				addProduction(at, this);
				learnedXsiType = true;
			}
		} else {
			addProduction(at, this);
		}
	}
	
}
