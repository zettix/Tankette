/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zettix.tankette.game;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author sean
 */
public class ModelManager {
    private final HashMap<String, Model> models;
    
    private long serial = 0l;
    
    private final DecimalFormat NF = new DecimalFormat("#####");

    public ModelManager() {
        this.models = new HashMap<>();
    }
    
    public synchronized String GetNewSerial() {
        return NF.format(serial++);
    }
        
    public synchronized Model getModelById(String id) {
        if (models.containsKey(id)) {
            return models.get(id);
        }
        return null;
    }

    public synchronized void addModel(Model p) {
        String id = p.getId();
        if (models.containsKey(id)) {
            // TODO complain.
        } else {
            models.put(id, p);
        }
    }

    public synchronized void delModel(String id) {
        if (models.containsKey(id)) {
            models.remove(id);
        } else {
            // TODO: complain.
        } 
    }

    public synchronized void moveModel(String id) {
        if (models.containsKey(id)) {
            // TODO: Game logic.

        } else {
            // TODO: complain.
        } 
    }

    public List getModelIdsAsList() {
       return new ArrayList<>(models.keySet());
    }
}
