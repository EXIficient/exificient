package com.siemens.ct.exi.main;


import com.siemens.ct.exi.core.CodingMode;
import com.siemens.ct.exi.core.EXIFactory;
import com.siemens.ct.exi.core.EncodingOptions;
import com.siemens.ct.exi.core.FidelityOptions;
import com.siemens.ct.exi.core.exceptions.EXIException;
import com.siemens.ct.exi.core.grammars.Grammars;
import com.siemens.ct.exi.core.helpers.DefaultEXIFactory;
import com.siemens.ct.exi.grammars.GrammarFactory;
import com.siemens.ct.exi.main.api.sax.EXIResult;
import junit.framework.TestCase;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class ParallelTest extends TestCase {
    public static EXIFactory getExiFactory() throws EXIException {
        GrammarFactory gf = GrammarFactory.newInstance();
        Grammars grammar = gf.createGrammars("./data/XSLT/schema-for-xslt20.xsd");
        EXIFactory exiFactory = DefaultEXIFactory.newInstance();
        exiFactory.setGrammars(grammar);
        FidelityOptions fidelityOptions = FidelityOptions.createDefault();
        fidelityOptions.setFidelity(FidelityOptions.FEATURE_STRICT, true);
        fidelityOptions.setFidelity(FidelityOptions.FEATURE_PREFIX, true);
        exiFactory.setFidelityOptions(fidelityOptions);
        exiFactory.setCodingMode(CodingMode.COMPRESSION);
        EncodingOptions encodingOptions = EncodingOptions.createDefault();
        encodingOptions.setOption(EncodingOptions.INCLUDE_OPTIONS);
        encodingOptions.setOption(EncodingOptions.INCLUDE_SCHEMA_ID);
        exiFactory.setEncodingOptions(encodingOptions);
        return exiFactory;
    }

    public byte[] encodeXmlFileToExi(String path, EXIFactory exiFactory) throws EXIException, SAXException, IOException {
        EXIResult exiResult = new EXIResult(exiFactory);
        ByteArrayOutputStream osEXI = new ByteArrayOutputStream();
        exiResult.setOutputStream(osEXI);
        XMLReader xmlReader = XMLReaderFactory.createXMLReader();
        xmlReader.setContentHandler( exiResult.getHandler() );
        xmlReader.parse(path); // parse XML input
        return osEXI.toByteArray();
    }

    public static final class ExiSch{
        public String Name;
        public String XslPath;
        public byte[] Exi;
        public EXIFactory ExiFactory;
    }

    public void test() throws IOException, SAXException, EXIException, InterruptedException, ExecutionException {
        EXIFactory exiFactory = getExiFactory();
        File dir = new File("./data/XSLT/Examples");
        File[] directoryListing = dir.listFiles();
        HashMap<String, ExiSch> nc1NameExiMap = new HashMap<String, ExiSch>();
        for (File child : directoryListing) {
            byte[] exi = encodeXmlFileToExi(child.getPath(), exiFactory);
            ExiSch exiSch = new ExiSch();
            exiSch.Exi = exi;
            exiSch.Name = child.getName();
            exiSch.XslPath = child.getAbsolutePath();
            exiSch.ExiFactory = exiFactory;
            nc1NameExiMap.put(exiSch.Name, exiSch);
        }
        Collection<Callable<ExiResult>> tasks = new ArrayList<Callable<ExiResult>>();
        for (ExiSch exiSch : nc1NameExiMap.values()) {
            tasks.add(new Task(exiSch));
        }

        ExecutorService executor = Executors.newFixedThreadPool(8);
        List<Future<ExiResult>> results = executor.invokeAll(tasks);
        int errorCount = 0;
        for(Future<ExiResult> result : results){
            ExiResult exiResult = result.get();
            if (!exiResult.valid){
                errorCount++;
            }
        }
        executor.shutdown();
        assertEquals(0, errorCount);
    }

    private static final class ExiResult {
        public boolean valid;
    }

    private final class Task implements Callable<ExiResult> {
        private final ExiSch exiSchData;

        Task(ExiSch exiSch){
            exiSchData = exiSch;
        }
        public ExiResult call() throws Exception {
            byte[] exi = encodeXmlFileToExi(exiSchData.XslPath, exiSchData.ExiFactory);
            ExiResult exiResult = new ExiResult();
            exiResult.valid = Arrays.equals(exi, exiSchData.Exi);
            return exiResult;
        }
    }
}

