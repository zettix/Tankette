/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zettix.tankette.server;

import com.zettix.tankette.game.Dot;
import com.zettix.tankette.game.Explosion;
import com.zettix.tankette.game.Hitbox;
import com.zettix.tankette.game.HitboxHandler;
import com.zettix.tankette.game.Model;
import com.zettix.tankette.game.ModelManager;
import java.util.HashSet;
import java.util.Set;
import javax.websocket.Session;
import com.zettix.tankette.game.Player;
import com.zettix.tankette.game.PlayerManager;
import com.zettix.tankette.game.Rocket;
import com.zettix.tankette.game.Turdle;
import java.io.IOException;
import java.text.DecimalFormat;
// import java.math.BigDecimal;
import java.util.HashMap;
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
public class GameHandler extends GameState {    
    // Because random.
    private final Random rnd = new Random();
    private final long delayseconds = 2l;
    private long period_ms = 100l;
    boolean doneloop = true;
    int doneloop_count = 0;
    private long milli_origin = System.currentTimeMillis();
    int turdle_serial = 0;
    int rocket_serial = 0;
    int explosion_serial = 0;
    long deltatime = 1l;
    private static final Logger LOG = Logger.getLogger(
                GameHandler.class.getName());
    private boolean testrocket = false;
    private double delta;  // change in ms.

    private static final DecimalFormat DF = new DecimalFormat("#.##");
    
    
    public GameHandler() {    
        now = System.currentTimeMillis();
        this.ScheduleRunMe();
    }

