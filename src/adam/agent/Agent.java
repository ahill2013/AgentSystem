package adam.agent;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

class Agent {

    byte _myID;
    byte _destID;
    byte _type;

    private DatagramSocket _sock;
    boolean _running = true;

    private final String _host;
    private final int _port;

    /**
     * Base class constructor for an agent
     * @param sock socket for UDP that listens on a specific port for packets
     * @param host IP address of the system
     * @param port port number open on the system. Send all packets here
     * @param ID unique ID of an agent
     */
    Agent(DatagramSocket sock, String host, int port, byte ID) {
            this._sock = sock;
            this._host = host;
            this._port = port;
            this._myID = ID;
    }

    /**
     * Sends a message to the system
     * @param message data to be sent
     */
    void sendMsg(byte[] message) {
        if (this._sock == null) {
            System.err.println("ERROR: sock is null");
            System.exit(-1);
        }

        try {
            final InetAddress host = InetAddress.getByName(this._host);
            //this._sock.getPort();
            DatagramPacket dp = new DatagramPacket(message, message.length, host, this._port);
            this._sock.send(dp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Listens and waits for a message to be received on the socket's port
     * @return byte[] of data that was received
     */
    byte[] recvMsg() {
        try {
            byte[] buffer = new byte[3];
            DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
            this._sock.receive(dp);
            return dp.getData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Tell the program to stop running
     */
    void terminate() {
        this._running = false;
    }

}
