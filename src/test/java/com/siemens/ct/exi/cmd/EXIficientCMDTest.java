package com.siemens.ct.exi.cmd;

import static org.junit.Assert.*;

import java.io.File;

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
		
		String sOutput = xmlNotebook + ".exii";
		
		
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

}
