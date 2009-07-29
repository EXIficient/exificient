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

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20080915
 */

public class StringTableDecoderImpl extends AbstractStringTable implements
		StringTableDecoder {
	protected StringTablePartitionDecoder uriPartition;
	protected Map<String, StringTablePartitionDecoder> prefixPartitions;
	protected Map<String, StringTablePartitionDecoder> localNamePartitions;

	protected Map<String, HashMap<String, StringTablePartitionArrayDecoder>> localValuePartitions;
	protected StringTablePartitionArrayDecoder globalValuePartition;

	/**
	 * The constructor will set all tables to their initial states. This
	 * includes loading the tables with inital values.
	 */
	public StringTableDecoderImpl(boolean isSchemaInformed) {
		uriPartition = getNewPartition();
		prefixPartitions = new HashMap<String, StringTablePartitionDecoder>();
		localNamePartitions = new HashMap<String, StringTablePartitionDecoder>();
		localValuePartitions = new HashMap<String, HashMap<String, StringTablePartitionArrayDecoder>>();
		globalValuePartition = getNewArrayPartition();

		// initialize
		initPartitions(isSchemaInformed);
	}

	protected StringTablePartitionDecoder getNewPartition() {
		return new StringTablePartitionDecoderImpl();
	}
	
	protected StringTablePartitionArrayDecoder getNewArrayPartition() {
		return new StringTablePartitionArrayDecoderImpl();
	}

//	protected int getID(StringTablePartitionEncoder partition, String key) {
//		return (partition == null) ? Constants.NOT_FOUND : partition
//				.getIndex(key);
//	}

	protected StringTablePartitionDecoder getPartition(
			Map<String, StringTablePartitionDecoder> partitions, String key) {
		StringTablePartitionDecoder partition = partitions.get(key);

		if (partition == null) {
			partitions.put(key, (partition = getNewPartition()));
		}

		return partition;
	}
	
	protected StringTablePartitionArrayDecoder getArrayPartition(
			Map<String, StringTablePartitionArrayDecoder> partitions, String key) {
		StringTablePartitionArrayDecoder partition = partitions.get(key);

		if (partition == null) {
			partitions.put(key, (partition = getNewArrayPartition()));
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

	public char[] getLocalValue(String uri, String localName, int id) {
		assert (localValuePartitions.get(uri).size() > 0);
		assert (localValuePartitions.get(uri).get(localName).getSize() > 0);

		return localValuePartitions.get(uri).get(localName).getValue(id);
	}

	public char[] getGlobalValue(int id) {
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

	public void addValue(String uri, String localName, char[] value) {
		// check URI section first
		HashMap<String, StringTablePartitionArrayDecoder> uriSection = localValuePartitions
				.get(uri);

		if (uriSection == null) {
			localValuePartitions
					.put(
							uri,
							(uriSection = new HashMap<String, StringTablePartitionArrayDecoder>()));
		}
		//	local table
		getArrayPartition(uriSection, localName).add(value);
		//	global table
		globalValuePartition.add(value);
	}

	public int getLocalValueTableSize(String uri, String localName) {
		// check URI section first
		HashMap<String, StringTablePartitionArrayDecoder> uriSection = localValuePartitions
				.get(uri);

		return (uriSection == null ? 0 : getArrayPartition(uriSection, localName)
				.getSize());
	}

	public int getGlobalValueTableSize() {
		return globalValuePartition.getSize();
	}

}
