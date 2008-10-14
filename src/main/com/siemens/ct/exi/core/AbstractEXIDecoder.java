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
import java.io.InputStream;

import javax.xml.XMLConstants;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.EXIDecoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.event.Attribute;
import com.siemens.ct.exi.grammar.event.Characters;
import com.siemens.ct.exi.grammar.event.Event;
import com.siemens.ct.exi.grammar.event.EventType;
import com.siemens.ct.exi.grammar.event.StartElement;
import com.siemens.ct.exi.grammar.rule.Rule;
import com.siemens.ct.exi.grammar.rule.SchemaInformedRule;
import com.siemens.ct.exi.io.block.DecoderBlock;
import com.siemens.ct.exi.io.channel.BitDecoderChannel;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20081009
 */

public abstract class AbstractEXIDecoder extends AbstractEXICoder implements EXIDecoder
{
	// next event
	protected Event			nextEvent;
	protected EventType		nextEventType;
	protected int			ec;

	// decoder stream
	protected InputStream	is;
	protected DecoderBlock	block;

	// current values
	protected String		elementURI;
	protected String		elementLocalName;
	protected String		attributeURI;
	protected String		attributeLocalName;

	protected String		attributeValue;
	protected String		xsiTypeUri;
	protected String		xsiTypeName;
	protected boolean		xsiNil;
	protected String		characters;
	protected String		comment;
	protected String		nsURI;
	protected String		nsPrefix;
	protected String		piTarget;
	protected String		piData;

	public AbstractEXIDecoder ( EXIFactory exiFactory )
	{
		super ( exiFactory );
	}

	@Override
	protected void initForEachRun () throws EXIException
	{
		super.initForEachRun ( );

		try
		{
			block = exiFactory.createDecoderBlock ( is );
		}
		catch ( IOException e )
		{
			throw new EXIException ( e );
		}
	}

	public void setInputStream ( InputStream is ) throws EXIException
	{
		this.is = is;

		// parse header (bit-wise BUT byte padded!)
		BitDecoderChannel headerChannel = new BitDecoderChannel ( is );
		EXIHeader.parse ( headerChannel );

		initForEachRun ( );
	}

	protected void decodeEventCode () throws EXIException
	{
		ec = decode1stLevelEventCode ( );

		if ( ec == Constants.NOT_FOUND )
		{
			nextEvent = null;

			// 2nd level ?
			int ec2 = decode2ndLevelEventCode ( );

			if ( ec2 == Constants.NOT_FOUND )
			{
				// 3rd level
				int ec3 = decode3rdLevelEventCode ( );
				nextEventType = getCurrentRule ( ).get3rdLevelEvent ( ec3, getFidelityOptions ( ) );
			}
			else
			{
				nextEventType = getCurrentRule ( ).get2ndLevelEvent ( ec2, getFidelityOptions ( ) );
				
				if ( nextEventType == EventType.ATTRIBUTE_INVALID_VALUE )
				{
					updateInvalidValueAttribute();
				}
			}
		}
		else
		{
			nextEvent = getCurrentRule ( ).get1stLevelEvent ( ec );
			nextEventType = nextEvent.getEventType ( );
		}
	}
	
	protected void updateInvalidValueAttribute() throws EXIException
	{
		SchemaInformedRule sir = (SchemaInformedRule)getCurrentRule ( );
		
		int ec3AT;
		try
		{
			ec3AT = block.readEventCode ( sir.getNumberOfSchemaDeviatedAttributes ( ) );
		}
		catch ( IOException e )
		{
			throw new EXIException( e );
		}
		
		ec = ec3AT + sir.getLeastAttributeEventCode ( );
		nextEvent = getCurrentRule ( ).get1stLevelEvent ( ec );
	}

	protected int decode1stLevelEventCode () throws EXIException
	{
		try
		{
			int ch1 = getCurrentRule ( ).get1stLevelCharacteristics ( getFidelityOptions ( ) );
			int level1 = block.readEventCode ( ch1 );

			if ( getCurrentRule ( ).hasSecondOrThirdLevel ( exiFactory.getFidelityOptions ( ) ) )
			{
				return ( level1 < ( ch1 - 1 ) ? level1 : Constants.NOT_FOUND );
			}
			else
			{
				return ( level1 < ch1 ? level1 : Constants.NOT_FOUND );
			}
		}
		catch ( IOException e )
		{
			throw new EXIException ( e );
		}
	}

