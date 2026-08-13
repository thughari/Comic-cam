#version 300 es
#extension GL_OES_EGL_image_external_essl3 : require
precision mediump float; uniform samplerExternalOES uCamera; in vec2 vUv; out vec4 fragColor; void main(){ fragColor = texture(uCamera, vUv); }
