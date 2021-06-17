package server.networking;


import engine.objects.Player;
import engine.properties.Colors;
import networking.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerNetworker {

    public static ServerNetworker active;

    ConnectedWorld world;

    public ServerNetworker(ConnectedWorld world) {
        active = this;
        this.world = world;
    }

    Queuer queuer;
    LinkedBlockingQueue<ServerMessageBuilder> toSend = new LinkedBlockingQueue<>();
    LinkedBlockingQueue<ServerMessageBuilder> toSendLimited = new LinkedBlockingQueue<>(20);

    ConnectionListener listener;
    public Map<Integer, ClientServerConnection> connections = Collections.synchronizedMap(new HashMap<>());
    int openConnectionId = 0;
    ServerSocket socket;

    ServerNetworker.Handler handler;
    LinkedBlockingQueue<FromClientRawMessage> messages = new LinkedBlockingQueue<>();

    String globalAddress;


    public void removeClientConnection(int id) {
        ClientServerConnection connection = connections.get(id);
        if (connection == null) return;
        try {
            connection.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        connections.remove(id);
        connection.leave();
    }


    public class FromClientRawMessage {
        int clientId;
        String message;

        public int getClient() {
            return clientId;
        }

        public String getMessage() {
            return message;
        }

        public FromClientRawMessage(int clientId, String message) {
            this.clientId = clientId;
            this.message = message;
        }
    }


    public boolean initServer(int port) {
        try {
            socket = new ServerSocket(port);
            globalAddress = InetAddress.getLocalHost().getHostAddress();
            System.out.println("Server socket opened [IP:" + globalAddress + " PORT:" + Config.port + "]");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void start() {
//        toSend.add(new MessageBuilder(new MessageChunk.ToServerChunk.Join(MessageChunk.ToServerChunk.Join.PlayerType.from(Client.playerType))));
        listener = new ConnectionListener();
        handler = new Handler();
        queuer = new Queuer(toSend);
        listener.start();
        handler.start();
        queuer.start();
        new Queuer(toSendLimited).start();
    }

    public class ClientServerConnection extends Thread {
        public Socket socket;
        PrintStream send;
        BufferedReader rec;
        int id = -1;
        int gamePlayerId;
        public void setGamePlayerId(int id) {gamePlayerId = id;}

//        LinkedBlockingQueue<MessageBuilder> toSend = new LinkedBlockingQueue<>(); // cap and make add block
//        Thread sender = new TickThread(() -> {
//            try {
//                send.println(toSend.take().getMessage());
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        });

        public void send(MessageBuilder message) {
            send.println(message.getMessage());
//            System.out.println("server sent [" + message.getMessage() + "]");
        }

        public ClientServerConnection(Socket socket) {
            try {
                this.socket = socket;
                send = new PrintStream(socket.getOutputStream());
                rec = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            connections.put(openConnectionId, this);
            setId(openConnectionId);
            openConnectionId++;
        }

        public void setId(int id) {
            this.id = id;
        }

//        public void queue(ServerMessageBuilder message) {
//            toSend.offer(message);
//        }
//        public void queue(MessageChunk smolBoi) {
//            queue(new ServerMessageBuilder(ServerMessageBuilder.TO_ALL,smolBoi));
//        }

        public void leave() {
            Player boi = (Player)world.connectedThings.get(gamePlayerId);
            System.out.println((boi).name + " disconnected");
            world.removeId(gamePlayerId);
            Colors.recycle(boi.color);
            queue((new MessageChunk.ToClientChunk.Leave(gamePlayerId)));
        }

        @Override
        public void run() {
//            sender.start();
            while (true) {
                try {
                    String line = rec.readLine();
                    if (line != null) messages.add(new FromClientRawMessage(id, line));
                } catch (SocketException e) {
                    removeClientConnection(id);
                    break;
                } catch (IOException e) {

                }
            }
        }
    }

    public boolean queue(ServerMessageBuilder message) {
        return toSend.add(message);
    }
    public boolean queue(MessageChunk message) {
        return queue(new ServerMessageBuilder(ServerMessageBuilder.TO_ALL,message));
    }

    public boolean queueLossy(ServerMessageBuilder message) {
        return toSendLimited.offer(message);
    }

    class ConnectionListener extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    System.out.println("Listening for connections...");
                    new ClientServerConnection(socket.accept()).start();
                    System.out.println("Connection handled!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

//    class Accepter extends Thread {
//        public void run() {
//            while(true) {
//                try {
//                    messages.add(rec.readLine());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    class Handler extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    ServerMessageBuilder message = ServerMessageBuilder.parse(messages.take());
                    message.act(world);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class Queuer extends Thread {

        BlockingQueue<ServerMessageBuilder> q;

        public Queuer(BlockingQueue<ServerMessageBuilder> q) {
            this.q = q;
        }

        @Override
        public void run() {

            while (true) {
                try {
                    ServerMessageBuilder send = q.take();
                    if (send.exclude) {
                        connections.forEach((id, c) -> {
                            if (id!=send.getTo()) c.send(send);
//                            if (id!=send.getTo()) c.queue(send);
                        });
                    } else {
                        if (send.getTo() == -1) connections.forEach((n, c) -> c.send(send));
//                        if (send.getTo() == -1) connections.forEach((n, c) -> c.queue(send));
                        else if (connections.containsKey(send.getTo())) connections.get(send.getTo()).send(send);
//                        else if (connections.containsKey(send.getTo())) connections.get(send.getTo()).queue(send);
                    }
//                    System.out.println("message [" + send.getMessage() + "] sent to user(s) [" + send.getTo() + "]");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
