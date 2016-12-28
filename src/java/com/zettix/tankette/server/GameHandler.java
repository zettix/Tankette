/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zettix.tankette.server;

import com.zettix.tankette.game.HitBoxHandler;
import com.zettix.tankette.game.Model;
import com.zettix.tankette.game.ModelManager;
import javax.websocket.Session;
import com.zettix.tankette.game.Player;
import com.zettix.tankette.game.PlayerManager;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.spi.JsonProvider;

/**
 *
 * @author sean
 */
@ApplicationScoped
public class GameHandler {
    private final HashMap<String, Session> sessions = new HashMap<>();
    // private final Set players = new HashSet<>();
    private static final PlayerManager PLAYERMANAGER = new PlayerManager();
    private static final ModelManager ROCKETMANAGER = new ModelManager();
    
    private final Timer timer = new Timer();
    private final HitBoxHandler hitboxHandler;
    // Because random.
    private final Random rnd = new Random();
    private final long delayseconds = 2l;
    private long period_ms = 100l;
    boolean doneloop = true;
    int doneloop_count = 0;
    private long nano_origin = System.nanoTime();
    private final double timescaler = 0.0000001;
    // private static final Logger LOG = Logger.getLogger(
    //            GameHandler.class.getName());

    //private static final Logger LOG = LogManager.getLogger(GameHandler.class.getName());
    private boolean testrocket = false;
    

    private static final DecimalFormat DF = new DecimalFormat("#.##");
    
    
    public GameHandler() {
        hitboxHandler = new HitBoxHandler();
        this.ScheduleRunMe();
    }

    private synchronized void MainLoop() {
        long now = System.nanoTime();
        long deltatime = now - nano_origin;
        nano_origin = now;
        // every frame game loop
        // update collions/physics
        // distribute to clients:
        // positions.
        // terrain.
        // explosions.
        //score, etc.
        if (doneloop) {
           doneloop = false;
           updatePlayers(deltatime);
           // InfoLog("Horey shett, it works.");
           period_ms--;
           if (period_ms < 0) {
               if (testrocket == false) {
                   testrocket = true;
                   Model p = new Model();
                   p.setId(ROCKETMANAGER.GetNewSerial());
                   p.setX(10.0);
                   p.setY(10.0);
                   p.setZ(10.0);
                   ROCKETMANAGER.addModel(p);
               }
           }
           doneloop = true;
        } else {
            doneloop_count++;
            if (doneloop_count > 1000) {
                doneloop = true;
                doneloop_count = 0;
            }
        }
    }
    class MainLoopTimer extends TimerTask {
        @Override
        public void run() {
            MainLoop();
        }
    }

    private void ScheduleRunMe() {

        timer.schedule(new MainLoopTimer(), delayseconds * 1000, 10);
    }

    public void updateCollisions() {
        hitboxHandler.DetectCollisions();
    }

    public synchronized void addSession(Session session) {
        try {
           sessions.put(session.getId(), session);
        } catch (Exception ex) {
            InfoLog("Could not add!!ZZZZZZZZZZZZZZZZ" + ex);
        }
        addPlayer(session);
        Player p = PLAYERMANAGER.getPlayerById(session.getId());
        sendToSession(session, createGamePacket(p));
        JsonObject regMessage = createRegisterMessage(p);
        sendToSession(session, regMessage);
    }
    
    private void sendToSession(Session session, JsonObject message) {
        try {
            session.getBasicRemote().sendText(message.toString());
        } catch (IOException ex) {
            sessions.remove(session);
            removePlayer(session.getId());
            // Logger.getLogger(RocketHandler.class.getName()).log(Level.INFO, ex);
        }
    }
    public synchronized void removeSession(Session session) {
        /*  at org.jboss.weld.bean.proxy.ProxyMethodHandler.getInstance(ProxyMethodHandler.java:125)
  at com.zettix.rocketsocket.RocketHandler$Proxy$_$$_WeldClientProxy.removeSession(Unknown Source)
  at com.zettix.rocketsocket.SocketServer.close(SocketServer.java:45)
        */
        String playerid = session.getId();
        removePlayer(playerid);
        sessions.remove(playerid);
        JsonObject delMe = createDelMessage(playerid);
        // Do not send to disconnected player:
        for (Session s : sessions.values()) {
            sendToSession((Session) s, delMe);
        }
    }

    public synchronized void addPlayer(Session s) {
        Player p = new Player();
        p.setX(rnd.nextDouble() * 10.0f);
        p.setY(0.0f);
        p.setZ(rnd.nextDouble() * 10.0f);
        p.setXr(0.0f);
        p.setYr(rnd.nextDouble() * 3.14f);
        p.setZr(0.0f);
        p.setId(s.getId());
        PLAYERMANAGER.addPlayer(p);
        hitboxHandler.AddPlayer(p);
    }

    public synchronized void removePlayer(String id) {
          PLAYERMANAGER.delPlayer(id);
          hitboxHandler.DelPlayer(id);
    }

    // TODO: convert sessions to hashmap.    
    public synchronized Session getSessionById(String id) {
        if (sessions.containsKey(id)) {
                return sessions.get(id);
        }
        return null;
    }
    
    public synchronized Player getPlayerById(String id) {
        return (Player) PLAYERMANAGER.getPlayerById(id);
    }
    
