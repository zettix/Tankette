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
    private double x, y, z, xr, yr, zr;
    private String id;
    public static long ONESEC = 1000000000l;
    public boolean toggleturdle, togglefire;
    public long shoot_timeout;
    public int hitpoints = 100;
    
    public Player() {
        
        radius = 6.0f;
        hitbox = new Hitbox(this);
        // Current model, from tank.js:
        //   this.hitbox_geo = new THREE.BoxGeometry(8, 4, 4.5);
        V3 dim = new V3(8.0, 4.0, 4.5);
        M4 mover = new M4().Identity().Move(-4.0, .0, -2.25);
        hitbox.boxHull = new BoxHull(dim);
        hitbox.boxHull.TransformObjectSpace(mover);
        
        
        nearest = 0.0f;
        velocity = .2f;
        rotation_speed = 0.01f;
        forward = false;
        back = false;
        left = false;
        right = false;
        moved = false;
        toggleturdle = false;
        togglefire = false;
        movecount = 0;
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