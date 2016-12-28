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
public class Model extends Object3D {
    public final double radius;
    public double nearest;
    public final double velocity;
    public final double rotation_speed;
    public final double[] hitbox = new double[6];
    public boolean forward, back, left, right, moved, toggleturdle;
    public int movecount;
    
    public void MoveForward(double delta) {
      double cosy = Math.cos(this.getYr());
      double siny = Math.sin(this.getYr());
      this.setX(getX() + delta * cosy * velocity);
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
    public Model() {
        radius = 3.0f;
        hitbox[0] = 0.0f;
        hitbox[1] = 6.0f;
        hitbox[2] = -0.5f;
        hitbox[3] = 0.5f;
        hitbox[4] = -0.5f;
        hitbox[5] = 0.5f;
        nearest = 0.0f;
        velocity = 1.0f;
        rotation_speed = 0.1f;
        forward = false;
        back = false;
        left = false;
        right = false;
        moved = false;
        movecount = 0;
    }
}
