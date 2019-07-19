/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zettix.tankette.game;

import com.zettix.graphics.gjkj.hull.BoxHull;
import com.zettix.graphics.gjkj.util.*;
import com.zettix.tankette.game.interfaces.AbstractTerrain;

import java.util.logging.Logger;

/**
 *
 * @author sean
 */
public class Bike extends Model {
    private String id;
    public static long ONESEC = 300l;
    public int hitpoints = 100;
    public long max_age = 40000L;
    
    private static final Logger LOGGER = Logger.getLogger(Bike.class.getName());
    
    public Bike() {
        super();
        setCollider(Collider.BIKE);  // tank-tank: bump, tank-missle: boom
        setRadius(6.0);
        V3 dim = new V3(6.0, 6.0, 2.5);
        M4 mover = new M4().identity().move(-3.0, -3.0, -1.25);
        Hitbox h = new Hitbox(this);
        h.boxHull = new BoxHull(dim);
        h.boxHull.TransformObjectSpace(mover);
        setHitbox(h);
        // TODO(sean): bikes need AI, so a mood object or something, a controller.
        setVelocity(.01);
        setForward(true);
    }
    
    public void TrackTheGround(AbstractTerrain terrain) {
        double groundzero = terrain.getHeight(this.getX(), this.getZ());
        this.setY(groundzero);
    }
    
    @Override
    public void Update(long now, double delta) {
        long age = now - getStartTime();
        if (age > max_age) {
            LOGGER.info("Old bike: " + age + " vs. " + now + " - " + 
                    getStartTime());
            setDone();
        } else {
            MoveForward(delta); // le smoking gun.  delta is age, in increases.
            // So real delta is needed.
        }
    }  
    @Override
    public String toString() {
        StringBuilder sb  = new StringBuilder("B:");
        sb.append(super.toString());
        return sb.toString();
    }
    
}
