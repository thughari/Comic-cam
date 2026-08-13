#version 300 es
precision mediump float; uniform sampler2D uCostume; uniform sampler2D uMask; in vec2 vUv; out vec4 fragColor; void main(){ vec4 c=texture(uCostume,vUv); float a=texture(uMask,vUv).r; fragColor=vec4(c.rgb,c.a*a); }
