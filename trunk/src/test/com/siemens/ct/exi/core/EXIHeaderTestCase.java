package com.siemens.ct.exi.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import com.siemens.ct.exi.CodingMode;
import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.EncodingOptions;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.SchemaIdResolver;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammars.Grammars;
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
		Grammars g = GrammarFactory.newInstance().createXSDTypesOnlyGrammars();
		ef.setGrammars(g);
		EncodingOptions ho = ef.getEncodingOptions();
		ho.setOption(EncodingOptions.INCLUDE_SCHEMA_ID); // built-in

		_testOptions(ef);
	}

	public void testEXIOptions8() throws EXIException, IOException {
		EXIFactory ef = DefaultEXIFactory.newInstance();
		String xsdLocation = "data/EXIOptionsHeader/EXIOptionsHeader.xsd";
		Grammars g = GrammarFactory.newInstance().createGrammars(xsdLocation);
		ef.setGrammars(g);
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
	
	public void testEXIOptions10() throws EXIException, IOException {		
		// PI and Comments
		EXIFactory ef = DefaultEXIFactory.newInstance();
		FidelityOptions fo = ef.getFidelityOptions();
		fo.setFidelity(FidelityOptions.FEATURE_COMMENT, true);
		fo.setFidelity(FidelityOptions.FEATURE_PI, true);
		
		_testOptions(ef, null);
	}
	
	public void testEXIOptions11() throws EXIException, IOException {
		// Profile
		EXIFactory ef = DefaultEXIFactory.newInstance();
		ef.setLocalValuePartitions(false);
		
		EncodingOptions ho = ef.getEncodingOptions();
		ho.setOption(EncodingOptions.INCLUDE_PROFILE_VALUES);

		_testOptions(ef);
	}
	
	public void testEXIOptions12() throws EXIException, IOException {
		// Profile
		EXIFactory ef = DefaultEXIFactory.newInstance();
		// ef.setLocalValuePartitions(false);
		ef.setMaximumNumberOfBuiltInProductions(0);
		
		// EncodingOptions ho = ef.getEncodingOptions();
		// ho.setOption(EncodingOptions.INCLUDE_PROFILE_VALUES);

		_testOptions(ef);
	}
	
	
	String schemaBla = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
		+ " <xs:element name='root' type='xs:string' nillable='true' >"
		+ " </xs:element>" + "</xs:schema>";
	
	public void testEXIOptionsBugID3425036() throws EXIException, IOException {
		String schemaId = "bla";
		
		GrammarFactory gf = GrammarFactory.newInstance();		
		
		InputStream is = new ByteArrayInputStream(schemaBla.getBytes());
		Grammars g = gf.createGrammars(is);
		g.setSchemaId(schemaId);
		EXIFactory ef = DefaultEXIFactory.newInstance();
		ef.setGrammars(g);
		
		EncodingOptions eo = ef.getEncodingOptions();
		eo.setOption(EncodingOptions.INCLUDE_OPTIONS);
		eo.setOption(EncodingOptions.INCLUDE_SCHEMA_ID);
		
		ef.setEncodingOptions(eo);
		
		// write header
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BitEncoderChannel bec = new BitEncoderChannel(baos);
		EXIHeaderEncoder he = new EXIHeaderEncoder();
		he.write(bec, ef);
		bec.flush();

		// Note: No exception should be thrown due to schemaId
		
		// decoder header
		BitDecoderChannel bdc = new BitDecoderChannel(new ByteArrayInputStream(baos.toByteArray()));
		EXIHeaderDecoder hd = new EXIHeaderDecoder();
		EXIFactory fDec = DefaultEXIFactory.newInstance();
		fDec.setSchemaIdResolver(new BlaSchemaIdResolver());
		hd.parse(bdc, fDec);
		
//		assertTrue(hd.isSchemaIdSet());
//		assertTrue(schemaId.equals(hd.getSchemaId()));
		
	}
	
	class BlaSchemaIdResolver implements SchemaIdResolver {

		public Grammars resolveSchemaId(String schemaId) throws EXIException {
			if ("bla".equals(schemaId)) {
				InputStream is = new ByteArrayInputStream(schemaBla.getBytes());
				return GrammarFactory.newInstance().createGrammars(is);
			} else {
				throw new RuntimeException("Unspoorted schemaId: " + schemaId);
			}
		}
		
	}
	
	
}
