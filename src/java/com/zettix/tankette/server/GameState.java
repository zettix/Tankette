/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zettix.tankette.server;

import com.zettix.tankette.game.interfaces.GameStateInterface;
import com.zettix.tankette.game.Explosion;
import com.zettix.tankette.game.HitboxHandler;
import com.zettix.tankette.game.ModelManager;
import com.zettix.tankette.game.PlayerManager;
import com.zettix.tankette.game.Bike;
import com.zettix.tankette.game.Rocket;
import com.zettix.tankette.game.Terrain;
import com.zettix.tankette.game.Turdle;
import com.zettix.tankette.game.interfaces.AbstractTerrain;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import javax.websocket.Session;

/**
 *
 * @author sean
 */
public class GameState implements GameStateInterface {

    public Map<String, Session> getSessions() {
        return sessions;
    }

    public Set getPlayers() {
        return players;
    }

    public PlayerManager getPLAYERMANAGER() {
        return PLAYERMANAGER;
    }
    
    public ModelManager<Bike> getBIKESMANAGER() {
        return BIKESMANAGER;
    }

    public ModelManager<Rocket> getROCKETMANAGER() {
        return ROCKETMANAGER;
    }

    public Set<Turdle> getTURDLES() {
        return TURDLES;
    }

    public ModelManager<Explosion> getEXPLOSIONMANAGER() {
        return EXPLOSIONMANAGER;
    }

    public long getNow() {
        return now;
    }

    public HitboxHandler getHitboxHandler() {
        return hitboxHandler;
    }
    
    public AbstractTerrain getTERRAIN() {
        return TERRAIN;
    }
    
    public Timer getTimer() { return timer;}

    protected final HashMap<String, Session> sessions;
    protected final Set players;
    protected final PlayerManager PLAYERMANAGER;
    protected final ModelManager<Bike> BIKESMANAGER;
    protected final ModelManager<Rocket> ROCKETMANAGER;
    protected final Set<Turdle> TURDLES;
    protected final ModelManager<Explosion> EXPLOSIONMANAGER;
    protected final AbstractTerrain TERRAIN;
    long now;
    final protected Timer timer;
    final HitboxHandler hitboxHandler;
    
    public GameState() {
      timer = new Timer();
      sessions = new HashMap<>();
      players = new HashSet<>();
      hitboxHandler = new HitboxHandler();
      PLAYERMANAGER = new PlayerManager();
      BIKESMANAGER = new ModelManager<>(this);
      ROCKETMANAGER = new ModelManager<>(this);
      TURDLES = new HashSet<>();
      EXPLOSIONMANAGER = new ModelManager<>(this);
      TERRAIN = new Terrain();
      System.out.println("GameState init done!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }
}
