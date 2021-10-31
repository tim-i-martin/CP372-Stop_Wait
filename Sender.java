import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Timer;

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

    public static int send_file(DatagramSocket socket,
                                String file_name,
                                int timeout,
                                boolean is_reliable,
                                int port_sender, int port_receiver, String IP) throws IOException {
        // if socket is null returns -1
        if (socket == null) {
            return -1;
        }
        // if is_reliable, set modulus to 1, otherwise set to 10
        int modulus = is_reliable?1:10;

        // entering this part of the code means that the socket existed
        // ============================================================

        // instantiates the reader for the file passed
        FileReader fileReader = new FileReader(file_name);

        // Get the IP address for the Sender from the txtIPReceiver input field
        InetAddress address = InetAddress.getByName(IP);

        byte[] buf = new byte[1]; //send buffer
        byte[] buf1 = new byte[1]; // receive buffer
        DatagramPacket send_packet = new DatagramPacket(buf, 2, address, port_receiver);
        DatagramPacket receive_packet_curr = new DatagramPacket(buf1, 2);
        DatagramPacket receive_packet_prev;

        // Constant loop reading from the file transferring that data to packets then sending
        // those packets
        int loop_condition = 1, loop_counter = 0;
        int offset = 0, length = 16, sequence_number = 0;
        while (loop_condition != -1) {
            loop_condition = read_from_file_to_datagram(fileReader,
                    send_packet, offset, length, sequence_number);

            // modulus will be 10 if this function is called w/ is_reliable == false
            // otherwise it will be 1 and the if statement will always evaluate true
            if (loop_counter % modulus == 0) {
                socket.send(send_packet);
            }
            boolean timeout_condition = true;
            socket.setSoTimeout(timeout);
            while (timeout_condition) {
                try {
                    receive_packet_prev = receive_packet_curr;
                    socket.receive(receive_packet_curr);

                    timeout_condition = false;
                    // *Todo* perform some sort of validation on packet received to decide if
                    // the previous packet needs to be resent and then re-enter the loop
                    // by setting the packet value

                } catch (SocketTimeoutException e) {
                    socket.send(send_packet);
                }
            }
            loop_counter++;
        };

        return 1;
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
        int readable;
        char[] cbuf = new char[length];

        if ((readable=fileReader.read(cbuf, offset, length)) != -1) {

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

        return -1;
    }


}
