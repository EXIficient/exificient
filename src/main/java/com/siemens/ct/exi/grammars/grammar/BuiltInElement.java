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

import com.siemens.ct.exi.grammars.event.Attribute;
import com.siemens.ct.exi.grammars.event.StartElement;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.6-SNAPSHOT
 */

/*
 * <Built-in Element Grammar>
 * 
 * ElementContent : EE 0 ChildContentItems (1.0)
 * 
 * ChildContentItems (n.m) : SE () ElementContent n. m CH ElementContent n.(m+1)
 * ER ElementContent n.(m+2) CM ElementContent n.(m+3).0 PI ElementContent
 * n.(m+3).1
 */
public class BuiltInElement extends AbstractBuiltInContent {

	protected BuiltInElement() {
		super();

		// EE on first level
		addProduction(END_ELEMENT, END_RULE);
	}
	
	@Override
	public boolean hasEndElement() {
		return true;
	}
	
	public GrammarType getGrammarType() {
		return GrammarType.BUILT_IN_ELEMENT_CONTENT;
	}
	
	@Override
	public void learnStartElement(StartElement se) {
		addProduction(se, this);
	}

	/*
	 * Note: learnEndElement ( ) not necessary since EE is already present on
	 * first level for element-content-rules
	 */
	
	@Override
	public void learnAttribute(Attribute at) {
		// this should never happen!
		throw new IllegalArgumentException(
				"ElementContent Rule cannot learn AT events");
	}

}
