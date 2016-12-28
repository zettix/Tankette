/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/* global THREE */

var tankette = tankette = tankette || {};

tankette.SkyBox = function(my_scene) {
  var urlPrefix = "images/";
  var urls = [ urlPrefix + "posx.jpg", urlPrefix + "negx.jpg",
      urlPrefix + "posy.jpg", urlPrefix + "negy.jpg",
      urlPrefix + "posz.jpg", urlPrefix + "negz.jpg" ];
  var textureCube = THREE.ImageUtils.loadTextureCube( urls );
  //var textureCube = THREE.CubeTextureLoader(urls);

  var shader = THREE.ShaderLib["cube"];
  var uniforms = THREE.UniformsUtils.clone( shader.uniforms );
  uniforms['tCube'].value = textureCube;   // textureCube has been init before
  var material = new THREE.ShaderMaterial({
      fragmentShader    : shader.fragmentShader,
      vertexShader  : shader.vertexShader,
      uniforms  : uniforms,
      depthWrite: false,
      side: THREE.BackSide
  });

  // var material = new THREE.MeshPhongMaterial({
   // map: textureCube,
    //normalMap: earthNormalMap,
    ////specularMap: earthSpecularMap
  //});

  //var pig   = new THREE.Mesh( new THREE.BoxGeometry( 3, 3, 3, 1, 1, 1, null, true ), material );
  //pig.position.x = 11;
  //my_scene.add(pig);

  // build the skybox Mesh 
  var skyboxMesh    = new THREE.Mesh( new THREE.BoxGeometry( 500, 500, 500, 1, 1, 1, null, true ), material );
  // add it to the scene
  my_scene.add(skyboxMesh);
  console.log("skybox added");
};

