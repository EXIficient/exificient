package com.siemens.ct.exi;

import java.io.InputStream;

import com.siemens.ct.exi.exceptions.UnsupportedOption;

public abstract class AbstractTestEncoder extends AbstractTestCoder {

	abstract public void encodeTo(EXIFactory ef, InputStream xmlInput)
			throws Exception;

	protected static void setupEncodingOptions(EXIFactory ef) throws UnsupportedOption {
		if (QuickTestConfiguration.INCLUDE_COOKIE) {
			ef.getEncodingOptions().setOption(EncodingOptions.INCLUDE_COOKIE);
		}
		if (QuickTestConfiguration.INCLUDE_OPTIONS) {
			ef.getEncodingOptions().setOption(EncodingOptions.INCLUDE_OPTIONS);
		}
		if (QuickTestConfiguration.INCLUDE_SCHEMA_ID) {
			ef.getEncodingOptions().setOption(EncodingOptions.INCLUDE_SCHEMA_ID);
		}
		if (QuickTestConfiguration.RETAIN_ENTITY_REFERENCE) {
			ef.getEncodingOptions().setOption(EncodingOptions.RETAIN_ENTITY_REFERENCE);
		}
	}
}