	protected int decode2ndLevelEventCode () throws EXIException
	{
		try
		{
			int ch2 = getCurrentRule ( ).get2ndLevelCharacteristics ( getFidelityOptions ( ) );
			int level2 = block.readEventCode ( ch2 );

			if ( getCurrentRule ( ).get3rdLevelCharacteristics ( getFidelityOptions ( ) ) > 0 )
			{
				return ( level2 < ( ch2 - 1 ) ? level2 : Constants.NOT_FOUND );
			}
			else
			{
				return ( level2 < ch2 ? level2 : Constants.NOT_FOUND );
			}
		}
		catch ( IOException e )
		{
			throw new EXIException ( e );
		}
	}

	protected int decode3rdLevelEventCode () throws EXIException
	{
		try
		{
			int ch3 = getCurrentRule ( ).get3rdLevelCharacteristics ( getFidelityOptions ( ) );
			return block.readEventCode ( ch3 );
		}
		catch ( IOException e )
		{
			throw new EXIException ( e );
		}
	}

	protected void decodeStartDocumentStructure () throws EXIException
	{
		// step forward
		replaceRuleAtTheTop ( getCurrentRule ( ).get1stLevelRule ( ec ) );
	}

	protected void decodeStartElementStructure () throws EXIException
	{
		// StartEvent
		this.elementURI = ( (StartElement) nextEvent ).getNamespaceURI ( );
		this.elementLocalName = ( (StartElement) nextEvent ).getLocalPart ( );

		// step forward in current rule (replace rule at the top)
		replaceRuleAtTheTop ( getCurrentRule ( ).get1stLevelRule ( ec ) );

		// update grammars etc.
		pushRule ( elementURI, elementLocalName );
		pushScope ( elementURI, elementLocalName );
	}

	protected void decodeStartElementGenericStructure () throws EXIException
	{
		// decode uri & local-name
		decodeStartElementExpandedName ( );

		Rule tmpStorage = getCurrentRule ( );

		// step forward in current rule (replace rule at the top)
		replaceRuleAtTheTop ( getCurrentRule ( ).get1stLevelRule ( ec ) );

		// learn start-element ?
		tmpStorage.learnStartElement ( elementURI, elementLocalName );

		// update grammars etc.
		pushRule ( elementURI, elementLocalName );
		pushScope ( elementURI, elementLocalName );
	}

	protected void decodeStartElementGenericUndeclaredStructure () throws EXIException
	{
		// decode uri & local-name
		decodeStartElementExpandedName ( );

		// learn start-element ?
		getCurrentRule ( ).learnStartElement ( elementURI, elementLocalName );

		// step forward in current rule (replace rule at the top)
		replaceRuleAtTheTop ( getCurrentRule ( ).getElementContentRuleForUndeclaredSE ( ) );

		// update grammars etc.
		pushRule ( elementURI, elementLocalName );
		pushScope ( elementURI, elementLocalName );
	}

	protected void decodeStartElementExpandedName () throws EXIException
	{
		try
		{
			// decode uri & local-name
			this.elementURI = block.readUri ( );
			this.elementLocalName = block.readLocalName ( elementURI );
		}
		catch ( IOException e )
		{
			throw new EXIException ( e );
		}
	}

	protected void decodeNamespaceDeclarationStructure () throws EXIException
	{
		try
		{
			// prefix mapping
			nsURI = block.readUri ( );
			nsPrefix = block.readPrefix ( nsURI );
			boolean local_element_ns = block.readBoolean ( );
			if ( local_element_ns )
			{
				// TODO local_element_ns
				// System.out.println ( "local_element_ns: " + nsPrefix );
			}
		}
		catch ( IOException e )
		{
			throw new EXIException ( e );
		}
	}

	protected Attribute decodeAttributeStructure () throws EXIException
	{
		// Attribute
		Attribute at = ( (Attribute) nextEvent );
		this.attributeURI = at.getNamespaceURI ( );
		this.attributeLocalName = at.getLocalPart ( );

		// step forward in current rule (replace rule at the top)
		replaceRuleAtTheTop ( getCurrentRule ( ).get1stLevelRule ( ec ) );

		return at;
	}

