/*
 * Copyright (c) 2007-2018 Siemens AG
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

package com.siemens.ct.exi.main.cmd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.DTDHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.siemens.ct.exi.core.CodingMode;
import com.siemens.ct.exi.core.EXIFactory;
import com.siemens.ct.exi.core.EncodingOptions;
import com.siemens.ct.exi.core.FidelityOptions;
import com.siemens.ct.exi.core.exceptions.EXIException;
import com.siemens.ct.exi.core.helpers.DefaultEXIFactory;
import com.siemens.ct.exi.grammars.GrammarFactory;
import com.siemens.ct.exi.main.api.sax.EXIResult;
import com.siemens.ct.exi.main.api.sax.SAXFactory;
import com.siemens.ct.exi.main.util.FragmentUtilities;
import com.siemens.ct.exi.main.util.NoEntityResolver;
import com.siemens.ct.exi.main.util.SkipRootElementXMLReader;

/*
 * 
 * # Notebook
 * -encode -i .\data\W3C\PrimerNotebook\notebook.xml
 * -encode -i .\data\W3C\PrimerNotebook\notebook.xml -schema .\data\W3C\PrimerNotebook\notebook.xsd
 * -encode -preservePrefixes -includeOptions -i .\trunk\data\W3C\PrimerNotebook\notebook.xsd
 * -encode -i .\data\W3C\PrimerNotebook\notebook.xml -schema .\data\W3C\PrimerNotebook\notebook.xsd -compression
 *  
 * # DTDs
 * -encode -i .\data\general\doc-10.xml -preserveDTDs
 * -decode -i .\data\general\doc-10.xml.exi -preserveDTDs
 * 
 * # ER
 * 
 * -encode -i D:\Projects\W3C\Group\EXI\TTFMS\data\interop\schemaInformedGrammar\\undeclaredProductions\er-01.xml -preservePIs -preserveDTDs
 * -decode -i D:\Projects\W3C\Group\EXI\TTFMS\data\interop\schemaInformedGrammar\\undeclaredProductions\er-01.xml.exi -preservePIs -preserveDTDs
 * 
 * # Bug33
 * -encode -i  .\data\bugs\ID33\\useme.xml -compression
 * -decode -i  .\data\bugs\ID33\\useme.xml.exi -compression
 * 
 */
/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Richard.Kuntschke@siemens.com
 * 
 */

public class EXIficientCMD {
	public static final PrintStream ps = System.out;

	public static final String HELP = "-h";

	public static final String ENCODE = "-" + CmdOption.encode;
	public static final String DECODE = "-" + CmdOption.decode;

	public static final String INPUT = "-i";
	public static final String OUTPUT = "-o";

	public static final String NO_SCHEMA = "-" + SchemaOption.noSchema;
	public static final String XSD_SCHEMA = "-" + SchemaOption.xsdSchema;
	public static final String SCHEMA = "-" + SchemaOption.schema;

	public static final String OPTION_STRICT = "-strict";
	public static final String PRESERVE_COMMENTS = "-preserveComments";
	public static final String PRESERVE_LEXICAL_VALUES = "-preserveLexicalValues";
	public static final String PRESERVE_PREFIXES = "-preservePrefixes";
	public static final String PRESERVE_PIS = "-preservePIs";
	public static final String PRESERVE_DTDS = "-preserveDTDs";

	public static final String INCLUDE_OPTIONS = "-includeOptions";
	public static final String INCLUDE_COOKIE = "-includeCookie";
	public static final String INCLUDE_SCHEMA_ID = "-includeSchemaId";
	public static final String INCLUDE_SCHEMA_LOCATION = "-includeSchemaLocation";
	public static final String INCLUDE_INSIGNIFICANT_XSI_NIL = "-includeInsignificantXsiNil";
	public static final String INCLUDE_PROFILE_VALUES = "-includeProfileValues";
	public static final String RETAIN_ENTITY_REFERENCE = "-retainEntityReference";
	public static final String FRAGMENT = "-fragment";
	public static final String SELF_CONTAINED = "-selfContained";
	public static final String DATATYPE_REPRESENTATION_MAP = "-datatypeRepresentationMap";

	public static final String CODING_BYTEPACKED = "-bytePacked";
	public static final String CODING_PRE_COMPRESSION = "-preCompression";
	public static final String CODING_COMPRESSION = "-compression";

