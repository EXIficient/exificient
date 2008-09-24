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
import java.io.OutputStream;

import javax.xml.XMLConstants;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.EXIEncoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.core.sax.NamespacePrefixLevels;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.exceptions.XMLParsingException;
import com.siemens.ct.exi.grammar.Grammar;
import com.siemens.ct.exi.grammar.SchemaInformedGrammar;
import com.siemens.ct.exi.grammar.TypeGrammar;
import com.siemens.ct.exi.grammar.event.DatatypeEvent;
import com.siemens.ct.exi.grammar.event.EventType;
import com.siemens.ct.exi.grammar.rule.Rule;
import com.siemens.ct.exi.grammar.rule.SchemaInformedRule;
import com.siemens.ct.exi.io.block.EncoderBlock;
import com.siemens.ct.exi.util.ExpandedName;
import com.siemens.ct.exi.util.datatype.XSDBoolean;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20080915
 */

public abstract class AbstractEXIEncoder extends AbstractEXICoder implements EXIEncoder
{
	protected EncoderBlock			block;

	protected OutputStream			os;

	// to parse raw nil value
	protected XSDBoolean			nil;

	// namespace prefixes are related to elements
	protected NamespacePrefixLevels	nsPrefixes;

	protected String				lastSEprefix	= null;

	public AbstractEXIEncoder ( EXIFactory exiFactory )
	{
		super ( exiFactory );

		nsPrefixes = new NamespacePrefixLevels ( );
		nil = XSDBoolean.newInstance ( );
	}

	@Override
	protected void initForEachRun () throws EXIException
	{
		super.initForEachRun ( );

		// setup encoder-block
		this.block = exiFactory.createEncoderBlock ( os );
	}

	public void setOutput ( OutputStream os ) throws EXIException
	{
		this.os = os;

		// EXI header
		EXIHeader.write ( os );
	}

	protected void encode1stLevelEventCode ( int pos ) throws EXIException
	{
		assert ( pos < getCurrentRule ( ).get1stLevelCharacteristics ( getFidelityOptions ( ) ) );

		try
		{
			block.writeEventCode ( pos, getCurrentRule ( ).get1stLevelCharacteristics ( getFidelityOptions ( ) ) );
		}
		catch ( IOException e )
		{
			throw new EXIException ( e );
		}
	}

	protected void encode2ndLevelEventCode ( int pos ) throws EXIException
	{
		try
		{
			// 1st level
			int ch1 = getCurrentRule ( ).get1stLevelCharacteristics ( getFidelityOptions ( ) );
			block.writeEventCode ( ch1 - 1, ch1 );

			// 2nd level
			int ch2 = getCurrentRule ( ).get2ndLevelCharacteristics ( getFidelityOptions ( ) );
			assert ( pos < ch2 );

			block.writeEventCode ( pos, ch2 );
		}
		catch ( IOException e )
		{
			throw new EXIException ( e );
		}
	}

	protected void encode3rdLevelEventCode ( int pos ) throws EXIException
	{
		try
		{
			// 1st level
			int ch1 = getCurrentRule ( ).get1stLevelCharacteristics ( getFidelityOptions ( ) );
			block.writeEventCode ( ch1 - 1, ch1 );

			// 2nd level
			int ch2 = getCurrentRule ( ).get2ndLevelCharacteristics ( getFidelityOptions ( ) );
			block.writeEventCode ( ch2 - 1, ch2 );

			// 3rd level
			int ch3 = getCurrentRule ( ).get3rdLevelCharacteristics ( getFidelityOptions ( ) );
			assert ( pos < ch3 );
			block.writeEventCode ( pos, ch3 );
		}
		catch ( IOException e )
		{
			throw new EXIException ( e );
		}
	}

	protected void encodeQName ( String uri, String localName ) throws EXIException
	{
		try
		{
			block.writeUri ( uri );
			block.writeLocalName ( localName, uri );
		}
		catch ( IOException e )
		{
			throw new EXIException ( e );
		}
	}

