precision mediump float;
uniform sampler2D uTexture; uniform vec2 uResolution; uniform float uProgress; varying vec2 vTexCoord;
void main(){ vec4 c=texture2D(uTexture,vTexCoord); float lum=dot(c.rgb,vec3(.299,.587,.114)); vec2 grid=fract((vTexCoord*uResolution+uProgress*18.0)/14.0)-.5; float dotMask=smoothstep(.28,.05,length(grid))*(1.0-lum)*.32; gl_FragColor=vec4(mix(c.rgb,vec3(0.0),dotMask),c.a); }