	public static final String BLOCK_SIZE = "-blockSize";
	public static final String VALUE_MAX_LENGTH = "-valueMaxLength";
	public static final String VALUE_PARTITION_CAPACITY = "-valuePartitionCapacity";

	public static final String NO_LOCAL_VALUE_PARTITIONS = "-noLocalValuePartitions";
	public static final String MAXIMUM_NUMBER_OF_BUILT_IN_PRODUCTIONS = "-maximumNumberOfBuiltInProductions";
	public static final String MAXIMUM_NUMBER_OF_BUILT_IN_ELEMENT_GRAMMARS = "-maximumNumberOfBuiltInElementGrammars";

	public static String DEFAULT_EXI_FILE_EXTENSION = ".exi";
	public static String DEFAULT_XML_FILE_EXTENSION = ".xml";

	protected boolean inputParametersOK;
	protected CmdOption cmdOption;
	protected EXIFactory exiFactory;
	protected String input;
	protected String output;

	public EXIficientCMD() {
	}

	private static void printHeader() {
		ps.println("#########################################################################");
		ps.println("###   EXIficient                                                     ###");
		ps.println("###   Command-Shell Options                                          ###");
		ps.println("#########################################################################");
	}

	private static void printHelp() {
		printHeader();

		ps.println();
		ps.println(" " + HELP
				+ "                               /* shows help */");
		ps.println();
		ps.println(" " + ENCODE);
		ps.println(" " + DECODE);
		ps.println();
		ps.println(" " + INPUT + " <input-file>");
		ps.println(" " + OUTPUT + " <output-file>");
		ps.println();
		ps.println(" " + SCHEMA + " <schema-input-file>");
		ps.println(" " + XSD_SCHEMA
				+ "                       /* XML schema datatypes only */");
		ps.println(" " + NO_SCHEMA + "                        /* default */");
		ps.println();
		ps.println(" " + OPTION_STRICT);
		ps.println(" " + PRESERVE_PREFIXES);
		ps.println(" " + PRESERVE_COMMENTS);
		ps.println(" " + PRESERVE_LEXICAL_VALUES);
		ps.println(" " + PRESERVE_PIS
				+ "                     /* processing instructions */");
		ps.println(" " + PRESERVE_DTDS
				+ "                    /* DTDs & entity references */");
		ps.println();
		ps.println(" " + CODING_BYTEPACKED);
		ps.println(" " + CODING_PRE_COMPRESSION);
		ps.println(" " + CODING_COMPRESSION);
		ps.println();
		ps.println(" " + BLOCK_SIZE + " <value>");
		ps.println(" " + VALUE_MAX_LENGTH + " <value>");
		ps.println(" " + VALUE_PARTITION_CAPACITY + " <value>");
		ps.println();
		ps.println(" " + NO_LOCAL_VALUE_PARTITIONS
				+ "          /* EXI Profile parameters */");
		ps.println(" " + MAXIMUM_NUMBER_OF_BUILT_IN_PRODUCTIONS + " <value>");
		ps.println(" " + MAXIMUM_NUMBER_OF_BUILT_IN_ELEMENT_GRAMMARS
				+ " <value>");
		ps.println();
		ps.println(" " + INCLUDE_OPTIONS);
		ps.println(" " + INCLUDE_COOKIE);
		ps.println(" " + INCLUDE_SCHEMA_ID);
		ps.println(" " + INCLUDE_SCHEMA_LOCATION);
		ps.println(" " + INCLUDE_INSIGNIFICANT_XSI_NIL);
		ps.println(" " + INCLUDE_PROFILE_VALUES);
		ps.println(" " + RETAIN_ENTITY_REFERENCE);
		ps.println(" " + FRAGMENT);
		ps.println(" " + SELF_CONTAINED + " <{urn:foo}elWithNS,elDefNS>");
		ps.println(" "
				+ DATATYPE_REPRESENTATION_MAP
				+ " <qnameType,qnameRepresentation,{http://www.w3.org/2001/XMLSchema}decimal,{http://www.w3.org/2009/exi}string>");

		ps.println();
		ps.println("# Examples");
		ps.println(" " + ENCODE + " " + SCHEMA + " notebook.xsd " + INPUT
				+ " notebook.xml");
		ps.println(" " + DECODE + " " + SCHEMA + " notebook.xsd " + INPUT
				+ " notebook.xml.exi " + OUTPUT + " notebookDec.xml");
	}

	protected static void printError(String msg) {
		ps.println("[ERROR] " + msg);
	}

