import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Timer;

import javax.swing.JTextField;

public class Sender {

    /**
     * Tests to see if a connection can be established with the sender
     *
     * @param port_sender - this is the senders port
     * @param port_receiver - this is the receivers port
     * @param IP - this is the receiver IP
     * @return
     * @throws IOException - stuff goes wrong
     */
    public static DatagramSocket test_for_life(int port_sender, int port_receiver, String IP) throws IOException {
        DatagramSocket socket;
        try {
            // Create new socket and associate w/ correct port later
            socket = new DatagramSocket(port_sender);

            // Get the IP address for the Sender from the txtIPReceiver input field
            InetAddress address = InetAddress.getByName(IP);

            // Get the port from the txtPORTSender


            // buffers
            byte[] buf = {12, 13}; //send buffer
            byte[] buf1 = new byte[2]; // receive buffer
            DatagramPacket send_packet = new DatagramPacket(buf, 2, address, port_receiver);
            DatagramPacket receive_packet = new DatagramPacket(buf1, 2);

            // connect() method
            socket.connect(address, port_receiver);

            // test isBound() method
            System.out.println("IsBound : " + socket.isBound());
            // test isConnected() method
            System.out.println("isConnected : " + socket.isConnected());
            // test getInetAddress() method
            System.out.println("InetAddress : " + socket.getInetAddress());
            // test getPort() method
            System.out.println("Port : " + socket.getPort());
            // test getRemoteSocketAddress() method
            System.out.println("Remote socket address : " +
                    socket.getRemoteSocketAddress());
            // test getLocalSocketAddress() method
            System.out.println("Local socket address : " +
                    socket.getLocalSocketAddress());

            // send() method
            socket.send(send_packet);
            System.out.println("...packet sent successfully....");

            // receive() method
            socket.receive(receive_packet);
            System.out.println("Received packet data : " +
                    Arrays.toString(receive_packet.getData()));

            // getLocalPort() method
            System.out.println("Local Port : " + socket.getLocalPort());

            // getLocalAddress() method
            System.out.println("Local Address : " + socket.getLocalAddress());

            // setSOTimeout() method
            socket.setSoTimeout(50);

            // getSOTimeout() method
            System.out.println("SO Timeout : " + socket.getSoTimeout());
        } catch (IOException e) {

            return null;
        }

        return socket;
    }

    public static void send_file(DatagramSocket socket,
                                String file_name,
                                int timeout,
                                boolean is_reliable,
                                int port_receiver, String IP,
                                JTextField txtPackageCount) throws IOException {
        // if socket is null returns -1
        if (socket == null) {
            //return -1;
            txtPackageCount.setText("No Connection");
        }


        System.out.println("In Sending file");

        // if is_reliable, set modulus to 1, otherwise set to 10
        int modulus = is_reliable?1:10;

        // if socket is not null, pre-emptively set the timeout since we may use it
        socket.setSoTimeout(timeout);

        // entering this part of the code means that the socket existed
        // ============================================================

        // instantiates the reader for the file passed
        FileReader fileReader = new FileReader(file_name);

        // Get the IP address for the Sender from the txtIPReceiver input field
        InetAddress address = InetAddress.getByName(IP);

        byte[] buf = new byte[18]; //send buffer
        byte[] buf1 = new byte[2]; // receive buffer
        DatagramPacket send_packet = new DatagramPacket(buf, 18, address, port_receiver);
        DatagramPacket receive_packet_curr = new DatagramPacket(buf1, 2);

        System.out.println("after packet creation");

        // Constant loop reading from the file transferring that data to packets then sending
        // those packets
        int loop_condition = 1, loop_counter = 0, total_counter = 0;
        int offset = 0, length = 16, sequence_number = 0;


        System.out.println("before loop");


        while (loop_condition != -1) {

            System.out.println("looping...");

            loop_condition = read_from_file_to_datagram(fileReader,
                    send_packet, offset, length, sequence_number);

            System.out.println("established loop condition");

            // modulus will be 10 if this function is called w/ is_reliable == false
            // otherwise it will be 1 and the if statement will always evaluate true
            if (loop_counter % modulus == 0) {
                System.out.println("before send call");
                socket.send(send_packet);
                System.out.println("after send call");
            }
            // calls helper function to resend the packet if timeout is reached
            resend_packet_on_timeout(socket, receive_packet_curr, send_packet, total_counter);

            // increases the offset variable by length characters to allow the
            // FileReader to read the next length characters
            offset = offset + length;
            // alternates the sequence number state
            sequence_number = (sequence_number + 1) % 2;

            total_counter++;
            loop_counter++;
        };

        System.out.println("outside the loop");

        // Once loop condition is set to -1, it means that the end of the file has been reached
        // send a packet containing value 2 at it's head to indicate to the receiver that
        // the FileWriter can be closed and it can re-enter the await-connection state
        send_packet.setData(new byte[] {2, 0});
        socket.send(send_packet);
        resend_packet_on_timeout(socket, receive_packet_curr, send_packet, total_counter);

        txtPackageCount.setText(String.valueOf(total_counter));

        return;
    }

    /**
     * This function reads the "length" characters (length*2 bytes since each character
     * is 2 bytes) from the offset character on and populates the data of the
     * datagram (length*2 + 1 bytes due to the sequence number) passed to the array
     *
     * @param fileReader - the FileReader to read the file
     * @param dg - the DatagramPacket to fill with the byte data
     * @param offset - the offset to read the first byte in the file from
     * @param length - the length of the characters to read
     * @throws IOException - stuff goes wrong
     */
    private static int read_from_file_to_datagram(FileReader fileReader,
                                                  DatagramPacket dg,
                                                  int offset,
                                                  int length,
                                                  int sequence_number) throws IOException {
        char[] cbuf = new char[length];

        if (fileReader.read(cbuf, offset, length) != -1) {

            // instantiates array length*2 + 2 bytes long for transfer of characters
            // (2 bytes per char) along with sequence number (1 bytes) + empty byte
            byte[] dg_data = new byte[length*2 + 2];
            dg_data[0] =(byte) sequence_number;

            // stores the data from the character buffer read from the file and then
            // copies the length*2 inputs in dg_data into the last length*2 positions
            // in array dg_data
            byte[] data = new String(cbuf).getBytes(StandardCharsets.UTF_8);
            System.arraycopy(data, 0, dg_data, 2, length*2);

            // sets the data in the datagram
            dg.setData(dg_data);

            return 1;
        }

        fileReader.close();
        return -1;
    }

    /**
     * This file
     *
     * @param socket - the socket to recieve from and send through
     * @param receive_packet_curr - the DatagramPacket to put received info into
     * @param send_packet - the DatagramPacket to send data using
     * @throws IOException - stuff goes wrong
     */
    private static void resend_packet_on_timeout(DatagramSocket socket,
                                                 DatagramPacket receive_packet_curr,
                                                 DatagramPacket send_packet,
                                                 int total_counter) throws IOException {

        // initial parameters to loop and await the ACK response
        // loop condition to make sure that we hit socket.receive() repeatedly until
        // we receive an ACK
        boolean timeout_condition = true;
        while (timeout_condition) {
            try {
                // assigns received packet to the DatagramPacket specified
                socket.receive(receive_packet_curr);
                // if this line is hit it means that an exception was not thrown
                // i.e. that the packet was received, and we can exit the loop
                timeout_condition = false;

            } catch (SocketTimeoutException e) {
                socket.send(send_packet);
                total_counter++;
            }
        }
    }


}
