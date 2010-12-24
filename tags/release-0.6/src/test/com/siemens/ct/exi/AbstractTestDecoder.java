package com.siemens.ct.exi;

import java.io.InputStream;
import java.io.OutputStream;

public abstract class AbstractTestDecoder extends AbstractTestCoder {

	abstract public void decodeTo(EXIFactory ef, InputStream exiDocument,
			OutputStream xmlOutput) throws Exception;
}