	// Note: value needs to be type-checked beforehands
	protected void encodeTypeValidValue ( int ec, String uri, String localName ) throws EXIException
	{
		try
		{
			// encode EventCode
			encode1stLevelEventCode ( ec );

			// step forward in current rule (replace rule at the top)
			replaceRuleAtTheTop ( getCurrentRule ( ).get1stLevelRule ( ec ) );

			// value content
			block.writeTypeValidValue ( uri, localName );
		}
		catch ( Exception e )
		{
			throw new EXIException ( e );
		}
	}

	protected void encodeGenericValue ( int ec, String uri, String localName, String value ) throws EXIException
	{
		// step forward in current rule (replace rule at the top)
		replaceRuleAtTheTop ( getCurrentRule ( ).get1stLevelRule ( ec ) );

		try
		{
			// content as string
			block.writeValueAsString ( uri, localName, value );
		}
		catch ( IOException e )
		{
			throw new EXIException ( e );
		}
	}

	protected void encodeUnexpectedAttribute ( String uri, String localName, String value ) throws EXIException
	{
		// encode expanded name
		encodeExpandedName ( uri, localName );

		// encode content as string
		try
		{
			block.writeValueAsString ( uri, localName, value );
		}
		catch ( IOException e )
		{
			throw new EXIException ( e );
		}
	}

	protected void encodeUnexpectedAttributeAndLearn ( String uri, String localName, String value ) throws EXIException
	{
		// encode unexpected attribute
		encodeUnexpectedAttribute ( uri, localName, value );

		// learn attribute event ?
		getCurrentRule ( ).learnAttribute ( uri, localName );
	}

	protected void encodeExpandedName ( String uri, String localName ) throws EXIException
	{
		try
		{
			// encode expanded name (uri & localName)
			block.writeUri ( uri );
			block.writeLocalName ( localName, uri );
		}
		catch ( IOException e )
		{
			throw new EXIException ( e );
		}
	}


	public void encodeStartDocument () throws EXIException
	{
		if ( this.os == null )
		{
			throw new EXIException ( "No valid EXI OutputStream set for encoding. Please use setOutput( ... )" );
		}

		this.initForEachRun ( );

		// replaceRuleAtTheTop ( getCurrentRule ( ).stepForward ( isStartTagRule
		// ( ), EventType.START_DOCUMENT ) );
		replaceRuleAtTheTop ( getCurrentRule ( ).get1stLevelRule ( 0 ) );

	}

	public void encodeEndDocument () throws EXIException
	{
		int ec = getCurrentRule ( ).get1stLevelEventCode ( eventED, getFidelityOptions ( ) );

		if ( ec == Constants.NOT_FOUND )
		{
			throw new EXIException ( "No EXI Event found for endDocument" );
		}
		else
		{
			// encode EventCode
			this.encode1stLevelEventCode ( ec );
		}

		try
		{
			// flush chunk(s) to bit/byte output stream
			block.flush ( );
			// block.close ( );
		}
		catch ( IOException e )
		{
			throw new EXIException ( e );
		}
	}

