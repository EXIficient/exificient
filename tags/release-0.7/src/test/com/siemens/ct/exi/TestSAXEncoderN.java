/*
 * Copyright (C) 2007-2011 Siemens AG
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

package com.siemens.ct.exi;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class TestSAXEncoderN extends TestSAXEncoder {
	public static final int N_RUNS = 100;

	public TestSAXEncoderN(OutputStream exiOuput) {
		super(exiOuput);
	}

	protected static void test(String xmlLocation, String exiLocation)
			throws Exception {

		// EXI output stream
		File f = new File(exiLocation);
		File path = f.getParentFile();
		if (!path.exists()) {
			path.mkdirs();
		}
		OutputStream encodedOutput = new BufferedOutputStream(
				new FileOutputStream(f));

		// create test-encoder
		TestSAXEncoderN testEncoderN = new TestSAXEncoderN(encodedOutput);

		long startTime = System.currentTimeMillis();

		// get factory
		EXIFactory ef = testEncoderN.getQuickTestEXIactory();

		for (int i = 0; i < N_RUNS; i++) {

			// XML input stream
			InputStream xmlInput = new BufferedInputStream(new FileInputStream(
					xmlLocation));

			testEncoderN.encodeTo(ef, xmlInput);
		}
		
		encodedOutput.flush();

		System.out.println("[ENC] " + QuickTestConfiguration.getXmlLocation()
				+ " --> " + exiLocation);
		long duration = System.currentTimeMillis() - startTime;
		System.out.println("Runtime: " + duration + " msecs for " + N_RUNS
				+ " runs.");
	}

	public static void main(String[] args) throws Exception {
		String xmlLocation = QuickTestConfiguration.getXmlLocation();
		String exiLocation = QuickTestConfiguration.getExiLocation() + "_"
				+ N_RUNS;
		test(xmlLocation, exiLocation);
	}
}
