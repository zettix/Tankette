/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zettix.tankette.game;

/**
 *
 * @author sean
 */
public class Player extends Object3D {
    public final double radius;
    public double nearest;
    public final double velocity;
    public final double rotation_speed;
    public final double[] hitbox = new double[6];
    public boolean forward, back, left, right, moved, togglefire;
    public int movecount;
    public long shoot_timeout = 1000; // microseconds.
    public long hitpoints = 1000;
    
    
    public void ResetShootTimeout() {
        shoot_timeout = 1000;
    }
    
    public void MoveForward(double delta) {
      double cosy = Math.cos(this.getYr());
      double siny = Math.sin(this.getYr());
      this.setX(getX() - delta * cosy * velocity);
      this.setZ(getZ() + delta * siny * velocity);
    }
    public void MoveBackward(double delta) {
      double cosy = Math.cos(this.getYr());
      double siny = Math.sin(this.getYr());
      this.setX(getX() + delta * cosy * velocity);
      this.setZ(getZ() - delta * siny * velocity);  
    }
    
    public void MoveLeft(double delta) {
      setYr(getYr() + delta * rotation_speed);
    }

    public void MoveRight(double delta) {
      setYr(getYr() - delta * rotation_speed);  
    }
    /**
     * Start with a hitbox.
     */
    public Player() {
        radius = 3.0f;
        hitbox[0] = 0.0f;
        hitbox[1] = 6.0f;
        hitbox[2] = -0.5f;
        hitbox[3] = 0.5f;
        hitbox[4] = -0.5f;
        hitbox[5] = 0.5f;
        nearest = 0.0f;
        velocity =  .2f;
        rotation_speed = 0.1f;
        forward = false;
        back = false;
        left = false;
        right = false;
        moved = false;
        togglefire = false;
        movecount = 0;
    }
    
    // Transform 6 points (collision box) to player space
    // for axis-alighned tests.
    public void Invert(double[] points) {
        // first translate:
        points[0] -= getX();
        points[1] -= getX();
        points[2] -= getY();
        points[3] -= getY();
        points[4] -= getZ();
        points[5] -= getZ();
        // maybe z/y/x?
        // TODO rotations.   
    }
        public void Transform(double[] points) {
        // first translate:
        points[0] += getX();
        points[1] += getX();
        points[2] += getY();
        points[3] += getY();
        points[4] += getZ();
        points[5] += getZ();
        // maybe z/y/x?
        // TODO rotations.
    }
}
