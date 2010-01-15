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

package com.siemens.ct.exi.core;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.exceptions.ErrorHandler;
import com.siemens.ct.exi.grammar.event.EventType;

/**
 * Encoder for SELF_CONTAINED elements
 * 
 * <p>
 * All productions of the form LeftHandSide : SC Fragment are evaluated as
 * follows:
 * </p>
 * <ol>
 * <li>Save the string table, grammars and any implementation-specific state
 * learned while processing this EXI Body.</li>
 * <li>Initialize the string table, grammars and any implementation-specific
 * state learned while processing this EXI Body to the state they held just
 * prior to processing this EXI Body.</li>
 * <li>Skip to the next byte-aligned boundary in the stream if it is not already
 * at such a boundary.</li>
 * <li>Let qname be the qname of the SE event immediately preceding this SC
 * event.</li>
 * <li>Let content be the sequence of events following this SC event that match
 * the grammar for element qname, up to and including the terminating EE event.</li>
 * <li>Evaluate the sequence of events (SD, SE(qname), content, ED) according to
 * the Fragment grammar.</li>
 * <li>Skip to the next byte-aligned boundary in the stream if it is not already
 * at such a boundary.</li>
 * <li>Restore the string table, grammars and implementation-specific state
 * learned while processing this EXI Body to that saved in step 1 above.</li>
 * </ol>
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20090414
 */

public class EXIEncoderInOrderSC extends EXIEncoderInOrder {

	protected EXIEncoderInOrderSC scEncoder;

	public EXIEncoderInOrderSC(EXIFactory exiFactory) {
		super(exiFactory);
	}

	@Override
	protected void initForEachRun() throws EXIException, IOException {
		super.initForEachRun();

		// clear possibly remaining encoder
		scEncoder = null;
	}

	@Override
	public void setOutput(OutputStream os, boolean exiBodyOnly)
			throws EXIException {
		if (scEncoder == null) {
			super.setOutput(os, exiBodyOnly);
		} else {
			scEncoder.setOutput(os, exiBodyOnly);
		}
	}

	@Override
	public void setErrorHandler(ErrorHandler errorHandler) {
		if (scEncoder == null) {
			super.setErrorHandler(errorHandler);
		} else {
			scEncoder.setErrorHandler(errorHandler);
		}
	}

	@Override
	public void encodeStartDocument() throws EXIException, IOException {
		if (scEncoder == null) {
			super.encodeStartDocument();
		} else {
			scEncoder.encodeStartDocument();
		}
	}

	@Override
	public void encodeEndDocument() throws EXIException, IOException {
		if (scEncoder == null) {
			super.encodeEndDocument();
		} else {
			scEncoder.encodeEndDocument();
		}
	}

	@Override
	public void encodeStartElement(String uri, String localName, String prefix)
			throws EXIException, IOException {
		// business as usual
		super.encodeStartElement(uri, localName, prefix);
		// start SC fragment ?
		QName qname = elementContext.qname;
		if (exiFactory.isSelfContainedElement(qname)) {
			int ec2 = currentRule.get2ndLevelEventCode(EventType.SELF_CONTAINED, fidelityOptions);
			encode2ndLevelEventCode(ec2);
			
			System.out.println(">> SC " + qname);
			//	TODO duplicate factory
			boolean fragment = exiFactory.isFragment();
			if (fragment) {
				scEncoder = (EXIEncoderInOrderSC) exiFactory.createEXIEncoder();
			} else {
				exiFactory.setFragment(true);
				scEncoder = (EXIEncoderInOrderSC) exiFactory.createEXIEncoder();
				exiFactory.setFragment(false);
			}
			// scEncoder.setOutput(os, true);
			scEncoder.os = this.os; // needs to be unequal null
			scEncoder.channel = this.channel;
			scEncoder.setErrorHandler(this.errorHandler);

			// Skip to the next byte-aligned boundary in the stream if it is not
			// already at such a boundary
			this.channel.align();

			// Evaluate the sequence of events (SD, SE(qname), content, ED)
			// according to the Fragment grammar
			scEncoder.encodeStartDocument();
			// TODO Fragment/Document grammar is set when startDoc is called!!
			scEncoder.encodeStartElementNoSC(uri, localName, prefix);

			// from now on content events are forwarded to the scEncoder
		}
	}
	
