package adam.agent;

import java.net.DatagramSocket;
import java.util.Arrays;

public class PongAgent extends Agent implements Runnable {

    PongAgent(DatagramSocket sock, String host, int port, byte ID) {
        super(sock, host, port, ID);
        this._type = 0;
    }

    @Override
    public void run() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                terminate();
            }
        });

        System.out.println("PongAgent[id=" + this._myID + "]: Waiting for pings...");

        while (this._running) {
            if (Thread.interrupted()) terminate();
            byte[] data = this.recvMsg();
            System.out.println("PongAgent received packet. Data: " + Arrays.toString(data));
            if (data[1] == this._myID && data[2] != this._type) {
                this._destID = data[0];
                System.out.println("PongAgent[id=" + this._myID + "]: Received ping from PingAgent[id=" + this._destID + "]");
                System.out.println("PongAgent[id=" + this._myID + "]: Sending pong to PingAgent[id=" + this._destID + "]");
                this.sendMsg(new byte[]{this._myID, this._destID, this._type});
            }
        }
    }

}
