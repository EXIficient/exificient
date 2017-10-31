package com.siemens.ct.exi.grammars.persistency;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi._2017.schemaforgrammars.ExiGrammars;
import com.siemens.ct.exi.api.sax.EXIResult;
import com.siemens.ct.exi.grammars.SchemaInformedGrammars;
import com.siemens.ct.exi.grammars.XSDGrammarsBuilder;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class Grammars2ExiTest extends TestCase {
    XSDGrammarsBuilder grammarBuilder = XSDGrammarsBuilder.newInstance();

    public Grammars2ExiTest() {
        super();
    }

    protected void _test(String xsd, String xml) throws Exception {
        grammarBuilder.loadGrammars(xsd);
        SchemaInformedGrammars grammarsIn = grammarBuilder.toGrammars();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Grammars2Exi.marshal(grammarsIn, baos);
        baos.close();
        byte[] expecteds = baos.toByteArray();
        Grammars2X grammars2X = new Grammars2X();
        ExiGrammars exiGrammars = grammars2X.toGrammarsX(grammarsIn);
        ByteArrayOutputStream baosXml = new ByteArrayOutputStream();
        Grammars2X.marshal(exiGrammars, baosXml);

        SchemaInformedGrammars grammarsOut = Grammars2Exi.unmarshal(new ByteArrayInputStream(expecteds));
        Assert.assertTrue(grammarsIn.equals(grammarsOut));
        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        baos2.close();

        EXIFactory exiFactoryIn = DefaultEXIFactory.newInstance();
        exiFactoryIn.setGrammars(grammarsIn);
        ByteArrayOutputStream osEXIIn = new ByteArrayOutputStream();
        EXIResult exiResultIn = new EXIResult(exiFactoryIn);
        exiResultIn.setOutputStream(osEXIIn);
        XMLReader xmlReaderIn = XMLReaderFactory.createXMLReader();
        xmlReaderIn.setContentHandler( exiResultIn.getHandler() );
        InputSource isXML = new InputSource(xml);
        xmlReaderIn.parse(isXML);
        osEXIIn.close();
        EXIFactory exiFactoryOut = DefaultEXIFactory.newInstance();
        exiFactoryOut.setGrammars(grammarsOut);
        ByteArrayOutputStream osEXIOut = new ByteArrayOutputStream();
        EXIResult exiResultOut = new EXIResult(exiFactoryOut);
        exiResultOut.setOutputStream(osEXIOut);
        XMLReader xmlReaderOut = XMLReaderFactory.createXMLReader();
        xmlReaderOut.setContentHandler( exiResultOut.getHandler() );
        xmlReaderOut.parse(isXML);
        osEXIOut.close();

        Assert.assertArrayEquals(osEXIIn.toByteArray(), osEXIOut.toByteArray());
    }

    @Test
    public void test_notebook() throws Exception {
        _test("data/W3C/PrimerNotebook/notebook.xsd", "data/W3C/PrimerNotebook/notebook.xml");
    }

    @Test
    public void test_XMLSample() throws Exception {
        _test("data/W3C/XMLSample/XMLSample.xsd", "data/W3C/XMLSample/XMLSample.xml");
    }

    @Test
    public void test_EXIbyExample() throws Exception {
        _test("data/W3C/EXIbyExample/XMLSample.xsd", "data/W3C/EXIbyExample/XMLSample.xml");
    }

    @Test
    public void test_choice1() throws Exception {
        _test("./data/schema/choice.xsd", "./data/schema/choice1.xml");
    }

    @Test
    public void test_choice2() throws Exception {
        _test("./data/schema/choice.xsd", "./data/schema/choice2.xml");
    }

    @Test
    public void test_choice3() throws Exception {
        _test("./data/schema/choice.xsd", "./data/schema/choice3.xml");
    }

    @Test
    public void test_choiceN1() throws Exception {
        _test("./data/schema/choiceN.xsd", "./data/schema/choiceN1.xml");
    }

    @Test
    public void test_sequence1() throws Exception {
        _test("./data/schema/sequence.xsd", "./data/schema/sequence1.xml");
    }

    @Test
    public void test_sequence2() throws Exception {
        _test("./data/schema/sequence.xsd", "./data/schema/sequence2.xml");
    }

    @Test
    public void test_group1() throws Exception {
        _test("./data/schema/group1.xsd", "./data/schema/group1.xml");
    }

    @Test
    public void test_group2() throws Exception {
        _test("./data/schema/group2.xsd", "./data/schema/group2.xml");
    }

    @Test
    public void test_group3() throws Exception {
        _test("./data/schema/group3.xsd", "./data/schema/group3.xml");
    }

    @Test
    public void test_union1() throws Exception {
        _test("./data/schema/union1.xsd", "./data/schema/union1.xml");
    }

    @Test
    public void test_substitutionGroup1a() throws Exception {
        _test("./data/schema/substitutionGroup1.xsd", "./data/schema/substitutionGroup1a.xml");
    }

    @Test
    public void test_substitutionGroup1b() throws Exception {
        _test("./data/schema/substitutionGroup1.xsd", "./data/schema/substitutionGroup1b.xml");
    }

    @Test
    public void test_attributeGroup1a() throws Exception {
        _test("./data/schema/attributeGroup1.xsd", "./data/schema/attributeGroup1a.xml");
    }

    @Test
    public void test_attributeGroup1b() throws Exception {
        _test("./data/schema/attributeGroup1.xsd", "./data/schema/attributeGroup1b.xml");
    }

    @Test
    public void test_occurrences1() throws Exception {
        _test("./data/schema/occurrences1.xsd", "./data/schema/occurrences1.xml");
    }

    @Test
    public void test_occurrences2() throws Exception {
        _test("./data/schema/occurrences2.xsd", "./data/schema/occurrences2.xml");
    }

    @Test
    public void test_all() throws Exception {
        _test("./data/schema/all.xsd", "./data/schema/all.xml");
    }

    @Test
    public void test_all2() throws Exception {
        _test("./data/schema/all2.xsd", "./data/schema/all2.xml");
    }

    @Test
    public void test_enumeration() throws Exception {
        _test("./data/schema/enumeration.xsd", "./data/schema/enumeration.xml");
    }

    @Test
    public void test_list() throws Exception {
        _test("./data/schema/list.xsd", "./data/schema/list.xml");
    }

    @Test
    public void test_unsignedInteger() throws Exception {
        _test("./data/schema/unsignedInteger.xsd", "./data/schema/unsignedInteger.xml");
    }

    @Test
    public void test_mixed() throws Exception {
        _test("./data/schema/mixed.xsd", "./data/schema/mixed.xml");
    }

    @Test
    public void test_wildcard() throws Exception {
        _test("./data/schema/wildcard.xsd", "./data/schema/wildcard.xml");
    }

    @Test
    public void test_wildcard2() throws Exception {
        _test("./data/schema/wildcard2.xsd", "./data/schema/wildcard2.xml");
    }

    @Test
    public void test_wildcard3() throws Exception {
        _test("./data/schema/wildcard3.xsd", "./data/schema/wildcard3.xml");
    }

    @Test
    public void test_nillable1() throws Exception {
        _test("./data/schema/nillable.xsd", "./data/schema/nillable1.xml");
    }

    @Test
    public void test_nillable2() throws Exception {
        _test("./data/schema/nillable.xsd", "./data/schema/nillable2.xml");
    }

    @Test
    public void test_type() throws Exception {
        _test("./data/schema/xsi-type.xsd", "./data/schema/xsi-type.xml");
    }

    @Test
    public void test_type2() throws Exception {
        _test("./data/schema/xsi-type2.xsd", "./data/schema/xsi-type2.xml");
    }

    @Test
    public void test_type3() throws Exception {
        _test("./data/schema/xsi-type.xsd", "./data/schema/xsi-type3.xml");
    }

    @Test
    public void test_type4() throws Exception {
        _test("./data/schema/xsi-type4.xsd", "./data/schema/xsi-type4.xml");
    }

    @Test
    public void test_type5() throws Exception {
        _test("./data/schema/empty.xsd", "./data/schema/xsi-type5.xml");
    }

    @Test
    public void test_vehicle() throws Exception {
        _test("./data/schema/vehicle.xsd", "./data/schema/vehicle.xml");
    }

    @Test
    public void test_anyAttributes() throws Exception {
        _test("./data/schema/anyAttributes.xsd", "./data/schema/anyAttributes.xml");
    }

    @Test
    public void test_globalAttribute1() throws Exception {
        _test("./data/schema/globalAttribute.xsd", "./data/schema/globalAttribute1.xml");
    }

    @Test
    public void test_any0() throws Exception {
        _test("./data/schema/any.xsd", "./data/schema/any0.xml");
    }

    @Test
    public void test_any1() throws Exception {
        _test("./data/schema/any1.xsd", "./data/schema/any1.xml");
    }

    @Test
    public void test_attributeSpace() throws Exception {
        _test("./data/schema/attributeSpace.xsd", "./data/schema/attributeSpace.xml");
    }

    @Test
    public void test_identicalQName2() throws Exception {
        _test("./data/schema/identicalQName2.xsd", "./data/schema/identicalQName2.xml");
    }

    @Test
    public void test_identicalQName() throws Exception {
        _test("./data/schema/identicalQName.xsd", "./data/schema/identicalQName.xml");
    }

    @Test
    public void test_identicalQName3() throws Exception {
        _test("./data/schema/identicalQName3.xsd", "./data/schema/identicalQName3.xml");
    }

    @Test
    public void test_booleanPattern() throws Exception {
        _test("./data/schema/booleanPattern.xsd", "./data/schema/booleanPattern.xml");
    }

    @Test
    public void test_pattern() throws Exception {
        _test("./data/schema/pattern.xsd", "./data/schema/pattern.xml");
    }

    @Test
    public void test_person() throws Exception {
        _test("./data/general/person.xsd", "./data/general/person.xml");

    }

    @Test
    public void test_person_adjusted() throws Exception {
        _test("./data/general/person.xsd", "./data/general/person_adjusted.xml");
    }

    @Test
    public void test_personal() throws Exception {
        _test("./data/general/personal.xsd", "./data/general/personal.xml");
    }

    @Test
    public void test_unbounded() throws Exception {
        _test("./data/general/unbounded.xsd", "./data/general/unbounded.xml");
    }

    @Test
    public void test_datatypeInteger() throws Exception {
        _test("./data/general/datatypeInteger.xsd", "./data/general/datatypeInteger.xml");
    }

    @Test
    public void test_datatypeFloat() throws Exception {
        _test("./data/general/datatypeFloat.xsd", "./data/general/datatypeFloat.xml");
    }

    @Test
    public void test_datatypes() throws Exception {
        _test("./data/general/datatypes.xsd", "./data/general/datatypes.xml");
    }

    @Test
    public void test_datatypes2() throws Exception {
        _test("./data/general/datatypes2.xsd", "./data/general/datatypes2.xml");
    }

    @Test
    public void test_order() throws Exception {
        _test("./data/general/order.xsd", "./data/general/order.xml");
    }

    @Test
    public void test_structure() throws Exception {
        _test("./data/general/complex-structure.xsd", "./data/general/complex-structure.xml");
    }

    @Test
    public void test_simpleContent() throws Exception {
        _test("./data/general/simpleContent.xsd", "./data/general/simpleContent.xml");
    }

    @Test
    public void test_emptyContent() throws Exception {
        _test("./data/general/emptyContent.xsd", "./data/general/emptyContent.xml");
    }

    @Test
    public void test_attributes() throws Exception {
        _test("./data/general/attributes.xsd", "./data/general/attributes.xml");
    }

    @Test
    public void test_randj() throws Exception {
        _test("./data/general/randj.xsd", "./data/general/randj.xml");
    }

    @Test
    public void test_po() throws Exception {
        _test("./data/general/po.xsd", "./data/general/po.xml");
    }

    @Test
    public void test_test1() throws Exception {
        _test("./data/general/test1.xsd", "./data/general/test1.xml");
    }

    @Test
    public void test_test1_pfx() throws Exception {
        _test("./data/general/test1.xsd", "./data/general/test1_pfx.xml");
    }

    @Test
    public void test_test2() throws Exception {
        _test("./data/general/test2.xsd", "./data/general/test2.xml");
    }

    @Test
    public void test_test3() throws Exception {
        _test("./data/general/test3.xsd", "./data/general/test3.xml");
    }

    @Test
    public void test_test4() throws Exception {
        _test("./data/general/test4.xsd", "./data/general/test4.xml");
    }

    @Test
    public void test_test5() throws Exception {
        _test("./data/general/test5.xsd", "./data/general/test5.xml");
    }

    @Test
    public void test_pi1() throws Exception {
        _test("./data/general/pi1.xsd", "./data/general/pi1.xml");
    }

    @Test
    public void test_docType() throws Exception {
        _test("./data/general/docType.xsd", "./data/general/docType.xml");
    }

    @Test
    public void test_docType1() throws Exception {
        _test("./data/general/docType.xsd", "./data/general/docType1.xml");
    }

    @Test
    public void test_docType2() throws Exception {
        _test("./data/general/docType.xsd", "./data/general/docType2.xml");
    }

    @Test
    public void test_docType3() throws Exception {
        _test("./data/general/docType.xsd", "./data/general/docType3.xml");
    }

    @Test
    public void test_entityReference1() throws Exception {
        _test("./data/general/empty.xsd", "./data/general/entityReference1.xml");
    }

    @Test
    public void test_entityReference2() throws Exception {
        _test("./data/general/empty.xsd", "./data/general/entityReference2.xml");
    }

    @Test
    public void test_cdata1() throws Exception {
        _test("./data/general/empty.xsd", "./data/general/cdata1.xml");
    }

    @Test
    public void test_patterns() throws Exception {
        _test("./data/general/patterns.xsd", "./data/general/patterns.xml");
    }

    @Test
    public void test_stringTable1() throws Exception {
        _test("./data/general/stringTable1.xsd", "./data/general/stringTable1.xml");
    }

    @Test
    public void test_stringTable2() throws Exception {
        _test("./data/general/stringTable2.xsd", "./data/general/stringTable2.xml");
    }
}