	public void encodeStartElement ( String uri, String localName ) throws EXIException
	{
		// update lookup event
		eventSE.setNamespaceURI ( uri );
		eventSE.setLocalPart ( localName );

		int ec = getCurrentRule ( ).get1stLevelEventCode ( eventSE, getFidelityOptions ( ) );

		if ( ec == Constants.NOT_FOUND )
		{
			// generic SE (on first level)
			int ecGeneric = getCurrentRule ( ).get1stLevelEventCode ( eventSEg, getFidelityOptions ( ) );

			if ( ecGeneric == Constants.NOT_FOUND )
			{
				// Undeclared SE(*) can be found on 2nd level
				int ecSEundeclared = getCurrentRule ( ).get2ndLevelEventCode (
						EventType.START_ELEMENT_GENERIC_UNDECLARED, exiFactory.getFidelityOptions ( ) );

				if ( ecSEundeclared == Constants.NOT_FOUND )
				{
					// TODO skip element ?
					throw new IllegalArgumentException ( "SE " + uri + ":" + localName );
				}
				else
				{
					// encode [undeclared] event-code
					encode2ndLevelEventCode ( ecSEundeclared );

					// encode expanded name
					encodeExpandedName ( uri, localName );

					// learn startElement event ?
					getCurrentRule ( ).learnStartElement ( uri, localName );

					// step forward in current rule (replace rule at the top)
					replaceRuleAtTheTop ( getCurrentRule ( ).getElementContentRuleForUndeclaredSE ( ) );

					// push next rule
					pushRule ( uri, localName );
				}
			}
			else
			{
				//	SE(*) on first level
				// encode EventCode
				encode1stLevelEventCode ( ecGeneric );

				// encode expanded name
				encodeExpandedName ( uri, localName );

				Rule tmpStorage = getCurrentRule ( );

				// step forward in current rule (replace rule at the top)
				replaceRuleAtTheTop ( getCurrentRule ( ).get1stLevelRule ( ecGeneric ) );

				// push next rule
				pushRule ( uri, localName );

				// learning in schema-less case
				tmpStorage.learnStartElement ( uri, localName );
			}
		}
		else
		{
			// encode EventCode
			encode1stLevelEventCode ( ec );

			// step forward in current rule (replace rule at the top)
			replaceRuleAtTheTop ( getCurrentRule ( ).get1stLevelRule ( ec ) );

			// push next rule
			pushRule ( uri, localName );
		}

		// update scope
		pushScope ( uri, localName );
	}

	public void encodeStartElement ( String uri, String localName, String prefix ) throws EXIException
	{
		nsPrefixes.addLevel ( );

		encodeStartElement ( uri, localName );

		// prefix
		lastSEprefix = prefix;

		if ( this.nsPrefixes.hasPrefixForURI ( uri ) )
		{
			// TODO *overlapping* prefixes: same namespace BUT different
			// prefixes
			// System.out.println ( "SE_PFX uri found for " + uri + " --> " +
			// prefix );
		}
		else
		{
			/*
			 * If there are no prefixes specified for the URI of the QName by
			 * preceding NS events in the EXI stream, the prefix is undefined.
			 * An undefined prefix is represented using zero bits (i.e.,
			 * omitted).
			 */
		}
	}

	public void encodeEndElement () throws EXIException
	{
		int ec = getCurrentRule ( ).get1stLevelEventCode ( eventEE, getFidelityOptions ( ) );

		// Special case: SAX does not inform about empty ("") CH events
		// --> if EE is not found check whether an empty CH event *helps*
		if ( ec == Constants.NOT_FOUND )
		{
			int ecCH = getCurrentRule ( ).get1stLevelEventCode ( eventCH, getFidelityOptions ( ) );

			if ( ecCH != Constants.NOT_FOUND
					&& block.isTypeValid ( getDatatypeOfEvent ( ecCH ), Constants.EMPTY_STRING ) )
			{

				// Yep, CH is successful

				// encode schema-valid content plus moves on in grammar
				encodeTypeValidValue ( ecCH, getScopeURI ( ), getScopeLocalName ( ) );

				// try the EE event once again
				ec = getCurrentRule ( ).get1stLevelEventCode ( eventEE, getFidelityOptions ( ) );
			}
		}

		if ( ec == Constants.NOT_FOUND )
		{
			// Undeclared EE can be found on 2nd level
			int ecEEundeclared = getCurrentRule ( ).get2ndLevelEventCode ( EventType.END_ELEMENT_UNDECLARED,
					exiFactory.getFidelityOptions ( ) );

			if ( ecEEundeclared == Constants.NOT_FOUND )
			{
				// TODO skip element ?
				throw new IllegalArgumentException ( "EE " + getScopeURI ( ) + ":" + getScopeLocalName ( ) );
			}
			else
			{
				// encode [undeclared] event-code
				encode2ndLevelEventCode ( ecEEundeclared );
			}
		}
		else
		{
			// encode EventCode
			encode1stLevelEventCode ( ec );
		}

		// pop the rule from the top of the stack
		popRule ( );
		popScope ( );

		// TODO how to detect whether we deal with preserve prefix on or not in
		// a smart way
		if ( lastSEprefix != null )
		{
			nsPrefixes.removeLevel ( );
		}
	}

