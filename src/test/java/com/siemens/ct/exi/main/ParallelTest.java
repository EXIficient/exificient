package com.siemens.ct.exi.main;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamResult;

import org.apache.xerces.impl.xs.SchemaGrammar;
import org.apache.xerces.impl.xs.XMLSchemaLoader;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xs.XSModel;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Assert;
import org.junit.Ignore;
import org.w3c.dom.Document;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.siemens.ct.exi.core.CodingMode;
import com.siemens.ct.exi.core.EXIFactory;
import com.siemens.ct.exi.core.EncodingOptions;
import com.siemens.ct.exi.core.FidelityOptions;
import com.siemens.ct.exi.core.exceptions.EXIException;
import com.siemens.ct.exi.core.grammars.Grammars;
import com.siemens.ct.exi.core.grammars.SchemaInformedGrammars;
import com.siemens.ct.exi.core.helpers.DefaultEXIFactory;
import com.siemens.ct.exi.grammars.GrammarFactory;
import com.siemens.ct.exi.main.api.dom.DOMBuilder;
import com.siemens.ct.exi.main.api.dom.DOMWriter;
import com.siemens.ct.exi.main.api.sax.EXIResult;
import com.siemens.ct.exi.main.data.AbstractTestCase;
import com.siemens.ct.exi.main.data.TestXSDResolver;

public class ParallelTest extends AbstractTestCase {

	static final int ENCODE_THREADS = 4; // 8
	static final int ENCODE_TASKS = 50; // 100

	static final int DECODE_THREADS = 4; // 8
	static final int DECODE_TASKS = 7; // 14

	public ParallelTest(String s) {
		super(s);
	}

	public void testParallelXerces() throws XNIException, IOException,
			InterruptedException, ExecutionException {

		Collection<Callable<XSModel>> tasks = new ArrayList<Callable<XSModel>>();
		for (int i = 0; i < 25; i++) {
			Callable<XSModel> task = new Callable<XSModel>() {
				public XSModel call() throws Exception {
					XMLEntityResolver entityResolver = new TestXSDResolver();
					String sXSD = "./data/XSLT/schema-for-xslt20.xsd";
					// load XSD schema & get XSModel
					XMLSchemaLoader sl = new XMLSchemaLoader();
					sl.setEntityResolver(entityResolver);
					XMLInputSource xsdSource = new XMLInputSource(null, sXSD,
							null);

					SchemaGrammar g = (SchemaGrammar) sl.loadGrammar(xsdSource);
					XSModel xsModel = g.toXSModel();
					return xsModel;
				}
			};

			tasks.add(task);
		}

		ExecutorService executor = Executors.newFixedThreadPool(4);
		List<Future<XSModel>> results = executor.invokeAll(tasks);

		for (Future<XSModel> result : results) {
			System.out.println("XSModel: " + result.get());
		}
	}

	@Ignore
	public void testParallelGrammarsCreation() throws XNIException,
			IOException, InterruptedException, ExecutionException,
			EXIException, ParserConfigurationException,
			DatatypeConfigurationException {

		Collection<Callable<Grammars>> tasks = new ArrayList<Callable<Grammars>>();
		for (int i = 0; i < 100; i++) {
			Callable<Grammars> task = new Callable<Grammars>() {
				public Grammars call() throws Exception {
					GrammarFactory gf = GrammarFactory.newInstance();
					Grammars grs = gf
							.createGrammars("./data/general/randj.xsd");

					return grs;
				}
			};

			tasks.add(task);
		}

		ExecutorService executor = Executors.newFixedThreadPool(10);
		List<Future<Grammars>> results = executor.invokeAll(tasks);

		byte[] comparisonBytes = null;

		for (Future<Grammars> result : results) {
			boolean equal = false;
			// TODO add proper test again
			/*
			if (comparisonBytes == null) {
				// first item
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				Grammars2X g2j = new Grammars2X();
				ExiGrammars exiGrammar = g2j
						.toGrammarsX((SchemaInformedGrammars) result.get());
				Grammars2X.marshal(exiGrammar, new StreamResult(baos));
				// System.out.println(new String(baos.toByteArray()));
				comparisonBytes = baos.toByteArray();
				equal = true;
			} else {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				Grammars2X g2j = new Grammars2X();
				ExiGrammars exiGrammar = g2j
						.toGrammarsX((SchemaInformedGrammars) result.get());
				Grammars2X.marshal(exiGrammar, new StreamResult(baos));
				byte[] bytes = baos.toByteArray();
				equal = Arrays.equals(comparisonBytes, bytes);
			}

			System.out.println("Grammars: " + result.get() + "\t" + equal);
			assertTrue(equal);
			*/
		}
	}

