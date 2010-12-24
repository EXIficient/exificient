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

import java.io.Serializable;

import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.grammar.EventInformation;
import com.siemens.ct.exi.grammar.event.Attribute;
import com.siemens.ct.exi.grammar.event.Event;
import com.siemens.ct.exi.grammar.event.EventType;
import com.siemens.ct.exi.grammar.event.StartElement;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.6
 */

public interface Rule extends Serializable {

	/*
	 * rule created according to schema information
	 */
	public boolean isSchemaInformed();

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

	public EventType get2ndLevelEvent(int eventCode,
			FidelityOptions fidelityOptions);

	public EventType get3rdLevelEvent(int eventCode,
			FidelityOptions fidelityOptions);


	public int get1stLevelEventCodeLength(FidelityOptions fidelityOptions);


	public int get2ndLevelCharacteristics(FidelityOptions fidelityOptions);

	public int get3rdLevelCharacteristics(FidelityOptions fidelityOptions);

	/*
	 * TODO move to SchemaInformedRule 
	 */
	public void addRule(Event event, Rule rule);

	

	/*
	 * learning grammar
	 */
	public void learnStartElement(StartElement se);

	public void learnEndElement();

	public void learnAttribute(Attribute at);

	public void learnCharacters();

	/*
	 * For moving to element grammar
	 */
	public Rule getElementContentRule();
	

	/*
	 * TODO move to SchemaInformedRule
	 * Schema-deviated attributes
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
	public EventInformation lookForEvent( EventType eventType );
	
	public EventInformation lookForStartElement( String namespaceURI, String localName);
	public EventInformation lookForStartElementNS( String namespaceURI );

	public EventInformation lookForAttribute( String namespaceURI, String localName);
	public EventInformation lookForAttributeNS( String namespaceURI );
	
	//	for decoder
	public EventInformation lookFor( int eventCode );
	
}
