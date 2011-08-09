package com.siemens.ct.exi;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

public class TestSAXDecoderN extends TestSAXDecoder {

	public TestSAXDecoderN() {
		super();
	}
	
	protected void test(String exiLocation, String decodedXMLLocation, EXIFactory ef ) throws Exception {

		// create test-decoder
		TestSAXDecoderN testDecoderN = new TestSAXDecoderN();
		
//		ef.setEXIBodyEncoder("com.siemens.ct.exi.gen.EXIBodyEncoderGen");
//		ef.setEXIBodyDecoder("com.siemens.ct.exi.gen.EXIBodyDecoderGen");

		// exi document
		InputStream exiDocument = new BufferedInputStream(new FileInputStream(exiLocation));

		// decoded xml output
		OutputStream xmlOutput = new FileOutputStream(decodedXMLLocation);

		long startTime = System.currentTimeMillis();
		
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
		"yes");

		// decode EXI to XML
		for (int i = 0; i < TestSAXEncoderN.N_RUNS; i++) {
			testDecoderN.decodeTo(ef, exiDocument, xmlOutput, transformer);
		}

		System.out.println("[DEC-SAX] "
				+ QuickTestConfiguration.getExiLocation() + " --> "
				+ decodedXMLLocation);
		
		long duration = System.currentTimeMillis() - startTime;
		System.out.println("Runtime: " + duration + " msecs for " + TestSAXEncoderN.N_RUNS
				+ " runs.");
	}
	
	public static void main(String[] args) throws Exception {
		String exiLocation = QuickTestConfiguration.getExiLocation() + "_"
				+ TestSAXEncoderN.N_RUNS;
		String decodedXMLLocation = exiLocation + ".xml";
		
		// create test-decoder
		TestSAXDecoderN testDecoderN = new TestSAXDecoderN();
		
		// get factory
		EXIFactory ef = testDecoderN.getQuickTestEXIactory();
		
		testDecoderN.test(exiLocation, decodedXMLLocation, ef);
	}

}
