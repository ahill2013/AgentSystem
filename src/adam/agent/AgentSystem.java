package adam.agent;

/**
 * Main class that runs the agent system
 */
public class AgentSystem {

    /**
     * Starts the agent system
     * @param args The agent you would like to run in this instance. Current options are PingAgent and PongAgent
     */
    public static void main(String[] args) {

        byte agentType = -1;

        // Handle command line arguments
        // TODO: Add more robust argument handling/parsing
        if (args.length != 1) {
            System.err.println("Usage: java AgentSystem.sh <agent class>");
            System.exit(-1);
        } else {
            if (args[0].equals("PingAgent"))
                agentType = 1;
            if (args[0].equals("PongAgent"))
                agentType = 0;
        }
        if (agentType == -1) {
            System.err.println("Invalid agent type specified. Choose from PingAgent/PongAgent");
            System.exit(-1);
        }

        // Create and start new agent system/handler
        final Thread agentHandler = new Thread(new AgentHandler(agentType));
        agentHandler.start();

        try {
            agentHandler.join();  // wait for the thread to die
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