    private synchronized JsonObject createGamePacket(Player forplayer) {
        JsonProvider provider = JsonProvider.provider();
        JsonArrayBuilder jplayerlist = provider.createArrayBuilder();
        
        List players = PLAYERMANAGER.getPlayerIdsAsList();
        for (Iterator it = players.iterator(); it.hasNext();) {
            Player p = (Player) PLAYERMANAGER.getPlayerById((String) it.next());

            int collision = 0;
            if (hitboxHandler.IsHit(p)) {
                collision = 1;
            }
            // TODO: Adjust precision of numbers.
            JsonObject pj = provider.createObjectBuilder()
              .add("id", p.getId())
              .add("x", DF.format(p.getX()))
              .add("y", DF.format(p.getY()))
              .add("z", DF.format(p.getZ()))
              .add("xr", DF.format(p.getXr()))
              .add("yr", DF.format(p.getYr()))
              .add("zr", DF.format(p.getZr()))
              .add("col", collision)
              .build();
            jplayerlist.add(pj);
        }
        
        JsonArrayBuilder jrocketlist = provider.createArrayBuilder();
        List rockets = ROCKETMANAGER.getModelIdsAsList();
        for (Iterator it = rockets.iterator(); it.hasNext();) {
            Model p = (Model) ROCKETMANAGER.getModelById((String) it.next());
            JsonObject rj = provider.createObjectBuilder()
              .add("id", p.getId())
              .add("x", DF.format(p.getX()))
              .add("y", DF.format(p.getY()))
              .add("z", DF.format(p.getZ()))
              .add("xr", DF.format(p.getXr()))
              .add("yr", DF.format(p.getYr()))
              .add("zr", DF.format(p.getZr()))
              .build();
            jrocketlist.add(rj);
        }
        
        // TODO !!!!!!!!!! for iterator rockets, iterator explosions...
        JsonObject packet = provider.createObjectBuilder()
                .add("msg_type", "V1")
                .add("playerlist", jplayerlist)
                .add("rocketlist", jrocketlist)
                .build();
        return packet;
    }

    private JsonObject createPosMessage(Player p) {
        int collision = 0;
        if (hitboxHandler.IsHit(p)) {
            collision = 1;
        }
        JsonProvider provider = JsonProvider.provider();
        JsonObject addMessage = provider.createObjectBuilder()
                .add("msg_type", "pos")
                .add("id", p.getId())
                .add("x",p.getX())
                .add("y",p.getY())
                .add("z",p.getZ())
                .add("xr",p.getXr())
                .add("yr",p.getYr())
                .add("zr",p.getZr())
                .add("col", collision)
                .build();
        return addMessage;
    }

    private JsonObject createRegisterMessage(Player p) {
        JsonProvider provider = JsonProvider.provider();
        JsonObject addMessage = provider.createObjectBuilder()
                .add("msg_type", "register")
                .add("id", p.getId())
                .build();
        return addMessage;
    }

    private JsonObject createAddMessage(Player p) {
        JsonProvider provider = JsonProvider.provider();
        JsonObject addMessage = provider.createObjectBuilder()
                .add("msg_type", "new")
                .add("id", p.getId())
                .add("x",p.getX())
                .add("y",p.getY())
                .add("z",p.getZ())
                .add("xr",p.getXr())
                .add("yr",p.getYr())
                .add("zr",p.getZr())
                .build();
        return addMessage;
    }
    
    private JsonObject createDelMessage(String id) {
        JsonProvider provider = JsonProvider.provider();
        JsonObject addMessage = provider.createObjectBuilder()
                .add("msg_type", "del")
                .add("id", id)
                .build();
        return addMessage;
    }

    private void updatePlayers(long deltatime) {
        
        List<Player> players = PLAYERMANAGER.getPlayersAsList();

        // Move
        for (Player p : players) {
            updatePlayerLocation(p, deltatime);
        }
        
        // Collide
        DetectCollisions();
        for (Player p : players) {
            if (hitboxHandler.IsHit(p)) {
                MoveUndo(p, deltatime);
            }
        }
                
        // Shoot
        for (Player p : players) {
            if (p.togglefire) {
                if (p.shoot_timeout > 0) {
                    p.shoot_timeout -= deltatime;
                } else {
                    // FIRE!!
                    p.ResetShootTimeout();
                }
            }
        }
        // Network
        for (Player p : players) {
            String id = p.getId();
            Session s = (Session) getSessionById(id);
            sendToSession(s, createGamePacket(p));
        }
    }

    public void updatePlayerLocation(Player p, long deltatime) {
        double delta = (double) (deltatime) * timescaler;
        p.moved = false;
        if (p.forward) {
            p.MoveForward(delta);
            p.moved = true;
        }
        if (p.back) {
            p.MoveBackward(delta);
            p.moved = true;
        }
        if (p.left) {
            p.MoveLeft(delta);
        }
        if (p.right) {
            p.MoveRight(delta);
        }
    }

    private void DetectCollisions() {
        hitboxHandler.DetectCollisions();
    }

    private void MoveUndo(Player p, long deltatime) {
        double delta = 2.0 * (double) (deltatime) * timescaler;
        //InfoLog(hitboxHandler.DetectCollisions());  // optimize by player?
        // simple: if collision on player, undo move.
          if (p.back) {
            p.MoveForward(delta);
          }
          if (p.forward) {
            p.MoveBackward(delta);
          }
          if (p.right) {
            p.MoveLeft(delta);
          }
          if (p.left) {
            p.MoveRight(delta);
          }
    }

    private void sendToAllConnectedSessions(JsonObject message) {
        // TODO: does this have to ack, they are TCP connections?
        for (Session session : sessions.values()) {
            sendToSession(session, message);
        }
    }

    public void LogHits() {
        InfoLog(hitboxHandler.GetHits());
    }
    
    public void InfoLog(String msg) {
        JsonProvider provider = JsonProvider.provider();
        JsonObject logMessage = provider.createObjectBuilder()
                .add("msg_type", "log")
                .add("log", msg)
                .build();
        sendToAllConnectedSessions(logMessage);
        //LOG.log(Level.INFO, msg);
    }
}