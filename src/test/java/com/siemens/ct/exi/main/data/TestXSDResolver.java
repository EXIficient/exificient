package com.siemens.ct.exi.main.data;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLInputSource;

public class TestXSDResolver implements
		org.apache.xerces.xni.parser.XMLEntityResolver {

	public TestXSDResolver() {
	}

	public XMLInputSource resolveEntity(XMLResourceIdentifier resourceIdentifier)
			throws XNIException, IOException {
		// String publicId = resourceIdentifier.getPublicId();
		// String baseSystemId = resourceIdentifier.getBaseSystemId();
		// String expandedSystemId =
		// resourceIdentifier.getExpandedSystemId();
		String literalSystemId = resourceIdentifier.getLiteralSystemId();

		// System.out.println(literalSystemId);

		if ("XMLSchema.dtd".equals(literalSystemId)
				|| "datatypes.dtd".equals(literalSystemId)) {
			InputStream isTypes = new FileInputStream("./data/W3C/xsd/"
					+ literalSystemId);

			String publicId = null;
			String systemId = null;
			String baseSystemId = null;
			String encoding = null;
			XMLInputSource xsdSourceTypes = new XMLInputSource(publicId,
					systemId, baseSystemId, isTypes, encoding);
			return xsdSourceTypes;
		} else if ("http://www.w3.org/2001/XMLSchema.xsd".equals(literalSystemId)) {
			InputStream isTypes = new FileInputStream("./data/W3C/xsd/"
					+ "XMLSchema.xsd");
			XMLInputSource xsdSourceTypes = new XMLInputSource(null,
					null, null, isTypes, null);
			return xsdSourceTypes;
		} else if ("http://www.w3.org/2001/xml.xsd".equals(literalSystemId)) {
			InputStream isTypes = new FileInputStream("./data/W3C/xsd/"
					+ "xml.xsd");

			String publicId = null;
			String systemId = null;
			String baseSystemId = null;
			String encoding = null;
			XMLInputSource xsdSourceTypes = new XMLInputSource(publicId,
					systemId, baseSystemId, isTypes, encoding);
			return xsdSourceTypes;
		} else {
			// Note: if the entity cannot be resolved, this method
			// should return null.
			return null;
		}

	}
}