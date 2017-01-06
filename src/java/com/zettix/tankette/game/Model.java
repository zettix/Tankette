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
    public enum Collider {TANK, MISSILE, I_DUNNO_STUFF};
    private Collider collider;
    public double radius;
    public double velocity;
    public double rotation_speed;
    public boolean forward, back, left, right, moved;
    public int movecount;
    public Hitbox hitbox;
    
    public void setCollider(Collider c) {
        collider = c;
    }
    
    public Collider getCollider() {
        return collider;
    }

    public void MoveForward(double delta) {
      double cosy = Math.cos(this.getYr());
      double siny = Math.sin(this.getYr());
      x -= delta * cosy * velocity;
      z += delta * siny * velocity;
    }
    public void MoveBackward(double delta) {
      double cosy = Math.cos(this.getYr());
      double siny = Math.sin(this.getYr());
      x += delta * cosy * velocity;
      z -= delta * siny * velocity;  
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
