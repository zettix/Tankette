/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zettix.tankette.game;

import com.zettix.tankette.game.interfaces.ModelInterface;

/**
 * Base class for a Model.
 *
 * @author sean
 */
public class Model extends Object3D  implements ModelInterface {
    public enum Collider {TANK, MISSILE, EXPLOSION, I_DUNNO_STUFF};
    // crude who-am-i needs thought.
    private Collider collider;
    private double radius;  // of sphere collider.
    private double velocity;
    private double rotation_speed;
    private boolean forward, back, left, right, moved;
    private long movecount;
    public long getMovecount() {return movecount; }
    public void setMovecount(long movecount) {this.movecount = movecount;}
    private Hitbox hitbox;
    private boolean done;
    private long ago = 0;
    private long age = 0;
    private long startTime = 0;

    public final double getRadius() { return radius; }
    public final void setRadius(double radius) { this.radius = radius; }

    public final double getVelocity() { return velocity; }
    public final void setVelocity(double velocity) { this.velocity = velocity; }

    public final double getRotation_speed() { return rotation_speed; }
    public final void setRotation_speed(double rotation_speed) { this.rotation_speed = rotation_speed; }

    public final boolean isForward() { return forward; }
    public final void setForward(boolean forward) { this.forward = forward; }

    public final boolean isBack() { return back; }
    public final void setBack(boolean back) { this.back = back; }

    public final boolean isLeft() { return left; }
    public final void setLeft(boolean left) { this.left = left; }

    public final boolean isRight() { return right; }
    public final void setRight(boolean right) { this.right = right; }

    public final boolean isMoved() { return moved; }
    public final void setMoved(boolean moved) { this.moved = moved; }

    public final Hitbox getHitbox() { return hitbox; }
    public final void setHitbox(Hitbox hitbox) { this.hitbox = hitbox; }

    public final long getAgo() { return ago; }
    public final void setAgo(long ago) { this.ago = ago; }

    public final long getAge() { return age; }
    public final void setAge(long age) { this.age = age; }

    public final long getStartTime() { return startTime; }
    public final void setStartTime(long startTime) { this.startTime = startTime; }
    
    
    public final long getDelta(long now_in) {
        long result = now_in - ago;
        ago = now_in;
        return result;
    }
    
    public final boolean isDone() {
        return done;
    }
    
    public final void setDone() {
        done = true;
    }
    
    public void Update(long now, double delta) {  // should be like pure virtual function in c++
    }
    
    public final void setCollider(Collider c) {collider = c;}
    public final Collider getCollider() {return collider;}

    public void MoveForward(double delta) {
      double cosy = Math.cos(this.getYr());
      double siny = Math.sin(this.getYr());
      p.coords[0] -= delta * cosy * velocity;
      p.coords[2] += delta * siny * velocity;
    }
    public void MoveBackward(double delta) {
      double cosy = Math.cos(this.getYr());
      double siny = Math.sin(this.getYr());
      p.coords[0] += delta * cosy * velocity;
      p.coords[2] -= delta * siny * velocity;  
    }
    
    public void MoveLeft(double delta) {
      setYr(getYr() + delta * rotation_speed);
    }

    public void MoveRight(double delta) {
      setYr(getYr() - delta * rotation_speed);  
    }

    public Model() {
        this.collider = null;
        radius = 3.0f;
        velocity = 1.0f;
        rotation_speed = 0.1f;
        forward = false;
        back = false;
        left = false;
        right = false;
        moved = false;
        movecount = 0L;
        long now  = System.currentTimeMillis();
        setStartTime(now);
        setAgo(now);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("M:");
        sb.append(super.toString());
        return sb.toString();
    }
}
