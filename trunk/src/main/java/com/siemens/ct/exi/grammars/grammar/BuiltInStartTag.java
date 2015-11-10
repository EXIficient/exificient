/*
 * Copyright (c) 2007-2015 Siemens AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
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
