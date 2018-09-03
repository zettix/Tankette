/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this templ ate file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zettix.tankette.game;

import com.zettix.graphics.gjkj.hull.BoxHull;
import com.zettix.graphics.gjkj.util.M4;
import com.zettix.graphics.gjkj.util.V3;
//import com.zettix.tankette.game.Hitbox;
/**
 *
 * @author sean
 */
public class Player extends Model {
    private String id;
    public static long ONESEC = 300l;  // milliseconds
    public boolean toggleturdle, togglefire;
    public long shoot_timeout;
    public int hitpoints = 100;
    
    public Player() {
        super();
        setCollider(Collider.TANK);
        setRadius(6.0);
        // Current model, from tank.js:
        //   this.hitbox_geo = new THREE.BoxGeometry(8, 4, 4.5);
        V3 dim = new V3(8.0, 4.0, 4.5);
        M4 mover = new M4().identity().move(-4.0, .0, -2.25);
        Hitbox h = new Hitbox(this);
        h.boxHull = new BoxHull(dim);
        h.boxHull.TransformObjectSpace(mover);
        setHitbox(h);
        
        setVelocity(.08);
        setRotation_speed(0.001);
        setForward(false);
        setBack(false);
        setLeft(false);
        setRight(false);
        setMoved(false);
        toggleturdle = false;
        togglefire = false;
        shoot_timeout = 0;
    }
        
    public void ResetShootTimeout(long now) {
        shoot_timeout = ONESEC + now;
    }
    
    @Override
    public String toString() {
        StringBuffer sb  = new StringBuffer("P:");
        sb.append(super.toString());
        return sb.toString();
    }
}