	protected void decodeAttributeGenericStructure () throws EXIException
	{
		//	decode structure
		decodeAttributeGenericUndeclaredStructure ();
		
		// step forward in current rule (replace rule at the top)
		replaceRuleAtTheTop ( getCurrentRule ( ).get1stLevelRule ( ec ) );
	}
	
	protected void decodeAttributeGenericUndeclaredStructure () throws EXIException
	{
		try
		{
			// decode uri & local-name
			this.attributeURI = block.readUri ( );
			this.attributeLocalName = block.readLocalName ( attributeURI );

			if ( attributeURI.equals ( XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI )
					&& ( attributeLocalName.equals ( Constants.XSI_NIL ) || attributeLocalName.equals ( Constants.XSI_TYPE ) ) )
			{
				//	no learning for xsi:type or xsi:nil
			}
			else
			{
				// update grammar
				getCurrentRule ( ).learnAttribute ( attributeURI, attributeLocalName );				
			}
		}
		catch ( IOException e )
		{
			throw new EXIException ( e );
		}
	}

	protected void decodeAttributeXsiType () throws EXIException
	{
		try
		{
			// decode type
			xsiTypeUri = block.readUri ( );
			xsiTypeName = block.readLocalName ( xsiTypeUri );
		}
		catch ( IOException e )
		{
			throw new EXIException ( e );
		}
	}

	protected void decodeAttributeXsiNil () throws EXIException
	{
		try
		{
			// decode nil
			this.xsiNil = block.readBoolean ( );
		}
		catch ( IOException e )
		{
			throw new EXIException ( e );
		}
	}

	protected Characters decodeCharactersStructure () throws IOException
	{
		// step forward
		replaceRuleAtTheTop ( getCurrentRule ( ).get1stLevelRule ( ec ) );

		return (Characters) nextEvent;
	}

	protected void decodeCharactersGenericStructure ()
	{
		replaceRuleAtTheTop ( getCurrentRule ( ).get1stLevelRule ( ec ) );
	}

	protected void decodeCharactersUndeclaredStructure ()
	{
		// learn character event ?
		getCurrentRule ( ).learnCharacters ( );

		// step forward in current rule (replace rule at the top)
		replaceRuleAtTheTop ( getCurrentRule ( ).getElementContentRule ( ) );
	}

	protected void decodeEndElementStructure ()
	{
		// pop top rule
		popRule ( );
		popScope ( );
	}

	protected void decodeEndDocumentStructure () throws EXIException
	{
		popRule ( );
	}

	protected void decodeCommentStructure () throws EXIException
	{
		try
		{
			comment = block.readString ( );

			// step forward
			replaceRuleAtTheTop ( getCurrentRule ( ).getElementContentRule ( ) );
		}
		catch ( IOException e )
		{
			throw new EXIException ( e );
		}

	}

	protected void decodeProcessingInstructionStructure () throws EXIException
	{
		try
		{
			// target & data
			piTarget = block.readString ( );
			piData = block.readString ( );

			// step forward
			replaceRuleAtTheTop ( getCurrentRule ( ).getElementContentRule ( ) );
		}
		catch ( IOException e )
		{
			throw new EXIException ( e );
		}
	}

	public String getElementURI ()
	{
		return elementURI;
	}

	public String getElementLocalName ()
	{
		return elementLocalName;
	}

	public String getAttributeURI ()
	{
		return attributeURI;
	}

	public String getAttributeLocalName ()
	{
		return attributeLocalName;
	}

	public String getAttributeValue ()
	{
		return attributeValue;
	}

	public String getXsiTypeUri ()
	{
		return this.xsiTypeUri;
	}

	public String getXsiTypeName ()
	{
		return this.xsiTypeName;
	}

	public boolean getXsiNil ()
	{
		return xsiNil;
	}

	public String getCharacters ()
	{
		return characters;
	}

	public String getComment ()
	{
		return comment;
	}

	public String getNSUri ()
	{
		return nsURI;
	}

	public String getNSPrefix ()
	{
		return nsPrefix;
	}

	public String getPITarget ()
	{
		return piTarget;
	}

	public String getPIData ()
	{
		return piData;
	}

}
