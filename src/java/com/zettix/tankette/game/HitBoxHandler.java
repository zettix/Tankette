/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zettix.tankette.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 *
 * @author sean
 */
public class HitBoxHandler {
    private final HashMap playerHitBoxes = new HashMap<>();
    
    public void AddPlayer(Player p) {
        String id = p.getId();
        if (playerHitBoxes.containsKey(id)) {
            // pass, log error if possible.
        } else {
            HitBox h = new HitBox(p);
            playerHitBoxes.put(p.getId(), h);
        }
    }
    
    public String GetHits() {
        StringBuilder out = new StringBuilder();
        Set keyset = playerHitBoxes.keySet();
        List keylist = new ArrayList(keyset);
        int listsize = keylist.size();
        
        out.append("HitBoxHandler: ");
        // for now clear since lists set up.
        for (int i = 0; i < listsize; i++) {
            HitBox h = (HitBox) playerHitBoxes.get(keylist.get(i));
            out.append(i);
            out.append(" ");
            if (h.is_hit) {
                out.append("H");
            } else {
                out.append("-");                
            }
        }
        return out.toString();
    }

    public void DelPlayer(String id) {
        if (playerHitBoxes.containsKey(id)) {
            playerHitBoxes.remove(id);
        } else {
            // pass, log error if possible.
        }
    }
    
    public boolean IsHit(Player p) {
        String id = p.getId();
        if (playerHitBoxes.containsKey(id)) {
            HitBox h = (HitBox) playerHitBoxes.get(p.getId());
            return h.is_hit;
        } else {
            // pass, log error if possible.
        }
        return false;
    }
    
    public String DetectCollisions() {
        // basically compare all hitboxes to each other.  Good ole n squared.
        // meaning this can get out of hand fast.  easiest is to have two
        // indexes into the map, but I have to put the keys into a list
        // first then go 1..n+2..n,
        Set keyset = playerHitBoxes.keySet();
        List keylist = new ArrayList(keyset);
        int listsize = keylist.size();
        
        StringBuilder out = new StringBuilder();
        
        out.append("Detect: ");
        // for now clear since lists set up.
        for (int i = 0; i < listsize; i++) {
            HitBox h = (HitBox) playerHitBoxes.get(keylist.get(i));
            h.is_hit = false;
        }
        
        for (int i = 0; i < listsize - 1; i++) {
            HitBox h = (HitBox) playerHitBoxes.get(keylist.get(i));
            for (int j = i + 1; j < listsize; j++) {
                HitBox h2 = (HitBox) playerHitBoxes.get(keylist.get(j));
                Player p = h2.player;
                Player pp = h.player;
                out.append("[ ");
                out.append(i);
                out.append(" p1: ");
                out.append(p.getId());
                out.append(" ~");
                out.append(j);
                out.append(" p2: ");
                out.append(pp.getId());

                if (0.0f < h.TestHitSphere(p)) {
                    //pass
                    out.append(" Miss] ");
                } else {
                    // do stuff, maybe add who hit whom later.
                    h.is_hit = true;
                    h2.is_hit = true;
                    out.append(" Hit!]");
                }
                 /*p = h.player;
                 if (h2.TestHitSphere(p)) {
                    // do stuff, maybe add who hit whom later.
                    h.is_hit = true;
                    h2.is_hit = true;
                } */
            }
        }
        return out.toString();
    }  
    
}
