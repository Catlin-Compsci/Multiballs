package client.networking;

import networking.ConnectedThing;
import networking.ConnectedWorld;
import networking.MessageBuilder;
import networking.MessageChunk;
import server.networking.ServerNetworker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientNetworker {

    ConnectedWorld world;
    public static ClientNetworker active;


    public ClientNetworker(ConnectedWorld world) {
        active = this;
        this.world = world;
    }

    Sender sender;
    LinkedBlockingQueue<MessageBuilder> toSend = new LinkedBlockingQueue<>();
    LinkedBlockingQueue<MessageBuilder> toSendLimited = new LinkedBlockingQueue<>(20);

    Accepter accepter;
    Socket socket;
    PrintStream send;
    BufferedReader rec;

    Handler handler;
    LinkedBlockingQueue<String> messages = new LinkedBlockingQueue<String>();

    public boolean initConnection(String ip, int port) {
        System.out.println("Connecting...");
        for (int i = 1; i <= 5; i++) {
            try {
                socket = new Socket(ip, port);
                rec = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                send = new PrintStream(socket.getOutputStream());
                return true;
            } catch(UnknownHostException e) {
                e.printStackTrace();
                System.out.println("HOST NOT FOUND! Check correct IP and port, and that the server is properly set up.");
            }
            catch (IOException e) {
//                e.printStackTrace();
                System.out.println("Failed to connect (Attempt " + i + " of 5)");
            }
        }
        return false;
    }

    public void start() {
        accepter = new Accepter();
        handler = new Handler();
        sender = new Sender(toSend);
        accepter.start();
        handler.start();
        sender.start();
        new Sender(toSendLimited).start();
    }

    public boolean queue(MessageBuilder message) {
        return toSend.offer(message);
    }
    public boolean queue(MessageChunk messageChunk) {
        return queue(new MessageBuilder(messageChunk));
    }
    public boolean queueLossy(MessageBuilder message) {
        return toSendLimited.offer(message);
    }
    public boolean queueLossy(MessageChunk chonk) {
        return queueLossy(new MessageBuilder(chonk));
    }

    class Accepter extends Thread {
        public void run() {
            while(true) {
//                System.out.println("accepter ticked");
                try {
                    String nuuu = rec.readLine();
                    if(nuuu!=null)messages.add(nuuu);
//                    System.out.println("message received: \""+nuuu+"\"");
                } catch (IOException e) {
                    e.printStackTrace();
                    initConnection(socket.getInetAddress().toString(),socket.getPort());
                    break;
                }
            }
        }
    }

    class Handler extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    MessageBuilder message = MessageBuilder.parse(messages.take());
                    message.act(world);
//                    System.out.println("message acted [" + message.getMessage() + "]");
                } catch (InterruptedException e) {e.printStackTrace();}
            }
        }
    }

    class Sender extends Thread {
        BlockingQueue<MessageBuilder> t;

        public Sender(BlockingQueue<MessageBuilder> t) {
            this.t = t;
        }

        @Override
        public void run() {
            while(true) {
                try {
                    MessageBuilder message = t.take();
//                    System.out.println("client sent [" + message.getMessage() + "]");
                    send.println(message.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                    initConnection(socket.getInetAddress().toString(),socket.getPort());
                    break;
                }
            }
        }
    }
}
