package com.siemens.ct.exi.main.api.sax;

import org.xml.sax.XMLReader;

import com.siemens.ct.exi.core.EXIFactory;
import com.siemens.ct.exi.core.FidelityOptions;
import com.siemens.ct.exi.core.exceptions.EXIException;

public class SAXFactory {
	
	final EXIFactory exiFactory;
	
	public SAXFactory(EXIFactory exiFactory) {
		this.exiFactory = exiFactory;
	}

	/**
	 * Returns an <code>EXIReader</code>
	 * 
	 * @return reader using the previously set coding options.
	 * @throws EXIException EXI exception
	 * 
	 */
	public XMLReader createEXIReader() throws EXIException {
		return new SAXDecoder(exiFactory);
	}
	

	/**
	 * Returns a <code>SAXEncoder</code> that implements
	 * <code>DefaultHandler2</code>
	 * 
	 * <p>
	 * Note that the output stream MUST be set.
	 * </p>
	 * 
	 * @return writer using the previously set coding options.
	 * @throws EXIException EXI exception
	 * 
	 */
	public SAXEncoder createEXIWriter() throws EXIException {
		final FidelityOptions fidelityOptions = exiFactory.getFidelityOptions();
		if (fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_PREFIX)
				|| fidelityOptions
						.isFidelityEnabled(FidelityOptions.FEATURE_COMMENT)
				|| fidelityOptions
						.isFidelityEnabled(FidelityOptions.FEATURE_PI)
				|| fidelityOptions
						.isFidelityEnabled(FidelityOptions.FEATURE_DTD)) {
			return new SAXEncoderExtendedHandler(exiFactory);
		} else {
			return new SAXEncoder(exiFactory);
		}
	}

}
