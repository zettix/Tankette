/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this group file, choose Tools | Templates
 * and open the template in the editor.
 */

/* global modellist */
/* global THREE */
/* global controls */
/* global chasecam */

var tankette = tankette = tankette || {};

console.log("ModelManager Init");

tankette.ModelManager = function(model, scene) {
    var models = {};
    var model_ = model;
    var self = this;
    
    var loaded = true;
    
    this.NumModels = function() {
        return Object.keys(models).length;
    };

    this.GetModel = function(modelid) {
      if (models.hasOwnProperty(modelid)) {
        return models[modelid];
      } else {
          console.log("Error! model not found:" + modelid);
      }
      return null;
    };

    this.GetModelIds = function() {
        // console.log("GetModelIds" + models.toString());
        var keylist = Object.keys(models);
        return keylist;
    };

    this.AddModel = function(modelid, x, y, z, xr, yr, zr) {
        if (loaded) {
          var rockie = new tankette.Model(model_, x, y, z, xr, yr, zr, 1.0);
          models[modelid] = rockie;
          scene.add(rockie.group);
          console.log("Added model type " + model_ + " with id: "  + modelid);
      } else {
          console.log("Model not loaded yet.");
      }
    };
    
    this.RemoveModel = function(modelid) {
      if (models.hasOwnProperty(modelid)) {
          scene.remove(models[modelid].group);
          delete models[modelid];
      } else {
          console.log("Error! model not found to remove:" + modelid);
      }
    };
    
    this.UpdateModel = function(modelid, x, y, z, xr, yr, zr) {
      if (models.hasOwnProperty(modelid)) {
          var p = this.GetModel(modelid);
          p.group.position.x = x;
          p.group.position.y = y;
          p.group.position.z = z;
          p.group.rotation.x = xr;
          p.group.rotation.y = yr;
          p.group.rotation.z = zr;
      } else {
          console.log("Error! Update model not found:" + modelid);
      }
      // Only player. maybe misslecam later... CamUpdate();
    };
    
    // incoming json packet from server
    this.HandleUpdateList = function(jsonlist) {
        var tmp_models = this.GetModelIds();
        var tmp_models_hash = {};
        if (tmp_models === undefined) {
            console.log("No models yet");
        } else {
          for (var i = 0; i < tmp_models.length; i += 1) {
              tmp_models_hash[tmp_models[i]] = true;
          }
        }
        var in_models = jsonlist;
        // console.log("Parsing V1 message..." + in_models.length);
        for (var i = 0; i < in_models.length; i += 1) {
            var in_p = in_models[i];
            if (tmp_models_hash.hasOwnProperty(in_p.id)) {
                this.UpdateModel(
                    in_p.id,
                    parseFloat(in_p.x),
                    parseFloat(in_p.y),
                    parseFloat(in_p.z),
                    parseFloat(in_p.xr),
                    parseFloat(in_p.yr),
                    parseFloat(in_p.zr));                        
                delete tmp_models_hash[in_p.id];
            } else {  // new model
                this.AddModel(
                    in_p.id,
                    parseFloat(in_p.x),
                    parseFloat(in_p.y),
                    parseFloat(in_p.z),
                    parseFloat(in_p.xr),
                    parseFloat(in_p.yr),
                    parseFloat(in_p.zr));
            }
        } // for
        var models_to_delete = Object.keys(tmp_models_hash);
        if (models_to_delete !== undefined) {
            for (var i = 0; i < models_to_delete.length; i += 1) {
                this.RemovePlayer(models_to_delete[i]); 
            }
        }
    };
};