/*
 * Copyright (C) 2007-2015 Siemens AG
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

package com.siemens.ct.exi;

import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammars.Grammars;

/**
 * The schemaId option may be used to identify the schema information used for
 * processing the EXI body.
 * 
 * <p>
 * When the "schemaId" element in the EXI options document contains the xsi:nil
 * attribute with its value set to true, no schema information is used for
 * processing the EXI body (i.e. a schema-less EXI stream). When the value of
 * the "schemaId" element is empty, no user defined schema information is used
 * for processing the EXI body; however, the built-in XML schema types are
 * available for use in the EXI body. When the schemaId option is absent (i.e.,
 * undefined), no statement is made about the schema information used to encode
 * the EXI body and this information MUST be communicated out of band. The EXI
 * specification does not dictate the syntax or semantics of other values
 * specified in this field. An example schemaId scheme is the use of URI that is
 * apt for globally identifying schema resources on the Web. The parties
 * involved in the exchange are free to agree on the scheme of schemaId field
 * that is appropriate for their use to uniquely identify the schema information
 * </p>
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.5-SNAPSHOT
 */

public interface SchemaIdResolver {

	/**
	 * Resolves schemaId to an actual grammar representation.
	 * 
	 * @param schemaId
	 * @return Grammars
	 * @throws EXIException
	 */
	public Grammars resolveSchemaId(String schemaId) throws EXIException;

}
