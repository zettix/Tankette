/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zettix.players;

import java.util.HashMap;
import java.util.Map;
import com.zettix.graphics.gjkj.GJKIntersect;
import com.zettix.graphics.gjkj.util.V3;
import com.zettix.graphics.gjkj.hull.BoxHull;

/**
 *
 * @author sean
 */
class Hitbox {
    
    public final Map players = new HashMap<>();
    public boolean is_hit = false;
    double[]  box = new double[6];  // axis aligned hitbox
    public final Player player;
    public BoxHull boxHull;
    
    Hitbox(Player p) {
        player = p;
    }

    Hitbox() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public double TestHitCylinder(Player p) {
        // From paper... a line defined by points P --> Q
        // D = Q - P, or the direciton vector.
        // For some other point P, we can test that it is inside.
        
        return 0.0f;
    }
    
    public boolean TestHitGJK(Player p) {
        GJKIntersect gjksolver = new GJKIntersect(boxHull, p.hitbox.boxHull);
        return gjksolver.Intersect();
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
        // This is in 2 parts: Sphere test then GJK test.
        // Sphere test is fast, just multiply.  GJK is accurate but slower.
        if (TestHitSphere(p) < 0.0) {  // Sphere intersection
            if (TestHitGJK(p)) { // GJK intersection
                return true;
            }
        }
        // transform player hitbox to local coords.
       
        return false;
    }
}