	public void encodeNamespaceDeclaration ( String uri, String prefix ) throws EXIException
	{
		if ( getFidelityOptions ( ).isFidelityEnabled ( FidelityOptions.FEATURE_PREFIX ) )
		{
			try
			{
				// event code
				int ec2 = getCurrentRule ( ).get2ndLevelEventCode ( EventType.NAMESPACE_DECLARATION,
						getFidelityOptions ( ) );
				encode2ndLevelEventCode ( ec2 );

				// prefix mapping
				block.writeUri ( uri );
				block.writePrefix ( prefix, uri );

				// local-element-ns
				if ( prefix.equals ( lastSEprefix ) )
				{
					// System.out.println ( "Prefix '" + prefix + "' is part of
					// an SE event followed by an associated NS event");
					block.writeBoolean ( true );
				}
				else
				{
					block.writeBoolean ( false );
				}
				nsPrefixes.addPrefix ( uri, prefix );
			}
			catch ( IOException e )
			{
				throw new EXIException ( e );
			}
		}
	}

	public void encodeXsiType ( String uri, String localName, String raw ) throws EXIException
	{
		if ( getCurrentRule ( ).isSchemaRule ( ) )
		{
			int ec2 = getCurrentRule ( ).get2ndLevelEventCode ( EventType.ATTRIBUTE_XSI_TYPE,
					exiFactory.getFidelityOptions ( ) );

			if ( ec2 == Constants.NOT_FOUND )
			{
				// schema deviation in strict mode ONLY
				assert ( getFidelityOptions ( ).isStrict ( ) );

				String msg = "Skip unexpected type-cast, xsi:type ({" + uri + "}" + localName;
				errorHandler.warning ( new EXIException ( msg ) );
			}
			else
			{
				// lookup type-grammar
				Grammar g = exiFactory.getGrammar ( );
				TypeGrammar tg = ( (SchemaInformedGrammar) g ).getTypeGrammar ( uri, localName );

				if ( tg == null )
				{
					// type unknown --> for know throw error
					throw new EXIException ( "EXI, no type grammar found for " + new ExpandedName ( uri, localName ) );
				}
				else
				{
					// encode event-code
					encode2ndLevelEventCode ( ec2 );
					// type as qname
					encodeQName ( uri, localName );

					// update grammar according to given xsi:type
					this.replaceRuleAtTheTop ( tg.getType ( ) );

					this.pushScopeType ( uri, localName );
				}
			}
		}
		else
		{
			// schema-less mode

			// AT(*) can be found on 2nd level
			int ecATundeclared = getCurrentRule ( ).get2ndLevelEventCode ( EventType.ATTRIBUTE_GENERIC_UNDECLARED,
					exiFactory.getFidelityOptions ( ) );

			if ( ecATundeclared == Constants.NOT_FOUND )
			{
				// Warn encoder that the attribute is simply skipped
				String msg = "Skip AT xsi:type: " + raw;
				errorHandler.warning ( new EXIException ( msg ) );
			}
			else
			{
				// encode event-code
				encode2ndLevelEventCode ( ecATundeclared );


				// TODO The value of each AT (xsi:type) event matching the AT(*)
				// terminal is represented as a QName (see 7.1.7 QName). If there is
				// no namespace in scope for the specified qname prefix, the QName
				// uri is set to empty ("") and the QName localName is set to the
				// full lexical value of the QName, including the prefix.
				encodeUnexpectedAttribute ( XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, Constants.XSI_TYPE, raw );
			}
		}
	}

