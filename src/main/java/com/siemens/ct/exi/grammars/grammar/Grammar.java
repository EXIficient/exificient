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

import java.io.Serializable;

import com.siemens.ct.exi.FidelityOptions;
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
 * @version 0.9.2-SNAPSHOT
 */

public interface Grammar extends Serializable {

	/*
	 * rule created according to schema information
	 */
	public boolean isSchemaInformed();
	
	/*
	 * Retrieve grammar type
	 */
	public GrammarType getGrammarType();

	/*
	 * number of events
	 */
	public int getNumberOfEvents();

	public boolean hasSecondOrThirdLevel(FidelityOptions fidelityOptions);

	/*
	 * fetch event-code
	 */

	public int get2ndLevelEventCode(EventType eventType,
			FidelityOptions fidelityOptions);

	public int get3rdLevelEventCode(EventType eventType,
			FidelityOptions fidelityOptions);

	/*
	 * events, rules
	 */

	public EventType get2ndLevelEventType(int eventCode,
			FidelityOptions fidelityOptions);

	public EventType get3rdLevelEventType(int eventCode,
			FidelityOptions fidelityOptions);

	public int get1stLevelEventCodeLength(FidelityOptions fidelityOptions);

	public int get2ndLevelCharacteristics(FidelityOptions fidelityOptions);

	public int get3rdLevelCharacteristics(FidelityOptions fidelityOptions);

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
	 * TODO move to SchemaInformedRule Schema-deviated attributes
	 */
	public int getNumberOfDeclaredAttributes();

	public int getLeastAttributeEventCode();

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
