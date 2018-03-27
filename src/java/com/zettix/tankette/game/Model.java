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
public class Model extends Object3D  implements ModelInterface {
    public enum Collider {TANK, MISSILE, I_DUNNO_STUFF};  // crude who-am-i needs thought.
    private Collider collider;
    private double radius;  // of sphere collider.
    private double velocity;
    private double rotation_speed;
    private boolean forward, back, left, right, moved;
    private int movecount;
    public int getMovecount() {return movecount; }
    public void setMovecount(int movecount) {this.movecount = movecount;}
    private Hitbox hitbox;
    private boolean done;
    private long ago = 0;
    private long age = 0;
    private long startTime = 0;

    public double getRadius() { return radius; }
    public void setRadius(double radius) { this.radius = radius; }

    public double getVelocity() { return velocity; }
    public void setVelocity(double velocity) { this.velocity = velocity; }

    public double getRotation_speed() { return rotation_speed; }
    public void setRotation_speed(double rotation_speed) { this.rotation_speed = rotation_speed; }

    public boolean isForward() { return forward; }
    public void setForward(boolean forward) { this.forward = forward; }

    public boolean isBack() { return back; }
    public void setBack(boolean back) { this.back = back; }

    public boolean isLeft() { return left; }
    public void setLeft(boolean left) { this.left = left; }

    public boolean isRight() { return right; }
    public void setRight(boolean right) { this.right = right; }

    public boolean isMoved() { return moved; }
    public void setMoved(boolean moved) { this.moved = moved; }

    public Hitbox getHitbox() { return hitbox; }
    public void setHitbox(Hitbox hitbox) { this.hitbox = hitbox; }

    public long getAgo() { return ago; }
    public void setAgo(long ago) { this.ago = ago; }

    public long getAge() { return age; }
    public void setAge(long age) { this.age = age; }

    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }
    
    
    public long getDelta(long now_in) {
        long result = now_in - ago;
        ago = now_in;
        return result;
    }
    
    public boolean isDone() {
        return done;
    }
    
    public void setDone() {
        done = true;
    }
    
    public void Update(long now, double delta) {  // should be like pure virtual function in c++
    }
    
    public void setCollider(Collider c) {collider = c;}
    public Collider getCollider() {return collider;}

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
