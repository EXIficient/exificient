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

package com.siemens.ct.exi.main.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import org.custommonkey.xmlunit.XMLTestCase;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.siemens.ct.exi.core.EXIBodyDecoder;
import com.siemens.ct.exi.core.EXIFactory;
import com.siemens.ct.exi.core.context.QNameContext;
import com.siemens.ct.exi.core.exceptions.EXIException;
import com.siemens.ct.exi.core.grammars.event.EventType;
import com.siemens.ct.exi.core.helpers.DefaultEXIFactory;
import com.siemens.ct.exi.core.values.Value;
import com.siemens.ct.exi.main.api.sax.EXIResult;

public class BoundedTableTestCase extends XMLTestCase {

	String xml = "<root><a>VAL_0</a><b>VAL_0</b><a>VAL_1</a><b>VAL_1</b><a>VAL_2</a><b>VAL_2</b><a>VAL_3</a><b>VAL_3</b><a>VAL_4</a><b>VAL_4</b><a>VAL_5</a><b>VAL_5</b><a>VAL_6</a><b>VAL_6</b><a>VAL_7</a><b>VAL_7</b><a>VAL_8</a><b>VAL_8</b><a>VAL_9</a><b>VAL_9</b><a>VAL_10</a><b>VAL_10</b><a>VAL_11</a><b>VAL_11</b><a>VAL_12</a><b>VAL_12</b><a>VAL_13</a><b>VAL_13</b><a>VAL_14</a><b>VAL_14</b><a>VAL_15</a><b>VAL_15</b><a>VAL_16</a><b>VAL_16</b><a>VAL_17</a><b>VAL_17</b><a>VAL_18</a><b>VAL_18</b><a>VAL_19</a><b>VAL_19</b><a>VAL_20</a><b>VAL_20</b><a>VAL_21</a><b>VAL_21</b><a>VAL_22</a><b>VAL_22</b><a>VAL_23</a><b>VAL_23</b><a>VAL_24</a><b>VAL_24</b><a>VAL_25</a><b>VAL_25</b><a>VAL_26</a><b>VAL_26</b><a>VAL_27</a><b>VAL_27</b><a>VAL_28</a><b>VAL_28</b><a>VAL_29</a><b>VAL_29</b><a>VAL_30</a><b>VAL_30</b><a>VAL_31</a><b>VAL_31</b><a>VAL_32</a><b>VAL_32</b><a>VAL_33</a><b>VAL_33</b><a>VAL_34</a><b>VAL_34</b><a>VAL_35</a><b>VAL_35</b><a>VAL_36</a><b>VAL_36</b><a>VAL_37</a><b>VAL_37</b><a>VAL_38</a><b>VAL_38</b><a>VAL_39</a><b>VAL_39</b><a>VAL_40</a><b>VAL_40</b><a>VAL_41</a><b>VAL_41</b><a>VAL_42</a><b>VAL_42</b><a>VAL_43</a><b>VAL_43</b><a>VAL_44</a><b>VAL_44</b><a>VAL_45</a><b>VAL_45</b><a>VAL_46</a><b>VAL_46</b><a>VAL_47</a><b>VAL_47</b><a>VAL_48</a><b>VAL_48</b><a>VAL_49</a><b>VAL_49</b><a>VAL_50</a><b>VAL_50</b><a>VAL_51</a><b>VAL_51</b><a>VAL_52</a><b>VAL_52</b><a>VAL_53</a><b>VAL_53</b><a>VAL_54</a><b>VAL_54</b><a>VAL_55</a><b>VAL_55</b><a>VAL_56</a><b>VAL_56</b><a>VAL_57</a><b>VAL_57</b><a>VAL_58</a><b>VAL_58</b><a>VAL_59</a><b>VAL_59</b><a>VAL_60</a><b>VAL_60</b><a>VAL_61</a><b>VAL_61</b><a>VAL_62</a><b>VAL_62</b><a>VAL_63</a><b>VAL_63</b><a>VAL_64</a><b>VAL_64</b><a>VAL_65</a><b>VAL_65</b><a>VAL_66</a><b>VAL_66</b><a>VAL_67</a><b>VAL_67</b><a>VAL_68</a><b>VAL_68</b><a>VAL_69</a><b>VAL_69</b><a>VAL_70</a><b>VAL_70</b><a>VAL_71</a><b>VAL_71</b><a>VAL_72</a><b>VAL_72</b><a>VAL_73</a><b>VAL_73</b><a>VAL_74</a><b>VAL_74</b><a>VAL_75</a><b>VAL_75</b><a>VAL_76</a><b>VAL_76</b><a>VAL_77</a><b>VAL_77</b><a>VAL_78</a><b>VAL_78</b><a>VAL_79</a><b>VAL_79</b><a>VAL_80</a><b>VAL_80</b><a>VAL_81</a><b>VAL_81</b><a>VAL_82</a><b>VAL_82</b><a>VAL_83</a><b>VAL_83</b><a>VAL_84</a><b>VAL_84</b><a>VAL_85</a><b>VAL_85</b><a>VAL_86</a><b>VAL_86</b><a>VAL_87</a><b>VAL_87</b><a>VAL_88</a><b>VAL_88</b><a>VAL_89</a><b>VAL_89</b><a>VAL_90</a><b>VAL_90</b><a>VAL_91</a><b>VAL_91</b><a>VAL_92</a><b>VAL_92</b><a>VAL_93</a><b>VAL_93</b><a>VAL_94</a><b>VAL_94</b><a>VAL_95</a><b>VAL_95</b><a>VAL_96</a><b>VAL_96</b><a>VAL_97</a><b>VAL_97</b><a>VAL_98</a><b>VAL_98</b><a>VAL_99</a><b>VAL_99</b><a>VAL_100</a><b>VAL_100</b><a>VAL_99</a><b>VAL_99</b><a>VAL_98</a><b>VAL_98</b><a>VAL_97</a><b>VAL_97</b><a>VAL_96</a><b>VAL_96</b><a>VAL_95</a><b>VAL_95</b><a>VAL_94</a><b>VAL_94</b><a>VAL_93</a><b>VAL_93</b><a>VAL_92</a><b>VAL_92</b><a>VAL_91</a><b>VAL_91</b><a>VAL_90</a><b>VAL_90</b><a>VAL_89</a><b>VAL_89</b><a>VAL_88</a><b>VAL_88</b><a>VAL_87</a><b>VAL_87</b><a>VAL_86</a><b>VAL_86</b><a>VAL_85</a><b>VAL_85</b><a>VAL_84</a><b>VAL_84</b><a>VAL_83</a><b>VAL_83</b><a>VAL_82</a><b>VAL_82</b><a>VAL_81</a><b>VAL_81</b><a>VAL_80</a><b>VAL_80</b><a>VAL_79</a><b>VAL_79</b><a>VAL_78</a><b>VAL_78</b><a>VAL_77</a><b>VAL_77</b><a>VAL_76</a><b>VAL_76</b><a>VAL_75</a><b>VAL_75</b><a>VAL_74</a><b>VAL_74</b><a>VAL_73</a><b>VAL_73</b><a>VAL_72</a><b>VAL_72</b><a>VAL_71</a><b>VAL_71</b><a>VAL_70</a><b>VAL_70</b><a>VAL_69</a><b>VAL_69</b><a>VAL_68</a><b>VAL_68</b><a>VAL_67</a><b>VAL_67</b><a>VAL_66</a><b>VAL_66</b><a>VAL_65</a><b>VAL_65</b><a>VAL_64</a><b>VAL_64</b><a>VAL_63</a><b>VAL_63</b><a>VAL_62</a><b>VAL_62</b><a>VAL_61</a><b>VAL_61</b><a>VAL_60</a><b>VAL_60</b><a>VAL_59</a><b>VAL_59</b><a>VAL_58</a><b>VAL_58</b><a>VAL_57</a><b>VAL_57</b><a>VAL_56</a><b>VAL_56</b><a>VAL_55</a><b>VAL_55</b><a>VAL_54</a><b>VAL_54</b><a>VAL_53</a><b>VAL_53</b><a>VAL_52</a><b>VAL_52</b><a>VAL_51</a><b>VAL_51</b><a>VAL_50</a><b>VAL_50</b><a>VAL_49</a><b>VAL_49</b><a>VAL_48</a><b>VAL_48</b><a>VAL_47</a><b>VAL_47</b><a>VAL_46</a><b>VAL_46</b><a>VAL_45</a><b>VAL_45</b><a>VAL_44</a><b>VAL_44</b><a>VAL_43</a><b>VAL_43</b><a>VAL_42</a><b>VAL_42</b><a>VAL_41</a><b>VAL_41</b><a>VAL_40</a><b>VAL_40</b><a>VAL_39</a><b>VAL_39</b><a>VAL_38</a><b>VAL_38</b><a>VAL_37</a><b>VAL_37</b><a>VAL_36</a><b>VAL_36</b><a>VAL_35</a><b>VAL_35</b><a>VAL_34</a><b>VAL_34</b><a>VAL_33</a><b>VAL_33</b><a>VAL_32</a><b>VAL_32</b><a>VAL_31</a><b>VAL_31</b><a>VAL_30</a><b>VAL_30</b><a>VAL_29</a><b>VAL_29</b><a>VAL_28</a><b>VAL_28</b><a>VAL_27</a><b>VAL_27</b><a>VAL_26</a><b>VAL_26</b><a>VAL_25</a><b>VAL_25</b><a>VAL_24</a><b>VAL_24</b><a>VAL_23</a><b>VAL_23</b><a>VAL_22</a><b>VAL_22</b><a>VAL_21</a><b>VAL_21</b><a>VAL_20</a><b>VAL_20</b><a>VAL_19</a><b>VAL_19</b><a>VAL_18</a><b>VAL_18</b><a>VAL_17</a><b>VAL_17</b><a>VAL_16</a><b>VAL_16</b><a>VAL_15</a><b>VAL_15</b><a>VAL_14</a><b>VAL_14</b><a>VAL_13</a><b>VAL_13</b><a>VAL_12</a><b>VAL_12</b><a>VAL_11</a><b>VAL_11</b><a>VAL_10</a><b>VAL_10</b><a>VAL_9</a><b>VAL_9</b><a>VAL_8</a><b>VAL_8</b><a>VAL_7</a><b>VAL_7</b><a>VAL_6</a><b>VAL_6</b><a>VAL_5</a><b>VAL_5</b><a>VAL_4</a><b>VAL_4</b><a>VAL_3</a><b>VAL_3</b><a>VAL_2</a><b>VAL_2</b><a>VAL_1</a><b>VAL_1</b><a>VAL_0</a><b>VAL_0</b></root>";