	public void encodeXsiNil ( String rawNil ) throws EXIException
	{
		if ( getCurrentRule ( ).isSchemaRule ( ) )
		{
			// nillable ?
			int ec2 = getCurrentRule ( ).get2ndLevelEventCode ( EventType.ATTRIBUTE_XSI_NIL,
					exiFactory.getFidelityOptions ( ) );

			if ( ec2 == Constants.NOT_FOUND )
			{
				System.err.println ( "xsi:nil schema deviation not handled yet!" );
			}
			else
			{
				// schema-valid boolean ?
				try
				{
					nil.parse ( rawNil );

					// encode event-code + nil value
					encode2ndLevelEventCode ( ec2 );
					try
					{
						block.writeBoolean ( nil.getBoolean ( ) );
					}
					catch ( IOException e )
					{
						throw new EXIException ( e );
					}

					if ( nil.getBoolean ( ) )
					{
						if ( getCurrentRule ( ) instanceof SchemaInformedRule )
						{

							replaceRuleAtTheTop ( ( (SchemaInformedRule) getCurrentRule ( ) ).getTypeEmpty ( ) );
						}
						else
						{
							throw new EXIException ( "EXI, no typeEmpty defined for xsi:nil" );
						}
					}

				}
				catch ( XMLParsingException e )
				{
					// TODO If the value is not a schema-valid Boolean, the AT
					// (xsi:nil) event is represented by the AT(*)
					// [schema-invalid value] terminal
				}
			}
		}
		else
		{
			// schema-less mode

			// AT(*) can be found on 2nd level
			int ecATundeclared = getCurrentRule ( ).get2ndLevelEventCode ( EventType.ATTRIBUTE_GENERIC_UNDECLARED,
					exiFactory.getFidelityOptions ( ) );

			if ( ecATundeclared == Constants.NOT_FOUND )
			{
				// Warn encoder that the attribute is simply skipped
				String msg = "Skip AT xsi:nil";
				errorHandler.warning ( new EXIException ( msg ) );
			}
			else
			{
				// encode event-code
				encode2ndLevelEventCode ( ecATundeclared );

				encodeUnexpectedAttribute ( XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, Constants.XSI_NIL, rawNil );
			}
		}
	}

	public void encodeAttribute ( final String uri, final String localName, String value ) throws EXIException
	{
		Rule currentRule = getCurrentRule ( );

		eventAT.setNamespaceURI ( uri );
		eventAT.setLocalPart ( localName );

		int ec = currentRule.get1stLevelEventCode ( eventAT, getFidelityOptions ( ) );

		if ( ec == Constants.NOT_FOUND )
		{
			// generic AT (on first level)
			int ecGeneric = getCurrentRule ( ).get1stLevelEventCode ( eventATg, getFidelityOptions ( ) );

			if ( ecGeneric == Constants.NOT_FOUND )
			{
				// Undeclared AT(*) can be found on 2nd level
				int ecATundeclared = getCurrentRule ( ).get2ndLevelEventCode ( EventType.ATTRIBUTE_GENERIC_UNDECLARED,
						exiFactory.getFidelityOptions ( ) );

				if ( ecATundeclared == Constants.NOT_FOUND )
				{
					// Warn encoder that the attribute is simply skipped
					// Note: should never happen except in strict mode
					assert ( getFidelityOptions ( ).isStrict ( ) );
					String msg = "Skip AT " + uri + ":" + localName + " = " + value + " (StrictMode="
							+ getFidelityOptions ( ).isStrict ( ) + ")";
					errorHandler.warning ( new EXIException ( msg ) );
				}
				else
				{
					// encode event-code
					encode2ndLevelEventCode ( ecATundeclared );

					encodeUnexpectedAttributeAndLearn ( uri, localName, value );
				}
			}
			else
			{
				// encode EventCode
				encode1stLevelEventCode ( ecGeneric );

				encodeUnexpectedAttributeAndLearn ( uri, localName, value );

				// step forward in current rule (replace rule at the top)
				replaceRuleAtTheTop ( getCurrentRule ( ).get1stLevelRule ( ecGeneric ) );

			}
		}
		else
		{
			// attribute event found
			if ( block.isTypeValid ( getDatatypeOfEvent ( ec ), value ) )
			{
				// encode schema-valid content plus moves on in grammar
				encodeTypeValidValue ( ec, uri, localName );
			}
			else
			{
				// schema-invalid value AT
				throw new IllegalArgumentException ( "expected AT with deviated content!" );
			}
		}
	}

	public void encodeAttribute ( String uri, String localName, String prefix, String value ) throws EXIException
	{
		throw new RuntimeException ( "Encoding of AT prefix not yet implemented" );
	}

