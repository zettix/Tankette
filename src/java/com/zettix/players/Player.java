/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zettix.players;

import com.zettix.graphics.gjkj.hull.BoxHull;
import com.zettix.graphics.gjkj.util.M4;
import com.zettix.graphics.gjkj.util.V3;
import com.zettix.players.Hitbox;
/**
 *
 * @author sean
 */
public class Player {
    private double x, y, z, xr, yr, zr;
    private String id;
    
    // collission detection
    public final double radius;
    public Hitbox hitbox;
    public double nearest;
    public final double velocity;
    public final double rotation_speed;
    public String getId() {return id;}
    public double getX() { return x;}
    public double getY() { return y;}
    public double getZ() { return z;}
    public double getXr() { return xr;}
    public double getYr() { return yr;}
    public double getZr() { return zr;}
    public void setX(double f) {  this.x = f;}
    public void setY(double f) {  this.y = f;}
    public void setZ(double f) {  this.z = f;}
    public void setXr(double f) {  this.xr = f;}
    public void setYr(double f) {  this.yr = f;}
    public void setZr(double f) {  this.zr = f;}
    public void setId(String s) { this.id = s;}
    public boolean forward, back, left, right, moved, toggleturdle;
    public int movecount;
    
    
    public void MoveForward(double delta) {
      double cosy = Math.cos(yr);
      double siny = Math.sin(yr);
      x -= delta * cosy * velocity;
      z += delta * siny * velocity;
    }
    public void MoveBackward(double delta) {
      double cosy = Math.cos(yr);
      double siny = Math.sin(yr);
      x += delta * cosy * velocity;
      z -= delta * siny * velocity;
    }
    
    public void MoveLeft(double delta) {
      yr += delta * rotation_speed;
    }

    public void MoveRight(double delta) {
      yr -= delta * rotation_speed;
    }
    /**
     * Start with a hit box.
     */
    public Player() {
        radius = 3.0f;
        hitbox = new Hitbox(this);
        // Current model, from rocket.js:
        //   this.hitbox_geo = new THREE.BoxGeometry(6, 1.1, 1.1);
        // 
        V3 dim = new V3(6.1, 1.1, 1.1);
        M4 mover = new M4().Identity().Move(-3.0, -0.6, -0.6);
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
        movecount = 0;
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
}