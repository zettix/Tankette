/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zettix.tankette.game;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author sean
 */
public class HitBox {    
    public final Map players = new HashMap<>();
    public boolean is_hit = false;
    double[]  box = new double[6];  // axis aligned hitbox
    public final Player player;
    
    HitBox(Player p) {
        System.arraycopy(p.hitbox, 0, box, 0, box.length);
        player = p;
    }

    HitBox() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public double TestHitCylinder(Player p) {
        // From paper... a line defined by points P --> Q
        // D = Q - P, or the direciton vector.
        // For some other point P, we can test that it is inside.
        
        return 0.0f;
    }
    
    public double TestHitSphere(Player p) {
        double xd = p.getX() - player.getX();
        double yd = p.getY() - player.getY();
        double zd = p.getZ() - player.getZ();
        double r2 = xd * xd + yd * yd + zd * zd;
        double rii = p.radius + player.radius;
        rii *= rii;
        // return r2 < rii;
        return r2 - rii;
    }
    
    
    public boolean TestHit(Player p) {
        // This is now GJK Collision algorithm.
        // transform player hitbox to local coords.
        
        double [] test_points = new double[6];
        System.arraycopy(p.hitbox, 0, test_points, 0, test_points.length);
        // text points
        p.Transform(test_points);
        player.Invert(test_points);
        if (((test_points[0] >= box[0]) && (test_points[0] <= box[1])) ||
                ((test_points[1] >= box[0]) && (test_points[1] <= box[1]))) {
            if (((test_points[2] >= box[2]) && (test_points[2] <= box[3])) ||
                ((test_points[3] >= box[2]) && (test_points[3] <= box[3]))) {
                if (((test_points[4] >= box[4]) && (test_points[4] <= box[5])) ||
                ((test_points[5] >= box[4]) && (test_points[5] <= box[5]))) {
                    return true;
                }
            }            
        }
        return false;
    }
   
}