	public static EXIFactory getExiFactory() throws EXIException {
		GrammarFactory gf = GrammarFactory.newInstance();
		// no internet connection, try offline
		XMLEntityResolver entityResolver = new TestXSDResolver();
		Grammars grammar = gf.createGrammars(
				"./data/XSLT/schema-for-xslt20.xsd", entityResolver);
		EXIFactory exiFactory = DefaultEXIFactory.newInstance();
		exiFactory.setGrammars(grammar);
		FidelityOptions fidelityOptions = FidelityOptions.createDefault();
		fidelityOptions.setFidelity(FidelityOptions.FEATURE_STRICT, true);
		fidelityOptions.setFidelity(FidelityOptions.FEATURE_PREFIX, true);
		// activate this option will make the tests pass
		// fidelityOptions.setFidelity(FidelityOptions.FEATURE_LEXICAL_VALUE,
		// true);
		exiFactory.setFidelityOptions(fidelityOptions);
		exiFactory.setCodingMode(CodingMode.COMPRESSION);
		EncodingOptions encodingOptions = EncodingOptions.createDefault();
		encodingOptions.setOption(EncodingOptions.INCLUDE_OPTIONS);
		encodingOptions.setOption(EncodingOptions.INCLUDE_SCHEMA_ID);
		exiFactory.setEncodingOptions(encodingOptions);
		return exiFactory;
	}

	public byte[] encodeDocToExi(Document doc, EXIFactory exiFactory)
			throws Exception {
		ByteArrayOutputStream exiEncodedOutput = new ByteArrayOutputStream();
		DOMWriter enc = new DOMWriter(exiFactory);
		enc.setOutput(exiEncodedOutput);
		enc.encode(doc);
		return exiEncodedOutput.toByteArray();
	}

	public byte[] encodePathToExi(String path, EXIFactory exiFactory)
			throws Exception {
		EXIResult exiResult = new EXIResult(exiFactory);
		ByteArrayOutputStream osEXI = new ByteArrayOutputStream();
		exiResult.setOutputStream(osEXI);
		XMLReader xmlReader = XMLReaderFactory.createXMLReader();
		xmlReader.setContentHandler(exiResult.getHandler());
		xmlReader.parse(path);
		return osEXI.toByteArray();
	}

	public Document decodeExiToDoc(byte[] exi, EXIFactory exiFactory)
			throws Exception {
		InputStream exiDocument = new ByteArrayInputStream(exi);
		DOMBuilder domBuilder = new DOMBuilder(exiFactory);
		Document doc = domBuilder.parse(exiDocument);
		return doc;
	}

	enum ParallelExiFactory {
		Single, ThreadLocal, PerTask,
	}

	public static final class ExiSch {
		public String Name;
		public byte[] Exi;
		public EXIFactory ExiFactory;
		public Document RoundTripDoc;
		public ParallelExiFactory ParallelExiFactory;
		public String Path;
	}

	private static class ThreadLocalExiFactory extends ThreadLocal<EXIFactory> {

