/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* global playerManager */
/* global packet_length */
/* global rocketManager */
/* global explosionManager */
/* global turdleManager */
/* global dotManager */

console.log("Websocket init");

//var socket = new WebSocket("ws://zettix.com:8889/Tankette/actions");
var socket = new WebSocket("ws://172.16.7.125:8080/Tankette/actions");
socket.onmessage = onMessage;

function onMessage(event) {
    var mm = JSON.parse(event.data);
    var msg_type = mm.msg_type;
    packet_length = event.data.length;
    if (msg_type === "pos") {
      playerManager.UpdatePlayer(
       mm.id,
       parseFloat(mm.x),
       parseFloat(mm.y),
       parseFloat(mm.z),
       parseFloat(mm.xr),
       parseFloat(mm.yr),
       parseFloat(mm.zr),
       parseInt(mm.col));
       console.log("Col: " + mm.col);
    } else if (msg_type === "new") {
      playerManager.AddPlayer(       
       mm.id,
       parseFloat(mm.x),
       parseFloat(mm.y),
       parseFloat(mm.z),
       parseFloat(mm.xr),
       parseFloat(mm.yr),
       parseFloat(mm.zr));
    } else if (msg_type === "register") {
        playerManager.RegisterWithServer(mm.id);
    } else if (msg_type === "del") {
        playerManager.RemovePlayer(mm.id);
    } else if (msg_type === "log") {
        console.log("Log:" + mm.log);
    } else if (msg_type === "V1") {
        websocket_packet_txt = mm.toString();
        var tmp_players = playerManager.GetPlayerIds();
        var tmp_players_hash = {};
        if (tmp_players === undefined) {
            console.log("No players yet");
        } else {
          for (var i = 0; i < tmp_players.length; i += 1) {
              tmp_players_hash[tmp_players[i]] = true;
          }
        }
        var in_players = mm.playerlist;
        // console.log("Parsing V1 message..." + in_players.length);
        for (var i = 0; i < in_players.length; i += 1) {
            // console.log("Player " + i);
            var in_p = in_players[i];
            if (tmp_players_hash.hasOwnProperty(in_p.id)) {
                playerManager.UpdatePlayer(
                        in_p.id,
                        parseFloat(in_p.x),
                        parseFloat(in_p.y),
                        parseFloat(in_p.z),
                        parseFloat(in_p.xr),
                        parseFloat(in_p.yr),
                        parseFloat(in_p.zr),
                        parseInt(in_p.col));
                        
                delete tmp_players_hash[in_p.id];
            } else {  // new player
              playerManager.AddPlayer(
                        in_p.id,
                        parseFloat(in_p.x),
                        parseFloat(in_p.y),
                        parseFloat(in_p.z),
                        parseFloat(in_p.xr),
                        parseFloat(in_p.yr),
                        parseFloat(in_p.zr),
                        parseInt(in_p.col));
            }
        }
        var players_to_delete = Object.keys(tmp_players_hash);
        if (players_to_delete !== undefined) {
            for (var i = 0; i < players_to_delete.length; i += 1) {
                playerManager.RemovePlayer(players_to_delete[i]); 
            }
        }
    
       
        // turdles.
        //if (turdleManager !== undefined) {
           var tmp_turdles = turdleManager.GetTurdleIds();
           var tmp_turdles_hash = {};
           if (tmp_turdles === undefined) {
              console.log("No turdles yet");
            } else { 
              for (var i = 0; i < tmp_turdles.length; i += 1) {
                  tmp_turdles_hash[tmp_turdles[i]] = true;
              }
            }
            var in_turdles = mm.turdlelist;
            //console.log("Turdle# " + in_turdles.length);
            if (in_turdles !== undefined) {
                for (var i = 0; i < in_turdles.length; i += 1) {
                    var in_p = in_turdles[i];
                    if (tmp_turdles_hash.hasOwnProperty(in_p.id)) {
                        turdleManager.UpdateTurdle(
                                in_p.id,
                                parseFloat(in_p.x),
                                parseFloat(in_p.y),
                                parseFloat(in_p.z),
                                parseFloat(in_p.xr),
                                parseFloat(in_p.yr),
                                parseFloat(in_p.zr),
                                parseFloat(in_p.s));

                        delete tmp_turdles_hash[in_p.id];
                    } else {  // new turdle
                      turdleManager.AddTurdle(
                                in_p.id,
                                parseFloat(in_p.x),
                                parseFloat(in_p.y),
                                parseFloat(in_p.z),
                                parseFloat(in_p.xr),
                                parseFloat(in_p.yr),
                                parseFloat(in_p.zr),
                                parseFloat(in_p.s));
                    }
                }
            }   
            var turdles_to_delete = Object.keys(tmp_turdles_hash);
            if (turdles_to_delete !== undefined) {
                for (var i = 0; i < turdles_to_delete.length; i += 1) {
                       turdleManager.RemoveTurdle(turdles_to_delete[i]); 
                }
            }
            
            // ROCKETS
           var tmp_rockets = rocketManager.GetModelIds();
           var tmp_rockets_hash = {};
           if (tmp_rockets === undefined) {
              console.log("No rockets yet");
            } else { 
              for (var i = 0; i < tmp_rockets.length; i += 1) {
                  tmp_rockets_hash[tmp_rockets[i]] = true;
              }
            }
            var in_rockets = mm.rocketlist;
            if (in_rockets !== undefined) {
                //console.log("Rockets: " + in_rockets.length);
                for (var i = 0; i < in_rockets.length; i += 1) {
                    var in_p = in_rockets[i];
                    if (tmp_rockets_hash.hasOwnProperty(in_p.id)) {
                        websocket_packet_txt = in_p.x;

                        rocketManager.UpdateModel(
                                in_p.id,
                                parseFloat(in_p.x),
                                parseFloat(in_p.y),
                                parseFloat(in_p.z),
                                parseFloat(in_p.xr),
                                parseFloat(in_p.yr),
                                parseFloat(in_p.zr));
                        delete tmp_rockets_hash[in_p.id];
                    } else {  // new rocket
                      rocketManager.AddModel(
                                in_p.id,
                                parseFloat(in_p.x),
                                parseFloat(in_p.y),
                                parseFloat(in_p.z),
                                parseFloat(in_p.xr),
                                parseFloat(in_p.yr),
                                parseFloat(in_p.zr));
                    }
                }
            }   
            var rockets_to_delete = Object.keys(tmp_rockets_hash);
            if (rockets_to_delete !== undefined) {
                for (var i = 0; i < rockets_to_delete.length; i += 1) {
                       rocketManager.RemoveModel(rockets_to_delete[i]); 
                }   
            }
            
            // DOTS
            var in_dots = mm.dotlist;
            if (in_dots !== undefined) {
                dotManager.RemoveDots();
                for (var i = 0; i < in_dots.length; i++) {
                    var in_p = in_dots[i];
                    dotManager.AddDot(
                            parseFloat(in_p.x),
                            parseFloat(in_p.y),
                            parseFloat(in_p.z));
                }
            }
            
            // EXPLOSIONS
          var tmp_explosions = explosionManager.GetModelIds();
          // console.log("Explosion ids: " + tmp_explosions);
          // console.log("Explosions: " + tmp_explosions.length);

           var tmp_explosions_hash = {};
           if (tmp_explosions === undefined) {
              console.log("No explosions yet");
            } else { 
              for (var i = 0; i < tmp_explosions.length; i += 1) {
                  tmp_explosions_hash[tmp_explosions[i]] = true;
              }
            }
            var in_explosions = mm.explosions;
            if (in_explosions !== undefined) {
                //console.log("Rockets: " + in_rockets.length);
                for (var i = 0; i < in_explosions.length; i += 1) {
                    var in_p = in_explosions[i];
                    var id = in_p[0];
                    //console.log("Incoming Explosion: " + id);
                    if (tmp_explosions_hash.hasOwnProperty(id)) {
                        //console.log("Updating explosion: " + id);
                        explosionManager.UpdateModel(
                                id,
                                parseFloat(in_p[1]),
                                parseFloat(in_p[2]),
                                parseFloat(in_p[3]), 0, 0, 0, 
                                parseFloat(in_p[4]));
                        delete tmp_explosions_hash[id];
                    } else {  // new explosion
                        //console.log("Adding Explosion: " + id);
                        explosionManager.AddModel(
                                id,
                                parseFloat(in_p[1]),
                                parseFloat(in_p[2]),
                                parseFloat(in_p[3]), 0, 0, 0, 
                                parseFloat(in_p[4]));
                    }
                }
            }   
            var explosions_to_delete = Object.keys(tmp_explosions_hash);
            if (explosions_to_delete !== undefined) {
                for (var i = 0; i < explosions_to_delete.length; i += 1) {
                       //console.log("Deleteting Explosion: " + explosions_to_delete[i]);

                       explosionManager.RemoveModel(explosions_to_delete[i]); 
                }   
            }
        //}
    } // if v1
};

function pushButtons(wasd) {
    var msg = {
        msg_type: "but",
        F: wasd.moveForward,
        B: wasd.moveBackward,
        L: wasd.moveLeft,
        R: wasd.moveRight,
        A: wasd.toggleFire,
        T: wasd.toggleTurdle};
    if (socket.readyState === socket.OPEN) {
      socket.send(JSON.stringify(msg));
    }
}
