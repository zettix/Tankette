/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zettix.tankette.server;
import com.zettix.tankette.game.Dot;
import com.zettix.tankette.game.HitboxHandler;
import java.util.HashSet;
import java.util.Set;
import javax.websocket.Session;
import com.zettix.tankette.game.Player;
import com.zettix.tankette.game.Turdle;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.log4j.*;
import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.spi.JsonProvider;

/**
 *
 * @author sean
 */
@ApplicationScoped
public class RocketHandler {
    private final Set sessions = new HashSet<>();
    private final Set players = new HashSet<>();
    private final Set turdles = new HashSet<>();
    
    private final Timer timer = new Timer();
    private final HitboxHandler hitboxHandler;
    // Because random.
    private final Random rnd = new Random();
    private final long delayseconds = 2l;
    private final long period_ms = 100l;
    boolean doneloop = true;
    int doneloop_count = 0;
    int turdle_serial = 0;
    // private static final Logger LOG = Logger.getLogger(
    //            RocketHandler.class.getName());

    //private static final Logger LOG = LogManager.getLogger(RocketHandler.class.getName());
    
    public RocketHandler() {
        hitboxHandler = new HitboxHandler();
        this.ScheduleRunMe();
    }
    
    private void RunMe() {
        // every frame game loop
        // update collions/physics
        // distribute to clients:
        // positions.
        // terrain.
        // explosions.
        //score, etc.
        if (doneloop) {
           doneloop = false;
           updatePlayers();
           // InfoLog("Horey shett, it works.");
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
            RunMe();
        }
    }
    
    private void ScheduleRunMe() {
        
        timer.schedule(new MainLoopTimer(), delayseconds * 1000, 10);
    }
    
    public void updateCollisions() {
        hitboxHandler.DetectCollisions();
    }
    
    public void addSession(Session session) {
        try {
           sessions.add(session);
        } catch (Exception ex) {
            InfoLog("Could not add!!ZZZZZZZZZZZZZZZZ" + ex);
        }
        // addPlayer(session);
        //for (Iterator it = players.iterator(); it.hasNext();) {
        //    Player pi = (Player) it.next();
        //    JsonObject addMessage = createAddMessage(pi);
        //    sendToSession(session, addMessage);
        //}
        addPlayer(session);
        sendToSession(session, createGamePacket());
        Player p = getPlayerById(session.getId());
        //JsonObject addMe = createAddMessage(p);
        //sendToAllConnectedSessions(addMe);
        JsonObject regMessage = createRegisterMessage(p);
        sendToSession(session, regMessage);
    }
    
    private void sendToSession(Session session, JsonObject message) {
        try {
            session.getBasicRemote().sendText(message.toString());
        } catch (IOException ex) {
            sessions.remove(session);
            removePlayer(session.getId());
            Logger.getLogger(RocketHandler.class.getName()).log(Level.INFO, ex);
        }
    }
    public void removeSession(Session session) {
        /* 	at org.jboss.weld.bean.proxy.ProxyMethodHandler.getInstance(ProxyMethodHandler.java:125)
	at com.zettix.rocketsocket.RocketHandler$Proxy$_$$_WeldClientProxy.removeSession(Unknown Source)
	at com.zettix.rocketsocket.SocketServer.close(SocketServer.java:45)
        */ 
        String playerid = session.getId();
        removePlayer(playerid);
        sessions.remove(session);
        JsonObject delMe = createDelMessage(playerid);
        sendToAllConnectedSessions(delMe);
    }
    
    public List getPlayers() {
        return new ArrayList<>(players);
    }

    public void addPlayer(Session s) {
        Player p = new Player();
        p.setX(rnd.nextDouble() * 10.0f);
        p.setY(0.0f);
        p.setZ(rnd.nextDouble() * 10.0f);
        p.setXr(0.0f);
        p.setYr(rnd.nextDouble() * 3.14f);
        p.setZr(0.0f);
        p.setId(s.getId());
        players.add(p);
        hitboxHandler.AddPlayer(p);
    }
    
    public void addTurdle(String id) {
        Player p = getPlayerById(id);
        if (p.moved) {
          p.movecount++;
          if (p.movecount > 5) {
            Turdle t = new Turdle();
            t.setId("A" + turdle_serial++);
            t.setX(p.getX());
            t.setY(p.getY());
            t.setZ(p.getZ());
            t.setXr(p.getXr());
            t.setYr(p.getYr());
            t.setZr(p.getZr());
            turdles.add(t);
            p.movecount = 0;
          }
        }
    }
    
