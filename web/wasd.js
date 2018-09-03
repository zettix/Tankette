/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* global THREE */

var WASD = WASD = WASD || {};

WASD.Controls = function(object , domElement) {
   // We are moving an object.  Possibly a camera but hopefully not,
   // a player.
   
   // References to stuff:
   this.object = object;
   this.target = new THREE.Vector3(0, 0, 0);
   this.domElement = ( domElement !== undefined) ? domElement : document;

   // Internal config
   this.movementSpeed = 1.0;
   this.lookSpeed = 0.005;
   this.autoForward = false;
   this.activeLook = true;
  
   // mouse and key states
   this.mouseX = 0;
   this.mouseY = 0;
   this.moveForward = false;
   this.moveBackward = false;
   this.moveLeft = false;
   this.moveRight = false;
   this.camForward = false;
   this.camBackward = false;
   this.camUp = false;
   this.camDown = false;
   this.camLeft = false;
   this.camRight = false;
   this.camRaise = false;
   this.camLower = false;
   this.camReset = false;
   this.b1_down = false;
   this.b2_down = false;
   this.b3_down = false;
   this.toggleCam = false;
   this.toggleTurdle = false;
   this.toggleFire = false;
   this.toggleWireFrame = false;
   
   // mouse screen interface:
   this.numCellsX = 3;
   this.numCellsY = 3;
   this.cellHit = 0;

   // Internal state
   this.viewHalfX = 1;
   this.viewHalfY = 1;
   this.viewCellXSize = 1;
   this.viewCellYSize = 1;
   this.xp = 1;
   this.yp = 1;

   // tabindex of -1 for why?
   if (this.domElement !== document) {
     this.domElement.setAttribute('tabindex', -1);
    this.isDocument = false;
   } else {
    this.isDocument = true;
   }

  // RESIZE
  this.handleResize = function() {
    if (this.isDocument) {
      this.viewHalfX = window.innerWidth / 2;
      this.viewHalfY = window.innerHeight / 2;
    } else {
      this.viewHalfX = this.domElement.offsetWidth / 2;
      this.viewHalfY = this.domElement.offsetHeight / 2;
    }
    this.viewCellXSize = (this.viewHalfX * 2 + 1) / this.numCellsX;
    this.viewCellYSize = (this.viewHalfY * 2 + 1) / this.numCellsY;
  };

  // MOUSE HANDLERS
  this.onMouseDown = function(event) {
    if (!this.isDocument) {
      this.domElement.focus();
    }
    event.preventDefault();
    event.stopPropagation();
    if ( this.activeLook ) {
      switch ( event.button ) {
        case 0: this.b1_down = true; break;
        case 1: this.b2_down = true; break;
        case 2: this.b3_down = true; break;
        default:
          break;
      }
    }
    this.onMouseMove(event);
    // 0, 0 is upper left
    if (this.yp === 0) {
      if (this.xp === 0) {
        this.toggleCam = true;
      } else if (this.xp === 2) {
        this.moveForward = true;
      }
    } else if (this.yp === 2) {
      if (this.xp === 0) {
        this.moveLeft = true;
      } else if (this.xp === 1) {
        this.moveRight = true;
      } else if (this.xp === 2) {
        this.moveBackward = true;
      }
    } else if (this.yp === 1) {
      if (this.xp === 1) {
        // this.moveBackward = true;
      }
    }
    // currently, -2 to 1, -1 to 0. 
    //alert("Jesus " + this.xp + " Christ " + this.yp);
    // -1037, -435 ul
    //  1039,  447 lr
    // so it's centered. zero is the dead center.
    // 
    //alert("Mother  " + this.mouseX + " of Mercy! " + this.mouseY);
  };
    
  this.onMouseUp = function(event) {
    if (!this.isDocument) {
      this.domElement.focus();
    }
    event.preventDefault();
    event.stopPropagation();
    if ( this.activeLook ) {
      switch ( event.button ) {
        case 0: this.b1_down = false; break;
        case 1: this.b2_down = false; break;
        case 2: this.b3_down = false; break;
        default:
          break;
      }
    }
    this.moveForward = false;
    this.moveBackward = false;
    this.moveLeft = false;
    this.moveRight = false;
    this.camForward = false;
    this.camBackward = false;
    this.camDown = false;
    this.camLeft = false;
    this.camRight = false;
    this.camUp = false;
    this.camRaise = false;
    this.camLower = false;
    this.camReset = false;
    this.toggleCam = false;
    this.toggleTurdle = false;
    this.toggleFire = false;
    
  };

  this.onMouseMove = function(event) {
    if (this.isDocument) {
      this.mouseX = event.pageX - this.viewHalfX;
      this.mouseY = event.pageY - this.viewHalfY;
    } else {
      this.mouseX = event.pageX - this.domElement.offsetLeft - this.viewHalfX;
      this.mouseY = event.pageY - this.domElement.offsetTop - this.viewHalfY;
    }
    
    this.xp = Math.floor((this.mouseX  + this.viewHalfX) / this.viewCellXSize);
    this.yp = Math.floor((this.mouseY  + this.viewHalfY) / this.viewCellYSize);
  };  

/*
Key Code Table  32 | space
|        backspace 8|     6 54|               v 86|            f3  114|
|              tab 9|     7 55|               w 87|            f4  115|
|           enter 13|     8 56|               x 88|            f5  116|
|           shift 16|     9 57|               y 89|            f6  117|
|           ctrl  17|     a 65|               z 90|            f7  118|
|             alt 18|     b 66|    left win key 91|            f8  119|
|     pause/break 19|     c 67|  right win key  92|            f9  120|
|       caps lock 20|     d 68|     select key  93|            f10 121|
|         escape  27|     e 69|       numpad 0  96|            f11 122|
|         page up 33|     f 70|       numpad 1  97|            f12 123|
|       page down 34|     g 71|       numpad 2  98|      num lock  144|
|             end 35|     h 72|       numpad 3  99|    scroll lock 145|
|           home  36|     i 73|      numpad 4  100|    semi-colon  186|
|     left arrow  37|     j 74|      numpad 5  101|    equal sign  187|
|       up arrow  38|     k 75|      numpad 6  102|          comma 188|
|     right arrow 39|     l 76|      numpad 7  103|          dash  189|
|     down arrow  40|     m 77|      numpad 8  104|        period  190|
|         insert  45|     n 78|      numpad 9  105|  forward slash 191|
|         delete  46|     o 79|      multiply  106|  grave accent  192|
|               0 48|     p 80|            add 107|  open bracket  219|
|               1 49|     q 81|      subtract  109|    back slash  220|
|               2 50|     r 82|  decimal point 110|  close braket  221|
|               3 51|     s 83|        divide  111|  single quote  222|
|               4 52|     t 84|            f1  112|                   
|               5 53|     u 85|            f2  113| 
*/

  // KEYBOARD HANDLERS

  this.onKeyBool = function( event, value) {
    switch ( event.keyCode ) {
      case 38: /*up*/ this.camForward = value; break;
      case 87: /*W*/ this.moveForward = value; break;
      case 37: /*left*/ this.camLeft = value; break;
      case 65: /*A*/ this.moveLeft = value; break;
      case 40: /*down*/ this.camBackward = value; break;
      case 83: /*S*/ this.moveBackward = value; break;
      case 39: /*right*/ this.camRight = value; break;
      case 68: /*D*/ this.moveRight = value; break;
      case 67: /*C*/ this.toggleCam = value; break;
      case 84: /*T*/ this.toggleTurdle = value; break;
      case 70: /*F*/
      case 32: /* */ this.toggleFire = value; break;
      case 88: /*X*/ this.toggleWireFrame = value; break;
      case 79: /*O*/ this.camUp = value; break;
      case 80: /*P*/ this.camDown = value; break;
      case 75: /*K*/ this.camRaise = value; break;
      case 76: /*L*/ this.camLower = value; break;
      case 82: /*R*/ this.camReset = value; break;
      default: break;
    }
  };


  this.onKeyDown = function ( event ) {
    this.onKeyBool(event, true);
  };

  this.onKeyUp = function ( event ) {
    //event.preventDefault();
    this.onKeyBool(event, false);
  };


  // update
  this.update = function(delta) {
    //var actualSpeed = this.movementSpeed * delta;
    // Basic function is to grab the mouse maybe, move maybe.
    /*if (this.moveForward) {
      this.object.MoveForward(delta);
    }
    if (this.moveBackward) {
      this.object.MoveBackward(delta);
    }
    if (this.moveLeft) {
      this.object.MoveLeft(delta);
    }
    if (this.moveRight) {
      this.object.MoveRight(delta);
    }*/
     
  };

  // BINDS
  this.contextmenu = function(event) {
    event.preventDefault();
  };

  // cleanup, called when?
  this.listenDown = function() {
    this.domElement.removeEventListener( 'contextmenu', this._contextmenu, false );
    this.domElement.removeEventListener( 'mousedown', this._onMouseDown, false );
    this.domElement.removeEventListener( 'mousemove', this._onMouseMove, false );
    this.domElement.removeEventListener( 'mouseup', this._onMouseUp, false );
    window.removeEventListener( 'keydown', this._onKeyDown, false );
    window.removeEventListener( 'keyup', this._onKeyUp, false );
  };

  this.dispose = function() {
    this.listenDown();
  };

  // Actual binds to dom/browser magic.
  this._onMouseMove = bind( this, this.onMouseMove );
  this._onMouseDown = bind( this, this.onMouseDown );
  this._onMouseUp = bind( this, this.onMouseUp );
  this._onKeyDown = bind( this, this.onKeyDown );
  this._onKeyUp = bind( this, this.onKeyUp );
  this._contextmenu = bind(this, this.contextmenu);

  this.listenUp = function() {
    this.domElement.addEventListener( 'contextmenu', this._contextmenu, false );
    this.domElement.addEventListener( 'mousemove', this._onMouseMove, false );
    this.domElement.addEventListener( 'mousedown', this._onMouseDown, false );
    this.domElement.addEventListener( 'mouseup', this._onMouseUp, false );
    window.addEventListener( 'keydown', this._onKeyDown, false );
    window.addEventListener( 'keyup', this._onKeyUp, false );
  };

  function bind( scope, fn ) {
    return function () {
      fn.apply( scope, arguments );
    };
  };

  this.listenUp();

  this.handleResize();
};