    private synchronized void MainLoop() {
        now = System.currentTimeMillis();
        deltatime = now - milli_origin;
        milli_origin = now;
        delta = (double) (deltatime); // seconds

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
           period_ms--;  // not really ms.
           if (period_ms < 0) {
               if (testrocket == false) {
                   InfoLog("Adding test rocket!");
                   testrocket = true;
                   Rocket p = new Rocket();
                   int serial = rocket_serial++;
                   p.setId("Test Rocket " + serial);
                   p.setX(3.0 - serial * 6);
                   p.setY(3.0 - serial * 6);
                   p.setZ(5.0 - serial * 5);
                   getROCKETMANAGER().addModel(p.getId(), p);
                   InfoLog("Adding test rocket! XYZ: " + p.getX() + " " + p.getY() + p.getZ() + p.getId());

                   long secs =  deltatime * 100000l;
                   Explosion e = new Explosion(now, secs, 10.0);
                   serial = explosion_serial++;
                   e.setId("Test Explosion " + serial);
                   e.setX(21.0 + serial * 3.0);
                   e.setY(1.0 + serial * 3.0);
                   e.setZ(11.0 + serial * 3.0);
                   getEXPLOSIONMANAGER().addModel(e.getId(), e);
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

        getTimer().schedule(new MainLoopTimer(), delayseconds * 1000, 10);
    }

    public void updateCollisions() {
        getHitboxHandler().DetectCollisions();
    }

    public synchronized void addSession(Session session) {
        try {
           getSessions().put(session.getId(), session);
        } catch (Exception ex) {
            InfoLog("Could not add!!ZZZZZZZZZZZZZZZZ" + ex);
        }
        addPlayer(session);
        Player p = getPLAYERMANAGER().getPlayerById(session.getId());
        sendToSession(session, createGamePacket());
        JsonObject regMessage = createRegisterMessage(p);
        sendToSession(session, regMessage);
    }
    
    private void sendToSession(Session session, JsonObject message) {
        try {
            session.getBasicRemote().sendText(message.toString());
        } catch (IOException | IllegalStateException | NullPointerException ex) {
            getSessions().remove(session.getId());
            removePlayer(session.getId());
            // Logger.getLogger(RocketHandler.class.getName()).log(Level.INFO, ex);
        }
    }
    public synchronized void removeSession(Session session) {
        String playerid = session.getId();
        removePlayer(playerid);
        getSessions().remove(playerid);
        JsonObject delMe = createDelMessage(playerid);
        // Do not send to disconnected player:
        getSessions().values().stream().forEach((s) -> {
            sendToSession((Session) s, delMe);
        });
    }

    // public List<Player> getPlayers() {
     //   return new ArrayList<>(getPlayers());
    //}

    public synchronized void addPlayer(Session s) {
        Player p = new Player();
        p.setX(rnd.nextDouble() * 10.0f);
        p.setY(0.0f);
        p.setZ(rnd.nextDouble() * 10.0f);
        p.setXr(0.0f);
        p.setYr(rnd.nextDouble() * 3.14f);
        p.setZr(0.0f);
        p.setId(s.getId());
        getPLAYERMANAGER().addPlayer(p);
        hitboxHandler.AddModel(p);
    }

    public synchronized void removePlayer(String id) {
          getPLAYERMANAGER().delPlayer(id);
          getHitboxHandler().DelModel(id);
    }

    public void addTurdle(String id) {
        Player p = getPlayerById(id);
        if (p.isMoved()) {
          int movecount = p.getMovecount();
          p.setMovecount(movecount + 1);
          if (movecount > 5) {
            Turdle t = new Turdle();
            t.setId("A" + turdle_serial++);
            t.setX(p.getX());
            t.setY(p.getY() + 10.0);
            t.setZ(p.getZ());
            t.setXr(p.getXr());
            t.setYr(p.getYr());
            t.setZr(p.getZr());
            getTURDLES().add(t);
          }
        }
    }
    
    public void removeTurdle(Turdle t) {        
          getTURDLES().remove(t);
    }

    public void addRocket(String id) {
        Player p = getPlayerById(id);
        
        if (p.shoot_timeout < now) {
            Rocket t = new Rocket();
            t.setId("R" + rocket_serial++);
            t.setX(p.getX());
            t.setY(p.getY() + 2.5);
            t.setZ(p.getZ());
            t.setXr(p.getXr());
            t.setYr(p.getYr());
            t.setZr(p.getZr());
            t.setCollider(Model.Collider.MISSILE);
            t.MoveForward(80.3 / (t.getVelocity() * delta));
            InfoLog("Well Adding Rocket Y'all...  what? yeah: " + t.getId());
            getROCKETMANAGER().addModel(t.getId(), t);
            //EXPLOSIONS.addModel(t.getId(), t);
            hitboxHandler.AddModel(t);
            p.ResetShootTimeout(now);
        }   
    }

    public void removeRocket(String id) {        
       getROCKETMANAGER().delModel(id);
       hitboxHandler.DelModel(id);
    } 
    
    // TODO: convert sessions to hashmap.    
    public synchronized Session getSessionById(String id) {
        if (getSessions().containsKey(id)) {
                return getSessions().get(id);
        }
        return null;
    }
    
    public synchronized Player getPlayerById(String id) {
        return (Player) getPLAYERMANAGER().getPlayerById(id);
    }

    public Turdle getTurdleById(String id) {
        for (Iterator it = getTURDLES().iterator(); it.hasNext();) {
            Turdle p = (Turdle) it.next();
            if (p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }
    
    
    private synchronized JsonObject createGamePacket() {
        JsonProvider provider = JsonProvider.provider();
        
        JsonArrayBuilder jplayerlist = provider.createArrayBuilder();
       
        // PLAYERS
        List players = getPLAYERMANAGER().getPlayerIdsAsList();
        for (Iterator it = players.iterator(); it.hasNext();) {
            Player p = (Player) getPLAYERMANAGER().getPlayerById((String) it.next());

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
        // ROCKETS
        JsonArrayBuilder jrocketlist = provider.createArrayBuilder();
        List<String> rockets = getROCKETMANAGER().getModelIdsAsList();
        InfoLog("Rockets on my end: " + rockets.size());
        for (String s : rockets) {
            Model p = (Model) getROCKETMANAGER().getModelById(s);
            JsonObject rj = provider.createObjectBuilder()
              .add("id", p.getId())
              .add("x", DF.format(p.getX()))
              .add("y", DF.format(p.getY()))
              .add("z", DF.format(p.getZ()))
              .add("xr", DF.format(p.getXr()))
              .add("yr", DF.format(p.getYr()))
              .add("zr", DF.format(p.getZr()))
              .add("s", DF.format(p.getScale()))
              .build();
            jrocketlist.add(rj);
        }
 
        JsonArrayBuilder jturdlelist = provider.createArrayBuilder();
        for (Iterator it = getTURDLES().iterator(); it.hasNext();) {
            Turdle p = (Turdle) it.next();
            JsonObject pj = provider.createObjectBuilder()
              .add("id",p.getId())
              .add("x", DF.format(p.getX()))
              .add("y", DF.format(p.getY()))
              .add("z", DF.format(p.getZ()))
              .add("xr", DF.format(p.getXr()))
              .add("yr", DF.format(p.getYr()))
              .add("zr", DF.format(p.getZr()))
              .add("s", DF.format(p.getScale()))
              .build();        
            jturdlelist.add(pj);
        }
        JsonArrayBuilder jdotlist = provider.createArrayBuilder();
        for (Iterator it = hitboxHandler.dots.iterator(); it.hasNext();) {
            Dot p = (Dot) it.next();
            JsonObject pj = provider.createObjectBuilder()
              .add("x", DF.format(p.getX()))
              .add("y", DF.format(p.getY()))
              .add("z", DF.format(p.getZ()))
              .build();
            jdotlist.add(pj);
        }
        
        JsonArrayBuilder jexplosionlist = provider.createArrayBuilder();
        for (String id: getEXPLOSIONMANAGER().getModelIdsAsList()) {
            Explosion p = getEXPLOSIONMANAGER().getModelById(id);
            JsonArrayBuilder pj = (JsonArrayBuilder) provider.createArrayBuilder()
              .add(p.getId())
              .add(p.getX())
              .add(p.getY())
              .add(p.getZ())
              .add(p.getScale());

              //.build();
            jexplosionlist.add(pj);
        }
       
        JsonObject packet = provider.createObjectBuilder()
                .add("msg_type", "V1")
                .add("playerlist", jplayerlist)
                .add("dotlist", jdotlist)
                .add("turdlelist", jturdlelist)
                .add("rocketlist", jrocketlist)
                .add("explosions", jexplosionlist)
                .build();
        return packet;
    }

    /*
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
*/
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
        Set<Turdle> removals = new HashSet<>();
        for (Iterator it = getTURDLES().iterator(); it.hasNext();) {
            Turdle t = (Turdle) it.next();
            long t_age = t.getAge();
            t.setAge(t_age + 1);
            double f = (double) (t_age) * 0.005;
            t.setXr(f);
            t.setYr(f);
            t.setZr(f);
            t.setScale(Math.sin(f) * Math.sin(f) * 10.00 + 1.0);
            if (t_age > 10000) {
                removals.add(t);
            }
        }
        for (Iterator it = removals.iterator(); it.hasNext();) {
            getTURDLES().remove(it.next());
        }
    }
    
    private void updateRockets() {
        getROCKETMANAGER().updateModels(getNow());
    }
    
    private void updateExplosions() {
        getEXPLOSIONMANAGER().updateModels(now);
    }

    private void updatePlayers() {
        
        List<Player> players = getPLAYERMANAGER().getPlayersAsList();

        // Move/Shoot
        for (Player p : players) {
            updatePlayerLocation(p);
            if (p.toggleturdle) {
               addTurdle(p.getId());
            }
            if (p.togglefire) {
                    addRocket(p.getId());
            }
        }
        
        // Collide
        DetectCollisions();
        for (Player p : players) {
            if (hitboxHandler.IsHit(p)) {
                MoveUndo(p);
            }
        }
        try {
            updateTurdles();
            updateRockets();
            updateExplosions();
            // Network
            sendToAllConnectedSessions(createGamePacket());
        } catch(java.util.ConcurrentModificationException ex) {
            InfoLog("Fix concurrent mods! " + ex.toString());
        }
        /*
        for (Player p : players) {
            String id = p.getId();
            Session s = (Session) getSessionById(id);
            sendToSession(s, createGamePacket(p));
        }
        */
    }

    public void updatePlayerLocation(Player p) {
        p.setMoved(false);
        if (p.isForward()) {
            p.MoveForward(delta);
            p.setMoved(true);
        }
        if (p.isBack()) {
            p.MoveBackward(delta);
            p.setMoved(true);
        }
        if (p.isLeft()) {
            p.MoveLeft(delta);
        }
        if (p.isRight()) {
            p.MoveRight(delta);
        }
    }

    private void DetectCollisions() {
        try {
          hitboxHandler.DetectCollisions();
          for (String s : hitboxHandler.GetHitboxKeys()) {
              InfoLog("XXXXXXXXXXXXXXXXXXXXXXXXXX Getting hitbox " + s);
              Hitbox h = hitboxHandler.GetHitboxByName(s);
              if (h.is_hit) {  // a hit is a pair event but handled separately.
                Model m = h.model;
                String id = m.getId();
                for (Model m2 : h.GetHits()) {
                   switch (m2.getCollider()) {
                       case MISSILE:
                           // kaboom.
                           m.setDone();
                           m2.setDone();
                           Explosion e = new Explosion(now, 1000, 5.0);
                           e.copy(m);  // this is legit because model extends Object3D.
                           e.setId(getEXPLOSIONMANAGER().GetNewSerial());
                           getEXPLOSIONMANAGER().addModel(e.getId(), e);
                           // make explosion.
                           break;
                       case TANK:
                           break;
                           
                       default:
                            break;
                   }
                }
              }
          }
        } catch (java.lang.NullPointerException ex) {
          InfoLog("Null Pointer Exception " + ex.toString());
        }
    }

    private void MoveUndo(Player p) {
        //InfoLog(hitboxHandler.DetectCollisions());  // optimize by player?
        // simple: if collision on player, undo move.
          if (p.isBack()) {
            p.MoveForward(delta * 2.0);
          }
          if (p.isForward()) {
            p.MoveBackward(delta * 2.0);
          }
          if (p.isRight()) {
            p.MoveLeft(delta * 2.0);
          }
          if (p.isLeft()) {
            p.MoveRight(delta * 2.0);
          }
    }

    private void sendToAllConnectedSessions(JsonObject message) {
        // TODO: does this have to ack, they are TCP connections?
        getSessions().values().stream().forEach((session) -> {
            sendToSession(session, message);
        });
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
        LOG.log(Level.INFO, msg);
    }
}
