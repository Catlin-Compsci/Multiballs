package networking.transit;

import engine.objects.Bullet;
import engine.objects.Player;
import engine.objects.VelocityThing;
import engine.properties.Colors;
import networking.engine.ConnectedWorld;
import server.networking.ServerNetworker;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.function.Supplier;

public interface MessageChunk {

    public char getPrefix();

    public String getChunk();

    public void act(ConnectedWorld world);

    public final static String del = ",";


    static DecimalFormat format = new Supplier<DecimalFormat>() {

        @Override
        public DecimalFormat get() {
            DecimalFormat toRet = new DecimalFormat();
            toRet.setMaximumFractionDigits(4);
            return toRet;
        }
    }.get();


//    List<Function<String[],MessageChunk> things = {
//            dfsdf[];
//    }

    interface ToServerChunk extends MessageChunk {

        class Join implements ToServerChunk {

            String name;
            int clientId;

            public Join(String name, int clientId) {
                this.name = name;
                this.clientId = clientId;
            }

            public Join(String name) {
                this.name = name;
            }

            @Override
            public char getPrefix() {
                return 'j';
            }

            @Override
            public String getChunk() {
                return getPrefix() + del + name;
            }

            @Override
            public void act(ConnectedWorld world) {
                // Init message with current game state (all current players & bullets)
                ServerMessageBuilder message = new ServerMessageBuilder(clientId);
                world.players.forEach(p -> message.addChunks(
                        new ToClientChunk.Join(p.getId(), Colors.idOf(p.color), p.name),
                        new ToClientChunk.Health(p.getId(), p.health),
                        new ToClientChunk.UgggOkBoringNameSetScore(p.getId(), p.score)
                ));
                world.bullets.forEach(b -> message.addChunks(new ToClientChunk.MakeBullet(b.parentPlayer.getId(), b.getId(), 0, 0)));

                System.out.println(name + " Joined the game");
                Color playerColor = Colors.takeRandom();
                Player newPlayer = new Player();
                newPlayer.color = playerColor;
                newPlayer.name = name;
                newPlayer.socketId = clientId;

                // Add player to wolrd
                world.players.add(newPlayer);
                // Add player to connected world and respond with player Id
                int connectedThingId = world.addThing(newPlayer);
                message.addChunks(new ToClientChunk.YouAre(connectedThingId, Colors.idOf(playerColor)));
                // Set player id in socket connection
                ServerNetworker.active.connections.get(clientId).setGamePlayerId(connectedThingId);

                // send join message to newly joined player
                ServerNetworker.active.queue(message);

                // send join message to current clients
                ServerNetworker.active.queue(new ServerMessageBuilder(clientId, true, new ToClientChunk.Join(connectedThingId, Colors.idOf(playerColor), name)));
            }
        }


        class ElonMuskWantsToSendHisLocation implements ToServerChunk {

            int id;
            double x;
            double xVel;
            double y;
            double yVel;

            public ElonMuskWantsToSendHisLocation(int id, double x, double xVel, double y, double yVel) {
                this.id = id;
                this.x = x;
                this.xVel = xVel;
                this.y = y;
                this.yVel = yVel;
            }

            @Override
            public char getPrefix() {
                return 'i';
            }

            @Override
            public String getChunk() {
                return getPrefix() + del + id + del + x + del + xVel + del + y + del + yVel;
            }

            @Override
            public void act(ConnectedWorld world) {
                VelocityThing thing = (VelocityThing) world.connectedThings.get(id);
                if (thing == null) return;
                thing.x = x;
                thing.y = y;
                thing.xVel = xVel;
                thing.yVel = yVel;
            }
        }

        class ImRotatinHea implements ToServerChunk {

            int playerId;
            double rot;

            public ImRotatinHea(int playerId, double rot) {
                this.playerId = playerId;
                this.rot = rot;
            }

            @Override
            public char getPrefix() {
                return 'r';
            }

            @Override
            public String getChunk() {
                return getPrefix() + del + playerId + del + rot;
            }

            @Override
            public void act(ConnectedWorld world) {
                Player player = (Player) world.connectedThings.get(playerId);
                if (player == null) return;
                player.rot = rot;
            }
        }

        class MakeBullet implements ToServerChunk {

            int playerThingId;
            double x;
            double y;

            public MakeBullet(int playerThingId, double x, double y) {
                this.playerThingId = playerThingId;
                this.x = x;
                this.y = y;
            }

            @Override
            public char getPrefix() {
                return 'B';
            }

            @Override
            public String getChunk() {
                return getPrefix() + del + playerThingId + del + x + del + y;
            }

