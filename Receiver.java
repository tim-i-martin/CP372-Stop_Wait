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
        String sender_ip = "127.0.0.1", file_name = "write_to.txt";
        int receiver_port = 4444, sender_port = 4443;
        DatagramSocket dg_socket;
        DatagramPacket dg_packet;
        FileWriter fw;

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






        // *TODO* wrap this behaviour in an if-else statement based on the size of the packet??
        // *TODO* Incoming - have it constantly looping and waiting on input - no TimeOut int
        // *TODO* needed here
        while (true) {
            //-----------------------------------------------------------------------------
            // outer loop includes behaviour for the isAlive connection - this is required
            // for the sender to connnect
            //-----------------------------------------------------------------------------


            // establishes DatagramSocket at the receiver_port number on this machine
            dg_socket = new DatagramSocket(receiver_port);

            // This is the writer for the file
            fw = new FileWriter(file_name, true);

            // these are the two buffers needed to create the DatagramPackets
            // one empty for the receipt of new data bytes
            // one non-empty for the sending bytes (not 100% sure why though)
            byte[] buf = new byte[2];
            byte[] send = {13, 18};
            // Creating the packet to fill upon receipt from sender
            dg_packet = new DatagramPacket(buf, 2);
            // packet receipt from sender
            dg_socket.receive(dg_packet);

            // test packet received:
            String msg = new String(buf);

            // sends initial response packet to Sender side w/ buffer send
            DatagramPacket send_dg_packet = new DatagramPacket(send,
                    2, dg_packet.getAddress(), dg_packet.getPort());
            dg_socket.send(send_dg_packet);

            // this is the loop entered after the handshake isAlive connection
            // this loop will call the write datagram to file after validation
            int sequence_number = 0;

            boolean measure_start = true;
            long startTime = 0;
            while (true) {
                // this is the buffer and packet for file storage
                buf = new byte[18];
                dg_packet = new DatagramPacket(buf, buf.length);

                dg_socket.receive(dg_packet);

                // measures the start time only on the first loop
                if (measure_start) startTime = System.currentTimeMillis();

                // if the sequence number of the packet matches the expected sequence
                // number, then write the datagram to the file and change the sequence #
                if (dg_packet.getData()[0] == sequence_number) {
                    write_datagram_to_file(dg_packet, fw);
                    send_ack(dg_socket, send_dg_packet, sequence_number);
                    // to alternate between two sequence #s we use mod 2 on an
                    // incrementally increasing function
                    sequence_number = (sequence_number + 1) % 2;
                } else {
                    if (dg_packet.getData()[0] == 2) {
                        break;
                    }
                    send_ack(dg_socket, send_dg_packet, sequence_number);
                }
                measure_start = false;
            }
            long endTime = System.currentTimeMillis();
            long transferTime = endTime - startTime;
            System.out.println("File transfer completed in " + transferTime + "milliseconds");

            fw.flush();
            fw.close();

            dg_socket.close();
        }
    }

    private static void write_datagram_to_file(DatagramPacket dp, FileWriter fw) throws IOException {
        // instantiate byte arrays for data from packet and for data to convert to string
        byte[] dp_data = dp.getData();
        byte[] data = new byte[dp_data.length - 2];

        // copies byte array indices from 1 on in dp_data to data
        System.arraycopy(dp_data, 2, data, 0, data.length);

        // instantiates String str from byte data in byte[] according to the UTF-8 charset
        String str = new String(data, StandardCharsets.UTF_8);
        // Writes character data to the file
        fw.write(str);

    }

    private static void send_ack(DatagramSocket dg_socket,
                                 DatagramPacket send_dg_packet,
                                 int sequence_number) throws IOException {
        // sets packet with byte value of the sequence_number to return as an ACK
        send_dg_packet.setData(new byte[] {(byte) sequence_number, 0});
        dg_socket.send(send_dg_packet);
    }
}
