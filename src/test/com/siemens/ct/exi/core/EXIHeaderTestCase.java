package com.siemens.ct.exi.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import com.siemens.ct.exi.CodingMode;
import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.EncodingOptions;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.Grammar;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;
import com.siemens.ct.exi.io.channel.BitDecoderChannel;
import com.siemens.ct.exi.io.channel.BitEncoderChannel;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.io.channel.EncoderChannel;

public class EXIHeaderTestCase extends TestCase {

	public void testDistinguishingBits1() throws EXIException {
		EXIHeaderEncoder headerEncoder = new EXIHeaderEncoder();
		
		EXIFactory ef = DefaultEXIFactory.newInstance();
		// CodingMode codingMode = CodingMode.BIT_PACKED;
		// HeaderOptions headerOptions = HeaderOptions.createDefault();
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BitEncoderChannel bec = new BitEncoderChannel(baos);
		
		headerEncoder.write(bec, ef);
		
		byte[] header = baos.toByteArray();
		assertTrue(header.length == (1));
		
		BitDecoderChannel bdc = new BitDecoderChannel(new ByteArrayInputStream(header));
		
		EXIHeaderDecoder headerDecoder = new EXIHeaderDecoder();
		headerDecoder.parse(bdc, DefaultEXIFactory.newInstance());
	}

	public void testDistinguishingBitsFailure1() throws EXIException, IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BitEncoderChannel bec = new BitEncoderChannel(baos);
		bec.encodeNBitUnsignedInteger(1, 1);
		bec.encodeNBitUnsignedInteger(1, 1); // should be 0
		bec.encode(21);
		byte[] header = baos.toByteArray();
		
