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

tankette.PlayerManager = function(model, scene, model_t) {
    var players = {};
    var model_ = model;
    var model_t_ = model_t;
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
        var threemodel = {};
        if (model_t_ === "t1") {
          threemodel = new tankette.Tank(model_, x, y, z, xr, yr, zr);
        } else {
          console.log("unknown model. " + playerid + " " + model_t_);
          return;
        }
        players[playerid] = threemodel;
        scene.add(threemodel.group);
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

    this.NetParse = function(jsonlist) {
        if (jsonlist === undefined) return;
        var tmp_players = this.GetPlayerIds();
        var tmp_players_hash = {};
        if (tmp_players === undefined) {
            console.log("No players yet");
        } else {
          for (var i = 0; i < tmp_players.length; i += 1) {
              tmp_players_hash[tmp_players[i]] = true;
          }
        }
        var in_players = jsonlist;
        // console.log("Parsing V1 message..." + in_players.length);
        for (var i = 0; i < in_players.length; i += 1) {
            // console.log("Player " + i);
            var in_p = in_players[i];
            if (tmp_players_hash.hasOwnProperty(in_p.id)) {
                this.UpdatePlayer(
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
              this.AddPlayer(
                        in_p.id,
                        parseFloat(in_p.x),
                        parseFloat(in_p.y),
                        parseFloat(in_p.z),
                        parseFloat(in_p.xr),
                        parseFloat(in_p.yr),
                        parseFloat(in_p.zr),
                        parseInt(in_p.col));
               console.log("Added player");
            }
        }
        var players_to_delete = Object.keys(tmp_players_hash);
        if (players_to_delete !== undefined) {
            for (var i = 0; i < players_to_delete.length; i += 1) {
                this.RemovePlayer(players_to_delete[i]);
                console.log("Removed player " + i);
            }
        }
    };
};