	protected void encodeStartElementNoSC(String uri, String localName, String prefix)
	throws EXIException, IOException {
		super.encodeStartElement(uri, localName, prefix);
	}

	@Override
	public void encodeEndElement() throws EXIException, IOException {
		if (scEncoder == null) {
			super.encodeEndElement();
		} else {
			QName qname = elementContext.qname;
			if (exiFactory.isSelfContainedElement(qname)) {
				// inner EE
				scEncoder.encodeEndElement();
				// end SC fragment
				scEncoder.encodeEndDocument();
				// Skip to the next byte-aligned boundary in the stream if it is
				// not already at such a boundary
				this.channel.align();
				// indicate that SC portion is over
				scEncoder = null;
				System.out.println("<< SC " + qname);
			}

			// outer EE
			// super.encodeEndElement();
			
			// NOTE: NO outer EE
			// Spec says "Evaluate the sequence of events (SD, SE(qname), content, ED) .."
			// e.g., "sc" is self-Contained element
			// Sequence: <sc>foo</sc>
			// --> SE(sc) --> SC --> SD --> SE(sc) --> CH --> EE --> ED
			// content == SE(sc) --> CH --> EE
			super.popElement();
		}
	}

	@Override
	public void encodeAttribute(String uri, String localName, String prefix,
			String value) throws EXIException, IOException {
		if (scEncoder == null) {
			super.encodeAttribute(uri, localName, prefix, value);
		} else {
			scEncoder.encodeAttribute(uri, localName, prefix, value);
		}
	}

	@Override
	public void encodeNamespaceDeclaration(String uri, String prefix)
			throws EXIException, IOException {
		if (scEncoder == null) {
			super.encodeNamespaceDeclaration(uri, prefix);
		} else {
			scEncoder.encodeNamespaceDeclaration(uri, prefix);
		}
	}

	@Override
	public void encodeXsiNil(String nil) throws EXIException, IOException {
		if (scEncoder == null) {
			super.encodeXsiNil(nil);
		} else {
			scEncoder.encodeXsiNil(nil);
		}
	}

	@Override
	public void encodeXsiType(String xsiTypeRaw) throws EXIException,
			IOException {
		if (scEncoder == null) {
			super.encodeXsiType(xsiTypeRaw);
		} else {
			scEncoder.encodeXsiType(xsiTypeRaw);
		}
	}

	@Override
	public void encodeCharacters(String chars) throws EXIException, IOException {
		if (scEncoder == null) {
			super.encodeCharacters(chars);
		} else {
			scEncoder.encodeCharacters(chars);
		}
	}

	@Override
	public void encodeDocType(String name, String publicID, String systemID,
			String text) throws EXIException, IOException {
		if (scEncoder == null) {
			super.encodeDocType(name, publicID, systemID, text);
		} else {
			scEncoder.encodeDocType(name, publicID, systemID, text);
		}
	}

	@Override
	public void encodeEntityReference(String name) throws EXIException,
			IOException {
		if (scEncoder == null) {
			super.encodeEntityReference(name);
		} else {
			scEncoder.encodeEntityReference(name);
		}
	}

	@Override
	public void encodeComment(char[] ch, int start, int length)
			throws EXIException, IOException {
		if (scEncoder == null) {
			super.encodeComment(ch, start, length);
		} else {
			scEncoder.encodeComment(ch, start, length);
		}
	}

	@Override
	public void encodeProcessingInstruction(String target, String data)
			throws EXIException, IOException {
		if (scEncoder == null) {
			super.encodeProcessingInstruction(target, data);
		} else {
			scEncoder.encodeProcessingInstruction(target, data);
		}
	}