		BitDecoderChannel bdc = new BitDecoderChannel(new ByteArrayInputStream(header));
		try {
			EXIHeaderDecoder headerDecoder = new EXIHeaderDecoder();
			headerDecoder.parse(bdc, DefaultEXIFactory.newInstance());
			fail("No Valid DistinguishingBits");
		} catch (Exception e) {
			// no Valid DistinguishingBits
		}
	}
	

	public void testCookie1() throws EXIException {
		EXIHeaderEncoder headerEncoder = new EXIHeaderEncoder();
		
		EXIFactory ef = DefaultEXIFactory.newInstance();
		ef.setCodingMode(CodingMode.BYTE_PACKED);
		ef.getEncodingOptions().setOption(EncodingOptions.INCLUDE_COOKIE);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BitEncoderChannel bec = new BitEncoderChannel(baos);
		headerEncoder.write(bec, ef);
		
		byte[] header = baos.toByteArray();
		assertTrue(header.length == (4+1));
		assertTrue(header[0] == '$');
		assertTrue(header[1] == 'E');
		assertTrue(header[2] == 'X');
		assertTrue(header[3] == 'I');
		
		BitDecoderChannel bdc = new BitDecoderChannel(new ByteArrayInputStream(header));
		
		EXIHeaderDecoder headerDecoder = new EXIHeaderDecoder();
		headerDecoder.parse(bdc, DefaultEXIFactory.newInstance());
	}
	
	public void testCookieFailure1() throws EXIException, IOException {
		// EXIHeaderEncoder headerEncoder = new EXIHeaderEncoder();
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BitEncoderChannel bec = new BitEncoderChannel(baos);
		bec.encode('$');
		bec.encode('B');
		bec.encode('L');
		bec.encode('A');
		byte[] header = baos.toByteArray();
		
		BitDecoderChannel bdc = new BitDecoderChannel(new ByteArrayInputStream(header));
		try {
			EXIHeaderDecoder headerDecoder = new EXIHeaderDecoder();
			headerDecoder.parse(bdc, DefaultEXIFactory.newInstance());
			fail("No Valid Cookie");
		} catch (Exception e) {
			// no valid cookie
		}
	}
	
	protected void _testOptions(EXIFactory test) throws EXIException, IOException {
		_testOptions(test, null);
	}

	protected void _testOptions(EXIFactory test, EXIFactory noOptionsFactory) throws EXIException, IOException {
		EXIHeaderEncoder headerEncoder = new EXIHeaderEncoder();
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		EncoderChannel encoderChannel = new BitEncoderChannel(baos);
		
		headerEncoder.writeEXIOptions(test, encoderChannel);
		encoderChannel.flush();
		
		DecoderChannel decoderChannel = new BitDecoderChannel(new ByteArrayInputStream(baos.toByteArray()));
		
		EXIHeaderDecoder headerDecoder = new EXIHeaderDecoder();
		if (noOptionsFactory == null) {
			noOptionsFactory = DefaultEXIFactory.newInstance();
		}
		
		EXIFactory decodedTest = headerDecoder.readEXIOptions(decoderChannel, noOptionsFactory);
		
		assertTrue(test.equals(decodedTest));
	}
	
	public void testEXIOptions1() throws EXIException, IOException {
		EXIFactory ef = DefaultEXIFactory.newInstance();
		
		_testOptions(ef);
	}
	
	public void testEXIOptions2() throws EXIException, IOException {
		EXIFactory ef = DefaultEXIFactory.newInstance();
		ef.setCodingMode(CodingMode.BYTE_PACKED);
		
		_testOptions(ef);
	}
	
	public void testEXIOptions3() throws EXIException, IOException {
		EXIFactory ef = DefaultEXIFactory.newInstance();
		ef.setFidelityOptions(FidelityOptions.createAll());
		ef.setFragment(true);
		
		_testOptions(ef);
	}
	
	public void testEXIOptions4() throws EXIException, IOException {
		EXIFactory ef = DefaultEXIFactory.newInstance();
		ef.setFidelityOptions(FidelityOptions.createAll());
		QName[] dtrMapTypes = new QName[2];
		dtrMapTypes[0] = new QName("", "bla");
		dtrMapTypes[1] = new QName("ccc", "xhc");
		QName[] dtrMapRepresentations = new QName[2];
		dtrMapRepresentations[0] = new QName(Constants.W3C_EXI_NS_URI, "string");
		dtrMapRepresentations[1] = new QName(Constants.W3C_EXI_NS_URI, "decimal");
		ef.setDatatypeRepresentationMap(dtrMapTypes, dtrMapRepresentations);
		
		_testOptions(ef);
	}
	
	public void testEXIOptions5() throws EXIException, IOException {
		EXIFactory ef = DefaultEXIFactory.newInstance();
		ef.setFidelityOptions(FidelityOptions.createAll());
		ef.setCodingMode(CodingMode.COMPRESSION);
		ef.setBlockSize(200);
		ef.setValueMaxLength(12);
		ef.setValuePartitionCapacity(66);
		QName[] dtrMapTypes = new QName[1];
		dtrMapTypes[0] = new QName("", "bla");
		QName[] dtrMapRepresentations = new QName[1];
		dtrMapRepresentations[0] = new QName(Constants.W3C_EXI_NS_URI, "string");
		ef.setDatatypeRepresentationMap(dtrMapTypes, dtrMapRepresentations);
		
		_testOptions(ef);
	}
	
	public void testEXIOptions6() throws EXIException, IOException {
		EXIFactory ef = DefaultEXIFactory.newInstance();
		EncodingOptions ho = ef.getEncodingOptions();
		ho.setOption(EncodingOptions.INCLUDE_SCHEMA_ID); // schema-less

		_testOptions(ef);
	}
	
	public void testEXIOptions7() throws EXIException, IOException {
		EXIFactory ef = DefaultEXIFactory.newInstance();
		Grammar g = GrammarFactory.newInstance().createXSDTypesOnlyGrammar();
		ef.setGrammar(g);
		EncodingOptions ho = ef.getEncodingOptions();
		ho.setOption(EncodingOptions.INCLUDE_SCHEMA_ID); // built-in

		_testOptions(ef);
	}

	public void testEXIOptions8() throws EXIException, IOException {
		EXIFactory ef = DefaultEXIFactory.newInstance();
		String xsdLocation = "data/EXIOptionsHeader/EXIOptionsHeader.xsd";
		Grammar g = GrammarFactory.newInstance().createGrammar(xsdLocation);
		ef.setGrammar(g);
		EncodingOptions ho = ef.getEncodingOptions();
		ho.setOption(EncodingOptions.INCLUDE_SCHEMA_ID); // schema-informed

		_testOptions(ef);
	}
	
	public void testEXIOptions9() throws EXIException, IOException {		
		EXIFactory ef = DefaultEXIFactory.newInstance();
		
		// STRICT as noOptionsFactory
		EXIFactory efStrict = DefaultEXIFactory.newInstance();
		efStrict.setFidelityOptions(FidelityOptions.createStrict());

		_testOptions(ef, efStrict);
	}
	
	
}