            @Override
            public void act(ConnectedWorld world) {
                Player player = (Player) world.connectedThings.get(playerThingId);
                Bullet bullet = new Bullet(player, x, y);
                int id = world.addThing(bullet);

                // Send
                ServerNetworker.active.queue(new ServerMessageBuilder(ServerMessageBuilder.TO_ALL,
                        new ToClientChunk.MakeBullet(playerThingId, id, x, y),
                        new MessageChunk.ToServerChunk.ElonMuskWantsToSendHisLocation(id, bullet.x, bullet.xVel, bullet.y, bullet.yVel)
                ));

                world.bullets.add(bullet);
            }
        }
    }


    interface ToClientChunk extends MessageChunk {

        class Health implements ToClientChunk {

            int id;
            double health;

            public Health(int id, double health) {
                this.id = id;
                this.health = health;
            }

            @Override
            public char getPrefix() {
                return 'h';
            }

            @Override
            public String getChunk() {
                return getPrefix() + del + id + del + health;
            }

            @Override
            public void act(ConnectedWorld world) {
                Player player = (Player) world.connectedThings.get(id);
                if (player == null) return;
                player.health = health;
            }
        }

        class TimeToDIEEEBullet implements ToClientChunk {

            int id;

            public TimeToDIEEEBullet(int id) {
                this.id = id;
            }

            @Override
            public char getPrefix() {
                return 'd';
            }

            @Override
            public String getChunk() {
                return getPrefix() + del + id;
            }

            @Override
            public void act(ConnectedWorld world) {
                world.removeId(id);
            }
        }

        class MakeBullet implements ToServerChunk {

            int playerThingId;
            int bulletId;
            double x;
            double y;

            public MakeBullet(int playerThingId, int bulletId, double x, double y) {
                this.playerThingId = playerThingId;
                this.bulletId = bulletId;
                this.x = x;
                this.y = y;
            }

            @Override
            public char getPrefix() {
                return 'm';
            }

            @Override
            public String getChunk() {
                return getPrefix() + del + playerThingId + del + bulletId + del + x + del + y;
            }

            @Override
            public void act(ConnectedWorld world) {
                Player player = (Player) world.connectedThings.get(playerThingId);
                if (player == null) return;
                Bullet bullet = new Bullet(player);
                world.putThing(bulletId, bullet);
                world.bullets.add(bullet);
            }
        }

        class UgggOkBoringNameSetScore implements ToClientChunk {

            int playerId;
            int score;

            public UgggOkBoringNameSetScore(int playerId, int score) {
                this.playerId = playerId;
                this.score = score;
            }

            @Override
            public char getPrefix() {
                return 's';
            }

            @Override
            public String getChunk() {
                return getPrefix() + del + playerId + del + score;
            }

            @Override
            public void act(ConnectedWorld world) {
                Player player = (Player) world.connectedThings.get(playerId);
                if (player == null) return;
                player.score = score;
            }
        }

        class Join implements ToClientChunk {

            int id;
            int colorId;
            String name;

            public Join(int id, int colorId, String name) {
                this.id = id;
                this.colorId = colorId;
                this.name = name;
            }

            @Override
            public char getPrefix() {
                return 'J';
            }

            @Override
            public String getChunk() {
                return getPrefix() + del + id + del + colorId + del + name;
            }

            @Override
            public void act(ConnectedWorld world) {
                Player player = new Player();
                player.color = Colors.colors[colorId];
                player.name = name;
                world.putThing(id, player);
                world.players.add(player);
                System.out.println(name + " joined the game!");
            }
        }

        class PlayerPoopooBreak implements ToClientChunk {

            int playerId;
            boolean is;

            public PlayerPoopooBreak(int playerId, boolean is) {
                this.playerId = playerId;
                this.is = is;
            }

            @Override
            public char getPrefix() {
                return 'p';
            }

            @Override
            public String getChunk() {
                return getPrefix() + del + playerId + del + is;
            }

            @Override
            public void act(ConnectedWorld world) {
                Player player = (Player)world.connectedThings.get(playerId);
                if(player == null) return;
                if(is) player.knockout();
                else player.respawn();
            }
        }

        class Leave implements ToClientChunk {

            int id;

            public Leave(int id) {
                this.id = id;
            }

            @Override
            public char getPrefix() {
                return 'l';
            }

            @Override
            public String getChunk() {
                return getPrefix() + del + id;
            }

            @Override
            public void act(ConnectedWorld world) {
                System.out.println(id + " left");
                world.removeId(id);
            }
        }

        class YouAre implements ToClientChunk {

            int id;
            int colorId;

            public YouAre(int id, int colorId) {
                this.id = id;
                this.colorId = colorId;
            }

            @Override
            public char getPrefix() {
                return 'b';
            }

            @Override
            public String getChunk() {
                return getPrefix() + del + id + del + colorId;
            }

            @Override
            public void act(ConnectedWorld world) {
                world.putThing(id, world.me);
                world.me.color = Colors.colors[colorId];
                System.out.println("this must be the place");
            }
        }
    }
}


