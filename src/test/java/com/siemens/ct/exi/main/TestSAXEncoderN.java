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
package com.siemens.ct.exi.main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.siemens.ct.exi.core.EXIFactory;
import com.siemens.ct.exi.core.exceptions.EXIException;

public class TestSAXEncoderN extends TestSAXEncoder {
	public static final int N_RUNS = 10;

	public TestSAXEncoderN(EXIFactory ef) throws EXIException {
		super(ef);
	}

	protected void test(String xmlLocation, String exiLocation)
			throws Exception {

		long startTime = System.currentTimeMillis();

		OutputStream exiOutput = new FileOutputStream(exiLocation);

		for (int i = 0; i < N_RUNS; i++) {

			// XML input stream
			InputStream xmlInput = new BufferedInputStream(new FileInputStream(
					xmlLocation));

			this.encodeTo(xmlInput, exiOutput);
			// encodeTo(ef, xmlInput);
		}

		exiOutput.flush();

		System.out.println("[ENC] " + QuickTestConfiguration.getXmlLocation()
				+ " --> " + exiLocation);
		long duration = System.currentTimeMillis() - startTime;
		System.out.println("Runtime: " + duration + " msecs for " + N_RUNS
				+ " runs.");
	}

	protected static OutputStream getOutputStream(String exiLocation)
			throws FileNotFoundException {
		// EXI output stream
		File f = new File(exiLocation);
		File path = f.getParentFile();
		if (!path.exists()) {
			boolean bool = path.mkdirs();
			assert (bool);
		}
		OutputStream encodedOutput = new BufferedOutputStream(
				new FileOutputStream(f));

		return encodedOutput;
	}

	public static void main(String[] args) throws Exception {
		String xmlLocation = QuickTestConfiguration.getXmlLocation();
		String exiLocation = QuickTestConfiguration.getExiLocation() + "_"
				+ N_RUNS;

		// create test-encoder
		TestSAXEncoderN testEncoderN = new TestSAXEncoderN(
				TestSAXEncoderN.getQuickTestEXIactory());

		// get factory
		// EXIFactory ef = TestSAXEncoderN.getQuickTestEXIactory();
		// testEncoderN.setupEXIWriter(ef);

		testEncoderN.test(xmlLocation, exiLocation);
	}
}
