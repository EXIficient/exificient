package com.siemens.ct.exi.core;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.CodingMode;
import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.core.container.ContextContainer;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.io.channel.ByteEncoderChannel;
import com.siemens.ct.exi.io.channel.EncoderChannel;

public class EXIEncoderReordered extends AbstractEXIEncoder {

	protected DeflaterOutputStream deflater;
	protected CodingMode codingMode;

	protected int blockValues;

	protected String lastValue;
	protected Datatype lastDatatype;

	protected List<QName> contextOrders;
	protected Map<QName, ContextContainer> contexts;

	public EXIEncoderReordered(EXIFactory exiFactory) {
		super(exiFactory);

		contextOrders = new ArrayList<QName>();
		contexts = new HashMap<QName, ContextContainer>();
	}

	@Override
	protected void initForEachRun() throws EXIException, IOException {
		super.initForEachRun();

		blockValues = 0;
		contextOrders.clear();
		contexts.clear();
	}

	@Override
	public void setOutput(OutputStream os, boolean exiBodyOnly)
			throws EXIException {
		super.setOutput(os, exiBodyOnly);
		this.codingMode = exiFactory.getCodingMode();

		channel = new ByteEncoderChannel(getStream());
	}

	@Override
	protected boolean isTypeValid(Datatype datatype, String value) {
		lastDatatype = datatype;
		lastValue = value;
		return super.isTypeValid(datatype, value);
	}

	protected void updateContextValue(QName valueContext, String value, Datatype datatype) {
		ContextContainer cc = contexts.get(valueContext);
		if(cc == null) {
			cc = new ContextContainer();
			contexts.put(valueContext, cc);
			contextOrders.add(valueContext);
		}
		
		cc.addValue(value, datatype);
		blockValues++;
	}

	@Override
	protected void writeValue(QName valueContext) throws IOException {
		updateContextValue(valueContext, lastValue, lastDatatype);
	}

	protected OutputStream getStream() {
		if (codingMode == CodingMode.COMPRESSION) {
			deflater = new DeflaterOutputStream(os, new Deflater(
					codingMode.getDeflateLevel(), true));
			return deflater;
		} else {
			assert(codingMode == CodingMode.PRE_COMPRESSION);
			return os;
		}
	}

	protected void finalizeStream() throws IOException {
		if (codingMode == CodingMode.COMPRESSION) {
			deflater.finish();
		}
		// else nothing to do
	}

	@Override
	public void flush() throws IOException {
		/*
		 * If the block contains at most 100 values, the block will contain only
		 * 1 compressed stream containing the structure channel followed by all
		 * of the value channels. The order of the value channels within the
		 * compressed stream is defined by the order in which the first value in
		 * each channel occurs in the EXI event sequence.
		 */
		if (blockValues <= Constants.MAX_NUMBER_OF_VALUES) {
			// 1. structure stream already written
			// 2. value channels in order
			for (QName contextOrder : contextOrders) {
//				// updating to right context
//				elementContext = contextOrder;
				ContextContainer cc = contexts.get(contextOrder);
				List<String> values = cc.getValues();
				List<Datatype> valueDatatypes = cc.getValueDatatypes();
				for (int i = 0; i < values.size(); i++) {
					typeEncoder.isValid(valueDatatypes.get(i), values.get(i));
					typeEncoder.writeValue(contextOrder, channel);
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
			for (QName contextOrder : contextOrders) {
//				// updating to right context
//				elementContext = contextOrder;
				ContextContainer cc = contexts.get(contextOrder);
				List<String> values = cc.getValues();
				if (values.size() <= Constants.MAX_NUMBER_OF_VALUES) {
					List<Datatype> valueDatatypes = cc.getValueDatatypes();
					for (int i = 0; i < values.size(); i++) {
						typeEncoder.isValid(valueDatatypes.get(i), values.get(i));
						typeEncoder.writeValue(contextOrder, leq100);
					}
					wasThereLeq100 = true;
				}
			}
			if (wasThereLeq100) {
				finalizeStream();
			}

			// all value channels having more than 100 values
			for (QName contextOrder : contextOrders) {
//				// updating to right context
//				elementContext = contextOrder;
				ContextContainer cc = contexts.get(contextOrder);
				List<String> values = cc.getValues();
				if (values.size() > Constants.MAX_NUMBER_OF_VALUES) {
					List<Datatype> valueDatatypes = cc.getValueDatatypes();
					//	create stream
					EncoderChannel gre100 = new ByteEncoderChannel(getStream());
					for (int i = 0; i < values.size(); i++) {
						typeEncoder.isValid(valueDatatypes.get(i), values.get(i));
						typeEncoder.writeValue(contextOrder, gre100);
					}
					//	finish stream
					finalizeStream();
				}
			}
		}

		// finalize document
		os.flush();
	}

}
