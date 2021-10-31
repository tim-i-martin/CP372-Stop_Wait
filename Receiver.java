import javax.xml.crypto.Data;
import java.io.FileWriter;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Receiver {
    public static void main(String[] args) throws IOException {

        int argCounter = 0;

        // Set variables used for Receiver - base values for use in case if limited
        // input to command line parameters
        String sender_ip = "127.0.0.1", file_name = "received.txt";
        int receiver_port = 4444, sender_port = 4443;
        DatagramSocket dg_socket;
        DatagramPacket dg_packet;
        FileWriter fw = new FileWriter("write_to.txt", true);

        // Parse program arguments into key variables
        for (String output : args) {
            switch (argCounter) {
                case 0:
                    sender_ip = args[argCounter];
                    break;
                case 1:
                    receiver_port = Integer.parseInt(args[argCounter]);
                    break;
                case 2:
                    sender_port = Integer.parseInt(args[argCounter]);
                    break;
                case 3:
                    file_name = args[argCounter];
                    break;
            }
            argCounter++;
        }

        fw = new FileWriter(file_name);

        // establishes DatagramSocket at the receiver_port number on this maching
        dg_socket = new DatagramSocket(receiver_port);

        // these are the two buffers needed to create the DatagramPackets
        // one empty for the receipt of new data bytes
        // one non-empty for the sending bytes (not 100% sure why though)
        byte[] buf = new byte[2];
        byte[] send = {13, 18};
        // Creating the packet to fill upon receipt from sender
        dg_packet = new DatagramPacket(buf, buf.length);
        // packet receipt from sender
        dg_socket.receive(dg_packet);


        // test packet received:
        String msg = new String(buf);
        System.out.println("Received: " + msg);

        // sends initial response packet to Sender side w/ buffer send
        DatagramPacket send_dg_packet = new DatagramPacket(send,
                2, dg_packet.getAddress(), dg_packet.getPort());
        dg_socket.send(send_dg_packet);

    }

    private static void write_datagram_to_file(DatagramPacket dp, FileWriter fw) throws IOException {
        // instantiate byte arrays for data from packet and for data to convert to string
        byte[] dp_data = dp.getData();
        byte[] data = new byte[dp_data.length - 1];

        // -------------------------------------------
        // *TODO* validate based on first bit in packet here -- this behaviour is probably better outside of the fxn within the function calling write_datagram_to_file
        int sequence_number = (int) dp_data[0];
        // -------------------------------------------

        // copies byte array indices from 1 on in dp_data to data
        System.arraycopy(dp_data, 1, data, 0, dp_data.length);

        // instantiates String str from byte data in byte[] according to the UTF-8 charset
        String str = new String(data, StandardCharsets.UTF_8);
        // Writes character data to the file
        for (int i = 0; i < str.length(); i++){
            fw.write(str.charAt(i));
        }
    }
}
