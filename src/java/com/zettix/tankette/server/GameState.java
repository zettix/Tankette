/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zettix.tankette.server;

import com.zettix.tankette.game.Explosion;
import com.zettix.tankette.game.HitboxHandler;
import com.zettix.tankette.game.ModelManager;
import com.zettix.tankette.game.PlayerManager;
import com.zettix.tankette.game.Rocket;
import com.zettix.tankette.game.Turdle;
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
    
    public Timer getTimer() { return timer;}

    private HashMap<String, Session> sessions;
    private Set players;
    private PlayerManager PLAYERMANAGER;
    private ModelManager<Rocket> ROCKETMANAGER;
    private Set<Turdle> TURDLES;
    private ModelManager<Explosion> EXPLOSIONMANAGER;
    long now;
    private Timer timer;
    final HitboxHandler hitboxHandler;
    
    public GameState() {
      timer = new Timer();
      sessions = new HashMap<>();
      players = new HashSet<>();
      hitboxHandler = new HitboxHandler();
      PLAYERMANAGER = new PlayerManager();
      ROCKETMANAGER = new ModelManager<>(hitboxHandler);
      TURDLES = new HashSet<>();
      EXPLOSIONMANAGER = new ModelManager<>(hitboxHandler);   
    }
}
