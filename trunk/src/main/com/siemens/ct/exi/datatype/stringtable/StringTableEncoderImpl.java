/*
 * Copyright (C) 2007-2009 Siemens AG
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

public class StringTableEncoderImpl extends AbstractStringTable implements
		StringTableEncoder {
	protected StringTablePartitionEncoder uriPartition;
	protected Map<String, StringTablePartitionEncoder> prefixPartitions;
	protected Map<String, StringTablePartitionEncoder> localNamePartitions;

	protected Map<String, HashMap<String, StringTablePartitionEncoder>> localValuePartitions;
	protected StringTablePartitionEncoder globalValuePartition;

	/**
	 * The constructor will set all tables to their initial states. This
	 * includes loading the partitions with inital values.
	 */
	public StringTableEncoderImpl(boolean isSchemaInformed) {
		uriPartition = getNewPartition();
		prefixPartitions = new HashMap<String, StringTablePartitionEncoder>();
		localNamePartitions = new HashMap<String, StringTablePartitionEncoder>();
		localValuePartitions = new HashMap<String, HashMap<String, StringTablePartitionEncoder>>();
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

	protected StringTablePartitionEncoder getNewPartition() {
		return new StringTablePartitionEncoderImpl();
	}

	protected int getID(StringTablePartitionEncoder partition, String key) {
		return (partition == null) ? Constants.NOT_FOUND : partition
				.getIndex(key);
	}

	protected StringTablePartitionEncoder getPartition(
			Map<String, StringTablePartitionEncoder> partitions, String key) {
		StringTablePartitionEncoder partition = partitions.get(key);

		if (partition == null) {
			partitions.put(key, (partition = getNewPartition()));
		}

		return partition;
	}

	/*
	 * ##########################################################################
	 * #### #### StringTableEncoder Interface
	 * ###################################
	 * ###########################################
	 */

	public int getURIID(String uri) {
		return uriPartition.getIndex(uri);
	}

	public int getPrefixID(String uri, String prefix) {
		return getID(prefixPartitions.get(uri), prefix);
	}

	public int getLocalNameID(String uri, String name) {
		return getID(localNamePartitions.get(uri), name);
	}

	public int getLocalValueID(String uri, String localName, String value) {
		// check URI section first
		HashMap<String, StringTablePartitionEncoder> uriSection = localValuePartitions
				.get(uri);

		return (uriSection == null ? Constants.NOT_FOUND : getID(uriSection
				.get(localName), value));
	}

	public int getGlobalValueID(String value) {
		return globalValuePartition.getIndex(value);
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
		HashMap<String, StringTablePartitionEncoder> uriSection = localValuePartitions
				.get(uri);

		if (uriSection == null) {
			localValuePartitions
					.put(
							uri,
							(uriSection = new HashMap<String, StringTablePartitionEncoder>()));
		}

		getPartition(uriSection, localName).add(value);
	}

	public int getLocalValueTableSize(String uri, String localName) {
		// check URI section first
		HashMap<String, StringTablePartitionEncoder> uriSection = localValuePartitions
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
