/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zettix.tankette.server;

import com.zettix.tankette.game.Player;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.inject.Inject;
import javax.websocket.server.ServerEndpoint;
import javax.enterprise.context.ApplicationScoped;
import org.json.JSONObject;

/**
 *
 * @author sean
 */
@ApplicationScoped
@ServerEndpoint("/actions")
public class SocketServer {
    
    private static final Logger LOG = Logger.getLogger("SocketServer");
    
    @Inject
    private GameHandler sessionHandler;
    
    @OnOpen
    public void open(Session session) {
       InfoLog("Open!!ZZZZZZZZZZZ\n");
       sessionHandler.addSession(session);
    }

    @OnClose
    public void close(Session session) {
        InfoLog("Close!! ZZZZZZZZZZ\n");
        sessionHandler.removeSession(session);  
    }

    @OnError
    public void onError(Throwable error) {
        Logger.getLogger(SocketServer.class.getName()).log(Level.SEVERE, null, error);
    }

    @OnMessage
    public void handleMessage(String message, Session session) {
        //System.out.println("MSG:"  + message);
        JSONObject jsonMessage = new JSONObject(message);
        String msg = jsonMessage.getString("msg_type");
        if (null != msg) switch (msg) {
            case "but":
                String playerid = session.getId();
                if (playerid != null && playerid.length() > 0) {
                    Player p = sessionHandler.getPlayerById(playerid);
                    if (p != null) {
                        /*  Do not trust the player to send position info.
                        InfoLog("Got info ZZZZZZZZZ"); */
                        // TODO(sean): make defensive copy of player
                            p.setForward(jsonMessage.getBoolean("F"));
                            p.setBack(jsonMessage.getBoolean("B"));
                            p.setLeft(jsonMessage.getBoolean("L"));
                            p.setRight(jsonMessage.getBoolean("R"));
                            p.toggleturdle = jsonMessage.getBoolean("T");
                            p.togglefire = jsonMessage.getBoolean("A");
                            //sessionHandler.updatePlayerLocation(
                            //        sessionHandler.getPlayerById(playerid));
                            //sessionHandler.LogHits();
                            String tilename = jsonMessage.getString("t");
                            if (tilename != null && tilename.length() > 0) {
                                //System.out.println("Creating packet for tile " + tilename);
                                JSONObject json = sessionHandler.createTerrainTileMessage(tilename);
                                sessionHandler.sendToSession(session, json);
                            }
                        }
                    }
                    break;     
                default:
                    break;
        }
            
    } 
    
    void InfoLog(String msg) {
       // LOG.warning(msg);
       Logger.getLogger(SocketServer.class.getName()).log(Level.SEVERE, 
               null, msg); 
    }
}
