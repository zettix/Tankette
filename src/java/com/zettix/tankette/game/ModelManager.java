/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zettix.tankette.game;

import com.zettix.tankette.game.interfaces.ModelInterface;
import com.zettix.tankette.game.interfaces.Object3dInterface;
import com.zettix.tankette.server.GameState;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manager for hitboxes.
 *
 * @author sean
 * @param <T>  Model class. Manage Models: Geometry and Hitboxes.
 */
public class ModelManager <T extends ModelInterface & Object3dInterface> {
    private final HashMap<String, T> models;
    private final GameState gameState;
    
    private static final Logger LOGGER = Logger.getLogger(ModelManager.class.getName());
    
    private long serial = 0l;
    
    private final DecimalFormat NF = new DecimalFormat("#####");

    public ModelManager(GameState g) {
        gameState = g;
        this.models = new HashMap<>();
    }
    
    public synchronized String GetNewSerial() {
        return "M" + NF.format(serial++);
    }
        
    public synchronized T getModelById(String id) {
        if (models.containsKey(id)) {
            return models.get(id);
        }
        return null;
    }
    
    public synchronized List<T> getModelsAsList() {
        return new LinkedList<>(models.values());
    }

    public synchronized void addModel(String id, T p) {
        if (models.containsKey(id)) {
            // TODO complain.
        } else {
            LOGGER.log(Level.INFO, "Adding model:{0}", p.toString()); 
            models.put(id, p);
        }
    }

    public synchronized void delModel(String id) {
        if (models.containsKey(id)) {
            if (Model.Collider.MISSILE == models.get(id).getCollider()) {
                Explosion e = new Explosion(gameState.getNow(), 1000, 5.0);
                e.copy((Object3D) models.get(id));
                e.setId(gameState.getEXPLOSIONMANAGER().GetNewSerial());
                gameState.getEXPLOSIONMANAGER().addModel(e.getId(), e);
            }
            models.remove(id);
            gameState.getHitboxHandler().DelModel(id);
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
    
    public synchronized void updateModels(long now, double delta) {
        List<String> dellist = new LinkedList<>();
        for(T t : models.values()) {
            t.Update(now, delta);
            if (t.isDone()) {
                String id = t.getId();
                LOGGER.info("Removing model:" + id + " :" + t.toString());
                dellist.add(id);
            }
        }
        dellist.forEach((id) -> {delModel(id);});
    }

    public List<String> getModelIdsAsList() {
       return new ArrayList<>(models.keySet());
    }
    
    public int getModelCount() {
        return models.size();
    }
}