	public void encodeCharacters ( String chars ) throws EXIException
	{
		try
		{
			int ec = getCurrentRule ( ).get1stLevelEventCode ( eventCH, getFidelityOptions ( ) );

			if ( ec == Constants.NOT_FOUND )
			{
				// generic CH (on first level)
				int ecGeneric = getCurrentRule ( ).get1stLevelEventCode ( eventCHg, getFidelityOptions ( ) );

				if ( ecGeneric == Constants.NOT_FOUND )
				{
					// Undeclared CH can be found on 2nd level
					int ecCHundeclared = getCurrentRule ( ).get2ndLevelEventCode (
							EventType.CHARACTERS_GENERIC_UNDECLARED, exiFactory.getFidelityOptions ( ) );

					if ( ecCHundeclared == Constants.NOT_FOUND )
					{
						// TODO skip characters ?
						throw new IllegalArgumentException ( "CH: " + chars );
					}
					else
					{
						// encode [undeclared] event-code
						encode2ndLevelEventCode ( ecCHundeclared );

						// learn characters event ?
						getCurrentRule ( ).learnCharacters ( );

						// content as string
						block.writeValueAsString ( getScopeURI ( ), getScopeLocalName ( ), chars );

						// step forward in current rule (replace rule at the
						// top)
						replaceRuleAtTheTop ( getCurrentRule ( ).getElementContentRule ( ) );
					}
				}
				else
				{
					// encode EventCode
					encode1stLevelEventCode ( ecGeneric );

					// encode schema-invalid content as string plus moves on in
					// grammar
					encodeGenericValue ( ecGeneric, getScopeURI ( ), getScopeLocalName ( ), chars );
				}
			}
			else
			{
				// characters event found
				if ( block.isTypeValid ( getDatatypeOfEvent ( ec ), chars ) )
				{
					// encode EventCode, schema-valid content plus moves on in
					// grammar
					encodeTypeValidValue ( ec, getScopeURI ( ), getScopeLocalName ( ) );
				}
				else
				{
					throw new IllegalArgumentException ( "expected CH with deviated content!" );
				}
			}
		}
		catch ( IOException e )
		{
			throw new EXIException ( e );
		}

	}

	protected Datatype getDatatypeOfEvent ( int eventCode )
	{
		assert ( getCurrentRule ( ).get1stLevelEvent ( eventCode ) instanceof DatatypeEvent );

		return ( (DatatypeEvent) getCurrentRule ( ).get1stLevelEvent ( eventCode ) ).getDatatype ( );
	}

	public void encodeComment ( char[] ch, int start, int length ) throws EXIException
	{
		if ( getFidelityOptions ( ).isFidelityEnabled ( FidelityOptions.FEATURE_COMMENT ) )
		{
			try
			{
				// comments can be found on 3rd level
				int ec3 = getCurrentRule ( ).get3rdLevelEventCode ( EventType.COMMENT, getFidelityOptions ( ) );
				encode3rdLevelEventCode ( ec3 );

				// encode CM content
				block.writeString ( new String ( ch, start, length ) );

				// step forward (if not alreay content rule)
				replaceRuleAtTheTop ( getCurrentRule ( ).getElementContentRule ( ) );
			}
			catch ( IOException e )
			{
				throw new EXIException ( e );
			}
		}
	}

	public void encodeProcessingInstruction ( String target, String data ) throws EXIException
	{
		if ( getFidelityOptions ( ).isFidelityEnabled ( FidelityOptions.FEATURE_PI ) )
		{
			try
			{
				// processing instructions can be found on 3rd level
				int ec3 = getCurrentRule ( ).get3rdLevelEventCode ( EventType.PROCESSING_INSTRUCTION,
						getFidelityOptions ( ) );
				encode3rdLevelEventCode ( ec3 );

				// encode PI content
				block.writeString ( target );
				block.writeString ( data );

				// step forward
				replaceRuleAtTheTop ( getCurrentRule ( ).getElementContentRule ( ) );
			}
			catch ( IOException e )
			{
				throw new EXIException ( e );
			}
		}
	}

}
