/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zettix.tankette.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 *
 * @author sean
 */
public class PlayerManager { 
        // from ID to instance.
        private final HashMap<String, Player> players = new HashMap<>();
        
        public synchronized Player getPlayerById(String id) {
            if (players.containsKey(id)) {
                return players.get(id);
            }
            return null;
        }
        
        public synchronized void addPlayer(Player p) {
            String id = p.getId();
            if (players.containsKey(id)) {
                // TODO complain.
            } else {
                players.put(id, p);
            }
        }
        
        public synchronized void delPlayer(String id) {
            if (players.containsKey(id)) {
                players.remove(id);
            } else {
                // TODO: complain.
            } 
        }
        
        public synchronized void movePlayer(String id) {
            if (players.containsKey(id)) {
                // TODO: Game logic.
                
            } else {
                // TODO: complain.
            } 
        }
        
        public List<Player> getPlayersAsList() {
            return new ArrayList<>(players.values());
        }
        
        public List<String> getPlayerIdsAsList() {
           return new ArrayList<>(players.keySet());
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("Playermanger:\n");
            for (Entry<String, Player> entry : players.entrySet()) {
                sb.append(entry.getKey() + ":" + entry.getValue().toString());
            }
            return sb.toString();
        }
}
