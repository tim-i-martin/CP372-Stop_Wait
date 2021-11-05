import java.awt.EventQueue;

import javax.swing.*;
import java.awt.BorderLayout;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Insets;
import java.awt.Color;
import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class Sender_GUI {
	//private BoardClient client = null;
	private JFrame frmClientPortal;
	private JTextField txtIPReceiver;
    private JTextField txtPORTReceiver;
    private JTextField txtPORTSender;
	private JTextField txtFILEInput;
    private JTextField txtTimeout;
    private JTextField txtPackageCount;

    private JRadioButton reliableRadio;
    private JRadioButton unreliableRadio;
    private ButtonGroup operatingMode;

	private JTextField textRefersTo;

	// Swing objects that will get passed to the BoardClient class
	private JTextArea textDisplay;

	private DatagramSocket socket;
	int port_sender;
	String IP;
    int port_receiver;
    String file_name;
    int timeout;
    boolean is_reliable;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Sender_GUI window = new Sender_GUI();
					window.frmClientPortal.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Sender_GUI() {
		initialize();
	}

	/**
	 * Create frmClientPortal JFrame
	 */
	private void renderClientPortal() {
		frmClientPortal = new JFrame();
		frmClientPortal.getContentPane().setBackground(Color.LIGHT_GRAY);
		frmClientPortal.setBackground(new Color(102, 255, 51));
		frmClientPortal.setTitle("Sending Portal");
		frmClientPortal.setBounds(100, 100, 652, 594);
		frmClientPortal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		frmClientPortal.getContentPane().setLayout(gridBagLayout);
	};

	/**
	 * Create txtIPaddress text field for Receiver
	 */
	private void renderTextIPReceiver() {
		txtIPReceiver = new JTextField();
		txtIPReceiver.setToolTipText("enter IP address of Receiver in format xxx.x.x.x");
		txtIPReceiver.setText("127.0.0.1");
		GridBagConstraints gbc_txtIPReceiver = new GridBagConstraints();
        gbc_txtIPReceiver.gridwidth = 3;
        gbc_txtIPReceiver.insets = new Insets(0, 0, 5, 5);
        gbc_txtIPReceiver.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtIPReceiver.gridx = 3;
        gbc_txtIPReceiver.gridy = 2;
		frmClientPortal.getContentPane().add(txtIPReceiver, gbc_txtIPReceiver);
		txtIPReceiver.setColumns(10);
	}

	/**
	 * create txtPORTReceiver to identify the receiver PORT number
	 */
	private void renderTextPORTReceiver() {
		txtPORTReceiver = new JTextField();
		txtPORTReceiver.setToolTipText("enter the 4 digit port number");
		txtPORTReceiver.setText("4444");
		GridBagConstraints gbc_txtPORTReceiver = new GridBagConstraints();
		gbc_txtPORTReceiver.gridwidth = 3;
		gbc_txtPORTReceiver.insets = new Insets(0, 0, 5, 5);
		gbc_txtPORTReceiver.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtPORTReceiver.gridx = 3;
		gbc_txtPORTReceiver.gridy = 4;
		frmClientPortal.getContentPane().add(txtPORTReceiver, gbc_txtPORTReceiver);
		txtPORTReceiver.setColumns(10);

	}

	/**
	 * create txtPORTSender JtextField() to identify where to receive ACK's
	 */
	private void renderTextPORTSender() {
		txtPORTSender = new JTextField();
		txtPORTSender.setToolTipText("enter the PORT to receive ACK's");
		txtPORTSender.setText("4443");
		GridBagConstraints gbc_txtPORTSender = new GridBagConstraints();
		gbc_txtPORTSender.gridwidth = 3;
		gbc_txtPORTSender.insets = new Insets(0, 0, 5, 5);
		gbc_txtPORTSender.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtPORTSender.gridx = 3;
		gbc_txtPORTSender.gridy = 5;
		frmClientPortal.getContentPane().add(txtPORTSender, gbc_txtPORTSender);
		txtPORTSender.setColumns(10);
	
	}

	/**
	 * create txtFILEInput JtextField() as a field to capture the file name to send
	 */
	private void renderTextFILEInput() {
		txtFILEInput = new JTextField();
		txtFILEInput.setToolTipText("enter the file name to transmit");
		txtFILEInput.setText("write_from.txt");
		GridBagConstraints gbc_txtFILEInput = new GridBagConstraints();
		gbc_txtFILEInput.gridwidth = 3;
		gbc_txtFILEInput.insets = new Insets(0, 0, 5, 5);
		gbc_txtFILEInput.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtFILEInput.gridx = 3;
		gbc_txtFILEInput.gridy = 6;
		frmClientPortal.getContentPane().add(txtFILEInput, gbc_txtFILEInput);
		txtFILEInput.setColumns(10);
	}

	/**
	 * create TextPackageCount JtextField() as a field to display current package count
	 */
	private void renderTextPackageCount() {
		txtPackageCount = new JTextField();
		//txtPackageCount.setToolTipText("enter the timeout in ms");
		txtPackageCount.setText("package count");
		GridBagConstraints gbc_txtPackageCount = new GridBagConstraints();
		gbc_txtPackageCount.gridwidth = 3;
		gbc_txtPackageCount.insets = new Insets(0, 0, 5, 5);
		gbc_txtPackageCount.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtPackageCount.gridx = 3;
		gbc_txtPackageCount.gridy = 8;
		frmClientPortal.getContentPane().add(txtPackageCount, gbc_txtPackageCount);
		txtPackageCount.setColumns(10);
	}

    /**
     * create the radio buttons to configure the manner in which the program functions
     */

     private void renderButtonOperatingMode(){
        reliableRadio = new JRadioButton();
        reliableRadio.setText("Reliable");
		reliableRadio.setSelected(true);
        
        unreliableRadio = new JRadioButton();
        unreliableRadio.setText("Unreliable");


        operatingMode = new ButtonGroup();
        operatingMode.add(reliableRadio);
        operatingMode.add(unreliableRadio);


        GridBagConstraints gbc_reliable = new GridBagConstraints();

        gbc_reliable.gridwidth = 3;
        gbc_reliable.insets = new Insets(0,0,5,5);
        gbc_reliable.fill = GridBagConstraints.HORIZONTAL;
        gbc_reliable.gridx = 3;
        gbc_reliable.gridy = 9;

        frmClientPortal.getContentPane().add(reliableRadio,gbc_reliable);

        GridBagConstraints gbc_unreliable = new GridBagConstraints();
        gbc_unreliable.gridwidth = 3;
        gbc_unreliable.insets = new Insets(0,0,5,5);
        gbc_unreliable.fill = GridBagConstraints.HORIZONTAL;
        gbc_unreliable.gridx = 6;
        gbc_unreliable.gridy = 9;

        frmClientPortal.getContentPane().add(unreliableRadio,gbc_unreliable);


     }


	/**
	 * create TextTimeout JtextField() as a field to capture the timeout calc
	 */
	private void renderTextTimeout() {
		txtTimeout = new JTextField();
		txtTimeout.setToolTipText("enter the timeout in ms");
		txtTimeout.setText("50");
		GridBagConstraints gbc_txtTimeout = new GridBagConstraints();
		gbc_txtTimeout.gridwidth = 3;
		gbc_txtTimeout.insets = new Insets(0, 0, 5, 5);
		gbc_txtTimeout.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtTimeout.gridx = 3;
		gbc_txtTimeout.gridy = 7;
		frmClientPortal.getContentPane().add(txtTimeout, gbc_txtTimeout);
		txtTimeout.setColumns(10);
	}


	private void renderBtnALIVE() {
		JButton btn = new JButton("ISALIVE");
		GridBagConstraints gbc_btnISALIVE = new GridBagConstraints();
		gbc_btnISALIVE.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnISALIVE.insets = new Insets(0, 0, 5, 5);
		gbc_btnISALIVE.gridx = 12;
		gbc_btnISALIVE.gridy = 2;
		frmClientPortal.getContentPane().add(btn, gbc_btnISALIVE);
		actionBtnISALIVE(btn);
	}

	/**
	 * create IPReceiverLabel JLabel()
	 */
	private void renderIPReceiverLabel() {
		JLabel IPReceiverLabel = new JLabel("IP address of Receiver");
		GridBagConstraints gbc_IPReceiverLabel = new GridBagConstraints();
		gbc_IPReceiverLabel.insets = new Insets(0, 0, 5, 5);
		gbc_IPReceiverLabel.gridx = 1;
		gbc_IPReceiverLabel.gridy = 2;
		frmClientPortal.getContentPane().add(IPReceiverLabel, gbc_IPReceiverLabel);
	}

	/**
	 * create PORTReceiverLabel JLabel()
	 */
	private void renderPORTReceiverLabel() {
		JLabel PORTReceiverLabel = new JLabel("Receiver's PORT#");
		GridBagConstraints gbc_PORTReceiver = new GridBagConstraints();
		gbc_PORTReceiver.insets = new Insets(0, 0, 5, 5);
		gbc_PORTReceiver.gridx = 1;
		gbc_PORTReceiver.gridy = 4;
		frmClientPortal.getContentPane().add(PORTReceiverLabel, gbc_PORTReceiver);
	}

	/**
	 * create PORTSenderLabel JLabel()
	 */
	private void renderPORTSenderLabel() {
		JLabel PORTSenderLabel = new JLabel("Senders's PORT# for ACK");
		GridBagConstraints gbc_PORTSender = new GridBagConstraints();
		gbc_PORTSender.insets = new Insets(0, 0, 5, 5);
		gbc_PORTSender.gridx = 1;
		gbc_PORTSender.gridy = 5;
		frmClientPortal.getContentPane().add(PORTSenderLabel, gbc_PORTSender);
	}


    /**
	 * create lblFILEInput JLabel()
	 */
	private void renderLblFILEInput() {
		JLabel lblFILEInput = new JLabel("File Name to Send");
		GridBagConstraints gbc_lblFILEInput = new GridBagConstraints();
		gbc_lblFILEInput.insets = new Insets(0, 0, 5, 5);
		gbc_lblFILEInput.gridx = 1;
		gbc_lblFILEInput.gridy = 6;
		frmClientPortal.getContentPane().add(lblFILEInput, gbc_lblFILEInput);
	}

	/**
	 * create TimeoutLabel JLabel()
	 */
	private void renderTimeoutLabel() {
		JLabel TimeoutLabel = new JLabel("Enter timeout length (ms): ");
		GridBagConstraints gbc_TimeoutLabel = new GridBagConstraints();
		gbc_TimeoutLabel.insets = new Insets(0, 0, 5, 5);
		gbc_TimeoutLabel.gridx = 1;
		gbc_TimeoutLabel.gridy = 7;
		frmClientPortal.getContentPane().add(TimeoutLabel, gbc_TimeoutLabel);
	}

	/**
	 * create PackageCountLabel JLabel()
	 */
	private void renderPackageCount() {
		JLabel PackageCountLabel = new JLabel("Current number of send-in order packages");
		GridBagConstraints gbc_PackageCountLabel = new GridBagConstraints();
		gbc_PackageCountLabel.insets = new Insets(0, 0, 5, 5);
		gbc_PackageCountLabel.gridx = 1;
		gbc_PackageCountLabel.gridy = 8;
		frmClientPortal.getContentPane().add(PackageCountLabel, gbc_PackageCountLabel);
	}


    /**
	 * create OperatingModeLabel JLabel()
	 */
	private void renderOperatingMode() {
		JLabel OperatingModeLabel = new JLabel("What mode of operation would you like?");
		GridBagConstraints gbc_OperatingModeLabel = new GridBagConstraints();
		gbc_OperatingModeLabel.insets = new Insets(0, 0, 5, 5);
		gbc_OperatingModeLabel.gridx = 1;
		gbc_OperatingModeLabel.gridy = 9;
		frmClientPortal.getContentPane().add(OperatingModeLabel, gbc_OperatingModeLabel);
	}


	/**
	 * Create btnGET JButton
	 */
	private JButton renderBtnSEND() {
		JButton btnSEND = new JButton("SEND");
		actionBtnSEND(btnSEND);
		GridBagConstraints gbc_btnSEND = new GridBagConstraints();
		gbc_btnSEND.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnSEND.insets = new Insets(0, 0, 5, 5);
		gbc_btnSEND.gridx = 12;
		gbc_btnSEND.gridy = 4;
		frmClientPortal.getContentPane().add(btnSEND, gbc_btnSEND);

		return btnSEND;
	}


	/**
	 *
	 */
	private void renderTextDisplay() {
		textDisplay = new JTextArea();
		GridBagConstraints gbc_textDisplay = new GridBagConstraints();
		gbc_textDisplay.gridwidth = 12;
		gbc_textDisplay.insets = new Insets(0, 0, 0, 5);
		gbc_textDisplay.fill = GridBagConstraints.BOTH;
		gbc_textDisplay.gridx = 1;
		gbc_textDisplay.gridy = 12;
		frmClientPortal.getContentPane().add(textDisplay, gbc_textDisplay);
		textDisplay.setEditable(false);
	}

	/**
	 * adds Action listener to CONNECT buttion
	 */
	private void actionBtnALIVE(JButton btnConnect) {
		btnConnect.addActionListener(e -> {
			String IP = txtIPReceiver.getText();
			try {
				int port = Integer.parseInt(txtPORTReceiver.getText());
			} catch (Exception ex) {
				textDisplay.setText("Invalid Request");
			}
		});
	}

	/**
	 * adds Action listener to GET buttion
	 */
	private void actionBtnSEND(JButton btnSEND) {
		btnSEND.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

                file_name = txtFILEInput.getText();
                IP = txtIPReceiver.getText();
                port_receiver = Integer.parseInt(txtPORTReceiver.getText());
                timeout = Integer.parseInt(txtTimeout.getText());

                is_reliable = reliableRadio.isSelected();

                try{
					//System.out.println("trying...");
					startSendThread(socket, file_name, timeout, is_reliable,
							port_receiver, IP, txtPackageCount);

				} catch (Exception ex){
					System.out.println(ex);
					textDisplay.setText("Invalid Request");
				}

			}
		});
	}

	/**
	 * adds Action listener to ISALIVE button
	 */
	private void actionBtnISALIVE(JButton btnISALIVE) {
		btnISALIVE.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					port_sender = Integer.parseInt(txtPORTSender.getText());
					IP = txtIPReceiver.getText();
					port_receiver = Integer.parseInt(txtPORTReceiver.getText());
					// Call the test_for_life static function from the Sender class
					// to assign the socket
					socket = Sender.test_for_life(port_sender, port_receiver, IP);
					if (socket != null){
						textDisplay.setText("Receiver at " +
								IP + ":" + port_receiver + " is alive");
					} else {
						textDisplay.setText("Receiver at " +
								IP + ":" + port_receiver +
								"is not alive. Please try a different port and IP");
					}
					// Get sender port, IP and receiver port
				} catch (Exception ex){
					textDisplay.setText("Invalid IP or Port");
				}

			}
		});
	}

	private void startSendThread(DatagramSocket socket,
								 String file_name,
								 int timeout,
								 boolean is_reliable,
								 int port_receiver, String IP,
								 JTextField txtPackageCount) {

		SwingWorker sw = new SwingWorker() {

			@Override
			protected Object doInBackground() throws Exception {
				Sender.send_file(
						socket, file_name, timeout, is_reliable,
						port_receiver, IP, txtPackageCount);

				return null;
			}
		};

		sw.execute();
	}



	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		// render Encapsulating frame widget
		renderClientPortal();

		// render input boxes for IP address() and PORT

        renderTextIPReceiver();
        renderIPReceiverLabel();

        renderTextPORTReceiver();
        renderPORTReceiverLabel();

        renderTextPORTSender();
        renderPORTSenderLabel();

        renderTextFILEInput();
        renderLblFILEInput();

        renderTextTimeout();
        renderTimeoutLabel(); 

        renderPackageCount();
        renderTextPackageCount();

        renderBtnALIVE();
		renderBtnSEND();

        renderOperatingMode();
        renderButtonOperatingMode();

		//Renders the Text Display for information from Server
		renderTextDisplay();
	}

}
