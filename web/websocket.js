/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* global bikeManager */
/* global playerManager */
/* global packet_length */
/* global rocketManager */
/* global explosionManager */
/* global turdleManager */
/* global dotManager */
/* global terrainManager */
/* global clock */

console.log("Websocket init");
// desktop:
var port = 8080;
var host = 'localhost';
// var host = 'zettix.com';
var socket = new WebSocket("ws://" + host +":" + port + "/Tankette/actions");
//var socket = new WebSocket("ws://zettix.com:8889/Tankette/actions");
// lap: var socket = new WebSocket("ws://172.16.7.80:11565/Tankette/actions");
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
    } else if (msg_type === "tile"){
        //console.log("TILE PACKET!!!!");
        terrainManager.AddTile(mm.n);
    } else if (msg_type === "T") {
       //console.log("T packet!!");
       var in_tiles = mm.n;
       if (in_tiles !== undefined) {
         terrainManager.Update(in_tiles, clock.getElapsed);
       } else {
           console.log("Nothing to terrian.");
       }
    } else if (msg_type === "register") {
        playerManager.RegisterWithServer(mm.id);
    } else if (msg_type === "del") {
        playerManager.RemovePlayer(mm.id);
    } else if (msg_type === "log") {
        console.log("Log:" + mm.log);
    } else if (msg_type === "V1") {
        websocket_packet_txt = mm.toString();
        playerManager.NetParse(mm.playerlist);
        turdleManager.NetParse(mm.turdlelist);
        rocketManager.NetParse(mm.rocketlist);
        if (mm.bikelist !== undefined) {
              //          console.log("BML " + mm.bikelist.length);
            bikeManager.NetParse(mm.bikelist);
        } else {
            console.log("No bike message." + websocket_packet_txt);
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
        T: wasd.toggleTurdle,
        t: terrainManager.Needed()
    };
    if (socket.readyState === socket.OPEN) {
      //  console.log(JSON.stringify(msg));
      socket.send(JSON.stringify(msg));
    }
}
