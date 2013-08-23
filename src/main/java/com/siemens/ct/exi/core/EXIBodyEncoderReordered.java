package com.siemens.ct.exi.core;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import com.siemens.ct.exi.CodingMode;
import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.core.container.ValueAndDatatype;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.io.channel.ByteEncoderChannel;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.values.Value;

/**
 * EXI encoder for (pre-)compression streams.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.2-SNAPSHOT
 */

public class EXIBodyEncoderReordered extends AbstractEXIBodyEncoder {

	protected OutputStream os;
	protected Deflater deflater;
	protected DeflaterOutputStream deflaterOS;
	protected CodingMode codingMode;

	protected int blockValues;

	protected Value lastValue;
	protected Datatype lastDatatype;
	
	// Note: Map needs to be sorted to retrieve correct channel order (e.g., LinkedHashMap)
	protected Map<QNameContext, List<ValueAndDatatype>> channelValuesAndDatatypes;

	public EXIBodyEncoderReordered(EXIFactory exiFactory) throws EXIException {
		super(exiFactory);

		this.codingMode = exiFactory.getCodingMode();
		
		// Note: needs to be sorted map for channel order 
		channelValuesAndDatatypes = new LinkedHashMap<QNameContext, List<ValueAndDatatype>>();
	}

	@Override
	protected void initForEachRun() throws EXIException, IOException {
		super.initForEachRun();

		blockValues = 0;
		
		channelValuesAndDatatypes.clear();
	}

	protected void initBlock() {
		blockValues = 0;
		
		// re-set all channels
		this.channelValuesAndDatatypes.clear();
	}
	
	protected void addValueAndDatatype(QNameContext qnc, ValueAndDatatype vd) {
		List<ValueAndDatatype> vds = this.channelValuesAndDatatypes.get(qnc);
		if(vds == null) {
			// create new list
			vds = new ArrayList<ValueAndDatatype>();
			// add to map (sorted due to linked hashmap)
			channelValuesAndDatatypes.put(qnc, vds);
		} 
		vds.add(vd);
	}

	public void setOutputStream(OutputStream os) throws EXIException,
			IOException {
		this.os = os;

		// setup new data-stream
		channel = new ByteEncoderChannel(getStream());

	}

	public void setOutputChannel(EncoderChannel encoderChannel) {
		this.channel = encoderChannel;
		this.os = channel.getOutputStream();
	}

	@Override
	protected boolean isTypeValid(Datatype datatype, Value value) {
		lastDatatype = datatype;
		lastValue = value;
		return super.isTypeValid(datatype, value);
	}

	@Override
	protected void writeValue(QNameContext valueContext) throws IOException {
		addValueAndDatatype(valueContext, new ValueAndDatatype(
				lastValue, lastDatatype));

		// new block goes directly after value
		if (++blockValues == exiFactory.getBlockSize()) {
			// blockValues larger than set blockSize
			// System.out.println("new block " + blockValues + " after " +
			// valueContext + " = '" + lastValue + "'");

			// close this block and setup new one
			closeBlock();
			initBlock();
			channel = new ByteEncoderChannel(getStream());
		}

	}

	protected OutputStream getStream() {
		if (codingMode == CodingMode.COMPRESSION) {
			// reuse deflater
			if (deflater == null) {
				deflater = new Deflater(codingMode.getDeflateLevel(), true);
			} else {
				deflater.reset();
			}
			deflaterOS = new DeflaterOutputStream(os, deflater);
			return deflaterOS;
		} else {
			assert (codingMode == CodingMode.PRE_COMPRESSION);
			return os;
		}
	}

	protected void closeBlock() throws IOException {
		/*
		 * Some EXI events have zero-byte representations and are not explicitly
		 * represented in the EXI stream. If a sequence of these events occurs
		 * following the final block, implementations must take care to avoid
		 * allocating an extra, empty block for the implicit events at the end
		 * of the stream.
		 */
		if (channel.getLength() == 0) {
			// empty block -> no deflate stream ...
		}
		/*
		 * If the block contains at most 100 values, the block will contain only
		 * 1 compressed stream containing the structure channel followed by all
		 * of the value channels. The order of the value channels within the
		 * compressed stream is defined by the order in which the first value in
		 * each channel occurs in the EXI event sequence.
		 */
		else if (blockValues <= Constants.MAX_NUMBER_OF_VALUES) {
			// 1. structure stream already written
			// 2. value channels in order
			Iterator<QNameContext> iterCh = this.channelValuesAndDatatypes.keySet().iterator();
			while(iterCh.hasNext()) {
				QNameContext contextOrder = iterCh.next();
				List<ValueAndDatatype> lvd = channelValuesAndDatatypes.get(contextOrder);
				for (ValueAndDatatype vd : lvd) {
					typeEncoder.isValid(vd.datatype, vd.value);
					typeEncoder.writeValue(contextOrder, channel,
							stringEncoder);
				}
			}

			finalizeStream();
		}
		/*
		 * If the block contains more than 100 values, the first compressed
		 * stream contains only the structure channel. The second compressed
		 * stream contains all value channels that contain less than 100 values.
		 * And the remaining compressed streams each contain only one channel,
		 * each having more than 100 values. The order of the value channels
		 * within the second compressed stream is defined by the order in which
		 * the first value in each channel occurs in the EXI event sequence.
		 * Similarly, the order of the compressed streams following the second
		 * compressed stream in the block is defined by the order in which the
		 * first value of the channel inside each compressed stream occurs in
		 * the EXI event sequence.
		 */
		else {
			// structure stream first (as a single stream)
			// --> finish first deflate structure stream
			finalizeStream();

			// all value channels that contain less (and equal) than 100 values
			// (as a single stream )
			EncoderChannel leq100 = new ByteEncoderChannel(getStream());
			boolean wasThereLeq100 = false;

			Iterator<QNameContext> iterCh = this.channelValuesAndDatatypes.keySet().iterator();
			while(iterCh.hasNext()) {
				QNameContext contextOrder = iterCh.next();
				List<ValueAndDatatype> lvd = channelValuesAndDatatypes.get(contextOrder);
				if (lvd.size() <= Constants.MAX_NUMBER_OF_VALUES) {
					for (ValueAndDatatype vd : lvd) {
						typeEncoder.isValid(vd.datatype, vd.value);
						typeEncoder.writeValue(contextOrder, leq100,
								stringEncoder);
					}
					wasThereLeq100 = true;
				}
			}

			if (wasThereLeq100) {
				finalizeStream();
			}

			// all value channels having more than 100 values
			iterCh = this.channelValuesAndDatatypes.keySet().iterator();
			while(iterCh.hasNext()) {
				QNameContext contextOrder = iterCh.next();
				List<ValueAndDatatype> lvd = channelValuesAndDatatypes.get(contextOrder);
				if (lvd.size() > Constants.MAX_NUMBER_OF_VALUES) {
					// create stream
					EncoderChannel gre100 = new ByteEncoderChannel(getStream());
					for (ValueAndDatatype vd : lvd) {
						typeEncoder.isValid(vd.datatype, vd.value);
						typeEncoder.writeValue(contextOrder, gre100,
								stringEncoder);
					}
					// finish stream
					finalizeStream();

				}
			}
		}
	}

	protected void finalizeStream() throws IOException {
		if (codingMode == CodingMode.COMPRESSION) {
			deflaterOS.finish();
		}
		// else nothing to do
	}

	@Override
	public void flush() throws IOException {
		// close remaining block (if any)
		closeBlock();

		// finalize document
		os.flush();
	}

}
