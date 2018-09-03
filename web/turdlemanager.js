/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/* global playerlist */
/* global THREE */
/* global controls */
/* global chasecam */


var tankette = tankette = tankette || {};

tankette.TurdleManager = function(model, scene) {
    var players = {};
    var model_ = model;
    var self = this;
        
    this.GetTurdle = function(playerid) {
      if (players.hasOwnProperty(playerid)) {
        return players[playerid];
      } else {
          console.log("Error! player not found:" + playerid);
      }
      return null;
    };
    
    this.NumTurdles = function() {
        return Object.keys(players).length;
    };
    
    
    this.GetTurdleIds = function() {
        var keylist = Object.keys(players);
        return keylist;
    };
    
    this.AddTurdle = function(playerid, x, y, z, xr, yr, zr, s) {
        var rockie = new tankette.Turdle(model_, x, y, z, xr, yr, zr, s);
        
        players[playerid] = rockie;
        scene.add(rockie.group);
       console.log("Checking registered player " + Object.keys(players));
    };
    this.RemoveTurdle = function(playerid) {
      if (players.hasOwnProperty(playerid)) {
          scene.remove(players[playerid].group);
          delete players[playerid];
      } else {
          console.log("Error! player not found:" + playerid);
      }
    };
      
    this.UpdateTurdle = function(playerid, x, y, z, xr, yr, zr, s) {
      if (players.hasOwnProperty(playerid)) {
          var p = this.GetTurdle(playerid);
          p.group.position.x = x;
          p.group.position.y = y;
          p.group.position.z = z;
          p.group.rotation.x = xr;
          p.group.rotation.y = yr;
          p.group.rotation.z = zr;
          p.group.scale.x = s;
          p.group.scale.y = s;
          p.group.scale.z = s;
      } else {
          console.log("Error! Update turdle not found:" + playerid);
      }
    };

    this.NetParse = function(jsonlist) {
      if (jsonlist === undefined) return;
      var tmp_turdles = this.GetTurdleIds();
      var tmp_turdles_hash = {};
      if (tmp_turdles === undefined) {
        console.log("No turdles yet");
      } else {
        for (var i = 0; i < tmp_turdles.length; i += 1) {
          tmp_turdles_hash[tmp_turdles[i]] = true;
        }
      }
      var in_turdles = jsonlist;
      //console.log("Turdle# " + in_turdles.length);
      if (in_turdles !== undefined) {
        for (var i = 0; i < in_turdles.length; i += 1) {
          var in_p = in_turdles[i];
          if (tmp_turdles_hash.hasOwnProperty(in_p.id)) {
                this.UpdateTurdle(
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
              this.AddTurdle(
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
                 this.RemoveTurdle(turdles_to_delete[i]);
        }
      }
    };
};
