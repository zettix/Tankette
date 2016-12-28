/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



/* global THREE */
/* global scene */

var tankette = tankette = tankette || {};

var updateGroupGeometry = function( mesh, geometry ) {

	mesh.children[ 0 ].geometry.dispose();
	mesh.children[ 1 ].geometry.dispose();

	mesh.children[ 0 ].geometry = new THREE.WireframeGeometry( geometry );
	mesh.children[ 1 ].geometry = geometry;

	// these do not update nicely together if shared

};

tankette.Text = function() {
    
    var data = {
            text : "TextGeometry",
            size : 5,
            height : 2,
            curveSegments : 12,
            font : "helvetiker",
            weight : "regular",
            bevelEnabled : false,
            bevelThickness : 1,
            bevelSize : 0.5,
            jsfont: null
    };

    var fonts = [
            "helvetiker",
            "optimer",
            "gentilis",
            "droid/droid_serif"
    ];

    var weights = [
            "regular", "bold"
    ];
    
    var mesh = new THREE.Object3D();
    mesh.matrixAutoUpdate = true;
    mesh.position.x = 0.0;
    mesh.position.y = 7.0;
    mesh.position.z = 5.0;
    
    var font_loaded = false;
    var loaded = false;

    mesh.add( new THREE.LineSegments(

            new THREE.Geometry(),

            new THREE.LineBasicMaterial({
                    color: 0xffffff,
                    transparent: true,
                    opacity: 0.5
            })

    ));

    mesh.add( new THREE.Mesh(

            new THREE.Geometry(),
    
            new THREE.MeshPhongMaterial({
                    color: 0x156289,
                    emissive: 0x072534,
                    side: THREE.DoubleSide,
                    shading: THREE.FlatShading
            })

    ));

    this.loadFont = function() {
        console.log("Loading font...");
        var loader = new THREE.FontLoader();
        loader.load( 'fonts/' + data.font + '_' + data.weight + '.typeface.js', function ( font ) {
            data.jsfont = font;
            font_loaded = true;
        } );
    };
    
    // Use this to set any of the options.
    this.configure = function(key, value) {
        data[key] = value;
    };
    
    this.generateGeometry = function() {
        if (font_loaded === true) {
            var geometry = new THREE.TextGeometry( data.text, {
                    font: data.jsfont,
                    size: data.size,
                    height: data.height,
                    curveSegments: data.curveSegments,
                    bevelEnabled: data.bevelEnabled,
                    bevelThickness: data.bevelThickness,
                    bevelSize: data.bevelSize
            } );
            geometry.center();

            updateGroupGeometry( mesh, geometry );
            return true;
        } else {
            return false;
        }
    };
    
    this.print = function(msg, scene, x, y, z, xr, yr, zr, s) {
        data.text = msg;
        if (generateGeometry()) {
            // scene.add(mesh);
        }
    };
    
    this.erase = function() {
        scene.remove(mesh);  
    };
    
    this.update = function() {
      if (loaded === false) {
        if (font_loaded === true) {
          loaded = true;
          if (this.generateGeometry()) {
            scene.add(mesh);
          }
          console.log('font loaded');
        }
      }
    }; 
    
    //Hide the wireframe
    mesh.children[ 0 ].visible = false;
    return this;
};