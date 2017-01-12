/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
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
    public static long ONESEC = 1000l;  // milliseconds
    public boolean toggleturdle, togglefire;
    public long shoot_timeout;
    public int hitpoints = 100;
    
    public Player() {
        setCollider(Collider.TANK);
        setRadius(6.0);
        // Current model, from tank.js:
        //   this.hitbox_geo = new THREE.BoxGeometry(8, 4, 4.5);
        V3 dim = new V3(8.0, 4.0, 4.5);
        M4 mover = new M4().Identity().Move(-4.0, .0, -2.25);
        Hitbox h = new Hitbox(this);
        h.boxHull = new BoxHull(dim);
        h.boxHull.TransformObjectSpace(mover);
        setHitbox(h);
        
        setVelocity(.02);
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
    
    // Transform 6 points (collision box) to player space
    // for axis-alighned tests.
    public void Invert(double[] points) {
        // first translate:
        points[0] -= x;
        points[1] -= x;
        points[2] -= y;
        points[3] -= y;
        points[4] -= z;
        points[5] -= z;
        // maybe z/y/x?
        // TODO rotations.
        
    }
        public void Transform(double[] points) {
        // first translate:
        points[0] += x;
        points[1] += x;
        points[2] += y;
        points[3] += y;
        points[4] += z;
        points[5] += z;
        // maybe z/y/x?
        // TODO rotations.
    }
        
    public void ResetShootTimeout(long now) {
        shoot_timeout = ONESEC + now;
    }
}
