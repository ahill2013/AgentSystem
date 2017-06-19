package adam.agent;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

/**
 * Message format should be byte[]{fromID, destID, msgType}
 */
public class AgentHandler implements Runnable {

    private static final int BUFFER_SIZE = 3;

    private String IP = getLAN();
    private final int ping_port = 2135;
    private final int pong_port = 2133;
    private final int server_port = 4446;
    private byte agentType;
    private boolean _running = true;

    private HashMap<Byte, String> agentTable = new HashMap<>();
    private List<String> pingList;
    private List<String> pongList;

    AgentHandler(byte type) {
        this.agentType = type;
    }

    @Override
    public void run() {
        try {
            DatagramSocket sock = null;
            DatagramPacket pack;
            Runtime.getRuntime().addShutdownHook(new Thread(this::terminate));

            // TODO: send broadcast and listen for replies. IF any replies, add to the HashMap
//            try {
//                byte[] buf = new byte[]{7};
//                InetAddress group = InetAddress.getByName("230.0.113.0");
//                pack = new DatagramPacket(buf, buf.length, group, this.server_port);
//                MulticastSocket multisock = new MulticastSocket(this.server_port);
//                multisock.joinGroup(group);
//                sock.send(pack);
//                multisock.setSoTimeout(5000);
//                pack = new DatagramPacket(buf, buf.length);
//                multisock.receive(pack);
//                System.out.println(Arrays.toString(pack.getData()));
//            } catch (SocketTimeoutException e) {
//                System.out.println("Socket timed out. Assuming no other systems active");
//            }
//            byte[] data = pack.getData();
//            for (int i=0; i<data[1]; i++) {
//
//            }

            // TODO: add unique ID to HashMap and start a new agent with that ID, based on agentType
            Thread agent = null;
            if (agentType == 1) {
                sock = new DatagramSocket(4444);
                agent = new Thread(new PingAgent(sock, this.IP, this.ping_port, (byte) (agentTable.size() + 1)));
            } else if (agentType == 0) {
                sock = new DatagramSocket(4446);
                agent = new Thread(new PongAgent(sock, this.IP, this.pong_port, (byte) (agentTable.size() + 1)));
            }
            if (agent == null) {
                System.err.println("Agent thread is null");
                System.exit(-3);
            }
            agentTable.put((byte) (agentTable.size() + 1), this.IP);
            agent.start();

            // hard code PongAgent as id=5 and id=2
            agentTable.put((byte) 5, this.IP);
            agentTable.put((byte) 2, this.IP);
            System.out.println(agentTable.toString());

            while (_running) {
                try {
                    // Receive
                    pack = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
                    sock.receive(pack);
                    // Interperet
                    byte[] data = pack.getData();
                    System.out.println("AgentHandler received packet. Data: " + Arrays.toString(data));
                    InetAddress host = InetAddress.getByName(agentTable.get(data[1]));
                    // Reply
                    if (data[2] == 1)
                        pack = new DatagramPacket(data, data.length, host, this.pong_port);
                    else if (data[2] == 0)
                        pack = new DatagramPacket(data, data.length, host, this.ping_port);
                    sock.send(pack);
                    // Reset buffer to prevent overflow
                    pack.setData(new byte[BUFFER_SIZE]);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            agent.join();
            sock.close();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    private String getLAN() {
        String IP_address = "";
        int count = 0 ;
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface current = interfaces.nextElement();
                //  System.out.println(current);
                if (!current.isUp() || current.isLoopback() || current.isVirtual()) continue;
                Enumeration<InetAddress> addresses = current.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress current_addr = addresses.nextElement();
                    if (current_addr.isLoopbackAddress()) continue;
                    if (current_addr instanceof Inet4Address &&  count == 0) {
                        IP_address = current_addr.getHostAddress() ;
                        //System.out.println(current_addr.getHostAddress());
                        count++;
                        break;
                    }
                }
            }
        } catch(SocketException SE) {
            SE.printStackTrace();
        }
        return  IP_address;
    }

    /**
     * Tell the program to stop running
     */
    private void terminate() {
        _running = false;
    }

}
