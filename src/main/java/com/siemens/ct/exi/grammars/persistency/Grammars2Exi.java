package com.siemens.ct.exi.grammars.persistency;

import com.siemens.ct.exi.CodingMode;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.EncodingOptions;
import com.siemens.ct.exi.SchemaIdResolver;
import com.siemens.ct.exi._2017.schemaforgrammars.ExiGrammars;
import com.siemens.ct.exi.api.sax.EXIResult;
import com.siemens.ct.exi.api.sax.SAXFactory;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.exceptions.UnsupportedOption;
import com.siemens.ct.exi.grammars.Grammars;
import com.siemens.ct.exi.grammars.SchemaInformedGrammars;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Grammars2Exi {
    static Class<ExiGrammars> CLASS = ExiGrammars.class;

    /**
     * Marshal the SchemaInformedGrammars into an EXI OutputStream.
     * @param schemaInformedGrammars
     *      The SchemaInformedGrammars to be marshalled.
     * @param exiOutputStream
     *      EXI will be send to this OutputStream.
     * @throws JAXBException
     *      If any unexpected problem occurs during the marshalling.
     * @throws EXIException
     * @throws ParserConfigurationException
     * @throws DatatypeConfigurationException
     * @throws IOException
     */
    public static void marshal(SchemaInformedGrammars schemaInformedGrammars,
                                OutputStream exiOutputStream)
            throws JAXBException, EXIException, ParserConfigurationException, DatatypeConfigurationException, IOException {
        Grammars2X g2j = new Grammars2X();
        ExiGrammars exiGrammar = g2j.toGrammarsX(schemaInformedGrammars);
        EXIFactory exiFactory = getExiFactory();
        EXIResult exiResult = new EXIResult(exiFactory);
        exiResult.setOutputStream(exiOutputStream);
        Grammars2X.marshal(exiGrammar, exiResult.getHandler());
    }

    /**
     * Implements SchemaIdResolver that resolves all of the time
     * with the given GrammarsForGrammars instance.
     */
    private static class GrammarsForGrammarsSchemaIdResolver implements SchemaIdResolver {
        private GrammarsForGrammars grammarsForGrammars;

        public GrammarsForGrammarsSchemaIdResolver(GrammarsForGrammars grammarsForGrammars) {
            this.grammarsForGrammars = grammarsForGrammars;
        }

        public Grammars resolveSchemaId(String schemaId) throws EXIException {
            return  this.grammarsForGrammars;
        }
    }

    /**
     * Get an EXIFactory with the grammars GrammarsForGrammars, coding mode COMPRESSION,
     * using default encoding options, with INCLUDE_OPTIONS and INCLUDE_SCHEMA_ID.
     * @return
     * @throws UnsupportedOption
     */
    private static EXIFactory getExiFactory() throws UnsupportedOption {
        EXIFactory exiFactory = DefaultEXIFactory.newInstance();
        GrammarsForGrammars grammar = new GrammarsForGrammars();
        exiFactory.setGrammars(grammar);
        exiFactory.setCodingMode(CodingMode.COMPRESSION);
        EncodingOptions encodingOptions = EncodingOptions.createDefault();
        encodingOptions.setOption(EncodingOptions.INCLUDE_OPTIONS);
        encodingOptions.setOption(EncodingOptions.INCLUDE_SCHEMA_ID);
        exiFactory.setEncodingOptions(encodingOptions);
        exiFactory.setSchemaIdResolver(new GrammarsForGrammarsSchemaIdResolver(grammar));
        return exiFactory;
    }

    /**
     * Unmarshal EXI InputStream and return the corresponding SchemaInformedGrammars.
     * @param exiInputStream
     *      The EXI InputStream.
     * @return
     * @throws JAXBException
     * @throws EXIException
     * @throws IOException
     * @throws SAXException
     * @throws TransformerConfigurationException
     */
    public static SchemaInformedGrammars unmarshal(InputStream exiInputStream)
            throws JAXBException, EXIException, IOException, SAXException, TransformerConfigurationException {
        EXIFactory exiFactory = getExiFactory();

        InputSource exiInputSource = new InputSource(exiInputStream);
        SAXSource exiSource = new SAXSource(exiInputSource);
        XMLReader exiReader = new SAXFactory(exiFactory).createEXIReader();
        exiSource.setXMLReader(exiReader);

        Object o = Grammars2X.unmarshal(exiSource);
        if(!(o instanceof ExiGrammars)) {
            throw new JAXBException("Unmarshalled object not of instance " + CLASS + ". Instead " + o.getClass());
        }
        ExiGrammars exiGrammars = (ExiGrammars)o;
        SchemaInformedGrammars schemaInformedGrammars = Grammars2X.toGrammars(exiGrammars);
        return schemaInformedGrammars;
    }
}
