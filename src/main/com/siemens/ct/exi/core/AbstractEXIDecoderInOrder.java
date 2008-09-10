/*
 * Copyright (C) 2007, 2008 Siemens AG
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

package com.siemens.ct.exi.core;

import java.io.IOException;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.Grammar;
import com.siemens.ct.exi.grammar.SchemaInformedGrammar;
import com.siemens.ct.exi.grammar.TypeGrammar;
import com.siemens.ct.exi.grammar.event.EventType;
import com.siemens.ct.exi.grammar.event.StartDocument;
import com.siemens.ct.exi.grammar.rule.SchemaInformedRule;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20080718
 */

public abstract class AbstractEXIDecoderInOrder extends AbstractEXIDecoder
{	
	public AbstractEXIDecoderInOrder( EXIFactory exiFactory )
	{
		super( exiFactory );
	}
	
	@Override
	protected void initForEachRun() throws EXIException
	{
		super.initForEachRun( );
		
		nextEvent = new StartDocument( );
		nextEventType = EventType.START_DOCUMENT;
	}
	
	public void inspectEvent( ) throws EXIException
	{
		decodeEventCode ( );
	}
	
	public boolean hasNextEvent( )
	{
		return nextEventType != EventType.END_DOCUMENT;
	}
	
	public EventType getNextEventType( ) 
	{
		return nextEventType;
	}
	
	
	public void decodeStartDocument( ) throws EXIException
	{
		decodeStartDocumentStructure ( );
	}
	
	public void decodeStartElement( ) throws EXIException
	{	
		decodeStartElementStructure ( );
	}
	
	public void decodeStartElementGeneric( ) throws EXIException
	{
		decodeStartElementGenericStructure ( );
	}

	public void decodeStartElementGenericUndeclared( ) throws EXIException
	{
		decodeStartElementGenericUndeclaredStructure ( );
	}
	
	public void decodeNamespaceDeclaration( ) throws EXIException
	{
		decodeNamespaceDeclarationStructure ( );
	}
	
	public void decodeAttribute( ) throws EXIException
	{
		try
		{
			//	decode attribute value
			attributeValue = block.readTypedValidValue ( decodeAttributeStructure ( ).getDatatype ( ), attributeURI, attributeLocalName );
		}
		catch ( IOException e )
		{
			throw new EXIException( e );
		}
	}
	
	
	public void decodeAttributeGeneric( ) throws EXIException
	{
		try
		{
			decodeAttributeGenericStructure ( );
					
			//	decode attribute value
			attributeValue = block.readValueAsString ( attributeURI, attributeLocalName );
		}
		catch ( IOException e )
		{
			throw new EXIException( e );
		}
	}
	public void decodeXsiType( ) throws EXIException
	{
		decodeAttributeXsiType ( );
		
		//	update grammar according to given xsi:type
		Grammar g = exiFactory.getGrammar ( );
		TypeGrammar tg = ((SchemaInformedGrammar)g).getTypeGrammar ( this.xsiTypeUri, this.xsiTypeName );
		this.replaceRuleAtTheTop ( tg.getType ( ) );
		
		//
		this.pushScopeType ( this.xsiTypeUri, this.xsiTypeName );
	}
	public void decodeXsiNil( ) throws EXIException
	{
		decodeAttributeXsiNil ( );
		
		if ( this.xsiNil )
		{
			//	jump to typeEmpty
			if ( getCurrentRule ( ) instanceof SchemaInformedRule )
			{
				replaceRuleAtTheTop ( ((SchemaInformedRule)getCurrentRule ( )).getTypeEmpty ( ) );
			}
			else
			{
				throw new EXIException( "EXI, no typeEmpty for xsi:nil");
			}
		}
	}
	
	public void decodeCharacters( ) throws EXIException
	{	
		try
		{
			characters = block.readTypedValidValue ( decodeCharactersStructure ( ).getDatatype ( ), getScopeURI ( ), getScopeLocalName ( ) );
		}
		catch ( IOException e )
		{
			throw new EXIException( e );
		}
	}
	
	protected void decodeCharactersGenericValue() throws EXIException
	{
		try
		{
			characters = block.readValueAsString ( getScopeURI ( ), getScopeLocalName ( ) );
		}
		catch ( IOException e )
		{
			throw new EXIException ( e );
		}
	}
	
	public void decodeCharactersGeneric( ) throws EXIException
	{	
		decodeCharactersGenericStructure ( );
		
		decodeCharactersGenericValue();
	}
	
	public void decodeCharactersGenericUndeclared( ) throws EXIException
	{	
		decodeCharactersUndeclaredStructure ( );
		
		decodeCharactersGenericValue();
	}
	
	
	public void decodeEndElement( ) throws EXIException
	{
		decodeEndElementStructure ( );
	}
	
	public void decodeEndDocument( ) throws EXIException
	{
		decodeEndDocumentStructure ( );
	}
	
	public void decodeComment( ) throws EXIException
	{
		decodeCommentStructure ( );
	}

	
	public void decodeProcessingInstruction( ) throws EXIException
	{
		decodeProcessingInstructionStructure ( );
	}
	
}
