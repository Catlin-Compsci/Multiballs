package networking.transit;


import server.networking.ServerNetworker;

public class ServerMessageBuilder extends MessageBuilder {

    public static int TO_ALL = -1;
    int to;
    public boolean exclude = false;

    public int getTo() {
        return to;
    }

    public ServerMessageBuilder(int to, boolean exclude) {
        super();
        this.to = to;
    }
    public ServerMessageBuilder(int to) {
        this(to,false);
    }
    public ServerMessageBuilder(int to, MessageChunk... chunks) {
        this(to,false,chunks);
    }
    public ServerMessageBuilder(int to, boolean exclude, MessageChunk... chunks) {
        super(chunks);
        this.exclude = exclude;
        this.to = to;
    }


    public static ServerMessageBuilder parse(ServerNetworker.FromClientRawMessage m) {
        return parse(m.getClient(),m.getMessage());
    }
    public static ServerMessageBuilder parse(int clientId, String m) {
        ServerMessageBuilder ret = new ServerMessageBuilder(clientId);
        String[] chunks = m.split(del);
        for (String chunk : chunks) {
            if(chunk.charAt(0)=='j') {
                String[] args = chunk.split(MessageChunk.del);
                ret.addChunks(new MessageChunk.ToServerChunk.Join(args[1], clientId));
                continue;
            }
            ret.addChunks(ret.getChunk(chunk));
        }
        return ret;
    }
}
