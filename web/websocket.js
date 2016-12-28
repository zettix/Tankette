/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* global playerManager */
/* global rocketManager */
/* global packet_length */

console.log("Websocket init");

var socket = new WebSocket("ws://zettix.com:8889/Tankette/actions");
//var socket = new WebSocket("ws://nyx:8080/BoxMove/actions");
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
        playerManager.HandleUpdateList(mm.playerlist);
        rocketManager.HandleUpdateList(mm.rocketlist);
    } // if v1
};

function pushButtons(wasd) {
    var msg = {
        msg_type: "but",
        F: wasd.moveForward,
        B: wasd.moveBackward,
        L: wasd.moveLeft,
        R: wasd.moveRight,
        A: wasd.toggleFire};
    if (socket.readyState === socket.OPEN) {
      socket.send(JSON.stringify(msg));
    }
}