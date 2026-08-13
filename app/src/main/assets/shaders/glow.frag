precision mediump float;
uniform sampler2D uTexture;
uniform vec2 uResolution;
uniform float uProgress;
varying vec2 vTexCoord;
void main() {
    vec4 c = texture2D(uTexture, vTexCoord);
    gl_FragColor = c;
}
