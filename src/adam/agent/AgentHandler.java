package adam.agent;

import java.io.IOException;
import java.net.*;
import java.util.*;

/**
 * Message format should be byte[]{fromID, destID, msgType}
 */
public class AgentHandler implements Runnable {

    private static final int BUFFER_SIZE = 3;

    private String IP = getLAN();
    private byte agentType;
    private boolean _running = true;

    private HashMap<Byte, String> ipTable = new HashMap<>();
    private HashMap<Byte, Integer> portTable = new HashMap<>();
    private List<Byte> pingList = new LinkedList<>();
    private List<Byte> pongList = new LinkedList<>();

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
            // hard code Agents for now
            ipTable.put((byte) 5, this.IP);
            portTable.put((byte) 5, 2133);
            pongList.add((byte) 5);
//            ipTable.put((byte) 2, this.IP);
//            portTable.put((byte) 2, 2132);
//            pongList.add((byte) 2);
//            ipTable.put((byte) 11, this.IP);
//            portTable.put((byte) 11, 2134);
//            pongList.add((byte) 11);
            ipTable.put((byte) 4, this.IP);
            portTable.put((byte) 4, 2135);
            pingList.add((byte) 4);

            // TODO: add unique ID to HashMap and start a new agent with that ID, based on agentType
            Thread agent = null;

            int ping_port = 2135;
            int pingserver_port = 4446;
            int pong_port = 2133;
            int pongserver_port = 4444;

            byte uniqueID = (byte) (ipTable.size() + 1);
            uniqueID = (byte) 4;

            if (agentType == 1) {
                sock = new DatagramSocket(ping_port);
//                portTable.put(uniqueID, ping_port);
//                pingList.add(uniqueID);
                agent = new Thread(new PingAgent(sock, this.IP, pingserver_port, uniqueID));
                sock = new DatagramSocket(pingserver_port);
            } else if (agentType == 0) {
                sock = new DatagramSocket(pong_port);
//                portTable.put(uniqueID, pong_port);
//                pongList.add(uniqueID);
                agent = new Thread(new PongAgent(sock, this.IP, pongserver_port, uniqueID));
                sock = new DatagramSocket(pongserver_port);
            }
            if (agent == null) {
                System.err.println("Agent thread is null");
                System.exit(-3);
            }
            ipTable.put(uniqueID, this.IP);
            agent.start();

            System.out.println(ipTable.toString());
            System.out.println(portTable.toString());

            //for (int i=0; i<pongList.size(); i++) {
            while (this._running) {
                if (Thread.interrupted()) terminate();

                try {
                    // Receive
                    pack = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
                    sock.receive(pack);
                    // Interperet
                    byte[] data = pack.getData();
                    System.out.println("AgentHandler received packet. Data: " + Arrays.toString(data));
                    InetAddress host = InetAddress.getByName(ipTable.get(data[1]));
                    Integer port = portTable.get(data[1]);
                    // Reply
                    if (data[2] == (byte) 1)
                        pack = new DatagramPacket(data, data.length, host, port);
                    if (data[2] == (byte) 0)
                        pack = new DatagramPacket(data, data.length, host, port);
                    sock.send(pack);

                    // Reset buffer
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
        this._running = false;
    }

}
