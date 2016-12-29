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
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;


/**
 *
 * @author sean
 */
@ApplicationScoped
@ServerEndpoint("/actions")
public class SocketServer {
    
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
        InfoLog("Handle this! ZZZZZZZZZZZZZZZZZfo XXXXXXXXXXXX\n");
        try (JsonReader reader = Json.createReader(new StringReader(message))) {
            JsonObject jsonMessage = reader.readObject();
            String msg = jsonMessage.getString("msg_type");
            if (null != msg) switch (msg) {
                case "but":
                    String playerid = session.getId();
                    Player p = sessionHandler.getPlayerById(playerid);
                    /*
                    p.setX((Double.valueOf(jsonMessage.getString("x"))));
                    p.setY((Double.valueOf(jsonMessage.getString("y"))));
                    p.setZ((Double.valueOf(jsonMessage.getString("z"))));
                    p.setXr((Double.valueOf(jsonMessage.getString("xr"))));
                    p.setYr((Double.valueOf(jsonMessage.getString("yr"))));
                    p.setZr((Double.valueOf(jsonMessage.getString("zr"))));
                    InfoLog("Got info ZZZZZZZZZ"); */
                    p.forward = jsonMessage.getBoolean("F");
                    p.back = jsonMessage.getBoolean("B");
                    p.left = jsonMessage.getBoolean("L");
                    p.right = jsonMessage.getBoolean("R");
                    p.toggleturdle = jsonMessage.getBoolean("T");
                    p.togglefire = jsonMessage.getBoolean("A");
                    //sessionHandler.updatePlayerLocation(
                    //        sessionHandler.getPlayerById(playerid));
                    //sessionHandler.LogHits();
                    break;
                default:
                    break;
            }
            
        }    
    } 
    void InfoLog(String msg) {
       Logger.getLogger(SocketServer.class.getName()).log(Level.SEVERE, 
               null, msg); 
    }
}