	protected static void printWarning(String msg) {
		ps.println("[Warning] " + msg);
	}

	protected void parseArguments(String[] args) throws EXIException {
		// arguments that need to be set
		cmdOption = null;
		SchemaOption schemaOption = SchemaOption.noSchema; // default
		String schemaLocation = null;

		input = null;
		output = null;

		exiFactory = DefaultEXIFactory.newInstance();

		// warning flags
		boolean wIncludeOptions = false;
		boolean wIncludeSchemaId = false;
		boolean wIncludeProfileValues = false;
		boolean wStrict = false;
		boolean wPreserveComments = false;
		boolean wPreservePIs = false;
		boolean wPreservePrefixes = false;
		boolean wPreserveDTD = false;

		int indexArgument = 0;
		while (indexArgument < args.length) {
			String argument = args[indexArgument];

			// ### HELP ?
			if (HELP.equalsIgnoreCase(argument)) {
				printHelp();
				break;
			}
			// ### CODING_OPTIONS
			else if (ENCODE.equalsIgnoreCase(argument)) {
				cmdOption = CmdOption.encode;
			} else if (DECODE.equalsIgnoreCase(argument)) {
				cmdOption = CmdOption.decode;
			}
			// ### IO_OPTIONS
			else if (INPUT.equalsIgnoreCase(argument)) {
				assert ((indexArgument + 1) < args.length);
				indexArgument++;

				input = args[indexArgument];
			} else if (OUTPUT.equalsIgnoreCase(argument)) {
				assert ((indexArgument + 1) < args.length);
				indexArgument++;

				output = args[indexArgument];
			}
			// ### SCHEMA_OPTIONS
			else if (NO_SCHEMA.equalsIgnoreCase(argument)) {
				// no schema
				schemaOption = SchemaOption.noSchema;
			} else if (XSD_SCHEMA.equalsIgnoreCase(argument)) {
				// XML schema datatypes only
				schemaOption = SchemaOption.xsdSchema;
			} else if (SCHEMA.equalsIgnoreCase(argument)) {
				// schema -> addition schema-location necessary
				schemaOption = SchemaOption.schema;

				assert ((indexArgument + 1) < args.length);
				indexArgument++;

				schemaLocation = args[indexArgument];
			}
			// ### OPTIONS
			else if (OPTION_STRICT.equalsIgnoreCase(argument)) {
				wStrict = true;
				FidelityOptions fo = FidelityOptions.createStrict();
				exiFactory.setFidelityOptions(fo);
			} else if (BLOCK_SIZE.equalsIgnoreCase(argument)) {
				assert ((indexArgument + 1) < args.length);
				indexArgument++;
				int blockSize = Integer.parseInt(args[indexArgument]);
				exiFactory.setBlockSize(blockSize);
			} else if (VALUE_MAX_LENGTH.equalsIgnoreCase(argument)) {
				assert ((indexArgument + 1) < args.length);
				indexArgument++;
				int valueMaxLength = Integer.parseInt(args[indexArgument]);
				exiFactory.setValueMaxLength(valueMaxLength);
			} else if (VALUE_PARTITION_CAPACITY.equalsIgnoreCase(argument)) {
				assert ((indexArgument + 1) < args.length);
				indexArgument++;
				int valuePartitionCapacity = Integer
						.parseInt(args[indexArgument]);
				exiFactory.setValuePartitionCapacity(valuePartitionCapacity);
			} else if (MAXIMUM_NUMBER_OF_BUILT_IN_ELEMENT_GRAMMARS
					.equalsIgnoreCase(argument)) {
				assert ((indexArgument + 1) < args.length);
				indexArgument++;

				int maximumNumberOfEvolvingBuiltInElementGrammars = Integer
						.parseInt(args[indexArgument]);
				exiFactory
						.setMaximumNumberOfBuiltInElementGrammars(maximumNumberOfEvolvingBuiltInElementGrammars);
			} else if (MAXIMUM_NUMBER_OF_BUILT_IN_PRODUCTIONS
					.equalsIgnoreCase(argument)) {
				assert ((indexArgument + 1) < args.length);
				indexArgument++;

				int maximumNumberOfBuiltInProductions = Integer
						.parseInt(args[indexArgument]);
				exiFactory
						.setMaximumNumberOfBuiltInProductions(maximumNumberOfBuiltInProductions);
			}
			// Comments
			else if (PRESERVE_COMMENTS.equalsIgnoreCase(argument)) {
				FidelityOptions fo = exiFactory.getFidelityOptions();
				fo.setFidelity(FidelityOptions.FEATURE_COMMENT,
						wPreserveComments = true);
			}
			// LexicalValues
			else if (PRESERVE_LEXICAL_VALUES.equalsIgnoreCase(argument)) {
				FidelityOptions fo = exiFactory.getFidelityOptions();
				fo.setFidelity(FidelityOptions.FEATURE_LEXICAL_VALUE, true);
			}
			// Prefixes
			else if (PRESERVE_PREFIXES.equalsIgnoreCase(argument)) {
				FidelityOptions fo = exiFactory.getFidelityOptions();
				fo.setFidelity(FidelityOptions.FEATURE_PREFIX,
						wPreservePrefixes = true);
			}
			// PIs
			else if (PRESERVE_PIS.equalsIgnoreCase(argument)) {
				FidelityOptions fo = exiFactory.getFidelityOptions();
				fo.setFidelity(FidelityOptions.FEATURE_PI, wPreservePIs = true);
			}
			// DTS
			else if (PRESERVE_DTDS.equalsIgnoreCase(argument)) {
				FidelityOptions fo = exiFactory.getFidelityOptions();
				fo.setFidelity(FidelityOptions.FEATURE_DTD, wPreserveDTD = true);
			}
			// ### BYTE_ALIGNED
			else if (CODING_BYTEPACKED.equalsIgnoreCase(argument)) {
				exiFactory.setCodingMode(CodingMode.BYTE_PACKED);
			}
			// ### PRE_COMPRESSION
			else if (CODING_PRE_COMPRESSION.equalsIgnoreCase(argument)) {
				exiFactory.setCodingMode(CodingMode.PRE_COMPRESSION);
			}
			// ### COMPRESSION
			else if (CODING_COMPRESSION.equalsIgnoreCase(argument)) {
				exiFactory.setCodingMode(CodingMode.COMPRESSION);
			}
			// ### NO_LOCAL_VALUE_PARTITIONS
			else if (NO_LOCAL_VALUE_PARTITIONS.equalsIgnoreCase(argument)) {
				exiFactory.setLocalValuePartitions(false);
			}
			// ### Include EXI Options
			else if (INCLUDE_OPTIONS.equalsIgnoreCase(argument)) {
				wIncludeOptions = true;
				exiFactory.getEncodingOptions().setOption(
						EncodingOptions.INCLUDE_OPTIONS);
			}
			// ### Include EXI Cookie in Options
			else if (INCLUDE_COOKIE.equalsIgnoreCase(argument)) {
				exiFactory.getEncodingOptions().setOption(
						EncodingOptions.INCLUDE_COOKIE);
			}
			// ### Include SchemaId in Options
			else if (INCLUDE_SCHEMA_ID.equalsIgnoreCase(argument)) {
				wIncludeSchemaId = true;
				exiFactory.getEncodingOptions().setOption(
						EncodingOptions.INCLUDE_SCHEMA_ID);
			}
			// ### Include SchemaLocation
			else if (INCLUDE_SCHEMA_LOCATION.equalsIgnoreCase(argument)) {
				exiFactory.getEncodingOptions().setOption(
						EncodingOptions.INCLUDE_XSI_SCHEMALOCATION);
			}
			// ### Include SchemaLocation
			else if (INCLUDE_INSIGNIFICANT_XSI_NIL.equalsIgnoreCase(argument)) {
				exiFactory.getEncodingOptions().setOption(
						EncodingOptions.INCLUDE_INSIGNIFICANT_XSI_NIL);
			}
			// ### Include Profile Values
			else if (INCLUDE_PROFILE_VALUES.equalsIgnoreCase(argument)) {
				wIncludeProfileValues = true;
				exiFactory.getEncodingOptions().setOption(
						EncodingOptions.INCLUDE_PROFILE_VALUES);
			}
			// ### Retain EntityReference
			else if (RETAIN_ENTITY_REFERENCE.equalsIgnoreCase(argument)) {
				exiFactory.getEncodingOptions().setOption(
						EncodingOptions.RETAIN_ENTITY_REFERENCE);
			}
			// ### FRAGMENT
			else if (FRAGMENT.equalsIgnoreCase(argument)) {
				exiFactory.setFragment(true);
			}
			// ### SELF_CONTAINED
			else if (SELF_CONTAINED.equalsIgnoreCase(argument)) {
				assert ((indexArgument + 1) < args.length);
				indexArgument++;

				String qnames = args[indexArgument];
				StringTokenizer st = new StringTokenizer(qnames, ",");
				QName[] scElements = new QName[st.countTokens()];
				int i = 0;
				while (st.hasMoreTokens()) {
					scElements[i++] = QName.valueOf(st.nextToken());
				}
				exiFactory.setSelfContainedElements(scElements);
				exiFactory.getFidelityOptions().setFidelity(
						FidelityOptions.FEATURE_SC, true);
			}
			// ### DATATYPE_REPRESENTATION_MAP
			else if (DATATYPE_REPRESENTATION_MAP.equalsIgnoreCase(argument)) {
				assert ((indexArgument + 1) < args.length);
				indexArgument++;

				String dtrs = args[indexArgument];
				StringTokenizer st = new StringTokenizer(dtrs, ",");

				assert (st.countTokens() % 2 == 0);
				QName[] dtrMapTypes = new QName[st.countTokens() / 2];
				QName[] dtrMapRepresentations = new QName[st.countTokens() / 2];

				int i = 0;
				while (st.hasMoreTokens()) {
					dtrMapTypes[i] = QName.valueOf(st.nextToken());
					dtrMapRepresentations[i] = QName.valueOf(st.nextToken());
					i++;
				}
				exiFactory.setDatatypeRepresentationMap(dtrMapTypes,
						dtrMapRepresentations);
			} else {
				System.out.println("Unknown option '" + argument + "'");
			}

			indexArgument++;
		}

		// inform user about warnings/side-effects
		// e.g., includeOptions & includeProfileValues ignored because
		// includeOptions not set
		if (wIncludeSchemaId && !wIncludeOptions) {
			printWarning(INCLUDE_SCHEMA_ID + " ignored because "
					+ INCLUDE_OPTIONS + " not set");
		}
		if (wIncludeProfileValues && !wIncludeOptions) {
			printWarning(INCLUDE_PROFILE_VALUES + " ignored because "
					+ INCLUDE_OPTIONS + " not set");
		}
		// e.g., preserveXX ignored given that strict is set
		if (exiFactory.getFidelityOptions().isStrict()) {
			if (wPreserveComments) {
				printWarning(PRESERVE_COMMENTS + " ignored because "
						+ OPTION_STRICT + " is set");
			}
			if (wPreservePIs) {
				printWarning(PRESERVE_PIS + " ignored because " + OPTION_STRICT
						+ " is set");
			}
			if (wPreserveDTD) {
				printWarning(PRESERVE_DTDS + " ignored because "
						+ OPTION_STRICT + " is set");
			}
			if (wPreservePrefixes) {
				printWarning(PRESERVE_PREFIXES + " ignored because "
						+ OPTION_STRICT + " is set");
			}
		} else {
			if (wStrict) {
				printWarning(OPTION_STRICT
						+ " ignored because a preserveOption is set");
			}
		}
		// TODO conflicting coding modes
		// TODO SC in strict mode

		// check input
		inputParametersOK = true;

		if (cmdOption == null) {
			inputParametersOK = false;
			printError("Missing coding option such as " + ENCODE + " and "
					+ DECODE);
		}

		if (input == null) {
			inputParametersOK = false;
			printError("Missing option -i");
		} else if (!(new File(input)).exists()) {
			inputParametersOK = false;
			printError("Not existing input parameter -i, \"" + input + "\"");
		} else {
			// ok
		}

		if (input != null && output == null) {
			// default output
			if (CmdOption.encode == cmdOption) {
				output = input + DEFAULT_EXI_FILE_EXTENSION;
			} else {
				output = input + DEFAULT_XML_FILE_EXTENSION;
			}
		}

		File fOutput = null;
		if (output == null) {
			inputParametersOK = false;
			printError("Missing output specification!");
		} else {
			fOutput = new File(output);

			if (fOutput.isDirectory()) {
				inputParametersOK = false;
				printError("Outputfile '" + output
						+ "' is unexpectedly a directory");
			} else {
				if (fOutput.exists()) {
					if (!fOutput.delete()) {
						inputParametersOK = false;
						printError("Existing outputfile '" + output
								+ "' could not be deleted.");
					}
				} else {
					// does not exits
					assert (!fOutput.exists());
					File parentDir = fOutput.getParentFile();
					if (parentDir != null && !parentDir.exists()) {
						if (!parentDir.mkdirs()) {
							inputParametersOK = false;
							printError("Output directories for file '" + output
									+ "' could not be created.");
						}
					}
				}
			}
		}

		if (inputParametersOK) {
			// schema available ?
			if (SchemaOption.noSchema == schemaOption) {
				// default: schema-less mode
				// exiFactory.setGrammar ( "" );
			} else if (SchemaOption.xsdSchema == schemaOption) {
				GrammarFactory gf = GrammarFactory.newInstance();
				exiFactory.setGrammars(gf.createXSDTypesOnlyGrammars());
			} else {
				GrammarFactory gf = GrammarFactory.newInstance();
				exiFactory.setGrammars(gf.createGrammars(schemaLocation));
			}
		}
	}

