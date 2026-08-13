#version 300 es
precision mediump float; uniform sampler2D uCostume; in vec2 vUv; out vec4 fragColor; void main(){ fragColor=texture(uCostume,vUv); }