		@Override
		protected EXIFactory initialValue() {
			try {
				return ParallelTest.getExiFactory();
			} catch (EXIException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	private static final ThreadLocalExiFactory generateThreadLocalExiFactory = new ThreadLocalExiFactory();

	public void testParallelEncodeWithSingleFactory() throws Exception {
		testParallelEncode(ParallelExiFactory.Single, ENCODE_THREADS,
				ENCODE_TASKS, false);
	}

	public void testParallelEncodeWithThreadLocalFactory() throws Exception {
		testParallelEncode(ParallelExiFactory.ThreadLocal, ENCODE_THREADS,
				ENCODE_TASKS, false);
	}

	public void testParallelEncodeWithPerTaskFactory() throws Exception {
		testParallelEncode(ParallelExiFactory.PerTask, ENCODE_THREADS,
				ENCODE_TASKS, false);
	}

	private void testParallelEncode(ParallelExiFactory parallelExiFactory,
			int nbThreads, int nbTask, boolean testXml) throws Exception {
		EXIFactory exiFactory = getExiFactory();
		ArrayList<ExiSch> nc1NameExiMap = getExiSch(parallelExiFactory,
				testXml, exiFactory);
		Collection<Callable<EncodeResult>> tasks = new ArrayList<Callable<EncodeResult>>();
		for (int i = 0; i < nbTask; i++) {
			tasks.add(new TaskEncode(
					nc1NameExiMap.get(i % nc1NameExiMap.size())));
		}

		ExecutorService executor = Executors.newFixedThreadPool(nbThreads);
		List<Future<EncodeResult>> results = executor.invokeAll(tasks);
		int differentExiCount = 0;

		if (testXml) {
			XMLUnit.setIgnoreWhitespace(true);
			XMLUnit.setIgnoreAttributeOrder(true);
			XMLUnit.setIgnoreDiffBetweenTextAndCDATA(true);
			XMLUnit.setIgnoreComments(true);
		}

		for (Future<EncodeResult> result : results) {
			EncodeResult exiResult = result.get();
			boolean equals = Arrays
					.equals(exiResult.exiOriginal, exiResult.exi);
			if (!equals) {
				differentExiCount++;
			}

			if (testXml) {
				Document document = decodeExiToDoc(exiResult.exi, exiFactory);
				Diff diff = compareXML(document, exiResult.roundTripDoc);
				if (equals) {
					Assert.assertTrue(diff.toString(), diff.similar());
				} else {
					Assert.assertFalse(diff.toString(), diff.similar());
				}
			}
		}
		executor.shutdown();
		assertEquals(0, differentExiCount);
	}

	public void testParallelDecodeWithSingleFactory() throws Exception {
		testParallelDecode(ParallelExiFactory.Single, DECODE_THREADS,
				DECODE_TASKS);
	}

	public void testParallelDecodeWithThreadLocalFactory() throws Exception {
		testParallelDecode(ParallelExiFactory.ThreadLocal, DECODE_THREADS,
				DECODE_TASKS);
	}

	public void testParallelDecodeWithParTaskFactory() throws Exception {
		testParallelDecode(ParallelExiFactory.PerTask, DECODE_THREADS,
				DECODE_TASKS);
	}

	private void testParallelDecode(ParallelExiFactory parallelExiFactory,
			int nbThreads, int nbTask) throws Exception {
		EXIFactory exiFactory = getExiFactory();
		ArrayList<ExiSch> nc1NameExiMap = getExiSch(parallelExiFactory, true,
				exiFactory);
		Collection<Callable<DecodeResult>> tasks = new ArrayList<Callable<DecodeResult>>();
		for (int i = 0; i < nbTask; i++) {
			tasks.add(new TaskDecode(
					nc1NameExiMap.get(i % nc1NameExiMap.size())));
		}

		ExecutorService executor = Executors.newFixedThreadPool(nbThreads);
		List<Future<DecodeResult>> results = executor.invokeAll(tasks);
		int differentExiCount = 0;

		XMLUnit.setIgnoreWhitespace(true);
		XMLUnit.setIgnoreAttributeOrder(true);
		XMLUnit.setIgnoreDiffBetweenTextAndCDATA(true);
		XMLUnit.setIgnoreComments(true);

		for (Future<DecodeResult> result : results) {
			DecodeResult exiResult = result.get();

			Diff diff = compareXML(exiResult.roundTripDoc, exiResult.doc);
			if (!diff.similar()) {
				differentExiCount++;
			}
		}
		executor.shutdown();
		assertEquals(0, differentExiCount);
	}

	private ArrayList<ExiSch> getExiSch(ParallelExiFactory parallelExiFactory,
			boolean testXml, EXIFactory exiFactory) throws Exception {
		// File dir = new File("./data/XSLT/Examples");
		File dir = new File("./data/W3C/XMLSample");
		File[] directoryListing = dir.listFiles();
		ArrayList<ExiSch> nc1NameExiMap = new ArrayList<ExiSch>();

		for (File child : directoryListing) {
			byte[] exi = encodePathToExi(child.getPath(), exiFactory);
			ExiSch exiSch = new ExiSch();
			exiSch.Exi = exi;
			exiSch.Name = child.getName();
			exiSch.Path = child.getPath();
			if (testXml) {
				// XMLUnit 1.3 can't easily differentiate between int attribute
				// : 1 vs 1.0
				// so we test between decoded documents
				exiSch.RoundTripDoc = decodeExiToDoc(exi, exiFactory);
			}

			exiSch.ParallelExiFactory = parallelExiFactory;
			if (parallelExiFactory == ParallelExiFactory.Single) {
				exiSch.ExiFactory = exiFactory;
			}
			nc1NameExiMap.add(exiSch);
		}
		return nc1NameExiMap;
	}

	private static final class EncodeResult {
		public byte[] exi;
		public byte[] exiOriginal;
		public Document roundTripDoc;
	}

	private abstract class ExiTask {
		protected ExiSch exiSchData;

		ExiTask(ExiSch exiSch) {
			exiSchData = exiSch;
		}

		protected EXIFactory getExiFactory() throws EXIException {
			EXIFactory exiFactory = exiSchData.ExiFactory;
			if (exiFactory == null) {
				if (exiSchData.ParallelExiFactory == ParallelExiFactory.ThreadLocal) {
					exiFactory = ParallelTest.generateThreadLocalExiFactory
							.get();
				} else if (exiSchData.ParallelExiFactory == ParallelExiFactory.PerTask) {
					exiFactory = ParallelTest.getExiFactory();
				}
			}
			return exiFactory;
		}
	}

	private final class TaskEncode extends ExiTask implements
			Callable<EncodeResult> {
		TaskEncode(ExiSch exiSch) {
			super(exiSch);
		}

		public EncodeResult call() throws Exception {
			EXIFactory exiFactory = getExiFactory();
			byte[] exi = encodePathToExi(exiSchData.Path, exiFactory);
			EncodeResult exiResult = new EncodeResult();
			exiResult.exi = exi;
			exiResult.exiOriginal = exiSchData.Exi;
			exiResult.roundTripDoc = exiSchData.RoundTripDoc;
			return exiResult;
		}
	}

	private static final class DecodeResult {
		public Document doc;
		public Document roundTripDoc;
	}

	private final class TaskDecode extends ExiTask implements
			Callable<DecodeResult> {
		TaskDecode(ExiSch exiSch) {
			super(exiSch);
		}

		public DecodeResult call() throws Exception {
			EXIFactory exiFactory = getExiFactory();
			Document doc = decodeExiToDoc(exiSchData.Exi, exiFactory);
			DecodeResult decodeResult = new DecodeResult();
			decodeResult.doc = doc;
			decodeResult.roundTripDoc = exiSchData.RoundTripDoc;
			return decodeResult;
		}
	}
}