	public void testValue(Value val) {
		assertTrue(val.toString().startsWith("VAL_"));
	}

	public void testSE(QNameContext qname) {
		String localPart = qname.getLocalName();
		assertTrue(localPart.equals("root") || localPart.equals("a")
				|| localPart.equals("b"));
	}

	public void testBoundedTable80() throws SAXException, IOException,
			EXIException {
		EXIFactory factory = DefaultEXIFactory.newInstance();
		factory.setValuePartitionCapacity(80);

		ByteArrayOutputStream os = new ByteArrayOutputStream();

		// write EXI stream
		{
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();

			EXIResult exiResult = new EXIResult(factory);
			exiResult.setOutputStream(os);
			xmlReader.setContentHandler(exiResult.getHandler());

			xmlReader.parse(new InputSource(new StringReader(xml)));
		}

		// read EXI stream
		os.flush();
		byte[] bytes = os.toByteArray();
		InputStream is = new ByteArrayInputStream(bytes, 1, bytes.length - 1); // header
		EXIBodyDecoder exiDecoder = factory.createEXIBodyDecoder();
		exiDecoder.setInputStream(is);

		EventType eventType;
		while ((eventType = exiDecoder.next()) != null) {
			switch (eventType) {
			case START_DOCUMENT:
				exiDecoder.decodeStartDocument();
				break;
			case END_DOCUMENT:
				exiDecoder.decodeEndDocument();
				break;
			case START_ELEMENT:
				QNameContext se = exiDecoder.decodeStartElement();
				testSE(se);
				break;
			case START_ELEMENT_GENERIC:
				// se = exiDecoder.decodeStartElementGeneric();
				se = exiDecoder.decodeStartElement();
				testSE(se);
				break;
			case START_ELEMENT_GENERIC_UNDECLARED:
				// se = exiDecoder.decodeStartElementGenericUndeclared();
				se = exiDecoder.decodeStartElement();
				testSE(se);
				break;
			case CHARACTERS:
				Value val = exiDecoder.decodeCharacters();
				testValue(val);
				break;
			case CHARACTERS_GENERIC_UNDECLARED:
				// val= exiDecoder.decodeCharactersGenericUndeclared();
				val = exiDecoder.decodeCharacters();
				testValue(val);
				break;
			case END_ELEMENT:
				exiDecoder.decodeEndElement();
				break;
			default:
				throw new RuntimeException("Unexpected event: " + eventType);
			}
		}

	}

}
