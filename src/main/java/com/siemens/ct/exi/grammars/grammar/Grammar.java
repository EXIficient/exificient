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
import com.siemens.ct.exi.grammars.event.Event;
import com.siemens.ct.exi.grammars.event.EventType;
import com.siemens.ct.exi.grammars.event.StartElement;
import com.siemens.ct.exi.grammars.production.Production;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.6-SNAPSHOT
 */

public interface Grammar {

	/*
	 * rule created according to schema information
	 */
	public boolean isSchemaInformed();
	
	
	/**
	 *  Informs whether a grammar has first level EndElement (EE) production
	 * 
	 * @return boolean according to whether EE is available
	 */
	public boolean hasEndElement();
	
	/*
	 * Retrieve grammar type
	 */
	public GrammarType getGrammarType();

	/*
	 * number of events
	 */
	public int getNumberOfEvents();
	
	public int get1stLevelEventCodeLength(boolean withFidelityOptionsOrNonStrict);


	/*
	 * 
	 */
	public void addProduction(Event event, Grammar grammar);

	/*
	 * learning grammar
	 */
	public void learnStartElement(StartElement se);

	public void learnEndElement();

	public void learnAttribute(Attribute at);

	public void learnCharacters();
	
	/**
	 * Learning is stopped for the EXI Profile.
	 * 
	 * <p>Grammar learning is stopped in the sense that counters are incremented but the actual events cannot be retrieved anymore.
	 * However, it is taken care that neither EE, CH, nor AT(xsi:type) is learned more than twice.</p> 
	 */
	public void stopLearning();

	
	/**
	 * Reports internal state
	 * 
	 * @return -1 for learning not stopped. Otherwise ID
	 */
	public int learningStopped();
	
	/*
	 * For moving to element content grammar
	 */
	public Grammar getElementContentGrammar();

	/*
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */

	// for encoder
	public Production getProduction(EventType eventType);

	public Production getStartElementProduction(String namespaceURI,
			String localName);

	public Production getStartElementNSProduction(String namespaceURI);

	public Production getAttributeProduction(String namespaceURI,
			String localName);

	public Production getAttributeNSProduction(String namespaceURI);

	// for decoder
	public Production getProduction(int eventCode);

}