	// final class SCEncodeState {
	// final TypeEncoder typeEncoder;
	// final Map<QName, StartElement> runtimeElements;
	// final List<RuntimeURIEntry> runtimeURIEntries;
	// final NamespaceSupport namespaces;
	// public SCEncodeState(TypeEncoder typeEncoder, Map<QName, StartElement>
	// runtimeElements, List<RuntimeURIEntry> runtimeURIEntries,
	// NamespaceSupport namespaces) {
	// this.typeEncoder = typeEncoder;
	// this.runtimeElements = runtimeElements;
	// this.runtimeURIEntries = runtimeURIEntries;
	// this.namespaces = namespaces;
	// }
	// }
	//	
	// // selfContained fragment states
	// protected List<SCEncodeState> scStates;
	//
	// public EXIEncoderInOrderSC(EXIFactory exiFactory) {
	// super(exiFactory);
	//
	// // selfContained
	// scStates = new ArrayList<SCEncodeState>();
	// }
	//	
	// @Override
	// protected void initForEachRun() throws EXIException, IOException {
	// super.initForEachRun();
	//
	// // clear possibly remaining states
	// scStates.clear();
	// }
	//
	// @Override
	// public void encodeStartElement(String uri, String localName, String
	// prefix)
	// throws EXIException, IOException {
	// assert (fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_SC));
	// // business as usual
	// super.encodeStartElement(uri, localName, prefix);
	// // start SC fragment
	// QName qname = elementContext.qname;
	// if (exiFactory.isSelfContainedElement(qname)) {
	// // System.out.println("SC Start " + qname);
	//			
	// // SC Fragment
	// int ec2 = currentRule.get2ndLevelEventCode(EventType.SELF_CONTAINED,
	// fidelityOptions);
	// encode2ndLevelEventCode(ec2);
	//			
	// // 1. Save the string table, grammars and any
	// // implementation-specific state learned while processing this EXI
	// // Body
	// SCEncodeState state = new SCEncodeState(typeEncoder, runtimeElements,
	// runtimeURIEntries, namespaces);
	// scStates.add(state);
	//
	// // 2. Initialize the string table, grammars and any
	// // implementation-specific state learned while processing this EXI
	// // Body to the state they held just prior to processing this EXI
	// // Body.
	// this.typeEncoder = exiFactory.createTypeEncoder();
	// this.runtimeElements = new HashMap<QName, StartElement>();
	// this.runtimeURIEntries = new ArrayList<RuntimeURIEntry>();
	// this.namespaces = new NamespaceSupport();
	// uriContext = runtimeURIEntries.get(0);
	//			
	// this.namespaces.pushContext(); // necessary for popping later on
	//
	// // 3. Skip to the next byte-aligned boundary in the stream if it is
	// // not already at such a boundary.
	// channel.align();
	//
	// // 4. Let qname be the qname of the SE event immediately preceding
	// // this SC event.
	//
	// // 5. Let content be the sequence of events following this SC event
	// // that match the grammar for element qname, up to and including the
	// // terminating EE event.
	//
	// // 6. Evaluate the sequence of events (SD, SE(qname), content, ED)
	// // according to the Fragment grammar.
	// Grammar grammarSC = exiFactory.getGrammar();
	// Rule scFragmentRule = grammarSC.getBuiltInFragmentGrammar();
	// EventInformation ei =
	// scFragmentRule.lookForEvent(EventType.START_DOCUMENT);
	//
	// // Note: no EventCode needs to be written since there is only
	// // one choice
	// if (ei == null) {
	// throw new EXIException("No EXI Event found for startDocument");
	// }
	//
	// // update current rule
	// currentRule = ei.next;
	// // create new stack item & push it
	// pushElementContext(new ElementContext(null, currentRule));
	//			
	// // encode "inner" element once again
	// super.encodeStartElement(uri, localName, prefix);
	//
	// // System.out.println("SC _end_" );
	// }
	// }
	//	
	// @Override
	// public void encodeEndElement() throws EXIException, IOException {
	// QName qname = elementContext.qname;
	// if (exiFactory.isSelfContainedElement(qname)) {
	// System.out.println("SC End " + qname);
	// // inner endElement
	// super.encodeEndElement();
	// // inner endDocument
	// EventInformation ei = currentRule.lookForEvent(EventType.END_DOCUMENT);
	// encode1stLevelEventCode(ei.getEventCode());
	// this.popElement();
	//
	// // 7. Skip to the next byte-aligned boundary in the stream if it is
	// // not already at such a boundary.
	// channel.align();
	//			
	// // 8. Restore the string table, grammars and implementation-specific
	// // state learned while processing this EXI Body to that saved in
	// // step 1 above.
	// assert(scStates.size() > 0);
	// SCEncodeState state = scStates.remove(scStates.size()-1);
	// this.typeEncoder = state.typeEncoder;
	// this.runtimeElements = state.runtimeElements;
	// this.runtimeURIEntries = state.runtimeURIEntries;
	// this.namespaces = state.namespaces;
	//			
	// }
	// // usual endElement
	// super.encodeEndElement();
	// }
	//
	// @Override
	// public int encodeStartFragmentSelfContained(String uri, String localName,
	// String prefix) throws EXIException {
	// throw new RuntimeException("[EXI] SC");
	// // try {
	// // int skipBytesSC = -1;
	// //
	// // // SC Fragment
	// // int ec2 = currentRule.get2ndLevelEventCode(
	// // EventType.SELF_CONTAINED, fidelityOptions);
	// //
	// // if (ec2 == Constants.NOT_FOUND) {
	// // // throw error
	// // throw new EXIException(
	// //
	// "SelfContained fragments need to be supported by EXI's Options. Please revise your configuration.");
	// // } else {
	// //
	// // this.encode2ndLevelEventCode(ec2);
	// //
	// // // 1. Save the string table, grammars, namespace prefixes and
	// // // any
	// // // implementation-specific state learned while processing this
	// // // EXI
	// // // Body.
	// // // 2. Initialize the string table, grammars, namespace prefixes
	// // // and
	// // // any implementation-specific state learned while processing
	// // // this
	// // // EXI Body to the state they held just prior to processing this
	// // // EXI
	// // // Body.
	// // // 3. Skip to the next byte-aligned boundary in the stream.
	// // channel.flush();
	// // // block.skipToNextByteBoundary();
	// //
	// // // TODO byte position
	// //
	// // // if (block.bytePositionSupported()) {
	// // // skipBytesSC = block.getBytePosition();
	// // // }
	// //
	// // // string tables
	// // // TypeEncoder te = this.block.getTypeEncoder();
	// // // scStringTables.add(typeEncoder.getStringTable());
	// // // TODO create *just* string table and not whole TypeEncoder
	// // // again
	// // // typeEncoder.setStringTable(exiFactory.createTypeEncoder()
	// // // .getStringTable());
	// // // runtime-rules
	// // scRuntimeDispatchers.add(this.runtimeDispatcher);
	// // this.runtimeDispatcher = new HashMap<String, Map<String, Rule>>();
	// // // TODO namespace prefixes
	// //
	// // // 4. Let qname be the qname of the SE event immediately
	// // // preceding
	// // // this SC event.
	// // // 5. Let content be the sequence of events following this SC
	// // // event
	// // // that match the grammar for element qname, up to and including
	// // // the
	// // // terminating EE event.
	// // // 6. Evaluate the sequence of events (SD, SE(qname), content,
	// // // ED)
	// // // according to the Fragment grammar.
	// // replaceRuleAtTheTop(grammar.getBuiltInFragmentGrammar());
	// // replaceRuleAtTheTop(currentRule.lookFor(eventSD).next);
	// // encodeStartElement(uri, localName, prefix);
	// // }
	// //
	// // return skipBytesSC;
	// // } catch (IOException e) {
	// // throw new EXIException(e);
	// // }
	//
	// }
	//
	// @Override
	// public void encodeEndFragmentSelfContained() throws EXIException {
	// throw new RuntimeException("[EXI] SC");
	// // try {
	// // // close SC fragment
	// // EventInformation ei = currentRule.lookFor(eventED);
	// //
	// // if (ei != null) {
	// // // encode EventCode
	// // this.encode1stLevelEventCode(ei.getEventCode());
	// // } else {
	// // throw new EXIException("No EXI Event found for endDocument");
	// // }
	// //
	// // popElementContext();
	// // popElementRule();
	// //
	// // // 7. Restore the string table, grammars, namespace prefixes and
	// // // implementation-specific state learned while processing this EXI
	// // // Body to that saved in step 1 above.
	// // //TypeEncoder te = this.block.getTypeEncoder();
	// // //
	// // typeEncoder.setStringTable(scStringTables.remove(scStringTables.size()
	// // - 1));
	// // this.runtimeDispatcher = scRuntimeDispatchers
	// // .remove(scRuntimeDispatchers.size() - 1);
	// // } catch (IOException e) {
	// // throw new EXIException(e);
	// // }
	// }
}
