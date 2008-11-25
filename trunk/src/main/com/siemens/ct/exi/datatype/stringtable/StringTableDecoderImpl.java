/*
 * Copyright (C) 2007, 2008 Siemens AG
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

package com.siemens.ct.exi.datatype.stringtable;

import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;

import com.siemens.ct.exi.Constants;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.2.20080915
 */

public class StringTableDecoderImpl extends AbstractStringTable implements
		StringTableDecoder {
	protected StringTablePartitionDecoder uriPartition;
	protected Map<String, StringTablePartitionDecoder> prefixPartitions;
	protected Map<String, StringTablePartitionDecoder> localNamePartitions;

	protected Map<String, HashMap<String, StringTablePartitionDecoder>> localValuePartitions;
	protected StringTablePartitionDecoder globalValuePartition;

	/**
	 * The constructor will set all tables to their initial states. This
	 * includes loading the tables with inital values.
	 */
	public StringTableDecoderImpl(boolean isSchemaInformed) {
		uriPartition = getNewPartition();
		prefixPartitions = new HashMap<String, StringTablePartitionDecoder>();
		localNamePartitions = new HashMap<String, StringTablePartitionDecoder>();
		localValuePartitions = new HashMap<String, HashMap<String, StringTablePartitionDecoder>>();
		globalValuePartition = getNewPartition();

		// URI
		initURI(uriPartition, isSchemaInformed);

		// Prefix: "", xml, xsi
		prefixPartitions.put(Constants.EMPTY_STRING, getNewPartition());
		prefixPartitions.put(XMLConstants.XML_NS_URI, getNewPartition());
		prefixPartitions.put(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,
				getNewPartition());
		initPrefixEmpty(prefixPartitions.get(Constants.EMPTY_STRING));
		initPrefixXML(prefixPartitions.get(XMLConstants.XML_NS_URI));
		initPrefixXSI(prefixPartitions
				.get(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI));

		// LocalName: xml, xsi, xsd
		localNamePartitions.put(XMLConstants.XML_NS_URI, getNewPartition());
		localNamePartitions.put(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,
				getNewPartition());
		localNamePartitions.put(XMLConstants.W3C_XML_SCHEMA_NS_URI,
				getNewPartition());
		initLocalNameXML(localNamePartitions.get(XMLConstants.XML_NS_URI));
		initLocalNameXSI(localNamePartitions
				.get(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI));
		initLocalNameXSD(localNamePartitions
				.get(XMLConstants.W3C_XML_SCHEMA_NS_URI));
	}

	protected StringTablePartitionDecoder getNewPartition() {
		return new StringTablePartitionDecoderImpl();
	}

	protected int getID(StringTablePartitionEncoder partition, String key) {
		return (partition == null) ? Constants.NOT_FOUND : partition
				.getIndex(key);
	}

	protected StringTablePartitionDecoder getPartition(
			Map<String, StringTablePartitionDecoder> partitions, String key) {
		StringTablePartitionDecoder partition = partitions.get(key);

		if (partition == null) {
			partitions.put(key, (partition = getNewPartition()));
		}

		return partition;
	}

	/*
	 * ##########################################################################
	 * #### #### StringTableDecoder Interface
	 * ###################################
	 * ###########################################
	 */

	public String getURIValue(int id) {
		return uriPartition.getValue(id);
	}

	public String getPrefixValue(String uri, int id) {
		return prefixPartitions.get(uri).getValue(id);
	}

	public String getLocalNameValue(String uri, int id) {
		return localNamePartitions.get(uri).getValue(id);
	}

	public String getLocalValue(String uri, String localName, int id) {
		assert (localValuePartitions.get(uri).size() > 0);
		assert (localValuePartitions.get(uri).get(localName).getSize() > 0);

		return localValuePartitions.get(uri).get(localName).getValue(id);
	}

	public String getGlobalValue(int id) {
		return globalValuePartition.getValue(id);
	}

	/*
	 * ##########################################################################
	 * #### #### StringTable Interface
	 * ##########################################
	 * ####################################
	 */

	public void addURI(String uri) {
		uriPartition.add(uri);
	}

	public int getURITableSize() {
		return uriPartition.getSize();
	}

	public void addPrefix(String uri, String prefix) {
		getPartition(prefixPartitions, uri).add(prefix);
	}

	public int getPrefixTableSize(String uri) {
		return getPartition(prefixPartitions, uri).getSize();
	}

	public void addLocalName(String uri, String name) {
		getPartition(localNamePartitions, uri).add(name);
	}

	public int getLocalNameTableSize(String uri) {
		return getPartition(localNamePartitions, uri).getSize();
	}

	public void addLocalValue(String uri, String localName, String value) {
		// check URI section first
		HashMap<String, StringTablePartitionDecoder> uriSection = localValuePartitions
				.get(uri);

		if (uriSection == null) {
			localValuePartitions
					.put(
							uri,
							(uriSection = new HashMap<String, StringTablePartitionDecoder>()));
		}

		getPartition(uriSection, localName).add(value);
	}

	public int getLocalValueTableSize(String uri, String localName) {
		// check URI section first
		HashMap<String, StringTablePartitionDecoder> uriSection = localValuePartitions
				.get(uri);

		return (uriSection == null ? 0 : getPartition(uriSection, localName)
				.getSize());
	}

	public void addGlobalValue(String value) {
		globalValuePartition.add(value);
	}

	public int getGlobalValueTableSize() {
		return globalValuePartition.getSize();
	}

}
