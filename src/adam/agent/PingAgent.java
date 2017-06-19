package adam.agent;

import java.net.DatagramSocket;
import java.util.Arrays;
import java.util.HashMap;

public class PingAgent extends Agent implements Runnable {

    private HashMap<Byte, String> pongAgentList = new HashMap<>();

    PingAgent(DatagramSocket sock, String host, int port, byte ID) {
        super(sock, host, port, ID);
        this._type = 1;
        //this._sock.setSoTimeout(5000);
    }

    @Override
    public void run() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::terminate));

        System.out.println("PingAgent[id=" + this._myID + "]: Looking for PongAgents...");
        if (lookForAgents()) {
            for (Byte agentID : pongAgentList.keySet()) {
                this._destID = agentID;
                System.out.println("PingAgent[id=" + this._myID + "]: Found PongAgent[id=" + this._destID + "]");
                System.out.println("PingAgent[id=" + this._myID + "]: Sending ping to PongAgent[id=" + this._destID + "]");
                this.sendMsg(new byte[]{this._myID, this._destID, this._type});
            }
            for (int i=0; i<pongAgentList.size(); i++) {
                byte[] data = this.recvMsg();
                System.out.println("PingAgent received packet. Data: " + Arrays.toString(data));
                if (data[1] == this._myID && data[2] != this._type)
                    System.out.println("PingAgent[id=" + this._myID + "]: Received pong from PongAgent[id=" + data[0] + "]");
            }

        } else
            System.out.println("No agents found...");
    }

    private boolean lookForAgents() {
        // TODO: actually look for agents
//        this.pongAgentList.put((byte) 2, "192.168.0.11");
        this.pongAgentList.put((byte) 5, "192.168.0.11");
//        this.pongAgentList.put((byte) 11, "192.168.0.11");
        return true;
    }

}
