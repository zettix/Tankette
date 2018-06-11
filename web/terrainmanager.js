/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* global THREE */
/* global clock */
/* global use_wireframe */

var tankette = tankette = tankette || {};

console.log("Terrain Manager Init");

tankette.TerrainManager = function(scene) {
    
    var tiles = {};  // all using key=data.name
    //https://stackoverflow.com/questions/18357529/threejs-remove-object-from-scene
    var needs = {};
    var requests = {};
    var sneeds = {};
    
    var loader = new THREE.TextureLoader();
    var self = this;
    this.Needed = function() {
        var result = "";
        for (var k in needs) {
            if (!requests.hasOwnProperty(k)) {
                requests[k] = clock.elapsedTime;
                return k;
            }
        }
        //console.log("Needed result: " + k);
        return result;
    };
    
    this.Update = function(server_needs, delta_time) {
        sneeds = server_needs;  // voor logging
        // throttle request updates to 60 seconds.
        for (var k in requests) {
            if (requests.hasOwnProperty(k)) {
                if (requests[k] > clock.elapsedTime + 160) {
                    delete requests[k];
                }
            }
        }
       // console.log("server_needs " + server_needs);
        for (var idx = 0; idx < server_needs.length; idx++) {
            var k = server_needs[idx];
            if (tiles.hasOwnProperty(k)) {
               // console.log("tiles has k: " + k);
            } else {
              if (! requests.hasOwnProperty(k)) {
              //  console.log("Update k: " + k);
                needs[k] = true;
              } else {
                //  console.log("Requesting k: " + k);
              }
            }
        }
         //console.log("Needs: " + needs);
        //  remove any tiles that are not in the server list.
        var server_map = server_needs.reduce(function(result, item) {result[item] = true; return result;}, {});
        var kills = [];
        for (var k in tiles) {
            if (! server_map.hasOwnProperty(k)) {
                var gname = tiles[k].name;
                console.log("Removing " + gname + " aka tile " + k);
                var targgy = scene.getObjectByName(gname);
                scene.remove(targgy);
                kills.push(k);
            }
        }  // cannot delete from list while iterating, I don't think.
        for (var me = 0; me < kills.length; me++) {
            console.log("By tile " + kills[me]);
            this.doDispose(tiles[kills[me]]);
            delete tiles[kills[me]];
        }
    };
    
    // ripped from https://github.com/mrdoob/three.js/issues/5175
    // dealloc ram in a terrain server is critical.
    this.doDispose = function (obj) {
        if (obj !== null)
        {
            for (var i = 0; i < obj.children.length; i++)
            {
                this.doDispose(obj.children[i]);
            }
            if (obj.geometry)
            {
                obj.geometry.dispose();
                obj.geometry = undefined;
            }
            if (obj.material)
            {
                if (obj.material.map)
                {
                    obj.material.map.dispose();
                    obj.material.map = undefined;
                }
                obj.material.dispose();
                obj.material = undefined;
            }
        }
        obj = undefined;
    };
    
    this.Summary = function() {
        var result = "Tiles: ";
        for (var k in tiles) {
            result += " " + k.toString() + ":" + tiles[k].id;
        }
        result += " scene.children: ";
        for (var k in scene.children) {
            result += " " + k.toString() + ":" + scene.children[k].id;
        }
        result += " sneeds: " + sneeds.toString();
        return result;
    };
    
    this.AddTile = function(inTile) {
        var group = new THREE.Object3D();
        group.position.x = inTile.x;
        group.position.z  = inTile.y;
        group.position.y = inTile.z;
        group.name = inTile.n;
        var limit = inTile.c;
        // var geo = new THREE.PlaneBufferGeometry(inTile.rx, inTile.ry, limit - 1, limit - 1);
        var geo = new THREE.PlaneGeometry(inTile.rx, inTile.ry, limit - 1, limit - 1);
        geo.applyMatrix( new THREE.Matrix4().makeRotationX( - Math.PI / 2 ) );
        // PlateGeometry is centered.  See source.  I need it to start at 0, 0, not width/2 etc.
        geo.applyMatrix( new THREE.Matrix4().makeTranslation(inTile.rx * 0.5, 0, inTile.ry * 0.5));
        // var geodata = geo.attributes.position.array;
        console.log("Adding a tile: " + inTile.x + "," + inTile.y + "," + inTile.z + ":"  + limit + " limit. geodata: ");
        console.log("geo ver length: " + geo.vertices.length + " and limit * 2 = " + limit);
        for (var yy = 0 ; yy < limit; yy++) {
           for (var xx = 0; xx < limit; xx++) {
               var iddix = yy * limit + xx;
              // console.log("> " + yy + " " + xx + " >[" + iddix + "]>" + inTile.d[yy * limit + xx]);
           }
        }
        for (var x = 0; x < geo.vertices.length; x++) {
            // console.log("Dogs Of War :" + x + "intile x " + inTile.d[x]);
            // geodata[x].y = inTile.d[x] * 10;
            // geodata[x].y = x * 10;
            geo.vertices[x].y = inTile.d[x];
            //geo.position[x].y = inTile.d[x] * 0.3;
 
        }
        geo.verticesNeedUpdate = true;
        var url = inTile.u;
        loader.load(url, function(tex) {
            tex.wrapS = THREE.ClampToEdgeWrapping;
            tex.wrapT = THREE.ClampToEdgeWrapping;
            var mat = new THREE.MeshBasicMaterial( {map: tex } );
            if (use_wireframe === true) {
              mat.wireframe = true;
            }
            var mesh = new THREE.Mesh( geo, mat );
            mesh.receiveShadow = true;
            group.add(mesh);
            scene.add(group );
            
            tiles[inTile.n] = group;
            if (needs.hasOwnProperty(inTile.n)) {
                delete needs[inTile.n];
            }
            if (requests.hasOwnProperty(inTile.n)) {
                delete requests[inTile.n];
            }
            console.log("Finished tile load of " + inTile.n);
        });
        console.log("Started tile load of" + inTile.n);
    };
    console.log("Terrain Manager Loaded.");
};
