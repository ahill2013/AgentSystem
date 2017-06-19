package adam.agent;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

class Agent {

    byte _myID;
    byte _destID;
    byte _type;

    DatagramSocket _sock;
    boolean _running = true;

    private final String _host;
    private final int _port;

    Agent(DatagramSocket sock, String host, int port, byte ID) throws SocketException {
            this._sock = sock;
            this._host = host;
            this._port = port;
            this._myID = ID;
    }

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

    void terminate() {
        _running = false;
    }

}
