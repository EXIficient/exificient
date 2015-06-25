/*
 * Copyright (C) 2007-2009 Siemens AG
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

package com.siemens.ct.exi.data;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import junit.framework.AssertionFailedError;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.siemens.ct.exi.CodingMode;
import com.siemens.ct.exi.DecodingOptions;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.EncodingOptions;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.api.sax.EXIResult;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;
import com.siemens.ct.exi.io.compression.EXIInflaterInputStream;

public class MultipleStreamTest extends AbstractTestCase {

	public static String XML_NOTEBOOK = "<notebook date=\"2007-09-12\"><note date=\"2007-07-23\" category=\"EXI\"><subject>EXI</subject><body>Do not forget it!</body></note><note date=\"2007-09-12\"><subject>shopping list</subject><body>milk, honey</body></note></notebook>";

	public MultipleStreamTest(String s) {
		super(s);
	}

	protected void _testEncode(EXIFactory exiFactory, byte[] isBytes,
			int numberOfEXIDocuments, ByteArrayOutputStream osEXI)
			throws AssertionFailedError, Exception {

		// encode exi document multiple times
		for (int i = 0; i < numberOfEXIDocuments; i++) {
			EXIResult exiResult = new EXIResult(exiFactory);
			exiResult.setOutputStream(osEXI);
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();
			xmlReader.setContentHandler(exiResult.getHandler());
			// set LexicalHandler
			xmlReader.setProperty(
					"http://xml.org/sax/properties/lexical-handler",
					exiResult.getLexicalHandler());

			xmlReader.parse(new InputSource(new ByteArrayInputStream(isBytes)));
		}

		// System.out.println("OutputSize: " + os.size());
	}

	protected void _testDecode(EXIFactory exiFactory, byte[] isBytes,
			int numberOfEXIDocuments, InputStream isEXI) throws EXIException,
			TransformerException, AssertionFailedError, IOException,
			ParserConfigurationException, SAXException {
		// decode EXI
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();

		for (int i = 0; i < numberOfEXIDocuments; i++) {
			// InputSource is = new InputSource(isEXI);
			InputSource is = new InputSource(isEXI);
			SAXSource exiSource = new SAXSource(is);
			XMLReader exiReader = exiFactory.createEXIReader();
			exiSource.setXMLReader(exiReader);
			ByteArrayOutputStream xmlOutput = new ByteArrayOutputStream();
			Result result = new StreamResult(xmlOutput);
			transformer.transform(exiSource, result);
			byte[] xmlDec = xmlOutput.toByteArray();
			// System.out.println("Decode #" + (i+1) + new String(xmlDec));

			ByteArrayInputStream baisControl = new ByteArrayInputStream(isBytes); // testing
																					// only
			checkXMLEquality(exiFactory, baisControl, new ByteArrayInputStream(
					xmlDec));
			// checkXMLValidity(exiFactory, new ByteArrayInputStream(xmlDec));
		}
	}

	protected void _test(EXIFactory exiFactory, byte[] isBytes,
			int numberOfEXIDocuments) throws AssertionFailedError, Exception {
		// output stream
		ByteArrayOutputStream osEXI = new ByteArrayOutputStream();

		_testEncode(exiFactory, isBytes, numberOfEXIDocuments, osEXI);

		InputStream isEXI = new ByteArrayInputStream(osEXI.toByteArray());
		
		_testDecode(exiFactory, isBytes, numberOfEXIDocuments, new PushbackInputStream(isEXI, DecodingOptions.PUSHBACK_BUFFER_SIZE));
	}

	public void testXMLNotebook4() throws Exception {
		// exi factory
		EXIFactory exiFactory = DefaultEXIFactory.newInstance();
		exiFactory.setCodingMode(CodingMode.COMPRESSION);
		EncodingOptions encOption = EncodingOptions.createDefault();
		encOption.setOption(EncodingOptions.INCLUDE_COOKIE);
		encOption.setOption(EncodingOptions.INCLUDE_OPTIONS);
		exiFactory.setEncodingOptions(encOption);

		_test(exiFactory, XML_NOTEBOOK.getBytes(), 4);
	}

	public void testXMLNotebook6Block3() throws Exception {
		// exi factory
		EXIFactory exiFactory = DefaultEXIFactory.newInstance();
		exiFactory.setBlockSize(3);
		exiFactory.setCodingMode(CodingMode.COMPRESSION);
		exiFactory.setFidelityOptions(FidelityOptions.createAll());

		_test(exiFactory, XML_NOTEBOOK.getBytes(), 6);
	}

	public void testXMLRandj3() throws Exception {
		// exi factory
		EXIFactory exiFactory = DefaultEXIFactory.newInstance();
		exiFactory.setCodingMode(CodingMode.COMPRESSION);
		exiFactory.setFidelityOptions(FidelityOptions.createAll());
		EncodingOptions encOption = EncodingOptions.createDefault();
		encOption.setOption(EncodingOptions.INCLUDE_COOKIE);
		encOption.setOption(EncodingOptions.INCLUDE_XSI_SCHEMALOCATION);
		encOption.setOption(EncodingOptions.INCLUDE_INSIGNIFICANT_XSI_NIL);
		exiFactory.setEncodingOptions(encOption);

		//
		String sXML = "./data/general/randj.xml";
		InputStream is = new FileInputStream(sXML);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int b;
		while ((b = is.read()) != -1) {
			baos.write(b);
		}
		is.close();

		_test(exiFactory, baos.toByteArray(), 3);
	}

	public void testXMLRandj3Add() throws Exception {
		// exi factory
		EXIFactory exiFactory = DefaultEXIFactory.newInstance();
		exiFactory.setCodingMode(CodingMode.COMPRESSION);
		exiFactory.setFidelityOptions(FidelityOptions.createAll());
		EncodingOptions encOption = EncodingOptions.createDefault();
		encOption.setOption(EncodingOptions.INCLUDE_COOKIE);
		encOption.setOption(EncodingOptions.INCLUDE_XSI_SCHEMALOCATION);
		encOption.setOption(EncodingOptions.INCLUDE_INSIGNIFICANT_XSI_NIL);
		exiFactory.setEncodingOptions(encOption);

		//
		String sXML = "./data/general/randj.xml";
		InputStream is = new FileInputStream(sXML);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int b;
		while ((b = is.read()) != -1) {
			baos.write(b);
		}
		is.close();

		ByteArrayOutputStream osEXI = new ByteArrayOutputStream();
		_testEncode(exiFactory, baos.toByteArray(), 3, osEXI);

		// add some bytes after EXI
		osEXI.write(31);
		osEXI.write(2);
		osEXI.write(99);
		osEXI.write(200);

		
		InputStream isEXI = new ByteArrayInputStream(osEXI.toByteArray());
		isEXI = new BufferedInputStream(isEXI);
		isEXI = new PushbackInputStream(isEXI, DecodingOptions.PUSHBACK_BUFFER_SIZE);
		_testDecode(exiFactory, baos.toByteArray(), 3, isEXI);

		// System.out.println(isEXI.read());
		assertTrue(isEXI.read() == 31);
		assertTrue(isEXI.read() == 2);
		assertTrue(isEXI.read() == 99);
		assertTrue(isEXI.read() == 200);
		assertTrue(isEXI.read() == -1);

	}

	// ///////////////////////////////////////////////////
	// TEST
	// //////////////////////////////////////////////////

	public static final boolean nowrap = true; // EXI requires no wrap

	final static int NUMBER_OF_EXI_FILES = 20;

	final static int NUMBER_OF_HEADER_BYTES = 10;
	final static byte HEADER_BYTE = 123;

	final static int NUMBER_OF_CHANNELS = 2;

	final static int NUMBER_OF_BYTES = 5;
	final static boolean RANDOM_BYTES = true;
	final static int BYTES_LENGTH = 26;

	public static void printJVMInfos() {
		System.out.println("java.class.path    : "
				+ System.getProperty("java.class.path"));
		System.out.println("java.vendor        : "
				+ System.getProperty("java.vendor"));
		System.out.println("java.vendor.url    : "
				+ System.getProperty("java.vendor.url"));
		System.out.println("java.version       : "
				+ System.getProperty("java.version"));
		System.out.println("sun.arch.data.model: "
				+ System.getProperty("sun.arch.data.model"));
	}

	public static void main(String[] args) throws IOException {
		printJVMInfos();

		List<byte[]> encodeBytes = new ArrayList<byte[]>();

		/*
		 * ENCODING
		 */
		System.out.println(">> Start encoding...");
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		for (int f = 0; f < NUMBER_OF_EXI_FILES; f++) {
			System.out.println("File " + f);

			// encode
			for (int j = 0; j < NUMBER_OF_HEADER_BYTES; j++) {
				// os.write(random.nextInt()); // EXI header
				os.write(HEADER_BYTE);
			}

			Deflater deflater = null;
			for (int k = 0; k < NUMBER_OF_CHANNELS; k++) {
				System.out.println(" Channel " + k);
				if (deflater == null) {
					deflater = new Deflater(Deflater.DEFAULT_COMPRESSION,
							nowrap);
				} else {
					deflater.reset();
				}

				DeflaterOutputStream deflaterOS = new DeflaterOutputStream(os,
						deflater);
				// some EXI data
				for (int i = 0; i < NUMBER_OF_BYTES; i++) {
					byte[] bs = getBytes(BYTES_LENGTH);
					encodeBytes.add(bs);
					System.out.println("\t" + new String(bs));
					deflaterOS.write(bs);
				}

				// finalize deflate stream
				deflaterOS.finish();
				// deflaterOS.close();

				System.out.println("\t\tLastByte of deflater "
						+ os.toByteArray()[os.toByteArray().length - 1]);

			}
			os.flush();
		}

		byte[] bytesEXI = os.toByteArray();
		os.close();

		/*
		 * DECODING
		 */

		System.out.println("<< Start decoding...");
		InputStream is = new ByteArrayInputStream(bytesEXI);

		is = new BufferedInputStream(is); // mark supported for other not
											// "markable" streams e.g., file
											// stream
		// is.mark(Integer.MAX_VALUE);
		is = new PushbackInputStream(is, DecodingOptions.PUSHBACK_BUFFER_SIZE);
		

		List<byte[]> decodeBytes = new ArrayList<byte[]>();

		for (int f = 0; f < NUMBER_OF_EXI_FILES; f++) {
			System.out.println("File " + f);
			System.out.println("Available Bytes before header: "
					+ is.available());

			// decode header
			for (int j = 0; j < NUMBER_OF_HEADER_BYTES; j++) {
				int h = is.read();// EXI header
				if (h != HEADER_BYTE) { // '€'
					throw new RuntimeException(
							"EXI Header Error BYTES MISMATCH");
				}
			}

			Inflater inflater = new Inflater(nowrap);

			System.out.println("Available Bytes after header: "
					+ is.available());

			// decode channels
			for (int k = 0; k < NUMBER_OF_CHANNELS; k++) {

				System.out.println("Remaining: " + inflater.getRemaining());
				// System.out.println("Available: " + is.available());

				//is.mark(inputBufferSize); // mark position for possible rewind,
											// max size according inflater
											// buffer

				// if ( inflater.getRemaining() == 0) {
				// // no skipping needed
				// } else {
				// // skip
				// int av = is.available() + inflater.getRemaining();
				// is.reset();
				// int toskip = is.available() - av;
				// is.skip(toskip);
				// }
				// inflater.reset();

				@SuppressWarnings("resource")
				EXIInflaterInputStream inflaterInputStream = new EXIInflaterInputStream(
						(PushbackInputStream) is, inflater, DecodingOptions.PUSHBACK_BUFFER_SIZE);

				for (int i = 0; i < NUMBER_OF_BYTES; i++) {
					byte[] bs = new byte[BYTES_LENGTH];
					for (int l = 0; l < BYTES_LENGTH; l++) {
						bs[l] = (byte) inflaterInputStream.read();
					}
					decodeBytes.add(bs);
					System.out.println("\t" + new String(bs));
				}

				System.out.println("Available Bytes after channel " + k + ": "
						+ is.available() + " and inflater still contains "
						+ inflater.getRemaining() + " in buffer and "
						+ inflater.getBytesRead() + " bytes read so far");

				if (inflater.getRemaining() > 0) {
					
					
					// inflater read beyond deflate stream, reset position
//					is.reset();
//					is.skip(inflater.getBytesRead());
					
					System.out.println("--> Rewind " + inflater.getRemaining()
							+ " bytes");
					
					inflaterInputStream.pushback();
				}

			}

			// // Note: in many cases not needed (e.g., if no buffered stream
			// passed)
			// // fix input stream so that another process can read data at the
			// right position...
			// if ( inflater.getRemaining() != 0) {
			// int av = inflater.getRemaining() + is.available();
			// is.reset();
			// int toskip = is.available() - av;
			// is.skip(toskip);
			// }

		}

		/*
		 * CONSISTENCY CHECK
		 */

		// compare strings
		if (encodeBytes.size() != decodeBytes.size()) {
			throw new RuntimeException("Bytes SIZE MISMATCH");
		}

		for (int i = 0; i < encodeBytes.size(); i++) {
			byte[] e = encodeBytes.get(i);
			byte[] d = decodeBytes.get(i);
			if (!Arrays.equals(e, d)) {
				throw new RuntimeException("Bytes MISMATCH");
			}
		}
	}

	private static SecureRandom random = new SecureRandom();

	public static byte[] getBytes(int len) {
		String s;
		if (RANDOM_BYTES) {
			s = new BigInteger(130, random).toString(32);
			if (s.length() > len) {
				s = s.substring(0, len);
			} else {
				while (s.length() < len) {
					s += "X";
				}
			}
		} else {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < len; i++) {
				sb.append((char) (i + 'A'));
			}
			s = sb.toString();
		}

		byte[] bs = s.getBytes();
		if (bs.length != len) {
			throw new RuntimeException("BYTES MISMATCH!!!");
		}

		return bs;

	}



}