    public void removeTurdle(Turdle t) {        
          turdles.remove(t);
    }
    
    public void removePlayer(String id) {        
          players.remove(getPlayerById(id));
          hitboxHandler.DelPlayer(id);
    }

    public Player getPlayerById(String id) {
        for (Iterator it = players.iterator(); it.hasNext();) {
            Player p = (Player) it.next();
            if (p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }

    public Turdle getTurdleById(String id) {
        for (Iterator it = turdles.iterator(); it.hasNext();) {
            Turdle p = (Turdle) it.next();
            if (p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }
    
    private synchronized JsonObject createGamePacket() {
        JsonProvider provider = JsonProvider.provider();
        JsonArrayBuilder playerlist = provider.createArrayBuilder();
        for (Iterator it = players.iterator(); it.hasNext();) {
            Player p = (Player) it.next();
            int collision = 0;
            if (hitboxHandler.IsHit(p)) {
                collision = 1;
            }
            JsonObject pj = provider.createObjectBuilder()
              .add("id", p.getId())
              .add("x", p.getX())
              .add("y", p.getY())
              .add("z", p.getZ())
              .add("xr", p.getXr())
              .add("yr", p.getYr())
              .add("zr", p.getZr())
              .add("col", collision)                    
              .build();        
            playerlist.add(pj);
        }
        JsonArrayBuilder turdlelist = provider.createArrayBuilder();
        for (Iterator it = turdles.iterator(); it.hasNext();) {
            Turdle p = (Turdle) it.next();
            JsonObject pj = provider.createObjectBuilder()
              .add("id", p.getId())
              .add("x", p.getX())
              .add("y", p.getY())
              .add("z", p.getZ())
              .add("xr", p.getXr())
              .add("yr", p.getYr())
              .add("zr", p.getZr())
              .add("s", p.getScale())      
              .build();        
            turdlelist.add(pj);
        }
        JsonArrayBuilder dotlist = provider.createArrayBuilder();
        for (Iterator it = hitboxHandler.dots.iterator(); it.hasNext();) {
            Dot p = (Dot) it.next();
            JsonObject pj = provider.createObjectBuilder()
              .add("x", p.getX())
              .add("y", p.getY())
              .add("z", p.getZ())
              .build(); 
            dotlist.add(pj);
        }
        
        JsonObject packet = provider.createObjectBuilder()
                .add("msg_type", "V1")
                .add("playerlist", playerlist)
                .add("turdlelist", turdlelist)
                .add("dotlist", dotlist)
                .build();
        return packet;
        
    }
    
    private JsonObject createMoveMessage(Player p) {
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
    
    private void updateTurdles() {
        Set removals = new HashSet<>();
        for (Iterator it = turdles.iterator(); it.hasNext();) {
            Turdle t = (Turdle) it.next();
            t.age++;
            double f = (double) (t.age) * 0.005;
            t.setXr(f);
            t.setYr(f);
            t.setZr(f);
            t.setScale(Math.sin(f) * Math.sin(f) * 10.00 + 1.0);
            if (t.age > 10000) {
                removals.add(t);
            }
        }
        for (Iterator it = removals.iterator(); it.hasNext();) {
            turdles.remove(it.next());
        }
    }
    
    
    
    private void updatePlayers() {
        
        for (Iterator it = players.iterator(); it.hasNext();) {
            Player p = (Player) it.next();
            updatePlayerLocation(p);
            if (p.toggleturdle) {
               addTurdle(p.getId());
            }
        }
        DetectCollisions();
        for (Iterator it = players.iterator(); it.hasNext();) {
            Player p = (Player) it.next();
            if (hitboxHandler.IsHit(p)) {
                MoveUndo(p);
            }
        }
        updateTurdles();
        sendToAllConnectedSessions(createGamePacket());
        /*
        for (Iterator it = players.iterator(); it.hasNext();) {
            Player p = (Player) it.next();
            sendToAllConnectedSessions(createMoveMessage(p));
        }
        */
    }
    
    public void updatePlayerLocation(Player p) {
        double delta = 1.0;
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
    
    private void MoveUndo(Player p) {
        double delta = 2.0;
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
        for (Iterator it = sessions.iterator(); it.hasNext();) {
            Session session = (Session) it.next();
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