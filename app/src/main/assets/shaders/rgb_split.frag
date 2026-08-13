precision mediump float;
uniform sampler2D uTexture; uniform vec2 uResolution; uniform float uOffset; varying vec2 vTexCoord;
void main(){ vec2 d=vec2(uOffset,0.0)/uResolution; float r=texture2D(uTexture,vTexCoord+d).r; vec2 gb=texture2D(uTexture,vTexCoord-d).gb; gl_FragColor=vec4(r,gb.x,gb.y,1.0); }