	protected void process() throws EXIException, TransformerException,
			IOException, SAXException {
		if (inputParametersOK) {
			// start coding
			switch (cmdOption) {
			case decode:
				decode(input, exiFactory, output);
				break;
			case encode:
				encode(input, exiFactory, output);
				break;
			default:
				printError("Unexptected command option " + cmdOption);
				break;
			}

		} else {
			// something not right
			// info messages (see above)
			printError("Input parameters were incorrect");
		}
	}

	public static void main(String[] args) {

		if (args == null || args.length == 0) {
			// show help
			printHelp();
		} else {
			EXIficientCMD cmd = new EXIficientCMD();
			try {
				cmd.parseArguments(args);
				cmd.process();
			} catch (Exception e) {
				printError(e.getLocalizedMessage() + e.getClass());
			}
		}
	}

	protected void decode(String input, EXIFactory exiFactory, String output)
			throws EXIException, TransformerException, IOException {
		OutputStream xmlOutput = new FileOutputStream(output);

		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		SAXSource exiSource = new SAXSource(new InputSource(
				new FileInputStream(input)));
		exiSource.setXMLReader(new SAXFactory(exiFactory).createEXIReader());

		if (exiFactory.isFragment()) {
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
					"yes");
		}

		transformer.transform(exiSource, new StreamResult(xmlOutput));

