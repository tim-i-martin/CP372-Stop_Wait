This was a group project for a networking class.

We were required to implement a reliable file transfer protocol on top of DatagramSocket API in Java.

We had two parts to the project which run on different systems (Sender and Receiver).  The job of the application is to reliably deliver a file from Sender to Receiver 
(effectively mimicking a stop and wait protocol).  

In our Sender file we were asked to create two modes to send data, reliable and unreliable. Where with the unreliable send, we set a timeout time and simply did not 
send each 10th packet.  This allowed us to witness the time to send files in different modes of transport.

