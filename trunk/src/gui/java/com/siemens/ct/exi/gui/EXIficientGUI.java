/*
 * Copyright (C) 2007-2014 Siemens AG
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

package com.siemens.ct.exi.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.siemens.ct.exi.CodingMode;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.EncodingOptions;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.api.sax.EXIResult;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammars.Grammars;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;

/*
 * Ideas:
 * - store config settings
 * 
 */
public class EXIficientGUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 684003639065972297L;

	JPanel contentPane;
	JTabbedPane tabbedPane;

	private final ButtonGroup buttonGroupSchema = new ButtonGroup();
	private JTextField textFieldEncodeXML;
	private JTextField textFieldEncodeEXI;
	private JTextField textFieldXSD;

	private JRadioButton rdbtnXmlSchemaDocument;
	private JRadioButton rdbtnXmlSchematypesOnly;
	private JRadioButton rdbtnStrict;

	private JComboBox comboBoxAlignment;

	private JProgressBar progressBarCompressionRatio;

	final JCheckBox chckbxPreserveCM;
	final JCheckBox chckbxPI;
	final JCheckBox chckbxPreserveDTD;
	final JCheckBox chckbxPreservePrefixes;
	final JCheckBox chckbxLexicalValues;

	protected JFileChooser fc;
	private final ButtonGroup buttonGroupStrict = new ButtonGroup();
	private JTextField textFieldDecodeEXI;
	private JTextField textFieldDecodeXML;

	private JTextField textFieldValueMaxLength;
	private JTextField textFieldValuePartitionCapacity;
	private JTextField textFieldBlockSize;

	// EXI header
	JCheckBox checkBoxIncludeCookie;
	JCheckBox checkBoxIncludeOptions;
	JCheckBox checkBoxIncludeSchemaId;
	JCheckBox checkBoxIncludeProfileValues;
	// EXI content
	JCheckBox checkBoxIncludeSchemaLocation;
	JCheckBox checkBoxIncludeInsignificantXsiNil;
	JCheckBox checkBoxRetainEntityReference;
	// EXI profile
	JCheckBox checkBoxNoLocalValuePartitions;
	JTextField textFieldMaximumNumberOfBuiltInProductions;
	JTextField textFieldMaximumNumberOfBuiltInElementGrammars;

	JLabel lblNewLabelCodingResults;

	protected JFileChooser getFileChooser() {
		if (fc == null) {
			fc = new JFileChooser();
		}
		return fc;
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager
							.getSystemLookAndFeelClassName());
					EXIficientGUI frame = new EXIficientGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	protected void doFileChooser(JTextField textField) {
		// get file chooser
		if (fc == null) {
			fc = getFileChooser();
		}

		int returnVal = fc.showOpenDialog(contentPane);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			// textField.setText(file.getAbsolutePath());

			if (textField == textFieldEncodeXML) {
				this.doUpdateXMLInput(file.getAbsolutePath());
			} else if (textField == textFieldDecodeEXI) {
				this.doUpdateEXIInput(file.getAbsolutePath());
			} else if (textField == textFieldXSD) {
				this.doUpdateXSDInput(file.getAbsolutePath());
			} else {
				System.err.println("Inavlid textfield " + textField);
			}

		}
	}

	protected void doUpdateXMLInput(String sXML) {
		textFieldEncodeXML.setText(sXML);
		String exiLoc = sXML + ".exi";
		textFieldEncodeEXI.setText(exiLoc);
		textFieldDecodeEXI.setText(exiLoc);
		textFieldDecodeXML.setText(exiLoc + ".xml");
	}

	protected void doUpdateEXIInput(String sEXI) {
		textFieldDecodeEXI.setText(sEXI);
		textFieldDecodeXML.setText(sEXI + ".xml");
	}

	protected void doUpdateXSDInput(String sXSD) {
		textFieldXSD.setText(sXSD);
	}

	protected void doEncode() {
		try {
			String xml = textFieldEncodeXML.getText();
			if (xml == null || xml.length() == 0) {
				throw new Exception("No XML input specified");
			}
			String exi = textFieldEncodeEXI.getText();
			System.out.println("Encode XML file: " + xml + " to " + exi);

			EXIFactory ef = getEXIFactory();

			// advanced encoding options
			EncodingOptions encOpt = ef.getEncodingOptions();

			if (checkBoxIncludeOptions.isSelected()) {
				encOpt.setOption(EncodingOptions.INCLUDE_OPTIONS);
				encOpt.setOption(EncodingOptions.INCLUDE_COOKIE);
			}
			if (checkBoxIncludeCookie.isSelected()) {
				encOpt.setOption(EncodingOptions.INCLUDE_COOKIE);
			}
			if (checkBoxIncludeSchemaId.isSelected()) {
				encOpt.setOption(EncodingOptions.INCLUDE_SCHEMA_ID);
			}
			if (checkBoxIncludeSchemaLocation.isSelected()) {
				encOpt.setOption(EncodingOptions.INCLUDE_XSI_SCHEMALOCATION);
			}
			if (checkBoxIncludeInsignificantXsiNil.isSelected()) {
				encOpt.setOption(EncodingOptions.INCLUDE_INSIGNIFICANT_XSI_NIL);
			}
			if (checkBoxIncludeProfileValues.isSelected()) {
				encOpt.setOption(EncodingOptions.INCLUDE_PROFILE_VALUES);
			}
			if (checkBoxRetainEntityReference.isSelected()) {
				encOpt.setOption(EncodingOptions.RETAIN_ENTITY_REFERENCE);
			}
			if (checkBoxNoLocalValuePartitions.isSelected()) {
				ef.setLocalValuePartitions(false);
			}
			if (textFieldMaximumNumberOfBuiltInProductions.getText().trim()
					.length() > 0) {
				ef.setMaximumNumberOfBuiltInProductions(Integer
						.parseInt(textFieldMaximumNumberOfBuiltInProductions
								.getText()));
			}
			if (textFieldMaximumNumberOfBuiltInElementGrammars.getText().trim()
					.length() > 0) {
				ef.setMaximumNumberOfBuiltInElementGrammars(Integer
						.parseInt(textFieldMaximumNumberOfBuiltInElementGrammars
								.getText()));
			}

			EXIResult exiResult = new EXIResult(ef);
			OutputStream fos = new FileOutputStream(exi);
			exiResult.setOutputStream(fos);
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();
			xmlReader.setContentHandler(exiResult.getHandler());
			xmlReader.parse(new InputSource(xml));
			fos.close();

			// everything went just fine
			doUpdateProgressBar(xml, exi);

		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("---");
			JOptionPane.showMessageDialog(contentPane, ex.getMessage(),
					"EXI encoding error", JOptionPane.ERROR_MESSAGE);
		}
	}

	protected EXIFactory getEXIFactory() throws EXIException {
		EXIFactory ef = DefaultEXIFactory.newInstance();

		// Grammars
		if (rdbtnXmlSchemaDocument.isSelected()) {
			String xsd = textFieldXSD.getText();
			Grammars g = GrammarFactory.newInstance().createGrammars(xsd);
			ef.setGrammars(g);
		} else if (rdbtnXmlSchematypesOnly.isSelected()) {
			Grammars g = GrammarFactory.newInstance()
					.createXSDTypesOnlyGrammars();
			ef.setGrammars(g);
		}
		// Strict vs. Non-Strict
		if (rdbtnStrict.isSelected()) {
			ef.setFidelityOptions(FidelityOptions.createStrict());
		} else {
			// other options
			if (chckbxPreserveCM.isSelected()) {
				ef.getFidelityOptions().setFidelity(
						FidelityOptions.FEATURE_COMMENT, true);
			}
			if (chckbxPI.isSelected()) {
				ef.getFidelityOptions().setFidelity(FidelityOptions.FEATURE_PI,
						true);
			}
			if (chckbxPreserveDTD.isSelected()) {
				ef.getFidelityOptions().setFidelity(
						FidelityOptions.FEATURE_DTD, true);
			}
			if (chckbxPreservePrefixes.isSelected()) {
				ef.getFidelityOptions().setFidelity(
						FidelityOptions.FEATURE_PREFIX, true);
			}
			if (chckbxLexicalValues.isSelected()) {
				ef.getFidelityOptions().setFidelity(
						FidelityOptions.FEATURE_LEXICAL_VALUE, true);
			}

		}
		// Coding mode
		CodingMode cm = CodingMode.valueOf(comboBoxAlignment.getSelectedItem()
				.toString());
		ef.setCodingMode(cm);

		if (textFieldValueMaxLength.getText().trim().length() > 0) {
			ef.setValueMaxLength(Integer.parseInt(textFieldValueMaxLength
					.getText()));
		}
		if (textFieldValuePartitionCapacity.getText().trim().length() > 0) {
			ef.setValuePartitionCapacity(Integer
					.parseInt(textFieldValuePartitionCapacity.getText()));
		}
		if (cm.usesRechanneling()) {
			if (textFieldBlockSize.getText().trim().length() > 0) {
				ef.setBlockSize(Integer.parseInt(textFieldBlockSize.getText()));
			}
		}

		return ef;
	}

	protected void doUpdateProgressBar(String xml, String exi) {
		File fXML = new File(xml);
		File fEXI = new File(exi);
		int perc = (int) (100.0 / (1.0 * fXML.length() / fEXI.length()));
		progressBarCompressionRatio.setValue(perc);

		// Compression Ratio = Uncompressed Size / Compressed Size
		float compressionRatio = 1.0f * fXML.length() / fEXI.length();

		DecimalFormat formatterB = new DecimalFormat("###,###,###"); // 123456.789
																		// ###,###.###
																		// 123,456.789
		String sXML = formatterB.format(fXML.length());
		String sEXI = formatterB.format(fEXI.length());

		DecimalFormat formatterCR = new DecimalFormat("###.##");
		String sCR = formatterCR.format(compressionRatio);

		// XML Size: 1,200B // EXI Size: 250B (21% of XML) // Compression Ratio:
		// 4.8 // Space Savings: 79%
		String results = "XML Size: " + sXML + "B // EXI Size: " + sEXI + "B ("
				+ perc + "% of XML) // Compression Ratio: " + sCR
				+ " // Space Savings: " + (100 - perc) + "%";
		lblNewLabelCodingResults.setText(results);
		lblNewLabelCodingResults.setVisible(true);
		// System.out.println("DD " + results);
	}

	@SuppressWarnings("unchecked")
	protected void doDragAndDrop(final JTextField textField) {
		textField.setDropTarget(new DropTarget() {
			private static final long serialVersionUID = 1L;

			public synchronized void drop(DropTargetDropEvent evt) {
				try {
					evt.acceptDrop(DnDConstants.ACTION_COPY);
					List<File> droppedFiles = (List<File>) evt
							.getTransferable().getTransferData(
									DataFlavor.javaFileListFlavor);
					for (File file : droppedFiles) {
						// process file(s)
						if (textField == textFieldEncodeXML) {
							doUpdateXMLInput(file.getAbsolutePath());
						} else if (textField == textFieldDecodeEXI) {
							doUpdateEXIInput(file.getAbsolutePath());
						} else if (textField == textFieldXSD) {
							doUpdateXSDInput(file.getAbsolutePath());
						} else {
							System.err
									.println("Invalid textfield " + textField);
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
	}

	protected void doDecode() {
		try {

			String exi = textFieldDecodeEXI.getText();
			if (exi == null || exi.length() == 0) {
				throw new Exception("No EXI input specified");
			}
			String xml = textFieldDecodeXML.getText();
			System.out.println("Decode EXI file: " + exi + " to " + xml);

			EXIFactory ef = getEXIFactory();

			TransformerFactory tf = TransformerFactory.newInstance();
			XMLReader exiReader = ef.createEXIReader();
			Transformer transformer = tf.newTransformer();

			if (ef.isFragment()) {
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
						"yes");
			}
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8"); // ASCII

			InputStream exiDocument = new FileInputStream(exi);
			OutputStream xmlOutput = new FileOutputStream(xml);

			Result result = new StreamResult(xmlOutput);
			InputSource is = new InputSource(exiDocument);
			SAXSource exiSource = new SAXSource(is);
			exiSource.setXMLReader(exiReader);
			transformer.transform(exiSource, result);
			exiDocument.close();
			xmlOutput.close();

			// everything went just fine
			doUpdateProgressBar(xml, exi);

		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("---");
			JOptionPane.showMessageDialog(contentPane, ex.getMessage(),
					"EXI decoding error", JOptionPane.ERROR_MESSAGE);
		}
	}

	void checkIncludeOptions() {
		checkBoxIncludeSchemaId.setEnabled(checkBoxIncludeOptions.isSelected());
		checkBoxIncludeProfileValues.setEnabled(checkBoxIncludeOptions
				.isSelected());
	}

	/**
	 * Create the frame.
	 */
	public EXIficientGUI() {
		setTitle("EXIficient");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 770, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		// EXI header
		checkBoxIncludeCookie = new JCheckBox(
				"Include optional EXI Cookie as part of the EXI stream");
		checkBoxIncludeOptions = new JCheckBox(
				"Include EXI Options as part of the EXI header");
		checkBoxIncludeSchemaId = new JCheckBox(
				"Include schemaId (as part of the EXI Options)");
		checkBoxIncludeProfileValues = new JCheckBox(
				"Include EXI Profile parameters (as part of the EXI Options)");
		checkBoxIncludeOptions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				checkIncludeOptions();
			}
		});
		checkIncludeOptions();
		// EXI content stream
		checkBoxIncludeSchemaLocation = new JCheckBox(
				"Include attribute \"schemaLocation\" and \"noNamespaceSchemaLocation\" in EXI stream");
		checkBoxIncludeInsignificantXsiNil = new JCheckBox(
				"Include insignificant xsi:nil values in EXI stream (e.g., xsi:nil=\"false\")");
		checkBoxRetainEntityReference = new JCheckBox(
				"Retain entity references as ER event instead of trying to resolve them (e.g., &amp; vs. &)");
		// profile
		checkBoxNoLocalValuePartitions = new JCheckBox(
				"no localValuePartitions (indicates that no local string value partition is used)");
		textFieldMaximumNumberOfBuiltInProductions = new JTextField();
		textFieldMaximumNumberOfBuiltInProductions.setUI(new HintTextFieldUI(
				"    unbounded", true, Color.GRAY));
		PlainDocument docA = (PlainDocument) textFieldMaximumNumberOfBuiltInProductions
				.getDocument();
		docA.setDocumentFilter(new IntegerRangeDocumentFilter(0,
				Integer.MAX_VALUE));
		textFieldMaximumNumberOfBuiltInProductions.setColumns(10);
		textFieldMaximumNumberOfBuiltInElementGrammars = new JTextField();
		textFieldMaximumNumberOfBuiltInElementGrammars
				.setUI(new HintTextFieldUI("    unbounded", true, Color.GRAY));
		PlainDocument docB = (PlainDocument) textFieldMaximumNumberOfBuiltInElementGrammars
				.getDocument();
		docB.setDocumentFilter(new IntegerRangeDocumentFilter(0,
				Integer.MAX_VALUE));
		textFieldMaximumNumberOfBuiltInElementGrammars.setColumns(10);

		JPanel panelEXIOptions = new JPanel();
		panelEXIOptions.setBorder(new TitledBorder(null, "EXI Options",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		contentPane.add(panelEXIOptions, BorderLayout.NORTH);
		panelEXIOptions.setLayout(new GridLayout(0, 3, 0, 0));

		JPanel panelCodingMode = new JPanel();
		panelCodingMode.setBorder(new TitledBorder(UIManager
				.getBorder("TitledBorder.border"), "Alignment & StringTable",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelEXIOptions.add(panelCodingMode);
		GridBagLayout gbl_panelCodingMode = new GridBagLayout();
		gbl_panelCodingMode.columnWidths = new int[] { 116, 116, 0 };
		gbl_panelCodingMode.rowHeights = new int[] { 0, 0, 0, 0, 50 };
		gbl_panelCodingMode.columnWeights = new double[] { 0.0, 1.0,
				Double.MIN_VALUE };
		gbl_panelCodingMode.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0 };
		panelCodingMode.setLayout(gbl_panelCodingMode);

		JLabel lblNewLabel = new JLabel("Alignment");
		lblNewLabel
				.setToolTipText("<html>The alignment option is used to control the alignment of event codes and content items\r\n<ul>\r\n <li><strong>bit-packed</strong> indicates that the event codes and associated content are packed in bits without any padding in-between</li>\r\n<li><strong>byte-alignment</strong> indicates that the event codes and associated content are aligned on byte boundaries</li>\r\n<li><strong>pre-compression</strong> indicates that all steps involved in compression are to be done with the exception of the final step of applying the DEFLATE algorithm (the primary use case of pre-compression is to avoid a duplicate compression step when compression capability is built into the transport protocol)</li>\r\n<li><strong>compression</strong> increases compactness using additional computational resources  by  applying the DEFLATE algorithm</li>\r\n<ul>\r\n</html>");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		panelCodingMode.add(lblNewLabel, gbc_lblNewLabel);

		comboBoxAlignment = new JComboBox();
		comboBoxAlignment
				.setToolTipText("<html>The alignment option is used to control the alignment of event codes and content items\r\n<ul>\r\n <li><strong>bit-packed</strong> indicates that the event codes and associated content are packed in bits without any padding in-between</li>\r\n<li><strong>byte-alignment</strong> indicates that the event codes and associated content are aligned on byte boundaries</li>\r\n<li><strong>pre-compression</strong> indicates that all steps involved in compression are to be done with the exception of the final step of applying the DEFLATE algorithm (the primary use case of pre-compression is to avoid a duplicate compression step when compression capability is built into the transport protocol)</li>\r\n<li><strong>compression</strong> increases compactness using additional computational resources  by  applying the DEFLATE algorithm</li>\r\n<ul>\r\n</html>");
		comboBoxAlignment.setAlignmentX(Component.LEFT_ALIGNMENT);
		comboBoxAlignment.setModel(new DefaultComboBoxModel(new String[] {
				CodingMode.BIT_PACKED.toString(),
				CodingMode.BYTE_PACKED.toString(),
				CodingMode.PRE_COMPRESSION.toString(),
				CodingMode.COMPRESSION.toString() }));
		GridBagConstraints gbc_comboBoxAlignment = new GridBagConstraints();
		gbc_comboBoxAlignment.insets = new Insets(0, 0, 5, 0);
		gbc_comboBoxAlignment.fill = GridBagConstraints.BOTH;
		gbc_comboBoxAlignment.gridx = 1;
		gbc_comboBoxAlignment.gridy = 0;
		panelCodingMode.add(comboBoxAlignment, gbc_comboBoxAlignment);

		JLabel lblNewLabel_1 = new JLabel("valueMaxLength");
		lblNewLabel_1
				.setToolTipText("Specifies the maximum string length of value content items to be considered for addition to the string table (default value unbounded)");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 1;
		panelCodingMode.add(lblNewLabel_1, gbc_lblNewLabel_1);

		textFieldValueMaxLength = new JTextField();
		lblNewLabel_1.setLabelFor(textFieldValueMaxLength);
		textFieldValueMaxLength.setUI(new HintTextFieldUI("    unbounded",
				true, Color.GRAY));
		textFieldValueMaxLength.setHorizontalAlignment(SwingConstants.TRAILING);
		PlainDocument doc1 = (PlainDocument) textFieldValueMaxLength
				.getDocument();
		;
		doc1.setDocumentFilter(new IntegerRangeDocumentFilter(0,
				Integer.MAX_VALUE));

		textFieldValueMaxLength
				.setToolTipText("Specifies the maximum string length of value content items to be considered for addition to the string table (default value unbounded)");
		GridBagConstraints gbc_textFieldValueMaxLength = new GridBagConstraints();
		gbc_textFieldValueMaxLength.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldValueMaxLength.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldValueMaxLength.gridx = 1;
		gbc_textFieldValueMaxLength.gridy = 1;
		panelCodingMode.add(textFieldValueMaxLength,
				gbc_textFieldValueMaxLength);
		textFieldValueMaxLength.setColumns(10);

		JLabel lblNewLabel_2 = new JLabel("valuePartitionCapacity");
		lblNewLabel_2
				.setToolTipText("Specifies the total capacity of value partitions in a string table (default value unbounded)");
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_2.gridx = 0;
		gbc_lblNewLabel_2.gridy = 2;
		panelCodingMode.add(lblNewLabel_2, gbc_lblNewLabel_2);

		textFieldValuePartitionCapacity = new JTextField();
		textFieldValuePartitionCapacity.setUI(new HintTextFieldUI(
				"    unbounded", true, Color.GRAY));
		textFieldValuePartitionCapacity
				.setHorizontalAlignment(SwingConstants.TRAILING);
		PlainDocument doc2 = (PlainDocument) textFieldValuePartitionCapacity
				.getDocument();
		;
		doc2.setDocumentFilter(new IntegerRangeDocumentFilter(0,
				Integer.MAX_VALUE));

		textFieldValuePartitionCapacity
				.setToolTipText("Specifies the total capacity of value partitions in a string table (default value unbounded)");
		GridBagConstraints gbc_textFieldValuePartitionCapacity = new GridBagConstraints();
		gbc_textFieldValuePartitionCapacity.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldValuePartitionCapacity.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldValuePartitionCapacity.gridx = 1;
		gbc_textFieldValuePartitionCapacity.gridy = 2;
		panelCodingMode.add(textFieldValuePartitionCapacity,
				gbc_textFieldValuePartitionCapacity);
		textFieldValuePartitionCapacity.setColumns(10);

		JLabel lblBlocksize = new JLabel("blockSize");
		lblBlocksize
				.setToolTipText("Specifies the block size used for EXI compression (default value 1,000,000)");
		GridBagConstraints gbc_lblBlocksize = new GridBagConstraints();
		gbc_lblBlocksize.anchor = GridBagConstraints.EAST;
		gbc_lblBlocksize.insets = new Insets(0, 0, 5, 5);
		gbc_lblBlocksize.gridx = 0;
		gbc_lblBlocksize.gridy = 3;
		panelCodingMode.add(lblBlocksize, gbc_lblBlocksize);

		textFieldBlockSize = new JTextField();
		textFieldBlockSize.setEnabled(false);
		textFieldBlockSize.setHorizontalAlignment(SwingConstants.TRAILING);
		textFieldBlockSize.setText("1000000");
		PlainDocument doc3 = (PlainDocument) textFieldBlockSize.getDocument();
		;
		doc3.setDocumentFilter(new IntegerRangeDocumentFilter(0,
				Integer.MAX_VALUE));

		textFieldBlockSize
				.setToolTipText("Specifies the block size used for EXI compression (default value 1,000,000)");
		GridBagConstraints gbc_textFieldBlockSize = new GridBagConstraints();
		gbc_textFieldBlockSize.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldBlockSize.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldBlockSize.gridx = 1;
		gbc_textFieldBlockSize.gridy = 3;
		panelCodingMode.add(textFieldBlockSize, gbc_textFieldBlockSize);
		textFieldBlockSize.setColumns(10);

		comboBoxAlignment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CodingMode cm = CodingMode.valueOf(comboBoxAlignment
						.getSelectedItem().toString());
				textFieldBlockSize.setEnabled(cm.usesRechanneling());
			}
		});

		JPanel panel_6 = new JPanel();
		panel_6.setBorder(new TitledBorder(null, "Coding Options",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelEXIOptions.add(panel_6);
		panel_6.setLayout(new BoxLayout(panel_6, BoxLayout.Y_AXIS));

		final JRadioButton rdbtnDefault = new JRadioButton("Default");
		rdbtnDefault.setSelected(true);
		buttonGroupStrict.add(rdbtnDefault);
		panel_6.add(rdbtnDefault);

		JPanel panelPreserveOptions = new JPanel();
		panel_6.add(panelPreserveOptions);
		panelPreserveOptions.setBorder(new EmptyBorder(0, 15, 0, 0));
		panelPreserveOptions.setLayout(new BoxLayout(panelPreserveOptions,
				BoxLayout.Y_AXIS));

		chckbxPreserveCM = new JCheckBox("Preserve Comments");
		panelPreserveOptions.add(chckbxPreserveCM);

		chckbxPI = new JCheckBox("Preserve Processing Instructions");
		panelPreserveOptions.add(chckbxPI);

		chckbxPreserveDTD = new JCheckBox("Preserve DTDs and Entity References");
		panelPreserveOptions.add(chckbxPreserveDTD);

		chckbxPreservePrefixes = new JCheckBox("Preserve Prefixes");
		panelPreserveOptions.add(chckbxPreservePrefixes);

		chckbxLexicalValues = new JCheckBox("Preserve Lexical Values");
		panelPreserveOptions.add(chckbxLexicalValues);

		rdbtnStrict = new JRadioButton("Strict");
		rdbtnStrict
				.setToolTipText("Strict interpretation of schemas is used to achieve better compactness (default value false)");
		buttonGroupStrict.add(rdbtnStrict);
		panel_6.add(rdbtnStrict);
		rdbtnDefault.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				boolean dec = rdbtnDefault.isSelected();
				chckbxPreserveCM.setEnabled(dec);
				chckbxPI.setEnabled(dec);
				chckbxPreserveDTD.setEnabled(dec);
				chckbxPreservePrefixes.setEnabled(dec);
				chckbxLexicalValues.setEnabled(dec);
			}
		});
		rdbtnStrict.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				boolean dec = !rdbtnStrict.isSelected();
				chckbxPreserveCM.setEnabled(dec);
				chckbxPI.setEnabled(dec);
				chckbxPreserveDTD.setEnabled(dec);
				chckbxPreservePrefixes.setEnabled(dec);
				chckbxLexicalValues.setEnabled(dec);
			}
		});

		final JButton btnBrowseXSD = new JButton("Browse");
		btnBrowseXSD.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doFileChooser(textFieldXSD);
			}
		});

		JPanel panelSchema = new JPanel();
		panelSchema.setBorder(new TitledBorder(null, "Schema Information",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelEXIOptions.add(panelSchema);
		GridBagLayout gbl_panelSchema = new GridBagLayout();
		gbl_panelSchema.rowHeights = new int[] { 69, 92 };
		gbl_panelSchema.columnWeights = new double[] { 1.0 };
		gbl_panelSchema.rowWeights = new double[] { 0.0, 0.0 };
		panelSchema.setLayout(gbl_panelSchema);

		JPanel panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel_1.anchor = GridBagConstraints.WEST;
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 0;
		panelSchema.add(panel_1, gbc_panel_1);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));

		JRadioButton rdbtnSchemaless = new JRadioButton("Schema-less");
		rdbtnSchemaless
				.setToolTipText("No schema information is used for processing the EXI body (i.e. a schema-less EXI stream)");
		panel_1.add(rdbtnSchemaless);
		rdbtnSchemaless.setSelected(true);
		buttonGroupSchema.add(rdbtnSchemaless);

		rdbtnXmlSchematypesOnly = new JRadioButton("XML schema-types only");
		rdbtnXmlSchematypesOnly
				.setToolTipText("No user defined schema information is used for processing the EXI body; however, the built-in XML schema types are available for use in the EXI body");
		panel_1.add(rdbtnXmlSchematypesOnly);
		buttonGroupSchema.add(rdbtnXmlSchematypesOnly);

		rdbtnXmlSchemaDocument = new JRadioButton("XML schema document");
		rdbtnXmlSchemaDocument
				.setToolTipText("The schemaId option may be used to identify the schema information used for processing the EXI body");

		panel_1.add(rdbtnXmlSchemaDocument);
		buttonGroupSchema.add(rdbtnXmlSchemaDocument);

		// rdbtnXmlSchemaDocument.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent e) {
		// boolean decXsd = rdbtnXmlSchemaDocument.isSelected();
		// textFieldXSD.setEnabled(decXsd);
		// btnBrowseXSD.setEnabled(decXsd);
		// }
		// });
		rdbtnXmlSchemaDocument.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				boolean decXsd = rdbtnXmlSchemaDocument.isSelected();
				// textFieldXSD.setEnabled(decXsd);
				btnBrowseXSD.setEnabled(decXsd);
			}
		});

		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 20, 0, 0);
		gbc_panel.anchor = GridBagConstraints.NORTHWEST;
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 1;
		panelSchema.add(panel, gbc_panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));

		textFieldXSD = new JTextField();
		textFieldXSD.setEnabled(false);
		textFieldXSD.setEditable(false);
		textFieldXSD.setUI(new HintTextFieldUI("  Select XSD", true));
		panel.add(textFieldXSD);
		textFieldXSD.setColumns(10);
		this.doDragAndDrop(textFieldXSD);

		btnBrowseXSD.setEnabled(false);
		panel.add(btnBrowseXSD);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);

		JPanel panelXML2EXI = new JPanel();
		tabbedPane.addTab("XML to EXI", null, panelXML2EXI, null);
		panelXML2EXI.setLayout(new BorderLayout(0, 0));

		JButton btnNewButton_1 = new JButton("Encode to EXI");

		panelXML2EXI.add(btnNewButton_1, BorderLayout.SOUTH);

		JPanel panel_4 = new JPanel();
		panelXML2EXI.add(panel_4, BorderLayout.NORTH);
		// panelXML2EXI.add(panel_4, BorderLayout.NORTH);
		panel_4.setLayout(new BoxLayout(panel_4, BoxLayout.Y_AXIS));

		JPanel panel_2 = new JPanel();
		panel_4.add(panel_2);
		panel_2.setLayout(new BoxLayout(panel_2, BoxLayout.X_AXIS));

		textFieldEncodeXML = new JTextField();
		textFieldEncodeXML.setEditable(false);
		textFieldEncodeXML.setEnabled(false);
		textFieldEncodeXML
				.setUI(new HintTextFieldUI("  Select XML file", true));
		panel_2.add(textFieldEncodeXML);
		textFieldEncodeXML.setColumns(10);
		this.doDragAndDrop(textFieldEncodeXML);

		JButton btnBrowseForXml = new JButton("Browse");
		panel_2.add(btnBrowseForXml);

		JPanel panel_3 = new JPanel();
		panel_4.add(panel_3);
		panel_3.setLayout(new BoxLayout(panel_3, BoxLayout.X_AXIS));

		textFieldEncodeEXI = new JTextField();
		textFieldEncodeEXI.setEnabled(false);
		textFieldEncodeEXI.setEditable(false);
		textFieldEncodeEXI.setHorizontalAlignment(SwingConstants.LEFT);
		textFieldEncodeEXI
				.setUI(new HintTextFieldUI("  EXI output file", true));
		panel_3.add(textFieldEncodeEXI);
		textFieldEncodeEXI.setColumns(10);

		JButton btnSelectExiOutpt = new JButton("Browse");
		btnSelectExiOutpt.setEnabled(false);
		panel_3.add(btnSelectExiOutpt);

		JPanel panel_11 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_11.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel_4.add(panel_11);

		JButton btnAdvancedOptions = new JButton(
				"Configure Advanced Encoding Options");
		panel_11.add(btnAdvancedOptions);
		btnAdvancedOptions.setHorizontalAlignment(SwingConstants.LEFT);
		btnAdvancedOptions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JPanel padSchemaId = new JPanel();
				padSchemaId.setLayout(new BorderLayout());
				padSchemaId.add(new JLabel("    "), BorderLayout.WEST);
				padSchemaId.add(checkBoxIncludeSchemaId, BorderLayout.CENTER);
				JPanel padProfileValues = new JPanel();
				padProfileValues.setLayout(new BorderLayout());
				padProfileValues.add(new JLabel("    "), BorderLayout.WEST);
				padProfileValues.add(checkBoxIncludeProfileValues,
						BorderLayout.CENTER);

				Object[] message = { "# EXI Header", checkBoxIncludeCookie,
						checkBoxIncludeOptions, padSchemaId, padProfileValues,
						"# EXI Content representation",
						checkBoxIncludeSchemaLocation,
						checkBoxIncludeInsignificantXsiNil,
						checkBoxRetainEntityReference,
						"# EXI Profile parameters",
						checkBoxNoLocalValuePartitions,
						"maximumNumberOfBuiltInProductions",
						textFieldMaximumNumberOfBuiltInProductions,
						"maximumNumberOfBuiltInElementGrammars",
						textFieldMaximumNumberOfBuiltInElementGrammars };

				JOptionPane pane = new JOptionPane(message,
						JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION);
				JDialog d = pane.createDialog(null,
						"Advanced EXI Encoding Options");
				d.setVisible(true);

			}
		});

		btnBrowseForXml.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doFileChooser(textFieldEncodeXML);
			}
		});

		JPanel panelEXI2XML = new JPanel();
		tabbedPane.addTab("EXI to XML", null, panelEXI2XML, null);
		panelEXI2XML.setLayout(new BorderLayout(0, 0));

		JButton btnNewButton = new JButton("Decode to XML");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doDecode();
			}
		});
		panelEXI2XML.add(btnNewButton, BorderLayout.SOUTH);

		JPanel panel_7 = new JPanel();
		panelEXI2XML.add(panel_7, BorderLayout.NORTH);
		panel_7.setLayout(new BoxLayout(panel_7, BoxLayout.Y_AXIS));

		JPanel panel_8 = new JPanel();
		panel_7.add(panel_8);
		panel_8.setLayout(new BoxLayout(panel_8, BoxLayout.X_AXIS));

		textFieldDecodeEXI = new JTextField();
		textFieldDecodeEXI.setEnabled(false);
		textFieldDecodeEXI.setEditable(false);
		panel_8.add(textFieldDecodeEXI);
		textFieldDecodeEXI.setColumns(10);
		textFieldDecodeEXI
				.setUI(new HintTextFieldUI("  Select EXI file", true));
		this.doDragAndDrop(textFieldDecodeEXI);

		JButton btnBrowseForEXI = new JButton("Browse");
		btnBrowseForEXI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doFileChooser(textFieldDecodeEXI);
			}
		});
		panel_8.add(btnBrowseForEXI);

		JPanel panel_9 = new JPanel();
		panel_7.add(panel_9);
		panel_9.setLayout(new BoxLayout(panel_9, BoxLayout.X_AXIS));

		textFieldDecodeXML = new JTextField();
		textFieldDecodeXML.setEnabled(false);
		textFieldDecodeXML.setEditable(false);
		panel_9.add(textFieldDecodeXML);
		textFieldDecodeXML.setColumns(10);
		textFieldDecodeXML
				.setUI(new HintTextFieldUI("  XML output file", true));

		JButton btnEncodeBrowseForXML = new JButton("Browse");
		btnEncodeBrowseForXML.setEnabled(false);
		panel_9.add(btnEncodeBrowseForXML);

		JPanel panel_10 = new JPanel();
		panel_7.add(panel_10);

		JPanel panel_5 = new JPanel();
		contentPane.add(panel_5, BorderLayout.SOUTH);
		FlowLayout flowLayout_2 = (FlowLayout) panel_5.getLayout();
		flowLayout_2.setAlignment(FlowLayout.LEFT);

		progressBarCompressionRatio = new JProgressBar();
		panel_5.add(progressBarCompressionRatio);
		progressBarCompressionRatio.setToolTipText("");
		progressBarCompressionRatio.setStringPainted(true);

		lblNewLabelCodingResults = new JLabel("");
		panel_5.add(lblNewLabelCodingResults);

		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doEncode();
			}
		});
	}

	class IntegerRangeDocumentFilter extends DocumentFilter {

		int minimum, maximum;

		int currentValue = 0;

		public IntegerRangeDocumentFilter(int minimum, int maximum) {
			this.minimum = minimum;
			this.maximum = maximum;
		}

		public void insertString(DocumentFilter.FilterBypass fb, int offset,
				String string, AttributeSet attr) throws BadLocationException {

			if (string == null) {
				return;
			} else {
				String newValue;
				Document doc = fb.getDocument();
				int length = doc.getLength();
				if (length == 0) {
					newValue = string;
				} else {
					String currentContent = doc.getText(0, length);
					StringBuffer currentBuffer = new StringBuffer(
							currentContent);
					currentBuffer.insert(offset, string);
					newValue = currentBuffer.toString();
				}
				currentValue = checkInput(newValue, offset);
				fb.insertString(offset, string, attr);
			}
		}

		public void remove(DocumentFilter.FilterBypass fb, int offset,
				int length) throws BadLocationException {

			Document doc = fb.getDocument();
			int currentLength = doc.getLength();
			String currentContent = doc.getText(0, currentLength);
			String before = currentContent.substring(0, offset);
			String after = currentContent.substring(length + offset,
					currentLength);
			String newValue = before + after;
			currentValue = checkInput(newValue, offset);
			fb.remove(offset, length);
		}

		public void replace(DocumentFilter.FilterBypass fb, int offset,
				int length, String text, AttributeSet attrs)
				throws BadLocationException {

			Document doc = fb.getDocument();
			int currentLength = doc.getLength();
			String currentContent = doc.getText(0, currentLength);
			String before = currentContent.substring(0, offset);
			String after = currentContent.substring(length + offset,
					currentLength);
			String newValue = before + (text == null ? "" : text) + after;
			currentValue = checkInput(newValue, offset);
			fb.replace(offset, length, text, attrs);
		}

		private int checkInput(String proposedValue, int offset)
				throws BadLocationException {
			int newValue = 0;
			if (proposedValue.length() > 0) {
				try {
					newValue = Integer.parseInt(proposedValue);
				} catch (NumberFormatException e) {
					throw new BadLocationException(proposedValue, offset);
				}
			}
			if ((minimum <= newValue) && (newValue <= maximum)) {
				return newValue;
			} else {
				throw new BadLocationException(proposedValue, offset);
			}
		}
	}

	class HintTextFieldUI extends BasicTextFieldUI implements FocusListener {

		private String hint;
		private boolean hideOnFocus;
		private Color color;

		public Color getColor() {
			return color;
		}

		public void setColor(Color color) {
			this.color = color;
			repaint();
		}

		private void repaint() {
			if (getComponent() != null) {
				getComponent().repaint();
			}
		}

		public boolean isHideOnFocus() {
			return hideOnFocus;
		}

		public void setHideOnFocus(boolean hideOnFocus) {
			this.hideOnFocus = hideOnFocus;
			repaint();
		}

		public String getHint() {
			return hint;
		}

		public void setHint(String hint) {
			this.hint = hint;
			repaint();
		}

		public HintTextFieldUI(String hint) {
			this(hint, false);
		}

		public HintTextFieldUI(String hint, boolean hideOnFocus) {
			this(hint, hideOnFocus, null);
		}

		public HintTextFieldUI(String hint, boolean hideOnFocus, Color color) {
			this.hint = hint;
			this.hideOnFocus = hideOnFocus;
			this.color = color;
		}

		@Override
		protected void paintSafely(Graphics g) {
			super.paintSafely(g);
			JTextComponent comp = getComponent();
			if (hint != null && comp.getText().length() == 0
					&& (!(hideOnFocus && comp.hasFocus()))) {
				if (color != null) {
					g.setColor(color);
				} else {
					g.setColor(comp.getForeground().brighter().brighter()
							.brighter());
				}
				int padding = (comp.getHeight() - comp.getFont().getSize()) / 2;
				g.drawString(hint, 2, comp.getHeight() - padding - 1);
			}
		}

		// @Override
		public void focusGained(FocusEvent e) {
			if (hideOnFocus)
				repaint();

		}

		// @Override
		public void focusLost(FocusEvent e) {
			if (hideOnFocus)
				repaint();
		}

		@Override
		protected void installListeners() {
			super.installListeners();
			getComponent().addFocusListener(this);
		}

		@Override
		protected void uninstallListeners() {
			super.uninstallListeners();
			getComponent().removeFocusListener(this);
		}
	}

}