		xmlOutput.flush();
		xmlOutput.close();
	}

	protected XMLReader getXMLReader() throws SAXException {
		// create xml reader
		XMLReader xmlReader;

		// xmlReader = XMLReaderFactory
		// .createXMLReader("org.apache.xerces.parsers.SAXParser");
		xmlReader = XMLReaderFactory.createXMLReader();

		// set XMLReader features
		xmlReader.setFeature("http://xml.org/sax/features/namespaces", true);
		// do not report namespace declarations as attributes
		xmlReader.setFeature("http://xml.org/sax/features/namespace-prefixes",
				false);
		// avoid validation
		xmlReader.setFeature("http://xml.org/sax/features/validation", false);
		// DTD
		xmlReader.setFeature("http://xml.org/sax/features/resolve-dtd-uris",
				false);
		// *skip* resolving entities like DTDs
		xmlReader.setEntityResolver(new NoEntityResolver());

		return xmlReader;
	}

	protected void encode(String input, EXIFactory exiFactory, String output)
			throws SAXException, EXIException, IOException {
		OutputStream os = new FileOutputStream(output);

		XMLReader xmlReader = getXMLReader();

		EXIResult exiResult = new EXIResult(exiFactory);
		exiResult.setOutputStream(os);

		xmlReader.setContentHandler(exiResult.getHandler());

		// set LexicalHandler
		xmlReader.setProperty("http://xml.org/sax/properties/lexical-handler",
				exiResult.getLexicalHandler());
		// set DeclHandler
		xmlReader.setProperty(
				"http://xml.org/sax/properties/declaration-handler",
				exiResult.getLexicalHandler());
		// set DTD handler
		xmlReader.setDTDHandler((DTDHandler) exiResult.getHandler());

		InputSource is;
		if (exiFactory.isFragment()) {
			// surround fragment section with *root* element
			// (necessary for xml reader to avoid messages like "root element
			// must
			// be well-formed")
			is = new InputSource(
					FragmentUtilities
							.getSurroundingRootInputStream(new FileInputStream(
									input)));
			// skip root element when passing infoset to EXI encoder
			xmlReader = new SkipRootElementXMLReader(xmlReader);
		} else {
			is = new InputSource(input);
		}

		xmlReader.parse(is);

		os.flush();
		os.close();
	}

}
