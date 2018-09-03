/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zettix.tankette.game.interfaces;

import com.zettix.tankette.game.Explosion;
import com.zettix.tankette.game.HitboxHandler;
import com.zettix.tankette.game.ModelManager;
import com.zettix.tankette.game.PlayerManager;
import com.zettix.tankette.game.Rocket;
import com.zettix.tankette.game.Turdle;
import com.zettix.tankette.game.Bike;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import javax.websocket.Session;

/**
 *
 * @author sean
 */
public interface GameStateInterface {
    public Map<String, Session> getSessions();
    public Set getPlayers();
    public PlayerManager getPLAYERMANAGER();
    public ModelManager<Bike> getBIKESMANAGER();
    public ModelManager<Rocket> getROCKETMANAGER() ;
    public Set<Turdle> getTURDLES();
    public ModelManager<Explosion> getEXPLOSIONMANAGER();
    public long getNow();
    public HitboxHandler getHitboxHandler();
    public Timer getTimer();
}
