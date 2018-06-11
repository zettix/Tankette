/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* global playerlist */
/* global THREE */
/* global controls */
/* global chasecam */
/* global CamUpdate */

var tankette = tankette = tankette || {};

console.log("PlayerManager Init");

tankette.PlayerManager = function(model, scene) {
    var players = {};
    var model_ = model;
    this.myself = THREE.Object3D();
    this.cam = {};
    
    this.SetCam = function(chasey) {
        this.cam = chasey;
    };
    
    this.RegisterWithServer = function(playerid) {
      console.log("Checking registered player " + playerid);
      if (players.hasOwnProperty(playerid)) {
          myself = players[playerid];
          
          console.log("I am : " + myself.group.position.x);
          if (controls === undefined) {
              console.log("Controls undefined!!!");
          } else {
             controls.object = myself;
         }
         if (chasecam === undefined) {
              console.log("chasecam undefined!!!");
          } else {
             chasecam.target = myself;
         }
      } else {
          console.log("Error! myself lost:" + playerid);
      };   
    };
    
    this.NumPlayers = function() {
        return Object.keys(players).length;
    };
    
    this.GetPlayer = function(playerid) {
      if (players.hasOwnProperty(playerid)) {
        return players[playerid];
      } else {
          console.log("Error! player not found:" + playerid);
      }
      return null;
    };
    
    this.GetPlayerIds = function() {
        // console.log("GetPlayerIds" + players.toString());
        var keylist = Object.keys(players);
        return keylist;
    };
    
    this.AddPlayer = function(playerid, x, y, z, xr, yr, zr) {
        var rockie = new tankette.Tank(model_, x, y, z, xr, yr, zr);
        
        players[playerid] = rockie;
        scene.add(rockie.group);
       console.log("Added player. " + playerid);
    };
    this.RemovePlayer = function(playerid) {
      if (players.hasOwnProperty(playerid)) {
          scene.remove(players[playerid].group);
          delete players[playerid];
      } else {
          console.log("Error! player not found:" + playerid);
      }
    };
      
    this.UpdatePlayer = function(playerid, x, y, z, xr, yr, zr, col) {
      if (players.hasOwnProperty(playerid)) {
          var p = this.GetPlayer(playerid);
          p.group.position.x = x;
          p.group.position.y = y;
          p.group.position.z = z;
          p.group.rotation.x = xr;
          p.group.rotation.y = yr;
          p.group.rotation.z = zr;
          if (col === 1) {
            p.HitMe();
         } else {
            p.MissMe();
          }
      } else {
          console.log("Error! Update player not found:" + playerid);
      }
      //if (this.cam.hasOwnProperty("CamUpdate")) {
        this.cam.CamUpdate();
      //}
    };
};
