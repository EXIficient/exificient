package com.siemens.ct.exi;

import java.io.InputStream;
import java.io.OutputStream;

public abstract class AbstractTestDecoder extends AbstractTestCoder {

//	abstract public void setupEXIReader(EXIFactory ef) throws Exception;
	
	abstract public void decodeTo(InputStream exiDocument, OutputStream xmlOutput) throws Exception;
}
