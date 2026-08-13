precision mediump float;
uniform sampler2D uTexture;
uniform float uBands;
varying vec2 vTexCoord;
void main() {
    vec4 c = texture2D(uTexture, vTexCoord);
    float bands = max(4.0, uBands);
    c.rgb = floor(c.rgb * bands) / bands;
    c.rgb = mix(c.rgb, vec3(c.r * .75 + .08, c.g * 1.05, c.b * 1.25 + .08), .35);
    gl_FragColor = c;
}
