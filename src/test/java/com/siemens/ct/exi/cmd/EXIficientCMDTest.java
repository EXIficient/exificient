/*
 * Copyright (c) 2007-2016 Siemens AG
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

package com.siemens.ct.exi.cmd;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import javax.xml.namespace.QName;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.siemens.ct.exi.CodingMode;
import com.siemens.ct.exi.EncodingOptions;
import com.siemens.ct.exi.FidelityOptions;

public class EXIficientCMDTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	final String xsdNotebook = "./data/W3C/PrimerNotebook/notebook.xsd";
	final String xmlNotebook = "./data/W3C/PrimerNotebook/notebook.xml";
	
	final String xmlFragment2 = "./data/fragment/fragment2.xml.frag";
	

	@Test
	public void test1() {
		String[] args = { EXIficientCMD.ENCODE, EXIficientCMD.SCHEMA,
				xsdNotebook + "X", EXIficientCMD.INPUT, xmlNotebook };
		try {
			EXIficientCMD cmd = new EXIficientCMD();
			cmd.parseArguments(args);
			cmd.process();

			fail("XSD not available");
		} catch (Exception e) {
			// Failure OK, no valid XSD
		}
	}

	@Test
	public void test2() throws Exception {
		String[] args = { EXIficientCMD.ENCODE, EXIficientCMD.SCHEMA,
				xsdNotebook, EXIficientCMD.INPUT, xmlNotebook };
		EXIficientCMD cmd = new EXIficientCMD();
		cmd.parseArguments(args);
		cmd.process();

		// File should be there
		File f = new File(xmlNotebook
				+ EXIficientCMD.DEFAULT_EXI_FILE_EXTENSION);
		if (f.exists()) {
			// ok --> delete
			f.delete();
		} else {
			fail("File " + f + " not created");
		}
	}

	@Test
	public void test3() throws Exception {
		int blockSize = 20;
		int valueMaxLenght = 10;
		int valuePartitionCapacity = 13;
		String[] args = { EXIficientCMD.ENCODE, EXIficientCMD.INPUT,
				xmlNotebook, EXIficientCMD.BLOCK_SIZE, blockSize + "",
				EXIficientCMD.VALUE_MAX_LENGTH, valueMaxLenght + "",
				EXIficientCMD.VALUE_PARTITION_CAPACITY,
				valuePartitionCapacity + "" };
		EXIficientCMD cmd = new EXIficientCMD();
		cmd.parseArguments(args);
		cmd.process();

		assertTrue(cmd.exiFactory.getCodingMode() == CodingMode.BIT_PACKED);
		assertFalse(cmd.exiFactory.getFidelityOptions().isStrict());

		assertTrue(cmd.exiFactory.getBlockSize() == blockSize);
		assertTrue(cmd.exiFactory.getValueMaxLength() == valueMaxLenght);
		assertTrue(cmd.exiFactory.getValuePartitionCapacity() == valuePartitionCapacity);

		// File should be there
		File f = new File(xmlNotebook
				+ EXIficientCMD.DEFAULT_EXI_FILE_EXTENSION);
		if (f.exists()) {
			// ok --> delete
			f.delete();
		} else {
			fail("File " + f + " not created");
		}
	}

	@Test
	public void testProfile1() throws Exception {
		int builtInProductions = 9;
		int builtInElementGrammars = 15;
		String[] args = { EXIficientCMD.ENCODE, EXIficientCMD.XSD_SCHEMA,
				EXIficientCMD.INPUT, xmlNotebook,
				EXIficientCMD.NO_LOCAL_VALUE_PARTITIONS,
				EXIficientCMD.MAXIMUM_NUMBER_OF_BUILT_IN_PRODUCTIONS,
				builtInProductions + "",
				EXIficientCMD.MAXIMUM_NUMBER_OF_BUILT_IN_ELEMENT_GRAMMARS,
				builtInElementGrammars + "" };
		EXIficientCMD cmd = new EXIficientCMD();
		cmd.parseArguments(args);
		cmd.process();

		assertTrue(cmd.exiFactory.getCodingMode() == CodingMode.BIT_PACKED);
		assertTrue(cmd.exiFactory.getGrammars().isSchemaInformed()); // xsd
																		// datatypes

		assertTrue(cmd.exiFactory.isLocalValuePartitions() == false);
		assertTrue(cmd.exiFactory.getMaximumNumberOfBuiltInProductions() == builtInProductions);
		assertTrue(cmd.exiFactory.getMaximumNumberOfBuiltInElementGrammars() == builtInElementGrammars);

		// File should be there
		File f = new File(xmlNotebook
				+ EXIficientCMD.DEFAULT_EXI_FILE_EXTENSION);
		if (f.exists()) {
			// ok --> delete
			f.delete();
		} else {
			fail("File " + f + " not created");
		}
	}

	@Test
	public void testBytePacked1() throws Exception {
		String[] args = { EXIficientCMD.ENCODE, EXIficientCMD.INPUT,
				xmlNotebook, EXIficientCMD.CODING_BYTEPACKED };
		EXIficientCMD cmd = new EXIficientCMD();
		cmd.parseArguments(args);
		cmd.process();

		assertTrue(cmd.exiFactory.getCodingMode() == CodingMode.BYTE_PACKED);

		// File should be there
		File f = new File(xmlNotebook
				+ EXIficientCMD.DEFAULT_EXI_FILE_EXTENSION);
		if (f.exists()) {
			// ok --> delete
			f.delete();
		} else {
			fail("File " + f + " not created");
		}
	}

	@Test
	public void testBytePreCompression1() throws Exception {
		String[] args = { EXIficientCMD.ENCODE, EXIficientCMD.INPUT,
				xmlNotebook, EXIficientCMD.CODING_PRE_COMPRESSION };
		EXIficientCMD cmd = new EXIficientCMD();
		cmd.parseArguments(args);
		cmd.process();

		assertTrue(cmd.exiFactory.getCodingMode() == CodingMode.PRE_COMPRESSION);

		// File should be there
		File f = new File(xmlNotebook
				+ EXIficientCMD.DEFAULT_EXI_FILE_EXTENSION);
		if (f.exists()) {
			// ok --> delete
			f.delete();
		} else {
			fail("File " + f + " not created");
		}
	}

	@Test
	public void testByteCompression1() throws Exception {
		String[] args = { EXIficientCMD.ENCODE, EXIficientCMD.INPUT,
				xmlNotebook, EXIficientCMD.CODING_COMPRESSION };
		EXIficientCMD cmd = new EXIficientCMD();
		cmd.parseArguments(args);
		cmd.process();

		assertTrue(cmd.exiFactory.getCodingMode() == CodingMode.COMPRESSION);

		// File should be there
		File f = new File(xmlNotebook
				+ EXIficientCMD.DEFAULT_EXI_FILE_EXTENSION);
		if (f.exists()) {
			// ok --> delete
			f.delete();
		} else {
			fail("File " + f + " not created");
		}
	}

	@Test
	public void testOptionsStrict1() throws Exception {
		String[] args = { EXIficientCMD.ENCODE, EXIficientCMD.INPUT,
				xmlNotebook, EXIficientCMD.OPTION_STRICT };
		EXIficientCMD cmd = new EXIficientCMD();
		cmd.parseArguments(args);
		cmd.process();

		assertTrue(cmd.exiFactory.getCodingMode() == CodingMode.BIT_PACKED);
		assertTrue(cmd.exiFactory.getFidelityOptions().isStrict());

		// File should be there
		File f = new File(xmlNotebook
				+ EXIficientCMD.DEFAULT_EXI_FILE_EXTENSION);
		if (f.exists()) {
			// ok --> delete
			f.delete();
		} else {
			fail("File " + f + " not created");
		}
	}

	@Test
	public void testOptionsPreserve1() throws Exception {
		String[] args = { EXIficientCMD.ENCODE, EXIficientCMD.INPUT,
				xmlNotebook, EXIficientCMD.PRESERVE_COMMENTS,
				EXIficientCMD.PRESERVE_LEXICAL_VALUES,
				EXIficientCMD.PRESERVE_PREFIXES, EXIficientCMD.PRESERVE_PIS,
				EXIficientCMD.PRESERVE_DTDS };
		EXIficientCMD cmd = new EXIficientCMD();
		cmd.parseArguments(args);
		cmd.process();

		assertTrue(cmd.exiFactory.getCodingMode() == CodingMode.BIT_PACKED);
		assertTrue(cmd.exiFactory.getFidelityOptions().isFidelityEnabled(FidelityOptions.FEATURE_COMMENT));
		assertTrue(cmd.exiFactory.getFidelityOptions().isFidelityEnabled(FidelityOptions.FEATURE_LEXICAL_VALUE));
		assertTrue(cmd.exiFactory.getFidelityOptions().isFidelityEnabled(FidelityOptions.FEATURE_PREFIX));
		assertTrue(cmd.exiFactory.getFidelityOptions().isFidelityEnabled(FidelityOptions.FEATURE_PI));
		assertTrue(cmd.exiFactory.getFidelityOptions().isFidelityEnabled(FidelityOptions.FEATURE_DTD));

		// File should be there
		File f = new File(xmlNotebook
				+ EXIficientCMD.DEFAULT_EXI_FILE_EXTENSION);
		if (f.exists()) {
			// ok --> delete
			f.delete();
		} else {
			fail("File " + f + " not created");
		}
	}
	
	@Test
	public void testEncodingOptions1() throws Exception {
		String[] args = { EXIficientCMD.ENCODE, EXIficientCMD.INPUT,
				xmlNotebook, EXIficientCMD.INCLUDE_OPTIONS,
				EXIficientCMD.INCLUDE_COOKIE ,
				EXIficientCMD.INCLUDE_SCHEMA_ID , EXIficientCMD.INCLUDE_SCHEMA_LOCATION ,
				EXIficientCMD.INCLUDE_INSIGNIFICANT_XSI_NIL,
				EXIficientCMD.INCLUDE_PROFILE_VALUES ,
				EXIficientCMD.RETAIN_ENTITY_REFERENCE};
		EXIficientCMD cmd = new EXIficientCMD();
		cmd.parseArguments(args);
		cmd.process();

		assertTrue(cmd.exiFactory.getCodingMode() == CodingMode.BIT_PACKED);
		assertTrue(cmd.exiFactory.getEncodingOptions().isOptionEnabled(EncodingOptions.INCLUDE_COOKIE));
		assertTrue(cmd.exiFactory.getEncodingOptions().isOptionEnabled(EncodingOptions.INCLUDE_SCHEMA_ID));
		assertTrue(cmd.exiFactory.getEncodingOptions().isOptionEnabled(EncodingOptions.INCLUDE_XSI_SCHEMALOCATION));
		assertTrue(cmd.exiFactory.getEncodingOptions().isOptionEnabled(EncodingOptions.INCLUDE_INSIGNIFICANT_XSI_NIL));
		assertTrue(cmd.exiFactory.getEncodingOptions().isOptionEnabled(EncodingOptions.INCLUDE_PROFILE_VALUES));
		assertTrue(cmd.exiFactory.getEncodingOptions().isOptionEnabled(EncodingOptions.RETAIN_ENTITY_REFERENCE));
		
		// File should be there
		File f = new File(xmlNotebook
				+ EXIficientCMD.DEFAULT_EXI_FILE_EXTENSION);
		if (f.exists()) {
			// ok --> delete
			f.delete();
		} else {
			fail("File " + f + " not created");
		}
	}
	
	
	@Test
	public void testCoding1() throws Exception {
		EXIficientCMD cmd = new EXIficientCMD();
		
		File tmp = File.createTempFile("xmlNotebook", "exi");
		String sOutput = tmp.getAbsolutePath();
		// String sOutput = xmlNotebook + ".exii";
		
		
		// encode
		String[] args1 = { EXIficientCMD.ENCODE, EXIficientCMD.INPUT,
				xmlNotebook, EXIficientCMD.OUTPUT, sOutput};
		cmd.parseArguments(args1);
		cmd.process();
		
		// decode
		String[] args2 = { EXIficientCMD.DECODE, EXIficientCMD.INPUT,
				sOutput};
		cmd.parseArguments(args2);
		cmd.process();
		
		// EXI File should be there
		File f = new File(sOutput);
		if (f.exists()) {
			// ok --> delete
			f.delete();
		} else {
			fail("File " + f + " not created");
		}
		
		// XML File should be there
		f = new File(sOutput
				+ EXIficientCMD.DEFAULT_XML_FILE_EXTENSION);
		if (f.exists()) {
			// ok --> delete
			f.delete();
		} else {
			fail("File " + f + " not created");
		}
	}
	
	@Test
	public void testFragment2() throws Exception {
		EXIficientCMD cmd = new EXIficientCMD();
		
		File tmp = File.createTempFile("xmlFragment2", "exi");
		String sOutput = tmp.getAbsolutePath();
		// String sOutput = xmlFragment2 + ".exii";
		
		// encode
		String[] args1 = { EXIficientCMD.ENCODE, EXIficientCMD.INPUT,
				xmlFragment2, EXIficientCMD.OUTPUT, sOutput, EXIficientCMD.FRAGMENT };
		cmd.parseArguments(args1);
		assertTrue(cmd.exiFactory.isFragment());
		cmd.process();
		
		// decode
		String[] args2 = { EXIficientCMD.DECODE, EXIficientCMD.INPUT,
				sOutput, EXIficientCMD.FRAGMENT };
		cmd.parseArguments(args2);
		assertTrue(cmd.exiFactory.isFragment());
		cmd.process();
		
		// EXI File should be there
		File f = new File(sOutput);
		if (f.exists()) {
			// ok --> delete
			f.delete();
		} else {
			fail("File " + f + " not created");
		}
		
		// XML File should be there
		f = new File(sOutput
				+ EXIficientCMD.DEFAULT_XML_FILE_EXTENSION);
		if (f.exists()) {
			// ok --> delete
			f.delete();
		} else {
			fail("File " + f + " not created");
		}
	}
	
	@Test
	public void testCodingSelfContained1() throws Exception {
		EXIficientCMD cmd = new EXIficientCMD();
		
		File tmp = File.createTempFile("xmlNotebook", "exi");
		String sOutput = tmp.getAbsolutePath();
		// String sOutput = xmlNotebook + ".exiii";
		String qnames = "el1,el2";
		
		
		// encode
		String[] args1 = { EXIficientCMD.ENCODE, EXIficientCMD.INPUT,
				xmlNotebook, EXIficientCMD.OUTPUT, sOutput, EXIficientCMD.SELF_CONTAINED, qnames};
		cmd.parseArguments(args1);
		assertTrue(cmd.exiFactory.isSelfContainedElement(new QName("el1")));
		assertTrue(cmd.exiFactory.isSelfContainedElement(new QName("el2")));
		cmd.process();
		
		// decode
		String[] args2 = { EXIficientCMD.DECODE, EXIficientCMD.INPUT,
				sOutput, EXIficientCMD.SELF_CONTAINED, qnames};
		cmd.parseArguments(args2);
		cmd.process();
		
		// EXI File should be there
		File f = new File(sOutput);
		if (f.exists()) {
			// ok --> delete
			f.delete();
		} else {
			fail("File " + f + " not created");
		}
		
		// XML File should be there
		f = new File(sOutput
				+ EXIficientCMD.DEFAULT_XML_FILE_EXTENSION);
		if (f.exists()) {
			// ok --> delete
			f.delete();
		} else {
			fail("File " + f + " not created");
		}
	}
	
	@Test
	public void testCodingDTR1() throws Exception {
		EXIficientCMD cmd = new EXIficientCMD();
		
		File tmp = File.createTempFile("xmlNotebook", "exi");
		String sOutput = tmp.getAbsolutePath(); //  xmlNotebook + ".exiiii";
		String typesRepr = "{http://www.w3.org/2001/XMLSchema}date,{http://www.w3.org/2009/exi}string,{http://www.w3.org/2001/XMLSchema}string,{http://www.w3.org/2009/exi}string";
		
		// encode
		String[] args1 = { EXIficientCMD.ENCODE, EXIficientCMD.INPUT,
				xmlNotebook, EXIficientCMD.OUTPUT, sOutput, EXIficientCMD.DATATYPE_REPRESENTATION_MAP, typesRepr};
		cmd.parseArguments(args1);
		assertTrue(cmd.exiFactory.getDatatypeRepresentationMapTypes().length == 2);
		assertTrue(cmd.exiFactory.getDatatypeRepresentationMapTypes()[0].getLocalPart().equals("date"));
		assertTrue(cmd.exiFactory.getDatatypeRepresentationMapTypes()[1].getLocalPart().equals("string"));
		assertTrue(cmd.exiFactory.getDatatypeRepresentationMapRepresentations().length == 2);
		assertTrue(cmd.exiFactory.getDatatypeRepresentationMapRepresentations()[0].getLocalPart().equals("string"));
		assertTrue(cmd.exiFactory.getDatatypeRepresentationMapRepresentations()[1].getLocalPart().equals("string"));
		
		cmd.process();
		
		// decode
		String[] args2 = { EXIficientCMD.DECODE, EXIficientCMD.INPUT,
				sOutput, EXIficientCMD.DATATYPE_REPRESENTATION_MAP, typesRepr};
		cmd.parseArguments(args2);
		cmd.process();
		
		// EXI File should be there
		File f = new File(sOutput);
		if (f.exists()) {
			// ok --> delete
			f.delete();
		} else {
			fail("File " + f + " not created");
		}
		
		// XML File should be there
		f = new File(sOutput
				+ EXIficientCMD.DEFAULT_XML_FILE_EXTENSION);
		if (f.exists()) {
			// ok --> delete
			f.delete();
		} else {
			fail("File " + f + " not created");
		}
	}

}
