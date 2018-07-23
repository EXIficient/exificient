package com.siemens.ct.exi.main.api.sax;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.siemens.ct.exi.core.CodingMode;
import com.siemens.ct.exi.core.EXIFactory;
import com.siemens.ct.exi.core.EncodingOptions;
import com.siemens.ct.exi.core.FidelityOptions;
import com.siemens.ct.exi.core.exceptions.UnsupportedOption;
import com.siemens.ct.exi.core.helpers.DefaultEXIFactory;

public class TransformerEncodeTest {
	private static final String XML = "<rpc id=\"a\" a=\"64\" xmlnx=\"a:b:c:d\" xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"101\">\n"
			+ "    <get-config>\n"
			+ "        <source>\n"
			+ "            <running/>\n"
			+ "        </source>\n"
			+ "    </get-config>\n" + "</rpc>";

	@BeforeClass
	public static void suiteSetUp() {
		XMLUnit.setIgnoreWhitespace(true);
	}

	@Test
	public void testEncode() throws Exception {
		final StringReader reader = new StringReader(XML);
		final EXIFactory factory = getFactory(true);
		final byte[] bytes = encode(factory, reader).toByteArray();
		final Document decode = decode(factory, new InputSource(
				new ByteArrayInputStream(bytes)));
		final Diff diff = XMLUnit.compareXML(XMLUnit.buildControlDocument(XML),
				decode);
		Assert.assertTrue(diff.toString(), diff.similar());
	}

	@Test
	public void testEncodeTransformer() throws Exception {
		final StringReader reader = new StringReader(XML);
		final EXIFactory factory = getFactory(true);
		final byte[] bytes = transformerEncode(factory, new InputSource(reader))
				.toByteArray();
		final Document decode = decode(factory, new InputSource(
				new ByteArrayInputStream(bytes)));
		final Diff diff = XMLUnit.compareXML(XMLUnit.buildControlDocument(XML),
				decode);
		Assert.assertTrue(diff.toString(), diff.similar());
	}

	private static EXIFactory getFactory(final boolean preservePrefixes)
			throws UnsupportedOption {
		final EXIFactory factory = DefaultEXIFactory.newInstance();
		factory.setCodingMode(CodingMode.BYTE_PACKED);
		final FidelityOptions fidelity = FidelityOptions.createDefault();
		fidelity.setFidelity(FidelityOptions.FEATURE_DTD, true);
		fidelity.setFidelity(FidelityOptions.FEATURE_LEXICAL_VALUE, true);
		fidelity.setFidelity(FidelityOptions.FEATURE_PREFIX, preservePrefixes);
		factory.setFidelityOptions(fidelity);
		final EncodingOptions opts = EncodingOptions.createDefault();
		opts.setOption(EncodingOptions.RETAIN_ENTITY_REFERENCE);
		opts.setOption(EncodingOptions.INCLUDE_OPTIONS);
		opts.setOption(EncodingOptions.INCLUDE_COOKIE);
		factory.setEncodingOptions(opts);
		return factory;
	}

	private static ByteArrayOutputStream encode(final EXIFactory exiFactory,
			final StringReader in) throws Exception {
		final ByteArrayOutputStream osEXI = new ByteArrayOutputStream();
		final EXIResult exiResult = new EXIResult(exiFactory);
		exiResult.setOutputStream(osEXI);
		final XMLReader xmlReader = XMLReaderFactory.createXMLReader();
		xmlReader.setContentHandler(exiResult.getHandler());
		xmlReader.parse(new InputSource(in)); // parse XML input
		osEXI.close();
		return osEXI;
	}

	private static ByteArrayOutputStream transformerEncode(
			final EXIFactory factory, final InputSource source)
			throws Exception {
		final SAXFactory exiFactory = new SAXFactory(factory);
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		final SAXEncoder encoder = exiFactory.createEXIWriter();
		encoder.setOutputStream(os);
		final Transformer transformer = TransformerFactory.newInstance()
				.newTransformer();
		final SAXResult outputTarget = new SAXResult(encoder);
		final Document doc = XMLUnit.buildTestDocument(source);
		transformer.transform(new DOMSource(doc), outputTarget);
		return os;
	}

	private static Document decode(final EXIFactory exiFactory,
			final InputSource is) throws Exception {
		final DOMResult result = new DOMResult();
		final SAXSource exiSource = new EXISource(exiFactory);
		exiSource.setInputSource(is);
		final TransformerFactory tf = TransformerFactory.newInstance();
		final Transformer transformer = tf.newTransformer();
		transformer.transform(exiSource, result);
		return (Document) result.getNode();
	}

}
