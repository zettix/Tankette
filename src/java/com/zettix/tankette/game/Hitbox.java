/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zettix.tankette.game;

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
    
    // public final Map<String, Model> models = new HashMap<>();
    public boolean is_hit = false;
    public final Model model;
    public BoxHull boxHull;
    
    Hitbox(Model p) {
        model = p;
    }

    Hitbox() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public double TestHitCylinder(Model p) {
        // From paper... a line defined by points P --> Q
        // D = Q - P, or the direciton vector.
        // For some other point P, we can test that it is inside.
        
        return 0.0f;
    }
    
    public boolean TestHitGJK(Model p) {
        GJKIntersect gjksolver = new GJKIntersect(boxHull, p.hitbox.boxHull);
        return gjksolver.Intersect();
    }
    
    public double TestHitSphere(Model p) {
        double xd = p.getX() - model.getX();
        double yd = p.getY() - model.getY();
        double zd = p.getZ() - model.getZ();
        double r2 = xd * xd + yd * yd + zd * zd;
        double rii = p.radius + model.radius;
        rii *= rii;
        // return r2 < rii;
        return r2 - rii;
    }
    
    
    public boolean TestHit(Model p) {
        // This is in 2 parts: Sphere test then GJK test.
        // Sphere test is fast, just multiply.  GJK is accurate but slower.
        if (TestHitSphere(p) < 0.0) {  // Sphere intersection
            if (TestHitGJK(p)) { // GJK intersection
                return true;
            }
        }
        // transform model hitbox to local coords.
       
        return false;
    }
}