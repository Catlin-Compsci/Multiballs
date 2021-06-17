package networking.transit;

import networking.engine.ConnectedWorld;

import java.util.LinkedList;


/*
Types of messages:

----------SERVER -> CLIENT--------

PHYSICS/LEVEL:
Send all players/trippers (ID, LOCATION & VEL)
Send all physics objects
Send a single physics object
Object deleted
Object created
Player Joined

CONNECTIONS:


----------CLIENT -> SERVER---------

PHYSICS/LEVEL:
Player/Tripper/Spectator position
Object Created

CONNECTIONS:
Joined

 */

public class MessageBuilder {
    LinkedList<MessageChunk> chunks = new LinkedList();
    public static final String del = "/";

    public MessageBuilder() {
    }
    public MessageBuilder(MessageChunk... toAdd) {
        this();
        addChunks(toAdd);
    }
    public static MessageBuilder parse(String message) {
//        System.out.println("Parsing: \"" + message + "\"");
        MessageBuilder ret = new MessageBuilder();
        String[] chunks = message.split(del);
        for (String chunk : chunks) {
            ret.addChunks(ret.getChunk(chunk));
        }
        return ret;
    }

    public void act(ConnectedWorld world) {
//        chunks.forEach(MessageChunk::act);
        chunks.forEach((b)->{
            try {
                b.act(world);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public String getMessage() {
        StringBuilder message = new StringBuilder();
//        chunks.forEach((chunk)->message.append(chunk.getChunk()+del));
        chunks.forEach((chunk)->{
//            try {
            message.append(chunk.getChunk() + del);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        });
        return message.toString();
    }

    protected MessageChunk getChunk(String boi) {
//        System.out.println("parsing chunk: [" + boi + "]");
        String[] args = boi.split(MessageChunk.del,-1);
        if(args[0].equals("i")) return new MessageChunk.ToServerChunk.ElonMuskWantsToSendHisLocation(Integer.parseInt(args[1]),Double.parseDouble(args[2]),Double.parseDouble(args[3]),Double.parseDouble(args[4]),Double.parseDouble(args[5]));
        if(args[0].equals("j")) return new MessageChunk.ToServerChunk.Join(args[1]);
        if(args[0].equals("b")) return new MessageChunk.ToClientChunk.YouAre(Integer.parseInt(args[1]),Integer.parseInt(args[2]));
        if(args[0].equals("J")) return new MessageChunk.ToClientChunk.Join(Integer.parseInt(args[1]),Integer.parseInt(args[2]),args[3]);
        if(args[0].equals("l")) return new MessageChunk.ToClientChunk.Leave(Integer.parseInt(args[1]));
        if(args[0].equals("B")) return new MessageChunk.ToServerChunk.MakeBullet(Integer.parseInt(args[1]),Double.parseDouble(args[2]),Double.parseDouble(args[3]));
        if(args[0].equals("m")) return new MessageChunk.ToClientChunk.MakeBullet(Integer.parseInt(args[1]),Integer.parseInt(args[2]),Double.parseDouble(args[3]),Double.parseDouble(args[4]));
        if(args[0].equals("r")) return new MessageChunk.ToServerChunk.ImRotatinHea(Integer.parseInt(args[1]),Double.parseDouble(args[2]));
        if(args[0].equals("d")) return new MessageChunk.ToClientChunk.TimeToDIEEEBullet(Integer.parseInt(args[1]));
        if(args[0].equals("h")) return new MessageChunk.ToClientChunk.Health(Integer.parseInt(args[1]),Double.parseDouble(args[2]));
        if(args[0].equals("s")) return new MessageChunk.ToClientChunk.UgggOkBoringNameSetScore(Integer.parseInt(args[1]),Integer.parseInt(args[2]));
        if(args[0].equals("p")) return new MessageChunk.ToClientChunk.PlayerPoopooBreak(Integer.parseInt(args[1]),Boolean.parseBoolean(args[2]));

        else return null;
    }

    public void addChunks(MessageChunk... toAdd) {
        for (MessageChunk chunk : toAdd) {
            chunks.add(chunk);
        }
    }
}
